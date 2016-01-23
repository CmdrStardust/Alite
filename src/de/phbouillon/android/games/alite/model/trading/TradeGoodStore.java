package de.phbouillon.android.games.alite.model.trading;

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

public abstract class TradeGoodStore {
	public static final int NUMBER_OF_GOODS = 18;
	
	public static final int FOOD             =  0;
	public static final int TEXTILES         =  1;
	public static final int RADIOACTIVES     =  2;
	public static final int SLAVES           =  3;
	public static final int LIQUOR_WINES     =  4;
	public static final int LUXURIES         =  5;
	public static final int NARCOTICS        =  6;
	public static final int COMPUTERS        =  7;
	public static final int MACHINERY        =  8;
	public static final int ALLOYS           =  9;
	public static final int FIREARMS         = 10;
	public static final int FURS             = 11;
	public static final int MINERALS         = 12;
	public static final int GOLD             = 13;
	public static final int PLATINUM         = 14;
	public static final int GEM_STONES       = 15;
	public static final int ALIEN_ITEMS      = 16;
	public static final int MEDICAL_SUPPLIES = 17;

	private static TradeGoodStore instance = null;	
	protected final TradeGood [] goods = new TradeGood[NUMBER_OF_GOODS]; 	

	protected TradeGoodStore() {
		initialize();
	}	
	
	public abstract void initialize();	
	
	public static TradeGoodStore get() {
		if (instance == null) {
			instance = new AliteTradeGoodStore();
		}
		return instance;
	}
	
	public static TradeGoodStore create(TradeGoodStore store) {
		instance = store;
		return instance;
	}

	public static void reset(TradeGoodStore newStore) {
		TradeGoodStore.instance = newStore;
	}

	public TradeGood [] goods() {
		return goods;
	}
	
	// Convenience methods
	
	public int ordinal(TradeGood good) {
		if (good == null) {
			return -1;
		}
		for (int i = 0; i < goods.length; i++) {
			if (good.equals(goods[i])) {
				return i;
			}
		}
		return -1;
	}
	
	public TradeGood fromNumber(int number) {
		return goods[number];
	}
	
	public TradeGood getRandomTradeGoodForContainer() {
		int num;
		
		do {
			num = (int) (Math.random() * NUMBER_OF_GOODS);
		} while (num == ALIEN_ITEMS);
		
		return goods[num];
	}
	
	public TradeGood food()              { return goods[FOOD];             }
	public TradeGood textiles()          { return goods[TEXTILES];         }
	public TradeGood radioactives()      { return goods[RADIOACTIVES];     }
	public TradeGood slaves()            { return goods[SLAVES];           }
	public TradeGood liquorWines()       { return goods[LIQUOR_WINES];     }
	public TradeGood luxuries()          { return goods[LUXURIES];         }
	public TradeGood narcotics()         { return goods[NARCOTICS];        }
	public TradeGood computers()         { return goods[COMPUTERS];        }
	public TradeGood machinery()         { return goods[MACHINERY];        }
	public TradeGood alloys()            { return goods[ALLOYS];           }
	public TradeGood firearms()          { return goods[FIREARMS];         }
	public TradeGood furs()              { return goods[FURS];             }
	public TradeGood minerals()          { return goods[MINERALS];         }
	public TradeGood gold()              { return goods[GOLD];             }
	public TradeGood platinum()          { return goods[PLATINUM];         }
	public TradeGood gemStones()         { return goods[GEM_STONES];       }
	public TradeGood alienItems()        { return goods[ALIEN_ITEMS];      }
	public TradeGood medicalSupplies()   { return goods[MEDICAL_SUPPLIES]; }	
	public TradeGood thargoidDocuments() { return new TradeGood(0, 0, 0, 0, Unit.GRAM, "Thargoid Documents", 0); }
}
