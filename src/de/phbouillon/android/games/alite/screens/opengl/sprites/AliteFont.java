package de.phbouillon.android.games.alite.screens.opengl.sprites;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.opengl.DefaultCoordinateTransformer;
import de.phbouillon.android.games.alite.screens.opengl.ICoordinateTransformer;

public class AliteFont extends Sprite {
	private static final long serialVersionUID = -5374460633489174173L;

	public static ICoordinateTransformer ct;
	
	private transient Alite alite;	
	private static final String TEXTURE_FILE = "textures/font.png";
	
	class CharacterData implements Serializable {
		private static final long serialVersionUID = 3097765273945320958L;

		public final int width;
		public final int height;
		public final float uStart;
		public final float uEnd;
		public final float vStart;
		public final float vEnd;
		
		CharacterData(int width, int height, float u1, float v1, float u2, float v2) {
			this.width = width;
			this.height = height;
			uStart = u1;
			vStart = v1;
			uEnd = u2;
			vEnd = v2;
		}
	}
	
	private final Map <Character, CharacterData> characters = new HashMap<Character, CharacterData>();
	
	public AliteFont(Alite alite) {
		super(alite, 0, 0, 1, 1, 0, 0, 1, 1, TEXTURE_FILE);
		this.alite = alite;
		try {
			initialize();
		} catch (IOException e) {
			AliteLog.e("ALITE Font", "Error reading font.", e);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "AliteFont.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "AliteFont.readObject I");
			this.alite = Alite.get();
			ct = new DefaultCoordinateTransformer(alite);
			initialize();
			AliteLog.e("readObject", "AliteFont.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}

	private final void initialize() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(alite.getFileIO().readPrivateFile("textures/font.dat")));
		String line = br.readLine();
		String [] parts;
		while (line != null && line.trim().length() > 0) {
			parts = line.split(" ");
			characters.put((char) Integer.parseInt(parts[0]), 
					new CharacterData(Integer.parseInt(parts[1]),
									  Integer.parseInt(parts[2]),
							          Float.parseFloat(parts[3]),
									  Float.parseFloat(parts[4]),
							          Float.parseFloat(parts[5]),
							          Float.parseFloat(parts[6])));
			line = br.readLine();
		}				
	}
	
	@Override
	public void render() {
	}
	
	public float getWidth(String text, float scale) {
		float textWidth = 0;
		for (char c: text.toCharArray()) {
			if (c == '\n') {
				break;
			}
			CharacterData data = characters.get(c);
			if (data == null) {
				continue;
			}
			textWidth += data.width * scale;
		}
		return textWidth;
	}
	
	public void drawText(String text, int x, int y, boolean center, float scale) {		
		alite.getTextureManager().setTexture(textureFilename);
		float height = 10.0f;
		if (center) {
			float textWidth = 0;
			for (char c: text.toCharArray()) {
				if (c == '\n') {
					break;
				}
				CharacterData data = characters.get(c);
				if (data == null) {
					continue;
				}
				textWidth += data.width * scale;
				height = data.height * scale;
			}
			x = (int) (x - textWidth / 2);
		}
		setUp();
		int origX = x;
		for (char c: text.toCharArray()) {
			if (c == '\n') {
				x = origX;
				y += height + 20;
			}
			CharacterData data = characters.get(c);
			if (data == null) {
				continue;
			}
			if (x > -20 && x < 1940) {
				setPosition(ct.getTextureCoordX(x), ct.getTextureCoordY(y), ct.getTextureCoordX(x + data.width * scale), ct.getTextureCoordY(y + data.height * scale));
				setTextureCoords(data.uStart, data.vStart, data.uEnd, data.vEnd);
				justRender();
			}
			x += data.width * scale;
		}
		cleanUp();
	}
}
