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

import java.util.function.Predicate;

import ch.fhnw.demopolis.model.Model;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.demopolis.tools.IDesignTool;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.scene.camera.IViewCameraState;
import ch.fhnw.ether.view.ProjectionUtilities;
import ch.fhnw.util.Pair;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeometryUtilities;
import ch.fhnw.util.math.geometry.Line;
import ch.fhnw.util.math.geometry.Plane;
import ch.fhnw.util.math.geometry.Polygon;

public class Picker {
	private static final Predicate<IDesignEntity> DEFAULT_FILTER = e -> false;

	private final Model model;

	private Predicate<IDesignEntity> entityFilter = DEFAULT_FILTER;

	private IDesignEntity previousHoverEntity;

	public Picker(Model model) {
		this.model = model;
	}

	public void setEntityFilter(Predicate<IDesignEntity> filter) {
		entityFilter = filter;
	}

	public void resetEntityFilter() {
		entityFilter = DEFAULT_FILTER;
		previousHoverEntity = null;
	}

	public void pointerClicked(IPointerEvent e, IDesignTool tool) {
		Pair<IDesignEntity, Vec3> p = pickEntity(e);
		if (p.first != null)
			tool.clicked(p.first, p.second);
	}

	public void pointerMoved(IPointerEvent e, IDesignTool tool) {
		Pair<IDesignEntity, Vec3> p = pickEntity(e);
		IDesignEntity entity = p.first;
		if (entity != previousHoverEntity && previousHoverEntity != null)
				tool.exited(previousHoverEntity);
		if (entity != null)
			tool.hover(entity, p.second);
		previousHoverEntity = p.first;
	}
	
	private Pair<IDesignEntity, Vec3> pickEntity(IPointerEvent e) {
		Vec3 p = getPositionOnGround(e);
		if (p != null) {
			for (IDesignEntity entity : model.getDesignEntities()) {
				if (!entityFilter.test(entity))
					continue;
				if (insideEntity(p, entity))
					return new Pair<>(entity, p);
			}
		}		
		return new Pair<>(null, p);
	}

	private Vec3 getPositionOnGround(IPointerEvent e) {
		IViewCameraState state = e.getView().getController().getRenderManager().getViewCameraState(e.getView());
		Line line = ProjectionUtilities.getRay(state, e.getX(), e.getY());
		Plane plane = new Plane(new Vec3(0, 0, 1));
		Vec3 p = plane.intersect(line);
		return p != null ? new Vec3(p.x, p.y, 0) : null;
	}
	
	private boolean insideEntity(Vec3 p, IDesignEntity entity) {
		for (Polygon shape : entity.getShapes()) {
			if (GeometryUtilities.testPointInPolygon2D(p.x, p.y, shape))
				return true;
		}
		return false;
	}
}
