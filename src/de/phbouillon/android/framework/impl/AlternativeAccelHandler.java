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

public class AlternativeAccelHandler implements SensorEventListener, IAccelerometerHandler {
	private float [] accelValues = new float[3];
	private float [] adjustedValues = new float[3];
	private final AndroidGame game;
	private int defaultOrientation;
	private boolean reversedLandscape = false;
	private final DeviceOrientationManager deviceOrientationManager;
	private boolean getUpdates;
	
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

	public AlternativeAccelHandler(AndroidGame game) {
		this.game = game;
		defaultOrientation = getDeviceDefaultOrientation();		
		deviceOrientationManager = new DeviceOrientationManager();
		SensorManager manager = (SensorManager) game.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) {
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
			getUpdates = true;
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

	public void adjustAccelOrientation(float [] eventValues)  { 
		if (defaultOrientation == 0 || defaultOrientation == 2) {
			// Landscape
			adjustedValues[0] = eventValues[0];
			adjustedValues[1] = -eventValues[2];
			adjustedValues[2] = eventValues[1];
		} else {
			// Portrait
			adjustedValues[0] = eventValues[0];
			adjustedValues[1] = eventValues[1];
			adjustedValues[2] = eventValues[2];
		}
	}
		
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!getUpdates) {
			SensorManager manager = (SensorManager) game.getSystemService(Context.SENSOR_SERVICE);
			if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) {
				manager.unregisterListener(this);
			}			
			return;
		}
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				for (int i = 0; i < 3; i++) {
					accelValues[i] = event.values[i];
				}
				break;
		}

		float roll  = (float) Math.atan2(accelValues[0], accelValues[2]);
		float pitch = (float) Math.atan2(accelValues[1], accelValues[2]);

		accelValues[2] = -roll;
		accelValues[1] = -pitch;
		accelValues[0] = 0;
		adjustAccelOrientation(accelValues);
	}
		
	public float getAccelX() {
		return reversedLandscape ? -adjustedValues[0] : adjustedValues[0];
	}

	public float getAccelY() {
		return reversedLandscape ? -adjustedValues[1] : adjustedValues[1];
	}
	
	public float getAccelZ() {
		return reversedLandscape ? -adjustedValues[2] : adjustedValues[2];
	}
	
	public void dispose() {
		if (deviceOrientationManager != null) {
			deviceOrientationManager.disable();
		}
		SensorManager manager = (SensorManager) game.getSystemService(Context.SENSOR_SERVICE);
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0) {
			manager.unregisterListener(this);
		}			
		getUpdates = false;
	}
}
