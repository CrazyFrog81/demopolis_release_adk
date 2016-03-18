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

package ch.fhnw.demopolis.render;

import assets.Asset;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.render.variable.base.FloatUniform;
import ch.fhnw.ether.render.variable.base.Vec3FloatUniform;
import ch.fhnw.ether.render.variable.builtin.LightUniformBlock;
import ch.fhnw.ether.render.variable.builtin.NormalArray;
import ch.fhnw.ether.render.variable.builtin.PositionArray;
import ch.fhnw.ether.render.variable.builtin.ViewUniformBlock;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.AbstractMaterial;
import ch.fhnw.ether.scene.mesh.material.ICustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGB;

public final class PolisMaterial extends AbstractMaterial implements ICustomMaterial {
	private static class PolisShader extends AbstractShader {
		PolisShader() {
			super(Asset.class, "demopolis.panel_shader", "polis_shader_v", Primitive.TRIANGLES);

			addArray(new PositionArray());
			addArray(new NormalArray());

			addUniform(new Vec3FloatUniform(IMaterial.AMBIENT, "material.ambientColor"));
			addUniform(new Vec3FloatUniform(IMaterial.DIFFUSE, "material.diffuseColor"));
			addUniform(new FloatUniform(IMaterial.ALPHA, "material.alpha"));
			
			addUniform(new ViewUniformBlock());
			addUniform(new LightUniformBlock());
		}
	}

	private final IShader shader = new PolisShader();
	
	private RGB ambient;
	private RGB diffuse;
	private float alpha;

	public PolisMaterial(RGB diffuse) {
		this(RGB.WHITE, diffuse, 1);
	}
	
	public PolisMaterial(RGB ambient, RGB diffuse) {
		this(ambient, diffuse, 1);
	}

	public PolisMaterial(RGB ambient, RGB diffuse, float alpha) {
		super(material(IMaterial.AMBIENT, IMaterial.DIFFUSE, IMaterial.ALPHA), 
			  geometry(IGeometry.POSITION_ARRAY, IGeometry.NORMAL_ARRAY));
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.alpha = alpha;
	}
	
	public RGB getAmbient() {
		return ambient;
	}
	
	public void setAmbient(RGB ambient) {
		this.ambient = ambient;
		updateRequest();
	}
	
	public RGB getDiffuse() {
		return diffuse;
	}
	
	public void setDiffuse(RGB diffuse) {
		this.diffuse = diffuse;
		updateRequest();
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
		updateRequest();
	}

	@Override
	public IShader getShader() {
		return shader;
	}

	@Override
	public Object[] getData() {
		return data(ambient, diffuse, alpha);
	}

}
