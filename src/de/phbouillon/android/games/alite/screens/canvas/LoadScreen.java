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
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.CommanderData;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class LoadScreen extends CatalogScreen {
	private boolean confirmedLoad = false;
	private boolean pendingShowMessage = false;
	
	public LoadScreen(Game game, String title) {
		super(game, title);
		deleteButton = null;
	}
	
	@Override
	public void activate() {
		super.activate();
		deleteButton = null;
		if (pendingShowMessage) {
			setMessage("Are you sure you want to load Commander " + selectedCommanderData.get(0).getName() + "?", MessageType.YESNO);
			confirmDelete = false;
			pendingShowMessage = false;
		}
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		LoadScreen ls = new LoadScreen(alite, "Load Commander");
		try {
			ls.currentPage = dis.readInt();
			ls.confirmDelete = dis.readBoolean();
			int selectionCount = dis.readInt();
			if (selectionCount != 0) {
				ls.pendingSelectionIndices = new ArrayList<Integer>();
				for (int i = 0; i < selectionCount; i++) {
					ls.pendingSelectionIndices.add(dis.readInt());
				}
			}
			ls.pendingShowMessage = dis.readBoolean();
		} catch (Exception e) {
			AliteLog.e("Load Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(ls);
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(currentPage);
		dos.writeBoolean(confirmDelete);
		dos.writeInt(selectedCommanderData.size());
		for (CommanderData c: selectedCommanderData) {
			// This is o(n^2), but does it really matter? 
			// The commander data could be transformed to a map, and doing it
			// here would simplify to o(n) (2 * n, to be precise), but even if
			// someone has stored 10000 commanders and wants to delete all of them,
			// this lookup isn't the problem.
			dos.writeInt(commanderData.indexOf(c));
		}
		dos.writeBoolean(getMessage() != null);
	}
	
	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}
		if (confirmedLoad && getMessage() == null) {
			newScreen = new StatusScreen(game);
			confirmedLoad = false;
		}
		if (selectedCommanderData.size() == 1) {
			if (messageResult == 0) {
				setMessage("Are you sure you want to load Commander " + selectedCommanderData.get(0).getName() + "?", MessageType.YESNO);
				confirmDelete = false;
				SoundManager.play(Assets.alert);
			} else {
				if (messageResult == 1) {
					Alite alite = (Alite) game;
					try {
						alite.getFileUtils().loadCommander(alite, selectedCommanderData.get(0).getFileName());
						setMessage("Cursor reset to " + selectedCommanderData.get(0).getDockedSystem() + ".");
						SoundManager.play(Assets.alert);
						confirmedLoad = true;		
					} catch (IOException e) {
						setMessage("Error while loading commander " + selectedCommanderData.get(0).getName() + ": " + e.getMessage());
					}
				} 
				clearSelection();
				messageResult = 0;
			}
		}
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
		return ScreenCodes.LOAD_SCREEN;
	}	
}
