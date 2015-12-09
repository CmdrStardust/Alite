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

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.TradeScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.SupernovaScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.TimedEvent;
import de.phbouillon.android.games.alite.screens.opengl.objects.SphericalSpaceObject;

public class SupernovaMission extends Mission {
	public static final int ID = 3;
	
	private char [] galaxySeed;
	private int supernovaSystemIndex;
	private int state;
	private final TimedEvent preStartEvent;
	private long startTime = -1;
	
	public SupernovaMission(final Alite alite) {
		super(alite, ID);
		preStartEvent = new TimedEvent(100000000) {
			private static final long serialVersionUID = -6824297537488646688L;

			@Override
			public void doPerform() {
				alite.getCobra().setFuel(alite.getCobra().getFuel() - 1);
				if (alite.getCobra().getFuel() <= 0) {
					setRemove(true);
				}
			}
		};
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	@Override
	protected boolean checkStart() {
		Player player = alite.getPlayer();
		return !started &&
			   !player.getActiveMissions().contains(this) &&
			   !player.getCompletedMissions().contains(this) &&
				player.getCompletedMissions().contains(MissionManager.getInstance().get(ThargoidDocumentsMission.ID)) &&
				player.getIntergalacticJumpCounter() + player.getJumpCounter() >= 64 && 
				player.getCondition() == Condition.DOCKED; 
	}

	@Override
	public boolean willStartOnDock() {
		Player player = alite.getPlayer();
		return !started &&
			   !player.getCompletedMissions().contains(this) &&
				player.getCompletedMissions().contains(MissionManager.getInstance().get(ThargoidDocumentsMission.ID)) &&
				player.getIntergalacticJumpCounter() + player.getJumpCounter() >= 64;		
	}
	
	@Override
	public TimedEvent getPreStartEvent(InGameManager manager) {
		manager.setMessage("Fuel leak");
		SoundManager.play(Assets.com_fuelSystemMalfunction);
		return preStartEvent;
	}
	
	public void setSupernovaSystem(char [] galaxySeed, int target) {
		this.galaxySeed = new char[3];
		for (int i = 0; i < 3; i++) {
			this.galaxySeed[i] = galaxySeed[i];
		}
		this.supernovaSystemIndex = target;
	}
	
	@Override
	protected void acceptMission(boolean accept) {
		alite.getPlayer().addActiveMission(this);
		if (state == 1) {
			alite.getCobra().clearInventory();
			alite.getCobra().addSpecialCargo("Unhappy Refugees", alite.getCobra().isEquipmentInstalled(EquipmentStore.largeCargoBay) ? Weight.tonnes(35) : Weight.tonnes(20));
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
		alite.getCobra().removeSpecialCargo("Unhappy Refugees");
		alite.getCobra().addTradeGood(TradeGoodStore.get().gemStones(), Weight.kilograms(1), 0);
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
		supernovaSystemIndex = dis.readInt();
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
		dos.writeInt(supernovaSystemIndex);
		dos.writeInt(state);
		dos.close();
		bos.close();		
		return bos.toByteArray();
	}

	@Override
	public AliteScreen getMissionScreen() {
		return new SupernovaScreen(alite, 0);
	}

	@Override
	public AliteScreen checkForUpdate() {
		if (alite.getPlayer().getCondition() != Condition.DOCKED || state < 1 || !started || !active) {
			return null;
		}
		if (state == 1 && !positionMatchesTarget(galaxySeed, supernovaSystemIndex)) {
			return new SupernovaScreen(alite, 3);
		} else if (state == 2 && !positionMatchesTarget(galaxySeed, supernovaSystemIndex)) {
			active = false;
			alite.getPlayer().removeActiveMission(this);
			alite.getPlayer().addCompletedMission(this);
		}
		return null;
	}	
	
	@Override
	public boolean performTrade(TradeScreen tradeScreen, Equipment equipment) {
		tradeScreen.setMessage("Sorry - there is no one here to trade with.");
		SoundManager.play(Assets.error);
		return true;
	}
	
	@Override
	public boolean performTrade(TradeScreen tradeScreen, TradeGood tradeGood) {
		tradeScreen.setMessage("Sorry - there is no one here to trade with.");
		SoundManager.play(Assets.error);
		return true;
	}
	
	@Override
	public TimedEvent getSpawnEvent(final ObjectSpawnManager manager) {
		boolean result = positionMatchesTarget(galaxySeed, supernovaSystemIndex);
		if ((state == 1 || state == 2) && result) {
			startTime = -1;
			return new TimedEvent(100000000) {				
				private static final long serialVersionUID = 7855977766031440861L;

				@Override
				public void doPerform() {
					InGameManager inGame = manager.getInGameManager();
					SphericalSpaceObject sun = (SphericalSpaceObject) inGame.getSun(); 
					sun.setNewSize(sun.getRadius() * 1.01f);
					SphericalSpaceObject sunGlow = (SphericalSpaceObject) inGame.getSunGlow();
					sunGlow.setNewSize(sun.getRadius() + 400.0f);
					long passedTime = 0;
					if (startTime == -1) {
						inGame.setMessage("Danger: Supernova");
						SoundManager.repeat(Assets.criticalCondition);
						startTime = System.nanoTime();
					} else {
						passedTime = System.nanoTime() - startTime;
					}
					if (passedTime >= 20000000000l) {
						setRemove(true);
						startTime = -1;
						inGame.gameOver();
					}
				}
			};
		}
		return null;
	}
	
	@Override
	public String getObjective() {
		return "Escape the supernova.";
	}
}
