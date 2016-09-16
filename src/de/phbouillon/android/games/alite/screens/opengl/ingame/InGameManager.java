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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import android.graphics.Rect;
import android.opengl.GLES11;
import android.opengl.Matrix;
import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AccelerometerHandler;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.LegalStatus;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.generator.enums.Government;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.model.statistics.WeaponType;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.ShipIntroScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.IMethodHook;
import de.phbouillon.android.games.alite.screens.opengl.HyperspaceScreen;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.Billboard;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.ExplosionBillboard;
import de.phbouillon.android.games.alite.screens.opengl.objects.LaserCylinder;
import de.phbouillon.android.games.alite.screens.opengl.objects.SkySphereSpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AIState;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.MathHelper;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkIII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Missile;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargon;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteHud;
import de.phbouillon.android.games.alite.screens.opengl.sprites.buttons.AliteButtons;

public class InGameManager implements Serializable {	
	private static final long serialVersionUID = -7222644863845482563L;
	private static final boolean DEBUG_OBJECT_DRAW_ORDER = false;
	
	public static final float RADAR_CENTER_X = AliteHud.RADAR_X1 + ((AliteHud.RADAR_X2 - AliteHud.RADAR_X1) >> 1);
	public static final float RADAR_CENTER_Y = AliteHud.RADAR_Y1 + ((AliteHud.RADAR_Y2 - AliteHud.RADAR_Y1) >> 1);
	public static final float RADAR_RADIUS_X = AliteHud.RADAR_X2 - RADAR_CENTER_X;
	public static final float RADAR_RADIUS_Y = AliteHud.RADAR_Y2 - RADAR_CENTER_Y;
	
	public static final long  SAFE_ZONE_RADIUS_SQ     = 19752615936l; // 140544m
	public static final long  EXT_SAFE_ZONE_RADIUS_SQ = 21025000000l; // 145000m
	
	public static boolean     OVERRIDE_SPEED = false;	
	public static boolean     playerInSafeZone = false;
	public static boolean     safeZoneViolated = false;
	
	private transient AliteScreen       postDockingScreen = null;
	private transient IMethodHook       postDockingHook   = null;
	private transient IMethodHook       hyperspaceHook    = null;	
	private transient String            feeText           = null;
	private transient Alite             alite;
	
	private final Vector3f              deltaYawRollPitch     = new Vector3f(0, 0, 0);
	private final Vector3f              tempVector            = new Vector3f(0, 0, -1);
	private final Vector3f              systemStationPosition = new Vector3f(0, 0, 0);
	private final Vector3f              zero                  = new Vector3f(0, 0, 0);
	private final Vector3f              deltaOrientation      = new Vector3f(0, 0, 0);
	
	private final float [][]            tempMatrix = new float[3][16];
	private final float []              viewMatrix = new float[16];		
	private final float []              lightPosition;
	private final float                 aspectRatio;
	
	private final AliteButtons          buttons;
	private final DockingComputerAI     dockingComputerAI;
	private final ObjectPicker          objectPicker;	
	private final SkySphereSpaceObject  skysphere;
			
	private List <AliteObject>          objectsToBeAdded = new ArrayList<AliteObject>();
	private List <DepthBucket>          sortedObjectsToDraw = new ArrayList<DepthBucket>();
	private List <TimedEvent>           timedEvents = new ArrayList<TimedEvent>();
	private List <TimedEvent>           removedTimedEvents = new ArrayList<TimedEvent>();

	private InGameHelper                helper;
	private AliteHud                    hud;
	private SystemData                  initialHyperspaceSystem;
	private LaserManager                laserManager = null;
	private OnScreenMessage             message = new OnScreenMessage();
	private OnScreenMessage             oldMessage;
	private SpaceObject                 missileLock = null;
	private Screen                      newScreen = null;		
	private ScrollingText               scrollingText = null;
	private ObjectSpawnManager          spawnManager;
	private StarDust                    starDust;
	private ViewingTransformationHelper viewingTransformationHelper = new ViewingTransformationHelper();
	private WitchSpaceRender            witchSpace = null;

	private CobraMkIII                  ship;	
	private AliteObject                 planet;
	private AliteObject                 sun;
	private AliteObject                 sunGlow;
	private AliteObject                 station;
	
	private TimedEvent                  hyperspaceTimer = null;
	private TimedEvent                  cloakingEvent = null;
	private TimedEvent                  jammingEvent = null;

	private boolean                     calibrated = false;
	private boolean                     changingSpeed = false;
	private boolean                     destroyed = false;
	private boolean                     ecmJammerActive = false;
	private boolean                     needsSpeedAdjustment = false;
	private boolean                     paused = false;
	private boolean                     planetWasSet = false;
	private boolean                     playerControl = true;	
	private boolean                     targetMissile = false;
	private boolean                     viewDirectionChanged = false;    
    private boolean                     vipersWillEngage = false;
    
	private int                         viewDirection = 0;
	private int                         lastX = -1;
	private int                         lastY = -1;	
	private int                         hudIndex = 0;		
	
	public InGameManager(final Alite alite, AliteHud hud, String skyMap, float [] lightPosition, boolean fromStation, boolean initStarDust) {
		this.alite = alite;
		helper = new InGameHelper(alite, this);
		this.hud = hud;		
		this.lightPosition = lightPosition;
		this.spawnManager = new ObjectSpawnManager(alite, this);
		this.dockingComputerAI = new DockingComputerAI(alite, this);
		alite.getPlayer().getCobra().resetEnergy();

		skysphere = new SkySphereSpaceObject(alite, "skysphere", 8000.0f, 16, 16, skyMap);
		ship = new CobraMkIII(alite);
		ship.setPlayerCobra(true);
		ship.setName("Camera");
		
		MathHelper.getRandomPosition(FlightScreen.PLANET_POSITION, tempVector, 115000.0f, 20000.0f).copy(systemStationPosition);
		if (fromStation) {
			tempVector.scale(-1500.0f);
			tempVector.add(systemStationPosition);
			ship.setPosition(tempVector);
			ship.setForwardVector(new Vector3f(0.0f, 0.0f, 1.0f));
			ship.setUpVector(new Vector3f(0.0f, 1.0f, 0.0f));
			ship.setRightVector(new Vector3f(1.0f, 0.0f, 0.0f));
		} else {
			ship.setPosition(FlightScreen.SHIP_ENTRY_POSITION);
		}		
		if (initStarDust && Settings.particleDensity > 0) {
			starDust = new StarDust(alite, ship.getPosition());
		}
		this.buttons = hud != null ? new AliteButtons(alite, ship, this) : null;		
		laserManager = new LaserManager(alite, this);
		timedEvents.addAll(laserManager.registerTimedEvents());
		Rect visibleArea = ((AndroidGraphics) alite.getGraphics()).getVisibleArea();
		aspectRatio = (float) visibleArea.width() / (float) visibleArea.height();		 
		spawnManager.startSimulation(alite.getPlayer().getCurrentSystem());			
		alite.getCobra().setMissileLocked(false);		
		objectPicker = new ObjectPicker(this, visibleArea);		
		AccelerometerHandler.needsCalibration = true;
	}
	
