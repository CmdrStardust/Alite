package de.phbouillon.android.games.alite.model.generator;

import java.util.Locale;

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

public class StringUtil {
	private StringUtil() {
		// Prevent instantiation
	}
	
	public static void addSpaceAndStringToBuilder(String stringToAdd, StringBuilder builder) {
		if (stringToAdd != null && stringToAdd.length() > 0) {
			if (builder.length() > 0 && builder.charAt(builder.length() - 1) != ' ') {
				builder.append(" ");
			}
			builder.append(stringToAdd);
		}
	}
	
	public static String readableCase(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		if (s.length() == 1) {
			return s;
		}
		return s.charAt(0) + s.substring(1).toLowerCase(Locale.US);		
	}
	
	/**
	 * Searches the given String for occurrences of asterisks ('*'). If an
	 * asterisk is found, it is removed along with any spaces before the asterisk.
	 * 
	 * @param temp the String to be analyzed and modified.
	 * @return a copy of the given String without white spaces and asterisks.
	 */
	public static String removeAdditionalWhitespaces(String temp) {
		int index = -1;
		int lastFound = -1;
		StringBuilder builder = new StringBuilder();
		while ((index = temp.indexOf("*", index + 1)) != -1) {
			builder.append(temp.substring(lastFound + 1, index - 1)); // delete whitespace
			lastFound = index;
		}
		builder.append(temp.substring(lastFound + 1));
		
		return builder.toString();		
	}
}
