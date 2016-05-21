package de.phbouillon.android.games.alite;

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

public enum ShipControl {
	ACCELEROMETER("Accelerometer"),
	ALTERNATIVE_ACCELEROMETER("Alternative Accelerometer"),
	CONTROL_PAD("Control Pad"),
	CURSOR_BLOCK("Cursor Block"),
	CURSOR_SPLIT_BLOCK("Split Cursor Blocks");
	
	private String description;
	
	ShipControl(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
