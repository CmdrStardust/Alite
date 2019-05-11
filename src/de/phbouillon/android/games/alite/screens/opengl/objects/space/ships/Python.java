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
 * Python model from Oolite: http://oolite.org
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

public class Python extends SpaceObject {
	private static final long serialVersionUID = 1388500568387270650L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);

    private static final float [] VERTEX_DATA = new float [] {
          -0.00f,    0.00f,  336.00f,   -0.00f,  103.38f,  -10.34f,
         206.76f,    0.00f, -134.40f, -206.76f,    0.00f, -134.40f,
          -0.00f, -103.38f,  -10.34f,   -0.00f,  103.38f, -175.76f,
          -0.00f, -103.38f, -175.76f,   -0.00f,   51.70f, -336.00f,
         103.38f,    0.00f, -336.00f, -103.38f,    0.00f, -336.00f,
          -0.00f,  -51.70f, -336.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,   0.00000f,  -1.00000f,  -0.00000f,  -0.99017f,  -0.13984f, 
         -1.00000f,  -0.00000f,   0.00165f,   1.00000f,  -0.00000f,   0.00165f, 
          0.00000f,   0.99017f,  -0.13984f,   0.00000f,  -0.98065f,   0.19579f, 
         -0.00000f,   0.98065f,   0.19579f,   0.00000f,  -0.74167f,   0.67077f, 
         -0.51044f,   0.00000f,   0.85991f,   0.51044f,   0.00000f,   0.85991f, 
          0.00000f,   0.74167f,   0.67077f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.37f,   0.75f,   0.02f,   0.75f,   0.48f,   0.98f, 
          0.48f,   0.52f,   0.02f,   0.75f,   0.37f,   0.75f, 
          0.48f,   0.52f,   0.37f,   0.75f,   0.52f,   0.75f, 
          0.25f,   0.48f,   0.69f,   0.25f,   0.35f,   0.25f, 
          0.35f,   0.25f,   0.69f,   0.25f,   0.25f,   0.02f, 
          0.35f,   0.25f,   0.25f,   0.02f,   0.19f,   0.25f, 
          0.52f,   0.75f,   0.37f,   0.75f,   0.48f,   0.98f, 
          0.52f,   0.75f,   0.48f,   0.98f,   0.70f,   0.88f, 
          0.52f,   0.75f,   0.69f,   0.75f,   0.70f,   0.62f, 
          0.19f,   0.25f,   0.25f,   0.02f,   0.03f,   0.09f, 
          0.19f,   0.25f,   0.25f,   0.48f,   0.35f,   0.25f, 
          0.19f,   0.25f,   0.02f,   0.29f,   0.03f,   0.41f, 
          0.70f,   0.62f,   0.48f,   0.52f,   0.52f,   0.75f, 
          0.84f,   0.57f,   0.75f,   0.75f,   0.84f,   0.93f, 
          0.84f,   0.57f,   0.84f,   0.93f,   0.93f,   0.75f, 
          0.03f,   0.09f,   0.02f,   0.21f,   0.19f,   0.25f, 
          0.03f,   0.41f,   0.25f,   0.48f,   0.19f,   0.25f, 
          0.70f,   0.88f,   0.69f,   0.75f,   0.52f,   0.75f
    };

    public Python(Alite alite) {
        super(alite, "Python", ObjectType.Trader);
        final float scale = 0.8f;
        shipType = ShipType.Python;
        boundingBox = new float [] {-206.76f * scale,  206.76f * scale, -103.38f * scale,  103.38f * scale, -336.00f * scale,  336.00f * scale};
        numberOfVertices = 54;
        textureFilename = "textures/python.png";
        maxSpeed          = 250.5f;
        maxPitchSpeed     = 0.800f;
        maxRollSpeed      = 2.000f;        
        hullStrength      = 80.0f;
        hasEcm            = false;
        cargoType         = 11;
        aggressionLevel   = 5;
        escapeCapsuleCaps = 1;
        bounty            = 0;
        score             = 80;
        legalityType      = 0;
        maxCargoCanisters = 3;        
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserColor = 0x7FFFFF00l;
        laserTexture = "textures/laser_yellow.png";
        init();
    }

    @Override
    protected void init() {
        final float scale = 0.8f;
        vertexBuffer = createScaledFaces(scale, VERTEX_DATA, NORMAL_DATA,
                1,   0,   3,   2,   0,   1,   2,   1,   5,   3,   0,   4,   4,   0,   2, 
                4,   2,   6,   5,   1,   3,   5,   3,   9,   5,   7,   8,   6,   2,   8, 
                6,   3,   4,   6,  10,   9,   8,   2,   5,   8,   7,   9,   8,   9,  10, 
                8,  10,   6,   9,   3,   6,   9,   7,   5);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 31, 19, 40, 0, 0, 0));
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
