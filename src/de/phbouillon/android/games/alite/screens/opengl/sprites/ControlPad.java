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
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.ShipControl;

public class ControlPad implements Serializable {
	private static final long serialVersionUID = -3050741925963060286L;
	private final int CPX = Settings.controlPosition == 0 ? (Settings.flatButtonDisplay ? 50 : 150) : Settings.flatButtonDisplay ? 1524 : 1424;
	private final int CPY = Settings.flatButtonDisplay ? 300 : 680;
	private final int WIDTH = 350;
	private final int HEIGHT = 350;

	private final Sprite [] controlPad = new Sprite[9];
	private transient Alite alite;
	
	private int fingerDown = 0;
	private int activeIndex = 0;
	private float accelY = 0.0f;
	private float accelZ = 0.0f;

	ControlPad(final Alite alite) {
		this.alite = alite;
		controlPad[0] = genSprite("cpn", CPX, CPY, WIDTH, HEIGHT);
		controlPad[1] = genSprite("cpu", CPX, CPY, WIDTH, HEIGHT);
		controlPad[2] = genSprite("cpru", CPX, CPY, WIDTH, HEIGHT);
		controlPad[3] = genSprite("cpr", CPX, CPY, WIDTH, HEIGHT);
		controlPad[4] = genSprite("cprd", CPX, CPY, WIDTH, HEIGHT);
		controlPad[5] = genSprite("cpd", CPX, CPY, WIDTH, HEIGHT);
		controlPad[6] = genSprite("cpld", CPX, CPY, WIDTH, HEIGHT);
		controlPad[7] = genSprite("cpl", CPX, CPY, WIDTH, HEIGHT);
		controlPad[8] = genSprite("cplu", CPX, CPY, WIDTH, HEIGHT);		
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "ControlPad.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "ControlPad.readObject I");
			this.alite     = Alite.get();
			AliteLog.e("readObject", "ControlPad.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}
	
	Sprite genSprite(String name, int x, int y, int width, int height) {
		SpriteData spriteData = alite.getTextureManager().getSprite(AliteHud.TEXTURE_FILE, name);
		return new Sprite(alite, AliteHud.ct.getTextureCoordX(x), AliteHud.ct.getTextureCoordY(y),
				                 AliteHud.ct.getTextureCoordX(x + width - 1), AliteHud.ct.getTextureCoordY(y + height - 1),
				   spriteData.x, spriteData.y, spriteData.x2, spriteData.y2, AliteHud.TEXTURE_FILE);	
	}

	public void fingerDown(int pointer) {
		int val = 1 << pointer;
		if ((fingerDown & val) == 0) {
			fingerDown += val;
		}
	}
	
	public boolean fingerUp(int pointer) {
		int val = 1 << pointer;
		if ((fingerDown & val) != 0) {
			fingerDown -= val;
			return true;
		}
		return false;
	}

	public float getZ() {
		return accelZ;
	}
	
	public float getY() {
		return accelY;
	}
	
	private void calculateActiveIndex(int x, int y) {		
		boolean left = x < 125;
		boolean right = x > 225;
		boolean up = y < 125;
		boolean down = y > 225;
		
		setActiveIndex(left, right, up, down);
	}
	
	private void computeSpeeds(float deltaTime) {		
		if (activeIndex > 5) { // left
			accelY += deltaTime * (accelY < 0 ? 5 : 1.66f);
			if (accelY > 2) {
				accelY = 2;
			}
		} else if (activeIndex > 1 && activeIndex < 5) { // right
			accelY -= deltaTime * (accelY > 0 ? 5 : 1.66f);
			if (accelY < -2) {
				accelY = -2;
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
		if (activeIndex == 1 || activeIndex == 2 || activeIndex == 8) { // up
			accelZ -= deltaTime * (accelZ > 0 ? 5 : 1.66f);
			if (accelZ < -2) {
				accelZ = -2;
			}
		} else if (activeIndex == 4 || activeIndex == 5 || activeIndex == 6) { // down
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
	}
	
	public void update(float deltaTime) {
		computeSpeeds(deltaTime);		
	}

	private boolean handleControlPad(TouchEvent event) {
		boolean result = false;
		
		if (event.x >= CPX && event.y >= CPY && event.x <= (CPX + WIDTH) && event.y <= (CPY + HEIGHT)) {
			if (event.type == TouchEvent.TOUCH_DOWN) {
				fingerDown(event.pointer);
				calculateActiveIndex(event.x - CPX, event.y - CPY);
			}
			if (event.type == TouchEvent.TOUCH_DRAGGED) {
				calculateActiveIndex(event.x - CPX, event.y - CPY);
			}
			result = true;
		}
		if (event.type == TouchEvent.TOUCH_UP) {
			if (fingerUp(event.pointer)) {
				result = true;
			}
		}
		if (fingerDown == 0) {
			activeIndex = 0;
		}		
		
		return result;		
	}
	
	public boolean handleUI(TouchEvent event) {
		boolean result = false;
		if (Settings.controlMode == ShipControl.CONTROL_PAD) {
			return handleControlPad(event);
		}
		return result;
	}
	
	public void setActiveIndex(boolean left, boolean right, boolean up, boolean down) {
		if (left) {
			if (up) {
				activeIndex = 8;
			} else if (down) {
				activeIndex = 6;
			} else {
				activeIndex = 7;
			}
		} else if (right) {
			if (up) {
				activeIndex = 2;
			} else if (down) {
				activeIndex = 4;
			} else {
				activeIndex = 3;
			}			
		} else {
			if (up) {
				activeIndex = 1;
			} else if (down) {
				activeIndex = 5;
			} else {
				activeIndex = 0;
			}						
		}		
	}
	
	void render() {
		float a = Settings.alpha * Settings.controlAlpha;
		GLES11.glColor4f(a, a, a, a);
		controlPad[activeIndex].justRender();
		GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, Settings.alpha);
	}	
}
