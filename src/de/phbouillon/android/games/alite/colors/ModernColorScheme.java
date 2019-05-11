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

class ModernColorScheme extends ColorScheme {
	public static final long GRAY                  = 0xff8c8c8cl;
	public static final long YELLOW                = 0xffefef00l;
	public static final long BLUE                  = 0xff8caaefl;
	public static final long WHITE                 = 0xffefefefl;
	public static final long BLACK                 = 0xff000000l;
	public static final long GREEN                 = 0xff8cef00l;
	public static final long RED                   = 0xffef0000l;
	public static final long ORANGE                = 0xffef6500l;
	public static final long PURPLE                = 0xffef00efl;
	public static final long ELECTRIC_BLUE         = 0xff0892d0l;
	public static final long DARK_ELECTRIC_BLUE    = 0xff085290l;	       	       	
	public static final long LIGHT_ORANGE          = 0xffeeacacl;        
	public static final long DARK_ORANGE           = 0xff885555l;        
	public static final long LIGHT_GREEN           = 0xff22ac22l;        
	public static final long DARK_GREEN            = 0xff115511l;        
	public static final long LIGHT_GRAY            = 0xff888888l;       
	public static final long DARK_GRAY             = 0xff444444l;       
	public static final long DARK_GRAY_MED_ALPHA   = 0xaa444444l;
	public static final long LIGHT_GRAY_MED_ALPHA  = 0xaa888888l;
	public static final long LIGHT_GREEN_LOW_ALPHA = 0x5500aa00l;
	public static final long DARK_GREEN_LOW_ALPHA  = 0x55007700l;
	
	@Override
	public long conditionGreen() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long conditionYellow() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long conditionRed() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long background() {
		return BLACK;
	}
	
	@Override
	public long baseInformation() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long mainText() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long informationText() {
		return DARK_ELECTRIC_BLUE;
	}
	
	@Override
	public long additionalText() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long message() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long warningMessage() {
		return ORANGE;
	}
	
	@Override
	public long fuelCircle() {
		return RED;
	}
	
	@Override
	public long dashedFuelCircle() {
		return BLUE;
	}
	
	@Override
	public long inhabitantInformation() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long arrow() {
		return GRAY;
	}
	
	@Override
	public long economy() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long government() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long techLevel() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long population() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long shipDistance() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long diameter() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long gnp() {
		return ELECTRIC_BLUE;
	}
		
	@Override
	public long currentSystemName() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long hyperspaceSystemName() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long legalStatus() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long remainingFuel() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long balance() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long rating() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long missionObjective() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long equipmentDescription() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long richIndustrial() {
		return BLUE;
	}

	@Override
	public long averageIndustrial() {
		return GREEN;
	}

	@Override
	public long poorIndustrial() {
		return PURPLE;
	}

	@Override
	public long mainIndustrial() {
		return RED;
	}

	@Override
	public long mainAgricultural() {
		return YELLOW;
	}

	@Override
	public long richAgricultural() {
		return WHITE;
	}

	@Override
	public long averageAgricultural() {
		return ORANGE;
	}

	@Override
	public long poorAgricultural() {
		return GRAY;
	}
	
	@Override
	public long backgroundLight() {
		return LIGHT_GRAY;
	}

	@Override
	public long backgroundDark() {
		return DARK_GRAY;
	}

	@Override
	public long frameLight() {
		return WHITE;
	}

	@Override
	public long frameDark() {
		return GRAY;
	}
	
	@Override
	public long coloredFrameLight() {
		return LIGHT_GRAY;
	}

	@Override
	public long coloredFrameDark() {
		return DARK_GRAY;
	}
	
	@Override
	public long selectedColoredFrameLight() {
		return LIGHT_ORANGE;
	}

	@Override
	public long selectedColoredFrameDark() {
		return DARK_ORANGE;
	}
	
	@Override
	public long activeColoredFrameLight() {
		return LIGHT_GREEN;
	}

	@Override
	public long activeColoredFrameDark() {
		return DARK_GREEN;
	}		
	
	@Override
	public long pulsingHighlighterDark() {
		return DARK_GREEN_LOW_ALPHA;
	}

	@Override
	public long pulsingHighlighterLight() {
		return LIGHT_GREEN_LOW_ALPHA;
	}
	
	@Override
	public long tutorialBubbleDark() {
		return DARK_GRAY_MED_ALPHA;
	}

	@Override
	public long tutorialBubbleLight() {
		return LIGHT_GRAY_MED_ALPHA;
	}
	
	@Override
	public long shipTitle() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long textAreaBackground() {
		return GRAY;
	}

	@Override
	public long cursor() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long price() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long scrollingText() {
		return ELECTRIC_BLUE;
	}
	
	@Override
	public long selectedText() {
		return ORANGE;
	}
	
	@Override
	public long frontShield(float value, float alpha) {
		return AliteColors.convertRgba((1 - value) * 0.93f, value * 0.73f, 0, alpha);
	}

	@Override
	public long aftShield(float value, float alpha) {
		return AliteColors.convertRgba((1 - value) * 0.93f, value * 0.73f, 0, alpha);
	}

	@Override
	public long fuel(float value, float alpha) {
		return AliteColors.convertRgba((1 - value) * 0.93f, value * 0.73f, 0, alpha);
	}

	@Override
	public long cabinTemperature(float value, float alpha) {
		return AliteColors.convertRgba(value * 0.93f, (1 - value) * 0.73f, 0, alpha);
	}

	@Override
	public long laserTemperature(float value, float alpha) {
		return AliteColors.convertRgba(value * 0.93f, (1 - value) * 0.73f, 0, alpha);
	}

	@Override
	public long altitude(float value, float alpha) {
		return AliteColors.convertRgba((1 - value) * 0.93f, value * 0.73f, 0, alpha);
	}

	@Override
	public long speed(float value, float alpha) {		
		return AliteColors.convertRgba((1 - value) * 0.93f, value * 0.73f, 0, alpha);
	}

	@Override
	public long energyBank(float value, float alpha) {
		return AliteColors.convertRgba(0, 0.73f, 0, alpha);
	}

	@Override
	public long lastEnergyBank(float value, float alpha) {
		return AliteColors.convertRgba((1 - value) * 0.93f, value * 0.73f, 0, alpha);
	}

	@Override
	public long indicatorBar(float alpha) {
		return AliteColors.convertRgba(0, 0.73f, 0, alpha);
	}
	
	@Override
	public long creditsDescription() {
		return DARK_ELECTRIC_BLUE;
	}

	@Override
	public long creditsPerson() {
		return ELECTRIC_BLUE;
	}

	@Override
	public long creditsAddition() {
		return DARK_ORANGE;
	}

	@Override
	public long highlightColor() {
		return DARK_GREEN_LOW_ALPHA;
	}
}
