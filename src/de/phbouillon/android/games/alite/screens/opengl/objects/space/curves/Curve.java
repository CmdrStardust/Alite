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

import java.io.Serializable;

import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.screens.opengl.objects.AliteObject;

public abstract class Curve extends AliteObject implements Serializable {
	protected CurveParameter px;
	protected CurveParameter py;
	protected CurveParameter pz;
	protected CurveParameter rx;
	protected CurveParameter ry;
	protected CurveParameter rz;
	
	protected float totalLength;
	
	protected final Vector3f position = new Vector3f(0, 0, 0);
	protected final Vector3f rotation = new Vector3f(0, 0, 0);
	protected final Vector3f cForward = new Vector3f(0, 0, 0);
	protected final Vector3f cRight   = new Vector3f(0, 0, 0);
	protected final Vector3f cUp      = new Vector3f(0, 0, 0);
		
	protected Curve(String name) {
		super(name);
	}
	
	public boolean reachedEnd() {
		return px.reachedEnd() || py.reachedEnd() || pz.reachedEnd();
	}
	
	protected void initialize(CurveParameter px, CurveParameter py, CurveParameter pz,
		     CurveParameter rx, CurveParameter ry, CurveParameter rz) {
		this.px = px;
		this.py = py;
		this.pz = pz;
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
		extractVectors();
		forwardVector.copy(cForward);
		rightVector.copy(cRight);
		cRight.negate();
		upVector.copy(cUp);
	}	
	
	public void compute(float t) {
		
		// R vector um rotation z rotieren und dann up berechnen....
		
		position.x = px.getValue(t);
		position.y = py.getValue(t);
		position.z = pz.getValue(t);
		rotation.x = rx.getValue(t);
		rotation.y = ry.getValue(t);
		rotation.z = rz.getValue(t);
		position.mulMat(getMatrix());
		boolean pxE = px.end;
		boolean pyE = py.end;
		boolean pzE = pz.end;
		cForward.x = px.getValue(t + 0.03f);
		cForward.y = py.getValue(t + 0.03f);
		cForward.z = pz.getValue(t + 0.03f);
		cForward.mulMat(getMatrix());
		cForward.sub(position);
		cForward.negate();
		cForward.normalize();		
		cForward.cross(cRight, cUp);
		cUp.normalize();
		px.end = pxE;
		py.end = pyE;
		pz.end = pzE;
	}

	public void compute(float t, boolean checkEnd) {
		boolean pxE = px.end;
		boolean pyE = py.end;
		boolean pzE = pz.end;
		compute(t);
		if (!checkEnd) {
			px.end = pxE;
			py.end = pyE;
			pz.end = pzE;
		}
	}

	public Vector3f getCurvePosition() {
		return position;
	}
	
	public Vector3f getCurveRotation() {		
		return rotation;
	}	
	
	public Vector3f getcForward() {
		return cForward;
	}
	
	public Vector3f getcUp() {
		return cUp;
	}
	
	public Vector3f getcRight() {
		return cRight;
	}
	
	@Override
	public boolean isVisibleOnHud() {
		return false;
	}

	@Override
	public Vector3f getHudColor() {
		return null;
	}		
}
