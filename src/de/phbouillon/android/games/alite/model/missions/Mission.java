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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.generator.GalaxyGenerator;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.TradeScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.TimedEvent;

public abstract class Mission {
	protected boolean active = false;
	protected boolean started = false;
	protected final Alite alite;
	private final int id;
	private String targetName = null;
	
	protected abstract boolean checkStart();
	protected abstract void acceptMission(boolean accept); 
	public abstract void onMissionAccept();
	public abstract void onMissionDecline();
	public abstract void onMissionComplete();
	public abstract void onMissionUpdate();	
	public abstract AliteScreen getMissionScreen();
	
	public boolean willStartOnDock() {
		return false;
	}
	
	protected Mission(Alite alite, int id) {
		this.alite = alite;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public boolean missionStarts() {
		boolean result = checkStart();
		if (result) {
			active = true;
			started = true;
			return true;
		}
		return false;
	}
	
	public abstract AliteScreen checkForUpdate();
	
	public void setPlayerAccepts(boolean playerAccepts) {
		acceptMission(playerAccepts);
		if (!playerAccepts) {
			active = false;
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public abstract void load(DataInputStream dis) throws IOException;
	public abstract byte [] save() throws IOException;
	
	public final SystemData findMostDistantSystem() {
		int maxDist = -1;
		SystemData current = alite.getPlayer().getCurrentSystem();
		SystemData target = null;
		for (SystemData system: alite.getGenerator().getSystems()) {
			int dist = current.computeDistance(system);
			if (dist > maxDist) {
				maxDist = dist;
				target = system;
			}
		}
		return target;
	}
	
	public final SystemData findRandomSystemInRange(int min, int max) {		
		List <SystemData> candidates = new ArrayList<SystemData>();
		SystemData current = alite.getPlayer().getCurrentSystem();
		for (SystemData system: alite.getGenerator().getSystems()) {
			int dist = current.computeDistance(system);
			if (dist >= min && dist <= max) {
				candidates.add(system);
			}
		}
		if (candidates.isEmpty()) {
			if (current.getIndex() == 0) {
				return alite.getGenerator().getSystem(1);
			} else {
				return alite.getGenerator().getSystem(0);
			}
		} 
		return candidates.get((int) (Math.random() * candidates.size()));
	}
	
	protected boolean positionMatchesTarget(char [] seed, int targetIndex) {
		for (int i = 0; i < 3; i++) {
			if (alite.getGenerator().getCurrentSeed()[i] != seed[i]) {
				return false;
			}
		}
		if (alite.getPlayer().getCurrentSystem() != null) {
			if (targetIndex == -1 || targetIndex == alite.getPlayer().getCurrentSystem().getIndex()) {
				return true;
			}
		}
		return false;
	}
	public TimedEvent getWitchSpaceSpawnEvent(final ObjectSpawnManager manager) {
		return null;
	}
	
	public TimedEvent getSpawnEvent(final ObjectSpawnManager manager) {
		return null;
	}
	
	public TimedEvent getConditionRedSpawnReplacementEvent(final ObjectSpawnManager manager) {
		return null;
	}
		
	public boolean willEnterWitchSpace() {
		return false;
	}
	
	public TimedEvent getPreStartEvent(InGameManager manager) {
		return null;
	}
	
	public boolean performTrade(TradeScreen tradeScreen, Equipment equipment) {
		return false;
	}
	
	public boolean performTrade(TradeScreen tradeScreen, TradeGood tradeGood) {
		return false;
	}
	
	public TimedEvent getViperSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		return null;
	}
	
	public TimedEvent getShuttleSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		return null;
	}
	
	public TimedEvent getAsteroidSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		return null;
	}
	
	public TimedEvent getTraderSpawnReplacementEvent(final ObjectSpawnManager objectSpawnManager) {
		return null;
	}
	
	public abstract String getObjective();
	
	public void resetStarted() {
		started = false;
	}
	
	public void resetTargetName() {
		targetName = null;
	}
	
	protected String getTargetName(int targetIndex, char [] galaxySeed) {
		if (targetName != null) {
			return targetName;
		}
		if (targetIndex != -1) {
			for (int i = 0; i < 3; i++) {
				if (alite.getGenerator().getCurrentSeed()[i] != galaxySeed[i]) {
					GalaxyGenerator gen = new GalaxyGenerator();
					gen.buildGalaxy(galaxySeed[0], galaxySeed[1], galaxySeed[2]);
					targetName = gen.getSystem(targetIndex).getName();
					return targetName;
				}
			}
			targetName = alite.getGenerator().getSystem(targetIndex).getName();
		} else {
			targetName = "<Unknown>";
		}
		return targetName;
	}
}
