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

import java.util.Locale;

import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public abstract class TradeScreen extends AliteScreen {	
	protected int X_OFFSET = 150;
	protected int Y_OFFSET = 100;
	protected int SIZE     = 225;
	protected int GAP_X    = 300;
	protected int GAP_Y    = 300;
	protected int COLUMNS  = 5;
	protected int ROWS     = 3;
	
	protected Button [][] tradeButton = null;

	protected int currentFrame = 0;
	protected long startSelectionTime = 0;
	protected Button selection = null;
	protected boolean loopingAnimation = false;
	
	protected int endAnimationFrame;
	protected String cashLeft = null;
	protected String errorText = null;
	
	public TradeScreen(Game game, int endAnimationFrame) {
		super(game);
		this.endAnimationFrame = endAnimationFrame;
	}
	
	protected abstract void createButtons();
		
	protected abstract String getCost(int row, int column);
	protected abstract void performTrade(int row, int column);
	protected abstract void presentSelection(int row, int column);
	
	protected void drawAdditionalTradeGoodInformation(int row, int column, float deltaTime) {
		// The default implementation does nothing.
	}

	private void computeCurrentFrame() {
		long timeDiff = (System.nanoTime() - startSelectionTime) / 41666666; // 1/24 second					
		if (loopingAnimation) {
			currentFrame = ((int) timeDiff % endAnimationFrame) + 1;
		} else {
			if (timeDiff > 120) {
				// Repeat the effect all 5 seconds (120 == 24 frames per second * 5)
				startSelectionTime = System.nanoTime();
				currentFrame = 0;
			}					
			currentFrame = (int) timeDiff;
			if (currentFrame > 15) {
				currentFrame = endAnimationFrame;
			}					
		}
	}
	
	protected void presentTradeStatus() {
		Player player = ((Alite) game).getPlayer();
		Graphics g = game.getGraphics();
		String cash = String.format(Locale.getDefault(), "%d.%d", player.getCash() / 10, player.getCash() % 10);
		String freeCargo = player.getCobra().getFreeCargo().getStringWithoutUnit();
		String spareText = Weight.getUnitString(player.getCobra().getFreeCargo().getAppropriateUnit()) + " spare";
		int cashTextWidth = g.getTextWidth("Cash:_", Assets.regularFont);
		int cashWidth = g.getTextWidth(cash, Assets.regularFont);
		int holdTextWidth = g.getTextWidth("Cr      Hold:_", Assets.regularFont);
		int freeCargoWidth = g.getTextWidth(freeCargo, Assets.regularFont);
		int spaceWidth = g.getTextWidth("_", Assets.regularFont);
		
		int halfWidth = (cashTextWidth + cashWidth + spaceWidth + 
				         holdTextWidth + freeCargoWidth + spaceWidth + 
				         g.getTextWidth(spareText, Assets.regularFont)) >> 1;
		int currentX = 860 - halfWidth;
		
		g.drawText("Cash: ", currentX, 1000, AliteColors.get().informationText(), Assets.regularFont);
		currentX += cashTextWidth;
		g.drawText(cash, currentX, 1000, AliteColors.get().additionalText(), Assets.regularFont);
		currentX += cashWidth + spaceWidth;
		g.drawText("Cr      Hold: ", currentX, 1000, AliteColors.get().informationText(), Assets.regularFont);
		currentX += holdTextWidth;
		g.drawText(freeCargo, currentX, 1000, AliteColors.get().additionalText(), Assets.regularFont);
		currentX += freeCargoWidth + spaceWidth;
		g.drawText(spareText, currentX, 1000, AliteColors.get().informationText(), Assets.regularFont);
	}

	protected void performTradeWhileInFlight(int row, int column) {
		SoundManager.play(Assets.error);
		errorText = "Not Docked.";										
	}

	public void presentTradeGoods(float deltaTime) {
		Graphics g = game.getGraphics();
		
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (tradeButton[x][y] == null) {
					continue;
				}
				if (selection == tradeButton[x][y]) {
					if (Settings.animationsEnabled) {
						computeCurrentFrame();
					}
					//tradeButton[x][y].setBorderColors(AliteColors.get().selectedColoredFrameLight(), AliteColors.get().selectedColoredFrameDark());
					tradeButton[x][y].render(g, currentFrame);
					GLES11.glEnable(GLES11.GL_BLEND);
					GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
					GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
					g.fillRect(tradeButton[x][y].getX(), tradeButton[x][y].getY(), tradeButton[x][y].getWidth(), tradeButton[x][y].getHeight(), AliteColors.get().highlightColor());
					GLES11.glDisable(GLES11.GL_BLEND);
					if (errorText == null) {
						presentSelection(y, x);
					}
				} else {
					//tradeButton[x][y].setBorderColors(AliteColors.get().coloredFrameLight(), AliteColors.get().coloredFrameDark());
					tradeButton[x][y].render(g);
				}				
				String price = getCost(y, x);	
				int halfWidth =  g.getTextWidth(price, Assets.regularFont) >> 1;
				g.drawText(price, x * GAP_X + X_OFFSET + (SIZE >> 1) - halfWidth, y * GAP_Y + Y_OFFSET + SIZE + 35, AliteColors.get().price(), Assets.regularFont);
				drawAdditionalTradeGoodInformation(y, x, deltaTime);
			}
		}
		if (errorText != null) {
			game.getGraphics().drawText(errorText, X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
		}
		if (cashLeft != null) {
			g.drawText(cashLeft, X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
		}
	}
	
	@Override
	public void processTouch(TouchEvent touch) {		
		if (getMessage() != null) {
			super.processTouch(touch);
			return;
		}	
		boolean handled = false;
		if (touch.type == TouchEvent.TOUCH_UP) {
			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLUMNS; x++) {
					if (tradeButton[x][y] == null) {
						continue;
					}
					if (tradeButton[x][y].isTouched(touch.x, touch.y)) {
						handled = true;
						if (selection == tradeButton[x][y]) {							
							if (((Alite) game).getCurrentScreen() instanceof FlightScreen) {
								performTradeWhileInFlight(y, x);
							} else {
								SoundManager.play(Assets.click);
								performTrade(y, x);
							}
						} else {
							errorText = null;
							startSelectionTime = System.nanoTime();
							currentFrame = 0;
							selection = tradeButton[x][y];													
							cashLeft = null;
							SoundManager.play(Assets.click);
						}
					}
				}
			}
		}
		if (!handled) {
			super.processTouch(touch);
		}
	}	
	
	public Screen getNewScreen() {
		return newScreen;
	}
}
