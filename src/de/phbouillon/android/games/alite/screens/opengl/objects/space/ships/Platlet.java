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
 * Platlet model and texture from Oolite: http://oolite.org
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Platlet extends SpaceObject {
	private static final long serialVersionUID = 6893800558985740414L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -20.00f, -36.00f,  -0.80f, -20.00f, -36.00f,   0.80f, 
        -20.00f,  36.00f,   0.80f, -20.00f,  36.00f,  -0.80f, 
         20.00f, -36.00f,  -0.80f,  20.00f, -36.00f,   0.80f, 
         20.00f,  36.00f,   0.80f,  20.00f,  36.00f,  -0.80f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,  -0.00000f, 
         -0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,  -0.00000f, 
         -1.00000f,  -0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f, 
          1.00000f,   0.00000f,   0.00000f,   1.00000f,  -0.00000f,   0.00000f, 
          0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          1.00f,   1.00f,   0.00f,   1.00f,   0.00f,   0.00f, 
          1.00f,   1.00f,   0.00f,   0.00f,   1.00f,   0.00f, 
          1.00f,   1.00f,   0.00f,   1.00f,   0.00f,   0.00f, 
          1.00f,   1.00f,   0.00f,   0.00f,   1.00f,   0.00f, 
          1.00f,   1.00f,   0.00f,   0.00f,   1.00f,   0.00f, 
          0.00f,   1.00f,   0.00f,   0.00f,   1.00f,   1.00f, 
          1.00f,   1.00f,   0.00f,   0.00f,   1.00f,   0.00f, 
          0.00f,   1.00f,   0.00f,   0.00f,   1.00f,   1.00f, 
          0.00f,   1.00f,   0.00f,   0.00f,   1.00f,   1.00f, 
          1.00f,   1.00f,   0.00f,   0.00f,   1.00f,   0.00f, 
          1.00f,   1.00f,   0.00f,   1.00f,   0.00f,   0.00f, 
          1.00f,   1.00f,   0.00f,   0.00f,   1.00f,   0.00f
    };

    public Platlet(Alite alite) {
        super(alite, "Platlet", ObjectType.Platlet);
        shipType = ShipType.Platlet;
        boundingBox = new float [] {-20.00f,  20.00f, -36.00f,  36.00f,  -0.80f,   0.80f};
        numberOfVertices = 36;
        textureFilename = "textures/platlet.png";
        maxSpeed          =  50.5f;
        maxPitchSpeed     = 0.100f;
        maxRollSpeed      = 0.100f;
        hullStrength      =  33.0f;
        hasEcm            = false;
        cargoType         = 0;
        aggressionLevel   = 0;
        escapeCapsuleCaps = 0;
        bounty            = 0;
        legalityType      = 3;
        maxCargoCanisters = 0;
        missileCount      = 0;        
        spawnCargoCanisters = false;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                7,   3,   2,   7,   2,   6,   5,   1,   0,   5,   0,   4,   2,   0,   1, 
                3,   0,   2,   6,   4,   7,   5,   4,   6,   0,   3,   4,   4,   3,   7, 
                6,   2,   1,   6,   1,   5);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename); 
        initTargetBox();  	
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
