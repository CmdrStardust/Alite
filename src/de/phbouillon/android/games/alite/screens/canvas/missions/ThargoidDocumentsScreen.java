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
import android.media.MediaPlayer;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.missions.ThargoidDocumentsMission;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.GalaxyScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;
import de.phbouillon.android.games.alite.screens.canvas.TextData;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class ThargoidDocumentsScreen extends AliteScreen {
	private final MediaPlayer mediaPlayer;
	
	private final String attentionCommander = "Attention Commander!";
	
	private final String missionDescription = 
			"The Navy has managed to obtain rare blueprints of a Thargoid " +
	        "battle ship giving details of the drive and weapon systems. " +
	        "These documents need to be taken as soon as possible to the " +
	        "main naval base in orbit around the planet shown here. The " +
	        "enemy is unaware that we have these documents so the journey " +
	        "should proceed without any trouble.";

	private final String accept = "Do you accept this mission?";
	
	private final String fullyServiced = 
			"The documents have been placed in your cargo hold. Your ship " +
			"has been fully serviced and is ready for takeoff. Good luck " +
			"Commander!";
				
	private final String success =
			"We are (again) forever in your debt for bringing these very " +
			"important documents to us. As a reward we have fitted your " +
			"ship with the latest naval energy unit which will recharge " +
			"your energy banks at twice the normal rate. In light of the " +
			"number of 'incidents' you get yourself involved in, you will " +
			"probably find it rather useful. The GIA also wishes to " +
			"apologize for the security leak to the Thargoids. They are " +
			"carrying out intensive investigations and the culprit will " +
			"soon be found.";	

	private MissionLine attCommander;
	private MissionLine missionLine;	
	private MissionLine acceptMission;
	private int lineIndex = 0;
	private TextData [] missionText;
	private Button acceptButton;
	private Button declineButton;
	private Pixmap acceptIcon;
	private Pixmap declineIcon;
	private SystemData targetSystem = null;
	private final ThargoidDocumentsMission mission;
	private final int givenState;
	
	public ThargoidDocumentsScreen(Game game, int state) {
		super(game);
		this.givenState = state;
		mission = ((ThargoidDocumentsMission) MissionManager.getInstance().get(ThargoidDocumentsMission.ID));
		this.mediaPlayer = new MediaPlayer();
		AndroidFileIO fio = (AndroidFileIO) ((Alite) game).getFileIO();
		String path = "sound/mission/2/";
		try {
			attCommander = new MissionLine(fio, path + "01.mp3", attentionCommander);
			if (state == 0) {
				missionLine = new MissionLine(fio, path + "02.mp3", missionDescription);
				targetSystem = mission.findMostDistantSystem();
				mission.setTarget(((Alite) game).getGenerator().getCurrentSeed(), targetSystem.getIndex(), state);
				acceptMission = new MissionLine(fio, path + "03.mp3", accept);
			} else if (state == 1) {
				missionLine = new MissionLine(fio, path + "04.mp3", fullyServiced);								
			} else if (state == 2) {
				missionLine = new MissionLine(fio, path + "05.mp3", success);
			 	mission.onMissionComplete();
				Player player = ((Alite) game).getPlayer();
				player.removeActiveMission(mission);
				player.addCompletedMission(mission);
				player.resetIntergalacticJumpCounter();
				player.resetJumpCounter();
			} else {
				AliteLog.e("Unknown State", "Invalid state variable has been passed to ThargoidDocumentScreen: " + state);
			}
		} catch (IOException e) {
			AliteLog.e("Error reading mission", "Could not read mission audio.", e);
		}
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
				newScreen = new ThargoidDocumentsScreen(game, 1);
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
		displayTitle("Mission #2 - Deliver Thargoid Documents");
		
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
		
		if (targetSystem != null) {
			displayStarMap();
		} 
	}

	private Point toScreen(SystemData systemData, int centerX, int centerY, float zoomFactor) {		
		int offsetX = (int) (centerX * zoomFactor * ConstrictorScreen.STRETCH_X) - 400;
		int offsetY = (int) (centerY * zoomFactor * ConstrictorScreen.STRETCH_Y) - 550;
		return new Point(((int) (systemData.getX() * zoomFactor)) * ConstrictorScreen.STRETCH_X + 900 - offsetX,
				         ((int) (systemData.getY() * zoomFactor)) * ConstrictorScreen.STRETCH_Y + 100 - offsetY);
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
	}
	
	@Override
	public void activate() {
		missionText = computeTextDisplay(game.getGraphics(), missionLine.getText(), 50, 300, 800, 40, AliteColors.get().mainText(), Assets.regularFont, false);
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
			alite.setScreen(new ThargoidDocumentsScreen(alite, state));
		} catch (Exception e) {
			AliteLog.e("Thargoid Documents Screen Initialize", "Error in initializer.", e);
			return false;
		}		
		return true;
	}		

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(givenState);
	}

	@Override
	public void loadAssets() {
		acceptIcon = game.getGraphics().newPixmap("yes_icon.png", true);
		declineIcon = game.getGraphics().newPixmap("no_icon.png", true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (mediaPlayer != null) {
			mediaPlayer.reset();
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
	public void pause() {
		super.pause();
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}
	}

	@Override
	public int getScreenCode() {
		return ScreenCodes.THARGOID_DOCUMENTS_SCREEN;
	}		
}
