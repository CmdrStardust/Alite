package de.phbouillon.android.games.alite.screens.opengl.sprites.buttons;

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

import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.statistics.WeaponType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.SpaceObjectTraverser;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Missile;

public class ECMTraverser implements SpaceObjectTraverser {
	private static final long serialVersionUID = 2946553436076856776L;
	private final InGameManager inGame;
	private boolean missileDestroyed = false;
	
	ECMTraverser(final InGameManager inGame) {
		this.inGame = inGame;
	}
	
	public void reset() {
		missileDestroyed = false;
	}
	
	@Override
	public boolean handle(SpaceObject so) {
		if (so instanceof Missile && !so.mustBeRemoved()) {
			Missile m = (Missile) so;
			if (m.getTarget() == inGame.getShip()) {
				if (!missileDestroyed) {
					SoundManager.play(Assets.ecm);
					if (inGame.getHud() != null) {
						inGame.getHud().showECM(6000);
					}
					missileDestroyed = true;
				}						
				m.setHullStrength(0);				
				inGame.explode(m, false, WeaponType.Missile);
				inGame.reduceShipEnergy(3);
			}
		}
		return false;
	}
}
