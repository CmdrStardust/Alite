package de.phbouillon.android.games.alite.screens.opengl.objects.space.curves;

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

import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class BreakDown extends Curve {	
	public BreakDown(SpaceObject reference) {
		super("BreakDown");

		setMatrix(reference.getMatrix());	
		applyDeltaRotation(0, 180, 0);
		
		// 500m is the approximate distance between the first two curve points
		float keyTime = 500.0f / reference.getMaxSpeed();
		
		CurveParameterKey key0tx = new CurveParameterKey(0, 0);
		CurveParameterKey key0ty = new CurveParameterKey(0, 0);
		CurveParameterKey key0tz = new CurveParameterKey(0, 0);
		CurveParameterKey key0rx = new CurveParameterKey(0, 0);
		CurveParameterKey key0ry = new CurveParameterKey(0, 0);
		CurveParameterKey key0rz = new CurveParameterKey(0, 0);
		
		CurveParameterKey key1tx = new CurveParameterKey(keyTime, 0);
		CurveParameterKey key1ty = new CurveParameterKey(keyTime, 0);
		CurveParameterKey key1tz = new CurveParameterKey(keyTime, 500);
		CurveParameterKey key1rx = new CurveParameterKey(keyTime, 0);
		CurveParameterKey key1ry = new CurveParameterKey(keyTime, 0);
		CurveParameterKey key1rz = new CurveParameterKey(keyTime, 0);
		
		CurveParameterKey key2tx = new CurveParameterKey(2 * keyTime, 0);
		CurveParameterKey key2ty = new CurveParameterKey(2 * keyTime, -500);
		CurveParameterKey key2tz = new CurveParameterKey(2 * keyTime, 500);
		CurveParameterKey key2rx = new CurveParameterKey(2 * keyTime, 0);
		CurveParameterKey key2ry = new CurveParameterKey(2 * keyTime, 0);
		CurveParameterKey key2rz = new CurveParameterKey(2 * keyTime, -90);

		CurveParameter curveTX = new CurveParameter(key0tx, key1tx, key2tx);
		CurveParameter curveTY = new CurveParameter(key0ty, key1ty, key2ty);
		CurveParameter curveTZ = new CurveParameter(key0tz, key1tz, key2tz);
		CurveParameter curveRX = new ConstCurveParameter(key0rx, key1rx, key2rx);
		CurveParameter curveRY = new ConstCurveParameter(key0ry, key1ry, key2ry);
		CurveParameter curveRZ = new ConstCurveParameter(key0rz, key1rz, key2rz);
		
		totalLength = 2 * keyTime;
		initialize(curveTX, curveTY, curveTZ, curveRX, curveRY, curveRZ);
	}	
}
