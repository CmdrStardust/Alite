package de.phbouillon.android.games.alite.screens.canvas.options;

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

import java.io.DataInputStream;

import android.content.pm.ActivityInfo;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.Slider;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Condition;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class DisplayOptionsScreen extends OptionsScreen {
	private Button animations;
	private Button colorScheme;
	private Button textureLevel;
	private Button colorDepth;
	private Button engineExhaust;
	private Button lockOrientation;
	private Button targetBox;
	private Button stardustDensity;
	private Slider alpha;
	private Slider controlsAlpha;
	private Button immersion;
	private Button back;
	
	public DisplayOptionsScreen(Game game) {
		super(game);	
	}
	
	@Override
	public void activate() {
		textureLevel    = createSmallButton(0, true, "Texture Details: " + getTextureLevelString(Settings.textureLevel));
		stardustDensity = createSmallButton(0, false, "Stardust Density: " + getStardustDensityString(Settings.particleDensity));
		colorDepth      = createSmallButton(1, true, "Color Depth: " + (Settings.colorDepth == 1 ? "32 Bit" : "16 Bit"));
		engineExhaust   = createSmallButton(1, false, "Engine Exhaust: " + (Settings.engineExhaust ? "On" : "Off"));
		lockOrientation = createSmallButton(2, true, "Lock Orientation: " + getOrientationLockString(Settings.lockScreen));
		targetBox       = createSmallButton(2, false, "Show Target Box: " + (Settings.targetBox ? "Yes" : "No"));
		alpha           = createFloatSlider(3, 0, 1, "HUD Alpha", Settings.alpha);
		controlsAlpha   = createFloatSlider(4, 0, 1, "Control Keys Alpha", Settings.controlAlpha);
	
		animations      = createSmallButton(5, true, "Animations: " + (Settings.animationsEnabled ? "On" : "Off"));
		colorScheme     = createSmallButton(5, false, "Color scheme: " + (Settings.colorScheme == 1 ? "Modern" : "Classic"));
		immersion       = createSmallButton(6, true, "Immersion: " + (Settings.navButtonsVisible ? "Off" : "Full"));
		back            = createSmallButton(6, false, "Back");
	}
	
	private String getTextureLevelString(int i) {
		switch (i) {
			case 1: return "High";
			case 2: return "Medium";
			case 4: return "Low";
		}
		return "Invalid";
	}
	
	private String getStardustDensityString(int i) {
		switch (i) {
			case 0: return "Off";
			case 1: return "Low";
			case 2: return "Medium";
			case 3: return "High";
		}
		return "Invalid";
	}
	
	private String getOrientationLockString(int i) {
		switch (i) {
			case 0: return "No Lock";
			case 1: return "Right";
			case 2: return "Left";
		}
		return "Invalid";
	}
	
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		
		displayTitle("Display Options");
		textureLevel.render(g);
		stardustDensity.render(g);
		engineExhaust.render(g);
		colorDepth.render(g);
		lockOrientation.render(g);
		targetBox.render(g);
		alpha.render(g);
		controlsAlpha.render(g);
		animations.render(g);
		colorScheme.render(g);
		immersion.render(g);
		back.render(g);
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		if (alpha.checkEvent(touch)) {
			Settings.alpha = alpha.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (controlsAlpha.checkEvent(touch)) {
			Settings.controlAlpha = controlsAlpha.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (textureLevel.isTouched(touch.x, touch.y)) { 
				SoundManager.play(Assets.click);
				Settings.textureLevel <<= 1;
				if (Settings.textureLevel > 4) {
					Settings.textureLevel = 1;
				}
				textureLevel.setText("Texture Details: " + getTextureLevelString(Settings.textureLevel));
				Settings.save(game.getFileIO());
			} else if (stardustDensity.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.particleDensity++;
				if (Settings.particleDensity >= 4) {
					Settings.particleDensity = 0;
				}
				stardustDensity.setText("Stardust Density: " + getStardustDensityString(Settings.particleDensity));
				Settings.save(game.getFileIO());
			} else if (colorDepth.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.colorDepth = -Settings.colorDepth + 1;
				colorDepth.setText("Color Depth: " + (Settings.colorDepth == 1 ? "32 Bit" : "16 Bit"));
				Settings.save(game.getFileIO());
			} else if (animations.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.animationsEnabled = !Settings.animationsEnabled;
				animations.setText("Animations: " + (Settings.animationsEnabled ? "On" : "Off"));
				Settings.save(game.getFileIO());				
			} else if (colorScheme.isTouched(touch.x, touch.y)) { 
				SoundManager.play(Assets.click);
				Settings.colorScheme = -Settings.colorScheme + 1;
				colorScheme.setText("Color scheme: " + (Settings.colorScheme == 1 ? "Modern" : "Classic"));
				AliteColors.update();
				Condition.update();
				Settings.save(game.getFileIO());
				newScreen = new DisplayOptionsScreen(game);
			} else if (lockOrientation.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.lockScreen++;
				if (Settings.lockScreen >= 3) {
					Settings.lockScreen = 0;
				}
				switch (Settings.lockScreen) {
					case 0: ((AndroidGame) game).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); break;
					case 1: ((AndroidGame) game).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
					case 2: ((AndroidGame) game).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break;
				}				
				lockOrientation.setText("Lock Orientation: " + getOrientationLockString(Settings.lockScreen));
				Settings.save(game.getFileIO());
			} else if (targetBox.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.targetBox = !Settings.targetBox;
				targetBox.setText("Show Target Box: " + (Settings.targetBox ? "Yes" : "No"));
				Settings.save(game.getFileIO());
			} else if (engineExhaust.isTouched(touch.x, touch.y)) {				
				SoundManager.play(Assets.click);
				Settings.engineExhaust = !Settings.engineExhaust;
				engineExhaust.setText("Engine Exhaust: " + (Settings.engineExhaust ? "On" : "Off"));
				Settings.save(game.getFileIO());				
			} else if (immersion.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.navButtonsVisible = !Settings.navButtonsVisible;
				immersion.setText("Immersion: " + (Settings.navButtonsVisible ? "Off" : "Full"));
				Settings.save(game.getFileIO());				
			} else if (back.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new OptionsScreen(game);
			}
		}
	}	
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.DISPLAY_OPTIONS_SCREEN;
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		alite.setScreen(new DisplayOptionsScreen(alite));
		return true;
	}		
}
