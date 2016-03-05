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
 * Cargo Canister model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.Equipment;
import de.phbouillon.android.games.alite.model.Weight;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class CargoCanister extends SpaceObject {
	private static final long serialVersionUID = -7940435691937589868L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.54f, 0.0f, 0.94f);
    
    private TradeGood content;
    private Weight weight;
    private Equipment specialContent;
    private long price;
    
    private static final float [] VERTEX_DATA = new float [] {
          48.00f,   32.00f,    0.00f,   48.00f,   10.00f,   30.00f,
          48.00f,  -26.00f,   18.00f,   48.00f,  -26.00f,  -18.00f,
          48.00f,   10.00f,  -30.00f,  -48.00f,   32.00f,    0.00f,
         -48.00f,   10.00f,   30.00f,  -48.00f,  -26.00f,   18.00f,
         -48.00f,  -26.00f,  -18.00f,  -48.00f,   10.00f,  -30.00f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.37262f,   0.90144f,  -0.22035f,   0.62549f,   0.40550f,   0.66659f, 
          0.62424f,  -0.50952f,   0.59221f,   0.37101f,  -0.85934f,  -0.35197f, 
          0.76888f,   0.04458f,  -0.63784f,  -0.62615f,   0.75740f,   0.18514f, 
         -0.62549f,   0.05440f,   0.77834f,  -0.37101f,  -0.85934f,   0.35197f, 
         -0.76785f,  -0.41782f,  -0.48563f,  -0.37206f,   0.48240f,  -0.79301f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
    	0.6070f, 0.0000f, 0.5000f, 0.5000f, 0.6070f, 0.5000f, 
    	0.8930f, 0.5000f, 0.5000f, 0.5000f, 0.5000f, 0.0000f, 
    	0.8930f, 0.0000f, 0.7940f, 0.5000f, 0.8930f, 0.5000f, 
    	0.7940f, 0.0000f, 0.7060f, 0.5000f, 0.7940f, 0.5000f, 
    	0.7060f, 0.0000f, 0.6070f, 0.5000f, 0.7060f, 0.5000f, 
    	0.8930f, 0.5000f, 0.5000f, 0.0000f, 0.8930f, 0.0000f, 
    	0.8930f, 0.0000f, 0.7940f, 0.0000f, 0.7940f, 0.5000f, 
    	0.7940f, 0.0000f, 0.7060f, 0.0000f, 0.7060f, 0.5000f, 
    	0.7060f, 0.0000f, 0.6070f, 0.0000f, 0.6070f, 0.5000f, 
    	0.6070f, 0.0000f, 0.5000f, 0.0000f, 0.5000f, 0.5000f, 
    	0.0000f, 0.1895f, 0.2500f, 0.0000f, 0.5000f, 0.1895f, 
    	0.0000f, 0.1895f, 0.4000f, 0.5000f, 0.1000f, 0.5000f, 
    	0.0000f, 0.1895f, 0.5000f, 0.1895f, 0.4000f, 0.5000f, 
    	0.1000f, 0.5000f, 0.4000f, 0.5000f, 0.5000f, 0.1895f, 
    	0.1000f, 0.5000f, 0.2500f, 0.0000f, 0.0000f, 0.1895f, 
    	0.1000f, 0.5000f, 0.5000f, 0.1895f, 0.2500f, 0.0000f, 
    };

    public CargoCanister(Alite alite) {
        super(alite, "Cargo Canister", ObjectType.CargoCanister);
        shipType = ShipType.CargoCanister;
        boundingBox = new float [] { -48.00f,   48.00f,  -26.00f,   32.00f,  -30.00f,   30.00f};
        numberOfVertices = 48;
        textureFilename = "textures/cargo_canister.png";
        maxSpeed            = 183.7f;
        maxPitchSpeed       = 0.500f;
        maxRollSpeed        = 0.500f;
        hullStrength        = 170.0f;
        hasEcm              = false;
        cargoType           = -1;
        spawnCargoCanisters = false;
        aggressionLevel     = 0;
        escapeCapsuleCaps   = 0;
        bounty              = 0;
        score               = 0;
        legalityType        = 3;
        maxCargoCanisters   = 0;        
        missileCount        = 0;
        init();
    }
    
    @Override
    protected void init() {
        vertexBuffer = createReversedFaces(VERTEX_DATA, NORMAL_DATA,
                1,   5,   6,   9,   5,   0,   4,   8,   9,   3,   7,   8,   2,   6,   7, 
                9,   0,   4,   4,   3,   8,   3,   2,   7,   2,   1,   6,   1,   0,   5, 
                4,   0,   1,   4,   2,   3,   4,   1,   2,   8,   7,   6,   8,   5,   9, 
                8,   6,   5);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);    	
        initTargetBox();
    }

    @Override
	protected boolean receivesProximityWarning() {
		return false;
	}

    public void setContent(TradeGood tradeGood, Weight quantity) {
    	content = tradeGood;
    	weight = quantity;
    }
    
    public void setContent(Equipment equipment) {
    	specialContent = equipment;
    	content = null;
    	weight = null;
    }
    
    public Equipment getEquipment() {
    	return specialContent;
    }
    
    public TradeGood getContent() {
    	return content;
    }
    
    public Weight getQuantity() {
    	return weight;
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
    
    public long getPrice() {
    	return price;
    }
    
    public void setPrice(long price) {
    	this.price = price;
    }
}
