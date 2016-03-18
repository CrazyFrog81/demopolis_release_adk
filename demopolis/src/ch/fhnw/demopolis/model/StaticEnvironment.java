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
import java.util.List;

import assets.Asset;
import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.ether.formats.obj.ObjReader;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.math.Vec3;

public class StaticEnvironment {
	private IMesh ground;
	private IMesh water;
	private IMesh blocks;
	private IMesh green;
	private IMesh buildings;
	private IMesh train;
	
	public StaticEnvironment() {
	}

	public void load(Model model) throws IOException {
		IScenario scenario = model.getScenario();
		Materials materials = model.getMaterials();
		ground = getMesh(scenario.getStaticGround(), materials.groundMaterial);
		water = getMesh(scenario.getStaticWater(), materials.waterMaterial);
		blocks = getMesh(scenario.getStaticBlocks(), materials.blockMaterial);
		green = getMesh(scenario.getStaticGreen(), materials.greenMaterial);
		buildings = getMesh(scenario.getStaticBuildings(), materials.buildingMaterial);
		train = getMesh(scenario.getStaticTrain(), materials.trainMaterial);

		blocks.setPosition(new Vec3(0, 0, I3DConfig.LAYER_1));
		green.setPosition(new Vec3(0, 0, I3DConfig.LAYER_2));
	}
	
	public void addToScene(IScene scene) {
		scene.add3DObjects(ground, water, blocks, green, buildings, train);		
	}
	
	public void removeFromScene(IScene scene) {
		scene.remove3DObjects(ground, water, blocks, green, buildings, train);		
	}
		
	private static IMesh getMesh(String[] assets, IMaterial material) throws IOException {
		List<IMesh> meshes = new ArrayList<>();
		for (String asset : assets) {
			meshes.addAll(new ObjReader(Asset.get(asset), ObjReader.Options.CONVERT_TO_Z_UP).getMeshes(material));
		}
		meshes = MeshUtilities.mergeMeshes(meshes);
		if (meshes.size() != 1)
			throw new IllegalArgumentException("cannot merge meshes into a single mesh");
		return meshes.get(0);
	}
}
