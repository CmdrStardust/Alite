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
 * Gecko model from Oolite: http://oolite.org
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

public class Gecko extends SpaceObject {
	private static final long serialVersionUID = 2155308128070729813L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -32.50f,   24.36f,  -81.24f,   32.50f,   24.36f,  -81.24f,
          20.30f,   -2.04f,   81.24f,  -20.30f,   -2.04f,   81.24f,
        -132.00f,    6.10f,  -30.46f,  -40.62f,  -24.36f,  -81.24f,
          40.62f,  -24.36f,  -81.24f,  132.00f,    6.10f,  -30.46f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.16851f,  -0.61607f,   0.76946f,  -0.16532f,  -0.89602f,   0.41208f, 
         -0.25520f,   0.76178f,  -0.59546f,   0.24759f,  -0.76381f,  -0.59607f, 
          0.81410f,  -0.10212f,   0.57167f,   0.21162f,   0.87206f,   0.44128f, 
         -0.21270f,   0.57327f,   0.79128f,  -0.81410f,  -0.10212f,   0.57167f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.55f,   0.00f,   0.58f,   0.44f,   0.85f,   0.30f, 
          0.45f,   0.00f,   0.41f,   0.44f,   0.58f,   0.44f, 
          0.45f,   0.00f,   0.58f,   0.44f,   0.55f,   0.00f, 
          0.15f,   0.30f,   0.41f,   0.44f,   0.45f,   0.00f, 
          0.14f,   0.71f,   0.44f,   1.00f,   0.39f,   0.57f, 
          0.39f,   0.57f,   0.41f,   0.44f,   0.12f,   0.48f, 
          0.39f,   0.57f,   0.44f,   1.00f,   0.55f,   1.00f, 
          0.39f,   0.57f,   0.55f,   1.00f,   0.61f,   0.57f, 
          0.61f,   0.57f,   0.58f,   0.44f,   0.41f,   0.44f, 
          0.61f,   0.57f,   0.41f,   0.44f,   0.39f,   0.57f, 
          0.61f,   0.57f,   0.55f,   1.00f,   0.85f,   0.71f, 
          0.88f,   0.48f,   0.58f,   0.44f,   0.61f,   0.57f
    };

    public Gecko(Alite alite) {
        super(alite, "Gecko", ObjectType.EnemyShip);
        shipType = ShipType.Gecko;
        boundingBox = new float [] {-132.00f,  132.00f,  -24.36f,   24.36f,  -81.24f,   81.24f};
        numberOfVertices = 36;
        textureFilename = "textures/gecko.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.500f;
        maxRollSpeed      = 3.000f;        
        hullStrength      = 190.0f;
        hasEcm            = true;
        cargoType         = 4; 
        aggressionLevel   = 15;
        escapeCapsuleCaps = 2;
        bounty            = 120;
        score             = 190;
        legalityType      = 1;
        maxCargoCanisters = 2;        
        laserHardpoints.add(VERTEX_DATA[6]);
        laserHardpoints.add(VERTEX_DATA[7]);
        laserHardpoints.add(VERTEX_DATA[8]);
        laserHardpoints.add(VERTEX_DATA[9]);
        laserHardpoints.add(VERTEX_DATA[10]);
        laserHardpoints.add(VERTEX_DATA[11]);
        laserColor = 0x7F00FF00l;
        laserTexture = "textures/laser_green.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                2,   1,   7,   3,   0,   1,   3,   1,   2,   4,   0,   3,   4,   3,   5, 
                5,   0,   4,   5,   3,   2,   5,   2,   6,   6,   1,   0,   6,   0,   5, 
                6,   2,   7,   7,   1,   6);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);  
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, 0, 0, 0, 0.81f, 0.38f, 0.71f, 0.7f));
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
