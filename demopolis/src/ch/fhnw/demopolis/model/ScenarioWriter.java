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

package ch.fhnw.demopolis.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import ch.fhnw.demopolis.model.entities.Block;
import ch.fhnw.demopolis.model.entities.BuildingBlock;
import ch.fhnw.demopolis.model.entities.BuildingBlock.Building;
import ch.fhnw.demopolis.model.entities.OpenSpaceBlock;
import ch.fhnw.demopolis.model.entities.Street;
import ch.fhnw.demopolis.model.entities.Block.BlockType;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Polygon;

public class ScenarioWriter {
	
	public ScenarioWriter() {
	}

	public void write(Model model, String path) throws IOException {
		String filename = path + "/json" + System.currentTimeMillis() + ".txt";
		FileWriter writer = new FileWriter(filename, false);
		
		
		Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true);


        JsonGeneratorFactory jf = Json.createGeneratorFactory(Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, true));
        JsonGenerator gen = jf.createGenerator(writer);		
        
        gen.writeStartObject();
        
        // write scenario name
        gen.write("scenario", model.getScenario().getName());
        
        // write streets
        gen.writeStartArray("streets");
        model.getDesignEntities().stream().filter(e -> e instanceof Street).map(e -> (Street)e).forEach(e -> writeStreet(gen, e));
        gen.writeEnd();
        
        // write open space
        gen.writeStartArray("openspace");
        model.getDesignEntities().stream().filter(e -> e instanceof OpenSpaceBlock).map(e -> (OpenSpaceBlock)e).forEach(e -> writeOpenSpace(gen, e));
        gen.writeEnd();
        
        // write lots & buildings
        gen.writeStartArray("buildings");
        model.getDesignEntities().stream().filter(e -> e instanceof BuildingBlock).map(e -> (BuildingBlock)e).forEach(e -> e.getBuildings().forEach(b -> writeBuilding(gen, b)));
        gen.writeEnd();
        
        // write existing buildings
        gen.writeStartArray("existing_blocks");
        model.getDesignEntities().stream().filter(e -> e instanceof Block).map(e -> (Block)e).filter(e -> e.getBlockType() == BlockType.LEAVE_AS_IS).forEach(e -> writeExistingBlock(gen, e));
        gen.writeEnd();
        
        // done
        gen.writeEnd();
        gen.close();
	}
	
	void writeStreet(JsonGenerator gen, Street s) {
		gen.writeStartObject();
		gen.write("id", s.getId());
		gen.write("type", s.getStreetType().toString());
		gen.write("width", s.getWidth());
		gen.writeStartArray("polygons");
		for (Polygon p : s.getShapes()) {
			gen.writeStartObject();
			writeVectorList(gen, "polygon", p.asList());
			gen.writeEnd();
		}
		gen.writeEnd();
		gen.writeEnd();
	}
	
	void writeOpenSpace(JsonGenerator gen, OpenSpaceBlock b) {
		gen.writeStartObject();
		gen.write("id", b.getId());
		gen.write("type", b.getOpenSpaceType().toString());
		writeVectorList(gen, "polygon", b.getShape().asList());
		writeVectorList(gen, "shrubs", b.getShrubPositions());
		writeVectorList(gen, "trees", b.getTreePositions());
		gen.writeEnd();
	}

	void writeBuilding(JsonGenerator gen, Building b) {
		gen.writeStartObject();
		gen.write("id", b.getId());
		gen.write("type", b.getType().toString());
		gen.write("height", b.getFinalHeight());
		gen.write("line", b.getLine().toString());
		gen.write("setback", b.getSetback().toString());
		gen.write("use", b.getUse().toString());
		writeVectorList(gen, "lot", b.getLot().asList());
		gen.writeStartArray("plan");
		for (Polygon p : b.getPlan()) {
			gen.writeStartObject();
			writeVectorList(gen, "polygon", p.asList());
			gen.writeEnd();
		}
		gen.writeEnd();
		gen.writeEnd();
	}	
	
	void writeExistingBlock(JsonGenerator gen, Block b) {
		gen.writeStartObject();
		gen.write("id", b.getId());
		writeVectorList(gen, "block", b.getShape().asList());
		gen.writeEnd();		
	}

	private void writeVectorList(JsonGenerator gen, String name, List<Vec3> l) {
		gen.writeStartArray(name);
		for (Vec3 v : l)
			writeVector(gen, v);
		gen.writeEnd();
	}
	
	private void writeVector(JsonGenerator gen, Vec3 v) {
		gen.writeStartObject();
		gen.write("x", v.x);
		gen.write("y", v.y);
		gen.write("z", v.z);
		gen.writeEnd();
	}

}
