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

import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Graphics.PixmapFormat;
import de.phbouillon.android.framework.MemUtil;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.opengl.TextureManager;

public class AndroidPixmap implements Pixmap {
	private Bitmap bitmap;
	private final PixmapFormat format;
	private final String fileName;
	private final TextureManager textureManager;
	private final int width;
	private final int height;
	private final FloatBuffer vertexBuffer;
	private final FloatBuffer texCoordBuffer;
	private int lastX = -1;
	private int lastY = -1;
	private final float tx2;
	private final float ty2;
	
	public AndroidPixmap(Bitmap bitmap, PixmapFormat format, String fileName, TextureManager textureManager, int width, int height, float tx2, float ty2) {
		this.format = format;
		this.fileName = fileName;
		this.textureManager = textureManager;
		this.bitmap = bitmap;
		textureManager.addTexture(fileName, bitmap);
		this.width = width;
		this.height = height;
		this.tx2 = tx2;
		this.ty2 = ty2;
		
		vertexBuffer = GlUtils.allocateFloatBuffer(4 * 8);
		texCoordBuffer = GlUtils.allocateFloatBuffer(4 * 8);
		
		texCoordBuffer.put(0);
		texCoordBuffer.put(0);
		texCoordBuffer.put(0);
		texCoordBuffer.put(ty2);
		texCoordBuffer.put(tx2);
		texCoordBuffer.put(0);
		texCoordBuffer.put(tx2);
		texCoordBuffer.put(ty2);

		texCoordBuffer.position(0);
		textureManager.setTexture(null);
	}
		
	@Override
	public int getWidth() {
		return width;
	}
	
	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public PixmapFormat getFormat() {
		return format;
	}

	@Override
	public void dispose() {
		if (textureManager != null) {
			textureManager.freeTexture(fileName);
		} else {
			AliteLog.e("TEXTURE MANAGER LOST!", "Texture Manager is NULL!");
		}
		if (bitmap != null && !bitmap.isRecycled()) {
			MemUtil.freeBitmap(bitmap);
		}
	}	
		
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void set(Bitmap bitmap) {
		if (this.bitmap != bitmap) {
			MemUtil.freeBitmap(this.bitmap);
		}
		this.bitmap = bitmap;
		textureManager.addTexture(fileName, bitmap);
	}

	public void setTextureCoordinates(int left, int top, int right, int bottom) {		
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		float l = ((float) left) / width;
		float t = ((float) top)  / height;
		float r = ((float) right) / width;
		float b = ((float) bottom) / height;
		
		texCoordBuffer.clear();
		
		texCoordBuffer.put(l);
		texCoordBuffer.put(t);
		texCoordBuffer.put(l);
		texCoordBuffer.put(b);
		texCoordBuffer.put(r);
		texCoordBuffer.put(t);
		texCoordBuffer.put(r);
		texCoordBuffer.put(b);

		texCoordBuffer.position(0);				
	}
	
	public void setCoordinates(int left, int top, int right, int bottom) {
		vertexBuffer.clear();
		vertexBuffer.put(left);
		vertexBuffer.put(top);
		vertexBuffer.put(left);
		vertexBuffer.put(bottom);
		vertexBuffer.put(right);
		vertexBuffer.put(top);
		vertexBuffer.put(right);
		vertexBuffer.put(bottom);
		vertexBuffer.position(0);
		lastX = -1;
		lastY = -1;
	}
	
	public void resetTextureCoordinates() {
		texCoordBuffer.clear();
		
		texCoordBuffer.put(0);
		texCoordBuffer.put(0);
		texCoordBuffer.put(0);
		texCoordBuffer.put(ty2);
		texCoordBuffer.put(tx2);
		texCoordBuffer.put(0);
		texCoordBuffer.put(tx2);
		texCoordBuffer.put(ty2);

		texCoordBuffer.position(0);		
	}
	
	public void render() {			
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		if (!textureManager.checkTexture(fileName)) {
			textureManager.addTexture(fileName, bitmap);
		}
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		textureManager.setTexture(fileName);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
		GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		textureManager.setTexture(null);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);		
	}
	
	public void render(int x, int y) {
		if (x != lastX || y != lastY) {
			vertexBuffer.clear();
			vertexBuffer.put(x);
			vertexBuffer.put(y);
			vertexBuffer.put(x);
			vertexBuffer.put(y + height - 1);
			vertexBuffer.put(x + width - 1);
			vertexBuffer.put(y);
			vertexBuffer.put(x + width - 1);
			vertexBuffer.put(y + height - 1);
			vertexBuffer.position(0);
			lastX = x;
			lastY = y;
		}
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		if (!textureManager.checkTexture(fileName)) {
			if (bitmap != null && !bitmap.isRecycled()) {
				textureManager.addTexture(fileName, bitmap);
			}
		}
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
		textureManager.setTexture(fileName);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
		GLES11.glDisable(GLES11.GL_BLEND);
		textureManager.setTexture(null);
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
	}
}
