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
import java.util.Locale;

import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.EquipmentStore;
import de.phbouillon.android.games.alite.model.InventoryItem;
import de.phbouillon.android.games.alite.model.Player;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.trading.Market;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.model.trading.Unit;
import de.phbouillon.android.games.alite.screens.opengl.ingame.FlightScreen;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class InventoryScreen extends TradeScreen {	
	static class InventoryPair {
		TradeGood good;
		InventoryItem item;
		
		InventoryPair(TradeGood good, InventoryItem item) {
			this.good = good;
			this.item = item;
		}
	}
	
	private static final String INVENTORY_HINT = "(Tap again to sell)";
	private static final String EJECT_HINT = "(Tap again to eject)";
	private final ArrayList<InventoryPair> inventoryList = new ArrayList<InventoryPair>();
	private Pixmap [] tradegoods;
	private Pixmap [] beam;
	private Pixmap thargoidDocuments;
	private Pixmap unhappyRefugees;
	private String pendingSelection = null;
	
	public InventoryScreen(Game game) {
		super(game, 0);
		X_OFFSET = 50;
		GAP_X = 270;
		GAP_Y = 290;
		COLUMNS = 6;
	}
		
	@Override
	public void activate() {		
		createButtons();
		if (pendingSelection != null) {
			for (Button [] bs: tradeButton) {
				if (bs == null) {
					continue;
				}
				for (Button b: bs) {
					if (b == null || b.getName() == null) {
						continue;
					}
					if (pendingSelection.equals(b.getName())) {
						selection = b;
						b.setSelected(true);
					}
				}
			}
			pendingSelection = null;
		}
	}
	
	public static boolean initialize(Alite alite, final DataInputStream dis) {
		InventoryScreen is = new InventoryScreen(alite);
		try {
			byte selectionLength = dis.readByte();
			if (selectionLength > 0) {
				is.pendingSelection = "";
				while (selectionLength > 0) {
					is.pendingSelection += dis.readChar();
					selectionLength--;
				}
			}
		} catch (Exception e) {
			AliteLog.e("Inventory Screen Initialize", "Error in initializer.", e);
			return false;
		}
		alite.setScreen(is);
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
		PlayerCobra cobra = ((Alite) game).getPlayer().getCobra();
		InventoryItem [] inventory = cobra.getInventory();
		inventoryList.clear();
		
		int counter = 0;
		for (InventoryItem item: inventory) {
			if (item.getWeight().getWeightInGrams() != 0) {
				inventoryList.add(new InventoryPair(TradeGoodStore.get().goods()[counter], item));
			}
			counter++;
		}
		tradeButton = new Button[COLUMNS][ROWS];
		for (int x = 0; x < COLUMNS; x++) {
			for (int y = 0; y < ROWS; y++) {
				tradeButton[x][y] = null;
			}
		}
		int y = 0;
		int x = 0;
		for (InventoryPair pair: inventoryList) {
			tradeButton[x][y] = new Button(x * GAP_X + X_OFFSET, y * GAP_Y + Y_OFFSET, SIZE, SIZE, tradegoods[TradeGoodStore.get().ordinal(pair.good)], beam);
			tradeButton[x][y].setName(pair.good.getName());
			tradeButton[x][y].setUseBorder(false);
			x++;
			if (x == COLUMNS) {
				x = 0;
				y++;
			}
		}
		renderSpecialCargo(x, y);
	}

	private void renderSpecialCargo(int x, int y) {
		PlayerCobra cobra = ((Alite) game).getPlayer().getCobra();
		Weight w = cobra.getSpecialCargo("Thargoid Documents");
		if (w != null) {
			tradeButton[x][y] = new Button(x * GAP_X + X_OFFSET, y * GAP_Y + Y_OFFSET, SIZE, SIZE, thargoidDocuments, beam);
			tradeButton[x][y].setUseBorder(false);
			x++;
			if (x == COLUMNS) {
				x = 0;
				y++;
			}
		}
		w = cobra.getSpecialCargo("Unhappy Refugees");
		if (w != null) {
			tradeButton[x][y] = new Button(x * GAP_X + X_OFFSET, y * GAP_Y + Y_OFFSET, SIZE, SIZE, unhappyRefugees, beam);
			tradeButton[x][y].setUseBorder(false);
		}				
	}
	
	private long cap(long value) {
        String binary = Long.toBinaryString(value);
        if (binary.length() > 32) {
            binary = binary.substring(binary.length() - 32);
            return Long.parseLong(binary, 2);
        }       
        return value;
    }
   
    private long computePrice(Market market, int factor, long weightInGrams, TradeGood good) {
        long tradeGoodPrice = (market.getPrice(good) * 19 * 4) / 20;
        long price = ((weightInGrams / factor) + (weightInGrams % factor != 0 ? 1 : 0)) * tradeGoodPrice;
        price = cap(price) / 4;
        return price;       
    }

	private String computeCashString(InventoryPair pair) {
		Market market = ((Alite) game).getPlayer().getMarket();
		
		int factor = pair.good.getUnit() == Unit.TON ? 1000000 : pair.good.getUnit() == Unit.KILOGRAM ? 1000 : 1;
		long price = computePrice(market, factor, pair.item.getWeight().getWeightInGrams(), pair.good);
		
		return String.format(Locale.getDefault(), "%d.%d Cr", price / 10, price % 10);		
	}
	
	private String computeGainLossString(InventoryPair pair) {
		Market market = ((Alite) game).getPlayer().getMarket();
		
		int factor = pair.good.getUnit() == Unit.TON ? 1000000 : pair.good.getUnit() == Unit.KILOGRAM ? 1000 : 1;
		long price = computePrice(market, factor, pair.item.getWeight().getWeightInGrams(), pair.good);
		long gain = price - pair.item.getPrice();
		boolean loss = false;
		if (gain < 0) {
			loss = true;
			gain = -gain;
		}
		return (loss ? "Loss: -" : "Gain: ") + String.format(Locale.getDefault(), "%d.%d Cr", gain / 10, gain % 10);		
	}

	@Override
	protected String getCost(int row, int column) {
		int index = row * COLUMNS + column;
		if (index >= inventoryList.size()) {
			return "";
		}
		InventoryPair pair = inventoryList.get(index); 
		return pair.item.getWeight().getFormattedString();
	}
	
	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		displayTitle("Inventory");
		
		if (inventoryList.isEmpty()) {
			if (!((Alite) game).getCobra().containsSpecialCargo()) {
				g.drawText("Cargo hold is empty.", 180, 200, AliteColors.get().warningMessage(), Assets.regularFont);
			}
		}
		presentTradeGoods(deltaTime);		
		presentTradeStatus();
	}
	
	@Override
	protected void presentSelection(int row, int column) {
		int index = row * COLUMNS + column;
		if (index >= inventoryList.size()) {
			try {
				if (tradeButton[column][row] != null && tradeButton[column][row].getPixmap() == thargoidDocuments) {
					game.getGraphics().drawText("Important Thargoid Documents", X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
				} else if (tradeButton[column][row] != null && tradeButton[column][row].getPixmap() == unhappyRefugees) {
					game.getGraphics().drawText("Unhappy Refugees", X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
				}
			} catch (IndexOutOfBoundsException e) {
				// Ignore.
			}
			return;
		}
		InventoryPair pair = inventoryList.get(index); 
		TradeGood tradeGood = pair.good;
		String worthText = tradeGood.getName() + " - worth: " + computeCashString(pair) + ". ";
		int width = game.getGraphics().getTextWidth(worthText, Assets.regularFont);
		game.getGraphics().drawText(worthText, X_OFFSET, 1050, AliteColors.get().message(), Assets.regularFont);
		String gainText = computeGainLossString(pair) + ". ";
		int widthComplete = width + game.getGraphics().getTextWidth(gainText, Assets.regularFont);
		game.getGraphics().drawText(gainText, X_OFFSET + width, 1050, gainText.startsWith("Loss") ? AliteColors.get().conditionRed() : AliteColors.get().conditionGreen(), Assets.regularFont);
		Alite alite = (Alite) game;
		if (alite.getCurrentScreen() instanceof FlightScreen && alite.getCobra().isEquipmentInstalled(EquipmentStore.fuelScoop)) {
			game.getGraphics().drawText(EJECT_HINT, X_OFFSET + widthComplete, 1050, AliteColors.get().message(), Assets.regularFont);			
		} else {
			game.getGraphics().drawText(INVENTORY_HINT, X_OFFSET + widthComplete, 1050, AliteColors.get().message(), Assets.regularFont);
		}
	}
	
	protected void performTradeWhileInFlight(int row, int column) {
		if (!((Alite) game).getCobra().isEquipmentInstalled(EquipmentStore.fuelScoop)) {
			super.performTradeWhileInFlight(row, column);
			return;
		}
		SoundManager.play(Assets.retroRocketsOrEscapeCapsuleFired);
		int index = row * COLUMNS + column;
		if (index >= inventoryList.size()) {
			return;
		}
		InventoryPair pair = inventoryList.get(index); 
		TradeGood tradeGood = pair.good;
    	Player player = ((Alite) game).getPlayer();
    	Weight ejectedWeight;
    	long ejectedPrice = pair.item.getPrice();
    	if (pair.item.getWeight().compareTo(Weight.tonnes(4)) < 0) {
    		ejectedWeight = pair.item.getWeight();    		
    		player.getCobra().removeTradeGood(tradeGood);    		 
    	} else {    		
    		ejectedWeight = Weight.tonnes(4);
    		player.getCobra().setTradeGood(tradeGood, pair.item.getWeight().sub(ejectedWeight), pair.item.getPrice());    		
    		player.getCobra().subUnpunishedTradeGood(tradeGood, ejectedWeight);
    	}
    	InGameManager manager = ((FlightScreen) ((Alite) game).getCurrentScreen()).getInGameManager(); 
    	manager.getLaserManager().ejectPlayerCargoCanister(manager.getShip(), tradeGood, ejectedWeight, ejectedPrice);
		
    	createButtons();		
	}

    @Override
	public void performTrade(int row, int column) {
		int index = row * COLUMNS + column;
		if (index >= inventoryList.size()) {
			return;
		}
		InventoryPair pair = inventoryList.get(index); 
		TradeGood tradeGood = pair.good;
    	Player player = ((Alite) game).getPlayer();
    	for (Mission m: player.getActiveMissions()) {
    		if (m.performTrade(this, tradeGood)) {
    			return;
    		}
    	}
    	Market market = player.getMarket();    			
		int factor = pair.good.getUnit() == Unit.TON ? 1000000 : pair.good.getUnit() == Unit.KILOGRAM ? 1000 : 1;
		long price = computePrice(market, factor, pair.item.getWeight().getWeightInGrams(), pair.good);
		int chanceInPercent = ((Alite) game).getPlayer().getLegalProblemLikelihoodInPercent();
		if (Math.random() * 100 < chanceInPercent) {
			((Alite) game).getPlayer().setLegalValue(
					((Alite) game).getPlayer().getLegalValue() + (int) (tradeGood.getLegalityType() * pair.item.getUnpunished().getQuantityInAppropriateUnit()));
		}
		player.getCobra().removeTradeGood(tradeGood);
    	player.setCash(player.getCash() + price);
    	cashLeft = String.format("Cash: %d.%d Cr", player.getCash() / 10, player.getCash() % 10);    
		SoundManager.play(Assets.kaChing);		
    	createButtons();
		try {
			((Alite) game).getFileUtils().autoSave((Alite) game);
		} catch (IOException e) {
			AliteLog.e("Auto saving failed", e.getMessage(), e);
		}    	
	}

    public String getCashLeft() {
    	return cashLeft;
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
		if (thargoidDocuments == null) {
			thargoidDocuments = g.newPixmap("trade_icons/thargoid_documents.png", true);
		}
		if (unhappyRefugees == null) {
			unhappyRefugees = g.newPixmap("trade_icons/unhappy_refugees.png", true);
		}
		super.loadAssets();
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
		if (thargoidDocuments != null) {
			thargoidDocuments.dispose();
			thargoidDocuments = null;
		}
		if (unhappyRefugees != null) {
			unhappyRefugees.dispose();
			unhappyRefugees = null;
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
		return ScreenCodes.INVENTORY_SCREEN;
	}
}
