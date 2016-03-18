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
import java.util.function.Function;

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.demopolis.model.Materials;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.Block.BlockType;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.util.FloatList;
import ch.fhnw.util.Pair;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.MathUtilities;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Basis;
import ch.fhnw.util.math.geometry.Line;
import ch.fhnw.util.math.geometry.Plane;
import ch.fhnw.util.math.geometry.Polygon;

public final class BuildingBlock extends AbstractDesignEntity {
	private static final float BLOCK_FADE = 0.5f;
	
	public static final float PERIMETER_RATIO = 4;
	public static final float MAX_PERIMETER_DEPTH = 15.0f;
	public static final float MIN_PERIMETER_DEPTH = 8.0f;
	
	public static final float MAX_SLAB_LENGTH = 75.0f;
	public static final float MAX_SLAB_DEPTH = 15.0f;
	
	public static final float MIN_RECT_SETBACK = 5.0f;
	public static final float MIN_RECT_LENGTH = 15.0f;
	public static final float MAX_RECT_LENGTH = 80.0f;
	
	public static final float MIN_POINT_SETBACK = 5.0f;
	public static final float MIN_POINT_LENGTH = 10.0f;
	public static final float MAX_POINT_LENGTH = 20.0f;

	public static final float SETBACK_0 = 1.0f; // we cheat a bit here...
	public static final float SETBACK_5 = 5.0f;
	public static final float SETBACK_8 = 8.0f;
	
	public static final float SETBACK_RATIO = 0.1f;
	
	public static final float HEIGHT_ONE_STOREY = 5.0f;
	public static final float HEIGHT_TWO_STOREY = 10.0f;
	public static final float HEIGHT_BERLIN_BLOCK = 22.0f;
	public static final float HEIGHT_64 = 64.0f;
	public static final float HEIGHT_150 = 150.0f;
	
	public static final float HEIGHT_DISTANCE_RATIO = 2.0f;
	public static final float HEIGHT_VARIATION = 0.1f;
	
	public static final float SPLIT_GAP_WIDTH = 10.0f;

	public static final float MIN_LOT_WIDTH = PERIMETER_RATIO * MIN_PERIMETER_DEPTH + SPLIT_GAP_WIDTH;
	
	
	public enum BuildingType implements IButtonInfo {
		NO_BUILDING(IUIColors.BLUE_C, IUIColors.BLUE_D, BuildingHeight.ONE_STOREY, BuildingHeight.ONE_STOREY, BuildingLine.STREET_EDGE, BuildingSetback.NO_SETBACK, BuildingUse.RESIDENTIAL),
		PERIMETER(IUIColors.GREEN_C, IUIColors.GREEN_D, BuildingHeight.BERLIN_BLOCK, BuildingHeight.BERLIN_BLOCK, BuildingLine.STREET_EDGE, BuildingSetback.NO_SETBACK, BuildingUse.RESIDENTIAL),
		PARALLEL(IUIColors.GREEN_B, IUIColors.GREEN_C, BuildingHeight.HIGHRISE_64M, BuildingHeight.BERLIN_BLOCK, BuildingLine.STREET_EDGE, BuildingSetback.NO_SETBACK, BuildingUse.RESIDENTIAL),
		RECTANGULAR(IUIColors.RED_C, IUIColors.RED_D, BuildingHeight.HIGHRISE_150M, BuildingHeight.BERLIN_BLOCK, BuildingLine.STREET_EDGE, BuildingSetback.HALF_HEIGHT, BuildingUse.MIXED),
		POINT(IUIColors.RED_B, IUIColors.RED_C, BuildingHeight.HIGHRISE_150M, BuildingHeight.HIGHRISE_64M, BuildingLine.STREET_EDGE, BuildingSetback.HALF_HEIGHT, BuildingUse.MIXED);
		
