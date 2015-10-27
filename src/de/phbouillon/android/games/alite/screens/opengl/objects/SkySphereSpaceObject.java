package de.phbouillon.android.games.alite.screens.opengl.objects;

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

import android.opengl.GLES11;
import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.Skysphere;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.IAdditionalGLParameterSetter;

public class SkySphereSpaceObject extends AliteObject implements Geometry, Serializable {
	private static final long serialVersionUID = -3204273124041313493L;
	private final Skysphere sphere;
	private final float radius;
	private boolean visibleOnHud = false;
	private IAdditionalGLParameterSetter additionalParameters = null;
	protected final float [] displayMatrix = new float[16];
	
	public SkySphereSpaceObject(Alite alite, String name, float radius, int slices, int stacks, String texture) {
		super(name);
		sphere = new Skysphere(alite, radius, slices, stacks, texture);
		this.radius = radius;
		boundingSphereRadius = 0.0f;
	}
	
	@Override
	public void render() {
		if (additionalParameters != null) {
			additionalParameters.setUp();
		}
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);
		sphere.render();
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		if (additionalParameters != null) {
			additionalParameters.tearDown();
		}
	}
	
	@Override
	public boolean isVisibleOnHud() {
		return visibleOnHud;
	}

	public void setVisibleOnHud(boolean b) {
		visibleOnHud = b;
	}
	
	@Override
	public Vector3f getHudColor() {
		return null;
	}
	
	public void setAdditionalGLParameters(IAdditionalGLParameterSetter additionalParameters) {
		this.additionalParameters = additionalParameters;
	}
	
	public void destroy() {
		if (sphere != null) {
			sphere.destroy();
		}
	}

	@Override
	public float getDistanceFromCenterToBorder(Vector3f dir) {	
		return radius;
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
}
