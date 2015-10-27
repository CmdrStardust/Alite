package de.phbouillon.android.games.alite.screens.opengl.sprites.buttons;

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

import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.gl.Sprite;

class ButtonData implements Serializable {
	private static final long serialVersionUID = -7572278067214518431L;

	Sprite sprite;
	float centerX;
	float centerY;
	boolean selected = false;
	boolean active = true;
	ButtonGroup parent;
	boolean yellow = false;
	boolean red = false;
	final String name;
	
	ButtonData(Sprite s, float cx, float cy, final String name) {
		sprite = s;
		centerX = cx;
		centerY = cy;
		this.name = name;
	}
	
	boolean isTouched(int x, int y, int touchEventType) {
		// Remember that click events are in "virtual space", hence _always_ range from 0 through 1920 and 0 through 1080 respectively!
		boolean isTouched = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) <= 10000.0f;
		if (touchEventType == TouchEvent.TOUCH_DOWN && isTouched) {
			selected = true;
		}
		return isTouched;
	}
	
	@Override
	public String toString() {
		return "Sprite " + name + " (" + centerX + ", " + centerY + ")";
	}
}