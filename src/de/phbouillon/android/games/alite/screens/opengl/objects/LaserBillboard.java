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
import java.util.ArrayList;
import java.util.List;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.model.Laser;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class LaserBillboard extends Billboard implements Serializable {
	private static final long serialVersionUID = -6567821362381241916L;
	private Laser laser;
	private final List<LaserBillboard> twins = new ArrayList<LaserBillboard>();
	private boolean aiming = false;
	private SpaceObject origin;
	
	public LaserBillboard(Alite alite) {
		super("Laser", alite, 0.0f, 0.0f, 0.0f, 16.0f, 16.0f, "textures/lasers.png", alite.getTextureManager().getSprite("textures/lasers.png", "photon1"));
	}

	public void setType(int type) {
		updateTextureCoordinates(alite.getTextureManager().getSprite("textures/lasers.png", "photon" + type));
	}
	
	public Laser getLaser() {
		return laser;
	}
	
	public void setLaser(Laser laser) {
		this.laser = laser;
	}
	
	public List <LaserBillboard> getTwins() {
		return twins;
	}
	
	public void setTwins(LaserBillboard twin1, LaserBillboard twin2) {
		twins.clear();
		twins.add(twin1);
		twins.add(twin2);
	}
	
	public void addTwin(LaserBillboard twin) {
		twins.add(twin);
	}
	
	public void clearTwins() {
		twins.clear();
	}
	
	public boolean isAiming() {
		return aiming;
	}
	
	public void setAiming(boolean aiming) {
		this.aiming = aiming;
	}
	
	public void setOrigin(SpaceObject origin) {
		this.origin = origin;
	}
	
	public SpaceObject getOrigin() {
		return origin;
	}
}
