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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.phbouillon.android.games.alite.model.generator.SystemData;

public abstract class Market {
	protected SystemData system;
	protected int        fluct;
	protected final Map <TradeGood, Integer> quantity;
	protected final Map <TradeGood, Integer> price;
	protected final TradeGoodStore store;
	
	protected Market(TradeGoodStore store) {
		quantity   = new HashMap<TradeGood, Integer>();
		price      = new HashMap<TradeGood, Integer>();
		fluct      = 0;
		this.store = store;
	}

	public void setSystem(SystemData system) {
		this.system = system;
	}
	
	public int getFluct() {
		return fluct;
	}
	
	public void setFluct(int fluct) {
		this.fluct = fluct;
	}

	public abstract void generate();
	
	public int getPrice(TradeGood good) {
		return price.get(good);
	}	
	
	public int getQuantity(TradeGood good) {
		return quantity.get(good);
	}
	
	public List <Integer> getQuantities() {
		ArrayList <Integer> result = new ArrayList<Integer>();
		for (TradeGood tg: TradeGoodStore.get().goods) {
			result.add(quantity.get(tg));
		}
		return result;
	}
	
	public void setQuantity(TradeGood good, int newQuantity) {
		quantity.put(good, newQuantity);
	}
	
	public TradeGood [] getGoods() {
		return store.goods;
	}	
}
