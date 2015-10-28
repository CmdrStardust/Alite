package de.phbouillon.android.games.alite.screens.opengl.sprites;

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

import java.io.Serializable;
import java.util.Locale;

public class SpriteData implements Serializable {
	private static final long serialVersionUID = 8049223987494885758L;
	public final String name;
	public final float x, y, x2, y2, origWidth, origHeight;
	
	public SpriteData(String name, float x, float y, float x2, float y2, float origWidth, float origHeight) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.x2 = x2;
		this.y2 = y2;
		this.origWidth = origWidth;
		this.origHeight = origHeight;
	}
	
	@Override
	public String toString() {
		return String.format(Locale.getDefault(), "%s [%5.2f, %5.2f] - [%5.2f, %5.2f], W: %5.2f, H: %5.2f", name, x, y, x2, y2, origWidth, origHeight);
	}
}