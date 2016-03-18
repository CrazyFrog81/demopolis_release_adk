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

import ch.fhnw.demopolis.model.Model;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.controller.tool.AbstractTool;
import ch.fhnw.ether.controller.tool.ITool;
import ch.fhnw.ether.controller.tool.PickUtilities;
import ch.fhnw.ether.controller.tool.PickUtilities.PickMode;
import ch.fhnw.ether.scene.camera.DefaultCameraControl;
import ch.fhnw.ether.scene.camera.IViewCameraState;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.IMesh.Primitive;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.material.LineMaterial;
import ch.fhnw.ether.scene.mesh.material.PointMaterial;
import ch.fhnw.ether.view.ProjectionUtilities;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Line;
import ch.fhnw.util.math.geometry.Plane;
import ch.fhnw.util.math.path.CatmullRomSpline;
import ch.fhnw.util.math.path.IPath;

public class CameraPath {
	private final float POINT_SIZE = 20;
	private final float PATH_DELTA = 0.001f;
	private final float KEY_DELTA = 1;
	
	private final float SPEED = 0.5f;
	
	private final float CAMERA_DAMP = 0.05f;
	private final float CAMREA_ROT_SCALE = 500f;
	
	private final IController controller;
	
	private final List<Vec3> introVertices;
	private final List<Vec3> loopVertices;
	
	private IPath introPath;
	private IPath loopPath;
	
	private IPath currentPath;
	
	private IMesh introPoints;
	private IMesh introLines;
	private IMesh loopPoints;
	private IMesh loopLines;

	private List<Vec3> currentVertices = null;
	private int currentVertexIndex = -1;
	
	private boolean animationRunning = false;
	private double animationStart = 0;

	Vec3 lastCameraDirection = Vec3.ZERO;
	float lastCameraRotation = 0;
	
	
	public CameraPath(IController controller, Model model) {
		this.controller = controller;
	
		introVertices = model.getScenario().getIntroCameraVertices();
		loopVertices = model.getScenario().getLoopCameraVertices();
		
		updatePaths();
		
		controller.animate((t, i) -> animate(t, i));
	}
	
	public void show() {
		if (introPoints == null)
			updateMeshes();
		controller.getScene().add3DObjects(introPoints, introLines, loopPoints, loopLines);
	}
	
	public void hide() {
		if (introPoints == null)
			return;
		controller.getScene().remove3DObjects(introPoints, introLines, loopPoints, loopLines);
	}
	
	public void toggleAnimation() {
		if (!animationRunning)
			startAnimation();
		else
			stopAnimation();
	}
	
	public void startAnimation() {
		animationRunning = true;
		animationStart = controller.getScheduler().getTime();
		currentPath = introPath;
		lastCameraDirection = Vec3.ZERO;
		lastCameraRotation = 0;		
	}
	
	public void stopAnimation() {
		animationRunning = false;
	}
	
	public ITool getPathTool() {
		return new AbstractTool(controller) {
			
			@Override
			public void keyPressed(IKeyEvent e) {
				float dx = 0;
				float dy = 0;
				float dz = 0;
				switch (e.getKeyCode()) {
				case IKeyEvent.VK_L:
					toggleAnimation();
					break;
				case IKeyEvent.VK_D:
					// dump paths
					System.out.println("// intro");
					for (Vec3 v : introVertices)
						System.out.println("new Vec3(" + v.x + ", " + v.y + ", " + v.z + "),");
					System.out.println("// loop");
					for (Vec3 v : loopVertices)
						System.out.println("new Vec3(" + v.x + ", " + v.y + ", " + v.z + "),");
					break;
				case IKeyEvent.VK_RIGHT:
					dx = KEY_DELTA;
					break;
				case IKeyEvent.VK_LEFT:
					dx = -KEY_DELTA;
					break;
				case IKeyEvent.VK_UP:
					dy = KEY_DELTA;
					break;
				case IKeyEvent.VK_DOWN:
					dy = -KEY_DELTA;
					break;
				case IKeyEvent.VK_SLASH:
					dz = KEY_DELTA;
					break;
				case IKeyEvent.VK_PERIOD:
					dz = -KEY_DELTA;
					break;
				default:
					return;
				}
				if (currentVertices != null && (dx != 0 || dy != 0 || dz != 0)) {
					Vec3 v = currentVertices.get(currentVertexIndex).add(new Vec3(dx, dy, dz).scale(e.isShiftDown() ? 0.1f : 1f));
					currentVertices.set(currentVertexIndex, v);
					updatePaths();
					updateMeshes();
				}
			}
			
			@Override
			public void pointerPressed(IPointerEvent e) {
				IViewCameraState vcs = controller.getRenderManager().getViewCameraState(e.getView());
				pickVertex(e.getX(), e.getY(), vcs);
			}
			
			@Override
			public void pointerDragged(IPointerEvent e) {
				if (currentVertices == null)
					return;
				Vec3 v = currentVertices.get(currentVertexIndex);
				
				IViewCameraState vcs = controller.getRenderManager().getViewCameraState(e.getView());
				Line line = ProjectionUtilities.getRay(vcs, e.getX(), e.getY());
				Plane plane = new Plane(v, new Vec3(0, 0, 1));
				Vec3 p = plane.intersect(line);
				if (p != null) {
					currentVertices.set(currentVertexIndex, p);
					updatePaths();
					updateMeshes();
				}
			}
		};
	}
	
