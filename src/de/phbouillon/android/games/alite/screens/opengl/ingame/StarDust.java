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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;

public class StarDust extends GraphicObject implements Serializable {
	private static final long serialVersionUID = -263791298507488418L;
	private static final long  SIZE      = 700000;
	private static final float SIZE_MUL  = 10.0f / (float) SIZE;	
	private static final float SPEED     = 1000.0f;
	
	private transient FloatBuffer dustParticles;
	private transient FloatBuffer particleSizes;
	private final int particleCount;
	private final float [] particles;
	private final float [] sizes;
	private transient Alite alite;
	
	StarDust(final Alite alite, Vector3f centerPosition) {
		super("Stardust");
		particleCount = Settings.particleDensity == 1 ? 500 : Settings.particleDensity == 2 ? 2000 : 4000;
		particles = new float[particleCount * 3];
		sizes = new float[particleCount];
		this.alite = alite;
		setPosition(centerPosition);
		for (int i = 0; i < particleCount; i++) {
			particles[i * 3 + 0] = (float) ((Math.random() * SIZE * 2) - SIZE);
			particles[i * 3 + 1] = (float) ((Math.random() * SIZE * 2) - SIZE);
			particles[i * 3 + 2] = (float) ((Math.random() * SIZE * 2) - SIZE);
			computeSize(i);
		}
		init();
	}
	
	private void init() {
		dustParticles = GlUtils.toFloatBufferPositionZero(particles);
		particleSizes = GlUtils.toFloatBufferPositionZero(sizes);
        float fogColor[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        GLES11.glFogfv(GLES11.GL_FOG_COLOR, fogColor, 0);
        GLES11.glFogf(GLES11.GL_FOG_START, SIZE * 0.5f);
        GLES11.glFogf(GLES11.GL_FOG_END, SIZE);
        GLES11.glFogx(GLES11.GL_FOG_MODE, GLES11.GL_LINEAR);		
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "StarDust.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "StarDust.readObject I");
			this.alite     = Alite.get();
			init();
			AliteLog.e("readObject", "StarDust.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	private void computeSize(int index) {		
		float distSq = particles[index * 3 + 0] * SIZE_MUL *
	            	   particles[index * 3 + 0] * SIZE_MUL +
	            	   particles[index * 3 + 1] * SIZE_MUL *
	            	   particles[index * 3 + 1] * SIZE_MUL +
	            	   particles[index * 3 + 2] * SIZE_MUL *
	            	   particles[index * 3 + 2] * SIZE_MUL;
		
		sizes[index] = 8.0f * AndroidGame.scaleFactor - distSq / 375.0f; 
											   // 375 == 3000 / 8, with 3000 being the estimated
		                     // "drop off" point; i.e. everything farther away
			                   // than that is too small to see anyway...
		
		if (sizes[index] < 0.01f) {			
			sizes[index] = 0.01f;
		}		
	}
	
	void render() {		
        GLES11.glEnable(GLES11.GL_FOG);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
        GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_POINT_SIZE_ARRAY_OES);		
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, dustParticles);
		GLES11.glPointSizePointerOES(GLES11.GL_FLOAT, 0, particleSizes);
		GLES11.glColor4f(0.7f, 0.7f, 1.0f, 0.8f);
		alite.getTextureManager().setTexture("textures/glow_mask.png");
        GLES11.glEnable(GLES11.GL_POINT_SPRITE_OES);
        GLES11.glTexEnvf(GLES11.GL_POINT_SPRITE_OES, GLES11.GL_COORD_REPLACE_OES, GLES11.GL_TRUE);
        GLES11.glEnable(GLES11.GL_BLEND);
        GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
        GLES11.glHint(GLES11.GL_POINT_SMOOTH_HINT, GLES11.GL_NICEST);
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);
		GLES11.glDrawArrays(GLES11.GL_POINTS, 0, particleCount);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glPointSize(1.0f);
		GLES11.glDisableClientState(GLES11.GL_POINT_SIZE_ARRAY_OES);
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glDisable(GLES11.GL_FOG);
	}

	private void adjustPosition(int index) {
		for (int i = 0; i < 3; i++) {
			if (particles[index * 3 + i] > SIZE) {
				particles[index * 3 + i] = -SIZE + (particles[index * 3 + i] - SIZE);
			} else if (particles[index * 3 + i] < -SIZE) {
				particles[index * 3 + i] = SIZE - (-SIZE - particles[index * 3 + i]);
			}
		}
	}
	
	void update(Vector3f newShipPosition, Vector3f forward) {	
		float dx = (worldPosition.x - newShipPosition.x) * SPEED;
		float dy = (worldPosition.y - newShipPosition.y) * SPEED;
		float dz = (worldPosition.z - newShipPosition.z) * SPEED;
		if (Math.abs(dx + dy + dz) < 0.01) {
			dx = forward.x * 250.0f;
			dy = forward.y * 250.0f;
			dz = forward.z * 250.0f;
		}
		for (int i = 0; i < particleCount; i++) {
			particles[i * 3 + 0] += dx;
			particles[i * 3 + 1] += dy;
			particles[i * 3 + 2] += dz;
			adjustPosition(i);
			computeSize(i);
		}
		setPosition(newShipPosition);
		dustParticles.clear();
		dustParticles.put(particles);		
		dustParticles.position(0);
		particleSizes.clear();
		particleSizes.put(sizes);
		particleSizes.position(0);
	}
}
