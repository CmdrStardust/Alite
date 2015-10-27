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

import java.io.DataInputStream;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.generator.SystemData;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class LocalScreen extends GalaxyScreen {		
	public LocalScreen(Game game) {
		super(game);
	}
	
	@Override
	public void activate() {		
		title = "Local Navigation Chart";
		Player player = ((Alite) game).getPlayer();
		SystemData hyper = player.getHyperspaceSystem();
		zoomFactor = 4.0f;
		game.getInput().setZoomFactor(zoomFactor);
		
		centerX = computeCenterX(hyper == null ? player.getPosition().x : hyper.getX());
		centerY = computeCenterY(hyper == null ? player.getPosition().y : hyper.getY());
		targetX = centerX;
		targetY = centerY;
		
		if (Math.abs(pendingZoomFactor - zoomFactor) > 0.0001 && pendingZoomFactor > 0) {
			zoomFactor = pendingZoomFactor;
			game.getInput().setZoomFactor(zoomFactor);
			pendingZoomFactor = -1.0f;
		}
		if (pendingCenterX != -1) {
			centerX = pendingCenterX;
			targetX = centerX;
			pendingCenterX = -1;
		}
		if (pendingCenterY != -1) {
			centerY = pendingCenterY;
			targetY = centerY;
			pendingCenterY = -1;
		}		
		setupUi();
		normalizeSystems();						
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		LocalScreen ls = new LocalScreen(alite);
		try {
			ls.zoomFactor = dis.readFloat();
			ls.centerX = dis.readInt();
			ls.centerY = dis.readInt();
			ls.pendingZoomFactor = ls.zoomFactor;
			ls.pendingCenterX    = ls.centerX;
			ls.pendingCenterY    = ls.centerY;
		} catch (Exception e) {
			AliteLog.e("Local Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(ls);
		return true;
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.LOCAL_SCREEN;
	}	
}
