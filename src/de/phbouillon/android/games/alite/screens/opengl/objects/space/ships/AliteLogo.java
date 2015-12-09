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

/**
 * Alite Logo; dummy "ship" instead of a billboard.
 */

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class AliteLogo extends SpaceObject {
	private static final long serialVersionUID = -630403162013561621L;
	
    private static final float [] VERTEX_DATA = new float [] {
        -202.00f, -132.50f, 0.00f,  202.00f, -132.50f, 0.00f,
        -202.00f,  132.50f, 0.00f,  202.00f,  132.50f, 0.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         0.00000f,  0.00000f,   1.00000f,   0.00000f,  0.00000f,   1.00000f, 
         0.00000f,  0.00000f,   1.00000f,   0.00000f,  0.00000f,   1.00000f,
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          1.00f, 0.00f,   0.00f, 0.00f,    1.00f, 0.52f, 
          1.00f, 0.52f,   0.00f, 0.00f,    0.00f, 0.52f
    };


    public AliteLogo(Alite alite) {
        super(alite, "Logo", ObjectType.Platlet);
        shipType = ShipType.Platlet;
        boundingBox = new float [] {-202.00f, -132.50f, 0.00f,  202.00f, 132.50f, 0.00f};
        numberOfVertices = 6;
        textureFilename   = "title_logo.png";
        maxSpeed          = 0.0f;
        maxPitchSpeed     = 0.0f;
        maxRollSpeed      = 0.0f;        
        hullStrength      = 40.0f;
        hasEcm            = false;
        cargoType         = 0;     
        aggressionLevel   = 0;
        escapeCapsuleCaps = 0;
        bounty            = 0;
        score             = 0;
        legalityType      = 0;
        maxCargoCanisters = 0;        
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA, 0, 1, 2, 2, 1, 3);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    
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
        return 242.0f;
    }
    
	@Override
	public void render() {		
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, numberOfVertices);
		GLES11.glDisable(GLES11.GL_BLEND);
		alite.getTextureManager().setTexture(null);
		if (Settings.engineExhaust && exhaust != null && !exhaust.isEmpty() && getSpeed() < 0f) {
			for (EngineExhaust ex: exhaust) {
				ex.render();
			}
		}
	}
}
