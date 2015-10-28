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

public class AliteMarket extends Market {
	public AliteMarket() {
		super(TradeGoodStore.create(new AliteTradeGoodStore()));
	}
	
	@Override
	public void generate() {
		for (TradeGood tradeGood: store.goods()) {
			int product = system.getEconomy().ordinal() * tradeGood.getGradient();
			int changing = fluct & tradeGood.getMaskByte();
			char q = (char) (((char) (tradeGood.getBaseQuantity() + changing - product)) & 0x00FF);
			if ((q & 0x80) > 0) {
				q = 0;
			}
			int val = tradeGood == store.alienItems() ? 0 : q & 0x3f;
			quantity.put(tradeGood, val);
			
			q = (char) (((char) (tradeGood.getBasePrice() + changing + product)));
			val = q * 4;
			price.put(tradeGood, val);
		}
	}
}
