package de.phbouillon.android.games.alite.screens;

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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.canvas.DiskScreen;
import de.phbouillon.android.games.alite.screens.canvas.QuitScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;

public class NavigationBar {	
	public static final int SIZE = 200;
	static int idCounter = 1;
	
	private int position;
	private int activeIndex;
	private int pendingIndex = -1;
	private boolean active = true;
	private final Alite alite;
	private int highlightedPosition = -1;
	
	static class NavigationEntry {
		String title;
		Pixmap image;
		String navigationTarget;
		boolean visible;
		int id;
		
		NavigationEntry(String title, Pixmap image, String navigationTarget) {
			this.title = title;
			this.image = image;
			this.navigationTarget = navigationTarget;
			visible = true;
			id = idCounter++;
		}
	}
	
	private final List <NavigationEntry> targets = new ArrayList<NavigationEntry>();
	
	public NavigationBar(Game game) {
		position = 0;
		this.alite = (Alite) game;
		
		Assets.launchIcon    = game.getGraphics().newPixmap("navigation_icons/launch_icon.png", true);
		Assets.statusIcon    = game.getGraphics().newPixmap("navigation_icons/status_icon.png", true);
		Assets.buyIcon       = game.getGraphics().newPixmap("navigation_icons/buy_icon.png", true);
		Assets.inventoryIcon = game.getGraphics().newPixmap("navigation_icons/inventory_icon.png", true);
		Assets.equipIcon     = game.getGraphics().newPixmap("navigation_icons/equipment_icon.png", true);
		Assets.localIcon     = game.getGraphics().newPixmap("navigation_icons/local_icon.png", true);
		Assets.galaxyIcon    = game.getGraphics().newPixmap("navigation_icons/galaxy_icon.png", true);
		Assets.planetIcon    = game.getGraphics().newPixmap("navigation_icons/planet_icon.png", true);
		Assets.diskIcon      = game.getGraphics().newPixmap("navigation_icons/disk_icon.png", true);
		Assets.optionsIcon   = game.getGraphics().newPixmap("navigation_icons/options_icon.png", true);
		Assets.libraryIcon   = game.getGraphics().newPixmap("navigation_icons/library_icon.png", true);
		Assets.academyIcon   = game.getGraphics().newPixmap("navigation_icons/academy_icon.png", true);
		Assets.hackerIcon    = game.getGraphics().newPixmap("navigation_icons/hacker_icon.png", true);
		Assets.quitIcon      = game.getGraphics().newPixmap("navigation_icons/quit_icon.png", true);
	}
	
	public int getHighlightedPosition() {
		return highlightedPosition;
	}
	
	public void setHighlightedPosition(int pos) {
		highlightedPosition = pos;
	}
	
	public void moveToScreen(int screenCode) {
		switch (screenCode) {
			case ScreenCodes.STATUS_SCREEN: setActiveIndex(2); break; 
			case ScreenCodes.BUY_SCREEN: setActiveIndex(3); break;
			case ScreenCodes.INVENTORY_SCREEN: setActiveIndex(4); break;
			case ScreenCodes.EQUIP_SCREEN: setActiveIndex(5); break;
			case ScreenCodes.GALAXY_SCREEN: setActiveIndex(6); break;
			case ScreenCodes.LOCAL_SCREEN: setActiveIndex(7); break;
			case ScreenCodes.PLANET_SCREEN: setActiveIndex(8); break;
			case ScreenCodes.DISK_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.CATALOG_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.LOAD_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.SAVE_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.OPTIONS_SCREEN: setActiveIndex(10); break;
			case ScreenCodes.DISPLAY_OPTIONS_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.GAMEPLAY_OPTIONS_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.AUDIO_OPTIONS_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.CONTROL_OPTIONS_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.DEBUG_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.MORE_DEBUG_OPTIONS_SCREEN: setActiveIndex(9); break;
			case ScreenCodes.LIBRARY_SCREEN: setActiveIndex(11); break;
			case ScreenCodes.LIBRARY_PAGE_SCREEN: setActiveIndex(11); break;
			case ScreenCodes.TUTORIAL_SELECTION_SCREEN: setActiveIndex(12); break;
			case ScreenCodes.HACKER_SCREEN: if (alite.isHackerActive()) {setActiveIndex(13);} break;
			case ScreenCodes.QUANTITY_PAD_SCREEN: setActiveIndex(3); break;
		}
		ensureVisible();		
	}
	
