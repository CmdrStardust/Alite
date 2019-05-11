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
 * Adder Mk II model and textures from Oolite
 * Renamed to Gopher for Alite. 
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Gopher extends SpaceObject {
    public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -74.64f, -38.62f, -177.92f,  74.64f, -38.62f, -177.92f, 
        -74.64f, -38.62f, -59.31f,  74.64f, -38.62f, -59.31f, 
        -74.64f, -38.62f,  59.31f,  74.64f, -38.62f,  59.31f, 
        -52.74f,   0.37f, 177.92f,  52.74f,   0.37f, 177.92f, 
        -57.61f,  38.62f, -177.92f, -57.61f,  19.05f, -177.92f, 
         57.61f,  19.05f, -177.92f,  57.61f,  38.62f, -177.92f, 
        -57.61f,  38.62f, -59.31f, -57.61f,  19.05f, -59.31f, 
         57.61f,  19.05f, -59.31f,  57.61f,  38.62f, -59.31f, 
        -57.61f,  38.62f,  -0.89f, -38.88f,  37.21f,  37.88f, 
         38.88f,  37.21f,  37.88f,  57.61f,  38.62f,  -0.89f, 
        -100.00f,   0.37f, -177.92f, -57.61f,   0.37f, -177.92f, 
         57.61f,   0.37f, -177.92f, 100.00f,   0.37f, -177.92f, 
        100.00f,   0.37f, -59.31f, 100.00f,   0.37f,  59.31f, 
        -100.00f,   0.37f,  59.31f, -100.00f,   0.37f, -59.31f, 
        -32.93f,  18.58f,  69.33f,  32.93f,  18.58f,  69.33f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,   0.00000f, 
          0.00000f,   1.00000f,  -0.00000f,   0.00000f,   1.00000f,   0.00000f, 
          0.00000f,   0.95001f,  -0.31223f,   0.00000f,   0.95001f,  -0.31223f, 
          0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f, 
          0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f, 
          0.00000f,  -0.98623f,  -0.16541f,   0.00000f,  -0.98623f,  -0.16541f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,  -0.83820f,   0.54537f,   0.00000f, 
         -0.83820f,   0.54537f,   0.00000f,  -0.83820f,   0.54537f,  -0.00000f, 
         -0.83820f,   0.54537f,  -0.00000f,  -0.66991f,  -0.74244f,  -0.00000f, 
         -0.66991f,  -0.74244f,   0.00000f,   0.83820f,   0.54537f,   0.00000f, 
          0.83820f,   0.54537f,   0.00000f,   0.83820f,   0.54537f,   0.00000f, 
          0.83820f,   0.54537f,   0.00000f,   0.66991f,  -0.74244f,   0.00000f, 
          0.66991f,  -0.74244f,   0.00000f,   0.66991f,  -0.74244f,   0.00000f, 
          0.66991f,  -0.74244f,   0.00000f,  -1.00000f,   0.00000f,  -0.00000f, 
         -1.00000f,   0.00000f,  -0.00000f,   1.00000f,   0.00000f,   0.00000f, 
          1.00000f,  -0.00000f,   0.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,  -0.00000f,  -0.99934f,  -0.03625f, 
          0.00000f,  -0.99934f,  -0.03625f,   0.00000f,  -0.86036f,  -0.50969f, 
          0.00000f,  -0.86036f,  -0.50969f,  -0.66991f,  -0.74244f,   0.00000f, 
         -0.66991f,  -0.74244f,   0.00000f,   0.35545f,  -0.91199f,  -0.20476f, 
         -0.35545f,  -0.91199f,  -0.20476f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,  -0.79503f,   0.51728f,  -0.31676f, 
         -0.48969f,  -0.84979f,  -0.19511f,   0.79503f,   0.51728f,  -0.31676f, 
          0.48969f,  -0.84979f,  -0.19511f,  -0.73033f,  -0.63936f,  -0.24049f, 
          0.73033f,  -0.63936f,  -0.24048f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.82f,   0.54f,   0.82f,   0.36f,   0.61f,   0.36f, 
          0.82f,   0.54f,   0.61f,   0.36f,   0.61f,   0.54f, 
          0.82f,   0.36f,   0.82f,   0.19f,   0.61f,   0.19f, 
          0.82f,   0.36f,   0.61f,   0.19f,   0.61f,   0.36f, 
          0.82f,   0.19f,   0.79f,   0.01f,   0.64f,   0.01f, 
          0.82f,   0.19f,   0.64f,   0.01f,   0.61f,   0.19f, 
          0.63f,   0.99f,   1.00f,   0.99f,   1.00f,   0.61f, 
          0.63f,   0.99f,   1.00f,   0.61f,   0.63f,   0.61f, 
          0.33f,   0.32f,   0.13f,   0.32f,   0.13f,   0.42f, 
          0.33f,   0.32f,   0.13f,   0.42f,   0.33f,   0.42f, 
          0.17f,   0.23f,   0.30f,   0.23f,   0.34f,   0.01f, 
          0.17f,   0.23f,   0.34f,   0.01f,   0.13f,   0.01f, 
          0.08f,   1.00f,   0.54f,   1.00f,   0.49f,   0.87f, 
          0.08f,   1.00f,   0.49f,   0.87f,   0.13f,   0.87f, 
          0.00f,   0.87f,   0.13f,   0.87f,   0.13f,   0.82f, 
          0.00f,   0.87f,   0.13f,   0.82f,   0.13f,   0.76f, 
          0.13f,   0.87f,   0.49f,   0.87f,   0.49f,   0.82f, 
          0.13f,   0.87f,   0.49f,   0.82f,   0.13f,   0.82f, 
          0.49f,   0.87f,   0.62f,   0.87f,   0.49f,   0.76f, 
          0.49f,   0.52f,   0.55f,   0.35f,   0.49f,   0.33f, 
          0.49f,   0.52f,   0.49f,   0.33f,   0.43f,   0.49f, 
          0.55f,   0.35f,   0.61f,   0.19f,   0.54f,   0.17f, 
          0.55f,   0.35f,   0.54f,   0.17f,   0.49f,   0.33f, 
          0.02f,   0.33f,   0.04f,   0.53f,   0.13f,   0.42f, 
          0.02f,   0.33f,   0.13f,   0.42f,   0.12f,   0.32f, 
          0.82f,   0.19f,   0.88f,   0.35f,   0.94f,   0.33f, 
          0.82f,   0.19f,   0.94f,   0.33f,   0.88f,   0.17f, 
          0.88f,   0.35f,   0.93f,   0.52f,   1.00f,   0.49f, 
          0.88f,   0.35f,   1.00f,   0.49f,   0.94f,   0.33f, 
          0.42f,   0.53f,   0.44f,   0.33f,   0.34f,   0.32f, 
          0.42f,   0.53f,   0.34f,   0.32f,   0.33f,   0.42f, 
          0.44f,   0.33f,   0.46f,   0.13f,   0.36f,   0.12f, 
          0.44f,   0.33f,   0.36f,   0.12f,   0.34f,   0.32f, 
          0.43f,   0.59f,   0.61f,   0.59f,   0.61f,   0.56f, 
          0.43f,   0.59f,   0.61f,   0.56f,   0.43f,   0.56f, 
          0.80f,   0.59f,   0.99f,   0.59f,   0.99f,   0.56f, 
          0.80f,   0.59f,   0.99f,   0.56f,   0.80f,   0.56f, 
          0.61f,   0.59f,   0.80f,   0.59f,   0.80f,   0.56f, 
          0.61f,   0.59f,   0.80f,   0.56f,   0.61f,   0.56f, 
          0.16f,   0.49f,   0.30f,   0.49f,   0.33f,   0.42f, 
          0.16f,   0.49f,   0.33f,   0.42f,   0.13f,   0.42f, 
          0.15f,   0.31f,   0.31f,   0.31f,   0.30f,   0.23f, 
          0.15f,   0.31f,   0.30f,   0.23f,   0.17f,   0.23f, 
          0.10f,   0.12f,   0.00f,   0.13f,   0.02f,   0.33f, 
          0.10f,   0.12f,   0.02f,   0.33f,   0.12f,   0.32f, 
          0.33f,   0.42f,   0.30f,   0.49f,   0.33f,   0.74f, 
          0.16f,   0.49f,   0.13f,   0.42f,   0.13f,   0.74f, 
          0.13f,   0.87f,   0.00f,   0.87f,   0.08f,   1.00f, 
          0.62f,   0.87f,   0.49f,   0.87f,   0.54f,   1.00f, 
          0.64f,   0.01f,   0.54f,   0.17f,   0.61f,   0.19f, 
          0.13f,   0.74f,   0.13f,   0.42f,   0.04f,   0.53f, 
          0.79f,   0.01f,   0.82f,   0.19f,   0.88f,   0.17f, 
          0.33f,   0.74f,   0.42f,   0.53f,   0.33f,   0.42f, 
          0.09f,   0.02f,   0.15f,   0.31f,   0.17f,   0.23f, 
          0.38f,   0.02f,   0.30f,   0.23f,   0.31f,   0.31f
    };

    public Gopher(Alite alite) {
        super(alite, "Gopher", ObjectType.EnemyShip);
        shipType = ShipType.Gopher;
        boundingBox = new float [] {-100.00f, 100.00f, -38.62f,  38.62f, -177.92f, 177.92f};
        numberOfVertices = 165;
        textureFilename = "textures/addermkii.png";
        maxSpeed          = 400.8f;
        maxPitchSpeed     = 1.500f;
        maxRollSpeed      = 2.750f;
        hullStrength      = 190.0f;
        hasEcm            = true;
        cargoType         = 3;
        aggressionLevel   = 10;
        escapeCapsuleCaps = 1;
        bounty            = 100;
        score             = 190;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[21]);
        laserHardpoints.add(VERTEX_DATA[22]);
        laserHardpoints.add(VERTEX_DATA[23]);
        laserHardpoints.add(VERTEX_DATA[18]);
        laserHardpoints.add(VERTEX_DATA[19]);
        laserHardpoints.add(VERTEX_DATA[20]);
        laserColor        = 0x7FFF0000l;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                                    0,   2,   3,   0,   3,   1,   2,   4,   5,   2,   5,   3,   4,   6,   7, 
                                    4,   7,   5,   9,  10,  14,   9,  14,  13,  12,  15,  19,  12,  19,  16, 
                                   28,  29,   7,  28,   7,   6,   0,   1,  22,   0,  22,  21,  20,  21,   9, 
                                   20,   9,   8,  21,  22,  10,  21,  10,   9,  22,  23,  11,   1,   3,  24, 
                                    1,  24,  23,   3,   5,  25,   3,  25,  24,  24,  25,  19,  24,  19,  15, 
                                    4,   2,  27,   4,  27,  26,   2,   0,  20,   2,  20,  27,  26,  27,  12, 
                                   26,  12,  16,  27,  20,   8,  27,   8,  12,   9,  13,  12,   9,  12,   8, 
                                   14,  10,  11,  14,  11,  15,  13,  14,  15,  13,  15,  12,  18,  17,  16, 
                                   18,  16,  19,  17,  18,  29,  17,  29,  28,  11,  23,  24,  11,  24,  15, 
                                   16,  17,   6,  18,  19,   7,  21,  20,   0,  23,  22,   1,   7,  25,   5, 
                                    7,  19,  25,   6,   4,  26,   6,  26,  16,   6,  17,  28,   7,  29,  18);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 16, 16, 22, -40, -10, 0, 0.55f, 0.56f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 16, 16, 22,  40, -10, 0, 0.55f, 0.56f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 16, 16, 22,   0, -10, 0, 0.55f, 0.56f, 0.8f, 0.7f));
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
