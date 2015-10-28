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

import java.util.Vector;

import android.os.Build.VERSION;
import android.view.View;
import de.phbouillon.android.framework.Input;

public class AndroidInput implements Input {
	private final AccelerometerHandler accelHandler;
	private final GyroscopeHandler gyroHandler;
	private final TouchHandler touchHandler;
		
	public AndroidInput(AndroidGame game, View view, float scaleX, float scaleY, int offsetX, int offsetY) {
		accelHandler = new AccelerometerHandler(game);
		gyroHandler = new GyroscopeHandler(game);
		int sdkVersion = VERSION.SDK_INT;		
		touchHandler = sdkVersion < 5 ? 
				new SingleTouchHandler(view, scaleX, scaleY, offsetX, offsetY) :
				new MultiTouchHandler(view, scaleX, scaleY, offsetX, offsetY);			
	}
	
	public void setView(View view) {
		if (touchHandler instanceof SingleTouchHandler) {
			((SingleTouchHandler) touchHandler).setView(view);
		} else {
			((MultiTouchHandler) touchHandler).setView(view);
		}
	}
	
	@Override
	public boolean isTouchDown(int pointer) {
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer) {
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer) {
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public float getAccelX() {
		return accelHandler.getAccelX();
	}

	@Override
	public float getAccelY() {
		return accelHandler.getAccelY();
	}

	@Override
	public float getAccelZ() {
		return accelHandler.getAccelZ();
	}

	@Override
	public float getGyroPitch() {
		return gyroHandler.getPitch();
	}

	@Override
	public float getGyroRoll() {
		return gyroHandler.getRoll();
	}

	@Override
	public Vector<TouchEvent> getTouchEvents() {
		return touchHandler.getTouchEvents();
	}

	@Override
	public Vector<TouchEvent> getAndRetainTouchEvents() {
		return touchHandler.getAndRetainTouchEvents();
	}

	@Override
	public int getTouchCount() {
		return touchHandler.getTouchCount();
	}	
	
	@Override
	public void setZoomFactor(float factor) {
		touchHandler.setZoomFactor(factor);
	}
		
	@Override
	public void dispose() {
		accelHandler.dispose();
	}
}
