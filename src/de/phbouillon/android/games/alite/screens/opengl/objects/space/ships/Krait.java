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
 * Krait model from Oolite: http://oolite.org
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

public class Krait extends SpaceObject {
	private static final long serialVersionUID = 2558902693785300904L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
        -176.00f,    0.00f,  160.00f, -174.00f,   -2.00f,  -56.00f,
        -174.00f,    2.00f,  -56.00f, -180.00f,    0.00f,  -54.00f,
         176.00f,    0.00f,  160.00f,  174.00f,   -2.00f,  -56.00f,
         174.00f,    2.00f,  -56.00f,  180.00f,    0.00f,  -54.00f,
          -0.00f,    0.00f,  160.00f, -180.00f,    0.00f,  -54.00f,
          -0.00f,   40.00f, -160.00f,   -0.00f,  -40.00f, -160.00f,
         180.00f,    0.00f,  -54.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.99840f,   0.00000f,  -0.05655f,  -0.26727f,   0.68704f,   0.67568f, 
         -0.26727f,  -0.68704f,   0.67568f,   0.71019f,   0.00000f,   0.70401f, 
          0.99840f,   0.00000f,  -0.05655f,   0.26727f,   0.68704f,   0.67568f, 
          0.26727f,  -0.68704f,   0.67568f,  -0.71019f,   0.00000f,   0.70401f, 
          0.00000f,   0.00000f,  -1.00000f,   0.79191f,   0.00000f,   0.61064f, 
          0.00000f,  -0.79893f,   0.60142f,   0.00000f,   0.79893f,   0.60142f, 
         -0.79191f,   0.00000f,   0.61064f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.04f,   0.37f,   0.03f,   0.02f,   0.03f,   0.37f, 
          0.06f,   0.36f,   0.03f,   0.02f,   0.05f,   0.36f, 
          0.03f,   0.37f,   0.03f,   0.38f,   0.04f,   0.37f, 
          0.05f,   0.36f,   0.03f,   0.02f,   0.04f,   0.37f, 
          0.59f,   0.37f,   0.59f,   0.02f,   0.58f,   0.36f, 
          0.59f,   0.37f,   0.59f,   0.38f,   0.59f,   0.37f, 
          0.59f,   0.37f,   0.59f,   0.02f,   0.59f,   0.37f, 
          0.58f,   0.36f,   0.59f,   0.02f,   0.57f,   0.36f, 
          0.08f,   0.70f,   0.31f,   0.97f,   0.31f,   0.56f, 
          0.31f,   0.44f,   0.31f,   0.03f,   0.08f,   0.30f, 
          0.31f,   0.45f,   0.05f,   0.50f,   0.31f,   0.55f, 
          0.31f,   0.56f,   0.31f,   0.97f,   0.54f,   0.70f, 
          0.31f,   0.55f,   0.58f,   0.50f,   0.31f,   0.45f, 
          0.54f,   0.30f,   0.31f,   0.03f,   0.31f,   0.44f
    };

    public Krait(Alite alite) {
        super(alite, "Krait", ObjectType.EnemyShip);
        shipType = ShipType.Krait;
        boundingBox = new float [] {-180.00f,  180.00f,  -40.00f,   40.00f, -160.00f,  160.00f};
        numberOfVertices = 42;
        textureFilename = "textures/krait.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.500f;
        maxRollSpeed      = 2.750f;        
        hullStrength      = 180.0f;
        hasEcm            = false;
        cargoType         = 12;   
        aggressionLevel   = 10;
        escapeCapsuleCaps = 0;
        bounty            = 100;
        score             = 180;
        legalityType      = 1;
        maxCargoCanisters = 1;        
        laserHardpoints.add(VERTEX_DATA[12]);
        laserHardpoints.add(VERTEX_DATA[13]);
        laserHardpoints.add(VERTEX_DATA[14]);
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserColor = 0x7F00FF00l;
        laserTexture = "textures/laser_green.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                1,   0,   2,   2,   0,   3,   2,   3,   1,   3,   0,   1,   5,   4,   7, 
                5,   7,   6,   6,   4,   5,   7,   4,   6,   9,   8,  11,  10,   8,   9, 
               10,   9,  11,  11,   8,  12,  11,  12,  10,  12,   8,  10);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, -30, 0, -20, 1.0f, 0.67f, 0.0f, 0.7f));
        	addExhaust(new EngineExhaust(this, 13, 13, 30, 30, 0, -20, 1.0f, 0.67f, 0.0f, 0.7f));
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
