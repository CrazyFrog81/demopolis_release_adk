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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.demopolis.model.Materials;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.material.PointMaterial;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeodesicSphere;
import ch.fhnw.util.math.geometry.Polygon;

public final class OpenSpaceBlock extends AbstractDesignEntity {
	public static final float PLAZA_OFFSET = 10;
	
	public static final float SHRUB_RADIUS = 20;
	public static final float SHRUB_MAX_HEIGHT = 2;
	public static final int SHRUB_MAX_PER_CLUSTER = 50;
	
	public static final float TREE_DISTANCE = 14;
	public static final float TREE_OFFSET = PLAZA_OFFSET * 0.75f;
	public static final float TREE_MIN_HEIGHT = 12;
	public static final float TREE_MAX_HEIGHT = 16;
	public static final float TREE_DIAMETER_RATIO = 0.8f;
	public static final float TREE_MAX_DIAMETER = TREE_MAX_HEIGHT * TREE_DIAMETER_RATIO;

	public static final float TRUNK_DIAMETER = 0.3f;
	
	public static final float TRUNK_HEIGHT_RATIO = 0.5f;
	
	public enum OpenSpaceType implements IButtonInfo {
		NON_LANDSCAPED(IUIColors.BLUE_C, IUIColors.BLUE_D),
		PLAZA(IUIColors.GREEN_D, IUIColors.GREEN_D),
		LAWN(IUIColors.GREEN_C, IUIColors.GREEN_C),
		SHRUBS(IUIColors.GREEN_B, IUIColors.GREEN_C),
		TREES(IUIColors.GREEN_A, IUIColors.GREEN_A);
		
		OpenSpaceType(RGB buttonColor, RGB designColor) {
			this.buttonColor = buttonColor;
			this.designColor = designColor;
		}

		@Override
		public RGB getButtonColor() {
			return buttonColor;
		}
		
		@Override
		public RGB getDesignColor() {
			return designColor;
		}
		
		public final RGB buttonColor;
		public final RGB designColor;
	}
	
	public static class PlantGroup {
		private static final float POINT_SIZE = 20;
		private static final PointMaterial SHRUB_MATERIAL = new PointMaterial(OpenSpaceType.SHRUBS.designColor.toRGBA(), POINT_SIZE);
		private static final PointMaterial TREE_MATERIAL = new PointMaterial(OpenSpaceType.TREES.designColor.toRGBA(), POINT_SIZE);
		
		private final List<Vec3> positions = new ArrayList<>();
		private final Vec3 p0;
		private final Vec3 p1;
		private final float radius;
		
		private final IMesh mesh;
		
		public PlantGroup(Vec3 p0, float radius, boolean shrub) {
			this.p0 = p0;
			this.p1 = null;
			this.radius = radius;
			mesh = new DefaultMesh(Primitive.POINTS, shrub ? SHRUB_MATERIAL : TREE_MATERIAL, DefaultGeometry.createV(p0.toArray()));
			mesh.setPosition(new Vec3(0, 0, I3DConfig.LAYER_2));
		}
		
		public PlantGroup(Vec3 p0, Vec3 p1, boolean shrub) {
			this.p0 = p0;
			this.p1 = p1;
			this.radius = 0;
			Vec3 d = p1.subtract(p0);
			int n = (int)(1 + d.length() / 40);
			for (int i = 0; i < n; ++i) {
				positions.add(p0.add(d.scale((i + 1f) / (n + 1f))));
			}
			mesh = new DefaultMesh(Primitive.POINTS, shrub ? SHRUB_MATERIAL : TREE_MATERIAL, DefaultGeometry.createV(Vec3.toArray(positions)));
			mesh.setPosition(new Vec3(0, 0, I3DConfig.LAYER_2));
		}
		
		public Vec3 getP0() {
			return p0;
		}
		
		public Vec3 getP1() {
			return p1;
		}
		
		public float getRadius() {
			return radius;
		}
		
		public IMesh getMesh() {
			return mesh;
		}
	}

	private static final GeodesicSphere DOME = new GeodesicSphere(1);

	private OpenSpaceType openSpaceType;

	private final PolisMaterial material;
	private final Polygon shape;
	private final Polygon plazaShape;
	private final Polygon treeShape;
	
