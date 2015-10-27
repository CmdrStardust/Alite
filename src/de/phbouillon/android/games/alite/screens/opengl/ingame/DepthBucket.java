package de.phbouillon.android.games.alite.screens.opengl.ingame;

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
import java.util.ArrayList;
import java.util.List;

import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;

class DepthBucket implements Serializable {
	private static final long serialVersionUID = 3003956374610521180L;

	float near;
	float far;
	int spaceObjectCount;
	final List <AliteObject> sortedObjects = new ArrayList<AliteObject>();	
	
	DepthBucket(float near, float far) {
		this.near = near;
		this.far = far;
		spaceObjectCount = 0;
	}
}
