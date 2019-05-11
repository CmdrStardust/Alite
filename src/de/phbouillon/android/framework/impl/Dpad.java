package de.phbouillon.android.framework.impl;

import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Dpad {
	public final static int NONE = -1;
	public final static int UP = 0;
	public final static int LEFT = 1;
	public final static int RIGHT = 2;
	public final static int DOWN = 3;
	public final static int CENTER = 4;

	int directionPressed = -1; // initialized to -1

	public int getDirectionPressed(InputEvent event) {
		if (!isDpadDevice(event)) {
			return -1;
		}
		directionPressed = NONE;
		
		// If the input event is a MotionEvent, check its hat axis values.
		if (event instanceof MotionEvent) {

			// Use the hat axis value to find the D-pad direction
			MotionEvent motionEvent = (MotionEvent) event;
			float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
			float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);
			boolean nox = false;
			boolean noy = false;
			
			// Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
			// LEFT and RIGHT direction accordingly.
			if (Float.compare(xaxis, -1.0f) == 0) {
				directionPressed = Dpad.LEFT;
			} else if (Float.compare(xaxis, 1.0f) == 0) {
				directionPressed = Dpad.RIGHT;
			} else if (Float.compare(xaxis, 0.0f) == 0) {
				nox = true;  
			}
			// Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
			// UP and DOWN direction accordingly.
			else if (Float.compare(yaxis, -1.0f) == 0) {
				directionPressed = Dpad.UP;
			} else if (Float.compare(yaxis, 1.0f) == 0) {
				directionPressed = Dpad.DOWN;
			} else if (Float.compare(yaxis, 0.0f) == 0) {
				noy = true;
			}			
			
			if (nox && noy) {
				directionPressed = Dpad.NONE;
			}
		}

		// If the input event is a KeyEvent, check its key code.
		else if (event instanceof KeyEvent) {
			// Use the key code to find the D-pad direction.
			KeyEvent keyEvent = (KeyEvent) event;
			if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
				directionPressed = Dpad.LEFT;
			} else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
				directionPressed = Dpad.RIGHT;
			} else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
				directionPressed = Dpad.UP;
			} else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
				directionPressed = Dpad.DOWN;
			} else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
				directionPressed = Dpad.CENTER;
			}
		}
		return directionPressed;
	}

	public static boolean isDpadDevice(InputEvent event) {
		// Check that input comes from a device with directional pads.
		return (event.getSource() & InputDevice.SOURCE_DPAD) != InputDevice.SOURCE_DPAD;
	}
}
