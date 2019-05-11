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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.opengl.GLES11;
import android.view.KeyEvent;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ButtonRegistry;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.generator.enums.Economy;
import de.phbouillon.android.games.alite.screens.NavigationBar;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen.Direction;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public abstract class AliteScreen extends Screen {
	protected static final int AXIS_X  = 0;
	protected static final int AXIS_Y  = 1;
	protected static final int AXIS_Z  = 2;
	protected static final int AXIS_RZ = 3;
	
	protected int startX = -1;
	protected int startY = -1;
	protected int lastX = -1;
	protected int lastY = -1;
	
	private String message = null;
	private MessageType msgType = MessageType.OK;
	private Button ok = null;
	private Button yes = null;
	private Button no = null;
	private Button selectedButton = null;
	protected Screen newScreen;
	protected Screen newControlScreen;
	protected boolean disposed = false;
	protected int messageResult = 0;
	private final List <String> textToDisplay = new ArrayList<String>();
	private TextData[] messageTextData;
	private boolean [] axisAtMax = new boolean[] {false, false, false, false};
	private boolean [] axisAtMin = new boolean[] {false, false, false, false};
	protected boolean messageIsModal = false;
	protected boolean largeMessage = false;
	private int selectedMenuIndex = -1;
	private boolean executeNavigation;
	
	public static enum MessageType {
		OK,
		YESNO
	}
	
	public AliteScreen(Game game) {
		super(game);
		Alite.setDefiningScreen(this);
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
	
		setUpForDisplay(visibleArea);
	}
			
	public void setMessage(String msg) {
		this.msgType = MessageType.OK;
		this.message = msg;	
		Graphics g = game.getGraphics();
		messageTextData = computeTextDisplay(g, message, 530, 410, 660, 60, AliteColors.get().message(), Assets.titleFont, false);
	}

	public void setMessage(String msg, MessageType messageType) {
		this.msgType = messageType;
		this.message = msg;		
		Graphics g = game.getGraphics();
		messageTextData = computeTextDisplay(g, message, 530, 410, 660, 60, AliteColors.get().message(), Assets.titleFont, false);
	}
	
	public void setMessage(String msg, MessageType messageType, GLText font) {
		this.msgType = messageType;
		this.message = msg;		
		Graphics g = game.getGraphics();
		messageTextData = computeTextDisplay(g, message, 530, 410, 660, 60, AliteColors.get().message(), font, false);
	}

	public void setLargeMessage(String msg, MessageType messageType, GLText font) {
		this.msgType = messageType;
		this.message = msg;		
		Graphics g = game.getGraphics();
		largeMessage = true;
		messageTextData = computeTextDisplay(g, message, 380, 310, 960, 60, AliteColors.get().message(), font, false);
	}

	public String getMessage() {
		return message;
	}
	
	protected void centerText(String text, int y, GLText f, long color) {
		int center = ((1920 - NavigationBar.SIZE) >> 1) - (game.getGraphics().getTextWidth(text, f) >> 1);
		game.getGraphics().drawText(text, center, y, color, f);		
	}

	protected void centerText(String text, int minX, int maxX, int y, GLText f, long color) {
		int center = ((maxX - minX) >> 1) + minX - (game.getGraphics().getTextWidth(text, f) >> 1);
		game.getGraphics().drawText(text, center, y, color, f);		
	}

	protected void centerTextWide(String text, int y, GLText f, long color) {
		int center = (1920 >> 1) - (game.getGraphics().getTextWidth(text, f) >> 1);
		game.getGraphics().drawText(text, center, y, color, f);		
	}

	protected void displayTitle(String title) {
		Graphics g = game.getGraphics();
		g.gradientRect(0, 0, 1719, 80, false, true, AliteColors.get().backgroundDark(), AliteColors.get().backgroundLight());
		g.drawPixmap(Assets.aliteLogoSmall, 20, 5);
		g.drawPixmap(Assets.aliteLogoSmall, 1600, 5);
		centerText(title, 60, Assets.titleFont, AliteColors.get().message());
	}
		
	protected void displayWideTitle(String title) {
		Graphics g = game.getGraphics();
		g.gradientRect(0, 0, 1919, 80, false, true, AliteColors.get().backgroundDark(), AliteColors.get().backgroundLight());
		g.drawPixmap(Assets.aliteLogoSmall, 20, 5);
		g.drawPixmap(Assets.aliteLogoSmall, 1800, 5);
		centerTextWide(title, 60, Assets.titleFont, AliteColors.get().message());
	}

	private void renderMessage(AliteScreen screen) {
		if (message != null) {
			Graphics g = game.getGraphics();
			if (largeMessage) {
				g.gradientRect(360, 240, 1000, 600, false, true, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
				g.rec3d(360, 240, 1000, 600, 5, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
			} else {
				g.gradientRect(510, 340, 700, 400, false, true, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());
				g.rec3d(510, 340, 700, 400, 5, AliteColors.get().backgroundLight(), AliteColors.get().backgroundDark());				
			}
			displayText(g, messageTextData);
			if (msgType == MessageType.OK) {
				if (ok == null) {
					Alite.setDefiningScreen(screen);
					ok = largeMessage ? new Button(1210, 690, 150, 150, "OK", Assets.regularFont, null) : new Button(1060, 590, 150, 150, "OK", Assets.regularFont, null);
					ButtonRegistry.get().addMessageButton(ok);
				}
				ok.render(g);
			} else if (msgType == MessageType.YESNO) {
				if (yes == null) {
					Alite.setDefiningScreen(screen);
					yes = largeMessage ? new Button(1010, 690, 150, 150, "Yes", Assets.regularFont, null) : new Button(860, 590, 150, 150, "Yes", Assets.regularFont, null);
					ButtonRegistry.get().addMessageButton(yes);
				}
				if (no == null) {
					Alite.setDefiningScreen(screen);
					no = largeMessage ? new Button(1210, 690, 150, 150, "No", Assets.regularFont, null) : new Button(1060, 590, 150, 150, "No", Assets.regularFont, null);
					ButtonRegistry.get().addMessageButton(no);
				}
				yes.render(g);
				no.render(g);
			}
		}
	}

	protected int movedAxis(int axis, float value) {
		int result = 0;
		if (value > 0.75f) {
			if (!axisAtMax[axis]) {
				axisAtMax[axis] = true;
				result = 1;
			}
		}
		if (value < -0.75f) {
			if (!axisAtMin[axis]) {
				axisAtMin[axis] = true;
				result = -1;
			}
		}
		if (value < 0.2f) {
			axisAtMax[axis] = false;
		}
		if (value > -0.2f) {
			axisAtMin[axis] = false;
		}
		return result;
	}
		
	public void processNavigationJoystick(float z, float rz) {	
		float rzAxis = movedAxis(AXIS_RZ, rz);
		if (rzAxis == -1) {
			NavigationBar navBar = ((Alite) game).getNavigationBar();
			if (selectedMenuIndex == -1) {
				selectedMenuIndex = navBar.getActiveIndex();
			}
			selectedMenuIndex = navBar.getPreviousPosition(selectedMenuIndex);
		}
		if (rzAxis == 1) {
			NavigationBar navBar = ((Alite) game).getNavigationBar();
			if (selectedMenuIndex == -1) {
				selectedMenuIndex = navBar.getActiveIndex();
			}
			selectedMenuIndex = navBar.getNextPosition(selectedMenuIndex);
		}
	}
	
	public void processNavigationButtonDown(int button) {		
	}
	
	public void processNavigationButtonUp(int button) {	
		if (selectedMenuIndex != -1 && button == KeyEvent.KEYCODE_BUTTON_B) {			
			executeNavigation = true;
		}
	}

	@Override
	public synchronized void update(float deltaTime) {
		NavigationBar navBar = ((Alite) game).getNavigationBar();
		if (selectedMenuIndex != -1) {
			navBar.setHighlightedPosition(selectedMenuIndex);
			navBar.ensureVisible(selectedMenuIndex);
		}
		newScreen = null;
		for (TouchEvent event: game.getInput().getTouchEvents()) {
			if (event.type == TouchEvent.TOUCH_DOWN && event.x >= (1920 - NavigationBar.SIZE)) {
				startX = event.x;
				startY = lastY = event.y;
			}
			if (event.type == TouchEvent.TOUCH_DRAGGED && event.x >= (1920 - NavigationBar.SIZE)) {
				if (event.y < lastY) {
					navBar.increasePosition(lastY - event.y);
				} else {
					navBar.decreasePosition(event.y - lastY);
				}
				lastY = event.y;
			}
			if (event.type == TouchEvent.TOUCH_UP) {
				if (Math.abs(startX - event.x) < 20 &&
					Math.abs(startY - event.y) < 20) {
					newScreen = navBar.touched((Alite) game, event.x, event.y);					
				}
			}
			ButtonRegistry.get().processTouch(event);
			processTouch(event);
		}	
		if (executeNavigation) {
			executeNavigation = false;
			newScreen = navBar.getScreenForSelection((Alite) game, selectedMenuIndex);			
			if (newScreen != null && newScreen.getClass().getName().equals(getClass().getName())) {
				newScreen = null;
			}
			if (newScreen != null) {
				navBar.setPendingIndex(selectedMenuIndex);
			}
			navBar.setHighlightedPosition(-1);
			selectedMenuIndex = -1;
		}
		if (newScreen == null) {
			newScreen = newControlScreen;
		}
		newControlScreen = null;
		if (newScreen != null) {
			performScreenChange();
			postScreenChange();
		}		
	}	
	
	public synchronized void updateWithoutNavigation(float deltaTime) {
		selectedMenuIndex = -1;
		newScreen = null;
		executeNavigation = false;
		for (TouchEvent event: game.getInput().getTouchEvents()) {
			ButtonRegistry.get().processTouch(event);
			processTouch(event);
		}	
		if (newScreen == null) {
			newScreen = newControlScreen;
		}
		newControlScreen = null;
		if (newScreen != null) {
			performScreenChange();
			postScreenChange();
		}		
	}	
	
	protected boolean inFlightScreenChange() {
		Screen oldScreen = game.getCurrentScreen();
		if (oldScreen instanceof FlightScreen) {
			// We're in flight, so do not dispose the flight screen,
			// but the flight's information screen
			FlightScreen flightScreen = (FlightScreen) oldScreen;
			Screen oldInformationScreen = flightScreen.getInformationScreen();
			if (oldInformationScreen != null) {
				oldInformationScreen.dispose();				
			}
			newScreen.loadAssets();
			newScreen.activate();
			newScreen.resume();
			newScreen.update(0);
			((Alite) game).getGraphics().setClip(-1, -1, -1, -1);
			flightScreen.setInformationScreen((AliteScreen) newScreen);
			((Alite) game).getNavigationBar().performScreenChange();
			return true;
		}		
		return false;
	}
	
	protected void performScreenChange() {
		executeNavigation = false;
		if (inFlightScreenChange()) {
			return;
		}		
		Screen oldScreen = game.getCurrentScreen();
		if (!(newScreen instanceof TextInputScreen)) {
			oldScreen.dispose();
		}
		game.setScreen(newScreen);
		((Alite) game).getNavigationBar().performScreenChange();
		postScreenChange();
		oldScreen = null;
	}
	
	private void cancelMessage() {
		SoundManager.play(Assets.click);
		message = null;
		yes = null;
		no = null;
		messageResult = -1;
		game.getInput().getTouchEvents();
		ButtonRegistry.get().clearMessageButtons();						
	}
	
	private void hitOk() {
		SoundManager.play(Assets.click);
		message = null;
		ok = null;
		game.getInput().getTouchEvents();
		ButtonRegistry.get().clearMessageButtons();
		messageIsModal = false;
		largeMessage = false;		
	}
	
	private void hitYes() {
		SoundManager.play(Assets.click);
		message = null;
		yes = null;
		no = null;
		messageResult = 1;
		game.getInput().getTouchEvents();
		ButtonRegistry.get().clearMessageButtons();
		messageIsModal = false;
		largeMessage = false;		
	}
	
	private void hitNo() {
		SoundManager.play(Assets.click);
		message = null;
		yes = null;
		no = null;
		messageResult = -1;
		game.getInput().getTouchEvents();
		ButtonRegistry.get().clearMessageButtons();
		messageIsModal = false;
		largeMessage = false;		
	}
	
	@Override
	public void processJoystick(float x, float y, float z, float rz, float hatX, float hatY) {		
		if (movedAxis(AXIS_Z, z) != 0) {
			if (selectedButton != null) {
				selectedButton.fingerUp(5);
			}
			if (selectedButton == null) {
				selectedButton = ok != null ? ok : yes != null ? yes : null;
			} else {
				if (ok != null) {
					selectedButton = ok;
				} else {
					if (yes != null && no != null) {
						selectedButton = selectedButton == yes ? no : yes;
					}
				}
			}
			if (selectedButton != null) {
				selectedButton.fingerDown(5);
			}
		}
	}

	@Override
	public void processButtonUp(int button) {
		if (button == KeyEvent.KEYCODE_BUTTON_A) {
			if (selectedButton == null) {
				if (ok != null) {
					hitOk();
				} else if (no != null) {
					hitNo();
				}
			} else {
				if (selectedButton == ok) {
					hitOk();
				} else if (selectedButton == yes) {
					hitYes();
				} else if (selectedButton == no) {
					hitNo();
				}
			}
			selectedButton = null;
		}
		if (button == KeyEvent.KEYCODE_BUTTON_X) {
			if (message != null && !messageIsModal) {
				cancelMessage();
			}
		}
	}

	protected void processTouch(TouchEvent touch) {		
		if (touch.type != TouchEvent.TOUCH_UP) {
			return;
		}
		if (message != null && !messageIsModal) {
			if (touch.x < 510 || touch.y < 340 || touch.x > 1210 || touch.y > 740) {
				cancelMessage();
				touch.x = -1;
				touch.y = -1;
			}
		}
		if (ok != null) {
			if (ok.isTouched(touch.x, touch.y)) {
				hitOk();
				touch.x = -1;
				touch.y = -1;
				return;
			}			
		}
		if (yes != null) {
			if (yes.isTouched(touch.x, touch.y)) {
				hitYes();
				touch.x = -1;
				touch.y = -1;				
				return;
			}			
		}
		if (no != null) {		
			if (no.isTouched(touch.x, touch.y)) {
				hitNo();
				touch.x = -1;
				touch.y = -1;
				return;			
			}			
		}
	}
	
	protected void drawArrow(final Graphics g, int x1, int y1, int x2, int y2, long color, Direction arrowHead) {
		int temp;
		if (x1 > x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		if (y1 > y2) {
			temp = y1;
			y1 = y2;
			y2 = temp;
		}
		g.drawLine(x1, y1, x2, y2, color);
		for (int i = 1; i < 10; i++) {
			switch (arrowHead) {
				case DIR_LEFT:  g.drawLine(x1 + i, y1 - i, x1 + i, y1 + i, color); break;
				case DIR_RIGHT: g.drawLine(x2 - i, y1 - i, x2 - i, y1 + i, color); break;
				case DIR_UP:    g.drawLine(x1 - i, y1 + i, x1 + i, y1 + i, color); break;
				case DIR_DOWN:  g.drawLine(x1 - i, y2 - i, x1 + i, y2 - i, color); break;
			}			
		}		
	}
	
	protected long getColor(Economy economy) {
		switch (economy) {
			case RICH_INDUSTRIAL:      return AliteColors.get().richIndustrial();
			case AVERAGE_INDUSTRIAL:   return AliteColors.get().averageIndustrial();
			case POOR_INDUSTRIAL:      return AliteColors.get().poorIndustrial();
			case MAINLY_INDUSTRIAL:    return AliteColors.get().mainIndustrial();
			case MAINLY_AGRICULTURAL:  return AliteColors.get().mainAgricultural();
			case RICH_AGRICULTURAL:    return AliteColors.get().richAgricultural();
			case AVERAGE_AGRICULTURAL: return AliteColors.get().averageAgricultural();
			case POOR_AGRICULTURAL:    return AliteColors.get().poorAgricultural();
		}
		return 0;
	}
	
	protected final TextData [] computeTextDisplay(Graphics g, String text, int x, int y, int fieldWidth, int deltaY, long color, GLText font, boolean centered) {
		String [] textWords = text.split(" ");
		textToDisplay.clear();
		String result = "";
		for (String word: textWords) {
			if (result.length() == 0) {
				result += word;
			} else {
				String test = result + " " + word;
				int width = g.getTextWidth(test, font);
				if (width > fieldWidth) {
					textToDisplay.add(result);
					result = word;
				} else {
					result += " " + word;
				}
			}
		}
		if (result.length() > 0) {
			textToDisplay.add(result);
		}			
		int count = 0;
		ArrayList <TextData> resultList = new ArrayList<TextData>();
		for (String t: textToDisplay) {
			int halfWidth = centered ? (g.getTextWidth(t, font) >> 1) : (fieldWidth >> 1);
			resultList.add(new TextData(t, x + (fieldWidth >> 1) - halfWidth, y + deltaY * count++, color, font));		
		}
		return resultList.toArray(new TextData[0]);
	}
	
	protected final void displayText(Graphics g, TextData [] textToDisplay) {
		if (textToDisplay == null) {
			return;
		}
		for (TextData td: textToDisplay) {
			g.drawText(td.text, td.x, td.y, td.color, td.font);
		}
	}
			
	@Override
	public void dispose() {
		disposed = true;
		ButtonRegistry.get().removeButtons(this);
	}
	
	@Override
	public void loadAssets() {
		disposed = false;
	}
	
	@Override
	public void pause() {
	}
	
	@Override
	public void resume() {
	}	
	
	@Override
	public void postLayout(Object dataObject) {		
	}
	
	@Override
	public void postNavigationRender(float deltaTime) {	
	}
	
	protected final void setUpForDisplay(Rect visibleArea) {		
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);

		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		GlUtils.ortho(game, visibleArea);

		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();

		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);	
	}
		
	@Override
	public void renderNavigationBar() {
		((Alite) game).getNavigationBar().render(game.getGraphics());
	}
	
	@Override
	public void postPresent(float deltaTime) {
		renderMessage(this);
	}
	
	@Override
	public void postScreenChange() {		
	}
}
