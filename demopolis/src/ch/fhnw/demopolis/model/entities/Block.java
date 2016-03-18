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

import java.util.Collections;
import java.util.List;

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.BuildingBlock.Building;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.geometry.Polygon;

public final class Block extends AbstractDesignEntity {
	
	public enum BlockType implements IButtonInfo {
		LEAVE_AS_IS(IUIColors.BLUE_C, IUIColors.BLUE_D),
		BUILT_SPACE(IUIColors.RED_C, IUIColors.RED_D),
		OPEN_SPACE(IUIColors.GREEN_C, IUIColors.GREEN_D);
		
		BlockType(RGB buttonColor, RGB designColor) {
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

	private BlockType blockType;

	private final PolisMaterial material;
	private final Polygon shape;
	private IMesh mesh;

	public Block(IDesignEntity entity) {
		super(entity);
		blockType = BlockType.LEAVE_AS_IS;
		material = new PolisMaterial(I3DColors.AMBIENT_LO, blockType.designColor);
		shape = entity.getShape();
	}
	
	public Block(IDesignEntity entity, Building building) {
		super(Type.BLOCK, building.getId(), entity.getId());
		blockType = BlockType.OPEN_SPACE;
		material = new PolisMaterial(I3DColors.AMBIENT_LO, blockType.designColor);
		shape = building.getLot();
	}
	
	public BlockType getBlockType() {
		return blockType;
	}
	
	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
		material.setDiffuse(blockType.designColor);
	}
	
	@Override
	public List<Polygon> getShapes() {
		return Collections.singletonList(shape);
	}
		
	@Override
	public List<IMesh> getMeshes() {
		if (mesh == null) {
			mesh = IDesignEntity.createMesh(material, shape, I3DConfig.LAYER_1);
		}
		return Collections.singletonList(mesh);
	}
	
	@Override
	public void fade(float amount) {
		material.setDiffuse(blockType.designColor.scaleRGB(amount * I3DColors.DIM));
	}

	@Override
	public List<IMesh> generate(Model model) {
		return Collections.singletonList(IDesignEntity.createMesh(model.getMaterials().blockMaterial, shape, I3DConfig.LAYER_1));
	}
}
