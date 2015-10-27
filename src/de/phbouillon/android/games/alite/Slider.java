package de.phbouillon.android.games.alite;

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
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.colors.AliteColors;

public class Slider {
	private static final int SLIDER_SIZE = 40;
	
	private final int xCoord;
	private final int yCoord;
	private final int width;
	private final int height;
	private final float minValue;
	private final float maxValue;
	private float currentValue;
	private String text;
	private GLText font;
	private long textColor = AliteColors.get().message();
	private long lightScaleColor = AliteColors.get().backgroundLight();
	private long darkScaleColor = AliteColors.get().backgroundDark();
	private long tickColor = AliteColors.get().message();
	private final int scaleHeight;
	private final int numberOfTicks;
	private final int [] tickX;	
	private int fingerDown = 0;
	private String minText;
	private String maxText;
	private String middleText;
	private boolean currentValueChanged = false;
	
	public Slider(int x, int y, int width, int height, float minValue, float maxValue, float currentValue, String text, GLText font) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = font;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.currentValue = currentValue;
		scaleHeight = Math.min(height >> 1, 10);
		numberOfTicks = 8;
		tickX = new int[numberOfTicks + 1];
		float cPos = x;
		int current = 0;
		while (cPos < (x + width) && current <= numberOfTicks) {
			tickX[current++] = (int) cPos;
			cPos = (width / numberOfTicks) * current + x;
		}
		tickX[numberOfTicks] = x + width - 1;
	}
		
	public void setScaleTexts(String min, String max, String middle) {
		this.minText = min;
		this.maxText = max;
		this.middleText = middle;
	}
	
	public void setTextColor(long textColor) {
		this.textColor = textColor;
	}

	public void setScaleColors(long lightScale, long darkScale, long tick) {
		this.lightScaleColor = lightScale;
		this.darkScaleColor = darkScale;
		this.tickColor = tick;
	}
	
	public void setColors(long textColor, long lightScale, long darkScale, long tick) {
		this.textColor = textColor;
		this.lightScaleColor = lightScale;
		this.darkScaleColor = darkScale;
		this.tickColor = tick;
	}
		
	public void setText(String text) {
		this.text = text;
	}
	
	public void setFont(GLText font) {
		this.font = font;
	}
		
	public String getText() {
		return text;
	}
	
	private void fingerDown(int pointer) {
		int val = 1 << pointer;
		if ((fingerDown & val) == 0) {
			fingerDown += val;
		}
	}
	
	private void fingerUp(int pointer) {
		int val = 1 << pointer;		
		if ((fingerDown & val) != 0) {
			fingerDown -= val;
		}
	}
	
	private boolean isDown(int pointer) {
		return (fingerDown & (1 << pointer)) != 0;
	}
	
	private void renderScale(Graphics g) {
		int middle = yCoord + (height >> 1);
		g.rec3d(xCoord, middle - (scaleHeight >> 1), width, scaleHeight, 2, lightScaleColor, darkScaleColor);
		for (int x: tickX) {
			g.drawLine(x, middle - scaleHeight - 3, x, middle + scaleHeight + 3, tickColor);
		}
		g.drawText(text + ": " + String.format("%3.2f", currentValue), xCoord + (width >> 1) - (g.getTextWidth(text, font) >> 1), middle - 20, textColor, font);
		if (minText != null) {
			int x = tickX[0] - (g.getTextWidth(minText, Assets.regularFont) >> 1);
			g.drawText(minText, x, middle + scaleHeight + 30, textColor, Assets.regularFont);
		}
		if (middleText != null) {
			int x = tickX[numberOfTicks >> 1] - (g.getTextWidth(middleText, Assets.regularFont) >> 1);
			g.drawText(middleText, x, middle + scaleHeight + 30, textColor, Assets.regularFont);
		}
		if (maxText != null) {
			int x = tickX[numberOfTicks] - (g.getTextWidth(maxText, Assets.regularFont) >> 1);
			g.drawText(maxText, x, middle + scaleHeight + 30, textColor, Assets.regularFont);
		}
	}
	
	private void renderPointer(Graphics g) {
		int middleX = (int) (((currentValue - minValue) / maxValue) * width + xCoord);
		int middleY = yCoord + (height >> 1);
		g.fillCircle(middleX, middleY, SLIDER_SIZE, tickColor, 32);
	}
	
	public void render(Graphics g) {
		renderScale(g);
		renderPointer(g);
	}

	private void modifyValue(int xVal) {
		if (xVal < xCoord) {
			xVal = xCoord;
		}
		if (xVal >= xCoord + width) {
			xVal = xCoord + width;
		}
		currentValue = ((float) xVal - (float) xCoord) / (float) width;
		if (currentValue < minValue) {
			currentValue = minValue;
		}
		if (currentValue > maxValue) {
			currentValue = maxValue;
		}
		currentValueChanged = true;		
	}
	
	public boolean checkEvent(TouchEvent e) {
		if (e.type == TouchEvent.TOUCH_DRAGGED) {
			if (isDown(e.pointer)) {
				modifyValue(e.x);
				return true;
			} 
		}
		if (e.type == TouchEvent.TOUCH_DOWN) {
			if (e.x >= xCoord && e.x <= xCoord + width) {
				if (e.y >= yCoord && e.y <= yCoord + height) {
					fingerDown(e.pointer);
					modifyValue(e.x);
					return false;
				}
			}
			int middleX = (int) (((currentValue - minValue) / maxValue) * width + xCoord);
			int middleY = yCoord + (height >> 1);
			if (e.x >= middleX - SLIDER_SIZE && e.x <= middleX + SLIDER_SIZE) {
				if (e.y >= middleY - SLIDER_SIZE && e.y <= middleY + SLIDER_SIZE) {
					fingerDown(e.pointer);
					return false;
				}
			}
		}
		if (e.type == TouchEvent.TOUCH_UP) {
			if (isDown(e.pointer)) {
				fingerUp(e.pointer);
				boolean result = currentValueChanged;
				currentValueChanged = false;
				return result;
			}
		}
		return false;
	}
					
	public int getX() {
		return xCoord;
	}
	
	public int getY() {
		return yCoord;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}	
	
	public float getCurrentValue() {
		return currentValue;
	}
}
