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
 * Thargoid model from Oolite: http://oolite.org
 * Texture from the DeepSpace OXP: http://www.box.com/shared/cx2st8mjhv.
 */

import java.util.ArrayList;
import java.util.List;

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Thargoid extends SpaceObject {
	private static final long serialVersionUID = -5931767540971901440L;
	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.55f, 0.55f);
	private long spawnThargonDistanceSq = -1;
	private final List <Thargon> activeThargons = new ArrayList<Thargon>();
	
    private static final float [] VERTEX_DATA = new float [] {
         -91.12f,   72.88f,   91.12f, -131.20f,   72.88f,    0.00f,
         -91.12f,   72.88f,  -91.12f,   -0.00f,   72.88f, -131.20f,
          91.12f,   72.88f,  -91.12f,  131.20f,   72.88f,    0.00f,
          91.12f,   72.88f,   91.12f,   -0.00f,   72.88f,  131.20f,
        -229.60f,  -72.88f,  229.60f, -328.00f,  -72.88f,    0.00f,
          -0.00f,  -72.88f,  328.00f, -229.60f,  -72.88f, -229.60f,
          -0.00f,  -72.88f, -328.00f,  229.60f,  -72.88f, -229.60f,
         328.00f,  -72.88f,    0.00f,  229.60f,  -72.88f,  229.60f,
        -278.80f,  -72.88f,  114.80f, -114.80f,  -72.88f,  278.80f,
        -278.80f,  -72.88f, -114.80f,  114.80f,  -72.88f,  278.80f,
        -114.80f,  -72.88f, -278.80f,  114.80f,  -72.88f, -278.80f,
         278.80f,  -72.88f, -114.80f,  278.80f,  -72.88f,  114.80f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.34807f,  -0.87046f,  -0.34807f,   0.41011f,  -0.91203f,   0.00000f, 
          0.29286f,  -0.91020f,   0.29286f,  -0.00000f,  -0.91203f,   0.41011f, 
         -0.29286f,  -0.91020f,   0.29286f,  -0.41011f,  -0.91203f,   0.00000f, 
         -0.34807f,  -0.87046f,  -0.34807f,   0.00000f,  -0.96960f,  -0.24469f, 
          0.66134f,   0.35392f,  -0.66134f,   0.93359f,   0.35834f,   0.00000f, 
          0.00000f,   0.35834f,  -0.93359f,   0.66134f,   0.35392f,   0.66134f, 
         -0.00000f,   0.35834f,   0.93359f,  -0.63684f,  -0.43459f,   0.63684f, 
         -0.90044f,  -0.43499f,  -0.00000f,  -0.66134f,   0.35392f,  -0.66134f, 
          0.90337f,  -0.17711f,  -0.39059f,   0.39059f,  -0.17711f,  -0.90337f, 
          0.90337f,  -0.17711f,   0.39059f,  -0.39059f,  -0.17711f,  -0.90337f, 
          0.39059f,  -0.17711f,   0.90337f,  -0.39059f,  -0.17711f,   0.90337f, 
         -0.14605f,   0.98726f,   0.06315f,  -0.90337f,  -0.17711f,  -0.39059f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.50f,   0.00f,   0.00f,   0.00f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.50f,   0.50f,   0.75f,   0.50f, 
          1.00f,   0.00f,   0.50f,   0.00f,   0.75f,   0.50f, 
          0.00f,   0.00f,   0.00f,   0.50f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.00f,   0.00f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.50f,   0.50f,   0.75f,   0.50f, 
          1.00f,   0.00f,   0.50f,   0.00f,   0.75f,   0.50f, 
          0.00f,   0.00f,   0.00f,   0.50f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.00f,   0.00f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.50f,   0.50f,   0.75f,   0.50f, 
          0.00f,   0.00f,   0.00f,   0.50f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.00f,   0.00f,   0.25f,   0.50f, 
          0.50f,   0.00f,   0.50f,   0.50f,   0.75f,   0.50f, 
          0.50f,   0.65f,   0.50f,   0.85f,   0.65f,   1.00f, 
          0.50f,   0.65f,   0.65f,   1.00f,   0.85f,   1.00f, 
          0.50f,   0.65f,   0.85f,   1.00f,   1.00f,   0.85f, 
          0.50f,   0.65f,   1.00f,   0.85f,   1.00f,   0.65f, 
          0.50f,   0.65f,   1.00f,   0.65f,   0.85f,   0.50f, 
          0.50f,   0.65f,   0.85f,   0.50f,   0.65f,   0.50f, 
          1.00f,   0.00f,   0.50f,   0.00f,   0.75f,   0.50f, 
          0.00f,   0.00f,   0.00f,   0.50f,   0.25f,   0.50f, 
          1.00f,   0.50f,   1.00f,   0.00f,   0.75f,   0.50f, 
          0.75f,   0.50f,   1.00f,   0.50f,   1.00f,   0.00f, 
          0.25f,   0.50f,   0.50f,   0.50f,   0.50f,   0.00f, 
          0.25f,   0.50f,   0.50f,   0.50f,   0.50f,   0.00f, 
          0.75f,   0.50f,   1.00f,   0.50f,   1.00f,   0.00f, 
          0.75f,   0.50f,   1.00f,   0.50f,   1.00f,   0.00f, 
          0.25f,   0.50f,   0.50f,   0.50f,   0.50f,   0.00f, 
          0.75f,   0.50f,   1.00f,   0.00f,   0.50f,   0.00f, 
          0.07f,   0.57f,   0.00f,   0.65f,   0.00f,   0.75f, 
          0.07f,   0.57f,   0.00f,   0.75f,   0.00f,   0.85f, 
          0.07f,   0.57f,   0.00f,   0.85f,   0.07f,   0.93f, 
          0.07f,   0.57f,   0.07f,   0.93f,   0.15f,   1.00f, 
          0.07f,   0.57f,   0.15f,   1.00f,   0.25f,   1.00f, 
          0.07f,   0.57f,   0.25f,   1.00f,   0.35f,   1.00f, 
          0.07f,   0.57f,   0.35f,   1.00f,   0.43f,   0.93f, 
          0.07f,   0.57f,   0.43f,   0.93f,   0.50f,   0.85f, 
          0.07f,   0.57f,   0.50f,   0.85f,   0.50f,   0.75f, 
          0.07f,   0.57f,   0.50f,   0.75f,   0.50f,   0.65f, 
          0.07f,   0.57f,   0.50f,   0.65f,   0.43f,   0.57f, 
          0.07f,   0.57f,   0.43f,   0.57f,   0.35f,   0.50f, 
          0.07f,   0.57f,   0.35f,   0.50f,   0.25f,   0.50f, 
          0.07f,   0.57f,   0.25f,   0.50f,   0.15f,   0.50f, 
          0.25f,   0.50f,   0.50f,   0.50f,   0.50f,   0.00f
    };

    public Thargoid(Alite alite) {
        super(alite, "Thargoid", ObjectType.Thargoid);
        shipType = ShipType.Thargoid;
        boundingBox = new float [] {-328.00f,  328.00f,  -72.88f,   72.88f, -328.00f,  328.00f};
        numberOfVertices = 132;
        textureFilename = "textures/thargoid_ds.png";
        affectedByEnergyBomb = false;
        maxSpeed             = 501.0f;
        maxPitchSpeed        = 1.000f;
        maxRollSpeed         = 2.000f;        
        hullStrength         = 500.0f;
        hasEcm               = true;
        cargoType            = 0;
        spawnCargoCanisters  = false;
        aggressionLevel      = 20;
        escapeCapsuleCaps    = 0;
        bounty               = 400;
        score                = 300;
        legalityType         = 7;
        maxCargoCanisters    = 0;
        missileCount         = 0;
        laserHardpoints.add(VERTEX_DATA[57]);
        laserHardpoints.add(VERTEX_DATA[58]);
        laserHardpoints.add(VERTEX_DATA[59]);
        laserHardpoints.add(VERTEX_DATA[51]);
        laserHardpoints.add(VERTEX_DATA[52]);
        laserHardpoints.add(VERTEX_DATA[53]);
        laserHardpoints.add(VERTEX_DATA[21]);
        laserHardpoints.add(VERTEX_DATA[22]);
        laserHardpoints.add(VERTEX_DATA[23]);
        laserColor = 0x7F00FF00l;
        laserTexture = "textures/laser_green.png";
        init();
    }

    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   7,  17,   0,   8,  16,   1,   0,  16,   1,   9,  18,   2,   1,  18, 
                2,  11,  20,   3,   2,  20,   3,  12,  21,   4,   3,  21,   4,  13,  22, 
                5,  14,  23,   6,   5,  23,   6,  15,  19,   7,   0,   1,   7,   1,   2, 
                7,   2,   3,   7,   3,   4,   7,   4,   5,   7,   5,   6,   7,   6,  19, 
                7,  10,  17,  14,   5,  22,  16,   9,   1,  17,   8,   0,  18,  11,   2, 
               19,  10,   7,  20,  12,   3,  21,  13,   4,  22,   5,   4,  22,  13,  21, 
               22,  21,  12,  22,  12,  20,  22,  20,  11,  22,  11,  18,  22,  18,   9, 
               22,   9,  16,  22,  16,   8,  22,   8,  17,  22,  17,  10,  22,  10,  19, 
               22,  19,  15,  22,  15,  23,  22,  23,  14,  23,  15,   6);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename); 
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
    
    public void setSpawnThargonDistanceSq(long distance) {
    	spawnThargonDistanceSq = distance;
    }
    
    public long getSpawnThargonDistanceSq() {
    	return spawnThargonDistanceSq;
    }

	public void addActiveThargon(Thargon thargon) {
		activeThargons.add(thargon);
	}

	public void removeActiveThargon(Thargon thargon) {
		activeThargons.remove(thargon);
	}

	public List <Thargon> getActiveThargons() {
		return activeThargons;
	}
}
