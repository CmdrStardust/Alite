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

import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;

public class Box {
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer texCoordBuffer;
	protected final Alite alite;
	private final float wh, hh, dh;
	private float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
	private float [] vertices;
	
	private final FloatBuffer createFaces(float [] vertexData, int ...indices) {
		vertices = new float[indices.length * 3];
		
		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = vertexData[i * 3]; 
			vertices[offset + 1] = vertexData[i * 3 + 1]; 
			vertices[offset + 2] = -vertexData[i * 3 + 2]; 			
			offset += 3;
		}
		
		return GlUtils.toFloatBufferPositionZero(vertices);
	}

	public float [] getVertices() {
		return vertices;
	}
	
	public Box(final Alite alite, float size) {
		this.alite = alite;
		float sh = size / 2;
		wh = sh;
		dh = sh;
		hh = sh;
	    float [] vertexData = new float [] {
	    	-sh, -sh, -sh,    sh, -sh, -sh,   sh, sh, -sh,  -sh, sh, -sh,
	    	-sh, -sh,  sh,    sh, -sh,  sh,   sh, sh,  sh,  -sh, sh,  sh,
	    };

		vertexBuffer = createFaces(vertexData,
                2, 1, 0, 0, 3, 2, 0, 1, 4, 1, 5, 4, 1, 2, 5, 2, 6, 5, 4, 5, 6, 6, 7, 4, 4, 7, 0, 7, 3, 0, 2, 3, 7, 7, 6, 2);
	}

	public Box(final Alite alite, float width, float height, float depth) {
		this.alite = alite;
		wh = width / 2;
		hh = height / 2;
		dh = depth / 2;
		
	    float [] vertexData = new float [] {
	    	-wh, -hh, -dh,    wh, -hh, -dh,   wh, hh, -dh,  -wh, hh, -dh,
	    	-wh, -hh,  dh,    wh, -hh,  dh,   wh, hh,  dh,  -wh, hh,  dh,
	    };
		
		vertexBuffer = createFaces(vertexData,
                2, 1, 0, 0, 3, 2, 0, 1, 4, 1, 5, 4, 1, 2, 5, 2, 6, 5, 4, 5, 6, 6, 7, 4, 4, 7, 0, 7, 3, 0, 2, 3, 7, 7, 6, 2);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void setAlpha(float a) {
		this.a = a;
	}
	
	public void setFarPlane(Vector3f far) {
		float [] vertexData = new float [] {
					-wh, -hh, -dh,    wh, -hh, -dh,   wh, hh, -dh,  -wh, hh, -dh,
					far.x - wh, far.y - hh, far.z,   far.x + wh, far.y - hh, far.z,   far.x + wh, far.y + hh, far.z,   far.x - wh, far.y + hh, far.z
		};
		
		vertexBuffer = createFaces(vertexData,
                2, 1, 0, 0, 3, 2, 0, 1, 4, 1, 5, 4, 1, 2, 5, 2, 6, 5, 4, 5, 6, 6, 7, 4, 4, 7, 0, 7, 3, 0, 2, 3, 7, 7, 6, 2);
	}
	
	public void render() {
		alite.getTextureManager().setTexture(null);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);		
		GLES11.glColor4f(r, g, b, a);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, 36);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
	}	
}
