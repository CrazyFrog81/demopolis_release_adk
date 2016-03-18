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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.fhnw.demopolis.model.IScenario;
import ch.fhnw.demopolis.model.entities.IDesignEntity;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public final class BerlinScenario implements IScenario {
	private static final Vec3 AP_MIN = new Vec3(-155, -340, -10);
	private static final Vec3 AP_MAX = new Vec3(945, 780, 200);
	private static final Vec3 AP_RF_MIN = new Vec3(-400, -720, -10);
	private static final Vec3 AP_RF_MAX = new Vec3(600, 770, 200);
	
	private static final String STATIC_GROUND = "berlin3d/ground_static.obj";
	private static final String STATIC_WATER = "berlin3d/water_static.obj";
	private static final String STATIC_BLOCKS = "berlin3d/blocks_static.obj";
	private static final String STATIC_GREEN = "berlin3d/green_static.obj";
	private static final String STATIC_BUILDINGS = "berlin3d/buildings_static.obj";
	private static final String STATIC_TRAIN = "berlin3d/train_static.obj";
	
	private static final IDesignEntity[] BLOCKS_AP = {
		IScenario.block("berlin3d/blocks/block_bl_19.obj"),
		IScenario.block("berlin3d/blocks/block_bl_20.obj"),
		IScenario.block("berlin3d/blocks/block_bl_21.obj"),
		IScenario.block("berlin3d/blocks/block_bl_22.obj"),
		IScenario.block("berlin3d/blocks/block_bl_23.obj"),
		IScenario.block("berlin3d/blocks/block_bl_24.obj"),
		IScenario.block("berlin3d/blocks/block_bl_25.obj"),
		IScenario.block("berlin3d/blocks/block_bl_26.obj"),
		IScenario.block("berlin3d/blocks/block_bl_27.obj"),
		IScenario.block("berlin3d/blocks/block_bl_28.obj"),
		IScenario.block("berlin3d/blocks/block_bl_29.obj"),
		IScenario.block("berlin3d/blocks/block_bl_30.obj"),
		IScenario.block("berlin3d/blocks/block_c_04.obj"),
		IScenario.block("berlin3d/blocks/block_s_26.obj"),
		IScenario.block("berlin3d/blocks/block_s_27.obj"),
		IScenario.block("berlin3d/blocks/block_s_28.obj"),
		IScenario.block("berlin3d/blocks/block_s_29.obj"),
		IScenario.block("berlin3d/blocks/block_s_30.obj"),
		IScenario.block("berlin3d/blocks/block_s_31.obj"),
		IScenario.block("berlin3d/blocks/block_s_32.obj"),
		IScenario.block("berlin3d/blocks/block_s_33.obj"),
		IScenario.block("berlin3d/blocks/block_s_34.obj"),
		IScenario.block("berlin3d/blocks/block_s_35.obj"),
		IScenario.block("berlin3d/blocks/block_s_36.obj"),
		IScenario.block("berlin3d/blocks/block_s_37.obj"),
		IScenario.block("berlin3d/blocks/block_s_38.obj"),
		IScenario.block("berlin3d/blocks/block_s_39.obj"),
		IScenario.block("berlin3d/blocks/block_s_40.obj"),
		IScenario.block("berlin3d/blocks/block_s_41.obj"),
		IScenario.block("berlin3d/blocks/block_s_42.obj"),
		IScenario.block("berlin3d/blocks/block_s_43.obj"),
		IScenario.block("berlin3d/blocks/block_s_44.obj"),
		IScenario.block("berlin3d/blocks/block_s_45.obj"),
	};

	private static final IDesignEntity[] BLOCKS_RF = {
		IScenario.block("berlin3d/blocks/block_bl_01.obj"),
		IScenario.block("berlin3d/blocks/block_bl_02.obj"),
		IScenario.block("berlin3d/blocks/block_bl_03.obj"),
		IScenario.block("berlin3d/blocks/block_bl_04.obj"),
		IScenario.block("berlin3d/blocks/block_bl_05.obj"),
		IScenario.block("berlin3d/blocks/block_bl_06.obj"),
		IScenario.block("berlin3d/blocks/block_bl_07.obj"),
		IScenario.block("berlin3d/blocks/block_bl_08.obj"),
		IScenario.block("berlin3d/blocks/block_bl_09.obj"),
		IScenario.block("berlin3d/blocks/block_bl_10.obj"),
		IScenario.block("berlin3d/blocks/block_bl_11.obj"),
		IScenario.block("berlin3d/blocks/block_bl_12.obj"),
		IScenario.block("berlin3d/blocks/block_bl_13.obj"),
		IScenario.block("berlin3d/blocks/block_bl_14.obj"),
		IScenario.block("berlin3d/blocks/block_bl_15.obj"),
		IScenario.block("berlin3d/blocks/block_bl_16.obj"),
		IScenario.block("berlin3d/blocks/block_bl_17.obj"),
		IScenario.block("berlin3d/blocks/block_bl_18.obj"),
		IScenario.block("berlin3d/blocks/block_br_01.obj"),
		IScenario.block("berlin3d/blocks/block_br_02.obj"),
		IScenario.block("berlin3d/blocks/block_br_03.obj"),
		IScenario.block("berlin3d/blocks/block_br_04.obj"),
		IScenario.block("berlin3d/blocks/block_c_01.obj"),
		IScenario.block("berlin3d/blocks/block_c_02.obj"),
		IScenario.block("berlin3d/blocks/block_c_03.obj"),
		IScenario.block("berlin3d/blocks/block_s_01.obj"),
		IScenario.block("berlin3d/blocks/block_s_02.obj"),
		IScenario.block("berlin3d/blocks/block_s_03.obj"),
		IScenario.block("berlin3d/blocks/block_s_04.obj"),
		IScenario.block("berlin3d/blocks/block_s_05.obj"),
		IScenario.block("berlin3d/blocks/block_s_06.obj"),
		IScenario.block("berlin3d/blocks/block_s_07.obj"),
		IScenario.block("berlin3d/blocks/block_s_08.obj"),
		IScenario.block("berlin3d/blocks/block_s_09.obj"),
		IScenario.block("berlin3d/blocks/block_s_10.obj"),
		IScenario.block("berlin3d/blocks/block_s_11.obj"),
		IScenario.block("berlin3d/blocks/block_s_12.obj"),
		IScenario.block("berlin3d/blocks/block_s_13.obj"),
		IScenario.block("berlin3d/blocks/block_s_14.obj"),
		IScenario.block("berlin3d/blocks/block_s_15.obj"),
		IScenario.block("berlin3d/blocks/block_s_16.obj"),
		IScenario.block("berlin3d/blocks/block_s_17.obj"),
		IScenario.block("berlin3d/blocks/block_s_18.obj"),
		IScenario.block("berlin3d/blocks/block_s_19.obj"),
		IScenario.block("berlin3d/blocks/block_s_20.obj"),
		IScenario.block("berlin3d/blocks/block_s_21.obj"),
		IScenario.block("berlin3d/blocks/block_s_22.obj"),
		IScenario.block("berlin3d/blocks/block_s_23.obj"),
		IScenario.block("berlin3d/blocks/block_s_24.obj"),
		IScenario.block("berlin3d/blocks/block_s_25.obj"),
	};
	
	private static final IDesignEntity[] BLOCKS_NOP  = {
		IScenario.block("berlin3d/blocks/block_nop_04.obj"),
		IScenario.block("berlin3d/blocks/block_nop_01.obj"),
		IScenario.block("berlin3d/blocks/block_nop_02.obj"),
		IScenario.block("berlin3d/blocks/block_nop_03.obj"),
		IScenario.block("berlin3d/blocks/block_nop_05.obj"),
		IScenario.block("berlin3d/blocks/block_nop_06.obj"),
		IScenario.block("berlin3d/blocks/block_nop_07.obj"),
		IScenario.block("berlin3d/blocks/block_nop_08.obj"),		
	};
	
	private static final IDesignEntity[] BUILDINGS_AP = {
		IScenario.building("berlin3d/buildings/building_bd_19.obj"),
		IScenario.building("berlin3d/buildings/building_bd_20.obj"),
		IScenario.building("berlin3d/buildings/building_bd_21.obj"),
		IScenario.building("berlin3d/buildings/building_bd_22.obj"),
		IScenario.building("berlin3d/buildings/building_bd_23.obj"),
		IScenario.building("berlin3d/buildings/building_bd_24.obj"),
		IScenario.building("berlin3d/buildings/building_bd_25.obj"),
		IScenario.building("berlin3d/buildings/building_bd_26.obj"),
		IScenario.building("berlin3d/buildings/building_bd_27.obj"),
		IScenario.building("berlin3d/buildings/building_bd_28.obj"),
		IScenario.building("berlin3d/buildings/building_bd_29.obj"),
		IScenario.building("berlin3d/buildings/building_bd_30.obj"),
	};

	private static final IDesignEntity[] BUILDINGS_RF = {
		IScenario.building("berlin3d/buildings/building_bd_02.obj"),
		IScenario.building("berlin3d/buildings/building_bd_03.obj"),
		IScenario.building("berlin3d/buildings/building_bd_05.obj"),
		IScenario.building("berlin3d/buildings/building_bd_06.obj"),
		IScenario.building("berlin3d/buildings/building_bd_07.obj"),
		IScenario.building("berlin3d/buildings/building_bd_08.obj"),
		IScenario.building("berlin3d/buildings/building_bd_10.obj"),
		IScenario.building("berlin3d/buildings/building_bd_11.obj"),
		IScenario.building("berlin3d/buildings/building_bd_12.obj"),
		IScenario.building("berlin3d/buildings/building_bd_13.obj"),
		IScenario.building("berlin3d/buildings/building_bd_15.obj"),
		IScenario.building("berlin3d/buildings/building_bd_17.obj"),
		IScenario.building("berlin3d/buildings/building_bd_18.obj"),
	};
	
	private static final IDesignEntity[] BUILDINGS_NOP = {
		IScenario.building("berlin3d/buildings/building_nop_01.obj"),
		IScenario.building("berlin3d/buildings/building_nop_02.obj"),		
	};
	
	private final boolean alexanderplatzOnly;
	
	public BerlinScenario(boolean alexanderplatzOnly) {
		this.alexanderplatzOnly = alexanderplatzOnly;
	}
	
	@Override
	public String getName() {
		return alexanderplatzOnly ? "Alexanderplatz Only" : "Alexanderplatz and Rathausforum";
	}

	@Override
	public String[] getStaticGround() {
		return new String[] { STATIC_GROUND };
	}

	@Override
	public String[] getStaticWater() {
		return new String[] { STATIC_WATER };
	}

	@Override
	public String[] getStaticBlocks() {
		List<String> b = new ArrayList<>();
		b.add(STATIC_BLOCKS);
		for (IDesignEntity e : BLOCKS_NOP)
			b.add(e.getAsset());
		if (alexanderplatzOnly) {
			for (IDesignEntity e : BLOCKS_RF)
				b.add(e.getAsset());
		}
		return b.toArray(new String[0]);
	}

	@Override
	public String[] getStaticGreen() {
		return new String[] { STATIC_GREEN };
	}

	@Override
	public String[] getStaticBuildings() {
		List<String> b = new ArrayList<>();
		b.add(STATIC_BUILDINGS);
		for (IDesignEntity e : BUILDINGS_NOP)
			b.add(e.getAsset());
		if (alexanderplatzOnly) {
			for (IDesignEntity e : BUILDINGS_RF)
				b.add(e.getAsset());
		}
		return b.toArray(new String[0]);
	}

	@Override
	public String[] getStaticTrain() {
		return new String[] { STATIC_TRAIN };
	}
	
	@Override
	public List<IDesignEntity> getEntities() {
		List<IDesignEntity> entities = new ArrayList<>();
		Collections.addAll(entities, BLOCKS_AP);
		Collections.addAll(entities, BUILDINGS_AP);
		if (!alexanderplatzOnly) {
			Collections.addAll(entities, BLOCKS_RF);
			Collections.addAll(entities, BUILDINGS_RF);
		}
		return entities;
	}
	
	@Override
	public BoundingBox getBounds() {
		BoundingBox b = new BoundingBox();
		if (alexanderplatzOnly) {
			b.add(AP_MIN);
			b.add(AP_MAX);
		} else {
			b.add(AP_RF_MIN);
			b.add(AP_RF_MAX);
		}
		return b;
	}
	
	@Override
	public List<Vec3> getIntroCameraVertices() {
		if (alexanderplatzOnly)
			return Arrays.asList(INTRO_AP);
		else
			return Arrays.asList(INTRO_AP_RF);
		//return generatePath(20, 100.0f, 100.0f);
	}
	
	@Override
	public List<Vec3> getLoopCameraVertices() {
		if (alexanderplatzOnly)
			return Arrays.asList(LOOP_AP);
		else
			return Arrays.asList(LOOP_AP_RF);
		//return generatePath(40, -100.0f, 100.0f);
	}
	
//	private List<Vec3> generatePath(int n, float x, float z) {
//		List<Vec3> path = new ArrayList<>();
//		float by = getBounds().getExtentY();
//		float dy = by / n;
//		float y = -by / 2;
//		for (int i = 0; i < n; ++i, y += dy)
//			path.add(new Vec3(x, y, z));
//		return path;
//	}
	
	private static final Vec3[] INTRO_AP_RF = new Vec3[] {
			// intro
			new Vec3(1213.7112, -992.38806, 20.0),
			new Vec3(1072.3823, -844.57556, 20.0),
			new Vec3(897.3141, -731.9398, 18.0),
			new Vec3(700.5009, -616.77734, 16.0),
			new Vec3(635.625, -469.89923, 16.0),
			new Vec3(548.57214, -306.482, 16.0),
			new Vec3(501.2925, -152.01266, 16.0),
			new Vec3(380.47, -9.620026, 15.0),
			new Vec3(231.3877, 148.77412, 15.0),
			new Vec3(79.825935, 269.33173, 20.0),
			new Vec3(-97.97158, 326.83325, 43.0),
			new Vec3(-273.29016, 269.74194, 84.0),
			new Vec3(-456.52283, 151.18736, 160.0),
			new Vec3(-622.73376, -19.814781, 160.0),
			new Vec3(-712.125, -226.06061, 160.0),
			new Vec3(-637.8302, -393.45093, 160.0),
			new Vec3(-474.5549, -445.39807, 160.0),
			new Vec3(-340.05463, -422.70117, 160.0),
			new Vec3(-234.99292, -366.76123, 160.0),
			new Vec3(-144.12778, -309.01672, 160.0),
	};
	
	private static final Vec3[] LOOP_AP_RF = new Vec3[] {
			// loop
			new Vec3(-234.99292, -366.76123, 160.0),
			new Vec3(-144.12778, -309.01672, 160.0),
			new Vec3(-54.09419, -249.16417, 160.0),
			new Vec3(31.085361, -188.5913, 160.0),
			new Vec3(111.45688, -127.79367, 150.0),
			new Vec3(189.46072, -69.692276, 132.0),
			new Vec3(265.8752, -11.532953, 105.0),
			new Vec3(345.38812, 53.555897, 57.0),
			new Vec3(406.5849, 112.36267, 28.0),
			new Vec3(473.26575, 176.53726, 10.0),
			new Vec3(525.16266, 254.94221, 7.0),
			new Vec3(462.46686, 321.8297, 5.0),
			new Vec3(381.04526, 373.29987, 5.0),
			new Vec3(313.40073, 386.27347, 5.0),
			new Vec3(248.65887, 333.08685, 5.0),
			new Vec3(187.175, 268.3444, 5.0),
			new Vec3(114.9937, 193.63544, 5.0),
			new Vec3(51.215534, 134.81859, 6.0),
			new Vec3(-28.553755, 67.92759, 8.0),
			new Vec3(-125.58177, -1.1351963, 11.0),
			new Vec3(-249.0459, -91.41991, 14.0),
			new Vec3(-321.56567, -199.28566, 43.0),
			new Vec3(-267.9953, -347.4219, 107.0),
			new Vec3(-119.329025, -435.97653, 160.0),
			new Vec3(64.344536, -397.55792, 160.0),
			new Vec3(205.35345, -300.19684, 160.0),
			new Vec3(295.15878, -153.65562, 160.0),
			new Vec3(304.5946, 16.88383, 160.0),
			new Vec3(211.9765, 146.08842, 160.0),
			new Vec3(79.888306, 228.47182, 160.0),
			new Vec3(-54.19763, 256.11243, 160.0),
			new Vec3(-177.08635, 249.93317, 160.0),
			new Vec3(-309.2738, 215.52554, 160.0),
			new Vec3(-445.8826, 136.55826, 160.0),
			new Vec3(-553.5524, 24.9877, 160.0),
			new Vec3(-636.14484, -116.843346, 160.0),
			new Vec3(-651.6145, -266.4372, 160.0),
			new Vec3(-581.26935, -401.9735, 160.0),
			new Vec3(-458.10507, -450.92767, 160.0),
			new Vec3(-348.45758, -420.30765, 160.0),
	};
	
	private static final Vec3[] INTRO_AP = new Vec3[] {
			// intro
			new Vec3(-1188.6715, 325.7528, 20.0),
			new Vec3(-1089.8418, 266.87613, 20.0),
			new Vec3(-926.39246, 217.93648, 20.0),
			new Vec3(-784.3792, 194.31583, 20.0),
			new Vec3(-663.3781, 179.15927, 20.0),
			new Vec3(-554.2841, 183.60445, 20.0),
			new Vec3(-440.59897, 203.20897, 20.0),
			new Vec3(-343.63467, 236.74933, 22.0),
			new Vec3(-245.2433, 287.946, 25.0),
			new Vec3(-144.24239, 319.41562, 38.0),
			new Vec3(-41.000362, 319.17365, 48.0),
			new Vec3(51.792076, 287.47702, 60.0),
			new Vec3(141.64218, 225.40073, 82.0),
			new Vec3(213.96686, 159.2899, 108.0),
			new Vec3(280.3473, 87.98105, 136.0),
			new Vec3(355.89105, 13.999588, 150.0),
			new Vec3(442.2851, -41.408646, 150.0),
			new Vec3(529.349, -59.788967, 150.0),
			new Vec3(599.3202, -32.38195, 150.0),
			new Vec3(666.5735, 26.274923, 150.0),
	};
	
	private static final Vec3[] LOOP_AP = new Vec3[] {
			// loop
			new Vec3(599.3202, -32.38195, 150.0),
			new Vec3(666.5735, 26.274923, 150.0),
			new Vec3(709.8979, 119.74842, 150.0),
			new Vec3(734.9936, 226.48016, 150.0),
			new Vec3(751.8225, 319.94626, 139.0),
			new Vec3(765.7222, 406.15012, 119.0),
			new Vec3(773.6181, 496.08972, 91.0),
			new Vec3(765.69385, 573.0033, 65.0),
			new Vec3(732.4224, 644.2225, 36.000008),
			new Vec3(687.4511, 694.53265, 20.0),
			new Vec3(636.0989, 721.5387, 12.0),
			new Vec3(589.7498, 726.8159, 8.0),
			new Vec3(554.2392, 707.0813, 6.0),
			new Vec3(524.9631, 678.79834, 5.0),
			new Vec3(493.7285, 643.2733, 5.0),
			new Vec3(463.41257, 604.5303, 5.0),
			new Vec3(432.49222, 564.74, 5.0),
			new Vec3(404.85785, 529.9224, 5.0),
			new Vec3(372.6127, 489.30585, 5.0),
			new Vec3(341.9581, 439.2202, 5.0),
			new Vec3(349.24298, 382.9903, 5.000004),
			new Vec3(414.81244, 321.062, 5.0),
			new Vec3(512.0924, 243.02045, 7.9999847),
			new Vec3(646.9556, 158.06754, 28.99997),
			new Vec3(855.1634, 118.026215, 53.49997),
			new Vec3(1227.6294, 203.21542, 58.000015),
			new Vec3(1351.0604, 392.26862, 72.000015),
			new Vec3(1267.9833, 568.9002, 98.0),
			new Vec3(1085.8146, 562.6236, 130.0),
			new Vec3(920.7514, 502.861, 144.00018),
			new Vec3(764.66406, 453.24332, 150.0),
			new Vec3(603.51666, 395.249, 150.0),
			new Vec3(419.5697, 342.93787, 149.99992),
			new Vec3(265.2222, 298.0648, 150.0),
			new Vec3(127.03484, 224.93312, 150.0),
			new Vec3(109.16999, 130.01128, 150.0),
			new Vec3(211.93091, 61.599693, 150.0),
			new Vec3(310.88022, 15.374615, 150.0),
			new Vec3(413.03268, -31.105064, 150.0),
			new Vec3(523.1385, -59.018166, 150.0),
	};
}
