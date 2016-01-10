package de.phbouillon.android.framework.impl;

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

import android.media.SoundPool;
import de.phbouillon.android.framework.Sound;

public class AndroidSound implements Sound {
	private final int soundId;
	private final SoundPool soundPool;
	private int currentStreamId = -1;
	private final SoundType soundType;
	private long delayToNextPlay = -1;
	
	public AndroidSound(SoundPool soundPool, int soundId, SoundType st) {
		this.soundId = soundId;
		this.soundPool = soundPool;
		this.soundType = st;
	}

	@Override
	public SoundType getType() {
		return soundType;
	}
	
	@Override
	public void play(float volume) {
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}
	
	@Override
	public void playOnce(float volume, long delayInMs) {
		long time = System.currentTimeMillis();		
		if (delayToNextPlay == -1 || delayToNextPlay < time) {
			delayToNextPlay = time + delayInMs;
			play(volume);
		}
	}
	
	@Override
	public void repeat(float volume) {
		if (currentStreamId != -1) {
			stop();
		}
		currentStreamId = soundPool.play(soundId, volume, volume, 0, -1, 1);
	}
	
	@Override
	public boolean isPlaying() {
		return currentStreamId != -1;
	}
	
	@Override
	public void stop() {
		if (currentStreamId != -1) {
			soundPool.stop(currentStreamId);
		}
		currentStreamId = -1;
	}

	@Override
	public void dispose() {
		soundPool.unload(soundId);
	}
}
