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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import de.phbouillon.android.framework.impl.Pool;
import de.phbouillon.android.framework.impl.Pool.PoolObjectFactory;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.AliteLog;

public class WayPoint implements Serializable {
	private static final long serialVersionUID = 1457361922469761843L;

	public final Vector3f upVector = new Vector3f(0, 0, 0);
	public final Vector3f position = new Vector3f(0, 0, 0);
	public boolean orientFirst = false;
		
	private static transient PoolObjectFactory <WayPoint> waypointFactory = new WayPointFactory();	
	private static transient Pool <WayPoint> pool = new Pool<WayPoint>(waypointFactory, 100);

	WayPoint() {		
	}
		
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "WayPoint.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "WayPoint.readObject I");
			waypointFactory = new WayPointFactory();
			pool = new Pool<WayPoint>(waypointFactory, 100);
			pool.reset();
			AliteLog.e("readObject", "WayPoint.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	public static WayPoint newWayPoint() {
		return newWayPoint(0, 0, 0, 0, 0, 0);
	}
	
	public static WayPoint newWayPoint(Vector3f position, Vector3f up) {
		return newWayPoint(position.x, position.y, position.z, up.x, up.y, up.z);
	}
	
	public static WayPoint newWayPoint(float x, float y, float z, float ux, float uy, float uz) {
		WayPoint result = pool.newObject();
		
		result.position.x = x;
		result.position.y = y;
		result.position.z = z;
		
		result.upVector.x = ux;
		result.upVector.y = uy;
		result.upVector.z = uz;
		
		result.orientFirst = false;
				
		return result;
	}
	
	public void reached() {
		pool.free(this);
	}
		
	public float distanceSq(GraphicObject go) {
		return position.distanceSq(go.getPosition());
	}
	
	public String toString() {
		return position.toString();
	}
}
