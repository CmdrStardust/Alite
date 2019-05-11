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
 * Drake Mk II model and texture by Marko Susimetsa (Wolfwood)
 * Renamed to Bushmaster for Alite. 
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Bushmaster extends SpaceObject {
    public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -30.43f, -82.43f, 279.95f, -79.60f,  77.69f,  18.01f, 
         79.60f,  77.69f,  18.01f,  30.43f, -82.43f, 279.95f, 
        -184.52f, -26.58f, -279.95f, -184.52f,  51.82f, -279.95f, 
        184.52f,  51.82f, -279.95f, 184.52f, -26.58f, -279.95f, 
          0.00f,  46.23f,  18.01f, -200.00f, -70.18f,  66.09f, 
        200.00f, -70.18f,  66.09f, -105.91f,  73.61f, -146.73f, 
        105.91f,  73.61f, -146.73f, -170.08f, -65.37f, -71.85f, 
        170.08f, -65.37f, -71.85f, 145.47f,   4.12f, -109.29f, 
        -145.47f,   4.12f, -109.29f, 132.31f,   6.16f, -26.92f, 
        -132.31f,   6.16f, -26.92f,   0.00f,  82.43f, -279.95f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.32883f,   0.83198f,   0.44686f,  -0.02224f,  -0.99897f,  -0.03960f, 
          0.38067f,  -0.90393f,  -0.19494f,   0.00000f,  -0.99506f,  -0.09926f, 
         -0.26062f,  -0.94567f,  -0.19439f,   0.01735f,  -0.99911f,  -0.03855f, 
          0.00000f,  -0.99836f,  -0.05721f,  -0.58469f,   0.63869f,   0.50020f, 
         -0.78392f,   0.61760f,  -0.06360f,  -0.32883f,   0.83198f,   0.44686f, 
          0.83369f,   0.53910f,   0.11974f,   0.00000f,   0.89757f,   0.44088f, 
          0.58469f,   0.63869f,   0.50020f,  -0.99760f,   0.00000f,   0.06922f, 
          0.00000f,   0.00000f,  -1.00000f,  -0.16329f,   0.98446f,  -0.06463f, 
         -0.73060f,   0.59570f,   0.33371f,   0.00000f,   0.00000f,  -1.00000f, 
          0.99760f,   0.00000f,   0.06922f,   0.82619f,   0.46446f,   0.31890f, 
          0.16329f,   0.98446f,  -0.06463f,   0.00000f,   0.00000f,  -1.00000f, 
          0.36635f,   0.92690f,  -0.08149f,  -0.82754f,   0.53789f,  -0.16076f, 
          0.78392f,   0.61760f,  -0.06360f,  -0.83369f,   0.53910f,   0.11974f, 
         -0.36635f,   0.92690f,  -0.08149f,   0.00000f,   0.98647f,   0.16392f, 
          0.73060f,   0.59570f,   0.33371f,   0.83369f,   0.53910f,   0.11974f, 
          0.00000f,   0.99781f,   0.06607f,  -0.90875f,   0.39479f,   0.13531f, 
          0.90875f,   0.39479f,   0.13531f,   0.82754f,   0.53789f,  -0.16076f, 
         -0.82619f,   0.46446f,   0.31890f,  -0.83369f,   0.53910f,   0.11974f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.38f,   0.75f,   0.68f,   0.78f,   0.69f,   0.70f, 
          0.39f,   0.25f,   0.61f,   0.43f,   0.76f,   0.40f, 
          0.39f,   0.25f,   0.76f,   0.40f,   0.98f,   0.42f, 
          0.39f,   0.25f,   0.98f,   0.42f,   0.98f,   0.02f, 
          0.39f,   0.25f,   0.98f,   0.02f,   0.76f,   0.04f, 
          0.39f,   0.25f,   0.76f,   0.04f,   0.61f,   0.01f, 
          0.39f,   0.25f,   0.61f,   0.01f,   0.39f,   0.19f, 
          0.69f,   0.70f,   0.60f,   0.57f,   0.38f,   0.75f, 
          0.34f,   0.46f,   0.30f,   0.55f,   0.41f,   0.65f, 
          0.69f,   0.87f,   0.68f,   0.78f,   0.38f,   0.82f, 
          0.10f,   0.23f,   0.13f,   0.33f,   0.27f,   0.27f, 
          0.38f,   0.82f,   0.68f,   0.78f,   0.38f,   0.75f, 
          0.38f,   0.82f,   0.60f,   1.00f,   0.69f,   0.87f, 
          0.04f,   0.65f,   0.27f,   0.65f,   0.03f,   0.57f, 
          0.43f,   0.13f,   0.23f,   0.01f,   0.04f,   0.13f, 
          0.98f,   0.59f,   0.86f,   0.67f,   0.99f,   0.78f, 
          0.03f,   0.57f,   0.22f,   0.57f,   0.17f,   0.50f, 
          0.43f,   0.05f,   0.23f,   0.01f,   0.43f,   0.13f, 
          0.41f,   0.35f,   0.17f,   0.42f,   0.39f,   0.42f, 
          0.41f,   0.35f,   0.22f,   0.35f,   0.17f,   0.42f, 
          0.98f,   0.98f,   0.99f,   0.78f,   0.86f,   0.90f, 
          0.04f,   0.13f,   0.23f,   0.01f,   0.04f,   0.05f, 
          0.68f,   0.78f,   0.86f,   0.67f,   0.69f,   0.70f, 
          0.41f,   0.65f,   0.30f,   0.55f,   0.27f,   0.65f, 
          0.02f,   0.42f,   0.13f,   0.33f,   0.10f,   0.23f, 
          0.17f,   0.50f,   0.30f,   0.55f,   0.34f,   0.46f, 
          0.86f,   0.90f,   0.68f,   0.78f,   0.69f,   0.87f, 
          0.86f,   0.90f,   0.86f,   0.67f,   0.68f,   0.78f, 
          0.27f,   0.27f,   0.22f,   0.35f,   0.41f,   0.35f, 
          0.27f,   0.27f,   0.13f,   0.33f,   0.22f,   0.35f, 
          0.86f,   0.90f,   0.99f,   0.78f,   0.86f,   0.67f, 
          0.27f,   0.65f,   0.30f,   0.55f,   0.22f,   0.57f, 
          0.17f,   0.42f,   0.22f,   0.35f,   0.13f,   0.33f, 
          0.17f,   0.42f,   0.13f,   0.33f,   0.02f,   0.42f, 
          0.22f,   0.57f,   0.03f,   0.57f,   0.27f,   0.65f, 
          0.22f,   0.57f,   0.30f,   0.55f,   0.17f,   0.50f
    };

    public Bushmaster(Alite alite) {
        super(alite, "Bushmaster", ObjectType.EnemyShip);
        shipType = ShipType.Bushmaster;
        boundingBox = new float [] {-200.00f, 200.00f, -82.43f,  82.43f, -279.95f, 279.95f};
        numberOfVertices = 108;
        textureFilename = "textures/drakemkii.png";
        maxSpeed          = 400.8f;
        maxPitchSpeed     = 2.500f;
        maxRollSpeed      = 2.500f;
        hullStrength      = 200.0f;
        hasEcm            = true;
        cargoType         = 15;
        aggressionLevel   = 13;
        escapeCapsuleCaps = 1;
        bounty            = 150;
        score             = 200;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[9]);
        laserHardpoints.add(VERTEX_DATA[10]);
        laserHardpoints.add(VERTEX_DATA[11]);
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserColor        = 0x7F0000FFl;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createReversedRotatedFaces(VERTEX_DATA, NORMAL_DATA,
                                    0,   8,   1,   0,   9,  13,   0,  13,   4,   0,   4,   7,   0,   7,  14, 
                                    0,  14,  10,   0,  10,   3,   1,   9,   0,   1,  18,   9,   2,   8,   3, 
                                    2,  17,  12,   3,   8,   0,   3,  10,   2,   4,  13,   5,   4,  19,   7, 
                                    5,  11,  19,   5,  16,  11,   5,  19,   4,   6,  14,   7,   6,  15,  14, 
                                    6,  19,  12,   7,  19,   6,   8,  11,   1,   9,  18,  13,  10,  17,   2, 
                                   11,  18,   1,  12,   8,   2,  12,  11,   8,  12,  15,   6,  12,  17,  15, 
                                   12,  19,  11,  13,  18,  16,  14,  15,  17,  14,  17,  10,  16,   5,  13, 
                                   16,  18,  11);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 52, 19, 30, -100, 15, 0, 0.84f, 0.12f, 0.18f, 0.7f));
        	addExhaust(new EngineExhaust(this, 52, 19, 30,  100, 15, 0, 0.84f, 0.12f, 0.18f, 0.7f));
        }
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
