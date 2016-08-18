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

import android.opengl.Matrix;
import de.phbouillon.android.framework.Sound;
import de.phbouillon.android.framework.Updater;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AIState;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AiStateCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AiStateCallbackHandler;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.WayPoint;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkIII;

final class DockingComputerAI implements AiStateCallbackHandler, Serializable {
	private static final long serialVersionUID = 7044055833923332317L;
	private static final int   DOCKING_DISTANCE         =  6000;
	private static final int   DOCKING_SPEED            =   280;
		
	private boolean active = false;	
	private InGameManager inGame;
	private transient Alite alite;
	private final Vector3f v1 = new Vector3f(0, 0, 0);
	private final DockingComputerAlignmentUpdater dcaUpdater;
	private boolean onFinalApproach = false;

	DockingComputerAI(Alite alite, InGameManager inGame) {
		this.inGame = inGame;
		this.alite = alite;
		this.dcaUpdater = new DockingComputerAlignmentUpdater(inGame);
		this.onFinalApproach = false;
	}
			
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "DockingComputerAI.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "DockingComputerAI.readObject I");
			this.alite     = Alite.get();
			AliteLog.e("readObject", "DockingComputerAI.readObject II");
			if (isActive()) {
				if (Assets.danube == null) {
					Assets.danube = alite.getAudio().newMusic("music/blue_danube.ogg", Sound.SoundType.MUSIC);
				}
				// Do not start music playback here: The game is in paused state, so play music
				// only after resume is called.
			}
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	class DockingComputerAlignmentUpdater implements Updater {
		private static final long serialVersionUID = -6558465914868554472L;
		private final InGameManager inGame;
		private final float [] matrixCopy = new float[16];
		private boolean orientationFound = false;
		
		DockingComputerAlignmentUpdater(final InGameManager inGame) {
			this.inGame = inGame;
			orientationFound = false;
		}
		
		@Override
		public void onUpdate(float deltaTime) {							
			// Step 3: Now that the point between station and planet has been reached,
			//         orient ship towards docking bay.
			Matrix.rotateM(matrixCopy, 0, inGame.getStation().getMatrix(), 0, (float) Math.toDegrees(FlightScreen.SPACE_STATION_ROTATION_SPEED), 0, 0, 1);
			v1.x = matrixCopy[0];
			v1.y = matrixCopy[1];
			v1.z = matrixCopy[2];
			if (orientationFound) {
				inGame.getShip().orientTowards(inGame.getStation(), v1, deltaTime);
			} else {
				inGame.getShip().orientTowardsUsingRollPitch(inGame.getStation(), v1, deltaTime);
			}
			float correctionAngle = inGame.getShip().getForwardVector().angleInDegrees(inGame.getStation().getForwardVector());
			
			// Step 4: Now align ship rotation with docking bay and fly towards the station.		
			if (correctionAngle < 3.0f) {
				orientationFound = true;
			}
			if (orientationFound && inGame.getShip().getSpeed() >= 0) {
				inGame.getShip().adjustSpeed(-DOCKING_SPEED);
			}
			if (inGame.getShip().getProximity() != null) {
				onFinalApproach = false;
				orientationFound = false;
				computePathToStation();
				inGame.getShip().setUpdater(null);				
			}
		}		
	}
	
	final void resumeMusic() {
		if (active) {
			if (Assets.danube == null) {
				Assets.danube = alite.getAudio().newMusic("music/blue_danube.ogg", Sound.SoundType.MUSIC);
			}
			if (Assets.danube == null) {
				inGame.setMessage("The Blue Danube now playing in your head.");
			}
			if (Assets.danube != null && !Assets.danube.isPlaying()) {
				Assets.danube.setLooping(true);
				Assets.danube.play();
			} 
		}
	}
	
	final void pauseMusic() {
		if (Assets.danube != null && Assets.danube.isPlaying()) {
			Assets.danube.pause();
		}
	}
	
	final void engage() {
		if (active) {
			return;			
		}
		onFinalApproach = false;
		active = true;
		inGame.setPlayerControl(false);
		dcaUpdater.orientationFound = false;
		AliteLog.d("DC Speed", "DC Speed = " + Settings.dockingComputerSpeed);
		if (Settings.dockingComputerSpeed == 1) {
			alite.setTimeFactor(PlayerCobra.SPEED_UP_FACTOR);
		}
		if (Assets.danube == null) {
			Assets.danube = alite.getAudio().newMusic("music/blue_danube.ogg", Sound.SoundType.MUSIC);
		}
		if (Assets.danube == null) {
			inGame.setMessage("The Blue Danube now playing in your head.");
		} else {
			Assets.danube.setLooping(true);
			Assets.danube.play();
		}
		computePathToStation();
	}
	
