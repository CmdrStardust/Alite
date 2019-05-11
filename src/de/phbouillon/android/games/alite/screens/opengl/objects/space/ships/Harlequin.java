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
 * Coluber Pitviper model and texture by Robert Triflinger from Oolite.
 * Renamed to Harlequin for Alite. 
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Harlequin extends SpaceObject {
    public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -0.00f, -12.52f,   7.27f,  -0.00f, -12.52f, -225.45f, 
        -38.87f, -34.97f, -117.93f, -38.87f, -12.52f, -149.67f, 
        -38.87f, -34.97f, -181.41f, -19.44f, -46.19f, -149.67f, 
         38.87f, -34.97f, -117.93f,  19.44f, -46.19f, -149.67f, 
         38.87f, -34.97f, -181.41f,  38.87f, -12.52f, -149.67f, 
         -0.00f,  32.36f, -117.93f,  19.44f,  21.14f, -149.67f, 
         -0.00f,  32.36f, -181.41f, -19.44f,  21.14f, -149.67f, 
        -101.69f, -71.23f, 225.45f, -62.82f, -48.79f, -117.93f, 
        -82.25f, -37.57f, -149.67f, -62.82f, -48.79f, -181.41f, 
        -62.82f, -71.23f, -149.67f, 101.69f, -71.23f, 225.45f, 
         62.82f, -48.79f, -117.93f,  62.82f, -71.23f, -149.67f, 
         62.82f, -48.79f, -181.41f,  82.25f, -37.57f, -149.67f, 
         -0.00f, 104.90f, 225.45f,  -0.00f,  60.01f, -117.93f, 
         19.44f,  71.23f, -149.67f,  -0.00f,  60.01f, -181.41f, 
        -19.44f,  71.23f, -149.67f, -101.69f, -138.56f, -149.67f, 
        101.69f, -138.56f, -149.67f, -160.00f, -37.57f, -149.67f, 
        -58.31f, 138.56f, -149.67f, 160.00f, -37.57f, -149.67f, 
         58.31f, 138.56f, -149.67f, -72.54f, -54.40f, -210.91f, 
        -130.85f, -54.40f, -210.91f, -101.69f, -104.90f, -210.91f, 
         72.54f, -54.40f, -210.91f, 101.69f, -104.90f, -210.91f, 
        130.85f, -54.40f, -210.91f, -29.15f, 121.73f, -210.91f, 
         -0.00f,  71.23f, -210.91f,  29.15f, 121.73f, -210.91f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.18073f,   0.95683f,  -0.22762f,  -0.91901f,  -0.32190f,  -0.22762f, 
          0.73828f,  -0.63493f,  -0.22762f,   0.74757f,  -0.54231f,   0.38347f, 
          0.09587f,   0.91857f,   0.38347f,  -0.84343f,  -0.37626f,   0.38347f, 
          0.91901f,  -0.32190f,  -0.22762f,   0.42640f,  -0.73855f,  -0.52223f, 
          0.79145f,  -0.45694f,   0.40598f,   0.42640f,  -0.73855f,   0.52223f, 
         -0.09587f,   0.91857f,   0.38347f,  -0.42640f,   0.73855f,   0.52223f, 
          0.00000f,   0.97776f,  -0.20973f,  -0.42640f,   0.73855f,  -0.52223f, 
         -0.42640f,   0.73855f,   0.52223f,  -0.18073f,   0.95683f,  -0.22762f, 
          0.42640f,   0.73855f,  -0.52223f,   0.00000f,   0.91388f,   0.40598f, 
          0.42640f,   0.73855f,   0.52223f,  -0.74757f,  -0.54231f,   0.38347f, 
         -0.42640f,  -0.73855f,   0.52223f,  -0.84677f,  -0.48888f,  -0.20973f, 
         -0.42640f,  -0.73855f,  -0.52223f,  -0.42640f,  -0.73855f,   0.52223f, 
         -0.73828f,  -0.63493f,  -0.22762f,  -0.85280f,   0.00000f,  -0.52223f, 
         -0.79145f,  -0.45694f,   0.40598f,  -0.85280f,   0.00000f,   0.52223f, 
          0.84343f,  -0.37626f,   0.38347f,   0.85280f,  -0.00000f,   0.52223f, 
          0.84677f,  -0.48888f,  -0.20973f,   0.85280f,   0.00000f,  -0.52223f, 
          0.85280f,  -0.00000f,   0.52223f,  -0.09015f,  -0.99309f,  -0.07510f, 
         -0.90512f,   0.41847f,  -0.07510f,  -0.00001f,  -0.94281f,  -0.33333f, 
          0.42640f,  -0.73855f,  -0.52223f,  -0.00001f,  -0.94281f,   0.33333f, 
          0.42640f,  -0.73855f,   0.52223f,  -0.81650f,   0.47140f,   0.33333f, 
         -0.04712f,  -0.97833f,   0.20162f,  -0.42640f,   0.73855f,  -0.52223f, 
         -0.81650f,   0.47140f,  -0.33333f,   0.90512f,   0.41847f,  -0.07510f, 
          0.81650f,   0.47140f,  -0.33333f,   0.09015f,  -0.99309f,  -0.07510f, 
          0.42640f,   0.73855f,  -0.52223f,   0.81650f,   0.47140f,   0.33333f, 
          0.42640f,   0.73855f,   0.52223f,   0.87082f,   0.44835f,   0.20162f, 
          0.00001f,  -0.94281f,   0.33333f,  -0.42640f,  -0.73855f,  -0.52223f, 
          0.00001f,  -0.94281f,  -0.33333f,  -0.81497f,   0.57462f,  -0.07510f, 
          0.81497f,   0.57462f,  -0.07510f,  -0.85280f,  -0.00000f,  -0.52223f, 
         -0.81649f,   0.47141f,  -0.33333f,  -0.85280f,   0.00000f,   0.52223f, 
         -0.82370f,   0.52998f,   0.20162f,   0.85280f,   0.00000f,  -0.52223f, 
          0.81649f,   0.47141f,   0.33333f,  -0.83505f,   0.48211f,   0.26505f, 
          0.83505f,   0.48212f,   0.26505f,  -0.86256f,   0.49800f,  -0.08939f, 
         -0.83505f,   0.48212f,   0.26505f,   0.86256f,   0.49800f,  -0.08939f, 
          0.83505f,   0.48212f,   0.26505f,   0.81649f,   0.47141f,  -0.33333f, 
          0.00000f,  -0.96423f,   0.26505f,   0.83505f,   0.48212f,   0.26505f, 
          0.00000f,  -0.96423f,   0.26505f,  -0.83505f,   0.48212f,   0.26505f, 
         -0.00000f,  -0.99600f,  -0.08939f,  -0.81649f,   0.47141f,   0.33333f, 
          0.00000f,  -0.96423f,   0.26505f,  -0.87082f,   0.44835f,   0.20162f, 
          0.00000f,   0.00000f,   1.00000f,  -0.00000f,  -0.96423f,   0.26505f, 
          0.04713f,  -0.97833f,   0.20162f,  -0.00000f,   0.00000f,   1.00000f, 
          0.83505f,   0.48211f,   0.26505f,  -0.00000f,   0.00000f,   1.00000f, 
          0.82370f,   0.52998f,   0.20162f,  -0.83505f,   0.48212f,   0.26505f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.50f,   0.87f,   0.47f,   0.58f,   0.43f,   0.64f, 
          0.45f,   0.72f,   0.16f,   0.76f,   0.22f,   0.79f, 
          0.55f,   0.72f,   0.84f,   0.68f,   0.78f,   0.65f, 
          0.98f,   0.72f,   0.84f,   0.76f,   0.90f,   0.78f, 
          0.50f,   0.44f,   0.54f,   0.58f,   0.56f,   0.52f, 
          0.02f,   0.72f,   0.16f,   0.68f,   0.10f,   0.66f, 
          0.78f,   0.79f,   0.84f,   0.76f,   0.55f,   0.72f, 
          0.78f,   0.79f,   0.79f,   0.83f,   0.84f,   0.76f, 
          0.84f,   0.76f,   0.98f,   0.72f,   0.84f,   0.68f, 
          0.84f,   0.76f,   0.90f,   0.82f,   0.90f,   0.78f, 
          0.44f,   0.52f,   0.47f,   0.58f,   0.50f,   0.44f, 
          0.44f,   0.52f,   0.40f,   0.52f,   0.47f,   0.58f, 
          0.47f,   0.58f,   0.50f,   0.87f,   0.54f,   0.58f, 
          0.47f,   0.58f,   0.39f,   0.63f,   0.43f,   0.64f, 
          0.47f,   0.58f,   0.40f,   0.52f,   0.37f,   0.57f, 
          0.57f,   0.64f,   0.54f,   0.58f,   0.50f,   0.87f, 
          0.57f,   0.64f,   0.61f,   0.63f,   0.54f,   0.58f, 
          0.54f,   0.58f,   0.50f,   0.44f,   0.47f,   0.58f, 
          0.54f,   0.58f,   0.60f,   0.52f,   0.56f,   0.52f, 
          0.10f,   0.78f,   0.16f,   0.76f,   0.02f,   0.72f, 
          0.10f,   0.78f,   0.10f,   0.82f,   0.16f,   0.76f, 
          0.16f,   0.76f,   0.45f,   0.72f,   0.16f,   0.68f, 
          0.16f,   0.76f,   0.22f,   0.83f,   0.22f,   0.79f, 
          0.16f,   0.76f,   0.10f,   0.82f,   0.16f,   0.85f, 
          0.22f,   0.65f,   0.16f,   0.68f,   0.45f,   0.72f, 
          0.22f,   0.65f,   0.22f,   0.61f,   0.16f,   0.68f, 
          0.16f,   0.68f,   0.02f,   0.72f,   0.16f,   0.76f, 
          0.16f,   0.68f,   0.10f,   0.62f,   0.10f,   0.66f, 
          0.90f,   0.66f,   0.84f,   0.68f,   0.98f,   0.72f, 
          0.90f,   0.66f,   0.90f,   0.62f,   0.84f,   0.68f, 
          0.84f,   0.68f,   0.55f,   0.72f,   0.84f,   0.76f, 
          0.84f,   0.68f,   0.79f,   0.61f,   0.78f,   0.65f, 
          0.84f,   0.68f,   0.90f,   0.62f,   0.85f,   0.59f, 
          0.03f,   0.11f,   0.98f,   0.27f,   0.90f,   0.02f, 
          0.90f,   0.02f,   0.98f,   0.27f,   0.03f,   0.11f, 
          0.79f,   0.83f,   0.85f,   0.97f,   0.85f,   0.85f, 
          0.85f,   0.85f,   0.84f,   0.76f,   0.79f,   0.83f, 
          0.85f,   0.85f,   0.85f,   0.97f,   0.90f,   0.82f, 
          0.90f,   0.82f,   0.84f,   0.76f,   0.85f,   0.85f, 
          0.40f,   0.52f,   0.25f,   0.57f,   0.37f,   0.57f, 
          0.67f,   0.99f,   0.59f,   0.74f,   0.75f,   0.96f, 
          0.37f,   0.57f,   0.39f,   0.63f,   0.47f,   0.58f, 
          0.37f,   0.57f,   0.25f,   0.57f,   0.39f,   0.63f, 
          0.03f,   0.11f,   0.98f,   0.27f,   0.90f,   0.02f, 
          0.61f,   0.63f,   0.75f,   0.57f,   0.63f,   0.57f, 
          0.90f,   0.02f,   0.98f,   0.27f,   0.03f,   0.11f, 
          0.63f,   0.57f,   0.54f,   0.58f,   0.61f,   0.63f, 
          0.63f,   0.57f,   0.75f,   0.57f,   0.60f,   0.52f, 
          0.60f,   0.52f,   0.54f,   0.58f,   0.63f,   0.57f, 
          0.67f,   0.99f,   0.59f,   0.74f,   0.75f,   0.96f, 
          0.10f,   0.82f,   0.15f,   0.97f,   0.16f,   0.85f, 
          0.16f,   0.85f,   0.22f,   0.83f,   0.16f,   0.76f, 
          0.16f,   0.85f,   0.15f,   0.97f,   0.22f,   0.83f, 
          0.03f,   0.11f,   0.98f,   0.27f,   0.90f,   0.02f, 
          0.90f,   0.02f,   0.98f,   0.27f,   0.03f,   0.11f, 
          0.16f,   0.59f,   0.16f,   0.68f,   0.22f,   0.61f, 
          0.16f,   0.59f,   0.22f,   0.61f,   0.15f,   0.47f, 
          0.10f,   0.62f,   0.16f,   0.68f,   0.16f,   0.59f, 
          0.67f,   0.99f,   0.59f,   0.74f,   0.75f,   0.96f, 
          0.85f,   0.59f,   0.79f,   0.61f,   0.84f,   0.68f, 
          0.85f,   0.59f,   0.90f,   0.62f,   0.85f,   0.47f, 
          0.59f,   0.74f,   0.75f,   0.96f,   0.75f,   0.82f, 
          0.50f,   0.97f,   0.43f,   0.81f,   0.21f,   0.97f, 
          0.03f,   0.43f,   0.98f,   0.29f,   0.03f,   0.14f, 
          0.21f,   0.97f,   0.43f,   0.81f,   0.28f,   0.81f, 
          0.03f,   0.43f,   0.98f,   0.29f,   0.03f,   0.14f, 
          0.21f,   0.97f,   0.43f,   0.81f,   0.28f,   0.81f, 
          0.85f,   0.47f,   0.79f,   0.61f,   0.85f,   0.59f, 
          0.50f,   0.97f,   0.43f,   0.81f,   0.21f,   0.97f, 
          0.59f,   0.74f,   0.75f,   0.96f,   0.75f,   0.82f, 
          0.59f,   0.74f,   0.75f,   0.96f,   0.75f,   0.82f, 
          0.50f,   0.97f,   0.43f,   0.81f,   0.21f,   0.97f, 
          0.03f,   0.43f,   0.98f,   0.29f,   0.03f,   0.14f, 
          0.15f,   0.47f,   0.10f,   0.62f,   0.16f,   0.59f, 
          0.21f,   0.97f,   0.43f,   0.81f,   0.28f,   0.81f, 
          0.75f,   0.96f,   0.59f,   0.74f,   0.67f,   0.99f, 
          0.89f,   0.35f,   0.65f,   0.35f,   0.77f,   0.57f, 
          0.75f,   0.82f,   0.75f,   0.96f,   0.59f,   0.74f, 
          0.75f,   0.96f,   0.59f,   0.74f,   0.67f,   0.99f, 
          0.65f,   0.35f,   0.77f,   0.57f,   0.89f,   0.35f, 
          0.75f,   0.82f,   0.75f,   0.96f,   0.59f,   0.74f, 
          0.65f,   0.35f,   0.77f,   0.57f,   0.89f,   0.35f, 
          0.75f,   0.96f,   0.59f,   0.74f,   0.67f,   0.99f, 
          0.75f,   0.82f,   0.75f,   0.96f,   0.59f,   0.74f
    };

    public Harlequin(Alite alite) {
        super(alite, "Harlequin", ObjectType.EnemyShip);
        shipType = ShipType.Harlequin;
        boundingBox = new float [] {-160.00f, 160.00f, -138.56f, 138.56f, -225.45f, 225.45f};
        numberOfVertices = 252;
        textureFilename = "textures/pitviper.png";
        maxSpeed          = 467.6f;
        maxPitchSpeed     = 3.000f;
        maxRollSpeed      = 3.250f;
        hullStrength      = 320.0f;
        hasEcm            = true;
        cargoType         = 16;
        aggressionLevel   = 20;
        escapeCapsuleCaps = 1;
        bounty            = 500;
        score             = 320;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[57]);
        laserHardpoints.add(VERTEX_DATA[58]);
        laserHardpoints.add(VERTEX_DATA[59]);        
        laserHardpoints.add(VERTEX_DATA[42]);
        laserHardpoints.add(VERTEX_DATA[43]);
        laserHardpoints.add(VERTEX_DATA[44]);
        laserHardpoints.add(VERTEX_DATA[72]);
        laserHardpoints.add(VERTEX_DATA[73]);
        laserHardpoints.add(VERTEX_DATA[74]);        
        laserColor        = 0x7F00FF00l;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                                    0,   5,   2,   0,   9,   6,   0,  13,  10,   1,   3,   4,   1,   7,   8, 
                                    1,  11,  12,   2,   3,   0,   2,  15,   3,   3,   1,  13,   3,  17,   4, 
                                    4,   5,   1,   4,  17,   5,   5,   0,   7,   5,  15,   2,   5,  17,  18, 
                                    6,   7,   0,   6,  20,   7,   7,   1,   5,   7,  22,   8,   8,   9,   1, 
                                    8,  22,   9,   9,   0,  11,   9,  20,   6,   9,  22,  23,  10,  11,   0, 
                                   10,  25,  11,  11,   1,   9,  11,  27,  12,  12,  13,   1,  12,  27,  13, 
                                   13,   0,   3,  13,  25,  10,  13,  27,  28,  14,  31,  15,  15,  29,  14, 
                                   15,  31,  16,  16,   3,  15,  16,  31,  17,  17,   3,  16,  17,  29,  18, 
                                   17,  31,  35,  18,  15,   5,  18,  29,  15,  19,  30,  20,  20,  30,  21, 
                                   20,  33,  19,  21,   7,  20,  21,  30,  22,  22,   7,  21,  22,  30,  38, 
                                   22,  33,  23,  23,  20,   9,  23,  33,  20,  24,  34,  25,  25,  32,  24, 
                                   26,  11,  25,  26,  25,  34,  27,  11,  26,  27,  34,  42,  28,  25,  13, 
                                   28,  27,  32,  29,  35,  37,  29,  37,  31,  30,  19,  33,  30,  40,  39, 
                                   31,  14,  29,  31,  37,  36,  32,  25,  28,  32,  41,  34,  32,  42,  41, 
                                   33,  38,  40,  33,  40,  30,  34,  24,  32,  34,  27,  26,  34,  41,  43, 
                                   35,  29,  17,  35,  36,  37,  36,  35,  31,  38,  33,  22,  38,  39,  40, 
                                   39,  38,  30,  41,  42,  43,  42,  32,  27,  43,  42,  34);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 10, 10, 40, -100, -75, -15, 0.33f, 0.22f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 10, 10, 40,  100, -75, -15, 0.33f, 0.22f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 10, 10, 40,    0, 105, -15, 0.33f, 0.22f, 0.8f, 0.7f));
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
