package de.phbouillon.android.framework.impl.gl;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.Rect;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public class Sprite implements Serializable {
	private static final long serialVersionUID = -8424677289585617300L;

	protected transient FloatBuffer vertexBuffer;
	protected transient FloatBuffer texCoordBuffer;
	protected transient Alite alite;

	protected Rect position;
	protected Rect textureCoords;
	protected Rect originalPosition;
	protected final String textureFilename;
	
	public Sprite(final Alite alite, float left, float top, float right,
			float bottom, float tLeft, float tTop, float tRight, float tBottom,
			final String textureFilename) {
		this.alite = alite;
		vertexBuffer = GlUtils.allocateFloatBuffer(4 * 8);
		texCoordBuffer = GlUtils.allocateFloatBuffer(4 * 8);
		
		position = new Rect(left, top, right, bottom);
		originalPosition = new Rect(left, top, right, bottom);
		textureCoords = new Rect(tLeft, tTop, tRight, tBottom);
		
		vertexBuffer.put(left);
		vertexBuffer.put(top);
		vertexBuffer.put(left);
		vertexBuffer.put(bottom);
		vertexBuffer.put(right);
		vertexBuffer.put(top);
		vertexBuffer.put(right);
		vertexBuffer.put(bottom);

		texCoordBuffer.put(tLeft);
		texCoordBuffer.put(tTop);
		texCoordBuffer.put(tLeft);
		texCoordBuffer.put(tBottom);
		texCoordBuffer.put(tRight);
		texCoordBuffer.put(tTop);
		texCoordBuffer.put(tRight);
		texCoordBuffer.put(tBottom);

		vertexBuffer.position(0);
		texCoordBuffer.position(0);

		this.textureFilename = textureFilename;
		alite.getTextureManager().addTexture(textureFilename);
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "Sprite.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "Sprite.readObject I");
			this.alite     = Alite.get();
			vertexBuffer = GlUtils.allocateFloatBuffer(4 * 8);
			texCoordBuffer = GlUtils.allocateFloatBuffer(4 * 8);
			setTextureCoords(textureCoords.left, textureCoords.top, textureCoords.right, textureCoords.bottom);
			setPosition(position);
			AliteLog.e("readObject", "Sprite.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Sprite " + textureFilename, e);
			throw(e);
		}
    }

	public void setTextureCoords(float tLeft, float tTop, float tRight, float tBottom) {
		texCoordBuffer.clear();
		texCoordBuffer.put(tLeft);
		texCoordBuffer.put(tTop);
		texCoordBuffer.put(tLeft);
		texCoordBuffer.put(tBottom);
		texCoordBuffer.put(tRight);
		texCoordBuffer.put(tTop);
		texCoordBuffer.put(tRight);
		texCoordBuffer.put(tBottom);
		texCoordBuffer.position(0);
	}
	
	public void setPosition(Rect rect) {
		setPosition(rect.left, rect.top, rect.right, rect.bottom);
	}
	
	public void scale(float scale, float left, float top, float right, float bottom) {
		float cx = (right - left) / 2.0f + left;
		float cy = (bottom - top) / 2.0f + top;
		float width = (right - left) / 2.0f * scale;
		float height = (bottom - top) / 2.0f * scale;
		float newLeft = cx - width;
		float newRight = cx + width;
		float newTop = cy - height;
		float newBottom = cy + height;
		setPosition(newLeft, newTop, newRight, newBottom);
	}
	
	public void setPosition(float left, float top, float right, float bottom) {
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
		position.left = left;
		position.top = top;
		position.right = right;
		position.bottom = bottom;
	}
	
	public void move(int dx, int dy) {
		setPosition(position.left + dx, position.top + dy, position.right + dx, position.bottom + dy);
	}
	
	public void resetPosition() {
		setPosition(originalPosition);
	}
	
	public Rect getPosition() {
		return position;
	}
	
	public Rect getOriginalPosition() {
		return originalPosition;
	}
	
	protected void setUp() {
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);

		alite.getTextureManager().setTexture(textureFilename);
		
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void render() {
	  setUp();
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
		cleanUp();
	}

	public void simpleRender() {
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);

		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	public void justRender() {
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	protected void cleanUp() {
		GLES11.glDisable(GLES11.GL_BLEND);
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glEnable(GLES11.GL_LIGHTING);

		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);
	}

	public void destroy() {
		alite.getTextureManager().freeTexture(textureFilename);
	}	
}
