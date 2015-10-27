package de.phbouillon.android.games.alite.screens.opengl.objects.space;

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

import de.phbouillon.android.framework.math.Vector3f;

public class MathHelper {
	private static final Vector3f v0 = new Vector3f(0, 0, 0);
	private static final Vector3f v1 = new Vector3f(0, 0, 0);
	private static final Vector3f v2 = new Vector3f(0, 0, 0);
	
	public static Vector3f getRandomPosition(Vector3f origin, Vector3f direction, float distance, float radius) {
		v1.x = (float) (0.7 - Math.random() * 1.4);
		v1.y = (float) (0.7 - Math.random() * 1.4);
		v1.z = (float) (0.7 - Math.random() * 1.4);
		v1.normalize();
		v1.scale((float) (Math.random() * radius));
		direction.scale(distance, v0);
		v0.add(origin);
		v0.add(v1, v2);
		
		v2.sub(origin, v1);
		v1.normalize();
		v1.scale(distance);
		origin.add(v1, v2);
		
		return v2;
	}
	
	public static void copyMatrix(float [] src, float [] dest) {
		for (int i = 0; i < 16; i++) dest[i] = src[i];
	}
}
