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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.graphics.Rect;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Music;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.SkySphereSpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.SpriteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.AIState;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Adder;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Anaconda;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.AspMkII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.BoaClassCruiser;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Boomslang;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Bushmaster;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkI;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkIII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Constrictor;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Coral;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Cottonmouth;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Cougar;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Dugite;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.EscapeCapsule;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.FerDeLance;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Gecko;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Gopher;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Harlequin;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Hognose2;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Indigo;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Krait;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Lora;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Lyre;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Mamba;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Missile;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.MorayStarBoat;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Mussurana;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.OrbitShuttle;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Python;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Rattlesnake;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Sidewinder;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargoid;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Thargon;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.TieFighter;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Transporter;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Viper;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.WolfMkII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Yellowbelly;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class ShipIntroScreen extends AliteScreen {
  private static final boolean ON_SIMULATOR = true;
	private static final boolean DEBUG_EXHAUST = false;		
	private static final boolean ONLY_CHANGE_SHIPS_AFTER_SWEEP = false;
	
	private final float [] lightAmbient  = { 0.5f, 0.5f, 0.7f, 1.0f };
	private final float [] lightDiffuse  = { 0.4f, 0.4f, 0.8f, 1.0f };
	private final float [] lightSpecular = { 0.5f, 0.5f, 1.0f, 1.0f };
	private final float [] lightPosition = { 100.0f, 30.0f, -10.0f, 1.0f };
	
	private final float [] sunLightAmbient  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightDiffuse  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightPosition = {0.0f, 0.0f, 0.0f, 1.0f};	
	
	private SkySphereSpaceObject skysphere;
	private AliteObject currentShip;
	
	private static final float START_Z    = -40000.0f;
	private static final float LOGO_X     = -960.0f;
	private static final float LOGO_Y     = -440.0f;
	private static final float LOGO_Z_FAR = -1400.0f;

	private long startTime;
	private long lastChangeTime;
	private int currentShipIndex = 0;
	private float currentDeltaX, targetDeltaX;
	private float currentDeltaY, targetDeltaY;
	private float currentDeltaZ, targetDeltaZ;

	private Button yesButton;
	private Button noButton;
	private Button tapToStartButton;
	private Pixmap yesIcon;
	private Pixmap noIcon;
	private boolean selectPreviousShip = false;

	enum DisplayMode {
		ZOOM_IN,
		DANCE,
		ZOOM_OUT;
	}

	private DisplayMode displayMode = DisplayMode.ZOOM_IN;
	private Music theChase;
	private boolean showLoadNewCommander;
	
	public ShipIntroScreen(Game game) {
		super(game);
		File [] commanders = null;
		showLoadNewCommander = true;
		try {
			commanders = game.getFileIO().getFiles("commanders", "(.*)\\.cmdr");
		} catch (IOException e) {
		}
		if (commanders == null || commanders.length == 0) {
			showLoadNewCommander = false;
		} else if (commanders != null && commanders.length == 1) {
			showLoadNewCommander = !"__autosave.cmdr".equals(commanders[0].getName());			
		} else if (commanders != null && commanders.length > 1) {
			showLoadNewCommander = true;
		}
		
	}	
		
	private void initGl() {
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
		int windowWidth = visibleArea.width();
		int windowHeight = visibleArea.height();

		float ratio = (float) windowWidth / (float) windowHeight;
		GlUtils.setViewport(visibleArea);
		GLES11.glDisable(GLES11.GL_FOG);
		GLES11.glPointSize(1.0f);
    GLES11.glLineWidth(1.0f);

    GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE_MINUS_SRC_ALPHA);
    GLES11.glDisable(GLES11.GL_BLEND);
        
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();		
		GlUtils.gluPerspective(game, 45.0f, ratio, 1.0f, 900000.0f);
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();

		GLES11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES11.glShadeModel(GLES11.GL_SMOOTH);
		
		GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_AMBIENT, lightAmbient, 0);
		GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_DIFFUSE, lightDiffuse, 0);
		GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_SPECULAR, lightSpecular, 0);
		GLES11.glLightfv(GLES11.GL_LIGHT1, GLES11.GL_POSITION, lightPosition, 0);
		GLES11.glEnable(GLES11.GL_LIGHT1);

		GLES11.glLightfv(GLES11.GL_LIGHT2, GLES11.GL_AMBIENT, sunLightAmbient, 0);
		GLES11.glLightfv(GLES11.GL_LIGHT2, GLES11.GL_DIFFUSE, sunLightDiffuse, 0);
		GLES11.glLightfv(GLES11.GL_LIGHT2, GLES11.GL_SPECULAR, sunLightSpecular, 0);
		GLES11.glLightfv(GLES11.GL_LIGHT2, GLES11.GL_POSITION, sunLightPosition, 0);
		GLES11.glEnable(GLES11.GL_LIGHT2);

		GLES11.glEnable(GLES11.GL_LIGHTING);
		
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT);
		GLES11.glHint(GLES11.GL_PERSPECTIVE_CORRECTION_HINT, GLES11.GL_NICEST);
		GLES11.glHint(GLES11.GL_POLYGON_SMOOTH_HINT, GLES11.GL_NICEST);
		GLES11.glEnable(GLES11.GL_CULL_FACE);	
	}

	@Override
	public void update(float deltaTime) {
		updateWithoutNavigation(deltaTime);
		updateAxes();	
		if (currentShip != null && !(currentShip instanceof SpriteObject)) {
			currentShip.applyDeltaRotation(currentDeltaX, currentDeltaY, currentDeltaZ);
			((SpaceObject) currentShip).update(deltaTime);
		}
		switch (displayMode) {
			case ZOOM_IN:  zoomIn(deltaTime);  break;
			case DANCE:    dance(deltaTime);   break;
			case ZOOM_OUT: zoomOut(deltaTime); break;
		}
	}
		
	@Override
	protected void processTouch(TouchEvent touch) {				
		super.processTouch(touch);
		if (touch.type == TouchEvent.TOUCH_SWEEP) {
			if (displayMode == DisplayMode.DANCE) {
				displayMode = DisplayMode.ZOOM_OUT;
			}
			if (touch.x2 > 0) {
			  AliteLog.d("TouchSweep", touch.x + ", " + touch.x2 + ", " + touch.y + ", " + touch.y2);
				selectPreviousShip = true;
			}
		}	
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (yesButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				newScreen = new LoadScreen(game, "Load Commander");
				((Alite) game).getNavigationBar().moveToScreen(newScreen);
			}
			if (noButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);				
				newScreen = new StatusScreen(game);
			}
			if (tapToStartButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);				
				newScreen = new StatusScreen(game);				
			}
		}
		if (newScreen != null) {
			if (theChase != null) {
				theChase.stop();
				theChase.dispose();
				theChase = null;
			}			
		}
	}
	
	@Override
	public void renderNavigationBar() {
	}

	private void debugExhausts() {
		if (DEBUG_EXHAUST) {
			if (currentShip instanceof SpaceObject) {
				SpaceObject so = (SpaceObject) currentShip;
				boolean ok = false;
				if (so.getNumberOfLasers() < 2) {
					ok = true;
				} else {
					ok = so.getLaserX(0) > so.getLaserX(1);
				}
				centerTextWide(ok ? "Ok!" : "NOT OK!", 150, Assets.titleFont,
							   ok ? AliteColors.get().conditionGreen() : AliteColors.get().conditionRed());
			}
		}		
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		final Graphics g = game.getGraphics();
		g.clear(0);
		displayShip();
		if (showLoadNewCommander) {
			g.drawText("Load New Commander?", 400, 1010, AliteColors.get().mainText(), Assets.titleFont);
		} 
		if (!(currentShip instanceof SpriteObject)) {
			centerTextWide(currentShip.getName(), 80, Assets.titleFont, AliteColors.get().shipTitle());
		}
		g.drawText("Alite is inspired by classic Elite", 1450, 1020, AliteColors.get().mainText(), Assets.smallFont);
		g.drawText("© Acornsoft, Bell & Braben", 1450, 1050, AliteColors.get().mainText(), Assets.smallFont);
		debugExhausts();
		((Alite) game).getTextureManager().setTexture(null);
		yesButton.render(g);
		noButton.render(g);
		tapToStartButton.render(g);
	}
	
	private void initDisplay(final Rect visibleArea) {		
		float aspectRatio = (float) visibleArea.width() / (float) visibleArea.height();
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glEnable(GLES11.GL_CULL_FACE);				
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		GlUtils.gluPerspective(game, 45.0f, aspectRatio, 1.0f, 900000.0f);
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);		
		GLES11.glLoadIdentity();
		
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glPushMatrix();	
		  GLES11.glMultMatrixf(skysphere.getMatrix(), 0);
	      skysphere.render();
		GLES11.glPopMatrix();	

		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glClear(GLES11.GL_DEPTH_BUFFER_BIT);

		if (currentShip instanceof SpriteObject) {			
			GLES11.glEnable(GLES11.GL_BLEND);
		    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
			GLES11.glDisable(GLES11.GL_CULL_FACE);
		} else {
			GLES11.glDisable(GLES11.GL_BLEND);
		}
	}
	
	private void endDisplay(final Rect visibleArea) {
		if (currentShip instanceof SpriteObject) {
			GLES11.glDisable(GLES11.GL_BLEND);
			GLES11.glEnable(GLES11.GL_CULL_FACE);
		}
			
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);		
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
				
		setUpForDisplay(visibleArea);		
	}
		
	public void displayShip() {
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();

		initDisplay(visibleArea);

		GLES11.glPushMatrix();
		  GLES11.glMultMatrixf(currentShip.getMatrix(), 0);		  
		  ((Geometry) currentShip).render();
		GLES11.glPopMatrix();

		endDisplay(visibleArea);
	}

	@Override
	public void activate() {
		initGl();	
		skysphere = new SkySphereSpaceObject((Alite) game, "skysphere", 8000.0f, 16, 16, "textures/star_map.png");		
		AliteObject ao = getShipForCurrentIndex();		
		if (ao instanceof SpriteObject) {
			ao.setPosition(LOGO_X, LOGO_Y, LOGO_Z_FAR);
		} else {
			ao.setPosition(0.0f, 0.0f, -(((SpaceObject) ao).getMaxExtentWithoutExhaust()) * 2.0f);	
		}		
		
		targetDeltaX = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		targetDeltaY = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		targetDeltaZ = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		currentShip = ao;
		startTime = System.nanoTime();
		displayMode = DisplayMode.DANCE;
		if (ao instanceof SpaceObject) {
			((SpaceObject) ao).setAIState(AIState.IDLE, (Object []) null);
			ao.setSpeed(-((SpaceObject)ao).getMaxSpeed());
		}		
		yesButton = new Button(1100, 940, 100, 100, yesIcon);
		yesButton.setGradient(true);
		yesButton.setVisible(showLoadNewCommander);
		noButton = new Button(1250, 940, 100, 100, noIcon);
		noButton.setGradient(true);			
		noButton.setVisible(showLoadNewCommander);
		tapToStartButton = new Button(560, 960, 800, 100, "Tap to start", Assets.regularFont, null);
		tapToStartButton.setTextColor(AliteColors.get().mainText());
		tapToStartButton.setGradient(true);
		tapToStartButton.setVisible(!showLoadNewCommander);		
	}
	

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
	}

	@Override
	public void loadAssets() {
		theChase = game.getAudio().newMusic("music/the_chase.mp3", false, false);
		yesIcon = game.getGraphics().newPixmap("yes_icon_small.png", true);
		noIcon = game.getGraphics().newPixmap("no_icon_small.png", true);
	}
	
	@Override
	public void pause() {
		super.pause();
		if (theChase != null) {			
			theChase.pause();
			theChase = null;
		}		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(4);
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeInt(currentShipIndex);
			dos.close();			
			((Alite) game).getFileUtils().saveState(game.getFileIO(), ScreenCodes.SHIP_INTRO_SCREEN, bos.toByteArray());
		} catch (IOException e) {
			AliteLog.e("Error writing state file.", "Error writing state file.", e);
		}
	}
	
	@Override
	public void resume() {
		super.resume();
		if (theChase != null) {
			theChase.setLooping(true);
			theChase.play();
		}
		initGl();
		((Alite) game).getTextureManager().reloadAllTextures();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (theChase != null) {
			theChase.stop();
			theChase.dispose();
			theChase = null;
		}
		if (yesIcon != null) {
			yesIcon.dispose();
			yesIcon = null;
		}
		if (noIcon != null) {
			noIcon.dispose();
			noIcon = null;
		}
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.SHIP_INTRO_SCREEN;
	}	
	
	private final void zoomIn(float deltaTime) {
		if (currentShip == null) {
			return;
		}
		Vector3f pos = currentShip.getPosition();
		float newZ = pos.z + deltaTime * 30000.0f;
		AliteObject ao = currentShip;
		boolean end = false;
		if (ao instanceof SpaceObject)  {
			if (newZ >= -(((SpaceObject) ao).getMaxExtentWithoutExhaust()) * 2.0f) {
				newZ = -(((SpaceObject) ao).getMaxExtentWithoutExhaust()) * 2.0f;
				end = true;
			}
		} else {
			if (newZ >= -1400.0f) {
				newZ = -1400.0f;
				end = true;
			}
		}
		if (end) {
			displayMode = DisplayMode.DANCE;			
			startTime = System.nanoTime();
			lastChangeTime = startTime;			
		}
		if (currentShip instanceof SpriteObject) {
			currentShip.setPosition(LOGO_X, LOGO_Y, newZ);
		} else {
			currentShip.setPosition(0, 0, newZ);
		}
	}
	
	private final void dance(float deltaTime) {
		if ((System.nanoTime() - lastChangeTime) > 4000000000l) {
			targetDeltaX = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
			targetDeltaY = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
			targetDeltaZ = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
			lastChangeTime = System.nanoTime();
		}		                                      
		if ((System.nanoTime() - startTime) > 15000000000l && !ONLY_CHANGE_SHIPS_AFTER_SWEEP) {			                                  
			displayMode = DisplayMode.ZOOM_OUT;
			startTime = System.nanoTime();
		}
	}
	
	private final void zoomOut(float deltaTime) {
		if (currentShip == null) {
			return;
		}
		Vector3f pos = currentShip.getPosition();
		float newZ = pos.z - deltaTime * 30000.0f;
		float threshold = currentShip instanceof SpriteObject ? 2.0f * START_Z : START_Z;
		if (newZ <= threshold) {				
			if (currentShip instanceof SpaceObject) {
				((SpaceObject) currentShip).dispose();
			}
			currentShip = getNextShip();
			newZ = currentShip instanceof SpaceObject ? START_Z : 2.0f * START_Z;
			displayMode = DisplayMode.ZOOM_IN;
			startTime = System.nanoTime();
		}
		if (currentShip instanceof SpriteObject) {
			currentShip.setPosition(LOGO_X, LOGO_Y, newZ);
		} else {
			currentShip.setPosition(0, 0, newZ);
		}
	}
	
	private final void updateAxes() {
		if (Math.abs(currentDeltaX - targetDeltaX) > 0.0001) {
			currentDeltaX += (targetDeltaX - currentDeltaX) / 8.0f;
		} 
		if (Math.abs(currentDeltaY - targetDeltaY) > 0.0001) {
			currentDeltaY += (targetDeltaY - currentDeltaY) / 8.0f;
		} 
		if (Math.abs(currentDeltaZ - targetDeltaZ) > 0.0001) {
			currentDeltaZ += (targetDeltaZ - currentDeltaZ) / 8.0f;
		}
	}
	
	
	private AliteObject getShipForCurrentIndex() {
		Alite alite = (Alite) game;
		switch (currentShipIndex) {
			case  0: if (ON_SIMULATOR) {
						currentShipIndex = 1;		 
						return new CobraMkIII(alite);						
					 } else {
						 return new SpriteObject((Alite) game, 227.0f, 980.0f, 1693.0f, 0.0f, 0.0f, 0.0f, 1.0f, 684.0f / 1024.0f, "title_logo_small.png"); 
					 }
			case  1: return new CobraMkIII(alite);
			case  2: return new Krait(alite);
			case  3: return new Thargoid(alite);
			case  4: return new BoaClassCruiser(alite);
			case  5: return new Gecko(alite);
			case  6: return new MorayStarBoat(alite);
			case  7: return new Adder(alite);
			case  8: return new Mamba(alite);
			case  9: return new Viper(alite);
			case 10: return new FerDeLance(alite);
			case 11: return new CobraMkI(alite);
			case 12: return new Python(alite);
			case 13: return new Anaconda(alite);
			case 14: return new AspMkII(alite);
			case 15: return new Sidewinder(alite);
			case 16: return new WolfMkII(alite);
			case 17: return new OrbitShuttle(alite);
			case 18: return new Transporter(alite);					
			case 19: return new Boomslang(alite);
			case 20: return new Constrictor(alite);
			case 21: return new Cottonmouth(alite);
			case 22: return new Cougar(alite);
			case 23: return new EscapeCapsule(alite);			
			case 24: return new Hognose2(alite);
			case 25: return new Lora(alite);
			case 26: return new Missile(alite);
			case 27: return new Gopher(alite);
			case 28: return new Coral(alite);
			case 29: return new Bushmaster(alite);
			case 30: return new Rattlesnake(alite);
			case 31: return new Mussurana(alite);
			case 32: return new Dugite(alite);
			case 33: return new Yellowbelly(alite);
			case 34: return new Indigo(alite);
			case 35: return new Harlequin(alite);
			case 36: return new TieFighter(alite);
			case 37: return new Lyre(alite);
			case 38: return new Thargon(alite);
		}
		return new CobraMkIII(alite);		
	}
	
	private AliteObject getNextShip() {
	  AliteLog.d("SPS", "SPS == " + selectPreviousShip + ", CSI == " + currentShipIndex);
		if (selectPreviousShip) {
			selectPreviousShip = false;
			currentShipIndex--;
			if (currentShipIndex < 0) {
				currentShipIndex = (DEBUG_EXHAUST ? 38 : 18);
			}
		} else {
			currentShipIndex++;
		}
		if (currentShipIndex == (DEBUG_EXHAUST ? 39 : 19)) {
			currentShipIndex = 0;
		}
		AliteObject ao = getShipForCurrentIndex();
		if (ao instanceof SpaceObject) {
			((SpaceObject) ao).setAIState(AIState.IDLE, (Object []) null);
			ao.setSpeed(-((SpaceObject)ao).getMaxSpeed());
		}
		return ao;
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		ShipIntroScreen sis = new ShipIntroScreen(alite);
		try {
			sis.currentShipIndex = dis.readInt();
		} catch (Exception e) {
			AliteLog.e("Ship Intro Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(sis);
		return true;
	}
}
