package de.phbouillon.android.games.alite.screens.opengl.objects;

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

import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;

public class ObjectUtils {
	private static final Vector3f tempVector = new Vector3f(0, 0, 0);
	
	public static final float computeDistanceSq(GraphicObject a, GraphicObject b) {
		float aDistance = 0.0f;
		float bDistance = 0.0f;
		float adx = 0.0f, ady = 0.0f, adz = 0.0f;
		float bdx = 0.0f, bdy = 0.0f, bdz = 0.0f;
		
		if (a instanceof Geometry || b instanceof Geometry) {
			tempVector.x = b.getPosition().x - a.getPosition().x;			 
			tempVector.y = b.getPosition().y - a.getPosition().y;
			tempVector.z = b.getPosition().z - a.getPosition().z;
			tempVector.normalize();
			if (a instanceof Geometry) {
				aDistance = ((Geometry) a).getDistanceFromCenterToBorder(tempVector);
				adx = aDistance * tempVector.x;
				ady = aDistance * tempVector.y;
				adz = aDistance * tempVector.z;
			}
			if (b instanceof Geometry) {
				tempVector.negate();
				bDistance = ((Geometry) b).getDistanceFromCenterToBorder(tempVector);
				bdx = bDistance * tempVector.x;
				bdy = bDistance * tempVector.y;
				bdz = bDistance * tempVector.z;
			}		
		}
		
		return (a.getPosition().x + adx - b.getPosition().x - bdx) * (a.getPosition().x + adx - b.getPosition().x - bdx) +
			   (a.getPosition().y + ady - b.getPosition().y - bdy) * (a.getPosition().y + ady - b.getPosition().y - bdy) +
		       (a.getPosition().z + adz - b.getPosition().z - bdz) * (a.getPosition().z + adz - b.getPosition().z - bdz);		
	}
}
