package de.phbouillon.android.games.alite.screens.canvas.tutorial;

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

import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.screens.canvas.EquipmentScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;

//This screen never needs to be serialized, as it is not part of the InGame state,
//also, all used inner classes (IMethodHook, etc.) will be reset upon state loading,
//hence they never need to be serialized, either.
@SuppressWarnings("serial")
public class TutEquipment extends TutorialScreen {
	private StatusScreen status;
	private EquipmentScreen equip;
	private int savedFuel;
	private long savedCash;
	private int screenToInitialize = 0;
	
	public TutEquipment(final Alite alite) {
		super(alite);

		savedFuel = alite.getCobra().getFuel();
		savedCash = alite.getPlayer().getCash();
		
		initLine_00();
		initLine_01();
		initLine_02();
		initLine_03();
		initLine_04();
		initLine_05();
	}
	
	private void initLine_00() {
		addLine(3, "Back so soon? Ok, I'll make this lesson brief and " +
				"simple. Consider it a favor, especially for you.").setY(700);
		
		status = new StatusScreen(alite);
	}
	
	private void initLine_01() {
		final TutorialLine line = 
				addLine(3,  "Go to the Equipment screen now.").setY(700);
						
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen newScreen = (TutEquipment.this).updateNavBar(deltaTime); 
				if (newScreen instanceof EquipmentScreen) {
					status.dispose();
					status = null;
					alite.getNavigationBar().setActiveIndex(5);
					equip = new EquipmentScreen(alite);
					equip.loadAssets();
					equip.activate();					
					line.setFinished();
					currentLineIndex++;
				} else if (newScreen != null) {
					line.setFinished();
				}
			}
		});	
	}

	private void initLine_02() {
		final TutorialLine line = addLine(3, "No, I said \"Equipment\" screen.").setY(700);
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen newScreen = (TutEquipment.this).updateNavBar(deltaTime); 
				if (newScreen instanceof EquipmentScreen) {
					status.dispose();
					status = null;
					alite.getNavigationBar().setActiveIndex(5);
					equip = new EquipmentScreen(alite);
					equip.loadAssets();
					equip.activate();					
					line.setFinished();
				} else if (newScreen != null) {
					line.setFinished();
					currentLineIndex--;
				}
			}
		});
	}

	private void initLine_03() {
		final TutorialLine line = addLine(3, 
				"Yes, here we are. This one works just as the buy screen: " +
				"Tap an item once to get information about the item and " +
				"tap it again to install it on your ship -- provided of " +
				"course you have enough credits. I have drained your " +
				"hyperspace fuel as a training exercise, so go ahead. Buy " +
                "some fuel.").setY(700).addHighlight(makeHighlight(150, 100, 225, 225));
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {				
				for (TouchEvent event : game.getInput().getTouchEvents()) {
					equip.processTouch(event);
				}
				if (equip.getSelectedEquipment() != null && equip.getSelectedEquipment() != EquipmentStore.fuel) {
					line.setFinished();
				} else if (equip.getEquippedEquipment() == EquipmentStore.fuel) {
					line.setFinished();
					currentLineIndex++;
				}
			}
		}).setFinishHook(new IMethodHook() {
			@Override
			public void execute(float deltaTime) {
				equip.clearSelection();
			}
		});
	}

	private void initLine_04() {
		final TutorialLine line = addLine(3, 
				"Fuel, nugget. Fuel! That's the upper left icon. Tap it " +
				"twice. Is it really that hard for you?").setY(700).
					addHighlight(makeHighlight(150, 100, 225, 225));

		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {				
				for (TouchEvent event : game.getInput().getTouchEvents()) {
					equip.processTouch(event);
				}
				if (equip.getSelectedEquipment() != null && equip.getSelectedEquipment() != EquipmentStore.fuel) {
					line.setFinished();
					currentLineIndex--;
					equip.clearSelection();
				} else if (equip.getEquippedEquipment() == EquipmentStore.fuel) {
					line.setFinished();
				}
			}
		});		
	}
	
	private void initLine_05() {
		addLine(3, "Wow. I'm impressed. You have now refueled your ship. " +
				"Well done! You can tap on the other icons if you must, " +
				"but come back tomorrow for another training session. " +
				"Today, I am busy. I need to go to that amazing -- um " +
				"-- shipyard. Now.").setY(700).setPause(5000);
	}
	
	@Override
	public void activate() {
		super.activate();
		switch (screenToInitialize) {
			case 0: status.activate();
			        alite.getNavigationBar().moveToScreen(ScreenCodes.STATUS_SCREEN);
			        break;
			case 1: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.EQUIP_SCREEN);
					equip = new EquipmentScreen(alite);
					equip.loadAssets();
					equip.activate();
					break;			        
		}
		if (currentLineIndex <= 0) {
			alite.getCobra().setFuel(0);
			alite.getPlayer().setCash(1000);
		}
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		TutEquipment te = new TutEquipment(alite);
		try {
			te.currentLineIndex = dis.readInt();
			te.screenToInitialize = dis.readByte();
			te.savedCash = dis.readLong();
			te.savedFuel = dis.readInt();
		} catch (Exception e) {
			AliteLog.e("Tutorial Equipment Screen Initialize", "Error in initializer.", e);
			return false;			
		}
		alite.setScreen(te);
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(currentLineIndex - 1);
		if (status != null) {
			dos.writeByte(0);
		} else if (equip != null) {
			dos.writeByte(1);
		} 		
		dos.writeLong(savedCash);
		dos.writeInt(savedFuel);
	}
	
	@Override
	public void loadAssets() {
		super.loadAssets();
		status.loadAssets();
	}
	
	@Override
	public void doPresent(float deltaTime) {
		if (status != null) {
			status.present(deltaTime);
		} else if (equip != null) {
			equip.present(deltaTime);
		} 
		
		renderText();
	}
		
	@Override
	public void dispose() {
		if (status != null) {
			status.dispose();
			status = null;
		}
		if (equip != null) {
			equip.dispose();
			equip = null;
		}
		alite.getCobra().setFuel(savedFuel);
		alite.getPlayer().setCash(savedCash);
		super.dispose();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.TUT_EQUIPMENT_SCREEN;
	}	
}
