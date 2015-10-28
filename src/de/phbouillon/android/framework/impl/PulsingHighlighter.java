package de.phbouillon.android.framework.impl;

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

import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.games.alite.Alite;

public class PulsingHighlighter {
	private static final long PULSE_UPDATE_FREQUENCY = 30000000l; // 0.3 s
	
	private final Alite alite;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final int delta;
	private final long lightColor;
	private final long darkColor;
	private int currentDelta;
	private long lastDraw = -1;
	private int expansion = 1;
	
	public PulsingHighlighter(final Alite alite, int x, int y, int width, int height, int delta, long lightColor, long darkColor) {
		this.alite = alite;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.delta = delta;
		this.lightColor = lightColor;
		this.darkColor = darkColor;
		currentDelta = 0;
	}
	
	public void display(float deltaTime) {
		Graphics g = alite.getGraphics();
		g.gradientRect(x - currentDelta, y - currentDelta, width + 2 * currentDelta, height + 2 * currentDelta, true, true, lightColor, darkColor);
		g.rec3d(x - currentDelta, y - currentDelta, width + 2 * currentDelta, height + 2 * currentDelta, 3, darkColor, lightColor);
		if (lastDraw == -1 || (System.nanoTime() - lastDraw) > PULSE_UPDATE_FREQUENCY) {
			lastDraw = System.nanoTime();
			currentDelta += expansion;
			if (currentDelta < 0 || currentDelta > delta) {
				expansion = -expansion;				
			}
		}
	}
}
