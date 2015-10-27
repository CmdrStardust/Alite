package de.phbouillon.android.games.alite.screens.opengl.sprites;

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

import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.opengl.ICoordinateTransformer;

public class CompassRenderer implements Serializable {
	private static final long serialVersionUID = -4526005581209851369L;
	private static final int COMPASS_CENTER_X = 1348; 
	private static final int COMPASS_CENTER_Y = 704;

	private transient Alite alite;
	private final ICoordinateTransformer ct;
	private final Sprite compass;
	private final Sprite compassDot;
	private boolean redDotActive = true;
	private float [] planet = new float[3];
	
	CompassRenderer(final Alite alite, final AliteHud hud, final ICoordinateTransformer ct) {
		this.alite = alite;
		this.ct = ct;
		compass    = hud.genSprite("target", 1284, 640); 		
		compassDot = hud.genSprite("red", 1284 + 55, 640 + 55);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "CompassRenderer.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "CompassRenderer.readObject I");
			this.alite = Alite.get();
			redDotActive = true; // Hack to ensure that the correct sprite is loaded:
			// If redDotActive was actually false, now setting it to true will result
			// in the texture sprite (green) being loaded on the next rendering
			// cycle. Ugly, but works :)
			AliteLog.e("readObject", "CompassRenderer.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	void setPlanet(float x, float y, float z) {
		planet[0] = x;
		planet[1] = y;
		planet[2] = z;
	}

	public void render() {		
		float l = (float) Math.sqrt(planet[0] * planet[0] + planet[1] * planet[1] + planet[2] * planet[2]);
		if (Math.abs(l) < 0.001f) {
			l = 1.0f;
		}
		int x = (int) ((float) COMPASS_CENTER_X + (planet[0] * 64.0f / l));
		int y = (int) ((float) COMPASS_CENTER_Y + (planet[1] * -64.0f / l));
		
		if (planet[2] < 0 && !redDotActive) {
			SpriteData spriteData =  alite.getTextureManager().getSprite(AliteHud.TEXTURE_FILE, "red");
			compassDot.setTextureCoords(spriteData.x, spriteData.y, spriteData.x2, spriteData.y2);
			redDotActive = true;
		} else if (planet[2] > 0 && redDotActive) {
			SpriteData spriteData =  alite.getTextureManager().getSprite(AliteHud.TEXTURE_FILE, "green");
			compassDot.setTextureCoords(spriteData.x, spriteData.y, spriteData.x2, spriteData.y2);
			redDotActive = false;			
		}
		
		compassDot.setPosition(ct.getTextureCoordX(x - 8), ct.getTextureCoordY(y - 8), ct.getTextureCoordX(x + 7), ct.getTextureCoordY(y + 7));
		
		compass.justRender();
		compassDot.simpleRender();
	}
	
	public boolean isTargetInCenter() {
		float l = (float) Math.sqrt(planet[0] * planet[0] + planet[1] * planet[1] + planet[2] * planet[2]);
		if (Math.abs(l) < 0.001f) {
			l = 1.0f;
		}
		int x = (int) ((float) COMPASS_CENTER_X + (planet[0] * 64.0f / l));
		int y = (int) ((float) COMPASS_CENTER_Y + (planet[1] * -64.0f / l));

		return redDotActive && Math.abs(x - COMPASS_CENTER_X) < 4 && Math.abs(y - COMPASS_CENTER_Y) < 4; 
	}
}
