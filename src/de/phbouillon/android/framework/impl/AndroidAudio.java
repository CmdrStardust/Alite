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

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import de.phbouillon.android.framework.Audio;
import de.phbouillon.android.framework.Music;
import de.phbouillon.android.framework.Sound;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.AliteStartManager;

public class AndroidAudio implements Audio {
	public static final int MAXIMUM_NUMBER_OF_CONCURRENT_SAMPLES = 20;
	
	private final AssetManager assets;
	private final SoundPool soundPool;
	private final AndroidFileIO fileIO;
	
	public AndroidAudio(Activity activity, AndroidFileIO fileIO) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.fileIO = fileIO;
		this.soundPool = new SoundPool(MAXIMUM_NUMBER_OF_CONCURRENT_SAMPLES, AudioManager.STREAM_MUSIC, 0);
	}
	
	@Override
	public Music newMusic(String fileName, boolean isEffect, boolean isVoice) {
		if (AliteStartManager.HAS_EXTENSION_APK) {
			try {
				return new AndroidMusic(fileIO, fileName, isVoice, isEffect);
			} catch (IOException e) {
				AliteLog.e("Cannot load music", "Music " + fileName + " not found.");
				return null;
			}			
		} else {
			try {
				AliteLog.d("Loading Music", "Loading music " + fileName + (isEffect ? " (effect)" : isVoice ? " (voice)" : ""));
				AssetFileDescriptor assetDescriptor = assets.openFd(fileName);
				AliteLog.d("Loading Music", "Loading music " + assetDescriptor.getStartOffset() + ", " + assetDescriptor.getLength() + ", " + assetDescriptor.getFileDescriptor().valid());
				return new AndroidMusic(assetDescriptor, isVoice, isEffect);
			} catch (IOException e) {
				AliteLog.e("Cannot load music", "Music " + fileName + " not found.");
				return null;
			}
		}
	}

	@Override
	public Sound newSound(String fileName) {
		try {			
			int soundId = AliteStartManager.HAS_EXTENSION_APK ? 
					soundPool.load(fileIO.getPrivatePath(fileName), 0) :
					soundPool.load(fileIO.getFileDescriptor(fileName), 0);
			return new AndroidSound(soundPool, soundId);
		} catch (IOException e) {
			AliteLog.e("Cannot load sound", "Sound " + fileName + " not found.");
			return null;			
		}
	}
	
	@Override
	public Sound newVoice(String fileName) {
		try {
			int soundId = AliteStartManager.HAS_EXTENSION_APK ? 
					soundPool.load(fileIO.getPrivatePath(fileName), 0) :
					soundPool.load(fileIO.getFileDescriptor(fileName), 0);
			return new AndroidSound(soundPool, soundId, true);
		} catch (IOException e) {
			AliteLog.e("Cannot load sound", "Sound " + fileName + " not found.");
			return null;			
		}
	}
}
