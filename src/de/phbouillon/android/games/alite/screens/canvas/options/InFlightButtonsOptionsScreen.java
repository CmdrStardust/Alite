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
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class InFlightButtonsOptionsScreen extends AliteScreen {
	private ButtonConfigData [] uiButton = new ButtonConfigData[12];

	private Pixmap torusDriveDockingComputerPixmap;
	private Pixmap hyperspacePixmap;
	private Pixmap galacticHyperspacePixmap;
	private Pixmap statusPixmap;
	private Pixmap ecmPixmap;
	private Pixmap escapeCapsulePixmap;
	private Pixmap energyBombPixmap;
	private Pixmap retroRocketsPixmap;
	private Pixmap ecmJammerPixmap;
	private Pixmap cloakingDevicePixmap;
	private Pixmap missilePixmap;
	private Pixmap firePixmap;
	private Pixmap overlayPixmap;
	
	private Button selectionMode;
	private Button reset;
	private Button back;
	private ButtonConfigData selectedButton = null;
	private ButtonConfigGroup [] buttonGroups = new ButtonConfigGroup[4];
	private boolean groupSelectionMode = true;
	private boolean confirmReset = false;
	
	class ButtonConfigData {
		Button button;
		int groupIndex;
		int settingsPosition;
		String name;
	}
	
	class ButtonConfigGroup {
		ButtonConfigData [] buttons = new ButtonConfigData[3];		
	}
	
	public InFlightButtonsOptionsScreen(Game game) {
		super(game);		
	}
	
	@Override
	public void activate() {
		uiButton[Settings.FIRE] = createButton(Settings.buttonPosition[Settings.FIRE], firePixmap, Settings.FIRE, "Fire Laser");
		uiButton[Settings.MISSILE] = createButton(Settings.buttonPosition[Settings.MISSILE], missilePixmap, Settings.MISSILE, "Fire Missile");
		uiButton[Settings.ECM] = createButton(Settings.buttonPosition[Settings.ECM], ecmPixmap, Settings.ECM, "ECM");
		uiButton[Settings.RETRO_ROCKETS] = createButton(Settings.buttonPosition[Settings.RETRO_ROCKETS], retroRocketsPixmap, Settings.RETRO_ROCKETS, "Retro Rockets");
		uiButton[Settings.ESCAPE_CAPSULE] = createButton(Settings.buttonPosition[Settings.ESCAPE_CAPSULE], escapeCapsulePixmap, Settings.ESCAPE_CAPSULE, "Escape Capsule");
		uiButton[Settings.ENERGY_BOMB] = createButton(Settings.buttonPosition[Settings.ENERGY_BOMB], energyBombPixmap, Settings.ENERGY_BOMB, "Energy Bomb");
		uiButton[Settings.STATUS] = createButton(Settings.buttonPosition[Settings.STATUS], statusPixmap, Settings.STATUS, "Status");
		uiButton[Settings.TORUS] = createButton(Settings.buttonPosition[Settings.TORUS], torusDriveDockingComputerPixmap, Settings.TORUS, "Torus Drive/Docking Computer");
		uiButton[Settings.HYPERSPACE] = createButton(Settings.buttonPosition[Settings.HYPERSPACE], hyperspacePixmap, Settings.HYPERSPACE, "Hyperspace");
		uiButton[Settings.GALACTIC_HYPERSPACE] = createButton(Settings.buttonPosition[Settings.GALACTIC_HYPERSPACE], galacticHyperspacePixmap, Settings.GALACTIC_HYPERSPACE, "Galactic Hyperspace");
	    uiButton[Settings.CLOAKING_DEVICE] = createButton(Settings.buttonPosition[Settings.CLOAKING_DEVICE], cloakingDevicePixmap, Settings.CLOAKING_DEVICE, "Cloaking Device");
	    uiButton[Settings.ECM_JAMMER] = createButton(Settings.buttonPosition[Settings.ECM_JAMMER], ecmJammerPixmap, Settings.ECM_JAMMER, "ECM Jammer");

		selectionMode = new Button(50, 860, 1620, 100, "Selection Mode: " + (groupSelectionMode ? "Group" : "Button"), Assets.titleFont, null);
		selectionMode.setGradient(true);			
		back = new Button(50, 970, 780, 100, "Back", Assets.titleFont, null);
		back.setGradient(true);
		reset = new Button(890, 970, 780, 100, "Reset Positions", Assets.titleFont, null);
		reset.setGradient(true);
	}
		
	private ButtonConfigData createButton(int position, Pixmap pixmap, int settingsPosition, String name) {
		int xt, yt, groupIndex, buttonIndex;
		if (position < 6) {
			xt = (((position % 3) % 2) == 0 ? 0 : 150) + (position < 3 ? 0 : 300);
			groupIndex = position < 3 ? 0 : 1;
		} else {
			xt = (((position % 3) % 2) == 0 ? 1500 : 1350) - (position < 9 ? 0 : 300);
			groupIndex = position < 9 ? 2 : 3;
		}
		yt = (position % 3) * 150 + 200;
		buttonIndex = position % 3;
		
		Button result = new Button(xt, yt, 200, 200, pixmap);
		result.setUseBorder(false);
		
		ButtonConfigData config = new ButtonConfigData();
		config.button = result;
		config.groupIndex = groupIndex;
		config.settingsPosition = settingsPosition;
		config.name = name;
		if (buttonGroups[groupIndex] == null) {
			buttonGroups[groupIndex] = new ButtonConfigGroup();
		}
		
		buttonGroups[groupIndex].buttons[buttonIndex] = config;

		return config;
	}
		
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		
		displayTitle("Button Position Options");
		
		g.drawText("Primary", 20, 150, AliteColors.get().mainText(), Assets.regularFont);
		g.drawText("Secondary", 320, 150, AliteColors.get().mainText(), Assets.regularFont);
		g.drawText("Secondary", 1180, 150, AliteColors.get().mainText(), Assets.regularFont);
		g.drawText("Primary", 1480, 150, AliteColors.get().mainText(), Assets.regularFont);
		String text;
		if (groupSelectionMode) {
			if (selectedButton == null) {
				text = "Select the group of buttons you'd like to change.";
			} else {
				text = "Select the target button group.";
			}
		} else {
			if (selectedButton == null) {
				text = "Select the button you'd like to change.";
			} else {
				text = "Select the target button.";
			}
		}
		centerText(text, 800, Assets.regularFont, AliteColors.get().mainText());
		
		for (ButtonConfigData b: uiButton) {
			b.button.render(g);
			if (b.button.isSelected()) {
				g.drawPixmap(overlayPixmap, b.button.getX(), b.button.getY());
				if (b.name.indexOf("/") == -1) {
					centerText(b.name, b.button.getY() + 100, Assets.regularFont, AliteColors.get().mainText());	
				} else {
					centerText(b.name.substring(0, b.name.indexOf("/")), b.button.getY() + 80, Assets.regularFont, AliteColors.get().mainText());
					centerText(b.name.substring(b.name.indexOf("/") + 1), b.button.getY() + 120, Assets.regularFont, AliteColors.get().mainText());
				}
				
			}
		}

		selectionMode.render(g);
		back.render(g);
		reset.render(g);
	}
	
	
	private void swapSingleButton(ButtonConfigData src, ButtonConfigData target) {
		AliteLog.d("Swapping Buttons", src.name + ", " + target.name + " => " + Settings.buttonPosition[src.settingsPosition] + ", " + Settings.buttonPosition[target.settingsPosition]);
		 
		for (int i = 0; i < 3; i++) {
			if (buttonGroups[src.groupIndex].buttons[i] == src) {
				buttonGroups[src.groupIndex].buttons[i] = null;
			}			
			if (buttonGroups[target.groupIndex].buttons[i] == target) {
				buttonGroups[target.groupIndex].buttons[i] = src;
			}
		}
		for (int i = 0; i < 3; i++) {
			if (buttonGroups[src.groupIndex].buttons[i] == null) {
				buttonGroups[src.groupIndex].buttons[i] = target;
			}
		}
		
		int srcValue = Settings.buttonPosition[src.settingsPosition];
		int tgtValue = Settings.buttonPosition[target.settingsPosition];
		int x = src.button.getX();
		int y = src.button.getY();
		int x2 = target.button.getX();
		int y2 = target.button.getY();
		target.button.move(x, y);
		src.button.move(x2, y2);		
		int srcGrp = src.groupIndex;
		int tgtGrp = target.groupIndex;
		src.groupIndex = tgtGrp;
		target.groupIndex = srcGrp;
		Settings.buttonPosition[src.settingsPosition] = tgtValue;
		Settings.buttonPosition[target.settingsPosition] = srcValue;		
				
		AliteLog.d("Swapped Buttons", src.name + ", " + target.name + " => " + Settings.buttonPosition[src.settingsPosition] + ", " + Settings.buttonPosition[target.settingsPosition]);
	}
	
	private void debug() {
		int count = 1;
		for (ButtonConfigGroup bcg: buttonGroups) {
			AliteLog.d("Button Group " + count, "Button Group " + count);
			int bc = 1;
			if (bcg == null) {
				AliteLog.d("  BCG - " + count + " == null!!", "  BCG - " + count + " == null!!");
			}
			if (bcg != null && bcg.buttons == null) {
				AliteLog.d("  BCG - " + count + ".buttons == null!!", "  BCG - " + count + ".buttons == null!!");
			} 
			if (bcg != null && bcg.buttons != null) {
				for (ButtonConfigData b: bcg.buttons) {
					AliteLog.d("  Button " + bc, "  Button " + bc + " => " + b.name + " => " + b.groupIndex + " => " + b.settingsPosition + " => " + Settings.buttonPosition[b.settingsPosition]);
					bc++;
				}
			}
			count++;
		}
	}
	
	private void swapButtons(ButtonConfigData target) {		
		if (groupSelectionMode) {
			if (target.groupIndex != selectedButton.groupIndex) {
				int srcGI = selectedButton.groupIndex;
				int tGI = target.groupIndex;
				for (int i = 0; i < 3; i++) {
					swapSingleButton(buttonGroups[tGI].buttons[i], buttonGroups[srcGI].buttons[i]);
				}
			}
		} else {
			swapSingleButton(selectedButton, target);
		}
		for (ButtonConfigData b: uiButton) {
			b.button.setSelected(false);
		}
		selectedButton = null;
		debug();
		Settings.save(game.getFileIO());
	}
	
	private void selectGroup(ButtonConfigData b) {
		for (int j = 0; j < 3; j++) {
			buttonGroups[b.groupIndex].buttons[j].button.setSelected(true);
		}
	}
	
	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}		

		if (touch.type == TouchEvent.TOUCH_UP) {
			if (confirmReset && messageResult != 0) {
				confirmReset = false;
				if (messageResult == 1) {
					for (int i = 0; i < 12; i++) {
						Settings.buttonPosition[i] = i;
					}
					activate();
				}
			} else if (back.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new ControlOptionsScreen(game, false);
			} else if (selectionMode.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				groupSelectionMode = !groupSelectionMode;
				selectionMode.setText("Selection Mode: " + (groupSelectionMode ? "Group" : "Button"));
				selectedButton = null;
				for (ButtonConfigData b: uiButton) {
					b.button.setSelected(false);
				}
			} else if (reset.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				setMessage("Are you sure?", MessageType.YESNO);
				confirmReset = true;
			} else {
				for (ButtonConfigData b: uiButton) {					
					if (b.button.isTouched(touch.x, touch.y)) {
						if (selectedButton == null) {
							selectedButton = b;							
							if (groupSelectionMode) {
								selectGroup(b);
							} else {
								b.button.setSelected(true);
							}
						} else {
							swapButtons(b);
						}
						break;
					}
				}
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (torusDriveDockingComputerPixmap != null) {
			torusDriveDockingComputerPixmap.dispose();
			torusDriveDockingComputerPixmap = null;
		}
		if (hyperspacePixmap != null) {
			hyperspacePixmap.dispose();
			hyperspacePixmap = null;
		}
		if (galacticHyperspacePixmap != null) {
			galacticHyperspacePixmap.dispose();
			galacticHyperspacePixmap = null;
		}
		if (statusPixmap != null) {
			statusPixmap.dispose();
			statusPixmap = null;
		}
		if (ecmPixmap != null) {
			ecmPixmap.dispose();
			ecmPixmap = null;
		}
		if (escapeCapsulePixmap != null) {
			escapeCapsulePixmap.dispose();
			escapeCapsulePixmap = null;
		}
		if (energyBombPixmap != null) {
			energyBombPixmap.dispose();
			energyBombPixmap = null;
		}
		if (retroRocketsPixmap != null) {
			retroRocketsPixmap.dispose();
			retroRocketsPixmap = null;
		}
		if (ecmJammerPixmap != null) {
			ecmJammerPixmap.dispose();
			ecmJammerPixmap = null;
		}
		if (cloakingDevicePixmap != null) {
			cloakingDevicePixmap.dispose();
			cloakingDevicePixmap = null;
		}
		if (missilePixmap != null) {
			missilePixmap.dispose();
			missilePixmap = null;
		}
		if (firePixmap != null) {
			firePixmap.dispose();
			firePixmap = null;
		}
		if (overlayPixmap != null) {
			overlayPixmap.dispose();
			overlayPixmap = null;
		}
	}

	@Override
	public void loadAssets() {
		Graphics g = game.getGraphics();
		
		torusDriveDockingComputerPixmap = g.newPixmap("buttons/torus_docking.png", true);
		hyperspacePixmap                = g.newPixmap("buttons/hyperspace.png", true);
		galacticHyperspacePixmap        = g.newPixmap("buttons/gal_hyperspace.png", true);
		statusPixmap                    = g.newPixmap("buttons/status.png", true);
		ecmPixmap                       = g.newPixmap("buttons/ecm.png", true);
		escapeCapsulePixmap             = g.newPixmap("buttons/escape_capsule.png", true);
		energyBombPixmap                = g.newPixmap("buttons/energy_bomb.png", true);
		retroRocketsPixmap              = g.newPixmap("buttons/retro_rockets.png", true);
		ecmJammerPixmap                 = g.newPixmap("buttons/ecm_jammer.png", true);
		cloakingDevicePixmap            = g.newPixmap("buttons/cloaking_device.png", true);
		missilePixmap                   = g.newPixmap("buttons/missile.png", true);
		firePixmap                      = g.newPixmap("buttons/fire.png", true);
		overlayPixmap                   = g.newPixmap("buttons/overlay.png", true);

		super.loadAssets();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.INFLIGHT_BUTTONS_OPTIONS_SCREEN;
	}		
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeBoolean(groupSelectionMode);
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		InFlightButtonsOptionsScreen ifbos = new InFlightButtonsOptionsScreen(alite);
		try {
			ifbos.groupSelectionMode = dis.readBoolean();
		} catch (Exception e) {
			AliteLog.e("Inflight Button Configuration Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(ifbos);
		return true;		
	}		
}
