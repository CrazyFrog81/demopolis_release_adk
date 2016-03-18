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

package ch.fhnw.demopolis.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.demopolis.config.I3DConfig;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.BuildingBlock;
import ch.fhnw.demopolis.model.entities.BuildingBlock.Building;
import ch.fhnw.demopolis.model.entities.BuildingBlock.BuildingType;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.ui.ControlPanel;
import ch.fhnw.demopolis.ui.UI.IToolControl;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;

public final class GenerationTool extends AbstractDesignTool {
	private enum AnimationState {
		IDLE,
		FADE_OUT_DESIGN,
		FADE_IN_3D,
		FLY_3D,
		FADE_OUT_3D
	}

	private List<IMesh> meshes = new ArrayList<>();
	
	private AnimationState state = AnimationState.IDLE;
	private double animationStartTime = 0;
	
	
	public GenerationTool(Model model, IScene scene, IToolControl control) throws IOException {
		super(model, scene, control, null);
		control.getController().animate((t, i) -> animate(t, i));
	}

	@Override
	public void activate(ControlPanel panel) {
		fade(1);
		
		List<Building> buildings = getBuildings(getDesignEntities());
		
		// finalize building heights & plans
		BuildingBlock.setFinalBuildingHeights(buildings);
		
		// generate all meshes
		meshes.clear();
		getDesignEntities().forEach(e -> meshes.addAll(e.generate(getModel())));
		
		System.out.println("meshes: " + meshes.size());
		meshes = MeshUtilities.mergeMeshes(meshes);
		System.out.println("meshes: " + meshes.size());
		getScene().add3DObjects(meshes);
		
		// write scenario to disk
		// NOTE: it's mandatory that this is called after generation, since some data doesn't exist before
		getModel().write();
		
		getControl().hideControlPanel();
		getControl().hideStatusPanel();
		state = AnimationState.FADE_OUT_DESIGN;
	}
	
	@Override
	public void deactivate(ControlPanel panel) {
		super.deactivate(panel);
		getScene().remove3DObjects(meshes);
		meshes.clear();
	}
	
	@Override
	public void clicked() {
		if (state == AnimationState.FLY_3D)
			state = AnimationState.FADE_OUT_3D;
	}

	@Override
	public void key(short key) {
		super.key(key);
		if (key == IKeyEvent.VK_SPACE && state == AnimationState.FLY_3D)
			state = AnimationState.FADE_OUT_3D;
	}

	private void animate(double time, double interval) {
		switch (state) {
		case IDLE:
			animationStartTime = time;
			break;
		case FADE_OUT_DESIGN: {
			double fade = Math.max(0, 1 - (time - animationStartTime) / I3DConfig.FADE_TIME);
			fade((float)fade);
			if (fade <= 0) {
				state = AnimationState.FADE_IN_3D;
				animationStartTime = time;
				getModel().getPopulation().addToScene(getScene());

				// start flying while we fade in
				getControl().setCameraPerspective();
				getControl().startAnimation();
			}
			break;
		}
		case FADE_IN_3D: {
			double fade = Math.min(1, (time - animationStartTime) / I3DConfig.FADE_TIME);
			fade((float)fade);
			if (fade >= 1) {
				state = AnimationState.FLY_3D;
				animationStartTime = time;
			}
			break;
		}
		case FLY_3D:
			animationStartTime = time;
			break;
		case FADE_OUT_3D: {
			double fade = Math.max(0, 1 - (time - animationStartTime) / I3DConfig.FADE_TIME);
			fade((float)fade);
			if (fade <= 0) {
				state = AnimationState.IDLE;
				animationStartTime = time;
				getModel().getPopulation().removeFromScene(getScene());
				getControl().showControlPanel();
				getControl().showStatusPanel();
				getControl().reset();

				// stop flying once we faded out
				getControl().setCameraOrtho();
				getControl().stopAnimation();
			}
			break;
		}
		}
	}
	
	private List<Building> getBuildings(List<IDesignEntity> entities) {
		List<Building> buildings = new ArrayList<>();
		for (IDesignEntity entity : entities) {
			if (!(entity instanceof BuildingBlock))
				continue;
			BuildingBlock block = (BuildingBlock)entity;
			for (Building building : block.getBuildings()) {
				if (building.getType() != BuildingType.NO_BUILDING)
					buildings.add(building);
			}
		}
		return buildings;
	}
}
