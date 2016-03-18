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
import java.util.stream.Collectors;

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.Block;
import ch.fhnw.demopolis.model.entities.Block.BlockType;
import ch.fhnw.demopolis.model.entities.BuildingBlock;
import ch.fhnw.demopolis.model.entities.BuildingBlock.Building;
import ch.fhnw.demopolis.model.entities.BuildingBlock.BuildingType;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.ui.ControlPanel;
import ch.fhnw.demopolis.ui.UI.IToolControl;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.util.math.Vec3;

public final class BuildingTypeTool extends AbstractDesignTool {

	private static final String TEXTURE = "gui/demopolis_ui_tool_bd_type.png";
	
	private BuildingType buildingType = BuildingType.NO_BUILDING;

	private List<Building> buildings;
	private boolean waitForHover = false;

	public BuildingTypeTool(Model model, IScene scene, IToolControl control) throws IOException {
		super(model, scene, control, TEXTURE);
		setButtons(createButtons(BuildingType.values(), t -> buildingType = (BuildingType)t));
	}

	@Override
	public void activate(ControlPanel panel) {
		activate(panel, 1);
		getControl().setEntityFilter(e -> e instanceof BuildingBlock);

		fade(I3DColors.LOW);
		getDesignEntities().replaceAll(e -> (e instanceof Block && ((Block)e).getBlockType() == BlockType.BUILT_SPACE) ? new BuildingBlock(e) : e);
		addMeshes();
	}
	
	@Override
	public void deactivate(ControlPanel panel) {
		super.deactivate(panel);
		removeMeshes();

		// special action at the end of the building use tool: convert all non-built to open space
		List<IDesignEntity> newEntities = new ArrayList<>();
		List<IDesignEntity> entities = getDesignEntities();
		for (IDesignEntity entity : entities) {
			if (!(entity instanceof BuildingBlock))
				continue;
			BuildingBlock block = (BuildingBlock)entity;
			List<Building> emptyBuildings = block.getBuildings()
											.stream()
											.filter(b -> b.getType() == BuildingType.NO_BUILDING || b.getPlan().isEmpty())
											.collect(Collectors.toList());
			block.getBuildings().removeAll(emptyBuildings);
			emptyBuildings.forEach(b -> newEntities.add(new Block(entity, b)));
		}
		entities.addAll(newEntities);
	}

	@Override
	public void clicked(IDesignEntity entity, Vec3 position) {
		if (waitForHover || buildings == null)
			return;

		BuildingBlock block = (BuildingBlock)entity;
		block.setBuildings(buildings);
		buildings = null;
		waitForHover = true;
	}
	
	@Override
	public void hover(IDesignEntity entity, Vec3 position) {
		waitForHover = false;

		BuildingBlock block = (BuildingBlock)entity;
		if (buildings == null) {
			buildings = new ArrayList<>();
			getScene().remove3DObjects(block.getMeshes());
		}
		buildings.forEach(b -> getScene().remove3DObjects(b.getMeshes()));
		buildings = block.createBuildings(buildingType, position);
		buildings.forEach(b -> getScene().add3DObjects(b.getMeshes()));
	}
	
	@Override
	public void exited(IDesignEntity entity) {
		BuildingBlock block = (BuildingBlock)entity;
		if (buildings != null) {
			buildings.forEach(b -> getScene().remove3DObjects(b.getMeshes()));
			getScene().add3DObjects(block.getMeshes());
		}
		buildings = null;
	}
}
