package de.phbouillon.android.framework;

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

import android.graphics.ColorFilter;
import de.phbouillon.android.framework.impl.gl.font.GLText;

public interface Graphics {
	public static enum PixmapFormat {
		ARGB8888, ARGB4444, RGB565
	}
	
	public Pixmap newPixmap(String fileName, boolean scale);
	public boolean existsAssetsFile(String fileName);
	public void clear(long color);
	public void drawLine(int x, int y, int x2, int y2, long color);
	public void drawRect(int x, int y, int width, int height, long color);
	public void fillRect(int x, int y, int width, int height, long color);
	public void rec3d(int x, int y, int width, int height, int borderSize, long lightColor, long darkColor);
	public void gradientRect(int x, int y, int width, int height, boolean horizontal, boolean vertical, long color1, long color2);
	public void drawCircle(int cx, int cy, int r, long color, int segments);
	public void fillCircle(int cx, int cy, int r, long color, int segments);
	public void drawText(String text, int x, int y, long color, GLText font);
	public int getTextWidth(String text, GLText font);
	public int getTextHeight(String text, GLText font);	
	public void drawPixmap(Pixmap pixmap, int x, int y);
	public void applyFilterToPixmap(Pixmap pixmap, ColorFilter filter);
	public void drawPixmapUnscaled(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight);
	public void setClip(int x1, int y1, int x2, int y2);
	public void drawDashedCircle(int cx, int cy, int r, long color, int segments);
}
