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
 * Adder model from Oolite: http://oolite.org
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

public class Adder extends SpaceObject {
	private static final long serialVersionUID = -3006426224645584383L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -42.40f,   18.84f,   42.40f,   42.40f,   18.84f,   42.40f,
          42.40f,    0.00f,  106.00f,  -42.40f,    0.00f,  106.00f,
          42.40f,  -18.84f,   42.40f,  -42.40f,  -18.84f,   42.40f,
         -70.66f,    0.00f,  -68.32f,  -42.40f,   18.84f, -106.00f,
         -70.66f,    0.00f, -106.00f,  -42.40f,  -18.84f, -106.00f,
          42.40f,  -18.84f, -106.00f,   70.66f,    0.00f, -106.00f,
          42.40f,   18.84f, -106.00f,   70.66f,    0.00f,  -68.32f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.36060f,  -0.92950f,  -0.07747f,  -0.36118f,  -0.92346f,  -0.12947f, 
         -0.76009f,   0.41957f,  -0.49621f,   0.76009f,  -0.41957f,  -0.49621f, 
         -0.31505f,   0.94440f,  -0.09407f,   0.36118f,   0.92346f,  -0.12947f, 
          0.99514f,   0.00000f,  -0.09848f,   0.20037f,  -0.66177f,   0.72244f, 
          0.74278f,   0.00000f,   0.66953f,   0.18162f,   0.92726f,   0.32742f, 
         -0.22492f,   0.54013f,   0.81097f,  -0.78784f,  -0.39392f,   0.47343f, 
         -0.15798f,  -0.80659f,   0.56961f,  -0.96823f,   0.23687f,  -0.08019f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.37f,   0.01f,   0.34f,   0.14f,   0.41f,   0.38f, 
          0.16f,   0.00f,   0.16f,   0.14f,   0.34f,   0.14f, 
          0.16f,   0.00f,   0.34f,   0.14f,   0.34f,   0.00f, 
          0.34f,   0.86f,   0.37f,   1.00f,   0.41f,   0.62f, 
          0.16f,   0.86f,   0.16f,   1.00f,   0.34f,   1.00f, 
          0.16f,   0.86f,   0.34f,   1.00f,   0.34f,   0.86f, 
          0.09f,   0.38f,   0.16f,   0.14f,   0.13f,   0.01f, 
          0.09f,   0.62f,   0.13f,   1.00f,   0.16f,   0.86f, 
          0.09f,   0.54f,   0.09f,   0.62f,   0.16f,   0.86f, 
          0.09f,   0.54f,   0.16f,   0.86f,   0.16f,   0.54f, 
          0.09f,   0.46f,   0.16f,   0.46f,   0.16f,   0.14f, 
          0.09f,   0.46f,   0.16f,   0.14f,   0.09f,   0.38f, 
          0.16f,   0.54f,   0.16f,   0.86f,   0.34f,   0.86f, 
          0.16f,   0.54f,   0.34f,   0.86f,   0.34f,   0.54f, 
          0.34f,   0.54f,   0.34f,   0.86f,   0.41f,   0.62f, 
          0.34f,   0.54f,   0.41f,   0.62f,   0.41f,   0.54f, 
          0.34f,   0.54f,   0.40f,   0.50f,   0.34f,   0.46f, 
          0.34f,   0.54f,   0.34f,   0.46f,   0.16f,   0.46f, 
          0.34f,   0.54f,   0.16f,   0.46f,   0.10f,   0.50f, 
          0.34f,   0.54f,   0.10f,   0.50f,   0.16f,   0.54f, 
          0.41f,   0.46f,   0.41f,   0.38f,   0.34f,   0.14f, 
          0.41f,   0.46f,   0.34f,   0.14f,   0.34f,   0.46f, 
          0.34f,   0.46f,   0.34f,   0.14f,   0.16f,   0.14f, 
          0.34f,   0.46f,   0.16f,   0.14f,   0.16f,   0.46f
    };

    public Adder(Alite alite) {
        super(alite, "Adder", ObjectType.EnemyShip);
        shipType = ShipType.Adder;
        boundingBox = new float [] { -70.66f,   70.66f,  -18.84f,   18.84f, -106.00f,  106.00f};
        numberOfVertices = 72;
        textureFilename = "textures/adder.png";
        maxSpeed          = 300.6f;
        maxPitchSpeed     = 2.0f;
        maxRollSpeed      = 2.8f;
        hullStrength      = 112.0f;
        hasEcm            = false;
        cargoType         = 6; 
        aggressionLevel   = 10;
        escapeCapsuleCaps = 1;
        bounty            = 80;
        score             = 110;
        legalityType      = 1;
        maxCargoCanisters = 1;
        laserHardpoints.add(VERTEX_DATA[6]);
        laserHardpoints.add(VERTEX_DATA[7]);
        laserHardpoints.add(VERTEX_DATA[8]);
        laserHardpoints.add(VERTEX_DATA[9]);
        laserHardpoints.add(VERTEX_DATA[10]);
        laserHardpoints.add(VERTEX_DATA[11]);    
        laserColor = 0x7FFFFF00l;
        laserTexture = "textures/laser_yellow.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                                    2,   1,  13,   3,   0,   1,   3,   1,   2,   4,   2,  13,   5,   3,   2, 
                                    5,   2,   4,   6,   0,   3,   6,   3,   5,   8,   6,   5,   8,   5,   9, 
                                    8,   7,   0,   8,   0,   6,   9,   5,   4,   9,   4,  10,  10,   4,  13, 
                                   10,  13,  11,  10,  11,  12,  10,  12,   7,  10,   7,   8,  10,   8,   9, 
                                   11,  13,   1,  11,   1,  12,  12,   1,   0,  12,   0,   7);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 18, -25, 0, 0, 0.7f, 0.0f, 0.56f, 0.7f));
        	addExhaust(new EngineExhaust(this, 13, 13, 18,  25, 0, 0, 0.7f, 0.0f, 0.56f, 0.7f));
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
