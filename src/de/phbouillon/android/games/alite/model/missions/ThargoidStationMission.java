package de.phbouillon.android.games.alite.model.missions;

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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.ThargoidStationScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.TimedEvent;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AIState;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AiStateCallbackHandler;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargoid;

public class ThargoidStationMission extends Mission {
	public static final int ID = 5;
	
	private char [] galaxySeed;
	private int targetIndex;
	private int state;
	
	private TimedEvent conditionRedEvent;

	class LaunchThargoidFromStationEvent extends TimedEvent {
		private static final long serialVersionUID = 8124490360245596874L;
		private final ObjectSpawnManager spawnManager;
		private final int numberOfThargoidsToSpawn;
		
		public LaunchThargoidFromStationEvent(final ObjectSpawnManager spawnManager, long delayInNanoSeconds, int numberOfThargoidsToSpawn) {
			super(delayInNanoSeconds);
			this.spawnManager = spawnManager;
			this.numberOfThargoidsToSpawn = numberOfThargoidsToSpawn;
		}

		@Override
		public void doPerform() {
			if (numberOfThargoidsToSpawn == 0 || state != 2) {
				return;
			}
			final Thargoid thargoid = new Thargoid(alite);
			spawnManager.launchFromBay(thargoid, new AiStateCallbackHandler() {
				private static final long serialVersionUID = 3546094888645182119L;

				@Override
				public void execute(SpaceObject so) {
					thargoid.setUpdater(null);
					thargoid.setInBay(false);
					thargoid.setIgnoreSafeZone(true);
					thargoid.setAIState(AIState.ATTACK, spawnManager.getInGameManager().getShip());
				}
			});
		}		
	}
	
	public ThargoidStationMission(Alite alite) {
		super(alite, ID);
	}
	
	public int getState() {
		return state;
	}
	
	@Override
	protected boolean checkStart() {
		Player player = alite.getPlayer();
		return !started &&
			   !player.getActiveMissions().contains(this) &&
			   !player.getCompletedMissions().contains(this) &&
			    player.getCompletedMissions().contains(MissionManager.getInstance().get(CougarMission.ID)) &&
				player.getIntergalacticJumpCounter() + player.getJumpCounter() >= 64 && 
				player.getCondition() == Condition.DOCKED; 
	}

	public void setTarget(char [] galaxySeed, int target, int state) {
		this.galaxySeed = new char[3];
		for (int i = 0; i < 3; i++) {
			this.galaxySeed[i] = galaxySeed[i];
		}
		this.targetIndex = target;
		this.state = state;
	}
	
	@Override
	protected void acceptMission(boolean accept) {
		// The player can't decline this mission...
		alite.getPlayer().addActiveMission(this);
		state = 1;			
	}
	
	@Override
	public void onMissionAccept() {
	}

	@Override
	public void onMissionDecline() {
	}

	@Override
	public void onMissionComplete() {
		active = false;
		alite.getCobra().addEquipment(EquipmentStore.ecmJammer);
	}

	@Override
	public void onMissionUpdate() {		
	}

	@Override
	public void load(DataInputStream dis) throws IOException {	
		galaxySeed = new char[3];
		galaxySeed[0] = dis.readChar();
		galaxySeed[1] = dis.readChar();
		galaxySeed[2] = dis.readChar();
		targetIndex = dis.readInt();
		state = dis.readInt();
		active = true;
		started = true;
	}

	@Override
	public byte [] save() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeChar(galaxySeed[0]);
		dos.writeChar(galaxySeed[1]);
		dos.writeChar(galaxySeed[2]);
		dos.writeInt(targetIndex);
		dos.writeInt(state);
		dos.close();
		bos.close();		
		return bos.toByteArray();
	}

	@Override
	public AliteScreen getMissionScreen() {
		return new ThargoidStationScreen(alite, 0);
	}

	@Override
	public AliteScreen checkForUpdate() {
		if (alite.getPlayer().getCondition() != Condition.DOCKED || state < 1 || !started || !active) {
			return null;
		}
		if (state == 3) {
			return new ThargoidStationScreen(alite, 1);
		} 
		return null;
	}
	
	
	@Override
	public TimedEvent getSpawnEvent(final ObjectSpawnManager manager) {
		boolean result = positionMatchesTarget(galaxySeed, targetIndex);
		if ((state == 1 && !result) || (state == 2 && result)) {
			// !result => Any system but the one where the player received the mission
			// result => The precise system where the Alien Space Station
			// was when the player first saw it. He escaped and now tries again... 
			state = 2;
			return new TimedEvent(10000000000l) {				
				private static final long serialVersionUID = 5516217861394636289L;

				@Override
				public void doPerform() {
					manager.getInGameManager().getStation().setName("Alien Space Station");		
					((SpaceObject) manager.getInGameManager().getStation()).setHullStrength(1024);
					((SpaceStation) manager.getInGameManager().getStation()).denyAccess();
					manager.getInGameManager().getStation().addDestructionCallback(new DestructionCallback() {				
						private static final long serialVersionUID = 6715650816893032921L;

						@Override
						public void onDestruction() {
							state = 3;
						}

						@Override
						public int getId() {
							return 2;
						}
					});
					setRemove(true);
				}
			};
		} 
		return null;
	}

	private void spawnThargoids(final ObjectSpawnManager manager) {
		if (manager.isInTorus()) {
			manager.leaveTorus();				
		}		
		SoundManager.play(Assets.com_conditionRed);
		manager.getInGameManager().repeatMessage("Condition Red!", 3);
		conditionRedEvent.pause();
		Vector3f spawnPosition = manager.spawnObject();	
		int thargoidNum = alite.getPlayer().getRating().ordinal() < 3 ? 1 : Math.random() < 0.5 ? 2 : 3;
		for (int i = 0; i < thargoidNum; i++) {
			Thargoid thargoid = new Thargoid(alite);
			thargoid.setSpawnThargonDistanceSq(manager.computeSpawnThargonDistanceSq());
			manager.spawnEnemyAndAttackPlayer(thargoid, i, spawnPosition, true);
		}						
	}

	@Override
	public TimedEvent getConditionRedSpawnReplacementEvent(final ObjectSpawnManager manager) {
		if (state != 2) {
			return null;
		}
		long delayToConditionRedEncounter = (long) ((((float) (2 << 9)) / 16.7f) * 1000000000l);
		conditionRedEvent = new TimedEvent(delayToConditionRedEncounter) {
			private static final long serialVersionUID = 7815560584428889246L;

			@Override
			public void doPerform() {
				spawnThargoids(manager);
			}			
		};
		return conditionRedEvent;
	}
	
	@Override
	public TimedEvent getViperSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		if (state == 2 && objectSpawnManager.getInGameManager().isInSafeZone()) {			
			return new LaunchThargoidFromStationEvent(objectSpawnManager, objectSpawnManager.getDelayToViperEncounter(), 0);
		}
		return null;
	}
	
	@Override
	public TimedEvent getShuttleSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		if (state == 2) {
			return new LaunchThargoidFromStationEvent(objectSpawnManager, objectSpawnManager.getDelayToShuttleEncounter(), 0);
		}
		return null;
	}
		
	@Override
	public TimedEvent getTraderSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		if (state == 2) {
			return new LaunchThargoidFromStationEvent(objectSpawnManager, objectSpawnManager.getDelayToViperEncounter(), 1);
		}
		return null;
	}

	@Override
	public String getObjective() {
		return "Destroy the Thargoid base.";
	}
}
