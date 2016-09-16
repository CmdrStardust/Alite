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
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import android.graphics.Point;
import android.util.SparseIntArray;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.Button.TextPosition;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.Rating;
import de.phbouillon.android.games.alite.model.generator.Raxxla;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.screens.NavigationBar;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class GalaxyScreen extends AliteScreen {
	public static final int HALF_WIDTH = 760;
	public static final int HALF_HEIGHT = 460;
	public static final int CROSS_SIZE = 40;
	public static final int CROSS_DISTANCE = 2;

	private static final int SCALE_CONST = 7;

	protected float zoomFactor;
	protected float pendingZoomFactor = -1.0f;
	protected String title;
	protected MappedSystemData [] systemData;

	private final HashSet <Integer> doubles = new HashSet<Integer>();
	private final SparseIntArray doubleCount = new SparseIntArray(70000);

	private Button findButton;
	private Button homeButton;
	private Pixmap findIcon;
	private Pixmap homeIcon;
	protected int centerX = 0;
	protected int centerY = 0;
	protected int pendingCenterX = -1;
	protected int pendingCenterY = -1;
	protected int targetX = 0;
	protected int targetY = 0;
	private int deltaX = 0;
	private int deltaY = 0;
	private boolean zoom = false;
	private MappedSystemData scalingReferenceSystem = null;

	class MappedSystemData {
		SystemData system;
		int x;
		int y;
		int xDiff;

		MappedSystemData(SystemData system, int x, int y) {
			this.system = system;
			this.x = x;
			this.y = y;
			xDiff = 0;
		}
	}

	public GalaxyScreen(Game game) {
		super(game);
	}

	public static boolean initialize(Alite alite, final DataInputStream dis) {
		GalaxyScreen gs = new GalaxyScreen(alite);
		try {
			gs.zoomFactor = dis.readFloat();
			gs.centerX = dis.readInt();
			gs.centerY = dis.readInt();
			gs.pendingZoomFactor = gs.zoomFactor;
			gs.pendingCenterX    = gs.centerX;
			gs.pendingCenterY    = gs.centerY;
		} catch (Exception e) {
			AliteLog.e("Galaxy Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(gs);
		return true;
	}

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeFloat(zoomFactor);
		dos.writeInt(centerX);
		dos.writeInt(centerY);
	}

	private MappedSystemData findClosestSystem(int x, int y) {
		int minDist = -1;
		Player player = ((Alite) game).getPlayer();
		int key;
		MappedSystemData closestSystem = null;
		for (MappedSystemData system: systemData) {
			key = (system.system.getX() << 8) + system.system.getY();
			int dist = (system.x - x) * (system.x - x) + (system.y - y) * (system.y - y);
			if (dist < minDist || closestSystem == null) {
				if (doubles.contains(key) && player.getHyperspaceSystem() == system.system) {
					continue;
				}
				minDist = dist;
				closestSystem = system;
			}
		}
		return closestSystem;
	}
	
	private String capitalize(String t) {		
		return t == null || t.length() < 1 ? "" : t.length() < 2 ? t : Character.toUpperCase(t.charAt(0)) + t.substring(1).toLowerCase(Locale.getDefault());
	}
	
	private void findSystem() {
		final Alite alite = (Alite) game;
		TextInputScreen textInput = new TextInputScreen(alite, "Find Planet", "Enter planet name", "", this,
			new TextCallback() {
				@Override
				public void onOk(String text) {
					if (text.trim().isEmpty()) {
						return;
					}
					boolean found = false;
					for (MappedSystemData system: systemData) {
						if (system.system.getName().equalsIgnoreCase(text)) {
							alite.getPlayer().setHyperspaceSystem(system.system);
							targetX = computeCenterX(system.system.getX());
							targetY = computeCenterY(system.system.getY());
							found = true;
							break;
						}
					}
					if (!found) {
						int galaxy = alite.getGenerator().findGalaxyOfPlanet(text);
						if (galaxy == -1) {
							setMessage("Planet " + capitalize(text) + " is unknown.");
							SoundManager.play(Assets.error);
						} else {
							setMessage("Planet " + capitalize(text) + " is in Galaxy " + galaxy + 
									". You are currently in Galaxy " + alite.getGenerator().getCurrentGalaxy() + ".");
							SoundManager.play(Assets.alert);
						}						
					}
				}

				@Override
				public void onCancel() {
				}
			});
		textInput.setMaxLength(8);
		textInput.setAllowSpace(false);
		newScreen = textInput;
	}

	@Override
	public void processTouch(TouchEvent touch) {
		super.processTouch(touch);

		if (getMessage() != null) {
			return;
		}

		if (touch.type == TouchEvent.TOUCH_SCALE) {
			if (scalingReferenceSystem == null) {
				scalingReferenceSystem = findClosestSystem(touch.x2, touch.y2);
			}
			int dx = scalingReferenceSystem.x - (int) (((scalingReferenceSystem.system.getX() - 128) * SCALE_CONST * touch.zoomFactor) + centerX);
			int dy = scalingReferenceSystem.y - (int) (((scalingReferenceSystem.system.getY() -  64) * SCALE_CONST * touch.zoomFactor) + centerY);
			centerX += dx;
			centerY += dy;
			targetX = centerX;
			targetY = centerY;
			zoomFactor = touch.zoomFactor;
			zoom = true;
			normalizeSystems();
		}
		if (game.getInput().getTouchCount() > 1) {
			return;
		}
		if (touch.type == TouchEvent.TOUCH_SWEEP && touch.x <= 1720) {
			deltaX = (int) (touch.x2 * 1.5f);
			deltaY = (int) (touch.y2 * 1.5f);
		}
		if (touch.type == TouchEvent.TOUCH_DOWN && touch.pointer == 0) {
			deltaX = 0;
			deltaY = 0;
			startX = touch.x;
			startY = touch.y;
			lastX = touch.x;
			lastY = touch.y;
		}
		if (touch.type == TouchEvent.TOUCH_DRAGGED && touch.pointer == 0) {
			if (zoom || touch.x > (1920 - NavigationBar.SIZE)) {
				return;
			}
			centerX += (touch.x - lastX);
			centerY += (touch.y - lastY);
			targetX = centerX;
			targetY = centerY;
			normalizeSystems();
			lastY = touch.y;
			lastX = touch.x;
		}
		if (touch.type == TouchEvent.TOUCH_UP && touch.pointer == 0) {
			if (touch.x > (1920 - NavigationBar.SIZE)) {
				return;
			}
			if (zoom) {
				zoom = false;
				scalingReferenceSystem = null;
				return;
			}
			if (Math.abs(startX - touch.x) < 20 &&
				Math.abs(startY - touch.y) < 20) {
				if (homeButton.isTouched(touch.x, touch.y)) {
					SystemData homeSystem = ((Alite) game).getPlayer().getCurrentSystem();
					((Alite) game).getPlayer().setHyperspaceSystem(homeSystem);
					if (homeSystem == null) {
						targetX = computeCenterX(((Alite) game).getPlayer().getPosition().x);
						targetY = computeCenterY(((Alite) game).getPlayer().getPosition().y);
					} else {
						targetX = computeCenterX(homeSystem.getX());
						targetY = computeCenterY(homeSystem.getY());
					}
					SoundManager.play(Assets.click);
					return;
				}
				if (findButton.isTouched(touch.x, touch.y)) {
					SoundManager.play(Assets.click);
					findSystem();
					return;
				}
				MappedSystemData closestSystem = findClosestSystem(touch.x, touch.y);
				if (closestSystem.x > 1720 || closestSystem.x < 0 || closestSystem.y > 1080 || closestSystem.y < 20) {
					return;
				}
				((Alite) game).getPlayer().setHyperspaceSystem(closestSystem.system);
				SoundManager.play(Assets.click);
			}
		}
	}

	private void computeBorders() {
		int tmp = (int) (-128 * SCALE_CONST * zoomFactor + centerX);
		if (tmp > 20) {
			centerX -= (tmp - 20);
			targetX = centerX;
		}
		tmp = (int) (-64 * SCALE_CONST * zoomFactor + centerY);
		if (tmp > 100) {
			centerY -= (tmp - 100);
			targetY = centerY;
		}

		tmp = (int) (127 * SCALE_CONST * zoomFactor + centerX);
		if (tmp < 1700) {
			centerX += (1700 - tmp);
			targetX = centerX;
		}

		tmp = (int) ( 63 * SCALE_CONST * zoomFactor + centerY);
		if (tmp < 1000) {
			centerY += (1000 - tmp);
			targetY = centerY;
		}
	}

	private int transformX(int x) {
		return (int) ((x - 128) * SCALE_CONST * zoomFactor) + centerX;
	}

	private int transformY(int y) {
		return (int) ((y -  64) * SCALE_CONST * zoomFactor) + centerY;
	}

	protected int computeCenterX(int x) {
		return (int) (HALF_WIDTH - (x - 128) * SCALE_CONST * zoomFactor) + 150;
	}

	protected int computeCenterY(int y) {
		return (int) (HALF_HEIGHT - (y - 64) * SCALE_CONST * zoomFactor) + 100;
	}

	protected void normalizeSystems() {
		// x ranges from 0 to 255,
		// y ranges from 0 to 127.
		computeBorders();

		for (MappedSystemData system: systemData) {
			int key = (system.system.getX() << 8) + system.system.getY();
			int dcx = 0;
			if (doubles.contains(key)) {
				int occ = doubleCount.get(key);
				doubleCount.put(key, occ + 1);
				dcx = occ << 3;
			}

			system.x = transformX(system.system.getX());
			system.y = transformY(system.system.getY());
			system.xDiff = dcx;
		}
		doubleCount.clear();
	}

	private void renderName(MappedSystemData system) {
		Graphics g = game.getGraphics();
		int nameWidth = g.getTextWidth(system.system.getName(), Assets.regularFont);
		int positionX = (int) (3 * zoomFactor) + 2;
		int positionY = 40;
		if (system.x + nameWidth > (HALF_WIDTH << 1)) {
			positionX = -positionX - nameWidth;
		}
		if (system.y + 40 > (HALF_HEIGHT << 1)) {
			positionY = -40;
		}
		g.drawText(system.system.getName(), system.x + positionX, system.y + positionY, getColor(system.system.getEconomy()), Assets.regularFont);
	}

	public void updateMap(float deltaTime) {
		int dx = (centerX - targetX) >> 4;
		int dy = (centerY - targetY) >> 3;
		centerX -= dx;
		centerY -= dy;

		if (deltaY != 0) {
			deltaY += deltaY > 0 ? -1 : 1;
			centerY += deltaY;
			targetY = centerY;
		}
		if (deltaX != 0) {
			deltaX += deltaX > 0 ? -1 : 1;
			centerX += deltaX;
			targetX = centerX;
		}

		normalizeSystems();
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		updateMap(deltaTime);
	}

	private void renderCurrentPositionCross() {
		Graphics g = game.getGraphics();
		Player player = ((Alite) game).getPlayer();
		SystemData hyperspaceSystem = player.getHyperspaceSystem();

		int px = transformX(hyperspaceSystem == null ? player.getPosition().x : hyperspaceSystem.getX());
		int py = transformY(hyperspaceSystem == null ? player.getPosition().y : hyperspaceSystem.getY());
		g.drawLine(px, py - CROSS_SIZE - CROSS_DISTANCE, px, py - CROSS_DISTANCE, AliteColors.get().baseInformation());
		g.drawLine(px, py + CROSS_SIZE + CROSS_DISTANCE, px, py + CROSS_DISTANCE, AliteColors.get().baseInformation());
		g.drawLine(px - CROSS_SIZE - CROSS_DISTANCE, py, px - CROSS_DISTANCE, py, AliteColors.get().baseInformation());
		g.drawLine(px + CROSS_SIZE + CROSS_DISTANCE, py, px + CROSS_DISTANCE, py, AliteColors.get().baseInformation());
	}

	private void renderCurrentFuelCircle() {
		Graphics g = game.getGraphics();
		Player player = ((Alite) game).getPlayer();

		SystemData currentSystem = player.getCurrentSystem();
		SystemData hyperspaceSystem = player.getHyperspaceSystem();

		if (hyperspaceSystem != null) {
			int xp = hyperspaceSystem.getX();
			int x1 = transformX((int) ((float) xp - 17.5f));
			int x2 = transformX((int) ((float) xp + 17.5f));
			int px = transformX(xp);
			int py = transformY(hyperspaceSystem.getY());
			g.drawDashedCircle(px, py, (x2 - x1) >> 1, AliteColors.get().dashedFuelCircle(), 64);
		}

		float fuel = (float) player.getCobra().getFuel();
		int xp = player.getCurrentSystem() == null ? player.getPosition().x : player.getCurrentSystem().getX();
		int x1 = transformX((int) ((float) xp - 17.5f * fuel / 70.0f));
        int x2 = transformX((int) ((float) xp + 17.5f * fuel / 70.0f));

        int px = transformX(currentSystem == null ? player.getPosition().x : player.getCurrentSystem().getX());
        int py = transformY(currentSystem == null ? player.getPosition().y : player.getCurrentSystem().getY());
        g.drawCircle(px, py, (x2 - x1) >> 1, AliteColors.get().fuelCircle(), 64);

	}

	private int computeDistance(SystemData target, Point position) {
		int dx = position.x - target.getX();
		int dy = position.y - target.getY();
		return (int) Math.sqrt(dx * dx + dy * dy) << 2;
	}

	private void renderDistance() {
		Player player = ((Alite) game).getPlayer();
		Graphics g = game.getGraphics();

        if (player.getHyperspaceSystem() != null) {
        	int distance = player.getCurrentSystem() == null ? computeDistance(player.getHyperspaceSystem(), player.getPosition())
				 : player.getHyperspaceSystem().computeDistance(player.getCurrentSystem());
        	g.drawText(String.format("%s: %d.%d Light Years",
				player.getHyperspaceSystem() == null ? "Unknown" : player.getHyperspaceSystem().getName(), distance / 10, distance % 10), 100, 1060, AliteColors.get().baseInformation(), Assets.regularFont);
        }
	}

	@Override
	public void present(float deltaTime) {
		Graphics g = game.getGraphics();

		g.clear(AliteColors.get().background());
		displayTitle(title);

		g.setClip(0, -1, -1, 1000);
		for (MappedSystemData system: systemData) {
			g.fillCircle(system.x + system.xDiff, system.y, (int) (3 * zoomFactor), getColor(system.system.getEconomy()), 32);
			if (zoomFactor >= 4.0f && system.x > 0 && system.x < 1920 && system.y > 0 && system.y < 1080) {
				renderName(system);
			}
		}

		renderCurrentPositionCross();
		renderCurrentFuelCircle();
		renderDistance();

		g.setClip(-1, -1, -1, -1);

		homeButton.render(g);
		findButton.render(g);
	}

	protected void setupUi() {
		initializeSystems();
		findDoubles();

		findButton = new Button(1375, 980, 320, 100, "Find", Assets.regularFont, null);
		findButton.setTextPosition(TextPosition.RIGHT);
		findButton.setGradient(true);
		findButton.setPixmap(findIcon);

		homeButton = new Button(1020, 980, 320, 100, "Home", Assets.regularFont, null);
		homeButton.setTextPosition(TextPosition.RIGHT);
		homeButton.setGradient(true);
		homeButton.setPixmap(homeIcon);
	}

	@Override
	public void activate() {
		zoomFactor = 1.0f;
		title = "Galactic Chart #" + ((Alite) game).getGenerator().getCurrentGalaxy();
		game.getInput().setZoomFactor(zoomFactor);
		centerX = HALF_WIDTH;
		centerY = HALF_HEIGHT;
		targetX = centerX;
		targetY = centerY;

		if (Math.abs(pendingZoomFactor - zoomFactor) > 0.0001 && pendingZoomFactor > 0) {
			zoomFactor = pendingZoomFactor;
			game.getInput().setZoomFactor(zoomFactor);
			pendingZoomFactor = -1.0f;
		}
		if (pendingCenterX != -1) {
			centerX = pendingCenterX;
			targetX = centerX;
			pendingCenterX = -1;
		}
		if (pendingCenterY != -1) {
			centerY = pendingCenterY;
			targetY = centerY;
			pendingCenterY = -1;
		}
		setupUi();
	}

	private final void findDoubles() {
		HashSet <Integer> usedCoords = new HashSet<Integer>();
		for (MappedSystemData s: systemData) {
			int key = (s.system.getX() << 8) + s.system.getY();
			if (usedCoords.contains(key)) {
				doubles.add(key);
			} else {
				usedCoords.add(key);
			}
		}
	}

	private void initializeSystems() {
		Player player = ((Alite) game).getPlayer();
		if (player.getRating() == Rating.ELITE && ((Alite) game).getGenerator().getCurrentGalaxyFromSeed() == 8) {
			ArrayList <SystemData> temp = new ArrayList<SystemData>(Arrays.asList(((Alite) game).getGenerator().getSystems()));
			Raxxla raxxla = new Raxxla();
			temp.add(raxxla.getSystem());
			systemData = new MappedSystemData[temp.size()];
			int count = 0;
			for (SystemData system: temp) {
				int x = (int) ((system.getX() - 128) * SCALE_CONST + HALF_WIDTH);
				int y = (int) ((system.getY() -  64) * SCALE_CONST + HALF_HEIGHT);
				systemData[count++] = new MappedSystemData(system, x, y);
			}
		} else {
			systemData = new MappedSystemData[256];
			int count = 0;
			for (SystemData system: ((Alite) game).getGenerator().getSystems()) {
				int x = (int) ((system.getX() - 128) * SCALE_CONST + HALF_WIDTH);
				int y = (int) ((system.getY() -  64) * SCALE_CONST + HALF_HEIGHT);
				systemData[count++] = new MappedSystemData(system, x, y);
			}
		}
	}

	public boolean namesVisible() {
		return zoomFactor >= 4.0f;
	}

	public Button getHomeButton() {
		return homeButton;
	}

	public float getZoomFactor() {
		return zoomFactor;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (findIcon != null) {
			findIcon.dispose();
			findIcon = null;
		}
		if (homeIcon != null) {
			homeIcon.dispose();
			homeIcon = null;
		}
	}

	@Override
	public void loadAssets() {
		findIcon = game.getGraphics().newPixmap("search_icon.png", true);
		homeIcon = game.getGraphics().newPixmap("home_icon.png", true);
		super.loadAssets();
	}

	@Override
	public int getScreenCode() {
		return ScreenCodes.GALAXY_SCREEN;
	}
}
