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
import java.util.EnumSet;
import java.util.List;

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.demopolis.model.Materials;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.Population;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Polygon;

public final class Street extends AbstractDesignEntity {
	public static final float LANE_WIDTH = 4;
	public static final float PARKING_WIDTH = 2.5f;
	public static final float WALK_WIDTH = 1.5f;
	
	public static final float LANE_MARKING_WIDTH = 0.4f;
	public static final float PARKING_MARKING_WIDTH = 0.2f;
	
	public enum StreetType implements IButtonInfo {
		FOUR_LANE(IUIColors.RED_C, IUIColors.RED_D, 4 * LANE_WIDTH + 2 * WALK_WIDTH),
		TWO_LANE(IUIColors.RED_B, IUIColors.RED_C, 2 * LANE_WIDTH + 2 * WALK_WIDTH),
		BY_ROAD(IUIColors.BLUE_C, IUIColors.BLUE_D, 1 * LANE_WIDTH + 2 * WALK_WIDTH),
		PEDESTRIAN(IUIColors.GREEN_C, IUIColors.GREEN_C, WALK_WIDTH);
		
		StreetType(RGB buttonColor, RGB designColor, float minWidth) {
			this.buttonColor = buttonColor;
			this.designColor = designColor;
			this.minWidth = minWidth;
		}
		
		public final RGB buttonColor;
		public final RGB designColor;
		public final float minWidth;
		
		@Override
		public RGB getButtonColor() {
			return buttonColor;
		}
		
		@Override
		public RGB getDesignColor() {
			return designColor;
		}
		
		public static StreetType getMaxType(float width) {
			for (StreetType t : StreetType.values())
				if (width >= t.minWidth)
					return t;
			throw new IllegalArgumentException("street width too small: " + width);
		}
	}
	
	private final PolisMaterial material;
	private final List<Polygon> shapes;
	private final List<IMesh> meshes;
	private StreetType streetType;

	public Street(IDesignEntity entity) {
		super(entity);
		material = new PolisMaterial(I3DColors.AMBIENT_LO, RGB.WHITE);
		shapes = entity.getShapes();
		meshes = new ArrayList<>();
		
		streetType = StreetType.getMaxType(getWidth());
		material.setDiffuse(streetType.designColor.scaleRGB(2));
	}
	
	public StreetType getStreetType() {
		return streetType;
	}
	
	public void setStreetType(StreetType type) {
		if (getWidth() > type.minWidth) {
			streetType = type;
		} else {
			streetType = StreetType.getMaxType(getWidth());
		}
		setColor(streetType.designColor);
	}
	
	public void setColor(RGB color) {
		material.setDiffuse(color.scaleRGB(I3DColors.HI));		
	}

	public float getWidth() {
		return getShape().getExtentV();
	}
	
	public boolean acceptType(StreetType streetType) {
		return StreetType.getMaxType(getWidth()).ordinal() <= streetType.ordinal();
	}
	
	@Override
	public List<Polygon> getShapes() {
		return shapes;
	}

	@Override
	public List<IMesh> getMeshes() {
		if (meshes.isEmpty()) {
			for (Polygon shape : getShapes())
				meshes.add(IDesignEntity.createMesh(material, shape, I3DConfig.LAYER_1));
			IMesh mesh = MeshUtilities.mergeMeshes(meshes).get(0);
			meshes.clear();
			meshes.add(mesh);
		}
		return meshes;
	}
	
	@Override
	public void fade(float amount) {
		material.setAmbient(I3DColors.AMBIENT_HI);
		material.setDiffuse(I3DColors.GROUND.scaleRGB(amount));
	}
	
