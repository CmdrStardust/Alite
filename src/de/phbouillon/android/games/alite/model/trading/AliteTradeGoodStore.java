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

public class AliteTradeGoodStore extends TradeGoodStore {
	@Override
	public void initialize() {
		goods[FOOD]             = new TradeGood(0x13, -0x02, 0x06, 0x01,       Unit.TON,      "Food");
		goods[TEXTILES]         = new TradeGood(0x14, -0x01, 0x0a, 0x03,       Unit.TON,      "Textiles");
		goods[RADIOACTIVES]     = new TradeGood(0x41, -0x03, 0x02, 0x07,       Unit.TON,      "Radioactives");
		goods[SLAVES]           = new TradeGood(0x28, -0x05, 0xe2, 0x1f, 2.5f, Unit.TON,      "Slaves");
		goods[LIQUOR_WINES]     = new TradeGood(0x53, -0x05, 0xfb, 0x0f,       Unit.TON,      "Liquor/Wines");
		goods[LUXURIES]         = new TradeGood(0xc4,  0x08, 0x36, 0x03,       Unit.TON,      "Luxuries");
		goods[NARCOTICS]        = new TradeGood(0xeb,  0x1d, 0x08, 0x78, 2.5f, Unit.TON,      "Narcotics");
		goods[COMPUTERS]        = new TradeGood(0x9a,  0x0e, 0x38, 0x03,       Unit.TON,      "Computers");
		goods[MACHINERY]        = new TradeGood(0x75,  0x06, 0x28, 0x07,       Unit.TON,      "Machinery");
		goods[ALLOYS]           = new TradeGood(0x4e,  0x01, 0x11, 0x1f,       Unit.TON,      "Alloys");
		goods[FIREARMS]         = new TradeGood(0x9c,  0x0d, 0x1d, 0x07, 1.3f, Unit.TON,      "Firearms");
		goods[FURS]             = new TradeGood(0xb0, -0x09, 0xdc, 0x3f,       Unit.TON,      "Furs");
		goods[MINERALS]         = new TradeGood(0x18, -0x01, 0x60, 0x03,       Unit.KILOGRAM, "Minerals");
		goods[GOLD]             = new TradeGood(0x61, -0x01, 0x42, 0x07,       Unit.KILOGRAM, "Gold");
		goods[PLATINUM]         = new TradeGood(0xab, -0x02, 0x37, 0x1f,       Unit.KILOGRAM, "Platinum");
		goods[GEM_STONES]       = new TradeGood(0x2d, -0x01, 0xfa, 0x0f,       Unit.GRAM,     "Gem-Stones");
		goods[ALIEN_ITEMS]      = new TradeGood(0x35,  0x0f, 0xf8, 0x07,       Unit.TON,      "Alien Items");
		goods[MEDICAL_SUPPLIES] = new TradeGood(0x7b,  0x10, 0x07, 0x78,       Unit.TON,      "Medical Supplies");
	}	
}
