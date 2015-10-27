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
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class DiskScreen extends AliteScreen {
	private static final int SIZE     = 450;
	private static final int X_OFFSET = 150;
	private static final int X_GAP    =  50;
	private static final int Y_OFFSET = 315;
	
	private static Pixmap loadIcon;
	private static Pixmap saveIcon;
	private static Pixmap catalogIcon;
		
	private Button [] button = new Button[3];
	private final String [] text = new String [] {"Load", "Save", "Catalog"};
	
	public DiskScreen(Game game) {
		super(game);
	}
	
	@Override
	public void activate() {	
		button[0] = new Button(X_OFFSET, Y_OFFSET, SIZE, SIZE, loadIcon);
		button[1] = new Button(X_OFFSET + X_GAP + SIZE, Y_OFFSET, SIZE, SIZE, saveIcon);
		button[2] = new Button(X_OFFSET + X_GAP * 2 + SIZE * 2, Y_OFFSET, SIZE, SIZE, catalogIcon);
		for (int i = 0; i < 3; i++) {
			button[i].setUseBorder(false);
		}
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		alite.setScreen(new DiskScreen(alite));
		return true;
	}

	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayTitle("Disk Menu");
		
		int index = 0;
		for (Button b: button) {
			if (b != null) {
				b.render(g);
				int halfWidth = g.getTextWidth(text[index], Assets.regularFont) >> 1;
				g.drawText(text[index], b.getX() + (b.getWidth() >> 1) - halfWidth, b.getY() + b.getHeight() + 35, AliteColors.get().mainText(), Assets.regularFont);
			}
			index++;
		}
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (button[0].isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new LoadScreen(game, "Load Commander");
			}
			if (button[1].isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new SaveScreen(game, "Save Commander");
			}
			if (button[2].isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new CatalogScreen(game, "Catalog");
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (loadIcon != null) {
			loadIcon.dispose();
			loadIcon = null;
		}
		if (saveIcon != null) {
			saveIcon.dispose();
			saveIcon = null;
		}
		if (catalogIcon != null) {
			catalogIcon.dispose();
			catalogIcon = null;
		}
	}

	@Override
	public void loadAssets() {
		Graphics g = game.getGraphics();
		if (loadIcon == null) {
			loadIcon = g.newPixmap("load_symbol.png", true);
		}
		if (saveIcon == null) {
			saveIcon = g.newPixmap("save_symbol.png", true);
		}
		if (catalogIcon == null) {
			catalogIcon = g.newPixmap("catalog_symbol.png", true);
		}
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
		return ScreenCodes.DISK_SCREEN;
	}		
}
