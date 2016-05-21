package de.phbouillon.android.games.alite.screens.opengl.objects.space;

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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.opengl.GLES11;
import android.opengl.Matrix;
import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.generator.SystemData;
import de.phbouillon.android.games.alite.model.generator.enums.Government;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.model.trading.TradeGood;
import de.phbouillon.android.games.alite.model.trading.TradeGoodStore;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.LaserManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.TargetBoxSpaceObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Adder;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Anaconda;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.AspMkII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Asteroid1;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Asteroid2;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.BoaClassCruiser;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Boomslang;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Bushmaster;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkI;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkIII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Coral;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Cottonmouth;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Dugite;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.FerDeLance;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Gecko;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Gopher;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Harlequin;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Hognose2;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Indigo;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Krait;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Lora;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Lyre;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Mamba;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Missile;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.MorayStarBoat;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Mussurana;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Python;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Rattlesnake;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Sidewinder;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.WolfMkII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Yellowbelly;

public abstract class SpaceObject extends AliteObject implements Geometry, Serializable {
	private static final long serialVersionUID = 5206073222641804313L;

	protected transient FloatBuffer vertexBuffer;
	protected transient FloatBuffer normalBuffer;

	protected transient FloatBuffer texCoordBuffer;
	protected transient Alite alite;

	protected int numberOfVertices;
	protected String textureFilename;
	protected final float [] displayMatrix = new float[16];
	protected float [] boundingBox;
	protected float [] originalBoundingBox = null;
	protected float [] vertices;
	protected float [] normals;
	protected float maxSpeed;
	protected float maxRollSpeed;
	protected float maxPitchSpeed;
	protected float hullStrength;
	protected boolean hasEcm;
	protected boolean spawnCargoCanisters;
	protected int cargoType;
    protected int aggressionLevel;
    protected int escapeCapsuleCaps;
    protected int missileCount = 2;
    protected int bounty;
    protected int score;
    protected int legalityType;
    protected int maxCargoCanisters;
    protected boolean affectedByEnergyBomb = true;
    protected boolean inBay = false;
    protected ShipType shipType;
    protected final List <Float> laserHardpoints = new ArrayList<Float>();
	protected final HashMap <AiStateCallback, AiStateCallbackHandler> aiStateCallbackHandlers = new HashMap<AiStateCallback, AiStateCallbackHandler>();
    protected boolean ejected = false;
    protected boolean cloaked = false;
    protected int cargoCanisterCount = 0;
	protected boolean ignoreSafeZone = false;
	protected long lastMissileTime = -1;
	protected long laserColor = 0x7FFFAA00l;
	protected String laserTexture = "textures/laser_orange.png";
	protected transient List <EngineExhaust> exhaust = new ArrayList<EngineExhaust>();
	protected boolean identified = false;

	private final List <ShipType> objectsToSpawn = new ArrayList<ShipType>();

	private final static String [] matrixString = new String[] {"", "", "", ""};
	private final SpaceObjectAI ai = new SpaceObjectAI(this);
	private ObjectType type;
	protected TargetBoxSpaceObject targetBox;
	protected SpaceObject proximity;

	private Vector3f overrideColor = new Vector3f(0, 0, 0);

	public SpaceObject(Alite alite, String name, ObjectType type) {
		super(name);
		this.alite = alite;
		this.spawnCargoCanisters = true;
		this.type = type;
	}
	
	protected abstract void init();

	protected void initTargetBox() {
		float size = getMaxExtentWithoutExhaust() * 1.25f;
		targetBox = new TargetBoxSpaceObject(alite, "targetBox", size, size, size);
		getHudColor().copy(v0);
		targetBox.setColor(v0.x, v0.y, v0.z);
	}

	public void addExhaust(EngineExhaust exhaust) {
		if (this.exhaust == null) {
			this.exhaust = new ArrayList<EngineExhaust>();
		}
		this.exhaust.add(exhaust);
	}

	public boolean isAffectedByEnergyBomb() {
		return affectedByEnergyBomb;
	}

	public List <EngineExhaust> getExhausts() {
		return exhaust == null ? Collections.<EngineExhaust> emptyList() : exhaust;
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "SpaceObject.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "SpaceObject.readObject I");
			this.alite = Alite.get();
			exhaust = new ArrayList<EngineExhaust>();
			init();
			AliteLog.e("readObject", "SpaceObject.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	Alite getGame() {
		return alite;
	}

	public void setAggression(int aggression) {
		this.aggressionLevel = aggression;
	}

	public void addObjectToSpawn(ShipType type) {
		objectsToSpawn.add(type);
	}

	public List <ShipType> getObjectsToSpawn() {
		return objectsToSpawn;
	}

	public void clearObjectsToSpawn() {
		objectsToSpawn.clear();
	}

	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}

