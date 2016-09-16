package de.phbouillon.android.games.alite;

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

import de.phbouillon.android.framework.Music;
import de.phbouillon.android.framework.Sound;

public class SoundManager {
	public static void play(Sound sound) {
		if (sound == null) {
			AliteLog.w("Sound not yet loaded.", "Can't play sound, because it wasn't loaded yet.");
			return;
		}
		sound.play(Settings.volumes[sound.getType().getValue()]);
	}
	
	public static void playOnce(Sound sound, long delayInMs) {
		if (sound == null) {
			AliteLog.w("Sound not yet loaded.", "Can't play sound, because it wasn't loaded yet.");
			return;
		}
		sound.playOnce(Settings.volumes[sound.getType().getValue()], delayInMs);
	}

	public static boolean isPlaying(Sound sound) {
		if (sound == null) {
			AliteLog.w("Sound not yet loaded.", "Can't play sound, because it wasn't loaded yet.");
			return false;
		}
		return sound.isPlaying();
	}
	
	public static void repeat(Sound sound) {
		if (sound == null) {
			AliteLog.w("Sound not yet loaded.", "Can't play sound, because it wasn't loaded yet.");
			return;
		}
		sound.repeat(Settings.volumes[sound.getType().getValue()]);
	}
	
	public static void stop(Sound sound) {
		if (sound == null) {
			AliteLog.w("Sound not yet loaded.", "Can't play sound, because it wasn't loaded yet.");
			return;
		}
		sound.stop();
	}
		
	private static final void stopInternal(Sound asset) {
		if (asset != null) {
			asset.stop();
		}
	}
	
	private static final void stopInternal(Music asset) {
		if (asset != null) {
			asset.stop();
		}
	}

	public static void stopAll() {
		stopInternal(Assets.alert);
		stopInternal(Assets.click);
		stopInternal(Assets.danube);
		if (Assets.danube != null) {
			Assets.danube.dispose();
			Assets.danube = null;
		}
		stopInternal(Assets.enemyFireLaser);
		stopInternal(Assets.energyLow);
		stopInternal(Assets.criticalCondition);
		stopInternal(Assets.temperatureHigh);
		stopInternal(Assets.altitudeLow);
		stopInternal(Assets.error);
		stopInternal(Assets.fireLaser);
		stopInternal(Assets.torus);
		stopInternal(Assets.fireMissile);
		stopInternal(Assets.hullDamage);
		stopInternal(Assets.kaChing);
		stopInternal(Assets.laserHit);
		stopInternal(Assets.missileLocked);
		stopInternal(Assets.scooped);
		stopInternal(Assets.ecm);
		stopInternal(Assets.identify);
		stopInternal(Assets.retroRocketsOrEscapeCapsuleFired);
		stopInternal(Assets.hyperspace);
		stopInternal(Assets.shipDestroyed);
	}
}
