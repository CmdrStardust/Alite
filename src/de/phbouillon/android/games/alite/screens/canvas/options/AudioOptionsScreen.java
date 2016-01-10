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
import de.phbouillon.android.framework.Sound;
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
	private Slider soundFxVolume;
	private Slider combatFxVolume;
	private Slider voiceVolume;
	private Slider vibrateLevel;
	private Button back;
	
	public AudioOptionsScreen(Game game) {
		super(game);		
	}
	
	@Override
	public void activate() {
		musicVolume = createFloatSlider(0, 0, 1, "Music Volume", Settings.volumes[Sound.SoundType.MUSIC.getValue()]);
		soundFxVolume = createFloatSlider(1, 0, 1, "Sound FX Volume", Settings.volumes[Sound.SoundType.SOUND_FX.getValue()]);
		combatFxVolume = createFloatSlider(2, 0, 1, "Combat FX Volume", Settings.volumes[Sound.SoundType.COMBAT_FX.getValue()]);
		voiceVolume = createFloatSlider(3, 0, 1, "Voice Volume", Settings.volumes[Sound.SoundType.VOICE.getValue()]);
		vibrateLevel = createFloatSlider(4, 0, 1, "Vibrate Level", Settings.vibrateLevel);
		back = createButton(6, "Back");
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
		soundFxVolume.render(g);
		combatFxVolume.render(g);
		voiceVolume.render(g);
		vibrateLevel.render(g);
		back.render(g);
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		if (musicVolume.checkEvent(touch)) {
			Settings.volumes[Sound.SoundType.MUSIC.getValue()] = musicVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (soundFxVolume.checkEvent(touch)) {
			Settings.volumes[Sound.SoundType.SOUND_FX.getValue()] = soundFxVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (combatFxVolume.checkEvent(touch)) {
			Settings.volumes[Sound.SoundType.COMBAT_FX.getValue()] = combatFxVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (voiceVolume.checkEvent(touch)) {
			Settings.volumes[Sound.SoundType.VOICE.getValue()] = voiceVolume.getCurrentValue();
			Settings.save(game.getFileIO());
		}
		if (vibrateLevel.checkEvent(touch)) {
			Settings.vibrateLevel = vibrateLevel.getCurrentValue();
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
