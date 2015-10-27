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

import java.io.Serializable;
import java.util.List;

import android.graphics.Rect;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.model.PlayerCobra;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

class ObjectPicker implements Serializable {
	private static final long serialVersionUID = -4145805906299449658L;

	private final Vector3f viewVector = new Vector3f(0, 0, 0);
	private final Vector3f upVector = new Vector3f(0, 0, 0);
	private final Vector3f hVector = new Vector3f(0, 0, 0);
	private final Vector3f vVector = new Vector3f(0, 0, 0);
	private final Vector3f rayOrigin = new Vector3f(0, 0, 0);
	private final Vector3f rayDirection = new Vector3f(0, 0, 0);
	private final Vector3f tempVector = new Vector3f(0, 0, 0);
	
	private final InGameManager inGame;
	private final float radians = (float) (45.0f * Math.PI / 180f);
	private final float halfHeight = (float) (Math.tan(radians / 2) * 1.0f);
	private final float halfScaledAspectRatio;
	private final float halfWindowWidth;
	private final float halfWindowHeight;
	private final float windowWidth;
	private final float windowHeight;
	
	ObjectPicker(InGameManager inGame, Rect visibleArea) {
		this.inGame = inGame;
		halfScaledAspectRatio = halfHeight * inGame.getAspectRatio();
		windowWidth = visibleArea.width();
		windowHeight = visibleArea.height();
		halfWindowWidth = windowWidth / 2.0f;
		halfWindowHeight = windowHeight / 2.0f;
	}
	
	private void initializeVectors() {
		switch (inGame.getViewDirection()) {
			case PlayerCobra.DIR_FRONT: inGame.getShip().getUpVector().copy(upVector);
					                    inGame.getShip().getForwardVector().copy(viewVector);
					                    break;
			case PlayerCobra.DIR_RIGHT: inGame.getShip().getUpVector().copy(upVector);
					                    inGame.getShip().getRightVector().copy(viewVector);
					                    viewVector.negate();
					                    break;
			case PlayerCobra.DIR_REAR:	inGame.getShip().getUpVector().copy(upVector);
					                    inGame.getShip().getForwardVector().copy(viewVector);
					                    viewVector.negate();
					                    break;
			case PlayerCobra.DIR_LEFT:	inGame.getShip().getUpVector().copy(upVector);
					                    inGame.getShip().getRightVector().copy(viewVector);
					                    break;
		}
		viewVector.cross(upVector, hVector);
		hVector.normalize();
		hVector.cross(viewVector, vVector);
		vVector.normalize();
		vVector.negate();
		vVector.scale(halfHeight);
		hVector.scale(halfScaledAspectRatio);
	}
	
	SpaceObject handleIdentify(int x, int y, final List <DepthBucket> sortedObjectsToDraw) {	
		initializeVectors();
		x = (int) (((float) x * windowWidth) / 1920.0f);
		y = (int) (((float) y * windowHeight) / 1080.0f);
		float xWorldPos = (x - halfWindowWidth) / halfWindowWidth;
		float yWorldPos = (halfWindowHeight - y) / halfWindowHeight;
				
		inGame.getShip().getPosition().add(viewVector, rayOrigin);
		hVector.scale(xWorldPos);
		vVector.scale(yWorldPos);
		rayOrigin.add(hVector);
		rayOrigin.add(vVector);
		rayOrigin.sub(inGame.getShip().getPosition(), rayDirection);
		rayDirection.normalize();
		rayDirection.negate();
		
		float minD = Float.MAX_VALUE;
		SpaceObject identifiedObject = null;
		for (DepthBucket db: sortedObjectsToDraw) {
			for (AliteObject object: db.sortedObjects) {
				if (object instanceof SpaceObject) {
					if (((SpaceObject) object).isCloaked()) {
						continue;
					}
					float radius = object.getBoundingSphereRadius();
					if (((SpaceObject) object).getType() != ObjectType.SpaceStation) {
						float distSq = inGame.getShip().getPosition().distanceSq(object.getPosition());
						float scale = distSq < 25000000 ? 1.0f : distSq / 25000000;
						if (scale > 3.0f) {
							scale = 3.0f;
						}
						radius *= scale;
					}
										
					float d = LaserManager.computeIntersectionDistance(rayDirection, rayOrigin, object.getPosition(), radius, tempVector);
					if (d > 0) {
						d += radius;
						if (d < minD) {
							AliteLog.d("Picking", "Object " + object.getName() + " identified at " + d + " - BSR: " + radius);
							minD = d;
							identifiedObject = (SpaceObject) object;
						}
					}
				}
			}
		}
		
		if (identifiedObject != null) {
			identifiedObject.setIdentified();
		}
		return identifiedObject;
	}
}
