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
 * Cat model and texture by Murgh (from Oolite)
 * Renamed to Cougar for Alite.
 */

import java.io.Serializable;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.MathHelper;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Cougar extends SpaceObject implements Serializable {
	private static final long serialVersionUID = 2806205530844502523L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.55f, 0.55f);
    private final CougarWing wing;
    private final float [] tempMatrix = new float[16];
    
    private static final float [] VERTEX_DATA = new float [] {
          0.00f,   30.43f,  198.00f,   
          0.00f,   99.00f, -198.00f, 
       -132.00f,    0.00f, -198.00f,   
          0.00f,  -99.00f, -198.00f, 
        132.00f,    0.00f, -198.00f, 
        -66.00f,    0.00f,   99.00f, 
         66.00f,    0.00f,   99.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.57907f,  -0.80333f,  -0.13909f,  -0.02771f,   0.95015f,  -0.31056f, 
          0.59474f,  -0.79298f,  -0.13216f,  -0.57907f,  -0.80333f,  -0.13909f, 
         -0.00000f,   0.00000f,   1.00000f,   0.59474f,   0.79298f,  -0.13216f, 
          0.00000f,   0.00000f,   1.00000f,   0.02771f,   0.95015f,  -0.31056f, 
         -0.59474f,   0.79298f,  -0.13216f,  -0.59474f,  -0.79298f,  -0.13216f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.25f,   0.25f,   0.13f,   0.42f,   0.25f,   0.99f, 
          0.54f,   0.78f,   0.66f,   0.58f,   0.54f,   0.00f, 
          0.25f,   0.99f,   0.13f,   0.42f,   0.00f,   0.96f, 
          0.25f,   0.99f,   0.37f,   0.42f,   0.25f,   0.25f, 
          1.00f,   0.81f,   0.75f,   0.63f,   0.75f,   0.99f, 
          0.30f,   0.05f,   0.42f,   0.58f,   0.54f,   0.00f, 
          0.75f,   0.63f,   0.51f,   0.81f,   0.75f,   0.99f, 
          0.54f,   0.00f,   0.42f,   0.58f,   0.54f,   0.78f, 
          0.54f,   0.00f,   0.66f,   0.58f,   0.79f,   0.05f, 
          0.50f,   0.96f,   0.37f,   0.42f,   0.25f,   0.99f
    };
    
    public Cougar(Alite alite) {
        super(alite, "Cougar", ObjectType.EnemyShip);
        shipType = ShipType.Cougar;
        boundingBox = new float [] {-198.00f, 198.00f, -99.00f,  99.00f, -198.00f, 198.00f};
        numberOfVertices = 30;
        textureFilename = "textures/cougar.png";
        affectedByEnergyBomb = false;
        maxSpeed          = 501.0f;
        maxPitchSpeed     = 2.250f;
        maxRollSpeed      = 2.250f;
        hullStrength      = 1024.0f;
        hasEcm            = true;
        cargoType         = 0;
        aggressionLevel   = 25;
        escapeCapsuleCaps = 0;
        bounty            = 5000;
        score             = 1000;
        legalityType      = 1;
        maxCargoCanisters = 0;                
        wing = new CougarWing(alite);
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserColor = 0x7F00FFFFl;
        laserTexture = "textures/laser_cyan.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   5,   1,   0,   6,   3,   1,   5,   2,   1,   6,   0,   2,   3,   1, 
                2,   5,   3,   3,   4,   1,   3,   5,   0,   3,   6,   4,   4,   6,   1);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename); 
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 40, -30, 0, 0, 0.32f, 0.95f, 0.14f, 0.7f));
        	addExhaust(new EngineExhaust(this, 13, 13, 40,  30, 0, 0, 0.32f, 0.95f, 0.14f, 0.7f));
        }
        initTargetBox();
    }

    @Override
    public boolean isVisibleOnHud() {
        return !cloaked;
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
	public void render() {		
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);		
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, numberOfVertices);
		wing.render();
		wing.applyDeltaRotation(0, 0, 180);
		GLES11.glMultMatrixf(wing.getMatrix(), 0);
		wing.render();
		wing.applyDeltaRotation(0, 0, 180);		
		alite.getTextureManager().setTexture(null);
		if (Settings.engineExhaust && !exhaust.isEmpty()) {
			for (EngineExhaust ex: exhaust) {
				ex.render();
			}
		}
	}
	
	@Override
	public boolean intersect(Vector3f origin, Vector3f direction, float scaleFactor) {
		boolean hit = super.intersect(origin, direction, scaleFactor);
		if (!hit) {
			MathHelper.copyMatrix(wing.getMatrix(), tempMatrix);
			wing.setMatrix(getMatrix());
			hit = wing.intersect(origin, direction, scaleFactor);
			wing.setMatrix(tempMatrix);
		}
		if (!hit) {
			MathHelper.copyMatrix(wing.getMatrix(), tempMatrix);
			wing.setMatrix(getMatrix());			
			wing.applyDeltaRotation(0, 0, 180);
			hit = wing.intersect(origin, direction, scaleFactor);
			wing.setMatrix(tempMatrix);			
		}
		return hit;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		wing.dispose();
	}
}
