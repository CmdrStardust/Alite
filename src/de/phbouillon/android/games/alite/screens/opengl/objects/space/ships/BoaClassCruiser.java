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
 * Boa Class Cruiser model from Oolite: http://oolite.org
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

public class BoaClassCruiser extends SpaceObject {
	private static final long serialVersionUID = 6390021751128227933L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -22.60f,    0.00f, -200.00f,   -0.00f,   38.26f, -194.78f,
          -0.00f,  104.34f, -158.26f,  -67.82f,  104.34f, -111.30f,
        -113.04f,   27.82f, -123.48f,  -80.00f,  -33.04f, -184.34f,
          22.60f,    0.00f, -200.00f,  -45.22f, -104.34f, -151.30f,
          45.22f, -104.34f, -151.30f,   80.00f,  -33.04f, -184.34f,
         113.04f,   27.82f, -123.48f,   67.82f,  104.34f, -111.30f,
          -0.00f,   27.82f,  200.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.28504f,  -0.11299f,   0.95183f,  -0.09597f,  -0.41911f,   0.90285f, 
          0.00000f,  -0.77354f,   0.63374f,   0.50843f,  -0.77986f,   0.36513f, 
          0.96046f,  -0.11220f,   0.25481f,   0.62314f,   0.17893f,   0.76137f, 
         -0.18761f,   0.04668f,   0.98113f,   0.41427f,   0.75518f,   0.50802f, 
         -0.50138f,   0.79083f,   0.35100f,  -0.62314f,   0.17893f,   0.76137f, 
         -0.90740f,  -0.18370f,   0.37798f,  -0.48268f,  -0.83976f,   0.24865f, 
          0.00000f,   0.01813f,  -0.99984f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.69f,   0.07f,   0.60f,   0.12f,   0.65f,   0.24f, 
          0.72f,   0.01f,   0.69f,   0.07f,   0.75f,   0.07f, 
          0.20f,   0.99f,   0.30f,   0.94f,   0.28f,   0.82f, 
          0.56f,   0.99f,   0.62f,   0.98f,   0.61f,   0.80f, 
          0.56f,   0.99f,   0.61f,   0.80f,   0.48f,   0.82f, 
          0.28f,   0.82f,   0.38f,   0.89f,   0.48f,   0.82f, 
          0.28f,   0.82f,   0.14f,   0.80f,   0.13f,   0.98f, 
          0.28f,   0.82f,   0.13f,   0.98f,   0.20f,   0.99f, 
          0.14f,   0.80f,   0.28f,   0.82f,   0.38f,   0.35f, 
          0.14f,   0.80f,   0.06f,   0.91f,   0.13f,   0.98f, 
          0.52f,   0.23f,   0.46f,   0.35f,   0.65f,   0.24f, 
          0.75f,   0.07f,   0.69f,   0.07f,   0.65f,   0.24f, 
          0.75f,   0.07f,   0.65f,   0.24f,   0.79f,   0.24f, 
          0.62f,   0.98f,   0.69f,   0.91f,   0.61f,   0.80f, 
          0.65f,   0.24f,   0.46f,   0.35f,   0.72f,   0.79f, 
          0.79f,   0.24f,   0.65f,   0.24f,   0.72f,   0.79f, 
          0.79f,   0.24f,   0.84f,   0.12f,   0.75f,   0.07f, 
          0.91f,   0.23f,   0.79f,   0.24f,   0.98f,   0.35f, 
          0.98f,   0.35f,   0.79f,   0.24f,   0.72f,   0.79f, 
          0.48f,   0.82f,   0.46f,   0.94f,   0.56f,   0.99f, 
          0.48f,   0.82f,   0.61f,   0.80f,   0.38f,   0.35f, 
          0.38f,   0.35f,   0.28f,   0.82f,   0.48f,   0.82f
    };

    public BoaClassCruiser(Alite alite) {
        super(alite, "Boa Class Cruiser", ObjectType.EnemyShip);
        shipType = ShipType.BoaClassCruiser;
        boundingBox = new float [] {-113.04f,  113.04f, -104.34f,  104.34f, -200.00f,  200.00f};
        numberOfVertices = 66;
        textureFilename = "textures/boaclasscruiser.png";
        maxSpeed          = 300.6f;
        maxPitchSpeed     = 1.000f;
        maxRollSpeed      = 2.800f;
        hullStrength      = 124.0f;
        hasEcm            = false;
        cargoType         = 9; 
        aggressionLevel   = 5;
        escapeCapsuleCaps = 1;
        bounty            = 80;
        score             = 130;
        legalityType      = 1;
        maxCargoCanisters = 3;        
        laserHardpoints.add(VERTEX_DATA[36]);
        laserHardpoints.add(VERTEX_DATA[37]);
        laserHardpoints.add(VERTEX_DATA[38]);
        laserColor = 0x7F0000FFl;
        laserTexture = "textures/laser_blue.png";
        init();        
    }
    
    @Override
    protected void init() {
		vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA, 0, 5, 7, 1, 0, 6,
				1, 2, 3, 1, 6, 10, 1, 10, 11, 3, 2, 11, 3, 4, 0, 3, 0, 1, 4, 3,
				12, 4, 5, 0, 5, 4, 7, 6, 0, 7, 6, 7, 8, 6, 9, 10, 7, 4, 12, 8,
				7, 12, 8, 9, 6, 9, 8, 10, 10, 8, 12, 11, 2, 1, 11, 10, 12, 12,
				3, 11);
		texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
		alite.getTextureManager().addTexture(textureFilename);
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, 0, 20, -5, 0.63f, 0.23f, 0.7f, 0.7f));
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
