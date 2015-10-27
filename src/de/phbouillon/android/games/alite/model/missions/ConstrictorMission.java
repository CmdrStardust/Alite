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
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.ConstrictorScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.TimedEvent;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Constrictor;

public class ConstrictorMission extends Mission {
	public static final int ID = 1;
	
	private char [] galaxySeed;
	private int targetIndex;
	private int state;
	private boolean constrictorCreated = false;
	
	public ConstrictorMission(Alite alite) {
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
				player.getIntergalacticJumpCounter() > 0 &&
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
		resetTargetName();
	}
	
	@Override
	protected void acceptMission(boolean accept) {
		if (accept) {
			alite.getPlayer().addActiveMission(this);			
			state = 1;			
			resetTargetName();
		} else {
			alite.getPlayer().resetIntergalacticJumpCounter();
			alite.getPlayer().resetJumpCounter();
			alite.getPlayer().addCompletedMission(this);
		}
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
		alite.getPlayer().setCash(alite.getPlayer().getCash() + 100000);
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
		resetTargetName();
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
		return new ConstrictorScreen(alite, 0);
	}

	@Override
	public AliteScreen checkForUpdate() {
		if (alite.getPlayer().getCondition() != Condition.DOCKED || state < 1 || !started || !active) {
			return null;
		}
		if (state == 1 && positionMatchesTarget(galaxySeed, targetIndex)) {
			return new ConstrictorScreen(alite, 2);
		} else if (state >= 2 && state <= 5 && positionMatchesTarget(galaxySeed, targetIndex)) {
			return new ConstrictorScreen(alite, 3);
		} else if (state == 6 && positionMatchesTarget(galaxySeed, targetIndex)) {
			// Player arrived at target, but _did not destroy_ the Constrictor...
			// Try again...
			state--;
			return new ConstrictorScreen(alite, 3);
		} else if (state == 7 && positionMatchesTarget(galaxySeed, targetIndex)) {
			return new ConstrictorScreen(alite, 4);
		}
		return null;
	}
	
	@Override
	public TimedEvent getSpawnEvent(final ObjectSpawnManager manager) {
		boolean result = positionMatchesTarget(galaxySeed, targetIndex);
		if (state == 6 && result && !constrictorCreated) {
			return new TimedEvent(0) {				
				private static final long serialVersionUID = 1657605081590177007L;

				@Override
				public void doPerform() {
					constrictorCreated = true;
					setRemove(true);
					SoundManager.play(Assets.com_conditionRed);
					manager.getInGameManager().repeatMessage("Condition Red!", 3);
					Vector3f spawnPosition = manager.spawnObject();		 
					Constrictor constrictor = new Constrictor(alite);
					manager.spawnEnemyAndAttackPlayer(constrictor, 0, spawnPosition, true);
					manager.lockConditionRedEvent();
					constrictor.addDestructionCallback(new DestructionCallback() {
						private static final long serialVersionUID = -7774734879444916116L;

						@Override
						public void onDestruction() {
							state = 7;
							manager.unlockConditionRedEvent();
						}
						
						@Override
						public int getId() {
							return 13;
						}
					});
				}
			};
		}
		return null;
	}
		
	@Override
	public String getObjective() {
		switch (state) {
			case 0: return "";
			case 1: return "Fly to " + getTargetName(targetIndex, galaxySeed) + "."; 
			case 2: return "Perform an intergalactic jump.";
			case 3: return "Fly to " + getTargetName(targetIndex, galaxySeed) + ".";
			case 4: return "Fly to " + getTargetName(targetIndex, galaxySeed) + ".";
			case 5: return "Fly to " + getTargetName(targetIndex, galaxySeed) + ".";
			case 6: return "Fly to " + getTargetName(targetIndex, galaxySeed) + ".";
		}
		return "";
	}
	
	public void setState(int s) {
		this.state = s;
	}
}
