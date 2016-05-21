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
 * Icosaeder model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;

public class Icosaeder extends SpaceObject implements SpaceStation {
	private static final long serialVersionUID = -5954401362815007288L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);
	private boolean accessAllowed = true;
	
    private static final float [] VERTEX_DATA = new float [] {
        250.00f,   0.00f, -154.50f, 250.00f,   0.00f, 154.50f, 
        -250.00f,   0.00f, 154.50f, -250.00f,   0.00f, -154.50f, 
          0.00f, -154.50f, -250.00f,   0.00f, 154.50f, -250.00f, 
          0.00f, 154.50f, 250.00f,   0.00f, -154.50f, 250.00f, 
        154.50f, -250.00f,   0.00f, -154.50f, -250.00f,   0.00f, 
        -154.50f, 250.00f,   0.00f, 154.50f, 250.00f,   0.00f, 
         16.00f, -48.00f, 250.00f, -16.00f, -48.00f, 250.00f, 
         16.00f,  48.00f, 250.00f, -16.00f,  48.00f, 250.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
        -0.57735f,   0.57735f,  -0.57735f,  -0.57735f,   0.57735f,   0.57735f, 
         0.00000f,   0.93416f,  -0.35685f,  -0.00000f,   0.93416f,   0.35685f, 
         0.57735f,   0.57735f,  -0.57735f,   0.57735f,   0.57735f,   0.57735f, 
        -0.35685f,   0.00000f,  -0.93416f,   0.35685f,   0.00000f,  -0.93416f, 
        -0.57735f,  -0.57735f,  -0.57735f,  -0.57735f,  -0.57735f,   0.57735f, 
         0.00000f,  -0.93416f,   0.35685f,   0.00000f,  -0.93416f,  -0.35685f, 
         0.57735f,  -0.57735f,  -0.57735f,   0.57735f,  -0.57735f,   0.57735f, 
        -0.37786f,   0.00000f,   0.92586f,  -0.38743f,  -0.05821f,   0.92006f, 
        -0.38743f,   0.05821f,   0.92006f,   0.00000f,   0.00000f,   1.00000f, 
        -0.00000f,   0.00000f,   1.00000f,   0.38743f,  -0.05821f,   0.92006f, 
         0.38743f,   0.05821f,   0.92006f,   0.37786f,  -0.00000f,   0.92586f, 
         0.93416f,  -0.35685f,   0.00000f,   0.93416f,   0.35685f,  -0.00000f, 
        -0.93416f,   0.35685f,   0.00000f,  -0.93416f,  -0.35685f,   0.00000f
   };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
    	1.0000f, 0.0000f, 0.5000f, 0.0000f, 0.7500f, 1.0000f, 
    	0.5000f, 0.0000f, 1.0000f, 0.0000f, 0.7500f, 1.0000f, 
    	0.5000f, 0.0000f, 1.0000f, 0.0000f, 0.7500f, 1.0000f, 
    	0.5000f, 0.0000f, 0.7500f, 1.0000f, 1.0000f, 0.0000f, 
    	0.7500f, 1.0000f, 1.0000f, 0.0000f, 0.5000f, 0.0000f, 
    	1.0000f, 0.0000f, 0.7500f, 1.0000f, 0.5000f, 0.0000f, 
    	0.2500f, 0.1900f, 0.0000f, 0.5000f, 0.2500f, 0.8100f, 
    	0.2500f, 0.8100f, 0.5000f, 0.5000f, 0.2500f, 0.1900f, 
    	0.7500f, 1.0000f, 1.0000f, 0.0000f, 0.5000f, 0.0000f, 
    	1.0000f, 0.0000f, 0.7500f, 1.0000f, 0.5000f, 0.0000f, 
    	0.5000f, 0.0000f, 0.7500f, 1.0000f, 1.0000f, 0.0000f, 
    	1.0000f, 0.0000f, 0.7500f, 1.0000f, 0.5000f, 0.0000f, 
    	0.7500f, 1.0000f, 1.0000f, 0.0000f, 0.5000f, 0.0000f, 
    	0.5000f, 0.0000f, 1.0000f, 0.0000f, 0.7500f, 1.0000f, 
    	0.2350f, 0.6000f, 0.0000f, 0.5000f, 0.2350f, 0.4000f, 
    	0.2500f, 0.1900f, 0.2350f, 0.4000f, 0.0000f, 0.5000f, 
    	0.0000f, 0.5000f, 0.2350f, 0.6000f, 0.2500f, 0.8100f, 
    	0.2500f, 0.8100f, 0.2350f, 0.6000f, 0.2650f, 0.6000f, 
    	0.2350f, 0.4000f, 0.2500f, 0.1900f, 0.2650f, 0.4000f, 
    	0.5000f, 0.5000f, 0.2650f, 0.4000f, 0.2500f, 0.1900f, 
    	0.2500f, 0.8100f, 0.2650f, 0.6000f, 0.5000f, 0.5000f, 
    	0.2650f, 0.4000f, 0.5000f, 0.5000f, 0.2650f, 0.6000f, 
    	0.2500f, 1.0000f, 0.5000f, 0.0000f, 0.0000f, 0.0000f, 
    	0.5000f, 0.0000f, 0.2500f, 1.0000f, 0.0000f, 0.0000f, 
    	0.5000f, 0.0000f, 0.2500f, 1.0000f, 0.0000f, 0.0000f, 
    	0.0000f, 0.0000f, 0.2500f, 1.0000f, 0.5000f, 0.0000f, 
    };
	
    private int playerHitCount = 0;
    private final IcosDockingBay bay;
    
	public Icosaeder(Alite alite) {
		super(alite, "Icosaeder Station", ObjectType.SpaceStation);
		shipType = ShipType.Icosaeder;
		boundingBox = new float [] {-960.00f, 960.00f, -960.00f, 960.00f, -960.00f, 960.00f};
		numberOfVertices = 78;
		textureFilename = "textures/icosaeder.png";
		bay = new IcosDockingBay(alite);
		
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
		// 3.84 is the scale factor to make the Icosaeder station the same size
		// as the Coriolis station (-960.0 to +960.0)
		vertexBuffer = createReversedScaledFaces(3.84f, VERTEX_DATA, NORMAL_DATA,
                3,  10,   5,  10,   2,   6,  10,  11,   5,  10,   6,  11,   5,  11,   0, 
               11,   6,   1,   4,   3,   5,   5,   0,   4,   4,   9,   3,   9,   7,   2, 
                8,   7,   9,   9,   4,   8,   4,   0,   8,   8,   1,   7,  15,   2,  13, 
                7,  13,   2,   2,  15,   6,   6,  15,  14,  13,   7,  12,   1,  12,   7, 
                6,  14,   1,  12,   1,  14,   8,   0,   1,   0,  11,   1,   2,  10,   3, 
                3,   9,   2);
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
