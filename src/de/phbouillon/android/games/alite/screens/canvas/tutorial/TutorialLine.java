package de.phbouillon.android.games.alite.screens.canvas.tutorial;

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
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import de.phbouillon.android.framework.impl.PulsingHighlighter;
import de.phbouillon.android.games.alite.AliteLog;

class TutorialLine implements OnCompletionListener {
	private String text;
	private final List <Object> speeches;
	private boolean isPlaying = false;
	private boolean finishHookExecuted = false;
	private long pause = 1000;
	private boolean skippable = true;
	private IMethodHook updateMethod = null;
	private IMethodHook prePresentMethod = null;
	private IMethodHook postPresentMethod = null;
	private IMethodHook finishHook = null;
	private List <PulsingHighlighter> highlights = null;
	private int x = 100;
	private int width = 1520;
	private int y = 800;
	private int height = 250;
	private int currentSpeechIndex = 0;
	private boolean mustRetainEvents = false;
		
	TutorialLine(Object speechObject, String text) {
		speeches = new ArrayList<Object>();
		if (speechObject != null) {
			speeches.add(speechObject);
		}
		this.text = text;
	}

	void reset() {
		currentSpeechIndex = 0;
	}
	
	void play(MediaPlayer mp) {
		if (isPlaying) {
			return;
		}
		try {
			isPlaying = true;
			finishHookExecuted = false;
			if (currentSpeechIndex >= speeches.size()) {
				return; // Happens for empty lines; but we still want to set "isPlaying".
			}
			mp.reset();
			Object speechObject = speeches.get(currentSpeechIndex);
			if (speechObject instanceof String) {
				mp.setDataSource((String) speechObject);
			} else {
				@SuppressWarnings("resource")
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
	
	void addSpeech(Object speechObject) {
		speeches.add(speechObject);
	}
	
	boolean isPlaying(MediaPlayer mp) {
		return isPlaying;
	}
	
	String getText() {
		return text;
	}

	TutorialLine setSkippable(boolean skip) {
		skippable = skip;
		return this;
	}
	
	boolean isSkippable() {
		return skippable;
	}
	
	void setFinished() {
		isPlaying = false;
		executeFinishHook();
	}
	
	void executeFinishHook() {
		if (finishHook != null && !finishHookExecuted) {
			finishHookExecuted = true;
			finishHook.execute(0);			
		}
	}
	
	TutorialLine setPause(long ms) {
		this.pause = ms;
		return this;
	}
		
	TutorialLine addHighlight(PulsingHighlighter highlight) {
		if (highlights == null) {
			highlights = new ArrayList<PulsingHighlighter>();
		}
		highlights.add(highlight);
		return this;
	}
	
	void clearHighlights() {
		highlights = null;
	}
	
	int getCurrentSpeechIndex() {
		return currentSpeechIndex;
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		currentSpeechIndex++;
		if (currentSpeechIndex < speeches.size()) {
			isPlaying = false;
			play(mp);
			return;
		}
		if (!skippable) {
			return;
		}
		if (pause == -1) {
			isPlaying = false;	
		} else {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable(){
				@Override
				public void run() {
					isPlaying = false;
				}}, pause);
		}		
	}	
	
	TutorialLine setFinishHook(IMethodHook method) {
		this.finishHook = method;
		return this;
	}
	
	TutorialLine setMustRetainEvents() {
		this.mustRetainEvents = true;
		return this;
	}
	
	TutorialLine setUpdateMethod(IMethodHook method) {
		this.updateMethod = method;
		return this;
	}
	
	IMethodHook getUpdateMethod() {
		return updateMethod;
	}
	
	void update(float deltaTime) {
		if (updateMethod != null) {
			updateMethod.execute(deltaTime);
		}
	}
	
	TutorialLine setPrePresentMethod(IMethodHook method) {
		this.prePresentMethod = method;
		return this;
	}
	
	void prePresent(float deltaTime) {
		if (prePresentMethod != null) {
			prePresentMethod.execute(deltaTime);
		}
	}
	
	TutorialLine setPostPresentMethod(IMethodHook method) {
		this.postPresentMethod = method;
		return this;
	}
	
	void postPresent(float deltaTime) {
		if (postPresentMethod != null) {
			postPresentMethod.execute(deltaTime);
		}
	}	
	
	void renderHighlights(float deltaTime) {
		if (highlights != null) {
			for (PulsingHighlighter highlight: highlights) {
				highlight.display(deltaTime);
			}
		}
	}
	
	int getX() {
		return x;
	}
	
	TutorialLine setX(int newX) {
		x = newX;
		return this;
	}
	
	int getY() {
		return y;
	}
	
	TutorialLine setY(int newY) {
		y = newY;
		return this;
	}
		
	int getWidth() {
		return width;
	}
	
	TutorialLine setWidth(int newWidth) {
		width = newWidth;
		return this;
	}
	
	int getHeight() {
		return height;
	}
	
	boolean mustRetainEvents() {
		return mustRetainEvents;
	}
	
	TutorialLine setHeight(int newHeight) {
		height = newHeight;
		return this;
	}

	void setText(String string) {
		text = string;
	}	
}
