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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.screens.opengl.ingame.LaserManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.MathHelper;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class LaserCylinder extends AliteObject implements Serializable {	
	private static final float sqrt1_2 = (float) Math.sqrt(0.5);
	private static final float [] sin = new float [] {0, sqrt1_2, 1, sqrt1_2, 0, -sqrt1_2, -1, -sqrt1_2};
	private static final float [] cos = new float [] {1, sqrt1_2, 0, -sqrt1_2, -1, -sqrt1_2, 0, sqrt1_2};
	private static final float radius = 8.0f;
	
	private Laser laser;
	private final List<LaserCylinder> twins = new ArrayList<LaserCylinder>();
	private boolean aiming = false;
	private SpaceObject origin;
	private transient FloatBuffer diskBuffer1;
	private transient FloatBuffer diskBuffer2;
	private transient FloatBuffer cylinderBuffer;
	private transient FloatBuffer [] normalBuffer;
	private transient FloatBuffer [] texCoordBuffer;
	private float [] emission = new float[4];
	private boolean visible = true;
	private final float [] saveMatrix = new float[16];
	private int removeInNFrames = -1;
	private String textureFilename = null;

	private float halfLength = 75.0f;
	private boolean beam = false;
	
	public LaserCylinder() {
		super("Laser");
		
		diskBuffer1       = GlUtils.allocateFloatBuffer(4 * 3 * 10);
		cylinderBuffer    = GlUtils.allocateFloatBuffer(4 * 3 * 18);
		diskBuffer2       = GlUtils.allocateFloatBuffer(4 * 3 * 10);
		normalBuffer      = new FloatBuffer[3];
		normalBuffer[0]   = GlUtils.allocateFloatBuffer(4 * 3 * 10);
		normalBuffer[1]   = GlUtils.allocateFloatBuffer(4 * 3 * 18);
		normalBuffer[2]   = GlUtils.allocateFloatBuffer(4 * 3 * 10);
		texCoordBuffer    = new FloatBuffer[3];
		texCoordBuffer[0] = GlUtils.allocateFloatBuffer(4 * 2 * 10);
		texCoordBuffer[1] = GlUtils.allocateFloatBuffer(4 * 2 * 18);
		texCoordBuffer[2] = GlUtils.allocateFloatBuffer(4 * 2 * 10);
		
		plotDiskPoints(diskBuffer1, normalBuffer[0], texCoordBuffer[0], radius, radius, 0, 0, -halfLength, false);
		plotCylinderPoints(radius, radius, radius, radius, halfLength * 2.0f, 0, 0, -halfLength);
		plotDiskPoints(diskBuffer2, normalBuffer[2], texCoordBuffer[2], radius, radius, 0, 0, halfLength, true);
		boundingSphereRadius = halfLength;
		setZPositioningMode(ZPositioning.Front);
		setSpeed(-LaserManager.LASER_SPEED);
	}
			
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "LaserCylinder.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "LaserCylinder.readObject I");			
			diskBuffer1    = GlUtils.allocateFloatBuffer(4 * 3 * 10);
			cylinderBuffer = GlUtils.allocateFloatBuffer(4 * 3 * 18);
			diskBuffer2    = GlUtils.allocateFloatBuffer(4 * 3 * 10);
			normalBuffer    = new FloatBuffer[3];
			normalBuffer[0] = GlUtils.allocateFloatBuffer(4 * 3 * 10);
			normalBuffer[1] = GlUtils.allocateFloatBuffer(4 * 3 * 18);
			normalBuffer[2] = GlUtils.allocateFloatBuffer(4 * 3 * 10);		
			texCoordBuffer    = new FloatBuffer[3];
			texCoordBuffer[0] = GlUtils.allocateFloatBuffer(4 * 2 * 10);
			texCoordBuffer[1] = GlUtils.allocateFloatBuffer(4 * 2 * 18);
			texCoordBuffer[2] = GlUtils.allocateFloatBuffer(4 * 2 * 10);
			plotDiskPoints(diskBuffer1, normalBuffer[0], texCoordBuffer[0], radius, radius, 0, 0, -halfLength, false);
			plotCylinderPoints(radius, radius, radius, radius, halfLength * 2, 0, 0, -halfLength);
			plotDiskPoints(diskBuffer2, normalBuffer[2], texCoordBuffer[2], radius, radius, 0, 0, halfLength, true);
			AliteLog.e("readObject", "LaserCylinder.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}		

	public void render(Alite alite) {
		if (!visible) {
			return;
		}
		GLES11.glDepthFunc(GLES11.GL_LEQUAL);
		GLES11.glDepthMask(false);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		if (textureFilename != null) {
			GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glEnable(GLES11.GL_LIGHTING);
			alite.getTextureManager().setTexture(textureFilename);
		} else {
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glDisable(GLES11.GL_LIGHTING);
			alite.getTextureManager().setTexture(null);
		}			
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
		GLES11.glColor4f(emission[0], emission[1], emission[2], emission[3]);
		for (int i = 0; i < 2; i++) {
			GLES11.glPushMatrix();
			GLES11.glMultMatrixf(getMatrix(), 0);
		
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, diskBuffer1);
			GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer[0]);
			if (textureFilename != null) {
				GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer[0]);
			}
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, 10);
					
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, cylinderBuffer);
			GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer[1]);
			if (textureFilename != null) {
				GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer[1]);
			}
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 18);
		
			GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, diskBuffer2);
			GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer[2]);
			if (textureFilename != null) {
				GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer[2]);
			}
			GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, 10);

			GLES11.glPopMatrix();
			MathHelper.copyMatrix(getMatrix(), saveMatrix);
			scale(0.7f, 0.7f, 0.7f);			
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
		}
		MathHelper.copyMatrix(saveMatrix, currentMatrix);
		extractVectors();
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		alite.getTextureManager().setTexture(null);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glDepthMask(true);
		GLES11.glDisable(GLES11.GL_BLEND);
	}
	
	public void setBeam(boolean beam) {
		if (this.beam == beam) {
			return;
		}
		this.beam = beam;
		halfLength = beam ? 7500.0f : 75.0f;
		setSpeed(-(beam ? LaserManager.LASER_BEAM_SPEED : LaserManager.LASER_SPEED));
		diskBuffer1.clear();
		cylinderBuffer.clear();
		diskBuffer2.clear();
		normalBuffer[0].clear();
		normalBuffer[1].clear();
		normalBuffer[2].clear();
		texCoordBuffer[0].clear();
		texCoordBuffer[1].clear();
		texCoordBuffer[2].clear();
		plotDiskPoints(diskBuffer1, normalBuffer[0], texCoordBuffer[0], radius, radius, 0, 0, -halfLength, false);
		plotCylinderPoints(radius, radius, radius, radius, halfLength * 2, 0, 0, -halfLength);
		plotDiskPoints(diskBuffer2, normalBuffer[2], texCoordBuffer[1], radius, radius, 0, 0, halfLength, true);		
		boundingSphereRadius = halfLength;
	}
	
	public void setVisible(boolean b) {
		visible = b;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setColor(long color) {
		setColor((int) (color & 0x00FF0000) >> 16, 
				 (int) (color & 0x0000FF00) >>  8,
				 (int) (color & 0x000000FF),
				 (int) ((color & 0xFF000000l) >> 24));
	}
	
	public void setColor(float r, float g, float b, float a) {
		emission[0] = r;
		emission[1] = g;
		emission[2] = b;
		emission[3] = a;
	}
		
	public void setTexture(String texture, Alite alite) {
		this.textureFilename = texture;
		alite.getTextureManager().addTexture(texture);
	}
	
	protected void plotDiskPoints(FloatBuffer diskBuffer, FloatBuffer normalBuffer, FloatBuffer texCoordBuffer, float rx, float ry, float x, float y, float z, boolean back) {
		 diskBuffer.put(x);
		 diskBuffer.put(y);
		 diskBuffer.put(z);
		 normalBuffer.put(0);
		 normalBuffer.put(0);
		 normalBuffer.put(1);
		 texCoordBuffer.put(0.5f);
		 texCoordBuffer.put(0.5f);
		 for (int i = 0; i < 8; i++) {
			 diskBuffer.put(x + sin[i] * rx);
			 diskBuffer.put(y + cos[i] * ry);
			 diskBuffer.put(z);
			 normalBuffer.put(0);
			 normalBuffer.put(0);
			 normalBuffer.put(1);
			 texCoordBuffer.put(0.5f);
			 texCoordBuffer.put(0.5f + cos[i] * 0.125f);
		 }
		 diskBuffer.put(x);
		 diskBuffer.put(y + ry);
		 diskBuffer.put(z);
		 normalBuffer.put(0);
		 normalBuffer.put(0);
		 normalBuffer.put(1);
		 texCoordBuffer.put(0.5f);
		 texCoordBuffer.put(0.625f);
		 diskBuffer.position(0);
		 normalBuffer.position(0);
		 texCoordBuffer.position(0);
	}

	protected void plotCylinderPoints(float r1x, float r1y, float r2x, float r2y, float len, float x, float y, float z) {		
		for (int i = 0; i < 8; i++) {
			cylinderBuffer.put(x + sin[i] * r1x);
			cylinderBuffer.put(y + cos[i] * r1y);
			cylinderBuffer.put(z);
			normalBuffer[1].put(sin[i]);
			normalBuffer[1].put(cos[i]);
			normalBuffer[1].put(0);
			texCoordBuffer[1].put(0);
			texCoordBuffer[1].put(0.375f + i * 0.03125f);
			cylinderBuffer.put(x + sin[i] * r2x);
			cylinderBuffer.put(y + cos[i] * r2y);
			cylinderBuffer.put(z + len);
			normalBuffer[1].put(sin[i]);
			normalBuffer[1].put(cos[i]);
			normalBuffer[1].put(0);
			texCoordBuffer[1].put(1);
			texCoordBuffer[1].put(0.375f + i * 0.03125f);			

		}
		cylinderBuffer.put(x + sin[0] * r1x);
		cylinderBuffer.put(y + cos[0] * r1y);
		cylinderBuffer.put(z);
		normalBuffer[1].put(sin[0]);
		normalBuffer[1].put(cos[0]);
		normalBuffer[1].put(0);
		texCoordBuffer[1].put(0);
		texCoordBuffer[1].put(0.625f);		
		cylinderBuffer.put(x + sin[0] * r2x);
		cylinderBuffer.put(y + cos[0] * r2y);
		cylinderBuffer.put(z + len);			
		normalBuffer[1].put(sin[0]);
		normalBuffer[1].put(cos[0]);
		normalBuffer[1].put(0);
		texCoordBuffer[1].put(1);
		texCoordBuffer[1].put(0.625f);
		cylinderBuffer.position(0);
		normalBuffer[1].position(0);
		texCoordBuffer[1].position(0);
	}
	
	public Laser getLaser() {
		return laser;
	}
	
	public void setLaser(Laser laser) {
		this.laser = laser;
	}
	
	public List <LaserCylinder> getTwins() {
		return twins;
	}
	
	public void setTwins(LaserCylinder twin1, LaserCylinder twin2) {
		twins.clear();
		twins.add(twin1);
		twins.add(twin2);
	}
	
	public void addTwin(LaserCylinder twin) {
		twins.add(twin);
	}
	
	public void clearTwins() {
		twins.clear();
	}
	
	public boolean isAiming() {
		return aiming;
	}
	
	public void setAiming(boolean aiming) {
		this.aiming = aiming;
	}
	
	public void setOrigin(SpaceObject origin) {
		this.origin = origin;
	}
	
	public SpaceObject getOrigin() {
		return origin;
	}
	
	@Override
	public boolean isVisibleOnHud() {
		return false;
	}

	@Override
	public Vector3f getHudColor() {
		return null;
	}

	public void removeInNFrames(int n) {
		removeInNFrames = n;
	}
	
	public void reset() {
		removeInNFrames = -1;
		textureFilename = null;
	}
	
	public int getRemoveInNFrames() {
		return removeInNFrames;
	}
	
	public void postRender() {
		if (removeInNFrames >= 0) {
			removeInNFrames--;
			if (removeInNFrames == -1) {
				for (LaserCylinder lc: getTwins()) {
					lc.setVisible(false);
					lc.clearTwins();
				}
				setVisible(false);							
				clearTwins();
			}
		}		
	}	
}
