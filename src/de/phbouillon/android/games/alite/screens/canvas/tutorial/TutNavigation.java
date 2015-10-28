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

import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.screens.canvas.GalaxyScreen;
import de.phbouillon.android.games.alite.screens.canvas.LocalScreen;
import de.phbouillon.android.games.alite.screens.canvas.PlanetScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;

//This screen never needs to be serialized, as it is not part of the InGame state,
//also, all used inner classes (IMethodHook, etc.) will be reset upon state loading,
//hence they never need to be serialized, either.
@SuppressWarnings("serial")
public class TutNavigation extends TutorialScreen {
	private StatusScreen status;
	private GalaxyScreen galaxy;
	private PlanetScreen planet;
	private LocalScreen  local;
	private int screenToInitialize = 0;
	
	public TutNavigation(final Alite alite) {
		super(alite);

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
	}
	
	private void initLine_00() {
		final TutorialLine line = addLine(4, 
				"Wonderful morning, eh, rookie? I have a bunch of " +
				"information for you today. So wipe the sleep from your " +
				"face and pay attention! You have seen the Galaxy screen " +
				"already, but today, I'll tell you more about it. First: " +
				"Open it.");
		
		status = new StatusScreen(alite);
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen screen = (TutNavigation.this).updateNavBar(deltaTime); 
				if (screen instanceof GalaxyScreen && !(screen instanceof LocalScreen)) {
					line.setFinished();
					status.dispose();
					status = null;
					alite.getNavigationBar().setActiveIndex(6);
					galaxy = new GalaxyScreen(alite);
					galaxy.loadAssets();
					galaxy.activate();										
				}								
			}
		});
	}
	
	private void initLine_01() {
		final TutorialLine line = addLine(4, 
				"The white cross you see indicates your current position. " +
				"The red circle around it, shows how far you can travel " +
				"with your current fuel. You can zoom in by pressing two " +
				"fingers on the screen and move them apart. Do that until " +
				"you can see the names of the systems appear.").setY(150);
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {
			@Override
			public void execute(float deltaTime) {
				for (TouchEvent event: game.getInput().getTouchEvents()) {
					galaxy.processTouch(event);
				}
				if (galaxy.namesVisible()) {
					line.setFinished();
				}
			}
		});
	}

	private void initLine_02() {
		final TutorialLine line = addLine(4, "Now, push the \"Home\" button.");
		
		line.setSkippable(false).setHeight(100).setUpdateMethod(new IMethodHook() {
			@Override
			public void execute(float deltaTime) {
				for (TouchEvent event: game.getInput().getTouchEvents()) {
					galaxy.processTouch(event);
					if (event.type == TouchEvent.TOUCH_UP) {
						if (galaxy.getHomeButton().isTouched(event.x, event.y)) {
							line.setFinished();
						}
					}
				}
			}
		}).addHighlight(makeHighlight(1020, 980, 320, 100)).setY(150);
	}

	private void initLine_03() {
		final TutorialLine line = addLine(4, 
				"See how it moved the map back to your current location? " +
				"Helps newbies like yourself not to get lost so easily... " +
				"Did you notice the different colors of the star systems? " +
				"These colors indicate the economy of the system. It ranges " +
				"from poor agricultures in grey to rich industrials in " +
				"blue. In case you forget, however, you can switch to the " +
				"planet screen to get more information on the selected " +
				"planet. Do that now.").setY(150).setHeight(300);
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (galaxy != null) {
					galaxy.updateMap(deltaTime);
				}
				Screen screen = (TutNavigation.this).updateNavBar(deltaTime); 
				if (screen instanceof PlanetScreen) {
					galaxy.dispose();
					galaxy = null;
					alite.getNavigationBar().setActiveIndex(8);
					planet = new PlanetScreen(alite);
					planet.loadAssets();
					planet.activate();					
					line.setFinished();
					currentLineIndex++;
				} else if (screen != null) {
					line.setFinished();
				}
			}
		});
	}

	private void initLine_04() {
		final TutorialLine line = addLine(4,
				"No, I told you to switch to the planet screen, wet-nose. " +
				"Try again.");
		
		line.setHeight(100).setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen newScreen = (TutNavigation.this).updateNavBar(deltaTime); 
				if (newScreen instanceof PlanetScreen) {
					galaxy.dispose();
					galaxy = null;
					alite.getNavigationBar().setActiveIndex(8);
					planet = new PlanetScreen(alite);
					planet.loadAssets();
					planet.activate();					
					line.setFinished();
				} else if (newScreen != null) {
					line.setFinished();
					currentLineIndex--;
				}
			}
		});
	}

	private void initLine_05() {
		addLine(4, "Here on the planet screen, you'll see all the relevant " +
				"information for a planetary system: The name, its economy, " +
				"its government, and the tech level, which tells you what " +
				"kind of equipment you'll be able to buy at that system. " +
				"You'll also have information on the inhabitants of that " +
				"planet, which you should read carefully, so you can trade " +
				"successfully and not risk insulting a feline by not " +
				"smelling enough -- not that I think you'd have a problem " +
				"with that particular requirement, nugget.").setY(700).setHeight(350);
	}

	private void initLine_06() {
		final TutorialLine line = addLine(4,
				"Now that you have gathered information on the planet, " +
				"switch to the local screen.").setY(150);
		
		line.setHeight(150).setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen screen = (TutNavigation.this).updateNavBar(deltaTime); 
				if (screen instanceof LocalScreen) {
					planet.dispose();
					planet = null;
					alite.getNavigationBar().setActiveIndex(7);
					local = new LocalScreen(alite);
					local.loadAssets();
					local.activate();					
					line.setFinished();
					currentLineIndex++;
				} else if (screen != null) {
					line.setFinished();
				}
			}
		});
	}

	private void initLine_07() {
		final TutorialLine line = addLine(4, 
				"No, I told you to switch to the local screen, wet-nose. " +
				"Try again.").setY(150);
		
		line.setHeight(150).setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen newScreen = (TutNavigation.this).updateNavBar(deltaTime); 
				if (newScreen instanceof LocalScreen) {
					planet.dispose();
					planet = null;
					alite.getNavigationBar().setActiveIndex(7);
					local = new LocalScreen(alite);
					local.loadAssets();
					local.activate();					
					line.setFinished();
				} else if (newScreen != null) {
					line.setFinished();
					currentLineIndex--;
				}
			}
		});		
	}

	private void initLine_08() {
		final TutorialLine line = addLine(4, 
				"The local screen is the same as the galaxy screen but it " +
				"shows only a small region around your current location if " +
				"accessed. You can zoom out, though. Just hold two fingers " +
				"on the screen and move them together. Do it now.").setY(150);
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				for (TouchEvent event: game.getInput().getTouchEvents()) {
					local.processTouch(event);
				}
				if (local.getZoomFactor() < 2.0f) {
					line.setFinished();
				}
			}
		});
	}

	private void initLine_09() {
		addLine(4, "Good. Now, you know everything about navigation. Or " +
				"almost: Remember that the system you select is always your " +
				"destination system. If you launch and engage hyperspace, " +
				"you'll end up in that system -- if you're not intercepted " +
				"by Thargoids that is.").setY(150);
	}

	private void initLine_10() {
		addLine(4, "Scared, nugget? Well, you should be. Life as a pilot is " +
				"hard, dangerous, and hardly rewarding. Still, we get to " +
				"see rare sights, if we master control of our ship and " +
				"survive assaults. Do you have it in you? We are soon going " +
				"to find out: Next lesson, I'll start the flying " +
				"instructions. Until then: Think hard if this is really " +
				"what you want. If not, don't show up next time. Your " +
				"secret is safe with me. I won't tell a soul about your " +
				"fears. Trust me.").setY(150).setHeight(350).setPause(5000);
	}

	@Override
	public void activate() {
		super.activate();
		switch (screenToInitialize) {
			case 0: status.activate();
					alite.getNavigationBar().moveToTop();
					alite.getNavigationBar().moveToScreen(ScreenCodes.STATUS_SCREEN);
					break;
			case 1: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.GALAXY_SCREEN);
					galaxy = new GalaxyScreen(alite);
					galaxy.loadAssets();
					galaxy.activate();
					break;
			case 2: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.LOCAL_SCREEN);
					local = new LocalScreen(alite);
					local.loadAssets();
					local.activate();
					break;
			case 3: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.PLANET_SCREEN);
					planet = new PlanetScreen(alite);
					planet.loadAssets();
					planet.activate();
					break;
		}
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		TutNavigation tn = new TutNavigation(alite);
		try {
			tn.currentLineIndex = dis.readInt();
			tn.screenToInitialize = dis.readByte();
		} catch (Exception e) {
			AliteLog.e("Tutorial Navigation Screen Initialize", "Error in initializer.", e);
			return false;			
		}
		alite.setScreen(tn);
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(currentLineIndex - 1);
		dos.writeByte(status != null ? 0 : galaxy != null ? 1 : local != null ? 2 : 3);
	}
	
	@Override
	public void loadAssets() {
		super.loadAssets();
		status.loadAssets();
	}
	
	@Override
	public void doPresent(float deltaTime) {
		if (status != null) {
			status.present(deltaTime);
		} else if (galaxy != null) {
			galaxy.present(deltaTime);
		} else if (planet != null) {
			planet.present(deltaTime);
		} else if (local != null) {
			local.present(deltaTime);
		}
		
		renderText();
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (planet != null) {
			planet.update(deltaTime);
		}
	}
	
	@Override
	public void dispose() {
		if (status != null) {
			status.dispose();
			status = null;
		}
		if (galaxy != null) {
			galaxy.dispose();
			galaxy = null;
		}
		if (local != null) {
			local.dispose();
			local = null;
		}
		if (planet != null) {
			planet.dispose();
			planet = null;
		}
		super.dispose();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.TUT_NAVIGATION_SCREEN;
	}	
}