		BuildingType(RGB buttonColor, 
					 RGB designColor, 
					 BuildingHeight maxHeight,
					 BuildingHeight defaultHeight, 
					 BuildingLine defaultLine, 
					 BuildingSetback defaultSetback,
					 BuildingUse defaultUse) {
			this.buttonColor = buttonColor;
			this.designColor = designColor;
			this.maxHeight = maxHeight;
			this.defaultHeight = defaultHeight;
			this.defaultLine = defaultLine;
			this.defaultSetback = defaultSetback;
			this.defaultUse = defaultUse;
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
		public final BuildingHeight maxHeight;
		public final BuildingHeight defaultHeight;
		public final BuildingLine defaultLine;
		public final BuildingSetback defaultSetback;
		public final BuildingUse defaultUse;
	}
	
	public enum BuildingHeight implements IButtonInfo {
		ONE_STOREY(IUIColors.GREEN_B, IUIColors.GREEN_C, HEIGHT_ONE_STOREY),
		TWO_STOREY(IUIColors.GREEN_C, IUIColors.GREEN_D, HEIGHT_TWO_STOREY),
		BERLIN_BLOCK(IUIColors.BLUE_C, IUIColors.BLUE_D, HEIGHT_BERLIN_BLOCK),
		HIGHRISE_64M(IUIColors.RED_B, IUIColors.RED_C, HEIGHT_64),
		HIGHRISE_150M(IUIColors.RED_C, IUIColors.RED_D, HEIGHT_150);
		
		BuildingHeight(RGB buttonColor, RGB designColor, float maxHeight) {
			this.buttonColor = buttonColor;
			this.designColor = designColor;
			this.maxHeight = maxHeight;
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
		public final float maxHeight;
	}
	
	public enum BuildingLine implements IButtonInfo {
		STREET_EDGE(IUIColors.BLUE_C, IUIColors.BLUE_D, SETBACK_0),
		FROM_STREET_5M(IUIColors.RED_B, IUIColors.RED_C, SETBACK_5),
		FROM_STREET_8M(IUIColors.RED_C, IUIColors.RED_D, SETBACK_8);
		
		BuildingLine(RGB buttonColor, RGB designColor, float distance) {
			this.buttonColor = buttonColor;
			this.designColor = designColor;
			this.distance = distance;
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
		public final float distance;
	}
	
	public enum BuildingSetback implements IButtonInfo {
		NO_SETBACK(IUIColors.BLUE_C, IUIColors.BLUE_D),
		HALF_HEIGHT(IUIColors.RED_C, IUIColors.RED_D);
		
