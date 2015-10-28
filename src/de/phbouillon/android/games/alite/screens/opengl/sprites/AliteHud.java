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
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.ShipControl;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.screens.opengl.ICoordinateTransformer;

public class AliteHud extends Sprite implements Serializable {
	private static final long serialVersionUID      = -1218984695547293867L;
	private static final long ENEMY_VISIBLE_PHASE   = 660000000l;
	private static final long ENEMY_INVISIBLE_PHASE = 340000000l;
	
	public static final int MAX_DISTANCE = 44000;
	public static final int MAX_DISTANCE_SQ = MAX_DISTANCE * MAX_DISTANCE;
	public static final int MAXIMUM_OBJECTS = 128;
	public static final int RADAR_X1 = 558;
	public static final int RADAR_Y1 = 700;
	public static final int RADAR_X2 = RADAR_X1 + 803;
	public static final int RADAR_Y2 = RADAR_Y1 + 303;

	public static final int ALITE_TEXT_X1 = 832;
	public static final int ALITE_TEXT_Y1 = 1020;
	public static final int ALITE_TEXT_X2 = ALITE_TEXT_X1 + 256;
	public static final int ALITE_TEXT_Y2 = ALITE_TEXT_Y1 + 60;

	public static ICoordinateTransformer ct;
	
	protected transient FloatBuffer lollipopBar;
	protected transient FloatBuffer lollipopStem;
	private float [][] objects = new float[MAXIMUM_OBJECTS][3];
	private float [][] objectColors = new float[MAXIMUM_OBJECTS][3];	
	private boolean [] enemy = new boolean[MAXIMUM_OBJECTS];
	private boolean [] enabled = new boolean[MAXIMUM_OBJECTS];
	private final Sprite laser;
	private final Sprite aliteText;
	private final Sprite safeIcon;
	private final Sprite ecmIcon;
	private final Sprite frontViewport;
	private final Sprite rearViewport;
	private final Sprite leftViewport;
	private final Sprite rightViewport;
	private boolean enemiesVisible = true;
	
	private transient Alite alite;
	private int viewDirection = 0;
	private int currentLaserIndex = 0;
	private boolean safeZone = false;
	private boolean extendedSafeZone = false;
	
	static final String TEXTURE_FILE = "textures/radar_final.png";
	
	private final InfoGaugeRenderer infoGauges;
	private final CompassRenderer compass;
	private float zoomFactor = 1.0f;
	private long ecmActive;
	private boolean witchSpace = false;
	private ControlPad controlPad;
	private CursorKeys controlKeys;
	private long lastCall = -1;
	
