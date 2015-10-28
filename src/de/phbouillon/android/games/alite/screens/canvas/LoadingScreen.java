package de.phbouillon.android.games.alite.screens.canvas;

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

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Settings;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class LoadingScreen extends AliteScreen {
	public LoadingScreen(Game game) {
		super(game);	
	}
		
	@Override
	public void activate() {
	}
		
	private void loadSoundFx() {
		Assets.click             = game.getAudio().newSound("sound/guiclick.ogg");
		Assets.alert             = game.getAudio().newSound("sound/beep.ogg");
		Assets.energyLow         = game.getAudio().newSound("sound/alert.ogg");
		Assets.altitudeLow       = game.getAudio().newSound("sound/alert.ogg");
		Assets.temperatureHigh   = game.getAudio().newSound("sound/alert.ogg");
		Assets.criticalCondition = game.getAudio().newSound("sound/alert.ogg");
		Assets.error             = game.getAudio().newSound("sound/witchabort.ogg");
		Assets.kaChing           = game.getAudio().newSound("sound/buy.ogg");
		Assets.fireLaser         = game.getAudio().newSound("sound/laser.ogg");
		Assets.laserHit          = game.getAudio().newSound("sound/laserhit.ogg");
		Assets.enemyFireLaser    = game.getAudio().newSound("sound/enemy_laser.ogg");
		Assets.hullDamage        = game.getAudio().newSound("sound/enemy_laserhit.ogg");
		Assets.shipDestroyed     = game.getAudio().newSound("sound/explosion.ogg");
		Assets.scooped           = game.getAudio().newSound("sound/scoop.ogg");
		Assets.fireMissile       = game.getAudio().newSound("sound/missile.ogg");
		Assets.missileLocked     = game.getAudio().newSound("sound/beep.ogg");
		Assets.torus             = game.getAudio().newSound("sound/torus.ogg");
		Assets.ecm               = game.getAudio().newSound("sound/ecm.ogg");
		Assets.identify          = game.getAudio().newSound("sound/boop.ogg");
		Assets.retroRocketsOrEscapeCapsuleFired = game.getAudio().newSound("sound/retros.ogg");
		Assets.hyperspace        = game.getAudio().newSound("sound/hyperspace.ogg");
	}
	
	private void loadComputerVoice() {
		Assets.com_aftShieldHasFailed        = game.getAudio().newVoice("sound/computer/aft_shield_failed.ogg");
		Assets.com_frontShieldHasFailed      = game.getAudio().newVoice("sound/computer/front_shield_failed.ogg");
		Assets.com_conditionRed              = game.getAudio().newVoice("sound/computer/condition_red.ogg");
		Assets.com_dockingComputerEngaged    = game.getAudio().newVoice("sound/computer/docking_on.ogg");
		Assets.com_dockingComputerDisengaged = game.getAudio().newVoice("sound/computer/docking_off.ogg");
		Assets.com_hyperdriveMalfunction     = game.getAudio().newVoice("sound/computer/hyperdrive_malfunction.ogg");
		Assets.com_hyperdriveRepaired        = game.getAudio().newVoice("sound/computer/hyperdrive_repaired.ogg");
		Assets.com_incomingMissile           = game.getAudio().newVoice("sound/computer/incoming_missile.ogg");
		Assets.com_laserTemperatureCritical  = game.getAudio().newVoice("sound/computer/laser_temperature.ogg");
		Assets.com_cabinTemperatureCritical  = game.getAudio().newVoice("sound/computer/cabin_temperature.ogg");
		Assets.com_targetDestroyed           = game.getAudio().newVoice("sound/computer/target_destroyed.ogg");		
		Assets.com_fuelSystemMalfunction     = game.getAudio().newVoice("sound/mission/3/00.mp3");
		Assets.com_accessDeclined            = game.getAudio().newVoice("sound/computer/access_declined.ogg");
		
		Assets.com_lostDockingComputer       = game.getAudio().newVoice("sound/computer/lost_docking.ogg");
		Assets.com_lostEcm                   = game.getAudio().newVoice("sound/computer/lost_ecm.ogg");
		Assets.com_lostEnergyBomb            = game.getAudio().newVoice("sound/computer/lost_bomb.ogg");
		Assets.com_lostEscapeCapsule         = game.getAudio().newVoice("sound/computer/lost_escape.ogg");
		Assets.com_lostExtraEnergyUnit       = game.getAudio().newVoice("sound/computer/lost_energy.ogg");
		Assets.com_lostFuelScoop             = game.getAudio().newVoice("sound/computer/lost_fuel_scoop.ogg");
		Assets.com_lostGalacticHyperdrive    = game.getAudio().newVoice("sound/computer/lost_galactic.ogg");
		Assets.com_lostRetroRockets          = game.getAudio().newVoice("sound/computer/lost_retro_rockets.ogg");
	}
	
	@Override
	public void update(float deltaTime) {
		AliteLog.d("Starting LoadingScreen", "Starting loading screen");
		long m1 = System.currentTimeMillis();
		Assets.aliteLogoSmall = game.getGraphics().newPixmap("alite_logo_small.png", true);
				
		loadSoundFx();
		loadComputerVoice();
		Settings.load(game.getFileIO());
		
		game.getGraphics().setClip(-1, -1, -1, -1);
			
		AliteLog.d("End LoadingScreen", "End LoadingScreen. Resource load took: " + (System.currentTimeMillis() - m1));
		try {
			if (!((Alite) game).getFileUtils().readState((Alite) game, game.getFileIO())) {
				game.setScreen(new ShipIntroScreen(game));
			}
		} catch (IOException e) {
			game.setScreen(new ShipIntroScreen(game));
		}		
	}

	@Override
	public void present(float deltaTime) {		
	}

	@Override
	public void dispose() {
	}

	@Override
	public void loadAssets() {
	}
	
	@Override
	public void pause() {
	}
	
	@Override
	public void resume() {
	}
	
	@Override
	public void postLayout(Object dataObject) {		
	}
	
	@Override
	public int getScreenCode() {
		return -1;
	}		
}
