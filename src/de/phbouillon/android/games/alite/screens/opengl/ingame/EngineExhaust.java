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
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.MathHelper;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class EngineExhaust extends GraphicObject implements Serializable {	
	private static final float sqrt1_2 = (float) Math.sqrt(0.5);
	private static final float [] sin = new float [] {0, sqrt1_2, 1, sqrt1_2, 0, -sqrt1_2, -1, -sqrt1_2};
	private static final float [] cos = new float [] {1, sqrt1_2, 0, -sqrt1_2, -1, -sqrt1_2, 0, sqrt1_2};
	private final SpaceObject so;
	private float [][] emission = new float[2][4];
	private float [] saveMatrix = new float[16];
	private transient FloatBuffer diskBuffer;
	private transient FloatBuffer cylinderBuffer;
	private transient FloatBuffer colorBuffer1;
	private transient FloatBuffer colorBuffer2;
	private float exhaustRadiusX;
	private float exhaustRadiusY;
	private float maxLen;
	private boolean depthTest = true;
	private float r, g, b, a;
	
	public EngineExhaust(SpaceObject ref, float radiusX, float radiusY, float maxLen, float x, float y, float z) {
		this(ref, radiusX, radiusY, maxLen, x, y, z, 0.7f, 0.8f, 0.8f, 0.7f);
	}
	
	public EngineExhaust(SpaceObject ref, float radiusX, float radiusY, float maxLen, float x, float y, float z, float r, float g, float b, float a) {
		this.so = ref;
		setPosition(x, y, ref.getBoundingBox()[5] + z);
		exhaustRadiusX = radiusX;
		exhaustRadiusY = radiusY;
		this.maxLen    = maxLen;
		diskBuffer     = GlUtils.allocateFloatBuffer(4 * 3 * 10);
		cylinderBuffer = GlUtils.allocateFloatBuffer(4 * 3 * 18);
		colorBuffer1   = GlUtils.allocateFloatBuffer(4 * 4 * 10);
		colorBuffer2   = GlUtils.allocateFloatBuffer(4 * 4 * 18);
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public float getMaxLen() {
		return maxLen;
	}
	
	public float getRadiusX() {
		return exhaustRadiusX;
	}
	
	public void setRadiusX(float radiusX) {
		this.exhaustRadiusX = radiusX;
	}
	
	public float getRadiusY() {
		return exhaustRadiusY;
	}
	
	public void setRadiusY(float radiusY) {
		this.exhaustRadiusY = radiusY;
	}

	public float getMaxLength() {
		return maxLen;
	}
	
	public float getR() {
		return r;
	}
	
	public float getG() {
		return g;
	}
	
	public float getB() {
		return b;
	}
	
	public float getA() {
		return a;
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void setMaxLength(float newMax) {
		maxLen = newMax;
	}
	
	public void setDepthTest(boolean b) {
		depthTest = b;
	}
	
	public boolean getDepthTest() {
		return depthTest;
	}
	
	public void update() {		
		float factor = -so.getSpeed() / so.getMaxSpeed();
		
		emission[0][0] = r - factor * 0.2f;
		emission[0][1] = g; 
		emission[0][2] = b + factor * 0.2f;
		emission[0][3] = a + factor * 0.3f;
		emission[1][0] = (0.6f * (r - factor * 0.2f)) * (1.0f - (float) Math.random() * 0.1f);
		emission[1][1] = (0.6f * g) * (1.0f - (float) Math.random() * 0.1f);
		emission[1][2] = (b + factor * 0.2f) * (1.0f - (float) Math.random() * 0.1f);
		emission[1][3] = (factor * 0.3f) * (1.0f - (float) Math.random() * 0.1f);

		diskBuffer.clear();
		cylinderBuffer.clear();
		colorBuffer1.clear();
		colorBuffer2.clear();
		plotDiskPoints(exhaustRadiusX, exhaustRadiusY, 0, 0, 1);
		plotCylinderPoints(exhaustRadiusX, exhaustRadiusY, exhaustRadiusX * 0.3f, exhaustRadiusY * 0.3f, maxLen * factor, 0, 0, 1);
	}
	
	 private void plotDiskPoints(float rx, float ry, float x, float y, float z) {
		 diskBuffer.put(x);
		 diskBuffer.put(y);
		 diskBuffer.put(z);
		 colorBuffer1.put(emission[0][0]);
		 colorBuffer1.put(emission[0][1]);
		 colorBuffer1.put(emission[0][2]);
		 colorBuffer1.put(emission[0][3]);
		 for (int i = 0; i < 8; i++) {
			 diskBuffer.put(x + sin[i] * rx);
			 diskBuffer.put(y + cos[i] * ry);
			 diskBuffer.put(z);	
			 colorBuffer1.put(emission[0][0]);
			 colorBuffer1.put(emission[0][1]);
			 colorBuffer1.put(emission[0][2]);
			 colorBuffer1.put(emission[0][3]);
		 }
		 diskBuffer.put(x);
		 diskBuffer.put(y + ry);
		 diskBuffer.put(z);			 
		 colorBuffer1.put(emission[0][0]);
		 colorBuffer1.put(emission[0][1]);
		 colorBuffer1.put(emission[0][2]);
		 colorBuffer1.put(emission[0][3]);
		 diskBuffer.position(0);
		 colorBuffer1.position(0);
	}

	private void plotCylinderPoints(float r1x, float r1y, float r2x, float r2y, float len, float x, float y, float z) {		
		float first = 0;
		for (int i = 0; i < 8; i++) {
			cylinderBuffer.put(x + sin[i] * r1x);
			cylinderBuffer.put(y + cos[i] * r1y);
			cylinderBuffer.put(z);
			colorBuffer2.put(emission[0][0]);
			colorBuffer2.put(emission[0][1]);
			colorBuffer2.put(emission[0][2]);
			colorBuffer2.put(emission[0][3]);
			cylinderBuffer.put(x + sin[i] * r2x);
			cylinderBuffer.put(y + cos[i] * r2y);
			float val = z + len + (float) Math.random() * len * 0.1f;
			if (i == 0) {
				first = val;
			}
			cylinderBuffer.put(val);	
			colorBuffer2.put(emission[1][0]);
			colorBuffer2.put(emission[1][1]);
			colorBuffer2.put(emission[1][2]);
			colorBuffer2.put(emission[1][3]);
		}
		cylinderBuffer.put(x + sin[0] * r1x);
		cylinderBuffer.put(y + cos[0] * r1y);
		cylinderBuffer.put(z);
		colorBuffer2.put(emission[0][0]);
		colorBuffer2.put(emission[0][1]);
		colorBuffer2.put(emission[0][2]);
		colorBuffer2.put(emission[0][3]);		
		cylinderBuffer.put(x + sin[0] * r2x);
		cylinderBuffer.put(y + cos[0] * r2y);
		cylinderBuffer.put(first);			
		colorBuffer2.put(emission[1][0]);
		colorBuffer2.put(emission[1][1]);
		colorBuffer2.put(emission[1][2]);
		colorBuffer2.put(emission[1][3]);
		cylinderBuffer.position(0);
		colorBuffer2.position(0);
	}
	
	public void render() {
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glDepthMask(false);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE);		
		MathHelper.copyMatrix(getMatrix(), saveMatrix);
		for (int i = 0; i < 2; i++) {
			GLES11.glPushMatrix();
			GLES11.glMultMatrixf(getMatrix(), 0);
		
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, diskBuffer);
			GLES11.glColorPointer(4, GLES11.GL_FLOAT, 0, colorBuffer1);
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, 10);
		
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, cylinderBuffer);
			GLES11.glColorPointer(4, GLES11.GL_FLOAT, 0, colorBuffer2);
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 18);
		
			GLES11.glPopMatrix();
			scale(0.4f, 0.4f, 1.2f);
		}
		MathHelper.copyMatrix(saveMatrix, currentMatrix);
		extractVectors();
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glDepthMask(true);
		GLES11.glDisable(GLES11.GL_BLEND);
	}
}
