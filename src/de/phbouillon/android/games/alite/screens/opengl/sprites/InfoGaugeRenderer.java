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
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.screens.opengl.ICoordinateTransformer;

public final class InfoGaugeRenderer implements Serializable {
	private static final long serialVersionUID = -6680085992043102347L;

	private static final float GAUGE_LENGTH = 350.0f;
	
	private transient Alite alite;
	private final Sprite gaugeOverlay;
	private final Sprite gaugeContent;
	private final Sprite missile;
	private final Sprite emptySlot;
	private final Sprite filledSlot;
	private final Sprite targettingSlot;
	private final Sprite lockedSlot;
	private final String [] uiStrings = new String [] {"fs", "as", "fu", "ct", "lt", "al", "sp", "rl", "dc", "1", "2", "3", "4"};
	private final Sprite [] uiTexts = new Sprite[13];
	private final ICoordinateTransformer ct;
	private float pitchPos = 175.0f;
	private float rollPos = 175.0f;

	InfoGaugeRenderer(final Alite alite, final AliteHud hud, final ICoordinateTransformer ct) {
		this.alite = alite;
		this.ct = ct;
		gaugeOverlay   = hud.genSprite("gauge_overlay", 150, 700); 
		gaugeContent   = hud.genSprite("gauge_content", 150, 700);	
		missile        = hud.genSprite("missile", 60, 994);
		emptySlot      = hud.genSprite("missile_empty", 0, 0);
		filledSlot     = hud.genSprite("missile_loaded", 0, 0);
		targettingSlot = hud.genSprite("missile_targetting", 0, 0);
		lockedSlot     = hud.genSprite("missile_targeted", 0, 0);
		int sy = 700;
		for (int i = 0; i < 13; i++) {
			int sx = i < 6 ? 80 : 1792;
			uiTexts[i] = hud.genSprite(uiStrings[i], sx, sy);
			sy += 40;
			if (i == 1 || i == 8) {
				sy += 25;
			}
			if (i == 5) {
				sy = 700;
			}
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "InfoGaugeRenderer.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "InfoGaugeRenderer.readObject I");
			this.alite     = Alite.get();
			AliteLog.e("readObject", "InfoGaugeRenderer.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	private final float extractFrontShield(float alpha) {
		float shieldValue = (float) alite.getCobra().getFrontShield();
		if (shieldValue > PlayerCobra.MAX_SHIELD) {
			shieldValue = PlayerCobra.MAX_SHIELD;
		}
		AliteColors.setGlColor(AliteColors.get().frontShield(shieldValue / PlayerCobra.MAX_SHIELD, alpha), Settings.alpha);
	    return shieldValue / PlayerCobra.MAX_SHIELD * GAUGE_LENGTH;		
	}
	
	private final float extractRearShield(float alpha) {
		float shieldValue = (float) alite.getCobra().getRearShield();
		if (shieldValue > PlayerCobra.MAX_SHIELD) {
			shieldValue = PlayerCobra.MAX_SHIELD;
		}
		AliteColors.setGlColor(AliteColors.get().aftShield(shieldValue / PlayerCobra.MAX_SHIELD, alpha), Settings.alpha);
	    return shieldValue / PlayerCobra.MAX_SHIELD * GAUGE_LENGTH;		
	}
	
	private final float extractFuel(float alpha) {
		float fuel = (float) alite.getCobra().getFuel();		
		AliteColors.setGlColor(AliteColors.get().fuel(fuel / PlayerCobra.MAX_FUEL, alpha), Settings.alpha);
	    return fuel / PlayerCobra.MAX_FUEL * GAUGE_LENGTH;
	}
	
	private final float extractCabinTemperature(float alpha) {
		float cabinTemperature = (float) alite.getCobra().getCabinTemperature();		
		AliteColors.setGlColor(AliteColors.get().cabinTemperature(cabinTemperature / PlayerCobra.MAX_CABIN_TEMPERATURE, alpha), Settings.alpha);
		return cabinTemperature / PlayerCobra.MAX_CABIN_TEMPERATURE * GAUGE_LENGTH;
	}
	
	private final float extractLaserTemperature(float alpha) {
		float laserTemperature = (float) alite.getCobra().getLaserTemperature();		
		AliteColors.setGlColor(AliteColors.get().laserTemperature(laserTemperature / PlayerCobra.MAX_LASER_TEMPERATURE, alpha), Settings.alpha);
	    return laserTemperature / PlayerCobra.MAX_LASER_TEMPERATURE * GAUGE_LENGTH;	
	}
	
	private final float extractAltitude(float alpha) {
		float altitude = (float) alite.getCobra().getAltitude();
		AliteColors.setGlColor(AliteColors.get().altitude(altitude / PlayerCobra.MAX_ALTITUDE, alpha), Settings.alpha);		
	    return altitude / PlayerCobra.MAX_ALTITUDE * GAUGE_LENGTH;
	}
	
	private final float extractSpeed(float alpha) {
		float speed = (float) alite.getCobra().getSpeed();
		if (-speed > PlayerCobra.MAX_SPEED) {
			speed = -PlayerCobra.MAX_SPEED;
		}
		if (speed > 0) { // Retro rockets fired
			speed = -PlayerCobra.MAX_SPEED;
		}
		AliteColors.setGlColor(AliteColors.get().speed(-speed / PlayerCobra.MAX_SPEED, alpha), Settings.alpha);
	    return (-speed / PlayerCobra.MAX_SPEED) * GAUGE_LENGTH;
	}
	
	private final float extractEnergyBank(float alpha, int bank) {
		float energyValue = (float) alite.getCobra().getEnergy(bank);
		AliteColors.setGlColor(AliteColors.get().energyBank(energyValue / PlayerCobra.MAX_ENERGY_BANK, alpha), Settings.alpha);		
		return energyValue / PlayerCobra.MAX_ENERGY_BANK * GAUGE_LENGTH;
	}
	
	private final float extractLastEnergyBank(float alpha) {
		float energyValue = (float) alite.getCobra().getEnergy(3);
		AliteColors.setGlColor(AliteColors.get().lastEnergyBank(energyValue / PlayerCobra.MAX_ENERGY_BANK, alpha), Settings.alpha);		
	    return energyValue / PlayerCobra.MAX_ENERGY_BANK * GAUGE_LENGTH;
	}
	
	private float computeDataValueAndSetColor(int index, float alpha) {
		switch (index) {		
			case  0: return extractFrontShield(alpha);
			case  1: return extractRearShield(alpha);
			case  2: return extractFuel(alpha);
			case  3: return extractCabinTemperature(alpha);
			case  4: return extractLaserTemperature(alpha);
			case  5: return extractAltitude(alpha);
			case  6: return extractSpeed(alpha);
			case  7: AliteColors.setGlColor(AliteColors.get().indicatorBar(alpha), Settings.alpha); 
					 return 0; // Roll dummy
			case  8: AliteColors.setGlColor(AliteColors.get().indicatorBar(alpha), Settings.alpha);
					 return 0; // Pitch dummy
			case  9: return extractEnergyBank(alpha, 0);
			case 10: return extractEnergyBank(alpha, 1);
			case 11: return extractEnergyBank(alpha, 2);			
			case 12: return extractLastEnergyBank(alpha); 
		}
		return GAUGE_LENGTH;
	}
	
	private void renderPitch(float sy) {
		float pitchValue = ((alite.getCobra().getPitch() + 2.0f) / 4.0f) * 350.0f; 
		if (pitchValue > 346.0f) {
			pitchValue = 346.0f;
		}
		if (pitchPos < pitchValue) {
			pitchPos += ((pitchValue - pitchPos) / 3.0f);
		} else if (pitchPos > pitchValue) {
			pitchPos -= ((pitchPos - pitchValue) / 3.0f);
		}
		gaugeContent.setPosition(ct.getTextureCoordX(1420 + pitchPos),
				 ct.getTextureCoordY(sy),
				 ct.getTextureCoordX(1420 + pitchPos + 4.0f),
				 ct.getTextureCoordY(sy + 36));							
	}
	
	private void renderRoll(float sy) {
		float rollValue = ((-alite.getCobra().getRoll() + 2.0f) / 4.0f) * 350.0f; 
		if (rollValue > 346.0f) {
			rollValue = 346.0f;
		}
		if (rollPos < rollValue) {
			rollPos += ((rollValue - rollPos) / 3.0f);
		} else if (rollPos > rollValue) {
			rollPos -= ((rollPos - rollValue) / 3.0f);
		}
		gaugeContent.setPosition(ct.getTextureCoordX(1420 + rollPos),
				 ct.getTextureCoordY(sy),
				 ct.getTextureCoordX(1420 + rollPos + 4.0f),
				 ct.getTextureCoordY(sy + 36));							
	}

	public void renderMissiles() {
		int installedMissiles = alite.getCobra().getMissiles();
		GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, 0.2f * Settings.alpha);
		missile.justRender();
		for (int i = 0; i < 4; i++) {
			if (i < installedMissiles) {
				if (i == installedMissiles - 1 && alite.getCobra().isMissileLocked()) {
					lockedSlot.setPosition(ct.getTextureCoordX(165 + i * 80), ct.getTextureCoordY(990),
							   ct.getTextureCoordX(165 + i * 80 + 80), ct.getTextureCoordY(1027));
					lockedSlot.justRender();					
				} else if (i == installedMissiles - 1 && alite.getCobra().isMissileTargetting()) {
					targettingSlot.setPosition(ct.getTextureCoordX(165 + i * 80), ct.getTextureCoordY(990),
							   ct.getTextureCoordX(165 + i * 80 + 80), ct.getTextureCoordY(1027));
					targettingSlot.justRender();										
				} else {
					filledSlot.setPosition(ct.getTextureCoordX(165 + i * 80), ct.getTextureCoordY(990),
  										   ct.getTextureCoordX(165 + i * 80 + 80), ct.getTextureCoordY(1027));
					filledSlot.justRender();
				}
			} else {
				emptySlot.setPosition(ct.getTextureCoordX(165 + i * 80), ct.getTextureCoordY(990),
			               ct.getTextureCoordX(165 + i * 80 + 80), ct.getTextureCoordY(1027));
				emptySlot.justRender();				
			}
		}
	}
	
	public void render() {	
		int sy = 700;
		float dataValue = GAUGE_LENGTH;

		for (int i = 0, n = uiTexts.length; i < n; i++) {
			Sprite s = uiTexts[i];
			s.justRender();
			
			gaugeOverlay.setPosition(ct.getTextureCoordX(i < 6 ? 150 : 1420),
	                 ct.getTextureCoordY(sy),
	                 ct.getTextureCoordX((i < 6 ? 150 : 1420) + 350),
	                 ct.getTextureCoordY(sy + 36));
			GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, 0.2f * Settings.alpha);
			gaugeOverlay.justRender();

			dataValue = computeDataValueAndSetColor(i, Settings.alpha); 
			if (i == 7) {
				renderRoll(sy);
			} else if (i == 8) {
				renderPitch(sy);
			} else {
				if (dataValue > 0) {
					dataValue++;
				}
				if (i >= 6) {
					dataValue++;
				}
				gaugeContent.setPosition(ct.getTextureCoordX(i < 6 ? 149 : 1419),
						 ct.getTextureCoordY(sy),
						 ct.getTextureCoordX((i < 6 ? 149 : 1419) + dataValue),
						 ct.getTextureCoordY(sy + 36));									
			}
			gaugeContent.justRender();

			GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, Settings.alpha);
			sy += 40;
			if (i == 1 || i == 8) {
				sy += 25;
			}
			if (i == 5) {
				sy = 700;
			}
		}	
		renderMissiles();
	}
}
