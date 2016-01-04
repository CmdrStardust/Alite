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
import de.phbouillon.android.framework.Sound;
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
		
	private void loadSounds() {
		Assets.click                         = game.getAudio().newSound("sound/guiclick.ogg",Sound.SoundType.SOUND_FX);
		Assets.alert                         = game.getAudio().newSound("sound/beep.ogg",Sound.SoundType.SOUND_FX);
		Assets.energyLow                     = game.getAudio().newSound("sound/alert.ogg",Sound.SoundType.SOUND_FX);
		Assets.altitudeLow                   = game.getAudio().newSound("sound/alert.ogg",Sound.SoundType.SOUND_FX);
		Assets.temperatureHigh               = game.getAudio().newSound("sound/alert.ogg",Sound.SoundType.SOUND_FX);
		Assets.criticalCondition             = game.getAudio().newSound("sound/alert.ogg",Sound.SoundType.SOUND_FX);
		Assets.error                         = game.getAudio().newSound("sound/witchabort.ogg",Sound.SoundType.SOUND_FX);
		Assets.kaChing                       = game.getAudio().newSound("sound/buy.ogg",Sound.SoundType.SOUND_FX);
		Assets.fireLaser                     = game.getAudio().newSound("sound/laser.ogg",Sound.SoundType.COMBAT_FX);
		Assets.laserHit                      = game.getAudio().newSound("sound/laserhit.ogg",Sound.SoundType.COMBAT_FX);
		Assets.enemyFireLaser                = game.getAudio().newSound("sound/enemy_laser.ogg",Sound.SoundType.COMBAT_FX);
		Assets.hullDamage                    = game.getAudio().newSound("sound/enemy_laserhit.ogg",Sound.SoundType.COMBAT_FX);
		Assets.shipDestroyed                 = game.getAudio().newSound("sound/explosion.ogg",Sound.SoundType.COMBAT_FX);
		Assets.scooped                       = game.getAudio().newSound("sound/scoop.ogg",Sound.SoundType.SOUND_FX);
		Assets.fireMissile                   = game.getAudio().newSound("sound/missile.ogg",Sound.SoundType.SOUND_FX);
		Assets.missileLocked                 = game.getAudio().newSound("sound/beep.ogg",Sound.SoundType.SOUND_FX);
		Assets.torus                         = game.getAudio().newSound("sound/torus.ogg",Sound.SoundType.SOUND_FX);
		Assets.ecm                           = game.getAudio().newSound("sound/ecm.ogg",Sound.SoundType.SOUND_FX);
		Assets.identify                      = game.getAudio().newSound("sound/boop.ogg",Sound.SoundType.SOUND_FX);
		Assets.retroRocketsOrEscapeCapsuleFired = game.getAudio().newSound("sound/retros.ogg",Sound.SoundType.SOUND_FX);
		Assets.hyperspace                    = game.getAudio().newSound("sound/hyperspace.ogg",Sound.SoundType.SOUND_FX);

		Assets.com_aftShieldHasFailed        = game.getAudio().newSound("sound/computer/aft_shield_failed.ogg",Sound.SoundType.VOICE);
		Assets.com_frontShieldHasFailed      = game.getAudio().newSound("sound/computer/front_shield_failed.ogg",Sound.SoundType.VOICE);
		Assets.com_conditionRed              = game.getAudio().newSound("sound/computer/condition_red.ogg",Sound.SoundType.VOICE);
		Assets.com_dockingComputerEngaged    = game.getAudio().newSound("sound/computer/docking_on.ogg",Sound.SoundType.VOICE);
		Assets.com_dockingComputerDisengaged = game.getAudio().newSound("sound/computer/docking_off.ogg",Sound.SoundType.VOICE);
		Assets.com_hyperdriveMalfunction     = game.getAudio().newSound("sound/computer/hyperdrive_malfunction.ogg",Sound.SoundType.VOICE);
		Assets.com_hyperdriveRepaired        = game.getAudio().newSound("sound/computer/hyperdrive_repaired.ogg",Sound.SoundType.VOICE);
		Assets.com_incomingMissile           = game.getAudio().newSound("sound/computer/incoming_missile.ogg",Sound.SoundType.VOICE);
		Assets.com_laserTemperatureCritical  = game.getAudio().newSound("sound/computer/laser_temperature.ogg",Sound.SoundType.VOICE);
		Assets.com_cabinTemperatureCritical  = game.getAudio().newSound("sound/computer/cabin_temperature.ogg",Sound.SoundType.VOICE);
		Assets.com_targetDestroyed           = game.getAudio().newSound("sound/computer/target_destroyed.ogg",Sound.SoundType.VOICE);		
		Assets.com_fuelSystemMalfunction     = game.getAudio().newSound("sound/mission/3/00.mp3",Sound.SoundType.VOICE);
		Assets.com_accessDeclined            = game.getAudio().newSound("sound/computer/access_declined.ogg",Sound.SoundType.VOICE);
		
		Assets.com_lostDockingComputer       = game.getAudio().newSound("sound/computer/lost_docking.ogg",Sound.SoundType.VOICE);
		Assets.com_lostEcm                   = game.getAudio().newSound("sound/computer/lost_ecm.ogg",Sound.SoundType.VOICE);
		Assets.com_lostEnergyBomb            = game.getAudio().newSound("sound/computer/lost_bomb.ogg",Sound.SoundType.VOICE);
		Assets.com_lostEscapeCapsule         = game.getAudio().newSound("sound/computer/lost_escape.ogg",Sound.SoundType.VOICE);
		Assets.com_lostExtraEnergyUnit       = game.getAudio().newSound("sound/computer/lost_energy.ogg",Sound.SoundType.VOICE);
		Assets.com_lostFuelScoop             = game.getAudio().newSound("sound/computer/lost_fuel_scoop.ogg",Sound.SoundType.VOICE);
		Assets.com_lostGalacticHyperdrive    = game.getAudio().newSound("sound/computer/lost_galactic.ogg",Sound.SoundType.VOICE);
		Assets.com_lostRetroRockets          = game.getAudio().newSound("sound/computer/lost_retro_rockets.ogg",Sound.SoundType.VOICE);
	}
	
	@Override
	public void update(float deltaTime) {
		AliteLog.d("Starting LoadingScreen", "Starting loading screen");
		long m1 = System.currentTimeMillis();
		Assets.aliteLogoSmall = game.getGraphics().newPixmap("alite_logo_small.png", true);
				
		loadSounds();
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
