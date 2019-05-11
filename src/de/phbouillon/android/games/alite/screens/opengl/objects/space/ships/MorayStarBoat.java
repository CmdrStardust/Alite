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
 * Moray Star Boat model from Oolite: http://oolite.org
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

public class MorayStarBoat extends SpaceObject {
	private static final long serialVersionUID = -6581240299998705777L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         -27.70f,   -7.38f, -120.00f,   -0.00f,  -46.16f,  120.00f,
          27.70f,   -7.38f, -120.00f,  110.76f,   -7.38f,   23.08f,
        -110.76f,   -7.38f,   23.08f,   55.38f,   46.16f,   64.62f,
         -55.38f,   46.16f,   64.62f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.64293f,   0.17327f,   0.74607f,   0.00000f,   0.75477f,  -0.65599f, 
         -0.47992f,  -0.48079f,   0.73384f,  -0.97155f,   0.07255f,  -0.22546f, 
          0.97155f,   0.07255f,  -0.22546f,  -0.41745f,  -0.83103f,  -0.36760f, 
          0.32783f,  -0.92090f,  -0.21089f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.99f,   0.75f,   0.55f,   0.80f,   0.81f,   0.96f, 
          0.55f,   0.70f,   0.55f,   0.80f,   0.99f,   0.75f, 
          0.55f,   0.70f,   0.99f,   0.75f,   0.81f,   0.54f, 
          0.13f,   0.50f,   0.00f,   0.75f,   0.20f,   0.65f, 
          0.30f,   0.53f,   0.20f,   0.65f,   0.55f,   0.70f, 
          0.30f,   0.97f,   0.55f,   0.80f,   0.20f,   0.85f, 
          0.20f,   0.65f,   0.00f,   0.75f,   0.20f,   0.85f, 
          0.20f,   0.85f,   0.55f,   0.80f,   0.55f,   0.70f, 
          0.20f,   0.85f,   0.55f,   0.70f,   0.20f,   0.65f, 
          0.20f,   0.85f,   0.00f,   0.75f,   0.13f,   1.00f
    };

    public MorayStarBoat(Alite alite) {
        super(alite, "Moray Star Boat", ObjectType.EnemyShip);
        shipType = ShipType.MorayStarBoat;
        boundingBox = new float [] {-110.76f,  110.76f,  -46.16f,   46.16f, -120.00f,  120.00f};
        numberOfVertices = 30;
        textureFilename = "textures/moraystarboat.png";
        maxSpeed          = 300.6f;
        maxPitchSpeed     = 1.500f;
        maxRollSpeed      = 2.500f;        
        hullStrength      = 96.0f;
        hasEcm            = false;
        cargoType         = 8;
        aggressionLevel   = 10;
        escapeCapsuleCaps = 2;
        bounty            = 80;
        score             = 100;
        legalityType      = 1;
        maxCargoCanisters = 1;        
        laserHardpoints.add(VERTEX_DATA[3]);
        laserHardpoints.add(VERTEX_DATA[4]);
        laserHardpoints.add(VERTEX_DATA[5]);
        laserColor = 0x7F0000FFl;
        laserTexture = "textures/laser_blue.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                1,   0,   4,   2,   0,   1,   2,   1,   3,   3,   1,   5,   3,   5,   2, 
                4,   0,   6,   5,   1,   6,   6,   0,   2,   6,   2,   5,   6,   1,   4);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename); 
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 19, 4, 30, 0, -5, 0));
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
