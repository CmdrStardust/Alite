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
import java.io.Serializable;

import de.phbouillon.android.games.alite.AliteLog;

public class FastSeedType implements Serializable {
	private static final long serialVersionUID = -5374482150403208912L;

	private char a;
	private char b;
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "FastSeedType " + a + " - " + b, e);
			throw(e);
		}
    }

	FastSeedType(char a, char b) {
		this.a = a;
		this.b = b;
	}
	
	public char a() {
		return a;
	}
	
	public char b() {
		return b;
	}
		
	public void setA(char a) {
		this.a = a;
	}
	
	public void setB(char b) {
		this.b = b;
	}	
}