	public final int getMissileCount() {
		return missileCount;
	}

	public final void setMissileCount(int newMissileCount) {
		missileCount = newMissileCount;
	}

	public boolean canFireMissile() {
		if (missileCount <= 0) {
			return false;
		}
		if (lastMissileTime == -1 || (System.nanoTime() - lastMissileTime) > 4000000000l) {
			lastMissileTime = System.nanoTime();
			return true;
		}
		return false;
	}

	public void setEjected(boolean b) {
		this.ejected = b;
	}

	public boolean hasEjected() {
		return ejected;
	}

	public float getBoundingSphereRadius() {
		float me = getMaxExtent() / 2.0f;
		return (float) Math.sqrt(me * me * 3);
	}

	public float getBoundingSphereRadiusSq() {
		float me = getMaxExtent() / 2.0f;
		return me * me * 3.0f;
	}

	public void setInBay(boolean b) {
		inBay = b;
	}

	public boolean isInBay() {
		return inBay;
	}

	public void setCloaked(boolean cloaked) {
		this.cloaked = cloaked;
	}

	public boolean isCloaked() {
		return cloaked;
	}

	public String getDisplayMatrixString() {
		return String.format(Locale.getDefault(), "[%4.2f, %4.2f, %4.2f, %4.2f\n" +
		                     " %4.2f, %4.2f, %4.2f, %4.2f\n" +
		                     " %4.2f, %4.2f, %4.2f, %4.2f\n" +
		                     " %4.2f, %4.2f, %4.2f, %4.2f]",
		                       displayMatrix[ 0], displayMatrix[ 4], displayMatrix[ 8], displayMatrix[12],
		                       displayMatrix[ 1], displayMatrix[ 5], displayMatrix[ 9], displayMatrix[13],
		                       displayMatrix[ 2], displayMatrix[ 6], displayMatrix[10], displayMatrix[14],
		                       displayMatrix[ 3], displayMatrix[ 7], displayMatrix[11], displayMatrix[15]);
	}

	protected boolean receivesProximityWarning() {
		return true;
	}

	public void hasBeenHitByPlayer() {
	}

	protected void computeLegalStatusAfterFriendlyHit() {
	    SystemData currentSystem = alite.getPlayer().getCurrentSystem();
	    int legalValue = alite.getPlayer().getLegalValue();	 
	    if (InGameManager.playerInSafeZone) {
	    	if (getType() == ObjectType.SpaceStation || alite.getPlayer().getCurrentSystem().getGovernment() != Government.ANARCHY) {
	    		InGameManager.safeZoneViolated = true;	    		
	    	}
	    }
	    if (currentSystem != null) {
	    	Government government = currentSystem.getGovernment();
	    	switch (government) {
	    		case ANARCHY: break; // In anarchies, you can do whatever you want.
	    		case FEUDAL: if (hullStrength < 10 && Math.random() > 0.9) { legalValue += 16; } break;
	    		case MULTI_GOVERNMENT: if (hullStrength < 10 && Math.random() > 0.8) { legalValue += 24; } break;
	    		case DICTATORSHIP: if (hullStrength < 10 && Math.random() > 0.6) { legalValue += 32; } break;
	    		case COMMUNIST: if (hullStrength < 20 && Math.random() > 0.4) { legalValue += 40; } break;
	    		case CONFEDERACY: if (hullStrength < 30 && Math.random() > 0.2) { legalValue += 48; } break;
	    		case DEMOCRACY: legalValue += 56; break;
	    		case CORPORATE_STATE: legalValue += 64; break;
	    	}
	    } else {
	    	// Default behavior as a "safeguard". Shouldn't really happen...
	    	legalValue += 64;
	    }
	    alite.getPlayer().setLegalValue(legalValue);	    
	}
	
