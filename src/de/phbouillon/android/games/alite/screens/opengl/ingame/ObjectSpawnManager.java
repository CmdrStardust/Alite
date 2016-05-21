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
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

import android.opengl.Matrix;
import de.phbouillon.android.framework.TimeFactorChangeListener;
import de.phbouillon.android.framework.Updater;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.LegalStatus;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.generator.enums.Government;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.IMethodHook;
import de.phbouillon.android.games.alite.screens.opengl.objects.BoxSpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AIState;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AiStateCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AiStateCallbackHandler;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.MathHelper;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.WayPoint;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.OrbitShuttle;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargoid;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargon;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Transporter;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Viper;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteHud;

public class ObjectSpawnManager implements Serializable {
	private static final long serialVersionUID = -6732126311025754393L;

	public static boolean ASTEROIDS_ENABLED             = true;
	public static boolean CONDITION_RED_OBJECTS_ENABLED = true;
	public static boolean TRADERS_ENABLED               = true;
	public static boolean SHUTTLES_ENABLED              = true;
	public static boolean VIPERS_ENABLED                = true;
	public static boolean THARGOIDS_ENABLED             = true;
	public static boolean THARGONS_ENABLED              = true;
	
	private transient int maxNumberOfTradeShips;
	private transient int maxNumberOfAsteroids;
	private transient int maxNumberOfShuttles;
	private transient int maxNumberOfThargoids;
	private transient int maxNumberOfEnemies;
	private transient int maxNumberOfVipers;
	
	private static final long LAUNCH_BREAK_TIMER  = 15 * 1000000000l;
	
	private static final float SPAWN_INTER_SHIP_DISTANCE            = 1500.0f;
	private static final float TRANSPORT_PLANET_DISTANCE_SQ         = 7225000000.0f;
	
	private static final int [] spawnMatrix = new int [] {0, 0,  1, 0,   -1, 0,   0, -1,   0, 1,   1, 1,
		                                                 -2, -2,  2, 0,   -2, 0,   0, -2,   0, 2,   2, 2};
	
	private static final float [] thargonSpawnMatrix = new float [] {0, -1,  0.7071067f, -0.7071067f,  1, 0,  0.7071067f, 0.7071067f, 
																	 0,  1, -0.7071067f,  0.7071067f, -1, 0, -0.7071067f, -0.7071067f};	
	private transient Alite alite;
	private InGameManager inGame;
	private SpawnTimer conditionRedTimer = new SpawnTimer();
	private SpawnTimer traderTimer = new SpawnTimer();
	private SpawnTimer asteroidTimer = new SpawnTimer();
    private SpawnTimer shuttleOrTransportTimer = new SpawnTimer();
    private SpawnTimer viperTimer = new SpawnTimer();
    private transient BoxSpaceObject gateWatcher;
    
    private long launchBreak = -1;
    
	private static final Vector3f vector = new Vector3f(0, 0, 0);
	private static final Vector3f vector2 = new Vector3f(0, 0, 0);
	private static final Vector3f vector3 = new Vector3f(0, 0, 0);
	private final float [] matrixCopy = new float[16];

	private boolean torus;
	private SystemData system;
	private boolean timedEventsMustBeInitialized = true;
	private int launchAreaViolations = 0;
	private long lastWarningTime = -1;
	
	private class SpawnTimer implements Serializable {
		private static final long serialVersionUID = -4989102775350929292L;

		private transient TimedEvent event;
		private boolean paused = false;
		private long pauseTime = -1;
		private boolean locked = false;
		private long lastExecutionTime = -1;
				
		private void clearTimes() {
			pauseTime         = -1;
			lastExecutionTime = -1;
		}
		
		private void setTimes() {
			if (event == null) {
				return;
			}
			pauseTime = event.pause();
			lastExecutionTime = event.lastExecutionTime;
		}
		
		private void initialize(long delay, final IMethodHook method) {
			event = new TimedEvent(delay, lastExecutionTime, pauseTime, locked) {				
				private static final long serialVersionUID = -92934290891367981L;

				@Override
				public void doPerform() {
					method.execute(0);
				}
			};
			clearTimes();
		}
		
		private void pause(boolean b) {
			if (event != null && paused != b) {
				if (b) {
					event.pause();
				} else {
					event.resume();
				}
				paused = b;
			}			
		}
	}
	
	ObjectSpawnManager(final Alite alite, final InGameManager inGame) {
		this.alite = alite;
		this.inGame = inGame;
		initializeLimits();
	}
		
