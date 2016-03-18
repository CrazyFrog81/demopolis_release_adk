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

import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.util.color.RGB;

public class ControlPanel extends Panel {
	public interface IButtonAction {
		void execute(Button button);
	}

	public static class Button {
		private final RGB color;
		private final IButtonAction hoverAction;
		private final IButtonAction clickAction;

		public Button(RGB color, IButtonAction hoverAction, IButtonAction clickAction) {
			this.color = color;
			this.hoverAction = hoverAction != null ? hoverAction : b -> {};
			this.clickAction = clickAction != null ? clickAction : b -> {};
		}

		public Button(RGB color, IButtonAction clickAction) {
			this(color, null , clickAction);
		}
	}
	
	private Button[] buttons = new Button[0];
	private int currentHighlight = -1;
	private int currentSelection = -1;

	public ControlPanel(Position position) throws IOException {
		super(null, position);
	}
	
	public void setButtons(Texture texture, Button[] buttons) {
		setTexture(texture);
		this.buttons = buttons;
		for (int i = 1; i < buttons.length; ++i) {
			setButtonColor(i, buttons[i] == null ? RGB.BLACK : buttons[i].color);
		}
		currentHighlight = -1;
		currentSelection = -1;
	}
	
	public void setSelection(int button) {
		if (button < 1 || button > buttons.length || buttons[button] == null)
			throw new IllegalArgumentException("invalid button selection: " + button);
		currentSelection = button;
		buttons[button].clickAction.execute(buttons[button]);
	}
	
	public int getSelection() {
		return currentSelection;
	}
	
	public void animateSelection(double time) {
		if (currentSelection == -1)
			return;
		float scale = 1.0f + 0.8f * (float)(Math.sin(Math.PI * time));
		setButtonColor(currentSelection, buttons[currentSelection].color.scaleRGB(scale));
	}
	
	public boolean pointerClicked(IPointerEvent e) {
		int button = getButton(e);
		if (button > 0 && button < buttons.length && buttons[button] != null) {
			if (currentSelection != -1)
				setButtonColor(currentSelection, buttons[currentSelection].color);
			currentSelection = button;
			buttons[button].clickAction.execute(buttons[button]);
			return true;
		}
		return false;
	}

	public void pointerMoved(IPointerEvent e) {
		int button = getButton(e);
		if (button == currentHighlight)
			return;
		if (currentHighlight != -1)
			setButtonColor(currentHighlight, buttons[currentHighlight].color);
		currentHighlight = -1;
		if (button > 0 && button < buttons.length && buttons[button] != null) {
			setButtonColor(button, buttons[button].color.scaleRGB(1.4f));
			currentHighlight = button;
			buttons[button].hoverAction.execute(buttons[button]);
		}
	}
	
	private int getButton(IPointerEvent e) {
		float w = e.getView().getViewport().w;
		float h = e.getView().getViewport().h;
		float x = e.getX();
		float y = e.getY();
		return getButton(x, y, w, h);
	}
}
