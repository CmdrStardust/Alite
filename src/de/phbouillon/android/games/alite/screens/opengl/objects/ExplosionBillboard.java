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

import de.phbouillon.android.games.alite.Alite;

public class ExplosionBillboard extends Billboard implements Serializable {
	private static final int EXPLOSION_FRAMES = 48;
			
	private int currentFrame;
	private Explosion explosion;
	
	public ExplosionBillboard(Explosion ex, Alite alite, int frame) {
		super("Explosion", alite, 0.0f, 0.0f, 0.0f, 130.0f, 130.0f, "textures/explosion2.png", alite.getTextureManager().getSprite("textures/explosion2.png", "frame" + frame));
		this.explosion = ex;
		currentFrame = frame;
		setZPositioningMode(ZPositioning.Front);
		boundingSphereRadius = 150.0f;
	}

	public void setFrame(int frame) {
		currentFrame = frame;
		if (currentFrame >= EXPLOSION_FRAMES || currentFrame < 0) {
			setRemove(true);
			return;
		}
		updateTextureCoordinates(alite.getTextureManager().getSprite("textures/explosion2.png", "frame" + frame));
	}

	@Override
	public void resize(float newWidth, float newHeight) {
		super.resize(newWidth, newHeight);
		boundingSphereRadius = (newWidth + newHeight) / 2.0f;
	}
	
	public int getFrame() {
		return currentFrame;
	}

	public Explosion getExplosion() {
		return explosion;
	}	
}
