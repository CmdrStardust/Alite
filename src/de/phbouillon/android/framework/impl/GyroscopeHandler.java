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

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import de.phbouillon.android.games.alite.Settings;

public class GyroscopeHandler implements SensorEventListener {
	private float [] values = new float[3];
	private float [] adjustedValues = new float[3];
	private final AndroidGame game;
	private int defaultOrientation;
	private boolean reversedLandscape = false;
	private final DeviceOrientationManager deviceOrientationManager;
	private final float filterThreshold = 0.01f; // Filter the sensor noise
	
	private class DeviceOrientationManager extends OrientationEventListener {
		public DeviceOrientationManager() {
			super(game, SensorManager.SENSOR_DELAY_GAME);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			switch (Settings.lockScreen) {
				case 0: break; // Need to calculate the orientation
				case 1: reversedLandscape = false; return;
				case 2: reversedLandscape = true; return;
			}				

			if (orientation == -1) {
				return;
			}
			if (defaultOrientation == -1) {
				defaultOrientation = getDeviceDefaultOrientation();
			}
			if (defaultOrientation == 0) {
				// Landscape
				if (reversedLandscape) {
					reversedLandscape = orientation > 330 || orientation < 30;
				} else {
					reversedLandscape = orientation > 150 && orientation < 210;
				}
			} else if (defaultOrientation == 1) {
				// Regular Phone
				if (reversedLandscape) {
					reversedLandscape = orientation < 240 || orientation > 300;
				} else {
					reversedLandscape = orientation > 60 && orientation < 120;
				}					
			} else if (defaultOrientation == 2) {
				// Reversed Landscape
				if (reversedLandscape) {
					reversedLandscape = orientation > 150 && orientation < 210;
				} else {
					reversedLandscape = orientation > 330 || orientation < 30;
				}
			} else if (defaultOrientation == 3) {
				// Upside Down Phone
				if (reversedLandscape) {
					reversedLandscape = orientation > 60 && orientation < 120;
				} else {
					reversedLandscape = orientation < 240 || orientation > 300;
				}
			}
		}
	};

	public GyroscopeHandler(AndroidGame game) {
		this.game = game;
		defaultOrientation = getDeviceDefaultOrientation();
		deviceOrientationManager = new DeviceOrientationManager();
		SensorManager manager = (SensorManager) game.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0) {
			Sensor gyroscope = manager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);
			manager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
		} 
		deviceOrientationManager.enable();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing to do		
	}

	private int getDeviceDefaultOrientation() {
		return ((WindowManager) game.getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
	}

	public void adjustOrientation()  { 
		if (reversedLandscape) {
			adjustedValues[0] = -values[0];
			adjustedValues[1] = -values[1];
			adjustedValues[2] = values[2];
		} else {
			adjustedValues[0] = values[0];
			adjustedValues[1] = values[1];
			adjustedValues[2] = values[2];
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
			case Sensor.TYPE_GYROSCOPE:
				for (int i = 0; i < 3; i++) {
					if (Math.abs(event.values[i]) > filterThreshold) {
						values[i] += event.values[i];
					}
				}
				break;
		}
		adjustOrientation();
	}

	public float getPitch() {
		values[1] = 0.0f;
		return -adjustedValues[1];
	}

	public float getRoll() {
		values[2] = 0.0f;
		values[0] = 0.0f;
		return adjustedValues[2] + adjustedValues[0];
	}

	public void dispose() {
		deviceOrientationManager.disable();
	}
}
