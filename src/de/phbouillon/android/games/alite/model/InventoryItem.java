package de.phbouillon.android.games.alite.model;

public class InventoryItem {
	private Weight weight;
	private Weight unpunished;
	private long totalBuyPrice;
	
	public InventoryItem() {
		clear();
	}
	
	public void add(Weight w, long price) {
		totalBuyPrice += price;
		weight = weight.add(w);
	}
	
	public void set(Weight w, long price) {
		totalBuyPrice = price;
		weight = weight.set(w);
	}

	public void addUnpunished(Weight w) {
		unpunished = unpunished.add(w);
	}
	
	public void subUnpunished(Weight weight) {
		unpunished = unpunished.sub(weight);
		if (unpunished.getWeightInGrams() < 0) {
			unpunished = Weight.grams(0);
		}
	}
	
	public void resetUnpunished() {
		unpunished = Weight.grams(0);
	}
	
	public Weight getUnpunished() {
		return unpunished;
	}
	
	public void clear() {
		this.weight = Weight.grams(0);
		this.totalBuyPrice = 0;
		this.unpunished = Weight.grams(0);
	}
	
	public long getPrice() {
		return totalBuyPrice;
	}
	
	public Weight getWeight() {
		return weight;
	}
}
