package de.phbouillon.android.games.alite.colors;

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

import com.dd.plist.NSDictionary;

import de.phbouillon.android.games.alite.oxp.ColorParser;

public class OXPColorScheme extends ColorScheme {
	private final String name;
	
	private long conditionGreen;
	private long conditionYellow;
	private long conditionRed;
	
	private long background;
	private long baseInformation;
	private long mainText;
	private long informationText;
	private long additionalText;	
	private long message;
	private long warningMessage;
	
	private long fuelCircle;
	private long dashedFuelCircle;
	
	private long inhabitantInformation;
	private long arrow;
	private long economy;
	private long government;
	private long techLevel;
	private long population;
	private long shipDistance;
	private long diameter;
	private long gnp;
	
	private long currentSystemName;
	private long hyperspaceSystemName;
	private long legalStatus;
	private long remainingFuel;
	private long balance;
	private long rating;	
	private long missionObjective;
	private long equipmentDescription;
			
	private long richIndustrial;
	private long averageIndustrial;
	private long poorIndustrial;
	private long mainIndustrial;
	private long mainAgricultural;
	private long richAgricultural;
	private long averageAgricultural;
	private long poorAgricultural;

	private long backgroundLight;
	private long backgroundDark;
	private long frameLight;
	private long frameDark;
	private long coloredFrameLight;
	private long coloredFrameDark;
	private long selectedColoredFrameLight;
	private long selectedColoredFrameDark;
	
	private long pulsingHighlighterDark;
	private long pulsingHighlighterLight;
	private long tutorialBubbleDark;
	private long tutorialBubbleLight;
	private long shipTitle;
	private long textAreaBackground;
	private long cursor;
	private long price;
	private long scrollingText;
	private long selectedText;
	
	private long creditsDescription;
	private long creditsPerson;
	private long creditsAddition;
	private long highlightColor;
	
	private float [] frontShieldMin;
	private float [] frontShieldMax;
	private float [] aftShieldMin;
	private float [] aftShieldMax;
	private float [] fuelMin;
	private float [] fuelMax;
	private float [] cabinTemperatureMin;
	private float [] cabinTemperatureMax;
	private float [] laserTemperatureMin;
	private float [] laserTemperatureMax;
	private float [] altitudeMin;
	private float [] altitudeMax;
	private float [] speedMin;
	private float [] speedMax;
	private float [] energyBankMin;
	private float [] energyBankMax;
	private float [] lastEnergyBankMin;
	private float [] lastEnergyBankMax;
	private float [] indicatorBar;

	public OXPColorScheme(String name) {
		this.name = name;
		initializeDefaults();
	}
	
