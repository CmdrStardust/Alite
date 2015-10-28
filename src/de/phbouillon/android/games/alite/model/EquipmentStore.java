package de.phbouillon.android.games.alite.model;

/* Alite - Discover the Universe on your Favorite Android Device
 * Copyright (C) 2015 Philipp Bouillon
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful and
 * fun, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * http://http://www.gnu.org/licenses/gpl-3.0.txt.
 */

public class EquipmentStore {
	public static final int ENTRIES = 18;
	
	public static final Equipment fuel = new Equipment("Fuel (WS Thru-Space Drive)", -1, "Fuel", "", false);
	public static final Equipment missiles = new Equipment("Lance And Ferman Missiles", 300, "Missiles", "a Missile", false);
	public static final Equipment largeCargoBay = new Equipment("Mariner 35t Freight Chamber", 4000, "Large Cargo Bay", "a Large Cargo Bay", false);
	public static final Equipment ecmSystem = new Equipment("Interspace Heavy Element ECM", 6000, "ECM System", "an ECM System", true);
	public static final Laser pulseLaser = new Laser("Ingram Model 1919A4 Pulse Laser", 4000, "Pulse Laser", 0, "a Pulse Laser (%1)", 598802395l, 5, 0x7fffff00l, false, "textures/laser_yellow.png");
	public static final Laser beamLaser = new Laser("Ingram Model 1928A2 Beam Laser", 10000, "Beam Laser", 1, "a Beam Laser (%1)", 359281437l, 9, 0x7f0000ffl, true, "textures/laser_blue.png");
	public static final Equipment fuelScoop = new Equipment("Deep Space ReAqx Fuel Scoop", 5250, "Fuel Scoop", "a Fuel Scoop", true); 
	public static final Equipment escapeCapsule = new Equipment("Xeesian FastJet LSC Escape Capsule", 10000, "Escape Capsule", "an Escape Capsule", true);
	public static final Equipment energyBomb = new Equipment("Medusa Pandora Energy Bomb", 9000, "Energy Bomb", "an Energy Bomb", true);
	public static final Equipment extraEnergyUnit = new Equipment("Extra Zieman Energy Unit", 15000, "Extra Energy Unit", "an Extra Energy Unit", true);
	public static final Equipment dockingComputer = new Equipment("SinCorn RemLock Docking Computer", 15000, "Docking Computer", "a Docking Computer", true);
	public static final Equipment galacticHyperdrive = new Equipment("Xexor/Hikan Galactic Hyperdrive", 50000, "Galactic Hyperdrive", "a Galactic Hyperdrive", true);
	public static final Laser miningLaser = new Laser("Kruger ARM64 Sp. Mining Laser", 8000, "Mining Laser", 2, "a Mining Laser (%1)", 479041916l, 7, 0x7f00ff00l, false, "textures/laser_green.png");
	public static final Laser militaryLaser = new Laser("M1928A2 Military Laser", 60000, "Military Laser", 3, "a Military Laser (%1)", 179640718l, 11, 0x7fff00ffl, true, "textures/laser_purple.png");
	public static final Equipment retroRockets = new Equipment("Xasar Ion Retro-Rockets", 80000, "Retro Rockets", "a set of Retro Rockets", true);
	
	public static final Equipment navalEnergyUnit = new Equipment("Naval Energy Unit", 15000, "Naval Energy Unit", "a Naval Energy Unit", false);
	public static final Equipment cloakingDevice = new Equipment("Cloaking Device", 150000, "Cloaking Device", "a Cloaking Device", false);
	public static final Equipment ecmJammer = new Equipment("ECM Jammer", 150000, "ECM Jammer", "an ECM Jammer", false);
	
	public static final int ordinal(Equipment e) {
		if (e == fuel) return 0;
		if (e == missiles) return 1;
		if (e == largeCargoBay) return 2;
		if (e == ecmSystem) return 3;
		if (e == pulseLaser) return 4;
		if (e == beamLaser) return 5;
		if (e == fuelScoop) return 6;
		if (e == escapeCapsule) return 7;
		if (e == energyBomb) return 8;
		if (e == extraEnergyUnit) return 9;
		if (e == dockingComputer) return 10;
		if (e == galacticHyperdrive) return 11;
		if (e == miningLaser) return 12;
		if (e == militaryLaser) return 13;
		if (e == retroRockets) return 14;
		if (e == navalEnergyUnit) return 15;
		if (e == cloakingDevice) return 16;
		if (e == ecmJammer) return 17;
		
		return 0;
	}
	
	public static final Equipment fromInt(int val) {
		switch (val) {
			case 0: return fuel;
			case 1: return missiles;
			case 2: return largeCargoBay;
			case 3: return ecmSystem;
			case 4: return pulseLaser;
			case 5: return beamLaser;
			case 6: return fuelScoop;
			case 7: return escapeCapsule;
			case 8: return energyBomb;
			case 9: return extraEnergyUnit;
			case 10: return dockingComputer;
			case 11: return galacticHyperdrive;
			case 12: return miningLaser;
			case 13: return militaryLaser;
			case 14: return retroRockets;
			case 15: return navalEnergyUnit;
			case 16: return cloakingDevice;
			case 17: return ecmJammer;
		}
		return null;
	}
}
