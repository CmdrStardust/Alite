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

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class RolfsCobra extends SpaceObject {
	private static final long serialVersionUID = -6404691170260270451L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -48.44f,  -0.00f, -143.30f, -15.37f,  11.14f, -126.15f, 
         -0.00f,  -0.00f, -194.22f, -31.43f, -14.43f,   0.00f, 
        -31.43f,  -8.63f, -62.87f,  31.43f,  -8.63f, -62.87f, 
         31.43f, -14.43f,   0.00f,  -0.00f,  38.93f,  -0.00f, 
         -0.00f,  38.93f, -62.87f, -31.43f,  31.43f,  -0.00f, 
        -31.43f,  31.79f, -62.87f, -131.99f,  -0.00f, -62.87f, 
        -169.84f, -26.45f,   0.00f,  31.43f, -36.32f,  51.90f, 
        -31.43f, -36.32f,  51.90f,  -8.19f,  -1.61f,  78.62f, 
          0.00f,  47.52f,  92.07f, -174.12f, -65.04f,  53.07f, 
         31.43f, -114.63f, 165.78f, -31.43f, -114.63f, 165.78f, 
         -0.00f, -60.60f, 159.72f, -95.88f, -122.32f, 165.78f, 
        -31.43f, -86.97f, 165.78f,  48.44f,  -0.00f, -143.30f, 
         15.37f,  11.14f, -126.15f,  31.43f,  31.43f,  -0.00f, 
         -0.00f,  33.44f, -149.65f, 131.99f,  -0.00f, -62.87f, 
         31.43f,  31.79f, -62.87f, 169.84f, -26.45f,   0.00f, 
          8.19f,  -1.61f,  78.62f, 174.12f, -65.04f,  53.07f, 
         95.88f, -122.32f, 165.78f,  31.43f, -86.97f, 165.78f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.53193f,  -0.52771f,   0.66225f,   0.51063f,  -0.81834f,   0.26378f, 
          0.00000f,   0.30782f,   0.95144f,  -0.04180f,   0.94638f,   0.32035f, 
          0.04366f,   0.99532f,   0.08619f,  -0.05239f,   0.99500f,   0.08501f, 
          0.03130f,   0.95306f,   0.30115f,   0.00000f,  -0.97951f,  -0.20139f, 
          0.00000f,  -0.99384f,   0.11079f,   0.30180f,  -0.92430f,  -0.23361f, 
          0.23493f,  -0.95151f,   0.19856f,   0.84165f,   0.11507f,   0.52761f, 
          0.91706f,   0.09253f,   0.38786f,   0.07383f,   0.86051f,   0.50406f, 
         -0.07386f,   0.87722f,   0.47438f,   0.60732f,  -0.65475f,  -0.44997f, 
          0.00000f,  -0.86258f,  -0.50592f,   0.92190f,   0.36050f,  -0.14194f, 
          0.05255f,   0.88275f,  -0.46690f,  -0.03505f,   0.88378f,  -0.46658f, 
          0.00000f,  -0.60061f,  -0.79954f,   0.39948f,   0.19300f,  -0.89619f, 
          0.19111f,  -0.35989f,  -0.91321f,  -0.53193f,  -0.52771f,   0.66225f, 
         -0.51063f,  -0.81834f,   0.26378f,  -0.30180f,  -0.92430f,  -0.23361f, 
          0.00000f,  -0.94927f,   0.31446f,  -0.84165f,   0.11507f,   0.52761f, 
         -0.23493f,  -0.95151f,   0.19856f,  -0.91706f,   0.09253f,   0.38786f, 
         -0.60732f,  -0.65475f,  -0.44997f,  -0.92190f,   0.36050f,  -0.14194f, 
         -0.39948f,   0.19300f,  -0.89619f,  -0.15845f,  -0.29839f,  -0.94120f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.02f,   0.31f,   0.25f,   0.29f,   0.18f,   0.23f, 
          0.75f,   0.63f,   0.75f,   0.49f,   0.64f,   0.49f, 
          0.75f,   0.63f,   0.64f,   0.49f,   0.64f,   0.63f, 
          0.75f,   0.49f,   0.70f,   0.31f,   0.64f,   0.49f, 
          0.63f,   0.26f,   0.47f,   0.31f,   0.63f,   0.31f, 
          0.47f,   0.26f,   0.25f,   0.29f,   0.47f,   0.31f, 
          0.63f,   0.05f,   0.47f,   0.09f,   0.63f,   0.26f, 
          0.64f,   0.63f,   0.47f,   0.48f,   0.41f,   0.64f, 
          0.47f,   0.09f,   0.18f,   0.23f,   0.47f,   0.26f, 
          0.64f,   0.49f,   0.62f,   0.38f,   0.47f,   0.48f, 
          0.64f,   0.73f,   0.75f,   0.73f,   0.75f,   0.63f, 
          0.64f,   0.73f,   0.75f,   0.63f,   0.64f,   0.63f, 
          0.13f,   0.02f,   0.16f,   0.15f,   0.01f,   0.15f, 
          0.77f,   0.30f,   0.79f,   0.05f,   0.63f,   0.26f, 
          0.40f,   0.76f,   0.64f,   0.73f,   0.64f,   0.63f, 
          0.40f,   0.76f,   0.64f,   0.63f,   0.41f,   0.64f, 
          0.64f,   0.98f,   0.75f,   0.98f,   0.75f,   0.73f, 
          0.64f,   0.98f,   0.75f,   0.73f,   0.64f,   0.73f, 
          0.32f,   0.15f,   0.16f,   0.15f,   0.13f,   0.02f, 
          0.96f,   0.27f,   0.99f,   0.17f,   0.77f,   0.30f, 
          0.54f,   0.99f,   0.64f,   0.73f,   0.40f,   0.76f, 
          0.25f,   0.29f,   0.02f,   0.31f,   0.18f,   0.23f, 
          0.78f,   0.38f,   0.70f,   0.31f,   0.75f,   0.49f, 
          0.47f,   0.31f,   0.63f,   0.26f,   0.63f,   0.31f, 
          0.19f,   0.38f,   0.25f,   0.29f,   0.47f,   0.31f, 
          0.47f,   0.26f,   0.47f,   0.09f,   0.63f,   0.26f, 
          0.92f,   0.48f,   0.75f,   0.63f,   0.99f,   0.64f, 
          0.25f,   0.29f,   0.18f,   0.23f,   0.47f,   0.26f, 
          0.78f,   0.38f,   0.75f,   0.49f,   0.92f,   0.48f, 
          0.63f,   0.26f,   0.77f,   0.30f,   0.63f,   0.31f, 
          0.63f,   0.05f,   0.79f,   0.05f,   0.63f,   0.26f, 
          0.75f,   0.63f,   0.75f,   0.73f,   0.99f,   0.76f, 
          0.75f,   0.63f,   0.99f,   0.76f,   0.99f,   0.64f, 
          0.16f,   0.15f,   0.32f,   0.15f,   0.13f,   0.02f, 
          0.79f,   0.05f,   0.99f,   0.17f,   0.77f,   0.30f, 
          0.75f,   0.73f,   0.86f,   0.99f,   0.99f,   0.76f, 
          0.02f,   0.31f,   0.19f,   0.38f,   0.25f,   0.29f, 
          0.25f,   0.29f,   0.19f,   0.38f,   0.02f,   0.31f, 
          0.70f,   0.31f,   0.62f,   0.38f,   0.64f,   0.49f, 
          0.16f,   0.15f,   0.13f,   0.02f,   0.01f,   0.15f, 
          0.77f,   0.30f,   0.63f,   0.26f,   0.63f,   0.31f, 
          0.40f,   0.97f,   0.40f,   0.91f,   0.29f,   0.91f, 
          0.40f,   0.97f,   0.29f,   0.91f,   0.29f,   0.97f, 
          0.29f,   0.91f,   0.19f,   0.98f,   0.29f,   0.97f, 
          0.40f,   0.97f,   0.50f,   0.98f,   0.40f,   0.91f, 
          0.40f,   0.91f,   0.35f,   0.86f,   0.29f,   0.91f, 
          0.77f,   0.30f,   0.96f,   0.27f,   0.93f,   0.31f, 
          0.93f,   0.31f,   0.96f,   0.27f,   0.77f,   0.30f, 
          0.25f,   0.29f,   0.19f,   0.38f,   0.47f,   0.31f, 
          0.25f,   0.29f,   0.47f,   0.26f,   0.47f,   0.31f, 
          0.18f,   0.23f,   0.47f,   0.09f,   0.47f,   0.26f, 
          0.18f,   0.23f,   0.25f,   0.29f,   0.47f,   0.26f, 
          0.99f,   0.17f,   0.96f,   0.27f,   0.77f,   0.30f, 
          0.99f,   0.17f,   0.79f,   0.05f,   0.77f,   0.30f, 
          0.75f,   0.73f,   0.75f,   0.98f,   0.86f,   0.99f, 
          0.54f,   0.99f,   0.64f,   0.98f,   0.64f,   0.73f, 
          0.92f,   0.48f,   0.75f,   0.49f,   0.75f,   0.63f, 
          0.64f,   0.63f,   0.64f,   0.49f,   0.47f,   0.48f, 
          0.63f,   0.26f,   0.47f,   0.26f,   0.47f,   0.31f, 
          0.47f,   0.31f,   0.47f,   0.26f,   0.63f,   0.26f, 
          0.47f,   0.09f,   0.63f,   0.05f,   0.63f,   0.26f, 
          0.47f,   0.09f,   0.47f,   0.26f,   0.63f,   0.26f, 
          0.79f,   0.05f,   0.63f,   0.05f,   0.63f,   0.26f, 
          0.79f,   0.05f,   0.77f,   0.30f,   0.63f,   0.26f
    };

    public RolfsCobra(Alite alite) {
        super(alite, "Rolfs Cobra", ObjectType.Trader);
        shipType = ShipType.Rattlesnake;
        boundingBox = new float [] {-174.12f, 174.12f, -122.32f,  47.52f, -194.22f, 165.78f};
        numberOfVertices = 192;
        textureFilename = "textures/rolfscobra.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.000f;
        maxRollSpeed      = 2.000f;
        hullStrength      =  72.0f;
        hasEcm            = true;
        cargoType         = 7; 
        aggressionLevel   = 10;
        escapeCapsuleCaps = 3;
        bounty            = 50;
        score             = 80;
        legalityType      = 0;
        maxCargoCanisters = 3;        
        laserHardpoints.add(VERTEX_DATA[3]);
        laserHardpoints.add(VERTEX_DATA[4]);
        laserHardpoints.add(VERTEX_DATA[5]);
        laserHardpoints.add(VERTEX_DATA[6]);
        laserHardpoints.add(VERTEX_DATA[7]);
        laserHardpoints.add(VERTEX_DATA[8]);
        init();
        
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                2,   1,   0,   6,   5,   4,   6,   4,   3,   5,   2,   4,   9,   8,   7, 
               10,   1,   8,  12,  11,   9,   3,  11,  12,  11,   0,  10,   4,   0,  11, 
               14,  13,   6,  14,   6,   3,  16,  15,   7,  15,  17,   9,  17,  14,   3, 
               17,   3,  12,  19,  18,  13,  19,  13,  14,  20,  15,  16,  22,  21,  15, 
               21,  14,  17,  24,   2,  23,  23,   2,   5,   8,  25,   7,  26,  24,   8, 
               28,  27,  25,  27,   6,  29,  24,  23,  28,  23,   5,  27,  25,  30,   7, 
               29,  31,  25,   6,  13,  31,   6,  31,  29,  30,  20,  16,  31,  32,  30, 
               13,  32,  31,   2,  26,   1,  24,  26,   2,   2,   0,   4,  30,  16,   7, 
               15,   9,   7,  19,  22,  33,  19,  33,  18,  33,  32,  18,  19,  21,  22, 
               22,  20,  33,  30,  33,  20,  20,  22,  15,   1,  26,   8,  24,  28,   8, 
               23,  27,  28,   0,   1,  10,  32,  33,  30,  21,  17,  15,  13,  18,  32, 
               21,  19,  14,  27,   5,   6,   3,   4,  11,   9,  10,   8,   8,  28,  25, 
               27,  29,  25,  11,  10,   9,  17,  12,   9,  31,  30,  25);
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
