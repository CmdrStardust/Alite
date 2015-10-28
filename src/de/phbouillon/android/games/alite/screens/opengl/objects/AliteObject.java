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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Vector3f;

public abstract class AliteObject extends GraphicObject implements Serializable {
	private static final long serialVersionUID = -5229181033103145634L;

	public enum ZPositioning {
		Front,
		Normal,
		Back;
	}
	
	protected boolean visible = true;
	protected boolean remove = false;
	protected ZPositioning positionMode = ZPositioning.Normal;
	protected float boundingSphereRadius;
	protected final Map <Integer, DestructionCallback> destructionCallbacks = new LinkedHashMap<Integer, DestructionCallback>();
	private transient boolean saving = false;
	
	protected final Vector3f v0    = new Vector3f(0, 0, 0);
	protected final Vector3f v1    = new Vector3f(0, 0, 0);
	protected final Vector3f v2    = new Vector3f(0, 0, 0);
	protected final Vector3f edge1 = new Vector3f(0, 0, 0);
	protected final Vector3f edge2 = new Vector3f(0, 0, 0);
	protected final Vector3f pvec  = new Vector3f(0, 0, 0);
	protected final Vector3f qvec  = new Vector3f(0, 0, 0);
	protected final Vector3f tvec  = new Vector3f(0, 0, 0);

	public AliteObject(String name) {
		super(name);
	}
	
	public void setSaving(boolean b) {
		saving = b;
		if (saving) {
			destructionCallbacks.clear();
		}
	}
	
	public void addDestructionCallback(final DestructionCallback callback) {
		if (saving) {
			return;
		}
		destructionCallbacks.put(callback.getId(), callback);
	}
	
	public boolean hasDestructionCallback(int id) {
		return destructionCallbacks.containsKey(id);
	}
	
	public Collection <DestructionCallback> getDestructionCallbacks() {
		if (saving) {
			return Collections.emptyList();
		}
		return destructionCallbacks.values();
	}
	
	public boolean needsDepthTest() {
		return true;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setRemove(boolean remove) {		
		this.remove = remove;
	}
	
	public boolean mustBeRemoved() {
		return remove;
	}
	
	public ZPositioning getZPositioningMode() {
		return positionMode;
	}
	
	public void setZPositioningMode(ZPositioning posMode) {
		positionMode = posMode;
	}
	
	public float getBoundingSphereRadius() {
		return boundingSphereRadius;
	}
	
	public void executeDestructionCallbacks() {
		for (DestructionCallback dc: destructionCallbacks.values()) {
			dc.onDestruction();
		}
	}
	
	protected boolean intersectInternal(int numberOfVertices, Vector3f origin, Vector3f direction, float [] verts) {
		for (int i = 0; i < numberOfVertices * 3; i += 9) {
			v0.x = verts[i + 0];
			v0.y = verts[i + 1];
			v0.z = verts[i + 2];
			
			v1.x = verts[i + 3];
			v1.y = verts[i + 4];
			v1.z = verts[i + 5];
			
			v2.x = verts[i + 6];
			v2.y = verts[i + 7];
			v2.z = verts[i + 8];

			v1.sub(v0, edge1);
			v2.sub(v0, edge2);
			direction.cross(edge2, pvec);
			float det = edge1.dot(pvec);
			if (Math.abs(det) < 0.00001f) {
				continue;
			}
			float invDet = 1.0f / det;
			origin.sub(v0, tvec);
			float u = tvec.dot(pvec) * invDet;
			if (u < 0.0f || u > 1.0f) {
				continue;
			}
			tvec.cross(edge1, qvec);
			float v = direction.dot(qvec) * invDet;
			if (v < 0.0f || u + v > 1.0f) {
				continue;
			}
			return true;
		}
		return false;
	}

	
	public abstract boolean isVisibleOnHud();
	public abstract Vector3f getHudColor();
}
