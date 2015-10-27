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

import android.annotation.SuppressLint;
import android.graphics.Point;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.colors.AliteColors;

@SuppressLint("RtlHardcoded")
public class Button {
	private int xCoord;
	private int yCoord;
	private final int width;
	private final int height;
	private String text;
	private Pixmap pixmap;
	private Pixmap pushedBackground;
	private Pixmap [] animation;
	private final Pixmap [] overlay;
	private GLText font;
	private String navigationTarget;
	private TextPosition textPosition = TextPosition.ONTOP;
	private boolean useBorder = true;
	private boolean gradient = false;
	private boolean selected = false;
	private int xOffset = 0;
	private int yOffset = 0;
	private int buttonEnd = 0;
	private int fingerDown = 0;
	private long textColor = AliteColors.get().message();
	private long borderColorLt = AliteColors.get().backgroundLight();
	private long borderColorDk = AliteColors.get().backgroundDark();
	private int pixmapXOffset = 0;
	private int pixmapYOffset = 0;
	private boolean visible = true;
	private String name;
	
	public static enum TextPosition {
		ABOVE, LEFT, RIGHT, BELOW, ONTOP
	}
	
	public Button(int x, int y, int width, int height, String text, GLText font, String navigationTarget) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = width;
		this.height = height;
		this.text = text;
		this.font = font;
		this.pixmap = null;
		this.animation = null;
		this.overlay = null;
		this.navigationTarget = navigationTarget;
		register();
	}
	
	public Button(int x, int y, int width, int height, Pixmap pixmap) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = width;
		this.height = height;
		this.text = null;
		this.font = null;
		this.pixmap = pixmap;
		this.animation = null;
		this.overlay = null;
		this.navigationTarget = null;
		register();
	}
		
	public Button(int x, int y, int width, int height, Pixmap pixmap, Pixmap [] overlay) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = width;
		this.height = height;
		this.text = null;
		this.font = null;
		this.pixmap = pixmap;
		this.animation = null;
		this.overlay = overlay;
		this.navigationTarget = null;
		register();
	}

	public Button(int x, int y, int width, int height, Pixmap [] pixmap) {
		this.xCoord = x;
		this.yCoord = y;
		this.width = width;
		this.height = height;
		this.text = null;
		this.font = null;
		this.pixmap = pixmap[0];
		this.animation = pixmap;
		this.overlay = null;
		this.navigationTarget = null;
		register();
	}
	
	public void move(int newX, int newY) {
		xCoord = newX;
		yCoord = newY;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPixmapOffset(int x, int y) {
		pixmapXOffset = x;
		pixmapYOffset = y;
	}
	
	private final void register() {
		ButtonRegistry.get().addButton(Alite.getDefiningScreen(), this);
	}
	
	public void setTextColor(long textColor) {
		this.textColor = textColor;
	}

	public void setBorderColors(long light, long dark) {
		this.borderColorLt = light;
		this.borderColorDk = dark;
	}
	
	public void setColors(long textColor, long light, long dark) {
		this.textColor = textColor;
		this.borderColorLt = light;
		this.borderColorDk = dark;
	}
	
	public void setXOffset(int x) {
		xOffset = x;
	}
	
	public void setYOffset(int y) {
		yOffset = y;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setFont(GLText font) {
		this.font = font;
	}
	
	public void setTextPosition(TextPosition position) {
		this.textPosition = position;
	}
	
	public void setUseBorder(boolean useBorder) {
		this.useBorder = useBorder;
	}
		
	public void setGradient(boolean gradient) {
		this.gradient = gradient;
	}
	
	public void setAnimation(Pixmap [] pixmap) {
		this.animation = pixmap;
	}
	
	public void setPixmap(Pixmap pixmap) {
		this.pixmap = pixmap;
	}
	
	public void setPushedBackground(Pixmap pixmap) {
		this.pushedBackground = pixmap;
	}
	
	public String getText() {
		return text;
	}
	
	public void fingerDown(int pointer) {
		int val = 1 << pointer;
		if ((fingerDown & val) == 0) {
			fingerDown += val;
		}
	}
	
	public void fingerUp(int pointer) {
		int val = 1 << pointer;
		if ((fingerDown & val) != 0) {
			fingerDown -= val;
		}
	}
	
	public void render(Graphics g) {
		render(g, 0);
	}
	
	private Point calculateTextPosition(Graphics g) {
		int halfWidth  = g.getTextWidth(text, font) >> 1;
		int halfHeight = g.getTextHeight(text, font) >> 1;
		
		TextPosition local = textPosition;
		if (pixmap == null && animation == null) {
			local = TextPosition.ONTOP;
		}
		Point result = new Point();
		
		int x = xCoord + xOffset;
		int y = yCoord + yOffset;
		switch (local) {		  
			case ABOVE: result.y = y;
						result.x = x + (width >> 1) - halfWidth;
			            break;
			            
			case LEFT:  result.x = x;
						result.y = (int) (y + (height >> 1) - halfHeight + font.getSize());
				        break;
				        
			case RIGHT: int l = x + pixmap.getWidth();
			            int r = x + width;
				        result.x = l + ((r - l) >> 1) - halfWidth;
				        result.y = (int) (y + (height >> 1) - halfHeight + font.getSize());
				        break;
				        
			case BELOW: result.y = y + height - (halfHeight << 1);
				        result.x = x + (width >> 1) - halfWidth;
			            break;
			            
			case ONTOP: result.x = x + (width >> 1) - halfWidth;
			            result.y = (int) (y + (height >> 1) - halfHeight + font.getSize());
			            break;
		}
		
		return result;
	}
	
	public void setButtonEnd(int pixel) {
		buttonEnd = pixel;
	}
	
	public void render(Graphics g, int frame) {
		if (!visible) {
			return;
		}
		int x = xCoord + xOffset;
		int y = yCoord + yOffset;
		if (gradient && useBorder) {
			g.gradientRect(x + 5, y + 5, width - 10, height - 10, true, true, borderColorLt, borderColorDk);
		} 			
		if (frame > 0 && animation != null && frame < animation.length && animation[frame] != null) {
			g.drawPixmap(animation[frame], x, y);
		} else {
			if (pixmap != null) {
				if (buttonEnd == 0) {
					g.drawPixmap(fingerDown == 0 || pushedBackground == null ? pixmap : pushedBackground, x + pixmapXOffset, y + pixmapYOffset);
				} else {
					int x1 = (int) (x * AndroidGame.scaleFactor);
					int y1 = (int) (y * AndroidGame.scaleFactor);
					int x2 = (int) ((float) x1 + (((float) width - (float) buttonEnd) * AndroidGame.scaleFactor) + 1.0f);
					int y2 = (int) (y1 + height * AndroidGame.scaleFactor);
					g.drawPixmapUnscaled(fingerDown == 0 || pushedBackground == null ? pixmap : pushedBackground, x1, y1, 0, 0, x2 - x1 + 1, y2 - y1 + 1);
					g.drawPixmapUnscaled(fingerDown == 0 || pushedBackground == null ? pixmap : pushedBackground, x2, y1, (int) (pixmap.getWidth() - buttonEnd * AndroidGame.scaleFactor), 0, -1, y2 - y1 + 1);
				}
			}
			if (frame > 0 && overlay != null && frame < overlay.length) {
				g.drawPixmap(overlay[frame], x, y);
			}
		}
		if (useBorder) {
			if (fingerDown == 0) {
				g.rec3d(x, y, width, height, 5, borderColorLt, borderColorDk);
			} else {
				g.rec3d(x, y, width, height, 5, borderColorDk, borderColorLt);
			}
		}
		if (text != null) {
			Point p = calculateTextPosition(g);
			g.drawText(text, p.x, p.y, textColor, font);
		}
	}
	
	public boolean isTouched(int x, int y) {
		if (!visible) {
			return false;
		}
		return x >= (this.xCoord + xOffset) && x <= (this.xCoord + xOffset + width - 1) &&
			   y >= (this.yCoord + yOffset) && y <= (this.yCoord + yOffset + height - 1);
	}
	
	public String getNavigationTarget() {
		return navigationTarget;
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
	
	public void setSelected(boolean sel) {
		selected = sel;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public Pixmap getPixmap() {
		return pixmap;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean v) {
		visible = v;
	}
}
