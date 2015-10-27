package de.phbouillon.android.games.alite.screens.opengl.ingame;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES11;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;

class OnScreenMessage implements Serializable {
	private static final long serialVersionUID = 1948958480746165833L;

	class DelayedText implements Serializable {
		private static final long serialVersionUID = -936827160142919485L;

		String text;
		long time;
		long duration;
		float scale;
		
		DelayedText(String text, long time) {
			this.text = text;
			this.time = time;
			this.duration = 5000000000l;
			this.scale = 1.0f;
		}
		
		DelayedText(String text, long time, long duration) {
			this.text = text;
			this.time = time;
			this.duration = duration;
			this.scale = 1.0f;
		}
	}
	
	private String text = "";
	private final Vector3f color = new Vector3f(0.94f, 0.94f, 0.0f);
	private long activationTime = 0;
	private long duration;
	private long repetitionTime;
	private long repetitionInterval;
	private long lastRepetitionInactive = 0;
	private int repetitionTimes = -1;
	private long repetitionDuration;
	private String repetitionText;
	private float scale = 1.0f;
	private final List <DelayedText> delayedTexts = new ArrayList<DelayedText>();
	
	void setText(String text) {
		this.text = text;
		this.scale = 1.0f;
		activate(System.nanoTime());
	}
	
	void setScaledText(String text, float scale) {
		this.scale = scale;
		this.text = text;
		activate(System.nanoTime());		
	}
	
	void setText(String text, long activationTimeDelay) {
		delayedTexts.add(new DelayedText(text, System.nanoTime() + activationTimeDelay));
	}

	void setTextForDuration(String text, long duration) {
		this.text = text;
		this.scale = 1.0f;
		activate(System.nanoTime(), duration);
	}

	void setScaledTextForDuration(String text, long duration, float scale) {
		this.text = text;
		this.scale = scale;
		activate(System.nanoTime(), duration);
	}

	void repeatText(String text, long interval) {
		if (repetitionText != null && repetitionText.equals(text)) {
			return;
		}
		this.scale = 1.0f;
		this.text = text;
		activate(System.nanoTime(), 1000000000l);
		repetitionDuration = 1000000000l;
		repetitionInterval = interval;
		repetitionText = text;
		repetitionTimes = -1;
	}
	
	void repeatText(String text, long interval, int times, long duration) {
		if (repetitionText != null && repetitionText.equals(text)) {
			return;
		}
		this.scale = 1.0f;
		this.text = text;
		activate(System.nanoTime(), duration);
		repetitionDuration = duration;
		repetitionInterval = interval;
		repetitionText = text;
		repetitionTimes = -1;
	}

	void repeatText(String text, long interval, int times) {
		if (repetitionText != null && repetitionText.equals(text)) {
			return;
		}
		this.scale = 1.0f;
		this.text = text;
		activate(System.nanoTime(), 1000000000l);
		repetitionDuration = 1000000000l;
		repetitionInterval = interval;
		repetitionText = text;
		repetitionTimes = times;
	}

	String getRepetitionText() {
		return repetitionText;
	}
	
	void clearRepetition() {
		this.repetitionText = null;
		this.repetitionTime = 0;
	}
	
	void activate(long nanoTime) {
		activate(nanoTime, 5000000000l);
	}

	void activate(long nanoTime, long duration) {
		this.activationTime = nanoTime;
		this.duration = duration;
	}
	
	boolean isActive() {
		return activationTime > 0 && System.nanoTime() - activationTime < duration;
	}
	
	void render(Alite alite) {
		long time = System.nanoTime();
		DelayedText toBeRemoved = null;
		for (DelayedText dt: delayedTexts) {
			if (time >= dt.time) {
				this.text = dt.text;
				scale = 1.0f;
				activate(time, dt.duration);
				toBeRemoved = dt;
			}
		}
		if (toBeRemoved != null) {
			delayedTexts.remove(toBeRemoved);
		}
		if (!isActive()) {
			if (lastRepetitionInactive <= 0 && repetitionText != null) {
				lastRepetitionInactive = System.nanoTime();
				repetitionTime = lastRepetitionInactive + repetitionInterval;
			}
			if (repetitionText != null && System.nanoTime() >= repetitionTime && repetitionTime > 0) {
				if (repetitionTimes > 0) {
					repetitionTimes--;
				} else if (repetitionTimes == 0) {
					clearRepetition();
					repetitionTimes = -1;
					return;
				}
				text = repetitionText;
				activate(System.nanoTime(), repetitionDuration);
			}
			return;
		}
		lastRepetitionInactive = 0;
		GLES11.glColor4f(color.x, color.y, color.z, 0.6f);
		alite.getFont().drawText(text, 960, 650, true, scale);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}
