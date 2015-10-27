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

import java.io.Serializable;

import android.opengl.GLES11;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Explosion implements Serializable {
	private static final long serialVersionUID = 9198779337593261863L;
	private static final long FRAME_TIME = 33333333l;
	
	private final Vector3f zOffset = new Vector3f(0, 0, 0);		
	private InGameManager inGame;
	private ExplosionBillboard [] explosions = new ExplosionBillboard[3];
	private long lastCall = -1;
	private boolean finished;
	private boolean rendered;
	private SpaceObject ref;
	
	public Explosion(Alite alite, SpaceObject ref, InGameManager inGame) {
		this.inGame = inGame;
		this.ref = ref;
		
		float size = ref.getMaxExtentWithoutExhaust();
		ref.getPosition().copy(zOffset);
		zOffset.z -= size / 3.0f;		
		for (int i = 0; i < 3; i++) {
			explosions[i] = new ExplosionBillboard(this, alite, i);
			explosions[i].setPosition(zOffset);				
			explosions[i].resize(size, size);			
			inGame.addObject(explosions[i]);
			zOffset.z += size / 3.0f;
			explosions[i].update(inGame.getShip());
		}
		finished = false;
	}
	
	public void update() {
		if (finished) {
			return;
		}
		long currentTime = System.nanoTime();
		if (lastCall == -1 || (currentTime - lastCall) > FRAME_TIME) {
			lastCall = currentTime;
			for (int i = 0; i < 3; i++) {
				if (explosions[i] == null) {
					continue;
				}
				explosions[i].setFrame(explosions[i].getFrame() + 1);
				explosions[i].resize(explosions[i].getWidth() * 1.1f, explosions[i].getHeight() * 1.1f);
				explosions[i].scale(1.2f);
			}			
		}
		boolean done = true;
		float size = ref.getMaxExtentWithoutExhaust() / 3.0f;
		ref.getPosition().copy(zOffset);

		for (int i = 0; i < 3; i++) {
			if (explosions[i] != null) {
				done = false;
				explosions[i].update(inGame.getShip());
				zOffset.add(explosions[i].getForwardVector(), (i - 1) * size);
				explosions[i].setPosition(zOffset);
			}
		}

		if (done) {
			finished = true;
		}
		rendered = false;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void render() {
		if (finished || rendered) {
			return;
		}
		GLES11.glColor4f(0.94f, 0, 0, 1.0f);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glDepthMask(false);		
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE);		
		for (int i = 0; i < 3; i++) {
			if (explosions[i] == null) {
				continue;
			}
			GLES11.glPushMatrix();
			GLES11.glMultMatrixf(explosions[i].getMatrix(), 0);
			explosions[i].batchRender();
			GLES11.glPopMatrix();
		}		
		Alite.get().getTextureManager().setTexture(null);
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glDepthMask(true);
		GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		rendered = true;
	}
}
