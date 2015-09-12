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
 * Asteroid model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Asteroid1 extends SpaceObject {
	private static final long serialVersionUID = -5144637597179190121L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
          -0.28f,  153.70f,   20.40f, -141.50f,  -25.56f,  -23.48f,
           2.70f, -166.30f,   23.42f,  154.98f,  -82.60f,  -26.72f,
         154.38f,  111.66f,   -6.20f,  115.42f,    3.12f,  122.26f,
         -76.62f,   -6.04f,  141.48f,   22.00f,   58.02f, -143.66f,
          -1.22f, -127.22f, -132.08f,  -49.70f,   75.02f,   97.02f,
         -90.64f,   78.46f,   -2.80f, -117.42f,  -16.20f,   70.52f,
         -46.58f,  -88.20f,   96.66f,  -88.28f, -113.72f,    0.78f,
         -74.76f,  -87.30f,  -94.76f,    0.98f, -158.20f,  -63.78f,
         -71.20f,   26.68f, -101.84f,    6.22f,  -46.24f, -143.94f,
           4.12f,  125.22f,  -72.20f,   64.52f,   97.36f,   90.30f,
          13.46f,   -2.70f,  137.66f,   67.80f,  -96.72f,   88.46f,
         101.24f,   97.08f,  -75.38f,   90.28f,  142.42f,    1.64f,
          93.94f, -142.52f,   -1.02f,   75.30f, -115.20f,  -92.18f,
         102.08f,  -12.30f,  -99.84f,  149.30f,   66.44f,   59.18f,
         152.24f,  -49.02f,   55.68f,  162.74f,   23.70f,  -12.84f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.25194f,   0.94376f,   0.21410f,  -0.97663f,  -0.03549f,  -0.21197f, 
         -0.10259f,  -0.93315f,   0.34455f,   0.87595f,  -0.38204f,  -0.29455f, 
          0.81301f,   0.56875f,  -0.12464f,   0.49900f,   0.00123f,   0.86660f, 
         -0.47550f,  -0.01088f,   0.87965f,   0.07238f,   0.35738f,  -0.93115f, 
         -0.07830f,  -0.60189f,  -0.79473f,  -0.44122f,   0.61826f,   0.65045f, 
         -0.78252f,   0.62047f,  -0.05175f,  -0.91337f,  -0.02554f,   0.40632f, 
         -0.34060f,  -0.61945f,   0.70730f,  -0.71052f,  -0.69761f,   0.09226f, 
         -0.68140f,  -0.40694f,  -0.60835f,  -0.13223f,  -0.94972f,  -0.28381f, 
         -0.64158f,   0.29898f,  -0.70639f,  -0.00796f,  -0.08938f,  -0.99597f, 
         -0.22400f,   0.82968f,  -0.51133f,   0.19288f,   0.63778f,   0.74568f, 
          0.06452f,  -0.00070f,   0.99792f,   0.28788f,  -0.60650f,   0.74114f, 
          0.53771f,   0.52772f,  -0.65755f,   0.32626f,   0.94312f,   0.06382f, 
          0.51640f,  -0.85114f,   0.09427f,   0.53101f,  -0.55487f,  -0.64043f, 
          0.64651f,  -0.02379f,  -0.76254f,   0.78421f,   0.38967f,   0.48288f, 
          0.85544f,  -0.33225f,   0.39729f,   0.96463f,   0.05415f,  -0.25798f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.25f,   0.07f,   1.00f,   0.22f,   0.00f,   0.00f, 
          0.50f,   0.26f,   0.25f,   0.07f,   0.00f,   0.00f, 
          0.00f,   0.00f,   0.64f,   0.31f,   0.50f,   0.26f, 
          0.00f,   0.00f,   1.00f,   0.22f,   0.80f,   0.29f, 
          0.00f,   0.00f,   0.80f,   0.29f,   0.64f,   0.31f, 
          1.00f,   0.22f,   0.25f,   0.07f,   0.05f,   0.23f, 
          0.36f,   0.37f,   0.25f,   0.07f,   0.50f,   0.26f, 
          0.25f,   0.32f,   0.05f,   0.23f,   0.25f,   0.07f, 
          0.36f,   0.37f,   0.25f,   0.32f,   0.25f,   0.07f, 
          0.56f,   0.48f,   0.50f,   0.26f,   0.64f,   0.31f, 
          0.99f,   0.45f,   0.80f,   0.29f,   1.00f,   0.22f, 
          0.05f,   0.23f,   0.02f,   0.48f,   1.00f,   0.22f, 
          0.71f,   0.49f,   0.64f,   0.31f,   0.80f,   0.29f, 
          0.48f,   0.48f,   0.36f,   0.37f,   0.50f,   0.26f, 
          0.25f,   0.51f,   0.05f,   0.23f,   0.25f,   0.32f, 
          0.36f,   0.37f,   0.25f,   0.51f,   0.25f,   0.32f, 
          1.00f,   0.22f,   0.02f,   0.48f,   0.99f,   0.45f, 
          0.50f,   0.26f,   0.56f,   0.48f,   0.48f,   0.48f, 
          0.64f,   0.31f,   0.63f,   0.48f,   0.56f,   0.48f, 
          0.71f,   0.49f,   0.63f,   0.48f,   0.64f,   0.31f, 
          0.99f,   0.45f,   0.82f,   0.59f,   0.80f,   0.29f, 
          0.80f,   0.29f,   0.82f,   0.59f,   0.71f,   0.49f, 
          0.12f,   0.62f,   0.02f,   0.48f,   0.05f,   0.23f, 
          0.48f,   0.48f,   0.38f,   0.63f,   0.36f,   0.37f, 
          0.12f,   0.62f,   0.05f,   0.23f,   0.25f,   0.51f, 
          0.38f,   0.63f,   0.25f,   0.51f,   0.36f,   0.37f, 
          0.61f,   0.64f,   0.56f,   0.48f,   0.63f,   0.48f, 
          0.61f,   0.64f,   0.63f,   0.48f,   0.71f,   0.49f, 
          0.99f,   0.45f,   0.02f,   0.48f,   0.00f,   0.66f, 
          0.50f,   0.68f,   0.48f,   0.48f,   0.56f,   0.48f, 
          0.97f,   0.69f,   0.82f,   0.59f,   0.99f,   0.45f, 
          0.74f,   0.74f,   0.71f,   0.49f,   0.82f,   0.59f, 
          0.02f,   0.48f,   0.12f,   0.62f,   0.00f,   0.66f, 
          0.50f,   0.68f,   0.38f,   0.63f,   0.48f,   0.48f, 
          0.50f,   0.68f,   0.56f,   0.48f,   0.61f,   0.64f, 
          0.61f,   0.64f,   0.71f,   0.49f,   0.74f,   0.74f, 
          0.24f,   0.72f,   0.12f,   0.62f,   0.25f,   0.51f, 
          0.99f,   0.45f,   0.00f,   0.66f,   0.97f,   0.69f, 
          0.38f,   0.63f,   0.24f,   0.72f,   0.25f,   0.51f, 
          0.97f,   0.69f,   0.74f,   0.74f,   0.82f,   0.59f, 
          0.12f,   0.62f,   0.11f,   0.81f,   0.00f,   0.66f, 
          0.34f,   0.93f,   0.38f,   0.63f,   0.50f,   0.68f, 
          0.55f,   0.84f,   0.50f,   0.68f,   0.61f,   0.64f, 
          0.12f,   0.62f,   0.24f,   0.72f,   0.11f,   0.81f, 
          0.55f,   0.84f,   0.61f,   0.64f,   0.74f,   0.74f, 
          0.34f,   0.93f,   0.24f,   0.72f,   0.38f,   0.63f, 
          0.99f,   0.78f,   0.97f,   0.69f,   0.00f,   0.66f, 
          0.94f,   0.93f,   0.74f,   0.74f,   0.97f,   0.69f, 
          0.11f,   0.81f,   0.99f,   0.78f,   0.00f,   0.66f, 
          0.55f,   0.84f,   0.34f,   0.93f,   0.50f,   0.68f, 
          0.11f,   0.81f,   0.24f,   0.72f,   0.34f,   0.93f, 
          0.99f,   0.78f,   0.94f,   0.93f,   0.97f,   0.69f, 
          0.94f,   0.93f,   0.55f,   0.84f,   0.74f,   0.74f, 
          0.34f,   0.93f,   0.94f,   0.93f,   0.11f,   0.81f, 
          0.99f,   0.78f,   0.11f,   0.81f,   0.94f,   0.93f, 
          0.94f,   0.93f,   0.34f,   0.93f,   0.55f,   0.84f
    };
    
    public Asteroid1(Alite alite) {
        super(alite, "Asteroid", ObjectType.Asteroid);
        shipType = ShipType.Asteroid;
        boundingBox = new float [] {-141.50f,  162.74f, -166.30f,  153.70f, -143.94f,  141.48f};
        numberOfVertices = 168;
        textureFilename = "textures/asteroid1.png";
        maxSpeed            = 280.0f;
        maxPitchSpeed       = 0.100f;
        maxRollSpeed        = 0.100f;
        hullStrength        =  60.0f;
        hasEcm              = false;
        cargoType           = 12;  
        spawnCargoCanisters = false;
        aggressionLevel     = 0;
        escapeCapsuleCaps   = 0;
        bounty              = 10;
        score               = 0;
        legalityType        = 3;
        maxCargoCanisters   = 2;     
        missileCount        = 0;
        init();
    }
    
    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                11,  10,   1,  13,  11,   1,   1,  14,  13,   1,  10,  16,   1,  16,  14, 
                10,  11,   9,  12,  11,  13,   6,   9,  11,  12,   6,  11,  15,  13,  14, 
                18,  16,  10,   9,   0,  10,  17,  14,  16,   2,  12,  13,  20,   9,   6, 
                12,  20,   6,  10,   0,  18,  13,  15,   2,  14,   8,  15,  17,   8,  14, 
                18,   7,  16,  16,   7,  17,  19,   0,   9,   2,  21,  12,  19,   9,  20, 
                21,  20,  12,  25,  15,   8,  25,   8,  17,  18,   0,  23,  24,   2,  15, 
                22,   7,  18,  26,  17,   7,   0,  19,  23,  24,  21,   2,  24,  15,  25, 
                25,  17,  26,   5,  19,  20,  18,  23,  22,  21,   5,  20,  22,  26,   7, 
                19,  27,  23,  28,  21,  24,   3,  24,  25,  19,   5,  27,   3,  25,  26, 
                28,   5,  21,   4,  22,  23,  29,  26,  22,  27,   4,  23,   3,  28,  24, 
                27,   5,  28,   4,  29,  22,  29,   3,  26,  28,  29,  27,   4,  27,  29, 
                29,  28,   3);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
        initTargetBox();
    }

    @Override
	protected boolean receivesProximityWarning() {
		return false;
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
    
    @Override
    public boolean avoidObstacles() {
    	return false;
    }
} 
