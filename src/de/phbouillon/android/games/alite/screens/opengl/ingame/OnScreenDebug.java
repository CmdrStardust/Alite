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

import java.util.List;

import android.opengl.GLES11;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;
import de.phbouillon.android.games.alite.screens.opengl.objects.ObjectUtils;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class OnScreenDebug {
	public static final void displayDockingAlignment(Alite alite, GraphicObject ship, SpaceObject spaceStation, float distanceSq) {
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		alite.getFont().drawText("Dist: " + distanceSq, 400, 50, false, 1.0f);
		float fz = spaceStation.getDisplayMatrix()[10];
		if (fz < 0.98f) {
			GLES11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		} else {
			GLES11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		}
		alite.getFont().drawText("fz: " + fz, 400, 90, false, 1.0f);

		float angle = ship.getForwardVector().angleInDegrees(spaceStation.getForwardVector());
		if (Math.abs(angle) > 15.0f) {
			GLES11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		} else {
			GLES11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		}
		alite.getFont().drawText(String.format("%4.2f", angle), 400, 130, false, 1.0f);
		
		float ux = Math.abs(spaceStation.getDisplayMatrix()[4]);
		if (ux < 0.95f) {
			GLES11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		} else {
			GLES11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		}
		alite.getFont().drawText("ux: " + ux, 400, 170, false, 1.0f);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public static final void debugDocking(final Alite alite, final GraphicObject ship, final List <DepthBucket> sortedObjectsToDraw) {
		for (DepthBucket depthBucket: sortedObjectsToDraw) {
			for (AliteObject object: depthBucket.sortedObjects) {
				if (object instanceof SpaceObject && ((SpaceObject) object).getType() == ObjectType.SpaceStation) {
					float distanceSq = ObjectUtils.computeDistanceSq(object, ship); 
					if (distanceSq < 64000000) {
						displayDockingAlignment(alite, ship, (SpaceObject) object, distanceSq);
					}
					return;
				}
			}
		}
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public static final void debugHitArea(final Alite alite, final SpaceObject enemy, float scaleFactor) {
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		alite.getFont().drawText("Hit area of " + enemy.getName() + ": " + 
		String.format("%3.2f", (scaleFactor * 100.0f)) + "%", 960, 50, false, 1.0f);
	}
	
	public static final void debugBuckets(Alite alite, final List <DepthBucket> sortedObjectsToDraw) {
		int depthBucketIndex = 1;
		for (DepthBucket depthBucket: sortedObjectsToDraw) {
			String objectsString = "";
			for (AliteObject o: depthBucket.sortedObjects) {
				objectsString += o.getName() + ", ";
			}
			String debugString = "Bucket " + depthBucketIndex + ": " + String.format("%7.2f", depthBucket.near) + ", " + String.format("%7.2f", depthBucket.far) + ": " + objectsString;
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			alite.getFont().drawText(debugString, 200, 10 + 40 * (depthBucketIndex - 1), false, 0.8f);
			depthBucketIndex++;
		}
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public static final void debugFPS(Alite alite) {
		GLES11.glColor4f(0.9f, 0.7f, 0.0f, 1.0f);
		alite.getFont().drawText(String.format("FPS: %3.1f", AndroidGame.fps), 400, 10, false, 1.0f);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public static final void debugMessage(Alite alite, String msg) {
		GLES11.glColor4f(0.9f, 0.7f, 0.0f, 1.0f);
		alite.getFont().drawText(msg, 400, 10, false, 1.0f);
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}	
}
