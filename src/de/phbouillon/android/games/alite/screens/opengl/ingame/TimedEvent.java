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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class TimedEvent implements Serializable {
	private static final long serialVersionUID = -7887711369377615831L;

	protected long delay;
	protected long lastExecutionTime;
	protected long pauseTime = -1;
	private boolean remove;
	protected boolean locked;
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		if (!remove) {
			out.defaultWriteObject();
		}
	}
	
	public TimedEvent(long delayInNanoSeconds) {
		this.delay = delayInNanoSeconds;
		lastExecutionTime = System.nanoTime();
		pauseTime = -1;
		locked = false;
	}
		
	public TimedEvent(long delayInNanoSeconds, long lastExecutionTime, long pauseTime, boolean locked) {
		this.delay = delayInNanoSeconds;
		this.lastExecutionTime = lastExecutionTime == -1 ? System.nanoTime() : lastExecutionTime;
		this.pauseTime = pauseTime;
		this.locked = locked;
	}

	protected void setRemove(boolean r) {
		remove = r;
	}
	
	public boolean mustBeRemoved() {
		return remove;
	}
	
	public void updateDelay(long newDelay) {
		this.delay = newDelay;
		lastExecutionTime = System.nanoTime();
		pauseTime = -1;
	}
	
	public long timeToNextTrigger() {
		return delay - (System.nanoTime() - lastExecutionTime);
	}
	
	public void perform(long time) {
		if (pauseTime != -1 || locked) {
			return;
		}
		if ((time - lastExecutionTime) >= delay) {
			lastExecutionTime = time;
			doPerform();
		}
	}

	public void lock() {
		locked = true;
	}
	
	public void unlock() {
		if (!locked) {
			return;
		}
		lastExecutionTime = System.nanoTime();
		locked = false;
	}
	
	public long pause() {
		if (pauseTime != -1) {
			return pauseTime;
		}
		pauseTime = System.nanoTime() - lastExecutionTime;
		return pauseTime;
	}	
	
	public boolean isPaused() {
		return pauseTime != -1;
	}
	
	public void resume() {
		if (pauseTime == -1) {
			return;
		}
		lastExecutionTime = System.nanoTime() - pauseTime;
		pauseTime = -1;
	}
	
	public abstract void doPerform();	
}
