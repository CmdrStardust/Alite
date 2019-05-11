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
 * Asp Mk II model from Oolite: http://oolite.org
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

public class AspMkII extends SpaceObject {
	private static final long serialVersionUID = -5258245599323455346L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -51.26f,   -9.86f,  138.00f,   51.26f,   -9.86f,  138.00f,
          84.78f,  -31.54f,   27.60f,   -0.00f,  -39.42f,  -27.60f,
         -84.78f,  -31.54f,   27.60f, -128.14f,    0.00f,  -27.60f,
          -0.00f,  -19.72f, -138.00f,  -84.78f,    0.00f, -138.00f,
         128.14f,    0.00f,  -27.60f,   84.78f,    0.00f, -138.00f,
          84.78f,   39.42f,   27.60f,  -84.78f,   39.42f,   27.60f,
          -0.00f,   19.72f, -138.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.65461f,  -0.27755f,  -0.70318f,  -0.64206f,   0.47029f,  -0.60547f, 
         -0.29832f,   0.95408f,  -0.02722f,  -0.00000f,   0.99759f,   0.06937f, 
          0.25461f,   0.96559f,  -0.05296f,   0.98623f,   0.07633f,  -0.14668f, 
         -0.00000f,   0.81737f,   0.57611f,   0.61355f,   0.02564f,   0.78924f, 
         -0.98623f,   0.07633f,  -0.14668f,  -0.61355f,   0.02564f,   0.78924f, 
         -0.29947f,  -0.94517f,  -0.13031f,   0.36133f,  -0.93028f,  -0.06342f, 
          0.00000f,  -0.74734f,   0.66445f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.38f,   0.31f,   0.25f,   0.23f,   0.12f,   0.31f, 
          0.12f,   0.31f,   0.17f,   0.49f,   0.34f,   0.49f, 
          0.12f,   0.31f,   0.34f,   0.49f,   0.38f,   0.31f, 
          0.38f,   0.31f,   0.47f,   0.23f,   0.39f,   0.05f, 
          0.38f,   0.31f,   0.39f,   0.05f,   0.25f,   0.05f, 
          0.25f,   0.05f,   0.25f,   0.23f,   0.38f,   0.31f, 
          0.12f,   0.31f,   0.25f,   0.23f,   0.25f,   0.05f, 
          0.25f,   0.05f,   0.11f,   0.05f,   0.12f,   0.31f, 
          0.11f,   0.05f,   0.03f,   0.23f,   0.12f,   0.31f, 
          0.37f,   0.72f,   0.33f,   0.54f,   0.17f,   0.54f, 
          0.37f,   0.72f,   0.17f,   0.54f,   0.13f,   0.72f, 
          0.39f,   0.97f,   0.46f,   0.79f,   0.37f,   0.72f, 
          0.25f,   0.97f,   0.39f,   0.97f,   0.37f,   0.72f, 
          0.13f,   0.72f,   0.04f,   0.79f,   0.11f,   0.97f, 
          0.13f,   0.72f,   0.11f,   0.97f,   0.25f,   0.97f, 
          0.78f,   0.90f,   0.62f,   0.94f,   0.78f,   0.98f, 
          0.78f,   0.90f,   0.78f,   0.98f,   0.94f,   0.94f, 
          0.38f,   0.31f,   0.34f,   0.49f,   0.47f,   0.23f, 
          0.03f,   0.23f,   0.17f,   0.49f,   0.12f,   0.31f, 
          0.46f,   0.79f,   0.33f,   0.54f,   0.37f,   0.72f, 
          0.13f,   0.72f,   0.17f,   0.54f,   0.04f,   0.79f, 
          0.13f,   0.72f,   0.25f,   0.97f,   0.37f,   0.72f
    };

    public AspMkII(Alite alite) {
        super(alite, "Asp Mk II", ObjectType.EnemyShip);
        shipType = ShipType.AspMkII;
        boundingBox = new float [] {-128.14f,  128.14f,  -39.42f,   39.42f, -138.00f,  138.00f};        
        numberOfVertices = 66;
        textureFilename = "textures/aspmkii.png";        
        maxSpeed          = 501.0f;
        maxPitchSpeed     = 1.0f;
        maxRollSpeed      = 2.0f;
        hullStrength      = 200.0f;
        hasEcm            = false;
        cargoType         = 3;
        aggressionLevel   = 15;
        escapeCapsuleCaps = 1;
        bounty            = 120;
        score             = 200;
        legalityType      = 1;
        maxCargoCanisters = 1;
        laserHardpoints.add(VERTEX_DATA[3]);
        laserHardpoints.add(VERTEX_DATA[4]);
        laserHardpoints.add(VERTEX_DATA[5]);
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserColor = 0x7F00FF00l;
        laserTexture = "textures/laser_green.png";
        init();
    }
     
    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                2,   3,   4,   4,   0,   1,   4,   1,   2,   2,   8,   9,   2,   9,   6, 
                6,   3,   2,   4,   3,   6,   6,   7,   4,   7,   5,   4,  10,   1,   0, 
               10,   0,  11,   9,   8,  10,  12,   9,  10,  11,   5,   7,  11,   7,  12, 
               12,   7,   6,  12,   6,   9,   2,   1,   8,   5,   0,   4,   8,   1,  10, 
               11,   0,   5,  11,  12,  10);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 32, 16, 40, 0, 0, 0, 1.0f, 0.45f, 0.0f, 0.7f));
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
