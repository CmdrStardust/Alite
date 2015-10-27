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

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.Slider;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class AudioOptionsScreen extends OptionsScreen {
	private Slider musicVolume;
	private Slider effectsVolume;
	private Slider voiceVolume;
	private Button back;
	
	public AudioOptionsScreen(Game game) {
		super(game);		
	}
	
	@Override
	public void activate() {
		musicVolume = createFloatSlider(0, 0, 1, "Music Volume", Settings.musicVolume);
		effectsVolume = createFloatSlider(1, 0, 1, "Effects Volume", Settings.effectsVolume);
		voiceVolume = createFloatSlider(2, 0, 1, "Voice Volume", Settings.voiceVolume);
		back = createButton(5, "Back");
	}
		
	
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		
		displayTitle("Audio Options");
		musicVolume.render(g);
		effectsVolume.render(g);
		voiceVolume.render(g);
		back.render(g);
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		if (musicVolume.checkEvent(touch)) {
			Settings.musicVolume = musicVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (effectsVolume.checkEvent(touch)) {
			Settings.effectsVolume = effectsVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (voiceVolume.checkEvent(touch)) {
			Settings.voiceVolume = voiceVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}

		if (touch.type == TouchEvent.TOUCH_UP) {
			if (back.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new OptionsScreen(game);
			}
		}
	}	
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.AUDIO_OPTIONS_SCREEN;
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		alite.setScreen(new AudioOptionsScreen(alite));
		return true;
	}			
}
