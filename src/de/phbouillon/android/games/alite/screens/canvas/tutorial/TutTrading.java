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
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.model.InventoryItem;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.canvas.BuyScreen;
import de.phbouillon.android.games.alite.screens.canvas.InventoryScreen;
import de.phbouillon.android.games.alite.screens.canvas.QuantityPadScreen;
import de.phbouillon.android.games.alite.screens.canvas.StatusScreen;

//This screen never needs to be serialized, as it is not part of the InGame state,
//also, all used inner classes (IMethodHook, etc.) will be reset upon state loading,
//hence they never need to be serialized, either.
@SuppressWarnings("serial")
public class TutTrading extends TutorialScreen {
	private StatusScreen status;
	private BuyScreen buy;
	private InventoryScreen inventory;
	private QuantityPadScreen quantity;
	private boolean success = false;
	private long savedCash;
	private int savedFoodQuantity;
	private InventoryItem [] savedInventory;
	private int screenToInitialize = 0;
	
	public TutTrading(final Alite alite) {
		super(alite);

		savedCash = alite.getPlayer().getCash();
		savedFoodQuantity = alite.getPlayer().getMarket().getQuantity(TradeGoodStore.get().food());
		savedInventory = new InventoryItem[TradeGoodStore.get().goods().length];
		InventoryItem [] currentItems = alite.getCobra().getInventory();
		for (int i = 0; i < TradeGoodStore.get().goods().length; i++) {
			savedInventory[i] = new InventoryItem();
			savedInventory[i].set(currentItems[i].getWeight(), currentItems[i].getPrice());
			savedInventory[i].addUnpunished(currentItems[i].getUnpunished());
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
	}
	
	private void initLine_00() {
		final TutorialLine line = addLine(2, 
				"Ah, I see you're back. Think you're ready for more " +
				"basics? Ok, so first, open the Buy screen again. You " +
				"remember how to do that, don't you?");
		
		status = new StatusScreen(alite);		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if ((TutTrading.this).updateNavBar(deltaTime) instanceof BuyScreen) {
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
	
	private void initLine_01() {
		addLine(2, "Here, you can see all the goods that are offered on " +
				"the station you're docked with. The price of the good is " +
				"given below the good and right next to the trade item, " +
				"you can see how much of it is available. If you can't " +
				"figure out what a symbol represents, just tap it, and a " +
				"description will be displayed at the bottom of the screen.");
	}

	private void initLine_02() {
		final TutorialLine line = addLine(2, 
			"Oh, don't be shy, try it: Tap one trade item once. Come on.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (buy.getSelectedGood() == null) {
					for (TouchEvent event: game.getInput().getTouchEvents()) {
						buy.processTouch(event);
					}
				}
				if (buy.getSelectedGood() != null) {
					line.setFinished();					
				}
			}
		});
	}

