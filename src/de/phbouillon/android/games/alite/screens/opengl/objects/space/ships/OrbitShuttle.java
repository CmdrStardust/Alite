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
 * Orbit Shuttle model from Oolite: http://oolite.org
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

public class OrbitShuttle extends SpaceObject {
	private static final long serialVersionUID = -2817167670824305091L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);

    private static final float [] VERTEX_DATA = new float [] {
          -0.00f,  -31.88f,   40.74f,   -0.00f,  -15.94f,   62.00f,
          31.88f,    0.00f,   40.74f,   35.42f,  -35.42f,  -62.00f,
         -35.42f,  -35.42f,  -62.00f,   35.42f,   35.42f,  -62.00f,
          -0.00f,   31.88f,   40.74f,  -35.42f,   35.42f,  -62.00f,
         -31.88f,    0.00f,   40.74f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.00000f,   0.92457f,  -0.38101f,   0.00000f,   0.18560f,  -0.98263f, 
         -0.87261f,   0.06439f,  -0.48415f,  -0.57853f,   0.57853f,   0.57497f, 
          0.68048f,   0.68048f,   0.27182f,  -0.68048f,  -0.68048f,   0.27182f, 
          0.00000f,  -0.81141f,  -0.58447f,   0.57853f,  -0.57853f,   0.57497f, 
          0.87261f,   0.06439f,  -0.48415f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          1.00f,   0.50f,   0.92f,   0.50f,   0.95f,   0.61f, 
          0.95f,   0.39f,   0.92f,   0.50f,   1.00f,   0.50f, 
          0.08f,   0.39f,   0.00f,   0.50f,   0.12f,   0.50f, 
          0.61f,   0.39f,   0.92f,   0.50f,   0.95f,   0.39f, 
          0.61f,   0.39f,   0.48f,   0.05f,   0.38f,   0.39f, 
          0.61f,   0.61f,   0.92f,   0.50f,   0.61f,   0.39f, 
          0.38f,   0.39f,   0.08f,   0.39f,   0.12f,   0.50f, 
          0.12f,   0.50f,   0.00f,   0.50f,   0.08f,   0.61f, 
          0.12f,   0.50f,   0.38f,   0.61f,   0.38f,   0.39f, 
          0.12f,   0.50f,   0.08f,   0.61f,   0.38f,   0.61f, 
          0.38f,   0.61f,   0.61f,   0.61f,   0.61f,   0.39f, 
          0.38f,   0.61f,   0.61f,   0.39f,   0.38f,   0.39f, 
          0.95f,   0.61f,   0.92f,   0.50f,   0.61f,   0.61f, 
          0.48f,   0.95f,   0.61f,   0.61f,   0.38f,   0.61f
    };

    public OrbitShuttle(Alite alite) {
        super(alite, "Orbit Shuttle", ObjectType.Shuttle);
        shipType = ShipType.OrbitShuttle;
        boundingBox = new float [] { -35.42f,   35.42f,  -35.42f,   35.42f,  -62.00f,   62.00f};
        numberOfVertices = 42;
        textureFilename = "textures/orbitshuttle.png";
        maxSpeed          = 100.2f;
        maxPitchSpeed     = 0.900f;
        maxRollSpeed      = 2.000f;        
        hullStrength      = 112.0f;
        hasEcm            = false;
        cargoType         = 8;     
        aggressionLevel   = 5;
        escapeCapsuleCaps = 1;
        bounty            = 0;
        score             = 3;
        legalityType      = 2;
        maxCargoCanisters = 1;  
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                1,   0,   8,   2,   0,   1,   2,   1,   6,   3,   0,   2,   3,   2,   5, 
                4,   0,   3,   5,   2,   6,   6,   1,   8,   6,   7,   5,   6,   8,   7, 
                7,   4,   3,   7,   3,   5,   8,   0,   4,   8,   4,   7);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);  
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 10, 10, 12, -25,  25, 0, 0.81f, 0.35f, 0.63f, 0.7f));
        	addExhaust(new EngineExhaust(this, 10, 10, 12,  25,  25, 0, 0.81f, 0.35f, 0.63f, 0.7f));
        	addExhaust(new EngineExhaust(this, 10, 10, 12, -25, -25, 0, 0.81f, 0.35f, 0.63f, 0.7f));
        	addExhaust(new EngineExhaust(this, 10, 10, 12,  25, -25, 0, 0.81f, 0.35f, 0.63f, 0.7f));        	
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
