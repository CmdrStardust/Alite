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
		goods[FOOD]             = new TradeGood(0x13, -0x02, 0x06, 0x01,       Unit.TON,      "Food",           48, 49,    47,    47,    48,    49,    49,    49,    48);
		goods[TEXTILES]         = new TradeGood(0x14, -0x01, 0x0a, 0x03,       Unit.TON,      "Textiles",       71, 71,    70,    71,    71,    72,    72,    72,    71);
		goods[RADIOACTIVES]     = new TradeGood(0x41, -0x03, 0x02, 0x07,       Unit.TON,      "Radioactives",  228, 230,   228,   228,   229,   230,   231,   231,   229);
		goods[SLAVES]           = new TradeGood(0x28, -0x05, 0xe2, 0x1f, 2.5f, Unit.TON,      "Slaves",        147, 149,   144,   145,   147,   149,   149,   151,   146);
		goods[LIQUOR_WINES]     = new TradeGood(0x53, -0x05, 0xfb, 0x0f,       Unit.TON,      "Liquor/Wines",  286, 289,   285,   285,   287,   290,   290,   291,   286);
		goods[LUXURIES]         = new TradeGood(0xc4,  0x08, 0x36, 0x03,       Unit.TON,      "Luxuries",      913, 908,   914,   913,   910,   906,   906,   904,   911);
		goods[NARCOTICS]        = new TradeGood(0xeb,  0x1d, 0x08, 0x78, 2.5f, Unit.TON,      "Narcotics", 1611, 1606,  1630,  1626,  1615,  1601,  1600,  1593,  1619);
		goods[COMPUTERS]        = new TradeGood(0x9a,  0x0e, 0x38, 0x03,       Unit.TON,      "Computers", 831, 828,   839,   837,   832,   825,   825,   822,   834);
		goods[MACHINERY]        = new TradeGood(0x75,  0x06, 0x28, 0x07,       Unit.TON,      "Machinery", 569, 570,   575,   574,   572,   569,   569,   567,   573);
		goods[ALLOYS]           = new TradeGood(0x4e,  0x01, 0x11, 0x1f,       Unit.TON,      "Alloys", 387, 389,   389,   389,   389,   389,   389,   388,   389);
		goods[FIREARMS]         = new TradeGood(0x9c,  0x0d, 0x1d, 0x07, 1.3f, Unit.TON,      "Firearms", 830, 829,   840,   838,   833,   827,   827,   823,   835);
		goods[FURS]             = new TradeGood(0xb0, -0x09, 0xdc, 0x3f,       Unit.TON,      "Furs", 694, 698,   691,   692,   695,   700,   700,   702,   694);
		goods[MINERALS]         = new TradeGood(0x18, -0x01, 0x60, 0x03,       Unit.KILOGRAM, "Minerals", 87, 87,    87,    87,    87,    88,    88,    88,    87);
		goods[GOLD]             = new TradeGood(0x61, -0x01, 0x42, 0x07,       Unit.KILOGRAM, "Gold", 385, 387,   386,   387,   387,   387,   387,   388,   387);
		goods[PLATINUM]         = new TradeGood(0xab, -0x02, 0x37, 0x1f,       Unit.KILOGRAM, "Platinum", 714, 717,   715,   715,   716,   717,   717,   717,   716);
		goods[GEM_STONES]       = new TradeGood(0x2d, -0x01, 0xfa, 0x0f,       Unit.GRAM,     "Gem-Stones", 194, 195,   195,   195,   195,   196,   196,   196,   195);
		goods[ALIEN_ITEMS]      = new TradeGood(0x35,  0x0f, 0xf8, 0x07,       Unit.TON,      "Alien Items", 447, 446,   458,   457,   451,   443,   443,   440,   453);
		goods[MEDICAL_SUPPLIES] = new TradeGood(0x7b,  0x10, 0x07, 0x78,       Unit.TON,      "Medical Supplies", 965, 967,   980,   978,   972,   964,   964,   960,   974);		
	}	
}
