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
 * Mamba model from Oolite: http://oolite.org
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

public class Mamba extends SpaceObject {
	private static final long serialVersionUID = -630403162013561621L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         140.00f,  -25.84f, -118.46f, -140.00f,  -25.84f, -118.46f,
          -0.00f,    0.00f,  118.46f,   68.92f,   25.84f, -118.46f,
         -68.92f,   25.84f, -118.46f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.32546f,   0.12077f,   0.93781f,   0.64504f,   0.23935f,   0.72569f, 
          0.00000f,  -0.90877f,  -0.41730f,  -0.28859f,  -0.90072f,   0.32468f, 
          0.22915f,  -0.71520f,   0.66029f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          1.00f,   0.69f,   0.55f,   0.42f,   0.55f,   0.95f, 
          0.00f,   0.69f,   0.43f,   0.98f,   0.45f,   0.82f, 
          0.45f,   0.56f,   0.43f,   0.39f,   0.00f,   0.69f, 
          0.45f,   0.56f,   0.00f,   0.69f,   0.45f,   0.82f, 
          0.45f,   0.82f,   0.55f,   0.95f,   0.55f,   0.42f, 
          0.45f,   0.82f,   0.55f,   0.42f,   0.45f,   0.56f
    };

    public Mamba(Alite alite) {
        super(alite, "Mamba", ObjectType.EnemyShip);
        shipType = ShipType.Mamba;
        boundingBox = new float [] {-140.00f,  140.00f,  -25.84f,   25.84f, -118.46f,  118.46f};
        numberOfVertices = 18;
        textureFilename = "textures/mamba.png";
        maxSpeed          = 400.8f;
        maxPitchSpeed     = 1.400f;
        maxRollSpeed      = 2.100f;        
        hullStrength      = 40.0f;
        hasEcm            = false;
        cargoType         = 10;     
        aggressionLevel   = 10;
        escapeCapsuleCaps = 1;
        bounty            = 40;
        score             = 40;
        legalityType      = 1;
        maxCargoCanisters = 1;        
        laserHardpoints.add(VERTEX_DATA[6]);
        laserHardpoints.add(VERTEX_DATA[7]);
        laserHardpoints.add(VERTEX_DATA[8]);
        laserColor = 0x7FFF0000l;
        laserTexture = "textures/laser_red.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                2,   0,   1,   2,   1,   4,   3,   0,   2,   3,   2,   4,   4,   1,   0, 
                4,   0,   3);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this,  7,  7, 30, -60, 0, 0, 1.0f, 0.81f, 0.2f, 0.7f));
        	addExhaust(new EngineExhaust(this,  7,  7, 30,  60, 0, 0, 1.0f, 0.81f, 0.2f, 0.7f));
        	addExhaust(new EngineExhaust(this, 22, 13, 40,   0, 0, 0, 1.0f, 0.5f, 0.8f, 0.7f));
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
