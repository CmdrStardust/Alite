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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.opengl.GLES11;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.InventoryItem;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.LegalStatus;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.opengl.HyperspaceScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ScoopCallback;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Adder;
import de.phbouillon.android.games.alite.screens.opengl.sprites.buttons.AliteButtons;

//This screen never needs to be serialized, as it is not part of the InGame state,
//also, all used inner classes (IMethodHook, etc.) will be reset upon state loading,
//hence they never need to be serialized, either.
@SuppressWarnings("serial")
public class TutAdvancedFlying extends TutorialScreen {
	private FlightScreen flight;
	private HyperspaceScreen hyperspace;
	private char [] savedGalaxySeed;
	private SystemData savedPresentSystem;
	private SystemData savedHyperspaceSystem;
	private List <Equipment> savedInstalledEquipment;
	private int savedFuel;
	private Laser [] savedLasers = new Laser[4];
	private boolean savedDisableTraders;
	private boolean savedDisableAttackers;
	private int savedMissiles;
	private long savedCredits;
	private int savedScore;
	private int savedLegalValue;
	private TutAdvancedFlying switchScreen = null;
	private long time = -1;
	private int savedMarketFluct;
	private Adder adder = null;
	private InventoryItem [] savedInventory;
	private LegalStatus savedLegalStatus;
	private int [] savedButtonConfiguration = new int[Settings.buttonPosition.length];
	
	public TutAdvancedFlying(final Alite alite, int lineIndex) {
		this(alite, lineIndex, null);
	}

	private TutAdvancedFlying(final Alite alite, int lineIndex, FlightScreen flight) {
		super(alite, true);
		
		this.flight = flight;
		AliteLog.d("TutAdvancedFlying", "Starting Advanced Flying: " + lineIndex);
		
		savedGalaxySeed = alite.getGenerator().getCurrentSeed();
		savedPresentSystem = alite.getPlayer().getCurrentSystem();
		savedHyperspaceSystem = alite.getPlayer().getHyperspaceSystem();		
		savedInstalledEquipment = new ArrayList<Equipment>();
		for (Equipment e: alite.getCobra().getInstalledEquipment()) {
			savedInstalledEquipment.add(e);
		}
		AliteLog.w("Setting installed Equipment [constructor]", "Number of Equipment items: " + savedInstalledEquipment.size());
		savedLasers[0] = alite.getCobra().getLaser(PlayerCobra.DIR_FRONT);
		savedLasers[1] = alite.getCobra().getLaser(PlayerCobra.DIR_RIGHT);
		savedLasers[2] = alite.getCobra().getLaser(PlayerCobra.DIR_REAR);
		savedLasers[3] = alite.getCobra().getLaser(PlayerCobra.DIR_LEFT);
		savedMarketFluct = alite.getPlayer().getMarket().getFluct();
		savedFuel = alite.getCobra().getFuel();
		savedDisableTraders = Settings.disableTraders;
		savedDisableAttackers = Settings.disableAttackers;
		savedMissiles = alite.getCobra().getMissiles();
		savedLegalStatus = alite.getPlayer().getLegalStatus();
		savedLegalValue = alite.getPlayer().getLegalValue();
		savedCredits = alite.getPlayer().getCash();
		savedScore = alite.getPlayer().getScore();
		System.arraycopy(Settings.buttonPosition, 0, savedButtonConfiguration, 0, Settings.buttonPosition.length);
		savedInventory = new InventoryItem[TradeGoodStore.get().goods().length];
		InventoryItem [] currentItems = alite.getCobra().getInventory();
		for (int i = 0; i < TradeGoodStore.get().goods().length; i++) {
			savedInventory[i] = new InventoryItem();
			savedInventory[i].set(currentItems[i].getWeight(), currentItems[i].getPrice());
			savedInventory[i].addUnpunished(currentItems[i].getUnpunished());
		}
	
		for (Equipment e: savedInstalledEquipment) {
			alite.getCobra().removeEquipment(e);
		}
		alite.getCobra().setLaser(PlayerCobra.DIR_FRONT, EquipmentStore.pulseLaser);
		alite.getCobra().setLaser(PlayerCobra.DIR_RIGHT, null);
		alite.getCobra().setLaser(PlayerCobra.DIR_REAR, null);
		alite.getCobra().setLaser(PlayerCobra.DIR_LEFT, null);
		alite.getGenerator().buildGalaxy(1);
		alite.getGenerator().setCurrentGalaxy(1);
		alite.getPlayer().setCurrentSystem(alite.getGenerator().getSystem(7)); // Lave
		alite.getPlayer().setHyperspaceSystem(alite.getGenerator().getSystem(129)); // Zaonce
		alite.getCobra().setFuel(70);
		alite.getPlayer().setLegalValue(0);
		for (int i = 0; i < Settings.buttonPosition.length; i++) {
			Settings.buttonPosition[i] = i;
		}
		
		initLine_00();
		initLine_01();
		initLine_02();
		initLine_03();
		initLine_04();
		initLine_05();
		initLine_06();
		initLine_07();
		initLine_08();
		initLine_09();
		initLine_10();
		initLine_11();
		initLine_12();
		initLine_13();
		initLine_14();
		initLine_15();
		initLine_16();
		initLine_17();
		initLine_18();
		initLine_19();
		initLine_20();
		initLine_21();
		initLine_22();
		initLine_23();
		initLine_24();
		initLine_25();
		
		currentLineIndex = lineIndex - 1;
		if (this.flight == null) {
			this.flight = new FlightScreen(alite, currentLineIndex == -1);
		}
	}
	