	private void initializeDefaults() {
		ModernColorScheme mcs = new ModernColorScheme();
		
		conditionGreen = mcs.conditionGreen();
		conditionYellow = mcs.conditionYellow();
		conditionRed = mcs.conditionRed();
		
		background = mcs.background();
		baseInformation = mcs.baseInformation();
		mainText = mcs.mainText();
		informationText = mcs.informationText();
		additionalText = mcs.additionalText();	
		message = mcs.message();
		warningMessage = mcs.warningMessage();
		
		fuelCircle = mcs.fuelCircle();
		dashedFuelCircle = mcs.dashedFuelCircle();
		
		inhabitantInformation = mcs.inhabitantInformation();
		arrow = mcs.arrow();
		economy = mcs.economy();
		government = mcs.government();
		techLevel = mcs.techLevel();
		population = mcs.population();
		shipDistance = mcs.shipDistance();
		diameter = mcs.diameter();
		gnp = mcs.gnp();
		
		currentSystemName = mcs.currentSystemName();
		hyperspaceSystemName = mcs.hyperspaceSystemName();
		legalStatus = mcs.legalStatus();
		remainingFuel = mcs.remainingFuel();
		balance = mcs.balance();
		rating = mcs.rating();	
		missionObjective = mcs.missionObjective();
		equipmentDescription = mcs.equipmentDescription();
				
		richIndustrial = mcs.richIndustrial();
		averageIndustrial = mcs.averageIndustrial();
		poorIndustrial = mcs.poorIndustrial();
		mainIndustrial = mcs.mainIndustrial();
		mainAgricultural = mcs.mainAgricultural();
		richAgricultural = mcs.richAgricultural();
		averageAgricultural = mcs.averageAgricultural();
		poorAgricultural = mcs.poorAgricultural();

		backgroundLight = mcs.backgroundLight();
		backgroundDark = mcs.backgroundDark();
		frameLight = mcs.frameLight();
		frameDark = mcs.frameDark();
		coloredFrameLight = mcs.coloredFrameLight();
		coloredFrameDark = mcs.coloredFrameDark();
		selectedColoredFrameLight = mcs.selectedColoredFrameLight();
		selectedColoredFrameDark = mcs.selectedColoredFrameDark();
		
		pulsingHighlighterDark = mcs.pulsingHighlighterDark();
		pulsingHighlighterLight = mcs.pulsingHighlighterLight();
		tutorialBubbleDark = mcs.tutorialBubbleDark();
		tutorialBubbleLight = mcs.tutorialBubbleLight();
		shipTitle = mcs.shipTitle();
		textAreaBackground = mcs.textAreaBackground();
		cursor = mcs.cursor();
		price = mcs.price();
		scrollingText = mcs.scrollingText();
		selectedText = mcs.selectedText();
		
		highlightColor = mcs.highlightColor();
		
		creditsAddition = mcs.creditsAddition();
		creditsDescription = mcs.creditsDescription();
		creditsPerson = mcs.creditsPerson();
		
		frontShieldMin      = new float [] {1.0f, 0.0f, 0.0f};
		frontShieldMax      = new float [] {0.0f, 1.0f, 0.0f};
		aftShieldMin        = new float [] {1.0f, 0.0f, 0.0f};
		aftShieldMax        = new float [] {0.0f, 1.0f, 0.0f};
		fuelMin             = new float [] {1.0f, 0.0f, 0.0f};
		fuelMax             = new float [] {0.0f, 1.0f, 0.0f};
		cabinTemperatureMin = new float [] {1.0f, 1.0f, 0.0f};
		cabinTemperatureMax = new float [] {1.0f, 0.0f, 0.0f};
		laserTemperatureMin = new float [] {0.0f, 1.0f, 0.0f};
		laserTemperatureMax = new float [] {1.0f, 0.0f, 0.0f};
		altitudeMin         = new float [] {1.0f, 0.0f, 0.0f};
		altitudeMax         = new float [] {0.0f, 1.0f, 0.0f};
		speedMin            = new float [] {0.0f, 1.0f, 0.0f};
		speedMax            = new float [] {1.0f, 0.0f, 0.0f};
		energyBankMin       = new float [] {0.0f, 0.0f, 1.0f};
		energyBankMax       = new float [] {0.0f, 0.0f, 1.0f};
		lastEnergyBankMin   = new float [] {1.0f, 0.0f, 0.0f};
		lastEnergyBankMax   = new float [] {0.0f, 0.0f, 1.0f};
		indicatorBar        = new float [] {0.0f, 1.0f, 0.0f};
	}
	
	public void read(NSDictionary colors) {
		if (colors.containsKey("default_text_color")) {
			mainText = ColorParser.getColorFromOXP(colors.get("default_text_color"));			
		}
		if (colors.containsKey("screen_title_color")) {
			message = ColorParser.getColorFromOXP(colors.get("screen_title_color"));			
		}
		if (colors.containsKey("market_cash_color")) {
			price = ColorParser.getColorFromOXP(colors.get("market_cash_color"));
		}
		if (colors.containsKey("systemdata_facts_color")) {
			
		}
	}
	
	public String name() {
		return name;
	}
	
	@Override
	public long conditionGreen() {
		return conditionGreen;
	}

	@Override
	public long conditionYellow() {
		return conditionYellow;
	}

	@Override
	public long conditionRed() {
		return conditionRed;
	}

	@Override
	public long background() {
		return background;
	}

	@Override
	public long baseInformation() {
		return baseInformation;
	}

	@Override
	public long mainText() {
		return mainText;
	}

	@Override
	public long informationText() {
		return informationText;
	}

	@Override
	public long additionalText() {
		return additionalText;
	}

	@Override
	public long message() {
		return message;
	}

	@Override
	public long warningMessage() {
		return warningMessage;
	}

	@Override
	public long fuelCircle() {
		return fuelCircle;
	}

	@Override
	public long dashedFuelCircle() {
		return dashedFuelCircle;
	}

	@Override
	public long inhabitantInformation() {
		return inhabitantInformation;
	}

	@Override
	public long arrow() {
		return arrow;
	}

	@Override
	public long economy() {
		return economy;
	}

	@Override
	public long government() {
		return government;
	}

	@Override
	public long techLevel() {
		return techLevel;
	}

	@Override
	public long population() {
		return population;
	}

	@Override
	public long shipDistance() {
		return shipDistance;
	}

	@Override
	public long diameter() {
		return diameter;
	}

	@Override
	public long gnp() {
		return gnp;
	}

	@Override
	public long currentSystemName() {
		return currentSystemName;
	}

	@Override
	public long hyperspaceSystemName() {
		return hyperspaceSystemName;
	}

	@Override
	public long legalStatus() {
		return legalStatus;
	}

