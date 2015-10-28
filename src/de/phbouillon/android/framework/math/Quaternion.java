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

import de.phbouillon.android.games.alite.AliteLog;

public class Quaternion implements Serializable {
	private static final long serialVersionUID = 8295982451561366627L;

	public float x;
	public float y;
	public float z;
	public float w;
	
	private static final Vector3f temp = new Vector3f(0, 0, 0);
	private static final Vector3f xUnit = new Vector3f(1, 0, 0);
	private static final Vector3f yUnit = new Vector3f(0, 1, 0);
	private static final float TOLERANCE = 0.00001f;
	private static final Quaternion t = new Quaternion();
	
	public Quaternion() {
		this(0, 0, 0, 1);
	}
	
	public Quaternion(float x, float y, float z) {
		this(x, y, z, 1);
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Quaternion " + this, e);
			throw(e);
		}
    }

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;		
	}
	
	public void copy(Quaternion dest) {
		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;
	}
	
	public void extractForwardVector(Vector3f vec) {
		vec.x = x * 2.0f * z - w * 2.0f * y;
		vec.y = y * 2.0f * z + w * 2.0f * x;
		vec.z = 1.0f - 2.0f * x - 2.0f * y;
		if (Math.abs(vec.x) > 0.0001 || Math.abs(vec.y) > 0.0001 || Math.abs(vec.z) > 0.0001) {
			vec.normalize();
		} else {
			vec.x = 0;
			vec.y = 0;
			vec.z = 1;
		}
	}
	
	public void extractRightVector(Vector3f vec) {
		vec.x = 1.0f - 2.0f * y - 2.0f * z;
		vec.y = x * 2.0f * y - w * 2.0f * z;
		vec.z = x * 2.0f * z + w * 2.0f * y;
		if (Math.abs(vec.x) > 0.0001 || Math.abs(vec.y) > 0.0001 || Math.abs(vec.z) > 0.0001) {
			vec.normalize();
		} else {
			vec.x = 1;
			vec.y = 0;
			vec.z = 0;
		}
	}
	
	public void extractUpVector(Vector3f vec) {
		vec.x = x * 2.0f * y + w * 2.0f * z;
		vec.y = 1.0f - 2.0f * x - 2.0f * z;
		vec.z = y * 2.0f * z - w * 2.0f * x;
		if (Math.abs(vec.x) > 0.0001 || Math.abs(vec.y) > 0.0001 || Math.abs(vec.z) > 0.0001) {
			vec.normalize();
		} else {
			vec.x = 0;
			vec.y = 1;
			vec.z = 0;
		}
	}
	
	private static void fromAxes(float xx, float xy, float xz, float yx,
			float yy, float yz, float zx, float zy, float zz, Quaternion r) {
		final float m00 = xx, m01 = xy, m02 = xz;
		final float m10 = yx, m11 = yy, m12 = yz;
		final float m20 = zx, m21 = zy, m22 = zz;
		final float t = m00 + m11 + m22;

		if (t >= 0) {
			float s = (float) Math.sqrt(t + 1);
			r.w = 0.5f * s;
			s = 0.5f / s;
			r.x = (m21 - m12) * s;
			r.y = (m02 - m20) * s;
			r.z = (m10 - m01) * s;
		} else if ((m00 > m11) && (m00 > m22)) {
			float s = (float) Math.sqrt(1.0 + m00 - m11 - m22);
			r.x = s * 0.5f;
			s = 0.5f / s;
			r.y = (m10 + m01) * s;
			r.z = (m02 + m20) * s;
			r.w = (m21 - m12) * s;
		} else if (m11 > m22) {
			float s = (float) Math.sqrt(1.0 + m11 - m00 - m22);
			r.y = s * 0.5f;
			s = 0.5f / s;
			r.x = (m10 + m01) * s;
			r.z = (m21 + m12) * s;
			r.w = (m02 - m20) * s;
		} else {
			float s = (float) Math.sqrt(1.0f + m22 - m00 - m11);
			r.z = s * 0.5f;
			s = 0.5f / s;
			r.x = (m02 + m20) * s;
			r.y = (m21 + m12) * s;
			r.w = (m10 - m01) * s;
		}
	}
	
	public static void fromMatrix(float [] matrix, Quaternion r) {
		fromAxes(matrix[ 0], matrix[ 4], matrix[ 8],
				 matrix[ 1], matrix[ 5], matrix[ 9],
				 matrix[ 2], matrix[ 6], matrix[10], r);
	}
		
	public static void fromVectors(Vector3f right, Vector3f up, Vector3f forward, Quaternion r) {
		fromAxes(right.x, up.x, forward.x,
				 right.y, up.y, forward.y,
				 right.z, up.z, forward.z, r);				
	}
	
	public static void lerp(Quaternion q1, Quaternion q2, Quaternion r, float alpha) {
		r.x = q1.x + alpha * (q2.x - q1.x);
		r.y = q1.y + alpha * (q2.y - q1.y);
		r.z = q1.z + alpha * (q2.z - q1.z);
		r.w = q1.w + alpha * (q2.w - q1.w);
		r.normalize();
	}
	
	public static void add(Quaternion q1, Quaternion q2, Quaternion r) {
		r.x = q1.x + q2.x;
		r.y = q1.y + q2.y;
		r.z = q1.z + q2.z;
		r.w = q1.w + q2.w;
	}
	
	public static void slerp(Quaternion q1, Quaternion q2, Quaternion r, float alpha) {
		float dot = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w;
		if (dot > 0.9995f) {
			lerp(q1, q2, r, alpha);
			return;
		}
		if (dot < -1.0) { dot = -1.0f; }
		if (dot > 1.0)  { dot = 1.0f; }
		float theta_0 = (float) Math.acos(dot);
		float ctheta = (float) Math.cos(theta_0 * alpha);
		float stheta = (float) Math.sin(theta_0 * alpha);
		
		t.x = q2.x + (-q1.x * dot);
		t.y = q2.y + (-q1.y * dot);
		t.z = q2.z + (-q1.z * dot);
		t.w = q2.w + (-q1.w * dot);
		t.normalize();
		
		r.x = q1.x * ctheta + t.x * stheta;
		r.y = q1.y * ctheta + t.y * stheta;
		r.z = q1.z * ctheta + t.z * stheta;
		r.w = q1.w * ctheta + t.w * stheta;
	}
	
	public void toSubMatrix(float [] r) {
		float sqx = x * x;
		float sqy = y * y;
		float sqz = z * z;
		float sqw = w * w;
		
		float invs = 1 / (sqx + sqy + sqz + sqw);
		r[ 0] = ( sqx - sqy - sqz + sqw) * invs;
	    r[ 5] = (-sqx + sqy - sqz + sqw) * invs;
	    r[10] = (-sqx - sqy + sqz + sqw) * invs;
	    
	    float tmp1 = x * y;
	    float tmp2 = z * w;
	    r[ 1] = 2.0f * (tmp1 + tmp2) * invs;
	    r[ 4] = 2.0f * (tmp1 - tmp2) * invs;
	    
	    tmp1 = x * z;
	    tmp2 = y * w;
	    r[ 2] = 2.0f * (tmp1 - tmp2) * invs;
	    r[ 8] = 2.0f * (tmp1 + tmp2) * invs;
	    
	    tmp1 = y * z;
	    tmp2 = x * w;
	    r[ 6] = 2.0f * (tmp1 + tmp2) * invs;
	    r[ 9] = 2.0f * (tmp1 - tmp2) * invs;   
	}
	
	public void toMatrix(float [] r) {
		float xx = x * x;
		float xy = x * y;
		float xz = x * z;
		float xw = x * w;
		float yy = y * y;
		float yz = y * z;
		float yw = y * w;
		float zz = z * z;
		float zw = z * w;
		r[ 0] = 1 - 2 * (yy + zz);
		r[ 1] = 2 * (xy + zw);
		r[ 2] = 2 * (xz - yw);
		r[ 3] = 0;
		r[ 4] = 2 * (xy - zw);
		r[ 5] = 1 - 2 * (xx + zz);
		r[ 6] = 2 * (yz + xw);
		r[ 7] = 0;
		r[ 8] = 2 * (xz + yw);
		r[ 9] = 2 * (yz - xw);
		r[10] = 1 - 2 * (xx + yy);
		r[11] = 0;
		r[12] = 0;
		r[13] = 0;
		r[14] = 0;
		r[15] = 1;
	}
		
	public void axisOfRotation(Vector3f axis) {
		if (w > 1) {
			normalize();
		}
		float w2 = w * w;
		if (w2 > 1.0f) {
			w2 = 1.0f;
		}		
		float s = 1f - w2;
		if (s < TOLERANCE) {
			axis.x = 1;
			axis.y = 0;
			axis.z = 0;			
		} else {
			s = (float) Math.sqrt(s);
			axis.x = x / s;
			axis.y = y / s;
			axis.z = z / s;
		}
		axis.normalize();
	}
	
	public float angleOfRotation() {
		if (w > 1.0f || w < -1.0f) {
			normalize();
		}
		return (float) (2.0 * Math.acos(w));
	}
	
	public float getRoll() {
		float fTy  = 2.0f * y;
		float fTz  = 2.0f * z;
		float fTwz = fTz * w;
		float fTxy = fTy * x;
		float fTyy = fTy * y;
		float fTzz = fTz * z;

		return (float) Math.atan2(fTxy + fTwz, 1.0f - (fTyy + fTzz));
	}

	public float getPitch() {
		float fTx  = 2.0f * x;
		float fTz  = 2.0f * z;
		float fTwx = fTx * w;
		float fTxx = fTx * x;
		float fTyz = fTz * y;
		float fTzz = fTz * z;

		return (float) Math.atan2(fTyz + fTwx, 1.0f - (fTxx + fTzz));
	}
	
	public float getYaw() {
		float fTx  = 2.0f * x;
		float fTy  = 2.0f * y;
		float fTz  = 2.0f * z;
		float fTwy = fTy * w;
		float fTxx = fTx * x;
		float fTxz = fTz * x;
		float fTyy = fTy * y;

		return (float) Math.atan2(fTxz + fTwy, 1.0f - (fTxx + fTyy));
	}
	
	// Assumption v0 and v1 are unit vectors!
	public static Quaternion rotationArc(Vector3f v0, Vector3f v1) {
		v0.cross(v1, temp);
		float d = v0.dot(v1);
		float s = (float) Math.sqrt(1.0 + d * 2.0);
		return new Quaternion(temp.x / s, temp.y / s, temp.z / s, s / 2.0f);
	}
	
	public static float pitchFromRotationArc(Vector3f v0, Vector3f v1) {		
		v0.cross(v1, temp);
		float d = v0.dot(v1);
		if (d < -0.9999f) {
			xUnit.cross(v0, temp);
			if (temp.length() < 0.00001f) {
				yUnit.cross(v0, temp);
			}
			temp.normalize();
			t.x = temp.x;
			t.y = temp.y;
			t.z = temp.z;
			t.w = 0;
		} else if (d > 0.9999f) {
			t.x = 0;
			t.y = 0;
			t.z = 0;
			t.w = 1;
		} else {
			t.x = temp.x;
			t.y = temp.y;
			t.z = temp.z;		
			t.w = (float) Math.sqrt(1.0 + d);
		}
		t.normalize();
		return t.getPitch();
	}
	
	public static void fromAngleAxis(float angle, Vector3f axis, Quaternion r) {
		float hAngle = angle / 2.0f;
		float sin = (float) Math.sin(hAngle);
		r.w = (float) Math.cos(hAngle);
		r.x = sin * axis.x;
		r.y = sin * axis.y;
		r.z = sin * axis.z;
	}
	
	public void computeDifference(final Quaternion q, final Quaternion r) {
		// Multiply _the inverse_ (!) of this quaternion with q and store the result in r...
		// Thus, r is a quaternion which transforms this quaternion into q...
		r.x = w * q.x - x * q.w - y * q.z + z * q.y;
		r.y = w * q.y - y * q.w - z * q.x + x * q.z;
		r.z = w * q.z - z * q.w - x * q.y + y * q.x;
		r.w = w * q.w + x * q.x + y * q.y + z * q.z;
	}
		
	// Assumption v0 and v1 are unit vectors!
	public static void rotationArc(Vector3f v0, Vector3f v1, Vector3f up, Quaternion r) {
		float d = v0.dot(v1);
		if (d >= 1.0f) {
			r.x = 0;
			r.y = 0;
			r.z = 0;
			r.w = 1;
			return;
		}
		if (d < (1e-6f - 1.0f)) {
			fromAngleAxis(3.1415926535f, up, r);
			return;
		}
		float s = (float) Math.sqrt((1.0 + d) * 2.0);
		float invs = 1 / s;
		v0.cross(v1, temp);
		r.x = temp.x * invs;
		r.y = temp.y * invs;
		r.z = temp.z * invs;
		r.w = s * 0.5f;
		r.normalize();
	}
	
	public void normalize() {
		float lensq = x * x + y * y + z * z + w * w;
		if (lensq > TOLERANCE && (Math.abs(lensq - 1.0f) > TOLERANCE)) {
			lensq = (float) Math.sqrt(lensq);
			w /= lensq;
			x /= lensq;
			y /= lensq;
			z /= lensq;
		}
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + ", " + w + "]";
	}
}
