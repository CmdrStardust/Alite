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

import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;

public class SpriteObject extends AliteObject implements Geometry {
	private final Sprite sprite;
	
	public SpriteObject(final Alite alite, float left, float top, float right,
			float bottom, float tLeft, float tTop, float tRight, float tBottom,
			final String textureFilename) {
		super("SpriteObject");
		this.sprite = new Sprite(alite, left, top, right, bottom, tLeft, tTop, tRight, tBottom, textureFilename);
	}

	@Override
	public boolean isVisibleOnHud() {
		return false;
	}

	@Override
	public Vector3f getHudColor() {
		return null;
	}

	public void render() {
	  sprite.simpleRender();
	}

	@Override
	public float getDistanceFromCenterToBorder(Vector3f dir) {
		return 0;
	}

	@Override
	public void setDisplayMatrix(float[] matrix) {
	}

	@Override
	public float[] getDisplayMatrix() {
		return null;
	}
}