	void initializeViperAction() {
		safeZoneViolated = false;
	    SystemData currentSystem = alite.getPlayer().getCurrentSystem();
	    if (alite.getPlayer().getLegalStatus() == LegalStatus.CLEAN) {
	    	vipersWillEngage = false;
	    	return;
	    }
	    if (currentSystem != null) {	    	
	    	vipersWillEngage = true;
	    	if (getStation() != null) {	    		
	    		int hitCount = ((SpaceStation) getStation()).getHitCount();
	    		if (hitCount > 1) {
	    			safeZoneViolated = true;
	    			vipersWillEngage = true;
	    		}
	    	}
	    	Government government = currentSystem.getGovernment();
	    	if ((government == Government.ANARCHY ||
	    		government == Government.FEUDAL) && !safeZoneViolated) {
	    		vipersWillEngage = false;
	    	}	    	
	    	int roll = (int) (Math.random() * 100);
	    	if (!safeZoneViolated && roll >= alite.getPlayer().getLegalProblemLikelihoodInPercent()) {
	    		vipersWillEngage = false;
	    	}
	    	AliteLog.d("Checking Viper Attack", "Roll: " + roll + ", Likelihood: " + alite.getPlayer().getLegalProblemLikelihoodInPercent() + ", Result: " + vipersWillEngage);
	    }
	}

	public LaserManager getLaserManager() {
		return laserManager;
	}
	
	void clearObjectTransformations() {
		if (viewingTransformationHelper != null) {
			viewingTransformationHelper.clearObjects(sortedObjectsToDraw);
		}
	}
	
