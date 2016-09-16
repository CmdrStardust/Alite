package de.phbouillon.android.games.alite.model.generator;

import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.trading.TradeGood;

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

public class GalaxyGenerator {
	// Base seed for the first galaxy
	private static final char [] BASE_SEED = {0x5a4a, 0x0248, 0xb753};		
	
	// Current seed of this galaxy
	private SeedType seed;
	private char [] currentSeed;
	
	// All 256 systems in this galaxy
	private SystemData [] system = new SystemData[256];
	
	private int currentGalaxy = 1;
	
	public void buildGalaxy(int galaxyNumber) {
		currentGalaxy = galaxyNumber;
		// Initialize first galaxy
		seed = new SeedType(BASE_SEED[0], BASE_SEED[1], BASE_SEED[2]);

		// Calculate seed for galaxy 'galaxyNumber'
		for (int galaxyCount = 1; galaxyCount < galaxyNumber; galaxyCount++) {
			nextGalaxy();
		}

		currentSeed = new char[] { seed.getWord(0), seed.getWord(1),
				seed.getWord(2) };
		buildGalaxy();
	}
	
	public char [] getCurrentSeed() {
		return currentSeed;
	}
	
	public char [] getNextSeed() {
		int nextGal = getCurrentGalaxy() + 1;
		if (nextGal > 8 || nextGal < 1) {
			nextGal = 1;
		}
		switch (nextGal) {
			case 1: return new char [] {0x5A4a, 0x0248, 0xB753}; 
			case 2: return new char [] {0xB494, 0x0490, 0x6FA6};
			case 3: return new char [] {0x6929, 0x0821, 0xDE4D};
			case 4: return new char [] {0xD252, 0x1042, 0xBD9A};
			case 5: return new char [] {0xA5A4, 0x2084, 0x7B35};
			case 6: return new char [] {0x4B49, 0x4009, 0xF66A};
			case 7: return new char [] {0x9692, 0x8012, 0xEDD4};
			case 8: return new char [] {0x2D25, 0x0124, 0xDBA9};
		}
		return new char [] {0x5A4a, 0x0248, 0x0B753};
	}
	
	private int determineGalaxyNumber(int seed0, int seed1, int seed2) {
		     if (seed0 == 0x5A4A && seed1 == 0x0248 && seed2 == 0xB753) { return 1; }
		else if (seed0 == 0xB494 && seed1 == 0x0490 && seed2 == 0x6FA6) { return 2; }
		else if (seed0 == 0x6929 && seed1 == 0x0821 && seed2 == 0xDE4D) { return 3; }
		else if (seed0 == 0xD252 && seed1 == 0x1042 && seed2 == 0xBD9A) { return 4; }
		else if (seed0 == 0xA5A4 && seed1 == 0x2084 && seed2 == 0x7B35) { return 5; }
		else if (seed0 == 0x4B49 && seed1 == 0x4009 && seed2 == 0xF66A) { return 6; }
		else if (seed0 == 0x9692 && seed1 == 0x8012 && seed2 == 0xEDD4) { return 7; }
		else if (seed0 == 0x2D25 && seed1 == 0x0124 && seed2 == 0xDBA9) { return 8; }
		return -1;
	}
	
	public void buildGalaxy(int seed0, int seed1, int seed2) {
		// Initialize first galaxy
		seed = new SeedType((char) seed0, (char) seed1, (char) seed2);
		currentSeed = new char [] {(char) seed0, (char) seed1, (char) seed2};
		
		int galaxy = determineGalaxyNumber(seed0, seed1, seed2);
		if (galaxy != -1) {
			currentGalaxy = galaxy;
		}
		
		buildGalaxy();
	}	
	
	public int getCurrentGalaxyFromSeed() {
		char seed0 = currentSeed[0];
		char seed1 = currentSeed[1];
		char seed2 = currentSeed[2];
		return determineGalaxyNumber(seed0, seed1, seed2);
	}
	
	public int getCurrentGalaxy() {
		return currentGalaxy;
	}
	
	public void setCurrentGalaxy(int galaxy) {
		currentGalaxy = galaxy;
	}
	
	public boolean setCurrentSeed(char [] seed) {
		if (this.seed.getWord(0) != seed[0] ||
			this.seed.getWord(1) != seed[1] ||
			this.seed.getWord(2) != seed[2]) {
			buildGalaxy(seed[0], seed[1], seed[2]);
			return true;
		}
		return false;
	}
	
	private void buildGalaxy() {
		// Generate the 256 planets in this galaxy
		for (int systemCount = 0; systemCount < 256; systemCount++) {
			system[systemCount] = SystemData.createSystem(systemCount, seed);
		}
	}
	
	private void nextGalaxy() {
		// Roll seed so that the next galaxy is created.
		// Multiplies the seed by 2; repeating this 8 times will return
		// the first galaxy seed again.
		seed.multiplyByTwo();
	}	
	
	public SystemData [] getSystems() {
		return system;
	}
	
	public SystemData getSystem(int index) {
		return system[index];
	}

	public int getAveragePrice(TradeGood tradeGood) {
		return tradeGood.getAveragePrice(currentGalaxy);
	}

	public int findGalaxyOfPlanet(String name) {
		int oldGalaxy = currentGalaxy;
				
		int i = oldGalaxy - 1;
		for (int j = 0; j < 7; j++) {
			i = (i + 1) % 8 + 1;
			AliteLog.d("Analyzing Galaxy", "Analyzing Galaxy " + i);
			buildGalaxy(i);
			for (SystemData s: system) {
				if (s.getName().equalsIgnoreCase(name)) {
					buildGalaxy(oldGalaxy);
					return i;
				}
			}
			i--;
		}
		buildGalaxy(oldGalaxy);
		return -1;
	}
}