	boolean needsInitialization() {
		return timedEventsMustBeInitialized;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		conditionRedTimer.setTimes();
		traderTimer.setTimes();
		asteroidTimer.setTimes();
		shuttleOrTransportTimer.setTimes();
		viperTimer.setTimes();
		out.defaultWriteObject();
    }

	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "ObjectSpawnManager.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "ObjectSpawnManager.readObject I");
			this.alite = Alite.get();
			conditionRedTimer = new SpawnTimer();
			traderTimer = new SpawnTimer();
			asteroidTimer = new SpawnTimer();
			shuttleOrTransportTimer = new SpawnTimer();
			viperTimer = new SpawnTimer();
			timedEventsMustBeInitialized = true;
			gateWatcher = new BoxSpaceObject(alite, "GateWatcher", 510, 510, 100);
			AliteLog.e("readObject", "ObjectSpawnManager.readObject II");
			initializeLimits();
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}
	
	private void initializeLimits() {
		int d = Settings.difficultyLevel;
		maxNumberOfTradeShips = 2;
		maxNumberOfAsteroids = 4;
		maxNumberOfShuttles = 2;
		maxNumberOfThargoids = d == 0 ? 0 : d == 1 ? 0 : d == 2 ? 1 : d == 3 ? 4 : d == 4 ? 8 : 10;
		maxNumberOfEnemies = d == 0 ? 0 : d == 1 ? 2 : d == 2 ? 5 : d == 3 ? 8 : d == 4 ? 12 : 20;
		maxNumberOfVipers = d == 0 ? 1 : d == 1 ? 1 : d == 2 ? 3 : d == 3 ? 8 : d == 4 ? 12 : 20;		
	}
	
	void initTimedEvents(InGameManager inGame) {
		if (conditionRedTimer == null || traderTimer == null || asteroidTimer == null || shuttleOrTransportTimer == null || viperTimer == null) {
			return;
		}
		timedEventsMustBeInitialized = false;
		alite = Alite.get();
		if (Alite.get().getPlayer() == null) {
			Alite.get().initialize();			
		}
		if (alite.getPlayer().getCurrentSystem() != null && alite.getPlayer().getCurrentSystem().getIndex() == 256) {
			return;
		}		
		if (conditionRedTimer.event != null) {
			conditionRedTimer.event.setRemove(true);
		}
		if (traderTimer.event != null) {
			traderTimer.event.setRemove(true);
		}
		if (asteroidTimer.event != null) {
			asteroidTimer.event.setRemove(true);
		}
		if (shuttleOrTransportTimer.event != null) {
			shuttleOrTransportTimer.event.setRemove(true);
		}
		if (viperTimer.event != null) {
			viperTimer.event.setRemove(true);
		}
		long delayToConditionRedEncounter = getDelayToConditionRedEncounter();
		long delayToTraderEncounter = getDelayToTraderEncounter();
		long delayToAsteroidEncounter = getDelayToAsteroidEncounter();
		long delayToShuttleOrTransporterEncounter = getDelayToShuttleEncounter();
		long delayToViperEncounter = getDelayToViperEncounter();
		this.inGame = inGame;
		if (delayToConditionRedEncounter > 0) {
			AliteLog.d("CRE", "CRE Timer, initializing");
			conditionRedTimer.initialize(delayToConditionRedEncounter, new IMethodHook() {				
				private static final long serialVersionUID = 2793174977898387221L;

				@Override
				public void execute(float deltaTime) {
					spawnConditionRedObject();					
				}
			});
		}
		AliteLog.d("Trader", "Trader Timer, initializing");
		traderTimer.initialize(delayToTraderEncounter, new IMethodHook() {
			private static final long serialVersionUID = 9039974139789907685L;

			@Override
			public void execute(float deltaTime) {
				spawnTrader();
			}
		});
		AliteLog.d("Asteroid", "Asteroid Timer, initializing");
		asteroidTimer.initialize(delayToAsteroidEncounter, new IMethodHook() {			
			private static final long serialVersionUID = 5843714386316634097L;

			@Override
			public void execute(float deltaTime) {
				spawnAsteroid();
				
			}
		});
		AliteLog.d("SOT", "SOT Timer, initializing");
		shuttleOrTransportTimer.initialize(delayToShuttleOrTransporterEncounter, new IMethodHook() {
			private static final long serialVersionUID = -8088231773858070919L;

			@Override
			public void execute(float deltaTime) {
				spawnShuttleOrTransporter();
			}
		});
		AliteLog.d("Viper", "Viper Timer, initializing");
		viperTimer.initialize(delayToViperEncounter, new IMethodHook() {			
			private static final long serialVersionUID = 6529856113983841949L;

			@Override
			public void execute(float deltaTime) {
				spawnViper();
			}
		});
		Iterator<Mission> missionIterator = alite.getPlayer().getActiveMissions().iterator();
		while (missionIterator.hasNext()) {
			Mission mission = missionIterator.next();
			TimedEvent te = inGame.isWitchSpace() ? mission.getWitchSpaceSpawnEvent(this) : mission.getSpawnEvent(this);
			if (te != null) {
				inGame.addTimedEvent(te);
			}
			te = mission.getConditionRedSpawnReplacementEvent(this);
			if (te != null) {
				conditionRedTimer.event = te;
			}
			te = mission.getTraderSpawnReplacementEvent(this);
			if (te != null) {
				traderTimer.event = te;
			}
			te = mission.getAsteroidSpawnReplacementEvent(this);
			if (te != null) {
				asteroidTimer.event = te;
			}
			te = mission.getShuttleSpawnReplacementEvent(this);
			if (te != null) {
				shuttleOrTransportTimer.event = te;
			}
			te = mission.getViperSpawnReplacementEvent(this);
			if (te != null) {
				viperTimer.event = te;
			}
		}
		if (inGame != null) {
			if (conditionRedTimer.event != null) {
				inGame.addTimedEvent(conditionRedTimer.event);
			}
			inGame.addTimedEvent(traderTimer.event);
			inGame.addTimedEvent(asteroidTimer.event);
			inGame.addTimedEvent(shuttleOrTransportTimer.event);
			inGame.addTimedEvent(viperTimer.event);
		}
		alite.setTimeFactorChangeListener(new TimeFactorChangeListener() {			
			@Override
			public void timeFactorChanged(int oldTimeFactor, int newTimeFactor) {
				updateTimers(((float) oldTimeFactor) / ((float) newTimeFactor));
			}
		});
	}
	
	private void updateTimerInternal(float factor, SpawnTimer timer) {
		if (timer != null && timer.event != null) {
			timer.event.updateDelay((long) (timer.event.timeToNextTrigger() * factor));
		}		
	}
	
	private void updateTimers(float factor) {
		updateTimerInternal(factor, conditionRedTimer);
		updateTimerInternal(factor, traderTimer);
		updateTimerInternal(factor, asteroidTimer);
		updateTimerInternal(factor, shuttleOrTransportTimer);
		updateTimerInternal(factor, viperTimer);
		
	}
	
	public void lockConditionRedEvent() {
		if (conditionRedTimer.event != null) {
			conditionRedTimer.event.lock();
		}
	}
	
	public void unlockConditionRedEvent() {
		if (conditionRedTimer.event != null) {
			conditionRedTimer.event.updateDelay(getDelayToConditionRedEncounter());
			conditionRedTimer.event.unlock();
		}		
	}
	
	void startSimulation(SystemData system) {
		this.system = system;
		torus = false;		
		gateWatcher = new BoxSpaceObject(alite, "GateWatcher", 510, 510, 100);
		initTimedEvents(inGame);
		launchAreaViolations = 0;
	}
	
	public long getDelayToConditionRedEncounter() {
		return system == null ? 0 : (long) ((((float) ((system.getGovernment().ordinal() + 2) << 9)) / 16.7f) * 1000000000l);	
	}
	
	public long getDelayToTraderEncounter() {
		return (long) (((200.0f + 100.0f * Math.random()) / 16.7f) * 1000000000l);
	}
	
	public long getDelayToAsteroidEncounter() {
		return (long) (((200.0f + 100.0f * Math.random()) / 16.7f) * 1000000000l);
	}
	
	public long getDelayToShuttleEncounter() {
		return (long) (((200.0f + 100.0f * Math.random()) / 16.7f) * 1000000000l);
	}
	
	public long getDelayToViperEncounter() {
		return (long) ((200.0f / 16.7f) * 1000000000l);
	}

	public InGameManager getInGameManager() {
		return inGame;
	}
	
