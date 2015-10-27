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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;

import android.opengl.GLES11;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public class Cylinder implements Serializable {
	private static final float [] sin = new float [] { 0.000000f,  0.195090f,  0.382683f,  0.555570f,  0.707107f,
		                                                           0.831470f,  0.923880f,  0.980785f,  1.000000f,
		                                                           0.980785f,  0.923880f,  0.831470f,  0.707107f,
		                                                           0.555570f,  0.382683f,  0.195090f,  0.000000f, 
		                                                          -0.195090f, -0.382683f, -0.555570f, -0.707107f, 
		                                                          -0.831470f, -0.923880f, -0.980785f, -1.000000f, 
		                                                          -0.980785f, -0.923880f, -0.831470f, -0.707107f, 
		                                                          -0.555570f, -0.382683f, -0.195090f};
	private static final float [] cos = new float [] { 1.000000f,  0.980785f,  0.923880f,  0.831470f,  0.707107f, 
		                                                           0.555570f,  0.382683f,  0.195090f,  0.000000f, 
		                                                          -0.195090f, -0.382683f, -0.555570f, -0.707107f,
		                                                          -0.831470f, -0.923880f, -0.980785f, -1.000000f,
		                                                          -0.980785f, -0.923880f, -0.831470f, -0.707107f,
		                                                          -0.555570f, -0.382683f, -0.195090f,  0.000000f, 
		                                                           0.195090f,  0.382683f,  0.555570f,  0.707107f, 
		                                                           0.831470f,  0.923880f,  0.980785f, };

	private transient FloatBuffer diskBuffer1;
	private transient FloatBuffer diskBuffer2;
	private transient FloatBuffer cylinderBuffer;
	private transient FloatBuffer [] normalBuffer;
	private transient FloatBuffer texCoordBuffer;
	private final String textureFilename;
	private float radius;
	private boolean hasTop;
	private boolean hasBottom;
	private float length;
	private transient Alite alite;
	private float r = 1.0f;
	private float g = 1.0f;
	private float b = 1.0f;
	private float a = 1.0f;
	private int segments;
	
	public Cylinder(final Alite alite, final float length, final float radius, int segments, final boolean hasTop, final boolean hasBottom, final String textureFilename) {
		float halfLength = length / 2.0f;
	
		if (segments <= 8) {
			segments = 8;
		} else if (segments <= 16) {
			segments = 16;
		} else {
			segments = 32;
		}
		this.alite        = alite;
		this.hasTop       = hasTop;
		this.hasBottom    = hasBottom;
		this.radius       = radius;
		this.length       = length;
		this.segments     = segments;
		diskBuffer1       = hasTop ? GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2)) : null;
		cylinderBuffer    = GlUtils.allocateFloatBuffer(4 * 3 * (segments * 2 + 2));
		diskBuffer2       = hasBottom ? GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2)) : null;
		normalBuffer      = new FloatBuffer[3];
		normalBuffer[0]   = GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2));
		normalBuffer[1]   = GlUtils.allocateFloatBuffer(4 * 3 * (segments * 2 + 2));
		normalBuffer[2]   = GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2));
		texCoordBuffer    = GlUtils.allocateFloatBuffer(4 * 2 * (segments * 2 + 2));

		this.textureFilename = textureFilename;
		
		if (hasTop) {
			plotDiskPoints(diskBuffer1, normalBuffer[0], radius, radius, 0, 0, -halfLength, false);
		}
		plotCylinderPoints(radius, radius, radius, radius, length, 0, 0, -halfLength);
		if (hasBottom) {
			plotDiskPoints(diskBuffer2, normalBuffer[2], radius, radius, 0, 0, halfLength, true);
		}
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "Cylinder.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "Cylinder.readObject II");
			this.alite        = Alite.get();
			diskBuffer1       = hasTop ? GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2)) : null;
			cylinderBuffer    = GlUtils.allocateFloatBuffer(4 * 3 * (segments * 2 + 2));
			diskBuffer2       = hasBottom ? GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2)) : null;
			normalBuffer      = new FloatBuffer[3];
			normalBuffer[0]   = GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2));
			normalBuffer[1]   = GlUtils.allocateFloatBuffer(4 * 3 * (segments * 2 + 2));
			normalBuffer[2]   = GlUtils.allocateFloatBuffer(4 * 3 * (segments + 2));
			texCoordBuffer    = GlUtils.allocateFloatBuffer(4 * 2 * (segments * 2 + 2));
			float halfLength  = length / 2.0f;
			if (hasTop) {
				plotDiskPoints(diskBuffer1, normalBuffer[0], radius, radius, 0, 0, -halfLength, false);
			}
			plotCylinderPoints(radius, radius, radius, radius, length, 0, 0, -halfLength);
			if (hasBottom) {
				plotDiskPoints(diskBuffer2, normalBuffer[2], radius, radius, 0, 0, halfLength, true);
			}
			if (textureFilename != null) {
				alite.getTextureManager().addTexture(textureFilename);
			}
			AliteLog.e("readObject", "Cylinder.readObject III");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Cylinder", e);
			throw(e);
		}
    }

	protected void plotDiskPoints(FloatBuffer diskBuffer, FloatBuffer normalBuffer, float rx, float ry, float x, float y, float z, boolean back) {
		 diskBuffer.put(x);
		 diskBuffer.put(y);
		 diskBuffer.put(z);
		 normalBuffer.put(0);
		 normalBuffer.put(0);
		 normalBuffer.put(1);		 
		 int step = segments == 32 ? 1 : segments == 16 ? 2 : 4;
		 for (int i = 0; i < 32; i += step) {
			 diskBuffer.put(x + sin[i] * rx);
			 diskBuffer.put(y + cos[i] * ry);
			 diskBuffer.put(z);
			 normalBuffer.put(0);
			 normalBuffer.put(0);
			 normalBuffer.put(1);
		 }
		 diskBuffer.put(x);
		 diskBuffer.put(y + ry);
		 diskBuffer.put(z);
		 normalBuffer.put(0);
		 normalBuffer.put(0);
		 normalBuffer.put(1);
		 diskBuffer.position(0);
		 normalBuffer.position(0);
	}

	protected void plotCylinderPoints(float r1x, float r1y, float r2x, float r2y, float len, float x, float y, float z) {		
		int step = segments == 32 ? 1 : segments == 16 ? 2 : 4;
		for (int i = 0; i < 32; i += step) {
			cylinderBuffer.put(x + sin[i] * r1x);
			cylinderBuffer.put(y + cos[i] * r1y);
			cylinderBuffer.put(z);
			normalBuffer[1].put(sin[i]);
			normalBuffer[1].put(cos[i]);
			normalBuffer[1].put(0);
			texCoordBuffer.put(i * 0.125f);
			texCoordBuffer.put(0);
			cylinderBuffer.put(x + sin[i] * r2x);
			cylinderBuffer.put(y + cos[i] * r2y);
			cylinderBuffer.put(z + len);
			normalBuffer[1].put(sin[i]);
			normalBuffer[1].put(cos[i]);
			normalBuffer[1].put(0);
			texCoordBuffer.put(i * 0.125f);
			texCoordBuffer.put(1);
		}
		cylinderBuffer.put(x + sin[0] * r1x);
		cylinderBuffer.put(y + cos[0] * r1y);
		cylinderBuffer.put(z);
		normalBuffer[1].put(sin[0]);
		normalBuffer[1].put(cos[0]);
		normalBuffer[1].put(0);
		texCoordBuffer.put(1);
		texCoordBuffer.put(0);
		cylinderBuffer.put(x + sin[0] * r2x);
		cylinderBuffer.put(y + cos[0] * r2y);
		cylinderBuffer.put(z + len);			
		normalBuffer[1].put(sin[0]);
		normalBuffer[1].put(cos[0]);
		normalBuffer[1].put(0);
		texCoordBuffer.put(1);
		texCoordBuffer.put(1);		
		cylinderBuffer.position(0);
		normalBuffer[1].position(0);
		texCoordBuffer.position(0);
	}
	
	public void render() {
		GLES11.glDisable(GLES11.GL_CULL_FACE);

		if (hasTop) {
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
			GLES11.glEnable(GLES11.GL_BLEND);
			GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);		
			GLES11.glColor4f(r, g, b, a);
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, diskBuffer1);
			GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer[0]);		
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, segments + 2);
		}
						
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer[1]);		
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, cylinderBuffer);		
		if (textureFilename != null) {
			GLES11.glEnable(GLES11.GL_LIGHTING);
			GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
			alite.getTextureManager().setTexture(textureFilename);
		} else {
			GLES11.glDisable(GLES11.GL_LIGHTING);
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);			
		}
        GLES11.glTexEnvf(GLES11.GL_TEXTURE_ENV, GLES11.GL_TEXTURE_ENV_MODE, GLES11.GL_MODULATE);        
	    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
        GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glColor4f(r, g, b, a);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, segments * 2 + 2);
		
		if (hasBottom) {
			GLES11.glDisable(GLES11.GL_LIGHTING);
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
			GLES11.glEnable(GLES11.GL_BLEND);
			GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);		
			GLES11.glColor4f(r, g, b, a);
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, diskBuffer2);
			GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer[2]);		
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, segments + 2);
		}
		
		GLES11.glColor4f(1, 1, 1, 1);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnable(GLES11.GL_LIGHTING);
	}

}