	private TutorialLine addTopLine(String text) {
		return addLine(7, text).setX(250).setWidth(1420).setY(20).setHeight(140);
	}
	
	private void initLine_00() {		
		addTopLine("Welcome back, Commander! A fair landing you produced " +
				"there. I'm proud of you!").setUpdateMethod(new IMethodHook() {				
					@Override
					public void execute(float deltaTime) {
						flight.getInGameManager().getShip().setSpeed(0);
						flight.getInGameManager().setPlayerControl(false);
					}
				});		
	}
	
	private void initLine_01() {
		addTopLine("You are now ready for more advanced controls, but we " +
				"need to be out in space for those.");
	}

	private void initLine_02() {
		final TutorialLine line =
			addTopLine("Therefore, when you activate the hyperspace, you will " +
				"reach a simulated space region a little off of Lave. Go " +
				"there, now. Activate the hyperspace drive.").
				setMustRetainEvents().addHighlight(
						makeHighlight(1710, 300, 200, 200)).
						setSkippable(false);
		
		line.setHeight(180).setUpdateMethod(new IMethodHook() {
			@Override
			public void execute(float deltaTime) {	
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				if (flight != null && !flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}				

				if (flight != null && flight.getInGameManager().getHyperspaceHook() == null) {
					flight.getInGameManager().setHyperspaceHook(new IMethodHook() {
						@Override
						public void execute(float deltaTime) {
							hyperspace = new HyperspaceScreen(alite, false);
							hyperspace.setNeedsSoundRestart();
							hideCloseButton = true;
							line.clearHighlights();
							line.setText("");
							line.setWidth(0);
							line.setHeight(0);
							hyperspace.activate();							
						}
					});
				}
				if (hyperspace != null && hyperspace.getFinishHook() == null) {
					hyperspace.setNeedsSoundRestart();
					hideCloseButton = true;
					line.clearHighlights();
					line.setText("");
					line.setWidth(0);
					line.setHeight(0);					
					hyperspace.setFinishHook(new IMethodHook() {								
						@Override
						public void execute(float deltaTime) {
							hyperspace.dispose();
							hyperspace = null;
							hideCloseButton = false;
							GLES11.glMatrixMode(GLES11.GL_TEXTURE);
							GLES11.glLoadIdentity();
							alite.getTextureManager().clear();
							switchScreen = new TutAdvancedFlying(alite, 3);
							switchScreen.savedGalaxySeed = new char[3];
							switchScreen.savedGalaxySeed[0] = savedGalaxySeed[0];
							switchScreen.savedGalaxySeed[1] = savedGalaxySeed[1];
							switchScreen.savedGalaxySeed[2] = savedGalaxySeed[2];
							switchScreen.savedPresentSystem = savedPresentSystem;
							switchScreen.savedHyperspaceSystem = savedHyperspaceSystem;
							switchScreen.savedInstalledEquipment = new ArrayList<Equipment>();
							for (int i = 0; i < savedInstalledEquipment.size(); i++) {								
								switchScreen.savedInstalledEquipment.add(savedInstalledEquipment.get(i));
							}
							switchScreen.savedFuel = savedFuel;
							for (int i = 0; i < 4; i++) {
								switchScreen.savedLasers[i] = savedLasers[i]; 
							}
							for (int i = 0; i < Settings.buttonPosition.length; i++) {
								switchScreen.savedButtonConfiguration[i] = savedButtonConfiguration[i];
							}
							switchScreen.savedDisableTraders = savedDisableTraders;
							switchScreen.savedDisableAttackers = savedDisableAttackers;
							switchScreen.savedMissiles = savedMissiles;
							switchScreen.savedCredits = savedCredits;
							switchScreen.savedScore = savedScore;
							switchScreen.savedLegalStatus = savedLegalStatus;
							switchScreen.savedInventory = new InventoryItem[savedInventory.length];
							for (int i = 0; i < switchScreen.savedInventory.length; i++) {
								switchScreen.savedInventory[i] = savedInventory[i];
							}

							line.setFinished();
						}
					});
				}
			}
		});	
	}
	
