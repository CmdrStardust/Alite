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

public enum Rating {
	HARMLESS       ("Harmless",           2048),
	MOSTLY_HARMLESS("Mostly Harmless",    4096),
	POOR           ("Poor",               8192),
	AVERAGE        ("Average",           16384),
	ABOVE_AVERAGE  ("Above Average",     32768),
	COMPETENT      ("Competent",         65536),
	DANGEROUS      ("Dangerous",        262144),
	DEADLY         ("Deadly",           655360),
	ELITE          ("E-L-I-T-E",            -1);
	
	private String name;
	private int upToScore;
	
	Rating(String name, int upToScore) {
		this.name = name;
		this.upToScore = upToScore;
	}
	
	public String getName() {
		return name;
	}
	
	public int getScoreThreshold() {
		return upToScore;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
