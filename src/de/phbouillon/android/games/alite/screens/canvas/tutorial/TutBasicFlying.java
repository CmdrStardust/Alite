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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.ShipControl;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.model.LegalStatus;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectSpawnManager;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Buoy;
import de.phbouillon.android.games.alite.screens.opengl.sprites.buttons.AliteButtons;

//This screen never needs to be serialized, as it is not part of the InGame state,
//also, all used inner classes (IMethodHook, etc.) will be reset upon state loading,
//hence they never need to be serialized, either.
@SuppressWarnings("serial")
public class TutBasicFlying extends TutorialScreen {
	private FlightScreen flight;
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
	private LegalStatus savedLegalStatus;
	private int savedLegalValue;
	private boolean resetShipPosition = true;
	private Buoy target1;
	private Buoy target2;
	private Buoy dockingBuoy;
	private int [] savedButtonConfiguration = new int[Settings.buttonPosition.length];
	private int savedMarketFluct;
	
	public TutBasicFlying(final Alite alite) {
		this(alite, null);
	}
	
	private TutBasicFlying(final Alite alite, FlightScreen flight) {
		super(alite, true);
		
		this.flight = flight;
		savedGalaxySeed = alite.getGenerator().getCurrentSeed();
		savedPresentSystem = alite.getPlayer().getCurrentSystem();
		savedHyperspaceSystem = alite.getPlayer().getHyperspaceSystem();
		savedInstalledEquipment = new ArrayList<Equipment>();
		for (Equipment e: alite.getCobra().getInstalledEquipment()) {
			savedInstalledEquipment.add(e);
		}
		savedLasers[0] = alite.getCobra().getLaser(PlayerCobra.DIR_FRONT);
		savedLasers[1] = alite.getCobra().getLaser(PlayerCobra.DIR_RIGHT);
		savedLasers[2] = alite.getCobra().getLaser(PlayerCobra.DIR_REAR);
		savedLasers[3] = alite.getCobra().getLaser(PlayerCobra.DIR_LEFT);
		savedFuel = alite.getCobra().getFuel();
		savedDisableTraders = Settings.disableTraders;
		savedDisableAttackers = Settings.disableAttackers;
		savedMissiles = alite.getCobra().getMissiles();
		savedCredits = alite.getPlayer().getCash();
		savedScore = alite.getPlayer().getScore();
		savedLegalStatus = alite.getPlayer().getLegalStatus();
		savedLegalValue = alite.getPlayer().getLegalValue();
		savedMarketFluct = alite.getPlayer().getMarket().getFluct();
		
		ObjectSpawnManager.SHUTTLES_ENABLED = false;
		ObjectSpawnManager.ASTEROIDS_ENABLED = false;
		ObjectSpawnManager.CONDITION_RED_OBJECTS_ENABLED = false;
		ObjectSpawnManager.THARGOIDS_ENABLED = false;
		ObjectSpawnManager.THARGONS_ENABLED = false;
		ObjectSpawnManager.TRADERS_ENABLED = false;
		ObjectSpawnManager.VIPERS_ENABLED = false;
		
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
		alite.getPlayer().setLegalValue(0);
		alite.getCobra().setFuel(70);
		System.arraycopy(Settings.buttonPosition, 0, savedButtonConfiguration, 0, Settings.buttonPosition.length);
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
		initLine_26();
		initLine_27();
		initLine_28();
		initLine_29();
		initLine_30();
		initLine_31();
		initLine_32();
		initLine_33();
		initLine_34();		
	}
	
	private TutorialLine addTopLine(String text) {
		return addLine(6, text).setX(250).setWidth(1420).setY(20).setHeight(140);
	}
	
	private void initLine_00() {		
		addTopLine("Good Morning kiddo, we'll do some real flying today, so " +
				"you'd better pay attention...").
				setUpdateMethod(new IMethodHook() {				
					@Override
					public void execute(float deltaTime) {
						flight.getInGameManager().getShip().setSpeed(0);
						flight.getInGameManager().setPlayerControl(false);
					}
				});

		if (flight == null) {
			flight = new FlightScreen(alite, true);
		}
	}
	
	private void initLine_01() {
		addTopLine("The horizontal bars on the radar tell you the direction " +
				"in which an object is, vertical bars indicate the " +
				"difference in altitude relative to your ship.").
				setHeight(180);
	}

