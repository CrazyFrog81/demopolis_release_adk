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

import assets.Asset;
import ch.fhnw.demopolis.config.IUIColors;
import ch.fhnw.ether.image.Frame;
import ch.fhnw.util.color.RGB;

public class StatusPanel extends Panel {
	public static final int NUM_TOOLS = 9;
	
	public static final RGB COLOR_LEVEL_DONE = IUIColors.GREEN_B;
	public static final RGB COLOR_LEVEL_CURRENT = IUIColors.GREEN_C;
	public static final RGB COLOR_LEVEL_OPEN = IUIColors.GRAY_A;

	public StatusPanel(String path, Position position) throws IOException {
		super(Frame.create(Asset.get(path)).getTexture(), position);
	}

	public void setLevel(int level) {
		for (int i = 1; i <= NUM_TOOLS; ++i) {
			if (i < level)
				setButtonColor(i, COLOR_LEVEL_DONE);
			else if (i == level)
				setButtonColor(i, COLOR_LEVEL_CURRENT);
			else
				setButtonColor(i, COLOR_LEVEL_OPEN);
		}
	}
}
