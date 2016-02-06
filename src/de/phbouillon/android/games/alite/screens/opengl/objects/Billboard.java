package de.phbouillon.android.games.alite.screens.opengl.objects;

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
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.opengl.sprites.SpriteData;

public class Billboard extends AliteObject implements Geometry {
	private static final long serialVersionUID = 6210999939098179291L;

	protected transient FloatBuffer vertexBuffer;
	protected transient FloatBuffer texCoordBuffer;
	protected String textureFilename;
	protected transient Alite alite;
	protected float rotation;
	protected final float [] displayMatrix = new float[16];
	private float width, height;
	private final float tLeft, tTop, tRight, tBottom;
	
	public Billboard(String name, Alite alite,
			float centerX, float centerY, float centerZ, float width, float height,
			float tLeft, float tTop, float tRight, float tBottom, String textureFile) {
		super(name);
		this.width = width;
		this.height = height;
		this.tLeft = tLeft;
		this.tTop = tTop;
		this.tRight = tRight;
		this.tBottom = tBottom;
		this.textureFilename = textureFile;
		alite.getTextureManager().addTexture(textureFile);
		this.alite = alite;
		worldPosition.x = centerX;
		worldPosition.y = centerY;
		worldPosition.z = centerZ;
		
		init();
		
		boundingSphereRadius = 100.0f;
	}
		
	public Billboard(String name, Alite alite,
			float centerX, float centerY, float centerZ, float width, float height,
			String textureFilename, SpriteData sprite) {
		super(name);
		this.textureFilename = textureFilename;
		this.alite = alite;
		alite.getTextureManager().addTexture(textureFilename);
		this.width = width;
		this.height = height;

		worldPosition.x = centerX;
		worldPosition.y = centerY;
		worldPosition.z = centerZ;
		
		if (sprite != null) {
			this.tLeft = sprite.x;
			this.tTop = sprite.y;
			this.tRight = sprite.x2;
			this.tBottom = sprite.y2;
		} else {
			this.tLeft = -1;
			this.tTop = -1;
			this.tRight = -1;
			this.tBottom = -1;
		}
		
		init();
		
		boundingSphereRadius = 100.0f;
	}

	private void init() {
		vertexBuffer   = GlUtils.allocateFloatBuffer(32);
		texCoordBuffer = GlUtils.allocateFloatBuffer(32);

		float w = width / 2.0f;
		float h = height / 2.0f;
		vertexBuffer.put(-w);
		vertexBuffer.put(-h);
		vertexBuffer.put(-w);
		vertexBuffer.put(h);
		vertexBuffer.put(w);
		vertexBuffer.put(-h);
		vertexBuffer.put(w);
		vertexBuffer.put(h);

		if (tLeft >= 0) {
			texCoordBuffer.put(tLeft);
			texCoordBuffer.put(tTop);
			texCoordBuffer.put(tLeft);
			texCoordBuffer.put(tBottom);
			texCoordBuffer.put(tRight);
			texCoordBuffer.put(tTop);
			texCoordBuffer.put(tRight);
			texCoordBuffer.put(tBottom);
		}

		vertexBuffer.position(0);
		texCoordBuffer.position(0);		
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "Billboard.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "Billboard.readObject I");
			this.alite = Alite.get();
			init();
			AliteLog.e("readObject", "Billboard.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	protected void updateTextureCoordinates(SpriteData sprite) {
		if (sprite == null) {
			// This can only happen if the player pauses the game when an
			// explosion billboard is on the screen... We ignore it, it will
			// be cleaned up in later frames anyway.
			return;
		}
		texCoordBuffer.clear();		
		texCoordBuffer.put(sprite.x);
		texCoordBuffer.put(sprite.y);
		texCoordBuffer.put(sprite.x);
		texCoordBuffer.put(sprite.y2);
		texCoordBuffer.put(sprite.x2);
		texCoordBuffer.put(sprite.y);
		texCoordBuffer.put(sprite.x2);
		texCoordBuffer.put(sprite.y2);
		texCoordBuffer.position(0);
	}
	
	public void update(GraphicObject camera) {	
		forwardVector.x = camera.getPosition().x - worldPosition.x;
		forwardVector.y = camera.getPosition().y - worldPosition.y;
		forwardVector.z = camera.getPosition().z - worldPosition.z;
		forwardVector.normalize();
		camera.getUpVector().cross(forwardVector, rightVector);
		rightVector.normalize();
		forwardVector.cross(rightVector, upVector);
		upVector.normalize();
		cached = false;	
		computeMatrix();
	}

	public void resize(float newWidth, float newHeight) {
		width = newWidth;
		height = newHeight;
		float w = width / 2.0f;
		float h = height / 2.0f;
		vertexBuffer.clear();
		vertexBuffer.put(-w);
		vertexBuffer.put(-h);
		vertexBuffer.put(-w);
		vertexBuffer.put(h);
		vertexBuffer.put(w);
		vertexBuffer.put(-h);
		vertexBuffer.put(w);
		vertexBuffer.put(h);
		vertexBuffer.position(0);		
	}	

	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setRotation(float r) {
		rotation = r;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	@Override
	public void render() {		
		GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);
		GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnable(GLES11.GL_LIGHTING);		
	}
	
	public void batchRender() {
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, 4);		
	}

	@Override
	public boolean isVisibleOnHud() {
		return false;
	}

	@Override
	public Vector3f getHudColor() {
		return null;
	}

	@Override
	public float getDistanceFromCenterToBorder(Vector3f dir) {
		return 0;
	}
	
	@Override
	public void setDisplayMatrix(float [] matrix) {		
		int counter = 0;
		for (float f: matrix) {
			displayMatrix[counter++] = f;
		}
	}
	
	@Override
	public float [] getDisplayMatrix() {
		return displayMatrix;
	}
}
