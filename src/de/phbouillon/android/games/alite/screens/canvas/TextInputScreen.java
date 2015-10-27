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

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class TextInputScreen extends AliteScreen {	
	private static final int BUTTON_WIDTH = 170;
	private static final int BUTTON_HEIGHT = 100;
	private static final int GAP_X = 20;
	private static final int GAP_Y = 40;
	private static final int OFFSET_X = 20;
	private static final int OFFSET_Y = 400;
	
	private final String title;
	private final String message;
	private String currentText;
	private final AliteScreen sourceScreen;
	private final int messageWidth;
	private boolean showCursor = true;
	private long lastBlinkTime;
	private final Button [] keyboard = new Button[41];
	private final int spaceWidth;
	private final TextCallback callback;
	private int maxLength = 0;
	private boolean allowSpace = true;
	private boolean shiftPressed = false;
	private boolean keyEnteredWhileShiftPressed = false;
	private boolean secondShift = false;
	
	public TextInputScreen(Game game, String title, String message, String currentText, AliteScreen source, TextCallback callback) {
		super(game);
		((Alite) game).getNavigationBar().setActive(false);
		this.title = title;
		this.message = message;
		this.currentText = currentText;
		this.sourceScreen = source;
		this.callback = callback;
		Graphics g = game.getGraphics();
		this.messageWidth = g.getTextWidth(message + ": ", Assets.titleFont) + 20;
		this.spaceWidth = g.getTextWidth("m m", Assets.titleFont) - g.getTextWidth("mm", Assets.titleFont);
		lastBlinkTime = System.nanoTime();
	}
	
	@Override
	public void activate() {		
		initializeKeyboard();		
	}
		
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	
	public void setAllowSpace(boolean allowSpace) {
		this.allowSpace = allowSpace;
	}
	
	private final Button b(String text, int row, int column) {
		int x = OFFSET_X + (column - 1) * GAP_X + (column - 1) * BUTTON_WIDTH;
		int y = OFFSET_Y + (row - 1) * GAP_Y + (row - 1) * BUTTON_HEIGHT;
	
		if (row == 3) {
			x += BUTTON_WIDTH / 2;
		} else if (row == 4) {
			x += BUTTON_WIDTH + BUTTON_WIDTH / 2 + GAP_X;
		}
		Button result = new Button(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, text, Assets.titleFont, null);
		result.setGradient(true);
		return result;
	}
	
	private final void initializeKeyboard() {
		keyboard[0] = b("1", 1, 1);
		keyboard[1] = b("2", 1, 2);
		keyboard[2] = b("3", 1, 3);
		keyboard[3] = b("4", 1, 4);
		keyboard[4] = b("5", 1, 5);
		keyboard[5] = b("6", 1, 6);
		keyboard[6] = b("7", 1, 7);
		keyboard[7] = b("8", 1, 8);
		keyboard[8] = b("9", 1, 9);
		keyboard[9] = b("0", 1, 10);

		keyboard[10] = b("q", 2, 1);
		keyboard[11] = b("w", 2, 2);
		keyboard[12] = b("e", 2, 3);
		keyboard[13] = b("r", 2, 4);
		keyboard[14] = b("t", 2, 5);
		keyboard[15] = b("QWERTZ".equals(Settings.keyboardLayout) ? "z" : "y", 2, 6);
		keyboard[16] = b("u", 2, 7);
		keyboard[17] = b("i", 2, 8);
		keyboard[18] = b("o", 2, 9);
		keyboard[19] = b("p", 2, 10);
		
		keyboard[20] = b("a", 3, 1);
		keyboard[21] = b("s", 3, 2);
		keyboard[22] = b("d", 3, 3);
		keyboard[23] = b("f", 3, 4);
		keyboard[24] = b("g", 3, 5);
		keyboard[25] = b("h", 3, 6);
		keyboard[26] = b("j", 3, 7);
		keyboard[27] = b("k", 3, 8);
		keyboard[28] = b("l", 3, 9);

		keyboard[29] = b("QWERTZ".equals(Settings.keyboardLayout) ? "y" : "z", 4, 1);
		keyboard[30] = b("x", 4, 2);
		keyboard[31] = b("c", 4, 3);
		keyboard[32] = b("v", 4, 4);
		keyboard[33] = b("b", 4, 5);
		keyboard[34] = b("n", 4, 6);
		keyboard[35] = b("m", 4, 7);
				
		keyboard[36] = new Button(OFFSET_X + 2 * GAP_X + 2 * BUTTON_WIDTH + BUTTON_WIDTH / 2, OFFSET_Y + 4 * GAP_Y + 4 * BUTTON_HEIGHT, 5 * BUTTON_WIDTH + 4 * GAP_X, BUTTON_HEIGHT, "Space", Assets.titleFont, null);
		keyboard[36].setGradient(true);
		keyboard[36].setVisible(allowSpace);
		
		keyboard[37] = new Button(OFFSET_X, OFFSET_Y + 3 * GAP_Y + 3 * BUTTON_HEIGHT, BUTTON_WIDTH + BUTTON_WIDTH / 2, BUTTON_HEIGHT, "Shift", Assets.titleFont, null);
		keyboard[37].setGradient(true);
		keyboard[38] = new Button(1900 - OFFSET_X - BUTTON_WIDTH - BUTTON_WIDTH / 2, OFFSET_Y + 3 * GAP_Y + 3 * BUTTON_HEIGHT, BUTTON_WIDTH + BUTTON_WIDTH / 2, BUTTON_HEIGHT, "<-", Assets.titleFont, null);
		keyboard[38].setGradient(true);
		
		keyboard[39] = new Button(OFFSET_X, OFFSET_Y + 4 * GAP_Y + 4 * BUTTON_HEIGHT, BUTTON_WIDTH + BUTTON_WIDTH / 2, BUTTON_HEIGHT, "Ok", Assets.titleFont, null);
		keyboard[39].setGradient(true);
		keyboard[40] = new Button(1900 - OFFSET_X - BUTTON_WIDTH - BUTTON_WIDTH / 2, OFFSET_Y + 4 * GAP_Y + 4 * BUTTON_HEIGHT, BUTTON_WIDTH + BUTTON_WIDTH / 2, BUTTON_HEIGHT, "Cancel", Assets.titleFont, null);
		keyboard[40].setGradient(true);
	}

	@Override
	public void update(float deltaTime) {		
		super.update(deltaTime);
		if (keyboard[36] != null) {
			keyboard[36].setVisible(allowSpace);
		}
	}
	
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayWideTitle(title);
		
		g.fillRect(0, 120, 1920, 200, AliteColors.get().textAreaBackground());
		g.rec3d(0, 120, 1920, 200, 6, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
		
		g.drawText(message + ":", 50, 200, AliteColors.get().message(), Assets.titleFont);
		g.drawText(currentText, 50 + messageWidth, 200, AliteColors.get().mainText(), Assets.titleFont);
		if (System.nanoTime() - lastBlinkTime > 500000000) {
			showCursor = !showCursor;
			lastBlinkTime = System.nanoTime();
		}
		if (showCursor) {
			int cursorXPos = 50 + messageWidth + g.getTextWidth(currentText, Assets.titleFont);
			if (currentText.endsWith(" ")) {
				int spacesAtEnd = 0;
				for (int i = currentText.length() - 1; i >= 0; i--) {
					if (currentText.charAt(i) == ' ') {
						spacesAtEnd++;
					} else {
						break;
					}
				}
				cursorXPos += spacesAtEnd * spaceWidth;
			}
			g.drawText("|", cursorXPos, 200, AliteColors.get().cursor(), Assets.titleFont);
		}
		
		for (Button b: keyboard) {
			if (b != null) {
				b.render(g);
			}
		}
	}
	
	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);	
		if (getMessage() != null) {
			return;
		}		
		if (touch.type == TouchEvent.TOUCH_DOWN) {
			if (keyboard[37] != null && keyboard[37].isTouched(touch.x, touch.y)) {
				if (keyboard[10].getText().equals("Q")) {
					secondShift = true;
				}
				shiftPressed = true;
				keyEnteredWhileShiftPressed = false;
				for (int i = 10; i < 36; i++) {
					keyboard[i].setText(keyboard[i].getText().toUpperCase(Locale.getDefault()));
				}							
			}
		}
		if (touch.type == TouchEvent.TOUCH_UP) {			
			for (Button b: keyboard) {
				if (b.isTouched(touch.x, touch.y)) {
					SoundManager.play(Assets.click);
					if (b.getText().equals("Space") && allowSpace) {
						currentText += " ";
					} else if (b.getText().equals("Shift")) {
						shiftPressed = false;
						if (keyEnteredWhileShiftPressed || secondShift) {
							for (int i = 10; i < 36; i++) {
								keyboard[i].setText(keyboard[i].getText().toLowerCase(Locale.getDefault()));
							}							
						}
						secondShift = false;
						keyEnteredWhileShiftPressed = false;
					} else if (b.getText().equals("<-")) {
						if (currentText.length() > 0) {
							currentText = currentText.substring(0, currentText.length() - 1);
						}
					} else if (b.getText().equals("Ok")) {
						((Alite) game).getNavigationBar().setActive(true);						
						callback.onOk(currentText);							
						newScreen = sourceScreen;
					} else if (b.getText().equals("Cancel")) {
						((Alite) game).getNavigationBar().setActive(true);
						callback.onCancel();
						newScreen = sourceScreen;
					} else {
						if (maxLength == 0 || maxLength > currentText.length()) {
							if (shiftPressed) {
								keyEnteredWhileShiftPressed = true;
							}
							currentText += b.getText();
						}
						if (!shiftPressed) {
							for (int i = 10; i < 36; i++) {
								keyboard[i].setText(keyboard[i].getText().toLowerCase(Locale.getDefault()));
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void renderNavigationBar() {
		// No navigation bar desired.
	}
	
	@Override
	public int getScreenCode() {
		return sourceScreen == null ? -1 : sourceScreen.getScreenCode();
	}	
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		if (sourceScreen != null) {
			sourceScreen.saveScreenState(dos);
		}
	}
}
