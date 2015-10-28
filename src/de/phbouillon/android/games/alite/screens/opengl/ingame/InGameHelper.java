package de.phbouillon.android.games.alite.screens.opengl.ingame;

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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.LegalStatus;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.statistics.WeaponType;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.ObjectUtils;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AIState;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CargoCanister;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.EscapeCapsule;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Missile;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Platlet;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargoid;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargon;

public class InGameHelper implements Serializable {
	private static final long serialVersionUID = 9018204797190210420L;
	// 1000 * 1000 * 3 is approximately the radius (squared) of a space station; add 1.000.000 (= 1000m) as a safety margin; 
	static final float STATION_PROXIMITY_DISTANCE_SQ = 4000000.0f;  
	static final float STATION_VESSEL_PROXIMITY_DISTANCE_SQ = 8000000.0f;
	private static final float PROXIMITY_WARNING_RADIUS_FACTOR = 18.0f;
	
	private transient Alite alite;
	private final InGameManager inGame;
	private final Vector3f tempVector;
	private float fuelScoopFuel = 0.0f;
	private long lastMissileWarning = -1;
	private long lastFrontWarning = -1;
	private long lastRearWarning = -1;
	private final AttackTraverser attackTraverser;
	private transient ScoopCallback scoopCallback = null;
	
