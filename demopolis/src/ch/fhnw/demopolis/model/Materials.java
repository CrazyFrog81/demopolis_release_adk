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

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.render.PolisMaterial;
import ch.fhnw.util.color.RGB;

public final class Materials {
	public final PolisMaterial groundMaterial = new PolisMaterial(I3DColors.GROUND);
	public final PolisMaterial waterMaterial = new PolisMaterial(I3DColors.WATER);
	public final PolisMaterial blockMaterial = new PolisMaterial(I3DColors.BLOCK);
	public final PolisMaterial greenMaterial = new PolisMaterial(I3DColors.GREEN);
	public final PolisMaterial buildingMaterial = new PolisMaterial(I3DColors.BUILDING);
	public final PolisMaterial trainMaterial = new PolisMaterial(I3DColors.TRAIN);
	
	public final PolisMaterial shrubMaterial = new PolisMaterial(I3DColors.SHRUB, I3DColors.SHRUB);
	public final PolisMaterial treeMaterial = new PolisMaterial(I3DColors.TREE, I3DColors.TREE);
	public final PolisMaterial trunkMaterial = new PolisMaterial(I3DColors.TRUNK, I3DColors.TRUNK);
	
	public final PolisMaterial streetMaterial = groundMaterial;
	public final PolisMaterial streetMarkMaterial = buildingMaterial;
	public final PolisMaterial sidewalkMaterial = new PolisMaterial(I3DColors.SIDEWALK);
	
	public void fade(float factor) {
		groundMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.GROUND, factor));	
		waterMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.WATER, factor));
		blockMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.BLOCK, factor));
		greenMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.GREEN, factor));
		buildingMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.BUILDING, factor));
		trainMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.TRAIN, factor));

		shrubMaterial.setAmbient(RGB.mix(RGB.WHITE, I3DColors.SHRUB, factor));
		shrubMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.SHRUB, factor));
		treeMaterial.setAmbient(RGB.mix(RGB.WHITE, I3DColors.TREE, factor));
		treeMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.TREE, factor));
		trunkMaterial.setAmbient(RGB.mix(RGB.WHITE, I3DColors.TRUNK, factor));
		trunkMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.TRUNK, factor));
		
		sidewalkMaterial.setDiffuse(RGB.mix(I3DColors.FADED, I3DColors.SIDEWALK, factor));
	}

	public PolisMaterial getFinalMaterial(IDesignEntity e) {
		switch (e.getType()) {
		case BLOCK:
			return blockMaterial;
		case STREET:
		case CROSSING:
		case BRIDGE:
			return groundMaterial;
		case BUILDING:
			return buildingMaterial;
		}
		throw new IllegalArgumentException("invalide entity type: " + e.getType());		
	}
}
