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

import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.TargetBox;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.IAdditionalGLParameterSetter;

public class TargetBoxSpaceObject extends AliteObject implements Geometry, Serializable {
	private final TargetBox box;
	private final Vector3f hudColor = new Vector3f(1.0f, 1.0f, 1.0f);
	private boolean visibleOnHud = false;
	private IAdditionalGLParameterSetter additionalParameters = null;
	protected final float [] displayMatrix = new float[16];
	
	public TargetBoxSpaceObject(Alite alite, String name, float width, float height, float depth) {
		super(name);
		box = new TargetBox(alite, width, height, depth);
		boundingSphereRadius = (float) Math.sqrt(width * width + height * height + depth * depth);
	}
		
	@Override
	public void render() {
		if (additionalParameters != null) {
			additionalParameters.setUp();
		}
		box.render();
		if (additionalParameters != null) {
			additionalParameters.tearDown();
		}
	}
	
	public void setColor(float r, float g, float b) {
		box.setColor(r, g, b, 1.0f);
	}
	
	public void setColor(float r, float g, float b, float a) {
		box.setColor(r, g, b, a);
	}

	public void setAlpha(float a) {
		box.setAlpha(a);
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
		return hudColor;
	}
	
	public void setHudColor(float r, float g, float b) {
		hudColor.x = r;
		hudColor.y = g;
		hudColor.z = b;
	}

	public void setAdditionalGLParameters(IAdditionalGLParameterSetter additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	@Override
	public float getDistanceFromCenterToBorder(Vector3f dir) {
		return 0.0f;
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
	
	@Override
	public boolean needsDepthTest() {
		return false;
	}
}
