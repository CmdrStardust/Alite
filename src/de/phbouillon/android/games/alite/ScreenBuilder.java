package de.phbouillon.android.games.alite;

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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import de.phbouillon.android.games.alite.screens.canvas.BuyScreen;
import de.phbouillon.android.games.alite.screens.canvas.CatalogScreen;
import de.phbouillon.android.games.alite.screens.canvas.DiskScreen;
import de.phbouillon.android.games.alite.screens.canvas.EquipmentScreen;
import de.phbouillon.android.games.alite.screens.canvas.GalaxyScreen;
import de.phbouillon.android.games.alite.screens.canvas.HackerScreen;
import de.phbouillon.android.games.alite.screens.canvas.HexNumberPadScreen;
import de.phbouillon.android.games.alite.screens.canvas.InventoryScreen;
import de.phbouillon.android.games.alite.screens.canvas.LibraryPageScreen;
import de.phbouillon.android.games.alite.screens.canvas.LibraryScreen;
import de.phbouillon.android.games.alite.screens.canvas.LoadScreen;
import de.phbouillon.android.games.alite.screens.canvas.LocalScreen;
import de.phbouillon.android.games.alite.screens.canvas.PlanetScreen;
import de.phbouillon.android.games.alite.screens.canvas.QuantityPadScreen;
import de.phbouillon.android.games.alite.screens.canvas.SaveScreen;
import de.phbouillon.android.games.alite.screens.canvas.ShipIntroScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.ConstrictorScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.CougarScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.SupernovaScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.ThargoidDocumentsScreen;
import de.phbouillon.android.games.alite.screens.canvas.missions.ThargoidStationScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.AudioOptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.ControlOptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.DebugSettingsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.DisplayOptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.GameplayOptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.InFlightButtonsOptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.MoreDebugSettingsScreen;
import de.phbouillon.android.games.alite.screens.canvas.options.OptionsScreen;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutAdvancedFlying;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutBasicFlying;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutEquipment;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutHud;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutIntroduction;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutNavigation;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutTrading;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.TutorialSelectionScreen;
import de.phbouillon.android.games.alite.screens.opengl.AboutScreen;
import de.phbouillon.android.games.alite.screens.opengl.HyperspaceScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;

