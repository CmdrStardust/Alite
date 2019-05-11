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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class EquipmentScreen extends TradeScreen {	
	private static final String EQUIP_HINT = "(Tap again to equip)";
	private int mountLaserPosition = -1;
	private int selectionIndex = -1;
	private static Pixmap [][] equipment;
	private Equipment equippedEquipment = null;
	private String pendingSelection = null;
	
	private static final String [] paths = {
			"equipment_icons/fuel",
			"equipment_icons/missiles",
			"equipment_icons/large_cargo_bay",
			"equipment_icons/ecm",
			"equipment_icons/pulse_laser",
			"equipment_icons/beam_laser",
			"equipment_icons/fuel_scoop",
			"equipment_icons/escape_capsule",
			"equipment_icons/energy_bomb",
			"equipment_icons/extra_energy_unit",
			"equipment_icons/docking_computer",
			"equipment_icons/galactic_hyperdrive",
			"equipment_icons/mining_laser",
			"equipment_icons/military_laser",
			"equipment_icons/retro_rockets"};
	
	public EquipmentScreen(Game game) {
		super(game, 15);
		loopingAnimation = true;
	}
	
	@Override
	public void activate() {		
		createButtons();
		if (pendingSelection != null) {
			for (Button [] bs: tradeButton) {
				if (bs == null) {
					continue;
				}
				for (Button b: bs) {
					if (b == null || b.getName() == null) {
						continue;
					}
					if (pendingSelection.equals(b.getName())) {
						selection = b;
						b.setSelected(true);
					}
				}
			}
			pendingSelection = null;
		}
	}

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeByte(selection == null ? 0 : selection.getName().length());
		if (selection != null) {
			dos.writeChars(selection.getName());
		}
	}

	public static boolean initialize(Alite alite, final DataInputStream dis) {
		EquipmentScreen es = new EquipmentScreen(alite);
		try {
			byte selectionLength = dis.readByte();
			if (selectionLength > 0) {
				es.pendingSelection = "";
				while (selectionLength > 0) {
					es.pendingSelection += dis.readChar();
					selectionLength--;
				}
			}
		} catch (Exception e) {
			AliteLog.e("Equipment Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(es);
		return true;
	}
		
	@Override
	protected void createButtons() {
		SystemData currentSystem = ((Alite) game).getPlayer().getCurrentSystem();
		int techLevel = currentSystem == null ? 1 : currentSystem.getTechLevel();
		tradeButton = new Button[COLUMNS][ROWS];
		int counter = 0;
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (equipment[counter].length > 1) {
					tradeButton[x][y] = new Button(x * GAP_X + X_OFFSET, y * GAP_Y + Y_OFFSET, SIZE, SIZE, equipment[counter]);
				} else {
					tradeButton[x][y] = new Button(x * GAP_X + X_OFFSET, y * GAP_Y + Y_OFFSET, SIZE, SIZE, equipment[counter][0]);
				}
				tradeButton[x][y].setName(paths[counter]);
				tradeButton[x][y].setUseBorder(false);
				counter++;
				if (techLevel < 10 && (counter - techLevel) > 1) {
					// Only show equipment items that are available on worlds
					// with the given tech level.
					return;
				}
			}
		}		
	}
	
	@Override
	protected String getCost(int row, int column) {
		Equipment equipment = ((Alite) game).getCobra().getEquipment(row * COLUMNS + column);
		int price = equipment.getCost();
		String equipmentPrice = String.format(Locale.getDefault(), "%d Cr", price / 10); // No decimals for equipment other than fuel...
		if (price == -1) { // variable price for fuel
			SystemData currentSystem = ((Alite) game).getPlayer().getCurrentSystem();
			price = currentSystem == null ? 10 : currentSystem.getFuelPrice();
			equipmentPrice = String.format(Locale.getDefault(), "%d.%d Cr", price / 10, price % 10);
		}
		return equipmentPrice;
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayTitle("Equip Ship");
		
		presentTradeGoods(deltaTime);
	}

	public void clearSelection() {
		selection = null;
		equippedEquipment = null;
	}
	
	@Override
	protected void presentSelection(int row, int column) {
		Equipment equipment = ((Alite) game).getCobra().getEquipment(row * COLUMNS + column);
		game.getGraphics().drawText(equipment.getName() + " " + EQUIP_HINT, X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
	}
	
	void setLaserPosition(int laserPosition) {
		this.mountLaserPosition = laserPosition;
	}
	
	public Equipment getSelectedEquipment() {
		if (selection == null) {
			return null;
		}
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (tradeButton[x][y] == null) {
					continue;
				}
				if (selection == tradeButton[x][y]) {
					return ((Alite) game).getCobra().getEquipment(y * COLUMNS + x);
				}				
			}
		}
		return null;

	}
	
	private int getNewLaserLocation(Laser laser, int row, int column) {
		Player player = ((Alite) game).getPlayer();
		PlayerCobra cobra = player.getCobra();		
		boolean front = cobra.getLaser(PlayerCobra.DIR_FRONT) != laser;
		boolean right = cobra.getLaser(PlayerCobra.DIR_RIGHT) != laser;
		boolean rear  = cobra.getLaser(PlayerCobra.DIR_REAR)  != laser;
		boolean left  = cobra.getLaser(PlayerCobra.DIR_LEFT)  != laser;
		int freeSlots = (front ? 1 : 0) + (right ? 1 : 0) + (rear ? 1 : 0) + (left ? 1 : 0);
		
		if (freeSlots == 0) {
			return -1;
		}
		
		newScreen = new LaserPositionSelectionScreen(this, game, front, right, rear, left, row, column);
    	return 0;
	}
	
	private void performAutoSave() {
		try {
			((Alite) game).getFileUtils().autoSave((Alite) game);
		} catch (IOException e) {
			AliteLog.e("Auto saving failed", e.getMessage(), e);
		}			
	}
	
    @Override
	protected void performTrade(int row, int column) {
    	Equipment equipment = ((Alite) game).getCobra().getEquipment(row * COLUMNS + column);
		Player player = ((Alite) game).getPlayer();
    	for (Mission mission: player.getActiveMissions()) {
    		if (mission.performTrade(this, equipment)) {
    			return;
    		}
    	}
		PlayerCobra cobra = player.getCobra();
		int price = equipment.getCost();
		int where = -1;
		if (equipment instanceof Laser) {
			if (mountLaserPosition == -1) {
				int result = getNewLaserLocation((Laser) equipment, row, column); 
				if (result == -1) {
					setMessage("All lasers (of that type) already mounted.");
					SoundManager.play(Assets.error);
				}
				return;
			} else if (mountLaserPosition == -2) { 
			    // Do nothing: User canceled.
				mountLaserPosition = -1;
				return;
			} else {
				where = mountLaserPosition;
				mountLaserPosition = -1;
				price = cobra.getLaser(where) == null ? equipment.getCost() : equipment.getCost() - cobra.getLaser(where).getCost();
			}
		}
		if (player.getCash() < price) {
			setMessage("Sorry - you don't have enough Credits.");
			SoundManager.play(Assets.error);
			return;
		}
		if (equipment.getCost() == -1) {
			// Fuel
			if (cobra.getFuel() == PlayerCobra.MAXIMUM_FUEL) {
				setMessage(String.format("Fuel system already full (%d.%d light years).", PlayerCobra.MAXIMUM_FUEL / 10, PlayerCobra.MAXIMUM_FUEL % 10));
				SoundManager.play(Assets.error);
				return;
			}
		}
		if (!(equipment instanceof Laser) && cobra.isEquipmentInstalled(equipment)) {
			setMessage("Only 1 " + equipment.getShortName() + " allowed.");
			SoundManager.play(Assets.error);
			return;
		}
		if (cobra.isEquipmentInstalled(EquipmentStore.navalEnergyUnit) && equipment.equals(EquipmentStore.extraEnergyUnit)) {
			setMessage("Only 1 " + equipment.getShortName() + " allowed.");
			SoundManager.play(Assets.error);
			return;			
		}
		
		if (equipment instanceof Laser) {
			if (where != -1) {
				player.setCash(player.getCash() - price);
				cobra.setLaser(where, (Laser) equipment);
				if (selectionIndex != -1) {
					disposeEquipmentAnimation(selectionIndex);
					selectionIndex = -1;
				}
				selection = null;
				cashLeft = String.format("Cash left: %d.%d Cr", player.getCash() / 10, player.getCash() % 10);

				SoundManager.play(Assets.kaChing);
	    		try {
					((Alite) game).getFileUtils().autoSave((Alite) game);
				} catch (IOException e) {
					AliteLog.e("Auto saving failed", e.getMessage(), e);
				}			
			}
		} else {
			if (equipment.getCost() == -1) {
				// Fuel
				price = player.getCurrentSystem().getFuelPrice();	
				int fuelToBuy = PlayerCobra.MAXIMUM_FUEL - cobra.getFuel();
				int priceToPay = (fuelToBuy * price) / 10;
				if (priceToPay > player.getCash()) {
					setMessage("Sorry - you don't have enough Credits.");
					SoundManager.play(Assets.error);
					return;
				}
				player.setCash(player.getCash() - priceToPay);
				player.getCobra().setFuel(PlayerCobra.MAXIMUM_FUEL);
				if (selectionIndex != -1) {
					disposeEquipmentAnimation(selectionIndex);
					selectionIndex = -1;
				}
				selection = null;
				cashLeft = String.format("Cash left: %d.%d Cr", player.getCash() / 10, player.getCash() % 10);
				SoundManager.play(Assets.kaChing);
				equippedEquipment = EquipmentStore.fuel;
				performAutoSave();
				return;
			} else if (equipment == EquipmentStore.missiles) {
				if (cobra.getMissiles() == PlayerCobra.MAXIMUM_MISSILES) {
					setMessage("Only " + PlayerCobra.MAXIMUM_MISSILES + " missiles allowed.");
					SoundManager.play(Assets.error);
					return;
				}
				player.setCash(player.getCash() - price);
				cobra.setMissiles(cobra.getMissiles() + 1);
				if (selectionIndex != -1) {
					disposeEquipmentAnimation(selectionIndex);
					selectionIndex = -1;
				}
				selection = null;
				cashLeft = String.format("Cash left: %d.%d Cr", player.getCash() / 10, player.getCash() % 10);
				SoundManager.play(Assets.kaChing);
				equippedEquipment = EquipmentStore.missiles;
				performAutoSave();
				return;
			}
			player.setCash(player.getCash() - price);
			cobra.addEquipment(equipment);
			if (equipment == EquipmentStore.retroRockets) {
				cobra.setRetroRocketsUseCount(4 + (int) (Math.random() * 3));
			}
			if (equipment == EquipmentStore.galacticHyperdrive) {
				((Alite) game).setIntergalActive(true);
			}
			SoundManager.play(Assets.kaChing);
			if (selectionIndex != -1) {
				disposeEquipmentAnimation(selectionIndex);
				selectionIndex = -1;
			}
			selection = null;
			cashLeft = String.format("Cash left: %d.%d Cr", player.getCash() / 10, player.getCash() % 10);
			equippedEquipment = equipment;
			performAutoSave();
		}
	}

    public Equipment getEquippedEquipment() {
    	return equippedEquipment;
    }
    
	@Override
	public void processTouch(TouchEvent touch) {
		if (getMessage() != null) {
			super.processTouch(touch);
			return;
		}		
		boolean handled = false;
		if (touch.type == TouchEvent.TOUCH_UP) {
			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLUMNS; x++) {
					if (tradeButton[x][y] == null) {
						continue;
					}
					if (tradeButton[x][y].isTouched(touch.x, touch.y)) {
						if (selection == tradeButton[x][y]) {
							handled = true;
							if (((Alite) game).getCurrentScreen() instanceof FlightScreen) {
								SoundManager.play(Assets.error);
								errorText = "Not Docked.";								
							} else {
								performTrade(y, x);		
								SoundManager.play(Assets.click);
							}
						} else {
							equippedEquipment = null;
							errorText = null;
							int oldSelectionIndex = selectionIndex;
							if (oldSelectionIndex != -1) {
							  disposeEquipmentAnimation(selectionIndex);
							}
							selectionIndex = y * COLUMNS + x;
							if (Settings.animationsEnabled) {
								loadEquipmentAnimation(game.getGraphics(), selectionIndex, paths[selectionIndex]);
								tradeButton[x][y].setAnimation(equipment[selectionIndex]);
							}
							startSelectionTime = System.nanoTime();
							currentFrame = 0;
							selection = tradeButton[x][y];													
							cashLeft = null;
							SoundManager.play(Assets.click);
							handled = true;
						}
					}
				}
			}
		}
		if (!handled) {
			super.processTouch(touch);
		}
	}

	protected void performScreenChange() {
		if (inFlightScreenChange()) {
			return;
		}
		Screen oldScreen = game.getCurrentScreen();
		if (!(newScreen instanceof LaserPositionSelectionScreen)) {
			oldScreen.dispose();
		}
		game.setScreen(newScreen);
		((Alite) game).getNavigationBar().performScreenChange();
		postScreenChange();
	}

    private void loadEquipmentAnimation(final Graphics g, int offset, String path) {
      for (int i = 1; i <= 15; i++) {
        equipment[offset][i] = g.newPixmap(path + "/" + i + ".png", true);
      }    	
    }
    
    private void disposeEquipmentAnimation(int offset) {
    	for (int i = 1; i <= 15; i++) {
    		if (equipment[offset][i] != null) {
    			equipment[offset][i].dispose();
    		}
    		equipment[offset][i] = null;
    	}
    }
    
    private void readEquipmentStill(final Graphics g, int offset, String path) {
		equipment[offset] = new Pixmap[16];
		equipment[offset][0] = g.newPixmap(path + ".png", true);
	}

	private void readEquipment(final Graphics g) {
		equipment = new Pixmap[15][1];
		for (int i = 0; i < 15; i++) {
			readEquipmentStill(g, i, paths[i]);
		}
	}

	@Override
	public void loadAssets() {
		Graphics g = game.getGraphics();

		if (equipment == null) {
			readEquipment(g);	
		}		
		super.loadAssets();
	}	
	
	@Override
	public void dispose() {
		super.dispose();
		if (equipment != null) {
			for (Pixmap [] ps: equipment) {				
				ps[0].dispose();				
			}
			equipment = null;
		}
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
		return ScreenCodes.EQUIP_SCREEN;
	}	
}
