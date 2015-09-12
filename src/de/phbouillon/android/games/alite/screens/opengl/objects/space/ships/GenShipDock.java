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
 * Generation Ship model and textures by Draco Caeles. 
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class GenShipDock extends SpaceObject {
	private static final long serialVersionUID = 6624622667443197817L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        139.71f, -227.76f, 787.50f, 254.83f, -156.25f, 787.50f, 
        254.83f, -58.46f, 787.50f, 142.28f, -35.45f, 787.50f, 
         -0.00f, -30.31f, 787.50f, -142.28f, -35.45f, 787.50f, 
        -254.83f, -58.46f, 787.50f, -254.83f, -156.25f, 787.50f, 
        -139.71f, -227.76f, 787.50f,  -0.00f, -253.59f, 787.50f, 
        229.35f, -153.02f, 487.50f, 125.74f, -217.38f, 487.50f, 
         -0.00f, -240.63f, 487.50f, -125.74f, -217.38f, 487.50f, 
        -229.35f, -153.02f, 487.50f, -229.35f, -65.01f, 487.50f, 
        -128.05f, -44.30f, 487.50f,  -0.00f, -39.68f, 487.50f, 
        128.05f, -44.30f, 487.50f, 229.35f, -65.01f, 487.50f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.18167f,   0.98244f,   0.04245f,  -0.18167f,   0.98244f,   0.04245f, 
         -0.52688f,   0.84823f,   0.05388f,  -0.52688f,   0.84823f,   0.05388f, 
         -0.99641f,   0.00000f,   0.08464f,  -0.99641f,   0.00000f,   0.08464f, 
         -0.20017f,  -0.97901f,   0.03838f,  -0.20017f,  -0.97901f,   0.03838f, 
         -0.03604f,  -0.99886f,   0.03118f,  -0.03604f,  -0.99886f,   0.03118f, 
          0.03604f,  -0.99886f,   0.03118f,   0.03604f,  -0.99886f,   0.03118f, 
          0.20017f,  -0.97901f,   0.03838f,   0.20017f,  -0.97901f,   0.03838f, 
          0.99641f,   0.00000f,   0.08464f,   0.99641f,  -0.00000f,   0.08464f, 
          0.52688f,   0.84823f,   0.05388f,   0.52688f,   0.84823f,   0.05388f, 
          0.18167f,   0.98244f,   0.04245f,   0.18167f,   0.98244f,   0.04245f, 
          0.00000f,  -0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.13f,   0.39f,   0.29f,   0.29f,   0.27f,   0.24f, 
          0.13f,   0.39f,   0.27f,   0.24f,   0.01f,   0.23f, 
          0.27f,   0.46f,   0.32f,   0.32f,   0.29f,   0.29f, 
          0.27f,   0.46f,   0.29f,   0.29f,   0.13f,   0.39f, 
          0.39f,   0.46f,   0.34f,   0.32f,   0.32f,   0.32f, 
          0.39f,   0.46f,   0.32f,   0.32f,   0.27f,   0.46f, 
          0.52f,   0.40f,   0.37f,   0.29f,   0.34f,   0.32f, 
          0.52f,   0.40f,   0.34f,   0.32f,   0.39f,   0.46f, 
          0.64f,   0.25f,   0.38f,   0.24f,   0.37f,   0.29f, 
          0.64f,   0.25f,   0.37f,   0.29f,   0.52f,   0.40f, 
          0.53f,   0.09f,   0.37f,   0.19f,   0.38f,   0.24f, 
          0.53f,   0.09f,   0.38f,   0.24f,   0.64f,   0.25f, 
          0.40f,   0.02f,   0.35f,   0.16f,   0.37f,   0.19f, 
          0.40f,   0.02f,   0.37f,   0.19f,   0.53f,   0.09f, 
          0.28f,   0.01f,   0.32f,   0.16f,   0.35f,   0.16f, 
          0.28f,   0.01f,   0.35f,   0.16f,   0.40f,   0.02f, 
          0.14f,   0.08f,   0.29f,   0.19f,   0.32f,   0.16f, 
          0.14f,   0.08f,   0.32f,   0.16f,   0.28f,   0.01f, 
          0.01f,   0.23f,   0.27f,   0.24f,   0.29f,   0.19f, 
          0.01f,   0.23f,   0.29f,   0.19f,   0.14f,   0.08f, 
          0.32f,   0.32f,   0.34f,   0.32f,   0.37f,   0.29f, 
          0.32f,   0.32f,   0.37f,   0.29f,   0.38f,   0.24f, 
          0.32f,   0.32f,   0.38f,   0.24f,   0.37f,   0.19f, 
          0.32f,   0.32f,   0.37f,   0.19f,   0.35f,   0.16f, 
          0.32f,   0.32f,   0.35f,   0.16f,   0.32f,   0.16f, 
          0.32f,   0.32f,   0.32f,   0.16f,   0.29f,   0.19f, 
          0.32f,   0.32f,   0.29f,   0.19f,   0.27f,   0.24f, 
          0.32f,   0.32f,   0.27f,   0.24f,   0.29f,   0.29f
    };

    public GenShipDock(Alite alite) {
        super(alite, "Generation Ship", ObjectType.Trader);
        boundingBox = new float [] {-254.83f, 254.83f, -253.59f,   0.00f, 487.50f, 787.50f};
        numberOfVertices = 84;
        textureFilename = "textures/genshipdock.png";
        init();
    }

    @Override
    protected void init() {
        createFaces(VERTEX_DATA, NORMAL_DATA,
                0,  11,  12,   0,  12,   9,   1,  10,  11,   1,  11,   0,   2,  19,  10, 
                2,  10,   1,   3,  18,  19,   3,  19,   2,   4,  17,  18,   4,  18,   3, 
                5,  16,  17,   5,  17,   4,   6,  15,  16,   6,  16,   5,   7,  14,  15, 
                7,  15,   6,   8,  13,  14,   8,  14,   7,   9,  12,  13,   9,  13,   8, 
               10,  19,  18,  10,  18,  17,  10,  17,  16,  10,  16,  15,  10,  15,  14, 
               10,  14,  13,  10,  13,  12,  10,  12,  11);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
    }

    @Override
    public boolean isVisibleOnHud() {
        return true;
    }

    @Override
    public Vector3f getHudColor() {
        return HUD_COLOR;
    }

    @Override
    public float getDistanceFromCenterToBorder(Vector3f dir) {
        return 50.0f;
    }
}
