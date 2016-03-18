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

public interface I3DColors {
	float LOW = 0.5f;
	float DIM = 0.5f; // 0.75
	float HI = 2.0f;
	
	RGB AMBIENT_HI	= new RGB(1, 1, 1);
	RGB AMBIENT_LO	= new RGB(0.25f, 0.25f, 0.25f);
	
	RGB GROUND		= new RGB(0.4f, 0.4f, 0.4f);
	RGB WATER		= new RGB(0.1f, 0.1f, 0.5f);
	RGB BLOCK		= new RGB(0.8f, 0.8f, 0.8f);
	RGB BUILDING	= new RGB(1, 1, 1);
	RGB TRAIN		= new RGB(1, 0, 0);
	RGB SIDEWALK	= GROUND.scaleRGB(0.8f);
	
	RGB GREEN		= new RGB(0, 0.5f, 0);
	RGB SHRUB		= new RGB(0, 0.5f, 0);
	RGB TREE		= new RGB(0, 0.5f, 0);
	RGB TRUNK		= new RGB(0.325f, 0.2f, 0.039f);
	
	RGB DISABLED	= RGB.GRAY10;
	
	RGB FADED		= RGB.GRAY10;
}
