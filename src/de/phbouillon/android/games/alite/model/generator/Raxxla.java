package de.phbouillon.android.games.alite.model.generator;

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

import de.phbouillon.android.games.alite.model.generator.enums.Economy;
import de.phbouillon.android.games.alite.model.generator.enums.Government;

/**
 * Yes, Raxxla... It's here. In Alite. -- Hey, no peeking! :)
 */
public class Raxxla {
	private SystemData raxxlaSystem;
	
	public Raxxla() {
		WritableSystemData temp = new WritableSystemData();
		temp.setCloudsTexture(1);
		temp.setDescription(
				"The fabled planet Raxxla is the home of all Elite pilots in the universe. " +
				"It provides retreat and peace for them along with a portal to another universe.");
		temp.setDiameter(42000);
		temp.setEconomy(Economy.RICH_INDUSTRIAL);
		temp.setFuelPrice(1);
		temp.setGovernmentType(Government.CORPORATE_STATE);
		temp.setIndex(256);
		temp.setInhabitantCode(null);
		temp.setInhabitants("Friendly Green Treeards");
		temp.setName("Raxxla");
		temp.setPlanetTexture(1);
		temp.setPopulation(4);
		temp.setProductivity(63568);
		temp.setRingsTexture(16);
		temp.setTechLevel(22);
		temp.setX(12);
		temp.setY(127);
		raxxlaSystem = temp;		
	}
	
	public final SystemData getSystem() {
		return raxxlaSystem;
	}
}
