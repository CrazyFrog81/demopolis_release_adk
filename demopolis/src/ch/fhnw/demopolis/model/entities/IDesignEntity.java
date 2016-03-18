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
import java.util.List;

import ch.fhnw.demopolis.model.Model;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Polygon;

public interface IDesignEntity {
	interface IButtonInfo {
		RGB getButtonColor();
		RGB getDesignColor();
	}

	enum Type {
		BLOCK, STREET, CROSSING, BRIDGE, BUILDING
	}

	Type getType();

	String getAsset();

	int getId();

	Polygon getShape();

	List<Polygon> getShapes();

	List<IMesh> getMeshes();
	
	void fade(float amount);
	
	List<IMesh> generate(Model model);

	static List<IMesh> getMeshes(List<IDesignEntity> entities) {
		List<IMesh> meshes = new ArrayList<>();
		for (IDesignEntity entity : entities) {
			meshes.addAll(entity.getMeshes());
		}
		return meshes;
	}
	
	static IMesh createMesh(IMaterial material, Polygon shape) {
		IGeometry geometry = DefaultGeometry.createVN(shape.getTriangleVertices(), null);
		IMesh mesh = new DefaultMesh(Primitive.TRIANGLES, material, geometry);
		return mesh;
	}
	
	static IMesh createMesh(IMaterial material, Polygon shape, float offset) {
		IMesh mesh = createMesh(material, shape);
		mesh.setPosition(new Vec3(0, 0, offset));
		return mesh;
	}
}