	public void setProximity(SpaceObject other) {
		if (other == null) {
			proximity = null;
			return;
		}
		if (!receivesProximityWarning()) {
			return;
		}
		if (proximity == other) {
			return;
		}
		if (other.getType() == ObjectType.SpaceStation) {
			if (inBay) {
				return;
			}
			float maxExtentSq = other.getMaxExtent();
			maxExtentSq *= maxExtentSq;
			if (other.getPosition().distanceSq(getPosition()) > maxExtentSq) {
				return;
			}
		}
		if (other.getType() == ObjectType.Missile) {
			if (((Missile) other).getSource() == this) {
				return;
			}
		}

		other.getPosition().sub(getPosition(), v0);
		float dotForward = v0.dot(getForwardVector());
		float dotUp = v0.dot(getUpVector());
		float dotRight = v0.dot(getRightVector());
		if (dotForward < 0 && getSpeed() < 0) {
			dotForward *= 0.25f * getMaxSpeed() / -getSpeed();
		}
		float dotSq = dotForward * dotForward + dotUp * dotUp + dotRight * dotRight;
		float collisionSq = getBoundingSphereRadius() * 3.0f + other.getBoundingSphereRadius() * 3.0f;
		collisionSq *= collisionSq;

		if (dotSq > collisionSq) {
			return;
		}
		if ((ai.getState() == AIState.EVADE) && (proximity != null)) {
			getPosition().sub(proximity.getPosition(), v0);
			v0.normalize();
			float angleProx = getForwardVector().angleInDegrees(v0);
			if (angleProx >= 180) {
				angleProx = 360 - angleProx;
			}
			getPosition().sub(other.getPosition(), v0);
			v0.normalize();
			float angleOther = getForwardVector().angleInDegrees(v0);
			if (angleOther >= 180) {
				angleOther = 360 - angleOther;
			}
			if (angleProx < angleOther) {
				return;
			}
		}
		if (Settings.VIS_DEBUG) {
			Vector3f pP = new Vector3f(0, 0, 0);
			Vector3f pvX = new Vector3f(0, 0, 0);
			Vector3f pvY = new Vector3f(0, 0, 0);
			Vector3f pvZ = new Vector3f(0, 0, 0);
			Vector3f oP = new Vector3f(0, 0, 0);
			Vector3f ovX = new Vector3f(0, 0, 0);
			Vector3f ovY = new Vector3f(0, 0, 0);
			Vector3f ovZ = new Vector3f(0, 0, 0);
			Vector3f tt = new Vector3f(0, 0, 0);
			getRightVector().copy(pvX);
			pvX.scale(0.0f-boundingBox[0]);
			getUpVector().copy(pvY);
			pvY.scale(0.0f-boundingBox[2]);
			getForwardVector().copy(pvZ);
			pvZ.scale(boundingBox[4]);
			getPosition().sub(pvX,pP);
			pP.sub(pvY);
			pP.sub(pvZ);
			getRightVector().copy(tt);
			tt.scale(boundingBox[1]);
			pvX.add(tt);
			getUpVector().copy(tt);
			tt.scale(boundingBox[3]);
			pvY.add(tt);
			getForwardVector().copy(tt);
			tt.scale(0.0f-boundingBox[5]);
			pvZ.add(tt);
			other.getRightVector().copy(ovX);
			ovX.scale(0.0f-other.boundingBox[0]);
			other.getUpVector().copy(ovY);
			ovY.scale(0.0f-other.boundingBox[2]);
			other.getForwardVector().copy(ovZ);
			ovZ.scale(other.boundingBox[4]);
			other.getPosition().sub(ovX,oP);
			oP.sub(ovY);
			oP.sub(ovZ);
			other.getRightVector().copy(tt);
			tt.scale(other.boundingBox[1]);
			ovX.add(tt);
			other.getUpVector().copy(tt);
			tt.scale(other.boundingBox[3]);
			ovY.add(tt);
			other.getForwardVector().copy(tt);
			tt.scale(0.0f-other.boundingBox[5]);
			ovZ.add(tt);
			if (this instanceof CobraMkIII && ((CobraMkIII) this).isPlayerCobra()) {
				AliteLog.e("AIS",
					   "PRXMTY: Player (" + getPosition().x + ":" + getPosition().y + ":" + getPosition().z + ":" + getBoundingSphereRadius() +
					   ":" + pP.x + ":" + pP.y + ":" + pP.z + ":" + pvX.x + ":" + pvX.y + ":" + pvX.z + ":" + pvY.x + ":" + pvY.y + ":" + pvY.z + ":" + pvZ.x + ":" + pvZ.y + ":" + pvZ.z +
					   ") " + other +
					   " (" + other.getPosition().x + ":" + other.getPosition().y + ":" + other.getPosition().z + ":" + other.getBoundingSphereRadius() +
					   ":" + oP.x + ":" + oP.y + ":" + oP.z + ":" + ovX.x + ":" + ovX.y + ":" + ovX.z + ":" + ovY.x + ":" + ovY.y + ":" + ovY.z + ":" + ovZ.x + ":" + ovZ.y + ":" + ovZ.z +
					   ")");
			}
			else {
				AliteLog.e("AIS",
					   "PRXMTY: " + this + " (" + getPosition().x + ":" + getPosition().y + ":" + getPosition().z + ":" + getBoundingSphereRadius() +
					   ":" + pP.x + ":" + pP.y + ":" + pP.z + ":" + pvX.x + ":" + pvX.y + ":" + pvX.z + ":" + pvY.x + ":" + pvY.y + ":" + pvY.z + ":" + pvZ.x + ":" + pvZ.y + ":" + pvZ.z +
					   ") " + other +
					   " (" + other.getPosition().x + ":" + other.getPosition().y + ":" + other.getPosition().z + ":" + other.getBoundingSphereRadius() +
					   ":" + oP.x + ":" + oP.y + ":" + oP.z + ":" + ovX.x + ":" + ovX.y + ":" + ovX.z + ":" + ovY.x + ":" + ovY.y + ":" + ovY.z + ":" + ovZ.x + ":" + ovZ.y + ":" + ovZ.z +
					   ")");
			}
                }
		proximity = other;
	}

