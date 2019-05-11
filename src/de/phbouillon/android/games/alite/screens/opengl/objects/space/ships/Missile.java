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
 * Missile model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import java.io.IOException;
import java.io.ObjectOutputStream;

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Missile extends SpaceObject {
	private static final long serialVersionUID = -2952821469349138597L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.0f);
    private SpaceObject target;
    private SpaceObject source;
    
    private static final float [] VERTEX_DATA = new float [] {
          11.20f,  -11.20f,  -56.00f,    0.00f,   -0.00f, -112.00f,
          11.20f,   11.20f,  -56.00f,  -11.20f,   11.20f,  -56.00f,
         -11.20f,  -11.20f,  -56.00f,   11.20f,  -11.20f,   45.44f,
          19.28f,  -19.28f,  112.00f,    5.60f,  -11.20f,   95.84f,
          11.20f,   11.20f,   45.44f,   -5.60f,  -11.20f,   95.84f,
         -11.20f,  -11.20f,   45.44f,   11.20f,   -5.60f,   95.84f,
          11.20f,    5.60f,   95.84f,   19.28f,   19.28f,  112.00f,
           5.60f,   11.20f,   95.84f,   -5.60f,   11.20f,   95.84f,
         -11.20f,    5.60f,   95.84f,  -11.20f,   11.20f,   45.44f,
         -19.28f,   19.28f,  112.00f,  -11.20f,   -5.60f,   95.84f,
         -19.28f,  -19.28f,  112.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.55015f,  -0.82793f,  -0.10895f,   0.00000f,   0.00000f,  -1.00000f, 
          0.82793f,   0.55015f,  -0.10895f,  -0.55015f,   0.82793f,  -0.10895f, 
         -0.82793f,  -0.55015f,  -0.10895f,   0.70695f,  -0.70695f,  -0.02121f, 
         -0.14854f,   0.14851f,   0.97769f,  -0.31134f,  -0.43875f,   0.84295f, 
          0.70695f,   0.70695f,  -0.02121f,   0.32012f,  -0.77966f,   0.53820f, 
         -0.70695f,  -0.70695f,  -0.02122f,   0.53308f,   0.21888f,   0.81726f, 
          0.34252f,  -0.24306f,   0.90753f,  -0.14855f,  -0.14851f,   0.97769f, 
         -0.32013f,   0.77966f,   0.53820f,   0.31134f,   0.43874f,   0.84296f, 
         -0.53309f,  -0.21888f,   0.81725f,  -0.70695f,   0.70695f,  -0.02122f, 
          0.14848f,  -0.14851f,   0.97770f,  -0.34252f,   0.24306f,   0.90752f, 
          0.14847f,   0.14852f,   0.97770f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.49f,   0.29f,   0.76f,   0.24f,   0.49f,   0.19f, 
          0.49f,   0.61f,   0.76f,   0.56f,   0.49f,   0.51f, 
          0.49f,   0.51f,   0.76f,   0.45f,   0.49f,   0.40f, 
          0.76f,   0.35f,   0.49f,   0.29f,   0.49f,   0.40f, 
          0.49f,   0.95f,   0.59f,   0.64f,   0.50f,   0.71f, 
          0.00f,   0.29f,   0.49f,   0.29f,   0.49f,   0.19f, 
          0.00f,   0.29f,   0.49f,   0.19f,   0.00f,   0.19f, 
          0.49f,   0.95f,   0.50f,   0.71f,   0.45f,   0.70f, 
          0.49f,   0.95f,   0.45f,   0.70f,   0.38f,   0.94f, 
          0.62f,   0.75f,   0.59f,   0.64f,   0.49f,   0.95f, 
          0.86f,   0.83f,   0.77f,   0.77f,   0.83f,   0.86f, 
          0.83f,   0.91f,   0.77f,   1.00f,   0.86f,   0.94f, 
          0.83f,   0.91f,   0.86f,   0.94f,   0.91f,   0.94f, 
          0.83f,   0.91f,   0.91f,   0.94f,   0.94f,   0.91f, 
          0.59f,   1.00f,   0.77f,   0.73f,   0.67f,   0.77f, 
          0.00f,   0.61f,   0.49f,   0.61f,   0.49f,   0.51f, 
          0.00f,   0.61f,   0.49f,   0.51f,   0.00f,   0.51f, 
          0.59f,   1.00f,   0.67f,   0.77f,   0.62f,   0.75f, 
          0.59f,   1.00f,   0.62f,   0.75f,   0.49f,   0.95f, 
          0.10f,   0.77f,   0.00f,   0.73f,   0.18f,   1.00f, 
          0.91f,   0.94f,   1.00f,   1.00f,   0.94f,   0.91f, 
          0.28f,   0.95f,   0.18f,   0.64f,   0.15f,   0.75f, 
          0.00f,   0.51f,   0.49f,   0.51f,   0.49f,   0.40f, 
          0.00f,   0.51f,   0.49f,   0.40f,   0.00f,   0.40f, 
          0.28f,   0.95f,   0.15f,   0.75f,   0.10f,   0.77f, 
          0.28f,   0.95f,   0.10f,   0.77f,   0.18f,   1.00f, 
          0.27f,   0.71f,   0.18f,   0.64f,   0.28f,   0.95f, 
          0.94f,   0.91f,   0.94f,   0.86f,   0.83f,   0.86f, 
          0.94f,   0.91f,   0.83f,   0.86f,   0.83f,   0.91f, 
          0.94f,   0.86f,   1.00f,   0.77f,   0.91f,   0.83f, 
          0.94f,   0.86f,   0.91f,   0.83f,   0.86f,   0.83f, 
          0.94f,   0.86f,   0.86f,   0.83f,   0.83f,   0.86f, 
          0.38f,   0.94f,   0.38f,   0.61f,   0.32f,   0.70f, 
          0.49f,   0.40f,   0.49f,   0.29f,   0.00f,   0.40f, 
          0.00f,   0.40f,   0.49f,   0.29f,   0.00f,   0.29f, 
          0.38f,   0.94f,   0.32f,   0.70f,   0.27f,   0.71f, 
          0.38f,   0.94f,   0.27f,   0.71f,   0.28f,   0.95f, 
          0.38f,   0.94f,   0.45f,   0.70f,   0.38f,   0.61f
    };

    private boolean ecmDestroy = false;
    
    public Missile(Alite alite) {
        super(alite, "Missile", ObjectType.Missile);
        shipType = ShipType.Missile;
        boundingBox = new float [] { -19.28f,   19.28f,  -19.28f,   19.28f, -112.00f,  112.00f};
        numberOfVertices = 114;
        textureFilename = "textures/missile.png";
        maxSpeed     = 701.4f;
        maxPitchSpeed = 12.000f;
        maxRollSpeed  = 12.000f;
        hullStrength =   1.0f;
        hasEcm       = false;
        cargoType    = 0;
        bounty       = 0;
        score        = 0;
        init();
    }

	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Missile " + getName(), e);
			throw(e);
		}
    }

    @Override
    protected void init() {
        vertexBuffer = createReversedFaces(VERTEX_DATA, NORMAL_DATA,
                0,   1,   2,   2,   1,   3,   3,   1,   4,   1,   0,   4,   5,   6,   7, 
                5,   0,   2,   5,   2,   8,   5,   7,   9,   5,   9,  10,  11,   6,   5, 
                7,   6,  11,  12,  13,  14,  12,  14,  15,  12,  15,  16,   8,  13,  12, 
                8,   2,   3,   8,   3,  17,   8,  12,  11,   8,  11,   5,  14,  13,   8, 
               15,  18,  16,  17,  18,  15,  17,   3,   4,  17,   4,  10,  17,  15,  14, 
               17,  14,   8,  16,  18,  17,  16,  19,  11,  16,  11,  12,  19,  20,   9, 
               19,   9,   7,  19,   7,  11,  10,  20,  19,   4,   0,  10,  10,   0,   5, 
               10,  19,  16,  10,  16,  17,  10,   9,  20);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);  
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, 0, 0, -15));
        }
        initTargetBox();
    }

    @Override
	protected boolean receivesProximityWarning() {
		return false;
	}

    public void setTarget(SpaceObject target) {
    	this.target = target;
    }
    
    public SpaceObject getTarget() {
    	return target;
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
    
    public void setWillBeDestroyedByECM(boolean b) {
    	ecmDestroy = b;
    }
    
    public boolean getWillBeDestroyedByECM() {
    	return ecmDestroy;
    }
    
    public void setSource(SpaceObject so) {
    	this.source = so;
    }
    
    public SpaceObject getSource() {
    	return source;
    }
}
