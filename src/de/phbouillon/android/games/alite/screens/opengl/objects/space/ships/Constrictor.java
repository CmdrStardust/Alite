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
 * Constrictor model and texture from Oolite: http://oolite.org
 */

import java.io.IOException;
import java.io.ObjectOutputStream;

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.missions.ConstrictorMission;
import de.phbouillon.android.games.alite.model.missions.MissionManager;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.DestructionCallback;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Constrictor extends SpaceObject {
	private static final long serialVersionUID = -8304383398110675254L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.55f, 0.55f);

    private static final float [] VERTEX_DATA = new float [] {
          40.00f,  -20.00f,  120.00f,  -40.00f,  -20.00f,  120.00f,
         -40.00f,   20.00f,  -30.00f,   40.00f,   20.00f,  -30.00f,
         108.00f,  -20.00f,   40.00f, -108.00f,  -20.00f,   40.00f,
        -108.00f,  -20.00f, -120.00f,  108.00f,  -20.00f, -120.00f,
          40.00f,   20.00f, -120.00f,  -40.00f,   20.00f, -120.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.24523f,  -0.72459f,  -0.64407f,   0.49013f,   0.18430f,  -0.85194f, 
          0.19314f,  -0.97466f,  -0.11284f,  -0.27046f,  -0.95704f,  -0.10457f, 
         -0.69513f,  -0.68597f,  -0.21503f,   0.92555f,   0.24776f,  -0.28631f, 
          0.63474f,  -0.45310f,   0.62595f,  -0.31737f,   0.71237f,   0.62595f, 
         -0.16495f,  -0.93110f,   0.32534f,   0.18243f,  -0.66996f,   0.71964f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.25f,   0.42f,   0.54f,   0.42f,   0.39f,   0.29f, 
          0.25f,   0.42f,   0.25f,   0.57f,   0.54f,   0.57f, 
          0.25f,   0.42f,   0.54f,   0.57f,   0.54f,   0.42f, 
          0.39f,   0.71f,   0.54f,   0.57f,   0.25f,   0.57f, 
          0.09f,   0.29f,   0.09f,   0.42f,   0.25f,   0.42f, 
          0.09f,   0.29f,   0.25f,   0.42f,   0.39f,   0.29f, 
          0.09f,   0.71f,   0.39f,   0.71f,   0.25f,   0.57f, 
          0.09f,   0.71f,   0.25f,   0.57f,   0.09f,   0.57f, 
          1.00f,   0.71f,   1.00f,   0.29f,   0.69f,   0.29f, 
          1.00f,   0.71f,   0.69f,   0.29f,   0.54f,   0.42f, 
          1.00f,   0.71f,   0.54f,   0.42f,   0.54f,   0.57f, 
          1.00f,   0.71f,   0.54f,   0.57f,   0.69f,   0.71f, 
          0.01f,   0.71f,   0.09f,   0.57f,   0.09f,   0.42f, 
          0.01f,   0.71f,   0.09f,   0.42f,   0.01f,   0.29f, 
          0.09f,   0.57f,   0.25f,   0.57f,   0.25f,   0.42f, 
          0.09f,   0.57f,   0.25f,   0.42f,   0.09f,   0.42f
    };

    public Constrictor(Alite alite) {
        super(alite, "Constrictor", ObjectType.EnemyShip);
        shipType = ShipType.Constrictor;
        boundingBox = new float [] {-108.00f,  108.00f,  -20.00f,   20.00f, -120.00f,  120.00f};
        numberOfVertices = 48;
        textureFilename = "textures/constrictor.png";
        affectedByEnergyBomb = false;
        maxSpeed             = 501.0f;
        maxPitchSpeed        = 1.750f;
        maxRollSpeed         = 2.500f;
        hullStrength         = 1024.0f;
        hasEcm               = true;
        cargoType            = 7;   
        aggressionLevel      = 25;
        escapeCapsuleCaps    = 0;
        bounty               = 4000;
        score                = 5000;
        legalityType         = 7;
        maxCargoCanisters    = 2;        
        laserHardpoints.add(VERTEX_DATA[0]);
        laserHardpoints.add(VERTEX_DATA[1]);
        laserHardpoints.add(VERTEX_DATA[2]);
        laserHardpoints.add(VERTEX_DATA[3]);
        laserHardpoints.add(VERTEX_DATA[4]);
        laserHardpoints.add(VERTEX_DATA[5]);
        init();
        laserColor = 0x7FFF00FFl;
        laserTexture = "textures/laser_purple.png";
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                2,   1,   5,   2,   3,   0,   2,   0,   1,   4,   0,   3,   6,   9,   2, 
                6,   2,   5,   7,   4,   3,   7,   3,   8,   7,   6,   5,   7,   5,   1, 
                7,   1,   0,   7,   0,   4,   7,   8,   9,   7,   9,   6,   8,   3,   2, 
                8,   2,   9);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);  
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 25, 8, 30, 0, 0, 0, 0.92f, 0.60f, 0.8f, 0.7f));
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
    
	private void writeObject(ObjectOutputStream out) throws IOException {
		destructionCallbacks.clear();
		out.defaultWriteObject();
    }

    @Override
    public void update(float deltaTime) {    	
		if (!hasDestructionCallback(12)) {
			addDestructionCallback(new DestructionCallback(){
				private static final long serialVersionUID = -7308534203291478174L;

				@Override
				public void onDestruction() {
					((ConstrictorMission) MissionManager.getInstance().get(ConstrictorMission.ID)).setState(7);
					MissionManager.getInstance().get(ConstrictorMission.ID).resetTargetName();				
				}

				@Override
				public int getId() {
					return 12;
				}
			});
		}    	
		super.update(deltaTime);
    }
}
