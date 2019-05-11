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
 * Wolf Mk II model by Murgh from Oolite.
 * Texture by Philipp Bouillon.
 */
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class WolfMkII extends SpaceObject {
	private static final long serialVersionUID = 5800051649178290390L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
          40.00f,  -28.88f,   15.56f,   40.00f,   28.88f,   15.56f,
         -40.00f,   28.88f,   15.56f,  -40.00f,  -28.88f,   15.56f,
          40.00f,  -28.88f,  -55.56f,   40.00f,   28.88f,  -55.56f,
         -40.00f,   28.88f,  -55.56f,  -40.00f,  -28.88f,  -55.56f,
         100.00f,    0.00f,   15.56f, -100.00f,    0.00f,   15.56f,
         100.00f,    0.00f,  -55.56f, -100.00f,    0.00f,  -55.56f,
          40.00f,    0.00f, -120.00f,  -40.00f,    0.00f, -120.00f,
          40.00f,    0.00f,  120.00f,  -40.00f,    0.00f,  120.00f,
          17.78f,    5.56f, -111.12f,  -17.78f,    5.56f, -111.12f,
         -17.78f,   -5.56f, -111.12f,   17.78f,   -5.56f, -111.12f,
         -28.88f,    0.00f, -120.00f,   28.88f,    0.00f, -120.00f,
          22.22f,   28.88f,   15.56f,  -22.22f,   28.88f,   15.56f,
          22.22f,    0.00f,  120.00f,  -22.22f,    0.00f,  120.00f,
           4.44f,   33.34f,   15.56f,   -4.44f,   33.34f,   15.56f,
          12.22f,   21.66f,   41.66f,  -12.22f,   21.66f,   41.66f,
          13.34f,   28.88f,  -55.56f,  -13.34f,   28.88f,  -55.56f,
           4.44f,   24.44f,  -55.56f,   -4.44f,   24.44f,  -55.56f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.14766f,   0.97761f,  -0.14990f,  -0.22209f,  -0.96583f,  -0.13357f, 
          0.17652f,  -0.97120f,  -0.16004f,   0.22092f,   0.96640f,  -0.13140f, 
         -0.15583f,   0.95031f,   0.26949f,  -0.15629f,  -0.96661f,   0.20307f, 
          0.19887f,  -0.96439f,   0.17438f,   0.19887f,   0.96439f,   0.17438f, 
         -0.96223f,   0.00000f,  -0.27222f,   0.98284f,   0.00000f,  -0.18444f, 
         -0.95916f,   0.00000f,   0.28287f,   0.91262f,   0.00000f,   0.40880f, 
         -0.33493f,  -0.00000f,   0.94224f,   0.33493f,   0.00000f,   0.94224f, 
         -0.46451f,  -0.53179f,  -0.70812f,   0.47730f,   0.00000f,  -0.87874f, 
          0.14696f,  -0.67220f,   0.72564f,  -0.14178f,  -0.78632f,   0.60133f, 
         -0.09683f,   0.62258f,   0.77655f,   0.13531f,   0.64732f,   0.75012f, 
         -0.32135f,   0.00000f,   0.94696f,   0.32135f,   0.00000f,   0.94696f, 
         -0.09597f,  -0.98843f,  -0.11749f,   0.09597f,  -0.98843f,  -0.11749f, 
          0.00000f,  -0.00000f,  -1.00000f,   0.00000f,  -0.58595f,  -0.81035f, 
         -0.00555f,  -0.99471f,  -0.10258f,   0.00394f,  -0.99329f,  -0.11562f, 
         -0.02866f,  -0.94944f,  -0.31263f,   0.03822f,  -0.95191f,  -0.30398f, 
          0.12996f,  -0.97276f,   0.19193f,  -0.12996f,  -0.97276f,   0.19193f, 
          0.14727f,  -0.96017f,   0.23746f,  -0.22173f,  -0.94453f,   0.24227f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.17f,   0.62f,   0.40f,   0.82f,   0.40f,   0.62f, 
          0.17f,   0.62f,   0.00f,   0.82f,   0.17f,   0.82f, 
          0.17f,   0.62f,   0.17f,   0.32f,   0.00f,   0.62f, 
          0.17f,   0.62f,   0.22f,   0.32f,   0.17f,   0.32f, 
          0.17f,   0.62f,   0.35f,   0.32f,   0.22f,   0.32f, 
          0.60f,   0.38f,   0.43f,   0.18f,   0.43f,   0.38f, 
          0.60f,   0.38f,   0.65f,   0.38f,   0.60f,   0.18f, 
          0.60f,   0.38f,   0.68f,   0.46f,   0.65f,   0.38f, 
          0.83f,   0.38f,   1.00f,   0.38f,   0.83f,   0.18f, 
          0.83f,   0.38f,   0.83f,   0.68f,   1.00f,   0.38f, 
          0.83f,   0.38f,   0.75f,   0.46f,   0.83f,   0.68f, 
          0.40f,   0.62f,   0.40f,   0.32f,   0.17f,   0.62f, 
          0.17f,   0.82f,   0.40f,   0.82f,   0.17f,   0.62f, 
          0.17f,   0.82f,   0.33f,   0.97f,   0.40f,   0.82f, 
          0.17f,   0.82f,   0.23f,   0.97f,   0.33f,   0.97f, 
          0.60f,   0.18f,   0.43f,   0.18f,   0.60f,   0.38f, 
          0.60f,   0.18f,   0.60f,   0.00f,   0.43f,   0.18f, 
          0.60f,   0.18f,   0.67f,   0.03f,   0.60f,   0.00f, 
          0.60f,   0.18f,   0.68f,   0.18f,   0.67f,   0.03f, 
          0.83f,   0.18f,   0.78f,   0.38f,   0.83f,   0.38f, 
          0.83f,   0.18f,   0.75f,   0.18f,   0.78f,   0.38f, 
          0.40f,   0.82f,   0.56f,   0.62f,   0.40f,   0.62f, 
          0.40f,   0.82f,   0.56f,   0.82f,   0.56f,   0.62f, 
          0.40f,   0.82f,   0.40f,   1.00f,   0.56f,   0.82f, 
          0.40f,   0.82f,   0.33f,   0.97f,   0.40f,   1.00f, 
          0.00f,   0.62f,   0.00f,   0.82f,   0.17f,   0.62f, 
          0.43f,   0.38f,   0.60f,   0.68f,   0.60f,   0.38f, 
          1.00f,   0.38f,   1.00f,   0.18f,   0.83f,   0.18f, 
          0.56f,   0.62f,   0.40f,   0.32f,   0.40f,   0.62f, 
          0.00f,   0.82f,   0.17f,   1.00f,   0.17f,   0.82f, 
          1.00f,   0.18f,   0.83f,   0.00f,   0.83f,   0.18f, 
          0.17f,   1.00f,   0.23f,   0.97f,   0.17f,   0.82f, 
          0.17f,   1.00f,   0.20f,   1.00f,   0.23f,   0.97f, 
          0.83f,   0.00f,   0.76f,   0.03f,   0.83f,   0.18f, 
          0.83f,   0.00f,   0.80f,   0.00f,   0.76f,   0.03f, 
          0.60f,   0.68f,   0.68f,   0.46f,   0.60f,   0.38f, 
          0.40f,   0.32f,   0.35f,   0.32f,   0.17f,   0.62f, 
          0.83f,   0.68f,   0.75f,   0.46f,   0.78f,   0.68f, 
          0.67f,   0.03f,   0.63f,   0.00f,   0.60f,   0.00f, 
          0.67f,   0.03f,   0.70f,   0.18f,   0.76f,   0.03f, 
          0.56f,   0.89f,   0.56f,   1.00f,   0.89f,   0.89f, 
          0.56f,   0.89f,   0.46f,   0.94f,   0.56f,   1.00f, 
          0.76f,   0.03f,   0.75f,   0.18f,   0.83f,   0.18f, 
          0.76f,   0.03f,   0.73f,   0.18f,   0.75f,   0.18f, 
          0.56f,   1.00f,   0.89f,   1.00f,   0.89f,   0.89f, 
          0.33f,   0.97f,   0.36f,   1.00f,   0.40f,   1.00f, 
          0.89f,   1.00f,   0.99f,   0.94f,   0.89f,   0.89f, 
          0.13f,   0.01f,   0.02f,   0.05f,   0.13f,   0.09f, 
          0.65f,   0.38f,   0.68f,   0.18f,   0.60f,   0.18f, 
          0.78f,   0.38f,   0.75f,   0.46f,   0.83f,   0.38f, 
          0.13f,   0.20f,   0.43f,   0.16f,   0.13f,   0.12f, 
          0.65f,   0.68f,   0.68f,   0.46f,   0.60f,   0.68f, 
          0.78f,   0.68f,   0.68f,   0.46f,   0.65f,   0.68f, 
          0.78f,   0.68f,   0.75f,   0.46f,   0.68f,   0.46f, 
          0.13f,   0.09f,   0.02f,   0.05f,   0.13f,   0.12f, 
          0.13f,   0.09f,   0.43f,   0.05f,   0.13f,   0.01f, 
          0.13f,   0.09f,   0.43f,   0.09f,   0.43f,   0.05f, 
          0.13f,   0.12f,   0.02f,   0.16f,   0.13f,   0.20f, 
          0.13f,   0.12f,   0.43f,   0.09f,   0.13f,   0.09f, 
          0.13f,   0.12f,   0.43f,   0.12f,   0.43f,   0.09f, 
          0.02f,   0.05f,   0.02f,   0.16f,   0.13f,   0.12f, 
          0.68f,   0.18f,   0.70f,   0.18f,   0.67f,   0.03f, 
          0.43f,   0.16f,   0.43f,   0.12f,   0.13f,   0.12f, 
          0.70f,   0.18f,   0.73f,   0.18f,   0.76f,   0.03f
    };

    public WolfMkII(Alite alite) {
        super(alite, "Wolf Mk II", ObjectType.EnemyShip);
        shipType = ShipType.WolfMkII;
        boundingBox = new float [] {-100.00f,  100.00f,  -28.88f,   33.34f, -120.00f,  120.00f};
        numberOfVertices = 192;
        textureFilename = "textures/wolfmkii.png";
        maxSpeed          = 501.0f;
        maxPitchSpeed     = 1.000f;
        maxRollSpeed      = 2.000f;        
        hullStrength      = 255.0f;
        hasEcm            = true;
        cargoType         = 9;
        aggressionLevel   = 15;
        escapeCapsuleCaps = 3;
        bounty            = 150;
        score             = 250;
        legalityType      = 1;
        maxCargoCanisters = 1;
        laserHardpoints.add(VERTEX_DATA[72]);
        laserHardpoints.add(VERTEX_DATA[73]);
        laserHardpoints.add(VERTEX_DATA[74]);
        laserHardpoints.add(VERTEX_DATA[75]);
        laserHardpoints.add(VERTEX_DATA[76]);
        laserHardpoints.add(VERTEX_DATA[77]);
        laserColor = 0x7FFF0000l;
        laserTexture = "textures/laser_red.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   7,   3,   0,  10,   4,   0,  14,   8,   0,  24,  14,   0,  25,  24, 
                1,  10,   8,   1,  22,   5,   1,  28,  22,   2,   9,   6,   2,  15,   9, 
                2,  29,  15,   3,  15,   0,   4,   7,   0,   4,  18,   7,   4,  19,  18, 
                5,  10,   1,   5,  12,  10,   5,  16,  12,   5,  30,  16,   6,  23,   2, 
                6,  31,  23,   7,   9,   3,   7,  11,   9,   7,  13,  11,   7,  18,  13, 
                8,  10,   0,   8,  14,   1,   9,  11,   6,   9,  15,   3,  10,  12,   4, 
               11,  13,   6,  12,  19,   4,  12,  21,  19,  13,  17,   6,  13,  20,  17, 
               14,  28,   1,  15,  25,   0,  15,  29,  25,  16,  21,  12,  16,  32,  17, 
               17,  18,  16,  17,  20,  18,  17,  31,   6,  17,  33,  31,  18,  19,  16, 
               18,  20,  13,  19,  21,  16,  22,  28,  26,  22,  30,   5,  23,  29,   2, 
               23,  31,  27,  24,  28,  14,  25,  28,  24,  25,  29,  28,  26,  28,  27, 
               26,  30,  22,  26,  32,  30,  27,  29,  23,  27,  32,  26,  27,  33,  32, 
               28,  29,  27,  30,  32,  16,  31,  33,  27,  32,  33,  17);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 27, 7, 40, 0, 0, -10, 0.97f, 0.10f, 0.8f, 0.7f));        	
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
