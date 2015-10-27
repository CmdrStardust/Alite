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

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.Rating;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.EndMissionScreen;

public class EndMission extends Mission {
	public static final int ID = 6;
	
	public EndMission(Alite alite) {
		super(alite, ID);
	}
		
	@Override
	protected boolean checkStart() {
		Player player = alite.getPlayer();
		return !started &&
			   !player.getActiveMissions().contains(this) &&
			   !player.getCompletedMissions().contains(this) &&
				player.getRating() == Rating.ELITE &&
				player.getCurrentSystem() != null && player.getCurrentSystem().getIndex() == 256 && 
				player.getCondition() == Condition.DOCKED; 
	}

	@Override
	protected void acceptMission(boolean accept) {
		alite.getPlayer().addCompletedMission(this);	
		active = false;
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
	}

	@Override
	public byte [] save() throws IOException {
		return new byte[0];
	}

	@Override
	public AliteScreen getMissionScreen() {
		return new EndMissionScreen(alite, 0);
	}

	@Override
	public AliteScreen checkForUpdate() {
		return null;
	}

	@Override
	public String getObjective() {
		return "";
	}				
}
