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
 * Cobra Mk III model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import java.io.IOException;
import java.io.ObjectOutputStream;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class CobraMkIII extends SpaceObject {
	private static final long serialVersionUID = -7600323198292178192L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.94f, 0.0f);
	private boolean playerCobra = false;
	private static final float [] lightAmbient   = new float [] {0.0f, 1.0f, 0.0f, 1.0f};
	private static final float [] lightSpecular  = new float [] {0.0f, 1.0f, 0.0f, 1.0f};
	private static final float [] lightPosition  = new float [] {0.0f, 41.54f, -90.0f, 1.0f};
	
    private static final float [] VERTEX_DATA = new float [] {
           0.00f,   41.54f,    0.00f,   44.30f,   -1.38f,   90.00f,
         -44.30f,   -1.38f,   90.00f,   44.30f,  -41.54f,  -90.00f,
         -44.30f,  -41.54f,  -90.00f, -121.84f,   27.70f,  -90.00f,
        -166.16f,   -8.30f,  -36.00f, -180.00f,   -8.30f,  -90.00f,
         121.84f,   27.70f,  -90.00f,  166.16f,   -8.30f,  -36.00f,
         180.00f,   -8.30f,  -90.00f,    0.00f,   41.54f,  -90.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.00000f,  -0.97853f,  -0.20610f,  -0.44677f,   0.11657f,  -0.88702f, 
          0.45650f,  -0.41442f,  -0.78732f,  -0.12265f,   0.88117f,   0.45661f, 
          0.09842f,   0.94448f,   0.31347f,   0.32028f,  -0.89272f,   0.31696f, 
          0.86781f,   0.13768f,  -0.47744f,   0.38677f,   0.06465f,   0.91991f, 
         -0.22579f,  -0.62936f,   0.74359f,  -0.86781f,   0.13768f,  -0.47744f, 
         -0.68141f,   0.11389f,   0.72299f,   0.00000f,  -0.89327f,   0.44951f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.60f,   0.40f,   0.81f,   0.50f,   0.99f,   0.24f, 
          0.60f,   0.60f,   0.81f,   0.50f,   0.60f,   0.40f, 
          0.19f,   0.40f,   0.60f,   0.40f,   0.32f,   0.13f, 
          0.19f,   0.60f,   0.60f,   0.60f,   0.60f,   0.40f, 
          0.19f,   0.60f,   0.60f,   0.40f,   0.19f,   0.40f, 
          0.19f,   0.60f,   0.20f,   0.91f,   0.32f,   0.87f, 
          0.99f,   0.77f,   0.81f,   0.50f,   0.60f,   0.60f, 
          0.99f,   0.77f,   0.60f,   0.60f,   0.86f,   0.87f, 
          0.32f,   0.87f,   0.60f,   0.60f,   0.19f,   0.60f, 
          0.97f,   0.91f,   0.99f,   0.77f,   0.86f,   0.87f, 
          0.99f,   0.24f,   0.81f,   0.50f,   1.00f,   0.50f, 
          0.03f,   0.23f,   0.00f,   0.50f,   0.03f,   0.77f, 
          0.03f,   0.23f,   0.03f,   0.77f,   0.11f,   0.90f, 
          0.03f,   0.23f,   0.11f,   0.90f,   0.19f,   0.60f, 
          0.03f,   0.23f,   0.19f,   0.60f,   0.19f,   0.40f, 
          0.03f,   0.23f,   0.19f,   0.40f,   0.11f,   0.10f, 
          0.86f,   0.13f,   0.60f,   0.40f,   0.99f,   0.24f, 
          0.86f,   0.13f,   0.99f,   0.24f,   0.97f,   0.09f, 
          0.32f,   0.13f,   0.20f,   0.09f,   0.19f,   0.40f, 
          1.00f,   0.50f,   0.81f,   0.50f,   0.99f,   0.77f
    };

    public CobraMkIII(Alite alite) {
        super(alite, "Cobra Mk III", ObjectType.Trader);
        shipType = ShipType.CobraMkIII;
        boundingBox = new float [] {-180.00f,  180.00f,  -41.54f,   41.54f,  -90.00f,   90.00f};
        numberOfVertices = 60;
        textureFilename = "textures/cobramkiii.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 1.000f;
        maxRollSpeed      = 2.000f;
        hullStrength      =  72.0f;
        hasEcm            = true;
        cargoType         = 7; 
        aggressionLevel   = 10;
        escapeCapsuleCaps = 3;
        bounty            = 40;
        score             = 70;
        legalityType      = 0;
        maxCargoCanisters = 3;        
        laserHardpoints.add(VERTEX_DATA[3]);
        laserHardpoints.add(VERTEX_DATA[4]);
        laserHardpoints.add(VERTEX_DATA[5]);
        laserHardpoints.add(VERTEX_DATA[6]);
        laserHardpoints.add(VERTEX_DATA[7]);
        laserHardpoints.add(VERTEX_DATA[8]);
        init();
    }
    
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Cobra Mk III " + getName(), e);
			throw(e);
		}
    }

    @Override
    protected void init() {
        GLES11.glLightfv(GLES11.GL_LIGHT3, GLES11.GL_AMBIENT, lightAmbient, 0);
        GLES11.glLightfv(GLES11.GL_LIGHT3, GLES11.GL_DIFFUSE, lightAmbient, 0);
        GLES11.glLightfv(GLES11.GL_LIGHT3, GLES11.GL_SPECULAR, lightSpecular, 0);
        GLES11.glLightfv(GLES11.GL_LIGHT3, GLES11.GL_POSITION, lightPosition, 0);
        
        GLES11.glLightf(GLES11.GL_LIGHT3, GLES11.GL_SPOT_CUTOFF, 35.0f);
        GLES11.glLightf(GLES11.GL_LIGHT3, GLES11.GL_SPOT_EXPONENT, 100.0f);
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                1,   0,   8,   2,   0,   1,   3,   1,   9,   4,   2,   1,   4,   1,   3, 
                4,   7,   6,   5,   0,   2,   5,   2,   6,   6,   2,   4,   7,   5,   6, 
                8,   0,  11,   8,  11,   5,   8,   5,   7,   8,   7,   4,   8,   4,   3, 
                8,   3,  10,   9,   1,   8,   9,   8,  10,   9,  10,   3,  11,   0,   5);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, -50, 0, 0));
        	addExhaust(new EngineExhaust(this, 13, 13, 30,  50, 0, 0));
        	addExhaust(new EngineExhaust(this,  5,  5, 18, -115, 0, 0, 1.0f, 0.5f, 0.0f, 0.7f));
        	addExhaust(new EngineExhaust(this,  5,  5, 18,  115, 0, 0, 1.0f, 0.5f, 0.0f, 0.7f));
        }
        initTargetBox();
    }

    @Override
    public void hasBeenHitByPlayer() {
    	computeLegalStatusAfterFriendlyHit();	
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
    
    public void setPlayerCobra(boolean b) {
    	this.playerCobra = b;
    	if (b) {
    		exhaust = null;
    	}
    }
    
    public boolean isPlayerCobra() {
    	return playerCobra;
    }
}
