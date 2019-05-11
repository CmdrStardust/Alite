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
 * Mosquito model and texture from Oolite.
 * Renamed to Indigo for Alite. 
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Indigo extends SpaceObject {
    public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         51.34f, -50.70f, -128.51f,  17.11f, -50.70f, -128.51f, 
        -17.11f, -50.70f, -128.51f, -51.34f, -50.70f, -128.51f, 
         81.93f, -13.91f, -150.75f,  27.30f, -13.91f, -150.75f, 
        -27.30f, -13.91f, -150.75f, -81.91f, -13.91f, -150.75f, 
         81.93f,   3.48f, -150.75f,  27.30f,  36.15f, -150.75f, 
        -27.30f,  36.15f, -150.75f, -81.91f,   3.48f, -150.75f, 
          0.00f, -13.91f, 150.75f,  51.34f, -50.70f, -66.93f, 
         17.11f, -50.70f, -66.93f, -17.11f, -50.70f, -66.93f, 
        -51.34f, -50.70f, -66.93f, -81.91f, -13.91f, -66.93f, 
        -81.91f,   3.48f, -66.93f, -27.30f,  36.15f, -66.93f, 
         27.30f,  36.15f, -66.93f,  81.93f,   3.48f, -66.93f, 
         81.93f, -13.91f, -66.93f,  81.91f, -13.91f, -12.56f, 
         27.30f, -24.93f, -12.56f, -27.30f, -24.93f, -12.56f, 
        -81.91f, -13.91f, -12.56f, -81.91f,   3.48f, -12.56f, 
        -27.30f,  36.15f, -12.56f,  27.30f,  36.15f, -12.56f, 
         81.91f,   3.48f, -12.56f, -18.57f,  50.62f, -65.29f, 
         18.57f,  50.62f, -65.29f, -18.57f,  50.70f, -11.62f, 
         18.57f,  50.70f, -11.62f, 160.00f, -25.36f, -126.27f, 
        -160.00f, -25.36f, -126.27f, 160.00f, -20.14f, -126.27f, 
        -160.00f, -20.14f, -126.27f, -160.00f, -25.36f, -91.26f, 
        -160.00f, -20.14f, -91.26f, 160.00f, -20.14f, -91.26f, 
        160.00f, -25.36f, -91.26f, -160.00f, -25.36f, -68.56f, 
        -160.00f, -20.14f, -68.56f, 160.00f, -20.14f, -68.56f, 
        160.00f, -25.36f, -68.56f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,   0.51736f,   0.85577f,   0.00000f,   0.51736f,   0.85577f, 
          0.00000f,   0.51736f,   0.85577f,   0.00000f,   0.51736f,   0.85577f, 
          0.00000f,   0.51736f,   0.85577f,   0.00000f,   0.51736f,   0.85577f, 
          0.00000f,  -0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,  -0.00000f, 
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,  -0.00000f, 
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,  -0.00000f, 
          0.00000f,   0.90363f,  -0.42832f,  -0.17318f,   0.85815f,  -0.48331f, 
          0.00000f,   0.90363f,  -0.42832f,   0.00000f,   0.90363f,  -0.42832f, 
         -0.00000f,   0.82820f,  -0.56043f,   0.18189f,   0.90131f,  -0.39313f, 
          0.76908f,   0.63916f,  -0.00000f,   0.76908f,   0.63915f,  -0.00000f, 
          1.00000f,  -0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f, 
          1.00000f,   0.00000f,  -0.00000f,   1.00000f,   0.00000f,  -0.00000f, 
          0.51343f,  -0.85813f,   0.00000f,   0.51343f,  -0.85813f,   0.00000f, 
          0.00000f,  -0.98597f,   0.16692f,   0.00000f,  -0.98597f,   0.16692f, 
         -0.51327f,  -0.85822f,  -0.00005f,  -0.51333f,  -0.85819f,   0.00000f, 
          0.51343f,  -0.85813f,  -0.00000f,   0.51343f,  -0.85813f,  -0.00000f, 
          0.00000f,  -1.00000f,   0.00149f,   0.00000f,  -1.00000f,   0.00149f, 
         -0.51333f,  -0.85819f,  -0.00014f,  -0.51343f,  -0.85813f,   0.00000f, 
         -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f, 
         -0.76888f,   0.63939f,   0.00000f,  -0.76888f,   0.63939f,   0.00000f, 
         -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f, 
          0.85603f,  -0.51693f,  -0.00000f,   0.85726f,  -0.51489f,   0.00077f, 
         -0.85728f,  -0.51485f,   0.00000f,  -0.85599f,  -0.51700f,   0.00077f, 
          0.29910f,   0.00000f,   0.95422f,   0.29910f,   0.00000f,   0.95422f, 
          0.28946f,  -0.95719f,   0.00000f,   0.28946f,  -0.95719f,   0.00000f, 
         -0.14513f,   0.98941f,  -0.00000f,  -0.14513f,   0.98941f,  -0.00000f, 
          0.28946f,  -0.95719f,  -0.00000f,   0.28946f,  -0.95719f,  -0.00000f, 
          0.58271f,   0.00000f,  -0.81268f,   0.58271f,   0.00000f,  -0.81268f, 
         -0.14513f,   0.98941f,   0.00000f,  -0.14513f,   0.98941f,   0.00000f, 
         -0.29916f,   0.00006f,   0.95420f,  -0.29918f,   0.00000f,   0.95420f, 
          0.14517f,   0.98941f,  -0.00000f,   0.14517f,   0.98941f,   0.00000f, 
         -0.28953f,  -0.95717f,  -0.00003f,  -0.28951f,  -0.95718f,   0.00000f, 
          0.14517f,   0.98941f,   0.00005f,   0.14517f,   0.98941f,   0.00000f, 
         -0.58271f,   0.00000f,  -0.81268f,  -0.58271f,   0.00000f,  -0.81268f, 
         -0.28953f,  -0.95717f,  -0.00008f,  -0.28946f,  -0.95719f,   0.00000f, 
          0.00000f,   0.00000f,   1.00000f,  -0.00000f,  -0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,  -0.00000f,   0.00000f,   1.00000f, 
         -0.19685f,   0.97545f,  -0.09873f,   0.00000f,   0.99773f,  -0.06732f, 
          0.19685f,   0.97545f,  -0.09873f,   0.76908f,   0.63915f,  -0.00000f, 
          0.89387f,   0.00000f,  -0.44833f,   0.48478f,  -0.81024f,  -0.32939f, 
          0.00000f,  -0.92915f,  -0.36970f,  -0.48478f,  -0.81024f,  -0.32939f, 
         -0.76888f,   0.63939f,  -0.00027f,  -0.89387f,  -0.00000f,  -0.44833f, 
          0.85603f,  -0.51693f,   0.00000f,  -0.85603f,  -0.51693f,   0.00000f, 
          0.82963f,  -0.47967f,  -0.28572f,  -0.82963f,  -0.47966f,  -0.28572f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.31f,   1.00f,   0.20f,   0.85f,   0.39f,   0.85f, 
          0.31f,   1.00f,   0.39f,   0.85f,   0.43f,   1.00f, 
          0.43f,   1.00f,   0.39f,   0.85f,   0.58f,   0.85f, 
          0.43f,   1.00f,   0.58f,   0.85f,   0.54f,   1.00f, 
          0.54f,   1.00f,   0.58f,   0.85f,   0.77f,   0.85f, 
          0.54f,   1.00f,   0.77f,   0.85f,   0.66f,   1.00f, 
          0.39f,   0.85f,   0.39f,   0.67f,   0.58f,   0.67f, 
          0.39f,   0.85f,   0.58f,   0.67f,   0.58f,   0.85f, 
          0.25f,   0.09f,   0.25f,   0.13f,   0.33f,   0.13f, 
          0.25f,   0.09f,   0.33f,   0.13f,   0.33f,   0.09f, 
          0.25f,   0.13f,   0.25f,   0.18f,   0.33f,   0.18f, 
          0.25f,   0.13f,   0.33f,   0.18f,   0.33f,   0.13f, 
          0.25f,   0.18f,   0.25f,   0.22f,   0.33f,   0.22f, 
          0.25f,   0.18f,   0.33f,   0.22f,   0.33f,   0.18f, 
          0.33f,   0.09f,   0.33f,   0.13f,   0.41f,   0.12f, 
          0.33f,   0.09f,   0.41f,   0.12f,   0.41f,   0.05f, 
          0.33f,   0.13f,   0.33f,   0.18f,   0.41f,   0.19f, 
          0.33f,   0.13f,   0.41f,   0.19f,   0.41f,   0.12f, 
          0.33f,   0.18f,   0.33f,   0.22f,   0.41f,   0.26f, 
          0.33f,   0.18f,   0.41f,   0.26f,   0.41f,   0.19f, 
          0.25f,   0.22f,   0.22f,   0.28f,   0.33f,   0.28f, 
          0.25f,   0.22f,   0.33f,   0.28f,   0.33f,   0.22f, 
          0.00f,   0.83f,   0.01f,   0.83f,   0.01f,   0.76f, 
          0.00f,   0.83f,   0.01f,   0.76f,   0.00f,   0.76f, 
          0.00f,   0.76f,   0.01f,   0.76f,   0.01f,   0.71f, 
          0.00f,   0.76f,   0.01f,   0.71f,   0.00f,   0.71f, 
          0.05f,   0.02f,   0.17f,   0.02f,   0.17f,   0.17f, 
          0.05f,   0.02f,   0.17f,   0.17f,   0.05f,   0.17f, 
          0.43f,   0.67f,   0.54f,   0.67f,   0.52f,   0.48f, 
          0.43f,   0.67f,   0.52f,   0.48f,   0.44f,   0.48f, 
          0.83f,   0.02f,   0.95f,   0.02f,   0.95f,   0.17f, 
          0.83f,   0.02f,   0.95f,   0.17f,   0.83f,   0.17f, 
          0.05f,   0.17f,   0.17f,   0.17f,   0.17f,   0.27f, 
          0.05f,   0.17f,   0.17f,   0.27f,   0.05f,   0.27f, 
          0.44f,   0.48f,   0.52f,   0.48f,   0.52f,   0.37f, 
          0.44f,   0.48f,   0.52f,   0.37f,   0.44f,   0.37f, 
          0.83f,   0.17f,   0.95f,   0.17f,   0.95f,   0.27f, 
          0.83f,   0.17f,   0.95f,   0.27f,   0.83f,   0.27f, 
          0.99f,   0.82f,   1.00f,   0.82f,   1.00f,   0.75f, 
          0.99f,   0.82f,   1.00f,   0.75f,   0.99f,   0.75f, 
          0.24f,   0.00f,   0.25f,   0.07f,   0.33f,   0.09f, 
          0.24f,   0.00f,   0.33f,   0.09f,   0.35f,   0.03f, 
          0.99f,   0.75f,   1.00f,   0.75f,   1.00f,   0.70f, 
          0.99f,   0.75f,   1.00f,   0.70f,   0.99f,   0.70f, 
          0.41f,   0.36f,   0.37f,   0.47f,   0.40f,   0.48f, 
          0.41f,   0.36f,   0.40f,   0.48f,   0.44f,   0.37f, 
          0.60f,   0.47f,   0.56f,   0.36f,   0.52f,   0.37f, 
          0.60f,   0.47f,   0.52f,   0.37f,   0.56f,   0.48f, 
          0.02f,   1.00f,   0.06f,   1.00f,   0.01f,   0.83f, 
          0.02f,   1.00f,   0.01f,   0.83f,   0.00f,   0.83f, 
          0.18f,   0.88f,   0.18f,   0.70f,   0.01f,   0.76f, 
          0.18f,   0.88f,   0.01f,   0.76f,   0.01f,   0.83f, 
          0.18f,   0.38f,   0.18f,   0.56f,   0.35f,   0.50f, 
          0.18f,   0.38f,   0.35f,   0.50f,   0.35f,   0.43f, 
          0.18f,   0.70f,   0.18f,   0.59f,   0.01f,   0.71f, 
          0.18f,   0.70f,   0.01f,   0.71f,   0.01f,   0.76f, 
          0.06f,   0.51f,   0.02f,   0.51f,   0.00f,   0.71f, 
          0.06f,   0.51f,   0.00f,   0.71f,   0.01f,   0.71f, 
          0.18f,   0.26f,   0.18f,   0.38f,   0.35f,   0.43f, 
          0.18f,   0.26f,   0.35f,   0.43f,   0.35f,   0.38f, 
          0.94f,   1.00f,   0.97f,   1.00f,   1.00f,   0.82f, 
          0.94f,   1.00f,   1.00f,   0.82f,   0.99f,   0.82f, 
          0.82f,   0.58f,   0.82f,   0.39f,   0.64f,   0.44f, 
          0.82f,   0.58f,   0.64f,   0.44f,   0.64f,   0.52f, 
          0.82f,   0.70f,   0.82f,   0.88f,   0.99f,   0.82f, 
          0.82f,   0.70f,   0.99f,   0.82f,   0.99f,   0.75f, 
          0.82f,   0.39f,   0.82f,   0.26f,   0.64f,   0.39f, 
          0.82f,   0.39f,   0.64f,   0.39f,   0.64f,   0.44f, 
          0.97f,   0.50f,   0.94f,   0.50f,   0.99f,   0.70f, 
          0.97f,   0.50f,   0.99f,   0.70f,   1.00f,   0.70f, 
          0.82f,   0.59f,   0.82f,   0.70f,   0.99f,   0.75f, 
          0.82f,   0.59f,   0.99f,   0.75f,   0.99f,   0.70f, 
          0.39f,   0.67f,   0.39f,   0.85f,   0.20f,   0.85f, 
          0.39f,   0.67f,   0.20f,   0.85f,   0.20f,   0.79f, 
          0.77f,   0.85f,   0.58f,   0.85f,   0.58f,   0.67f, 
          0.77f,   0.85f,   0.58f,   0.67f,   0.77f,   0.79f, 
          0.50f,   0.01f,   0.61f,   0.01f,   0.66f,   0.34f, 
          0.61f,   0.01f,   0.72f,   0.01f,   0.66f,   0.34f, 
          0.72f,   0.01f,   0.83f,   0.01f,   0.66f,   0.34f, 
          0.33f,   0.22f,   0.33f,   0.28f,   0.40f,   0.28f, 
          0.02f,   0.51f,   0.06f,   0.51f,   0.02f,   0.12f, 
          0.05f,   0.27f,   0.17f,   0.27f,   0.17f,   0.58f, 
          0.44f,   0.37f,   0.52f,   0.37f,   0.48f,   0.00f, 
          0.83f,   0.27f,   0.95f,   0.27f,   0.84f,   0.59f, 
          0.35f,   0.03f,   0.33f,   0.09f,   0.41f,   0.05f, 
          0.94f,   0.50f,   0.97f,   0.50f,   0.97f,   0.12f, 
          0.30f,   0.63f,   0.40f,   0.48f,   0.37f,   0.47f, 
          0.66f,   0.63f,   0.60f,   0.47f,   0.56f,   0.48f, 
          0.48f,   0.00f,   0.41f,   0.36f,   0.44f,   0.37f, 
          0.48f,   0.00f,   0.52f,   0.37f,   0.56f,   0.36f
    };

    public Indigo(Alite alite) {
        super(alite, "Indigo", ObjectType.EnemyShip);
        shipType = ShipType.Indigo;
        boundingBox = new float [] {-160.00f, 160.00f, -50.70f,  50.70f, -150.75f, 150.75f};
        numberOfVertices = 270;
        textureFilename = "textures/mosquito.png";
        maxSpeed          = 434.2f;
        maxPitchSpeed     = 2.500f;
        maxRollSpeed      = 2.750f;
        hullStrength      = 220.0f;
        hasEcm            = true;
        cargoType         = 8;
        aggressionLevel   = 12;
        escapeCapsuleCaps = 1;
        bounty            = 200;
        score             = 220;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[36]);
        laserHardpoints.add(VERTEX_DATA[37]);
        laserHardpoints.add(VERTEX_DATA[38]);
        laserColor        = 0x7F0000FFl;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                                    0,   4,   5,   0,   5,   1,   1,   5,   6,   1,   6,   2,   2,   6,   7, 
                                    2,   7,   3,   5,   9,  10,   5,  10,   6,   0,   1,  14,   0,  14,  13, 
                                    1,   2,  15,   1,  15,  14,   2,   3,  16,   2,  16,  15,  13,  14,  24, 
                                   13,  24,  23,  14,  15,  25,  14,  25,  24,  15,  16,  26,  15,  26,  25, 
                                    3,   7,  17,   3,  17,  16,  36,  38,  40,  36,  40,  39,  39,  40,  44, 
                                   39,  44,  43,  11,  10,  19,  11,  19,  18,  10,   9,  32,  10,  32,  31, 
                                    9,   8,  21,   9,  21,  20,  18,  19,  28,  18,  28,  27,  31,  32,  34, 
                                   31,  34,  33,  20,  21,  30,  20,  30,  29,  37,  35,  42,  37,  42,  41, 
                                    4,   0,  13,   4,  13,  22,  41,  42,  46,  41,  46,  45,  28,  19,  31, 
                                   28,  31,  33,  20,  29,  34,  20,  34,  32,   7,  11,  38,   7,  38,  36, 
                                   11,  18,  40,  11,  40,  38,  17,   7,  36,  17,  36,  39,  18,  27,  44, 
                                   18,  44,  40,  27,  26,  43,  27,  43,  44,  26,  17,  39,  26,  39,  43, 
                                    8,   4,  35,   8,  35,  37,   4,  22,  42,   4,  42,  35,  21,   8,  37, 
                                   21,  37,  41,  22,  23,  46,  22,  46,  42,  23,  30,  45,  23,  45,  46, 
                                   30,  21,  41,  30,  41,  45,   9,   5,   4,   9,   4,   8,   7,   6,  10, 
                                    7,  10,  11,  23,  24,  12,  24,  25,  12,  25,  26,  12,  16,  17,  26, 
                                   26,  27,  12,  27,  28,  12,  33,  34,  12,  29,  30,  12,  22,  13,  23, 
                                   30,  23,  12,  10,  31,  19,   9,  20,  32,  12,  28,  33,  12,  34,  29);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30,  50,  0, 0, 0.7f, 0.8f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 13, 13, 30, -50,  0, 0, 0.7f, 0.8f, 0.8f, 0.7f));
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