public class ScreenBuilder {
	public static boolean createScreen(Alite alite, byte [] state) {
		int screen = state[0];	
		if (screen != ScreenCodes.FLIGHT_SCREEN) {
			alite.loadAutosave();
		}
		DataInputStream dis = state.length > 0 ? new DataInputStream(new ByteArrayInputStream(state, 1, state.length - 1)) : null;
		try {
			switch (screen) {
				case ScreenCodes.INTRO_SCREEN: AliteLog.e("ScreenBuilder", "ScreenBuilderError: Cannot create Intro Screen here -- wrong Activity."); break;
				case ScreenCodes.BUY_SCREEN: return BuyScreen.initialize(alite, dis);
				case ScreenCodes.EQUIP_SCREEN: return EquipmentScreen.initialize(alite, dis);
				case ScreenCodes.INVENTORY_SCREEN: return InventoryScreen.initialize(alite, dis);
				case ScreenCodes.GALAXY_SCREEN: return GalaxyScreen.initialize(alite, dis);
				case ScreenCodes.LOCAL_SCREEN: return LocalScreen.initialize(alite, dis);
				case ScreenCodes.SHIP_INTRO_SCREEN: return ShipIntroScreen.initialize(alite, dis);
				case ScreenCodes.STATUS_SCREEN: return StatusScreen.initialize(alite, dis);
				case ScreenCodes.PLANET_SCREEN: return PlanetScreen.initialize(alite, dis);
				case ScreenCodes.QUANTITY_PAD_SCREEN: return QuantityPadScreen.initialize(alite, dis);
				case ScreenCodes.DISK_SCREEN: return DiskScreen.initialize(alite, dis);
				case ScreenCodes.LOAD_SCREEN: return LoadScreen.initialize(alite, dis);
				case ScreenCodes.SAVE_SCREEN: return SaveScreen.initialize(alite, dis);
				case ScreenCodes.CATALOG_SCREEN: return CatalogScreen.initialize(alite, dis);
				case ScreenCodes.OPTIONS_SCREEN: return OptionsScreen.initialize(alite, dis);
				case ScreenCodes.DISPLAY_OPTIONS_SCREEN: return DisplayOptionsScreen.initialize(alite, dis);
				case ScreenCodes.AUDIO_OPTIONS_SCREEN: return AudioOptionsScreen.initialize(alite, dis);
				case ScreenCodes.CONTROL_OPTIONS_SCREEN: return ControlOptionsScreen.initialize(alite, dis);
				case ScreenCodes.GAMEPLAY_OPTIONS_SCREEN: return GameplayOptionsScreen.initialize(alite, dis);
				case ScreenCodes.INFLIGHT_BUTTONS_OPTIONS_SCREEN: return InFlightButtonsOptionsScreen.initialize(alite, dis);
				case ScreenCodes.DEBUG_SCREEN: return DebugSettingsScreen.initialize(alite, dis);
				case ScreenCodes.MORE_DEBUG_OPTIONS_SCREEN: return MoreDebugSettingsScreen.initialize(alite, dis);
				case ScreenCodes.ABOUT_SCREEN: return AboutScreen.initialize(alite, dis);		
				case ScreenCodes.LIBRARY_SCREEN: return LibraryScreen.initialize(alite, dis);
				case ScreenCodes.LIBRARY_PAGE_SCREEN: return LibraryPageScreen.initialize(alite, dis);
				case ScreenCodes.TUTORIAL_SELECTION_SCREEN: return TutorialSelectionScreen.initialize(alite, dis);
				case ScreenCodes.HACKER_SCREEN: return HackerScreen.initialize(alite, dis);
				case ScreenCodes.HEX_NUMBER_PAD_SCREEN: return HexNumberPadScreen.initialize(alite, dis);
				case ScreenCodes.CONSTRICTOR_SCREEN: return ConstrictorScreen.initialize(alite, dis);
				case ScreenCodes.COUGAR_SCREEN: return CougarScreen.initialize(alite, dis);
				case ScreenCodes.SUPERNOVA_SCREEN: return SupernovaScreen.initialize(alite, dis);
				case ScreenCodes.THARGOID_DOCUMENTS_SCREEN: return ThargoidDocumentsScreen.initialize(alite, dis);
				case ScreenCodes.THARGOID_STATION_SCREEN: return ThargoidStationScreen.initialize(alite, dis);
				case ScreenCodes.TUT_INTRODUCTION_SCREEN: return TutIntroduction.initialize(alite, dis);
				case ScreenCodes.TUT_TRADING_SCREEN: return TutTrading.initialize(alite, dis);
				case ScreenCodes.TUT_EQUIPMENT_SCREEN: return TutEquipment.initialize(alite, dis);
				case ScreenCodes.TUT_NAVIGATION_SCREEN: return TutNavigation.initialize(alite, dis);
				case ScreenCodes.TUT_HUD_SCREEN: return TutHud.initialize(alite, dis);
				case ScreenCodes.TUT_BASIC_FLYING_SCREEN: return TutBasicFlying.initialize(alite, dis);
				case ScreenCodes.TUT_ADVANCED_FLYING_SCREEN: return TutAdvancedFlying.initialize(alite, dis);
				case ScreenCodes.HYPERSPACE_SCREEN: return HyperspaceScreen.initialize(alite, dis);
				case ScreenCodes.FLIGHT_SCREEN: return FlightScreen.initialize(alite, dis);				
			}
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
			alite.getNavigationBar().moveToScreen(alite.getCurrentScreen());
		}
		return false;
	}	
}
