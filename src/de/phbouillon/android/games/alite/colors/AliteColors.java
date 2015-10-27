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

import de.phbouillon.android.games.alite.Settings;
import android.opengl.GLES11;

public class AliteColors {	
	private static AliteColors instance = new AliteColors();
	
	private ColorScheme colorScheme; 
	
	public AliteColors() {
		switch (Settings.colorScheme) {
			case 0: colorScheme = new ClassicColorScheme(); break;
			case 1: colorScheme = new ModernColorScheme(); break;
			default: colorScheme = new ClassicColorScheme(); break;
		}
	}
	
	public static ColorScheme get() {
		return instance.colorScheme;
	}
	
	public static void update() {
		instance = new AliteColors();
	}
	
	public static void setGlColor(long color) {
		int alpha = (int) ((color & 0xFF000000l) >> 24);
		int red   = (int) (color & 0x00FF0000) >> 16;
		int green = (int) (color & 0x0000FF00) >>  8;
		int blue  = (int) (color & 0x000000FF);
		
		GLES11.glColor4f(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
	}	

	public static void setGlColor(long color, float modifier) {
		int alpha = (int) ((color & 0xFF000000l) >> 24);
		int red   = (int) (color & 0x00FF0000) >> 16;
		int green = (int) (color & 0x0000FF00) >>  8;
		int blue  = (int) (color & 0x000000FF);
		
		GLES11.glColor4f(red / 255.0f * modifier, green / 255.0f * modifier, blue / 255.0f * modifier, alpha / 255.0f * modifier);
	}	
	
	public static long convertRgba(float r, float g, float b, float a) {
		return (((int) (a * 255.0f)) << 24) +
			   (((int) (r * 255.0f)) << 16) +
			   (((int) (g * 255.0f)) <<  8) +
			    ((int) (b * 255.0f));
	}
}
