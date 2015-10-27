package de.phbouillon.android.framework;

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

public abstract class GlScreen extends Screen implements Serializable {
	private static final long serialVersionUID = 2776881193369718139L;

	protected boolean isActive;
	protected boolean isDisposed;
	
	public GlScreen(Game game) {
		super(game);
		isActive = false;
	}
	
	public abstract void onActivation();
	public abstract void performUpdate(float deltaTime);
	public abstract void performPresent(float deltaTime);
	
	@Override
	public void activate() {
		try {
			onActivation();
		} finally {
			isActive = true;
		}
	}
	
	public final boolean isActive() {
		return isActive;
	}
	
	@Override
	public final void update(float deltaTime) {
		if (!isActive || isDisposed) {
			return;
		}
		performUpdate(deltaTime);
	}
	
	@Override
	public final void present(float deltaTime) {
		if (!isActive) {
			return;
		}
		performPresent(deltaTime);
	}
		
	@Override
	public void pause() {
		isActive = false;
	}
	
	@Override
	public void resume() {
	}
	
	@Override
	public void postLayout(Object dataObject) {		
	}

	@Override
	public void postScreenChange() {		
	}
	
	@Override
	public void postNavigationRender(float deltaTime) {		
	}
	
	protected void performScreenChange(Screen newScreen) {
		Screen oldScreen = game.getCurrentScreen();
		oldScreen.dispose();
		game.setScreen(newScreen);
		postScreenChange();
		oldScreen = null;
	}
	
	@Override
	public void renderNavigationBar() {	
	}
	
	@Override
	public void dispose() {
		pause();
		isDisposed = true;
	}
}