	public SpaceObject getProximity() {
		return proximity;
	}

	@Override
	public void render() {
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
		GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		GLES11.glDrawArrays(GLES11.GL_TRIANGLES, 0, numberOfVertices);
		alite.getTextureManager().setTexture(null);
		if (Settings.engineExhaust && exhaust != null && !exhaust.isEmpty() && getSpeed() < 0f) {
			for (EngineExhaust ex: exhaust) {
				ex.render();
			}
		}
	}

	public void renderTargetBox(float distSq) {
		if (!Settings.targetBox || targetBox == null || distSq <= SpaceObjectAI.SHOOT_DISTANCE_SQ) {
			return;
		}
		float alpha = (distSq - SpaceObjectAI.SHOOT_DISTANCE_SQ) / (LaserManager.MAX_ENEMY_DISTANCE_SQ - SpaceObjectAI.SHOOT_DISTANCE_SQ);
		targetBox.setAlpha(alpha);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE);
		targetBox.render();
		GLES11.glDisable(GLES11.GL_BLEND);
	}

	@Override
	public void setDisplayMatrix(float [] matrix) {
		int counter = 0;
		for (float f: matrix) {
			displayMatrix[counter++] = f;
		}
	}

	@Override
	public float [] getDisplayMatrix() {
		return displayMatrix;
	}

	protected final FloatBuffer createFaces(float [] vertexData, float [] normalData, int ...indices) {
		vertices = new float[indices.length * 3];
		normals  = new float[indices.length * 3];

		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = vertexData[i * 3];
			vertices[offset + 1] = vertexData[i * 3 + 1];
			vertices[offset + 2] = -vertexData[i * 3 + 2];

			normals[offset]      = -normalData[i * 3];
			normals[offset + 1]  = -normalData[i * 3 + 1];
			normals[offset + 2]  = normalData[i * 3 + 2];
			offset += 3;
		}
		normalBuffer = GlUtils.toFloatBufferPositionZero(normals);

		return GlUtils.toFloatBufferPositionZero(vertices);
	}

	protected final FloatBuffer createScaledFaces(float scale, float [] vertexData, float [] normalData, int ...indices) {
		vertices = new float[indices.length * 3];
		normals  = new float[indices.length * 3];

		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = vertexData[i * 3] * scale;
			vertices[offset + 1] = vertexData[i * 3 + 1] * scale;
			vertices[offset + 2] = -vertexData[i * 3 + 2] * scale;

			normals[offset]      = -normalData[i * 3];
			normals[offset + 1]  = -normalData[i * 3 + 1];
			normals[offset + 2]  = normalData[i * 3 + 2];
			offset += 3;
		}
		normalBuffer = GlUtils.toFloatBufferPositionZero(normals);

		return GlUtils.toFloatBufferPositionZero(vertices);
	}

	protected final FloatBuffer createReversedFaces(float [] vertexData, float [] normalData, int ...indices) {
		vertices = new float[indices.length * 3];
		normals  = new float[indices.length * 3];

		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = vertexData[i * 3];
			vertices[offset + 1] = vertexData[i * 3 + 1];
			vertices[offset + 2] = vertexData[i * 3 + 2];

			normals[offset]      = -normalData[i * 3];
			normals[offset + 1]  = -normalData[i * 3 + 1];
			normals[offset + 2]  = -normalData[i * 3 + 2];
			offset += 3;
		}
		normalBuffer = GlUtils.toFloatBufferPositionZero(normals);

		return GlUtils.toFloatBufferPositionZero(vertices);
	}

	protected final FloatBuffer createReversedRotatedFaces(float [] vertexData, float [] normalData, int ...indices) {
		vertices = new float[indices.length * 3];
		normals  = new float[indices.length * 3];

		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = -vertexData[i * 3];
			vertices[offset + 1] = vertexData[i * 3 + 1];
			vertices[offset + 2] = -vertexData[i * 3 + 2];

			normals[offset]      = normalData[i * 3];
			normals[offset + 1]  = -normalData[i * 3 + 1];
			normals[offset + 2]  = normalData[i * 3 + 2];
			offset += 3;
		}
		normalBuffer = GlUtils.toFloatBufferPositionZero(normals);

		return GlUtils.toFloatBufferPositionZero(vertices);
	}

