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

public class GenShipNames6 extends SpaceObject {
	private static final long serialVersionUID = -718434068105067290L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -105.47f, 1582.27f,  -0.00f, 105.47f, 1582.27f,  -0.00f, 
        1603.13f, 105.47f,  -0.00f, 1603.13f, -105.47f,  -0.00f, 
        -105.47f, -1623.98f,  -0.00f, 105.47f, -1623.98f,  -0.00f, 
        -1603.13f, -105.47f,  -0.00f, -1603.13f, 105.47f,  -0.00f, 
        -1603.13f, 105.47f, -1512.50f, -1603.13f, -105.47f, -1512.50f, 
        105.47f, -1623.98f, -1512.50f, -105.47f, -1623.98f, -1512.50f, 
        1603.13f, -105.47f, -1512.50f, 1603.13f, 105.47f, -1512.50f, 
        105.47f, 1582.27f, -1512.50f, -105.47f, 1582.27f, -1512.50f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,   0.00000f, 
          1.00000f,   0.00000f,   0.00000f,   1.00000f,  -0.00000f,   0.00000f, 
          0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f, 
         -1.00000f,   0.00000f,  -0.00000f,  -1.00000f,  -0.00000f,   0.00000f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.00f,   0.40f,   0.49f,   0.40f,   0.49f,   0.33f, 
          0.00f,   0.40f,   0.49f,   0.33f,   0.00f,   0.33f, 
          0.00f,   0.40f,   0.49f,   0.40f,   0.49f,   0.33f, 
          0.00f,   0.40f,   0.49f,   0.33f,   0.00f,   0.33f, 
          0.00f,   0.40f,   0.49f,   0.40f,   0.49f,   0.33f, 
          0.00f,   0.40f,   0.49f,   0.33f,   0.00f,   0.33f, 
          0.00f,   0.40f,   0.48f,   0.40f,   0.48f,   0.33f, 
          0.00f,   0.40f,   0.48f,   0.33f,   0.00f,   0.33f
    };

    public GenShipNames6(Alite alite) {
        super(alite, "Generation Ship", ObjectType.Trader);
        boundingBox = new float [] {-1603.13f, 1603.13f, -1623.98f, 1582.27f, -1512.50f,   0.00f};
        numberOfVertices = 24;
        textureFilename = "textures/genshipnames.png";
        init();
    }

    @Override
    protected void init() {
        createFaces(VERTEX_DATA, NORMAL_DATA,
                1,  14,  15,   1,  15,   0,   3,  12,  13,   3,  13,   2,   4,  11,  10, 
                4,  10,   5,   7,   8,   9,   7,   9,   6);
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
