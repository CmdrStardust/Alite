package de.phbouillon.android.games.alite.screens.opengl;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import de.phbouillon.android.framework.MemUtil;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.framework.impl.Pool;
import de.phbouillon.android.framework.impl.Pool.PoolObjectFactory;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.screens.opengl.sprites.SpriteData;

public class TextureManager {	
	private static final int MAX_TEXTURES = 500;
	
	private class Texture implements Serializable {
		private static final long serialVersionUID = 3417797379929074799L;

		final int [] index = new int[1];
		
		public boolean isValid() {
			return index[0] != 0;
		}
	}
	
	private final Map <String, Texture> textures = Collections.synchronizedMap(new HashMap<String, Texture>());
	private final Set <String> bitmaps = Collections.synchronizedSet(new HashSet<String>());
	private final AndroidGame game;
	private final Map <String, SpriteData> sprites = Collections.synchronizedMap(new HashMap<String, SpriteData>());
	private static int count = 1;
	private final PoolObjectFactory <Texture> factory = new PoolObjectFactory<Texture>() {
		private static final long serialVersionUID = -7926981761767264375L;

		@Override
		public Texture createObject() {
			AliteLog.d("New Texture", "New Texture created. Count == " + count++);
			return new Texture();
		}
	};
	private final Pool <Texture> texturePool = new Pool<Texture>(factory, MAX_TEXTURES);
	
	public TextureManager(AndroidGame game) {
		this.game = game;
		
	}
		
	public int addTexture(String fileName) {
		if (bitmaps.contains(fileName)) {
			return 0;
		}
		Texture texture = textures.get(fileName);
		if (texture == null || !texture.isValid()) {
			texture = texturePool.newObject();
			texture.index[0] = 0;
			GLES11.glGenTextures(1, texture.index, 0);
			loadTexture(fileName, texture.index[0]);
			textures.put(fileName, texture);			
		} 
		return texture.index[0];
	}
	
	public boolean checkTexture(String fileName) {
		Texture texture = textures.get(fileName);
		return texture != null && texture.isValid();
	}
	
	public int addTexture(String name, Bitmap bitmap) {
		Texture texture = textures.get(name);
		if (texture != null) {
			freeTexture(name);
		}
		bitmaps.add(name);
		texture = texturePool.newObject();	
		texture.index[0] = 0;
		GLES11.glGenTextures(1, texture.index, 0);
		loadTexture(bitmap, texture.index[0]);
		textures.put(name, texture);
		return texture.index[0];
	}
	
	private Texture forceAddTexture(String fileName) {
		Texture texture = texturePool.newObject();
		texture.index[0] = 0;
		GLES11.glGenTextures(1, texture.index, 0);
		loadTexture(fileName, texture.index[0]);
		textures.put(fileName, texture);			
		return texture;
	}

