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
 * Cobra Mk I model from Oolite: http://oolite.org
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

public class CobraMkI extends SpaceObject {
	private static final long serialVersionUID = 3626878375580111820L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);

    private static final float [] VERTEX_DATA = new float [] {
          41.72f,    0.00f,  114.72f,  146.00f,   -4.18f,    2.08f,
         114.72f,  -31.28f, -114.72f,   -0.00f,   27.12f,    0.00f,
          66.74f,   31.28f, -114.72f, -114.72f,  -31.28f, -114.72f,
         -41.72f,    0.00f,  114.72f, -146.00f,   -4.18f,    2.08f,
         -66.74f,   31.28f, -114.72f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.40698f,  -0.67699f,  -0.61323f,  -0.88994f,  -0.45606f,  -0.00272f, 
         -0.33666f,   0.88305f,   0.32694f,   0.00000f,  -0.98935f,  -0.14555f, 
         -0.27607f,  -0.83599f,   0.47424f,   0.34745f,   0.53169f,   0.77239f, 
          0.49691f,   0.03987f,  -0.86689f,   0.88994f,  -0.45606f,  -0.00272f, 
          0.30290f,  -0.91724f,   0.25871f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.00f,   0.64f,   0.22f,   0.72f,   0.44f,   0.59f, 
          0.56f,   0.50f,   1.00f,   0.64f,   0.79f,   0.44f, 
          0.45f,   0.44f,   0.21f,   0.44f,   0.44f,   0.59f, 
          0.22f,   0.72f,   0.00f,   0.64f,   0.00f,   0.80f, 
          0.44f,   0.59f,   0.21f,   0.44f,   0.00f,   0.64f, 
          0.56f,   0.94f,   0.56f,   0.50f,   0.44f,   0.59f, 
          0.56f,   0.94f,   0.44f,   0.59f,   0.44f,   0.85f, 
          1.00f,   0.80f,   1.00f,   0.64f,   0.56f,   0.50f, 
          1.00f,   0.80f,   0.56f,   0.50f,   0.56f,   0.94f, 
          1.00f,   0.80f,   0.56f,   0.94f,   0.79f,   1.00f, 
          0.00f,   0.80f,   0.21f,   1.00f,   0.44f,   0.85f, 
          0.21f,   1.00f,   0.44f,   1.00f,   0.44f,   0.85f, 
          0.44f,   0.85f,   0.22f,   0.72f,   0.00f,   0.80f, 
          0.44f,   0.85f,   0.44f,   0.59f,   0.22f,   0.72f
    };

    public CobraMkI(Alite alite) {
        super(alite, "Cobra Mk I", ObjectType.Trader);
        shipType = ShipType.CobraMkI;
        boundingBox = new float [] {-146.00f,  146.00f,  -31.28f,   31.28f, -114.72f,  114.72f};
        numberOfVertices = 42;
        textureFilename = "textures/cobramki.png";
        maxSpeed          = 317.3f;
        maxPitchSpeed     = 1.200f;
        maxRollSpeed      = 2.000f;
        hullStrength      =  72.0f;
        hasEcm            = true;
        cargoType         = 0;   
        aggressionLevel   = 5;
        escapeCapsuleCaps = 3;
        bounty            = 0;
        score             = 70;
        legalityType      = 0;
        maxCargoCanisters = 0;        
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserHardpoints.add(VERTEX_DATA[18]);
        laserHardpoints.add(VERTEX_DATA[19]);
        laserHardpoints.add(VERTEX_DATA[20]);
        init();
        laserColor = 0x7F00FF00l;
        laserTexture = "textures/laser_green.png";
    }
    
    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   3,   4,   2,   0,   1,   2,   1,   4,   3,   0,   6,   4,   1,   0, 
                5,   2,   4,   5,   4,   8,   6,   0,   2,   6,   2,   5,   6,   5,   7, 
                6,   7,   8,   7,   5,   8,   8,   3,   6,   8,   4,   3);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 18, 13, 24, -30, 0, 0));
        	addExhaust(new EngineExhaust(this, 18, 13, 24,  30, 0, 0));
        }
        initTargetBox();
    }

    @Override
    public void hasBeenHitByPlayer() {
    	computeLegalStatusAfterFriendlyHit();	
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