	private void animate(double time, double interval) {
		if (!animationRunning || currentPath == null || controller.getCurrentView() == null)
			return;

		float t = (float)(time - animationStart) * SPEED / currentPath.getNumNodes();
		
		if (t >= 1) {
			t = 0;
			animationStart = time;
			currentPath = loopPath;
		}
		
		DefaultCameraControl dcc = new DefaultCameraControl(controller.getCamera(controller.getCurrentView()));
		Vec3 p = currentPath.position(t);
		Vec3 v = currentPath.velocity(t);
		
		Vec3 newD = lastCameraDirection.scale(1 - CAMERA_DAMP).add(v.normalize().scale(CAMERA_DAMP));
		
		Vec3 c = lastCameraDirection.cross(newD);
		
		float newR = (1 - CAMERA_DAMP) * lastCameraRotation + CAMERA_DAMP * (-c.z * CAMREA_ROT_SCALE);
		
		Mat4 rot = Mat4.rotate(newR, newD);
		Vec3 up = rot.transform(Vec3.Z);
		
		dcc.setPosition(p);
		dcc.setTarget(p.add(newD));
		dcc.setUp(up);
		
		lastCameraDirection = newD;
		lastCameraRotation = newR;
	}
	
	private void updatePaths() {
		introPath = new CatmullRomSpline(introVertices, false);
		loopPath = new CatmullRomSpline(loopVertices, true);
	}
	
	private void updateMeshes() {
		if (introPoints == null) {
			introPoints = new DefaultMesh(Primitive.POINTS, new PointMaterial(RGBA.YELLOW, POINT_SIZE), DefaultGeometry.createV(new float[0]));
			introLines = new DefaultMesh(Primitive.LINE_STRIP, new LineMaterial(RGBA.YELLOW), DefaultGeometry.createV(new float[0]));
			loopPoints = new DefaultMesh(Primitive.POINTS, new PointMaterial(RGBA.GREEN, POINT_SIZE), DefaultGeometry.createV(new float[0]));
			loopLines = new DefaultMesh(Primitive.LINE_STRIP, new LineMaterial(RGBA.GREEN), DefaultGeometry.createV(new float[0]));
		}
		introPoints.getGeometry().modify((attributes, data) -> {
			data[0] = Vec3.toArray(introVertices);
		});

		introLines.getGeometry().modify((attributes, data) -> {
			List<Vec3> lines = new ArrayList<>();
			for (float t = 0; t < 1; t += PATH_DELTA)
				lines.add(introPath.position(t));
			data[0] = Vec3.toArray(lines);
		});
		
		loopPoints.getGeometry().modify((attributes, data) -> {
			data[0] = Vec3.toArray(loopVertices);
		});

		loopLines.getGeometry().modify((attributes, data) -> {
			List<Vec3> lines = new ArrayList<>();
			for (float t = 0; t < 1; t += PATH_DELTA)
				lines.add(loopPath.position(t));
			data[0] = Vec3.toArray(lines);
		});
	}
	
	private void pickVertex(int x, int y, IViewCameraState vcs) {
		for (int i = 0; i < introVertices.size(); ++i) {
			Vec3 v = introVertices.get(i);
			if (PickUtilities.pickPoints(PickMode.POINT, x, y, 0, 0, vcs, v.toArray()) < Float.POSITIVE_INFINITY) {
				currentVertices = introVertices;
				currentVertexIndex = i;
				return;
			}
		}
		for (int i = 0; i < loopVertices.size(); ++i) {
			Vec3 v = loopVertices.get(i);
			if (PickUtilities.pickPoints(PickMode.POINT, x, y, 0, 0, vcs, v.toArray()) < Float.POSITIVE_INFINITY) {
				currentVertices = loopVertices;
				currentVertexIndex = i;
				System.out.println("index = " + currentVertexIndex);
				return;
			}
		}
		currentVertices = null;
		currentVertexIndex = -1;
	}	
}