//	public void setPauseConditionRedEvent(boolean b) {
//		conditionRedTimer.pause(b);
//	}
	
	public void enterTorus() {
		SoundManager.repeat(Assets.torus);
		torus = true;		
		if (conditionRedTimer.event == null) {
			return;
		}
		conditionRedTimer.event.updateDelay((long) (30l * 1000000000l / 16.7));
	}
	
	public void leaveTorus() {
		SoundManager.stop(Assets.torus);
		torus = false;
		if (conditionRedTimer.event != null) {
			conditionRedTimer.event.updateDelay((long) ((((float) ((system.getGovernment().ordinal() + 2) << 9)) / 16.7f) * 1000000000l));
		}
		inGame.yankOutOfTorus();
	}
	
	public boolean isInTorus() {
		return torus;
	}
		
	private Vector3f spawnObject(float distance) {
		vector.x = (float) (0.7 - Math.random() * 1.4);
		vector.y = (float) (0.7 - Math.random() * 1.4);
		vector.z = (float) (0.7 - Math.random() * 1.4);		
		Vector3f spawnPosition = MathHelper.getRandomPosition(inGame.getShip().getPosition(), vector, distance, 1000.0f);
		vector.x = spawnPosition.x;
		vector.y = spawnPosition.y;
		vector.z = spawnPosition.z;
		return spawnPosition;
	}
	
	public void spawnEnemyAndAttackPlayer(final SpaceObject ship, int index, Vector3f spawnPosition, boolean addCallback) {
		ship.setPosition(vector);
		ship.orientTowards(inGame.getShip(), 0);
		inGame.addObject(ship);
		ship.assertOrthoNormal();
		ship.setAIState(AIState.ATTACK, inGame.getShip());
		if (index < 12) {
			vector.x = spawnPosition.x + spawnMatrix[index * 2]     * SPAWN_INTER_SHIP_DISTANCE;
			vector.y = spawnPosition.y + spawnMatrix[index * 2 + 1] * SPAWN_INTER_SHIP_DISTANCE;
		}
		if (addCallback) {
			ship.addDestructionCallback(new DestructionCallback() {
				private static final long serialVersionUID = -3628212229724389819L;

				@Override
				public void onDestruction() {
					if (inGame.getNumberOfObjects(ObjectType.EnemyShip) == 0 && conditionRedTimer.event != null) {						
						unlockConditionRedEvent();
					} 
				}

				@Override
				public int getId() {
					return 8;
				}
			});
		}
	}
		
	void spawnThargoidInWitchSpace() {
		if (inGame.getWitchSpace() == null) {
			return;
		}
		Vector3f spawnPosition = spawnObject(getSpawnDistance());		 
		Thargoid thargoid = new Thargoid(alite);
		thargoid.setSpawnThargonDistanceSq(computeSpawnThargonDistanceSq());
		spawnEnemyAndAttackPlayer(thargoid, 0, spawnPosition, false);
		thargoid.addDestructionCallback(new DestructionCallback(){
			private static final long serialVersionUID = -5841075572165732277L;

			public void onDestruction() {
				if (inGame.getWitchSpace() != null) {
					inGame.getWitchSpace().increaseWitchSpaceKillCounter();
				}
				long secondsToSpawn = (long) ((Math.random() * 6 + 6) * 1000000000l);
				inGame.addTimedEvent(new TimedEvent(secondsToSpawn) {					
					private static final long serialVersionUID = -8236066205369429808L;

					@Override
					public void doPerform() {
						spawnThargoidInWitchSpace();
						setRemove(true);
					}
				});
			}

			@Override
			public int getId() {
				return 9;
			}
		});
	}
	
	public void spawnEnemyNow() {
		SoundManager.play(Assets.com_conditionRed);
		inGame.repeatMessage("Condition Red!", 3);
		int num = calculateNumberOfObjectsToSpawn();
		int enemies = inGame.getNumberOfObjects(ObjectType.EnemyShip);
		if (num + enemies > maxNumberOfEnemies) {
			num = maxNumberOfEnemies - enemies;
		}
		if (num < 0) {
			return;
		}
		Vector3f spawnPosition = spawnObject(getSpawnDistance());		 
		for (int i = 0; i < num; i++) {
			SpaceObject ship = SpaceObject.createRandomEnemy(alite);
			spawnEnemyAndAttackPlayer(ship, i, spawnPosition, true);
		}
	}
	
	private void spawnConditionRedObject() {
		if (inGame.getWitchSpace() != null || conditionRedTimer.event == null) {
			return;
		}		
		if (!THARGOIDS_ENABLED && !THARGONS_ENABLED && !CONDITION_RED_OBJECTS_ENABLED) {
			return;
		}
		if (Settings.disableAttackers) {
			return;
		}
		if (!inGame.isPlayerAlive()) {
			return;
		}
		if (inGame.isInSafeZone()) {
			return;
		}
		int thargoidNum = checkSpawnThargoid();
		int thargoids = inGame.getNumberOfObjects(ObjectType.Thargoid);
		if (thargoidNum + thargoids > maxNumberOfThargoids) {
			thargoidNum = maxNumberOfThargoids - thargoids;
		}
		int num = calculateNumberOfObjectsToSpawn();
		int enemies = inGame.getNumberOfObjects(ObjectType.EnemyShip);
		if (num + enemies > maxNumberOfEnemies) {
			num = maxNumberOfEnemies - enemies;
		}
		if ((thargoidNum + num) <= 0 || !CONDITION_RED_OBJECTS_ENABLED) {
			return;
		}
		if (torus) {
			int randByte = (int) (Math.random() * 256);
			if ((system.getGovernment().ordinal() << 5) > randByte) {
				return;
			} else {
				leaveTorus();				
			}
		}		
		SoundManager.play(Assets.com_conditionRed);
		inGame.repeatMessage("Condition Red!", 3);
		if (thargoidNum > 0 && THARGOIDS_ENABLED) {			
			conditionRedTimer.event.lock();
			Vector3f spawnPosition = spawnObject(getSpawnDistance());		 
			for (int i = 0; i < thargoidNum; i++) {
				Thargoid thargoid = new Thargoid(alite);
				thargoid.setSpawnThargonDistanceSq(computeSpawnThargonDistanceSq());
				spawnEnemyAndAttackPlayer(thargoid, i, spawnPosition, true);
			}
			return;
		} 
		conditionRedTimer.event.lock();
		Vector3f spawnPosition = spawnObject(getSpawnDistance());		 
		for (int i = 0; i < num; i++) {
			SpaceObject ship = SpaceObject.createRandomEnemy(alite);
			spawnEnemyAndAttackPlayer(ship, i, spawnPosition, true);
		}
	}
	
	public long computeSpawnThargonDistanceSq() {
		if ((int) (Math.random() * 100) < alite.getPlayer().getRating().ordinal() * 20) {
			long d = (long) (8192 + 8192 * Math.random());
			return d * d;
		}
		return -1;
	}
	
	private int checkSpawnThargoid() {
		if (Settings.difficultyLevel < 2) {
			return 0;
		}
		int prob = alite.getPlayer().getRating().ordinal() * Settings.difficultyLevel;
		int maxProb = 20 + 5 * (Settings.difficultyLevel - 3);
		int minNum = 1;
		int maxNum = 2 + (Settings.difficultyLevel - 3);
			
		if ((int) (Math.random() * 100) < Math.min(prob, maxProb)) {
			return alite.getPlayer().getRating().ordinal() < 5 ? 1 : Math.random() < 0.8 ? minNum : maxNum;
		}
		return 0;
	}
		
	public boolean isLaunchFromStationSafe(boolean trader) {
		if (!inGame.isInSafeZone()) {
			return false;
		}
		if (launchBreak != -1 && System.nanoTime() < launchBreak) {
			return false;
		}
		if (trader) {
			// Vipers fly out of the docking port, so it is forbidden for all other ships ;)
			if (inGame.isVipersWillEngage()) {
				return false;
			}
		}
		if (inGame.getShipInDockingBay() != null) {
			return false;
		}		

		float calcSpeed = inGame.getShip().getSpeed();
		if (calcSpeed >= 0) {
			calcSpeed = -20.0f;
		}
		inGame.getSystemStationPosition().copy(vector);
		inGame.getStation().getForwardVector().copy(vector2);
		vector2.scale(940);
		// Vector holds the center of the collision sphere in front of the station
		vector.add(vector2);
		gateWatcher.setPosition(vector);
		
		// Vector2 holds the forward vector of the ship (negated, so that the reference system is correct)
		// or, if the ship is (almost) at a standstill, we take the vector from ship to docking bay
		if (calcSpeed >= -20.0f) {
			vector.sub(inGame.getShip().getPosition(), vector2);
			vector2.normalize();
			AliteLog.d("Forward vector", "FV (speed near 0): " + vector2);
		} else {
			inGame.getShip().getForwardVector().copy(vector2);
			vector2.negate();
			AliteLog.d("Forward vector", "FV (speed normal): " + vector2);
		}		
		float intersectionDistance = LaserManager.computeIntersectionDistance(vector2, inGame.getShip().getPosition(), vector, 580, vector3);
		float travelDistance = -calcSpeed * 8.5f;
		float distShipStationSq = inGame.getShip().getPosition().distanceSq(vector);
		AliteLog.d("Intersection check", "Intersection distance: " + intersectionDistance + ", travelDistance: " + travelDistance + ", Speed: " + calcSpeed + ", Distance: " + inGame.getShip().getPosition().distance(vector));
		if (intersectionDistance >= 0 && distShipStationSq < 640000) {
			if (calcSpeed > -40.0f) {
				handleLaunchAreaViolations();
			}
			return false;
		}
		boolean okToLaunch = intersectionDistance <= 0 || intersectionDistance > travelDistance;
		if (!okToLaunch) {			
			okToLaunch = !gateWatcher.intersect(inGame.getShip().getPosition(), vector2);
		}
		return okToLaunch;
	}
	
	private void handleLaunchAreaViolations() {
		long time = System.nanoTime();
		if (lastWarningTime != -1 && (time - lastWarningTime) <= 5000000000l) {
			return;
		}
		lastWarningTime = time;
		if (launchAreaViolations == 0) {
			// TODO Play sample				
			inGame.setMessage("Hey rookie! Clear the launch area immediately.");
			AliteLog.d("Receive warning", "Hey rookie! Clear the launch area immediately.");
			launchAreaViolations++;
			if (alite.getPlayer().getLegalStatus() != LegalStatus.CLEAN) {
				launchAreaViolations++;
			}
		} else if (launchAreaViolations == 1) {
			// TODO Play sample				
			inGame.setMessage("Clear the launch area immediately, or we will open fire.");
			AliteLog.d("Receive warning", "Clear the launch area immediately, or we will open fire.");
			launchAreaViolations++;					
		} else {
			// TODO Play sample
			inGame.setMessage("Space Station defensive measures activated.");
			((SpaceStation) inGame.getStation()).denyAccess();
			alite.getPlayer().setLegalValue(alite.getPlayer().getLegalValue() + 4);
			inGame.getShip().setUpdater(new Updater() {						
				private static final long serialVersionUID = 4046742301009349763L;
				private long lastExecution = -1;
				
				@Override
				public void onUpdate(float deltaTime) {
					long time = System.nanoTime();
					if (lastExecution == -1 || (time - lastExecution) >= 1000000000l) {
						lastExecution = time;
						float distance = inGame.getShip().getPosition().distance(inGame.getSystemStationPosition());
						AliteLog.d("Distance to Station", "Distance to Station: " + distance);
						if (distance > 4000) {
							inGame.getShip().setUpdater(null);
							return;
						}
						inGame.getLaserManager().damageShip(15, true);
					}
				}
			});
		}
	}

	private void spawnTrader() {
		traderTimer.event.updateDelay((long) ((((600.0f + 300.0f * Math.random()) / 16.7f) * 1000000000l) / ((float) alite.getTimeFactor())));
		if (inGame.getWitchSpace() != null) {
			return;
		}
		if (inGame.getNumberOfObjects(ObjectType.Trader) >= maxNumberOfTradeShips || !TRADERS_ENABLED || Settings.disableTraders) {
			return;
		}
		if (inGame.isInSafeZone()) {
			if (!isLaunchFromStationSafe(true)) {
				return;
			}
			final SpaceObject ship = SpaceObject.createRandomTraderWithoutAnaconda(alite);			
			launchFromBay(ship, new AiStateCallbackHandler() {				
				private static final long serialVersionUID = -5203119902007110520L;

				@Override
				public void execute(SpaceObject so) {
					so.setInBay(false);
					so.setUpdater(new Updater() {
						private static final long serialVersionUID = 118694905617185715L;

						@Override
						public void onUpdate(float deltaTime) {
							float zDistanceSqToShip = (ship.getPosition().z - inGame.getShip().getPosition().z) *
								    				  (ship.getPosition().z - inGame.getShip().getPosition().z);
							if (zDistanceSqToShip >= 603979776) { // (16384 + 8192) ^ 2...
								ship.setRemove(true);					
							}
						}
					});
					so.setAIState(AIState.FLY_STRAIGHT, ship.getMaxSpeed());
				}
			});
			return;
		}
		spawnObject(getSpawnDistance());	
		final SpaceObject ship = SpaceObject.createRandomTrader(alite);
		ship.setPosition(vector);
		ship.setRandomOrientation(vector, inGame.getShip().getUpVector());
		ship.setAIState(AIState.IDLE, 0);
		ship.setAIState(AIState.FLY_STRAIGHT, ship.getMaxSpeed());
		ship.setUpdater(new Updater() {
			private static final long serialVersionUID = -2146899348570326187L;

			@Override
			public void onUpdate(float deltaTime) {
				float zDistanceSqToShip = (ship.getPosition().z - inGame.getShip().getPosition().z) *
					    				  (ship.getPosition().z - inGame.getShip().getPosition().z);
				if (zDistanceSqToShip >= 603979776) { // (16384 + 8192) ^ 2...
					ship.setRemove(true);					
				}
			}
		});
		inGame.addObject(ship);
	}

	public void launchFromBay(final SpaceObject so, final AiStateCallbackHandler callback) {
		so.setInBay(true);
		so.setPosition(inGame.getSystemStationPosition());
		inGame.getStation().getRightVector().copy(vector);
		vector.negate();
		so.setUpVector(vector);		
		so.setForwardVector(inGame.getStation().getForwardVector());
		so.setRightVector(inGame.getStation().getUpVector());		
		so.applyDeltaRotation(180, 0, 0);
		inGame.getStation().getForwardVector().copy(vector);
		vector.scale(1000);
		vector.add(inGame.getStation().getPosition());		
		WayPoint [] wps = new WayPoint[] {WayPoint.newWayPoint(vector, so.getUpVector())};
		so.setAIState(AIState.FLY_PATH, (Object []) wps);
		so.setUpdater(new Updater() {
			private static final long serialVersionUID = 9195512054255867095L;

			@Override
			public void onUpdate(float deltaTime) {
				Matrix.rotateM(matrixCopy, 0, inGame.getStation().getMatrix(), 0, (float) Math.toDegrees(FlightScreen.SPACE_STATION_ROTATION_SPEED), 0, 0, 1);
				vector.x = -matrixCopy[0];
				vector.y = -matrixCopy[1];
				vector.z = -matrixCopy[2];				
				so.applyDeltaRotation(180, 0, 0);		
				so.orientTowards(inGame.getStation(), vector, deltaTime);
				so.applyDeltaRotation(180, 0, 0);
			}
		});		
		so.registerAiStateCallbackHandler(AiStateCallback.EndOfWaypointsReached, callback); 
		inGame.addObject(so);	
		launchBreak = System.nanoTime() + LAUNCH_BREAK_TIMER;
	}
	
	private void spawnAsteroid() {	
		asteroidTimer.event.updateDelay((long) ((((600.0f + 300.0f * Math.random()) / 16.7f) * 1000000000l) / ((float) alite.getTimeFactor())));
		if (inGame.getWitchSpace() != null) {
			return;
		}
		if (inGame.getNumberOfObjects(ObjectType.Asteroid) >= maxNumberOfAsteroids || !ASTEROIDS_ENABLED) {
			return;
		}
		if (inGame.isInSafeZone()) {
			// Do not spawn asteroids in safe zone; debatable... 
			return;
		}
		spawnObject(getSpawnDistance());
		final SpaceObject asteroid = SpaceObject.createRandomAsteroid(alite);
		asteroid.setPosition(vector);
		asteroid.setSpeed(0);
		vector.x = (float) (-2.0 + Math.random() * 4.0);
		vector.y = (float) (-2.0 + Math.random() * 4.0);
		vector.z = (float) (-2.0 + Math.random() * 4.0);
		vector.normalize();
		final float rx = vector.x;
		final float ry = vector.y;
		final float rz = vector.z;
		vector.x = (float) (-2.0 + Math.random() * 4.0);
		vector.y = (float) (-2.0 + Math.random() * 4.0);
		vector.z = (float) (-2.0 + Math.random() * 4.0);
		vector.normalize();
		final float ix = vector.x;
		final float iy = vector.y;
		final float iz = vector.z;
		asteroid.setUpdater(new Updater() {			
			private static final long serialVersionUID = 5578311067399465378L;

			@Override
			public void onUpdate(float deltaTime) {
				float speed = asteroid.getMaxSpeed();
				asteroid.getPosition().copy(vector);
				float x = vector.x + ix * speed * deltaTime;
				float y = vector.y + iy * speed * deltaTime;
				float z = vector.z + iz * speed * deltaTime;
				asteroid.setPosition(x, y, z);
				asteroid.applyDeltaRotation(rx, ry, rz);
				if (asteroid.getPosition().distance(inGame.getShip().getPosition()) >= AliteHud.MAX_DISTANCE) {
					asteroid.setRemove(true);					
				}
			}
		});		

		inGame.addObject(asteroid);
	}

	private void spawnShuttleOrTransporter() {	
		shuttleOrTransportTimer.event.updateDelay((long) ((((600.0f + 300.0f * Math.random()) / 16.7f) * 1000000000l) / ((float) alite.getTimeFactor())));
		if (inGame.getWitchSpace() != null || !inGame.isInSafeZone()) {
			return;
		}
		if (!SHUTTLES_ENABLED) {
			return;
		}
		if (!isLaunchFromStationSafe(true)) {
			return;
		}
		if (inGame.getNumberOfObjects(ObjectType.Shuttle) >= maxNumberOfShuttles) {
			return;
		}
		final SpaceObject shuttleOrTransport;
		if (Math.random() < 0.5) {
			shuttleOrTransport = new OrbitShuttle(alite);
		} else {
			shuttleOrTransport = new Transporter(alite);
		}
		launchFromBay(shuttleOrTransport, new AiStateCallbackHandler() {
			private static final long serialVersionUID = 2203774721593552494L;

			@Override
			public void execute(SpaceObject so) {
				shuttleOrTransport.setUpdater(null);
				shuttleOrTransport.setInBay(false);
				WayPoint [] wps = new WayPoint[] {WayPoint.newWayPoint(FlightScreen.PLANET_POSITION, shuttleOrTransport.getUpVector())};
				shuttleOrTransport.setAIState(AIState.FLY_PATH, (Object []) wps);
				shuttleOrTransport.setUpdater(new Updater() {
					private static final long serialVersionUID = 2702506138360802890L;

					@Override
					public void onUpdate(float deltaTime) {
						if (shuttleOrTransport.getPosition().distanceSq(FlightScreen.PLANET_POSITION) < TRANSPORT_PLANET_DISTANCE_SQ) {
							shuttleOrTransport.setRemove(true);
						}
					}					
				});
			}
		});
	}
	
	private void spawnViper() {
		AliteLog.d("Spawn Viper", "SafeZoneViolated: " + InGameManager.safeZoneViolated + ", VipersWillEngage: " + inGame.isVipersWillEngage());
		if (inGame.getWitchSpace() != null) {
			return;
		}
		if (alite.getPlayer().getLegalStatus() == LegalStatus.CLEAN) {
			return;
		}
		int currentNumberOfVipers = inGame.getNumberOfObjects(ObjectType.Viper) + inGame.getNumberOfObjects(ObjectType.EnemyShip); 
		if (inGame.getNumberOfObjects(ObjectType.Viper) >= maxNumberOfVipers || !VIPERS_ENABLED) {
			return;
		}		
		if (!isLaunchFromStationSafe(false)) {
			return;
		}
		int maxNumberOfVipers = alite.getPlayer().getRating().ordinal() + 1;
		if (alite.getPlayer().getLegalStatus() == LegalStatus.FUGITIVE) {
			maxNumberOfVipers <<= 1;
		}
		if (currentNumberOfVipers >= maxNumberOfVipers) {
			return;
		}
	    SystemData currentSystem = alite.getPlayer().getCurrentSystem();
	    SpaceObject shipType = null;
	    if (currentSystem != null) {	    	
	    	boolean playerIsCurrentlyOffensive = InGameManager.safeZoneViolated;
	    	if (!playerIsCurrentlyOffensive && inGame.getStation() != null) {	    		
	    		int hitCount = ((SpaceStation) inGame.getStation()).getHitCount();
	    		if (hitCount > 1) {
	    			playerIsCurrentlyOffensive = true;
	    		}
	    	}
	    	Government government = currentSystem.getGovernment();
	    	if ((government == Government.ANARCHY ||
	    		government == Government.FEUDAL) && !playerIsCurrentlyOffensive) {
	    		return;
	    	}
	    	if (!playerIsCurrentlyOffensive && !inGame.isVipersWillEngage()) {
	    		return;
	    	}
	    	inGame.setVipersWillEngage(true);
	    	shipType = government == Government.ANARCHY || government == Government.FEUDAL ? 
	    			SpaceObject.createRandomDefensiveShip(alite) : new Viper(alite);
	    	shipType.setHudColor(Viper.HUD_COLOR);
			Condition conditionOld = alite.getPlayer().getCondition();
			alite.getPlayer().setCondition(Condition.RED);
			if (conditionOld != Condition.RED) {
				SoundManager.play(Assets.com_conditionRed);
				inGame.repeatMessage("Condition Red!", 3);
			}
	    }
	    
		final SpaceObject viper = shipType == null ? new Viper(alite) : shipType;	
		viper.setIgnoreSafeZone(true);
		launchFromBay(viper, new AiStateCallbackHandler() {
			private static final long serialVersionUID = -2431074037545740323L;

			@Override
			public void execute(SpaceObject so) {
				viper.setUpdater(null);
				viper.setInBay(false);				
				viper.setAIState(AIState.ATTACK, inGame.getShip());
			}
		});
	}
	
	private int calculateNumberOfObjectsToSpawn() {
		int d = Settings.difficultyLevel;
		int result = (int) (Math.random() * ((alite.getPlayer().getRating().ordinal() + 3) >> 1)) + 1;
		if (d > 3) {
			result += (d - 3);
		}
		result += alite.getPlayer().getLegalStatus().ordinal() * (Math.random() < 0.05 ? 1 : 0);
		if (alite.getPlayer().getCurrentSystem() != null && alite.getPlayer().getCurrentSystem().getGovernment().ordinal() < 2) {
			result += Math.random() < 0.05 ? 1 : 0;
		}
		
		int maxAtATime = d == 0 ? 0 : d == 1 ? 2 : d == 2 ? 3 : d == 3 ? 4 : d == 4 ? 6 : 12;
				
		if (result > maxAtATime) {
			result = maxAtATime;
		}
		return result; 
	}
	
	private final float getSpawnDistance() {
		return (float) (16384 + 8192 * Math.random());
	}	
	
	public final Vector3f spawnObject() {
		return spawnObject(getSpawnDistance());
	}
	
	public static void spawnThargons(Alite alite, final Thargoid mother, final InGameManager inGame) {
		if (!THARGONS_ENABLED) {
			return;
		}
		int numberOfThargons = (int) ((Math.random() * 4) + 4);
		mother.getForwardVector().copy(vector);
		vector.scale(40);
		vector.add(mother.getPosition());
		vector.copy(vector2);
		for (int i = 0; i < numberOfThargons; i++) {
			final Thargon thargon = new Thargon(alite);
			thargon.setPosition(vector);
			thargon.orientTowards(inGame.getShip(), 0);
			inGame.addObject(thargon);
			thargon.assertOrthoNormal();
			thargon.setAIState(AIState.ATTACK, inGame.getShip());
			vector.x = vector2.x + thargonSpawnMatrix[i * 2]     * SPAWN_INTER_SHIP_DISTANCE;
			vector.y = vector2.y + thargonSpawnMatrix[i * 2 + 1] * SPAWN_INTER_SHIP_DISTANCE;
			mother.addActiveThargon(thargon);
			thargon.setMother(mother);
			thargon.addDestructionCallback(new DestructionCallback() {				
				private static final long serialVersionUID = -7756830551169201213L;

				@Override
				public void onDestruction() {
					if (mother.getHullStrength() > 0) {
						mother.removeActiveThargon(thargon);
					}
				}

				@Override
				public int getId() {
					return 10;
				}
			});
			mother.addDestructionCallback(new DestructionCallback() {				
				private static final long serialVersionUID = -2344244194022201965L;

				@Override
				public void onDestruction() {
					for (Thargon thargon: mother.getActiveThargons()) {
						if (thargon.getHullStrength() > 0) {
							thargon.setAIState(AIState.FLY_STRAIGHT, -thargon.getSpeed());
						}
					}
				}

				@Override
				public int getId() {
					return 11;
				}
			});
		}
	}

	public void setPaused(boolean p) {		
		conditionRedTimer.pause(p);
		traderTimer.pause(p);
		asteroidTimer.pause(p);
		shuttleOrTransportTimer.pause(p);
		viperTimer.pause(p);
	}
}
