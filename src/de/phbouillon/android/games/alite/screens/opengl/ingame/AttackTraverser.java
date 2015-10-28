package de.phbouillon.android.games.alite.screens.opengl.ingame;

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
import java.io.ObjectInputStream;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargon;

public class AttackTraverser implements SpaceObjectTraverser {
	private static final long serialVersionUID = 1824100796383524100L;
	private transient Alite alite;
	
	AttackTraverser(final Alite alite) {
		this.alite = alite;
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "AttackTraverser.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "AttackTraverser.readObject I");
			this.alite = Alite.get();
			AliteLog.e("readObject", "AttackTraverser.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	@Override
	public boolean handle(SpaceObject so) {
		if (so.getType() == ObjectType.EnemyShip ||
		    so.getType() == ObjectType.Thargoid ||
		    so.getType() == ObjectType.Thargon && ((Thargon) so).getMother() != null && ((Thargon) so).getMother().getHullStrength() > 0) {
				alite.getPlayer().setCondition(Condition.RED);
			return true;
		}
		return false;
	}

}
