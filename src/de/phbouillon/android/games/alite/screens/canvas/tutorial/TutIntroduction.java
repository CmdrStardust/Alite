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

import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.screens.canvas.BuyScreen;
import de.phbouillon.android.games.alite.screens.canvas.GalaxyScreen;
import de.phbouillon.android.games.alite.screens.canvas.LocalScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;

//This screen never needs to be serialized, as it is not part of the InGame state,
//also, all used inner classes (IMethodHook, etc.) will be reset upon state loading,
//hence they never need to be serialized, either.
@SuppressWarnings("serial")
public class TutIntroduction extends TutorialScreen {
	private StatusScreen status;
	private BuyScreen buy;
	private GalaxyScreen galaxy;
	private int screenToInitialize = 0;
	private Pixmap quelo;
	
	public TutIntroduction(final Alite alite) {
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
		initLine_11();		
		initLine_12();
		initLine_13();
		initLine_14();				
	}
	
	private void initLine_00() {		
		addLine(1, "Welcome to your basic training, nugget. I am Commander " +
				"Quelo and I'll make sure that you can handle that Cobra " +
				"once you get out of my training. Remember kid: Owning a " +
				"Cobra does not give you the ability to fly it. I will give " +
				"you that ability.").setPostPresentMethod(new IMethodHook() {					
					@Override
					public void execute(float deltaTime) {
						game.getGraphics().drawPixmap(quelo, 642, 200);
					}
				});

		status = new StatusScreen(alite);
	}
	
	private void initLine_01() {
		addLine(1, "What you see on the screen in front of you is the " +
				"status screen. You can check all the details of your Cobra.");
	}

	private void initLine_02() {
		addLine(1, "It tells you where you are currently docked and where " +
				"you're headed.").addHighlight(makeHighlight(30, 120, 750, 90));
	}
	
	private void initLine_03() {
		addLine(1, "Watch that 'Condition' read-out: If it turns to red, " +
				"there might be a chance for you to make a kill and get " +
				"some bounty -- or get killed in the process.").addHighlight(
						makeHighlight(30, 190, 750, 50));
	}
	
	private void initLine_04() {
		addLine(1, "Your legal status should be clean at all times, unless " +
				"you plan to trade in illegal goods or can't tell friend " +
				"from foe and destroy a friendly Python while targetting a " +
				"Krait.").addHighlight(makeHighlight(30, 230, 750, 50));
	}
	
	private void initLine_05() {
		addLine(1, "Your rating tells others how dangerous you are. A " +
				"fresh-meat like you should be concerned with surviving " +
				"out there in space. It's a hard life outside and only few " +
				"of us survive. However, in the unlikely event that you " +
				"should prove less inept than usual, there might be a slim " +
				"chance your rating will improve to Mostly Harmless and " +
				"beyond, but you need to kill other ships for that, kiddo.").
				addHighlight(makeHighlight(30, 270, 750, 50));
	}
	
	private void initLine_06() {
		addLine(1, "The fuel indicator tells you how many light years you " +
				"can travel in hyperspace,").addHighlight(
						makeHighlight(30, 310, 750, 50));
	}
	
	private void initLine_07() {
		addLine(1, "and finally you can check your current balance by " +
				"looking at the cash read-out.").addHighlight(
						makeHighlight(30, 350, 750, 50));
	}
	
	private void initLine_08() {
		addLine(1, "One more thing about the status screen is that it shows " +
				"the equipment of your ship. You start with a measly pulse " +
				"laser, which couldn't even scare my granny, and three " +
				"Lance and Ferman missiles. You may want to improve on that " +
				"ASAP, greenhorn.").addHighlight(makeHighlight(970, 350, 200, 50)).
				addHighlight(makeHighlight(1330, 540, 300, 50));
	}
	
