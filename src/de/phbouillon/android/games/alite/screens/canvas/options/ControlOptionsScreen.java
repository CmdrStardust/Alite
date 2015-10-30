package de.phbouillon.android.games.alite.screens.canvas.options;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.ShipControl;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutIntroduction;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class ControlOptionsScreen extends OptionsScreen {
	private Button shipControlMode;
	private Button controlDisplaySide;
	private Button controlUseGyroscope;
	private Button keyboardLayout;
	private Button dockingSpeed;
	private Button laserAutoFire;
	private Button radarTapZoom;
	private Button autoId;
    private Button buttonPositionOptions;
    
	private Button back;
	private boolean forwardToIntroduction;
	
	public ControlOptionsScreen(Game game, boolean forwardToIntroduction) {
		super(game);
		this.forwardToIntroduction = forwardToIntroduction;
		((Alite) game).getNavigationBar().moveToScreen(getScreenCode());
	}
	
	@Override
	public void activate() {
		shipControlMode       = createButton(0, "Ship Control: " + Settings.controlMode.getDescription());
		controlDisplaySide    = createSmallButton(1, true, computeControlDisplaySideText());
		controlDisplaySide.setVisible(Settings.controlMode != ShipControl.ACCELEROMETER);
		controlUseGyroscope   = createSmallButton(1, false, "Gyroscope: " + (Settings.gyroscopeEnabled ? "On" : "Off"));
		controlUseGyroscope.setVisible(Settings.controlMode != ShipControl.ACCELEROMETER);
		
		autoId                = createSmallButton(2, true, "Auto Id: " + (Settings.autoId ? "On" : "Off"));
		buttonPositionOptions = createSmallButton(2, false, "Configure Button Positions");
		buttonPositionOptions.setVisible(!forwardToIntroduction);
		dockingSpeed          = createSmallButton(3, true, "Docking Computer: " + (Settings.dockingComputerFast ? "Fast" : "Slow"));
		keyboardLayout        = createSmallButton(3, false, "Keyboard: " + Settings.keyboardLayout);
		
		laserAutoFire         = createSmallButton(4, true, "Laser: " + (Settings.laserButtonAutofire ? "Auto Fire" : "Single Shot"));
		radarTapZoom          = createSmallButton(4, false, "Change View: " + (Settings.tapRadarToChangeView ? "Tap" : "Slide"));
				
		back                  = createButton(5, forwardToIntroduction ? "Start Training" : "Back");
	}
			
	private String computeControlDisplaySideText() {
		switch (Settings.controlMode) {
			case ACCELEROMETER: return "";
			case CONTROL_PAD: return "Position: " + (Settings.controlPosition == 0 ? "Left" : "Right"); 
			case CURSOR_BLOCK: return "Position: " + (Settings.controlPosition == 0 ? "Left" : "Right");
			case CURSOR_SPLIT_BLOCK: return "Dive/Climb is: " + (Settings.controlPosition == 0 ? "Left" : "Right");
		}
		return "";
	}
	
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		
		displayTitle("Control Options");
		shipControlMode.render(g);
		controlDisplaySide.render(g);
		controlUseGyroscope.render(g);
		autoId.render(g);
		buttonPositionOptions.render(g);
		dockingSpeed.render(g);
		keyboardLayout.render(g);		
		laserAutoFire.render(g);
		radarTapZoom.render(g);

		back.render(g);
		if (forwardToIntroduction) {
			centerText("Please review your Control Settings before we begin your training.",
					115, Assets.regularFont, AliteColors.get().mainText());
		}
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (shipControlMode.isTouched(touch.x, touch.y)) { 
				SoundManager.play(Assets.click);
				int val = Settings.controlMode.ordinal();
				val++;
				if (val >= ShipControl.values().length) {
					val = 0;
				}
				Settings.controlMode = ShipControl.values()[val];
				shipControlMode.setText("Ship Control: " + Settings.controlMode.getDescription());
				controlDisplaySide.setVisible(Settings.controlMode != ShipControl.ACCELEROMETER);
				controlDisplaySide.setText(computeControlDisplaySideText());
				controlUseGyroscope.setVisible(Settings.controlMode != ShipControl.ACCELEROMETER);
				controlUseGyroscope.setText("Gyroscope: " + (Settings.gyroscopeEnabled ? "On" : "Off"));
				Settings.save(game.getFileIO());
			} else if (controlDisplaySide.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.controlPosition = -Settings.controlPosition + 1;
				controlDisplaySide.setText(computeControlDisplaySideText());
				Settings.save(game.getFileIO());
			} else if (controlUseGyroscope.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.gyroscopeEnabled = !Settings.gyroscopeEnabled;
				controlUseGyroscope.setText("Gyroscope: " + (Settings.gyroscopeEnabled ? "On" : "Off"));
				Settings.save(game.getFileIO());
			} else if (autoId.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.autoId = !Settings.autoId;
				autoId.setText("Auto Id: " + (Settings.autoId ? "On" : "Off"));
				Settings.save(game.getFileIO());
			} else if (buttonPositionOptions.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new InFlightButtonsOptionsScreen(game);				
			} else if (back.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = forwardToIntroduction ? new TutIntroduction((Alite) game) : new OptionsScreen(game);
			} else if (keyboardLayout.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				if ("QWERTY".equals(Settings.keyboardLayout)) {
					Settings.keyboardLayout = "QWERTZ";
				} else {
					Settings.keyboardLayout = "QWERTY";
				}
				keyboardLayout.setText("Keyboard: " + Settings.keyboardLayout);
				Settings.save(game.getFileIO());
			} else if (dockingSpeed.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.dockingComputerFast = !Settings.dockingComputerFast;
				dockingSpeed.setText("Docking Computer: " + (Settings.dockingComputerFast ? "Fast" : "Slow"));
				Settings.save(game.getFileIO());
			} else if (laserAutoFire.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.laserButtonAutofire = !Settings.laserButtonAutofire;
				laserAutoFire.setText("Laser: " + (Settings.laserButtonAutofire ? "Auto Fire" : "Single Shot"));
				Settings.save(game.getFileIO());				
			} else if (radarTapZoom.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				Settings.tapRadarToChangeView = !Settings.tapRadarToChangeView;
				radarTapZoom.setText("Change View: " + (Settings.tapRadarToChangeView ? "Tap" : "Slide"));
				Settings.save(game.getFileIO());								
			}
		}
	}	
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.CONTROL_OPTIONS_SCREEN;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeBoolean(forwardToIntroduction);
	}

	public static boolean initialize(Alite alite, DataInputStream dis) {
		boolean forward = false;
		try {
			forward = dis.readBoolean();
		} catch (IOException e) {
			AliteLog.e("Error in initializer", "Error in initializer", e);
		}
		alite.setScreen(new ControlOptionsScreen(alite, forward));
		return true;
	}		
}
