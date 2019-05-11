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
 * Transporter model from Oolite: http://oolite.org
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

public class Transporter extends SpaceObject {
	private static final long serialVersionUID = -8482449272214429602L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);

    private static final float [] VERTEX_DATA = new float [] {
         -86.18f,  -16.58f, -116.00f,  -72.92f,  -33.14f, -116.00f,
          72.92f,  -33.14f, -116.00f,   86.18f,  -16.58f, -116.00f,
          76.22f,    9.94f, -116.00f,   -0.00f,   33.14f, -116.00f,
         -76.22f,    9.94f, -116.00f,   99.42f,  -33.14f,   39.78f,
          89.48f,   -6.62f,   39.78f,  -36.46f,  -33.14f,  116.00f,
          36.46f,  -33.14f,  116.00f,  -99.42f,  -33.14f,   39.78f,
         -89.48f,   -6.62f,   39.78f,   -0.00f,   16.58f,   39.78f,
          26.52f,   -6.62f,  116.00f,  -26.52f,   -6.62f,  116.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.81751f,   0.12837f,   0.56143f,   0.22341f,   0.75599f,   0.61528f, 
         -0.22341f,   0.75599f,   0.61528f,  -0.76627f,  -0.02386f,   0.64208f, 
         -0.46107f,  -0.49074f,   0.73932f,   0.00000f,  -0.98628f,   0.16507f, 
          0.37917f,  -0.29043f,   0.87857f,  -0.92923f,   0.20763f,  -0.30566f, 
         -0.65207f,  -0.73780f,  -0.17455f,   0.26364f,   0.25710f,  -0.92973f, 
         -0.17962f,   0.90273f,  -0.39090f,   0.92507f,   0.30538f,  -0.22582f, 
          0.56229f,  -0.79786f,  -0.21738f,  -0.00000f,  -0.98264f,  -0.18551f, 
         -0.36123f,  -0.51455f,  -0.77765f,   0.42618f,  -0.60707f,  -0.67070f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.53f,   0.60f,   0.53f,   0.56f,   0.18f,   0.56f, 
          0.53f,   1.00f,   0.53f,   0.94f,   0.18f,   1.00f, 
          0.02f,   0.28f,   0.02f,   0.47f,   0.38f,   0.47f, 
          0.02f,   0.28f,   0.38f,   0.28f,   0.38f,   0.08f, 
          0.63f,   0.60f,   0.57f,   0.58f,   0.53f,   0.60f, 
          0.63f,   0.60f,   0.53f,   0.60f,   0.53f,   0.94f, 
          0.63f,   0.60f,   0.53f,   0.94f,   0.57f,   0.97f, 
          0.63f,   0.60f,   0.57f,   0.97f,   0.63f,   0.94f, 
          0.63f,   0.60f,   0.63f,   0.94f,   0.68f,   0.78f, 
          0.38f,   0.47f,   0.02f,   0.47f,   0.02f,   0.53f, 
          0.38f,   0.47f,   0.02f,   0.53f,   0.38f,   0.53f, 
          0.38f,   0.47f,   0.38f,   0.28f,   0.02f,   0.28f, 
          0.00f,   0.86f,   0.18f,   1.00f,   0.53f,   0.94f, 
          0.00f,   0.86f,   0.53f,   0.94f,   0.53f,   0.60f, 
          0.00f,   0.86f,   0.53f,   0.60f,   0.18f,   0.56f, 
          0.00f,   0.86f,   0.18f,   0.56f,   0.00f,   0.69f, 
          0.38f,   0.02f,   0.02f,   0.02f,   0.02f,   0.08f, 
          0.38f,   0.02f,   0.02f,   0.08f,   0.38f,   0.08f, 
          0.38f,   0.08f,   0.02f,   0.08f,   0.02f,   0.28f, 
          0.38f,   0.28f,   0.38f,   0.47f,   0.55f,   0.34f, 
          0.55f,   0.34f,   0.38f,   0.47f,   0.41f,   0.54f, 
          0.55f,   0.34f,   0.41f,   0.54f,   0.58f,   0.40f, 
          0.55f,   0.34f,   0.61f,   0.36f,   0.61f,   0.19f, 
          0.55f,   0.34f,   0.61f,   0.19f,   0.55f,   0.21f, 
          0.55f,   0.34f,   0.55f,   0.21f,   0.38f,   0.28f, 
          0.55f,   0.21f,   0.58f,   0.16f,   0.41f,   0.01f, 
          0.55f,   0.21f,   0.41f,   0.01f,   0.38f,   0.08f, 
          0.55f,   0.21f,   0.38f,   0.08f,   0.38f,   0.28f
    };

    public Transporter(Alite alite) {
        super(alite, "Transporter", ObjectType.Shuttle);
        shipType = ShipType.Transporter;
        boundingBox = new float [] { -99.42f,   99.42f,  -33.14f,   33.14f, -116.00f,  116.00f};
        numberOfVertices = 84;
        textureFilename = "textures/transporter.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.000f;
        maxRollSpeed      = 2.000f;        
        hullStrength      = 80.0f;
        hasEcm            = false;
        cargoType         = 0;
        aggressionLevel   = 5;
        escapeCapsuleCaps = 0;
        bounty            = 0;
        score             = 10;
        legalityType      = 2;
        maxCargoCanisters = 0;
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                1,   0,  11,   3,   2,   7,   5,   4,   8,   5,  13,  12,   6,   0,   1, 
                6,   1,   2,   6,   2,   3,   6,   3,   4,   6,   4,   5,   8,   4,   3, 
                8,   3,   7,   8,  13,   5,  10,   7,   2,  10,   2,   1,  10,   1,  11, 
               10,  11,   9,  11,   0,   6,  11,   6,  12,  12,   6,   5,  13,   8,  14, 
               14,   8,   7,  14,   7,  10,  14,  10,   9,  14,   9,  15,  14,  15,  13, 
               15,   9,  11,  15,  11,  12,  15,  12,  13);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);   
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, -37, -5, 0));
        	addExhaust(new EngineExhaust(this, 13, 13, 30,  37, -5, 0));
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
