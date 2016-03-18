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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import assets.Asset;
import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.model.entities.IDesignEntity.IButtonInfo;
import ch.fhnw.demopolis.ui.ControlPanel;
import ch.fhnw.demopolis.ui.ControlPanel.Button;
import ch.fhnw.demopolis.ui.UI.IToolControl;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public abstract class AbstractDesignTool implements IDesignTool {
	public static final int BUTTON_INDEX_MAX = 9;
	public static final int BUTTON_INDEX_NEXT = 8;
	public static final int BUTTON_INDEX_ABORT = 9;
	
	public static final RGB COLOR_NEXT = IUIColors.GREEN_C;
	public static final RGB COLOR_ABORT = IUIColors.RED_C;

	private final Model model;
	private final IScene scene;
	private final IToolControl control;
	private final Texture texture;
	private final Button[] buttons;
	
	protected AbstractDesignTool(Model model, IScene scene, IToolControl control, String texture) throws IOException {
		this.model = model;
		this.scene = scene;
		this.control = control;
		this.texture = texture != null ? Frame.create(Asset.get(texture)).getTexture() : null;
		this.buttons = new Button[10];
	}
	
	@Override
	public void deactivate(ControlPanel panel) {
		getControl().resetEntityFilter();
	}
	
	@Override
	public void clicked() {
		// ignored
		// note: clicked() is only used by generation tool, where we need to
		// detect a mouse click to stop animation...
	}
	
	@Override
	public void clicked(IDesignEntity entity, Vec3 position) {
		// ignored
	}

	@Override
	public void hover(IDesignEntity entity, Vec3 position) {
		// ignored
	}
	
	@Override
	public void exited(IDesignEntity entity) {
		// ignored
	}
	
	@Override
	public void key(short key) {
		switch (key) {
		case IKeyEvent.VK_O:
			control.setCameraOrtho();
			break;
		case IKeyEvent.VK_P:
			control.setCameraPerspective();
			break;
		case IKeyEvent.VK_F:
			control.getController().getViews().get(0).getWindow().setFullscreen(true);
		}
	}
	
	protected void activate(ControlPanel panel, int selection) {
		panel.setButtons(texture, buttons);
		panel.setSelection(selection);
	}
	
	protected void fade(float amount) {
		model.getMaterials().fade(amount);
		getDesignEntities().forEach(e -> e.fade(amount));
	}	

	protected void fade(float amount, Predicate<IDesignEntity> filter) {
		model.getMaterials().fade(amount);
		getDesignEntities().forEach(e -> {
			if (filter.test(e)) e.fade(amount);
		});
	}

	protected void addMeshes() {
		getScene().add3DObjects(IDesignEntity.getMeshes(getDesignEntities()));		
	}
	
	protected void removeMeshes() {
		getScene().remove3DObjects(IDesignEntity.getMeshes(getDesignEntities()));
	}
	
	protected final Model getModel() {
		return model;
	}
	
	protected final List<IDesignEntity> getDesignEntities() {
		return model.getDesignEntities();
	}
	
	protected final IScene getScene() {
		return scene;
	}
	
	protected final IToolControl getControl() {
		return control;
	}
	
	protected final void setButtons(Button[] buttons) {
		for (int i = 1; i <= BUTTON_INDEX_MAX; ++i) {
			if (i == BUTTON_INDEX_NEXT)
				this.buttons[i] = getNextButton();
			else if (i == BUTTON_INDEX_ABORT)
				this.buttons[i] = getAbortButton();
			else if (i < buttons.length + 1)
				this.buttons[i] = buttons[i - 1];
			else
				this.buttons[i] = null;
		}
	}
	
	protected Button[] createButtons(IButtonInfo[] selectors, Consumer<IButtonInfo> op) {
		Button[] buttons = new Button[selectors.length];
		for (int i = 0; i < buttons.length; ++i) {
			final int j = i;
			buttons[j] = new Button(selectors[j].getButtonColor(), b -> op.accept(selectors[j]));
		}
		return buttons;
	}

	private Button getNextButton() {
		return new Button(COLOR_NEXT, b -> control.nextTool());
	}

	private Button getAbortButton() {
		return new Button(COLOR_ABORT, b -> control.reset());
	}	
}
