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
import java.util.List;

import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.model.entities.IDesignEntity.Type;
import ch.fhnw.demopolis.model.entities.StaticModel;
import ch.fhnw.demopolis.model.entities.StaticShape;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public interface IScenario {
	static IDesignEntity block(String asset) {
		try {
			Type type;
			int id = id(asset);
			if (asset.contains("block_bl") || asset.contains("block_nop")) {
				type = Type.BLOCK;
			} else if (asset.contains("block_s")) {
				type = Type.STREET;
			} else if (asset.contains("block_c")) {
				type = Type.CROSSING;
			} else if (asset.contains("block_br")) {
				type = Type.BRIDGE;
			} else {
				throw new IllegalArgumentException("unknown block asset type: " + asset);
			}
			return new StaticShape(type, asset, id);
		} catch (IOException e) {
			throw new IllegalArgumentException("cant load asset: " + asset);
		}
	}

	static IDesignEntity building(String asset) {
		try {
			int id = id(asset);
			return new StaticModel(asset, id);
		} catch (IOException e) {
			throw new IllegalArgumentException("cant load asset: " + asset);
		}
	}

	static int id(String asset) {
		return Integer.parseInt(asset.substring(asset.length() - 6, asset.length() - 4));
	}
	
	String getName();

	String[] getStaticGround();

	String[] getStaticWater();

	String[] getStaticBlocks();

	String[] getStaticGreen();

	String[] getStaticBuildings();

	String[] getStaticTrain();

	List<IDesignEntity> getEntities();
	
	List<Vec3> getIntroCameraVertices();
	
	List<Vec3> getLoopCameraVertices();
	
	BoundingBox getBounds();
}
