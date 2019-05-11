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

import android.graphics.Rect;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.Slider;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
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
public class ShipEditorScreen extends AliteScreen {
	public static final int STRETCH_X = 7;
	public static final int STRETCH_Y = 7;
	
	private final float [] lightAmbient  = { 0.5f, 0.5f, 0.7f, 1.0f };
	private final float [] lightDiffuse  = { 0.4f, 0.4f, 0.8f, 1.0f };
	private final float [] lightSpecular = { 0.5f, 0.5f, 1.0f, 1.0f };
	private final float [] lightPosition = { 100.0f, 30.0f, -10.0f, 1.0f };
	
	private final float [] sunLightAmbient  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightDiffuse  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightPosition = {0.0f, 0.0f, 0.0f, 1.0f};	

	private SpaceObject currentShip;
	
	private Button increaseX;
	private Button decreaseX;
	private Button increaseY;
	private Button decreaseY;
	private Button increaseZ;
	private Button decreaseZ;
	private Button increaseRadiusX;
	private Button decreaseRadiusX;
	private Button increaseRadiusY;
	private Button decreaseRadiusY;
	private Button increaseLength;
	private Button decreaseLength;
	private Button decreaseSpeed;
	private Button increaseSpeed;
	private Button toggleExhaustCount;
	private Button nextShip;

	private Slider r1;
	private Slider g1;
	private Slider b1;
	private Slider a1;
	
	private float lastZoom = -1.0f;
	private int numberOfExhausts = 2;
	private final Vector3f temp = new Vector3f(0, 0, 0);
	
	class ExhaustParameters {
		int xOffset;
		int yOffset;
		int zOffset;
		int radiusX;
		int radiusY;
		int maxLength;
		float r1, g1, b1, a1;
		
		public String toString() {
			return "xOffset: " + xOffset + ", yOffset: " + yOffset + ", zOffset: " + zOffset + ", radiusX: " + radiusX + ", radiusY: " + radiusY + ", len: " + maxLength + ", (" + r1 + ", " + g1 + ", " + b1 + ", " + a1 + ")";
		}
	}
	
	private ExhaustParameters exp = new ExhaustParameters();
	
	public ShipEditorScreen(Game game) {
		super(game);
		currentShip = new CobraMkIII((Alite) game);
		currentShip.getExhausts().clear();
		exp.xOffset = 50;
		exp.yOffset = 0;
		exp.zOffset = 0;
		exp.radiusX = 13;
		exp.radiusY = 13;
		exp.maxLength = 300;
		exp.r1 = 0.7f;
		exp.g1 = 0.8f;
		exp.b1 = 0.8f;
		exp.a1 = 0.7f;

		currentShip.addExhaust(new EngineExhaust(currentShip, 13.0f, 13.0f, 30.0f, -50.0f, 0, 0));
		currentShip.addExhaust(new EngineExhaust(currentShip, 13.0f, 13.0f, 30.0f,  50.0f, 0, 0));
		currentShip.setPosition(0, 0, -700.0f);	
		currentShip.setAIState(AIState.IDLE, (Object []) null);
		currentShip.setSpeed(-currentShip.getMaxSpeed());
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
		if (currentShip != null) {			
			currentShip.update(deltaTime);
		}
	}
	
	private void modifyExhaust() {
		for (EngineExhaust ex: currentShip.getExhausts()) {
			ex.getPosition().copy(temp);
			if (temp.x < 0) {
				ex.setPosition(-exp.xOffset, exp.yOffset, currentShip.getBoundingBox()[5] + exp.zOffset);
			} else {
				ex.setPosition(exp.xOffset, exp.yOffset, currentShip.getBoundingBox()[5] + exp.zOffset);
			}
			ex.setRadiusX(exp.radiusX);
			ex.setRadiusY(exp.radiusY);
			ex.setMaxLength(exp.maxLength);
			ex.setColor(exp.r1, exp.g1, exp.b1, exp.a1);
		}
	}
	
