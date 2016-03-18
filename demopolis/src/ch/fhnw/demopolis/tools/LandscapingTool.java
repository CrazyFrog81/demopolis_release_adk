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
import ch.fhnw.demopolis.model.entities.Block;
import ch.fhnw.demopolis.model.entities.Block.BlockType;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.model.entities.OpenSpaceBlock;
import ch.fhnw.demopolis.model.entities.OpenSpaceBlock.OpenSpaceType;
import ch.fhnw.demopolis.model.entities.OpenSpaceBlock.PlantGroup;
import ch.fhnw.demopolis.ui.ControlPanel;
import ch.fhnw.demopolis.ui.UI.IToolControl;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public final class LandscapingTool extends AbstractDesignTool {

	private static final String TEXTURE = "gui/demopolis_ui_tool_landscaping.png";

	private OpenSpaceType openSpaceType;
	private PlantGroup group;
	
	private RGB savedColor = null;

	public LandscapingTool(Model model, IScene scene, IToolControl control) throws IOException {
		super(model, scene, control, TEXTURE);
		setButtons(createButtons(OpenSpaceType.values(), t -> openSpaceType = (OpenSpaceType)t));
	}

	@Override
	public void activate(ControlPanel panel) {
		activate(panel, 1);
		getControl().setEntityFilter(e -> e instanceof OpenSpaceBlock);
		
		fade(I3DColors.LOW);
		getDesignEntities().replaceAll(e -> (e instanceof Block && ((Block)e).getBlockType() == BlockType.OPEN_SPACE) ? new OpenSpaceBlock(e) : e);
		addMeshes();
	}
	
	@Override
	public void deactivate(ControlPanel panel) {
		super.deactivate(panel);
		removeMeshes();
	}

	@Override
	public void clicked(IDesignEntity entity, Vec3 position) {
		OpenSpaceBlock block = (OpenSpaceBlock)entity;
		switch (openSpaceType) {
		case NON_LANDSCAPED:
		case PLAZA:
		case LAWN:
			if (block.getOpenSpaceType() != openSpaceType || block.hasShrubs() || block.hasTrees()) {
				getScene().remove3DObjects(entity.getMeshes());
				((OpenSpaceBlock)entity).setOpenSpaceType(openSpaceType);
				getScene().add3DObjects(entity.getMeshes());				
			}
			break;
		case SHRUBS:
			if (group != null) {
				block.addShrubs(group);
				group = null;
			}
			break;
		case TREES:
			if (group != null) {
				block.addTrees(group);
				group = null;
			}
			break;
		}
		savedColor = null;
	}
	
	@Override
	public void hover(IDesignEntity entity, Vec3 position) {
		OpenSpaceBlock block = (OpenSpaceBlock)entity;
		if (group != null)
			getScene().remove3DObject(group.getMesh());
		
		switch (openSpaceType) {
		case NON_LANDSCAPED:
		case LAWN:
		case PLAZA:
			if (savedColor == null) {
				savedColor = block.getColor();
				block.setColor(openSpaceType.designColor);
			}
			break;
		case SHRUBS:
			group = block.createShrubs(position);
			break;
		case TREES:
			group = block.createTrees(position);
			break;
		}
		if (group != null)
			getScene().add3DObject(group.getMesh());
	}

	@Override
	public void exited(IDesignEntity entity) {
		OpenSpaceBlock block = (OpenSpaceBlock)entity;
		if (savedColor != null) {
			block.setColor(savedColor);
			savedColor = null;
		}
		if (group != null) {
			getScene().remove3DObject(group.getMesh());
			group = null;
		}
	}
}
