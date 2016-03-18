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

import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.camera.Camera;
import ch.fhnw.ether.scene.camera.ICamera;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class ViewControl {	
	private final IController controller;
	private final Model model;
	
	private final ICamera cameraOrtho;
	private final ICamera cameraPerspective;
	
	private final DirectionalLight light;

	public ViewControl(IController controller, Model model) {
		this.controller = controller;
		this.model = model;
		
		cameraOrtho = createCamera(true);
		cameraPerspective = createCamera(false);
		
		light = new DirectionalLight(I3DConfig.LIGHT_DIRECTION, I3DConfig.LIGHT_AMBIENT, I3DConfig.LIGHT_DIFFUSE);
		
		controller.getScene().add3DObjects(cameraOrtho, cameraPerspective, light);
		setCameraOrtho();
	}

	public void setCameraOrtho() {
		controller.setCamera(controller.getViews().get(0), cameraOrtho);
	}

	public void setCameraPerspective() {
		controller.setCamera(controller.getViews().get(0), cameraPerspective);
	}

	public void setLightTop() {
		light.setPosition(Vec3.Z);
	}

	public void setLightDefault() {
		light.setPosition(I3DConfig.LIGHT_DIRECTION);
	}
	
	private Camera createCamera(boolean ortho) {
		BoundingBox b = model.getScenario().getBounds();
		Vec3 center = new Vec3(b.getCenterX(), b.getCenterY(), 0);
		if (ortho) {
			IView v = controller.getViews().get(0);
			Vec3 eye = new Vec3(center.x, center.y, 1000);
			float width = v.getViewport().getAspect() * Math.max(b.getExtentX(), b.getExtentY());
			return new Camera(eye, center, -width, I3DConfig.DEFAULT_NEAR, I3DConfig.DEFAULT_FAR);						
		} else {
			Vec3 eye = new Vec3(center.x, b.getMinY(), 1000);
			return new Camera(eye, Vec3.ZERO, I3DConfig.DEFAULT_FOV, I3DConfig.DEFAULT_NEAR, I3DConfig.DEFAULT_FAR);			
		}
	}
}
