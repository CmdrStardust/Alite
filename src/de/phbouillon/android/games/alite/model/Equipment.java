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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import de.phbouillon.android.games.alite.AliteLog;

public class Equipment implements Serializable {
	private static final long serialVersionUID = -3769076685902433528L;

	private final String name;
	private final int cost;
	private final String shortName;
	private final String descriptiveQuantity;
	private final boolean canBeLost;
	
	public Equipment(String name, int cost, String shortName, String descriptiveQuantity, boolean canBeLost) {
		this.name = name;
		this.cost = cost;
		this.shortName = shortName;
		this.descriptiveQuantity = descriptiveQuantity;
		this.canBeLost = canBeLost;
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Equipment " + getName(), e);
			throw(e);
		}
    }

	public String getName() {
		return name;
	}
	
	public int getCost() {
		return cost;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String getDescriptiveQuantity() {
		return descriptiveQuantity;
	}
	
	public boolean canBeLost() {
		return canBeLost;
	}
}
