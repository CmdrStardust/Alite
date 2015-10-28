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
import de.phbouillon.android.framework.impl.gl.Sphere;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.screens.opengl.IAdditionalGLParameterSetter;
import de.phbouillon.android.games.alite.screens.opengl.sprites.SpriteData;

public class SphericalSpaceObject extends AliteObject implements Geometry, Serializable {
	private static final long serialVersionUID = 5293882896307129631L;

	private final Sphere sphere;
	private final Vector3f hudColor = new Vector3f(1.0f, 1.0f, 1.0f);
	private boolean visibleOnHud = true;
	private IAdditionalGLParameterSetter additionalParameters = null;
	protected final float [] displayMatrix = new float[16];
	private float scaleFactor = 1.0f;
	
	public SphericalSpaceObject(Alite alite, String name, float radius, int precision, String texture) {
		super(name);
		sphere = new Sphere(alite, radius, 32, 32, texture, null, false);
		boundingSphereRadius = radius;
	}
	
	public SphericalSpaceObject(Alite alite, String name, float radius, int precision, String texture, SpriteData spriteData) {
		super(name);
		sphere = new Sphere(alite, radius, 32, 32, texture, spriteData, false);
		boundingSphereRadius = radius;
	}
	
	public float getRadius() {
		return sphere.getRadius();
	}
	
	public void setNewSize(float radius) {
		sphere.setNewSize(radius);
		boundingSphereRadius = radius;
	}
	
	@Override
	public void render() {
		if (additionalParameters != null) {
			additionalParameters.setUp();
		}
		sphere.render();
		if (additionalParameters != null) {
			additionalParameters.tearDown();
		}
	}

	public void drawArrays() {
		sphere.drawArrays();
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
		return sphere.getRadius();
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

	public void setColor(float r, float g, float b, float a) {
		sphere.setColor(r, g, b, a);
	}
	
	public float getR() { return sphere.getR(); };
	public float getG() { return sphere.getG(); };
	public float getB() { return sphere.getB(); };
	public float getA() { return sphere.getA(); };
	
	public void setScaleFactor(float sf) {
		this.scaleFactor = sf;
	}
	
	public float getScaleFactor() {
		return scaleFactor;
	}
}