	private IMesh blockMesh;
	private IMesh plazaMesh;
	private final List<PlantGroup> shrubs = new ArrayList<>();
	private final List<PlantGroup> trees = new ArrayList<>();
	
	private final List<Vec3> shrubPositions = new ArrayList<>();
	private final List<Vec3> treePositions = new ArrayList<>();

	public OpenSpaceBlock(IDesignEntity entity) {
		super(entity);
		openSpaceType = OpenSpaceType.NON_LANDSCAPED;
		material = new PolisMaterial(I3DColors.AMBIENT_LO, openSpaceType.designColor);
		shape = entity.getShape();
		plazaShape = shape.offset(PLAZA_OFFSET);
		treeShape = shape.offset(TREE_OFFSET);
	}
	
	public OpenSpaceType getOpenSpaceType() {
		return openSpaceType;
	}
	
	public void setOpenSpaceType(OpenSpaceType openSpaceType) {
		this.openSpaceType = openSpaceType;
		material.setDiffuse(openSpaceType.designColor);
		if (openSpaceType == OpenSpaceType.PLAZA && plazaShape != null) {
			if (plazaMesh == null) {
				plazaMesh = IDesignEntity.createMesh(new PolisMaterial(I3DColors.GROUND), plazaShape, I3DConfig.LAYER_2);
			}
		} else {
			plazaMesh = null;			
		}
		shrubs.clear();
		trees.clear();
	}
	
	public RGB getColor() {
		return material.getDiffuse();
	}
	
	public void setColor(RGB color) {
		material.setDiffuse(color);
	}
	
	public boolean hasShrubs() {
		return !shrubs.isEmpty();
	}
	
	public PlantGroup createShrubs(Vec3 position) {
		for (PlantGroup g : shrubs) {
			if (g.getP0().distance(position) < SHRUB_RADIUS / 2)
				return null;
		}		
		return new PlantGroup(position, SHRUB_RADIUS, true);
	}
	
	public List<PlantGroup> getShrubs() {
		return shrubs;
	}
	
	public List<Vec3> getShrubPositions() {
		return shrubPositions;
	}

	public void addShrubs(PlantGroup group) {
		shrubs.add(group);
	}

	public boolean hasTrees() {
		return !trees.isEmpty();
	}
	
	public PlantGroup createTrees(Vec3 position) {
		if (treeShape == null)
			return null;

		int index = treeShape.getClosestEdge(position);
		float distance = treeShape.getLine(index).distance(position);
		if (distance < TREE_MAX_DIAMETER) {
			Vec3 p0 = treeShape.get(index);
			Vec3 p1 = treeShape.get(index + 1);
			
			for (PlantGroup g : trees) {
				if (g.getP0().equals(p0))
					return null;
			}
			return new PlantGroup(p0, p1, false);
		} else {
			for (PlantGroup g : trees) {
				if (g.getP0().distance(position) < TREE_MAX_DIAMETER)
					return null;
			}		
			return new PlantGroup(position, TREE_MAX_DIAMETER / 2, false);
		}
	}	

	public List<PlantGroup> getTrees() {
		return shrubs;
	}
	
	public List<Vec3> getTreePositions() {
		return treePositions;
	}

	public void addTrees(PlantGroup group) {
		trees.add(group);
	}

	@Override
	public List<Polygon> getShapes() {
		return Collections.singletonList(shape);
	}
		
	@Override
	public List<IMesh> getMeshes() {
		List<IMesh> meshes = new ArrayList<>();
		if (blockMesh == null) {
			blockMesh = IDesignEntity.createMesh(material, shape, I3DConfig.LAYER_1);
		}
		meshes.add(blockMesh);
		if (plazaMesh != null)
			meshes.add(plazaMesh);
		trees.forEach(p -> meshes.add(p.getMesh()));
		shrubs.forEach(p -> meshes.add(p.getMesh()));
		return meshes;
	}
	
	@Override
	public void fade(float amount) {
		// ignored
	}
	
