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

import java.io.IOException;
import java.util.function.Predicate;

import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.tools.BuildingHeightTool;
import ch.fhnw.demopolis.tools.BuildingLineTool;
import ch.fhnw.demopolis.tools.BuildingSetbackTool;
import ch.fhnw.demopolis.tools.BuildingTypeTool;
import ch.fhnw.demopolis.tools.BuildingUseTool;
import ch.fhnw.demopolis.tools.GenerationTool;
import ch.fhnw.demopolis.tools.IDesignTool;
import ch.fhnw.demopolis.tools.LandscapingTool;
import ch.fhnw.demopolis.tools.SpaceAllocationTool;
import ch.fhnw.demopolis.tools.StreetTool;
import ch.fhnw.demopolis.ui.Panel.Position;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.controller.tool.AbstractTool;
import ch.fhnw.ether.controller.tool.ITool;
import ch.fhnw.ether.controller.tool.NavigationTool;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.util.AutoDisposer;

public class UI {
	private static final boolean DBG = true;
	
	public interface IToolControl {
		void nextTool();
		void reset();
		void setEntityFilter(Predicate<IDesignEntity> predicate);
		void resetEntityFilter();
		
		void showStatusPanel();
		void hideStatusPanel();
		
		void showControlPanel();
		void hideControlPanel();
		
		void setCameraOrtho();
		void setCameraPerspective();
		
		void setLightTop();
		void setLightDefault();
		
		void startAnimation();
		void stopAnimation();
		
		IController getController();
	}

	private final IToolControl toolControl = new IToolControl() {
		@Override
		public void nextTool() {
			if (currentDesignTool < designTools.length - 1) {
				designTools[currentDesignTool].deactivate(controlPanel);
				dumpStatus("after level " + currentDesignTool);
				currentDesignTool++;
				designTools[currentDesignTool].activate(controlPanel);
				statusPanel.setLevel(currentDesignTool + 1);
			} else {
				reset();
			}
		}

		@Override
		public void reset() {
			// make sure we're on the clean side
			AutoDisposer.runGC();

			if (currentDesignTool > -1)
				designTools[currentDesignTool].deactivate(controlPanel);

			dumpStatus("after reset");
			
			model.resetDesignEntities();
			
			currentDesignTool = 0;
			designTools[currentDesignTool].activate(controlPanel);
			statusPanel.setLevel(currentDesignTool + 1);
		}

		@Override
		public void setEntityFilter(Predicate<IDesignEntity> filter) {
			picker.setEntityFilter(filter);
		}

		@Override
		public void resetEntityFilter() {
			picker.resetEntityFilter();
		}

		@Override
		public void showStatusPanel() {
			statusPanel.show(controller);
		}

		@Override
		public void hideStatusPanel() {
			statusPanel.hide(controller);
		}

		@Override
		public void showControlPanel() {
			controlPanel.show(controller);
		}

		@Override
		public void hideControlPanel() {
			controlPanel.hide(controller);
		}

		@Override
		public void setCameraOrtho() {
			viewControl.setCameraOrtho();
		}

		@Override
		public void setCameraPerspective() {
			viewControl.setCameraPerspective();
		}

		@Override
		public void setLightTop() {
			viewControl.setLightTop();
		}

		@Override
		public void setLightDefault() {
			viewControl.setLightDefault();
		}
		
		@Override
		public void startAnimation() {
			controller.getCurrentView().getWindow().setPointerVisible(false);
			cameraPath.startAnimation();
		}
		
		@Override
		public void stopAnimation() {
			controller.getCurrentView().getWindow().setPointerVisible(true);
			cameraPath.stopAnimation();
		}
		
		@Override
		public IController getController() {
			return controller;
		}
	};
	
	private final Model model;
	private final StatusPanel statusPanel;
	private final ControlPanel controlPanel;
	private final Picker picker;
	
	private IController controller;
	private ViewControl viewControl;
	private CameraPath cameraPath;
	
	private ITool navigationTool;
	private ITool pathTool;

	private IDesignTool[] designTools;
	private int currentDesignTool = -1;
	
	public UI(Model model) throws IOException {
		this.model = model;

		statusPanel = new StatusPanel("gui/demopolis_ui_status.png", Position.LEFT);
		controlPanel = new ControlPanel(Position.RIGHT);
		picker = new Picker(model);
	}

	public void enable(IController controller) throws IOException {
		this.controller = controller;
		this.viewControl = new ViewControl(controller, model);
		this.cameraPath = new CameraPath(controller, model);

		statusPanel.show(controller);
		controlPanel.show(controller);
		
		navigationTool = new NavigationTool(controller, new AbstractTool(controller) {
			@Override
			public void pointerClicked(IPointerEvent e) {
				try {
					if (pathTool == null) {
						if (controlPanel.isVisible() && controlPanel.pointerClicked(e))
								return;
						designTools[currentDesignTool].clicked();
						picker.pointerClicked(e, designTools[currentDesignTool]);
					} else {
						pathTool.pointerClicked(e);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			@Override
			public void pointerPressed(IPointerEvent e) {
				try {
					if (pathTool != null)
						pathTool.pointerPressed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			@Override
			public void pointerMoved(IPointerEvent e) {
				try {
					if (pathTool == null) {
						if (controlPanel.isVisible()) 
							controlPanel.pointerMoved(e);
						picker.pointerMoved(e, designTools[currentDesignTool]);
					} else {
						pathTool.pointerMoved(e);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			@Override
			public void pointerDragged(IPointerEvent e) {
				try {
					if (pathTool == null)
						picker.pointerClicked(e, designTools[currentDesignTool]);
					else
						pathTool.pointerDragged(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			
			@Override
			public void keyPressed(IKeyEvent e) {
				try {
					if (e.getKeyCode() == IKeyEvent.VK_C) {
						if (pathTool == null) {
							pathTool = cameraPath.getPathTool();
							statusPanel.hide(controller);
							controlPanel.hide(controller);
							cameraPath.show();
						} else {
							pathTool = null;
							statusPanel.show(controller);
							controlPanel.show(controller);
							cameraPath.hide();
						}
						return;
					}
					if (pathTool == null)
						designTools[currentDesignTool].key(e.getKeyCode());
					else
						pathTool.keyPressed(e);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		IScene scene = controller.getScene();
		designTools = new IDesignTool[] {
			new SpaceAllocationTool(model, scene, toolControl),
			new StreetTool(model, scene, toolControl),
			new BuildingTypeTool(model, scene, toolControl),
			new BuildingHeightTool(model, scene, toolControl),
			new BuildingLineTool(model, scene, toolControl),
			new BuildingSetbackTool(model, scene, toolControl),
			new BuildingUseTool(model, scene, toolControl),
			new LandscapingTool(model, scene, toolControl),
			new GenerationTool(model, scene, toolControl),
		};

		model.getEnvironment().addToScene(scene);
		toolControl.reset();
		
		controller.setTool(navigationTool);
		
		controller.animate((time, interval) -> {
			controlPanel.animateSelection(time);
		});
	}

	public void viewResized(int w, int h) {
		statusPanel.viewResized(w, h);
		controlPanel.viewResized(w, h);
	}
	
	private void dumpStatus(String where) {
		if (DBG)
			System.err.println(where + ": " + controller.getScene().get3DObjects().size() + " objects in scene");
	}
}