	private void initLine_09() {
		final TutorialLine line = addLine(1, 
				"On the right hand side, you find the command console. Put " +
				"your finger on it and drag it all the way up.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				(TutIntroduction.this).updateNavBar(deltaTime);
				if (alite.getNavigationBar().isAtBottom()) {
					line.setFinished();
				}
			}
		}).addHighlight(makeHighlight(1740, 20, 160, 1040));				
	}
	
	private void initLine_10() {
		addLine(1, "Fascinating, eh? This console allows you to interact " +
				"with the station you are docked with and do all the " +
				"trading and improving your ship -- so you don't even have " +
				"to leave your ship anymore.");
	}
	
	private void initLine_11() {
		final TutorialLine line = addLine(1, 
				"Want to see what there is to do in the console? Ok, so go " +
				"ahead, kiddo. Push the \"Buy\" button on the console. If " +
				"you can't find it, that's because you have to drag the " +
				"console back down first.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if ((TutIntroduction.this).updateNavBar(deltaTime) instanceof BuyScreen) {
					status.dispose();
					status = null;
					alite.getNavigationBar().setActiveIndex(3);
					buy = new BuyScreen(alite);
					buy.loadAssets();
					buy.activate();					
					line.setFinished();
				}				
			}
		});		
	}
	
	private void initLine_12() {
		final TutorialLine line = addLine(1, 
				"I see, you have got that one right. Now, for today's final " +
				"exercise, try to find the \"Galaxy\" button and marvel at " +
				"the vastness of the universe you and I are living in.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				Screen result = (TutIntroduction.this).updateNavBar(deltaTime); 
				if (result instanceof GalaxyScreen && !(result instanceof LocalScreen)) {
					buy.dispose();
					buy = null;
					alite.getNavigationBar().setActiveIndex(6);
					galaxy = new GalaxyScreen(alite);
					galaxy.loadAssets();
					galaxy.activate();
					line.setFinished();
				}				
			}
		});		
	}
	
	private void initLine_13() {
		addLine(1, "Great. You have shown the ability to do as you are " +
				"told. I like that. My dog does what I tell him. Maybe I " +
				"should introduce the two of you. Remind me to do this.");
	}

	private void initLine_14() {
		addLine(1, "Now, go have a good think about what you've learned and " +
				"come back when you're ready for more, nugget.").setPause(5000);
	}

	@Override
	public void activate() {
		super.activate();
		switch (screenToInitialize) {
			case 0: status.activate(); 
					alite.getNavigationBar().moveToScreen(ScreenCodes.STATUS_SCREEN);
					break;
			case 1: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.BUY_SCREEN);
					buy = new BuyScreen(alite);
					buy.loadAssets();
					buy.activate();
					break;
			case 2: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.GALAXY_SCREEN);
					galaxy = new GalaxyScreen(alite);
					galaxy.loadAssets();
					galaxy.activate();
					break;
		}
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		TutIntroduction ti = new TutIntroduction(alite);
		try {
			ti.currentLineIndex = dis.readByte();
			ti.screenToInitialize = dis.readByte();
		} catch (Exception e) {
			AliteLog.e("Tutorial Introduction Screen Initialize", "Error in initializer.", e);
			return false;			
		}
		alite.setScreen(ti);
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeByte(currentLineIndex - 1);
		dos.writeByte(status != null ? 0 : buy != null ? 1 : 2);
	}
	
	@Override
	public void loadAssets() {
		super.loadAssets();
		status.loadAssets();
		if (quelo != null) {
			quelo.dispose();
		}
		quelo = game.getGraphics().newPixmap("quelo.png", true);
	}
	
	@Override
	public void doPresent(float deltaTime) {
		if (status != null) {
			status.present(deltaTime);
		} else if (buy != null) {
			buy.present(deltaTime);
		} else if (galaxy != null) {
			galaxy.present(deltaTime);
		}
		renderText();
	}
		
	@Override
	public void dispose() {
		if (status != null) {
			status.dispose();
			status = null;
		}
		if (buy != null) {
			buy.dispose();
			buy = null;
		}
		if (galaxy != null) {
			galaxy.dispose();
			galaxy = null;
		}
		super.dispose();
		if (quelo != null) {
			quelo.dispose();
			quelo = null;
		}
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.TUT_INTRODUCTION_SCREEN;
	}	
}
