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

public class InhabitantComputation {
	private static final String [] DESCRIPTION = {
		"Large", "Fierce", "Small"
	};
	
	private static final String [] COLOR = {
		"Green", "Red", "Yellow", "Blue", "White", "Harmless"
	};
	
	private static final String [] APPEARANCE = {
		"Slimy", "Bug-eyed", "Horned", "Bony", "Fat", "Furry", "Mutant", "Weird"
	};
	
	private static final String [] TYPE = {
		"Rodent", "Frog", "Lizard", "Lobster", "Bird", "Humanoid", "Feline", "Insect"
	};

	private static String getDescription(int index) {
		return index < DESCRIPTION.length ? DESCRIPTION[index] : "";
	}
	
	private static String getColor(int index) {
		return index < COLOR.length ? COLOR[index] : "";
	}
	
	private static String getAppearance(int index) {
		return index < APPEARANCE.length ? APPEARANCE[index] : "";
	}
	
	private static String getType(int index) {
		return index < TYPE.length ? TYPE[index] : "";
	}

	private static void computeHumanColonial(SeedType seed, SystemData result) {
		// This generates unique binary representations for all Human Colonials
		// in the 8 "official" galaxies, _except_ for exactly two planets who
		// have the exact same human inhabitant code... The plan was to create
		// a mission including these two planets.
		String inhabitantCode = Integer.toBinaryString(-seed.getWord(0) * 3 - seed.getWord(1) * 5 + seed.getWord(2) * 7);			
		if (inhabitantCode.length() > 20) {
			inhabitantCode = inhabitantCode.substring(inhabitantCode.length() - 20);
		}
		while (inhabitantCode.length() < 20) {
			inhabitantCode = "0" + inhabitantCode; 
		}
		inhabitantCode = "0000" + inhabitantCode + new StringBuilder(inhabitantCode.substring(11, 19)).reverse().toString();
		if (seed.getLoByte(2) < 4) {
			inhabitantCode = inhabitantCode.substring(0, 31) + "1";
		}
		result.inhabitantCode = inhabitantCode;
	}
	
	public static String computeInhabitantString(SeedType seed, SystemData result) {
		if (seed.getLoByte(2) < 128) {
			computeHumanColonial(seed, result);
			return "Human Colonial";
		}
		result.inhabitantCode = null;
		StringBuilder inhabitantName = new StringBuilder();
		int descriptionFlag = seed.getHiByte(2) >> 2;
		int colorFlag = descriptionFlag;
		descriptionFlag &= 7;
		inhabitantName.append(getDescription(descriptionFlag));
		
		colorFlag >>= 3;
		colorFlag &= 7;
		StringUtil.addSpaceAndStringToBuilder(getColor(colorFlag), inhabitantName);
		
		int appearanceFlag = seed.getHiByte(0) ^ seed.getHiByte(1);
		int temp = appearanceFlag;
		appearanceFlag &= 7;
		StringUtil.addSpaceAndStringToBuilder(getAppearance(appearanceFlag), inhabitantName);
		
		int typeFlag = ((seed.getHiByte(2) & 3) + temp) & 7;
		StringUtil.addSpaceAndStringToBuilder(getType(typeFlag), inhabitantName);

		return inhabitantName.toString();
	}
}
