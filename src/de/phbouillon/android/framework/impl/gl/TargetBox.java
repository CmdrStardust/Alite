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
import java.io.Serializable;
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public class TargetBox implements Serializable {
	private static final long serialVersionUID = 9054522060986894235L;

	protected transient FloatBuffer lineBuffer;
	protected transient Alite alite;
	private final float wh, hh, dh;
	private float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
	
	public TargetBox(final Alite alite, float width, float height, float depth) {
		this.alite = alite;
		wh = width / 2;
		hh = height / 2;
		dh = depth / 2;
		
		initialize();
	}
	
	private void initialize() {
	    float [] vertexData = new float [] {
	    	-wh, -hh, -dh,    wh, -hh, -dh,   wh, hh, -dh,  -wh, hh, -dh,
		    -wh, -hh,  dh,    wh, -hh,  dh,   wh, hh,  dh,  -wh, hh,  dh,
		};
			
		lineBuffer = createLines(vertexData,
				    0, 1, 1, 2, 2, 3, 3, 0, 0, 4, 4, 5, 5, 6, 6, 7, 7, 4, 1, 5, 2, 6, 3, 7); 
	}
	
	private final FloatBuffer createLines(float [] vertexData, int ...indices) {
		float [] lines = new float[indices.length * 3];
		
		int offset = 0;
		for (int i: indices) {
			lines[offset]     = vertexData[i * 3]; 
			lines[offset + 1] = vertexData[i * 3 + 1]; 
			lines[offset + 2] = -vertexData[i * 3 + 2]; 			
			offset += 3;
		}
		
		return GlUtils.toFloatBufferPositionZero(lines);
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "TargetBox.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "TargetBox.readObject I");
			this.alite = Alite.get();
			initialize();
			AliteLog.e("readObject", "TargetBox.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
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
		
	public void render() {
		alite.getTextureManager().setTexture(null);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, lineBuffer);		
		GLES11.glColor4f(r, g, b, a);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glLineWidth(5);
		GLES11.glDrawArrays(GLES11.GL_LINES, 0, 24);
		GLES11.glLineWidth(1);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glEnable(GLES11.GL_CULL_FACE);
	}
}
