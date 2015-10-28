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

import de.phbouillon.android.games.alite.colors.AliteColors;

public enum Condition {
	DOCKED("Docked", AliteColors.get().conditionGreen()), 
	GREEN("Green",   AliteColors.get().conditionGreen()), 
	YELLOW("Yellow", AliteColors.get().conditionYellow()), 
	RED("Red",       AliteColors.get().conditionRed());
	
	private String name;
	private long color;
	
	Condition(String name, long color) {
		this.name = name;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public long getColor() {
		return color;
	}
	
	public static void update() {
		DOCKED.color = AliteColors.get().conditionGreen();
		GREEN.color = AliteColors.get().conditionGreen();
		YELLOW.color = AliteColors.get().conditionYellow();
		RED.color = AliteColors.get().conditionRed();
	}
}
