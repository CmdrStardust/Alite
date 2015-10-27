package de.phbouillon.android.games.alite.oxp;

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

import java.util.Locale;

import android.graphics.Color;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSNumber;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;

import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.colors.AliteColors;

public class ColorParser {
	public static long getColorFromOXP(NSObject colorDef) {
		if (colorDef instanceof NSDictionary) {
			return getColorFromOXPDict((NSDictionary) colorDef);
		} else if (colorDef instanceof NSString) {
			return getColorFromOXPString((NSString) colorDef);
		} else if (colorDef instanceof NSArray) {
			return getColorFromOXPArray((NSArray) colorDef);
		}
		AliteLog.e("Unknown color definition", "ColorDef is unknown: " + colorDef);
		return 0;
	}
	
	private static float getAlpha(NSDictionary dictionary) {
		if (dictionary.containsKey("alpha")) {
			return ((NSNumber) dictionary.get("alpha")).floatValue();
		}
		if (dictionary.containsKey("opacity")) {
			return ((NSNumber) dictionary.get("opacity")).floatValue();			
		}
		return 1.0f;
	}
	
	private static float getValue(NSDictionary dictionary) {
		if (dictionary.containsKey("value")) {
			return ((NSNumber) dictionary.get("value")).floatValue();
		}
		if (dictionary.containsKey("brightness")) {
			return ((NSNumber) dictionary.get("brightness")).floatValue();			
		}
		return 1.0f;
	}

	private static long getColorFromOXPDict(NSDictionary dictionary) {
		if (dictionary.containsKey("hue")) {
			float h = ((NSNumber) dictionary.get("hue")).floatValue();
			float s = dictionary.containsKey("saturation") ? ((NSNumber) dictionary.get("saturation")).floatValue() : 1.0f;
			float v = getValue(dictionary);
			float a = getAlpha(dictionary);
			if (a <= 1.0f) {
				a *= 255.0f;
			}
			return Color.HSVToColor((int) a, new float [] {h, s, v});
		}
		float r = dictionary.containsKey("red")   ? ((NSNumber) dictionary.get("red")).floatValue()   : 0.0f;
		float g = dictionary.containsKey("green") ? ((NSNumber) dictionary.get("green")).floatValue() : 0.0f;
		float b = dictionary.containsKey("blue")  ? ((NSNumber) dictionary.get("blue")).floatValue()  : 0.0f;
		float a = getAlpha(dictionary);
		return AliteColors.convertRgba(r, g, b, a);
	}
	
	private static float parseStringColor(String color) {
		float colorValue = 0;
		try {
			colorValue = Float.parseFloat(color);
		} catch (NumberFormatException e) {				
		}
		return colorValue;
	}
	
	private static long getColorFromOXPString(NSString colorString) {
		String color = colorString.getContent();
		String [] colorValues = color.split(" ");
		if (colorValues.length == 1) {
			// Check for color names
			String c = color.toLowerCase(Locale.getDefault()).trim();
			if (c.endsWith("color")) {
				if ("blackcolor".equals(c)) return AliteColors.convertRgba(0, 0, 0, 1);
				if ("darkgraycolor".equals(c)) return AliteColors.convertRgba(0.33f, 0.33f, 0.33f, 1);
				if ("lightgraycolor".equals(c)) return AliteColors.convertRgba(0.66f, 0.66f, 0.66f, 1);
				if ("whitecolor".equals(c)) return AliteColors.convertRgba(1, 1, 1, 1);
				if ("graycolor".equals(c)) return AliteColors.convertRgba(0.5f, 0.5f, 0.5f, 1);
				if ("redcolor".equals(c)) return AliteColors.convertRgba(1, 0, 0, 1);
				if ("greencolor".equals(c)) return AliteColors.convertRgba(0, 1, 0, 1);
				if ("bluecolor".equals(c)) return AliteColors.convertRgba(0, 0, 1, 1);
				if ("cyancolor".equals(c)) return AliteColors.convertRgba(0, 1, 1, 1);
				if ("yellowcolor".equals(c)) return AliteColors.convertRgba(1, 1, 0, 1);
				if ("magentacolor".equals(c)) return AliteColors.convertRgba(1, 0, 1, 1);
				if ("orangecolor".equals(c)) return AliteColors.convertRgba(1, 0.5f, 0, 1);
				if ("purplecolor".equals(c)) return AliteColors.convertRgba(0.5f, 0, 0.5f, 1);
				if ("browncolor".equals(c)) return AliteColors.convertRgba(0.6f, 0.4f, 0.2f, 1);
				if ("clearcolor".equals(c)) return AliteColors.convertRgba(0, 0, 0, 0);
			}
			float r = parseStringColor(colorValues[0]);
			if (r > 1) {
				r /= 255.0f;
			}
			return AliteColors.convertRgba(r, 0, 0, 1);
		}
		float r = 0, g = 0, b = 0, a = 1;
		if (colorValues.length > 0) {
			r = parseStringColor(colorValues[0]);
		}
		if (colorValues.length > 1) {
			g = parseStringColor(colorValues[1]);
		}

		if (colorValues.length > 2) {
			b = parseStringColor(colorValues[2]);
		}

		if (colorValues.length > 3) {
			a = parseStringColor(colorValues[3]);
		}
		if (r <= 1.0f && g <= 1.0f && b <= 1.0f && a <= 1.0f) {
			return AliteColors.convertRgba(r, g, b, a);
		} 
		if (colorValues.length <= 3) {
			a = 255.0f;
		}
		return AliteColors.convertRgba(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}
	
	private static long getColorFromOXPArray(NSArray colorArray) {
		NSObject[] array = colorArray.getArray();
		float r = array.length > 0 ? ((NSNumber) array[0]).floatValue() : 0;
		float g = array.length > 1 ? ((NSNumber) array[1]).floatValue() : 0;
		float b = array.length > 2 ? ((NSNumber) array[2]).floatValue() : 0;
		float a = array.length > 3 ? ((NSNumber) array[3]).floatValue() : 1;
		
		if (r <= 1.0f && g <= 1.0f && b <= 1.0f && a <= 1.0f) {
			return AliteColors.convertRgba(r, g, b, a);
		} 
		a = array.length > 3 ? ((NSNumber) array[3]).floatValue() : 255.0f;
		return AliteColors.convertRgba(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}
}
