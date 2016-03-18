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

package ch.fhnw.demopolis.model.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import assets.Asset;
import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.ether.formats.ModelObject;
import ch.fhnw.ether.formats.obj.ObjReader;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;
import ch.fhnw.util.math.geometry.Polygon;

public final class StaticModel extends AbstractDesignEntity {
	private final ModelObject object;
	private final PolisMaterial material;
	private final List<Polygon> shapes = new ArrayList<>(1);
	private List<IMesh> meshes;

	public StaticModel(String asset, int id) throws IOException {
		super(Type.BUILDING, asset, id);
		object = new ObjReader(Asset.get(asset), ObjReader.Options.CONVERT_TO_Z_UP).getObject();
		material = new PolisMaterial(I3DColors.BUILDING);
	}
	
	@Override
	public List<Polygon> getShapes() {
		if (shapes == null) {
			BoundingBox b = new BoundingBox();
			meshes.forEach(m -> b.add(m.getBounds()));
			Polygon shape = new Polygon(new Vec3(b.getMinX(), b.getMinY(), 0),
										new Vec3(b.getMaxX(), b.getMinY(), 0),
										new Vec3(b.getMaxX(), b.getMaxY(), 0),
										new Vec3(b.getMinX(), b.getMaxY(), 0));
			shapes.add(shape);
		}
		return shapes;
	}

	@Override
	public List<IMesh> getMeshes() {
		if (meshes == null)
			meshes = object.getMeshes(material);
		return meshes;
	}
	
	@Override
	public void fade(float amount) {
		material.setDiffuse(I3DColors.BUILDING.scaleRGB(amount));
	}
	
	@Override
	public List<IMesh> generate(Model model) {
		List<IMesh> m = new ArrayList<>();
		for (IMesh mesh : meshes) {
			m.add(new DefaultMesh(Primitive.TRIANGLES, model.getMaterials().buildingMaterial, mesh.getGeometry()));
		}
		return m;
	}
}
