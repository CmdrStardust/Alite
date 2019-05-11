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
 * Sidewinder model from Oolite: http://oolite.org
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

public class Sidewinder extends SpaceObject {
	private static final long serialVersionUID = 6782602453723466978L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
          -0.00f,   29.54f,  -68.92f,   63.02f,    0.00f,   68.92f,
         -63.02f,    0.00f,   68.92f,  128.00f,    0.00f,  -68.92f,
        -128.00f,    0.00f,  -68.92f,   -0.00f,  -29.54f,  -68.92f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,  -0.98081f,   0.19498f,  -0.57892f,  -0.00000f,  -0.81539f, 
          0.57892f,   0.00000f,  -0.81539f,  -0.24250f,   0.00000f,   0.97015f, 
          0.24250f,   0.00000f,   0.97015f,   0.00000f,   0.98081f,   0.19498f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.57f,   0.73f,   0.19f,   0.56f,   0.19f,   0.92f, 
          0.57f,   0.39f,   0.19f,   0.56f,   0.57f,   0.73f, 
          0.57f,   0.39f,   0.57f,   0.73f,   0.94f,   0.56f, 
          0.10f,   0.92f,   0.18f,   0.56f,   0.10f,   0.21f, 
          0.10f,   0.92f,   0.10f,   0.21f,   0.02f,   0.56f, 
          0.19f,   0.21f,   0.19f,   0.56f,   0.57f,   0.39f, 
          0.94f,   0.21f,   0.57f,   0.39f,   0.94f,   0.56f, 
          0.94f,   0.56f,   0.57f,   0.73f,   0.94f,   0.92f
    };

    public Sidewinder(Alite alite) {
        super(alite, "Sidewinder", ObjectType.EnemyShip);
        shipType = ShipType.Sidewinder;
        boundingBox = new float [] {-128.00f,  128.00f,  -29.54f,   29.54f,  -68.92f,   68.92f};
        numberOfVertices = 24;
        textureFilename = "textures/sidewinder.png";
        maxSpeed          = 334.0f;
        maxPitchSpeed     = 1.600f;
        maxRollSpeed      = 2.800f;        
        hullStrength      = 40.0f;
        hasEcm            = false;
        cargoType         = 11;
        aggressionLevel   = 10;
        escapeCapsuleCaps = 1;
        bounty            = 40;
        score             = 40;
        legalityType      = 1;
        maxCargoCanisters = 1;
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
                1,   0,   3,   2,   0,   1,   2,   1,   5,   3,   0,   4,   3,   4,   5, 
                4,   0,   2,   4,   2,   5,   5,   1,   3);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename); 
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, 0, 0, 0, 0.93f, 0.33f, 0.8f, 0.7f));
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