	private void initLine_03() {
		addTopLine("You have now reached a space region outside Lave, you " +
				"can see clearly that you are outside the safe zone.");
	}
	
	private void initLine_04() {
		final TutorialLine line =
		   addTopLine("Now, in order to reach Lave station, you have to fly " +
				"towards the planet. So bring the circle in the small radar " +
				"to your front. Remember: The circle is red once the planet " +
				"is in front of you.").setHeight(180).
				setSkippable(false).setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}				
				if (flight.getInGameManager().isTargetInCenter()) {
					SoundManager.play(Assets.identify);
					line.setFinished();
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				flight.getInGameManager().setPlayerControl(false);
				flight.getInGameManager().getShip().adjustSpeed(0);
				flight.getInGameManager().setNeedsSpeedAdjustment(true);
				flight.setHandleUI(false);
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
			}
		});						
	}			
	
	private void initLine_05() {
		final TutorialLine line = 
				addTopLine("Good. Now accelerate to maximum speed by " +
						"sliding your finger up.").setSkippable(false);
		line.setMustRetainEvents().setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}				
				if (alite.getCobra().getSpeed() <= -PlayerCobra.MAX_SPEED) {
					line.setFinished();
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				flight.getInGameManager().setPlayerControl(false);
				flight.setHandleUI(false);
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
			}
		});	
	}
	
	private void initLine_06() {
		addTopLine("Ok. If you traveled at this speed, it would take " +
				"forever to reach Lave, but did you notice that a new " +
				"button appeared once you reached maximum speed?");		
	}
	
	private void initLine_07() {
		addTopLine("This is the torus drive button and it will allow you to " +
				"utilize the engine boosters.").addHighlight(
						makeHighlight(1560, 150, 200, 200));		
	}
	
	private void initLine_08() { 
		final TutorialLine line =
			addTopLine("It only works, however, if you are already flying " +
					"with maximum speed and no other ships are in the " +
					"vicinity. Try it. Activate the torus drive.").
					setMustRetainEvents().setSkippable(false).
					addHighlight(makeHighlight(1560, 150, 200, 200));
		
		line.setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				AliteButtons.OVERRIDE_TORUS = false;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}								
				if (alite.getCobra().getSpeed() < -PlayerCobra.TORUS_TEST_SPEED) {
					if (time == -1) {
						time = System.nanoTime();
					} else {
						if (System.nanoTime() - time > 5000000000l) {
							flight.getInGameManager().getSpawnManager().leaveTorus();
							line.setFinished();
						}
					}
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				flight.getInGameManager().setPlayerControl(false);
				flight.setHandleUI(false);
				flight.getInGameManager().getShip().adjustSpeed(0);
				flight.getInGameManager().setNeedsSpeedAdjustment(true);				
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
				AliteButtons.OVERRIDE_TORUS = false;
			}
		});		
	}
	
	private void initLine_09() {
		addTopLine("Wow... That was fast, wasn't it? Why did we stop?");
	}
	
	private void initLine_10() {
		addTopLine("Usually the torus drive is interrupted, if you are too " +
				"close to the planet or the star, or an enemy ship appears " +
				"waiting to get a shot at you.");
	}
	
	private void initLine_11() {
		addTopLine("In this simulation, I killed the torus drive.");
	}
	
	private void initLine_12() {
		addTopLine("And now, you're about to make a kill. An enemy ship " +
				"will appear soon, and I want you to destroy it.");
	}
	
	private void initLine_13() {
		addTopLine("It will attack you, but it is a relatively harmless and " +
				"slow Adder. There is a slim chance, though, that it might " +
				"destroy you.");		
	}

	private void initLine_14() {
		addTopLine("In that case: You pay the drinks tonight and we'll try " +
				"again tomorrow.");
	}
	
	private void initLine_15() {
		addTopLine("Ready? Good. Go!");		
	}

	private void initLine_16() {
		final TutorialLine line = addEmptyLine().setMustRetainEvents().
				setSkippable(false);
		
		line.setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
				AliteButtons.OVERRIDE_TORUS = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}	
				if (adder == null) {
					adder = (Adder) flight.findObjectByName("Adder");
					if (adder == null) {
						SoundManager.play(Assets.com_conditionRed);
						flight.getInGameManager().repeatMessage("Condition Red!", 3);
						Vector3f spawnPosition = flight.getInGameManager().getSpawnManager().spawnObject();
						adder = new Adder(alite);
						adder.setAggression(4);
						adder.setMissileCount(0);
						adder.setCargoCanisterCount(2);
						flight.getInGameManager().getSpawnManager().spawnEnemyAndAttackPlayer(adder, 0, spawnPosition, true);
						adder.addDestructionCallback(new DestructionCallback() {
							@Override
							public void onDestruction() {							
								line.setFinished();
							}

							@Override
							public int getId() {
								return 3;
							}
						});						
					}
				}
				if (adder.getDestructionCallbacks().isEmpty()) {
					AliteLog.e("Adder DC is empty", "Adder DC is empty");
					adder.addDestructionCallback(new DestructionCallback() {
						@Override
						public void onDestruction() {							
							line.setFinished();
						}

						@Override
						public int getId() {
							return 4;
						}
					});						
				}				
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				flight.getInGameManager().setPlayerControl(false);
				flight.setHandleUI(false);
				flight.getInGameManager().getLaserManager().setAutoFire(false);
				flight.getInGameManager().getShip().adjustSpeed(0);
				flight.getInGameManager().setNeedsSpeedAdjustment(true);				
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
				AliteButtons.OVERRIDE_TORUS = false;
			}
		});		
	}

	private void initLine_17() {
		addTopLine("Hey not bad... You destroyed the Adder. And it released " +
				"a couple of Cargo Canisters. You can see them on the radar " +
				"as purple bars.");
	}

	private void initLine_18() {
		addTopLine("Remember: If you tap on a target on your screen, it " +
				"will be identified by the computer.");		
	}
	
	private void initLine_19() {
		addTopLine("Your simulated Cobra is equipped with a fuel scoop, so " +
				"you may collect those Cargo Canisters and their contents " +
				"are then added to your inventory.").setHeight(180);
	}

	private void initLine_20() {
		addTopLine("To successfully do that, ram a Cargo Canister with " +
				"the lower part of your Cobra, where the Fuel Scoop is " +
				"installed. Go ahead, now.").setHeight(180);
	}
	
	private void initLine_21() {
		final TutorialLine line =
				addEmptyLine().setSkippable(false).setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				AliteButtons.OVERRIDE_TORUS = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}
				if (!alite.getCobra().isEquipmentInstalled(EquipmentStore.fuelScoop)) {
					alite.getCobra().addEquipment(EquipmentStore.fuelScoop);
				}
				if (flight.getInGameManager().getScoopCallback() == null) {				
					flight.getInGameManager().addScoopCallback(new ScoopCallback() {						
						@Override
						public void scooped(SpaceObject scoopedObject) {							
							line.setFinished();
							currentLineIndex++;
						}
						
						@Override
						public void rammed(SpaceObject rammedObject) {
							line.setFinished();
						}
					});
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				flight.getInGameManager().setPlayerControl(false);
				flight.getInGameManager().getLaserManager().setAutoFire(false);
				flight.getInGameManager().getShip().adjustSpeed(0);
				flight.getInGameManager().setNeedsSpeedAdjustment(true);
				flight.setHandleUI(false);
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
				AliteButtons.OVERRIDE_TORUS = false;
			}
		});		

	}

	private void initLine_22() {
		final TutorialLine line = 
			addTopLine("Oh no. You have destroyed the Cargo Canister. " +
					"Remember: You must touch it with the lower part of " +
					"your Cobra.").setSkippable(false);
		
		line.setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (line.getCurrentSpeechIndex() > 0) {
					line.setFinished();
					currentLineIndex++;
				}
			}
		});
	}

	private void initLine_23() {
		addTopLine("Excellent flying, Commander.");
	}
	
	private void initLine_24() {
		addTopLine("This is all I can teach you, really. As a last training " +
				"exercise, dock at Lave station. You are on your own. Good " +
				"luck out there, Commander.");
	}

	private void initLine_25() {
		final TutorialLine line = addEmptyLine().setSkippable(false).
				setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				AliteButtons.OVERRIDE_TORUS = false;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}
				if (flight.getInGameManager().getPostDockingHook() == null) {
					flight.getInGameManager().setPostDockingHook(new IMethodHook() {						
						@Override
						public void execute(float deltaTime) {
							AliteButtons.OVERRIDE_HYPERSPACE = false;
							AliteButtons.OVERRIDE_INFORMATION = false;
							AliteButtons.OVERRIDE_MISSILE = false;
							AliteButtons.OVERRIDE_LASER = false;
							AliteButtons.OVERRIDE_TORUS = false;
							dispose();
						}
					});									
				}
				if (flight.getInGameManager().getActualPostDockingScreen() == null) {
					flight.getInGameManager().setPostDockingScreen(new TutorialSelectionScreen(alite));					
				}
			}
		});				
	}
	
	@Override
	public void activate() {
		super.activate();
		for (int i = 0; i < Settings.buttonPosition.length; i++) {
			Settings.buttonPosition[i] = i;
		}
		for (Equipment e: savedInstalledEquipment) {
			alite.getCobra().removeEquipment(e);
		}
		alite.getCobra().setLaser(PlayerCobra.DIR_FRONT, EquipmentStore.pulseLaser);
		alite.getCobra().setLaser(PlayerCobra.DIR_RIGHT, null);
		alite.getCobra().setLaser(PlayerCobra.DIR_REAR, null);
		alite.getCobra().setLaser(PlayerCobra.DIR_LEFT, null);
		alite.getGenerator().buildGalaxy(1);
		alite.getGenerator().setCurrentGalaxy(1);
		alite.getPlayer().setCurrentSystem(alite.getGenerator().getSystem(7)); // Lave
		alite.getPlayer().setHyperspaceSystem(alite.getGenerator().getSystem(129)); // Zaonce
		alite.getCobra().setFuel(70);
		alite.getCobra().setMissiles(4);
		alite.getCobra().clearInventory();
		
		if (flight != null) {
			flight.loadAssets();
			flight.activate();
			flight.getInGameManager().setPlayerControl(false);
			flight.getInGameManager().getShip().setSpeed(0);
			flight.setPause(false);
		}
		if (hyperspace != null) {
			hyperspace.loadAssets();
			hyperspace.activate();
		}
		
		Settings.disableTraders = true;
		Settings.disableAttackers = true;
		ObjectSpawnManager.SHUTTLES_ENABLED = false;
		ObjectSpawnManager.ASTEROIDS_ENABLED = false;
		ObjectSpawnManager.CONDITION_RED_OBJECTS_ENABLED = false;
		ObjectSpawnManager.THARGOIDS_ENABLED = false;
		ObjectSpawnManager.THARGONS_ENABLED = false;
		ObjectSpawnManager.TRADERS_ENABLED = false;
		ObjectSpawnManager.VIPERS_ENABLED = false;		
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {		
		try {
			boolean flight = dis.readBoolean();
			boolean hyper = dis.readBoolean();
			FlightScreen fs = null;
			HyperspaceScreen hs = null;
			if (flight) {
				fs = FlightScreen.createScreen(alite, dis);
			}
			if (hyper) {
				hs = HyperspaceScreen.createScreen(alite, dis);
			}
			int lineIndex = dis.readInt();
			TutAdvancedFlying ta = new TutAdvancedFlying(alite, lineIndex, fs);
			ta.hyperspace = hs;
			ta.savedGalaxySeed = new char[3];
			ta.savedGalaxySeed[0] = dis.readChar();
			ta.savedGalaxySeed[1] = dis.readChar();
			ta.savedGalaxySeed[2] = dis.readChar();
			alite.getGenerator().buildGalaxy(ta.savedGalaxySeed[0], ta.savedGalaxySeed[1], ta.savedGalaxySeed[2]);
			int systemIndex = dis.readInt();
			int hyperspaceIndex = dis.readInt();
			ta.savedPresentSystem = systemIndex == -1 ? null : alite.getGenerator().getSystem(systemIndex);
			ta.savedHyperspaceSystem = hyperspaceIndex == -1 ? null : alite.getGenerator().getSystem(hyperspaceIndex);
			ta.savedInstalledEquipment = new ArrayList<Equipment>();
			int numEquip = dis.readInt();
			for (int i = 0; i < numEquip; i++) {
				ta.savedInstalledEquipment.add(EquipmentStore.fromInt(dis.readByte()));
			}
			AliteLog.w("Setting installed Equipment [initialize]", "Number of Equipment items: " + ta.savedInstalledEquipment.size());
			ta.savedFuel = dis.readInt();
			for (int i = 0; i < 4; i++) {
				int laser = dis.readInt();
				ta.savedLasers[i] = laser < 0 ? null : (Laser) EquipmentStore.fromInt(laser); 
			}
			for (int i = 0; i < Settings.buttonPosition.length; i++) {
				ta.savedButtonConfiguration[i] = dis.readInt();
			}
			ta.savedDisableTraders = dis.readBoolean();
			ta.savedDisableAttackers = dis.readBoolean();
			ta.savedMissiles = dis.readInt();
			ta.savedCredits = dis.readLong();
			ta.savedScore = dis.readInt();
			ta.savedLegalStatus = LegalStatus.values()[dis.readInt()];
			ta.savedLegalValue = dis.readInt();
			ta.savedInventory = new InventoryItem[dis.readInt()];
			for (int i = 0; i < ta.savedInventory.length; i++) {
				ta.savedInventory[i] = new InventoryItem();
				ta.savedInventory[i].set(Weight.grams(dis.readLong()), dis.readLong());
				ta.savedInventory[i].addUnpunished(Weight.grams(dis.readLong()));
			}
			ta.time = dis.readLong();
			ta.savedMarketFluct = dis.readInt();
			ta.adder = (Adder) ta.flight.findObjectByName("Adder");
			if (ta.adder != null) {
				ta.adder.setSaving(false);
			}

			alite.setScreen(ta);
		} catch (Exception e) {
			AliteLog.e("Tutorial Advanced Flying Screen Initialize", "Error in initializer.", e);
			return false;			
		}		
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}		
		if (adder != null) {
			adder.setSaving(true);
		}
		dos.writeBoolean(flight != null);
		dos.writeBoolean(hyperspace != null);
		if (flight != null) {
			flight.saveScreenState(dos);
		}
		if (hyperspace != null) {
			hyperspace.saveScreenState(dos);
		}
		dos.writeInt(currentLineIndex); // 1 is subtracted in the read object code, because it has to be passed to the constructor.		
		dos.writeChar(savedGalaxySeed[0]);
		dos.writeChar(savedGalaxySeed[1]);
		dos.writeChar(savedGalaxySeed[2]);
		dos.writeInt(savedPresentSystem == null ? -1 : savedPresentSystem.getIndex());
		dos.writeInt(savedHyperspaceSystem == null ? -1 : savedHyperspaceSystem.getIndex());
		dos.writeInt(savedInstalledEquipment.size());
		for (Equipment e: savedInstalledEquipment) {
			dos.writeByte(EquipmentStore.ordinal(e));
		}
		dos.writeInt(savedFuel);
		for (int i = 0; i < 4; i++) {
			dos.writeInt(savedLasers[i] == null ? -1 : EquipmentStore.ordinal(savedLasers[i]));
		}
		for (int i = 0; i < Settings.buttonPosition.length; i++) {
			dos.writeInt(savedButtonConfiguration[i]);
		}	
		dos.writeBoolean(savedDisableTraders);
		dos.writeBoolean(savedDisableAttackers);
		dos.writeInt(savedMissiles);
		dos.writeLong(savedCredits);
		dos.writeInt(savedScore);
		dos.writeInt(savedLegalStatus.ordinal());
		dos.writeInt(savedLegalValue);
		dos.writeInt(savedInventory.length);
		for (InventoryItem w: savedInventory) {
			dos.writeLong(w.getWeight().getWeightInGrams());
			dos.writeLong(w.getPrice());
			dos.writeLong(w.getUnpunished().getWeightInGrams());
		}
		dos.writeLong(time);
		dos.writeInt(savedMarketFluct);
	}

	@Override
	public void loadAssets() {
		super.loadAssets();
		flight.loadAssets();
	}
	
	@Override 
	public void renderGlPart(float deltaTime, final Rect visibleArea) {
		if (hyperspace != null) {
			hyperspace.initializeGl(visibleArea);
			hyperspace.present(deltaTime);
			if (flight != null) {
				flight.dispose();
				flight = null;
			}
		}
		if (flight != null) {
			flight.initializeGl(visibleArea);
			flight.present(deltaTime);
		} 
	}
	
	@Override
	public void doPresent(float deltaTime) {
		renderText();
	}
		
	public void doUpdate(float deltaTime) {
		if (hyperspace != null) {
			hyperspace.update(deltaTime);
			if (flight != null) {
				flight.dispose();
				flight = null;
			}
		}
		if (flight != null) {
			if (!flight.getInGameManager().isDockingComputerActive()) {
				alite.getCobra().setRotation(0, 0);
			}
			flight.update(deltaTime);		
		} 
		if (switchScreen != null) {
			newScreen = switchScreen;
			performScreenChange();
			postScreenChange();
		}
	}
		
	@Override
	public void dispose() {
		if (hyperspace != null) {
			hyperspace.dispose();
			hyperspace = null;
		} 
		if (flight != null) {
			if (!flight.isDisposed()) {
				flight.dispose();
			}
			flight = null;
		} 
		ObjectSpawnManager.SHUTTLES_ENABLED = true;
		ObjectSpawnManager.ASTEROIDS_ENABLED = true;
		ObjectSpawnManager.CONDITION_RED_OBJECTS_ENABLED = true;
		ObjectSpawnManager.THARGOIDS_ENABLED = true;
		ObjectSpawnManager.THARGONS_ENABLED = true;
		ObjectSpawnManager.TRADERS_ENABLED = true;
		ObjectSpawnManager.VIPERS_ENABLED = true;
		
		AliteLog.w("Disposing Advanced Flying", "Disposing Advanced Flying");
		alite.getCobra().removeEquipment(EquipmentStore.fuelScoop);
		AliteLog.w("Setting Equipment", "Saved Equipment: " + savedInstalledEquipment.size());
		for (Equipment e: savedInstalledEquipment) {
			alite.getCobra().addEquipment(e);
			AliteLog.w("Setting Equipment", "Saved Equipment: " + e.getName());
		}
		alite.getCobra().setLaser(PlayerCobra.DIR_FRONT, savedLasers[0]);
		alite.getCobra().setLaser(PlayerCobra.DIR_RIGHT, savedLasers[1]);
		alite.getCobra().setLaser(PlayerCobra.DIR_REAR, savedLasers[2]);
		alite.getCobra().setLaser(PlayerCobra.DIR_LEFT, savedLasers[3]);
		alite.getGenerator().buildGalaxy(savedGalaxySeed[0], savedGalaxySeed[1], savedGalaxySeed[2]);
		alite.getGenerator().setCurrentGalaxy(alite.getGenerator().getCurrentGalaxyFromSeed());
		alite.getPlayer().setCurrentSystem(savedPresentSystem); 
		alite.getPlayer().setHyperspaceSystem(savedHyperspaceSystem);
		alite.getPlayer().getMarket().setFluct(savedMarketFluct);
		alite.getPlayer().getMarket().generate();
		alite.getCobra().setFuel(savedFuel);
		alite.getCobra().setMissiles(savedMissiles);
		alite.getCobra().setInventory(savedInventory);
		alite.getPlayer().setLegalStatus(savedLegalStatus);
		alite.getPlayer().setLegalValue(savedLegalValue);
		alite.getPlayer().setCash(savedCredits);
		alite.getPlayer().setScore(savedScore);
		Settings.disableAttackers = savedDisableAttackers;
		Settings.disableTraders = savedDisableTraders;
		for (int i = 0; i < Settings.buttonPosition.length; i++) {
			Settings.buttonPosition[i] = savedButtonConfiguration[i];
		}

		super.dispose();
	}

	@Override
	public int getScreenCode() {
		return ScreenCodes.TUT_ADVANCED_FLYING_SCREEN;
	}
}
