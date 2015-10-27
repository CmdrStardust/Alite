package de.phbouillon.android.games.alite.screens.opengl.ingame;

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

import java.io.Serializable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteFont;

public class ScrollingText implements Serializable {
	private static final long serialVersionUID = -9124382771601908756L;

	private final String textToDisplay;
	private float x = 1920.0f;
	private int width;
	private final AliteFont font;
	
	ScrollingText(Alite alite) {
		this.font = alite.getFont();
		long diffInSeconds = TimeUnit.SECONDS.convert(alite.getGameTime(), TimeUnit.NANOSECONDS);
		int diffInDays = (int) (diffInSeconds / 86400);
		diffInSeconds -= (diffInDays * 86400);
		int diffInHours = (int) (diffInSeconds / 3600);
		diffInSeconds -= (diffInHours * 3600);
		int diffInMinutes = (int) (diffInSeconds / 60);
		String gameTime = String.format(Locale.getDefault(), 
				"%d day" + (diffInDays == 1 ? "" : "s") + 
				", %d hour" + (diffInHours == 1 ? "" : "s") + 
				", and %d minute" + (diffInMinutes == 1 ? "" : "s"), diffInDays, diffInHours, diffInMinutes);		

		textToDisplay = "Alite v" + Alite.VERSION_STRING + 
						" - by Philipp Bouillon and Duane McDonnell. You have played for " + gameTime +
						" and you have scored " + alite.getPlayer().getScore() + " points. ";
		width = (int) font.getWidth(textToDisplay, 1.5f);
	}
	
	void render(float deltaTime) {
		x -= deltaTime * 128.0f;
		AliteColors.setGlColor(AliteColors.get().scrollingText());
		font.drawText(textToDisplay, (int) x, 400, false, 1.5f);
		if (x + width < -20) {
			x = 1920.0f;
		}
	}
}
