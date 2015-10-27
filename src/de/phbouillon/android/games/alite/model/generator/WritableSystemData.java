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

import java.io.IOException;
import java.io.ObjectOutputStream;

import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.generator.enums.Economy;
import de.phbouillon.android.games.alite.model.generator.enums.Government;

public class WritableSystemData extends SystemData {
	private static final long serialVersionUID = 3729807075791612718L;

	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "WritableSystemData " + this, e);
			throw(e);
		}
    }

	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setEconomy(Economy economy) {
		this.economy = economy;
	}
	
	public void setGovernmentType(Government govType) {
		this.govType = govType;
	}
	
	public void setTechLevel(int techLevel) {
		this.techLevel = techLevel;
	}
	
	public void setPopulation(int population) {
		this.population = population;
	}
	
	public void setProductivity(int productivity) {
		this.productivity = productivity;
	}
	
	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}

	public void setFuelPrice(int fuelPrice) {
		this.fuelPrice = fuelPrice;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setInhabitants(String inhabitants) {
		this.inhabitants = inhabitants;
	}
	
	public void setInhabitantCode(String inhabitantCode) {
		this.inhabitantCode = inhabitantCode;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setPlanetTexture(int planetTexture) {
		this.planetTexture = planetTexture;
	}
	
	public void setRingsTexture(int ringsTexture) {
		this.ringsTexture = ringsTexture;
	}
	
	public void setCloudsTexture(int cloudsTexture) {
		this.cloudsTexture = cloudsTexture;
	}
}
