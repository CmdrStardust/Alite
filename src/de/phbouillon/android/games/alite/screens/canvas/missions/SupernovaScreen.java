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
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.missions.SupernovaMission;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;
import de.phbouillon.android.games.alite.screens.canvas.TextData;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class SupernovaScreen extends AliteScreen {
	private final MediaPlayer mediaPlayer;
	
	private final String supernovaAnnouncement = 
			"Warning - the sun in this system is going supernova. You are " +
			"advised to leave as soon as possible.";
	
	private final String missionDescription = 
			"Please help us! Everyone else has fled the supernova but we " +
			"were left behind when our shuttle was damaged by an asteroid. " +
			"We managed to limp back to the space station but face certain " +
			"death if we don't leave soon. There are only a few of us. We " +
			"could fit in your cargo hold.";

	private final String accept = "Will you help us?";
	
	private final String dropUsOff = 
			"Thank you, you will be well rewarded by our government. I'm " +
			"afraid that we've had to dump any cargo you had to get us all " +
			"in. If you could just drop us off at the nearest star system " +
			"that'll be fine.";
			
	private final String success =
			"Thank you for carrying us to safety. Please accept a selection " +
			"of precious Zanxian gem stones as a token of our appreciation.";
	
	private final String missionDecline = 
			"I hope you die a horribly long and lingering death at the " +
			"hands of a slimy green lobstoid, that's if you're now blown " +
			"to bits by our sun!";
	
	private MissionLine announcement;
	private MissionLine missionLine;	
	private MissionLine acceptMission;
	private int lineIndex = 0;
	private TextData [] missionText;
	private TextData [] announcementText;
	private Button acceptButton;
	private Button declineButton;
	private Pixmap acceptIcon;
	private Pixmap declineIcon;
	private final SupernovaMission mission;
	private final int givenState;
	
	public SupernovaScreen(Game game, int state) {
		super(game);
		this.givenState = state;
		mission = ((SupernovaMission) MissionManager.getInstance().get(SupernovaMission.ID));
		this.mediaPlayer = new MediaPlayer();
		AndroidFileIO fio = (AndroidFileIO) ((Alite) game).getFileIO();
		String path = "sound/mission/3/";
		try {			
			if (state == 0) {
				announcement = new MissionLine(fio, path + "01.mp3", supernovaAnnouncement);
				missionLine = new MissionLine(fio, path + "02.mp3", missionDescription);
				acceptMission = new MissionLine(fio, path + "06.mp3", accept);
			} else if (state == 1) {
				missionLine = new MissionLine(fio, path + "05.mp3", missionDecline);
				mission.setSupernovaSystem(((Alite) game).getGenerator().getCurrentSeed(), ((Alite) game).getPlayer().getCurrentSystem().getIndex());
			} else if (state == 2) {
				missionLine = new MissionLine(fio, path + "03.mp3", dropUsOff);
				mission.setSupernovaSystem(((Alite) game).getGenerator().getCurrentSeed(), ((Alite) game).getPlayer().getCurrentSystem().getIndex());
			} else if (state == 3) {
				missionLine = new MissionLine(fio, path + "04.mp3", success);
				mission.onMissionComplete();
				Player player = ((Alite) game).getPlayer();
				player.removeActiveMission(mission);
				player.addCompletedMission(mission);
				player.resetIntergalacticJumpCounter();
				player.resetJumpCounter();
			} else {
				AliteLog.e("Unknown State", "Invalid state variable has been passed to SupernovaScreen: " + state);
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
		if (lineIndex == 0) {
			if (announcement != null && !announcement.isPlaying()) {
				announcement.play(mediaPlayer);
				lineIndex++;
			} else if (announcement == null && !missionLine.isPlaying()) {
				missionLine.play(mediaPlayer);
				lineIndex += 2;
			}
		} else if (lineIndex == 1 && (announcement != null && !announcement.isPlaying()) && !missionLine.isPlaying()) {
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
				mission.setState(1);
				mission.setPlayerAccepts(true);				
				newScreen = new SupernovaScreen(game, 2);
			}
			if (declineButton.isTouched(touch.x, touch.y)) {
				SoundManager.play(Assets.click);
				// The player cannot opt to evade the super nova by not accepting
				// the mission, hence we "accept" it here, so that the mission
				// is in the list of active missions. We use a different state,
				// though.
				mission.setState(2);
				mission.setPlayerAccepts(true);				
				newScreen = new SupernovaScreen(game, 1);
			}
		}
	}
	
	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayTitle("Mission #3 - Supernova");
		
		if (announcementText != null) {
			displayText(g, announcementText);
		}
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
	}
	
	@Override
	public void activate() {
		if (announcement != null) {
			announcementText = computeTextDisplay(game.getGraphics(), announcement.getText(), 50, 200, 800, 40, AliteColors.get().informationText(), Assets.regularFont, false);
		}
		missionText = computeTextDisplay(game.getGraphics(), missionLine.getText(), 50, announcement == null ? 200 : 400, 800, 40, AliteColors.get().mainText(), Assets.regularFont, false);
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
			alite.setScreen(new SupernovaScreen(alite, state));
		} catch (Exception e) {
			AliteLog.e("Supernova Screen Initialize", "Error in initializer.", e);
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
		return ScreenCodes.SUPERNOVA_SCREEN;
	}		
}
