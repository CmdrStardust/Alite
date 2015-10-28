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
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.ThargoidDocumentsScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.TimedEvent;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargoid;

public class ThargoidDocumentsMission extends Mission {
	public static final int ID = 2;
	
	private char [] galaxySeed;
	private int targetIndex;
	private int state;
	
	private TimedEvent conditionRedEvent;
	
	public ThargoidDocumentsMission(Alite alite) {
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
				player.getCompletedMissions().contains(MissionManager.getInstance().get(ConstrictorMission.ID)) &&
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
			alite.getCobra().setMissiles(4);
			alite.getCobra().setFuel(70);
			alite.getCobra().addSpecialCargo("Thargoid Documents", Weight.grams(482));
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
		alite.getCobra().removeSpecialCargo("Thargoid Documents");
		alite.getCobra().removeEquipment(EquipmentStore.extraEnergyUnit);
		alite.getCobra().addEquipment(EquipmentStore.navalEnergyUnit);
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
		return new ThargoidDocumentsScreen(alite, 0);
	}

	@Override
	public AliteScreen checkForUpdate() {
		if (alite.getPlayer().getCondition() != Condition.DOCKED || state < 1 || !started || !active) {
			return null;
		}
		if (state == 1 && positionMatchesTarget(galaxySeed, targetIndex)) {
			return new ThargoidDocumentsScreen(alite, 2);
		} 
		return null;
	}
	
	private void spawnThargoids(final ObjectSpawnManager manager) {
		if (manager.isInTorus()) {
			int randByte = (int) (Math.random() * 256);
			if ((0 << 5) > randByte) {
				return;
			} else {
				manager.leaveTorus();				
			}
		}		
		SoundManager.play(Assets.com_conditionRed);
		manager.getInGameManager().repeatMessage("Condition Red!", 3);
		conditionRedEvent.pause();
		Vector3f spawnPosition = manager.spawnObject();	
		int thargoidNum = alite.getPlayer().getRating().ordinal() < 3 ? 1 : Math.random() < 0.5 ? 1 : 2;
		for (int i = 0; i < thargoidNum; i++) {
			Thargoid thargoid = new Thargoid(alite);
			thargoid.setSpawnThargonDistanceSq(manager.computeSpawnThargonDistanceSq());
			manager.spawnEnemyAndAttackPlayer(thargoid, i, spawnPosition, true);
		}						
	}
	
	@Override
	public TimedEvent getConditionRedSpawnReplacementEvent(final ObjectSpawnManager manager) {
		long delayToConditionRedEncounter = (long) ((((float) (2 << 9)) / 16.7f) * 1000000000l);
		conditionRedEvent = new TimedEvent(delayToConditionRedEncounter) {
			private static final long serialVersionUID = 4448575917547734318L;

			@Override
			public void doPerform() {
				spawnThargoids(manager);
			}			
		};
		return conditionRedEvent;
	}
	
	@Override
	public boolean willEnterWitchSpace() {
		return Math.random() <= 0.15;
	}
	
	@Override
	public String getObjective() {
		return "Take the documents to " + getTargetName(targetIndex, galaxySeed) + ".";
	}	
}
