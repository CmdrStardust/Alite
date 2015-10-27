package de.phbouillon.android.games.alite.screens.opengl.sprites.buttons;

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
import java.io.ObjectInputStream;
import java.io.Serializable;

import android.opengl.GLES11;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Condition;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;
import de.phbouillon.android.games.alite.screens.opengl.DefaultCoordinateTransformer;
import de.phbouillon.android.games.alite.screens.opengl.ICoordinateTransformer;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class AliteButtons implements Serializable {
	private static final long serialVersionUID = 3492100128009209777L;

	private static final float RETRO_ROCKET_SPEED    = 8350.0f; // m/s

	public static boolean OVERRIDE_HYPERSPACE  = false;
	public static boolean OVERRIDE_INFORMATION = false;
	public static boolean OVERRIDE_LASER       = false;
	public static boolean OVERRIDE_MISSILE     = false;
	public static boolean OVERRIDE_TORUS       = false;
	
	private static final int TORUS_DRIVE      =  0;
	private static final int HYPERSPACE       =  1;
	private static final int GAL_HYPERSPACE   =  2;	
	private static final int STATUS           =  3;
	private static final int DOCKING_COMPUTER =  4;
	private static final int ECM              =  5;
	private static final int ESCAPE_CAPSULE   =  6;
	private static final int ENERGY_BOMB      =  7;	
	private static final int RETRO_ROCKETS    =  8;	
	private static final int ECM_JAMMER       =  9;	
	private static final int CLOAKING_DEVICE  = 10;
    private static final int MISSILE          = 11;
    private static final int FIRE             = 12;
    
	private ButtonData [] buttons = new ButtonData[15];	
	private Sprite overlay;
	private Sprite yellowOverlay;
	private Sprite redOverlay;
	private Sprite cycleLeft;
	private Sprite cycleRight;
	private final String textureFilename;
	private final ICoordinateTransformer ct;
	protected transient Alite alite;
	protected final GraphicObject ship;
	private Screen newScreen;
	private final InGameManager inGame;
	private ButtonData source = null;
	private ButtonGroup [] buttonGroup;	
	private boolean sweepLeftPossible = false;
	private boolean sweepRightPossible = false;
	private boolean actionPerformed = false;
	private boolean sweepLeftDown = false;
	private boolean sweepRightDown = false;
	private long downTime = -1;
	
	private final TorusBlockingTraverser torusTraverser;
	private final EnergyBombTraverser energyBombTraverser;
	private final ECMTraverser ecmTraverser;
	
	public AliteButtons(Alite alite, GraphicObject ship, InGameManager inGame) {
		ct = new DefaultCoordinateTransformer(alite);
		this.alite = alite;
		this.ship = ship;
		this.inGame = inGame;
		
		textureFilename = "textures/ui4.png";
		alite.getTextureManager().addTexture(textureFilename);
		createButtonGroups();
		createButtons();
						
		overlay       = new Sprite(alite, 0, 0, 20, 20, 200.0f / 1024.0f, 600.0f / 1024.0f, 400.0f / 1024.0f,  800.0f / 1024.0f, textureFilename);
		yellowOverlay = new Sprite(alite, 0, 0, 20, 20, 600.0f / 1024.0f,             0.0f, 800.0f / 1024.0f,  200.0f / 1024.0f, textureFilename);
		redOverlay    = new Sprite(alite, 0, 0, 20, 20, 400.0f / 1024.0f, 800.0f / 1024.0f, 600.0f / 1024.0f, 1000.0f / 1024.0f, textureFilename);
		cycleLeft     = new Sprite(alite, ct.getTextureCoordX(250), ct.getTextureCoordY(400), ct.getTextureCoordX(350), ct.getTextureCoordY(500), 800.0f / 1024.0f, 800.0f / 1024.0f, 900.0f / 1024.0f,  900.0f / 1024.0f, textureFilename);
		cycleRight    = new Sprite(alite, ct.getTextureCoordX(1560), ct.getTextureCoordY(400), ct.getTextureCoordX(1660), ct.getTextureCoordY(500), 800.0f / 1024.0f, 800.0f / 1024.0f, 900.0f / 1024.0f,  900.0f / 1024.0f, textureFilename);
		newScreen     = null;
		
		torusTraverser = new TorusBlockingTraverser(inGame);
		energyBombTraverser = new EnergyBombTraverser(inGame);
		ecmTraverser = new ECMTraverser(inGame);
	}

	public void reset() {
		createButtonGroups();
		createButtons();		
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "AliteButtons.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "AliteButtons.readObject I");
			this.alite     = Alite.get();
			AliteLog.e("readObject", "AliteButtons.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	private void createButtons() {
		buttons[FIRE]                    = genButtonData(3, 1, Settings.buttonPosition[Settings.FIRE], "Fire");
		buttons[MISSILE]                 = genButtonData(2, 3, Settings.buttonPosition[Settings.MISSILE], "Missile");
		buttons[MISSILE].active          = false;
		buttons[ECM]                     = genButtonData(0, 3, Settings.buttonPosition[Settings.ECM], "ECM");
		buttons[ECM].active              = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.ecmSystem);
		buttons[RETRO_ROCKETS]           = genButtonData(1, 2, Settings.buttonPosition[Settings.RETRO_ROCKETS], "Retro Rockets");
		buttons[RETRO_ROCKETS].active    = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.retroRockets);
		buttons[ESCAPE_CAPSULE]          = genButtonData(0, 4, Settings.buttonPosition[Settings.ESCAPE_CAPSULE], "Escape Capsule");
		buttons[ESCAPE_CAPSULE].active   = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.escapeCapsule);
		buttons[ENERGY_BOMB]             = genButtonData(1, 1, Settings.buttonPosition[Settings.ENERGY_BOMB], "Energy Bomb");
		buttons[ENERGY_BOMB].active      = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.energyBomb);
		
		buttons[STATUS]                  = genButtonData(2, 2, Settings.buttonPosition[Settings.STATUS], "Status");
		// Torus drive and docking computer button are always in the same position, as they're mutually exclusive.
		buttons[TORUS_DRIVE]             = genButtonData(2, 1, Settings.buttonPosition[Settings.TORUS], "Torus Drive");
		buttons[TORUS_DRIVE].active      = false;
		buttons[DOCKING_COMPUTER]        = genButtonData(0, 0, Settings.buttonPosition[Settings.TORUS], "Docking Computer");
		buttons[DOCKING_COMPUTER].active = false;
		buttons[HYPERSPACE]              = genButtonData(0, 1, Settings.buttonPosition[Settings.HYPERSPACE], "Hyperspace");
		buttons[HYPERSPACE].active       = alite.isHyperspaceTargetValid();

		buttons[GAL_HYPERSPACE]          = genButtonData(0, 2, Settings.buttonPosition[Settings.GALACTIC_HYPERSPACE], "Galactic Hyperspace");
		buttons[GAL_HYPERSPACE].active   = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.galacticHyperdrive);		
		buttons[CLOAKING_DEVICE]         = genButtonData(3, 2, Settings.buttonPosition[Settings.CLOAKING_DEVICE], "Cloaking Device");
		buttons[CLOAKING_DEVICE].active  = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.cloakingDevice);						
		buttons[ECM_JAMMER]              = genButtonData(1, 0, Settings.buttonPosition[Settings.ECM_JAMMER], "ECM Jammer");	
		buttons[ECM_JAMMER].active       = alite.getPlayer().getCobra().isEquipmentInstalled(EquipmentStore.ecmJammer);
	}
	
	private void createButtonGroups() {
		buttonGroup = new ButtonGroup[4];
		
		buttonGroup[0] = new ButtonGroup(0, true, true);
		buttonGroup[1] = new ButtonGroup(1, true, false);
		buttonGroup[2] = new ButtonGroup(2, false, true);		
		buttonGroup[3] = new ButtonGroup(3, false, false);
	}
	
	private ButtonData genButtonData(float x, float y, int positionIndex, String name) {
		// Linke Seite: (0, 0), (1, 1), (0, 2)
		// Rechte Seite: (11.4, 0), (10.4, 1), (11.4, 2)
		float xt, yt;
		int groupIndex;
		if (positionIndex < 6) {
			// Linke Seite
			xt = ((positionIndex % 3) % 2) == 0 ? 0.0f : 1.0f;
			groupIndex = positionIndex < 3 ? 0 : 1;
		} else {
			// Rechte Seite
			xt = ((positionIndex % 3) % 2) == 0 ? 11.4f : 10.4f;
			groupIndex = positionIndex < 9 ? 2 : 3;
		}
		yt = positionIndex % 3;
		
		ButtonData result = new ButtonData(new Sprite(alite, ct.getTextureCoordX(xt * 150.0f), ct.getTextureCoordY(yt * 150.0f), ct.getTextureCoordX(xt * 150.0f + 200.0f), ct.getTextureCoordY(yt * 150.0f + 200.0f),
                (x * 200.0f) / 1024.0f, (y * 200.0f) / 1024.0f, ((x + 1.0f) * 200.0f) / 1024.0f, ((y + 1.0f) * 200.0f) / 1024.0f, textureFilename),
                xt * 150.0f + 100.0f, yt * 150.0f + 100.0f, name);
		buttonGroup[groupIndex].addButton(result);
		return result;
	}
		
	private void check(int button, Equipment equip) {
		if (buttons[button] != null) {
			buttons[button].active = alite.getCobra().isEquipmentInstalled(equip);
		}
	}
	
	public void update() {
		if (buttons[FIRE] != null) {
			buttons[FIRE].active = alite.getCobra().getLaser(inGame.getViewDirection()) != null;
			buttons[FIRE].yellow = alite.getLaserManager() != null && alite.getLaserManager().isAutoFire();
		}
		if (buttons[MISSILE] != null) {
			if (downTime != -1 && (System.nanoTime() - downTime) > 1500000000l && buttons[MISSILE].red) {
				inGame.handleMissileIcons();
				downTime = -1;
				buttons[MISSILE].red = false;
				SoundManager.play(Assets.click);
				source = null;
				buttons[MISSILE].selected = false;
			}
			SpaceObject missileLock = inGame.getMissileLock();
			if (missileLock != null && (missileLock.getHullStrength() < 0 || missileLock.mustBeRemoved())) {
				inGame.handleMissileIcons();
				downTime = -1;
				buttons[MISSILE].red = false;
				buttons[MISSILE].selected = false;
				inGame.setMessage("Target lost");
			}
			buttons[MISSILE].active = alite.getCobra().getMissiles() > 0;
			buttons[MISSILE].yellow = alite.getCobra().isMissileTargetting();			
			buttons[MISSILE].red = alite.getCobra().isMissileLocked();			
		}
		if (buttons[TORUS_DRIVE] != null) {
			boolean torusActive = alite.getCobra().getSpeed() < -PlayerCobra.MAX_SPEED;
			buttons[TORUS_DRIVE].active = alite.getCobra().getSpeed() <= -PlayerCobra.MAX_SPEED &&
					                           !inGame.isInExtendedSafeZone() &&
					                           (torusActive || !inGame.traverseObjects(torusTraverser)) &&
					                           alite.getCobra().getCabinTemperature() == 0 &&
					                           !inGame.isWitchSpace();
		}
		if (buttons[DOCKING_COMPUTER] != null) {
			buttons[DOCKING_COMPUTER].active = alite.getCobra().isEquipmentInstalled(EquipmentStore.dockingComputer) &&
					inGame.isInSafeZone();
			buttons[DOCKING_COMPUTER].yellow = inGame.isDockingComputerActive();
		}
		if (buttons[HYPERSPACE] != null) {
			buttons[HYPERSPACE].active = alite.isHyperspaceTargetValid() && 
					!inGame.isHyperdriveMalfunction();
		}
		if (buttons[GAL_HYPERSPACE] != null) {
			buttons[GAL_HYPERSPACE].active = alite.getCobra().isEquipmentInstalled(EquipmentStore.galacticHyperdrive) &&
					!inGame.isHyperdriveMalfunction();
		}
		check(CLOAKING_DEVICE, EquipmentStore.cloakingDevice);
		check(ECM, EquipmentStore.ecmSystem);
		check(ESCAPE_CAPSULE, EquipmentStore.escapeCapsule);
		check(ENERGY_BOMB, EquipmentStore.energyBomb);
		if (buttons[RETRO_ROCKETS] != null) {
			buttons[RETRO_ROCKETS].active = alite.getCobra().isEquipmentInstalled(EquipmentStore.retroRockets) && alite.getCobra().getSpeed() <= 0.0f;
		}
		
		int countLeft = 0;
		int countRight = 0;
		boolean needsSweepLeft = false;
		boolean needsSweepRight = false;
		for (ButtonGroup bg: buttonGroup) {
			if (bg.hasActiveButtons()) {
				if (bg.left) {
					countLeft++;
				} else {
					countRight++;
				}
			} else {
				if (bg.active) {
					if (bg.left) {
						needsSweepLeft = true;
					} else {
						needsSweepRight = true;
					}
				}
			}
		}
		sweepLeftPossible = countLeft > 1;
		sweepRightPossible = countRight > 1;
		if (countLeft >= 1 && needsSweepLeft) {
			sweepLeft();
		}
		if (countRight >= 1 && needsSweepRight) {
			sweepRight();
		}
	}
	
	public void render() {
		GLES11.glColor4f(Settings.alpha, Settings.alpha, Settings.alpha, Settings.alpha);
		for (ButtonGroup bg: buttonGroup) {
			if (bg.active) {
				for (ButtonData bd: bg.buttons) {
					if (bd != null && bd.active) {
						bd.sprite.render();
						if (bd.selected) {
							overlay.setPosition(bd.sprite.getPosition());
							overlay.render();
						}
						if (bd.yellow) {
							yellowOverlay.setPosition(bd.sprite.getPosition());
							yellowOverlay.render();
						}
						if (bd.red) {
							redOverlay.setPosition(bd.sprite.getPosition());
							redOverlay.render();
						}
					}
				}
			}
		}
		if (sweepLeftPossible) {
			cycleLeft.render();
		}
		if (sweepRightPossible) {
			cycleRight.render();
		}
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
			
	private void sweepLeft() {
		int activeIndex = 0;
		for (int i = 0, n = buttonGroup.length; i < n; i++) {
			if (buttonGroup[i].left && buttonGroup[i].active) {
				activeIndex = i;
				buttonGroup[i].active = false;
			}
		}		
		int iterations = 0;
		do {
			activeIndex++;
			if (activeIndex >= buttonGroup.length) {
				activeIndex = 0;
			}
			iterations++;
		} while (iterations <= buttonGroup.length && (!buttonGroup[activeIndex].left || !buttonGroup[activeIndex].hasActiveButtons()));
		if (iterations <= buttonGroup.length) {
			buttonGroup[activeIndex].active = true;
		} else {
			buttonGroup[0].active = true;
		}
	}
		
	private void sweepRight() {
		int activeIndex = 0;
		int firstRight = -1;
		for (int i = 0, n = buttonGroup.length; i < n; i++) {
			if (!buttonGroup[i].left && firstRight == -1) {
				firstRight = i;
			}
			if (!buttonGroup[i].left && buttonGroup[i].active) {
				activeIndex = i;
				buttonGroup[i].active = false;
			}
		}		
		int iterations = 0;
		do {
			activeIndex++;
			if (activeIndex >= buttonGroup.length) {
				activeIndex = 0;
			}
			iterations++;
		} while (iterations <= buttonGroup.length && (buttonGroup[activeIndex].left || !buttonGroup[activeIndex].hasActiveButtons()));
		if (iterations <= buttonGroup.length) {
			buttonGroup[activeIndex].active = true;
		} else {
			buttonGroup[firstRight].active = true;
		}
	}

	public boolean handleTouch(TouchEvent e) {
		boolean result = false;
		boolean fireButtonPressed = false;
		for (int i = 0; i < buttons.length; i++) {
			ButtonData bd = buttons[i];
			if (bd == null || !bd.active || !bd.parent.active) {
				continue;
			}
			if (bd.isTouched(e.x, e.y, e.type)) {
				if (e.type == TouchEvent.TOUCH_DOWN) {
					actionPerformed = false;
					source = bd;
					if (source == buttons[MISSILE] && buttons[MISSILE].red) {
						downTime = System.nanoTime();
					}
					if (source == buttons[FIRE] && !Settings.laserButtonAutofire) {
						fireButtonPressed = true;
						toggleAutoFire();
					}
				} else if (e.type == TouchEvent.TOUCH_UP) {
					if (source != null) {
						source.selected = false;
					}
					if (source != bd) {
						source = null;
						return true;
					}					
					source = null;
					SoundManager.play(Assets.click);
					switch (i) {
					    case TORUS_DRIVE:      engageTorusDrive();         break;
						case HYPERSPACE:       engageHyperspace();         break;
						case GAL_HYPERSPACE:   engageGalacticHyperspace(); break;
						case ECM:              engageECM();                break;
						case ESCAPE_CAPSULE:   engageEscapeCapsule();      break;
						case ECM_JAMMER:       toggleECMJammer();          break;
						case DOCKING_COMPUTER: engageDockingComputer();    break;
						case ENERGY_BOMB:      engageEnergyBomb();         break;
						case RETRO_ROCKETS:    engageRetroRockets();       break;
						case STATUS:           goToStatusView();           break;
						case FIRE:             toggleAutoFire();           break;
						case MISSILE:          updateMissileState();       break;
						case CLOAKING_DEVICE:  toggleCloakingDevice();     break;
					}
				} else {
					if (bd == buttons[FIRE]) {
						fireButtonPressed = true;
					}
				}
				result = true;
			} 
		}
		if (!fireButtonPressed && !Settings.laserButtonAutofire) {
			deactivateFire();
		}
		if (e.type == TouchEvent.TOUCH_DOWN) {
			if (sweepLeftPossible && e.x >= 250 && e.x <= 350 && e.y >= 400 && e.y <= 500) {
				sweepLeftDown = true;
			}
			if (sweepRightPossible && e.x >= 1560 && e.x <= 1660 && e.y >= 400 && e.y <= 500) {
				sweepRightDown = true;
			}			
		}
		if (e.type == TouchEvent.TOUCH_UP) {
			downTime = -1;
			if (source != null) {
				source.selected = false;
				source = null;
			}
			if (sweepLeftDown && e.x >= 250 && e.x <= 350 && e.y >= 400 && e.y <= 500) {
				sweepLeft();
				actionPerformed = true;				
			}
			if (sweepRightDown && e.x >= 1560 && e.x <= 1660 && e.y >= 400 && e.y <= 500) {
				sweepRight();
				actionPerformed = true;				
			}
			sweepLeftDown = false;
			sweepRightDown = false;
			source = null;
			result |= actionPerformed;
			actionPerformed = false;
		}
		return result;
	}

	private void deactivateFire() {
		if (OVERRIDE_LASER) {
			return;
		}
		if (alite.getLaserManager() != null) {
			alite.getLaserManager().setAutoFire(false);
		}		
	}
	
	private void toggleAutoFire() {
		if (OVERRIDE_LASER) {
			return;
		}
		if (alite.getLaserManager() != null) {
			alite.getLaserManager().setAutoFire(!alite.getLaserManager().isAutoFire());
		}
	}
	
	private void toggleCloakingDevice() {
		ButtonData cloak = buttons[CLOAKING_DEVICE];
		inGame.getShip().setCloaked(!inGame.getShip().isCloaked());
		if (inGame.getShip().isCloaked()) {
			cloak.yellow = true;
		} else {
			cloak.yellow = false;
		}
		inGame.setCloak(inGame.getShip().isCloaked());
	}
	
	private void updateMissileState() {
		if (OVERRIDE_MISSILE) {
			return;
		}
		ButtonData missile = buttons[MISSILE];
		if (missile == null) {
			return;
		}
		if (!missile.yellow && !missile.red) {
			missile.yellow = true;
			inGame.handleMissileIcons();
		} else if (missile.yellow) {
			missile.yellow = false;
			inGame.handleMissileIcons();
		} else if (missile.red) {
			missile.red = false;
			inGame.fireMissile();
		}
	}
	
	private void toggleECMJammer() {
		ButtonData ecmJammer = buttons[ECM_JAMMER];
		inGame.toggleECMJammer();
		if (inGame.isECMJammer()) {
			ecmJammer.yellow = true;
		} else {
			ecmJammer.yellow = false;
		}
	}

	private void engageEscapeCapsule() {
		if (inGame.isWitchSpace()) {
// TODO add computer voice file			
//			SoundManager.play(Assets.escapeCapsuleMalfunction);
			inGame.setMessage("Escape Capsule Malfunction");
			return;
		}
		alite.getCobra().removeEquipment(EquipmentStore.escapeCapsule);
		for (TradeGood g: TradeGoodStore.get().goods()) {
			alite.getCobra().removeTradeGood(g);	
		}		
		SoundManager.stop(Assets.energyLow);
		SoundManager.stop(Assets.criticalCondition);
		SoundManager.play(Assets.retroRocketsOrEscapeCapsuleFired);
		inGame.clearMessageRepetition();
		inGame.setPlayerControl(false);
		if (alite.getCurrentScreen() instanceof FlightScreen) {
			((FlightScreen) alite.getCurrentScreen()).setInformationScreen(null);
		}
		inGame.forceForwardView();
		inGame.killHud();			
		ship.setUpdater(new EscapeCapsuleUpdater(alite, inGame, ship, System.nanoTime()));
		alite.getPlayer().setCondition(Condition.DOCKED);
		// The police track record is identified by the ship's id,
		// so leaving it with an escape capsule can be abused to get a
		// fresh start.
		alite.getPlayer().setLegalValue(0);
	}

	private void engageECM() {
		ecmTraverser.reset();
		inGame.traverseObjects(ecmTraverser);
		inGame.reduceShipEnergy(1);
	}

	private void engageGalacticHyperspace() {
		boolean active = inGame.toggleHyperspaceCountdown(true);
		buttons[GAL_HYPERSPACE].yellow = active;
	}

	private void goToStatusView() {
		if (OVERRIDE_INFORMATION) {
			return;
		}
		if (alite.getCurrentScreen() instanceof FlightScreen) {
			StatusScreen screen = new StatusScreen(alite);		
			alite.getNavigationBar().setFlightMode(true);
			alite.getNavigationBar().setActiveIndex(2);
			screen.loadAssets();
			screen.activate();
			screen.resume();
			screen.update(0);
			alite.getGraphics().setClip(-1, -1, -1, -1);
			((FlightScreen) alite.getCurrentScreen()).setInformationScreen(screen);
		}		
	}
	
	private void engageHyperspace() {
		if (OVERRIDE_HYPERSPACE) {
			return;
		}
		boolean active = inGame.toggleHyperspaceCountdown(false);	
		buttons[HYPERSPACE].yellow = active;
	}

	private void engageTorusDrive() {
		if (OVERRIDE_TORUS) {
			return;
		}
		if (ship.getSpeed() < -600) {
			inGame.getSpawnManager().leaveTorus();
		} else {
			inGame.getSpawnManager().enterTorus();
			ship.setSpeed(-33400.0f);
			inGame.setPlayerControl(false);
		}
	}
	
	private void engageDockingComputer() {	
		inGame.toggleDockingComputer(true);
	}
	
	private void engageEnergyBomb() {
		alite.getCobra().removeEquipment(EquipmentStore.energyBomb);
		inGame.traverseObjects(energyBombTraverser);
	}
	
	private void engageRetroRockets() {
		alite.getCobra().setRetroRocketsUseCount(alite.getCobra().getRetroRocketsUseCount() - 1);		
		SoundManager.play(Assets.retroRocketsOrEscapeCapsuleFired);
		ship.setSpeed(RETRO_ROCKET_SPEED);
	}
	
	public Screen getNewScreen() {
		return newScreen;
	}
}