	public void moveToScreen(Screen screen) {
		if (screen == null) {
			return;
		}
		moveToScreen(screen.getScreenCode());
	}
	
	public void ensureVisible(int index) {
		position = 0;
		int realSize = targets.size();
		for (int i = 0; i < targets.size(); i++) {
			if (!targets.get(i).visible) {
				realSize--;
			}
		}
		boolean found = (position + 1080) / SIZE > (index + 1);
		while (!found) {
			position += SIZE;
			if (position > (SIZE * realSize - 1080)) {
				found = true;
				position = SIZE * realSize - 1080;
			} else {
				found = (position + 1080) / SIZE > (index + 1);
			}
		}		
	}
	
	private void ensureVisible() {
		ensureVisible(activeIndex);
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public synchronized int add(String title, Pixmap image, String navigationTarget) {		
		NavigationEntry entry = new NavigationEntry(title, image, navigationTarget);
		targets.add(entry);
		return entry.id;
	}
	
	public synchronized int addInvisible(String title, Pixmap image) {		
		NavigationEntry entry = new NavigationEntry(title, image, null);
		targets.add(entry);
		entry.visible = false;
		return entry.id;
	}

	public void setFlightMode(boolean b) {
		targets.get(0).title = b ? "Front" : "Launch";
		targets.get(1).visible = false;
		targets.get(9).visible = !b;
		targets.get(12).visible = !b;
		targets.get(13).visible = !b && alite.isHackerActive();
	}
	
	public void setVisible(int id, boolean visible) {
		for (NavigationEntry entry: targets) {
			if (entry.id == id) {
				entry.visible = visible;
			}
		}
		position = 0;
	}
	
	public boolean isVisible(int id) {
		for (NavigationEntry entry: targets) {
			if (entry.id == id) {
				return entry.visible;
			}
		}		
		return false;
	}
	
	public void render(Graphics g) {
		if (AndroidGame.resetting) {
			GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT);
			return;
		}
		int counter = 0;
		int positionCounter = 0;
		int selX = -1;
		int selY = -1;
		for (NavigationEntry entry: targets) {
			if (!entry.visible) {
				counter++;
				continue;
			}
			if ((counter * SIZE + SIZE) < position) {
				counter++;
				positionCounter++;
				continue;
			}
			int halfWidth  = g.getTextWidth(entry.title, Assets.regularFont) >> 1;
			int halfHeight = g.getTextHeight(entry.title, Assets.regularFont) >> 1;

			int y = positionCounter * SIZE - position + 1;	
			int x = 1920 - SIZE;
						
			g.gradientRect(x + 5, y + 5, SIZE - 6, SIZE - 6, true, true, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
			if (entry.image != null) {
				g.drawPixmap(entry.image, x + 5, y + 5);
			}
			if (counter == activeIndex) {
				selX = x;
				selY = y;
			}
			if (counter == highlightedPosition) {
				g.rec3d(x, y, SIZE, SIZE, 5, counter == activeIndex ? AliteColors.get().selectedColoredFrameLight() : AliteColors.get().activeColoredFrameLight(), 
						 				     counter == activeIndex ? AliteColors.get().selectedColoredFrameDark() : AliteColors.get().activeColoredFrameDark());				
			} else {
				g.rec3d(x, y, SIZE, SIZE, 5, counter == activeIndex ? AliteColors.get().selectedColoredFrameLight() : AliteColors.get().coloredFrameLight(), 
											 counter == activeIndex ? AliteColors.get().selectedColoredFrameDark() : AliteColors.get().coloredFrameDark());
			}

			y = positionCounter * SIZE;
			long color = counter == activeIndex ? AliteColors.get().selectedText() : AliteColors.get().message();
			int yPos = entry.image == null ? (int) (y + (SIZE >> 1) - halfHeight + Assets.regularFont.getSize() / 2) - position :
				                             (int) (y + SIZE - position - 10);
			
			g.drawText(entry.title, 1920 - (SIZE >> 1) - halfWidth, yPos, color, Assets.regularFont);
			counter++;
			positionCounter++;
		}
		if (selX != -1 && selY != -1) {
			g.rec3d(selX, selY, SIZE, SIZE, 5, AliteColors.get().selectedColoredFrameLight(), AliteColors.get().selectedColoredFrameDark());
		}
	}
	
	public void setActiveIndex(int newIndex) {
		activeIndex = newIndex;
	}
	
	public int getActiveIndex() {
		return activeIndex;
	}
	
	public void increasePosition(int delta) {		
		position += delta;
		int realSize = targets.size();
		for (int i = 0; i < targets.size(); i++) {
			if (!targets.get(i).visible) {
				realSize--;
			}
		}
		
		if (position > (SIZE * realSize - 1080)) {
			position = SIZE * realSize - 1080;
		}
	}
	
	public boolean isAtTop() {
		return position == 0;
	}
	
	public void moveToTop() {
		position = 0;
	}
	
	public boolean isAtBottom() {
		int realSize = targets.size();
		for (int i = 0; i < targets.size(); i++) {
			if (!targets.get(i).visible) {
				realSize--;
			}
		}
		return position == SIZE * realSize - 1080;
	}
	
	public void decreasePosition(int delta) {
		position -= delta;
		if (position < 0) {
			position = 0;
		}
	}
	
	public int getNextPosition(int pos) {
		for (int i = pos + 1; i < targets.size(); i++) {
			if (targets.get(i).visible) {
				return i;
			}
		}
		for (int i = 0; i < Math.min(pos, targets.size()); i++) {
			if (targets.get(i).visible) {
				return i;
			}
		}
		return 0;
	}
	
	public int getPreviousPosition(int pos) {
		for (int i = pos - 1; i >= 0; i--) {
			if (targets.get(i).visible) {
				return i;
			}
		}
		for (int i = targets.size() - 1; i > pos; i--) {
			if (targets.get(i).visible) {
				return i;
			}
		}
		return 0;
	}
	
	public Screen getScreenForSelection(Alite game, int selection) {
		return getScreenFromNavigationEntry(game, targets.get(selection));
	}
	
	public int getRealIndex(int index) {
		int realIndex = index;
		for (int i = 0; i <= index; i++) {
			if (i >= targets.size()) {
				AliteLog.e("NavigationBar", "Index out of bounds: " + index);
				return -1;				
			}
			if (!targets.get(i).visible) {
				realIndex++;
			}
		}
		index = realIndex;
		return index;
	}
	
	private Screen getScreenFromNavigationEntry(Alite game, NavigationEntry entry) {
		Screen newScreen = null;
		if (entry.navigationTarget != null) {
			SoundManager.play(Assets.click);
			Method method;
			try {				
				method = game.getClass().getMethod("get" + entry.navigationTarget, new Class<?>[0]);
				newScreen = (Screen) method.invoke(game, new Object[0]);				
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}						
		}
		else {
			if (entry.title.equals("Launch")) {
				SoundManager.play(Assets.click);
				newScreen = new FlightScreen(game, true);
			} else if (entry.title.equals("Gal. Jump")) {
				SoundManager.play(Assets.click);
				newScreen = new FlightScreen(game, true);				
			} else if (entry.title.equals("Quit")) {
				SoundManager.play(Assets.click);
				FlightScreen fs = game.getCurrentScreen() instanceof FlightScreen ? (FlightScreen) game.getCurrentScreen() : null;
				newScreen = new QuitScreen(game, fs);
			}
		}		
		return newScreen;
	}
	
	public Screen getScreenForIndex(Alite game, int index) {
		int realIndex = index;
		for (int i = 0; i <= index; i++) {
			if (i >= targets.size()) {
				AliteLog.e("NavigationBar", "Index out of bounds: " + index);
				return null;				
			}
			if (!targets.get(i).visible) {
				realIndex++;
			}
		}
		index = realIndex;

		NavigationEntry entry = targets.get(index);		
		return getScreenFromNavigationEntry(game, entry);
	}
	
	public Screen touched(Alite game, int x, int y) {
		if (x < (1920 - SIZE) || active == false) {
			return null;
		}

		int targetY = y + position;
		int index = targetY / SIZE;
		int realIndex = index;
		for (int i = 0; i <= realIndex; i++) {
			if (i >= targets.size()) {
				AliteLog.e("NavigationBar", "Index out of bounds: " + index);
				return null;				
			}
			if (!targets.get(i).visible) {
				realIndex++;
			}
		}
		index = realIndex;
		
		if (index < 0 || index >= targets.size()) {
			AliteLog.e("NavigationBar", "Index out of bounds: " + index);
			return null;
		}		
		if ((index == activeIndex || pendingIndex != -1) && index != 9) {
			// Nothing to do...
			return null;
		}
		if (index == 9 && game.getCurrentScreen() instanceof DiskScreen) {
			// Nothing to do... Otherwise, if index == 9 (DiskScreen) and
			// the current screen is _not_ instance of DiskScreen, we are in
			// a sub menu of the disk screen and want to return to the
			// disk screen. This feels like a hack. To much explanation
			// necessary... :(
			return null;
		}
		NavigationEntry entry = targets.get(index);
		Screen newScreen = null;
		if (entry.navigationTarget != null) {
			SoundManager.play(Assets.click);
			Method method;
			try {				
				method = game.getClass().getMethod("get" + entry.navigationTarget, new Class<?>[0]);
				Object potentialNewScreen = method.invoke(game, new Object[0]);
				if (potentialNewScreen == null) {
					SoundManager.play(Assets.error);
					// This can occur, if the "Planet" screen is requested, while the
					// player is in witch space and his "current location" is still 
					// between planets.
				} else {
					newScreen = (Screen) potentialNewScreen; 				
					pendingIndex = index;
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}						
		}
		else {
			if (entry.title.equals("Launch")) {
				SoundManager.play(Assets.click);
				try {
					AliteLog.d("[ALITE]", "Performing autosave. [Launch]");
					((Alite) game).getFileUtils().autoSave((Alite) game);
				} catch (Exception e) {
					AliteLog.e("[ALITE]", "Autosaving commander failed.", e);
				}
//				newScreen = new AutomaticLaunchScreen(game);
				InGameManager.safeZoneViolated = false;
				newScreen = new FlightScreen(game, true);
			} else if (entry.title.equals("Gal. Jump")) {
				SoundManager.play(Assets.click);
				newScreen = new FlightScreen(game, true);				
			} else if (entry.title.equals("Front")) {
				SoundManager.play(Assets.click);
				((FlightScreen) game.getCurrentScreen()).setForwardView();
				((FlightScreen) game.getCurrentScreen()).setInformationScreen(null);
			} else if (entry.title.equals("Quit")) {
			  SoundManager.play(Assets.click);
			  FlightScreen fs = game.getCurrentScreen() instanceof FlightScreen ? (FlightScreen) game.getCurrentScreen() : null;
			  newScreen = new QuitScreen(game, fs);
			}
		}
		return newScreen;
	}
	
	public void setPendingIndex(int idx) {
		pendingIndex = idx;
	}
	
	public void resetPending() {
		pendingIndex = -1;
	}
	
	public void performScreenChange() {
		if (pendingIndex != -1) {
			activeIndex = pendingIndex;
			pendingIndex = -1;
			highlightedPosition = -1;
		}
	}
}
