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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import ch.fhnw.demopolis.config.I3DColors;
import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.BuildingBlock;
import ch.fhnw.demopolis.model.entities.BuildingBlock.Building;
import ch.fhnw.demopolis.model.entities.BuildingBlock.BuildingHeight;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.ui.ControlPanel;
import ch.fhnw.demopolis.ui.UI.IToolControl;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.util.math.Vec3;

public final class BuildingHeightTool extends AbstractDesignTool {
	
	public static class BuildingParameter<T> {
		private final Function<Building, T> getter;
		private final BiConsumer<Building, T> setter;
		private final Consumer<T> updater;
		
		private T parameter;
		
		private Building savedBuilding;
		private T savedParameter;

		public BuildingParameter(Function<Building, T> getter, BiConsumer<Building, T> setter, Consumer<T> updater) {
			this.getter = getter;
			this.setter = setter;
			this.updater = updater;
		}
		
		public T get() {
			return parameter;
		}
		
		public void set(T parameter) {
			this.parameter = parameter;
			updater.accept(parameter);
		}
		
		public void clicked(IDesignEntity entity, Vec3 position) {
			Building building = ((BuildingBlock)entity).getBuilding(position);
			if (building != null)
				setter.accept(building, parameter);
			savedBuilding = null;
			savedParameter = null;
		}
		
		public void hover(IDesignEntity entity, Vec3 position) {
			Building building = ((BuildingBlock)entity).getBuilding(position);
			if (building != null) {
				if (building != savedBuilding) {
					if (savedBuilding != null) {
						setter.accept(savedBuilding, savedParameter);
					}
					savedBuilding = building;
					savedParameter = getter.apply(building);
					setter.accept(building, parameter);
				}
			}
		}
		
		public void exited(IDesignEntity entity) {
			if (savedBuilding != null) {
				setter.accept(savedBuilding, savedParameter);
				savedBuilding = null;
				savedParameter = null;
			}
		}	
	}
	
	private static final String TEXTURE = "gui/demopolis_ui_tool_bd_height.png";

	private BuildingParameter<BuildingHeight> buildingParameter = 
			new BuildingParameter<>(b -> b.getHeight(), (b, t) -> b.setHeight(t), t -> update(t));

	public BuildingHeightTool(Model model, IScene scene, IToolControl control) throws IOException {
		super(model, scene, control, TEXTURE);
		setButtons(createButtons(BuildingHeight.values(), t -> buildingParameter.set((BuildingHeight)t)));
	}

	@Override
	public void activate(ControlPanel panel) {
		activate(panel, 3);

		getControl().setEntityFilter(e -> e instanceof BuildingBlock);

		fade(I3DColors.LOW, e -> !(e instanceof BuildingBlock));
		getDesignEntities()
			.stream()
			.filter(e -> e instanceof BuildingBlock)
			.map(e -> (BuildingBlock)e)
			.forEach(e -> e.setDefaultColor(b -> b.getHeight().designColor));
		addMeshes();
		update(buildingParameter.get());
	}
	
	@Override
	public void deactivate(ControlPanel panel) {
		super.deactivate(panel);
		removeMeshes();
	}

	@Override
	public void clicked(IDesignEntity entity, Vec3 position) {
		buildingParameter.clicked(entity, position);
	}
	
	@Override
	public void hover(IDesignEntity entity, Vec3 position) {
		buildingParameter.hover(entity, position);
	}
	
	@Override
	public void exited(IDesignEntity entity) {
		buildingParameter.exited(entity);
	}
	
	private void update(BuildingHeight buildingHeight) {
	}
}
