package de.phbouillon.android.framework.impl.gl;

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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.graphics.Rect;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;

public class GlUtils {	
	public static void setViewport(Rect rect) {
		GLES11.glViewport(rect.left, rect.top, rect.width(), rect.height());
	}

	public static void gluPerspective(Game game, float fovy, float aspect, float znear, float zfar) {
		float xmin, xmax, ymin, ymax;

	    ymax = (float) (znear * Math.tan(fovy * Math.PI / 360.0));
	    ymin = -ymax;
	    xmin = ymin * aspect;
	    xmax = ymax * aspect;

	    GLES11.glFrustumf(xmin, xmax, ymin, ymax, znear, zfar);
	}

	public static void ortho(Game game, Rect r) {
		GLES11.glOrthof(r.left, r.right, r.bottom, r.top, 0.0f, 1.0f);
	}
	
	public static FloatBuffer allocateFloatBuffer(int capacity) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(capacity);
		vbb.order(ByteOrder.nativeOrder());
		return vbb.asFloatBuffer();
	}

	public static FloatBuffer toFloatBufferPositionZero(float[] values) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(values.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = vbb.asFloatBuffer();
		buffer.put(values);
		buffer.position(0);
		return buffer;
	}
	
	public static ShortBuffer toShortBufferPositionZero(short [] values) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(values.length * 2);
		vbb.order(ByteOrder.nativeOrder());
		ShortBuffer buffer = vbb.asShortBuffer();
		buffer.put(values);
		buffer.position(0);
		return buffer;
	}	
}
