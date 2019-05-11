package de.phbouillon.android.games.alite.screens.canvas;

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

import android.annotation.SuppressLint;
import android.os.Build;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.AliteStartManager;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class QuitScreen extends AliteScreen {	
	private Screen callingScreen;
	private Screen mockStatusScreen;
	
	public QuitScreen(Game game, FlightScreen flightScreen) {
		super(game);
		mockStatusScreen = new StatusScreen(game);
		mockStatusScreen.loadAssets();
		if (flightScreen != null) {		  
		  this.callingScreen = flightScreen;		  
		} else {
		  this.callingScreen = mockStatusScreen;
		}
	}
		
	@Override
	public void activate() {
	  mockStatusScreen.activate();
		setUpForDisplay(((AndroidGraphics) game.getGraphics()).getVisibleArea());
	}
	
	@Override
	public void update(float deltaTime) {
    super.updateWithoutNavigation(deltaTime);
	  if (getMessage() == null) {
      setMessage("Do you really want to quit Alite?", MessageType.YESNO, Assets.regularFont);
      messageIsModal = true;
	  }
	}
	
	@SuppressLint("NewApi")
	private Screen handleMessage() {
		if (messageResult != 0) {
			if (messageResult == 1) {
				try {
					AliteLog.d("[ALITE]", "Performing autosave. [Quit]");
					((Alite) game).getFileUtils().autoSave(((Alite) game));
				} catch (Exception e) {
					AliteLog.e("[ALITE]", "Autosaving commander failed.", e);
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					((Alite) game).finishAffinity();
				} else {
					((Alite) game).setResult(AliteStartManager.ALITE_RESULT_CLOSE_ALL);
					((Alite) game).finish();
				}
			} else {
				return callingScreen;
			}
		}		
		return null;
	}
	
	@Override 
	public void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		newScreen = handleMessage();
	}
	
	@Override
	public void processButtonUp(int button) {
		super.processButtonUp(button);
		newControlScreen = handleMessage();
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		if (callingScreen != null && !(callingScreen instanceof FlightScreen)) {
		  callingScreen.present(deltaTime);
		} else if (callingScreen instanceof FlightScreen) {
		  mockStatusScreen.present(deltaTime);
		}
	}

	@Override
	public void dispose() {
		if (mockStatusScreen != null) {
		  mockStatusScreen.dispose();
		  mockStatusScreen = null;
		}
    super.dispose();
	}

	@Override
	public void loadAssets() {
	  mockStatusScreen.loadAssets();
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}	
		
	@Override
	public int getScreenCode() {
		return callingScreen == null ? -1 : callingScreen.getScreenCode();
	}	
}
