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

import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.missions.ConstrictorMission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.GalaxyScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;
import de.phbouillon.android.games.alite.screens.canvas.TextData;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Constrictor;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class ConstrictorScreen extends AliteScreen {
	public static final int STRETCH_X = 7;
	public static final int STRETCH_Y = 7;

	private transient MediaPlayer mediaPlayer;
	
	private final String attentionCommander = "Attention Commander!";
	
	private final String missionDescription = 
			"A prototype model of the new top-secret military ship, the " +
			"Constrictor, has been stolen by unknown agents from a Navy " +
			"research base. Due to the capabilities of this ship the " +
			"Galactic Co-operative of Worlds is offering a large reward " + 
			"to anyone who destroys the Constrictor before it falls into " +
			"enemy hands.";

	private final String accept = "Do you accept this mission?";
	
	private final String reportToBase = 
			"For full mission briefing, please report to the space station " +
			"at this system here. Due to the sensitive nature of this " +
			"assignment, it is vital that you keep details of the " +
			"Constrictor to yourself. Good luck Commander!";
			
	private final String intergalacticJump =
			"The Galactic Intelligence Agency (GIA) reports that the " +
	        "Constrictor made an intergalactic jump out of this system a " +
			"short while ago. It is suggested that you make the same jump " +
	        "and report to the nearest space station for further details.";
	
	private final String hyperspaceJump =
			"GIA reports that after causing havoc, the Constrictor made a " +
			"hyperspace jump out of this system. The deep space tracking " +
			"station suggests that its destination is this system here.";
	
	private final String success =
			"The Galactic Co-operative of Worlds is forever in your debt, " +
	        "oh mighty trader! As a small token of our appreciation, please " +
			"accept a reward of 10,000 credits for your trouble. While I " +
	        "have your attention, the Galactic Police would like me to " +
			"remind you about the small matter of several hundred unpaid " +
	        "parking tickets!";
	
	private final float [] lightAmbient  = { 0.5f, 0.5f, 0.7f, 1.0f };
	private final float [] lightDiffuse  = { 0.4f, 0.4f, 0.8f, 1.0f };
	private final float [] lightSpecular = { 0.5f, 0.5f, 1.0f, 1.0f };
	private final float [] lightPosition = { 100.0f, 30.0f, -10.0f, 1.0f };
	
	private final float [] sunLightAmbient  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightDiffuse  = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightSpecular = {1.0f, 1.0f, 1.0f, 1.0f};
	private final float [] sunLightPosition = {0.0f, 0.0f, 0.0f, 1.0f};	

	private MissionLine attCommander;
	private MissionLine missionLine;	
	private MissionLine acceptMission;
	private int lineIndex = 0;
	private Constrictor constrictor;
	private TextData [] missionText;
	private long lastChangeTime;
	private float currentDeltaX, targetDeltaX;
	private float currentDeltaY, targetDeltaY;
	private float currentDeltaZ, targetDeltaZ;
	private Button acceptButton;
	private Button declineButton;
	private Pixmap acceptIcon;
	private Pixmap declineIcon;
	private SystemData targetSystem = null;
	private final ConstrictorMission mission;
	private final int givenState;
	
	public ConstrictorScreen(Game game, int state) {
		super(game);
		this.givenState = state;
		mission = ((ConstrictorMission) MissionManager.getInstance().get(ConstrictorMission.ID));
		this.mediaPlayer = new MediaPlayer();
		AndroidFileIO fio = (AndroidFileIO) ((Alite) game).getFileIO();
		String path = "sound/mission/1/";
		try {
			attCommander = new MissionLine(fio, path + "01.mp3", attentionCommander);
			if (state == 0) {
				missionLine = new MissionLine(fio, path + "02.mp3", missionDescription);
				acceptMission = new MissionLine(fio, path + "03.mp3", accept);
				constrictor = new Constrictor((Alite) game);
				constrictor.setPosition(200, 0, -700.0f);				
			} else if (state == 1) {
				missionLine = new MissionLine(fio, path + "04.mp3", reportToBase);
				targetSystem = mission.findMostDistantSystem();
				mission.setTarget(((Alite) game).getGenerator().getCurrentSeed(), targetSystem.getIndex(), state);
			} else if (state == 2) {
				missionLine = new MissionLine(fio, path + "06.mp3", intergalacticJump);
				mission.setTarget(((Alite) game).getGenerator().getNextSeed(), -1, state);
			} else if (state == 3) {
				missionLine = new MissionLine(fio, path + "05.mp3", hyperspaceJump);
				targetSystem = mission.findRandomSystemInRange(75, 120);
				mission.setTarget(((Alite) game).getGenerator().getCurrentSeed(), targetSystem.getIndex(), mission.getState() + 1);
			} else if (state == 4) {
				missionLine = new MissionLine(fio, path + "07.mp3", success);
				mission.onMissionComplete();
				Player player = ((Alite) game).getPlayer();
				player.removeActiveMission(mission);
				player.addCompletedMission(mission);
				player.resetIntergalacticJumpCounter();
				player.resetJumpCounter();
			} else {
				AliteLog.e("Unknown State", "Invalid state variable has been passed to ConstrictorScreen: " + state);
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
		constrictor.applyDeltaRotation(currentDeltaX, currentDeltaY, currentDeltaZ);
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
		if (acceptButton == null && declineButton == null) {
			super.update(deltaTime);
		} else {
			updateWithoutNavigation(deltaTime);
		}
		if (lineIndex == 0 && !attCommander.isPlaying()) {
			attCommander.play(mediaPlayer);
			lineIndex++;
		} else if (lineIndex == 1 && !attCommander.isPlaying() && !missionLine.isPlaying()) {
			missionLine.play(mediaPlayer);
			lineIndex++;
		} else if (lineIndex == 2 && acceptMission != null && !acceptMission.isPlaying() && !missionLine.isPlaying()) {
			acceptMission.play(mediaPlayer);
			lineIndex++;
		}
		if (constrictor != null) {
			dance(deltaTime);
		}
	}
	
	@Override
	protected void processTouch(TouchEvent touch) {				
		super.processTouch(touch);
		if (acceptButton == null && declineButton == null) {
			return;
		}
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (acceptButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				mission.setPlayerAccepts(true);
				newScreen = new ConstrictorScreen(game, 1);
			}
			if (declineButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				mission.setPlayerAccepts(false);
				newScreen = new StatusScreen(game);
			}
		}
	}
	
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());		
		displayTitle("Mission #1 - Destroy the Constrictor");
		
		g.drawText(attentionCommander, 50, 200, AliteColors.get().informationText(), Assets.regularFont);
		if (missionText != null) {
			displayText(g, missionText);
		}
		if (acceptMission != null) {
			g.drawText(accept, 50, 800, AliteColors.get().informationText(), Assets.regularFont);
			if (acceptButton != null) {
				acceptButton.render(g);
			}
			if (declineButton != null) {
				declineButton.render(g);
			}
		}
		
		if (constrictor != null) {
			displayShip();
		} else if (targetSystem != null) {
			displayStarMap();
		} else {
			Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
			setUpForDisplay(visibleArea);
		}
	}

	private Point toScreen(SystemData systemData, int centerX, int centerY, float zoomFactor) {		
		int offsetX = (int) (centerX * zoomFactor * STRETCH_X) - 400;
		int offsetY = (int) (centerY * zoomFactor * STRETCH_Y) - 550;
		return new Point(((int) (systemData.getX() * zoomFactor)) * STRETCH_X + 900 - offsetX,
				         ((int) (systemData.getY() * zoomFactor)) * STRETCH_Y + 100 - offsetY);
	}

	private void drawSystem(SystemData system, int centerX, int centerY, float zoomFactor, boolean clearBackground) {
		Graphics g = game.getGraphics();
		Point p = toScreen(system, centerX, centerY, zoomFactor);
		if (p.x < 900 || p.x > 1700 || p.y < 100 || p.y > 1000) {
			return;
		}
		g.fillCircle(p.x, p.y, (int) (3 * zoomFactor), getColor(system.getEconomy()), 32);						
		int nameWidth = g.getTextWidth(system.getName(), system == targetSystem ? Assets.regularFont : Assets.smallFont);
		int nameHeight = g.getTextHeight(system.getName(), system == targetSystem ? Assets.regularFont : Assets.smallFont);
		int positionX = (int) (3 * zoomFactor) + 2;
		int positionY = 40;
		if (p.x + nameWidth > (GalaxyScreen.HALF_WIDTH << 1)) {
			positionX = -positionX - nameWidth;
		}
		if (p.y + 40 > (GalaxyScreen.HALF_HEIGHT << 1)) {
			positionY = -40;
		}
		if (clearBackground) {
			g.fillRect(p.x + positionX, p.y + positionY - nameHeight, nameWidth, nameHeight, AliteColors.get().background());
		}
		g.drawText(system.getName(), p.x + positionX, p.y + positionY, getColor(system.getEconomy()), system == targetSystem ? Assets.regularFont : Assets.smallFont);
		if (system == targetSystem) {
			g.drawLine(p.x, p.y - GalaxyScreen.CROSS_SIZE - GalaxyScreen.CROSS_DISTANCE, p.x, p.y - GalaxyScreen.CROSS_DISTANCE, AliteColors.get().baseInformation());
			g.drawLine(p.x, p.y + GalaxyScreen.CROSS_SIZE + GalaxyScreen.CROSS_DISTANCE, p.x, p.y + GalaxyScreen.CROSS_DISTANCE, AliteColors.get().baseInformation());
			g.drawLine(p.x - GalaxyScreen.CROSS_SIZE - GalaxyScreen.CROSS_DISTANCE, p.y, p.x - GalaxyScreen.CROSS_DISTANCE, p.y, AliteColors.get().baseInformation());
			g.drawLine(p.x + GalaxyScreen.CROSS_SIZE + GalaxyScreen.CROSS_DISTANCE, p.y, p.x + GalaxyScreen.CROSS_DISTANCE, p.y, AliteColors.get().baseInformation());				
		}
	}
	
	private void displayStarMap() {
		int centerX = targetSystem.getX();
		int centerY = targetSystem.getY();
		
		for (SystemData system: ((Alite) game).getGenerator().getSystems()) {
			drawSystem(system, centerX, centerY, 3.0f, false);
		}
		// Make sure the target system is rendered on top...
		drawSystem(targetSystem, centerX, centerY, 3.0f, true);
		
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
		setUpForDisplay(visibleArea);
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
		GLES11.glMultMatrixf(constrictor.getMatrix(), 0);
		constrictor.render();
		GLES11.glPopMatrix();
		
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);		
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		setUpForDisplay(visibleArea);
	}

	@Override
	public void activate() {
		initGl();
		missionText = computeTextDisplay(game.getGraphics(), missionLine.getText(), 50, 300, 800, 40, AliteColors.get().mainText(), Assets.regularFont, false);
		targetDeltaX = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		targetDeltaY = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		targetDeltaZ = Math.random() < 0.5 ? (float) Math.random() * 2.0f + 2.0f : -(float) Math.random() * 2.0f - 2.0f;
		lastChangeTime = System.nanoTime();
		if (acceptMission != null) {
			acceptButton = new Button(50, 860, 200, 200, acceptIcon);
			acceptButton.setGradient(true);
			declineButton = new Button(650, 860, 200, 200, declineIcon);
			declineButton.setGradient(true);			
		}
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		try {
			int state = dis.readInt();
			if (state == 3) {
				ConstrictorScreen cs = new ConstrictorScreen(alite, state);
				cs.targetSystem = alite.getGenerator().getSystems()[dis.readInt()];
				// Mission (model) state has been increased in constructor; now reduce it again...
				cs.mission.setTarget(alite.getGenerator().getCurrentSeed(), cs.targetSystem.getIndex(), cs.mission.getState() - 1);
				alite.setScreen(cs);
			} else {
				alite.setScreen(new ConstrictorScreen(alite, state));
			}
		} catch (Exception e) {
			AliteLog.e("Constrictor Screen Initialize", "Error in initializer.", e);
			return false;
		}		
		return true;
	}		

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(givenState);
		if (givenState == 3) {
			dos.writeInt(targetSystem.getIndex());
		}
	}

	@Override
	public void loadAssets() {
		acceptIcon = game.getGraphics().newPixmap("yes_icon.png", true);
		declineIcon = game.getGraphics().newPixmap("no_icon.png", true);
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
		if (constrictor != null) {
			constrictor.dispose();
			constrictor = null;
		}
		if (acceptIcon != null) {
			acceptIcon.dispose();
			acceptIcon = null;
		}
		if (declineIcon != null) {
			declineIcon.dispose();
			declineIcon = null;
		}
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.CONSTRICTOR_SCREEN;
	}	
}
