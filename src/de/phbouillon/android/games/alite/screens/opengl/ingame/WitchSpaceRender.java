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
import java.io.Serializable;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Rating;

public class WitchSpaceRender implements Serializable {
	private static final long serialVersionUID = -1502376043768839904L;

	private transient Alite alite;
	private final InGameManager inGame;
	private int witchSpaceKillCounter = 0;
	private TimedEvent driveRepairedMessage = null;
	private boolean hyperdriveMalfunction = false;
	private boolean witchSpace = false;
	
	WitchSpaceRender(Alite alite, InGameManager inGame) {
		this.alite = alite;
		this.inGame = inGame;
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "WitchSpaceRender.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "WitchSpaceRender.readObject I");
			this.alite = Alite.get();
			AliteLog.e("readObject", "WitchSpaceRender.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	void increaseWitchSpaceKillCounter() {
		witchSpaceKillCounter++;
		if (driveRepairedMessage == null) {
			if (hyperdriveMalfunction && witchSpaceKillCounter >= Math.min(8, (alite.getPlayer().getRating().ordinal() + 1))) {
				driveRepairedMessage = new TimedEvent((long) ((Math.random() * 5 + 3) * 1000000000l)) {					
					private static final long serialVersionUID = -5599485138177057364L;

					@Override
					public void doPerform() {
						hyperdriveMalfunction = false;
						inGame.getMessage().repeatText("Drive Repaired!", 1000000000l, 4);
						SoundManager.play(Assets.com_hyperdriveRepaired);
						setRemove(true);
					}
				};
				inGame.addTimedEvent(driveRepairedMessage);
			}
		}
	}

	public boolean isHyperdriveMalfunction() {
		return hyperdriveMalfunction;
	}

	public boolean isWitchSpace() {
		return witchSpace;
	}

	void enterWitchSpace() {
		witchSpace = true;
		witchSpaceKillCounter = 0;
		alite.getPlayer().setHyperspaceSystem(null);
		hyperdriveMalfunction = true;
		if (inGame.getHud() != null) {
			inGame.getHud().setWitchSpace(true);
		}
		inGame.getMessage().repeatText("Drive Malfunction!", 1000000000l, 4);
		SoundManager.play(Assets.com_hyperdriveMalfunction);
		int maxAttackersNumber = alite.getPlayer().getRating().ordinal() - Rating.AVERAGE.ordinal();
		if (maxAttackersNumber < 1) {
			maxAttackersNumber = 1;
		} else if (maxAttackersNumber > 4) {
			maxAttackersNumber = 4;
		}
		int attackers = (int) (Math.random() * maxAttackersNumber);
		if (attackers < 1) {
			attackers = 1;
		} else if (attackers > 4) {
			attackers = 4;
		}
		for (int i = 0; i < attackers; i++) {
			inGame.getSpawnManager().spawnThargoidInWitchSpace();
		}		
	}
}
