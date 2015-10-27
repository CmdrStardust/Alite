package de.phbouillon.android.games.alite.screens.canvas.missions;

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

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.AliteStartManager;

class MissionLine implements OnCompletionListener {
	private final String text;
	private final Object speechObject;
	private boolean isPlaying = false;
	
	MissionLine(AndroidFileIO fio, String speechPath, String text) throws IOException {
		if (speechPath != null) {
			if (AliteStartManager.HAS_EXTENSION_APK) {
				speechObject = fio.getPrivatePath(speechPath);
			} else {
				speechObject = fio.getFileDescriptor(speechPath);
			}
		} else {
			speechObject = null;
		}
		this.text = text;
	}

	void play(MediaPlayer mp) {
		if (isPlaying || speechObject == null) {
			return;
		}
		try {
			isPlaying = true;
			mp.reset();
			if (AliteStartManager.HAS_EXTENSION_APK) {
				mp.setDataSource((String) speechObject);
			} else {
				AssetFileDescriptor afd = ((AssetFileDescriptor) speechObject); 
				mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			}
			mp.setOnCompletionListener(this);
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			AliteLog.e("Error playing speech file", "Error playing speech file", e);
		}
	}

	boolean isPlaying() {
		return isPlaying;
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		isPlaying = false;	
	}	
	
	String getText() {
		return text;
	}
}
