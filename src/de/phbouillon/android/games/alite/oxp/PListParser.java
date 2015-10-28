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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;

import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;

public class PListParser {
	protected final Alite alite;
	protected final String oxpBasePath;
	protected final String oxpName;
	
	public PListParser(Alite alite, String oxpBasePath, String oxpName) {
		this.alite = alite;
		this.oxpBasePath = oxpBasePath;
		this.oxpName = oxpName;
	}
	
	protected NSDictionary parseFile(String fileName) {
		FileInputStream plist = null;
		try {
			plist = (FileInputStream) alite.getFileIO().readFile(oxpBasePath + fileName);
			NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(plist);
			return rootDict;
		} catch (IOException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (PropertyListFormatException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (ParseException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (SAXException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} finally {
			if (plist != null) {
				try {
					plist.close();
				} catch (IOException e) {
				}
			}
		}		
		return new NSDictionary();
	}
	
	static NSDictionary parseFile(InputStream stream) {
		FileInputStream plist = null;
		try {
			NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(stream);
			return rootDict;
		} catch (IOException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (PropertyListFormatException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (ParseException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} catch (SAXException e) {
			AliteLog.e("Error reading PList", e.getMessage(), e);
		} finally {
			if (plist != null) {
				try {
					plist.close();
				} catch (IOException e) {
				}
			}
		}		
		return new NSDictionary();
	}

}
