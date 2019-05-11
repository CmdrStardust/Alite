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
 * Huntsman model and texture by Eric Walsh (Galileo) from Oolite.
 * Renamed to Mussurana for Alite. 
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Mussurana extends SpaceObject {
    public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -46.67f,  46.67f, -111.12f, -140.00f,  17.15f, -55.24f, 
        -56.00f, -40.76f, 118.51f, -23.33f, -46.67f, 136.21f, 
        -132.03f, -37.91f, -136.21f, -40.44f, -23.33f,  42.88f, 
        -40.44f, -23.33f, -111.12f, -44.59f,  37.55f, -81.56f, 
        -42.52f,   0.00f, -19.34f, -93.33f,  31.91f, -83.18f, 
        -140.00f, -11.81f,  31.63f, -140.00f, -38.26f,  -5.07f, 
          0.00f, -23.33f,  21.88f, -48.22f, -35.00f,  89.55f, 
          0.00f,  46.67f, -111.12f,   0.00f,  37.55f, -81.56f, 
          0.00f,   0.00f, -19.34f,   0.00f, -23.33f, -111.12f, 
         46.67f,  46.67f, -111.12f, 140.00f,  17.15f, -55.24f, 
         56.00f, -40.76f, 118.51f,  23.33f, -46.67f, 136.21f, 
        132.03f, -37.91f, -136.21f,  40.44f, -23.33f,  42.88f, 
         40.44f, -23.33f, -111.12f,  44.59f,  37.55f, -81.56f, 
         42.52f,   0.00f, -19.34f,  93.33f,  31.91f, -83.18f, 
        140.00f, -11.81f,  31.63f, 140.00f, -38.26f,  -5.07f, 
         48.22f, -35.00f,  89.55f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.26075f,   0.02318f,  -0.96513f,  -0.00000f,   0.95561f,   0.29464f, 
         -0.11956f,   0.94636f,   0.30018f,  -0.52276f,   0.68014f,  -0.51393f, 
          0.00000f,   0.94868f,   0.31623f,   0.13163f,   0.95632f,   0.26102f, 
         -0.20371f,  -0.97776f,   0.04985f,  -0.02379f,  -0.99971f,  -0.00410f, 
          0.17081f,   0.95656f,   0.23627f,   0.06200f,   0.97556f,   0.21082f, 
          0.15717f,  -0.98757f,   0.00000f,  -0.52276f,   0.68014f,  -0.51393f, 
         -0.99480f,  -0.09659f,  -0.03220f,  -0.99469f,   0.08352f,  -0.06020f, 
          0.81005f,   0.49947f,  -0.30716f,   0.03587f,   0.93612f,   0.34985f, 
          0.17676f,   0.92351f,   0.34042f,   0.43414f,  -0.88832f,  -0.14972f, 
          0.00000f,  -1.00000f,   0.00000f,   0.00000f,   0.00000f,  -1.00000f, 
         -0.11541f,   0.84872f,   0.51609f,  -0.00000f,   0.85615f,   0.51673f, 
         -0.01025f,   0.89115f,   0.45359f,  -0.00000f,   0.87026f,   0.49260f, 
         -0.00000f,   0.85615f,   0.51673f,  -0.66247f,  -0.60769f,   0.43800f, 
          0.00000f,   0.87026f,   0.49260f,  -0.00000f,   0.00000f,  -1.00000f, 
          0.00000f,   0.00000f,  -1.00000f,  -0.00000f,   0.95561f,   0.29464f, 
          0.00000f,   0.95561f,   0.29464f,  -0.00000f,   0.95561f,   0.29464f, 
          0.00000f,   0.85615f,   0.51673f,   0.00000f,  -1.00000f,   0.00000f, 
          0.00000f,  -1.00000f,   0.00000f,  -0.00000f,   0.00000f,  -1.00000f, 
          0.52276f,   0.68014f,  -0.51393f,  -0.03587f,   0.93612f,   0.34985f, 
          0.01025f,   0.89115f,   0.45359f,   0.99480f,  -0.09659f,  -0.03220f, 
         -0.17081f,   0.95656f,   0.23627f,   0.00000f,   0.94868f,   0.31623f, 
          0.66247f,  -0.60769f,   0.43800f,  -0.06200f,   0.97556f,   0.21082f, 
          0.20371f,  -0.97776f,   0.04985f,  -0.81005f,   0.49947f,  -0.30716f, 
         -0.26075f,   0.02318f,  -0.96513f,   0.52276f,   0.68014f,  -0.51393f, 
          0.02379f,  -0.99971f,  -0.00410f,  -0.43414f,  -0.88832f,  -0.14972f, 
          0.00000f,  -1.00000f,  -0.00000f,  -0.15717f,  -0.98757f,  -0.00000f, 
         -0.13163f,   0.95632f,   0.26102f,   0.00000f,   0.85615f,   0.51673f, 
          0.11956f,   0.94636f,   0.30018f,  -0.17676f,   0.92351f,   0.34042f, 
          0.11541f,   0.84872f,   0.51609f,   0.99469f,   0.08352f,  -0.06020f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.79f,   0.03f,   0.78f,   0.18f,   0.99f,   0.21f, 
          0.59f,   0.23f,   0.52f,   0.24f,   0.59f,   0.33f, 
          0.59f,   0.23f,   0.52f,   0.13f,   0.52f,   0.24f, 
          0.45f,   0.03f,   0.52f,   0.13f,   0.60f,   0.03f, 
          0.45f,   0.03f,   0.25f,   0.03f,   0.05f,   0.21f, 
          0.45f,   0.03f,   0.12f,   0.23f,   0.22f,   0.25f, 
          0.12f,   0.76f,   0.68f,   0.58f,   0.04f,   0.72f, 
          0.12f,   0.76f,   0.45f,   0.76f,   0.68f,   0.58f, 
          0.05f,   0.21f,   0.12f,   0.23f,   0.45f,   0.03f, 
          0.01f,   0.29f,   0.12f,   0.23f,   0.05f,   0.21f, 
          0.68f,   0.72f,   0.88f,   0.66f,   0.88f,   0.32f, 
          0.60f,   0.03f,   0.52f,   0.13f,   0.59f,   0.23f, 
          0.06f,   0.54f,   0.42f,   0.45f,   0.23f,   0.40f, 
          0.06f,   0.54f,   0.34f,   0.51f,   0.42f,   0.45f, 
          0.82f,   0.91f,   0.29f,   0.88f,   0.17f,   0.91f, 
          0.22f,   0.25f,   0.37f,   0.24f,   0.45f,   0.03f, 
          0.22f,   0.25f,   0.27f,   0.33f,   0.37f,   0.24f, 
          0.88f,   0.32f,   0.86f,   0.22f,   0.68f,   0.72f, 
          0.88f,   0.66f,   0.97f,   0.37f,   0.88f,   0.32f, 
          0.78f,   0.18f,   0.69f,   0.03f,   0.69f,   0.19f, 
          0.52f,   0.24f,   0.52f,   0.13f,   0.37f,   0.24f, 
          0.52f,   0.24f,   0.37f,   0.33f,   0.52f,   0.33f, 
          0.37f,   0.24f,   0.52f,   0.13f,   0.45f,   0.03f, 
          0.37f,   0.24f,   0.27f,   0.33f,   0.37f,   0.33f, 
          0.37f,   0.24f,   0.37f,   0.33f,   0.52f,   0.24f, 
          0.42f,   0.45f,   0.34f,   0.51f,   0.64f,   0.54f, 
          0.27f,   0.33f,   0.37f,   0.24f,   0.37f,   0.33f, 
          0.69f,   0.03f,   0.78f,   0.18f,   0.79f,   0.03f, 
          0.69f,   0.03f,   0.78f,   0.18f,   0.69f,   0.19f, 
          0.59f,   0.33f,   0.52f,   0.24f,   0.59f,   0.23f, 
          0.52f,   0.33f,   0.59f,   0.33f,   0.52f,   0.24f, 
          0.52f,   0.33f,   0.52f,   0.24f,   0.59f,   0.33f, 
          0.37f,   0.33f,   0.52f,   0.24f,   0.52f,   0.33f, 
          0.97f,   0.66f,   0.97f,   0.37f,   0.88f,   0.66f, 
          0.97f,   0.66f,   0.88f,   0.66f,   0.97f,   0.37f, 
          0.79f,   0.03f,   0.78f,   0.18f,   0.69f,   0.03f, 
          0.59f,   0.23f,   0.52f,   0.13f,   0.60f,   0.03f, 
          0.45f,   0.03f,   0.37f,   0.24f,   0.22f,   0.25f, 
          0.45f,   0.03f,   0.52f,   0.13f,   0.37f,   0.24f, 
          0.23f,   0.40f,   0.42f,   0.45f,   0.06f,   0.54f, 
          0.45f,   0.03f,   0.12f,   0.23f,   0.05f,   0.21f, 
          0.05f,   0.21f,   0.25f,   0.03f,   0.45f,   0.03f, 
          0.64f,   0.54f,   0.34f,   0.51f,   0.42f,   0.45f, 
          0.05f,   0.21f,   0.12f,   0.23f,   0.01f,   0.29f, 
          0.04f,   0.72f,   0.68f,   0.58f,   0.12f,   0.76f, 
          0.17f,   0.91f,   0.29f,   0.88f,   0.82f,   0.91f, 
          0.99f,   0.21f,   0.78f,   0.18f,   0.79f,   0.03f, 
          0.60f,   0.03f,   0.52f,   0.13f,   0.45f,   0.03f, 
          0.68f,   0.58f,   0.45f,   0.76f,   0.12f,   0.76f, 
          0.68f,   0.72f,   0.86f,   0.22f,   0.88f,   0.32f, 
          0.88f,   0.32f,   0.97f,   0.37f,   0.88f,   0.66f, 
          0.88f,   0.32f,   0.88f,   0.66f,   0.68f,   0.72f, 
          0.22f,   0.25f,   0.12f,   0.23f,   0.45f,   0.03f, 
          0.52f,   0.24f,   0.37f,   0.33f,   0.37f,   0.24f, 
          0.52f,   0.24f,   0.52f,   0.13f,   0.59f,   0.23f, 
          0.37f,   0.24f,   0.27f,   0.33f,   0.22f,   0.25f, 
          0.37f,   0.24f,   0.52f,   0.13f,   0.52f,   0.24f, 
          0.42f,   0.45f,   0.34f,   0.51f,   0.06f,   0.54f
    };

    public Mussurana(Alite alite) {
        super(alite, "Mussurana", ObjectType.EnemyShip);
        shipType = ShipType.Mussurana;
        boundingBox = new float [] {-140.00f, 140.00f, -46.67f,  46.67f, -136.21f, 136.21f};
        numberOfVertices = 174;
        textureFilename = "textures/huntsman.png";
        maxSpeed          = 400.8f;
        maxPitchSpeed     = 1.500f;
        maxRollSpeed      = 1.500f;
        hullStrength      = 200.0f;
        hasEcm            = true;
        cargoType         = 9;
        aggressionLevel   = 10;
        escapeCapsuleCaps = 1;
        bounty            = 100;
        score             = 200;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[63]);
        laserHardpoints.add(VERTEX_DATA[64]);
        laserHardpoints.add(VERTEX_DATA[65]);
        laserHardpoints.add(VERTEX_DATA[9]);
        laserHardpoints.add(VERTEX_DATA[10]);
        laserHardpoints.add(VERTEX_DATA[11]);
        laserColor        = 0x7FFFFF00l;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createReversedRotatedFaces(VERTEX_DATA, NORMAL_DATA,
                                    0,   6,   4,   0,   7,  14,   0,   9,   7,   1,   9,   4,   1,  10,   2, 
                                    1,  13,   5,   2,   4,   3,   2,  11,   4,   2,  13,   1,   3,  13,   2, 
                                    4,   6,   5,   4,   9,   0,   4,  10,   1,   4,  11,  10,   4,  13,   3, 
                                    5,   8,   1,   5,  12,   8,   5,  13,   4,   6,  12,   5,   6,  14,  17, 
                                    7,   9,   8,   7,  16,  15,   8,   9,   1,   8,  12,  16,   8,  16,   7, 
                                   10,  11,   2,  12,  26,  16,  14,   6,   0,  14,  24,  17,  14,  25,  18, 
                                   15,  14,   7,  15,  25,  14,  16,  25,  15,  17,  12,   6,  17,  24,  12, 
                                   18,  24,  14,  18,  27,  22,  19,  26,  23,  19,  27,  26,  19,  28,  22, 
                                   19,  30,  20,  20,  28,  19,  20,  29,  28,  20,  30,  21,  21,  22,  20, 
                                   21,  30,  22,  22,  24,  18,  22,  27,  19,  22,  29,  20,  22,  30,  23, 
                                   23,  12,  24,  23,  24,  22,  23,  30,  19,  25,  16,  26,  25,  27,  18, 
                                   26,  12,  23,  26,  27,  25,  28,  29,  22);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 19, 19, 24, 0, 10, -25, 1.0f, 0.75f, 0.30f, 0.7f));
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
