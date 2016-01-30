package de.phbouillon.android.games.alite.model;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;

public class PlayerCobra {
	public static final int   DIR_FRONT = 0;
	public static final int   DIR_RIGHT = 1;
	public static final int   DIR_REAR  = 2;
	public static final int   DIR_LEFT  = 3;
	
	public static final int   EQUIPMENT_ITEMS       = EquipmentStore.ENTRIES - 2;
	public static final int   MAXIMUM_FUEL          = 70;
	public static final int   MAXIMUM_MISSILES      = 4;
	public static final int   DEFAULT_MISSILES      = 3;
	public static final int   SPEED_UP_FACTOR       = 10;       // speed-up factor to use when torus can't be engaged
	public static final float MAX_SPEED             = 367.4f;   // m/s -- about 1320 km/h; just faster than sonic speed on Earth
	public static final float TORUS_SPEED           = 33400.0f; // torus drive speed
	public static final float TORUS_TEST_SPEED      = 10000.0f; // value to use to test if torus is engaged
	public static final float MAX_SHIELD            = 24;
	public static final float MAX_FUEL              = 70;
	public static final float MAX_CABIN_TEMPERATURE = 30;
	public static final float MAX_LASER_TEMPERATURE = 48;
	public static final float MAX_ALTITUDE          = 30;
	public static final int   MAX_ENERGY_BANK       = 24;
	public static final int   MAX_ENERGY            = 96;

	private int fuel;
	private int missiles;	
	private final Laser [] lasers = new Laser [] {EquipmentStore.pulseLaser, null, null, null};
	private final Equipment [] equipment;
	private final boolean [] equipmentInstalled;
	private Weight maxCargoHold = Weight.tonnes(20);	
	private final InventoryItem [] inventory;
	private int retroRocketsUseCount = 0;
	private final Map <String, Weight> specialCargo = new HashMap<String, Weight>();
	
	private int frontShield;
	private int rearShield;
	private int [] energyBank;
	private int laserTemperature = 0;
	private int cabinTemperature = 0;
	private float altitude = MAX_ALTITUDE;
	private float pitch = 0f;
	private float roll = 0f;
	private float speed = 0f;
	private boolean missileLocked = false;
	private boolean missileTargetting = false;
	private final List <Equipment> equipmentList = new ArrayList<Equipment>();
	
	public PlayerCobra() {
		equipment = new Equipment[EQUIPMENT_ITEMS];
		equipmentInstalled = new boolean[EQUIPMENT_ITEMS];
		inventory = new InventoryItem[TradeGoodStore.get().goods().length];
		for (int i = 0; i < inventory.length; i++) {
		  inventory[i] = new InventoryItem();
		}
		fillEquipment();
		fillTradeGoods();
		lasers[0] = EquipmentStore.pulseLaser;
		for (int i = 1; i < 4; i++) {
			lasers[i] = null;
		}
		for (int i = 0; i < equipment.length; i++) {
			equipmentInstalled[i] = false;
		}		
		fuel = MAXIMUM_FUEL;
		missiles = DEFAULT_MISSILES;
		energyBank = new int[4];
		laserTemperature = 0;
		resetEnergy();
	}
	
	private void fillEquipment() {
		equipment[0] = EquipmentStore.largeCargoBay;
		equipment[1] = EquipmentStore.ecmSystem;
		equipment[2] = EquipmentStore.pulseLaser;
		equipment[3] = EquipmentStore.beamLaser;
		equipment[4] = EquipmentStore.fuelScoop;
		equipment[5] = EquipmentStore.escapeCapsule;
		equipment[6] = EquipmentStore.energyBomb;
		equipment[7] = EquipmentStore.extraEnergyUnit;
		equipment[8] = EquipmentStore.dockingComputer;
		equipment[9] = EquipmentStore.galacticHyperdrive;
		equipment[10] = EquipmentStore.miningLaser;
		equipment[11] = EquipmentStore.militaryLaser;
		equipment[12] = EquipmentStore.retroRockets;
		equipment[13] = EquipmentStore.navalEnergyUnit;
		equipment[14] = EquipmentStore.cloakingDevice;
		equipment[15] = EquipmentStore.ecmJammer;
	}
	
	private void fillTradeGoods() {
		for (int i = 0; i < TradeGoodStore.get().goods().length; i++) {
			inventory[i].clear();
		}
	}
	
	public Laser setLaser(int where, Laser laser) {
		Laser oldLaser = lasers[where];
		lasers[where] = laser;
		return oldLaser;
	}
	
	public Laser getLaser(int where) {
		return lasers[where];
	}
	
	public void addTradeGood(TradeGood good, Weight weight, long price) {
		int ordinal = TradeGoodStore.get().ordinal(good);
		inventory[ordinal].add(weight, price);
	}
	
