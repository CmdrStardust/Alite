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
 * Thargon model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Thargon extends SpaceObject {
	private static final long serialVersionUID = -5754199155975733990L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.55f, 0.55f);
	private Thargoid mother = null;
	
    private static final float [] VERTEX_DATA = new float [] {
          29.06f,  -10.90f,  -43.58f,  -29.06f,  -10.90f,  -43.58f,
         -46.00f,  -10.90f,   10.90f,    0.00f,  -10.90f,   43.58f,
          46.00f,  -10.90f,   10.90f,  -12.10f,   10.90f,  -23.00f,
           0.00f,   10.90f,  -14.52f,   -7.26f,   10.90f,  -36.32f,
           7.26f,   10.90f,  -36.32f,   12.10f,   10.90f,  -23.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.45469f,  -0.37120f,   0.80961f,   0.68116f,   0.12606f,   0.72120f, 
          0.84959f,  -0.39280f,  -0.35201f,   0.00000f,  -0.76796f,  -0.64049f, 
         -0.86464f,   0.35223f,  -0.35824f,   0.33657f,  -0.94141f,   0.02155f, 
          0.00000f,  -0.95906f,  -0.28320f,   0.16053f,  -0.83319f,   0.52918f, 
         -0.26437f,  -0.83429f,   0.48381f,  -0.27908f,  -0.96010f,   0.01787f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.58f,   0.94f,   0.85f,   0.97f,   0.67f,   0.81f, 
          0.58f,   0.56f,   0.60f,   0.71f,   0.67f,   0.70f, 
          0.19f,   0.50f,   0.01f,   0.75f,   0.19f,   1.00f, 
          0.85f,   0.53f,   0.67f,   0.70f,   0.70f,   0.75f, 
          0.19f,   1.00f,   0.48f,   0.91f,   0.48f,   0.59f, 
          0.19f,   1.00f,   0.48f,   0.59f,   0.19f,   0.50f, 
          0.85f,   0.97f,   0.99f,   0.75f,   0.70f,   0.75f, 
          0.67f,   0.70f,   0.85f,   0.53f,   0.58f,   0.56f, 
          0.70f,   0.75f,   0.99f,   0.75f,   0.85f,   0.53f, 
          0.70f,   0.75f,   0.67f,   0.81f,   0.85f,   0.97f, 
          0.60f,   0.71f,   0.48f,   0.59f,   0.48f,   0.91f, 
          0.60f,   0.71f,   0.48f,   0.91f,   0.60f,   0.79f, 
          0.67f,   0.81f,   0.70f,   0.75f,   0.67f,   0.70f, 
          0.67f,   0.81f,   0.67f,   0.70f,   0.60f,   0.71f, 
          0.67f,   0.81f,   0.60f,   0.71f,   0.60f,   0.79f, 
          0.67f,   0.81f,   0.60f,   0.79f,   0.58f,   0.94f
    };

    public Thargon(Alite alite) {
        super(alite, "Thargon", ObjectType.Thargon);
        shipType = ShipType.Thargon;
        boundingBox = new float [] { -46.00f,   46.00f,  -10.90f,   10.90f,  -43.58f,   43.58f};
        numberOfVertices = 48;
        textureFilename = "textures/thargon_ds.png";
        maxSpeed            = 250.5f;
        maxPitchSpeed       = 1.000f;
        maxRollSpeed        = 2.000f;        
        hullStrength        = 30.0f;
        hasEcm              = false;
        cargoType           = 16;    
        spawnCargoCanisters = false;
        aggressionLevel     = 5;
        escapeCapsuleCaps   = 0;
        bounty              = 30;
        score               = 30;
        legalityType        = 7;
        maxCargoCanisters   = 0;
        missileCount        = 0;
        laserHardpoints.add(VERTEX_DATA[9]);
        laserHardpoints.add(VERTEX_DATA[10]);
        laserHardpoints.add(VERTEX_DATA[11]);
        laserColor = 0x7F00FFAAl;
        laserTexture = "textures/laser_dark_cyan.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   4,   9,   1,   7,   5,   2,   3,   4,   2,   5,   6,   4,   0,   1, 
                4,   1,   2,   4,   3,   6,   5,   2,   1,   6,   3,   2,   6,   9,   4, 
                7,   1,   0,   7,   0,   8,   9,   6,   5,   9,   5,   7,   9,   7,   8, 
                9,   8,   0);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
        initTargetBox();
    }

    @Override
	protected boolean receivesProximityWarning() {
		boolean result = mother != null && mother.getHullStrength() > 0;
		return result;
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
    
    public void setMother(Thargoid thargoid) {
    	mother = thargoid;
    }
    
    public Thargoid getMother() {
    	return mother;
    }   
}