	@Override
	protected void processTouch(TouchEvent touch) {				
		super.processTouch(touch);
		if (r1.checkEvent(touch)) {
			exp.r1 = r1.getCurrentValue();
			modifyExhaust();
			return;
		}
		if (g1.checkEvent(touch)) {
			exp.g1 = g1.getCurrentValue();
			modifyExhaust();
			return;
		}
		if (b1.checkEvent(touch)) {
			exp.b1 = b1.getCurrentValue();
			modifyExhaust();
			return;
		}
		if (a1.checkEvent(touch)) {
			exp.a1 = a1.getCurrentValue();
			modifyExhaust();
			return;
		}

		if (touch.type == TouchEvent.TOUCH_SCALE && game.getInput().getTouchCount() > 1) {
			if (lastZoom  < 0) {
				lastZoom = touch.zoomFactor;
			} else {
				if (touch.zoomFactor > lastZoom) {
					currentShip.setPosition(0, 0, currentShip.getPosition().z + 5);
				} else {
					currentShip.setPosition(0, 0, currentShip.getPosition().z - 5);
				}
			}
			return;
		}
		if (game.getInput().getTouchCount() > 1) {
			return;
		}
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (increaseX.isTouched(touch.x, touch.y)) {
				exp.xOffset += 5;
				modifyExhaust();
			}
			if (decreaseX.isTouched(touch.x, touch.y)) {
				exp.xOffset -= 5;
				modifyExhaust();
			}
			if (increaseY.isTouched(touch.x, touch.y)) {
				exp.yOffset += 5;
				modifyExhaust();
			}
			if (decreaseY.isTouched(touch.x, touch.y)) {
				exp.yOffset -= 5;
				modifyExhaust();
			}
			if (increaseZ.isTouched(touch.x, touch.y)) {
				exp.zOffset += 5;
				modifyExhaust();
			}
			if (decreaseZ.isTouched(touch.x, touch.y)) {
				exp.zOffset -= 5;
				modifyExhaust();
			}
			if (increaseRadiusX.isTouched(touch.x, touch.y)) {
				exp.radiusX += 1;
				modifyExhaust();
			}
			if (decreaseRadiusX.isTouched(touch.x, touch.y)) {
				exp.radiusX -= 1;
				modifyExhaust();
			}
			if (increaseRadiusY.isTouched(touch.x, touch.y)) {
				exp.radiusY += 1;
				modifyExhaust();
			}
			if (decreaseRadiusY.isTouched(touch.x, touch.y)) {
				exp.radiusY -= 1;
				modifyExhaust();
			}
			if (increaseLength.isTouched(touch.x, touch.y)) {
				exp.maxLength += 20;
				modifyExhaust();
			}
			if (decreaseLength.isTouched(touch.x, touch.y)) {
				exp.maxLength -= 20;
				modifyExhaust();
			}

			if (increaseSpeed.isTouched(touch.x, touch.y)) {
				float newSpeed = currentShip.getSpeed() - 10.0f;
				if (-newSpeed > currentShip.getMaxSpeed()) {
					newSpeed = -currentShip.getMaxSpeed();
				}
				currentShip.setSpeed(newSpeed);
			}
			if (decreaseSpeed.isTouched(touch.x, touch.y)) {
				float newSpeed = currentShip.getSpeed() + 10.0f;
				if (newSpeed > 0) {
					newSpeed = 0;
				}
				currentShip.setSpeed(newSpeed);
			}
			if (toggleExhaustCount.isTouched(touch.x, touch.y)) {
				numberOfExhausts = -numberOfExhausts + 3;
				currentShip.getExhausts().clear();
				for (int i = 0; i < numberOfExhausts; i++) {
					currentShip.addExhaust(new EngineExhaust(currentShip, exp.radiusX, exp.radiusY, exp.maxLength, i == 0 ? -exp.xOffset : exp.xOffset, exp.yOffset, exp.zOffset));
					currentShip.getExhausts().get(i).setColor(exp.r1, exp.g1, exp.b1, exp.a1);
				}
			}
			if (nextShip.isTouched(touch.x, touch.y)) {
				AliteLog.e("Exhaust Configuration", currentShip.getName() + ": Number of exhausts: " + numberOfExhausts + " Params: " + exp);
				exp.xOffset = 50;
				exp.yOffset = 0;
				exp.zOffset = 0;
				exp.radiusX = 13;
				exp.radiusY = 13;
				exp.maxLength = 300;
				exp.r1 = 0.7f;
				exp.g1 = 0.8f;
				exp.b1 = 0.8f;
				exp.a1 = 0.7f;
				numberOfExhausts = 2;
				getNextShip();
				currentShip.getExhausts().clear();
				currentShip.setSpeed(-currentShip.getMaxSpeed());
				currentShip.setPosition(0, 0, -700.0f);
				for (int i = 0; i < numberOfExhausts; i++) {
					currentShip.addExhaust(new EngineExhaust(currentShip, exp.radiusX, exp.radiusY, exp.maxLength, i == 0 ? -exp.xOffset : exp.xOffset, exp.yOffset, exp.zOffset));
					currentShip.getExhausts().get(i).setColor(exp.r1, exp.g1, exp.b1, exp.a1);
				}				
			}
		}