// -1 0 0
//	0 1 0
//	0 0 -1
	protected final FloatBuffer createReversedScaledFaces(float scale, float [] vertexData, float [] normalData, int ...indices) {
		vertices = new float[indices.length * 3];
		normals  = new float[indices.length * 3];

		int offset = 0;
		for (int i: indices) {
			vertices[offset]     = vertexData[i * 3] * scale;
			vertices[offset + 1] = vertexData[i * 3 + 1] * scale;
			vertices[offset + 2] = vertexData[i * 3 + 2] * scale;

			normals[offset]      = -normalData[i * 3];
			normals[offset + 1]  = -normalData[i * 3 + 1];
			normals[offset + 2]  = -normalData[i * 3 + 2];
			offset += 3;
		}
		normalBuffer = GlUtils.toFloatBufferPositionZero(normals);

		return GlUtils.toFloatBufferPositionZero(vertices);
	}

	public float [] getBoundingBox() {
		return boundingBox;
	}

	public void scaleBoundingBox(float scale) {
		if (originalBoundingBox == null) {
			originalBoundingBox = new float[boundingBox.length];
			for (int i = 0; i < boundingBox.length; i++) {
				originalBoundingBox[i] = boundingBox[i];
			}
		}
		for (int i = 0; i < originalBoundingBox.length; i++) {
			boundingBox[i] = originalBoundingBox[i] * scale;
		}
	}

	@Override
	public void scale(float scale) {
		computeMatrix();
		Matrix.scaleM(currentMatrix, 0, scale, scale, scale);
		scaleBoundingBox(scale);
		extractVectors();
	}

	public float getMaxExtent() {
		float add = 0.0f;
		if (exhaust != null && !exhaust.isEmpty()) {
			add = exhaust.get(0).getMaxLen();
		}
		return Math.max(Math.abs(boundingBox[0]) + Math.abs(boundingBox[1]),
			            Math.max(Math.abs(boundingBox[2]) + Math.abs(boundingBox[3]),
					Math.abs(boundingBox[4]) + Math.abs(boundingBox[5]) + add));
	}

	public float getMaxExtentWithoutExhaust() {
		return Math.max(Math.abs(boundingBox[0]) + Math.abs(boundingBox[1]),
			            Math.max(Math.abs(boundingBox[2]) + Math.abs(boundingBox[3]),
					Math.abs(boundingBox[4]) + Math.abs(boundingBox[5])));
	}

	public float getMedianRadius() {
		return (Math.abs(boundingBox[0]) + Math.abs(boundingBox[1]) +
			    Math.abs(boundingBox[2]) + Math.abs(boundingBox[3]) +
			    Math.abs(boundingBox[4]) + Math.abs(boundingBox[5])) / 6.0f;
	}

	public void dispose() {
		if (textureFilename != null) {
			alite.getTextureManager().freeTexture(textureFilename);
		}
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public float getMaxRollSpeed() {
		return maxRollSpeed;
	}

	public float getMaxPitchSpeed() {
		return maxPitchSpeed;
	}

	public float getHullStrength() {
		return hullStrength;
	}

	public boolean hasEcm() {
		return hasEcm;
	}

	public int getAggressionLevel() {
		return aggressionLevel;
	}

	public boolean spawnsCargoCanisters() {
		return spawnCargoCanisters;
	}

	public int getMaxCargoCanisters() {
		return maxCargoCanisters;
	}

	public TradeGood getCargoType() {
		if (cargoType <= 0) {
			return null;
		}
		return TradeGoodStore.get().fromNumber(cargoType);
	}

	public float applyDamage(float amount) {
		hullStrength -= amount;
		if (hullStrength < 0) {
			hullStrength = 0;
		}
		return hullStrength;
	}

	public float setHullStrength(float newHullStrength) {
		hullStrength = newHullStrength;
		if (hullStrength < 0) {
			hullStrength = 0;
		}
		return hullStrength;
	}

	public boolean intersect(Vector3f origin, Vector3f direction) {
		float [] verts = new float[vertices.length];
		float [] matrix = getMatrix();
		for (int i = 0; i < numberOfVertices * 3; i += 3) {
			verts[i + 0] = matrix[0] * vertices[i + 0] + matrix[ 4] * vertices[i + 1] + matrix[ 8] * vertices[i + 2] + matrix[12];
			verts[i + 1] = matrix[1] * vertices[i + 0] + matrix[ 5] * vertices[i + 1] + matrix[ 9] * vertices[i + 2] + matrix[13];
			verts[i + 2] = matrix[2] * vertices[i + 0] + matrix[ 6] * vertices[i + 1] + matrix[10] * vertices[i + 2] + matrix[14];
		}
		return intersectInternal(numberOfVertices, origin, direction, verts);
	}

	public boolean intersect(Vector3f origin, Vector3f direction, float scaleFactor) {
		float [] verts = new float[vertices.length];
		float [] matrix = getMatrix();
		for (int i = 0; i < numberOfVertices * 3; i += 3) {
			verts[i + 0] = matrix[0] * (vertices[i + 0] * scaleFactor) + matrix[ 4] * (vertices[i + 1] * scaleFactor) + matrix[ 8] * (vertices[i + 2] * scaleFactor) + matrix[12];
			verts[i + 1] = matrix[1] * (vertices[i + 0] * scaleFactor) + matrix[ 5] * (vertices[i + 1] * scaleFactor) + matrix[ 9] * (vertices[i + 2] * scaleFactor) + matrix[13];
			verts[i + 2] = matrix[2] * (vertices[i + 0] * scaleFactor) + matrix[ 6] * (vertices[i + 1] * scaleFactor) + matrix[10] * (vertices[i + 2] * scaleFactor) + matrix[14];
		}
		return intersectInternal(numberOfVertices, origin, direction, verts);
	}

	public static final String debugMatrix(float [] matrix, boolean cr) {
		matrixString[0] += String.format("[%+07.4f %+07.4f %+07.4f %+07.4f  ", matrix[ 0], matrix[ 4], matrix[ 8], matrix[12]);
		matrixString[1] += String.format(" %+07.4f %+07.4f %+07.4f %+07.4f  ", matrix[ 1], matrix[ 5], matrix[ 9], matrix[13]);
		matrixString[2] += String.format(" %+07.4f %+07.4f %+07.4f %+07.4f  ", matrix[ 2], matrix[ 6], matrix[10], matrix[14]);
		matrixString[3] += String.format(" %+07.4f %+07.4f %+07.4f %+07.4f] ", matrix[ 3], matrix[ 7], matrix[11], matrix[15]);
		if (cr) {
			return matrixString[0] + "\n" + matrixString[1] + "\n" + matrixString[2] + "\n" + matrixString[3];
		}
		return "";
	}

	public void orientTowards(float x, float y, float z, float ux, float uy, float uz) {
		v0.x = x;
		v0.y = y;
		v0.z = z;
		v1.x = ux;
		v1.y = uy;
		v1.z = uz;
		ai.orient(v0, v1, 0);
	}

	public float orientTowards(GraphicObject object, float deltaTime) {
		object.getUpVector().copy(v0);
//		v0.negate();
		return ai.orient(object.getPosition(), v0, deltaTime);
	}

	public float orientTowards(GraphicObject object, Vector3f up, float deltaTime) {
		return ai.orient(object.getPosition(), up, deltaTime);
	}

	public float orientTowardsUsingRollPitch(GraphicObject object, Vector3f up, float deltaTime) {
		return ai.orientUsingRollPitchOnly(object.getPosition(), up, deltaTime);
	}

	public void setRandomOrientation(Vector3f origin, Vector3f up) {
		up.copy(v0);
		v1.x = (float) (0.7 - Math.random() * 1.4);
		v1.y = (float) (0.7 - Math.random() * 1.4);
		v1.z = (float) (0.7 - Math.random() * 1.4);
		MathHelper.getRandomPosition(origin, v1, 16384.0f, 8192.0f);
		ai.orient(v1, v0, 0);
	}

	final void updateInternals() {
		computeMatrix();
	}

	final void computeInternals() {
		extractVectors();
	}

	public void update(float deltaTime) {
		ai.update(deltaTime);
		if (Settings.engineExhaust && exhaust != null) {
			for (EngineExhaust ex: exhaust) {
				ex.update();
			}
		}
	}

	public void setAIState(AIState newState, Object ...data) {
		ai.setState(newState, data);
	}

	public AIState getAIState() {
		return ai.getState();
	}

	public String getCurrentAIStack() {
		return ai.getStateStack();
	}

	public int getNumberOfLasers() {
		return laserHardpoints.size() / 3;
	}

	public float getLaserX(int i) {
		return laserHardpoints.get(i * 3);
	}

	public float getLaserY(int i) {
		return laserHardpoints.get(i * 3 + 1);
	}

	public float getLaserZ(int i) {
		return laserHardpoints.get(i * 3 + 2);
	}

	private static SpaceObject createGalaxyLocalShip(final Alite alite, int extraShip, int which) {
		// From the initial seed of galaxy 1, retain only the first four bits (0x5).
		// Now, if this is done for each of the "official" 8 galaxies, the galaxies 1-8 have those
		// values: 0x5, 0xB, 0x6, 0xD, 0xA, 0x4, 0x9, and 0x2. Now, these "identifiers" have been
		// paired randomly with the remaining 8 numbers. This means that additional ships will be
		// computed as follows:
		// The first four bits of the galaxy seed have the value...
		//  5 or  8: No additional ships.
		// 11 or 12: Additional ships for "Galaxy 2" (Cottonmouth and Hognose)
		//  6 or  7: Additional ships for "Galaxy 3" (Boomslang and Lora)
		// 13 or 14: Additional ships for "Galaxy 4" (Gopher and Mussurana)
		// 10 or 15: Additional ships for "Galaxy 5" (Bushmaster and Indigo)
		//  4 or  0: Additional ships for "Galaxy 6" (Coral and Yellowbelly)
		//  9 or  1: Additional ships for "Galaxy 7" (Dugite and Lyre)
		//  2 or  3: Additional ships for "Galaxy 8" (Harlequin and Rattlesnake)
		if (extraShip == 11 || extraShip == 12) {
			return which == 0 ? new Cottonmouth(alite) : new Hognose2(alite);  // Galaxy 2
		}
		if (extraShip == 6 || extraShip == 7) {
			return which == 0 ? new Boomslang(alite) : new Lora(alite);        // Galaxy 3
		}
		if (extraShip == 13 || extraShip == 14) {
			return which == 0 ? new Gopher(alite) : new Mussurana(alite);      // Galaxy 4
		}
		if (extraShip == 10 || extraShip == 15) {
			return which == 0 ? new Bushmaster(alite) : new Indigo(alite);     // Galaxy 5
		}
		if (extraShip == 4 || extraShip == 0) {
			return which == 0 ? new Coral(alite) : new Yellowbelly(alite);     // Galaxy 6
		}
		if (extraShip == 9 || extraShip == 1) {
			return which == 0 ? new Dugite(alite) : new Lyre(alite);           // Galaxy 7
		}
		if (extraShip == 2 || extraShip == 3) {
			return which == 0 ? new Harlequin(alite) : new Rattlesnake(alite); // Galaxy 8
		}
		return null;
	}

	public static SpaceObject createRandomEnemy(final Alite alite) {
		int extraShip = ((int) alite.getGenerator().getCurrentSeed()[0] >> 12);
		int type = (int) (Math.random() * 9 + (extraShip == 5 || extraShip == 8 ? 0 : 2));
		AliteLog.d("Ship Statistics", "Returning extraShip == " + extraShip + " -- type == " + type);
		if (type == 9 || type == 10) {
			SpaceObject result = createGalaxyLocalShip(alite, extraShip, type - 9);
			if (result == null) {
				// Fallback routine for galaxy local ships that do not yet exist.
				type = (int) (Math.random() * 9);
			} else {
				return result;
			}
		}
		switch (type) {
			case 0: return new Adder(alite);
			case 1: return new AspMkII(alite);
			case 2: return new BoaClassCruiser(alite);
			case 3: return new Gecko(alite);
			case 4: return new Krait(alite);
			case 5: return new Mamba(alite);
			case 6: return new MorayStarBoat(alite);
			case 7: return new Sidewinder(alite);
			case 8: return new WolfMkII(alite);
			case 9: return createGalaxyLocalShip(alite, extraShip, 0);
			case 10: return createGalaxyLocalShip(alite, extraShip, 1);
		}
		return new Krait(alite);
	}

	private static SpaceObject createGalaxyLocalDefensiveShip(final Alite alite, int extraShip, int which) {
		if (extraShip == 11 || extraShip == 12) {
			return new Cottonmouth(alite);
		}
		if (extraShip == 13 || extraShip == 14) {
			return which == 0 ? new Gopher(alite) : new Mussurana(alite);      // Galaxy 4
		}
		if (extraShip == 10 || extraShip == 15) {
			return which == 0 ? new Bushmaster(alite) : new Indigo(alite);     // Galaxy 5
		}
		if (extraShip == 4 || extraShip == 0) {
			return new Coral(alite);     // Galaxy 6
		}
		if (extraShip == 9 || extraShip == 1) {
			return new Lyre(alite);           // Galaxy 7
		}
		return null;
	}

	public static SpaceObject createRandomDefensiveShip(final Alite alite) {
		int extraShip = ((int) alite.getGenerator().getCurrentSeed()[0] >> 12);
		int type = (int) (Math.random() * 10 + (extraShip == 5 || extraShip == 8 ? 0 : 2));
		if (type > 9) {
			SpaceObject result = createGalaxyLocalDefensiveShip(alite, extraShip, type - 10);
			if (result == null) {
				type = (int) (Math.random() * 10);
			} else {
				return result;
			}
		}
		switch (type) {
			case 0: return new CobraMkIII(alite);
			case 1: return new Krait(alite);
			case 2: return new Adder(alite);
			case 3: return new Mamba(alite);
			case 4: return new FerDeLance(alite);
			case 5: return new CobraMkI(alite);
			case 6: return new Python(alite);
			case 7: return new AspMkII(alite);
			case 8: return new Sidewinder(alite);
			case 9: return new WolfMkII(alite);
		}
		return new AspMkII(alite);
	}
	
	public static SpaceObject createRandomTrader(final Alite alite) {
		int type = (int) (Math.random() * 5);
		switch (type) {
			case 0: return new Anaconda(alite);
			case 1: return new CobraMkI(alite);
			case 2: return new CobraMkIII(alite);
			case 3: return new FerDeLance(alite);
			case 4: return new Python(alite);
		}
		return new CobraMkIII(alite);
	}

	// The Anaconda is too big for the station, so -- better not let it fly from its docking bay :)
	public static SpaceObject createRandomTraderWithoutAnaconda(final Alite alite) {
		int type = (int) (Math.random() * 4);
		switch (type) {
			case 0: return new CobraMkI(alite);
			case 1: return new CobraMkIII(alite);
			case 2: return new FerDeLance(alite);
			case 3: return new Python(alite);
		}
		return new CobraMkIII(alite);
	}

	public static SpaceObject createRandomAsteroid(final Alite alite) {
		int type = (int) (Math.random() * 2);
		switch (type) {
			case 0: return new Asteroid1(alite);
			case 1: return new Asteroid2(alite);
		}
		return new Asteroid1(alite);
	}

	public void aiStateCallback(AiStateCallback type) {
		AiStateCallbackHandler handler = aiStateCallbackHandlers.get(type);
		if (handler != null) {
			handler.execute(this);
		}
	}

	public void executeHit(SpaceObject player) {
		ai.executeHit(player);
	}

	public void registerAiStateCallbackHandler(AiStateCallback type, AiStateCallbackHandler handler) {
		aiStateCallbackHandlers.put(type, handler);
	}

	public void clearAiStateCallbackHandler(AiStateCallback type) {
		aiStateCallbackHandlers.remove(type);
	}

	public int getBounty() {
		return bounty;
	}

	public int getScore() {
		return score;
	}

	public ShipType getShipType() {
		return shipType;
	}

	public boolean avoidObstacles() {
		return true;
	}

	public String toString() {
		return getName();
	}

	public int getCargoCanisterOverrideCount() {
		return cargoCanisterCount;
	}

	public void setCargoCanisterCount(int count) {
		cargoCanisterCount = count;
	}

	public void setIgnoreSafeZone(boolean b) {
		ignoreSafeZone = true;
	}

	public boolean isIgnoreSafeZone() {
		return ignoreSafeZone;
	}

	public long getLaserColor() {
		return laserColor;
	}

	public String getLaserTexture() {
		return laserTexture;
	}

	public boolean isIdentified() {
		return identified;
	}

	public void setIdentified() {
		identified = true;
	}

	public void setHudColor(Vector3f hudColor) {
		hudColor.copy(overrideColor);
	}

	public boolean hasOverrideColor() {
		return overrideColor.lengthSq() > 0.005f;
	}
	
	public Vector3f getOverrideColor() {
		return overrideColor;
	}
}
