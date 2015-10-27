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

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.PlayerCobra;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class LaserPositionSelectionScreen extends AliteScreen {	
	private final int row;
	private final int column;
	private final Button [] pads;
	private final boolean front;
	private final boolean right;
	private final boolean rear;
	private final boolean left;
	private final EquipmentScreen equipmentScreen;
	private static Pixmap cobra;
	
	public LaserPositionSelectionScreen(EquipmentScreen equipmentScreen, Game game, boolean front, boolean right, boolean rear, boolean left, int row, int column) {
		super(game);
		this.row = row;
		this.column = column;
		this.front = front;
		this.right = right;
		this.rear = rear;
		this.left = left;
		this.equipmentScreen = equipmentScreen;
		int count = (front ? 1 : 0) + (right ? 1 : 0) + (rear ? 1 : 0) + (left ? 1 : 0); 
		pads = new Button[count];
	}

	@Override
	public void activate() {
		initializeButtons();		
	}
	
	private void initializeButtons() {		
		int counter = 0;
		if (front) {
			pads[counter++] = new Button(760, 210, 200, 100, "Front", Assets.regularFont, null);
		} 
		if (right) {
			pads[counter++] = new Button(1350, 480, 200, 200, "Right", Assets.regularFont, null);
		}
		if (rear) {
			pads[counter++] = new Button(760, 855, 200, 100, "Rear", Assets.regularFont, null);
		}
		if (left) {
			pads[counter++] = new Button(170, 480, 200, 200, "Left", Assets.regularFont, null);
		}
		for (Button b: pads) {
			b.setTextColor(AliteColors.get().mainText());
		}
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		
		equipmentScreen.present(deltaTime);
		g.gradientRect(160, 160, 1400, 800, false, true, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
		g.rec3d(160, 160, 1400, 800, 10, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
		int halfWidth = g.getTextWidth("Select Position For Laser", Assets.regularFont) >> 1;
		g.drawText("Select Position For Laser", 860 - halfWidth, 195, AliteColors.get().message(), Assets.regularFont);
		g.drawPixmap(cobra, 380, 310);
		
		for (Button b: pads) {
			b.render(g);
		}
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}		
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (touch.x < 160 || touch.y < 160 || touch.x > 1560 || touch.y > 960) {
				equipmentScreen.setLaserPosition(-2);
				newScreen = equipmentScreen;
				return;	
			}
			for (Button b: pads) {
				if (b.isTouched(touch.x, touch.y)) {
					SoundManager.play(Assets.click);
					String t = b.getText();
					if ("Front".equals(t)) {
						equipmentScreen.setLaserPosition(PlayerCobra.DIR_FRONT);
						newScreen = equipmentScreen;
						return;
					}
					if ("Right".equals(t)) {
						equipmentScreen.setLaserPosition(PlayerCobra.DIR_RIGHT);
						newScreen = equipmentScreen;
						return;
					}
					if ("Rear".equals(t)) {
						equipmentScreen.setLaserPosition(PlayerCobra.DIR_REAR);
						newScreen = equipmentScreen;
						return;
					}
					if ("Left".equals(t)) {
						equipmentScreen.setLaserPosition(PlayerCobra.DIR_LEFT);
						newScreen = equipmentScreen;
						return;
					}
				}
 			}
		}
	}
	
	@Override
	protected void performScreenChange() {
		dispose();
		game.setScreen(equipmentScreen);
		equipmentScreen.performTrade(row, column);	
		((Alite) game).getNavigationBar().performScreenChange();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (cobra != null) {
			cobra.dispose();
			cobra = null;
		}
	}

	@Override
	public void loadAssets() {
		if (cobra == null) {
			cobra = game.getGraphics().newPixmap("cobra_small.png", true);
		}
		super.loadAssets();
	}
	
	@Override
	public int getScreenCode() {
		return -1;
	}	
}
