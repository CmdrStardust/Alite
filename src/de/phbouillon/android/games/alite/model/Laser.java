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

public class Laser extends Equipment implements Serializable {
	private static final long serialVersionUID = -8132898632556359341L;

	private final int index;
	private long lastShot = 0;
	private final long delayTime;
	private final int power;
	private final long color;
	private final boolean beam;
	private final String textureName;
	
	public Laser(String name, int cost, String shortName, int index, String quantityDescription, long delay, int power, long color, boolean beam, String texture) {
		super(name, cost, shortName, quantityDescription, false);
		this.index = index;
		this.power = power;
		this.delayTime = delay;
		this.color = color;
		this.beam = beam;
		this.textureName = texture;
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Laser " + getName(), e);
			throw(e);
		}
    }

	public int getIndex() {
		return index;
	}
	
	private final boolean canFireAgain() {
		return lastShot == 0 || ((System.nanoTime() - lastShot) > delayTime);
	}
	
	public boolean fire() {
		if (!canFireAgain()) {
			return false;
		}
		lastShot = System.nanoTime();
		return true;
	}
	
	public int getPower() {
		return power;
	}
	
	public long getColor() {
		return color;
	}
	
	public String getTexture() {
		return textureName;
	}
	
	public boolean isBeam() {
		return beam;
	}
}
