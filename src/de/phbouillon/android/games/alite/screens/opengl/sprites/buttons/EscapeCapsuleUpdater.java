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

import java.io.IOException;
import java.io.ObjectInputStream;

import de.phbouillon.android.framework.Updater;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkIII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.EscapeCapsule;

class EscapeCapsuleUpdater implements Updater {
	private static final long serialVersionUID = -296076467539527770L;

	private enum EscapeCapsuleState {
		SPAWN,
		MOVE,
		QUIT
	}
	
	private transient Alite alite;
	private final InGameManager inGame;
	private final GraphicObject ship;
	private final long startTime;
	private EscapeCapsuleState state = EscapeCapsuleState.SPAWN;
	private CobraMkIII cobra = null;
	private EscapeCapsule esc = null;
	private final Vector3f vec1 = new Vector3f(0, 0, 0);
	private final Vector3f vec2 = new Vector3f(0, 0, 0);
	
	public EscapeCapsuleUpdater(Alite alite, InGameManager inGame, GraphicObject ship, long startTime) {
		this.alite = alite;
		this.inGame = inGame;
		this.ship = ship;
		this.startTime = startTime;
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "EscapeCapsuleUpdate.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "EscapeCapsuleUpdate.readObject I");
			this.alite = Alite.get();
			AliteLog.e("readObject", "EscapeCapsuleUpdate.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	@Override
	public void onUpdate(float deltaTime) {
		switch (state) {
			case SPAWN: spawnShip(); 
						spawnEscapeCapsule();
						break;
			case MOVE: moveShip(); 
					   moveEscapeCapsule(deltaTime);
					   break;
			case QUIT: endSequence(); break;
		}
	}

	private final void spawnShip() {
		ship.computeMatrix();
		cobra = new CobraMkIII(alite);
		cobra.setUpVector(ship.getUpVector());
		cobra.setRightVector(ship.getRightVector());
		cobra.setForwardVector(ship.getForwardVector());
		cobra.applyDeltaRotation(15, 15, -25);
		ship.getPosition().copy(vec1);
		ship.getForwardVector().copy(vec2);
		vec2.scale(-800);
		vec1.add(vec2);
		ship.getUpVector().copy(vec2);
		vec2.scale(-50);
		vec1.add(vec2);
		cobra.setPosition(vec1);
		cobra.setSpeed(-cobra.getMaxSpeed());
		ship.setSpeed(0);		
		state = EscapeCapsuleState.MOVE;
		inGame.addObject(cobra);
	}
	
	private final void spawnEscapeCapsule() {
		esc = new EscapeCapsule(alite);
		cobra.getForwardVector().copy(vec1);
		vec1.negate();
		esc.setForwardVector(vec1);
		
		esc.setRightVector(cobra.getRightVector());
		cobra.getUpVector().copy(vec1);
		vec1.negate();
		esc.setUpVector(vec1);
		cobra.getPosition().copy(vec1);
		esc.setPosition(vec1);
		esc.setSpeed(-esc.getMaxSpeed());
		inGame.addObject(esc);
	}
	
	private final void moveShip() {
		if ((System.nanoTime() - startTime) > 4000000000l) {
			state = EscapeCapsuleState.QUIT;
		}
		// Nothing else to be done here; InGameRender advances the cobra...
	}

	private final void moveEscapeCapsule(float deltaTime) {
		esc.moveForward(deltaTime);
	}
		
	private final void endSequence() {
		inGame.terminateToStatusScreen();
	}
}