	@Override
	public List<IMesh> generate(Model model) {
		Materials materials = model.getMaterials();
		
		List<IMesh> meshes = new ArrayList<>();
		List<Vec3> occlusion = new ArrayList<>();
		
		// create foundation
		switch (getOpenSpaceType()) {
		case NON_LANDSCAPED:
			meshes.add(IDesignEntity.createMesh(materials.blockMaterial, getShape(), I3DConfig.LAYER_1));
			break;
		case LAWN:
			meshes.add(IDesignEntity.createMesh(materials.greenMaterial, getShape(), I3DConfig.LAYER_1));
			break;
		case PLAZA:
			meshes.add(IDesignEntity.createMesh(materials.greenMaterial, getShape(), I3DConfig.LAYER_1));
			if (plazaShape != null)
				meshes.add(IDesignEntity.createMesh(materials.blockMaterial, plazaShape, I3DConfig.LAYER_2));
			break;
		default:
			break;			
		}
		
		shrubPositions.clear();
		treePositions.clear();
		generateShrubs(materials, meshes, occlusion);
		generateTrees(materials, meshes, occlusion);
		
		model.getPopulation().addPlace(getShape(), occlusion);
		return meshes;
	}

	private void generateShrubs(Materials materials, List<IMesh> meshes, List<Vec3> occlusion) {
		for (PlantGroup g : shrubs) {
			int n = MathUtilities.random(0, SHRUB_MAX_PER_CLUSTER);
			shrub:
			for (int i = 0; i < n; ++i) {
				float r = MathUtilities.random(0, g.radius);
				float a = (float)MathUtilities.random(0, 2 * Math.PI);
				float x = g.p0.x + r * (float)Math.cos(a);
				float y = g.p0.y + r * (float)Math.sin(a);
				float z = MathUtilities.random(0, SHRUB_MAX_HEIGHT / 2);
				float s = MathUtilities.random(SHRUB_MAX_HEIGHT / 4, SHRUB_MAX_HEIGHT);
				
				Vec3 position = new Vec3(x, y, 0);
				if (shape.project(position) == null)
					continue;
				for (int j = 0; j < shape.getNumVertices(); ++j) {
					if (shape.getLine(j).distance(position) < s)
						continue shrub;
				}
				
				IMesh shrub = new DefaultMesh(Primitive.TRIANGLES, materials.shrubMaterial, DefaultGeometry.createVN(DOME.getTriangles(), null));
				shrub.setTransform(Mat4.trs(x, y, z, 0, 0, 0, s, s, s));
				meshes.add(shrub);
				occlusion.add(new Vec3(x, y, s));
				shrubPositions.add(new Vec3(x, y, 0));
			}
		}
	}

	private void generateTrees(Materials materials, List<IMesh> meshes, List<Vec3> occlusion) {
		for (PlantGroup g : trees) {
			Vec3 p0 = g.p0;
			Vec3 p1 = g.p1;
			float h = MathUtilities.random(TREE_MIN_HEIGHT, TREE_MAX_HEIGHT);
			if (p1 == null) {
				generateTree(materials, meshes, occlusion, p0, h);
			} else {
				Vec3 d = p1.subtract(p0);
				int n = (int)(1 + d.length() / MathUtilities.random(TREE_DISTANCE, 1.4f * TREE_DISTANCE));
				for (int i = 0; i < n; ++i) {
					if (Math.random() > 0.95)
						continue;
					Vec3 p = p0.add(d.scale((i + 1f) / (n + 1f)));
					generateTree(materials, meshes, occlusion, p, h);
				}
			}
		}
	}
	
	private void generateTree(Materials materials, List<IMesh> meshes, List<Vec3> occlusion, Vec3 position, float h) {
		float hh = MathUtilities.random(0.9f, 1.0f) * h;
		float w = TREE_DIAMETER_RATIO * hh;
		float ht = hh - w;
		float wt = TRUNK_DIAMETER;
		IMesh tree = new DefaultMesh(Primitive.TRIANGLES, materials.treeMaterial, DefaultGeometry.createVN(DOME.getTriangles(), null));
		tree.setTransform(Mat4.trs(position.x, position.y, ht + hh / 2, 0, 0, 0, w, w, hh));
		IMesh trunk = MeshUtilities.createCylinder(materials.trunkMaterial, 6, false);
		trunk.setTransform(Mat4.trs(position.x, position.y, ht / 2, 0, 0, 0, wt, wt, ht));
		meshes.add(tree);
		meshes.add(trunk);
		occlusion.add(new Vec3(position.x, position.y, wt));
		treePositions.add(new Vec3(position.x, position.y, 0));
	}
}
