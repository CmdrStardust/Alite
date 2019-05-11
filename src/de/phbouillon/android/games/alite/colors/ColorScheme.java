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

public abstract class ColorScheme {
	public abstract long conditionGreen();
	public abstract long conditionYellow();
	public abstract long conditionRed();

	public abstract long background();
	public abstract long baseInformation();
	public abstract long mainText();
	public abstract long informationText();
	public abstract long additionalText();	
	public abstract long message();
	public abstract long warningMessage();
	
	public abstract long fuelCircle();
	public abstract long dashedFuelCircle();
	
	public abstract long inhabitantInformation();
	public abstract long arrow();
	public abstract long economy();
	public abstract long government();
	public abstract long techLevel();
	public abstract long population();
	public abstract long shipDistance();
	public abstract long diameter();
	public abstract long gnp();
	
	public abstract long currentSystemName();
	public abstract long hyperspaceSystemName();
	public abstract long legalStatus();
	public abstract long remainingFuel();
	public abstract long balance();
	public abstract long rating();	
	public abstract long missionObjective();
	public abstract long equipmentDescription();
			
	public abstract long richIndustrial();
	public abstract long averageIndustrial();
	public abstract long poorIndustrial();
	public abstract long mainIndustrial();
	public abstract long mainAgricultural();
	public abstract long richAgricultural();
	public abstract long averageAgricultural();
	public abstract long poorAgricultural();

	public abstract long backgroundLight();
	public abstract long backgroundDark();
	public abstract long frameLight();
	public abstract long frameDark();
	public abstract long coloredFrameLight();
	public abstract long coloredFrameDark();
	public abstract long selectedColoredFrameLight();
	public abstract long selectedColoredFrameDark();
	public abstract long activeColoredFrameLight();
	public abstract long activeColoredFrameDark();
	
	public abstract long pulsingHighlighterDark();
	public abstract long pulsingHighlighterLight();
	public abstract long tutorialBubbleDark();
	public abstract long tutorialBubbleLight();
	public abstract long shipTitle();
	public abstract long textAreaBackground();
	public abstract long cursor();
	public abstract long price();
	public abstract long scrollingText();
	public abstract long selectedText();

	public abstract long creditsDescription();
	public abstract long creditsPerson();
	public abstract long creditsAddition();

	public abstract long frontShield(float value, float alpha);
	public abstract long aftShield(float value, float alpha);
	public abstract long fuel(float value, float alpha);
	public abstract long cabinTemperature(float value, float alpha);
	public abstract long laserTemperature(float value, float alpha);
	public abstract long altitude(float value, float alpha);
	public abstract long speed(float value, float alpha);
	public abstract long energyBank(float value, float alpha);
	public abstract long lastEnergyBank(float value, float alpha);
	public abstract long indicatorBar(float alpha);
	public abstract long highlightColor();
}