	float getAspectRatio() {
		return aspectRatio;
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			in.defaultReadObject();
			this.alite     = Alite.get();
			if (spawnManager != null && timedEvents != null) {
				spawnManager.initTimedEvents(this);
			}
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	public void addScoopCallback(ScoopCallback callback) {
		if (helper != null) {
			helper.setScoopCallback(callback);		
		}
	}

	public ScoopCallback getScoopCallback() {
		if (helper != null) {
			return helper.getScoopCallback();		
		}
		return null;
	}

	void preMissionCheck() {
		for (Mission m: MissionManager.getInstance().getMissions()) {
			if (m.willStartOnDock()) {
				TimedEvent te = m.getPreStartEvent(this);
				if (te != null) {
					addTimedEvent(te);
				}
			}
		}		
	}
	
	AliteScreen getPostDockingScreen() {
		if (postDockingScreen == null) {
			postDockingScreen = new StatusScreen(alite);
		}
		return postDockingScreen;
	}
	
	public void setPostDockingHook(IMethodHook hook) {
		postDockingHook = hook;
	}
	
	public IMethodHook getPostDockingHook() {
		return postDockingHook;
	}
	
	public void setPostDockingScreen(AliteScreen screen) {
		postDockingScreen = screen;
	}

	public Screen getActualPostDockingScreen() {
		return postDockingScreen;
	}
	
	public void setMessage(String text) {
		message.setText(text);
	}
	
	public void repeatMessage(String text, int times) {
		message.repeatText(text, 1000000000l, times);
	}

	public AliteHud getHud() {
		return hud;
	}
		
	Vector3f getSystemStationPosition() {
		return systemStationPosition;
	}
	
	void initStarDust() {
		if (starDust != null) {
			starDust.setPosition(ship.getPosition());
		}
	}
	
	public boolean isDockingComputerActive() {
		return dockingComputerAI.isActive();
	}
	
	public void toggleDockingComputer(boolean playSound) {
		if (!((SpaceStation) getStation()).accessAllowed()) {
			if (playSound) {
				SoundManager.play(Assets.com_accessDeclined);
			}			
			return;
		}
		if (dockingComputerAI.isActive()) {
			if (playSound) {
				SoundManager.play(Assets.com_dockingComputerDisengaged);
			}
			if (hud != null) {
				hud.mapDirections(false, false, false, false);
			}
			dockingComputerAI.disengage();
		} else {
			if (playSound) {
				SoundManager.play(Assets.com_dockingComputerEngaged);
			}
			dockingComputerAI.engage();
		}
	}

	public void toggleStationHandsDocking() {
		if (!((SpaceStation) getStation()).accessAllowed()) {
			SoundManager.play(Assets.com_accessDeclined);
			return;
		}
		if (feeText != null || dockingComputerAI.isActive()) {
			// Once station hands initiated docking, there's nothing you can do
			// to stop it again...
			return; 
		} else {
		  long dockingFee = Math.max(alite.getPlayer().getCurrentSystem().getStationHandsDockingFee(), (long) (alite.getPlayer().getCash() * 0.1f));
			feeText = String.format("The fee for assisted docking is %s.%s Cr. Accept?", dockingFee / 10, dockingFee % 10);
		}
	}

	private void initiateStationHandsDocking() {
		// TODO different sound ("Transaction has been received. Lean back and enjoy the flight.")
		SoundManager.play(Assets.com_dockingComputerEngaged);
		dockingComputerAI.engage();
	}
	
	public void yesSelected() {
		if (feeText == null) {
			return;
		}
		feeText = null;
		SoundManager.play(Assets.click);
		long dockingFee = Math.max(alite.getPlayer().getCurrentSystem().getStationHandsDockingFee(), (long) (alite.getPlayer().getCash() * 0.1f));
		alite.getPlayer().setCash(alite.getPlayer().getCash() - dockingFee);
		initiateStationHandsDocking();
	}
	
	public void noSelected() {
		if (feeText == null) {
			return;
		}
		SoundManager.play(Assets.click);
		feeText = null;
	}
	
	void setPlanet(AliteObject planet) {
		this.planet = planet;
	}
	
	public AliteObject getPlanet() {
		return planet;
	}
	
	void setSun(AliteObject sun) {
		this.sun = sun;
	}
	
	void setSunGlow(AliteObject sunGlow) {
		this.sunGlow = sunGlow;
	}
	
	public AliteObject getSun()  {
		return sun;
	}
	
	public AliteObject getSunGlow() {
		return sunGlow;
	}
	
	void setStation(AliteObject station) {
		this.station = station;
	}
	
	public AliteObject getStation() {
		return station;
	}
	
	void yankOutOfTorus() {
		getShip().setSpeed(-PlayerCobra.MAX_SPEED);
		setPlayerControl(true);
	}

	public void setPlayerControl(boolean playerControl) {
		this.playerControl = playerControl;
		deltaYawRollPitch.x = 0.0f;
		deltaYawRollPitch.y = 0.0f;
		deltaYawRollPitch.z = 0.0f;
	}
	
	public boolean isPlayerControl() {
		return playerControl;
	}
	
	public void killHud() {
		this.hud = null;
	}
	
	public boolean isPlayerAlive() {
		return hud != null;
	}
	
	public ObjectSpawnManager getSpawnManager() {
		return spawnManager;
	}
	
	void addTimedEvent(final TimedEvent event) {
		timedEvents.add(event);
	}
			
	private final float clamp(float val, float min, float max) {
		float result = val < min ? min : val > max ? max : val;
		return result;
	}

	public CobraMkIII getShip() {
		return ship;
	}
	
	public void calibrate() {
		// Ensures that a re-calibration occurs on the next frame.
		calibrated = false;
		AccelerometerHandler.needsCalibration = true;
	}
		
	private void getAlternativeAccelerometerData() {
		if (!calibrated) {			
			zero.x = alite.getInput().getAccelX();
			zero.y = alite.getInput().getAccelY();
			zero.z = alite.getInput().getAccelZ();
			calibrated = true;
		} else {
			deltaOrientation.x = -clamp(((int) ((alite.getInput().getAccelX() - zero.x) * 10.0f)) / 4.0f, -2.0f, 2.0f);
			deltaOrientation.y =  clamp(((int) ((alite.getInput().getAccelY() - zero.y) * 50.0f)) / 10.0f, -2.0f, 2.0f);
			deltaOrientation.z = -clamp(((int) ((alite.getInput().getAccelZ() - zero.z) * 50.0f)) / 10.0f, -2.0f, 2.0f);
			
			deltaYawRollPitch.z = /*(float) (30.0f * Math.PI / 180.0f) **/ deltaOrientation.z;
			if (Settings.reversePitch) {
				deltaYawRollPitch.z = -deltaYawRollPitch.z;
			}
			deltaYawRollPitch.y = /*(float) (30.0f * Math.PI / 180.0f) **/ deltaOrientation.y;
		}				
	}

	private void getAccelerometerData() {
		if (!calibrated) {			
			calibrated = true;
		}
		float accelY = alite.getInput().getAccelY();
		float accelZ = alite.getInput().getAccelZ();
		
		deltaYawRollPitch.x = 0;
		deltaYawRollPitch.y = -clamp((int) (accelY * 50.0f) / 10.0f, -2.0f, 2.0f);
		deltaYawRollPitch.z =  clamp((int) (accelZ * 30.0f) / 10.0f, -2.0f, 2.0f);
					
		if (Settings.reversePitch) {
			deltaYawRollPitch.z = -deltaYawRollPitch.z;
		}
	}
	
	private void getHudControlData() {
		if (hud != null) {
			deltaYawRollPitch.z = hud.getZ();
			if (Settings.reversePitch) {
				deltaYawRollPitch.z = -deltaYawRollPitch.z;
			}
			deltaYawRollPitch.y = hud.getY();
		} 		
	}
		
	private void updateShipOrientation() {
		if (!playerControl) {
			return;
		}
		switch (Settings.controlMode) {
			case ACCELEROMETER: getAccelerometerData(); break;
			case ALTERNATIVE_ACCELEROMETER: getAlternativeAccelerometerData(); break;
			case CONTROL_PAD: getHudControlData(); break;
			case CURSOR_BLOCK: getHudControlData(); break;
			case CURSOR_SPLIT_BLOCK: getHudControlData(); break;
		}
	}
			
	void terminateToTitleScreen() {
		SoundManager.stopAll();
		witchSpace = null;
		message.clearRepetition();
		try {
			alite.getFileUtils().autoLoad(alite);
		} catch (IOException e) {
			AliteLog.e("Game Over", "Cannot reset commander to last autosave. Resetting.", e);
			alite.getPlayer().reset(); 
		}
		alite.getNavigationBar().setFlightMode(false);
		newScreen = new ShipIntroScreen(alite);		
	}
				
	public void terminateToStatusScreen() {
		SoundManager.stopAll();
		witchSpace = null;
		message.clearRepetition();
		alite.getNavigationBar().setFlightMode(false);
		try {
			AliteLog.d("[ALITE]", "Performing autosave. [Docked]");
			alite.getFileUtils().autoSave(alite);
		} catch (Exception e) {
			AliteLog.e("[ALITE]", "Autosaving commander failed.", e);
		}
		newScreen = new StatusScreen(alite);		
	}

	public void addObject(AliteObject object) {
		objectsToBeAdded.add(object);
	}

	private synchronized void spawnMissile(SpaceObject so) {
		Missile missile = helper.spawnMissile(so, getShip());
        message.repeatText("Incoming Missile", 2000000000l);
        missile.addDestructionCallback(new DestructionCallback() {
			private static final long serialVersionUID = -4168441227358105959L;

			@Override
			public void onDestruction() {
				message.clearRepetition();
			}

			@Override
			public int getId() {
				return 7;
			}
        });		
	}
	
	private synchronized void spawnObjects(AliteObject ao) {
		if (ao instanceof SpaceObject) {
			for (ShipType st: ((SpaceObject) ao).getObjectsToSpawn()) {
				switch (st) {
					case Missile: spawnMissile((SpaceObject) ao); break;
					case EscapeCapsule: helper.launchEscapeCapsule((SpaceObject) ao); break;
					default: AliteLog.d("Unknown ShipType", "Supposed to spawn a " + st + " - but don't know how."); break;
				}
			}
			((SpaceObject) ao).clearObjectsToSpawn();
		}		
	}
	
	private synchronized void updatePlanet(AliteObject ao) {
		float distSq = ao.getPosition().distanceSq(ship.getPosition());
		if (distSq > EXT_SAFE_ZONE_RADIUS_SQ) {
			alite.getCobra().setAltitude(PlayerCobra.MAX_ALTITUDE);
		} else {
			alite.getCobra().setAltitude(PlayerCobra.MAX_ALTITUDE * (distSq / EXT_SAFE_ZONE_RADIUS_SQ));
		}
		playerInSafeZone = witchSpace == null && distSq < SAFE_ZONE_RADIUS_SQ;
		if (hud != null) {
			hud.setSafeZone(playerInSafeZone);
		}
		boolean extendedSafeZone = witchSpace == null && distSq < EXT_SAFE_ZONE_RADIUS_SQ;
		if (extendedSafeZone && spawnManager.isInTorus()) {
			spawnManager.leaveTorus();
			message.setText("Mass locked.");
		}
		if (hud != null) {
			hud.setExtendedSafeZone(extendedSafeZone);
		}		
	}
	
	private synchronized boolean removeObjectIfNecessary(Iterator <AliteObject> objectIterator, AliteObject ao) {
		boolean wasRemoved = false;
		if (ao.mustBeRemoved()) {				
			ao.executeDestructionCallbacks();
			objectIterator.remove();
			wasRemoved = true;
		} 
		if (!wasRemoved && ao instanceof SpaceObject && ((SpaceObject) ao).getHullStrength() <= 0) {
			ao.executeDestructionCallbacks();
			objectIterator.remove();
			wasRemoved = true;
		}
		return wasRemoved;
	}
	
	private synchronized void updateObjects(float deltaTime, List <AliteObject> allObjects) {
		helper.checkShipObjectCollision(allObjects);
		laserManager.update(deltaTime, ship, allObjects, objectsToBeAdded);
		Iterator <AliteObject> objectIterator = allObjects.iterator();
		if (hud != null) {
			buttons.update();
		}
		helper.checkProximity(allObjects);
		if (dockingComputerAI != null && dockingComputerAI.isActive()) {			
			if (!dockingComputerAI.isOnFinalApproach()) {
				helper.checkShipStationProximity();
			}
			if (ship.getProximity() != null && ship.getProximity() != station) {
				float distanceSq = ship.getPosition().distanceSq(ship.getProximity().getPosition());
				if (distanceSq > InGameHelper.STATION_VESSEL_PROXIMITY_DISTANCE_SQ) {
					ship.setProximity(null);
				}
			}			
		}
		
		while (objectIterator.hasNext()) {
			AliteObject ao = objectIterator.next();
			if ("Planet".equals(ao.getName())) {
				updatePlanet(ao);
			}
			spawnObjects(ao);
			if (removeObjectIfNecessary(objectIterator, ao)) {
				continue;
			}
			if (ao instanceof SpaceObject) {
				((SpaceObject) ao).update(deltaTime);
				if (((SpaceObject) ao).getAIState() != AIState.FOLLOW_CURVE) {
					ao.moveForward(deltaTime);
				}
			}
			if (ao.getName().equals("Missile")) {
				helper.handleMissileUpdate((Missile) ao, deltaTime);
				ao.getPosition().sub(getShip().getPosition(), tempVector);
				if (tempVector.lengthSq() > AliteHud.MAX_DISTANCE * AliteHud.MAX_DISTANCE) {
					objectIterator.remove();
				}
			} 
			ao.onUpdate(deltaTime);
		}		
	}
	
	private synchronized void updateTimedEvents() {
		removedTimedEvents.clear();
		long time = System.nanoTime();
		for (TimedEvent event: timedEvents) {
			event.perform(time);
			if (event.mustBeRemoved()) {
				removedTimedEvents.add(event);
			}
		}
		for (TimedEvent event: removedTimedEvents) {
			timedEvents.remove(event);
		}		
	}
	
	private synchronized void handleStationAccessDeclined() {
		if (!((SpaceStation) getStation()).accessAllowed()) {
			if (isDockingComputerActive()) {
				if (hud != null) {
					hud.mapDirections(false, false, false, false);
				}
				dockingComputerAI.disengage();
				SoundManager.playOnce(Assets.com_accessDeclined, 3000);
				message.setText("Access to the station has been declined!");
			}			
		}		
	}
	
	private synchronized void updateRetroRockets(float deltaTime) {
		if (ship.getSpeed() > 0) {
			// Retro rockets decaying
			float newSpeed = ship.getSpeed() * (1.0f - deltaTime + deltaTime / 1.6f);
			if (newSpeed < 100) {
				newSpeed = 0;
			}
			ship.setSpeed(newSpeed);
		}		
	}
	
	public synchronized void performUpdate(float deltaTime, List <AliteObject> allObjects) {	
		if (paused || destroyed || helper == null) {
			return;
		}
		if (hud != null) {
			hud.update(deltaTime);
		}
		if (spawnManager != null && timedEvents != null && spawnManager.needsInitialization()) {
			spawnManager.initTimedEvents(this);
		}

		updateShipOrientation();
		ship.moveForward(deltaTime);				
		ship.onUpdate(deltaTime);
		helper.updatePlayerCondition();
		handleStationAccessDeclined();
		updateRetroRockets(deltaTime);
		
		if (starDust != null) {
			starDust.update(ship.getPosition(), ship.getForwardVector());
		}
		
		laserManager.performUpdate(deltaTime, viewDirection, ship);

		updateTimedEvents();		
		if (dockingComputerAI.isActive() && Settings.dockingComputerSpeed == 2) {
			if (alite.getPlayer().getLegalStatus() == LegalStatus.CLEAN || !vipersWillEngage) {		
				helper.automaticDockingSequence();
			} else if (alite.getPlayer().getCurrentSystem().getGovernment() == Government.ANARCHY || alite.getPlayer().getCurrentSystem().getGovernment() == Government.FEUDAL) {
				if (!safeZoneViolated) {
					helper.automaticDockingSequence();
				}
			}
		}
		updateObjects(deltaTime, allObjects);
		
		for (AliteObject eo: objectsToBeAdded) {
			allObjects.add(eo);
		}
		objectsToBeAdded.clear();
		if ((!isPlayerControl() && dockingComputerAI.isActive()) || needsSpeedAdjustment) {
			if (!isPlayerAlive()) {
				dockingComputerAI.disengage();
			}
			ship.update(deltaTime);
			if (Math.abs(ship.getTargetSpeed() - ship.getSpeed()) < 0.0001) {
				needsSpeedAdjustment = false;
			}
		}
	}
	
	public void setNeedsSpeedAdjustment(boolean b) {
		needsSpeedAdjustment = b;
	}
		
	public void setViewport(int newViewDirection) {
		if (viewDirection == newViewDirection) {
			if (hud.getZoomFactor() > 3.5f) {
				hud.setZoomFactor(1.0f);
			} else {
				hud.zoomIn();
			}
		} else {
			laserManager.setAutoFire(false);
			viewDirection = newViewDirection;
		}
	}
	
	public void toggleZoom() {
		if (hud.getZoomFactor() > 3.5f) {
			hud.setZoomFactor(1.0f);
		} else {
			hud.zoomIn();
		}		
	}
	
	private int computeNewViewport(int x, int y) {
		if (Math.abs(x - RADAR_CENTER_X) / RADAR_RADIUS_X > Math.abs(y - RADAR_CENTER_Y) / RADAR_RADIUS_Y) {
			return x > RADAR_CENTER_X ? PlayerCobra.DIR_RIGHT : PlayerCobra.DIR_LEFT;
		} else {
			return y > RADAR_CENTER_Y ? PlayerCobra.DIR_REAR : PlayerCobra.DIR_FRONT;
		}
	}
	
	public void handleMissileIcons() {
		if (alite.getCobra().isMissileLocked()) {
			alite.getCobra().setMissileLocked(false);
			missileLock = null;
			targetMissile = false;
		} else {
			targetMissile = !targetMissile;
		}
		alite.getCobra().setMissileTargetting(targetMissile);									
	}
	
	public void fireMissile() {
		if (alite.getCobra().isMissileLocked()) {
			if (missileLock == null) {
				// This should not happen, but just in case: Deactivate the missile now.
				alite.getCobra().setMissileLocked(false);
				missileLock = null;
				targetMissile = false;
				laserManager.handleTouchUp(viewDirection, ship);
			} else {
				alite.getCobra().setMissileLocked(false);
				alite.getCobra().setMissiles(alite.getCobra().getMissiles() - 1);
				SoundManager.play(Assets.fireMissile);
				helper.spawnMissile(getShip(), missileLock);
				missileLock = null;
			}
		}		
	}
		
	private void handleSpeedChange(TouchEvent e) {
		if (lastX != -1 && lastY != -1) {
			int diffX = e.x - lastX;
			int diffY = e.y - lastY;
			int ady = Math.abs(diffY);
			int adx = Math.abs(diffX);
			if (adx > 10 || ady > 10 || changingSpeed) {
				if (ady > adx && playerControl && !OVERRIDE_SPEED && ship.getSpeed() <= 0) {
					// No speed change if retro rockets are being fired right now...
					changingSpeed = true;
					float newSpeed = ship.getSpeed() + diffY / 1.4f;
					if (diffY > 0) {
						if (newSpeed > 0.0f) {
							newSpeed = 0.0f;
						}
					} else if (newSpeed < -PlayerCobra.MAX_SPEED) {
						newSpeed = -PlayerCobra.MAX_SPEED;
					}
					ship.setSpeed(newSpeed);
					lastX = e.x;
					lastY = e.y;
				}
				if (adx > ady && playerControl) {					
					if (!Settings.tapRadarToChangeView) {
						if (adx > 500) {
							if (diffX < 0) {
								int vd = viewDirection + 1;
								if (vd == 4) {
									vd = 0;
								}
								setViewport(vd);
							} else {
								int vd = viewDirection - 1;
								if (vd < 0) {
									vd = 3;
								}
								setViewport(vd);
							}
							lastX = e.x;
							lastY = e.y;
							viewDirectionChanged = true;
						}
					} else {
						if (!OVERRIDE_SPEED && ship.getSpeed() <= 0) {
							changingSpeed = true;
							float newSpeed = ship.getSpeed() - diffX;
							if (newSpeed > 0.0f) {
								newSpeed = 0.0f;
							} else if (newSpeed < -PlayerCobra.MAX_SPEED) {
								newSpeed = -PlayerCobra.MAX_SPEED;
							}
							ship.setSpeed(newSpeed);
							lastX = e.x;
							lastY = e.y;							
						}
					}
				}
			}
		}		
	}
		
	public int handleExternalViewportChange(TouchEvent e) {
		if (Settings.tapRadarToChangeView) {
			if (e.type == TouchEvent.TOUCH_UP) {
				if (e.x >= AliteHud.RADAR_X1 && e.x <= AliteHud.RADAR_X2 &&
						e.y >= AliteHud.RADAR_Y1 && e.y <= AliteHud.RADAR_Y2 &&
						isPlayerAlive()) {
					return computeNewViewport(e.x, e.y);
				} 				
			}
		} else {
			if (e.type == TouchEvent.TOUCH_DOWN) {
				lastX = e.x;
				lastY = e.y;
			}
			if (e.type == TouchEvent.TOUCH_DRAGGED) {
				if (lastX != -1 && lastY != -1) {
					int diffX = e.x - lastX;
					int adx = Math.abs(diffX);
					if (adx > 500) {
						if (diffX < 0) {
							int vd = viewDirection + 1;
							if (vd == 4) {
								vd = 0;
							}
							lastX = e.x;
							return vd;
						} else {
							int vd = viewDirection - 1;
							if (vd < 0) {
								vd = 3;
							}
							lastX = e.x;
							return vd;
						}
					}
				}						
			}
			if (e.type == TouchEvent.TOUCH_UP) {
				if (e.x >= AliteHud.RADAR_X1 && e.x <= AliteHud.RADAR_X2 &&
						e.y >= AliteHud.RADAR_Y1 && e.y <= AliteHud.RADAR_Y2 &&
						isPlayerAlive()) {
					return 4;
				}
			}
		}
		return -1;
	}
	
	public boolean handleUI(TouchEvent e) {
		if (hud == null) {
			return false;
		}
		buttons.checkFireRelease(e);
		if (isPlayerControl()) {
			if (hud.handleUI(e)) {
				return false;
			}
		}
		if (!buttons.handleTouch(e)) {
			if (e.type == TouchEvent.TOUCH_DOWN) {
				lastX = e.x;
				lastY = e.y;
			}
			if (e.type == TouchEvent.TOUCH_UP) {
				lastX = -1;
				lastY = -1;
				if (changingSpeed) {
					changingSpeed = false;
				} else {						
					if (e.x >= AliteHud.RADAR_X1 && e.x <= AliteHud.RADAR_X2 &&
							e.y >= AliteHud.RADAR_Y1 && e.y <= AliteHud.RADAR_Y2 &&
							isPlayerAlive()) {
						if (Settings.tapRadarToChangeView) {
							setViewport(computeNewViewport(e.x, e.y));
						} else if (!viewDirectionChanged) {
							toggleZoom();
						}
					} else if (e.x >= AliteHud.ALITE_TEXT_X1 && e.x <= AliteHud.ALITE_TEXT_X2 &&
							   e.y >= AliteHud.ALITE_TEXT_Y1 && e.y <= AliteHud.ALITE_TEXT_Y2) {
						if (alite.getCurrentScreen() instanceof FlightScreen) {
							((FlightScreen) alite.getCurrentScreen()).setPause(true);
						}						
				    } else {	
						SpaceObject picked = objectPicker.handleIdentify(e.x, e.y, sortedObjectsToDraw);
						if (picked != null) {
							SoundManager.play(Assets.identify);
							message.setText(picked.getName());			
						}						
					}
				}
				viewDirectionChanged = false;
				return true;
			}
			if (e.type == TouchEvent.TOUCH_DRAGGED) {
				handleSpeedChange(e);
			}
		} else {
			newScreen = buttons.getNewScreen();
			return true;
		}
		return false;
	}
		
	public Screen getNewScreen() {
		return newScreen;
	}
		
	private void performViewTransformation(float deltaTime) {
		// Kudos to Quelo!! 
		// Thanks for getting me started on OpenGL -- from simply
		// looking at this method, one cannot immediately grasp the
		// complexities of the ideas behind it. 
		// And without Quelo, I certainly would not have understood!
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();		
		
		GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_POSITION, lightPosition, 0); 
		
		if (!paused) {
			ship.applyDeltaRotation((float) Math.toDegrees(deltaYawRollPitch.z * deltaTime),
									(float) Math.toDegrees(deltaYawRollPitch.x * deltaTime),
									(float) Math.toDegrees(deltaYawRollPitch.y * deltaTime));
		}

		ship.orthoNormalize();		
		Matrix.invertM(viewMatrix, 0, ship.getMatrix(), 0);
		GLES11.glLoadMatrixf(viewMatrix, 0);		
	}
			