	public void freeTexture(String fileName) {
		Texture texture = textures.get(fileName);
		if (texture != null && texture.isValid()) {			
			GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);
			GLES11.glDeleteTextures(1, texture.index, 0);			
			GLES11.glFinish();
			textures.put(fileName, null);
			bitmaps.remove(fileName);
			texturePool.free(texture);
		}
	}
	
	public void setTexture(String fileName) {
		if (fileName == null) {
			GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);			
			return;
		}
		Texture texture = textures.get(fileName);
		if (texture == null || !texture.isValid()) {		
			addTexture(fileName);
			texture = textures.get(fileName);
		}
		if (texture != null) {
			if (!texture.isValid()) {
				texture.index[0] = addTexture(fileName);
			}
			
			if (texture.index[0] != 0) {
				GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, texture.index[0]);
			} 
		} 
	}

	public SpriteData getSprite(String fileName, String spriteName) {
		return sprites.get(fileName + ":" + spriteName);
	}
	
	public synchronized void freeAllTextures() {
		Iterator <String> iterator = Collections.synchronizedSet(textures.keySet()).iterator();
		ArrayList <String> toBeRemoved = new ArrayList<String>();
		while (iterator.hasNext()) {
			String fileName = iterator.next();
			Texture texture = textures.get(fileName);
			if (texture != null) {				
				GLES11.glDeleteTextures(1, texture.index, 0);					
				texturePool.free(texture);
				toBeRemoved.add(fileName);
			}						
		}
		for (String s: toBeRemoved) {
			textures.put(s, null);
		}
		sprites.clear();
	}
	
	public void reloadAllTextures() {
		for (String fileName : textures.keySet()) {
			Texture texture = textures.get(fileName);
			if (texture != null) {
				if (!bitmaps.contains(fileName)) {
					continue;
				}
			}
			if (!bitmaps.contains(fileName)) {
				texture = forceAddTexture(fileName);
			}
		}
	}
	
	public synchronized void clear() {
		AliteLog.d("Clearing all Textures Mem Dump (PRE)", AliteLog.getMemoryData());
		freeAllTextures();
		textures.clear();
		AliteLog.d("Clearing all Textures Mem Dump (POST)", AliteLog.getMemoryData());
	}
	
	private final void parseTextures(String fileName, InputStream textureAtlas, float width, float height) throws IOException {
		if (textureAtlas == null) {
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(textureAtlas));
		String line = br.readLine();
		String [] parts;
		String [] coords;
		String [] size;
		float x, y, sx, sy;
		while (line != null) {
			if (line.trim().length() == 0) {
				line = br.readLine();
				continue;
			}
			parts = line.split(";");
			if (parts.length != 3) {
				AliteLog.e("Error in Sprite atlas", line);				
			} else {
				coords = parts[1].split(",");
				size = parts[2].split(",");
				x = Float.parseFloat(coords[0]) / Settings.textureLevel;
				y = Float.parseFloat(coords[1]) / Settings.textureLevel;
				sx = Float.parseFloat(size[0]) / Settings.textureLevel;
				sy = Float.parseFloat(size[1]) / Settings.textureLevel;
				sprites.put(fileName + ":" + parts[0], new SpriteData(parts[0], x / width, y / height, (x + sx - 1) / width, (y + sy - 1) / height, Float.parseFloat(size[0]), Float.parseFloat(size[1])));
			}
			line = br.readLine();
		}		
	}

	private final Bitmap newBitmap(String fileName) {		
		Config config = Settings.colorDepth == 1 ? Config.ARGB_8888 : Config.ARGB_4444;
		Options options = new Options();
		options.inPreferredConfig = config;
		options.inSampleSize = Settings.textureLevel;
		InputStream in = null;
		Bitmap bitmap = null;
		try {
			in = game.getFileIO().readPrivateFile(fileName);
			bitmap = BitmapFactory.decodeStream(in, null, options);
			if (bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} catch (IOException e) {
			AliteLog.e("Error loading bitmap", "Couldn't load bitmap from asset '" + fileName + "'");
//			throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return bitmap;
	}
	
	private final void loadTexture(String fileName, int index) {				
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, index);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_LINEAR);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_LINEAR);		
		AliteLog.e("Loading Texture: " + fileName, fileName + " Creating bitmap....");
		Bitmap bitmap = newBitmap(fileName);
		if (bitmap == null) {
			AliteLog.e("Loading Texture: " + fileName, "Creating bitmap failed!");
			return; 
		}
		
		GLUtils.texImage2D(GLES11.GL_TEXTURE_2D, 0, bitmap, 0);

		String textureAtlas = fileName.substring(0, fileName.lastIndexOf(".")) + ".txt";
		if (game.getGraphics().existsAssetsFile(textureAtlas)) {
			InputStream textureAtlasInputStream = null;
			try {
				textureAtlasInputStream = game.getFileIO().readPrivateFile(textureAtlas);
				parseTextures(fileName, textureAtlasInputStream, bitmap.getWidth(), bitmap.getHeight());
			} catch (IOException e) {
				AliteLog.e("Cannot read texture atlas.", e.getMessage(), e);
			} finally {
				if (textureAtlasInputStream != null) {
					try {
						textureAtlasInputStream.close();
					} catch (IOException e) {
						AliteLog.e("Error closing texture atlas.", e.getMessage(), e);
					}
				}
			}
		}
		MemUtil.freeBitmap(bitmap);
	}
	
	private final void loadTexture(Bitmap bitmap, int index) {				
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, index);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_LINEAR);
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_LINEAR);
		GLUtils.texImage2D(GLES11.GL_TEXTURE_2D, 0, bitmap, 0);
	}	
}