	@Override
	public List<IMesh> generate(Model model) {
		Materials materials = model.getMaterials();
		Population population = model.getPopulation();
		
		List<IMesh> meshes = new ArrayList<>();
		for (Polygon p : getShapes()) {
			float width = p.getExtentV();
			switch (getStreetType()) {
			case PEDESTRIAN: {
				meshes.add(IDesignEntity.createMesh(materials.sidewalkMaterial, p, I3DConfig.LAYER_1));
				population.addLane(p, EnumSet.of(Population.Type.PERSONS, Population.Type.CYCLISTS));
				break;
			}
			case BY_ROAD: {
				float budget = width - StreetType.BY_ROAD.minWidth;
				if (budget < LANE_WIDTH) {
					Polygon[] pp = splitToInnerFixedWidth(p, LANE_WIDTH);
					meshes.add(IDesignEntity.createMesh(materials.sidewalkMaterial, pp[0], I3DConfig.LAYER_1));
					// don't add street polygon (use ground coloring instead)
					//meshes.add(IDesignEntity.createMesh(materials.streetMaterial, pp[1], I3DConfig.LAYER_1));
					meshes.add(IDesignEntity.createMesh(materials.sidewalkMaterial, pp[2], I3DConfig.LAYER_1));
	
					population.addLane(pp[0], EnumSet.of(Population.Type.PERSONS));
					population.addLane(pp[1], EnumSet.of(Population.Type.CYCLISTS, Population.Type.CARS));
					population.addLane(pp[2], EnumSet.of(Population.Type.PERSONS));
					break;
				}
				// fall into two lane if enough budget
			}
			case TWO_LANE: {
				Polygon[] pp = splitToHalf(p);
				Polygon mark = makeMark(pp[0].get(3), pp[0].get(2), LANE_MARKING_WIDTH);
				meshes.add(IDesignEntity.createMesh(materials.streetMarkMaterial, mark, I3DConfig.LAYER_1));
				addOneLane(meshes, materials, population, pp[0]);
				addOneLane(meshes, materials, population, pp[1]);
				break;
			}
			case FOUR_LANE: {
				Polygon[] pp = splitToHalf(p);
				Polygon mark = makeMark(pp[0].get(3), pp[0].get(2), LANE_MARKING_WIDTH);
				meshes.add(IDesignEntity.createMesh(materials.streetMarkMaterial, mark, I3DConfig.LAYER_1));
				addTwoLanes(meshes, materials, population, pp[0]);
				addTwoLanes(meshes, materials, population, pp[1]);
				break;
			}
			}
		}
		return meshes;
	}
	
	private static final float W_TOTAL_1 = 6.5f;
	private static final float W_LANE_1 = 3f / W_TOTAL_1;
	private static final float W_PARKING_1 = 2f / W_TOTAL_1;
	//private static final float W_WALK_1 = 1f / W_TOTAL_1;
	
	private static final float W_TOTAL_2 = 9.5f;
	private static final float W_LANE_2 = 3f / W_TOTAL_2;
	private static final float W_PARKING_2 = 2f / W_TOTAL_2;
	//private static final float W_WALK_2 = 1f / W_TOTAL_2;

	private void addOneLane(List<IMesh> meshes, Materials materials, Population population, Polygon p) {
		Vec3 v0 = p.get(0);
		Vec3 v1 = p.get(1);
		Vec3 v2 = p.getVertexOnEdge(1, 1 - W_LANE_1 - W_PARKING_1);
		Vec3 v3 = p.getVertexOnEdge(1, 1 - W_LANE_1);
		Vec3 v4 = p.get(2);
		Vec3 v5 = p.get(3);
		Vec3 v6 = p.getVertexOnEdge(3, W_LANE_1);
		Vec3 v7 = p.getVertexOnEdge(3, W_LANE_1 + W_PARKING_1);
		meshes.add(IDesignEntity.createMesh(materials.streetMarkMaterial, makeMark(v6, v3, PARKING_MARKING_WIDTH), I3DConfig.LAYER_1));
		population.addLane(new Polygon(v6, v3, v4, v5), EnumSet.of(Population.Type.CYCLISTS, Population.Type.CARS));
		population.addLane(new Polygon(v7, v2, v3, v6), EnumSet.of(Population.Type.CARS));

		Polygon sidewalk = new Polygon(v0, v1, v2, v7);
		meshes.add(IDesignEntity.createMesh(materials.sidewalkMaterial, sidewalk, I3DConfig.LAYER_1));
		population.addLane(sidewalk, EnumSet.of(Population.Type.PERSONS));
	}