	private void renderHud() {
		if (hud == null) {
			return;
		}
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glPushMatrix();		
		GLES11.glLoadIdentity();
		Rect visibleArea = ((AndroidGraphics) alite.getGraphics()).getVisibleArea();
		GlUtils.ortho(alite, visibleArea);		
		
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();
		if (playerControl) {
			alite.getCobra().setRotation(deltaYawRollPitch.z, deltaYawRollPitch.y);
		}
		alite.getCobra().setSpeed(ship.getSpeed());
		hud.render();	
		hud.clear();
	}
	
	private void renderButtons() {
		if (hud == null) {
			return;
		}
		buttons.render();
		
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glPopMatrix();
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
	}
		
	public boolean isTargetInCenter() {
		return hud != null && hud.isTargetInCenter(); 
	}

	private boolean isEnemy(AliteObject go) {
		if (!(go instanceof SpaceObject)) {
			return false;
		}
		SpaceObject so = (SpaceObject) go;
		ObjectType type = so.getType();
		if (type == ObjectType.EnemyShip || type == ObjectType.Thargoid || type == ObjectType.Viper) {
			return true;
		}
		if (type == ObjectType.Thargon && (((Thargon) so).getMother() != null && ((Thargon) so).getMother().getHullStrength() > 0)) {
			return true;
		}
		return false;
	}
	
