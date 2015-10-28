package de.phbouillon.android.games.alite.screens.canvas.missions;

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

import android.graphics.Rect;
import android.media.MediaPlayer;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.missions.CougarMission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.TextData;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Cougar;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class CougarScreen extends AliteScreen {
	private final MediaPlayer mediaPlayer;
	
	private final String missionDescription = 
			"Warning to all Traders: Reports have been coming in from " +
			"Traders in this sector of an unknown hostile ship with " +
			"awesome capabilities. Rumours suggest that this ship is " +
			"fitted with a device which causes on-board computer systems " +
			"to malfunction.";
		
	private final float [] lightAmbient  = { 0.5f, 0.5f, 0.7f, 1.0f };
	private final float [] lightDiffuse  = { 0.4f, 0.4f, 0.8f, 1.0f };
	private final float [] lightSpecular = { 0.5f, 0.5f, 1.0f, 1.0f };
	private final float [] lightPosition = { 100.0f, 30.0f, -10.0f, 1.0f };
	
	private final float [] sunLightAmbient  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightDiffuse  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightPosition = {0.0f, 0.0f, 0.0f, 1.0f};	

	private MissionLine missionLine;	
	private int lineIndex = 0;
	private Cougar cougar;
	private TextData [] missionText;
	private long lastChangeTime;
	private float currentDeltaX, targetDeltaX;
	private float currentDeltaY, targetDeltaY;
	private float currentDeltaZ, targetDeltaZ;
	private final CougarMission mission;
	private final int givenState;
	
	public CougarScreen(Game game, int state) {
		super(game);
		this.givenState = state;
		mission = ((CougarMission) MissionManager.getInstance().get(CougarMission.ID));
		this.mediaPlayer = new MediaPlayer();
		AndroidFileIO fio = (AndroidFileIO) ((Alite) game).getFileIO();
		String path = "sound/mission/4/";
		try {
			if (state == 0) {
				missionLine = new MissionLine(fio, path + "01.mp3", missionDescription);
				cougar = new Cougar((Alite) game);
				cougar.setPosition(200, 0, -700.0f);				
				mission.setPlayerAccepts(true);
				mission.setTarget(((Alite) game).getGenerator().getCurrentSeed(), ((Alite) game).getPlayer().getCurrentSystem().getIndex(), 1);
			} else {
				AliteLog.e("Unknown State", "Invalid state variable has been passed to CougarScreen: " + state);
			}
		} catch (IOException e) {
			AliteLog.e("Error reading mission", "Could not read mission audio.", e);
		}
	}	
	
	private final void dance(float deltaTime) {
		if ((System.nanoTime() - lastChangeTime) > 4000000000l) {
			targetDeltaX = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
			targetDeltaY = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
			targetDeltaZ = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
			lastChangeTime = System.nanoTime();
		}
		if (Math.abs(currentDeltaX - targetDeltaX) > 0.0001) {
			currentDeltaX += (targetDeltaX - currentDeltaX) / 8.0f;
		} 
		if (Math.abs(currentDeltaY - targetDeltaY) > 0.0001) {
			currentDeltaY += (targetDeltaY - currentDeltaY) / 8.0f;
		} 
		if (Math.abs(currentDeltaZ - targetDeltaZ) > 0.0001) {
			currentDeltaZ += (targetDeltaZ - currentDeltaZ) / 8.0f;
		}
		cougar.applyDeltaRotation(currentDeltaX, currentDeltaY, currentDeltaZ);
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
		super.update(deltaTime);
		if (lineIndex == 0 && !missionLine.isPlaying()) {
			missionLine.play(mediaPlayer);
			lineIndex++;
		} 
		if (cougar != null) {
			dance(deltaTime);
		}
	}	
	
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());		
		displayTitle("Mission #4 - Destroy the Hostile Ship");
		
		if (missionText != null) {
			displayText(g, missionText);
		}
		
		if (cougar != null) {
			displayShip();
		} else {
			Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
			setUpForDisplay(visibleArea);
		}
	}
	
	public void displayShip() {
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
		float aspectRatio = (float) visibleArea.width() / (float) visibleArea.height();
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glEnable(GLES11.GL_CULL_FACE);				
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glLoadIdentity();
		GlUtils.gluPerspective(game, 45.0f, aspectRatio, 1.0f, 100000.0f);
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
		GLES11.glMultMatrixf(cougar.getMatrix(), 0);
		cougar.render();
		GLES11.glPopMatrix();
		
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);		
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		setUpForDisplay(visibleArea);
	}

	@Override
	public void activate() {
		initGl();
		missionText = computeTextDisplay(game.getGraphics(), missionLine.getText(), 50, 200, 800, 40, AliteColors.get().mainText(), Assets.regularFont, false);
		targetDeltaX = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		targetDeltaY = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		targetDeltaZ = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		lastChangeTime = System.nanoTime();
	}
		
	public static boolean initialize(Alite alite, DataInputStream dis) {
		try {
			int state = dis.readInt();
			alite.setScreen(new CougarScreen(alite, state));
		} catch (Exception e) {
			AliteLog.e("Cougar Screen Initialize", "Error in initializer.", e);
			return false;
		}		
		return true;
	}		

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(givenState);
	}

	@Override
	public void pause() {
		super.pause();
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}
		if (cougar != null) {
			cougar.dispose();
			cougar = null;
		}
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.COUGAR_SCREEN;
	}		
}
