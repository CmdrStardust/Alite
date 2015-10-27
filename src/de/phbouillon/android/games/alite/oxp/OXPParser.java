package de.phbouillon.android.games.alite.oxp;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;

public class OXPParser {
	private String identifier;
	private String name;
	private String version;
	private String category;
	private String description;
	private String author;
	private String license;
	
	public OXPParser(String fileName) throws IOException {		
		if (fileName.toLowerCase(Locale.getDefault()).endsWith(".oxz")) {
			parseOXZ(fileName);
		} else {
			parseOXP(fileName);
		}
	}
	
	private String readRequiredString(NSDictionary dict, String parameter, String fileName) throws IOException {
		if (!dict.containsKey(parameter)) {
			throw new IOException("Invalid manifest file. No " + parameter + " given. (" + fileName + ")");
		}
		return ((NSString) dict.get(parameter)).getContent();		
	}
	
	private String readString(NSDictionary dict, String parameter) {
		if (!dict.containsKey(parameter)) {
			return "";
		}
		return ((NSString) dict.get(parameter)).getContent();		
	}

	private void parseOXZ(String fileName) throws IOException {
		ZipResourceFile zip = new ZipResourceFile(fileName);
		InputStream manifestStream = zip.getInputStream("manifest.plist");
		if (manifestStream == null) {
			throw new FileNotFoundException("Cannot read manifest file of " + fileName + ".");
		}
		NSDictionary manifest = PListParser.parseFile(manifestStream);
		if (manifest.isEmpty()) {
			throw new FileNotFoundException("Cannot parse manifest file of " + fileName + ".");
		}
		identifier  = readRequiredString(manifest, "identifier", fileName);
		name        = readRequiredString(manifest, "title", fileName);
		version     = readRequiredString(manifest, "version", fileName);
		category    = readString(manifest, "category");
		description = readString(manifest, "description");
		author      = readString(manifest, "author");
		license     = readString(manifest, "license");
	}
	
	private void parseOXP(String fileName) {
		
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}

	public String getLicense() {
		return license;
	}
}
