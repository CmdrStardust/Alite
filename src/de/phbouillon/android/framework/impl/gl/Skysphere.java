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
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.opengl.GLES11;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public class Skysphere extends Sphere implements Serializable {
	private static final long serialVersionUID = 4648170914967080291L;

	public Skysphere(final Alite alite, final float radius, final int slices, final int stacks, final String textureFilename) {
		super(alite, radius, slices, stacks, textureFilename, null, true);
	}
		
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Skysphere", e);
			throw(e);
		}
    }

	public void render() {
		GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glDrawArrays(glDrawMode, 0, numberOfVertices);
		GLES11.glEnable(GLES11.GL_LIGHTING);
		
		GLES11.glEnable(GLES11.GL_CULL_FACE);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);
	}
}