	public InGameHelper(Alite alite, InGameManager inGame) {
		this.alite = alite;
		this.inGame = inGame;
		this.tempVector = new Vector3f(0, 0, 0);
		this.attackTraverser = new AttackTraverser(alite);
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "InGameHelper.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "InGameHelper.readObject I");
			this.alite = Alite.get();
			AliteLog.e("readObject", "InGameHelper.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}
	
	void setScoopCallback(ScoopCallback callback) {
		scoopCallback = callback;
	}
	
	ScoopCallback getScoopCallback() {
		return scoopCallback;
	}

	void damageShip(int amount, boolean front) {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		int shield = front ? alite.getCobra().getFrontShield() : alite.getCobra().getRearShield();
		if (shield == 0) {
			int newVal = alite.getCobra().getEnergy() - amount;
			if (newVal < 0) {
				newVal = 0;
			}
			alite.getCobra().setEnergy(newVal);
		} else {
			int remainingAmount = amount - shield;
			shield -= amount;
			if (shield < 0) {
				shield = 0;
			}
			if (remainingAmount > 0) {
				int newVal = alite.getCobra().getEnergy() - remainingAmount;
				if (newVal < 0) {
					newVal = 0;
				}
				alite.getCobra().setEnergy(newVal);				
			}
			if (front) {
				if (shield <= 0 && (lastFrontWarning == -1 || lastFrontWarning < System.nanoTime())) {
					SoundManager.play(Assets.com_frontShieldHasFailed);
					lastFrontWarning = System.nanoTime() + 4000000000l;
				}
				alite.getCobra().setFrontShield(shield);
			} else {
				if (shield <= 0 && (lastRearWarning == -1 || lastRearWarning < System.nanoTime())) {
					SoundManager.play(Assets.com_aftShieldHasFailed);
					lastRearWarning = System.nanoTime() + 4000000000l;
				}
				alite.getCobra().setRearShield(shield);
			}			
		}
		inGame.getLaserManager().checkEnergyLow();
		if (alite.getCobra().getEnergy() <= 0) {
			inGame.gameOver();
		}
	}
	
	final void ramCargo(SpaceObject rammedObject) {
		rammedObject.applyDamage(4000.0f);
		damageShip(10, true);
		SoundManager.play(Assets.hullDamage);		
	}
	
	void checkShipStationProximity() {
		SpaceObject ship = inGame.getShip();
		SpaceObject station = (SpaceObject) inGame.getStation();
		if (ship == null || station == null) {
			return;
		}
		float distanceSq = ship.getPosition().distanceSq(station.getPosition());
		if (distanceSq <= STATION_PROXIMITY_DISTANCE_SQ) {
			if (ship.getProximity() != station) {
				ship.setProximity(station);
				AliteLog.e("Proximity", "Setting ship/station proximity");
			}
		} else {
			if (ship.getProximity() == station) {
				ship.setProximity(null);
			}
		}
	}
		
	void checkProximity(List <AliteObject> allObjects) {
		int n = allObjects.size();
		if (n < 2) {
			return;
		}			
		for (int i = 0; i < n - 1; i++) {
			if (!(allObjects.get(i) instanceof SpaceObject)) {
				continue;
			}
			SpaceObject objectA = (SpaceObject) allObjects.get(i);
			float objectAProximityDistance = objectA.getBoundingSphereRadiusSq() * PROXIMITY_WARNING_RADIUS_FACTOR;
			float distanceCamSq = objectA.getPosition().distanceSq(inGame.getShip().getPosition());
			if (distanceCamSq <= objectAProximityDistance) {
				objectA.setProximity(inGame.getShip());
			}			
			for (int j = i + 1; j < n; j++) {
				if (!(allObjects.get(j) instanceof SpaceObject)) {
					continue;
				}
				SpaceObject objectB = (SpaceObject) allObjects.get(j);
				float distanceSq = objectA.getPosition().distanceSq(objectB.getPosition());
				float objectBProximityDistance = objectB.getBoundingSphereRadiusSq() * PROXIMITY_WARNING_RADIUS_FACTOR;
				if (distanceSq <= objectAProximityDistance + objectBProximityDistance) {
					objectA.setProximity(objectB);
					objectB.setProximity(objectA);
				}				
				distanceCamSq = objectB.getPosition().distanceSq(inGame.getShip().getPosition());
				if (distanceCamSq <= objectBProximityDistance) {
					objectB.setProximity(inGame.getShip());
				}
				if (objectA instanceof SpaceStation) {
					float intersectionDistance = LaserManager.computeIntersectionDistance(objectB.getForwardVector(), objectB.getPosition(), objectA.getPosition(), 1000.0f, tempVector);
					float travelDistance = -objectB.getSpeed() * 3.0f;
					if (intersectionDistance > 0 && intersectionDistance < travelDistance) {
						objectB.setProximity(objectA);
					}
				}
				if (objectB instanceof SpaceStation) {
					float intersectionDistance = LaserManager.computeIntersectionDistance(objectA.getForwardVector(), objectA.getPosition(), objectB.getPosition(), 1000.0f, tempVector);
					float travelDistance = -objectA.getSpeed() * 3.0f;
					if (intersectionDistance > 0 && intersectionDistance < travelDistance) {
						objectA.setProximity(objectB);
					}
				}
			}
		}		
	}

	final void scoop(SpaceObject cargo) {
		if (!alite.getCobra().isEquipmentInstalled(EquipmentStore.fuelScoop)) {
			ramCargo(cargo);
			if (scoopCallback != null) {
				scoopCallback.rammed(cargo);
			}
			return;			
		}

		// Cargo must be in the lower half of the screen...
		if (cargo.getDisplayMatrix()[13] >= 0) {
			ramCargo(cargo);
			if (scoopCallback != null) {
				scoopCallback.rammed(cargo);
			}
			return;
		}
		if (cargo instanceof CargoCanister && ((CargoCanister) cargo).getEquipment() != null) {
			cargo.applyDamage(4000.0f);
			SoundManager.play(Assets.scooped);
			alite.getCobra().addEquipment(((CargoCanister) cargo).getEquipment());
			inGame.getMessage().setText(((CargoCanister) cargo).getEquipment().getName());
			if (scoopCallback != null) {
				scoopCallback.scooped(cargo);
			}
			return;
		}
		Weight quantity = cargo instanceof CargoCanister ? ((CargoCanister) cargo).getQuantity() : Weight.tonnes((int) (Math.random() * 3 + 1));
		if (alite.getCobra().getFreeCargo().compareTo(quantity) < 0) {
			ramCargo(cargo);
			if (scoopCallback != null) {
				scoopCallback.rammed(cargo);
			}
			inGame.getMessage().setText("Cargo hold is full.");
			return;			
		}
		cargo.applyDamage(4000.0f);
		SoundManager.play(Assets.scooped);
		if (scoopCallback != null) {
			scoopCallback.scooped(cargo);
		}
		alite.getCobra().addTradeGood(
				cargo instanceof CargoCanister ? ((CargoCanister) cargo).getContent() :
				cargo instanceof EscapeCapsule ? TradeGoodStore.get().slaves() :
				cargo instanceof Thargon ? TradeGoodStore.get().alienItems() :
				TradeGoodStore.get().alloys(), quantity);
		if (cargo instanceof CargoCanister) {
			alite.getPlayer().setLegalValue(
					alite.getPlayer().getLegalValue() + (int) (((CargoCanister) cargo).getContent().getLegalityType() * quantity.getQuantityInAppropriateUnit()));					
		} else if (cargo instanceof EscapeCapsule) {
			alite.getPlayer().setLegalValue(
					alite.getPlayer().getLegalValue() + (int) (TradeGoodStore.get().slaves().getLegalityType() * quantity.getQuantityInAppropriateUnit()));								
		}
		inGame.getMessage().setText(cargo instanceof CargoCanister ? ((CargoCanister) cargo).getContent().getName() :
			            cargo instanceof EscapeCapsule ? TradeGoodStore.get().slaves().getName() :
			            cargo instanceof Thargon ? TradeGoodStore.get().alienItems().getName() :
			            TradeGoodStore.get().alloys().getName());
	}	
	
	public void automaticDockingSequence() {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		alite.getPlayer().setCondition(Condition.DOCKED);
		SoundManager.stopAll();
		inGame.getMessage().clearRepetition();
		alite.getNavigationBar().setFlightMode(false);
		inGame.setNewScreen(inGame.getPostDockingScreen());
	}

	final void checkShipObjectCollision(List <AliteObject> allObjects) {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		for (AliteObject object: allObjects) {
			float distanceSq = ObjectUtils.computeDistanceSq(object, inGame.getShip());
			if (object instanceof Thargoid) {
				if (distanceSq <= ((Thargoid) object).getSpawnThargonDistanceSq()) {
					((Thargoid) object).setSpawnThargonDistanceSq(-1);
					ObjectSpawnManager.spawnThargons(alite, (Thargoid) object, inGame);
				}
			}			
			if (distanceSq < 2500) {
				if (object instanceof SpaceObject && ((SpaceObject) object).getType() == ObjectType.SpaceStation && inGame.getWitchSpace() == null) {
					if (inGame.getDockingComputerAI().checkForCorrectDockingAlignment((SpaceObject) object, inGame.getShip())) {
						inGame.clearMissileLock();
						automaticDockingSequence();
					} else {
						SoundManager.play(Assets.hullDamage);
						damageShip(5, ((SpaceObject) object).getDisplayMatrix()[14] < 0);
						inGame.getShip().setSpeed(0.0f);
					}
				} else if (object.getName().equals("Cargo Canister") ||
						   object instanceof Thargon && (((Thargon) object).getMother() == null || ((Thargon) object).getMother().getHullStrength() <= 0) ||
						   object instanceof EscapeCapsule ||
						   object instanceof Platlet) {
					scoop((SpaceObject) object);
				} else if (!(object instanceof Missile)) {		
					if (inGame.getWitchSpace() != null && (object.getName().equals("Planet") ||
					           object.getName().equals("Sun") ||
					           (object instanceof SpaceObject && ((SpaceObject) object).getType() == ObjectType.SpaceStation) ||
					           object.getName().equals("Glow"))) {
						continue;
					}
					SoundManager.play(Assets.hullDamage);
					if (object instanceof SpaceObject && ((SpaceObject) object).getHullStrength() > 0) {
						AliteLog.d("Crash Occurred", object.getName() + " crashed into player. " + ((SpaceObject) object).getCurrentAIStack());
						damageShip(20, ((SpaceObject) object).getDisplayMatrix()[14] < 0);
						((SpaceObject) object).setHullStrength(0);
						((SpaceObject) object).setRemove(true);
						inGame.computeBounty((SpaceObject) object, WeaponType.Collision);
						inGame.explode((SpaceObject) object, true, WeaponType.Collision);
					} 				
				} 
			}
		}		
	}	
	
	void launchEscapeCapsule(SpaceObject source) {
		SoundManager.play(Assets.retroRocketsOrEscapeCapsuleFired);
		EscapeCapsule esc = new EscapeCapsule(alite);
		
		source.getUpVector().copy(tempVector);
		esc.setForwardVector(tempVector);		
		esc.setRightVector(source.getRightVector());
		source.getForwardVector().copy(tempVector);
		tempVector.negate();
		esc.setUpVector(tempVector);
		
		source.getPosition().copy(tempVector);
		esc.setPosition(tempVector);
		esc.setSpeed(-esc.getMaxSpeed());
		inGame.addObject(esc);
	}
	
	Missile spawnMissile(SpaceObject source, SpaceObject target) {	
		Vector3f shipPos = source.getPosition();
		if (inGame.getViewDirection() == PlayerCobra.DIR_FRONT || source != inGame.getShip()) {
			source.getForwardVector().copy(tempVector);
		} else if (inGame.getViewDirection() == PlayerCobra.DIR_RIGHT) {
			source.getRightVector().copy(tempVector);
			tempVector.negate();
		} else if (inGame.getViewDirection() == PlayerCobra.DIR_REAR) {
			source.getForwardVector().copy(tempVector);
			tempVector.negate();
		} else if (inGame.getViewDirection() == PlayerCobra.DIR_LEFT) {
			source.getRightVector().copy(tempVector);
		}
		
		float x = shipPos.x + tempVector.x * -10f;
		float y = shipPos.y + tempVector.y * -10f;
		float z = shipPos.z + tempVector.z * -10f;
		Missile missile = new Missile(alite);
		missile.setPosition(x, y, z);
		missile.orientTowards(x + tempVector.x * -1000.0f,
							  y + tempVector.y * -1000.0f,
							  z + tempVector.z * -1000.0f, source.getUpVector().x, source.getUpVector().y, source.getUpVector().z);
		missile.setSpeed(-missile.getMaxSpeed());
		missile.setVisible(true);
		missile.setTarget(target);
		missile.setSource(source);
		if (source == inGame.getShip()) {
			// Check if target has ECM; then the missile's fate will be
			// decided here: If the player is close enough, he'll have a small
			// chance to get through (10%).
			if (target.hasEcm()) {
				// < 1000m (1000 * 1000 = 1000000)
				if (target.getPosition().distanceSq(source.getPosition()) < 1000000) {
					if (Math.random() > 0.1) {
						missile.setWillBeDestroyedByECM(true);
					}
				} else {
					missile.setWillBeDestroyedByECM(true);
				}				
			}
			if (target.getType() == ObjectType.SpaceStation ||
			    target.getType() == ObjectType.Shuttle ||
			    target.getType() == ObjectType.Trader) {
				alite.getPlayer().setLegalValue(alite.getPlayer().getLegalValue() + 32);
			}
		}
		missile.setAIState(AIState.MISSILE_TRACK, target);
		inGame.addObject(missile);
		return missile;
	}

	void handleMissileUpdate(Missile missile, float deltaTime) {
		missile.moveForward(deltaTime);
		// And track target object...
		if (missile.getTarget() == inGame.getShip() && (lastMissileWarning == -1 || ((System.nanoTime() - lastMissileWarning) > 2000000000l))) {
			lastMissileWarning = System.nanoTime();
			SoundManager.play(Assets.com_incomingMissile);
		}
		if (missile.getHullStrength() > 0) {
			if (missile.getTarget() == null || missile.getTarget().mustBeRemoved() || missile.getTarget().getHullStrength() <= 0) {
				inGame.setMessage("Target lost");
				missile.setHullStrength(0);
				inGame.explode(missile, true, WeaponType.PulseLaser); 						
			} else {
				missile.update(deltaTime);
				if (missile.getWillBeDestroyedByECM() && missile.getPosition().distanceSq(missile.getTarget().getPosition()) < 40000 && !inGame.isECMJammer()) {
					missile.setHullStrength(0);							
					inGame.explode(missile, true, WeaponType.ECM);
					SoundManager.play(Assets.ecm);
					if (inGame.getHud() != null) {
						inGame.getHud().showECM(6000);
					}
				} else {
					float distance = LaserManager.computeIntersectionDistance(missile.getForwardVector(), missile.getPosition(), missile.getTarget().getPosition(), missile.getTarget().getBoundingSphereRadius(), tempVector);
					if (distance > 0 && (distance < -missile.getSpeed() * deltaTime || distance < 4000)) {
						missile.setHullStrength(0);							
						inGame.explode(missile, true, WeaponType.SelfDestruct);
						if (missile.getWillBeDestroyedByECM() && !inGame.isECMJammer()) {
							SoundManager.play(Assets.ecm);
							if (inGame.getHud() != null) {
								inGame.getHud().showECM(6000);
							}
						} else {
							if (missile.getTarget() == inGame.getShip()) {
								SoundManager.play(Assets.hullDamage);
								damageShip(40, missile.getDisplayMatrix()[14] < 0);
							} else {
								missile.getTarget().setHullStrength(0);
								missile.getTarget().setRemove(true);
								inGame.computeBounty(missile.getTarget(), WeaponType.Missile);
								inGame.explode(missile.getTarget(), true, WeaponType.Missile);
							}
						}
					}
				}
			}			
		}		
	}
	
	void checkAltitudeLowAlert() {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		float altitude = alite.getCobra().getAltitude();
		if (altitude < 6 && !SoundManager.isPlaying(Assets.altitudeLow)) {
			SoundManager.repeat(Assets.altitudeLow);
			inGame.getMessage().repeatText("Altitude Low", 1000000000l);
		} else if (altitude >= 6 && SoundManager.isPlaying(Assets.altitudeLow)) {
			SoundManager.stop(Assets.altitudeLow);
			inGame.getMessage().clearRepetition();
		}				
		if (altitude < 2) {
			inGame.gameOver();
		}
	}
	
	void checkCabinTemperatureAlert(float deltaTime) {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		int cabinTemperature = alite.getCobra().getCabinTemperature();
		if (cabinTemperature > 24 && !SoundManager.isPlaying(Assets.temperatureHigh)) {
			fuelScoopFuel = alite.getCobra().getFuel();
			SoundManager.repeat(Assets.temperatureHigh);
			inGame.getMessage().repeatText("Temperature Level Critical", 1000000000l);
		} else if (cabinTemperature <= 24 && SoundManager.isPlaying(Assets.temperatureHigh)) {			
			SoundManager.stop(Assets.temperatureHigh);
			inGame.getMessage().clearRepetition();
		}
		if (cabinTemperature > 26 && alite.getCobra().isEquipmentInstalled(EquipmentStore.fuelScoop) && alite.getCobra().getFuel() < PlayerCobra.MAXIMUM_FUEL) {
			inGame.getMessage().repeatText("Fuel Scoop Activated", 1000000000l);
			fuelScoopFuel += 20 * -inGame.getShip().getSpeed() / inGame.getShip().getMaxSpeed() * deltaTime;
			int newFuel = (int) fuelScoopFuel;
			if (newFuel > PlayerCobra.MAX_FUEL) {
				newFuel = (int) PlayerCobra.MAX_FUEL;
			}
			alite.getCobra().setFuel(newFuel);
			if (newFuel >= PlayerCobra.MAX_FUEL) {
				inGame.getMessage().repeatText("Temperature Level Critical", 1000000000l);
			}
		}
		if (cabinTemperature > 28) {
			inGame.gameOver();
		}
	}
	
	void updatePlayerCondition() {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		if (alite.getCobra().getEnergy() < PlayerCobra.MAX_ENERGY_BANK || 
			alite.getCobra().getCabinTemperature() > 24	               ||
			alite.getCobra().getAltitude() < 6                         ||
			inGame.getWitchSpace() != null) {
			alite.getPlayer().setCondition(Condition.RED);
			return;
		}
		if (inGame.isInSafeZone()) {
			if (alite.getPlayer().getLegalStatus() == LegalStatus.CLEAN) {
				alite.getPlayer().setCondition(Condition.GREEN);
			} else {
				alite.getPlayer().setCondition(inGame.getNumberOfObjects(ObjectType.Viper) > 0 ? Condition.RED : Condition.YELLOW);
			}
		} else {
			alite.getPlayer().setCondition(Condition.YELLOW);
			// Can refer to sortedObjectsToDraw here, because we're in update: PRE rendering, so the
			// list has not yet been emptied...
			inGame.traverseObjects(attackTraverser);
		}	
	}
}
