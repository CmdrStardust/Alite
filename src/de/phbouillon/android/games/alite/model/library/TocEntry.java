package de.phbouillon.android.games.alite.model.library;

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

import java.util.ArrayList;
import java.util.List;

public class TocEntry {
	private List <TocEntry> children;
	private TocEntry parent;
	
	private String name;
	private LibraryPage linkedPage;
	
	public TocEntry(final String name, final LibraryPage linkedPage, final TocEntry parent) {
		this.name = name;
		this.linkedPage = linkedPage;
		this.parent = parent;
		this.children = new ArrayList<TocEntry>();
	}
	
	void addChild(TocEntry child) {
		this.children.add(child);
	}
	
	public TocEntry [] getChildren() {
		return children.toArray(new TocEntry[0]);
	}
	
	public String getName() {
		return name;
	}
	
	public TocEntry getParent() {
		return parent;
	}
	
	public LibraryPage getLinkedPage() {
		return linkedPage;
	}
}
