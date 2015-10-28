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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.CommanderData;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class CatalogScreen extends AliteScreen {
	protected String title;
	private List<Button> button = new ArrayList<Button>();
	protected List<CommanderData> commanderData = new ArrayList<CommanderData>();
	private Pixmap buttonBackground;
	private Pixmap buttonBackgroundPushed;
	private Pixmap buttonBackgroundSelected;
	private Button more;
	private Button back;	
	protected Button deleteButton;
	protected int currentPage = 0;
	protected boolean confirmDelete = false;
	protected List<CommanderData> selectedCommanderData = new ArrayList<CommanderData>();
	protected List<Integer> pendingSelectionIndices = null;
	private boolean pendingShowMessage = false;
	
	public CatalogScreen(Game game, String title) {
		super(game);
		this.title = title;
	}
	
	@Override
	public void activate() {
		File [] commanders = null;
		try {
			commanders = game.getFileIO().getFiles("commanders", "(.*)\\.cmdr");
		} catch (IOException e) {
		}
		back = new Button(1400, 950, 100, 100, "<", Assets.regularFont, null);
		back.setGradient(true);
		more = new Button(1550, 950, 100, 100, ">", Assets.regularFont, null);
		more.setGradient(true);
		
		button.clear();
		commanderData.clear();
		if (commanders != null) {
			for (int i = 0; i < commanders.length; i++) {
				CommanderData data = ((Alite) game).getFileUtils().getQuickCommanderInfo((Alite) game, "commanders/" + commanders[i].getName());
				if (data != null) {
					Button b = new Button(20, 210 + (i % 5) * 140, 1680, 120, buttonBackground);
					b.setPushedBackground(buttonBackgroundPushed);
					button.add(b);
					b.setText("");
					b.setFont(Assets.regularFont);
					b.setUseBorder(false);
					commanderData.add(data);
				}
			}
		}
		Collections.sort(commanderData, new Comparator<CommanderData>() {
			@Override
			public int compare(CommanderData c1, CommanderData c2) {
				if (c1 == null) {
					return c2 == null ? 0 : -1;
				}
				if (c2 == null) {
					return 1;
				}
				// Autosave always comes first
				if (c1.getFileName().indexOf("commanders/__autosave") != -1) {
					if (c2.getFileName().indexOf("commanders/__autosave") != -1) {
						try {
							long d1 = game.getFileIO().fileLastModifiedDate(c1.getFileName());
							long d2 = game.getFileIO().fileLastModifiedDate(c2.getFileName());
							return d1 > d2 ? -1 : d1 == d2 ? 0 : 1;
						} catch (IOException e) {
							return 0;
						}
					}
					return -1;
				}
				if (c2.getFileName().indexOf("commanders/__autosave") != -1) {
					return 1;
				}
				String n1 = c1.getName() == null ? "" : c1.getName();
				String n2 = c2.getName() == null ? "" : c2.getName();
				int result = n1.compareTo(n2);
				if (result == 0) {
					long t1 = c1.getGameTime();
					long t2 = c2.getGameTime();
					result = t1 < t2 ? 1 : -1; // We want to display longer game times first. 
				}
				return result;
			}
		});
		
		deleteButton = new Button(50, 950, 600, 100, "Delete selected Commander", Assets.regularFont, null);
		deleteButton.setGradient(true);		
		if (pendingSelectionIndices != null) {
			int n = commanderData.size();
			for (int i: pendingSelectionIndices) {
				if (i >= 0 && i < n) {
					selectedCommanderData.add(commanderData.get(i));
					button.get(i).setPixmap(buttonBackgroundSelected);
					button.get(i).setSelected(true);
				}
			}
			pendingSelectionIndices.clear();
		}
		if (pendingShowMessage) {
			if (selectedCommanderData.size() == 1) {
				setMessage("Are you sure you want to delete Commander " + selectedCommanderData.get(0).getName() + "?", MessageType.YESNO);
			} else {
				setMessage("Are you sure you want to delete the selected Commanders?", MessageType.YESNO);
			}
			pendingShowMessage = false;
		}
		confirmDelete = true;
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		CatalogScreen cs = new CatalogScreen(alite, "Catalog");
		try {
			cs.currentPage = dis.readInt();
			cs.confirmDelete = dis.readBoolean();
			int selectionCount = dis.readInt();
			if (selectionCount != 0) {
				cs.pendingSelectionIndices = new ArrayList<Integer>();
				for (int i = 0; i < selectionCount; i++) {
					cs.pendingSelectionIndices.add(dis.readInt());
				}
			}
			cs.pendingShowMessage = dis.readBoolean();
		} catch (Exception e) {
			AliteLog.e("Load Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(cs);
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
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (more.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				currentPage++;
			}
			if (back.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				currentPage--;
			}
			for (int i = currentPage * 5; i < Math.min(currentPage * 5 + 5, button.size()); i++) {
				if (button.get(i).isTouched(touch.x, touch.y)) {
					SoundManager.play(Assets.click);
					boolean select = !selectedCommanderData.contains(commanderData.get(i));
					if (select) {
						selectedCommanderData.add(commanderData.get(i));	
						button.get(i).setPixmap(buttonBackgroundSelected);
					} else {
						selectedCommanderData.remove(commanderData.get(i));
						button.get(i).setPixmap(buttonBackground);
					}
					button.get(i).setSelected(select);
					if (deleteButton != null) {
						deleteButton.setText(selectedCommanderData.size() > 1 ? "Delete selected Commanders" : "Delete selected Commander");
					}
				}
			}
			if (confirmDelete && messageResult != 0) {
				confirmDelete = false;
				if (messageResult == 1) {
					for (CommanderData cd: selectedCommanderData) {
						try {
							((Alite) game).getFileIO().deleteFile(cd.getFileName());
						} catch (IOException e) {
							AliteLog.e("[ALITE] Delete file", "Cannot delete file " + cd.getFileName() + ".", e);
						}
					}
					newScreen = new CatalogScreen(game, "Catalog");
				} 
				clearSelection();
				messageResult = 0;
			}
			if (deleteButton != null && deleteButton.isTouched(touch.x, touch.y)) {
				if (messageResult == 0) {
					SoundManager.play(Assets.alert);
					if (selectedCommanderData.size() == 1) {
						setMessage("Are you sure you want to delete Commander " + selectedCommanderData.get(0).getName() + "?", MessageType.YESNO);
					} else {
						setMessage("Are you sure you want to delete the selected Commanders?", MessageType.YESNO);
					}
					confirmDelete = true;
				}
			} 
		}
	}

	protected void clearSelection() {
		for (int i = 0; i < button.size(); i++) {
			if (button.get(i).isSelected()) {
				button.get(i).setSelected(false);
				button.get(i).setPixmap(buttonBackground);				
			}
		}
		selectedCommanderData.clear();
	}
	
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayTitle(title);
		
		g.gradientRect(20, 100, 1680, 80, true, true, AliteColors.get().backgroundDark(), AliteColors.get().backgroundLight());
		g.rec3d(20, 100, 1680, 800, 3, AliteColors.get().frameLight(), AliteColors.get().frameDark());		
		g.drawText("Commander Name",   50, 160, AliteColors.get().mainText(), Assets.titleFont);
		g.drawText("Docked",          600, 160, AliteColors.get().mainText(), Assets.titleFont);
		g.drawText("Time",            850, 160, AliteColors.get().mainText(), Assets.titleFont);
		g.drawText("Score",          1100, 160, AliteColors.get().mainText(), Assets.titleFont);
		g.drawText("Rating",         1300, 160, AliteColors.get().mainText(), Assets.titleFont);
		g.rec3d(20, 100, 1680, 80, 3, AliteColors.get().frameLight(), AliteColors.get().frameDark());
		g.rec3d(590, 100, 3, 800, 3, AliteColors.get().frameLight(), AliteColors.get().frameDark());
		g.rec3d(840, 100, 3, 800, 3, AliteColors.get().frameLight(), AliteColors.get().frameDark());
		g.rec3d(1090, 100, 3, 800, 3, AliteColors.get().frameLight(), AliteColors.get().frameDark());
		g.rec3d(1290, 100, 3, 800, 3, AliteColors.get().frameLight(), AliteColors.get().frameDark());
		
		for (int i = currentPage * 5; i < Math.min(currentPage * 5 + 5, button.size()); i++) {
			button.get(i).render(g);
			CommanderData data = commanderData.get(i);
			long color = i % 2 == 0 ? AliteColors.get().message() : AliteColors.get().mainText();
			String textToDisplay = data.getName();
			String suffix = "";
			if (data.getFileName().indexOf("commanders/__autosave") != -1) {
				int no = 0;
				if (data.getFileName().indexOf("1") != -1) {
					no = 1;
				} else if (data.getFileName().indexOf("2") != -1) {
					no = 2;
				}
				textToDisplay = "[Autosave " + (no + 1) + "] " + data.getName();
			}
			while (g.getTextWidth(textToDisplay + suffix, Assets.regularFont) > 500) {
				textToDisplay = textToDisplay.substring(0, textToDisplay.length() - 1);
				suffix = "...";
			}
			g.drawText(textToDisplay + suffix, 50, 285 + (i % 5) * 140, color, Assets.regularFont);			
			g.drawText(data.getDockedSystem(), 600, 285 + (i % 5) * 140, color, Assets.regularFont);
			String timeString = StatusScreen.getGameTime(data.getGameTime());
			g.drawText(timeString, 1080 - g.getTextWidth(timeString, Assets.regularFont), 285 + (i % 5) * 140, color, Assets.regularFont);
			g.drawText("" + data.getPoints(), 1280 - g.getTextWidth("" + data.getPoints(), Assets.regularFont), 285 + (i % 5) * 140, color, Assets.regularFont);
			g.drawText(data.getRating().getName(), 1300, 285 + (i % 5) * 140, color, Assets.regularFont);
		}
		back.render(g);		
		more.render(g);
		if (deleteButton != null) {
			deleteButton.render(g);
		}
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		back.setVisible(currentPage > 0);
		more.setVisible(currentPage < (button.size() - 1) / 5);
		if (deleteButton != null) {
			deleteButton.setVisible(!selectedCommanderData.isEmpty());
		}
	}
		
	@Override
	public void dispose() {
		super.dispose();
		if (buttonBackground != null) {
			buttonBackground.dispose();
			buttonBackground = null;
		}
		if (buttonBackgroundSelected != null) {
			buttonBackgroundSelected.dispose();
			buttonBackgroundSelected = null;
		}
		if (buttonBackgroundPushed != null) {
			buttonBackgroundPushed.dispose();
			buttonBackgroundPushed = null;
		}
	}

	@Override
	public void loadAssets() {
		buttonBackground = game.getGraphics().newPixmap("catalog_button.png", true);
		buttonBackgroundSelected = game.getGraphics().newPixmap("catalog_button_selected.png", true);
		buttonBackgroundPushed = game.getGraphics().newPixmap("catalog_button_pushed.png", true);
		super.loadAssets();
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
		return ScreenCodes.CATALOG_SCREEN;
	}		
}
