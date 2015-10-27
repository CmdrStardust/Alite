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

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class HexNumberPadScreen extends AliteScreen {
	private static final int OFFSET_X = 20;
	private static final int OFFSET_Y = 20;
	private static final int BUTTON_SIZE = 150;
	private static final int GAP = 10;
	
	private final int xPos;
	private final int yPos;
	private final String [] buttonTexts = new String [] {"7", "8", "9", "E", "F", "4", "5", "6", "C", "D", "1", "2", "3", "A", "B", "0", "<-", "OK"};
	private final Button [] pads;
	private String currentValueString = "";
	private final HackerScreen hackerScreen;
	private final int valueIndex;
	
	public HexNumberPadScreen(HackerScreen hackerScreen, Game game, int x, int y, int valueIndex) {
		super(game);
		this.xPos = x;
		this.yPos = y;
		this.valueIndex = valueIndex;
		this.hackerScreen = hackerScreen;
		
		pads = new Button[18];
	}

	@Override
	public void activate() {
		initializeButtons();
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(xPos);
		dos.writeInt(yPos);
		dos.writeInt(valueIndex);
		dos.writeInt(currentValueString.length());
		if (currentValueString.length() > 0) {
			dos.writeChars(currentValueString);
		}
		hackerScreen.saveScreenState(dos);
	}

	public static boolean initialize(Alite alite, final DataInputStream dis) {
		try {
			int xPos = dis.readInt();
			int yPos = dis.readInt();
			int valueIndex  = dis.readInt();
			int len  = dis.readInt();
			String currentValue = "";
			for (int i = 0; i < len; i++) {
				currentValue += dis.readChar();
			}
			HackerScreen hackerScreen = HackerScreen.readScreen(alite, dis);
			hackerScreen.loadAssets();
			hackerScreen.activate();
			HexNumberPadScreen hnps = new HexNumberPadScreen(hackerScreen, alite, xPos, yPos, valueIndex);
			hnps.currentValueString = currentValue;
			alite.setScreen(hnps);
		} catch (Exception e) {
			AliteLog.e("Hex Number Pad Screen Initialize", "Error in initializer.", e);
			return false;
		}
		return true;
	}

	private void initializeButtons() {		
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 5; x++) {
				if (y == 3 && x > 2) {
					break;
				}
				pads[y * 5 + x] = new Button(xPos + x * (BUTTON_SIZE + GAP) + OFFSET_X,
						                     yPos + y * (BUTTON_SIZE + GAP) + OFFSET_Y,
						                     BUTTON_SIZE, BUTTON_SIZE,
						                     buttonTexts[y * 5 + x], Assets.regularFont, null);
			}
		}
	}
	
	@Override
	public void renderNavigationBar() {
		// No navigation bar desired.
	}

	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		
		hackerScreen.present(deltaTime);
		int width = (BUTTON_SIZE + GAP) * 5 + 2 * OFFSET_X;
		int height =  (BUTTON_SIZE + GAP) * 4 + 2 * OFFSET_Y;
		g.gradientRect(xPos, yPos, width, height, false, true, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
		g.rec3d(xPos, yPos, width, height, 10, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
		for (Button b: pads) {
			b.render(g);
		}
		g.fillRect(50, 1018, 1669, 56, AliteColors.get().background());
		g.drawText("New value for byte " + String.format("%02X", valueIndex) + ": " + currentValueString, 50, 1050, AliteColors.get().message(), Assets.regularFont);
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (getMessage() != null) {
			return;
		}		
		if (touch.type == TouchEvent.TOUCH_UP) {
			int width = (BUTTON_SIZE + GAP) * 5 + 2 * OFFSET_X;
			int height =  (BUTTON_SIZE + GAP) * 4 + 2 * OFFSET_Y;
			if (touch.x < xPos || touch.y < yPos || touch.x > (xPos + width) || touch.y > (yPos + height)) {				
				newScreen = hackerScreen;
			}
			for (Button b: pads) {
				if (b.isTouched(touch.x, touch.y)) {
					SoundManager.play(Assets.click);
					String t = b.getText();
					if ("<-".equals(t)) {
						if (currentValueString.length() > 0) {
							currentValueString = currentValueString.substring(0, currentValueString.length() - 1);
						}
					} else if ("OK".equals(t)) {
						try {
							byte newValue = (byte) Integer.parseInt(currentValueString, 16);
							hackerScreen.state.values[valueIndex] = newValue;
							hackerScreen.values[valueIndex].setText(String.format("%02X", newValue));
						} catch (NumberFormatException e) {
							// Ignore and don't set new value (can happen if user hits ok before entering a new number)
						}						
						newScreen = hackerScreen;
					} else if (currentValueString.length() < 2) {
						currentValueString += t;
					}
				}
 			}
		}
	}
		
	@Override
	protected void performScreenChange() {
		if (inFlightScreenChange()) {
			return;
		}		
		dispose();
		game.setScreen(hackerScreen);
		((Alite) game).getNavigationBar().performScreenChange();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void loadAssets() {
		super.loadAssets();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.HEX_NUMBER_PAD_SCREEN;
	}	
}
