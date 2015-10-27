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

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES11;
import android.opengl.GLSurfaceView;
import de.phbouillon.android.framework.FileIO;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.MemUtil;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.screens.opengl.TextureManager;

public class AndroidGraphics implements Graphics {
	private GLSurfaceView glView;
	private final float scaleFactor;
	private final Rect visibleArea;
	private final FloatBuffer lineBuffer;
	private final FloatBuffer rectBuffer;
	private final FloatBuffer circleBuffer;
	private final FloatBuffer colorBuffer;
	private final TextureManager textureManager;
	private final Canvas converterCanvas = new Canvas();
	private final Paint paint = new Paint();
	private final Canvas canvas = new Canvas();
	private final Paint filterPaint = new Paint();
	private final Matrix scaleMatrix = new Matrix();
	private final Options options = new Options();
	private final FileIO fileIO;
	
	public AndroidGraphics(FileIO fileIO, float scaleFactor, Rect visibleArea, TextureManager textureManager) {
		this.fileIO = fileIO;
		this.scaleFactor = scaleFactor;
		this.visibleArea = visibleArea;
		lineBuffer = GlUtils.allocateFloatBuffer(2 * 8);
		rectBuffer = GlUtils.allocateFloatBuffer(4 * 8);
		circleBuffer = GlUtils.allocateFloatBuffer(64 * 8);
		colorBuffer = GlUtils.allocateFloatBuffer(4 * 16);
		this.textureManager = textureManager;
		filterPaint.setFilterBitmap(true);
	}
		
	public final Rect getVisibleArea() {
		return visibleArea;
	}
	
	private int transX(int x) {
		return (int) (scaleFactor * x + visibleArea.left);
	}
	
	private int transY(int y) {
		return (int) (scaleFactor * y + visibleArea.top);
	}
			
	public void setGlView(GLSurfaceView glView) {
		this.glView = glView;
	}
	
	public GLSurfaceView getGlView() {
		return glView;
	}
	