	public void addUnpunishedTradeGood(TradeGood good, Weight weight) {
		int ordinal = TradeGoodStore.get().ordinal(good);		
		inventory[ordinal].addUnpunished(weight);
	}

	public void subUnpunishedTradeGood(TradeGood good, Weight weight) {
		int ordinal = TradeGoodStore.get().ordinal(good);
		inventory[ordinal].subUnpunished(weight);
	}

	public void setTradeGood(TradeGood good, Weight weight, long price) {
		int ordinal = TradeGoodStore.get().ordinal(good);
		inventory[ordinal].set(weight, price);		
	}
	
	public void setUnpunishedTradeGood(TradeGood good, Weight unpunished) {
		int ordinal = TradeGoodStore.get().ordinal(good);		
		inventory[ordinal].resetUnpunished();
		inventory[ordinal].addUnpunished(unpunished);				
	}
	
	public Weight removeTradeGood(TradeGood good) {
		int ordinal = TradeGoodStore.get().ordinal(good);
		Weight currentWeight = inventory[ordinal].getWeight();
		inventory[ordinal].clear();
		return currentWeight;
	}
	
	public void addEquipment(Equipment equip) {
		for (int i = 0; i < equipment.length; i++) {
			if (equipment[i] == equip) {
				if (equip == EquipmentStore.largeCargoBay) {
					maxCargoHold = Weight.tonnes(35);
				}
				equipmentInstalled[i] = true;
				return;
			}
		}
	}
	
	public void removeEquipment(Equipment equip) {
		for (int i = 0; i < equipment.length; i++) {
			if (equipment[i] == equip) {
				if (equip == EquipmentStore.largeCargoBay) {
					maxCargoHold = Weight.tonnes(20);
				}
				equipmentInstalled[i] = false;
				return;
			}
		}		
	}
	
	public void clearEquipment() {
		for (int i = 0; i < equipment.length; i++) {
			equipmentInstalled[i] = false;
			if (equipment[i] == EquipmentStore.largeCargoBay) {
				maxCargoHold = Weight.tonnes(20);
			}			
		}		
		lasers[0] = EquipmentStore.pulseLaser;
		for (int i = 1; i < 4; i++) {
			lasers[i] = null;
		}
	}
	
	public boolean isEquipmentInstalled(Equipment equip) {
		for (int i = 0; i < equipment.length; i++) {
			if (equipment[i] == equip) {
				return equipmentInstalled[i];
			}
		}
		return false;
	}
	
	public List<Equipment> getInstalledEquipment() {
		equipmentList.clear();
		for (int i = 0; i < equipmentInstalled.length; i++) {
			if (equipmentInstalled[i] && !(equipment[i] instanceof Laser)) {
				equipmentList.add(equipment[i]);
			}
		}
		return equipmentList;
	}
	
	public List<Equipment> getInstalledLosableEquipment() {
		equipmentList.clear();
		for (int i = 0; i < equipmentInstalled.length; i++) {
			if (equipmentInstalled[i] && equipment[i].canBeLost()) {
				equipmentList.add(equipment[i]);
			}
		}
		return equipmentList;
	}

	public Equipment getEquipment(int index) {
		if (index == 0) {
			return EquipmentStore.fuel;
		} else if (index == 1) {
			return EquipmentStore.missiles;
		}
		return equipment[index - 2];
	}
	
	public Weight getFreeCargo() {
		Weight freeCargo = maxCargoHold;
		for (InventoryItem i: inventory) {
			freeCargo = freeCargo.sub(i.getWeight());
		}
		for (Weight w: specialCargo.values()) {
			freeCargo = freeCargo.sub(w);
		}
		return freeCargo;
	}
	
	public InventoryItem [] getInventory() {
		return inventory;
	}
	
