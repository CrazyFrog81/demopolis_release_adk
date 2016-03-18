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

package ch.fhnw.demopolis.config;

import ch.fhnw.util.color.RGB;

public interface IUIColors {
	float D = 0.1f;

	RGB GRAY_A = new RGB(0.3f, 0.3f, 0.3f);
	RGB GRAY_B = new RGB(0.4f, 0.4f, 0.4f);
	RGB GRAY_C = new RGB(0.5f, 0.5f, 0.5f);
	RGB GRAY_D = new RGB(0.6f, 0.6f, 0.6f);

	RGB RED_A = new RGB(0.2f, D, D);
	RGB RED_B = new RGB(0.4f, D, D);
	RGB RED_C = new RGB(0.6f, D, D);
	RGB RED_D = new RGB(0.8f, D, D);
	
	RGB GREEN_A = new RGB(D, 0.2f, D);
	RGB GREEN_B = new RGB(D, 0.4f, D);
	RGB GREEN_C = new RGB(D, 0.6f, D);
	RGB GREEN_D = new RGB(D, 0.8f, D);

	RGB BLUE_A = new RGB(D, D, 0.4f);
	RGB BLUE_B = new RGB(D, D, 0.6f);
	RGB BLUE_C = new RGB(D, D, 0.8f);
	RGB BLUE_D = new RGB(D, D, 1.0f);

	RGB YELLOW_A = new RGB(0.2f, 0.2f, D);
	RGB YELLOW_B = new RGB(0.4f, 0.4f, D);
	RGB YELLOW_C = new RGB(0.6f, 0.6f, D);
	RGB YELLOW_D = new RGB(0.8f, 0.8f, D);
}
