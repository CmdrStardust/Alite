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
 * Buoy model and texture from Oolite: http://oolite.org
 */

import java.io.IOException;
import java.io.ObjectOutputStream;

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Buoy extends SpaceObject {
	private static final long serialVersionUID = 7312586132171028731L;

	private final Vector3f hudColor = new Vector3f(0xef, 0xef, 0x00);

    private static final float [] VERTEX_DATA = new float [] {
         65.00f,   0.00f,   0.00f,   0.00f,  65.00f,   0.00f, 
        -65.00f,   0.00f,   0.00f,   0.00f, -65.00f,   0.00f, 
        -45.95f,  45.95f,   0.00f, -45.95f, -45.95f,   0.00f, 
         45.95f, -45.95f,   0.00f,  45.95f,  45.95f,   0.00f, 
          0.00f,   0.00f, -13.00f,   0.00f,   0.00f,  13.00f, 
         -0.00f,   0.00f, -65.00f,   0.00f,   0.00f,  65.00f, 
          0.00f,  45.95f,  45.95f,   0.00f, -45.95f,  45.95f, 
         -0.00f, -45.95f, -45.95f,  -0.00f,  45.95f, -45.95f, 
        -13.00f,   0.00f,   0.00f,  13.00f,   0.00f,  -0.00f, 
        -45.95f,  -0.00f,  45.95f, -45.95f,   0.00f, -45.95f, 
         45.95f,   0.00f, -45.95f,  45.95f,  -0.00f,  45.95f, 
          0.00f,  13.00f,   0.00f,   0.00f, -13.00f,  -0.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.19547f,  -0.08101f,   0.97736f,   0.19547f,  -0.97736f,   0.08101f, 
          0.19547f,   0.97736f,  -0.08101f,   0.19547f,   0.97736f,   0.08101f, 
          0.19547f,  -0.97736f,  -0.08101f,   0.19547f,   0.08101f,  -0.97736f, 
          0.19547f,  -0.08101f,  -0.97736f,   0.19547f,   0.08101f,   0.97736f, 
          0.08101f,  -0.97736f,  -0.19547f,   0.08101f,  -0.97736f,   0.19547f, 
          0.08101f,   0.97736f,   0.19547f,   0.08101f,   0.97736f,  -0.19547f, 
          0.08101f,  -0.19547f,  -0.97736f,   0.08101f,   0.19547f,  -0.97736f, 
          0.08101f,   0.19547f,   0.97736f,   0.08101f,  -0.19547f,   0.97736f, 
          0.97736f,   0.08101f,   0.19547f,   0.97736f,  -0.08101f,   0.19547f, 
          0.97736f,   0.19547f,   0.08101f,   0.97736f,  -0.19547f,   0.08101f, 
          0.97736f,  -0.19547f,  -0.08101f,   0.97736f,   0.19547f,  -0.08101f, 
          0.97736f,   0.08101f,  -0.19547f,   0.97736f,  -0.08101f,  -0.19547f, 
         -0.97736f,  -0.08101f,   0.19547f,  -0.97736f,   0.08101f,   0.19547f, 
         -0.97736f,  -0.19547f,   0.08101f,  -0.97736f,   0.19547f,   0.08101f, 
         -0.97736f,   0.19547f,  -0.08101f,  -0.97736f,  -0.19547f,  -0.08101f, 
         -0.97736f,  -0.08101f,  -0.19547f,  -0.97736f,   0.08101f,  -0.19547f, 
         -0.08101f,  -0.97736f,   0.19547f,  -0.08101f,  -0.97736f,  -0.19547f, 
         -0.08101f,   0.97736f,  -0.19547f,  -0.08101f,   0.97736f,   0.19547f, 
         -0.08101f,   0.19547f,  -0.97736f,  -0.08101f,  -0.19547f,  -0.97736f, 
         -0.08101f,  -0.19547f,   0.97736f,  -0.08101f,   0.19547f,   0.97736f, 
         -0.19547f,  -0.97736f,   0.08101f,  -0.19547f,  -0.97736f,  -0.08101f, 
         -0.19547f,   0.97736f,  -0.08101f,  -0.19547f,   0.97736f,   0.08101f, 
         -0.19547f,   0.08101f,  -0.97736f,  -0.19547f,  -0.08101f,  -0.97736f, 
         -0.19547f,  -0.08101f,   0.97736f,  -0.19547f,   0.08101f,   0.97736f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.50f,   0.50f,   0.85f,   0.14f,   1.00f,   0.50f, 
          0.00f,   0.50f,   0.14f,   0.14f,   0.50f,   0.50f, 
          1.00f,   0.50f,   0.85f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.85f,   1.00f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.85f,   0.00f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.14f,   0.00f,   0.50f, 
          0.00f,   0.50f,   0.14f,   0.85f,   0.50f,   0.50f, 
          1.00f,   0.50f,   0.85f,   0.85f,   0.50f,   0.50f, 
          0.50f,   1.00f,   0.14f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.14f,   0.50f,   0.00f, 
          0.50f,   1.00f,   0.85f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.14f,   0.50f,   0.00f, 
          0.50f,   0.50f,   0.14f,   0.85f,   0.50f,   1.00f, 
          0.50f,   0.00f,   0.14f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.85f,   0.50f,   1.00f, 
          0.50f,   0.00f,   0.85f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.85f,   1.00f,   0.50f, 
          1.00f,   0.50f,   0.85f,   0.14f,   0.50f,   0.50f, 
          0.50f,   1.00f,   0.85f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.14f,   0.50f,   0.00f, 
          0.50f,   0.00f,   0.14f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.85f,   0.50f,   1.00f, 
          0.00f,   0.50f,   0.14f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.14f,   0.00f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.85f,   0.00f,   0.50f, 
          0.00f,   0.50f,   0.14f,   0.14f,   0.50f,   0.50f, 
          0.50f,   1.00f,   0.14f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.14f,   0.50f,   0.00f, 
          0.50f,   0.00f,   0.85f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.85f,   0.50f,   1.00f, 
          1.00f,   0.50f,   0.85f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.14f,   1.00f,   0.50f, 
          0.50f,   0.00f,   0.85f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.85f,   0.50f,   1.00f, 
          0.50f,   0.00f,   0.14f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.85f,   0.50f,   1.00f, 
          0.50f,   0.50f,   0.85f,   0.14f,   0.50f,   0.00f, 
          0.50f,   1.00f,   0.85f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.14f,   0.50f,   0.00f, 
          0.50f,   1.00f,   0.14f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.14f,   1.00f,   0.50f, 
          1.00f,   0.50f,   0.85f,   0.85f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.14f,   0.00f,   0.50f, 
          0.00f,   0.50f,   0.14f,   0.85f,   0.50f,   0.50f, 
          1.00f,   0.50f,   0.85f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.85f,   0.85f,   1.00f,   0.50f, 
          0.00f,   0.50f,   0.14f,   0.14f,   0.50f,   0.50f, 
          0.50f,   0.50f,   0.14f,   0.85f,   0.00f,   0.50f
    };

    public Buoy(Alite alite) {
        super(alite, "Buoy", ObjectType.Buoy);
        shipType = ShipType.Buoy;
        boundingBox = new float [] {-65.00f,  65.00f, -65.00f,  65.00f, -65.00f,  65.00f};
        numberOfVertices = 144;
        textureFilename = "textures/buoy.png";
        maxSpeed          = 0.0f;
        maxPitchSpeed     = 0.0f;
        maxRollSpeed      = 0.0f;
        hullStrength      = 55.0f;
        hasEcm            = false;
        cargoType         = 0;
        aggressionLevel   = 0;
        escapeCapsuleCaps = 0;
        score             = 0;
        bounty            = 0;
        legalityType      = 1;
        maxCargoCanisters = 0;
        missileCount      = 0;
        init();
    }
    
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Buoy " + getName(), e);
			throw(e);
		}
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                8,   4,   2,   2,  19,  22,   2,  18,  23,  23,  19,   2,  22,  18,   2, 
                9,   5,   2,   2,   4,   9,   2,   5,   8,  11,  18,  22,  22,  19,  10, 
               10,  19,  23,  23,  18,  11,   9,   4,   1,   3,   5,   9,   8,   5,   3, 
                1,   4,   8,  16,  14,  10,  10,  15,  16,   3,  14,  16,  16,  15,   1, 
                1,  12,  16,  16,  13,   3,  11,  13,  16,  16,  12,  11,  17,  15,  10, 
               10,  14,  17,   1,  15,  17,  17,  14,   3,   3,  13,  17,  17,  12,   1, 
               11,  12,  17,  17,  13,  11,  10,  20,  22,  22,  21,  11,  11,  21,  23, 
               23,  20,  10,   9,   6,   3,   1,   7,   9,   8,   7,   1,   3,   6,   8, 
               22,  20,   0,   0,  21,  22,  23,  21,   0,   0,  20,  23,   0,   6,   9, 
                9,   7,   0,   0,   7,   8,   8,   6,   0);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    
        initTargetBox();
    }

    @Override
    public boolean isVisibleOnHud() {
        return true;
    }

    public void setHudColor(float r, float g, float b) {
    	hudColor.x = r;
    	hudColor.y = g;
    	hudColor.z = b;
    	targetBox.setColor(r, g, b);
    }
    
    @Override
    public Vector3f getHudColor() {
        return hudColor;
    }

    @Override
    public float getDistanceFromCenterToBorder(Vector3f dir) {
        return 50.0f;
    }
}
