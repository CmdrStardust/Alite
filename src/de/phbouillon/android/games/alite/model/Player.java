package de.phbouillon.android.games.alite.model;

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
import java.util.List;

import android.graphics.Point;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.generator.enums.Government;
import de.phbouillon.android.games.alite.model.missions.Mission;
import de.phbouillon.android.games.alite.model.trading.AliteMarket;
import de.phbouillon.android.games.alite.model.trading.Market;

public class Player {
	private static final int LAVE_INDEX = 7;
	
	private String name;
	private SystemData currentSystem;
	private SystemData hyperspaceSystem;
	private Condition condition;
	private LegalStatus legalStatus;
	private Rating rating;
	private long cash;
	private int score;
	private int killCount;
	private PlayerCobra cobra;
	private final Alite alite;
	private final Market market;
	private int legalValue;
	private final Point position = new Point(-1, -1);
	private int jumpCounter = 0;
	private int intergalacticJumpCounter = 0;
	private final List <Mission> activeMissions = new ArrayList<Mission>();
	private final List <Mission> completedMissions = new ArrayList<Mission>();
	private boolean cheater;
	
	public Player(Alite alite) {
		this.alite = alite;
		market = new AliteMarket();
		reset();
	}
		
	public void reset() {
		cobra = new PlayerCobra();
		name = "Jameson";
		condition = Condition.DOCKED;
		legalStatus = LegalStatus.CLEAN;
		legalValue = 0;
		rating = Rating.HARMLESS;
		cash = 1000;
		score = 0;
		killCount = 0;
		this.alite.getGenerator().buildGalaxy(1);
		currentSystem = this.alite.getGenerator().getSystems()[LAVE_INDEX];
		hyperspaceSystem = this.alite.getGenerator().getSystems()[LAVE_INDEX];
		market.setFluct(0);
		market.setSystem(currentSystem);
		market.generate();	
		position.x = -1;
		position.y = -1;
		clearMissions();
		jumpCounter = 0;
		intergalacticJumpCounter = 0;
		cheater = false;		
	}
		
	public PlayerCobra getCobra() {
		return cobra;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public SystemData getCurrentSystem() {
		return currentSystem;
	}

	public SystemData getHyperspaceSystem() {
		return hyperspaceSystem;
	}

	public void setHyperspaceSystem(SystemData hyperspaceSystem) {
		this.hyperspaceSystem = hyperspaceSystem;
	}
	
	public void setCurrentSystem(SystemData currentSystem) {
		this.currentSystem = currentSystem;
		if (currentSystem != null) {
			market.setFluct((int) (Math.random() * 256));
			market.setSystem(currentSystem);
			market.generate();
		}
	}
	
	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition newCondition) {
		condition = newCondition;
		if((condition != Condition.GREEN) && (condition != Condition.YELLOW))
			alite.setTimeFactor(1);
	}
	
	public LegalStatus getLegalStatus() {
		return legalStatus;
	}

	public void setLegalStatus(LegalStatus legalStatus) {
		this.legalStatus = legalStatus;
	}
	
	public Rating getRating() {
		return rating;
	}
	
	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public long getCash() {
		return cash;
	}
	
	public void setCash(long newCash) {
		cash = newCash;
	}
	
	public Market getMarket() {
		return market;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void increaseKillCount(int amount) {
		killCount += amount;
	}
	
	public void setKillCount(int killCount) {
		this.killCount = killCount;
	}
	
	public int getKillCount() {
		return killCount;
	}
	
	public void setLegalValue(int legalValue) {
		this.legalValue = legalValue < 0 ? 0 : legalValue > 255 ? 255 : legalValue;
		if (this.legalValue == 0) {
			setLegalStatus(LegalStatus.CLEAN);
		} else if (this.legalValue < 32) {
			setLegalStatus(LegalStatus.OFFENDER);
		} else {
			setLegalStatus(LegalStatus.FUGITIVE);
		}
	}
		
	public int getLegalValue() {
		return legalValue;
	}
		
	public void setPosition(int px, int py) {
		position.x = px;
		position.y = py;
	}
	
	public Point getPosition() {
		return position;
	}

	public void increaseJumpCounter() {
		jumpCounter++;
	}
	
	public void resetJumpCounter() {
		jumpCounter = 0;
	}
	
	public int getJumpCounter() {
		return jumpCounter;
	}
	
	public void setJumpCounter(int counter) {
		jumpCounter = counter;
	}
	
	public void increaseIntergalacticJumpCounter() {
		intergalacticJumpCounter++;
	}
	
	public void resetIntergalacticJumpCounter() {
		intergalacticJumpCounter = 0;
	}
	
	public void setIntergalacticJumpCounter(int counter) {
		intergalacticJumpCounter = counter;
	}
	
	public int getIntergalacticJumpCounter() {
		return intergalacticJumpCounter;
	}
	
	public void clearMissions() {
		activeMissions.clear();
		completedMissions.clear();
	}
	
	public void addActiveMission(Mission mission) {
		activeMissions.add(mission);
	}
	
	public void removeActiveMission(Mission mission) {
		activeMissions.remove(mission);
	}

	public void addCompletedMission(Mission mission) {
		completedMissions.add(mission);
	}
	
	public List <Mission> getActiveMissions() {
		return activeMissions;
	}
	
	public List <Mission> getCompletedMissions() {
		return completedMissions;
	}

	public boolean isCheater() {
		return cheater;
	}

	public void setCheater(boolean b) {
		cheater = b;
	}

	public int getLegalProblemLikelihoodInPercent() {
		if (getCurrentSystem() == null) {
			return 0;
		}
		Government g = getCurrentSystem().getGovernment();
		switch (g) {
			case ANARCHY: return 0;
			case FEUDAL: return 10;
			case MULTI_GOVERNMENT: return 20;
			case DICTATORSHIP: return 40;
			case COMMUNIST: return 60;
			case CONFEDERACY: return 80;
			case DEMOCRACY: return 100;
			case CORPORATE_STATE: return 100;		
		}
		return 0;
	}
}