	private void initLine_03() {
		final TutorialLine line = addLine(2, 
				"See? That wasn't so hard after all, was it? Now, if you " +
				"tap it again, and the item is available, you can buy some.");
		
		line.setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				buy.resetSelection();
			}
		}).setHeight(150);
	}

	private void initLine_04() {
		final TutorialLine line = addLine(2, 
				"Let's try that: Tap on the symbol for food.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (buy.getSelectedGood() == null) {
					for (TouchEvent event: game.getInput().getTouchEvents()) {
						buy.processTouch(event);
					}
				}
				TradeGood selectedGood = buy.getSelectedGood();
				if (selectedGood != null) {
					line.setFinished();
					if (selectedGood == TradeGoodStore.get().food()) {
						currentLineIndex++;
					} else {
						buy.resetSelection();
					}					
				}
			}
		}).addHighlight(makeHighlight(50, 100, 225, 225)).setHeight(150);
	}

	private void initLine_05() {
		final TutorialLine line = addLine(2, 
				"No, I told you to tap on the food-icon, that's the one " +
				"in the upper left corner, wet-nose.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (buy.getSelectedGood() == null) {
					for (TouchEvent event: game.getInput().getTouchEvents()) {
						buy.processTouch(event);
					}
				}
				TradeGood selectedGood = buy.getSelectedGood();
				if (selectedGood != null) {
					line.setFinished();
					if (selectedGood != TradeGoodStore.get().food()) {
						currentLineIndex--;
						buy.resetSelection();
					}
				}
			}
		}).addHighlight(makeHighlight(50, 100, 225, 225)).setHeight(150);		
	}

	private void initLine_06() {
		final TutorialLine line = addLine(2, "Ok. Now tap it again.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (buy.getGoodToBuy() == null) {
					for (TouchEvent event : game.getInput().getTouchEvents()) {
						buy.processTouch(event);
					}
					if (buy.getGoodToBuy() == TradeGoodStore.get().food()) {
						quantity = (QuantityPadScreen) buy.getNewScreen();
						quantity.loadAssets();
						quantity.activate();					
						line.setFinished();							
					}
				}
			}
		}).setHeight(150);	
	}

	private void initLine_07() {
		final TutorialLine line = addLine(2, 
				"Now you need to enter the quantity: How much do you " +
				"want to buy. Let's not overindulge ourselves here: " +
				"Although this is a simulation only, and nothing will " +
				"really be added to your cargo bay, enter a 1 followed by OK.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				for (TouchEvent event : game.getInput().getTouchEvents()) {
					quantity.processTouch(event);					
				}
				if (quantity.getNewScreen() == buy) {
					success = "1".equals(buy.getBoughtAmount());
					if (success) {
						currentLineIndex++;
					}
					line.setFinished();
				}
			}
		}).setWidth(800).setHeight(350).setY(400).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (quantity != null) {
					quantity.clearAmount();
					if (success) {
						quantity.dispose();
						quantity = null;
						buy.performTrade(0, 0);
					}
				}
			}
		});		
	}

	private void initLine_08() {
		final TutorialLine line = addLine(2, 
				"I don't know if you failed basic reading, but it sure " +
				"looks like that you fleshy-headed mutant. Type a 1, that's " +
				"the number in the first column, third row, followed by OK; " +
				"that's the third column, fourth row button.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				for (TouchEvent event : game.getInput().getTouchEvents()) {
					quantity.processTouch(event);					
				}
				if (quantity.getNewScreen() == buy) {
					success = "1".equals(buy.getBoughtAmount());
					line.setFinished();					 
					if (!success) {
						currentLineIndex--;
					} 
				}
			}
		}).setWidth(800).setHeight(350).setY(400).setFinishHook(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if (success) {
					quantity.dispose();
					quantity = null;
					buy.performTrade(0, 0);
				} else {
					quantity.clearAmount();
				}
			}
		});				
	}

	private void initLine_09() {
		final TutorialLine line = addLine(2, 
				"Good. You have now bought 1 ton of food and your " +
				"account has been reduced by the price of food. Now go to " +
				"the inventory screen and see how that ton of food is " +
				"displayed there.");
		
		line.setSkippable(false).setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				if ((TutTrading.this).updateNavBar(deltaTime) instanceof InventoryScreen) {
					buy.dispose();
					buy = null;
					alite.getNavigationBar().setActiveIndex(4);
					inventory = new InventoryScreen(alite);
					inventory.loadAssets();
					inventory.activate();					
					line.setFinished();
				}								
			}
		}).setHeight(150);		
	}

	private void initLine_10() {
		final TutorialLine line = addLine(2, 
				"See? You can also see at the bottom of the screen that " +
				"you now have less cargo space available. You start with a " +
				"20t Cargo hold in a Cobra Mk III. I sure hope you'll " +
				"upgrade that soon. Now, what about selling? To sell " +
				"something, go to the inventory screen and tap the item you " +
				"want to sell twice. Easy, right? Go ahead, do it...");
		
		line.setSkippable(false).setY(500).
			addHighlight(makeHighlight(450, 970, 850, 40)).
			setUpdateMethod(new IMethodHook() {			
			@Override
			public void execute(float deltaTime) {
				for (TouchEvent event : game.getInput().getTouchEvents()) {
					inventory.processTouch(event);					
				}
				if (inventory.getCashLeft() != null) {
					line.setFinished();
				}
			}
		});		
	}

	private void initLine_11() {
		addLine(2, "Yeah. See. Now you sold the food back. But what's that? " +
				"You lost money in the process! That's the standard fee for " +
				"selling anything at a space station. It will automatically " +
				"be removed from your price. So you better make sure that " +
				"you sell your goods at a higher price than you bought " +
				"them. Don't worry, though, cub, this was only a " +
				"simulation. We did not take anything from your account. " +
				"Understood all that? -- I didn't think so. So go back and " +
				"think about everything I told you today and then come back " +
				"when you're ready for more.").setY(500).setHeight(400).
				setPause(5000);
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
					alite.getNavigationBar().moveToScreen(ScreenCodes.BUY_SCREEN);
					buy = new BuyScreen(alite);
					buy.loadAssets();
					buy.activate();
					break;
			case 2: status.dispose();
					status = null;
					alite.getNavigationBar().moveToScreen(ScreenCodes.BUY_SCREEN);
					buy = new BuyScreen(alite);
					buy.loadAssets();
					buy.activate();
			    	int avail = alite.getPlayer().getMarket().getQuantity(TradeGoodStore.get().food());
			    	String maxAmountString = avail + TradeGoodStore.get().food().getUnit().toUnitString();
			    	quantity = new QuantityPadScreen(buy, alite, maxAmountString, 1075, 200, 0, 0);
					quantity.loadAssets();
					quantity.activate();					
					break;
			case 3: status.dispose();
			        status = null;
			        alite.getNavigationBar().moveToScreen(ScreenCodes.INVENTORY_SCREEN);
			        inventory = new InventoryScreen(alite);
			        inventory.loadAssets();
			        inventory.activate();
			        break;
		}
		if (currentLineIndex <= 0) {
			alite.getCobra().clearInventory();
			alite.getPlayer().getMarket().setQuantity(TradeGoodStore.get().food(), 17);
			alite.getPlayer().setCash(1000);
		}
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		TutTrading tt = new TutTrading(alite);
		try {
			tt.currentLineIndex = dis.readInt();
			tt.screenToInitialize = dis.readByte();
			tt.savedCash = dis.readLong();
			tt.savedInventory = new InventoryItem[dis.readInt()];
			for (int i = 0; i < tt.savedInventory.length; i++) {
				tt.savedInventory[i] = new InventoryItem();
				tt.savedInventory[i].set(Weight.grams(dis.readLong()), dis.readLong());
				tt.savedInventory[i].addUnpunished(Weight.grams(dis.readLong()));
			}
			tt.savedFoodQuantity = dis.readInt();
		} catch (Exception e) {
			AliteLog.e("Tutorial Trading Screen Initialize", "Error in initializer.", e);
			return false;			
		}
		alite.setScreen(tt);
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(currentLineIndex - 1);
		if (status != null) {
			dos.writeByte(0);
		} else if (buy != null) {
			dos.writeByte(quantity != null ? 2 : 1);
		} else if (inventory != null) {
			dos.writeByte(3);
		}
		dos.writeLong(savedCash);
		dos.writeInt(savedInventory.length);
		for (InventoryItem w: savedInventory) {
			dos.writeLong(w.getWeight().getWeightInGrams());
			dos.writeLong(w.getPrice());
			dos.writeLong(w.getUnpunished().getWeightInGrams());
		}
		dos.writeInt(savedFoodQuantity);
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
		} else if (buy != null) {
			if (quantity != null) {
				quantity.present(deltaTime);
			} else {
				buy.present(deltaTime);
			}		
		} else if (inventory != null) {
			inventory.present(deltaTime);
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
		if (inventory != null) {
			inventory.dispose();
			inventory = null;
		}
		if (quantity != null) {
			quantity.dispose();
			quantity = null;
		}
		alite.getPlayer().setCash(savedCash);
		alite.getCobra().clearInventory();
		alite.getCobra().setInventory(savedInventory);
		alite.getPlayer().getMarket().setQuantity(TradeGoodStore.get().food(), savedFoodQuantity);
		super.dispose();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.TUT_TRADING_SCREEN;
	}	
}