		BuildingSetback(RGB buttonColor, RGB designColor) {
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

	public enum BuildingUse implements IButtonInfo {
		RESIDENTIAL(IUIColors.BLUE_C, IUIColors.BLUE_D),
		COMMERCIAL(IUIColors.RED_C, IUIColors.RED_D),
		CULTURAL(IUIColors.GREEN_C, IUIColors.GREEN_D),
		RECREATIONAL(IUIColors.GREEN_B, IUIColors.GREEN_C),
		MIXED(IUIColors.YELLOW_C, IUIColors.YELLOW_D);
		
		BuildingUse(RGB buttonColor, RGB designColor) {
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
	
	public static class Building {
		private final Polygon lot;
		private final String id;
		private final List<Polygon> plan;

		private final PolisMaterial lotMaterial = new PolisMaterial(I3DColors.AMBIENT_LO, RGB.WHITE);
		private final PolisMaterial buildingMaterial = new PolisMaterial(I3DColors.AMBIENT_LO, RGB.WHITE);
		private List<IMesh> meshes;
		
		private final BuildingType type;
		private BuildingHeight height;
		private BuildingLine line;
		private BuildingSetback setback;
		private BuildingUse use;
		
		private List<Polygon> finalPlan;
		private float finalHeight;
		
		public Building(Polygon lot, String id, BuildingType type) {
			this.lot = lot;
			this.id = id;
			this.plan = createPlan(lot, type);
			this.type = type;
			this.height = type.defaultHeight;
			this.line = type.defaultLine;
			this.setback = type.defaultSetback;
			this.use = type.defaultUse;
			setColor(type.designColor);
		}
		
		public Polygon getLot() {
			return lot;
		}
		
		public String getId() {
			return id;
		}
		
		public List<Polygon> getPlan() {
			return plan;
		}
		
		public List<IMesh> getMeshes() {
			if (meshes == null) {
				meshes = new ArrayList<>();
				meshes.add(IDesignEntity.createMesh(lotMaterial, lot, I3DConfig.LAYER_1));
				for (Polygon p : plan)
					meshes.add(IDesignEntity.createMesh(buildingMaterial, p, I3DConfig.LAYER_2));
			}
			return meshes;
		}
		
		public void setColor(RGB color) {
			lotMaterial.setDiffuse(color.scaleRGB(I3DColors.HI * BLOCK_FADE));
			buildingMaterial.setDiffuse(color.scaleRGB(I3DColors.HI));
		}
		
		public BuildingType getType() {
			return type;
		}
		
		public BuildingHeight getHeight() {
			return height;
		}
		
		public void setHeight(BuildingHeight height) {
			this.height = height.ordinal() <= getType().maxHeight.ordinal() ? height : getType().maxHeight;
			setColor(this.height.designColor);
		}
		
		public BuildingLine getLine() {
			return line;
		}
		
		public void setLine(BuildingLine line) {
			this.line = line;
			setColor(line.designColor);
		}

		public BuildingSetback getSetback() {
			return setback;
		}

		public void setSetback(BuildingSetback setback) {
			if (type == BuildingType.PERIMETER)
				setback = BuildingSetback.NO_SETBACK;
			this.setback = setback;
			setColor(setback.designColor);
		}
		
		public BuildingUse getUse() {
			return use;
		}

		public void setUse(BuildingUse use) {
			this.use = use;
			setColor(use.designColor);
		}
		
		public float getFinalHeight() {
			return finalHeight;
		}
		
		public List<Polygon> getFinalPlan() {
			return finalPlan;
		}
		
		public void setFinalPlan(List<Polygon> finalPlan) {
			this.finalPlan = finalPlan;
		}
		
		public void setFinalHeight(float finalHeight) {
			this.finalHeight = finalHeight;
		}
		
		public List<IMesh> generate(Materials materials) {
			return generateBuilding(materials, this);
		}		
	}

	private final Polygon shape;
	private final List<Building> buildings = new ArrayList<>();

	public BuildingBlock(IDesignEntity entity) {
		super(entity);
		shape = entity.getShape();
		buildings.add(new Building(shape, getAsset(), BuildingType.NO_BUILDING));
	}
	
	public List<Building> getBuildings() {
		return buildings;
	}
	
	public void setBuildings(List<Building> buildings) {
		this.buildings.clear();
		this.buildings.addAll(buildings);
	}

	@Override
	public List<Polygon> getShapes() {
		return Collections.singletonList(shape);
	}
		
	@Override
	public List<IMesh> getMeshes() {
		List<IMesh> meshes = new ArrayList<>();
		buildings.forEach(b -> meshes.addAll(b.getMeshes()));
		return meshes;
	}
	
	@Override
	public void fade(float amount) {
		buildings.forEach(b -> {
			b.lotMaterial.setAmbient(I3DColors.AMBIENT_LO);
			b.lotMaterial.setDiffuse(BlockType.LEAVE_AS_IS.designColor.scaleRGB(amount * I3DColors.DIM));
			b.buildingMaterial.setAmbient(I3DColors.AMBIENT_HI);
			b.buildingMaterial.setDiffuse(I3DColors.BUILDING.scaleRGB(amount));
		});
	}
	
	@Override
	public List<IMesh> generate(Model model) {
		List<IMesh> meshes = new ArrayList<>();
		buildings.forEach(b -> meshes.addAll(b.generate(model.getMaterials())));
		return meshes;
	}
	
	public List<Building> createBuildings(BuildingType type, Vec3 position) {
		List<Building> bs = new ArrayList<>();
		bs.addAll(buildings);
		Building b = getBuilding(position);

		if (b == null)
			return bs;

		bs.remove(b);
		Vec3 v0 = b.lot.get(0);
		Vec3 u = b.lot.getU();
		Vec3 v = b.lot.getV();
		float eu = b.lot.getExtentU();
		float ev = b.lot.getExtentV();

		Plane plane0 = null;
		Plane plane1 = null;
		String first = b.id;
		String second = b.id;

		float du = Line.fromRay(v0, v).distance(position) / eu;
		float dv = Line.fromRay(v0, u).distance(position) / ev;
		if (du < 0.2f && eu > MIN_LOT_WIDTH) {
			plane0 = new Plane(v0.add(u.scale(eu / 2 + SPLIT_GAP_WIDTH / 2)), u);
			plane1 = new Plane(v0.add(u.scale(eu / 2 - SPLIT_GAP_WIDTH / 2)), u);
			first = first + "_r";
			second = second + "_l";
		} else if (du > 0.8f && eu > MIN_LOT_WIDTH) {
			plane0 = new Plane(v0.add(u.scale(eu / 2 - SPLIT_GAP_WIDTH / 2)), u.negate());
			plane1 = new Plane(v0.add(u.scale(eu / 2 + SPLIT_GAP_WIDTH / 2)), u.negate());
			first = first + "_l";
			second = second + "_r";
		} else if (dv < 0.2f && ev > MIN_LOT_WIDTH) {
			plane0 = new Plane(v0.add(v.scale(ev / 2 + SPLIT_GAP_WIDTH / 2)), v);
			plane1 = new Plane(v0.add(v.scale(ev / 2 - SPLIT_GAP_WIDTH / 2)), v);
			first = first + "_t";
			second = second + "_b";				
		} else if (dv > 0.8f && ev > MIN_LOT_WIDTH) {
			plane0 = new Plane(v0.add(v.scale(ev / 2 - SPLIT_GAP_WIDTH / 2)), v.negate());
			plane1 = new Plane(v0.add(v.scale(ev / 2 + SPLIT_GAP_WIDTH / 2)), v.negate());
			first = first + "_b";
			second = second + "_t";				
		} else {
			// center = dont split
		}

		if (plane0 == null) {
			bs.add(new Building(b.lot, b.id, type));
		} else {
			try {
				Pair<Polygon, Polygon> split0 = b.lot.split(plane0);
				Pair<Polygon, Polygon> split1 = split0.second.split(plane1);
				bs.add(new Building(split0.first, first, b.type));
				bs.add(new Building(split1.first, b.id + "_m", BuildingType.NO_BUILDING));
				bs.add(new Building(split1.second, second, type));
			} catch (Exception e) {
				// revert in case where split goes wrong
				bs.add(new Building(b.lot, b.id, type));
			}
		}

		boolean allEmpty = true;
		for (Building bb : bs) {
			if (bb.type != BuildingType.NO_BUILDING) {
				allEmpty = false;
				break;
			}
		}
		if (allEmpty) {
			bs.clear();
			bs.add(new Building(shape, getAsset(), BuildingType.NO_BUILDING));
		}
		return bs;
	}
	
	public void setDefaultColor(Function<Building, RGB> function) {
		buildings.forEach(b -> b.setColor(function.apply(b)));		
	}

	public Building getBuilding(Vec3 position) {
		for (Building b : buildings) {
			if (b.getLot().project(position) != null)
				return b;
		}
		return null;
	}
	
	public static void setFinalBuildingHeights(List<Building> buildings) {
		// TODO: warning - the current implementation doesn't scale for many buildings...
		for (Building b0 : buildings) {
			if (b0.getPlan().isEmpty())
				continue;

			BuildingHeight h0 = b0.getHeight();
			if (h0 == BuildingHeight.BERLIN_BLOCK) {
				b0.setFinalHeight(h0.maxHeight);
				continue;
			}
			if (h0 == BuildingHeight.ONE_STOREY || h0 == BuildingHeight.TWO_STOREY) {
				b0.setFinalHeight(getHeight(h0.maxHeight));
				continue;
			}
				
			float height = h0.maxHeight;
			Vec3 c0 = getCenter(b0.getPlan());
			for (Building b1 : buildings) {
				if (b0 == b1 || b1.getPlan().isEmpty())
					continue;
				Vec3 c1 = getCenter(b1.getPlan());
				float d = c1.distance(c0);
				height = Math.min(height, d * HEIGHT_DISTANCE_RATIO);
			}
			height = getHeight(Math.max(height, HEIGHT_ONE_STOREY));
			b0.setFinalHeight(height);
		}
	}
	
	
	private static final FloatList TRI = new FloatList();

	private static List<IMesh> generateBuilding(Materials materials, Building building) {
		List<IMesh> meshes = new ArrayList<>();

		// add lot
		meshes.add(IDesignEntity.createMesh(materials.blockMaterial, building.lot, I3DConfig.LAYER_1));
		
		// calculate building line
		Polygon lot = building.getLot().offset(building.getLine().distance);
		if (lot == null)
			return meshes;
		
		// re-calculate final building plan
		List<Polygon> plan = createPlan(lot, building.getType());
		if (plan.isEmpty())
			return meshes;
		
		// generate building triangles
		TRI.clear();
		switch (building.getType()) {
		case NO_BUILDING:
			break;
		case PERIMETER: {
			for (Polygon p : plan) {
				for (Polygon e : p.extrude(building.finalHeight, false, true))
					TRI.addAll(e.getTriangleVertices());
			}
			break;
		}
		case PARALLEL:
		case POINT: 
		case RECTANGULAR: {
			if (building.getSetback() == BuildingSetback.NO_SETBACK) {
				for (Polygon p : plan) {
					for (Polygon e : p.extrude(building.finalHeight, false, true))
						TRI.addAll(e.getTriangleVertices());
				}
			} else {
				for (Polygon p : plan) {
					Pair<Polygon, Polygon> split = p.split(0, p.getExtentV() * SETBACK_RATIO);
					if (split.first != null) {
						for (Polygon e : split.first.extrude(building.finalHeight / 2, false, true))
							TRI.addAll(e.getTriangleVertices());
					}
					if (split.second != null) {
						for (Polygon e : split.second.extrude(building.finalHeight, false, true))
							TRI.addAll(e.getTriangleVertices());
					}
				}				
			}
			break;
		}
		}
		if (!TRI.isEmpty())
			meshes.add(new DefaultMesh(Primitive.TRIANGLES, materials.buildingMaterial, DefaultGeometry.createVN(TRI.toArray(), null)));
		return meshes;		
	}
	
	
	private static List<Polygon> createPlan(Polygon lot, BuildingType type) {
		switch (type) {
		case NO_BUILDING:
			return Collections.emptyList();
		case PERIMETER: {
			float u = lot.getExtentU();
			float v = lot.getExtentV();
			float uv = Math.min(u, v);
			float depth = Math.min(MAX_PERIMETER_DEPTH, uv / PERIMETER_RATIO);
			return asList(createOffsetPolygon(lot, depth));
		}
		case PARALLEL: {
			Polygon inner = createInnerRectangle(lot);
			if (inner == null)
				return Collections.emptyList();

			List<Polygon> plan = new ArrayList<>();
			Basis basis = inner.getBasis();
			Polygon xy = inner.transform(basis.getUVWToXYZTransform());
			float u = xy.getExtentU();
			float v = xy.getExtentV();
			float l = Math.min(u, MAX_SLAB_LENGTH);
			float w = 2.5f * MAX_SLAB_DEPTH;
			float x0 = u / 2 - l / 2;
			float x1 = u / 2 + l / 2;

			int n = (int)(v / w);
			for (int i = 0; i < n; ++i) {
				float y0 = i * w;
				float y1 = i * w + MAX_SLAB_DEPTH;				
				Polygon slab = new Polygon(new Vec3(x0, y0, 0), new Vec3(x1, y0, 0), new Vec3(x1, y1, 0), new Vec3(x0, y1, 0));
				plan.add(slab.transform(basis.getXYZToUVWTransform()));
			}
			return plan;
		}
		case RECTANGULAR: {
			Polygon inner = createInnerRectangle(lot);
			if (inner == null)
				return Collections.emptyList();

			Basis basis = inner.getBasis();
			Polygon xy = inner.transform(basis.getUVWToXYZTransform());
			float u = xy.getExtentU();
			float v = xy.getExtentV();
			float umin = u - 2 * MIN_RECT_SETBACK;
			float vmin = v - 2 * MIN_RECT_SETBACK;
			float l = Math.min(MAX_RECT_LENGTH, umin);
			float w = Math.min(MAX_RECT_LENGTH / 2, vmin);
			if (l < MIN_RECT_LENGTH || w < MIN_RECT_LENGTH)
				return Collections.emptyList();
			float x0 = u / 2 - l / 2;
			float x1 = u / 2 + l / 2;
			float y0 = v / 2 - w / 2;
			float y1 = v / 2 + w / 2;
			Polygon rect = new Polygon(new Vec3(x0, y0, 0), new Vec3(x1, y0, 0), new Vec3(x1, y1, 0), new Vec3(x0, y1, 0));
			return asList(rect.transform(basis.getXYZToUVWTransform()));
		}
		case POINT: {
			Polygon inner = createInnerRectangle(lot);
			if (inner == null)
				return Collections.emptyList();

			Basis basis = inner.getBasis();
			Polygon xy = inner.transform(basis.getUVWToXYZTransform());
			float u = xy.getExtentU();
			float v = xy.getExtentV();
			float min = Math.min(u, v) - 2 * MIN_POINT_SETBACK;
			float l = Math.min(MAX_POINT_LENGTH, min);
			if (l < MIN_POINT_LENGTH)
				return Collections.emptyList();
			float x0 = u / 2 - l / 2;
			float x1 = u / 2 + l / 2;
			float y0 = v / 2 - l / 2;
			float y1 = v / 2 + l / 2;
			Polygon point = new Polygon(new Vec3(x0, y0, 0), new Vec3(x1, y0, 0), new Vec3(x1, y1, 0), new Vec3(x0, y1, 0));
			return asList(point.transform(basis.getXYZToUVWTransform()));
		}
		}
		throw new IllegalArgumentException("invalid building type " + type);
	}
	
	private static List<Polygon> asList(Polygon polygon) {
		return Collections.singletonList(polygon);
	}
	
	private static Polygon createOffsetPolygon(Polygon outer, float offset) {
		if (!outer.isConvex())
			throw new IllegalArgumentException("polygon must be convex");

		Polygon inner = outer.offset(offset);
		if (inner == null)
			return outer;
		
		// connect vertices (this works in most cases, but likely not always)
		int n = outer.getNumVertices();
		int m = inner.getNumVertices();
		List<Vec3> v = new ArrayList<>(n + m);
		for (int i = 0; i <= n; ++i)
			v.add(outer.get(i));
		for (int i = m; i >= 0; --i)
			v.add(inner.get(i));
		return new Polygon(v);
	}
	
	private static Polygon createInnerRectangle(Polygon outer) {
		Vec3 v = outer.getV().scale(outer.getExtentV());
		Vec3 v0 = outer.get(0);
		Vec3 v1 = outer.get(1);
		float d0 = outer.get(-1).subtract(v0).dot(v1.subtract(v0));
		float d1 = outer.get(2).subtract(v1).dot(v0.subtract(v1));
		if (d0 > 0)
			v0 = outer.getVertexOnEdge(0, 0.15f);
		if (d1 > 0)
			v1 = outer.getVertexOnEdge(0, 0.85f);

		for (float scale = 1; scale > 0; scale -= 0.1f) {
			Vec3 d = v.scale(scale);			
			Vec3 v2 = v1.add(d);
			Vec3 v3 = v0.add(d);
			if (outer.project(v2) != null && outer.project(v3) != null)
				return new Polygon(v0, v1, v2, v3);
		}
		return null;
	}

	private static Vec3 getCenter(List<Polygon> plan) {
		Vec3 v = Vec3.ZERO;
		for (Polygon p : plan)
			v = v.add(p.getCenter());
		v = v.scale(1.0f / plan.size());
		return v;
	}
	
	private static float getHeight(float height) {
		float v = 0.5f * HEIGHT_VARIATION;
		return MathUtilities.random(height - v, height + v);
	}
}
