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
 * Dodec Station model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceStation;

public class Dodec extends SpaceObject implements SpaceStation {
	private static final long serialVersionUID = -5685018008875998753L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.94f, 0.0f, 0.94f);
	private boolean accessAllowed = true;
	
    private static final float [] VERTEX_DATA = new float [] {
        0.00f, 150.00f, 196.00f, 143.00f,  46.00f, 196.00f, 
       88.00f, -121.00f, 196.00f, -88.00f, -121.00f, 196.00f, 
      -143.00f,  46.00f, 196.00f,   0.00f, 243.00f,  46.00f, 
      231.00f,  75.00f,  46.00f, 143.00f, -196.00f,  46.00f, 
      -143.00f, -196.00f,  46.00f, -231.00f,  75.00f,  46.00f, 
      143.00f, 196.00f, -46.00f, 231.00f, -75.00f, -46.00f, 
        0.00f, -243.00f, -46.00f, -231.00f, -75.00f, -46.00f, 
      -143.00f, 196.00f, -46.00f,  88.00f, 121.00f, -196.00f, 
      143.00f, -46.00f, -196.00f,   0.00f, -150.00f, -196.00f, 
      -143.00f, -46.00f, -196.00f, -88.00f, 121.00f, -196.00f, 
      -16.00f, -48.00f, 196.00f, -16.00f,  48.00f, 196.00f, 
       16.00f, -48.00f, 196.00f,  16.00f,  48.00f, 196.00f
  };

    private static final float [] NORMAL_DATA = new float [] {
        -0.84959f,   0.27576f,  -0.44961f,  -0.85064f,   0.28015f,  -0.44488f, 
        -0.84954f,   0.27606f,  -0.44953f,   0.00000f,   0.89443f,  -0.44721f, 
        -0.00314f,   0.89248f,  -0.45107f,   0.00506f,   0.89367f,  -0.44869f, 
         0.84795f,   0.27926f,  -0.45055f,   0.84992f,   0.27760f,  -0.44786f, 
         0.85170f,   0.27397f,  -0.44670f,   0.00000f,  -0.00000f,  -1.00000f, 
         0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f, 
        -0.84949f,  -0.27585f,   0.44975f,  -0.84962f,  -0.27589f,   0.44947f, 
        -0.85064f,  -0.28015f,   0.44488f,   0.00000f,  -0.89443f,   0.44721f, 
         0.00000f,  -0.89052f,   0.45494f,   0.00000f,  -0.89443f,   0.44721f, 
         0.84795f,  -0.27926f,   0.45055f,   0.85129f,  -0.27643f,   0.44598f, 
         0.84949f,  -0.27585f,   0.44975f,   0.00000f,  -0.00000f,   1.00000f, 
         0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
        -0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
         0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
         0.00000f,   0.00000f,   1.00000f,   0.00000f,  -0.00000f,   1.00000f, 
        -0.52566f,  -0.72306f,  -0.44818f,  -0.52590f,  -0.72294f,  -0.44810f, 
        -0.52578f,  -0.72295f,  -0.44823f,   0.52592f,  -0.72287f,  -0.44818f, 
         0.52578f,  -0.72295f,  -0.44823f,   0.52573f,  -0.72306f,  -0.44810f, 
        -0.52585f,   0.72305f,   0.44797f,  -0.52578f,   0.72295f,   0.44823f, 
        -0.52578f,   0.72295f,   0.44823f,   0.52578f,   0.72295f,   0.44823f, 
         0.52585f,   0.72305f,   0.44797f,   0.52578f,   0.72295f,   0.44823f
   };

   private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
	   0.4050f, 0.5000f, 0.0000f, 0.8100f, 0.2500f, 1.0000f, 
	   0.4050f, 0.5000f, 0.0950f, 0.5000f, 0.0000f, 0.8100f, 
	   0.4050f, 0.5000f, 0.2500f, 1.0000f, 0.5000f, 0.8100f, 
	   0.0950f, 0.5000f, 0.4050f, 0.5000f, 0.5000f, 0.8100f, 
	   0.0950f, 0.5000f, 0.5000f, 0.8100f, 0.2500f, 1.0000f, 
	   0.0950f, 0.5000f, 0.2500f, 1.0000f, 0.0000f, 0.8100f, 
	   0.4050f, 0.5000f, 0.0950f, 0.5000f, 0.0000f, 0.8100f, 
	   0.4050f, 0.5000f, 0.0000f, 0.8100f, 0.2500f, 1.0000f, 
	   0.4050f, 0.5000f, 0.2500f, 1.0000f, 0.5000f, 0.8100f, 
	   0.4050f, 0.5000f, 0.2500f, 0.0000f, 0.5000f, 0.1900f, 
	   0.4050f, 0.5000f, 0.0000f, 0.1900f, 0.2500f, 0.0000f, 
	   0.4050f, 0.5000f, 0.0950f, 0.5000f, 0.0000f, 0.1900f, 
	   0.5000f, 0.1900f, 0.7500f, 0.0000f, 1.0000f, 0.1900f, 
	   0.5000f, 0.1900f, 1.0000f, 0.1900f, 0.9050f, 0.5000f, 
	   0.5000f, 0.1900f, 0.9050f, 0.5000f, 0.5950f, 0.5000f, 
	   1.0000f, 0.1900f, 0.5950f, 0.5000f, 0.9050f, 0.5000f, 
	   1.0000f, 0.1900f, 0.7500f, 0.0000f, 0.5000f, 0.1900f, 
	   1.0000f, 0.1900f, 0.5000f, 0.1900f, 0.5950f, 0.5000f, 
	   0.5000f, 0.1900f, 0.9050f, 0.5000f, 0.5950f, 0.5000f, 
	   0.5000f, 0.1900f, 1.0000f, 0.1900f, 0.9050f, 0.5000f, 
	   0.5000f, 0.1900f, 0.7500f, 0.0000f, 1.0000f, 0.1900f, 
	   0.4050f, 0.0000f, 0.2800f, 0.1350f, 0.5000f, 0.3100f, 
	   0.2800f, 0.3100f, 0.5000f, 0.3100f, 0.2800f, 0.1350f, 
	   0.5000f, 0.3100f, 0.2800f, 0.3100f, 0.2500f, 0.5000f, 
	   0.2800f, 0.1350f, 0.4050f, 0.0000f, 0.2200f, 0.1350f, 
	   0.2200f, 0.3100f, 0.2500f, 0.5000f, 0.2800f, 0.3100f, 
	   0.0950f, 0.0000f, 0.2200f, 0.1350f, 0.4050f, 0.0000f, 
	   0.2500f, 0.5000f, 0.2200f, 0.3100f, 0.0000f, 0.3100f, 
	   0.2200f, 0.1350f, 0.0000f, 0.3100f, 0.2200f, 0.3100f, 
	   0.0950f, 0.0000f, 0.0000f, 0.3100f, 0.2200f, 0.1350f, 
	   0.0950f, 1.0000f, 0.5000f, 0.6900f, 0.4050f, 1.0000f, 
	   0.0950f, 1.0000f, 0.2500f, 0.5000f, 0.5000f, 0.6900f, 
	   0.0950f, 1.0000f, 0.0000f, 0.6900f, 0.2500f, 0.5000f, 
	   0.0000f, 0.6900f, 0.5000f, 0.6900f, 0.2500f, 0.5000f, 
	   0.0000f, 0.6900f, 0.0950f, 1.0000f, 0.4050f, 1.0000f, 
	   0.0000f, 0.6900f, 0.4050f, 1.0000f, 0.5000f, 0.6900f, 
	   0.7500f, 0.5000f, 1.0000f, 0.3100f, 0.9050f, 0.0000f, 
	   0.7500f, 0.5000f, 0.9050f, 0.0000f, 0.5950f, 0.0000f, 
	   0.7500f, 0.5000f, 0.5950f, 0.0000f, 0.5000f, 0.3100f, 
	   0.9050f, 0.0000f, 0.7500f, 0.5000f, 0.5000f, 0.3100f, 
	   0.9050f, 0.0000f, 1.0000f, 0.3100f, 0.7500f, 0.5000f, 
	   0.9050f, 0.0000f, 0.5000f, 0.3100f, 0.5950f, 0.0000f, 
    };
	
    private int playerHitCount = 0;
    private final DodecDockingBay bay;
    
	public Dodec(Alite alite) {
		super(alite, "Dodec Station", ObjectType.SpaceStation);
		shipType = ShipType.Dodec;
		boundingBox = new float [] {-1131.4286f, 1131.4286f, -1190.2041f, 1190.2041f, -960.0f, 960.0f};
        numberOfVertices = 126;
		textureFilename = "textures/dodec.png";
		bay = new DodecDockingBay(alite);

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
		// 4.897959 = Scale factor so that the z-dimension is the same size as
		// the scaled Coriolis size. The Coriolis has -960.0 to +960.0 for its z
		// size. So, the scale factor here is 960 / 196; 196 is maxZ.
		vertexBuffer = createReversedScaledFaces(4.897959f, VERTEX_DATA, NORMAL_DATA,
                19,  13,   9,  19,  18,  13,  19,   9,  14,  15,  19,  14,  15,  14,   5, 
                15,   5,  10,  16,  15,  10,  16,  10,   6,  16,   6,  11,  19,  17,  18, 
                19,  16,  17,  19,  15,  16,   9,  13,   8,   9,   8,   3,   9,   3,   4, 
                 8,   2,   3,   8,  12,   7,   8,   7,   2,   7,   1,   2,   7,   6,   1, 
                 7,  11,   6,   3,  20,   4,  21,   4,  20,   4,  21,   0,  20,   3,  22, 
                23,   0,  21,   2,  22,   3,   0,  23,   1,  22,   1,  23,   2,   1,  22, 
                18,   8,  13,  18,  12,   8,  18,  17,  12,  17,   7,  12,  17,  16,  11, 
                17,  11,   7,   5,  14,   9,   5,   9,   4,   5,   4,   0,   6,   5,   0, 
                 6,  10,   5,   6,   0,   1);
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
		return 1191.0f; // Rounded -- but will do ;)
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
