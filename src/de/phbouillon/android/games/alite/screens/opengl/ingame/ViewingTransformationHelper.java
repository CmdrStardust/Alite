package de.phbouillon.android.games.alite.screens.opengl.ingame;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.opengl.GLES11;
import android.opengl.Matrix;
import de.phbouillon.android.framework.impl.Pool;
import de.phbouillon.android.framework.impl.Pool.PoolObjectFactory;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject.ZPositioning;
import de.phbouillon.android.games.alite.screens.opengl.objects.LaserCylinder;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class ViewingTransformationHelper implements Serializable {
	private static final long serialVersionUID = -4312341410231970141L;
	private static final boolean USE_DEPTH_BUCKETS = true;
	
	private List <DistanceObjectPair> distancePairs = new ArrayList<DistanceObjectPair>();
	class DistanceFactory implements PoolObjectFactory<DistanceObjectPair> {
		private static final long serialVersionUID = 175831326802438855L;

		@Override
		public DistanceObjectPair createObject() {
			return new DistanceObjectPair(0.0f, null);
		}		
	}	
	private transient PoolObjectFactory <DistanceObjectPair> distanceFactory = new DistanceFactory();
	private transient Pool <DistanceObjectPair> distancePairPool = new Pool<DistanceObjectPair>(distanceFactory, 1000);
	
	class BucketFactory implements PoolObjectFactory<DepthBucket> {
		private static final long serialVersionUID = -3330249489769269321L;

		@Override
		public DepthBucket createObject() {
			return new DepthBucket(0, 0);
		}		
	}
	private transient PoolObjectFactory <DepthBucket> bucketFactory = new BucketFactory();
	private transient Pool <DepthBucket> bucketPool = new Pool<DepthBucket>(bucketFactory, 50);
	
	private class DistanceObjectPair implements Serializable {
		private static final long serialVersionUID = -8375585740258957162L;
		
		float distance;
		AliteObject object;
		
		DistanceObjectPair(float distance, AliteObject object) {
			this.distance = distance;
			this.object = object;
		}
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "ViewingTransformationHelper.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "ViewingTransformationHelper.readObject I");
			distanceFactory = new DistanceFactory();
			distancePairPool = new Pool<DistanceObjectPair>(distanceFactory, 1000);
			distancePairPool.reset();
			bucketFactory = new BucketFactory();
			bucketPool = new Pool<DepthBucket>(bucketFactory, 50);
			bucketPool.reset();
			objectPairComparator = new DistanceObjectPairComparator();
			distancePairs.clear();
			AliteLog.e("readObject", "ViewingTransformationHelper.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	class DistanceObjectPairComparator implements Comparator <DistanceObjectPair>, Serializable {
		private static final long serialVersionUID = -5924643772472050335L;

		@Override
		public int compare(DistanceObjectPair lhs, DistanceObjectPair rhs) {
			switch (lhs.object.getZPositioningMode()) {
				case Front:  if (rhs.object.getZPositioningMode() == ZPositioning.Front) { return Float.compare(rhs.distance, lhs.distance); } else { return 1; }
				case Normal: if (rhs.object.getZPositioningMode() == ZPositioning.Front) { return -1; } else if (rhs.object.getZPositioningMode() == ZPositioning.Back) { return 1; } else { return Float.compare(rhs.distance, lhs.distance); }
				case Back:   if (rhs.object.getZPositioningMode() == ZPositioning.Back)  { return Float.compare(rhs.distance, lhs.distance); } else { return -1; }
			}
			return Float.compare(rhs.distance, lhs.distance);
		}		
	}
	
	private Comparator<DistanceObjectPair> objectPairComparator = new DistanceObjectPairComparator();
	
	private final void applyRightViewDirection(final float [] viewMatrix) {
		float temp;
		
		temp = viewMatrix[12];
		viewMatrix[12] = viewMatrix[14];
		viewMatrix[14] = -temp;
    
		temp = viewMatrix[0];
		viewMatrix[0] = viewMatrix[2];
		viewMatrix[2] = -temp;
    
		temp = viewMatrix[4];
		viewMatrix[4] = viewMatrix[6];
		viewMatrix[6] = -temp;
    
		temp = viewMatrix[8];
		viewMatrix[8] = viewMatrix[10];
		viewMatrix[10] = -temp;				    		
	}
	
	private final void applyRearViewDirection(final float [] viewMatrix) {
		viewMatrix[12] = -viewMatrix[12];
	    viewMatrix[14] = -viewMatrix[14];
	    
	    viewMatrix[0] = -viewMatrix[0];
	    viewMatrix[2] = -viewMatrix[2];
	    
	    viewMatrix[4] = -viewMatrix[4];
	    viewMatrix[6] = -viewMatrix[6];
	    
	    viewMatrix[8] = -viewMatrix[8];
	    viewMatrix[10] = -viewMatrix[10];		
	}
	
	private final void applyLeftViewDirection(final float [] viewMatrix) {
		float temp;
		
		temp = viewMatrix[12];
        viewMatrix[12] = -viewMatrix[14];
        viewMatrix[14] = temp;
        
        temp = viewMatrix[0];
        viewMatrix[0] = -viewMatrix[2];
        viewMatrix[2] = temp;
        
        temp = viewMatrix[4];
        viewMatrix[4] = -viewMatrix[6];
        viewMatrix[6] = temp;
        
        temp = viewMatrix[8];
        viewMatrix[8] = -viewMatrix[10];
        viewMatrix[10] = temp;		
	}
	
	final void applyViewDirection(int viewDirection, final float [] viewMatrix) {
		switch (viewDirection) {
			case 1: applyRightViewDirection(viewMatrix); break;
			case 2: applyRearViewDirection(viewMatrix);  break;
			case 3: applyLeftViewDirection(viewMatrix);  break;
		}
		GLES11.glLoadMatrixf(viewMatrix, 0);
	}

	private final void sortObjects(final List <? extends AliteObject> objects, final float [] viewMatrix, final float [] tempMatrix, boolean witchSpace, SpaceObject ship) {
		for (AliteObject eo: objects) {
			if (witchSpace && (eo.getName().equals("Planet") ||
					           eo.getName().equals("Sun") ||
					           (eo instanceof SpaceObject && ((SpaceObject) eo).getType() == ObjectType.SpaceStation) ||
					           eo.getName().equals("Glow"))) {
				continue;
			}
			Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, eo.getMatrix(), 0);
			DistanceObjectPair distancePair = distancePairPool.newObject();
			distancePair.distance = Math.abs(tempMatrix[14]);
			distancePair.object = eo;
			distancePairs.add(distancePair);
		}			
	}
	
	private final void createSingleBucket(final List <DepthBucket> sortedObjectsToDraw) {
		DepthBucket currentBucket = bucketPool.newObject();
		currentBucket.sortedObjects.clear();
		currentBucket.near = 1.0f;
		currentBucket.far = 1000000000f;
		currentBucket.spaceObjectCount = 0;
			
		for (DistanceObjectPair p: distancePairs) {
			currentBucket.sortedObjects.add(p.object);
			currentBucket.spaceObjectCount += p.object instanceof SpaceObject ? 1 : 0;
		}
		sortedObjectsToDraw.add(currentBucket);
	}
	
	private final void partitionDepths(final List <DepthBucket> sortedObjectsToDraw) {
		DepthBucket currentBucket = null;
		DepthBucket behind = bucketPool.newObject();
		behind.near = -1;
		behind.far = -1;
		behind.spaceObjectCount = 0;
		behind.sortedObjects.clear();
		
		for (DistanceObjectPair p: distancePairs) {
			if (p.object != null) {
				float dist = p.distance;
				float size = p.object.getBoundingSphereRadius();
				if (Settings.targetBox && p.object instanceof SpaceObject) {
					size *= 1.3f;
				}
				if (dist < -size) {
					behind.sortedObjects.add(p.object);
					if (p.object instanceof SpaceObject) {
						behind.spaceObjectCount++;
					}
					continue;
				}
				float near = dist - size;
				float far = dist + size;
				if (near < 1.0f && far > 1.0f) {
					near = 1.0f;
				}
				if (currentBucket == null) {					
					currentBucket = bucketPool.newObject();
					currentBucket.sortedObjects.clear();
					currentBucket.near = near;
					currentBucket.far = far;
					currentBucket.spaceObjectCount = p.object instanceof SpaceObject ? 1 : 0;
					currentBucket.sortedObjects.add(p.object);		
				} else {					
					if (far > currentBucket.near) {
						currentBucket.sortedObjects.add(p.object);
						if (p.object instanceof SpaceObject) {
							currentBucket.spaceObjectCount++;
						}
						if (currentBucket.near > near) {
							currentBucket.near = near;
						}
						if (currentBucket.far < far) {
							currentBucket.far = far;
						}
					} else {
						sortedObjectsToDraw.add(currentBucket);
						float newFar = currentBucket.near;
						currentBucket = bucketPool.newObject();
						currentBucket.near = near;
						currentBucket.far = newFar;
						currentBucket.sortedObjects.clear();
						currentBucket.sortedObjects.add(p.object);						
						currentBucket.spaceObjectCount = p.object instanceof SpaceObject ? 1 : 0;
					}					
				}
			} 
		}	
		if (currentBucket != null) {
			sortedObjectsToDraw.add(currentBucket);
		}
		if (behind.sortedObjects.size() > 0) {
			sortedObjectsToDraw.add(behind);
		} else {
			bucketPool.free(behind);
		}
	}
	
	final void clearObjects(final List <DepthBucket> sortedObjectsToDraw) {
		if (sortedObjectsToDraw == null) {
			return;
		}
		for (DepthBucket bucket: sortedObjectsToDraw) {
			bucketPool.free(bucket);
		}
		sortedObjectsToDraw.clear();
	}
	
	final void sortObjects(final List <AliteObject> objects, final float [] viewMatrix, final float [] tempMatrix, List <LaserCylinder> lasers, final List <DepthBucket> sortedObjectsToDraw, boolean witchSpace, SpaceObject ship) {		
		for (DistanceObjectPair dop: distancePairs) {
			distancePairPool.free(dop);
		}
		distancePairs.clear();
		sortObjects(objects, viewMatrix, tempMatrix, witchSpace, ship);
		sortObjects(lasers, viewMatrix, tempMatrix, witchSpace, ship);
		Collections.sort(distancePairs, objectPairComparator);
		sortedObjectsToDraw.clear();
		if (USE_DEPTH_BUCKETS) {
			partitionDepths(sortedObjectsToDraw);
		} else {
			createSingleBucket(sortedObjectsToDraw);
		}
	}
}
