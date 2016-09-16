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
import java.util.Locale;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.Screen;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ButtonRegistry;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.trading.Market;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.model.trading.Unit;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class BuyScreen extends TradeScreen {	
	private static final String MARKET_HINT = "(Tap again to buy)";
	private static final int [] availColors = new int [] {0xffff0000, 0xffff3e00, 0xffff7d00, 0xffffbd00, 0xffffff00, 0xffffff3b, 0xffffff7d, 0xffffffbf, 0xffffffff};
	private int currentAvailabiltyColor;
	private String boughtAmount = null;
	private long startTime = System.nanoTime();
	private Pixmap [] tradegoods;
	private Pixmap [] beam;
	private String pendingSelection = null;
	
	public BuyScreen(Game game) {
		super(game, 0);
		X_OFFSET = 50;
		GAP_X = 270;
		GAP_Y = 290;
		COLUMNS = 6;		
	}
	
	@Override
	public void activate() {
		if (tradeButton == null) {
			createButtons();
		} else {
			for (Button [] bs: tradeButton) {
				for (Button b: bs) {
					ButtonRegistry.get().addButton(this, b);
				}
			}
		}
		if (pendingSelection != null) {
			for (Button [] bs: tradeButton) {
				for (Button b: bs) {
					if (pendingSelection.equals(b.getName())) {
						selection = b;
						b.setSelected(true);
					}
				}
			}
			pendingSelection = null;
		}
	}
		
	public static BuyScreen readScreen(Alite alite, final DataInputStream dis) {
		BuyScreen bs = new BuyScreen(alite);
		try {
			byte selectionLength = dis.readByte();
			if (selectionLength > 0) {
				bs.pendingSelection = "";
				while (selectionLength > 0) {
					bs.pendingSelection += dis.readChar();
					selectionLength--;
				}
			}
		} catch (Exception e) {
			AliteLog.e("Buy Screen Initialize", "Error in initializer.", e);
			return null;
		}
		return bs;
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		BuyScreen bs = readScreen(alite, dis);
		if (bs == null) {
			return false;
		}
		alite.setScreen(bs);
		return true;
	}

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeByte(selection == null ? 0 : selection.getName().length());
		if (selection != null) {
			dos.writeChars(selection.getName());
		}
	}

	@Override
	protected void createButtons() {
		tradeButton = new Button[COLUMNS][ROWS];
		
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				tradeButton[x][y] = new Button(x * GAP_X + X_OFFSET, y * GAP_Y + Y_OFFSET, SIZE, SIZE, tradegoods[y * COLUMNS + x], beam);
				TradeGood tradeGood = TradeGoodStore.get().goods()[y * COLUMNS + x];
				tradeButton[x][y].setName(tradeGood.getName());
				tradeButton[x][y].setUseBorder(false);
			}
		}		
	}
	
	@Override
	protected String getCost(int row, int column) {
		Market market = ((Alite) game).getPlayer().getMarket();
		int price = market.getPrice(TradeGoodStore.get().goods()[row * COLUMNS + column]);
		return String.format(Locale.getDefault(), "%d.%d Cr", price / 10, price % 10);
	}
		
	public void resetSelection() {
		selection = null;
	}
	
	public TradeGood getSelectedGood() {
		if (selection == null) {
			return null;
		}
		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLUMNS; x++) {
				if (tradeButton[x][y] == null) {
					continue;
				}
				if (selection == tradeButton[x][y]) {
					return TradeGoodStore.get().goods()[y * COLUMNS + x];
				}				
			}
		}
		return null;
	}
		
	public TradeGood getGoodToBuy() {
		if (newScreen instanceof QuantityPadScreen) {
			return getSelectedGood();
		}
		return null;
	}
	
	public String getBoughtAmount() {
		return boughtAmount;
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayTitle("Buy Cargo");
		
		long timeDiff = (System.nanoTime() - startTime) / 500000000; // half seconds
		currentAvailabiltyColor = (int) (timeDiff % availColors.length);
		presentTradeGoods(deltaTime);		
		presentTradeStatus();
	}

	@Override
	protected void drawAdditionalTradeGoodInformation(int row, int column, float deltaTime) {
		TradeGood tradeGood = TradeGoodStore.get().goods()[row * COLUMNS + column];
		int avail = ((Alite) game).getPlayer().getMarket().getQuantity(tradeGood);
		if (avail > 0) {
			int height = SIZE * avail / 63 + 1;
			game.getGraphics().fillRect(column * GAP_X + X_OFFSET + SIZE + 1,
										row * GAP_Y + Y_OFFSET + SIZE - 1 - height, 20, height, availColors[currentAvailabiltyColor]);
		}
	}
	
	@Override
	protected void presentSelection(int row, int column) {
		TradeGood tradeGood = TradeGoodStore.get().goods()[row * COLUMNS + column];
		int avail = ((Alite) game).getPlayer().getMarket().getQuantity(tradeGood);
		int avgPrice = ((Alite) game).getGenerator().getAveragePrice(tradeGood);
		String average = String.format(Locale.getDefault(), "%d.%d Cr ", avgPrice / 10, avgPrice % 10);
		game.getGraphics().drawText(tradeGood.getName() + " - " + avail + tradeGood.getUnit().toUnitString() + " available. Average Price: " + average + MARKET_HINT, X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
	}
		
	public void setBoughtAmountString(String s) {
		this.boughtAmount = s;
	}
		
    @Override
	public void performTrade(int row, int column) {    	
    	TradeGood tradeGood = TradeGoodStore.get().goods()[row * COLUMNS + column];
    	Player player = ((Alite) game).getPlayer();
    	Market market = player.getMarket();
    	for (Mission mission: player.getActiveMissions()) {
    		if (mission.performTrade(this, tradeGood)) {
    			return;
    		}
    	}
    	int avail = market.getQuantity(tradeGood);
    	if (avail == 0) {
    		setMessage("Sorry - that item is out of stock.");
    		SoundManager.play(Assets.error);
    		return;
    	}
    	String maxAmountString = avail + tradeGood.getUnit().toUnitString();
    	if (boughtAmount == null) {
    		if (column < 3) {
    			newScreen = new QuantityPadScreen(this, game, maxAmountString, 1075, 200, row, column);
    		} else {
    			newScreen = new QuantityPadScreen(this, game, maxAmountString, 215, 200, row, column);
    		}
    	} else {
    		long buyAmount = 0;
    		try {
    			buyAmount = Long.parseLong(boughtAmount);
    		} catch (NumberFormatException e) {
    			boughtAmount = null;
    			return;
    		}
    		boughtAmount = null;
    		if (buyAmount > avail) {
    			SoundManager.play(Assets.error);
    			setMessage("Sorry - we don't have that much in stock.");
    			return;
    		}
    		long totalPrice = buyAmount * market.getPrice(tradeGood);
    		if (totalPrice > player.getCash()) {
    			SoundManager.play(Assets.error);
    			setMessage("Sorry - you don't have enough credits.");
    			return;
    		}
    		Weight buyWeight = tradeGood.getUnit() == Unit.GRAM ? Weight.grams(buyAmount) :
    			               tradeGood.getUnit() == Unit.KILOGRAM ? Weight.kilograms(buyAmount) :
    			               Weight.tonnes(buyAmount);
    		if (buyWeight.compareTo(player.getCobra().getFreeCargo()) == 1) {
    			SoundManager.play(Assets.error);
    			setMessage("Sorry - you don't have enough room in your hold.");
    			return;
    		}    		    		
    		market.setQuantity(tradeGood, (int) (market.getQuantity(tradeGood) - buyAmount));
    		player.getCobra().addTradeGood(tradeGood, buyWeight, totalPrice);
    		player.setCash(player.getCash() - totalPrice);
    		SoundManager.play(Assets.kaChing);
    		cashLeft = String.format("Cash left: %d.%d Cr", player.getCash() / 10, player.getCash() % 10);
    		selection = null;
    		int chanceInPercent = ((Alite) game).getPlayer().getLegalProblemLikelihoodInPercent();
    		if (Math.random() * 100 < chanceInPercent) {
    			((Alite) game).getPlayer().setLegalValue(
    				((Alite) game).getPlayer().getLegalValue() + (int) (tradeGood.getLegalityType() * buyAmount));
    		}
    		try {
				((Alite) game).getFileUtils().autoSave((Alite) game);
			} catch (IOException e) {
				AliteLog.e("Auto saving failed", e.getMessage(), e);
			}
    	}
	}

	private void readBeamAnimation(final Graphics g) {
		beam = new Pixmap[16];
		beam[0] = g.newPixmap("trade_icons/beam.png", true);
		for (int i = 1; i < 16; i++) {
			beam[i] = g.newPixmap("trade_icons/beam/" + i + ".png", true);
		}
	}
	
	private void readTradegoods(final Graphics g) {
		tradegoods = new Pixmap[18];
		tradegoods[ 0] = g.newPixmap("trade_icons/food.png", true);
		tradegoods[ 1] = g.newPixmap("trade_icons/textiles.png", true);
		tradegoods[ 2] = g.newPixmap("trade_icons/radioactives.png", true);
		tradegoods[ 3] = g.newPixmap("trade_icons/slaves.png", true);
		tradegoods[ 4] = g.newPixmap("trade_icons/liquor_wines.png", true);
		tradegoods[ 5] = g.newPixmap("trade_icons/luxuries.png", true);
		tradegoods[ 6] = g.newPixmap("trade_icons/narcotics.png", true);
		tradegoods[ 7] = g.newPixmap("trade_icons/computers.png", true);
		tradegoods[ 8] = g.newPixmap("trade_icons/machinery.png", true);
		tradegoods[ 9] = g.newPixmap("trade_icons/alloys.png", true);
		tradegoods[10] = g.newPixmap("trade_icons/firearms.png", true);
		tradegoods[11] = g.newPixmap("trade_icons/furs.png", true);
		tradegoods[12] = g.newPixmap("trade_icons/minerals.png", true);
		tradegoods[13] = g.newPixmap("trade_icons/gold.png", true);
		tradegoods[14] = g.newPixmap("trade_icons/platinum.png", true);
		tradegoods[15] = g.newPixmap("trade_icons/gem_stones.png", true);
		tradegoods[16] = g.newPixmap("trade_icons/alien_items.png", true);
		tradegoods[17] = g.newPixmap("trade_icons/medical_supplies.png", true);
	}

	@Override
	public void loadAssets() {
		Graphics g = game.getGraphics();

		if (beam == null && Settings.animationsEnabled) {
			readBeamAnimation(g);
		}
		if (tradegoods == null) {
			readTradegoods(g);
		}		
		super.loadAssets();
	}	
	
	protected void performScreenChange() {
		if (inFlightScreenChange()) {
			return;
		}
		Screen oldScreen = game.getCurrentScreen();
		if (!(newScreen instanceof QuantityPadScreen)) {
			oldScreen.dispose();
		}
		game.setScreen(newScreen);
		((Alite) game).getNavigationBar().performScreenChange();
		postScreenChange();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (beam != null) {
			for (Pixmap p: beam) {
				p.dispose();				
			}
			beam = null;
		}
		if (tradegoods != null) {
			for (Pixmap p: tradegoods) {
				p.dispose();
			}
			tradegoods = null;
		}
	}
	
	@Override
	public void pause() {
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public int getScreenCode() {
		return ScreenCodes.BUY_SCREEN;
	}	
}
