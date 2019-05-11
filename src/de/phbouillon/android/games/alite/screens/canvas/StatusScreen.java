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

import java.io.DataInputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.net.Uri;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.screens.canvas.options.ControlOptionsScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class StatusScreen extends AliteScreen {	
	private static final int SHIP_X                 = 360;
	private static final int SHIP_Y                 = 400;
	
	private static volatile Pixmap cobra;
	private AliteScreen forwardingScreen = null;
	private boolean askForTutorial = false;
	private boolean requireAnswer = false;
	private boolean pendingShowControlOptions = false;
	
	enum Direction {
		DIR_LEFT, DIR_UP, DIR_RIGHT, DIR_DOWN
	}
	
	public StatusScreen(Game game) {
		super(game);		
	}
		
	@Override
	public void activate() {
		setUpForDisplay(((AndroidGraphics) game.getGraphics()).getVisibleArea());
		((Alite) game).getNavigationBar().setActiveIndex(2);
		for (Mission m: MissionManager.getInstance().getMissions()) {
			if (m.missionStarts()) {
				forwardingScreen = m.getMissionScreen();
				break;
			} else if (m.isActive()) {
				AliteScreen t = m.checkForUpdate();
				if (t != null) {
					forwardingScreen = t;
					break;
				}
			}
		}
		if (!Settings.hasBeenPlayedBefore) {
			Player player = ((Alite) game).getPlayer();
			if (((Alite) game).getGenerator().getCurrentGalaxyFromSeed() == 1) {
				if (player.getCurrentSystem() != null && player.getCurrentSystem().getIndex() == 7) {
					askForTutorial = true;
					requireAnswer = true;
					Settings.hasBeenPlayedBefore = true;
					Settings.save(game.getFileIO());
				}
			}
		}
	}

	private void drawInformation(final Graphics g) {
		Player player = ((Alite) game).getPlayer();
		
		g.drawText("Present System:",    40, 150, AliteColors.get().informationText(), Assets.regularFont);
		g.drawText("Hyperspace:",        40, 190, AliteColors.get().informationText(), Assets.regularFont);
		g.drawText("Condition:",         40, 230, AliteColors.get().informationText(), Assets.regularFont);
		g.drawText("Legal Status:",      40, 270, AliteColors.get().informationText(), Assets.regularFont);
		g.drawText("Rating:",            40, 310, AliteColors.get().informationText(), Assets.regularFont);
		g.drawText("Fuel:",              40, 350, AliteColors.get().informationText(), Assets.regularFont);
		g.drawText("Cash:",              40, 390, AliteColors.get().informationText(), Assets.regularFont);
		if (!player.getActiveMissions().isEmpty()) {
			g.drawText("Mission Objective:", 750, 150, AliteColors.get().informationText(), Assets.regularFont);
		}
		
		if (player.getCurrentSystem() != null) {
			g.drawText(player.getCurrentSystem().getName(),    400, 150, AliteColors.get().currentSystemName(), Assets.regularFont);
		}
		if (player.getHyperspaceSystem() != null) {
			g.drawText(player.getHyperspaceSystem().getName(), 400, 190, AliteColors.get().hyperspaceSystemName(), Assets.regularFont);
		}
		g.drawText(player.getCondition().getName(),        400, 230, player.getCondition().getColor(),  Assets.regularFont);
		g.drawText(player.getLegalStatus().getName(),      400, 270, AliteColors.get().legalStatus(),  Assets.regularFont);
		g.drawText(player.getRating().getName() + "  (Score:  " + player.getScore() + ")", 400, 310, AliteColors.get().rating(),   Assets.regularFont);
		g.drawText(String.format("%d.%d Light Years", player.getCobra().getFuel() / 10, player.getCobra().getFuel() % 10), 400, 350, AliteColors.get().remainingFuel(), Assets.regularFont);
		g.drawText(String.format("%d.%d Credits", player.getCash() / 10, player.getCash() % 10), 400, 390, AliteColors.get().balance(), Assets.regularFont);
		if (!player.getActiveMissions().isEmpty()) {
			g.drawText(player.getActiveMissions().get(0).getObjective(), 1110, 150, AliteColors.get().missionObjective(), Assets.regularFont);
		}
		if (!(((Alite) game).getCurrentScreen() instanceof FlightScreen)) {
			g.drawText("Visit http://alite.mobi", 50, 1050, AliteColors.get().mainText(), Assets.regularFont);
		}
	}

	private void drawLasers(final Graphics g) {
		Alite alite = (Alite) game;
		PlayerCobra cobra = alite.getCobra();

		long lineColor = AliteColors.get().arrow();
		long textColor = AliteColors.get().equipmentDescription();
		
		Laser front = cobra.getLaser(PlayerCobra.DIR_FRONT);
		if (front != null) {
			g.drawText(front.getShortName(), SHIP_X + 630, SHIP_Y - 20, textColor, Assets.smallFont);
			int halfWidth = g.getTextWidth(front.getShortName(), Assets.smallFont) >> 1;
			g.drawLine(SHIP_X + 630 + halfWidth, SHIP_Y - 10, SHIP_X + 630 + halfWidth, SHIP_Y + 20, lineColor);
			drawArrow(g, SHIP_X + 630 + halfWidth, SHIP_Y + 20, SHIP_X + 490, SHIP_Y + 20, lineColor, Direction.DIR_LEFT);
		}		
		Laser back = cobra.getLaser(PlayerCobra.DIR_REAR);
		if (back != null) {
			g.drawText(back.getShortName(), SHIP_X + 130, SHIP_Y + 620, textColor, Assets.smallFont);
			int halfWidth = g.getTextWidth(back.getShortName(), Assets.smallFont) >> 1;
			g.drawLine(SHIP_X + 130 + halfWidth, SHIP_Y + 580, SHIP_X + 130 + halfWidth, SHIP_Y + 590, lineColor);
			g.drawLine(SHIP_X + 130 + halfWidth, SHIP_Y + 580, SHIP_X + 480, SHIP_Y + 580, lineColor);
			drawArrow(g, SHIP_X + 480, SHIP_Y + 580, SHIP_X + 480, SHIP_Y + 550, lineColor, Direction.DIR_UP);
		}
		Laser right = cobra.getLaser(PlayerCobra.DIR_RIGHT);
		if (right != null) {
			g.drawText(right.getShortName(), SHIP_X + 1000, SHIP_Y + 420, textColor, Assets.smallFont);
			int halfWidth = g.getTextWidth(right.getShortName(), Assets.smallFont) >> 1;
			g.drawLine(SHIP_X + 1000 + halfWidth, SHIP_Y + 390, SHIP_X + 1000 + halfWidth, SHIP_Y + 370, lineColor);
			drawArrow(g, SHIP_X + 940, SHIP_Y + 370, SHIP_X + 1000 + halfWidth, SHIP_Y + 370, lineColor, Direction.DIR_LEFT);
		}
		Laser left = cobra.getLaser(PlayerCobra.DIR_LEFT);
		if (left != null) {
			g.drawText(left.getShortName(), SHIP_X - 250, SHIP_Y + 420, textColor, Assets.smallFont);
			int halfWidth = g.getTextWidth(left.getShortName(), Assets.smallFont) >> 1;
			g.drawLine(SHIP_X - 250 + halfWidth, SHIP_Y + 390, SHIP_X - 250 + halfWidth, SHIP_Y + 370, lineColor);
			drawArrow(g, SHIP_X + 20, SHIP_Y + 370, SHIP_X - 250 + halfWidth, SHIP_Y + 370, lineColor, Direction.DIR_RIGHT);
		}
	}
	
	private void drawEquipment(final Graphics g) {
		Alite alite = (Alite) game;
		List <Equipment> installedEquipment = alite.getCobra().getInstalledEquipment();
		PlayerCobra cobra = alite.getCobra();
		int missileCount = cobra.getMissiles();
		
		int counter = 0;
		int beginY = missileCount > 0 ? SHIP_Y + 140 : SHIP_Y + 170;
		for (Equipment equip: installedEquipment) {
			g.drawText(equip.getShortName(), SHIP_X + 980, beginY - counter * 30, AliteColors.get().equipmentDescription(), Assets.smallFont);
			g.drawLine(SHIP_X + 960, beginY - 12 - counter * 30, SHIP_X + 930, beginY - 12 - counter * 30, AliteColors.get().arrow());
			g.drawLine(SHIP_X + 930, beginY - 12 - counter * 30, SHIP_X + 930, beginY + 24 - counter * 30, AliteColors.get().arrow());
			counter++;
		}
		if (missileCount > 0) {
			g.drawText("Missiles x " + missileCount, SHIP_X + 980, SHIP_Y + 170, AliteColors.get().equipmentDescription(), Assets.smallFont);
			g.drawLine(SHIP_X + 960, beginY + 18, SHIP_X + 930, beginY + 18, AliteColors.get().arrow());			
		} 
		if (missileCount > 0 || installedEquipment.size() > 0) {
			g.drawLine(SHIP_X + 930, beginY + 18, SHIP_X + 930, SHIP_Y + 200, AliteColors.get().arrow());
			drawArrow(g, SHIP_X + 780, SHIP_Y + 200, SHIP_X + 930, SHIP_Y + 200, AliteColors.get().arrow(), Direction.DIR_LEFT);
		}
	}
	
	public static String getGameTime(long gameTime) {				
		long diffInSeconds = TimeUnit.SECONDS.convert(gameTime, TimeUnit.NANOSECONDS);
		int diffInDays = (int) (diffInSeconds / 86400);
		diffInSeconds -= (diffInDays * 86400);
		int diffInHours = (int) (diffInSeconds / 3600);
		diffInSeconds -= (diffInHours * 3600);
		int diffInMinutes = (int) (diffInSeconds / 60);
		diffInSeconds -= (diffInMinutes * 60);		
		return String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", diffInDays, diffInHours, diffInMinutes, diffInSeconds);
	}

	private void drawGameTime(final Graphics g, float deltaTime) {		
		g.drawRect(SHIP_X + 800, SHIP_Y + 570, 300, 100, AliteColors.get().message());
		int halfWidth = g.getTextWidth("Game Time:", Assets.regularFont) >> 1;
		g.drawText("Game Time:", SHIP_X + 800 + 150 - halfWidth, SHIP_Y + 610, AliteColors.get().message(), Assets.regularFont);
		String text = getGameTime(((Alite) game).getGameTime());
		halfWidth = g.getTextWidth(text, Assets.regularFont) >> 1;
		g.drawText(text, SHIP_X + 800 + 150 - halfWidth, SHIP_Y + 650, AliteColors.get().message(), Assets.regularFont);
	}
	
	@Override
	public void update(float deltaTime) {
		if (askForTutorial || getMessage() != null) {
			super.updateWithoutNavigation(deltaTime);
		} else {
			super.update(deltaTime);
		}
		if (forwardingScreen != null) {			
			newScreen = forwardingScreen;
			forwardingScreen = null;
			performScreenChange();
			postScreenChange();
		}
		if (askForTutorial) {
			askForTutorial = false;
			setMessage("You seem to be new to Alite, Commander. Would you like to visit the Training Academy now?", MessageType.YESNO, Assets.regularFont);
			messageIsModal = true;
		}
	}

	private Screen processAnswer() {
		Screen screen = null;
		requireAnswer = false;			
		if (messageResult == 1) {
			screen = new ControlOptionsScreen(game, !pendingShowControlOptions);
		} else if (!pendingShowControlOptions) {				
			setLargeMessage("If you want to visit the Academy later, you can find it at the bottom of the Command Console. Before you launch, it might be a good idea to review your Control Settings. Would you like to do so, now?", MessageType.YESNO, Assets.regularFont);
			requireAnswer = true;
			pendingShowControlOptions = true;
			messageIsModal = true;							
		}
		messageResult = 0;
		return screen;
	}
	
	@Override 
	public void processTouch(TouchEvent touch) {
		super.processTouch(touch);
		if (touch.type == TouchEvent.TOUCH_UP) {
			if (!(((Alite) game).getCurrentScreen() instanceof FlightScreen)) {
				if (touch.x > 30 && touch.x < 960 && touch.y > 1020) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://alite.mobi"));
					((Alite) game).startActivityForResult(browserIntent, 0);
				}
			}
		}
		if (requireAnswer && messageResult != 0) {
			newScreen = processAnswer();
		}		
	}
	
	@Override
	public void processButtonUp(int button) {
		super.processButtonUp(button);
		if (requireAnswer && messageResult != 0) {
			newControlScreen = processAnswer();
		}		
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		Player player = ((Alite) game).getPlayer();
		g.clear(AliteColors.get().background());
		displayTitle("Commander " + player.getName());
		if (cobra == null) {
		  loadAssets();
		} else {
		  g.drawPixmap(cobra, SHIP_X, SHIP_Y);
		}
		
		drawInformation(g);
		drawEquipment(g);		
		drawLasers(g);
		drawGameTime(g, deltaTime);		
	}

	@Override
	public void dispose() {
		super.dispose();
		if (cobra != null) {
			cobra.dispose();
			cobra = null;
		}
	}

	@Override
	public void loadAssets() {
		if (cobra != null) {
			cobra.dispose();
		}
		cobra = game.getGraphics().newPixmap("cobra_small.png", true);
		super.loadAssets();
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}	
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		alite.setScreen(new StatusScreen(alite));
		return true;
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.STATUS_SCREEN;
	}	
}
