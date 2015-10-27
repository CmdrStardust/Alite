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

public class CommanderData {
	private final String name;
	private final String dockedSystem;
	private final long gameTime;
	private final int points;
	private final Rating rating;
	private final String fileName;
	
	public CommanderData(String name, String dockedSystem, long gameTime, int points, Rating rating, String fileName) {
		this.name = name;
		this.dockedSystem = dockedSystem;
		this.gameTime = gameTime;
		this.points = points;
		this.rating = rating;
		this.fileName = fileName;
	}
	
	public String getName() { 
		return name; 
	}
	
	public String getDockedSystem() {
		return dockedSystem;
	}
	
	public long getGameTime() {
		return gameTime;
	}
	
	public int getPoints() {
		return points;
	}
	
	public Rating getRating() {
		return rating;
	}

	public String getFileName() {
		return fileName;
	}
}