	public AliteHud(Alite alite) {
		super(alite, ct.getTextureCoordX(RADAR_X1), ct.getTextureCoordY(RADAR_Y1), ct.getTextureCoordX(RADAR_X2), ct.getTextureCoordY(RADAR_Y2), 
				 0, 0, 1, 1, TEXTURE_FILE);
		SpriteData spriteData = alite.getTextureManager().getSprite(TEXTURE_FILE, "radar");
		setTextureCoords(spriteData.x, spriteData.y, spriteData.x2, spriteData.y2);

		this.alite    = alite;
		lollipopBar   = GlUtils.allocateFloatBuffer(4 * 8);
		lollipopStem  = GlUtils.allocateFloatBuffer(4 * 8);
		laser         = genSprite("pulse_laser", 896, 476);
		aliteText     = genSprite("alite", 864, 1030);
		safeIcon      = genSprite("s", 1284, RADAR_Y2 - 68);
		ecmIcon       = genSprite("e", RADAR_X1 - 40, RADAR_Y2 - 68);
		frontViewport = genSprite("front", RADAR_X1 + ((RADAR_X2 - RADAR_X1) >> 1) - 133, RADAR_Y1);
		rearViewport  = genSprite("rear",  RADAR_X1 + ((RADAR_X2 - RADAR_X1) >> 1) - 133, RADAR_Y1 + ((RADAR_Y2 - RADAR_Y1) >> 1));
		leftViewport  = genSprite("left", RADAR_X1, RADAR_Y1 + ((RADAR_Y2 - RADAR_Y1) >> 1) - 73);
		rightViewport = genSprite("right", RADAR_X1 + ((RADAR_X2 - RADAR_X1) >> 1), RADAR_Y1 + ((RADAR_Y2 - RADAR_Y1) >> 1) - 73);
		infoGauges    = new InfoGaugeRenderer(alite, this, ct);
		compass       = new CompassRenderer(alite, this, ct);
		if (Settings.controlMode == ShipControl.CONTROL_PAD) {
			controlPad = new ControlPad(alite);
		} else if (Settings.controlMode == ShipControl.CURSOR_BLOCK) {
			controlKeys = new CursorKeys(alite, false);
		} else if (Settings.controlMode == ShipControl.CURSOR_SPLIT_BLOCK) {
			controlKeys = new CursorKeys(alite, true);		
		}
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "AliteHud.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "AliteHud.readObject I");
			this.alite    = Alite.get();
			lollipopBar   = GlUtils.allocateFloatBuffer(4 * 8);
			lollipopStem  = GlUtils.allocateFloatBuffer(4 * 8);
			alite.getTextureManager().addTexture(TEXTURE_FILE);
			SpriteData spriteData = alite.getTextureManager().getSprite(TEXTURE_FILE, "radar");
			setTextureCoords(spriteData.x, spriteData.y, spriteData.x2, spriteData.y2);
			currentLaserIndex = -1;
			computeLaser();
			AliteLog.e("readObject", "AliteHud.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	Sprite genSprite(String name, int x, int y) {
		SpriteData spriteData = alite.getTextureManager().getSprite(TEXTURE_FILE, name);
		return new Sprite(alite, ct.getTextureCoordX(x), ct.getTextureCoordY(y), ct.getTextureCoordX(x + spriteData.origWidth), ct.getTextureCoordY(y + spriteData.origHeight),
				   spriteData.x, spriteData.y, spriteData.x2, spriteData.y2, TEXTURE_FILE);	
	}
	
	public void setSafeZone(boolean b) {
		safeZone = b;
	}
	
	public boolean isInSafeZone() {
		return safeZone;
	}
	
	public void setExtendedSafeZone(boolean b) {
		extendedSafeZone = b;
	}
	
	public boolean isInExtendedSafeZone() {
		return extendedSafeZone;
	}
	
	private final void computeLaser() {
		Laser laser = null;		
		switch (viewDirection) {
			case 0: laser = alite.getPlayer().getCobra().getLaser(PlayerCobra.DIR_FRONT); break;
			case 1: laser = alite.getPlayer().getCobra().getLaser(PlayerCobra.DIR_RIGHT); break;
			case 2: laser = alite.getPlayer().getCobra().getLaser(PlayerCobra.DIR_REAR);  break;
			case 3: laser = alite.getPlayer().getCobra().getLaser(PlayerCobra.DIR_LEFT);  break;
		}
		if (laser != null) {
			if (laser.getIndex() != currentLaserIndex) {
				SpriteData spriteData =  alite.getTextureManager().getSprite(TEXTURE_FILE, 
						laser.getIndex() == 0 ? "pulse_laser" :
						laser.getIndex() == 1 ? "beam_laser" :
						laser.getIndex() == 2 ? "mining_laser" :
							"military_laser");
				currentLaserIndex = laser.getIndex();
				this.laser.setTextureCoords(spriteData.x, spriteData.y, spriteData.x2, spriteData.y2);
			}
		} else {
			currentLaserIndex = -1;
		}
	}
	
	public void setObject(int index, float x, float y, float z, Vector3f color, boolean isEnemy) {
		if (index >= MAXIMUM_OBJECTS) {
			AliteLog.e("ALITE Hud", "Maximum number of HUD objects exceeded!");
			return;
		}
		objects[index][0] = x;
		objects[index][1] = y;
		objects[index][2] = z;
		objectColors[index][0] = color.x;
		objectColors[index][1] = color.y;
		objectColors[index][2] = color.z;
		enemy[index] = isEnemy;
		enabled[index] = true;
	}
	
	public int zoomIn() {
		if (zoomFactor > 3.0f) {
			return 4;
		}
		if (Math.abs(zoomFactor - 1.0f) < 0.001f) {
			zoomFactor = 2.0f;
			return 2;
		} 
		zoomFactor = 4.0f;
		return 4;
	}
	
	public int zoomOut() {
		if (zoomFactor < 1.5f) {
			return 1;
		}
		if (Math.abs(zoomFactor - 2.0f) < 0.001f) {
			zoomFactor = 1.0f;
			return 1;
		}
		zoomFactor = 2.0f;
		return 2;		
	}
	
	public void setZoomFactor(float newZoomFactor) {
		zoomFactor = newZoomFactor;
	}
	
	public float getZoomFactor() {
		return zoomFactor;
	}
	
	public void clear() {
		for (int i = 0; i < MAXIMUM_OBJECTS; i++) {
			enabled[i] = false;
		}
	}
	
	public void setPlanet(float x, float y, float z) {
		compass.setPlanet(x, y, z);
	}
	
	public void disableObject(int index) {
		if (index >= MAXIMUM_OBJECTS) {
			return;
		}
		enabled[index] = false;
	}
		
	private void renderLollipops() {
		float x1, y1, y2;
		for (int i = 0; i < MAXIMUM_OBJECTS; i++) {
			if (enabled[i] && (!enemy[i] || enemiesVisible)) {
				x1 = objects[i][0] / (110.0f / zoomFactor) + RADAR_X1 + 402;
				y1 = objects[i][2] / (294.0f / zoomFactor) + RADAR_Y1 + 146;
				y2 = y1 - objects[i][1] / (294.0f / zoomFactor);						
				float distance = objects[i][0] * objects[i][0] + objects[i][1] * objects[i][1] + objects[i][2] * objects[i][2];
				if (distance > 44100.0f / zoomFactor * 44100.0f / zoomFactor) {
					// Don't show objects farther away than 44,100m
					continue;
				}

				lollipopBar.put(ct.getTextureCoordX(x1 - 13.0f));
				lollipopBar.put(ct.getTextureCoordY(y2));

				lollipopBar.put(ct.getTextureCoordX(x1 - 13.0f));
				lollipopBar.put(ct.getTextureCoordY(y2 + 15.0f));

				lollipopBar.put(ct.getTextureCoordX(x1 + 12.0f));
				lollipopBar.put(ct.getTextureCoordY(y2));
				
				lollipopBar.put(ct.getTextureCoordX(x1 + 12.0f));
				lollipopBar.put(ct.getTextureCoordY(y2 + 15.0f));
				
				lollipopBar.position(0);
				
				GLES11.glColor4f(objectColors[i][0] * Settings.alpha, objectColors[i][1] * Settings.alpha, objectColors[i][2] * Settings.alpha, Settings.alpha);
				GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, lollipopBar);
				GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);		
				
				if (Math.abs(y2 - y1) > 15.0f) {
					lollipopStem.put(ct.getTextureCoordX(x1 - 13.0f));
					lollipopStem.put(y1 < y2 ? ct.getTextureCoordY(y1 + 15.0f) : ct.getTextureCoordY(y1));

					lollipopStem.put(ct.getTextureCoordX(x1 - 13.0f));
					lollipopStem.put(y1 < y2 ? ct.getTextureCoordY(y2) : ct.getTextureCoordY(y2 + 15.0f));

					lollipopStem.put(ct.getTextureCoordX(x1 - 3));
					lollipopStem.put(y1 < y2 ? ct.getTextureCoordY(y1 + 15.0f) : ct.getTextureCoordY(y1));

					lollipopStem.put(ct.getTextureCoordX(x1 - 3));
					lollipopStem.put(y1 < y2 ? ct.getTextureCoordY(y2) : ct.getTextureCoordY(y2 + 15.0f));

					lollipopStem.position(0);
					GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, lollipopStem);
					GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
				}
			}
		}
	}
	
	public boolean isWitchSpace() {
		return witchSpace;
	}
	
	public void setWitchSpace(boolean b) {
		witchSpace = b;
	}
	
	public void setViewDirection(int viewDirection) {
		if (viewDirection != this.viewDirection) {
			this.viewDirection = viewDirection;
			computeLaser();
		}
	}
			
	public void showECM(long time) {
		ecmActive = System.currentTimeMillis() + time;
	}
	
	public float getY() {
		return controlPad != null ? controlPad.getY() : controlKeys != null ? controlKeys.getY() : 0.0f;
	}
	
	public float getZ() {
		return controlPad != null ? controlPad.getZ() : controlKeys != null ? controlKeys.getZ() : 0.0f;
	}

	public void update(float deltaTime) {
		long t = System.nanoTime();
		if (lastCall == -1) {
			lastCall = t;
		} else {
			if (enemiesVisible && (t - lastCall) >= ENEMY_VISIBLE_PHASE) {
				lastCall = t;
				enemiesVisible = false;
			} else if (!enemiesVisible && (t - lastCall) >= ENEMY_INVISIBLE_PHASE) {
				lastCall = t;
				enemiesVisible = true;
			}
		}
		if (controlPad != null) {
			controlPad.update(deltaTime);
		}
		if (controlKeys != null) {
			controlKeys.update(deltaTime);
		}
	}
	
	public boolean handleUI(TouchEvent event) {
		if (controlPad != null) {
			return controlPad.handleUI(event);
		}
		if (controlKeys != null) {
			return controlKeys.handleUI(event);
		}
		return false;
	}
	
	public void mapDirections(boolean left, boolean right, boolean up, boolean down) {
		if (controlPad != null) {
			controlPad.setActiveIndex(left, right, up, down);
		}
		if (controlKeys != null) {
			controlKeys.setHighlight(left, right, up, down);
		}
	}
	
	@Override
	public void render() {
		
		setUp();
		GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, Settings.alpha);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);

		computeLaser();
		if (currentLaserIndex >= 0) {
			laser.simpleRender();
		}
		if (!witchSpace) {
			compass.render();
		}

		infoGauges.render();
		GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, Settings.alpha);
		aliteText.justRender();
		if (safeZone) {
			safeIcon.justRender();
		}
		if (System.currentTimeMillis() < ecmActive) {
			ecmIcon.justRender();
		}
		
		if (Settings.controlMode == ShipControl.CONTROL_PAD && controlPad != null) {
			controlPad.render();
		} else if ((Settings.controlMode == ShipControl.CURSOR_BLOCK || Settings.controlMode == ShipControl.CURSOR_SPLIT_BLOCK) && controlKeys != null) {
			controlKeys.render();
		}
		
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
		switch (viewDirection) {
			case 0: frontViewport.justRender(); break;
			case 1: rightViewport.justRender(); break;
			case 2: rearViewport.justRender();  break;
			case 3: leftViewport.justRender();  break;
		}
		
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);			
		renderLollipops();
		if (zoomFactor > 1.5f && zoomFactor < 3.0f) {
			GLES11.glColor4f(0.94f * Settings.alpha, 0.94f * Settings.alpha, 0.0f, 0.6f * Settings.alpha);
			alite.getFont().drawText("x2", RADAR_X1 + 20, RADAR_Y1 + 20, false, 1.0f);			
		} else if (zoomFactor > 3.0f) {
			GLES11.glColor4f(0.94f * Settings.alpha, 0.94f * Settings.alpha, 0.0f, 0.6f * Settings.alpha);
			alite.getFont().drawText("x4", RADAR_X1 + 20, RADAR_Y1 + 20, false, 1.0f);				
		}
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_LIGHTING);

		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		cleanUp();
		
	}
	
	public boolean isTargetInCenter() {
		return compass != null && compass.isTargetInCenter();
	}
}
