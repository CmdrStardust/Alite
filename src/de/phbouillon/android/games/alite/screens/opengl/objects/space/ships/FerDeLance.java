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
 * Fer-de-Lance model from Oolite: http://oolite.org
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

public class FerDeLance extends SpaceObject {
	private static final long serialVersionUID = 8291946362401818396L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);

    private static final float [] VERTEX_DATA = new float [] {
          84.70f,   22.58f,  -35.76f,   -0.00f,  -37.64f,  160.00f,
         -84.70f,   22.58f,  -35.76f,   -0.00f,   37.64f,  -84.70f,
          84.70f,  -37.64f,  -35.76f,   22.58f,  -37.64f, -160.00f,
          22.58f,    0.00f, -160.00f,  -22.58f,  -37.64f, -160.00f,
         -22.58f,    0.00f, -160.00f,  -84.70f,  -37.64f,  -35.76f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.76238f,  -0.64636f,   0.03158f,   0.00000f,  -0.55050f,  -0.83484f, 
          0.76238f,  -0.64636f,   0.03158f,   0.00000f,  -0.99341f,   0.11464f, 
         -0.79561f,   0.58790f,   0.14619f,  -0.34064f,   0.76169f,   0.55117f, 
         -0.46409f,  -0.38443f,   0.79802f,   0.32049f,   0.35831f,   0.87687f, 
          0.55446f,  -0.45929f,   0.69399f,   0.66485f,   0.73692f,   0.12216f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.35f,   0.97f,   0.77f,   0.89f,   0.72f,   0.79f, 
          0.77f,   0.36f,   0.35f,   0.28f,   0.72f,   0.46f, 
          0.19f,   0.51f,   0.35f,   0.42f,   0.19f,   0.03f, 
          0.19f,   0.51f,   0.19f,   0.03f,   0.02f,   0.42f, 
          0.19f,   0.51f,   0.02f,   0.42f,   0.14f,   0.67f, 
          0.72f,   0.79f,   0.77f,   0.89f,   0.99f,   0.73f, 
          0.72f,   0.79f,   0.99f,   0.73f,   0.96f,   0.67f, 
          0.23f,   0.67f,   0.35f,   0.42f,   0.19f,   0.51f, 
          0.23f,   0.67f,   0.19f,   0.51f,   0.14f,   0.67f, 
          0.23f,   0.67f,   0.14f,   0.67f,   0.14f,   0.75f, 
          0.23f,   0.67f,   0.14f,   0.75f,   0.23f,   0.75f, 
          0.99f,   0.52f,   0.77f,   0.36f,   0.72f,   0.46f, 
          0.99f,   0.52f,   0.72f,   0.46f,   0.96f,   0.58f, 
          0.72f,   0.46f,   0.34f,   0.63f,   0.72f,   0.79f, 
          0.72f,   0.46f,   0.72f,   0.79f,   0.96f,   0.67f, 
          0.72f,   0.46f,   0.96f,   0.67f,   0.96f,   0.58f
    };

    public FerDeLance(Alite alite) {
        super(alite, "Fer-De-Lance", ObjectType.Trader);
        shipType = ShipType.Fer_De_Lance;
        boundingBox = new float [] { -84.70f,   84.70f,  -37.64f,   37.64f, -160.00f,  160.00f};
        numberOfVertices = 48;
        textureFilename = "textures/ferdelance.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.000f;
        maxRollSpeed      = 3.600f;        
        hullStrength      = 80.0f;
        hasEcm            = false;
        cargoType         = 3; 
        aggressionLevel   = 5;
        escapeCapsuleCaps = 1;
        bounty            = 50;
        score             = 80;
        legalityType      = 0;
        maxCargoCanisters = 1;        
        laserHardpoints.add(VERTEX_DATA[3]);
        laserHardpoints.add(VERTEX_DATA[4]);
        laserHardpoints.add(VERTEX_DATA[5]);
        init();
    }
    
    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                1,   0,   4,   2,   1,   9,   3,   0,   1,   3,   1,   2,   3,   2,   8, 
                4,   0,   6,   4,   6,   5,   6,   0,   3,   6,   3,   8,   6,   8,   7, 
                6,   7,   5,   8,   2,   9,   8,   9,   7,   9,   1,   4,   9,   4,   5, 
                9,   5,   7);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);  
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 14, 14, 22, 0, -20, 0, 0.94f, 0.73f, 0.23f, 0.7f));
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
