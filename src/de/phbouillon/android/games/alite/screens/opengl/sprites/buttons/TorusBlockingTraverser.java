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

import java.io.Serializable;

import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.screens.opengl.ingame.SpaceObjectTraverser;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteHud;

public class TorusBlockingTraverser implements SpaceObjectTraverser, Serializable {
	private static final long serialVersionUID = -5185606119234486770L;
	private final InGameManager inGame;
	
	TorusBlockingTraverser(final InGameManager inGame) {
		this.inGame = inGame;
	}
	
	@Override
	public boolean handle(SpaceObject so) {
		ObjectType type = so.getType();
		if (type == ObjectType.EnemyShip || type == ObjectType.Missile || type == ObjectType.SpaceStation ||
			(type == ObjectType.Shuttle && !Settings.freePath) || type == ObjectType.Thargoid || type == ObjectType.Thargon ||
			(type == ObjectType.Trader && !Settings.freePath) || type == ObjectType.Viper) {
			if (so.getPosition().distanceSq(inGame.getShip().getPosition()) < AliteHud.MAX_DISTANCE_SQ) {
				return true;
			}
		}
		return false;
	}
}
