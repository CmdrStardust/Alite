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

public class Asteroid2 extends SpaceObject {
	private static final long serialVersionUID = 5711378010738676362L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -13.30f,  153.20f,   21.96f, -132.70f,  -25.74f,  -22.54f,
         -11.24f, -159.18f,   23.06f,  162.32f,  -84.12f,  -26.90f,
         171.70f,  135.84f,   -5.42f,  118.34f,   12.68f,   91.22f,
         -98.82f,   -2.86f,  106.22f,   20.86f,   68.82f, -103.58f,
         -16.04f, -146.06f, -100.96f,  -70.50f,   86.24f,   76.68f,
        -112.56f,   91.82f,   -1.30f, -141.56f,  -14.60f,   52.42f,
         -66.44f,  -92.74f,   76.34f, -110.68f, -125.34f,    0.56f,
         -92.80f,  -99.76f,  -75.32f,  -13.34f, -166.80f,  -48.08f,
         -85.98f,   33.34f,  -77.88f,   -2.84f,  -53.74f, -108.68f,
          -2.94f,  145.76f,  -49.56f,   61.36f,  116.46f,   72.18f,
           1.00f,    2.86f,  104.92f,   64.08f, -101.76f,   69.88f,
         112.74f,  118.02f,  -54.44f,   96.64f,  146.72f,    4.34f,
          93.30f, -154.80f,   -1.72f,   70.34f, -128.24f,  -73.76f,
         108.66f,   -7.74f,  -77.76f,  162.78f,   85.80f,   42.92f,
         162.28f,  -46.08f,   38.52f,  177.76f,   38.38f,  -13.38f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.22510f,   0.93295f,   0.28095f,  -0.93977f,  -0.01140f,  -0.34163f, 
         -0.10333f,  -0.87722f,   0.46885f,   0.83794f,  -0.39123f,  -0.38053f, 
          0.73157f,   0.67438f,  -0.10007f,   0.39034f,  -0.01170f,   0.92060f, 
         -0.41618f,  -0.00605f,   0.90926f,   0.03675f,   0.27881f,  -0.95964f, 
         -0.08923f,  -0.58287f,  -0.80765f,  -0.41967f,   0.54058f,   0.72914f, 
         -0.80442f,   0.58306f,  -0.11380f,  -0.92671f,  -0.01789f,   0.37534f, 
         -0.29738f,  -0.52149f,   0.79976f,  -0.73912f,  -0.66719f,   0.09251f, 
         -0.66065f,  -0.34889f,  -0.66469f,  -0.14455f,  -0.96387f,  -0.22375f, 
         -0.56007f,   0.24539f,  -0.79127f,  -0.00601f,  -0.03675f,  -0.99931f, 
         -0.19486f,   0.81950f,  -0.53893f,   0.14151f,   0.57515f,   0.80571f, 
          0.04213f,  -0.00204f,   0.99911f,   0.23209f,  -0.50153f,   0.83343f, 
          0.38267f,   0.52227f,  -0.76210f,   0.19324f,   0.96891f,   0.15448f, 
          0.45619f,  -0.87360f,   0.16944f,   0.42409f,  -0.53910f,  -0.72768f, 
          0.48134f,  -0.01122f,  -0.87646f,   0.69737f,   0.34230f,   0.62968f, 
          0.80521f,  -0.30908f,   0.50607f,   0.94324f,   0.02222f,  -0.33138f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.25f,   0.07f,   1.00f,   0.22f,   0.00f,   0.00f, 
          0.50f,   0.26f,   0.25f,   0.07f,   0.00f,   0.00f, 
          0.00f,   0.00f,   0.64f,   0.31f,   0.50f,   0.26f, 
          0.00f,   0.00f,   1.00f,   0.22f,   0.80f,   0.29f, 
          1.00f,   0.22f,   0.25f,   0.07f,   0.05f,   0.23f, 
          0.36f,   0.37f,   0.25f,   0.07f,   0.50f,   0.26f, 
          0.00f,   0.00f,   0.80f,   0.29f,   0.64f,   0.31f, 
          0.25f,   0.32f,   0.05f,   0.23f,   0.25f,   0.07f, 
          0.36f,   0.37f,   0.25f,   0.32f,   0.25f,   0.07f, 
          0.56f,   0.48f,   0.50f,   0.26f,   0.64f,   0.31f, 
          0.99f,   0.45f,   0.80f,   0.29f,   1.00f,   0.22f, 
          0.05f,   0.23f,   0.02f,   0.48f,   1.00f,   0.22f, 
          0.48f,   0.48f,   0.36f,   0.37f,   0.50f,   0.26f, 
          0.71f,   0.49f,   0.64f,   0.31f,   0.80f,   0.29f, 
          0.25f,   0.51f,   0.05f,   0.23f,   0.25f,   0.32f, 
          0.36f,   0.37f,   0.25f,   0.51f,   0.25f,   0.32f, 
          0.50f,   0.26f,   0.56f,   0.48f,   0.48f,   0.48f, 
          1.00f,   0.22f,   0.02f,   0.48f,   0.99f,   0.45f, 
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
          0.50f,   0.68f,   0.48f,   0.48f,   0.56f,   0.48f, 
          0.99f,   0.45f,   0.02f,   0.48f,   0.00f,   0.66f, 
          0.74f,   0.74f,   0.71f,   0.49f,   0.82f,   0.59f, 
          0.97f,   0.69f,   0.82f,   0.59f,   0.99f,   0.45f, 
          0.02f,   0.48f,   0.12f,   0.62f,   0.00f,   0.66f, 
          0.50f,   0.68f,   0.38f,   0.63f,   0.48f,   0.48f, 
          0.50f,   0.68f,   0.56f,   0.48f,   0.61f,   0.64f, 
          0.61f,   0.64f,   0.71f,   0.49f,   0.74f,   0.74f, 
          0.24f,   0.72f,   0.12f,   0.62f,   0.25f,   0.51f, 
          0.38f,   0.63f,   0.24f,   0.72f,   0.25f,   0.51f, 
          0.99f,   0.45f,   0.00f,   0.66f,   0.97f,   0.69f, 
          0.97f,   0.69f,   0.74f,   0.74f,   0.82f,   0.59f, 
          0.34f,   0.93f,   0.38f,   0.63f,   0.50f,   0.68f, 
          0.12f,   0.62f,   0.11f,   0.81f,   0.00f,   0.66f, 
          0.55f,   0.84f,   0.50f,   0.68f,   0.61f,   0.64f, 
          0.55f,   0.84f,   0.61f,   0.64f,   0.74f,   0.74f, 
          0.12f,   0.62f,   0.24f,   0.72f,   0.11f,   0.81f, 
          0.34f,   0.93f,   0.24f,   0.72f,   0.38f,   0.63f, 
          0.99f,   0.78f,   0.97f,   0.69f,   0.00f,   0.66f, 
          0.94f,   0.93f,   0.74f,   0.74f,   0.97f,   0.69f, 
          0.55f,   0.84f,   0.34f,   0.93f,   0.50f,   0.68f, 
          0.11f,   0.81f,   0.99f,   0.78f,   0.00f,   0.66f, 
          0.11f,   0.81f,   0.24f,   0.72f,   0.34f,   0.93f, 
          0.94f,   0.93f,   0.55f,   0.84f,   0.74f,   0.74f, 
          0.99f,   0.78f,   0.94f,   0.93f,   0.97f,   0.69f, 
          0.94f,   0.93f,   0.34f,   0.93f,   0.55f,   0.84f, 
          0.34f,   0.93f,   0.94f,   0.93f,   0.11f,   0.81f, 
          0.99f,   0.78f,   0.11f,   0.81f,   0.94f,   0.93f
    };

    public Asteroid2(Alite alite) {
        super(alite, "Asteroid", ObjectType.Asteroid);
        shipType = ShipType.Asteroid;
        boundingBox = new float [] {-141.56f,  177.76f, -166.80f,  153.20f, -108.68f,  106.22f};
        numberOfVertices = 168;
        textureFilename = "textures/asteroid2.png";
        maxSpeed            = 312.0f;
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
        maxCargoCanisters   = 3;
        missileCount        = 0;
        init();        
    }
    
    @Override
    protected void init() {
		vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA, 11, 10, 1, 13, 11,
				1, 1, 14, 13, 1, 10, 16, 10, 11, 9, 12, 11, 13, 1, 16, 14, 6,
				9, 11, 12, 6, 11, 15, 13, 14, 18, 16, 10, 9, 0, 10, 2, 12, 13,
				17, 14, 16, 20, 9, 6, 12, 20, 6, 13, 15, 2, 10, 0, 18, 14, 8,
				15, 17, 8, 14, 18, 7, 16, 16, 7, 17, 19, 0, 9, 2, 21, 12, 19,
				9, 20, 21, 20, 12, 25, 15, 8, 25, 8, 17, 24, 2, 15, 18, 0, 23,
				26, 17, 7, 22, 7, 18, 0, 19, 23, 24, 21, 2, 24, 15, 25, 25, 17,
				26, 5, 19, 20, 21, 5, 20, 18, 23, 22, 22, 26, 7, 28, 21, 24,
				19, 27, 23, 3, 24, 25, 3, 25, 26, 19, 5, 27, 28, 5, 21, 4, 22,
				23, 29, 26, 22, 3, 28, 24, 27, 4, 23, 27, 5, 28, 29, 3, 26, 4,
				29, 22, 29, 28, 3, 28, 29, 27, 4, 27, 29);
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
