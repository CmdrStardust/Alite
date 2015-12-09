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

import android.opengl.GLES11;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Rect;
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;

public class CursorKeys implements Serializable {
	private static final long serialVersionUID = 2357990555586247354L;

	private final int WIDTH;
	private final int HEIGHT;
	private final int GAP;
	private final int CPX; 
	private final int CPX2; 
	private final int CPY;
	private final int CPY2;

	private final Sprite [] cursorKeys = new Sprite[8];
	private final Rect [] buttonCoordinates = new Rect[4];
	private final int [] downPointer = new int[4];
	private transient Alite alite;
	
	private float accelY = 0.0f;
	private float accelZ = 0.0f;

	CursorKeys(final Alite alite, boolean split) {
		this.alite = alite;

		WIDTH = 192;
		HEIGHT = 192;
		GAP = 16;		
		if (split) {			  
			int x1 = Settings.flatButtonDisplay ? 20 : 120;
			int x2 = Settings.flatButtonDisplay ? (Settings.controlPosition == 1 ? 1900 - WIDTH : 1494) : 1394;
			CPX = Settings.controlPosition == 0 ? x1 : x2;
			CPX2 = Settings.controlPosition == 1 ? x1 : x2;
			CPY  = Settings.flatButtonDisplay ? 350 : 740;
			CPY2 = CPY + HEIGHT + GAP;
			buttonCoordinates[0] = new Rect(CPX + (Settings.flatButtonDisplay ? 0 : (WIDTH >> 1)), CPY - (HEIGHT >> 1), WIDTH, HEIGHT);
			buttonCoordinates[1] = new Rect(CPX2 + WIDTH + GAP, CPY, WIDTH, HEIGHT);
			buttonCoordinates[2] = new Rect(CPX + (Settings.flatButtonDisplay ? 0 : (WIDTH >> 1)), CPY - (HEIGHT >> 1) + GAP + HEIGHT, WIDTH, HEIGHT);
			buttonCoordinates[3] = new Rect(CPX2, CPY, WIDTH, HEIGHT);
		} else {
			CPX = Settings.controlPosition == 0 ? 30 : 1304;
			CPX2 = Settings.controlPosition == 1 ? 30 : 1304;
			CPY  = Settings.flatButtonDisplay ? 270 : 670;
			CPY2 = CPY + HEIGHT + GAP;
			buttonCoordinates[0] = new Rect(CPX + WIDTH + GAP, CPY, WIDTH, HEIGHT);
			buttonCoordinates[1] = new Rect(CPX + ((WIDTH + GAP) << 1), CPY2, WIDTH, HEIGHT);
			buttonCoordinates[2] = new Rect(CPX + WIDTH + GAP, CPY2, WIDTH, HEIGHT);
			buttonCoordinates[3] = new Rect(CPX, CPY2, WIDTH, HEIGHT);
		}
		cursorKeys[0] = genSprite("cu", buttonCoordinates[0]);						
		cursorKeys[1] = genSprite("cr", buttonCoordinates[1]);
		cursorKeys[2] = genSprite("cd", buttonCoordinates[2]);
		cursorKeys[3] = genSprite("cl", buttonCoordinates[3]);
		cursorKeys[4] = genSprite("cuh", buttonCoordinates[0]);
		cursorKeys[5] = genSprite("crh", buttonCoordinates[1]);
		cursorKeys[6] = genSprite("cdh", buttonCoordinates[2]);
		cursorKeys[7] = genSprite("clh", buttonCoordinates[3]);		
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "CursorKeys.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "CursorKeys.readObject I");
			this.alite     = Alite.get();
			AliteLog.e("readObject", "CursorKeys.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	Sprite genSprite(String name, Rect r) {
		// Careful: Rect is used as (x, y) - (width, height) here... So right and bottom are really width and height!
		SpriteData spriteData = alite.getTextureManager().getSprite(AliteHud.TEXTURE_FILE, name);
		return new Sprite(alite, AliteHud.ct.getTextureCoordX(r.left), AliteHud.ct.getTextureCoordY(r.top),
				                 AliteHud.ct.getTextureCoordX(r.left + r.right - 1), AliteHud.ct.getTextureCoordY(r.top + r.bottom - 1),
				   spriteData.x, spriteData.y, spriteData.x2, spriteData.y2, AliteHud.TEXTURE_FILE);	
	}

	public float getZ() {
		return accelZ;
	}
	
	public float getY() {
		return accelY;
	}
	
	public void setHighlight(boolean left, boolean right, boolean up, boolean down) {
		// 728 is an arbitrary value: Just use any value that is highly unlikely to be
		// used as a real touch pointer... Unless someone has 728 fingers (simultaneously)
		// on the device and said device supports 728 simultaneous touch inputs, we should
		// be safe :).
		downPointer[0] = up ? 728 : 0;
		downPointer[1] = right ? 728 : 0;
		downPointer[2] = down ? 728 : 0;
		downPointer[3] = left ? 728 : 0;
	}
	
	public void update(float deltaTime) {
		if (downPointer[0] != 0) { // up
			accelZ -= deltaTime * (accelZ > 0 ? 5 : 1.66f);
			if (accelZ < -2) {
				accelZ = -2;
			}
		} else if (downPointer[2] != 0) { // down
			accelZ += deltaTime * (accelZ < 0 ? 5 : 1.66f);
			if (accelZ > 2) {
				accelZ = 2;
			}			
		} else {
			if (accelZ > 0) {
				accelZ -= deltaTime * 3.33f;
				if (accelZ < 0) {
					accelZ = 0.0f;
				}
			} else if (accelZ < 0) {
				accelZ += deltaTime * 3.33f;
				if (accelZ > 0) {
					accelZ = 0.0f;
				}				
			}						
		}
		if (downPointer[1] != 0) { // right
			accelY -= deltaTime * (accelY > 0 ? 5 : 1.66f);
			if (accelY < -2) {
				accelY = -2;
			}						
		} else if (downPointer[3] != 0) { // left
			accelY += deltaTime * (accelY < 0 ? 5 : 1.66f);
			if (accelY > 2) {
				accelY = 2;
			}									
		} else {
			if (accelY > 0) {
				accelY -= deltaTime * 3.33f;
				if (accelY < 0) {
					accelY = 0.0f;
				}
			} else if (accelY < 0) {
				accelY += deltaTime * 3.33f;
				if (accelY > 0) {
					accelY = 0.0f;
				}				
			}			
		}
	}
		
	public boolean handleUI(TouchEvent event) {
		boolean result = false;
		
		if (event.type == TouchEvent.TOUCH_DOWN) {
			for (int i = 0; i < 4; i++) {
				if (event.x >= buttonCoordinates[i].left &&
				    event.x <= (buttonCoordinates[i].left + buttonCoordinates[i].right) &&
				    event.y >= buttonCoordinates[i].top &&
				    event.y <= (buttonCoordinates[i].top + buttonCoordinates[i].bottom)) {
					result = true;
					int val = 1 << event.pointer;
					if ((downPointer[i] & val) == 0) {
						downPointer[i] += val;
					}
				}
			}
		} else if (event.type == TouchEvent.TOUCH_UP) {
			for (int i = 0; i < 4; i++) {
				int val = 1 << event.pointer;
				if ((downPointer[i] & val) != 0) {
					downPointer[i] -= val;
					result = true;
				}
			}
		} else if (event.type == TouchEvent.TOUCH_DRAGGED) {
			for (int i = 0; i < 4; i++) {
				int val = 1 << event.pointer;
				if ((downPointer[i] & val) != 0) {
					result = true;
				}
			}			
		}
		
		return result;
	}
		
	void render() {
		float a = Settings.alpha * Settings.controlAlpha;
		GLES11.glColor4f(a, a, a, a);		
		for (int i = 0; i < 4; i++) {
			cursorKeys[downPointer[i] > 0 ? i + 4 : i].justRender();
		}
		GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, Settings.alpha);
	}	
}