	private void initLine_02() {
		final TutorialLine line = 
				addTopLine("Too difficult for you, rookie? Let me show you:");
		
		line.setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Vector3f position = new Vector3f(0, 0, 0);
				Vector3f vec = new Vector3f(0, 0, 0);
				
				target1 = new Buoy(alite);
				flight.getInGameManager().getShip().getPosition().copy(position);
				flight.getInGameManager().getShip().getRightVector().copy(vec);
				vec.scale(-8000.0f);
				position.add(vec);
				flight.getInGameManager().getShip().getForwardVector().copy(vec);
				vec.scale(-11000.0f);
				position.add(vec);
				flight.getInGameManager().getShip().getUpVector().copy(vec);
				vec.scale(17000.0f);
				position.add(vec);
				target1.setPosition(position);
				target1.scale(6.0f);
				target1.setName("Yellow Target");
				flight.getInGameManager().addObject(target1);
				
				target2 = new Buoy(alite);
				flight.getInGameManager().getShip().getPosition().copy(position);
				flight.getInGameManager().getShip().getRightVector().copy(vec);
				vec.scale(8000.0f);
				position.add(vec);
				flight.getInGameManager().getShip().getForwardVector().copy(vec);
				vec.scale(14000.0f);
				position.add(vec);
				flight.getInGameManager().getShip().getUpVector().copy(vec);
				vec.scale(-10000.0f);
				position.add(vec);
				target2.setPosition(position);
				target2.setHudColor(0x00, 0x00, 0xef);
				target2.scale(6.0f);
				target2.setName("Blue Target");
				flight.getInGameManager().addObject(target2);
			}
		});
	}
	
	private void initLine_03() {
		addTopLine("See those two bars? The yellow bar is a target which is " +
				"in front of you, to your left, and above you.");
	}
	
	private void initLine_04() {
		addTopLine("The blue bar is a target behind you, on your right, and " +
				"below you.");
	}
	
	private void initLine_05() {
		addTopLine("But wait, there's more. Did you notice the small radar " +
				"to the right and above your main radar?").addHighlight(
						makeHighlight(1284, 640, 128, 128));
	}
	
	private void initLine_06() {
		addTopLine("Inside the safe zone, the small radar shows the " +
				"position of the space station.");		
	}
	
	private void initLine_07() {
		addTopLine("If the dot in the radar is red, the station is in front " +
				"of you, if it is green, it is behind you.");		
	}
	
	private void initLine_08() { 
		addTopLine("Outside the safe zone, the dot represents the planet: " +
				"Red means the planet is in front of you, green means, the " +
				"planet is behind you.").setHeight(180);		
	}
	
	private void initLine_09() {
		addTopLine("But why don't you try to get the blue target in front " +
				"of you. You will also see how the smaller radar will " +
				"change.");
	}
	
	private void initLine_10() {
		addLine(6, "I will let you control the pitch and roll of your " +
				"Cobra. To control pitch, "
			+ (Settings.controlMode == ShipControl.ACCELEROMETER ||
			   Settings.controlMode == ShipControl.ALTERNATIVE_ACCELEROMETER ?
				"tilt your screen towards you to go up and away from " +
				"you to go down." :
			   Settings.controlMode == ShipControl.CONTROL_PAD ?
				"tap the upper part of the control pad to go down and " +
				"the lower part to go up." :
				"tap the arrow pointing up to go down and the arrow " +
				"pointing down to go up."),
				Settings.controlMode == ShipControl.ACCELEROMETER ||
				Settings.controlMode == ShipControl.ALTERNATIVE_ACCELEROMETER ? "a" :
				Settings.controlMode == ShipControl.CONTROL_PAD ? "b" : "c").
			setX(250).setWidth(1420).setY(20).setHeight(220);
	}
	
	private void initLine_11() {
		addLine(6, "To control roll, "
			+ (Settings.controlMode == ShipControl.ACCELEROMETER ||
			   Settings.controlMode == ShipControl.ALTERNATIVE_ACCELEROMETER ?
				"rotate the left side of your screen away from you to roll " +
				"left and rotate it towards you to roll right." :
			   Settings.controlMode == ShipControl.CONTROL_PAD ?
			    "tap the left part of the control pad to roll left and the " +
			    "right part to roll right." :
				"tap the arrow pointing left to roll left and the arrow " +
				"pointing right to roll right."),
				Settings.controlMode == ShipControl.ACCELEROMETER ||
				Settings.controlMode == ShipControl.ALTERNATIVE_ACCELEROMETER ? "a" :
				Settings.controlMode == ShipControl.CONTROL_PAD ? "b" : "c").
			setX(250).setWidth(1420).setY(20).setHeight(220);
	}
	
	private void initLine_12() {
		final TutorialLine line = addTopLine(
				"Now, are you ready to take command of the Cobra? Try to " +
				"bring the blue target into the crosshairs in front of you.").
				setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				InGameManager.OVERRIDE_SPEED = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
				}
				if (flight.getInGameManager().getLaserManager().isUnderCross(
						target2,
						flight.getInGameManager().getShip(),
						flight.getInGameManager().getViewDirection())) {
					SoundManager.play(Assets.identify);
					line.setFinished();
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				flight.getInGameManager().setPlayerControl(false);
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
				InGameManager.OVERRIDE_SPEED = false;
			}
		});
	}
	
	private void initLine_13() {
		addTopLine("Not bad for a rookie. You sure took your time, you will " +
				"have to master the controls in order to survive, wet-nose.");		
	}

	private void initLine_14() {
		final TutorialLine line = addTopLine(
				"Why don't you get the yellow target in front of you?").setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				InGameManager.OVERRIDE_SPEED = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
				}
				if (flight.getInGameManager().getLaserManager().isUnderCross(
						target1,
						flight.getInGameManager().getShip(),
						flight.getInGameManager().getViewDirection())) {
					SoundManager.play(Assets.identify);
					line.setFinished();
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;
				InGameManager.OVERRIDE_SPEED = false;
				flight.getInGameManager().setPlayerControl(false);
			}
		});
	}

	private void initLine_15() {
		addTopLine("Ok, I think you're getting the hang of this. Now, let's " +
				"approach the yellow target, ok?");
	}
	
	private void initLine_16() {
		addTopLine("To increase speed, slide your finger up on the screen. " +
				"To reduce speed, slide it down.");		
	}

	private void initLine_17() {
		addTopLine("If you tap on the target, the computer will identify " +
				"the target for you.");		
	}

	private void initLine_18() {
		final TutorialLine line = addTopLine(
				"Go ahead, fly a little closer.").setMustRetainEvents();		
		
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
				if (target1.getPosition().distanceSq(flight.getInGameManager().getShip().getPosition()) < 360000000l) {
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

	private void initLine_19() {
		addTopLine("Good. That's close enough. Now, for a little target " +
				"practice:");		
	}
	
	private void initLine_20() {
		addTopLine("We are using simulated lasers, so don't worry, you " +
				"cannot harm anyone.");
	}

	private void initLine_21() {
		final TutorialLine line = 
			addLine(6, "Do try to destroy the target in front of you, " +
					"though. It will be affected by the simulated laser. To " +
					"engage the laser, press the button on the upper left.").
				setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = false;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}
				if (target1.getDestructionCallbacks().isEmpty()) {
					target1.addDestructionCallback(new DestructionCallback() {
						@Override
						public void onDestruction() {
							line.setFinished();
						}

						@Override
						public int getId() {
							return 5;
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
			}
		});		

	}

	private void initLine_22() {
		addTopLine("Right on, Commander!");		
	}

	private void initLine_23() {
		final TutorialLine line = 
			addTopLine("Now for the remaining target: Approach the blue " +
					"target.").setMustRetainEvents();
		
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
				if (target2.getPosition().distanceSq(flight.getInGameManager().getShip().getPosition()) < 360000000l) {
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

	private void initLine_24() {
		final TutorialLine line = 
			addLine(6, "Ok, you're close enough, now tap the missile button " +
					"on the left once to target the missile.").
				setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}				
				if (alite.getCobra().isMissileTargetting()) {
					line.setFinished();
				} else if (alite.getCobra().isMissileLocked()) {
					line.setFinished();
					currentLineIndex++;
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

	private void initLine_25() {
		final TutorialLine line =
			addTopLine("Get the blue target into your crosshairs, so that " +
					"the missile indicator turns red.").setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = true;					
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}
				if (flight.getInGameManager().getLaserManager().isUnderCross(
						target2,
						flight.getInGameManager().getShip(),
						flight.getInGameManager().getViewDirection())) {
					SoundManager.play(Assets.identify);
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

	private void initLine_26() {
		final TutorialLine line = 
			addLine(6, "And then tap the missile button again.").
			setHeight(180).setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {				
			private void writeObject(ObjectOutputStream out)
		            throws IOException {
				try {
					AliteLog.e("Writing", "Writing");
					out.defaultWriteObject();
					AliteLog.e("DONEWriting", "DONE Writing");
				} catch(IOException e) {
					AliteLog.e("PersistenceException", "WriteObject!!", e);
					throw(e);
				}
		    }
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}
				// TARGET2 is being serialized!!
				// So, the destruction callback is being serialized, _too_ (!).
				// The destruction callback must have the reference to the tutorial line,
				// which is not serializable. This causes problems, of course.
				// Solution: Delete the destruction callbacks before calling write object....   
				if (target2.getDestructionCallbacks().isEmpty()) {
					target2.addDestructionCallback(new DestructionCallback() {
						transient TutorialLine tLine = line;
						
						private void writeObject(ObjectOutputStream out)
					            throws IOException {
							try {
								AliteLog.e("Destruction Callback", "Destruction Callback");
								out.defaultWriteObject();
								AliteLog.e("DONEWriting", "DONE Writing Destruction Callback");
							} catch(IOException e) {
								AliteLog.e("PersistenceException", "WriteObject Destruction Callback!!", e);
								throw(e);
							}
					    }
						
						private void readObject(ObjectInputStream in) throws IOException {
							try {
								in.defaultReadObject();
								tLine = line;
							} catch (ClassNotFoundException e) {
								AliteLog.e("Error in Initializer", e.getMessage(), e);
							}
						}
						
						@Override
						public void onDestruction() {
							tLine.setFinished();
						}

						@Override
						public int getId() {
							return 6;
						}
					});					
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

	private void initLine_27() {
		addTopLine("Good shooting, Commander!");		
	}

	private void initLine_28() {
		addTopLine("Now let's return to the station, shall we?");				
	}
	
	private void initLine_29() {
		addTopLine("Remember, the docking bay of a Space Station is always " +
				"oriented towards the planet.");
	}
	
	private void initLine_30() {
		addTopLine("So pick a point between the planet and the station and " +
				"approach the station from there.");
	}
	
	private void initLine_31() {
		final TutorialLine line = addTopLine("Let me help you: See the new " +
				"red target? Approach it.").setMustRetainEvents();
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (dockingBuoy == null) {
					dockingBuoy = (Buoy) flight.findObjectByName("Docking Buoy");
					if (dockingBuoy == null) {
						InGameManager man = flight.getInGameManager();
						dockingBuoy = new Buoy(alite);
						Vector3f position = new Vector3f(0, 0, 0);
						man.getPlanet().getPosition().sub(man.getStation().getPosition(), position);
						position.normalize();	
						position.scale(6000);
						position.add(man.getStation().getPosition());

						dockingBuoy.setPosition(position);
						dockingBuoy.setHudColor(0xef, 0x00, 0x00);
						dockingBuoy.setName("Docking Buoy");
						flight.getInGameManager().addObject(dockingBuoy);
					}
				}
				
				AliteButtons.OVERRIDE_HYPERSPACE = true;
				AliteButtons.OVERRIDE_INFORMATION = true;
				AliteButtons.OVERRIDE_MISSILE = true;
				AliteButtons.OVERRIDE_LASER = true;
				if (!flight.getInGameManager().isPlayerControl()) {
					flight.getInGameManager().calibrate();
					flight.getInGameManager().setPlayerControl(true);
					flight.setHandleUI(true);
				}				
				if (dockingBuoy.getPosition().distanceSq(flight.getInGameManager().getShip().getPosition()) < 40000) {
					SoundManager.play(Assets.identify);
					dockingBuoy.setRemove(true);
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

	private void initLine_32() {
		final TutorialLine line = 
				addTopLine("Good. Now turn around so that you face the " +
						"station.").setMustRetainEvents();

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
				if (flight.getInGameManager().getLaserManager().isUnderCross(
						(SpaceObject) flight.getInGameManager().getStation(),
						flight.getInGameManager().getShip(),
						flight.getInGameManager().getViewDirection())) {
					line.setFinished();
				}
			}
		}).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				AliteButtons.OVERRIDE_HYPERSPACE = false;
				AliteButtons.OVERRIDE_INFORMATION = false;
				AliteButtons.OVERRIDE_MISSILE = false;
				AliteButtons.OVERRIDE_LASER = false;				
				flight.getInGameManager().setPlayerControl(false);
				flight.setHandleUI(false);
			}
		});

	}
	
	private void initLine_33() {
		addTopLine("And then dock: Keep the docking bay horizontal and " +
				"approach the station carefully.");
	}

	private void initLine_34() {
		final TutorialLine line = 
			addLine(6, "This will be the last thing I teach you, today. If " +
				"you manage to dock successfully, you can officially call " +
				"yourself 'Commander'. Good luck, wet-nose.").
				setMustRetainEvents();
		
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
				if (flight.getInGameManager().getPostDockingHook() == null) {
					flight.getInGameManager().setPostDockingHook(new IMethodHook() {						
						@Override
						public void execute(float deltaTime) {
							dispose();
						}
					});					
				}
				if (flight.getInGameManager().getActualPostDockingScreen() == null) {
					flight.getInGameManager().setPostDockingScreen(
						new TutorialSelectionScreen(alite));
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

		flight.activate();
		flight.getInGameManager().setPlayerControl(false);
		flight.getInGameManager().getShip().setSpeed(0);	
		flight.setPause(false);
		
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
			FlightScreen fs = FlightScreen.createScreen(alite, dis);
			TutBasicFlying tb = new TutBasicFlying(alite, fs);
			tb.currentLineIndex = dis.readInt();
			tb.savedGalaxySeed = new char[3];
			tb.savedGalaxySeed[0] = dis.readChar();
			tb.savedGalaxySeed[1] = dis.readChar();
			tb.savedGalaxySeed[2] = dis.readChar();
			alite.getGenerator().buildGalaxy(tb.savedGalaxySeed[0], tb.savedGalaxySeed[1], tb.savedGalaxySeed[2]);
			int systemIndex = dis.readInt();
			int hyperspaceIndex = dis.readInt();
			tb.savedPresentSystem = systemIndex == -1 ? null : alite.getGenerator().getSystem(systemIndex);
			tb.savedHyperspaceSystem = hyperspaceIndex == -1 ? null : alite.getGenerator().getSystem(hyperspaceIndex);
			tb.savedInstalledEquipment = new ArrayList<Equipment>();
			int numEquip = dis.readInt();
			for (int i = 0; i < numEquip; i++) {
				tb.savedInstalledEquipment.add(EquipmentStore.fromInt(dis.readByte()));
			}
			tb.savedFuel = dis.readInt();
			for (int i = 0; i < 4; i++) {
				int laser = dis.readInt();
				tb.savedLasers[i] = laser < 0 ? null : (Laser) EquipmentStore.fromInt(laser); 
			}
			for (int i = 0; i < Settings.buttonPosition.length; i++) {
				tb.savedButtonConfiguration[i] = dis.readInt();
			}
			tb.savedDisableTraders = dis.readBoolean();
			tb.savedDisableAttackers = dis.readBoolean();
			tb.savedMissiles = dis.readInt();
			tb.savedCredits = dis.readLong();
			tb.savedScore = dis.readInt();
			tb.savedLegalStatus = LegalStatus.values()[dis.readInt()];
			tb.savedLegalValue = dis.readInt();
			tb.resetShipPosition = dis.readBoolean();
			tb.savedMarketFluct = dis.readInt();
			
			tb.target1 = (Buoy) tb.flight.findObjectByName("Yellow Target");
			if (tb.target1 != null) {
				tb.target1.setSaving(false);
			}
			tb.target2 = (Buoy) tb.flight.findObjectByName("Blue Target");
			if (tb.target2 != null) {
				tb.target2.setSaving(false);
			}
			Buoy buoy = (Buoy) tb.flight.findObjectByName("Docking Buoy");
			if (buoy != null) {
				buoy.setSaving(false);
			}
			alite.setScreen(tb);
		} catch (Exception e) {
			AliteLog.e("Tutorial Basic Flying Screen Initialize", "Error in initializer.", e);
			return false;			
		}		
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		if (mediaPlayer != null) {
			mediaPlayer.reset();
		}
		if (target1 != null) {
			target1.setSaving(true);
		}
		if (target2 != null) {
			target2.setSaving(true);
		}
		if (dockingBuoy != null) {
			dockingBuoy.setSaving(true);
		}
		flight.saveScreenState(dos);
		dos.writeInt(currentLineIndex - 1);
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
		dos.writeBoolean(resetShipPosition);	
		dos.writeInt(savedMarketFluct);
	}
	
	@Override
	public void loadAssets() {
		super.loadAssets();
		flight.loadAssets();
	}
	
	@Override 
	public void renderGlPart(float deltaTime, final Rect visibleArea) {
		if (flight != null) {
			flight.initializeGl(visibleArea);
			flight.present(deltaTime);
		} 		
	}
	
	@Override
	public void doPresent(float deltaTime) {
		renderText();
	}
	
	private void rotate(GraphicObject go, float x, float y, float z) {
		go.applyDeltaRotation((float) Math.toDegrees(x),
							  (float) Math.toDegrees(y),
							  (float) Math.toDegrees(z));
	}
	
	private void rotateBuoys(float deltaTime) {
		if (target1 != null && target1.getHullStrength() > 0 && !target1.mustBeRemoved()) {
			rotate(target1, 3 * deltaTime, 5 * deltaTime, 2 * deltaTime);
		}
		if (target2 != null && target2.getHullStrength() > 0 && !target2.mustBeRemoved()) {
			rotate(target2, 2 * deltaTime, 3 * deltaTime, 5 * deltaTime);
		}		
		if (dockingBuoy != null && dockingBuoy.getHullStrength() > 0 && !dockingBuoy.mustBeRemoved()) {
			rotate(dockingBuoy, 5 * deltaTime, 2 * deltaTime, 3 * deltaTime);
		}		
	}
	
	public void doUpdate(float deltaTime) {
		if (flight != null) {
			if (!flight.getInGameManager().isDockingComputerActive()) {
				alite.getCobra().setRotation(0, 0);
			}
			flight.update(deltaTime);		
			if (resetShipPosition) {
				resetShipPosition = false;
				// Move the ship 30000m to the right, so that the space station is out of our way.
				Vector3f offset = new Vector3f(0, 0, 0);
				flight.getInGameManager().getShip().getRightVector().copy(offset);
				offset.scale(30000.0f);
				offset.add(flight.getInGameManager().getShip().getPosition());
				flight.getInGameManager().getShip().setPosition(offset);				
			}
		}
		rotateBuoys(deltaTime);
	}
		
	@Override
	public void dispose() {
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
		
		for (Equipment e: savedInstalledEquipment) {
			alite.getCobra().addEquipment(e);
		}
		System.arraycopy(savedButtonConfiguration, 0, Settings.buttonPosition, 0, Settings.buttonPosition.length);
		alite.getCobra().setLaser(PlayerCobra.DIR_FRONT, savedLasers[0]);
		alite.getCobra().setLaser(PlayerCobra.DIR_RIGHT, savedLasers[1]);
		alite.getCobra().setLaser(PlayerCobra.DIR_REAR, savedLasers[2]);
		alite.getCobra().setLaser(PlayerCobra.DIR_LEFT, savedLasers[3]);
		alite.getGenerator().buildGalaxy(savedGalaxySeed[0], savedGalaxySeed[1], savedGalaxySeed[2]);
		alite.getGenerator().setCurrentGalaxy(alite.getGenerator().getCurrentGalaxyFromSeed());
		alite.getPlayer().setCurrentSystem(savedPresentSystem); 
		alite.getPlayer().getMarket().setFluct(savedMarketFluct);
		alite.getPlayer().getMarket().generate();
		alite.getPlayer().setHyperspaceSystem(savedHyperspaceSystem);
		alite.getCobra().setFuel(savedFuel);
		alite.getCobra().setMissiles(savedMissiles);
		alite.getPlayer().setLegalStatus(savedLegalStatus);
		alite.getPlayer().setLegalValue(savedLegalValue);
		alite.getPlayer().setCash(savedCredits);
		alite.getPlayer().setScore(savedScore);
		Settings.disableAttackers = savedDisableAttackers;
		Settings.disableTraders = savedDisableTraders;
		super.dispose();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.TUT_BASIC_FLYING_SCREEN;
	}	
}
