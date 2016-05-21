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
 * Coriolis station model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;

public class Coriolis extends SpaceObject implements SpaceStation {
	private static final long serialVersionUID = -7194908031739710169L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);
	private boolean accessAllowed = true;
	
    private static final float [] VERTEX_DATA = new float [] {
        160.00f,   0.00f, 160.00f,   0.00f, 160.00f, 160.00f, 
        -160.00f,   0.00f, 160.00f,   0.00f, -160.00f, 160.00f, 
        160.00f, -160.00f,   0.00f, 160.00f, 160.00f,   0.00f, 
        -160.00f, 160.00f,   0.00f, -160.00f, -160.00f,   0.00f, 
        160.00f,   0.00f, -160.00f,   0.00f, 160.00f, -160.00f, 
        -160.00f,   0.00f, -160.00f,   0.00f, -160.00f, -160.00f, 
         10.24f, -30.72f, 160.00f,  10.24f,  30.72f, 160.00f, 
        -10.24f,  30.72f, 160.00f, -10.24f, -30.72f, 160.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
         -0.57735f,   0.57735f,  -0.57735f,  -0.57735f,   0.57735f,   0.57735f, 
          0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f,   0.00000f, 
          0.57735f,   0.57735f,  -0.57735f,   0.57735f,   0.57735f,   0.57735f, 
          0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f, 
         -0.57735f,  -0.57735f,  -0.57735f,  -0.57735f,  -0.57735f,   0.57735f, 
         -0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f, 
          0.57735f,  -0.57735f,  -0.57735f,   0.57735f,  -0.57735f,   0.57735f, 
         -0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,  -0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
         -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f, 
          1.00000f,   0.00000f,  -0.00000f,   1.00000f,   0.00000f,   0.00000f
    };
		
	private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
		0.5f, 0.0f, 0.5f, 0.25f, 0.75f, 0.0f,
		0.75f, 0.5f, 0.5f, 0.25f, 0.5f, 0.5f,
		0.25f, 0.5f, 0.25f, 0.0f, 0.0f, 0.25f,
		0.25f, 0.5f, 0.5f, 0.25f, 0.25f, 0.0f,
		0.75f, 0.0f, 1.0f, 0.25f, 1.0f, 0.0f,
		1.0f, 0.5f, 1.0f, 0.25f, 0.75f, 0.5f,
		0.25f, 0.0f, 0.0f, 0.25f, 0.25f, 0.5f,
		0.25f, 0.0f, 0.25f, 0.5f, 0.5f, 0.25f,
		0.75f, 0.0f, 0.5f, 0.25f, 0.5f, 0.0f,
		0.5f, 0.5f, 0.5f, 0.25f, 0.75f, 0.5f,
		0.5f, 0.25f, 0.0f, 0.25f, 0.25f, 0.0f,
		0.5f, 0.25f, 0.25f, 0.5f, 0.0f, 0.25f,
		0.75f, 0.0f, 1.0f, 0.0f, 1.0f, 0.25f,
		1.0f, 0.25f, 1.0f, 0.5f, 0.75f, 0.5f,
		0.5f, 0.25f, 0.7344999999999999f, 0.203f, 0.7344999999999999f, 0.297f,
		0.75f, 0.0f, 0.7344999999999999f, 0.203f, 0.5f, 0.25f,
		0.5f, 0.25f, 0.7344999999999999f, 0.297f, 0.75f, 0.5f,
		0.7344999999999999f, 0.203f, 0.75f, 0.0f, 0.7655000000000001f, 0.203f,
		0.7655000000000001f, 0.297f, 0.75f, 0.5f, 0.7344999999999999f, 0.297f,
		1.0f, 0.25f, 0.7655000000000001f, 0.203f, 0.75f, 0.0f,
		0.75f, 0.5f, 0.7655000000000001f, 0.297f, 1.0f, 0.25f,
		0.7655000000000001f, 0.203f, 1.0f, 0.25f, 0.7655000000000001f, 0.297f,
		0.0f, 0.25f, 0.25f, 0.0f, 0.5f, 0.25f,
		0.0f, 0.25f, 0.5f, 0.25f, 0.25f, 0.5f,
		0.0f, 0.25f, 0.5f, 0.25f, 0.25f, 0.0f,
		0.0f, 0.25f, 0.25f, 0.5f, 0.5f, 0.25f,
	};
	
    private int playerHitCount = 0;
    private final DockingBay bay;
    
	public Coriolis(Alite alite) {
		super(alite, "Coriolis Station", ObjectType.SpaceStation);
		shipType = ShipType.Coriolis;
		boundingBox = new float [] {-960.00f, 960.00f, -960.00f, 960.00f, -960.00f, 960.00f};
		numberOfVertices = 78;
		textureFilename = "textures/coriolis.png";
		bay = new DockingBay(alite);

		affectedByEnergyBomb = false;
		maxSpeed             = 0.0f;
        maxPitchSpeed        = 8.000f;
        maxRollSpeed         = 8.000f;        
        hullStrength         = 255.0f;
        hasEcm               = true;
        cargoType            = 7;  
        spawnCargoCanisters  = false;
        aggressionLevel      = 0;
        escapeCapsuleCaps    = 0;
        bounty               = 0;
        score                = 500;
        legalityType         = 3;
        maxCargoCanisters    = 3;        
        
        this.spawnCargoCanisters = false;
        init();
	}
	
    @Override
    protected void init() {
		vertexBuffer = createReversedScaledFaces(6.0f, VERTEX_DATA, NORMAL_DATA,
				   10, 6, 9, 1, 6, 2,  1, 9, 6, 1, 5, 9, 9, 5, 8, 0, 5, 1,
                11, 10, 9, 11, 9, 8, 11, 7, 10, 2, 7, 3, 4, 7, 11, 4, 3, 7,
                11, 8, 4, 4, 0, 3, 2, 15, 14, 3, 15, 2, 2, 14, 1, 15, 3, 12,
                13, 1, 14, 0, 12, 3, 1, 13, 0, 12, 0, 13, 10, 7, 2, 10, 2, 6,
                8, 0, 4, 8, 5, 0);
		texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
		alite.getTextureManager().addTexture(textureFilename); 
		initTargetBox();
    }

    @Override
	public void executeHit(SpaceObject player) {
    	if ("Alien Space Station".equals(getName())) {
    		return;
    	}
    	alite.getPlayer().setLegalValue(alite.getPlayer().getLegalValue() + 10);
    	playerHitCount++;
    	if (playerHitCount > 1) {
    		computeLegalStatusAfterFriendlyHit();
    	}
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
		return 960.0f; // Rounded -- but will do ;)
	}
	    
	@Override
	public void render() {		
		super.render();
		bay.render();
	}

	@Override
    public boolean accessAllowed() {
    	return accessAllowed && playerHitCount < 4;
    }

	@Override
	public void denyAccess() {
		accessAllowed = false;		
	}

	@Override
	public int getHitCount() {
		return playerHitCount;
	}
}	