	final void disengage() {
		if (!active) {
			return;
		}
		onFinalApproach = false;
		active = false;
		dcaUpdater.orientationFound = false;
		inGame.getShip().setProximity(null);
		inGame.getShip().clearAiStateCallbackHandler(AiStateCallback.EndOfWaypointsReached);
		inGame.getShip().setAIState(AIState.IDLE, (Object []) null);
		inGame.getShip().setUpdater(null);
		inGame.setNeedsSpeedAdjustment(true);
		inGame.getShip().adjustSpeed(0);		
		inGame.setPlayerControl(true);
		if (Assets.danube != null) {
			Assets.danube.stop();
			Assets.danube.dispose();
			Assets.danube = null;
		}
	}
	
	final boolean isActive() {
		return active;
	}
		
	final boolean isOnFinalApproach() {
		return onFinalApproach;
	}
			
	private void computePathToStation() {
		SpaceObject ship = inGame.getShip();
		AliteObject planet = inGame.getPlanet();
		AliteObject station = inGame.getStation();

		// Step 1: Find point between station and planet,
		//         DOCKING_DISTANCE meters away from the station
		planet.getPosition().sub(station.getPosition(), v1);
		v1.normalize();	
		v1.scale(DOCKING_DISTANCE);
		v1.add(station.getPosition());
		// v1 holds the result of step 1
		
		// Step 2: Fly towards the computed point - evading anything that
		//         might be in the way (the station for example)
		WayPoint [] wps = new WayPoint[] {WayPoint.newWayPoint(v1, station.getUpVector())};	
		wps[0].orientFirst = true;		
		ship.setAIState(AIState.FLY_PATH, (Object []) wps);
		ship.registerAiStateCallbackHandler(AiStateCallback.EndOfWaypointsReached, this);
	}

	@Override
	public void execute(SpaceObject so) {
		final SpaceObject ship = inGame.getShip();

		ship.clearAiStateCallbackHandler(AiStateCallback.EndOfWaypointsReached);
		if (ship.getUpdater() != dcaUpdater) {
			onFinalApproach = true;
			ship.setProximity(null);
			ship.setUpdater(dcaUpdater);
		}
	}
	
	final boolean checkForCorrectDockingAlignment(SpaceObject spaceStation, CobraMkIII ship) {
		// Checks for correct alignment are activated even if the Docking Computer is
		// steering the ship... Maybe risky, but seems to work so far...
		// It should at least be checked if the DC approaches from the correct side and
		// the angle is ok... 				
		if (!((SpaceStation) inGame.getStation()).accessAllowed()) {
			// Ok, you cannot land here at all... Access denied...
			SoundManager.playOnce(Assets.com_accessDeclined, 3000);
			inGame.getMessage().setText("Access to the station has been declined");
			return false;
		}
	
		int v = inGame.getViewDirection();
		float fz = spaceStation.getDisplayMatrix()[10];
		float ux = Math.abs(spaceStation.getDisplayMatrix()[4]);
		if (v == 1) {
			fz = spaceStation.getDisplayMatrix()[8];
			ux = Math.abs(spaceStation.getDisplayMatrix()[6]);
		} else if (v == 2) {
			fz = -spaceStation.getDisplayMatrix()[10];
		} else if (v == 3) {
			fz = -spaceStation.getDisplayMatrix()[8];
			ux = Math.abs(spaceStation.getDisplayMatrix()[6]);
		}
		if (fz < 0.98f) {
			AliteLog.d("Docking Check FAILURE", "fz too low");
			return false;
		}

		float angle = ship.getForwardVector().angleInDegrees(spaceStation.getForwardVector());
		if (Math.abs(angle) > 15.0f) {
			AliteLog.d("Docking Check FAILURE", "too high or too low");
			return false;
		}

		if (ux < 0.95f) {
			AliteLog.d("Docking Check FAILURE", "ux too low");
			return false;
		}
		
		AliteLog.d("Docking Check SUCCESS", "Successfully docked.");
		alite.getCobra().setLaserTemperature(0);
		alite.getCobra().setMissileLocked(false);		
		alite.getCobra().setMissileTargetting(false);
		return true;
	}
}
