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

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.model.entities.IDesignEntity.Type;
import ch.fhnw.demopolis.model.entities.Street;
import ch.fhnw.demopolis.model.entities.Street.StreetType;
import ch.fhnw.demopolis.ui.ControlPanel;
import ch.fhnw.demopolis.ui.UI.IToolControl;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.util.math.Vec3;

public final class StreetTool extends AbstractDesignTool {

	private static final String TEXTURE = "gui/demopolis_ui_tool_street.png";

	private StreetType streetType = StreetType.FOUR_LANE;
	
	public StreetTool(Model model, IScene scene, IToolControl control) throws IOException {
		super(model, scene, control, TEXTURE);
		setButtons(createButtons(StreetType.values(), t -> update((StreetType)t)));
	}

	@Override
	public void activate(ControlPanel panel) {
		activate(panel, 1);

		fade(I3DColors.LOW);
		getDesignEntities().replaceAll(e -> e.getType() == Type.STREET ? new Street(e) : e);
		addMeshes();
		update(streetType);
	}
	
	@Override
	public void deactivate(ControlPanel panel) {
		super.deactivate(panel);
		removeMeshes();
	}
	
	@Override
	public void clicked(IDesignEntity entity, Vec3 position) {
		((Street)entity).setStreetType(streetType);
	}
	
	@Override
	public void hover(IDesignEntity entity, Vec3 position) {
		if (((Street)entity).getWidth() > streetType.minWidth)
			((Street)entity).setColor(streetType.designColor);
	}
	
	@Override
	public void exited(IDesignEntity entity) {
		((Street)entity).setColor(((Street)entity).getStreetType().designColor);
	}
	
	private void update(StreetType streetType) {
		this.streetType = streetType;
		getControl().setEntityFilter(e -> e instanceof Street && ((Street)e).acceptType(streetType));
		getDesignEntities()	
			.stream()
			.filter(e -> e instanceof Street)
			.map(e -> (Street)e)
			.forEach(e -> e.setColor(e.acceptType(streetType) ? e.getStreetType().designColor : I3DColors.DISABLED));
	}
}
