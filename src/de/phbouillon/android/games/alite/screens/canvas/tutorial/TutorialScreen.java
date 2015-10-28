package de.phbouillon.android.games.alite.screens.canvas.tutorial;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.PulsingHighlighter;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.AliteStartManager;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.screens.NavigationBar;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.TextData;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public abstract class TutorialScreen extends AliteScreen {
	private static final int TEXT_LINE_HEIGHT = 50;
	
	protected final transient Alite alite;
	protected final transient List <TutorialLine> lines = new ArrayList<TutorialLine>();
	protected transient TutorialLine currentLine;
	protected int currentLineIndex;
	protected transient TextData [] textData;
	protected final transient MediaPlayer mediaPlayer;
	protected final boolean isGl;
	protected boolean hideCloseButton = false;
	
	private boolean tutorialAborted = false;
	private transient Button closeButton;
	private transient Pixmap closeIcon;
	
	private int currentX = -1;
	private int currentY = -1;
	private int currentWidth = -1;
	private int currentHeight = -1;
	
	public TutorialScreen(Alite alite) {
		super(alite);
		isGl = false;
		this.alite = alite;
		this.currentLineIndex = -1;
		this.currentLine = null;
		this.mediaPlayer = new MediaPlayer();
	}
	
	public TutorialScreen(Alite alite, boolean gl) {
		super(alite);
		isGl = gl;
		this.alite = alite;
		this.currentLineIndex = -1;
		this.currentLine = null;
		this.mediaPlayer = new MediaPlayer();
	}

	@Override
	public void activate() {
		closeButton = new Button(0, 980, 100, 100, closeIcon);
		closeButton.setUseBorder(false);
	}
	
	protected PulsingHighlighter makeHighlight(int x, int y, int width, int height) {
		return new PulsingHighlighter(alite, x, y, width, height, 20, 
				AliteColors.get().pulsingHighlighterLight(), 
				AliteColors.get().pulsingHighlighterDark());
	}
	
	protected void renderText() {
		if (currentLine != null) {
			currentY = currentLine.getY();
			currentHeight = currentLine.getHeight();
			if (currentHeight != 0 && textData != null && textData.length > 0) {
				currentHeight = textData[textData.length - 1].y + 30 - currentY;
				if (currentY + currentHeight > 1080) {
					AliteLog.e("Overfull VBox", "Attention: Overfull VBox for " + currentLine.getText() + " => " + (currentY + currentHeight));
				}
				currentLine.setHeight(currentHeight);
			}
			currentX = currentLine.getX();
			currentWidth = currentLine.getWidth();
		}
		
		if (currentLine == null || currentLine.getText().isEmpty()) {
			return;
		}
		
		Graphics g = alite.getGraphics();
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		GLES11.glEnable(GLES11.GL_BLEND);
		if (currentX != -1 && currentY != -1) {
			g.gradientRect(currentX, currentY, currentWidth, currentHeight, true, true, AliteColors.get().tutorialBubbleDark(), AliteColors.get().tutorialBubbleLight());
		}
		GLES11.glDisable(GLES11.GL_BLEND);
		if (currentX != -1 && currentY != -1) {
			g.rec3d(currentX, currentY, currentWidth, currentHeight, 4, AliteColors.get().tutorialBubbleLight(), AliteColors.get().tutorialBubbleDark());
		}
		if (currentLine != null && textData != null) {
			displayText(g, textData);
		}		
	}
	
	protected void initLines(int tutorialIndex, String... texts) {
		int index = 1;
		String path = "sound/tutorial/" + tutorialIndex + "/";
		try {
			AndroidFileIO afi = (AndroidFileIO) alite.getFileIO();
			String audioName = path + (index < 10 ? "0" + index : index) + ".mp3";
			for (String t: texts) {
				lines.add(new TutorialLine(AliteStartManager.HAS_EXTENSION_APK ? afi.getPrivatePath(audioName) :
					                                                             afi.getFileDescriptor(audioName), t));
				index++;
			} 
		} catch (IOException e) {
			AliteLog.e("Error Reading Tutorial", "Error reading Tutorial", e);
		}
	}
	
	protected TutorialLine addLine(int tutorialIndex, String line) {
		String path = "sound/tutorial/" + tutorialIndex + "/";		
		try {
			int index = lines.size() + 1;
			AndroidFileIO afi = (AndroidFileIO) alite.getFileIO();
			String audioName = path + (index < 10 ? "0" + index : index) + ".mp3";
			TutorialLine result = new TutorialLine(AliteStartManager.HAS_EXTENSION_APK ? afi.getPrivatePath(audioName) :
				                                                                         afi.getFileDescriptor(audioName), line);
			lines.add(result);
			return result;
		} catch (IOException e) {
			AliteLog.e("Error Reading Tutorial", "Error reading Tutorial", e);
			return null;
		}				
	}
		
	protected TutorialLine addLine(int tutorialIndex, String line, String option) {
		String path = "sound/tutorial/" + tutorialIndex + "/";		
		int index = lines.size() + 1;
		AndroidFileIO afi = (AndroidFileIO) alite.getFileIO();
		String audioName = path + (index < 10 ? "0" + index : index);
		try {
			TutorialLine result = new TutorialLine(AliteStartManager.HAS_EXTENSION_APK ? afi.getPrivatePath(audioName + ".mp3") :
				                                                                         afi.getFileDescriptor(audioName + ".mp3"), line);
			result.addSpeech(AliteStartManager.HAS_EXTENSION_APK ? afi.getPrivatePath(audioName + option + ".mp3") :
				                                                   afi.getFileDescriptor(audioName + option + ".mp3"));
			lines.add(result);
			return result;
		} catch (IOException e) {
			AliteLog.e("Error Reading Tutorial", "Error reading Tutorial", e);
			return null;
		}
	}

	protected TutorialLine addEmptyLine() {
		TutorialLine result = new TutorialLine(null, "").setHeight(0).setWidth(0);
		lines.add(result);
		return result;
	}

	public Screen updateNavBar(float deltaTime) {	
		NavigationBar navBar = ((Alite) game).getNavigationBar();
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
					Screen screen = navBar.touched((Alite) game, event.x, event.y);
					navBar.resetPending();
					return screen;
				}
			}
		}		
		return null;
	}
	
	protected abstract void doPresent(float deltaTime);
	
	protected void renderGlPart(float deltaTime, final Rect visibleArea) {		
	}
	
	protected void doUpdate(float deltaTime) {		
	}
	
	@Override 
	public void renderNavigationBar() {
		if (isGl) {
			return;
		}
		super.renderNavigationBar();
	}
	
	@Override
	public void present(float deltaTime) {
		if (isGl) {
			Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
			renderGlPart(deltaTime, visibleArea);
			setUpForDisplay(visibleArea);
		}
		if (currentLine != null) {
			currentLine.prePresent(deltaTime);
		}
		doPresent(deltaTime);
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		GLES11.glEnable(GLES11.GL_BLEND);
		if (!hideCloseButton) {
			closeButton.render(alite.getGraphics());
		}
		GLES11.glDisable(GLES11.GL_BLEND);
		if (currentLine != null) {
			currentLine.postPresent(deltaTime);
		}
	}
	
	@Override
	public void postNavigationRender(float deltaTime) {
		if (currentLine != null) {
			GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE_MINUS_SRC_ALPHA);
			GLES11.glEnable(GLES11.GL_BLEND);
			currentLine.renderHighlights(deltaTime);
			GLES11.glDisable(GLES11.GL_BLEND);
		}		
	}

	protected void checkTutorialClose(TouchEvent touch) {
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (closeButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				setMessage("Are you sure you want to quit this tutorial?", MessageType.YESNO);
			}
		}
	}

	@Override
	public void processTouch(TouchEvent event) {
		super.processTouch(event);
		if (messageResult == 1) {
			if (currentLine != null) {
				mediaPlayer.reset();
			}
			newScreen = new TutorialSelectionScreen(alite);			
			performScreenChange();
			postScreenChange();			
			tutorialAborted = true;
		}
	}
	
	@Override
	public void update(float deltaTime) {
		if (tutorialAborted) {
			game.getInput().getTouchEvents();
			return;
		}
		if (!hideCloseButton) {
			for (TouchEvent event: game.getInput().getAndRetainTouchEvents()) {
				processTouch(event);
				if (tutorialAborted) {
					return;
				}
				checkTutorialClose(event);
			}
		}
		
		if (currentLine == null) {
			currentLineIndex++;
			if (currentLineIndex >= lines.size()) {
				newScreen = new TutorialSelectionScreen(alite);
				performScreenChange();
				postScreenChange();
				return;
			}
			currentLine = lines.get(currentLineIndex);
			currentLine.reset();
			currentLine.play(mediaPlayer);
			textData = computeTextDisplay(game.getGraphics(), currentLine.getText(),
										  currentLine.getX() + 50, 
										  currentLine.getY() + 50, 
										  currentLine.getWidth() - 100, 
										  TEXT_LINE_HEIGHT, 
										  AliteColors.get().mainText(), Assets.regularFont, false);
		} else {
			if (!currentLine.isPlaying(mediaPlayer)) {
				currentLine.executeFinishHook();
				currentLine = null;
			}		
		}	
		
		if (currentLine != null) {
			currentLine.update(deltaTime);
		}
		
		if (currentLine == null || !currentLine.mustRetainEvents()) {
			for (TouchEvent event: game.getInput().getTouchEvents()) {
				if (event.type == TouchEvent.TOUCH_UP && currentLine != null) {
					if (event.x > currentLine.getX() && event.x < (currentLine.getX() + currentLine.getWidth()) && 
						event.y > currentLine.getY() && event.y < (currentLine.getY() + currentLine.getHeight()) && 
						currentLine.isSkippable()) {
						mediaPlayer.reset();
						currentLine.executeFinishHook();
						currentLine = null;
					}
				}
			}
		}
		doUpdate(deltaTime);
	}
	
	@Override
	public void loadAssets() {
		closeIcon = game.getGraphics().newPixmap("no_icon_small.png", true); //close_icon.png", true);
		super.loadAssets();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		alite.getPlayer().setCondition(Condition.DOCKED);
		if (closeIcon != null) {
			closeIcon.dispose();
			closeIcon = null;
		}
		alite.getNavigationBar().setActiveIndex(9); // Disk Screen
	}

	@Override
	public void pause() {
		super.pause();
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}
	}
}
