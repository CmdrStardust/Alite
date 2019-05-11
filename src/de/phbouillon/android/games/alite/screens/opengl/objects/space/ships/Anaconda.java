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
 * Anaconda model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Anaconda extends SpaceObject {
	private static final long serialVersionUID = -8823193650788636911L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);

    private static final float [] VERTEX_DATA = new float [] {
         -47.72f, -110.12f, -211.06f,   47.72f, -110.12f, -211.06f,
          80.76f,  -40.38f, -275.30f,   -0.00f,    0.00f, -312.00f,
         -80.76f,  -40.38f, -275.30f,  137.64f,   12.84f, -234.92f,
          78.92f,  110.12f, -247.76f,   -0.00f,  100.94f, -293.64f,
          80.76f,  -95.44f, -132.14f,  137.64f,  -14.68f, -146.82f,
         -80.76f,  -95.44f, -132.14f,   -0.00f,  -14.68f,  312.00f,
        -137.64f,   12.84f, -234.92f, -137.64f,  -14.68f, -146.82f,
         -78.92f,  110.12f, -247.76f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.30197f,   0.89975f,   0.31507f,  -0.35821f,   0.83539f,   0.41691f, 
         -0.48807f,   0.44954f,   0.74813f,   0.00000f,   0.14291f,   0.98974f, 
          0.48807f,   0.44954f,   0.74813f,  -0.86014f,   0.19891f,   0.46967f, 
         -0.58051f,  -0.75936f,   0.29392f,   0.00000f,  -0.55897f,   0.82919f, 
         -0.45125f,   0.88922f,  -0.07521f,  -0.98988f,   0.00829f,  -0.14164f, 
          0.54007f,   0.84023f,  -0.04833f,  -0.00000f,  -0.04653f,  -0.99892f, 
          0.86014f,   0.19891f,   0.46967f,   0.98988f,   0.00829f,  -0.14164f, 
          0.58051f,  -0.75936f,   0.29392f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.20f,   0.99f,   0.30f,   0.99f,   0.25f,   0.83f, 
          0.67f,   0.05f,   0.58f,   0.00f,   0.50f,   0.07f, 
          0.30f,   0.99f,   0.34f,   0.88f,   0.25f,   0.83f, 
          0.25f,   0.83f,   0.34f,   0.88f,   0.42f,   0.82f, 
          0.25f,   0.83f,   0.16f,   0.88f,   0.20f,   0.99f, 
          0.25f,   0.83f,   0.25f,   0.72f,   0.14f,   0.71f, 
          0.25f,   0.83f,   0.14f,   0.71f,   0.08f,   0.82f, 
          0.99f,   0.07f,   0.82f,   0.05f,   0.84f,   0.15f, 
          0.99f,   0.07f,   0.92f,   0.00f,   0.82f,   0.05f, 
          0.42f,   0.82f,   0.35f,   0.71f,   0.25f,   0.83f, 
          0.34f,   0.63f,   0.45f,   0.59f,   0.46f,   0.49f, 
          0.35f,   0.71f,   0.25f,   0.72f,   0.25f,   0.83f, 
          0.84f,   0.15f,   0.80f,   0.06f,   0.70f,   0.06f, 
          0.84f,   0.15f,   0.70f,   0.06f,   0.66f,   0.15f, 
          0.84f,   0.15f,   0.95f,   0.16f,   0.99f,   0.07f, 
          0.95f,   0.16f,   0.84f,   0.15f,   0.75f,   0.65f, 
          0.46f,   0.49f,   0.25f,   0.00f,   0.34f,   0.63f, 
          0.66f,   0.15f,   0.75f,   0.65f,   0.84f,   0.15f, 
          0.75f,   0.65f,   0.66f,   0.15f,   0.55f,   0.16f, 
          0.08f,   0.82f,   0.16f,   0.88f,   0.25f,   0.83f, 
          0.50f,   0.07f,   0.66f,   0.15f,   0.67f,   0.05f, 
          0.50f,   0.07f,   0.55f,   0.16f,   0.66f,   0.15f, 
          0.04f,   0.49f,   0.04f,   0.59f,   0.16f,   0.63f, 
          0.16f,   0.63f,   0.34f,   0.63f,   0.25f,   0.00f, 
          0.16f,   0.63f,   0.25f,   0.68f,   0.34f,   0.63f, 
          0.16f,   0.63f,   0.25f,   0.00f,   0.04f,   0.49f
    };

    public Anaconda(Alite alite) {
        super(alite, "Anaconda", ObjectType.Trader);
        shipType = ShipType.Anaconda;
        boundingBox = new float [] {-137.64f,  137.64f, -110.12f,  110.12f, -312.00f,  312.00f};
        numberOfVertices = 78;
        textureFilename = "textures/anaconda.png";        
        maxSpeed          = 167.0f;
        maxPitchSpeed     = 0.4f;
        maxRollSpeed      = 0.75f;
        hullStrength      = 112.0f;
        hasEcm            = true;
        cargoType         = 8;
        aggressionLevel   = 10;
        escapeCapsuleCaps = 3;
        bounty            = 0;
        score             = 80;
        legalityType      = 0;
        maxCargoCanisters = 3;
        laserHardpoints.add(VERTEX_DATA[33]);
        laserHardpoints.add(VERTEX_DATA[34]);
        laserHardpoints.add(VERTEX_DATA[35]);
        laserColor = 0x7FFF0000l;
        laserTexture = "textures/laser_red.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   1,   3,   0,   4,  12,   1,   2,   3,   3,   2,   5,   3,   4,   0, 
                3,   7,  14,   3,  14,  12,   5,   1,   8,   5,   2,   1,   5,   6,   3, 
                6,   5,   9,   6,   7,   3,   8,   1,   0,   8,   0,  10,   8,   9,   5, 
                9,   8,  11,   9,  11,   6,  10,  11,   8,  11,  10,  13,  12,   4,   3, 
               12,  10,   0,  12,  13,  10,  13,  12,  14,  14,   6,  11,  14,   7,   6, 
               14,  11,  13);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 19, 19, 40, -40, 25, -30));
        	addExhaust(new EngineExhaust(this, 19, 19, 40,  40, 25, -30));
        }
        initTargetBox();
    }
    
    @Override
    public void hasBeenHitByPlayer() {
    	computeLegalStatusAfterFriendlyHit();	
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
