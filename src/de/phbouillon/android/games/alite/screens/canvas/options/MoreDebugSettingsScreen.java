package de.phbouillon.android.games.alite.screens.canvas.options;

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

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.missions.ConstrictorMission;
import de.phbouillon.android.games.alite.model.missions.CougarMission;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.missions.SupernovaMission;
import de.phbouillon.android.games.alite.model.missions.ThargoidDocumentsMission;
import de.phbouillon.android.games.alite.model.missions.ThargoidStationMission;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class MoreDebugSettingsScreen extends AliteScreen {
	private Button startConstrictorMission;
	private Button startThargoidDocumentsMission;
	private Button startSupernovaMission;
	private Button startCougarMission;
	private Button startThargoidBaseMission;
	private Button clearMission;
    private Button back;
    
	public MoreDebugSettingsScreen(Game game) {
		super(game);		
	}
		
	@Override
	public void activate() {
		startConstrictorMission = new Button(50, 130, 1620, 100, "Start Constrictor Mission", Assets.titleFont, null);
		startConstrictorMission.setGradient(true);
		startThargoidDocumentsMission = new Button(50, 250, 1620, 100, "Start Thargoid Documents Mission", Assets.titleFont, null);
		startThargoidDocumentsMission.setGradient(true);
		startSupernovaMission = new Button(50, 370, 1620, 100, "Start Supernova Mission", Assets.titleFont, null);
		startSupernovaMission.setGradient(true);
		startCougarMission = new Button(50, 490, 1620, 100, "Start Cougar Mission", Assets.titleFont, null);
		startCougarMission.setGradient(true);
		startThargoidBaseMission = new Button(50, 610, 1620, 100, "Start Thargoid Base Mission", Assets.titleFont, null);
		startThargoidBaseMission.setGradient(true);
		clearMission = new Button(50, 730, 1620, 100, "Clear Active Mission", Assets.titleFont, null);
		clearMission.setGradient(true);
		back = new Button(50, 970, 1620, 100, "Back", Assets.titleFont, null);
		back.setGradient(true);
	}
		
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());		
		displayTitle("More Debug Options");
		
		startConstrictorMission.render(g);
		startThargoidDocumentsMission.render(g);
		startSupernovaMission.render(g);
		startCougarMission.render(g);
		startThargoidBaseMission.render(g);
		clearMission.render(g);
		back.render(g);
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}		

		Alite alite = (Alite) game;
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (startConstrictorMission.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				alite.getCobra().clearSpecialCargo();
				alite.getPlayer().clearMissions();
				alite.getPlayer().setIntergalacticJumpCounter(1);
				alite.getPlayer().setJumpCounter(62);
				MissionManager.getInstance().get(ConstrictorMission.ID).resetStarted();
			} else if (startThargoidDocumentsMission.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);				
				alite.getCobra().clearSpecialCargo();
				alite.getPlayer().clearMissions();
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ConstrictorMission.ID));
				MissionManager.getInstance().get(ThargoidDocumentsMission.ID).resetStarted();
				alite.getPlayer().resetIntergalacticJumpCounter();
				alite.getPlayer().setJumpCounter(63);				
			} else if (startSupernovaMission.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				alite.getCobra().clearSpecialCargo();
				alite.getPlayer().clearMissions();
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ConstrictorMission.ID));
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ThargoidDocumentsMission.ID));
				MissionManager.getInstance().get(SupernovaMission.ID).resetStarted();
				alite.getPlayer().resetIntergalacticJumpCounter();
				alite.getPlayer().setJumpCounter(63);								
			} else if (startCougarMission.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				alite.getCobra().clearSpecialCargo();
				alite.getPlayer().clearMissions();
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ConstrictorMission.ID));
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ThargoidDocumentsMission.ID));
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(SupernovaMission.ID));
				MissionManager.getInstance().get(CougarMission.ID).resetStarted();
				alite.getPlayer().resetIntergalacticJumpCounter();
				alite.getPlayer().setJumpCounter(63);												
			} else if (startThargoidBaseMission.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				alite.getCobra().clearSpecialCargo();
				alite.getPlayer().clearMissions();
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ConstrictorMission.ID));
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(ThargoidDocumentsMission.ID));
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(SupernovaMission.ID));
				alite.getPlayer().addCompletedMission(MissionManager.getInstance().get(CougarMission.ID));
				MissionManager.getInstance().get(ThargoidStationMission.ID).resetStarted();
				alite.getPlayer().resetIntergalacticJumpCounter();
				alite.getPlayer().setJumpCounter(63);																
			} else if (clearMission.isTouched(touch.x, touch.y)) { 
				SoundManager.play(Assets.click);
				alite.getPlayer().getActiveMissions().clear();
				String completedMissions = "Completed Missions: ";
				for (Mission m: alite.getPlayer().getCompletedMissions()) {
					completedMissions += m.getClass().getName() + "; ";
				}
				setMessage(completedMissions);				
			} else if (back.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new DebugSettingsScreen(game);
			}
		}
	}
		
	@Override
	public int getScreenCode() {
		return ScreenCodes.MORE_DEBUG_OPTIONS_SCREEN;
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		alite.setScreen(new MoreDebugSettingsScreen(alite));
		return true;
	}		
}
