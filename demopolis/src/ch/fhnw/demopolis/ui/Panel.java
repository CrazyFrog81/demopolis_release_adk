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
package ch.fhnw.demopolis.ui;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.demopolis.render.PanelMaterial;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.IMesh.Queue;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.IGeometryAttribute;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec2;
import ch.fhnw.util.math.Vec3;

public class Panel {
	public enum Position {
		LEFT, RIGHT
	}

	public static final int MAX_BUTTONS = 12;

	public static final float SCREEN_W = 1920;
	public static final float SCREEN_H = 1080;
	public static final float SCREEN_A = SCREEN_W / SCREEN_H;

	public static final float PANEL_W = 280;
	public static final float PANEL_H = 1080;
	public static final float PANEL_A = PANEL_W / PANEL_H;

	public static final float BUTTON_O = 10;
	public static final float BUTTON_W = PANEL_W - 2 * BUTTON_O;
	public static final float BUTTON_H = 70;

	public static final RGB COLOR_TITLE = IUIColors.GRAY_B;

	private final IMesh mesh;
	private final Position position;
	private boolean visible;

	public Panel(Texture texture, Position position) {
		this.mesh = new DefaultMesh(Primitive.TRIANGLES, new PanelMaterial(texture), createPanelGeometry(), Queue.SCREEN_SPACE_OVERLAY);
		this.position = position;
		setButtonColor(0, COLOR_TITLE);
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void show(IController controller) {
		if (!visible)
			controller.getRenderManager().addMesh(mesh);
		else 
			System.err.println("warning: panel already visible");
		visible = true;
	}
	
	public void hide(IController controller) {
		if (visible)
			controller.getRenderManager().removeMesh(mesh);
		else
			System.err.println("warning: panel already hidden");
		visible = false;	
	}

	public IMesh getMesh() {
		return mesh;
	}

	public Position getPosition() {
		return position;
	}

	Texture getTexture() {
		return ((PanelMaterial) mesh.getMaterial()).getTexture();
	}

	void setTexture(Texture texture) {
		((PanelMaterial) mesh.getMaterial()).setTexture(texture);
	}

	int getButton(float x, float y, float w, float h) {
		if (position == Position.RIGHT)
			x -= (w - h * PANEL_A);
		x *= PANEL_H / h;
		y *= PANEL_H / h;
		if (x < BUTTON_O || x > BUTTON_O + BUTTON_W)
			return -1;
		y = PANEL_H - y;
		int button = (int) (y / (BUTTON_O + BUTTON_H));
		y = y % (BUTTON_O + BUTTON_H);
		if (y < BUTTON_O)
			return -1;
		if (button >= MAX_BUTTONS)
			return -1;
		return button;
	}

	void setButtonColor(int button, RGB color) {
		mesh.getGeometry().modify(1, new IGeometry.IAttributeVisitor() {
			@Override
			public void visit(IGeometryAttribute attribute, float[] data) {
				// note that button index 0 would be overall panel rectangle,
				// thus button + 1
				int offset = (button + 1) * 24;
				for (int i = offset; i < offset + 24;) {
					data[i++] = color.r;
					data[i++] = color.g;
					data[i++] = color.b;
					data[i++] = 1;
				}
			}
		});
	}

	public void viewResized(float w, float h) {
		float sx = h / PANEL_H;
		float sy = h / PANEL_H;
		float tx = position == Position.LEFT ? 0 : w - h * PANEL_A;
		mesh.setTransform(Mat4.trs(tx, 0, 0, 0, 0, 0, sx, sy, 1));
	}

	private static IGeometry createPanelGeometry() {
		List<Vec3> vertices = new ArrayList<>();
		List<RGBA> colors = new ArrayList<>();
		List<Vec2> texCoords = new ArrayList<>();

		// add main rectangle
		addRectangle(vertices, colors, texCoords, 0, 0, PANEL_W, PANEL_H, RGBA.BLACK);

		// add 12 buttons
		for (int i = 1; i <= MAX_BUTTONS; ++i) {
			float y = PANEL_H - i * (BUTTON_O + BUTTON_H);
			addRectangle(vertices, colors, texCoords, BUTTON_O, y, BUTTON_W, BUTTON_H, RGBA.BLACK);
		}

		return DefaultGeometry.createVCM(Vec3.toArray(vertices), RGBA.toArray(colors), Vec2.toArray(texCoords));
	}

	private static void addRectangle(List<Vec3> v, List<RGBA> c, List<Vec2> t, float x, float y, float w, float h, RGBA color) {
		float x0 = x;
		float y0 = y;
		float x1 = x + w;
		float y1 = y + h;
		float s0 = x0 / PANEL_W;
		float t0 = y0 / PANEL_H;
		float s1 = x1 / PANEL_W;
		float t1 = y1 / PANEL_H;
		v.add(new Vec3(x0, y0, 0));
		v.add(new Vec3(x1, y0, 0));
		v.add(new Vec3(x1, y1, 0));
		v.add(new Vec3(x0, y0, 0));
		v.add(new Vec3(x1, y1, 0));
		v.add(new Vec3(x0, y1, 0));
		t.add(new Vec2(s0, t0));
		t.add(new Vec2(s1, t0));
		t.add(new Vec2(s1, t1));
		t.add(new Vec2(s0, t0));
		t.add(new Vec2(s1, t1));
		t.add(new Vec2(s0, t1));
		c.add(color);
		c.add(color);
		c.add(color);
		c.add(color);
		c.add(color);
		c.add(color);
	}
}
