package de.phbouillon.android.framework.impl;

import de.phbouillon.android.framework.Screen;
import android.opengl.GLSurfaceView;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class AndroidView extends GLSurfaceView {
	private final Dpad dpad;
	private final AndroidGame game;
	
	public AndroidView(AndroidGame game) {
		super(game);
		this.game = game;
		dpad = new Dpad();
	}
		
	private boolean isController(int sourceId) {
		return (sourceId & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
			   (sourceId & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD ||
			   (sourceId & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
	}
	
	private static float getCenteredAxis(MotionEvent event,
	        InputDevice device, int axis, int historyPos) {
	    final InputDevice.MotionRange range =
	            device.getMotionRange(axis, event.getSource());

	    // A joystick at rest does not always report an absolute position of
	    // (0,0). Use the getFlat() method to determine the range of values
	    // bounding the joystick axis center.
	    if (range != null) {
	        final float flat = range.getFlat();
	        final float value =
	                historyPos < 0 ? event.getAxisValue(axis):
	                event.getHistoricalAxisValue(axis, historyPos);

	        // Ignore axis values that are within the 'flat' region of the
	        // joystick axis center.
	        if (Math.abs(value) > flat) {
	            return value;
	        }
	    }
	    return 0;
	}

	private void processJoystickInput(Screen screen, MotionEvent event, int historyPos) {
		if (screen == null) {
			return;
		}
	    InputDevice inputDevice = event.getDevice();
	    float z = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z, historyPos);
	    float rz = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ, historyPos);
	    screen.processNavigationJoystick(z, rz);
	    screen.processJoystick(
	    		getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X, historyPos),
	    		getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y, historyPos),
	    		z,
	    		rz,
	    		getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_X, historyPos),
	    		getCenteredAxis(event, inputDevice, MotionEvent.AXIS_HAT_Y, historyPos));
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (game.getCurrentScreen() == null) {
			return true;
		}
		if (isController(event.getSource())) {
			if (Dpad.isDpadDevice(event)) {
				if (game.getCurrentScreen() != null) {
					game.getCurrentScreen().processDPad(dpad.getDirectionPressed(event));
				}
			} 
			// Check that the event came from a game controller
	        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
	                InputDevice.SOURCE_JOYSTICK &&
	                event.getAction() == MotionEvent.ACTION_MOVE) {

	            // Process all historical movement samples in the batch
	            final int historySize = event.getHistorySize();

	            // Process the movements starting from the
	            // earliest historical position in the batch
	            for (int i = 0; i < historySize; i++) {
	                // Process the event at historical position i
	                processJoystickInput(game.getCurrentScreen(), event, i);
	            }

	            // Process the current movement sample in the batch (position -1)
	            processJoystickInput(game.getCurrentScreen(), event, -1);
	        }
		}
		return super.onGenericMotionEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isController(event.getSource()) && game.getCurrentScreen() != null) {
			game.getCurrentScreen().processButtonDown(keyCode);
			game.getCurrentScreen().processNavigationButtonDown(keyCode);
		}		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (isController(event.getSource()) && game.getCurrentScreen() != null) {
			game.getCurrentScreen().processButtonUp(keyCode);
			game.getCurrentScreen().processNavigationButtonUp(keyCode);
		}		
		return super.onKeyUp(keyCode, event);
	}
}
