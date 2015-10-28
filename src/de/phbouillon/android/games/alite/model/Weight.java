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
import java.util.Locale;

import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.trading.Unit;

public class Weight implements Comparable <Weight>, Serializable {
	private static final long serialVersionUID = -7041624303034666651L;

	public static final int GRAMS     = 1;
	public static final int KILOGRAMS = 1000;
	public static final int TONNES    = 1000000;
	
	private final long grams;
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Weight " + this, e);
			throw(e);
		}
    }

	public static Weight unit(Unit u, long amount) {
		switch (u) {
			case TON: return tonnes(amount);
			case KILOGRAM: return kilograms(amount);
			case GRAM: return grams(amount);
		}
		return null;
	}
	
	public static Weight tonnes(long t) {
		return new Weight(t * 1000000l);
	}
	
	public static Weight kilograms(long kg) {
		return new Weight(kg * 1000l);
	}
	
	public static Weight grams(long g) {
		if (g < 0) { 
			g = 0; 
		}
		return new Weight(g);
	}
	
	private Weight(long grams) {
		this.grams = grams;
	}

	public Weight set(Weight weight) {
		return grams(weight.getWeightInGrams());
	}
	
	@Override
	public int compareTo(Weight another) {
		return another == null || grams > another.grams ? 1 : grams == another.grams ? 0 : -1;
	}
	
	public Weight add(Weight another) {
		return grams(grams + another.grams);
	}
	
	public Weight sub(Weight another) {
		return grams(grams - another.grams);
	}
	
	public int getAppropriateUnit() {
		return grams < 1000 ? GRAMS : grams < 1000000 ? KILOGRAMS : TONNES;
	}
		
	public static String getUnitString(int unit) {
		return unit == GRAMS ? "g" : unit == KILOGRAMS ? "kg" : "t";
	}
	
	public int getQuantityInAppropriateUnit() {
		int unit = getAppropriateUnit();
		return unit == GRAMS ? (int) grams : unit == KILOGRAMS ? (int) (grams / 1000) : (int) (grams / 1000000); 
	}
	
	public long getWeightInGrams() {
		return grams;
	}
	
	public String getStringWithoutUnit() {
		if (grams < 1000) {
			return String.format(Locale.getDefault(), "%d", grams);
		} else if (grams < 1000000) {
			return String.format(Locale.getDefault(), "%d.%d", grams / 1000, (grams / 100) % 10);
		} else {
			return String.format(Locale.getDefault(), "%d.%d", grams / 1000000, (grams / 100000) % 10);
		}
	}
	
	public String getFormattedString() {
		return getStringWithoutUnit() + getUnitString(getAppropriateUnit());
	}
	
	@Override
	public String toString() {
		return getFormattedString();
	}
}