	public boolean hasCargo() {
		for (int i = 0; i < TradeGoodStore.get().goods().length; i++) {
			if (inventory[i].getWeight().getWeightInGrams() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasCargo(TradeGood good) {	
		int ordinal = TradeGoodStore.get().ordinal(good);
		if (ordinal == -1) {
			return false;
		}
		return inventory[ordinal].getWeight().getWeightInGrams() > 0;
	}

	public void clearInventory() {
		fillTradeGoods();
	}
	
	public void setInventory(InventoryItem [] data) {
		clearInventory();
		for (int i = 0; i < data.length; i++) {
			setTradeGood(TradeGoodStore.get().goods()[i], data[i].getWeight(), data[i].getPrice());
			setUnpunishedTradeGood(TradeGoodStore.get().goods()[i], data[i].getUnpunished());
		}
	}
	
	public int getMissiles() {
		return missiles;
	}
	
	public void setMissiles(int newMissileCount) {
		missiles = Math.min(MAXIMUM_MISSILES, newMissileCount);
	}
	
	public int getFuel() {
		return fuel;
	}
	
	public void setFuel(int newFuel) {
		if (newFuel < 0) {
			newFuel = 0;
		}
		this.fuel = newFuel;		
	}

	public void resetEnergy() {
		frontShield   = (int) MAX_SHIELD;
		rearShield    = (int) MAX_SHIELD;
		energyBank[0] = MAX_ENERGY_BANK;
		energyBank[1] = MAX_ENERGY_BANK;
		energyBank[2] = MAX_ENERGY_BANK;
		energyBank[3] = MAX_ENERGY_BANK;
	}
	
	public int getFrontShield() {
		return frontShield;
	}
	
	public int getRearShield() {
		return rearShield;
	}
	
	public int getEnergy() {
		return energyBank[0] + energyBank[1] + energyBank[2] + energyBank[3];
	}

	public int getEnergy(int idx) {
		return energyBank[idx];
	}

	public void setFrontShield(int newVal) {
		if (newVal > MAX_SHIELD + Settings.shieldPowerOverride) {
			newVal = (int) MAX_SHIELD + Settings.shieldPowerOverride;
		}
		if (newVal < 0) {
			newVal = 0;
		}
		frontShield = newVal;
	}
	
	public void setRearShield(int newVal) {
		if (newVal > MAX_SHIELD + Settings.shieldPowerOverride) {
			newVal = (int) MAX_SHIELD + Settings.shieldPowerOverride;
		}
		if (newVal < 0) {
			newVal = 0;
		}
		rearShield = newVal;
	}
	
	public void setEnergy(int newVal) {
		if (newVal > MAX_ENERGY) {
			newVal = MAX_ENERGY;
		}
		if (newVal < 0) {
			newVal = 0;
		}
		energyBank[0] = MAX_ENERGY_BANK;
		energyBank[1] = MAX_ENERGY_BANK;
		energyBank[2] = MAX_ENERGY_BANK;
		energyBank[3] = MAX_ENERGY_BANK;
		if (newVal <= 0) {
			energyBank[0] = 0;
			energyBank[1] = 0;
			energyBank[2] = 0;
			energyBank[3] = 0;			
		} else {
			if (newVal > 3 * MAX_ENERGY_BANK) {
				energyBank[0] = newVal - (3 * MAX_ENERGY_BANK);				
			} else if (newVal > 2 * MAX_ENERGY_BANK) {
				energyBank[0] = 0;
				energyBank[1] = newVal - (2 * MAX_ENERGY_BANK);
			} else if (newVal > MAX_ENERGY_BANK) {
				energyBank[0] = 0;
				energyBank[1] = 0;
				energyBank[2] = newVal - MAX_ENERGY_BANK;
			} else {
				energyBank[0] = 0;
				energyBank[1] = 0;
				energyBank[2] = 0; 
				energyBank[3] = newVal;				
			}
		}
	}
	
	public void setLaserTemperature(int temp) {
		if (temp < 0) {
			temp = 0;			
		}
		if (temp > 48) {
			temp = 48;		
		}
		this.laserTemperature = temp;
	}
	
	public int getLaserTemperature() {
		return laserTemperature;
	}

	public int getCabinTemperature() {
		return cabinTemperature;
	}

	public void setCabinTemperature(int temp) {
		cabinTemperature = temp;
	}
	
	public float getAltitude() {
		return altitude;
	}

	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}
	
	public void setRotation(float pitch, float roll) {
		this.pitch = pitch;
		this.roll = roll;
	}
	
	public boolean isMissileLocked() {
		return missileLocked;
	}
	
	public void setMissileLocked(boolean b) {
		if (b) {
			missileTargetting = false;
		}
		missileLocked = b;
	}
	
	public boolean isMissileTargetting() {
		return missileTargetting;
	}
	
	public void setMissileTargetting(boolean b) {
		if (b) {
			missileLocked = false;
		}
		missileTargetting = b;
	}

	public int getRetroRocketsUseCount() {
		return retroRocketsUseCount;
	}
	
	public void setRetroRocketsUseCount(int newCount) {
		retroRocketsUseCount = newCount;
		if (newCount == 0) {
			removeEquipment(EquipmentStore.retroRockets);
		}
	}
	
	public boolean containsSpecialCargo() {
		return !specialCargo.isEmpty();
	}
	
	public Weight getSpecialCargo(String name) {
		return specialCargo.get(name);
	}
	
	public void clearSpecialCargo() {
		specialCargo.clear();
	}
	
	public void removeSpecialCargo(String name) {
		specialCargo.remove(name);
	}
	
	public void addSpecialCargo(String name, Weight weight) {
		specialCargo.put(name, weight);
	}

	public void reset() {
		clearInventory();
		clearSpecialCargo();
		clearEquipment();
	}
}
