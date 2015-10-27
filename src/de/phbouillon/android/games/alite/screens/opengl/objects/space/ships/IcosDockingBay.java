package de.phbouillon.android.games.alite.screens.opengl.objects.space.ships;

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
import java.io.Serializable;
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public class IcosDockingBay implements Serializable {
	private static final long serialVersionUID = 8489941188955095492L;

	private transient FloatBuffer vertexBuffer;
	private transient FloatBuffer normalBuffer;
	private transient FloatBuffer texCoordBuffer;
	private int numberOfVertices;
	private String textureFilename;
	private float [] vertices;
	private float [] normals;
	private transient Alite alite;
	
    private static final float [] BAY_VERTEX_DATA = new float [] {
        -16.00f, -48.00f,  250.00f, -16.00f, -48.00f,    0.00f, 
        -16.00f,  48.00f,    0.00f, -16.00f,  48.00f,  250.00f, 
         16.00f, -48.00f,  250.00f,  16.00f, -48.00f,    0.00f, 
         16.00f,  48.00f,    0.00f,  16.00f,  48.00f,  250.00f
    };

    private static final float [] BAY_NORMAL_DATA = new float [] {
          0.66667f,   0.66667f,   0.33333f,   0.70711f,   0.70711f,   0.00000f, 
          0.70711f,  -0.70711f,   0.00000f,   0.40825f,  -0.40825f,   0.81650f, 
         -0.66667f,   0.33333f,   0.66667f,  -0.44721f,   0.89443f,   0.00000f, 
         -0.89443f,  -0.44721f,   0.00000f,  -0.40825f,  -0.81650f,   0.40825f
    };

    private static final float [] BAY_TEXTURE_COORDINATE_DATA = new float [] {
          0.00f,   1.00f,   0.00f,   0.00f,   0.50f,   0.00f, 
          0.50f,   1.00f,   0.00f,   1.00f,   0.50f,   0.00f, 
          0.00f,   0.00f,   0.00f,   1.00f,   0.50f,   1.00f, 
          0.50f,   0.00f,   0.00f,   0.00f,   0.50f,   1.00f,  
          
          1.00f,   1.00f,   0.50f,   0.00f,   0.50f,   1.00f, 
          1.00f,   0.00f,   0.50f,   0.00f,   1.00f,   1.00f, 
          0.50f,   1.00f,   0.50f,   0.00f,   1.00f,   1.00f, 
          1.00f,   1.00f,   0.50f,   0.00f,   1.00f,   0.00f, 
          
          0.50f,   0.00f,   0.00f,   1.00f,   0.00f,   0.00f, 
          0.50f,   1.00f,   0.00f,   1.00f,   0.50f,   0.00f
    };

	private final FloatBuffer create(float scale, float [] vertexData, float [] normalData, int ...indices) {
		vertices = new float[indices.length * 3];
		normals  = new float[indices.length * 3];
		
		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = vertexData[i * 3] * scale; 
			vertices[offset + 1] = vertexData[i * 3 + 1] * scale; 
			vertices[offset + 2] = vertexData[i * 3 + 2] * scale; 
			
			normals[offset]      = normalData[i * 3];
			normals[offset + 1]  = normalData[i * 3 + 1];
			normals[offset + 2]  = normalData[i * 3 + 2];			
			offset += 3;
		}
		normalBuffer = GlUtils.toFloatBufferPositionZero(normals);
		
		return GlUtils.toFloatBufferPositionZero(vertices);
	}

	IcosDockingBay(final Alite alite) {
		this.alite = alite;
		init();
	}
	
	private void init() {
		vertexBuffer = create(3.84f, BAY_VERTEX_DATA, BAY_NORMAL_DATA,
                2,   3,   7,   6,   2,   7,   0,   1,   5,   4,   0,   5,   2,   0,   3, 
                1,   0,   2,   7,   4,   6,   6,   4,   5,   5,   2,   1,   6,   2,   5);
		texCoordBuffer = GlUtils.toFloatBufferPositionZero(BAY_TEXTURE_COORDINATE_DATA);
        numberOfVertices = 30;
        textureFilename = "textures/darkmetal_2.png";
        alite.getTextureManager().addTexture(textureFilename);		
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "IcosDockingBay.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "IcosDockingBay.readObject I");
			this.alite = Alite.get();
			init();
			AliteLog.e("readObject", "IcosDockingBay.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	void render() {
		boolean enabled = GLES11.glIsEnabled(GLES11.GL_CULL_FACE);
		if (enabled) {
			GLES11.glDisable(GLES11.GL_CULL_FACE);
		}
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);		
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, numberOfVertices);
		if (enabled) {
			GLES11.glEnable(GLES11.GL_CULL_FACE);
		}		
	}
}