	private void renderHudObject(float deltaTime, AliteObject go) {
		if (go instanceof SpaceObject && ((SpaceObject) go).isCloaked()) {
			return;
		}
		if (go.isVisibleOnHud() && hud != null) {
			Matrix.multiplyMM(tempMatrix[1], 0, viewMatrix, 0, go.getMatrix(), 0);			
			if (go instanceof SpaceObject && ((SpaceObject) go).hasOverrideColor()) {
				hud.setObject(hudIndex++, tempMatrix[1][12], tempMatrix[1][13], tempMatrix[1][14], ((SpaceObject) go).getOverrideColor(), isEnemy(go));
			} else {
				hud.setObject(hudIndex++, tempMatrix[1][12], tempMatrix[1][13], tempMatrix[1][14], go.getHudColor(), isEnemy(go));
			}
		}
		if (go instanceof SpaceObject
				&& ((SpaceObject) go).getType() == ObjectType.SpaceStation
				&& hud != null && !planetWasSet) {
			Matrix.multiplyMM(tempMatrix[1], 0, viewMatrix, 0, go.getMatrix(), 0);
			hud.setPlanet(tempMatrix[1][12], tempMatrix[1][13], tempMatrix[1][14]);
		}
		if ("Planet".equals(go.getName()) && hud != null) {
			if (!playerInSafeZone) {
				Matrix.multiplyMM(tempMatrix[1], 0, viewMatrix, 0, go.getMatrix(), 0);
				hud.setPlanet(tempMatrix[1][12], tempMatrix[1][13], tempMatrix[1][14]);
				planetWasSet = true;
			} else {
				helper.checkAltitudeLowAlert();
			}
		}
		if ("Sun".equals(go.getName()) && hud != null) {
			float distSq = go.getPosition().distanceSq(ship.getPosition());
			if (distSq > EXT_SAFE_ZONE_RADIUS_SQ || witchSpace != null) {
				alite.getCobra().setCabinTemperature(0);
			} else {
				if (spawnManager.isInTorus()) {
					spawnManager.leaveTorus();
					message.setText("Mass locked.");
				}
				alite.getCobra().setCabinTemperature(
						(int) (PlayerCobra.MAX_CABIN_TEMPERATURE - PlayerCobra.MAX_CABIN_TEMPERATURE
							* ((distSq - FlightScreen.SUN_SIZE * FlightScreen.SUN_SIZE) / EXT_SAFE_ZONE_RADIUS_SQ)));
				helper.checkCabinTemperatureAlert(deltaTime);
			}
		}
	}
					
