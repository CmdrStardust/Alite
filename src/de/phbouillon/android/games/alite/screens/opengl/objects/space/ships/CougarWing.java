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

import java.io.Serializable;

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class CougarWing extends SpaceObject implements Serializable {
	private static final long serialVersionUID = 1735424471936957642L;

	private static final float [] VERTEX_DATA = new float [] {
        -66.00f,   0.00f,  99.00f, -118.80f,   0.00f, 158.40f, 
        -125.40f,  -3.30f, -198.00f, -125.40f,   3.30f, -198.00f, 
        -198.00f,   0.00f, -132.00f, -132.00f,   0.00f, -198.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.01020f,   0.99991f,  -0.00907f,  -0.98058f,   0.00000f,   0.19612f, 
          0.03640f,   0.99929f,  -0.00993f,  -0.01020f,  -0.99991f,  -0.00907f, 
          0.40825f,   0.81650f,   0.40825f,   0.00000f,   0.00000f,   1.00000f, 
          0.03640f,  -0.99929f,  -0.00993f,   0.40825f,  -0.81650f,   0.40825f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.68f,   0.04f,   0.02f,   0.92f,   0.97f,   0.04f, 
          0.99f,   0.07f,   0.37f,   0.98f,   0.39f,   0.99f, 
          0.97f,   0.04f,   0.02f,   0.92f,   0.37f,   0.96f, 
          0.97f,   0.04f,   0.02f,   0.92f,   0.68f,   0.04f, 
          0.02f,   0.92f,   0.03f,   0.94f,   0.37f,   0.96f, 
          0.02f,   0.48f,   0.01f,   0.46f,   0.00f,   0.48f, 
          0.37f,   0.96f,   0.02f,   0.92f,   0.97f,   0.04f, 
          0.37f,   0.96f,   0.03f,   0.94f,   0.02f,   0.92f
    };

    public CougarWing(Alite alite) {
        super(alite, "CougarWing", ObjectType.EnemyShip);
        shipType = ShipType.Cougar;
        boundingBox = new float [] {-198.00f,   0.00f,  -3.30f,   3.30f, -198.00f, 158.40f};
        numberOfVertices = 24;
        textureFilename = "textures/cougarwing.png";
        affectedByEnergyBomb = false;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   2,   1,   0,   3,   2,   1,   2,   4,   1,   3,   0,   2,   5,   4, 
                3,   5,   2,   4,   3,   1,   4,   5,   3);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
    }

    @Override
    public boolean isVisibleOnHud() {
        return false;
    }

    @Override
    public float getDistanceFromCenterToBorder(Vector3f dir) {
        return 50.0f;
    }

	@Override
	public Vector3f getHudColor() {
		return null;
	}
}
