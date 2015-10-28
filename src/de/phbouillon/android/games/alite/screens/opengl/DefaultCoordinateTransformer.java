package de.phbouillon.android.games.alite.screens.opengl;

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

import android.graphics.Rect;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.games.alite.Alite;

public class DefaultCoordinateTransformer implements ICoordinateTransformer {
	private static final long serialVersionUID = -6866805331162564974L;

	private final float offsetX;
	private final float offsetY;
	private final float ratio;
	private final boolean conversionNeeded;
	
	public DefaultCoordinateTransformer(Alite alite) {
		Rect visibleArea = ((AndroidGraphics) alite.getGraphics()).getVisibleArea();
		if (visibleArea.width() == 1920 && visibleArea.height() == 1080) {
			conversionNeeded = false;
			offsetX = 0.0f;
			offsetY = 0.0f;
			ratio   = 1.0f;
		} else {
			conversionNeeded = true;
			offsetX = (float) visibleArea.left;
			offsetY = (float) visibleArea.top;
			ratio   = (float) visibleArea.width() / 1920.0f;
		}
	}
	
	@Override
	public float getRatio() {
		return ratio;
	}
	
	@Override
	public float getTextureCoordX(int x) {
		return conversionNeeded ? offsetX + x * ratio : x;
	}
	
	@Override
	public float getTextureCoordY(int y) {
		return conversionNeeded ? offsetY + y * ratio : y;
	}

	@Override
	public float getTextureCoordX(float x) {
		return conversionNeeded ? offsetX + x * ratio : x;
	}

	@Override
	public float getTextureCoordY(float y) {
		return conversionNeeded ? offsetY + y * ratio : y;
	}
}
