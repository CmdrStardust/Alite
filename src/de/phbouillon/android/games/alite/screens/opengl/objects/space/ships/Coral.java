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
 * Bandy-Bandy Courier model and texture by Murgh (from Oolite)
 * Renamed to Coral for Alite.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Coral extends SpaceObject {
    public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -77.64f,  49.69f, -130.13f,  77.65f,  49.69f, -130.15f, 
         -0.00f,  49.69f,  40.65f, 150.00f,  -0.00f, -211.46f, 
         -0.00f,  -0.00f, 211.46f, -150.00f,  -0.00f, -211.46f, 
         -0.00f, -49.69f,  40.65f,  77.64f, -49.69f, -130.15f, 
        -77.64f, -49.69f, -130.15f, 108.30f,  -0.01f,  95.09f, 
        -108.29f,  -0.01f,  95.09f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.62751f,  -0.77391f,  -0.08538f,   0.00012f,   0.00006f,   1.00000f, 
         -0.34553f,  -0.92516f,  -0.15713f,  -0.00000f,  -1.00000f,  -0.00005f, 
         -0.28750f,  -0.91966f,  -0.26754f,   0.34554f,  -0.92515f,  -0.15713f, 
          0.74706f,  -0.00007f,   0.66476f,  -0.62756f,  -0.77387f,  -0.08538f, 
          0.28746f,   0.91967f,  -0.26754f,   0.28752f,  -0.91965f,  -0.26753f, 
         -0.74707f,  -0.00008f,   0.66474f,   0.62742f,   0.77399f,  -0.08536f, 
          0.00000f,   1.00000f,   0.00000f,   0.00000f,  -0.00013f,   1.00000f, 
         -0.34552f,   0.92518f,  -0.15706f,  -0.62740f,   0.77400f,  -0.08534f, 
          0.34553f,   0.92517f,  -0.15707f,  -0.28745f,   0.91967f,  -0.26754f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.14f,   0.61f,   0.08f,   0.22f,   0.01f,   0.74f, 
          0.59f,   0.26f,   0.86f,   0.26f,   0.59f,   0.09f, 
          0.40f,   0.61f,   0.45f,   0.22f,   0.27f,   0.33f, 
          0.27f,   0.33f,   0.14f,   0.61f,   0.40f,   0.61f, 
          0.27f,   0.33f,   0.45f,   0.22f,   0.27f,   0.03f, 
          0.27f,   0.33f,   0.08f,   0.22f,   0.14f,   0.61f, 
          0.47f,   0.17f,   0.59f,   0.26f,   0.59f,   0.09f, 
          0.52f,   0.74f,   0.45f,   0.22f,   0.40f,   0.61f, 
          0.73f,   0.98f,   0.73f,   0.68f,   0.55f,   0.79f, 
          0.27f,   0.03f,   0.08f,   0.22f,   0.27f,   0.33f, 
          0.98f,   0.17f,   0.86f,   0.09f,   0.86f,   0.26f, 
          0.48f,   0.27f,   0.55f,   0.79f,   0.60f,   0.40f, 
          0.73f,   0.68f,   0.87f,   0.40f,   0.60f,   0.40f, 
          0.59f,   0.09f,   0.86f,   0.26f,   0.86f,   0.09f, 
          0.87f,   0.40f,   0.73f,   0.68f,   0.92f,   0.79f, 
          0.87f,   0.40f,   0.92f,   0.79f,   0.99f,   0.28f, 
          0.60f,   0.40f,   0.55f,   0.79f,   0.73f,   0.68f, 
          0.92f,   0.79f,   0.73f,   0.68f,   0.73f,   0.98f
    };

    public Coral(Alite alite) {
        super(alite, "Coral", ObjectType.EnemyShip);
        shipType = ShipType.Coral;
        boundingBox = new float [] {-150.00f, 150.00f, -49.69f,  49.69f, -211.46f, 211.46f};
        numberOfVertices = 54;
        textureFilename = "textures/bandybandy.png";
        maxSpeed          = 400.8f;
        maxPitchSpeed     = 3.000f;
        maxRollSpeed      = 2.250f;
        hullStrength      = 220.0f;
        hasEcm            = true;
        cargoType         = 6;
        aggressionLevel   = 14;
        escapeCapsuleCaps = 1;
        bounty            = 200;
        score             = 220;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[12]);
        laserHardpoints.add(VERTEX_DATA[13]);
        laserHardpoints.add(VERTEX_DATA[14]);
        laserColor        = 0x7F00FF00l;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                                    0,  10,   5,   1,   0,   7,   1,   9,   2,   2,   0,   1,   2,   9,   4, 
                                    2,  10,   0,   3,   1,   7,   3,   9,   1,   4,   6,  10,   4,  10,   2, 
                                    5,   8,   0,   5,  10,   8,   6,   7,   8,   7,   0,   8,   7,   6,   9, 
                                    7,   9,   3,   8,  10,   6,   9,   6,   4);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 24, 30, -45, 5, -80, 0.60f, 0.08f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 13, 24, 30,  45, 5, -80, 0.60f, 0.08f, 0.8f, 0.7f));
        	addExhaust(new EngineExhaust(this, 13, 24, 30,   0, 5, -80, 0.60f, 0.08f, 0.8f, 0.7f));
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