	@Override
	public boolean existsAssetsFile(String fileName) {
		try {
			return fileIO.existsPrivateFile(fileName);
		} catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public Pixmap newPixmap(String fileName, final boolean scale) {
		Config config = Settings.colorDepth == 1 ? Config.ARGB_8888 : Config.ARGB_4444;
		options.inPreferredConfig = config;
		options.inSampleSize = Settings.textureLevel;
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = fileIO.readPrivateFile(fileName);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		float tx2 = 1.0f, ty2 = 1.0f;
		float newWidth = bitmap.getWidth() * Settings.textureLevel;
		float newHeight = bitmap.getHeight() * Settings.textureLevel;
		if (scale) {
			// adjust bitmap size
			newWidth = bitmap.getWidth() * Settings.textureLevel * scaleFactor;
			newHeight = bitmap.getHeight() * Settings.textureLevel * scaleFactor;
			int textureWidth = determineTextureSize((int) newWidth);
			int textureHeight = determineTextureSize((int) newHeight);
			tx2 = (float) newWidth / (float) textureWidth;
			ty2 = (float) newHeight / (float) textureHeight;
			Bitmap scaledBitmap = Bitmap.createBitmap(textureWidth, textureHeight, bitmap.getConfig());
		
			float ratioX = newWidth / (float) bitmap.getWidth();
			float ratioY = newHeight / (float) bitmap.getHeight();
			float middleX = newWidth / 2.0f;
			float middleY = newHeight / 2.0f;

			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
			
			canvas.setBitmap(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, filterPaint);

			MemUtil.freeBitmap(bitmap);
			bitmap = scaledBitmap;
		}
				
		PixmapFormat format;
		if (bitmap.getConfig() == Config.RGB_565) {
			format = PixmapFormat.RGB565;
		} else if (bitmap.getConfig() == Config.ARGB_4444) {
			format = PixmapFormat.ARGB4444;
		} else {
			format = PixmapFormat.ARGB8888;
		}
		AndroidPixmap pm = new AndroidPixmap(bitmap, format, fileName, textureManager, (int) newWidth, (int) newHeight, tx2, ty2);
		return pm;
	}

	public Pixmap newPixmap(Bitmap bitmap, String fileName) {
		PixmapFormat format;
		float newWidth = bitmap.getWidth();
		float newHeight = bitmap.getHeight();
		int textureWidth = determineTextureSize((int) newWidth);
		int textureHeight = determineTextureSize((int) newHeight);
		float tx2 = (float) newWidth / (float) textureWidth;
		float ty2 = (float) newHeight / (float) textureHeight;
		Bitmap scaledBitmap = Bitmap.createBitmap(textureWidth, textureHeight, bitmap.getConfig());
		
		float ratioX = newWidth / (float) bitmap.getWidth();
		float ratioY = newHeight / (float) bitmap.getHeight();
		float middleX = newWidth / 2.0f;
		float middleY = newHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
		MemUtil.freeBitmap(bitmap);
		bitmap = scaledBitmap;
		
		if (bitmap.getConfig() == Config.RGB_565) {
			format = PixmapFormat.RGB565;
		} else if (bitmap.getConfig() == Config.ARGB_4444) {
			format = PixmapFormat.ARGB4444;
		} else {
			format = PixmapFormat.ARGB8888;
		}
		AndroidPixmap pm = new AndroidPixmap(bitmap, format, fileName, textureManager, (int) newWidth, (int) newHeight, tx2, ty2);
		return pm;
	}
	
	private final int determineTextureSize(int size) {
		int tSize = 64;
		while (tSize < size && tSize < 2048) {
			tSize <<= 1;
		}
		return tSize;
	}

	private final void setGlColor(long color) {
		int alpha = (int) ((color & 0xFF000000l) >> 24);
		int red   = (int) (color & 0x00FF0000) >> 16;
		int green = (int) (color & 0x0000FF00) >>  8;
		int blue  = (int) (color & 0x000000FF);
		
		if (alpha <= 0) {
			AliteLog.e("Color Alpha value " + alpha + " detected.", "Stack trace following for 0 alpha value in color.");
			Thread.dumpStack();
		}
		GLES11.glColor4f(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
	}
	
	@Override
	public void clear(long color) {
		int alpha = (int) ((color & 0xFF000000l) >> 24);
		int red   = (int) (color & 0x00FF0000) >> 16;
		int green = (int) (color & 0x0000FF00) >>  8;
		int blue  = (int) (color & 0x000000FF);
		
		GLES11.glClearColor(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f);
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void drawLine(int x, int y, int x2, int y2, long color) {
		x = transX(x);
		y = transY(y);
		x2 = transX(x2);
		y2 = transY(y2);
		
		GLES11.glLineWidth(5 * scaleFactor);
		setGlColor(color);
		lineBuffer.clear();
		lineBuffer.put(x);
		lineBuffer.put(y);
		lineBuffer.put(x2);
		lineBuffer.put(y2);
		lineBuffer.position(0);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, lineBuffer);
		GLES11.glDrawArrays(GLES11.GL_LINES, 0, 2);
		GLES11.glLineWidth(1);
	}

	@Override
	public void drawRect(int x, int y, int width, int height, long color) {
		int x2 = transX(x + width - 1);
		int ty2 = transY(y + height - 1);
		x = transX(x);
		y = transY(y);
		int ty = y < 0 ? 0 : y;
		if (ty2 < 0) {
			return;
		}

		GLES11.glLineWidth(5 * scaleFactor);
		setGlColor(color);
		rectBuffer.clear();
		rectBuffer.put(x);
		rectBuffer.put(ty);
		rectBuffer.put(x2);
		rectBuffer.put(ty);
		rectBuffer.put(x2);
		rectBuffer.put(ty2);
		rectBuffer.put(x);
		rectBuffer.put(ty2);
		rectBuffer.position(0);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, rectBuffer);
		GLES11.glDrawArrays(GLES11.GL_LINE_LOOP, 0, 4);
		GLES11.glLineWidth(1);
	}

	@Override
	public void rec3d(int x, int y, int width, int height, int borderSize, long lightColor, long darkColor) {
		for (int i = 0; i < borderSize; i++) {
			drawLine(x + i, y + i, x + i, y + height - 1 - i, lightColor);
			drawLine(x + i, y + i, x + width - 1 - i, y + i, lightColor);
			drawLine(x + width - 1 - i, y + i, x + width - 1 - i, y + height - 1, darkColor);
			drawLine(x + i, y + height - 1 - i, x + width - 1 - i, y + height - 1 - i, darkColor);
		}		
	}
	
	@Override
	public void fillRect(int x, int y, int width, int height, long color) {
		int x2 = transX(x + width - 1);
		int ty2 = transY(y + height - 1);
		x = transX(x);
		y = transY(y);
		int ty = y < 0 ? 0 : y;
		if (ty2 < 0) {
			return;
		}
		
		setGlColor(color);
		rectBuffer.clear();
		rectBuffer.put(x);
		rectBuffer.put(ty);
		rectBuffer.put(x2);
		rectBuffer.put(ty);
		rectBuffer.put(x2);
		rectBuffer.put(ty2);
		rectBuffer.put(x);
		rectBuffer.put(ty2);
		rectBuffer.position(0);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, rectBuffer);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, 4);
	}
	
	@Override
	public void gradientRect(int x, int y, int width, int height, boolean horizontal, boolean vertical, long color1, long color2) {
		int x2 = transX(x + width - 1);
		int ty2 = transY(y + height - 1);
		x = transX(x);
		y = transY(y);
		int ty = y < 0 ? 0 : y;
		if (ty2 < 0) {
			return;
		}
		
		float alpha1 = (float) ((int) ((color1 & 0xFF000000l) >> 24)) / 255.0f;
		float red1   = ((color1 & 0x00FF0000) >> 16) / 255.0f;
		float green1 = ((color1 & 0x0000FF00) >>  8) / 255.0f;
		float blue1  =  (color1 & 0x000000FF)        / 255.0f;
		float alpha2 = (float) ((int) ((color2 & 0xFF000000l) >> 24)) / 255.0f;
		float red2   = ((color2 & 0x00FF0000) >> 16) / 255.0f;
		float green2 = ((color2 & 0x0000FF00) >>  8) / 255.0f;
		float blue2  =  (color2 & 0x000000FF)        / 255.0f;

		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
		rectBuffer.clear();
		rectBuffer.put(x);
		rectBuffer.put(ty);
		rectBuffer.put(x2);
		rectBuffer.put(ty);
		rectBuffer.put(x2);
		rectBuffer.put(ty2);
		rectBuffer.put(x);
		rectBuffer.put(ty2);
		rectBuffer.position(0);
		colorBuffer.clear();
		colorBuffer.put(red1); colorBuffer.put(green1); colorBuffer.put(blue1); colorBuffer.put(alpha1);
		colorBuffer.put(horizontal ? red2 : red1); colorBuffer.put(horizontal ? green2 : green1); colorBuffer.put(horizontal ? blue2 : blue1); colorBuffer.put(horizontal ? alpha2 : alpha1);
		colorBuffer.put(red2); colorBuffer.put(green2); colorBuffer.put(blue2); colorBuffer.put(alpha2);
		colorBuffer.put(vertical ? red2 : red1); colorBuffer.put(vertical ? green2 : green1); colorBuffer.put(vertical ? blue2 : blue1); colorBuffer.put(vertical ? alpha2 : alpha1);
		colorBuffer.position(0);
		GLES11.glColorPointer(4, GLES11.GL_FLOAT, 0, colorBuffer);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, rectBuffer);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, 4);
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
	}
	
	@Override
	public void fillCircle(int cx, int cy, int r, long color, int segments) {
		if (segments > 64) {
			segments = 64;
		}
		cx = transX(cx);
		cy = transY(cy);
		r = (int) (r * scaleFactor);

		circleBuffer.clear();
		float step = 360.0f / segments;
		for (float i = 0; i < 360.0f; i += step) {
			float ang = (float) Math.toRadians(i);
			circleBuffer.put((float) (cx + Math.cos(ang) * r));
			circleBuffer.put((float) (cy + Math.sin(ang) * r));
		}
		circleBuffer.position(0);
		setGlColor(color);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, circleBuffer);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_FAN, 0, segments);
	}

	@Override
	public void drawCircle(int cx, int cy, int r, long color, int segments) {
		if (segments > 64) {
			segments = 64;
		}
		cx = transX(cx);
		cy = transY(cy);
		r = (int) (r * scaleFactor);

		circleBuffer.clear();
		float step = 360.0f / segments;
		for (float i = 0; i < 360.0f; i += step) {
			float ang = (float) Math.toRadians(i);
			circleBuffer.put((float) (cx + Math.cos(ang) * r));
			circleBuffer.put((float) (cy + Math.sin(ang) * r));
		}
		circleBuffer.position(0);
		setGlColor(color);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, circleBuffer);
		GLES11.glDrawArrays(GLES11.GL_LINE_LOOP, 0, segments);
	}
	
	@Override
	public void drawDashedCircle(int cx, int cy, int r, long color, int segments) {
		if (segments > 64) {
			segments = 64;
		}
		cx = transX(cx);
		cy = transY(cy);
		r = (int) (r * scaleFactor);

		circleBuffer.clear();
		float step = 360.0f / segments;
		for (float i = 0; i < 360.0f; i += step) {
			float ang = (float) Math.toRadians(i);
			circleBuffer.put((float) (cx + Math.cos(ang) * r));
			circleBuffer.put((float) (cy + Math.sin(ang) * r));
		}
		circleBuffer.position(0);
		setGlColor(color);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, circleBuffer);
		GLES11.glDrawArrays(GLES11.GL_LINES, 0, segments);
	}

	@Override
	public void drawPixmapUnscaled(Pixmap pixmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight) {
		if (srcWidth < 0) {
			srcWidth = pixmap.getWidth() - srcX + 1;
		}
		if (srcHeight < 0) {
			srcHeight = pixmap.getHeight() - srcY + 1;
		}		
		((AndroidPixmap) pixmap).setTextureCoordinates(srcX, srcY, srcX + srcWidth - 1, srcY + srcHeight - 1);
		((AndroidPixmap) pixmap).setCoordinates(x + visibleArea.left, y + visibleArea.top, 
				                                x + visibleArea.left + srcWidth - 1, y + visibleArea.top + srcHeight - 1);
		((AndroidPixmap) pixmap).render();
		((AndroidPixmap) pixmap).resetTextureCoordinates();
	}

	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y) {	
		((AndroidPixmap) pixmap).render(transX(x), transY(y));
	}
	
	@Override
	public void applyFilterToPixmap(Pixmap pixmap, ColorFilter filter) {
		Bitmap bit = ((AndroidPixmap) pixmap).getBitmap();
		converterCanvas.setBitmap(bit);
	    paint.setColorFilter(filter);
	    converterCanvas.drawBitmap(bit, 0, 0, paint);
	    ((AndroidPixmap) pixmap).set(bit);
	}

	@Override
	public void drawText(String text, int x, int y, long color, GLText font) {
		if (font == null) {
			return;
		}
		float alpha = (float) ((((long) color) & (long) 0xFF000000) >> 24) / 255.0f;
		float red   = ((color & 0x00FF0000) >> 16) / 255.0f;
		float green = ((color & 0x0000FF00) >>  8) / 255.0f;
		float blue  =  (color & 0x000000FF)        / 255.0f;
	    GLES11.glEnable(GLES11.GL_BLEND);
	    GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
		font.begin(red, green, blue, alpha);
		y -= (int) (font.getSize());
		font.draw(text, transX(x), transY(y));
		font.end();
		GLES11.glDisable(GLES11.GL_BLEND);
		textureManager.setTexture(null);
	}

	@Override
	public int getTextWidth(String text, GLText font) {
		if (font == null) {
			return 0;
		}
		return (int) (font.getLength(text) / scaleFactor);
	}

	@Override
	public int getTextHeight(String text, GLText font) {
		if (font == null) {
			return 0;
		}
		return (int) (font.getHeight() / scaleFactor);
	}
	
	@Override
	public void setClip(int x1, int y1, int x2, int y2) {
		if (x1 == -1 && y1 == -1 && x2 == -1 && y2 == -1) {
			GLES11.glDisable(GLES11.GL_SCISSOR_TEST);
			return;
		}
		x1 = x1 == -1 ? Math.max(visibleArea.left - 1, 0) : transX(x1);
		y1 = y1 == -1 ? Math.max(visibleArea.top  - 1, 0) : transY(y1);
		x2 = x2 == -1 ? Math.min(visibleArea.right + 1, visibleArea.width()) : transX(x2);
		y2 = y2 == -1 ? Math.min(visibleArea.bottom + 1, visibleArea.height()) : transY(y2);			
		GLES11.glEnable(GLES11.GL_SCISSOR_TEST);
		GLES11.glScissor(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}
}
