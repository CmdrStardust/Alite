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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.opengl.GLES11;
import android.opengl.Matrix;
import android.os.Vibrator;
import de.phbouillon.android.framework.Updater;
import de.phbouillon.android.framework.impl.Pool;
import de.phbouillon.android.framework.impl.Pool.PoolObjectFactory;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.Rating;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.statistics.WeaponType;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.Explosion;
import de.phbouillon.android.games.alite.screens.opengl.objects.LaserCylinder;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObjectAI;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CargoCanister;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Platlet;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteHud;

public class LaserManager implements Serializable {
	private static final long serialVersionUID = -3608347957484414304L;

	private static final long  NAVAL_REFRESH_RATE    = 898203592l;
	private static final long  NORMAL_REFRESH_RATE   = 1437125748l;
	public  static final float MAX_ENEMY_DISTANCE_SQ = 603979776; // (16384 + 8192) squared

	public  static final int   MAX_LASERS                       = 500;
	public  static final float LASER_SPEED                      = 12400.0f;
	public  static final float LASER_BEAM_SPEED                 = 124000.0f;
	private static final float DIST_FRONT                       = -10.0f;
	private static final float DIST_RIGHT                       = 30.0f;
	private static final float DIST_CONVERGE                    = 24000.0f;
	private static final float CARGO_CANISTER_EJECTION_DISTANCE = 800.0f;

	private transient Alite alite;
	private final Vector3f shotOrigin;
	private final Vector3f shotDirection;
	private final Vector3f laserRight;
	private final Vector3f laserForward;
	private long lastLaserFireUp = 0;
	private long lastFrontWarning = -1;
	private long lastRearWarning = -1;
	private boolean autoFire = false;
	private final Vector3f tempVector = new Vector3f(0, 0, 0);
	private final Vector3f tempVector2 = new Vector3f(0, 0, 0);
	private final float [] tempVecArray = new float [] {0.0f, 0.0f, 0.0f, 0.0f};
	private final float [] tempVecArray2 = new float [] {0.0f, 0.0f, 0.0f, 0.0f};
	private final List <LaserCylinder> createdLasers = new ArrayList<LaserCylinder>();
	private final List <Explosion> activeExplosions = new ArrayList<Explosion>();
	private InGameManager inGame;

	private long lockTime = -1;

	class LaserCylinderFactory implements PoolObjectFactory <LaserCylinder> {
		private static final long serialVersionUID = -4204164060599078689L;

		@Override
		public LaserCylinder createObject() {
			LaserCylinder result = new LaserCylinder();
			result.setVisible(false);
			return result;
		}
	}

	private transient PoolObjectFactory <LaserCylinder> laserFactory = new LaserCylinderFactory();
	private transient Pool <LaserCylinder> laserPool = new Pool<LaserCylinder>(laserFactory, MAX_LASERS);
	final List <LaserCylinder> activeLasers = new ArrayList<LaserCylinder>();

