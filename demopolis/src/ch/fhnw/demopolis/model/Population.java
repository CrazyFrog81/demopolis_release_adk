/*
 * Copyright (c) 2015 - 2016 Stefan Muller Arisona, Simon Schubiger
 * Copyright (c) 2015 - 2016 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.demopolis.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import assets.Asset;
import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.config.IPopulationAssets;
import ch.fhnw.demopolis.render.MaskMaterial;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.ether.formats.obj.ObjReader;
import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Flag;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Polygon;

public class Population {
	public enum Type {
		PERSONS,
		CYCLISTS,
		CARS
	}
	
	private static class Lane {
		final EnumSet<Type> types;
		final Vec3 v0;
		final Vec3 v1;
		final float length;
		final float width;
		final float angle;
		
		public Lane(Polygon p, EnumSet<Type> types) {
			this.types = types;
			v0 = p.getVertexOnEdge(p.getNumVertices() - 1, 0.5f);
			v1 = p.getVertexOnEdge(1, 0.5f);
			length = v0.distance(v1);
			width = p.getExtentV();
			Vec3 d = v1.subtract(v0);
			angle = (float)Math.toDegrees(Math.atan2(d.y, d.x));
		}
	}

	private static class Place {
		final Polygon area;
		final List<Vec3> occlusion;
		
		Place(Polygon area, List<Vec3> occlusion) {
			this.area = area;
			this.occlusion = occlusion;
		}
	}

	private final List<IMesh> persons = new ArrayList<>();
	private final List<IMesh> cyclists = new ArrayList<>();
	private final List<IMesh> cars = new ArrayList<>();
	
	private final List<Lane> lanes = new ArrayList<>();
	private final List<Place> places = new ArrayList<>();
	
	private List<IMesh> meshes = new ArrayList<>();
	
	private boolean fender = false;
	
	public Population() {
	}

	public void load(Model model) throws IOException {
		for (String s : IPopulationAssets.PERSONS)
			persons.add(getFlatMesh(s, IPopulationAssets.PERSON_HEIGHT));
		
		for (String s : IPopulationAssets.CYLISTS)
			cyclists.add(getFlatMesh(s, IPopulationAssets.CYCLIST_HEIGHT));
		
		for (String s : IPopulationAssets.CARS)
			cars.add(getVehicleMesh(s));
	}
	
	public void addToScene(IScene scene) {
		for (Lane lane : lanes) {
			boolean pedestrian = lane.types.contains(Type.PERSONS);
			float distance = pedestrian ? IPopulationAssets.PEDESTRIAN_GAP : IPopulationAssets.CAR_GAP;
			float prob = pedestrian ? IPopulationAssets.PEDESTRIAN_PROBABILITY : IPopulationAssets.CAR_PROBABILITY;
			
			float delta = distance / lane.length;
			for (float t = 0; t < 1; t += delta) {
				if (Math.random() > prob)
					continue;
				Vec3 v = Vec3.lerp(lane.v0, lane.v1, t);
				float x = pedestrian ? v.x + MathUtilities.random(-lane.width / 3, lane.width / 3) : v.x;
				float y = pedestrian ? v.y + MathUtilities.random(-lane.width / 3, lane.width / 3) : v.y;
				float angle = pedestrian ? lane.angle + MathUtilities.random(0, 360) : lane.angle;
				meshes.add(instantiate(getMesh(lane.types), x, y, angle));
			}
		}
		
		for (Place place : places) {
			Polygon p = place.area.offset(2);
			float u = p.getExtentU();
			float v = p.getExtentV();
			float a = u * v;
			int n = (int)(a * IPopulationAssets.NUM_PEOPLE);
			person:
			for (int i = 0; i < n; ++i) {
				float x = MathUtilities.random(-u, u);
				float y = MathUtilities.random(-v, v);
				Vec3 v0 = p.getCenter().add(new Vec3(x, y, 0));
				if (p.project(v0) == null)
					continue person;
				for (Vec3 o : place.occlusion) {
					Vec3 v1 = new Vec3(o.x, o.y, 0);
					float r = o.z;
					if (v0.distance(v1) < r + 1)
						continue person;
				}
				float angle = MathUtilities.random(0, 360);
				meshes.add(instantiate(getMesh(EnumSet.of(Type.PERSONS)), v0.x, v0.y, angle));
			}
		}

		System.out.println("merging from: " + meshes.size());
		meshes = MeshUtilities.mergeMeshes(meshes);
		System.out.println("merged to: " + meshes.size());
		scene.add3DObjects(meshes);
	}
	
	private IMesh getMesh(EnumSet<Type> types) {
		boolean checkFender = false;
		List<IMesh> meshes = null;
		if (types.size() == 1) {
			if (types.contains(Type.PERSONS)) {
				meshes = persons;
				checkFender = true;
			} else if (types.contains(Type.CYCLISTS)) {
				meshes = cyclists;
			} else {
				meshes = cars;
			}
		} else {
			if (types.contains(Type.PERSONS)) {
				meshes = (Math.random() > IPopulationAssets.CYCLIST_PROBABILITY) ? persons : cyclists;
			} else {
				meshes = (Math.random() > IPopulationAssets.CYCLIST_PROBABILITY) ? cars : cyclists;
			}
		}
		int index = MathUtilities.random(0, meshes.size() - 1);
		if (checkFender && index == 0) {
			if (!fender) {
				fender = true;
			} else {
				index = 1;
			}
		}
		return meshes.get(index);
	}

	public void removeFromScene(IScene scene) {
		scene.remove3DObjects(meshes);
		
		// clean up for next round
		meshes.clear();
		lanes.clear();
		places.clear();
		fender = false;
	}
	
	public void addLane(Polygon p, EnumSet<Population.Type> types) {
		lanes.add(new Lane(p, types));
	}
	
	public void addPlace(Polygon area, List<Vec3> occlusion) {
		places.add(new Place(area, occlusion));
	}
	
	private static IMesh getFlatMesh(String asset, float h) throws IOException {
		Texture texture = Frame.create(Asset.get(asset)).getTexture();
		float aspect = (float)texture.getWidth() / (float)texture.getHeight();
		float w = 0.5f * h * aspect;
		float[] v = new float[] { 
				-w, 0, 0, w, 0, 0, w, 0, h,
				-w, 0, 0, w, 0, h, -w, 0, h
		};
		float[] t = MeshUtilities.DEFAULT_QUAD_TEX_COORDS;
		return new DefaultMesh(Primitive.TRIANGLES, new MaskMaterial(texture), DefaultGeometry.createVM(v, t), Flag.DONT_CULL_FACE);
	}
	
	private static IMesh getVehicleMesh(String asset) throws IOException {
		PolisMaterial material = new PolisMaterial(RGB.BLACK, RGB.GRAY80);
		List<IMesh> meshes = new ObjReader(Asset.get(asset), ObjReader.Options.CONVERT_TO_Z_UP).getMeshes(material);
		meshes = MeshUtilities.mergeMeshes(meshes);
		if (meshes.size() != 1)
			throw new IllegalArgumentException("cannot merge meshes into a single mesh");
		return meshes.get(0);
	}

	private static IMesh instantiate(IMesh mesh, float x, float y, float rot) {
		IMesh instance = mesh.getInstance();
		instance.setTransform(Mat4.trs(x, y, I3DConfig.LAYER_2, 0, 0, rot, 1, 1, 1));
		return instance;
	}
}
