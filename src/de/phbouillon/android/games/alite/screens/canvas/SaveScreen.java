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
import java.io.IOException;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.io.FileUtils;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class SaveScreen extends CatalogScreen {
	private final Button saveNewCommanderButton;
	private boolean confirmedSave = false;
	private String pendingMessage;

	public SaveScreen(Game game, String title) {
		super(game, title);
		saveNewCommanderButton = new Button(50, 950, 500, 100, "Save New Commander", Assets.regularFont, null);
		saveNewCommanderButton.setGradient(true);
		deleteButton = null;
		pendingMessage = null;
	}

	public SaveScreen(Game game, String title, String msg) {
		super(game, title);
		saveNewCommanderButton = new Button(50, 950, 500, 100, "Save New Commander", Assets.regularFont, null);
		saveNewCommanderButton.setGradient(true);
		deleteButton = null;
		pendingMessage = msg;
	}

	public static boolean initialize(Alite alite, final DataInputStream dis) {
		alite.setScreen(new SaveScreen(alite, "Save Commander"));
		return true;
	}

	@Override
	public void activate() {
		super.activate();
		deleteButton = null;
		if (pendingMessage != null) {
			setMessage(pendingMessage);
		}
		confirmedSave = pendingMessage != null;
		pendingMessage = null;
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}
		if (confirmedSave) {
			newScreen = new StatusScreen(game);
			confirmedSave = false;
		}
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (saveNewCommanderButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				final Alite alite = (Alite) game;
				TextInputScreen textInput = new TextInputScreen(alite, "New Commander Name", "Enter the name for the new commander", alite.getPlayer().getName(), this, new TextCallback() {
					@Override
					public void onOk(String text) {
						String fileName;
						try {
							fileName = FileUtils.generateRandomFilename("commanders", "", 12, ".cmdr", game.getFileIO());
							alite.getFileUtils().saveCommander(alite, text, fileName);
						} catch (IOException e) {
							AliteLog.e("[ALITE] SaveCommander", "Error while saving commander.", e);
						}
						pendingMessage = "Commander " + text + " saved successfully.";
						confirmedSave = true;
					}

					@Override
					public void onCancel() {
					}
				});
				textInput.setMaxLength(16);
				newScreen = textInput;
			}
		}
		if (selectedCommanderData.size() == 1) {
			if (messageResult == 0) {
				setMessage("Are you sure you want to overwrite Commander " + selectedCommanderData.get(0).getName() + "?", MessageType.YESNO);
				confirmDelete = false;
				SoundManager.play(Assets.alert);
			} else {
				if (messageResult == 1) {
					Alite alite = (Alite) game;
					try {
						alite.getFileUtils().saveCommander(alite, selectedCommanderData.get(0).getName(), selectedCommanderData.get(0).getFileName());
						setMessage("Commander " + selectedCommanderData.get(0).getName() + " saved successfully.");
						SoundManager.play(Assets.alert);
						confirmedSave = true;
					} catch (IOException e) {
						setMessage("Error while saving commander " + selectedCommanderData.get(0).getName() + ": " + e.getMessage());
					}
				}
				clearSelection();
				messageResult = 0;
			}
		}
	}

	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		super.present(deltaTime);
		Graphics g = game.getGraphics();
		saveNewCommanderButton.render(g);
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
		return ScreenCodes.SAVE_SCREEN;
	}
}