	@Override
	public long remainingFuel() {
		return remainingFuel;
	}

	@Override
	public long balance() {
		return balance;
	}

	@Override
	public long rating() {
		return rating;
	}

	@Override
	public long missionObjective() {
		return missionObjective;
	}

	@Override
	public long equipmentDescription() {
		return equipmentDescription;
	}

	@Override
	public long richIndustrial() {
		return richIndustrial;
	}

	@Override
	public long averageIndustrial() {
		return averageIndustrial;
	}

	@Override
	public long poorIndustrial() {
		return poorIndustrial;
	}

	@Override
	public long mainIndustrial() {
		return mainIndustrial;
	}

	@Override
	public long mainAgricultural() {
		return mainAgricultural;
	}

	@Override
	public long richAgricultural() {
		return richAgricultural;
	}

	@Override
	public long averageAgricultural() {
		return averageAgricultural;
	}

	@Override
	public long poorAgricultural() {
		return poorAgricultural;
	}

	@Override
	public long backgroundLight() {
		return backgroundLight;
	}

	@Override
	public long backgroundDark() {
		return backgroundDark;
	}

	@Override
	public long frameLight() {
		return frameLight;
	}

	@Override
	public long frameDark() {
		return frameDark;
	}

	@Override
	public long coloredFrameLight() {
		return coloredFrameLight;
	}

	@Override
	public long coloredFrameDark() {
		return coloredFrameDark;
	}

	@Override
	public long selectedColoredFrameLight() {
		return selectedColoredFrameLight;
	}

	@Override
	public long selectedColoredFrameDark() {
		return selectedColoredFrameDark;
	}

	@Override
	public long pulsingHighlighterDark() {
		return pulsingHighlighterDark;
	}

	@Override
	public long pulsingHighlighterLight() {
		return pulsingHighlighterLight;
	}

	@Override
	public long tutorialBubbleDark() {
		return tutorialBubbleDark;
	}

	@Override
	public long tutorialBubbleLight() {
		return tutorialBubbleLight;
	}

	@Override
	public long shipTitle() {
		return shipTitle;
	}

	@Override
	public long textAreaBackground() {
		return textAreaBackground;
	}

	@Override
	public long cursor() {
		return cursor;
	}

	@Override
	public long price() {
		return price;
	}

	@Override
	public long scrollingText() {
		return scrollingText;
	}

	@Override
	public long selectedText() {
		return selectedText;
	}

	private long interpolate(float [] min, float [] max, float value, float alpha) {
		return AliteColors.convertRgba(min[0] + value * (max[0] - min[0]),
                					   min[1] + value * (max[1] - min[1]),
                					   min[2] + value * (max[2] - min[2]), alpha);		
	}
	
	@Override
	public long frontShield(float value, float alpha) {
		return interpolate(frontShieldMin, frontShieldMax, value, alpha);
	}

	@Override
	public long aftShield(float value, float alpha) {
		return interpolate(aftShieldMin, aftShieldMax, value, alpha);	}

	@Override
	public long fuel(float value, float alpha) {
		return interpolate(fuelMin, fuelMax, value, alpha);
	}

	@Override
	public long cabinTemperature(float value, float alpha) {
		return interpolate(cabinTemperatureMin, cabinTemperatureMax, value, alpha);
	}

	@Override
	public long laserTemperature(float value, float alpha) {
		return interpolate(laserTemperatureMin, laserTemperatureMax, value, alpha);
	}

	@Override
	public long altitude(float value, float alpha) {
		return interpolate(altitudeMin, altitudeMax, value, alpha);
	}

	@Override
	public long speed(float value, float alpha) {
		return interpolate(speedMin, speedMax, value, alpha);
	}

	@Override
	public long energyBank(float value, float alpha) {
		return interpolate(energyBankMin, energyBankMax, value, alpha);
	}

	@Override
	public long lastEnergyBank(float value, float alpha) {
		return interpolate(lastEnergyBankMin, lastEnergyBankMax, value, alpha);
	}

	@Override
	public long indicatorBar(float alpha) {
		return AliteColors.convertRgba(indicatorBar[0], indicatorBar[1], indicatorBar[2], alpha);
	}

	@Override
	public long creditsDescription() {
		return creditsDescription;
	}

	@Override
	public long creditsPerson() {
		return creditsPerson;
	}

	@Override
	public long creditsAddition() {
		return creditsAddition;
	}
	
	@Override
	public long highlightColor() {
		return highlightColor;
	}

	@Override
	public long activeColoredFrameLight() {
		return selectedColoredFrameLight;
	}

	@Override
	public long activeColoredFrameDark() {
		return selectedColoredFrameDark;
	}		
}
