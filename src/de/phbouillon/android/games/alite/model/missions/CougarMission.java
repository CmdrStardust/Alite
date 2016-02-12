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

import de.phbouillon.android.framework.Updater;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.CougarScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.TimedEvent;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.AspMkII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CargoCanister;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Cougar;

public class CougarMission extends Mission {
	public static final int ID = 4;
	
	private char [] galaxySeed;
	private int targetIndex;
	private int state;
	private final Vector3f tempVector = new Vector3f(0, 0, 0);
	private boolean cougarCreated = false;
	
	public CougarMission(Alite alite) {
		super(alite, ID);
	}
	
	public int getState() {
		return state;
	}
	
	class CougarCloakingUpdater implements Updater {
		private static final long serialVersionUID = 6077773193969694018L;
		private long nextUpdateEvent;
		private long lastCheck;
		private final Cougar cougar;
		private boolean cloaked = false;
		
		CougarCloakingUpdater(Cougar cougar) {
			this.cougar = cougar;
			computeNextUpdateTime();			
		}
		
		private void computeNextUpdateTime() {
			lastCheck = System.nanoTime();
			// 6.5 - 12 seconds later.
			nextUpdateEvent = 6500000000l + (long) (Math.random() * 5500000000l);
		}
		
		@Override
		public void onUpdate(float deltaTime) {
			if (System.nanoTime() >= (lastCheck + nextUpdateEvent)) {
				computeNextUpdateTime();
				cloaked = !cloaked;
				cougar.setCloaked(cloaked);
			}
		}		
	}
	
	@Override
	protected boolean checkStart() {
		Player player = alite.getPlayer();
		return !started &&
			   !player.getActiveMissions().contains(this) &&
			   !player.getCompletedMissions().contains(this) &&
			    player.getCompletedMissions().contains(MissionManager.getInstance().get(SupernovaMission.ID)) &&
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
		return new CougarScreen(alite, 0);
	}

	@Override
	public AliteScreen checkForUpdate() {
		return null;
	}
	
	private void spawnCargoCanister(final InGameManager inGame, final Cougar cougar) {
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
		final CargoCanister cargo = new CargoCanister(alite);
		cargo.setContent(EquipmentStore.cloakingDevice);
		cargo.setSpeed(0.0f);
		final float speed = 0.2f + ((cargo.getMaxSpeed() - 0.2f) * (float) Math.random());
		cargo.setPosition(cougar.getPosition().x, cougar.getPosition().y, cougar.getPosition().z);
		cargo.setUpdater(new Updater() {			
			private static final long serialVersionUID = 4203394658109589557L;

			@Override
			public void onUpdate(float deltaTime) {
				cargo.getPosition().copy(tempVector);
				float x = tempVector.x + ix * speed * deltaTime;
				float y = tempVector.y + iy * speed * deltaTime;
				float z = tempVector.z + iz * speed * deltaTime;
				cargo.setPosition(x, y, z);
				cargo.applyDeltaRotation(rx, ry, rz);
			}
		});		
		inGame.addObject(cargo);						
	}
	
	@Override
	public TimedEvent getSpawnEvent(final ObjectSpawnManager manager) {
		boolean result = !positionMatchesTarget(galaxySeed, targetIndex);
		if (state == 1 && result && !cougarCreated) {
			alite.getPlayer().addCompletedMission(this);			
			alite.getPlayer().resetIntergalacticJumpCounter();
			alite.getPlayer().resetJumpCounter();
			return new TimedEvent(4000000000l) {				
				private static final long serialVersionUID = -8640036894816728823L;

				@Override
				public void doPerform() {
					if (cougarCreated) {
						return;
					}
					cougarCreated = true;
					manager.lockConditionRedEvent();
					setRemove(true);
					SoundManager.play(Assets.com_conditionRed);
					manager.getInGameManager().repeatMessage("Condition Red!", 3);
					Vector3f spawnPosition = manager.spawnObject();
					final Cougar cougar = new Cougar(alite);
					AspMkII asp1 = new AspMkII(alite);
					AspMkII asp2 = new AspMkII(alite);
					manager.spawnEnemyAndAttackPlayer(asp1, 0, spawnPosition, true);
					manager.spawnEnemyAndAttackPlayer(cougar, 1, spawnPosition, true);
					manager.spawnEnemyAndAttackPlayer(asp2, 2, spawnPosition, true);
					alite.getPlayer().removeActiveMission(CougarMission.this);
					cougar.setUpdater(new CougarCloakingUpdater(cougar));
					cougar.addDestructionCallback(new DestructionCallback() {						
						private static final long serialVersionUID = -4949764387008051526L;

						@Override
						public void onDestruction() {
							spawnCargoCanister(manager.getInGameManager(), cougar);
							manager.unlockConditionRedEvent();
						}

						@Override
						public int getId() {
							return 1;
						}
					});
				}
			};
		}
		return null;
	}

	@Override
	public String getObjective() {
		return "Destroy the unknown ship.";
	}
}