	private void addTwoLanes(List<IMesh> meshes, Materials materials, Population population, Polygon p) {
		Vec3 v0 = p.get(0);
		Vec3 v1 = p.get(1);
		Vec3 v2 = p.getVertexOnEdge(1, 1 - W_LANE_2 - W_LANE_2 - W_PARKING_2);
		Vec3 v3 = p.getVertexOnEdge(1, 1 - W_LANE_2 - W_LANE_2);
		Vec3 v4 = p.getVertexOnEdge(1, 1 - W_LANE_2);
		Vec3 v5 = p.get(2);
		Vec3 v6 = p.get(3);
		Vec3 v7 = p.getVertexOnEdge(3, W_LANE_2);
		Vec3 v8 = p.getVertexOnEdge(3, W_LANE_2 + W_LANE_2);
		Vec3 v9 = p.getVertexOnEdge(3, W_LANE_2 + W_LANE_2 + W_PARKING_2);

		meshes.add(IDesignEntity.createMesh(materials.streetMarkMaterial, makeMark(v7, v4, LANE_MARKING_WIDTH), I3DConfig.LAYER_1));
		population.addLane(new Polygon(v7, v4, v5, v6), EnumSet.of(Population.Type.CYCLISTS, Population.Type.CARS));
		population.addLane(new Polygon(v8, v3, v4, v7), EnumSet.of(Population.Type.CYCLISTS, Population.Type.CARS));
		population.addLane(new Polygon(v9, v2, v3, v8), EnumSet.of(Population.Type.CARS));

		meshes.add(IDesignEntity.createMesh(materials.streetMarkMaterial, makeMark(v8, v3, PARKING_MARKING_WIDTH), I3DConfig.LAYER_1));
		
		Polygon sidewalk = new Polygon(v0, v1, v2, v9);
		meshes.add(IDesignEntity.createMesh(materials.sidewalkMaterial, sidewalk, I3DConfig.LAYER_1));
		population.addLane(sidewalk, EnumSet.of(Population.Type.PERSONS));
	}

	private Polygon[] splitToInnerFixedWidth(Polygon p, float w) {
		Vec3 v0 = p.get(0);
		Vec3 v1 = p.get(1);
		Vec3 v4 = p.get(2);
		Vec3 v5 = p.get(3);
		float l0 = v5.distance(v0);
		float t6 = 0.5f * (1 - w / l0);
		float t7 = 1 - t6;
		float l1 = v4.distance(v1);
		float t2 = 0.5f * (1 - w / l1);
		float t3 = 1 - t2;
		
		
		Vec3 v2 = p.getVertexOnEdge(1, t2);
		Vec3 v3 = p.getVertexOnEdge(1, t3);
		Vec3 v6 = p.getVertexOnEdge(3, t6);
		Vec3 v7 = p.getVertexOnEdge(3, t7);
		return new Polygon[] {
			new Polygon(v0, v1, v2, v7),
			new Polygon(v7, v2, v3, v6),
			new Polygon(v4, v5, v6, v3)
		};
	}

	private Polygon[] splitToHalf(Polygon p) {
		Vec3 v0 = p.get(0);
		Vec3 v1 = p.get(1);
		Vec3 v4 = p.get(2);
		Vec3 v5 = p.get(3);
		Vec3 v2 = p.getVertexOnEdge(1, 0.5f);
		Vec3 v3 = p.getVertexOnEdge(3, 0.5f);
		return new Polygon[] {
			new Polygon(v0, v1, v2, v3),
			new Polygon(v4, v5, v3, v2)
		};
	}
	
	private Polygon makeMark(Vec3 p0, Vec3 p1, float width) {
		Vec3 d = p1.subtract(p0);
		float length = d.length();
		Vec3 m0 = new Vec3(1, -width / 2, 0);
		Vec3 m1 = new Vec3(length - 1, -width / 2, 0);
		Vec3 m2 = new Vec3(length - 1, width / 2, 0);
		Vec3 m3 = new Vec3(1, width / 2, 0);
		return new Polygon(m0, m1, m2, m3).transform(Mat4.trs(p0.x, p0.y, 0, 0, 0, (float)Math.toDegrees(Math.atan2(d.y, d.x)), 1, 1, 1));
	}
}