	LaserManager(final Alite alite, final InGameManager inGame) {
		this.alite = alite;
		this.inGame = inGame;
		this.shotOrigin = new Vector3f(0, 0, 0);
		this.shotDirection = new Vector3f(0, 0, 0);
		laserRight = new Vector3f(0.0f, 0.0f, 0.0f);
		laserForward = new Vector3f(0.0f, 0.0f, 0.0f);
		alite.getTextureManager().addTexture("textures/lasers.png");
		alite.setLaserManager(this);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "LaserManager.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "LaserManager.readObject I");
			this.alite     = Alite.get();
			this.laserFactory = new LaserCylinderFactory();
			laserPool = new Pool<LaserCylinder>(laserFactory, MAX_LASERS);
			laserPool.reset();
			this.alite.setLaserManager(this);
			AliteLog.e("readObject", "LaserManager.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	static final float computeIntersectionDistance(final Vector3f dir, final Vector3f origin, final Vector3f center, final float radius, final Vector3f tVec) {
		origin.sub(center, tVec);
		float ocsq  = tVec.lengthSq();
		float loc   = dir.dot(tVec);
		float docsq = loc * loc;
		float expUnderRoot = docsq - ocsq + radius * radius;
		if (expUnderRoot < 0) {
			return -1.0f;
		}
		float sqr = (float) Math.sqrt(expUnderRoot);
		float d = -loc;
		if (d - sqr > 0) {
			return d - sqr;
		} else if (d + sqr > 0) {
			return d + sqr;
		}
		return -1.0f;
	}

	private final void spawnPlatlets(final SpaceObject so, final WeaponType weaponType) {
		int platletCount = Math.random() < 0.9 ? 0 : 1;
		if (weaponType == WeaponType.MiningLaser) {
			platletCount = (int) (Math.random() * (so.getMaxCargoCanisters() + 1));
		}
		for (int i = 0; i < platletCount; i++) {
			final Platlet platlet = new Platlet(alite);
			tumbleObject(so, platlet);
		}
	}

	private void tumbleObject(final SpaceObject explodedObject, final SpaceObject createdObject, Vector3f offset) {
		tempVector.x = (float) (-2.0 + Math.random() * 4.0);
		tempVector.y = (float) (-2.0 + Math.random() * 4.0);
		tempVector.z = (float) (-2.0 + Math.random() * 4.0);
		tempVector.normalize();
		final float ix = tempVector.x;
		final float iy = tempVector.y;
		final float iz = tempVector.z;
		tempVector.x = (float) (-2.0 + Math.random() * 4.0);
		tempVector.y = (float) (-2.0 + Math.random() * 4.0);
		tempVector.z = (float) (-2.0 + Math.random() * 4.0);
		tempVector.normalize();
		final float rx = tempVector.x;
		final float ry = tempVector.y;
		final float rz = tempVector.z;

		createdObject.setSpeed(0.0f);
		final float speed = 0.2f + ((createdObject.getMaxSpeed() - 0.2f) * (float) Math.random());
		float x = explodedObject.getPosition().x + (offset == null ? 0 : offset.x);
		float y = explodedObject.getPosition().y + (offset == null ? 0 : offset.y);
		float z = explodedObject.getPosition().z + (offset == null ? 0 : offset.z);
		createdObject.setPosition(x, y, z);
		createdObject.setUpdater(new Updater() {
			private static final long serialVersionUID = -303661146540057753L;

			@Override
			public void onUpdate(float deltaTime) {
				createdObject.getPosition().copy(tempVector);
				float x = tempVector.x + ix * speed * deltaTime;
				float y = tempVector.y + iy * speed * deltaTime;
				float z = tempVector.z + iz * speed * deltaTime;
				createdObject.setPosition(x, y, z);
				createdObject.applyDeltaRotation(rx, ry, rz);
			}
		});
		inGame.addObject(createdObject);
	}

	private void tumbleObject(final SpaceObject explodedObject, final SpaceObject createdObject) {
		tumbleObject(explodedObject, createdObject, null);
	}

	public void ejectPlayerCargoCanister(final SpaceObject so, TradeGood tradeGood, Weight weight, long price) {
		final CargoCanister cargo = new CargoCanister(alite);
		cargo.setContent(tradeGood, weight);
		cargo.setPrice(price);
		so.getForwardVector().copy(tempVector2);
		tempVector2.scale(CARGO_CANISTER_EJECTION_DISTANCE);
		tumbleObject(so, cargo, tempVector2);
	}

	private final void spawnCargoCanisters(final SpaceObject so, int forceCount, WeaponType weaponType) {
		AliteLog.d("Spawn Cargo Canisters", so.getName() + " has Cargo type: " + so.getCargoType() + " and spawns cargo canisters: " + so.spawnsCargoCanisters());
		if (so.getType() == ObjectType.Asteroid) {
			spawnPlatlets(so, weaponType);
			return;
		}
		if (so.getCargoType() == null || !so.spawnsCargoCanisters()) {
			// Legacy: The cargo type is still declared at a ship, but not longer used...
			// However, if it was defined that a ship has no cargo type, we still
			// exit here...
			return;
		}

		int numberOfCanistersToSpawn = forceCount > 0 ? forceCount : (int) (Math.random() * (so.getMaxCargoCanisters() + 1));
		for (int i = 0; i < numberOfCanistersToSpawn; i++) {
			final CargoCanister cargo = new CargoCanister(alite);
			TradeGood tradeGood = TradeGoodStore.get().getRandomTradeGoodForContainer();
			cargo.setContent(tradeGood, Weight.unit(tradeGood.getUnit(), (int) (Math.random() * 3 + 1)));
			tumbleObject(so, cargo);
		}
	}

	public void explode(SpaceObject so, boolean createCanisters, WeaponType weaponType) {
		SoundManager.play(Assets.shipDestroyed);
		activeExplosions.add(new Explosion(alite, so, inGame));
		if (createCanisters) {
			spawnCargoCanisters(so, so.getCargoCanisterOverrideCount(), weaponType);
		}
	}

	public final boolean isUnderCross(final SpaceObject object, final GraphicObject ship, final int viewDirection) {
		if (object.isCloaked()) {
			return false;
		}
		ship.getPosition().copy(shotOrigin);
		switch (viewDirection) {
			case 0: ship.getForwardVector().copy(shotDirection); break;
			case 1: ship.getRightVector().copy(shotDirection); shotDirection.negate(); break;
			case 2: ship.getForwardVector().copy(shotDirection); shotDirection.negate(); break;
			case 3: ship.getRightVector().copy(shotDirection); break;
		}
		shotDirection.negate();
		shotDirection.normalize();
		float d = computeIntersectionDistance(shotDirection, shotOrigin, object.getPosition(), object.getBoundingSphereRadius(), tempVector);
		if (d > 0.0f && d < AliteHud.MAX_DISTANCE) {
			float distSq = object.getPosition().distanceSq(ship.getPosition());
			float scaleFactor = distSq <= SpaceObjectAI.SHOOT_DISTANCE_SQ ? 1.0f : 1.0f + 5.0f * ((distSq - SpaceObjectAI.SHOOT_DISTANCE_SQ) / MAX_ENEMY_DISTANCE_SQ);
			if (scaleFactor > 3.0f) {
				scaleFactor = 3.0f;
			} else if (scaleFactor < 1.0f) {
				scaleFactor = 1.0f;
			}
			return object.intersect(shotOrigin, shotDirection, scaleFactor);
		}
		return false;
	}

	final void gameOver(final GraphicObject ship) {
		if (inGame.isDockingComputerActive()) {
			inGame.toggleDockingComputer(false);
		}
		inGame.killHyperspaceJump();
		if (ship.getUpdater() instanceof GameOverUpdater) {
			return;
		}
		SoundManager.stop(Assets.energyLow);
		SoundManager.stop(Assets.criticalCondition);
		inGame.getMessage().clearRepetition();
		inGame.setPlayerControl(false);
		if (alite.getCurrentScreen() instanceof FlightScreen) {
			((FlightScreen) alite.getCurrentScreen()).setInformationScreen(null);
		}
		inGame.forceForwardView();
		inGame.killHud();
		ship.setUpdater(new GameOverUpdater(alite, inGame, ship, System.nanoTime()));
		alite.getPlayer().setCondition(Condition.DOCKED);
	}

	private void loseCargo() {
		if (alite.getCobra().hasCargo()) {
			inGame.setMessage("Cargo lost.");
			alite.getCobra().clearInventory();
			// TODO add computer voice message
		}
	}

	private void loseEquipment() {
		List<Equipment> installedLosableEquipment = alite.getCobra().getInstalledLosableEquipment();
		if (installedLosableEquipment.isEmpty()) {
			return;
		}
		int i = (int) (Math.random() * installedLosableEquipment.size());
		Equipment lostEquip = installedLosableEquipment.get(i);
		inGame.setMessage(lostEquip.getShortName() + " lost.");
		alite.getCobra().removeEquipment(lostEquip);
		if (lostEquip == EquipmentStore.dockingComputer) {
			SoundManager.play(Assets.com_lostDockingComputer);
			inGame.getDockingComputerAI().disengage();
		} else if (lostEquip == EquipmentStore.ecmSystem) {
			SoundManager.play(Assets.com_lostEcm);
		} else if (lostEquip == EquipmentStore.fuelScoop) {
			SoundManager.play(Assets.com_lostFuelScoop);
		} else if (lostEquip == EquipmentStore.escapeCapsule) {
			SoundManager.play(Assets.com_lostEscapeCapsule);
		} else if (lostEquip == EquipmentStore.energyBomb) {
			SoundManager.play(Assets.com_lostEnergyBomb);
		} else if (lostEquip == EquipmentStore.extraEnergyUnit) {
			SoundManager.play(Assets.com_lostExtraEnergyUnit);
		} else if (lostEquip == EquipmentStore.galacticHyperdrive) {
			SoundManager.play(Assets.com_lostGalacticHyperdrive);
		} else if (lostEquip == EquipmentStore.retroRockets) {
			SoundManager.play(Assets.com_lostRetroRockets);
		}
	}

	public void damageShip(int amount, boolean front) {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		SoundManager.play(Assets.hullDamage);
		int shield = front ? alite.getCobra().getFrontShield() : alite.getCobra().getRearShield();
		if (shield > 0) {
			if (shield >= amount) {
				shield -= amount;
				amount = 0;
			}
			else {
				amount -= shield;
				shield = 0;
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
		long vbLen = (long) (Settings.vibrateLevel * 30.0f);
		if (amount > 0) {
			int newVal = alite.getCobra().getEnergy() - amount;
			if (newVal <= 0) {
				alite.getCobra().setEnergy(0);
				inGame.gameOver();
				vbLen <<= 1;
			} else {
				alite.getCobra().setEnergy(newVal);
				checkEnergyLow();
				if (newVal <= (2 * PlayerCobra.MAX_ENERGY_BANK)) {
					if (Math.random() * 256 < 20) {
						if (!inGame.getMessage().isActive()) {
							if (Math.random() * 20 < 1) {
								loseCargo();
							} else {
								loseEquipment();
							}
						}
					}
				}
			}
			vbLen <<= 1;
		}
		if (vbLen > 0) {
			Vibrator vb = (Vibrator) alite.getSystemService(Context.VIBRATOR_SERVICE);
			if (vb != null) {
				vb.vibrate(vbLen);
			}
		}
	}

	public final void checkPlayerHit(final LaserCylinder laser, final float distanceToNextShot, final GraphicObject ship) {
		if (!inGame.isPlayerAlive() || Settings.invulnerable) {
			return;
		}
		// Fire at player, check for hit.
		float intersectionDistance = computeIntersectionDistance(shotDirection, shotOrigin, ship.getPosition(), 156, tempVector);
		if (intersectionDistance > 0 && intersectionDistance <= distanceToNextShot) {
			for (LaserCylinder lc: laser.getTwins()) {
				lc.setVisible(false);
				lc.clearTwins();
			}
			laser.setVisible(false);
			laser.clearTwins();
			damageShip(8, shotDirection.dot(ship.getForwardVector()) >= 0);
		}
	}

	private final void checkPromotion() {
		int score = alite.getPlayer().getScore();
		boolean promoted = false;
		while (score >= alite.getPlayer().getRating().getScoreThreshold() && alite.getPlayer().getRating().getScoreThreshold() > 0) {
			alite.getPlayer().setRating(Rating.values()[alite.getPlayer().getRating().ordinal() + 1]);
			promoted = true;
		}
		if (promoted) {
			inGame.getMessage().setText("Right On, Commander.");
		}
	}

	final void computeBounty(SpaceObject destroyedObject, WeaponType wt) {
		int bounty = destroyedObject.getBounty();
		alite.getPlayer().setCash(alite.getPlayer().getCash() + bounty);
		computeScore(destroyedObject, wt);
		if (!(destroyedObject instanceof CargoCanister)) {
			SoundManager.play(Assets.com_targetDestroyed);
			String bountyString = "Bounty for " + destroyedObject.getName() + ": " +
							  (bounty == 0 ? "None." : String.format(Locale.getDefault(), "%d.%d Cr.", bounty / 10, bounty % 10));
			inGame.getMessage().setText(bountyString, 10000000000l);
		}
	}

	final void computeScore(SpaceObject destroyedObject, WeaponType wt) {
		int points = destroyedObject.getScore();
		if (Settings.difficultyLevel == 0) {
			points >>= 1;
		} else if (Settings.difficultyLevel == 1) {
			points = (int) (points * 0.75f);
		} else if (Settings.difficultyLevel == 2) {
			points = (int) (points * 0.85f);
		} else if (Settings.difficultyLevel == 4) {
			points = (int) (points * 1.25f);
		} else if (Settings.difficultyLevel == 5) {
			points <<= 1;
		}			
		AliteLog.d("Player kill", "Destroyed " + destroyedObject.getName() + " at Difficulty " + Settings.difficultyLevel + " for " + points + " points.");	
		alite.getPlayer().setScore(alite.getPlayer().getScore() + points);
		checkPromotion();
		if (destroyedObject.getScore() > 0) {
			alite.getPlayer().increaseKillCount(1);
			if ((alite.getPlayer().getKillCount() % 1024) == 0) {
				inGame.getMessage().setText("Good Shooting, Commander!");
			} else if ((alite.getPlayer().getKillCount() % 256) == 0) {
				inGame.getMessage().setText("Right On, Commander.");
			}
		}
	}

	public final void checkObjectHit(final LaserCylinder laser, final float distanceToNextShot, final GraphicObject ship, List <AliteObject> allObjects) {
		for (AliteObject eo: allObjects) {
			if (eo instanceof SpaceObject) {
				if (laser.getOrigin() == eo) {
					continue;
				}
				if (((SpaceObject) eo).isCloaked()) {
					continue;
				}
				float distSq = eo.getPosition().distanceSq(ship.getPosition());
				float scaleFactor = distSq <= SpaceObjectAI.SHOOT_DISTANCE_SQ ? 1.0f : 1.0f + 5.0f * ((distSq - SpaceObjectAI.SHOOT_DISTANCE_SQ) / MAX_ENEMY_DISTANCE_SQ);
				if (scaleFactor > 3.0f) {
					scaleFactor = 3.0f;
				} else if (scaleFactor < 1.0f) {
					scaleFactor = 1.0f;
				}
				float intersectionDistance = computeIntersectionDistance(shotDirection, shotOrigin, eo.getPosition(), ((SpaceObject) eo).getBoundingSphereRadius() * scaleFactor, tempVector);
				if (intersectionDistance > 0 && intersectionDistance <= distanceToNextShot) {
					if (((SpaceObject) eo).intersect(shotOrigin, shotDirection, scaleFactor)) {
						// Make sure the laser is at least rendered once...
						laser.removeInNFrames(4);
						for (LaserCylinder lc: laser.getTwins()) {
							lc.removeInNFrames(4);
						}
						SoundManager.play(Assets.laserHit);
						if (((SpaceObject) eo).getType() != ObjectType.SpaceStation || "Alien Space Station".equals(eo.getName())) {
							// Space Stations are invulnerable --- in general ;)
							if (Settings.laserPowerOverride != 0) {
								alite.getPlayer().setCheater(true);
							}
							float hullStrength = ((SpaceObject) eo).applyDamage(laser.getLaser().getPower() + Settings.laserPowerOverride);
							if (hullStrength <= 0) {
								if (laser.getOrigin() == null) {
									// Player has destroyed something
									computeBounty((SpaceObject) eo, WeaponType.values()[laser.getLaser().getIndex()]);
								}
								explode((SpaceObject) eo, true, WeaponType.values()[laser.getLaser().getIndex()]);
								((SpaceObject) eo).setRemove(true);
							}
						}
						((SpaceObject) eo).executeHit((SpaceObject) ship);
					}
				}
			}
		}
	}

	final void update(float deltaTime, final GraphicObject ship, List <AliteObject> allObjects, List <AliteObject> objectsToBeAdded) {
		Iterator<LaserCylinder> laserIterator = activeLasers.iterator();
		while (laserIterator.hasNext()) {
			LaserCylinder laser = laserIterator.next();
			if (laser.isVisible()) {
				laser.getPosition().copy(shotOrigin); // Origin of shot
				laser.moveForward(deltaTime, laser.getInitialDirection());
				Vector3f laserPos = laser.getPosition();
				laser.getPosition().copy(shotDirection);
				shotDirection.sub(shotOrigin);
				float distanceToNextShot = shotDirection.length();
				shotDirection.normalize(); // Direction of shot
				if (laser.getOrigin() != null && laser.getRemoveInNFrames() == -1) {
					checkPlayerHit(laser, distanceToNextShot, ship);
				}
				if (laser.getOrigin() == null && laser.getRemoveInNFrames() == -1) {
					checkObjectHit(laser, distanceToNextShot, ship, allObjects);
				}
				Vector3f shipPos  = ship.getPosition();
				float distanceSq = (laserPos.x - shipPos.x) * (laserPos.x - shipPos.x) +
					           (laserPos.y - shipPos.y) * (laserPos.y - shipPos.y) +
					           (laserPos.z - shipPos.z) * (laserPos.z - shipPos.z);
				if (distanceSq > 1936000000.0f) {
					laser.setVisible(false);
					laserPool.free(laser);
					laserIterator.remove();
				}
			} else {
				laserPool.free(laser);
				laserIterator.remove();
			}
		}
		Iterator<Explosion> explosionIterator = activeExplosions.iterator();
		while (explosionIterator.hasNext()) {
			Explosion ex = explosionIterator.next();
			ex.update();
			if (ex.isFinished())  {
				explosionIterator.remove();
			}
		}
	}

	public void fire(SpaceObject so, GraphicObject ship) {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		SoundManager.play(Assets.enemyFireLaser);
		createdLasers.clear();
		int max = so.getNumberOfLasers();
		for (int i = 0; i < max; i++) {
			int off = i == 0 ? -1 : i == 1 ? 1 : 0;
			if (max == 1) {
				off = 0;
			}
			tempVecArray[0] = so.getLaserX(i);
			tempVecArray[1] = so.getLaserY(i);
			tempVecArray[2] = so.getLaserZ(i);
			tempVecArray[3] = 1.0f;
			Matrix.multiplyMV(tempVecArray2, 0, so.getMatrix(), 0, tempVecArray, 0);
			ship.getForwardVector().scale(-Math.abs(ship.getSpeed()) - 80, tempVector);
			tempVector.add(ship.getPosition());
			float xd = tempVecArray2[0] - tempVector.x + off * DIST_RIGHT * so.getRightVector().x;
			float yd = tempVecArray2[1] - tempVector.y + off * DIST_RIGHT * so.getRightVector().y;
			float zd = tempVecArray2[2] - tempVector.z + off * DIST_RIGHT * so.getRightVector().z;
			LaserCylinder laser = laserPool.newObject();
			laser.reset();
			laser.setBeam(false);
			laser.setColor(so.getLaserColor());
			laser.setTexture(so.getLaserTexture(), alite);
			laser.clearTwins();
			laser.setPosition(tempVecArray2[0], tempVecArray2[1], tempVecArray2[2]);
			laser.setInitialDirection(xd, yd, zd);
			laser.setForwardVector(xd, yd, zd);
			so.getUpVector().cross(laser.getForwardVector(), tempVector);
			laser.setRightVector(tempVector);
			laser.setUpVector(so.getUpVector());
			laser.setVisible(true);
			laser.setLaser(EquipmentStore.militaryLaser);
			laser.setOrigin(so);
			laser.setAiming(false);
			activeLasers.add(laser);
			createdLasers.add(laser);
		}
		for (int i = 0; i < createdLasers.size(); i++) {
			for (int j = i + 1; j < createdLasers.size(); j++) {
				createdLasers.get(i).addTwin(createdLasers.get(j));
				createdLasers.get(j).addTwin(createdLasers.get(i));
			}
		}
	}

	private void computeLaserVectors(int viewDirection, GraphicObject ship) {
		if (viewDirection == 0) {
			ship.getRightVector().copy(laserRight);
			ship.getForwardVector().copy(laserForward);
		} else if (viewDirection == 1) {
			ship.getRightVector().copy(laserForward);
			laserForward.negate();
			ship.getForwardVector().copy(laserRight);
		} else if (viewDirection == 2) {
			ship.getForwardVector().copy(laserForward);
			laserForward.negate();
			ship.getRightVector().copy(laserRight);
			laserRight.negate();
		} else if (viewDirection == 3) {
			ship.getRightVector().copy(laserForward);
			ship.getForwardVector().copy(laserRight);
			laserRight.negate();
		}

		if (!Settings.laserDoesNotOverheat) {
			alite.getCobra().setLaserTemperature(alite.getCobra().getLaserTemperature() + 1);
		}
		if (alite.getCobra().getLaserTemperature() == 40 && (lockTime == -1 || (System.nanoTime() - lockTime) > 5000000000l)) {
			SoundManager.play(Assets.com_laserTemperatureCritical);
			lockTime = System.nanoTime();
		}
	}

	private LaserCylinder spawnPlayerLaserCylinder(Laser laser, GraphicObject ship, float x, float y, float z, float xd, float yd, float zd, boolean aiming) {
		LaserCylinder laserCylinder = laserPool.newObject();
		laserCylinder.reset();
		laserCylinder.setBeam(laser.isBeam());
		laserCylinder.setColor(laser.getColor());
		laserCylinder.setTexture(laser.getTexture(), alite);
		laserCylinder.clearTwins();
		laserCylinder.setOrigin(null);
		laserCylinder.setPosition(x, y, z);
		laserCylinder.setInitialDirection(xd, yd, zd);
		laserCylinder.setVisible(true);
		laserCylinder.setLaser(laser);
		laserCylinder.setAiming(aiming);
		laserCylinder.setForwardVector(laserCylinder.getInitialDirection());
		ship.getUpVector().cross(laserCylinder.getForwardVector(), tempVector);
		laserCylinder.setRightVector(tempVector);
		laserCylinder.setUpVector(ship.getUpVector());
		activeLasers.add(laserCylinder);

		return laserCylinder;
	}

	private void playerFireLaserCylinder(Laser laser, GraphicObject ship) {
		Vector3f shipPos = ship.getPosition();
		float x = shipPos.x + laserForward.x * DIST_FRONT + laserRight.x * DIST_RIGHT;
		float y = shipPos.y + laserForward.y * DIST_FRONT+ laserRight.y * DIST_RIGHT;
		float z = shipPos.z + laserForward.z * DIST_FRONT + laserRight.z * DIST_RIGHT;
		float xd = laserForward.x + (laserRight.x * DIST_RIGHT) / DIST_CONVERGE;
		float yd = laserForward.y + (laserRight.y * DIST_RIGHT) / DIST_CONVERGE;
		float zd = laserForward.z + (laserRight.z * DIST_RIGHT) / DIST_CONVERGE;
		LaserCylinder laser1 = spawnPlayerLaserCylinder(laser, ship, x, y, z, xd, yd, zd, false);

		x = shipPos.x + laserForward.x * DIST_FRONT - laserRight.x * DIST_RIGHT;
		y = shipPos.y + laserForward.y * DIST_FRONT - laserRight.y * DIST_RIGHT;
		z = shipPos.z + laserForward.z * DIST_FRONT - laserRight.z * DIST_RIGHT;
		xd = laserForward.x - (laserRight.x * DIST_RIGHT) / DIST_CONVERGE;
		yd = laserForward.y - (laserRight.y * DIST_RIGHT) / DIST_CONVERGE;
		zd = laserForward.z - (laserRight.z * DIST_RIGHT) / DIST_CONVERGE;
		LaserCylinder laser2 = spawnPlayerLaserCylinder(laser, ship, x, y, z, xd, yd, zd, false);

		x = shipPos.x + laserForward.x * DIST_FRONT;
		y = shipPos.y + laserForward.y * DIST_FRONT;
		z = shipPos.z + laserForward.z * DIST_FRONT;
		xd = laserForward.x;
		yd = laserForward.y;
		zd = laserForward.z;
		LaserCylinder laser3 = spawnPlayerLaserCylinder(laser, ship, x, y, z, xd, yd, zd, true);

		laser1.setTwins(laser2, laser3);
		laser2.setTwins(laser1, laser3);
		laser3.setTwins(laser1, laser2);
		SoundManager.play(Assets.fireLaser);
	}

	void fire(int viewDirection, GraphicObject ship) {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		if (alite.getCobra().getLaserTemperature() >= PlayerCobra.MAX_LASER_TEMPERATURE) {
			return;
		}
		Laser laser = alite.getCobra().getLaser(viewDirection);
		if (laser == null || !laser.fire()) {
			return;
		}

		computeLaserVectors(viewDirection, ship);
		playerFireLaserCylinder(laser, ship);
	}

	final List <TimedEvent> registerTimedEvents() {
		List <TimedEvent> timedEvents = new ArrayList<TimedEvent>();
		timedEvents.add(new TimedEvent(NORMAL_REFRESH_RATE) { // Cool down laser and replenish energy banks...
			private static final long serialVersionUID = -7421881699858933872L;

			@Override
			public void doPerform() {
				long nd = ((alite.getCobra().isEquipmentInstalled(EquipmentStore.navalEnergyUnit)) ? NAVAL_REFRESH_RATE:NORMAL_REFRESH_RATE) / alite.getTimeFactor();
				if (delay != nd) {
					updateDelay(nd);
				}
				alite.getCobra().setLaserTemperature(alite.getCobra().getLaserTemperature() - 1);

				int energy = alite.getCobra().getEnergy();
				boolean updateFrontRearShields = (energy == PlayerCobra.MAX_ENERGY) ||
					    alite.getCobra().isEquipmentInstalled(EquipmentStore.extraEnergyUnit) ||
					    alite.getCobra().isEquipmentInstalled(EquipmentStore.navalEnergyUnit);
				if (energy < PlayerCobra.MAX_ENERGY) {
					alite.getCobra().setEnergy(energy + 1);
				}
				if (updateFrontRearShields) {
					alite.getCobra().setFrontShield(alite.getCobra().getFrontShield() + 1);
					alite.getCobra().setRearShield(alite.getCobra().getRearShield() + 1);
				}
				checkEnergyLow();
			}
		});
		return timedEvents;
	}

	public final void checkEnergyLow() {
		if (!inGame.isPlayerAlive()) {
			return;
		}
		int energy = alite.getCobra().getEnergy();
		if (energy < PlayerCobra.MAX_ENERGY_BANK) {
			AliteLog.d("EnergyLow", "EnergyLow! -- Playing? " + SoundManager.isPlaying(Assets.energyLow));
		}
		if (energy < PlayerCobra.MAX_ENERGY_BANK && !SoundManager.isPlaying(Assets.energyLow)) {
			SoundManager.repeat(Assets.energyLow);
		} else if (energy >= PlayerCobra.MAX_ENERGY_BANK && SoundManager.isPlaying(Assets.energyLow)) {
			SoundManager.stop(Assets.energyLow);
		}
	}

	void performUpdate(final float deltaTime, final int viewDirection, final GraphicObject ship) {
		if (autoFire) {
			fire(viewDirection, ship);
		}
	}

	public void setAutoFire(boolean b) {
		Laser laser = alite.getCobra().getLaser(inGame.getViewDirection());
		if (laser == null) {
			autoFire = false;
			return;
		}
		autoFire = b;
	}

	public boolean isAutoFire() {
		return autoFire && inGame.isPlayerAlive();
	}

	void handleTouchUp(final int viewDirection, final GraphicObject ship) {
		if (lastLaserFireUp != 0 && (System.nanoTime() - lastLaserFireUp) <= 500000000l) {
			autoFire = !autoFire;
		}
		lastLaserFireUp = System.nanoTime();
		if (!autoFire) {
			fire(viewDirection, ship);
		}
	}

	void renderLaser(LaserCylinder laser, final GraphicObject ship) {
		if (laser.isAiming()) {
			return;
		}
		alite.getTextureManager().setTexture(null);
		GLES11.glPushMatrix();
		laser.render(alite);
	    GLES11.glPopMatrix();
	    laser.postRender();
	}

	public void destroy() {
		alite.setLaserManager(null);
		inGame = null;
	}
}
