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
 * Viper model from Oolite: http://oolite.org
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

public class Viper extends SpaceObject {
	private static final long serialVersionUID = -369557815673890708L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.33f, 0.33f, 0.53f);

    private static final float [] VERTEX_DATA = new float [] {
         -43.64f,   27.92f,  -96.00f,   43.64f,   27.92f,  -96.00f,
           0.00f,   27.92f,    0.00f,   87.28f,    0.00f,  -96.00f,
           0.00f,    0.00f,   96.00f,  -87.28f,    0.00f,  -96.00f,
           0.00f,  -27.92f,    0.00f,   43.64f,  -27.92f,  -96.00f,
         -43.64f,  -27.92f,  -96.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.20251f,  -0.70321f,   0.68153f,  -0.20251f,  -0.70321f,   0.68153f, 
          0.00000f,  -0.97606f,  -0.21748f,  -0.99974f,   0.00000f,   0.02294f, 
          0.34086f,   0.53260f,  -0.77469f,   0.71765f,  -0.37378f,   0.58759f, 
         -0.14683f,   0.96869f,  -0.20022f,  -0.12433f,   0.43175f,   0.89338f, 
          0.36302f,   0.91390f,   0.18167f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.28f,   0.78f,   0.38f,   0.57f,   0.18f,   0.57f, 
          0.28f,   0.78f,   0.18f,   0.57f,   0.06f,   0.58f, 
          0.28f,   0.78f,   0.06f,   0.58f,   0.28f,   1.00f, 
          0.50f,   0.58f,   0.38f,   0.57f,   0.28f,   0.78f, 
          0.50f,   0.58f,   0.28f,   0.78f,   0.28f,   1.00f, 
          0.28f,   0.23f,   0.28f,   0.01f,   0.06f,   0.42f, 
          0.28f,   0.23f,   0.06f,   0.42f,   0.18f,   0.44f, 
          0.18f,   0.44f,   0.08f,   0.50f,   0.18f,   0.57f, 
          0.18f,   0.44f,   0.18f,   0.57f,   0.38f,   0.57f, 
          0.18f,   0.44f,   0.38f,   0.57f,   0.48f,   0.50f, 
          0.18f,   0.44f,   0.48f,   0.50f,   0.38f,   0.44f, 
          0.18f,   0.44f,   0.38f,   0.44f,   0.28f,   0.23f, 
          0.38f,   0.44f,   0.50f,   0.42f,   0.28f,   0.01f, 
          0.38f,   0.44f,   0.28f,   0.01f,   0.28f,   0.23f
    };

    public Viper(Alite alite) {
        super(alite, "Viper", ObjectType.Viper);
        shipType = ShipType.Viper;
        boundingBox = new float [] { -87.28f,   87.28f,  -27.92f,   27.92f,  -96.00f,   96.00f};
        numberOfVertices = 42;
        textureFilename = "textures/viper.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.800f;
        maxRollSpeed      = 2.800f;        
        hullStrength      = 120.0f;
        hasEcm            = false;
        cargoType         = 0;
        aggressionLevel   = 10;
        escapeCapsuleCaps = 0;
        bounty            = 0;
        score             = 120;
        legalityType      = 4;
        maxCargoCanisters = 0;
        laserHardpoints.add(VERTEX_DATA[12]);
        laserHardpoints.add(VERTEX_DATA[13]);
        laserHardpoints.add(VERTEX_DATA[14]);
        laserColor = 0x7F0000FFl;
        laserTexture = "textures/laser_blue.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                2,   0,   1,   2,   1,   3,   2,   3,   4,   5,   0,   2,   5,   2,   4, 
                6,   4,   3,   6,   3,   7,   7,   3,   1,   7,   1,   0,   7,   0,   5, 
                7,   5,   8,   7,   8,   6,   8,   5,   4,   8,   4,   6);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);   
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 10, 10, 30, -40, 0, 0, 0.97f, 0.66f, 0.0f, 0.7f));
        	addExhaust(new EngineExhaust(this, 10, 10, 30,  40, 0, 0, 0.97f, 0.66f, 0.0f, 0.7f));
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
