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

import de.phbouillon.android.framework.Geometry;
import de.phbouillon.android.framework.impl.gl.Box;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.IAdditionalGLParameterSetter;

public class BoxSpaceObject extends AliteObject implements Geometry {
	private final Box box;
	private final Vector3f hudColor = new Vector3f(1.0f, 1.0f, 1.0f);
	private boolean visibleOnHud = false;
	private IAdditionalGLParameterSetter additionalParameters = null;
	protected final float [] displayMatrix = new float[16];

	public BoxSpaceObject(Alite alite, String name, float width, float height, float depth) {
		super(name);
		box = new Box(alite, width, height, depth);
		boundingSphereRadius = width;
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
	
	public void setFarPlane(Vector3f far) {
		box.setFarPlane(far);
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
	
	public boolean intersect(Vector3f origin, Vector3f direction) {
		float [] vertices = box.getVertices();
		float [] verts = new float[vertices.length];
		float [] matrix = getMatrix();
		for (int i = 0; i < 36 * 3; i += 3) {
			verts[i + 0] = matrix[0] * vertices[i + 0] + matrix[ 4] * vertices[i + 1] + matrix[ 8] * vertices[i + 2] + matrix[12];
			verts[i + 1] = matrix[1] * vertices[i + 0] + matrix[ 5] * vertices[i + 1] + matrix[ 9] * vertices[i + 2] + matrix[13];
			verts[i + 2] = matrix[2] * vertices[i + 0] + matrix[ 6] * vertices[i + 1] + matrix[10] * vertices[i + 2] + matrix[14];
		}
		return intersectInternal(36, origin, direction, verts);
	}
}
