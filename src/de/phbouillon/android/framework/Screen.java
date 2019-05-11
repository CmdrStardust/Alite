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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public abstract class Screen implements Serializable {
	private static final long serialVersionUID = -4453530718411300375L;
	protected transient Game game;
	
	public Screen(Game game) {
		this.game = game;
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "Screen.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "Screen.readObject I");
			game = Alite.get();
			AliteLog.e("readObject", "Screen.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class Not Found", e.getMessage(), e);
		}
	}
	
 	public abstract void update(float deltaTime);
	public abstract void present(float deltaTime);
	public abstract void postPresent(float deltaTime);
	public abstract void renderNavigationBar();
	public abstract void postNavigationRender(float deltaTime);
	public abstract void pause();
	public abstract void resume();
	public abstract void dispose();
	public abstract void loadAssets();
	public abstract void postLayout(Object dataObject);	 
	public abstract void activate();
	public abstract void postScreenChange();
	public abstract int  getScreenCode();
		
	public void processDPad(int direction) {		
	}
	
	public void processNavigationJoystick(float z, float rz) {	
	}
	
	public void processNavigationButtonDown(int button) {		
	}
	
	public void processNavigationButtonUp(int button) {		
	}
	
	public void processJoystick(float x, float y, float z, float rz, float hatX, float hatY) {		
	}
	
	public void processButtonUp(int button) {	
	}
	
	public void processButtonDown(int button) {		
	}
	
	public void saveScreenState(DataOutputStream dos) throws IOException {		
	}
}