		if (touch.y > 800 || touch.x > 800) {
			return;
		}

		if (touch.type == TouchEvent.TOUCH_DRAGGED) {
			if (lastX != -1 && lastY != -1) {
				int diffX = touch.x - lastX;
				int diffY = touch.y - lastY;
				int ady = Math.abs(diffY);
				int adx = Math.abs(diffX);
				if (adx > ady) {
					currentShip.applyDeltaRotation(0, diffX, 0);
				} else {
					currentShip.applyDeltaRotation(diffY, 0, 0);
				}
			}
			lastX = touch.x;
			lastY = touch.y;
		}
		if (touch.type == TouchEvent.TOUCH_DOWN) {
			lastX = touch.x;
			lastY = touch.y;
		}
	}
	
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());		
		displayWideTitle("Ship Configuration Screen");
		
		increaseX.render(g);
		decreaseX.render(g);
		increaseY.render(g);
		decreaseY.render(g);
		increaseZ.render(g);
		decreaseZ.render(g);
		increaseRadiusX.render(g);
		decreaseRadiusX.render(g);
		increaseRadiusY.render(g);
		decreaseRadiusY.render(g);
		increaseLength.render(g);
		decreaseLength.render(g);
		increaseSpeed.render(g);
		decreaseSpeed.render(g);
		toggleExhaustCount.render(g);
		nextShip.render(g);
		
		if (currentShip != null) {
			g.drawText(currentShip.getName(), 20, 150, AliteColors.get().informationText(), Assets.regularFont);
		}
		
		r1.render(g);
		g1.render(g);
		b1.render(g);
		a1.render(g);
		
		if (currentShip != null) {
			displayShip();
		}
	}
	
	public void displayShip() {
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
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

		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		GLES11.glDepthFunc(GLES11.GL_LESS);
		GLES11.glClear(GLES11.GL_DEPTH_BUFFER_BIT);
		GLES11.glPushMatrix();
		GLES11.glMultMatrixf(currentShip.getMatrix(), 0);
		currentShip.render();		
		GLES11.glPopMatrix();
		
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);		
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		setUpForDisplay(visibleArea);
	}

	@Override
	public void activate() {
		initGl();
		decreaseX = new Button(0, 900, 150, 80, "X-", Assets.smallFont, null);
		decreaseX.setGradient(true);			
		increaseX = new Button(0, 1000, 150, 80, "X+", Assets.smallFont, null);
		increaseX.setGradient(true);
		decreaseY = new Button(200, 900, 150, 80, "Y-", Assets.smallFont, null);
		decreaseY.setGradient(true);			
		increaseY = new Button(200, 1000, 150, 80, "Y+", Assets.smallFont, null);
		increaseY.setGradient(true);
		decreaseZ = new Button(400, 900, 150, 80, "Z-", Assets.smallFont, null);
		decreaseZ.setGradient(true);			
		increaseZ = new Button(400, 1000, 150, 80, "Z+", Assets.smallFont, null);
		increaseZ.setGradient(true);
		decreaseRadiusX = new Button(600, 900, 150, 80, "Rx-", Assets.smallFont, null);
		decreaseRadiusX.setGradient(true);			
		increaseRadiusX = new Button(600, 1000, 150, 80, "Rx+", Assets.smallFont, null);
		increaseRadiusX.setGradient(true);
		decreaseRadiusY = new Button(800, 900, 150, 80, "Ry-", Assets.smallFont, null);
		decreaseRadiusY.setGradient(true);			
		increaseRadiusY = new Button(800, 1000, 150, 80, "Ry+", Assets.smallFont, null);
		increaseRadiusY.setGradient(true);
		decreaseLength = new Button(1000, 900, 150, 80, "L-", Assets.smallFont, null);
		decreaseLength.setGradient(true);			
		increaseLength = new Button(1000, 1000, 150, 80, "L+", Assets.smallFont, null);
		increaseLength.setGradient(true);
		decreaseSpeed = new Button(1200, 900, 150, 80, "S-", Assets.smallFont, null);
		decreaseSpeed.setGradient(true);			
		increaseSpeed = new Button(1200, 1000, 150, 80, "S+", Assets.smallFont, null);
		increaseSpeed.setGradient(true);
		toggleExhaustCount = new Button(1400, 900, 150, 80, "TC", Assets.smallFont, null);
		toggleExhaustCount.setGradient(true);
		nextShip = new Button(1400, 1000, 150, 80, "Next", Assets.smallFont, null);
		nextShip.setGradient(true);
		
		r1 = new Slider(1100, 150, 720, 100, 0.2f, 1.0f, 0.7f, "r1", Assets.smallFont);
		g1 = new Slider(1100, 350, 720, 100, 0, 1, 0.8f, "g1", Assets.smallFont);
		b1 = new Slider(1100, 550, 720, 100, 0, 0.8f, 0.8f, "b1", Assets.smallFont);
		a1 = new Slider(1100, 750, 720, 100, 0, 0.7f, 0.7f, "a1", Assets.smallFont);		
	}
	

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
	}

	@Override
	public void loadAssets() {
	}
			
	@Override
	public int getScreenCode() {
		return 0;
	}	
	
	public void getNextShip() {
		Alite a = (Alite) game;
		if (currentShip instanceof Adder) currentShip = new Anaconda(a);
		else if (currentShip instanceof Anaconda) currentShip = new AspMkII(a);
		else if (currentShip instanceof AspMkII) currentShip = new BoaClassCruiser(a);
		else if (currentShip instanceof BoaClassCruiser) currentShip = new Boomslang(a);
		else if (currentShip instanceof Boomslang) currentShip = new CobraMkI(a);
		else if (currentShip instanceof CobraMkI) currentShip = new CobraMkIII(a);
		else if (currentShip instanceof CobraMkIII) currentShip = new Constrictor(a);
		else if (currentShip instanceof Constrictor) currentShip = new Cottonmouth(a);
		else if (currentShip instanceof Cottonmouth) currentShip = new Cougar(a);
		else if (currentShip instanceof Cougar) currentShip = new EscapeCapsule(a);
		else if (currentShip instanceof EscapeCapsule) currentShip = new FerDeLance(a);
		else if (currentShip instanceof FerDeLance) currentShip = new Gecko(a);
		else if (currentShip instanceof Gecko) currentShip = new Hognose2(a);
		else if (currentShip instanceof Hognose2) currentShip = new Krait(a);
		else if (currentShip instanceof Krait) currentShip = new Lora(a);
		else if (currentShip instanceof Lora) currentShip = new Mamba(a);
		else if (currentShip instanceof Mamba) currentShip = new Missile(a);
		else if (currentShip instanceof Missile) currentShip = new MorayStarBoat(a);
		else if (currentShip instanceof MorayStarBoat) currentShip = new OrbitShuttle(a);
		else if (currentShip instanceof OrbitShuttle) currentShip = new Python(a);
		else if (currentShip instanceof Python) currentShip = new Sidewinder(a);
		else if (currentShip instanceof Sidewinder) currentShip = new Thargoid(a);
		else if (currentShip instanceof Thargoid) currentShip = new Thargon(a);
		else if (currentShip instanceof Thargon) currentShip = new Transporter(a);
		else if (currentShip instanceof Transporter) currentShip = new Viper(a);
		else if (currentShip instanceof Viper) currentShip = new WolfMkII(a);
		else if (currentShip instanceof WolfMkII) currentShip = new Gopher(a); 
		else if (currentShip instanceof Gopher) currentShip = new Coral(a);  
		else if (currentShip instanceof Coral) currentShip = new Bushmaster(a); 
		else if (currentShip instanceof Bushmaster) currentShip = new Rattlesnake(a); 
		else if (currentShip instanceof Rattlesnake) currentShip = new Mussurana(a); 
		else if (currentShip instanceof Mussurana) currentShip = new Dugite(a); 
		else if (currentShip instanceof Dugite) currentShip = new Yellowbelly(a); 
		else if (currentShip instanceof Yellowbelly) currentShip = new Indigo(a); 
		else if (currentShip instanceof Indigo) currentShip = new Harlequin(a);
		else if (currentShip instanceof Harlequin) currentShip = new TieFighter(a); 
		else if (currentShip instanceof TieFighter) currentShip = new Lyre(a);
		else if (currentShip instanceof Lyre) currentShip = new Adder(a);
	}
	
	@Override
	public void renderNavigationBar() {
	}
}