 	private void renderAllObjects(final float deltaTime, final List <DepthBucket> objects) { 		
 		if (witchSpace == null) {
 			GLES11.glPushMatrix();	
 			  skysphere.setPosition(ship.getPosition());
		      GLES11.glMultMatrixf(skysphere.getMatrix(), 0);
		      skysphere.render();
		    GLES11.glPopMatrix();
 		}
		
		if (starDust != null && witchSpace == null) {
			// Now render star dust... 
			GLES11.glPushMatrix();
			GLES11.glMultMatrixf(starDust.getMatrix(), 0);			
			GLES11.glDisable(GLES11.GL_DEPTH_TEST);
			starDust.render();
			GLES11.glEnable(GLES11.GL_DEPTH_TEST);
			GLES11.glPopMatrix();
		}

		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glClear(GLES11.GL_DEPTH_BUFFER_BIT);
		Rect visibleArea = ((AndroidGraphics) alite.getGraphics()).getVisibleArea();
		float aspectRatio = (float) visibleArea.width() / (float) visibleArea.height();		 

		GLES11.glPushMatrix();
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		GlUtils.gluPerspective(alite, 45.0f, aspectRatio, 0.1f, 1.0f);
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);		
		GLES11.glPopMatrix();

		if (DEBUG_OBJECT_DRAW_ORDER) {
			AliteLog.d("----Debugging Objects----", "--------Debugging Objects--------");
			for (DepthBucket bucket: objects) {
				if (bucket.near <= 0 || bucket.far <= 0) {
					AliteLog.d("Bucket error", "[E] Bucket near: " + bucket.near + ", Bucket far: " + bucket.far);
				} else {
					AliteLog.d("Bucket ok", "[O] Bucket near: " + bucket.near + ", Bucket far: " + bucket.far);					
				}
				for (AliteObject go: bucket.sortedObjects) {
					AliteLog.d("  OIB", "  Object: " + go.getName());
				}
			}
			AliteLog.d("----Debugging Objects End----", "--------Debugging Objects End--------");
		}		
		for (DepthBucket bucket: objects) {
			if (bucket.near > 0 && bucket.far > 0) {
				GLES11.glPushMatrix();
				GLES11.glMatrixMode(GLES11.GL_PROJECTION);
				GLES11.glLoadIdentity();
				GlUtils.gluPerspective(alite, 45.0f, aspectRatio, bucket.near, bucket.far);			
				GLES11.glMatrixMode(GLES11.GL_MODELVIEW);			
				GLES11.glPopMatrix();
			}
			GLES11.glClear(GLES11.GL_DEPTH_BUFFER_BIT);			
			for (AliteObject go: bucket.sortedObjects) {
				if (go instanceof SpaceObject && ((SpaceObject) go).isCloaked()) {
					continue;
				}
				if (go instanceof LaserCylinder) {
					laserManager.renderLaser((LaserCylinder) go, ship);
					continue;
				}
				if (go instanceof ExplosionBillboard) {
					((ExplosionBillboard) go).getExplosion().render();
					continue;
				} 
				if (!isPlayerAlive() && go instanceof SpaceObject && ((SpaceObject) go).getType() == ObjectType.SpaceStation) {
					// If the player rams the space station, the station gets in the way of the game over sequence.
					// So we don't draw it.
					continue;
				}
				GLES11.glPushMatrix();
				MathHelper.copyMatrix(tempMatrix[0], viewMatrix);
				if (viewDirection == 0) {
					renderHudObject(deltaTime, go);
				}
				if (go.needsDepthTest()) {
					GLES11.glEnable(GLES11.GL_DEPTH_TEST);
				} else {
					GLES11.glDisable(GLES11.GL_DEPTH_TEST);
				}
				if (go instanceof Geometry) {
					float distSq = 1.0f;
					if (go instanceof SpaceObject) {
						distSq = ship.getPosition().distanceSq(go.getPosition());
					}
					if (go instanceof Billboard) {
						if (go instanceof Billboard) {
							((Billboard) go).update(ship);
						}
						GLES11.glEnable(GLES11.GL_BLEND);
					    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
						GLES11.glDisable(GLES11.GL_CULL_FACE);
					}
					float [] goMatrix;
					if (go instanceof SpaceObject && ((SpaceObject) go).getType() == ObjectType.SpaceStation) {						
						float scale = distSq < EXT_SAFE_ZONE_RADIUS_SQ ? 1.0f : EXT_SAFE_ZONE_RADIUS_SQ / distSq;
						goMatrix = go.getScaledMatrix(scale);
						((SpaceObject) go).scaleBoundingBox(scale);
					} else {
						goMatrix = go.getMatrix();
					}
					GLES11.glMultMatrixf(goMatrix, 0);
					Matrix.multiplyMM(tempMatrix[2], 0, viewMatrix, 0,
							go.getMatrix(), 0);
					((Geometry) go).setDisplayMatrix(tempMatrix[2]);
					if (alite.getCobra().getLaser(viewDirection) != null) {
						if (go instanceof SpaceObject) {
							if ((targetMissile && alite.getCobra().getMissiles() > 0) || (Settings.autoId && !((SpaceObject) go).isIdentified())) {							
								if (laserManager.isUnderCross((SpaceObject) go, ship, viewDirection)) {
									if (targetMissile) {
										AliteLog.d("Targetted", "Targetted " + go.getName());
										setMessage("Missiled locked on "  + go.getName());
										alite.getCobra().setMissileLocked(true);
										missileLock = (SpaceObject) go;
										SoundManager.play(Assets.missileLocked);
										targetMissile = false;
									} else if (Settings.autoId && isPlayerAlive()) {
										SoundManager.play(Assets.identify);
										setMessage(go.getName());										
									}
									((SpaceObject) go).setIdentified();
								}
							}
						}
					}
					((Geometry) go).render();
					if (go instanceof SpaceObject) {
						((SpaceObject) go).renderTargetBox(distSq);
					}
					if (go instanceof Billboard) {
						GLES11.glDisable(GLES11.GL_BLEND);
						GLES11.glEnable(GLES11.GL_CULL_FACE);
					}
				}
				GLES11.glPopMatrix();
			}
		}
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);
		GLES11.glPushMatrix();
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		GlUtils.gluPerspective(alite, 45.0f, aspectRatio, 1.0f, 900000.0f);
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);		
		GLES11.glPopMatrix();		
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);		
	}
			
 	public void renderScroller(final float deltaTime) {
 		GLES11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
 		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT);
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glPushMatrix();		
		GLES11.glLoadIdentity();
		Rect visibleArea = ((AndroidGraphics) alite.getGraphics()).getVisibleArea();
		GlUtils.ortho(alite, visibleArea);		
		
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		message.render(alite);
		if (scrollingText != null) {
			scrollingText.render(deltaTime);
		}
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glPopMatrix();
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
 	}
 	
	public void render(final float deltaTime, final List <AliteObject> objects) {
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT);

		if (destroyed) {
			return;
		}
		if (laserManager == null) {
			return;
		}
		if (hud != null) {
			hud.setViewDirection(viewDirection);
		}
		performViewTransformation(deltaTime);
		viewingTransformationHelper.clearObjects(sortedObjectsToDraw);
		hudIndex = 0;
		planetWasSet = false;
		if (viewDirection != 0) {			
			MathHelper.copyMatrix(viewMatrix, tempMatrix[0]);			
			for (AliteObject go: objects) {
				MathHelper.copyMatrix(tempMatrix[0], viewMatrix);
				renderHudObject(deltaTime, go);
			}
			MathHelper.copyMatrix(tempMatrix[0], viewMatrix);
			viewingTransformationHelper.applyViewDirection(viewDirection, viewMatrix);
		}
		MathHelper.copyMatrix(viewMatrix, tempMatrix[0]);
		
		viewingTransformationHelper.sortObjects(objects, viewMatrix, tempMatrix[2], laserManager.activeLasers, sortedObjectsToDraw, witchSpace != null, ship);
		try {
			renderAllObjects(deltaTime, sortedObjectsToDraw);
		} catch (ConcurrentModificationException e) {
			// Ignore...
			// This can happen if the game state is being paused while the current
			// screen is being rendered. Ignoring it is a bit of a hack, but gets
			// rid of the issue...
		}

		if (hud != null) {
			if (dockingComputerAI.isActive()) {
				hud.mapDirections(alite.getCobra().getRoll() > 0.1,
						          alite.getCobra().getRoll() < -0.1,
						          alite.getCobra().getPitch() < -0.1,
						          alite.getCobra().getPitch() > 0.1);
			}
			renderHud();
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			if (message == null) {
				message = new OnScreenMessage();
			}
			message.render(alite);
			if (scrollingText != null) {
				scrollingText.render(deltaTime);
			} else if (feeText != null) {
				GLES11.glColor4f(0.94f, 0.94f, 0.0f, 0.6f);
				alite.getFont().drawText(feeText, 960, 150, true, 1.0f);
				GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				buttons.renderYesNoButtons();
			}
			if (Settings.displayFrameRate) {
				OnScreenDebug.debugFPS(alite);
			}
			if (Settings.displayDockingInformation) {
				OnScreenDebug.debugDocking(alite, ship, sortedObjectsToDraw);
			}
			renderButtons();			
		} else {
			GLES11.glMatrixMode(GLES11.GL_PROJECTION);
			GLES11.glPushMatrix();		
			GLES11.glLoadIdentity();
			Rect visibleArea = ((AndroidGraphics) alite.getGraphics()).getVisibleArea();
			GlUtils.ortho(alite, visibleArea);
			
			GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
			GLES11.glLoadIdentity();
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			message.render(alite);
			if (scrollingText != null) {
				scrollingText.render(deltaTime);
			}
			GLES11.glMatrixMode(GLES11.GL_PROJECTION);
			GLES11.glPopMatrix();
			GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		}		
	}
	
	public void destroy() {
		destroyed = true;
		if (message != null) {
			message.clearRepetition();
		}
		SoundManager.stopAll();
		if (skysphere != null) {
			skysphere.destroy();
		}
		if (laserManager != null) {
			laserManager.destroy();
		}
		laserManager = null;
		helper = null;
		viewingTransformationHelper = null;
	}
	
	public final boolean traverseObjects(SpaceObjectTraverser traverser) {
		// This method is called only from the AliteButtons update method and hence
		// sortedObjectsToDraw still contains the objects of the last rendered frame.
		for (DepthBucket db: sortedObjectsToDraw) {
			for (AliteObject eo: db.sortedObjects) {
				if (eo instanceof SpaceObject && !eo.mustBeRemoved()) {
					if (traverser.handle((SpaceObject) eo)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	InGameHelper getHelper() {
		return helper;
	}
	
	public void enterWitchSpace() {
		witchSpace = new WitchSpaceRender(alite, this);
		witchSpace.enterWitchSpace();
	}

	public boolean isWitchSpace() {
		return witchSpace != null;
	}
	
	public void escapeWitchSpace() {
		witchSpace = null;
	}
	
	public boolean isHyperdriveMalfunction() {
		return witchSpace != null && witchSpace.isHyperdriveMalfunction();
	}
		
	public boolean isInSafeZone() {
		return hud != null ? hud.isInSafeZone() : false;
	}

	public boolean isInExtendedSafeZone() {
		return hud != null ? hud.isInExtendedSafeZone() : false;
	}

	int getNumberOfObjects(ObjectType type) {
		// This is called only from timed events; hence sortedObjectsToDraw still contains
		// the objects of the last rendered frame.
		int count = 0;
		for (DepthBucket db: sortedObjectsToDraw) {
			for (AliteObject eo: db.sortedObjects) {
				if (eo instanceof SpaceObject && ((SpaceObject) eo).getType().equals(type) && !eo.mustBeRemoved()) {
					count++;
				}
			}
		}
		return count;
	}
	
	SpaceObject getShipInDockingBay() {
		// This is called only from timed events; hence sortedObjectsToDraw still contains
		// the objects of the last rendered frame.
		for (DepthBucket db: sortedObjectsToDraw) {
			for (AliteObject eo: db.sortedObjects) {
				if (eo instanceof SpaceObject && ((SpaceObject) eo).isInBay()) {
					return (SpaceObject) eo;
				}
			}
		}
		return null;
	}
	
	void setPaused(boolean p) {
		this.paused = p;
		spawnManager.setPaused(p);
		if (p) {
			if (oldMessage == null) {
				// If oldMessage is _not_ null, oldMessage already contains the
				// message before the pause; so, setPause is called twice with true.
				// (Can happen during state save/restore).
				oldMessage = message;
			}
			if (scrollingText == null) {
				scrollingText = new ScrollingText(alite);
			}
			message = new OnScreenMessage();
			message.repeatText("Alite is Paused (tap screen to continue)...", 5000000000l, -1, 3000000000l);
		} else {
			scrollingText = null;
			message.clearRepetition();
			message = oldMessage;
			oldMessage = null;
			calibrate();
		}
		for (TimedEvent te: timedEvents) {
			if (p) {
				te.pause();
			} else {
				te.resume();
			}
		}
		if (dockingComputerAI != null) {
			if (paused) {
				dockingComputerAI.pauseMusic();
			} else {
				dockingComputerAI.resumeMusic();
			}
		}
	}

	public void clearMessageRepetition() {
		message.clearRepetition();
	}
	
	public void forceForwardView() {
		viewDirection = 0;
		if (hud != null) {
			hud.setZoomFactor(1);
		}
	}

	public void killHyperspaceJump() {
		if (hyperspaceTimer != null) {
			hyperspaceTimer.pause();
			hyperspaceTimer.setRemove(true);
			hyperspaceTimer = null;
		}
	}
		
	public boolean toggleHyperspaceCountdown(boolean isIntergalactic) {
		if (hyperspaceTimer != null) {
			hyperspaceTimer.pause();
			hyperspaceTimer.setRemove(true);
			hyperspaceTimer = null;
			message.setText("Hyperspace jump aborted.");
			return false;
		} else {
			initialHyperspaceSystem = alite.getPlayer().getHyperspaceSystem();
			hyperspaceTimer = new HyperspaceTimer(this, isIntergalactic);
			timedEvents.add(hyperspaceTimer);
			return true;
		}
	}
		
	public void setCloak(boolean cloaked) {
		if (cloaked) {
			cloakingEvent = new CloakingEvent(this);
			timedEvents.add(cloakingEvent);
		} else {
			cloakingEvent.pause();
			cloakingEvent.setRemove(true);
			cloakingEvent = null;
			message.clearRepetition();
		}
	}
	
	final void performHyperspaceJump(boolean isIntergalactic) {
		if (hyperspaceTimer != null) {
			hyperspaceTimer.pause();
			hyperspaceTimer.setRemove(true);
			hyperspaceTimer = null;			
		}
		newScreen = new HyperspaceScreen(alite, isIntergalactic);
		escapeWitchSpace();
		playerInSafeZone = false;
		alite.getPlayer().setHyperspaceSystem(initialHyperspaceSystem);
	}

	public int getViewDirection() {
		return viewDirection;
	}

	public void explode(SpaceObject so, boolean createCanisters, WeaponType weaponType) {
		laserManager.explode(so, createCanisters, weaponType);
	}

	public void gameOver() {
		laserManager.gameOver(getShip());
	}

	public void computeBounty(SpaceObject destroyedObject, WeaponType weaponType) {
		laserManager.computeBounty(destroyedObject, weaponType);
	}
	
	public void computeScore(SpaceObject destroyedObject, WeaponType weaponType) {
		laserManager.computeScore(destroyedObject, weaponType);
	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void reduceShipEnergy(int i) {
		alite.getCobra().setEnergy(alite.getCobra().getEnergy() - i);
		laserManager.checkEnergyLow();
		if (alite.getCobra().getEnergy() <= 0) {
			gameOver();
		}
	}
	
	public IMethodHook getHyperspaceHook() {
		return hyperspaceHook;
	}
	
	public void setHyperspaceHook(IMethodHook hyperspaceHook) {
		this.hyperspaceHook = hyperspaceHook;
	}

	public void toggleECMJammer() {
		ecmJammerActive = !ecmJammerActive;
		if (ecmJammerActive) {
			jammingEvent = new JammingEvent(this);
			timedEvents.add(jammingEvent);			
		} else {
			jammingEvent.pause();
			jammingEvent.setRemove(true);
			jammingEvent = null;
			message.clearRepetition();			
		}
	}
	
	public boolean isECMJammer() {
		return ecmJammerActive;
	}

	void resetHud() {
		if (hud != null) {
			hud = new AliteHud(alite);
			if (buttons != null) {
				buttons.reset();
			}			
		}
	}
	
	WitchSpaceRender getWitchSpace() {
		return witchSpace;
	}

	void setNewScreen(AliteScreen screen) {
		newScreen = screen;
	}

	DockingComputerAI getDockingComputerAI() {
		return dockingComputerAI;
	}

	void clearMissileLock() {
		missileLock = null;
	}

	OnScreenMessage getMessage() {
		return message;
	}	
	
	public SpaceObject getMissileLock() {
		return missileLock;
	}

	public boolean isVipersWillEngage() {
		return vipersWillEngage;
	}

	public void setVipersWillEngage(boolean b) {
		vipersWillEngage = b;
	}
}