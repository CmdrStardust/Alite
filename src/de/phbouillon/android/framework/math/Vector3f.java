package de.phbouillon.android.framework.math;

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

public class Vector3f implements Serializable {	
	private static final long serialVersionUID = -540022331413138350L;

	public float x;
	public float y;
	public float z;
	
	public Vector3f(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Vector3f " + this, e);
			throw(e);
		}
    }

	public float get(final int i) {
		return i == 0 ? x : i == 1 ? y : z;
	}
	
	public void add(Vector3f b) {
		x += b.x;
		y += b.y;
		z += b.z;
	}
	
	public void sub(Vector3f b) {
		x -= b.x;
		y -= b.y;
		z -= b.z;
	}
	
	public void scale(float s) {
		x *= s;
		y *= s;
		z *= s;
	}

	public void add(Vector3f b, Vector3f r) {
		r.x = x + b.x;
		r.y = y + b.y;
		r.z = z + b.z;
	}

	public void add(Vector3f b, float scale) {
		x += b.x * scale;
		y += b.y * scale;
		z += b.z * scale;
	}

	public void sub(Vector3f b, Vector3f r) {
		r.x = x - b.x;
		r.y = y - b.y;
		r.z = z - b.z;
	}
	
	public void scale(float s, Vector3f r) {
		r.x = x * s;
		r.y = y * s;
		r.z = z * s;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float lengthSq() {
		return x * x + y * y + z * z;
	}
	
	public float distance(Vector3f v) {
		return (float) Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z));
	}
	
	public float distanceSq(Vector3f v) {
		return (x - v.x) * (x - v.x) + (y - v.y) * (y - v.y) + (z - v.z) * (z - v.z);
	}
	
	public void normalize() {
		float root = (float) Math.sqrt(x * x + y * y + z * z);
		float t = root < 0.00001f ? 1.0f : 1.0f / root;
		x *= t;
		y *= t;
		z *= t;
	}
	
	public void normalize(Vector3f result) {
		float root = (float) Math.sqrt(x * x + y * y + z * z);
		float t = root < 0.00001f ? 1.0f : 1.0f / root;
		result.x = x * t;
		result.y = y * t;
		result.z = z * t;
	}

	public boolean isZeroVector() {
		return Math.abs(x) < 0.0001 && Math.abs(y) < 0.0001 && Math.abs(z) < 0.0001;
	}
	
	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}
	
	public void cross(Vector3f b) {
		float tx = y * b.z - b.y * z;
		float ty = z * b.x - b.z * x;
		float tz = x * b.y - b.x * y;
		x = tx;
		y = ty;
		z = tz;
	}
	
	public void cross(Vector3f b, Vector3f r) {
		r.x = y * b.z - b.y * z;
		r.y = z * b.x - b.z * x;
		r.z = x * b.y - b.x * y;
	}

	public float dot(Vector3f b) {
		return x * b.x + y * b.y + z * b.z;
	}
	
	public String toString() {
		return String.format(Locale.getDefault(), "[%5.2f %5.2f %5.2f]", x, y, z);
	}
	
	public void copy(Vector3f dest) {
		dest.x = x;
		dest.y = y;
		dest.z = z;
	}
	
	public void mulMat(float [] mat) {
		float tx = mat[ 0] * x + mat[ 4] * y + mat[ 8] * z + mat[12];
		float ty = mat[ 1] * x + mat[ 5] * y + mat[ 9] * z + mat[13];
		float tz = mat[ 2] * x + mat[ 6] * y + mat[10] * z + mat[14];
		float td = mat[ 3] * x + mat[ 7] * y + mat[11] * z + mat[15];
		if (Math.abs(td) > 0.0001) {
			tx /= td;
			ty /= td;
			tz /= td;
		}
		x = tx;
		y = ty;
		z = tz;
	}
	
	public void mulMat(float [] mat, Vector3f r) {		
		r.x = mat[ 0] * x + mat[ 4] * y + mat[ 8] * z + mat[12];
		r.y = mat[ 1] * x + mat[ 5] * y + mat[ 9] * z + mat[13];
		r.z = mat[ 2] * x + mat[ 6] * y + mat[10] * z + mat[14];
		float td = mat[ 3] * x + mat[ 7] * y + mat[11] * z + mat[15];
		if (Math.abs(td) > 0.0001) {
			r.x /= td;
			r.y /= td;
			r.z /= td;
		}
	}
	
	// Assumption: Both vectors are normalized!
	public float angleInDegrees(Vector3f o) {
		float dot = this.dot(o);
		if (dot < -1) { 
			dot = -1; 
		}
		if (dot > 1) {
			dot = 1;
		}
		return (float) Math.toDegrees(Math.acos(dot));
	}

	public void project(Vector3f onto, Vector3f result) {
		onto.normalize(result);
		result.scale(dot(result));
	}
}