// This is a OpenGL ES 1.0 dynamic font rendering system. It loads actual font
// files, generates a font map (texture) from them, and allows rendering of
// text strings.
//
// NOTE: the rendering portions of this class uses a sprite batcher in order
// provide decent speed rendering. Also, rendering assumes a BOTTOM-LEFT
// origin, and the (x,y) positions are relative to that, as well as the
// bottom-left of the string to render.
// Taken from here: http://fractiousg.blogspot.de/2012/04/rendering-text-in-opengl-on-android.html

package de.phbouillon.android.framework.impl.gl.font;

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

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES11;
import android.opengl.GLUtils;
import de.phbouillon.android.framework.MemUtil;
import de.phbouillon.android.games.alite.Settings;

public class GLText {

	// --Constants--//
	public final static int CHAR_START = 32; // First Character (ASCII Code)
	public final static int CHAR_END = 126; // Last Character (ASCII Code)

	// 169: copyright
	// 201: E-acute
	// 233: e-acute
	// 235: e-diaeresis
	// 176: degree sign
	public final static int [] ADDITIONAL_CHARS = new int [] {169, 201, 233, 235, 176};	
	public final static char [] ADDITIONAL_CHARS_STR = new char [] { '\u00a9', '\u00c9', '\u00e9', '\u00eb', '\u00b0' };
	
	public final static int CHAR_CNT = (((CHAR_END - CHAR_START + ADDITIONAL_CHARS.length) + 1) + 1); // Character
																			// Count
																			// (Including
																			// Character
																			// to
																			// use
																			// for
																			// Unknown)

	public final static int CHAR_NONE = 32; // Character to Use for Unknown
											// (ASCII Code)
	public final static int CHAR_UNKNOWN = (CHAR_CNT - 1); // Index of the
															// Unknown Character

	public final static int FONT_SIZE_MIN = 6; // Minumum Font Size (Pixels)
	public final static int FONT_SIZE_MAX = 180; // Maximum Font Size (Pixels)

	public final static int CHAR_BATCH_SIZE = 100; // Number of Characters to
													// Render Per Batch

	private int givenFontSize;
	
	// --Members--//
	SpriteBatch batch; // Batch Renderer

	int fontPadX, fontPadY; // Font Padding (Pixels; On Each Side, ie. Doubled
							// on Both X+Y Axis)

	float fontHeight; // Font Height (Actual; Pixels)
	float fontAscent; // Font Ascent (Above Baseline; Pixels)
	float fontDescent; // Font Descent (Below Baseline; Pixels)

	int textureId; // Font Texture ID [NOTE: Public for Testing Purposes Only!]
	int textureSize; // Texture Size for Font (Square) [NOTE: Public for Testing
						// Purposes Only!]
	TextureRegion textureRgn; // Full Texture Region

	float charWidthMax; // Character Width (Maximum; Pixels)
	float charHeight; // Character Height (Maximum; Pixels)
	final float[] charWidths; // Width of Each Character (Actual; Pixels)
	TextureRegion[] charRgn; // Region of Each Character (Texture Coordinates)
	int cellWidth, cellHeight; // Character Cell Width/Height
	int rowCnt, colCnt; // Number of Rows/Columns

	float scaleX, scaleY; // Font Scale (X,Y Axis)
	float spaceX; // Additional (X,Y Axis) Spacing (Unscaled)
	
	// --Constructor--//
	// D: save GL instance + asset manager, create arrays, and initialize the
	// members
	// A: gl - OpenGL ES 10 Instance
	public GLText() {
		batch = new SpriteBatch(CHAR_BATCH_SIZE); // Create Sprite Batch (with
													// Defined Size)

		charWidths = new float[CHAR_CNT]; // Create the Array of Character
											// Widths
		charRgn = new TextureRegion[CHAR_CNT]; // Create the Array of Character
												// Regions

		// initialize remaining members
		fontPadX = 0;
		fontPadY = 0;

		fontHeight = 0.0f;
		fontAscent = 0.0f;
		fontDescent = 0.0f;

		textureId = -1;
		textureSize = 0;

		charWidthMax = 0;
		charHeight = 0;

		cellWidth = 0;
		cellHeight = 0;
		rowCnt = 0;
		colCnt = 0;

		scaleX = 1.0f; // Default Scale = 1 (Unscaled)
		scaleY = 1.0f; // Default Scale = 1 (Unscaled)
		spaceX = 0.0f;
	}

	public boolean load(AssetManager assets, String path, int size, int givenSize, int padX, int padY) {
		Typeface tf = Typeface.createFromAsset(assets, path);
		return load(tf, size, givenSize, padX, padY);
	}
	
	public boolean load(String file, int size, int givenSize, int padX, int padY) {
		Typeface tf = Typeface.createFromFile(file);
		return load(tf, size, givenSize, padX, padY);
	}
	
	// --Load Font--//
	// description
	// this will load the specified font file, create a texture for the defined
	// character range, and setup all required values used to render with it.
	// arguments:
	// file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
	// size - Requested pixel size of font (height)
	// padX, padY - Extra padding per character (X+Y Axis); to prevent
	// overlapping characters.
	private boolean load(Typeface tf, int size, int givenSize, int padX, int padY) {
		
		// setup requested values
		fontPadX = padX; // Set Requested X Axis Padding
		fontPadY = padY; // Set Requested Y Axis Padding

		givenFontSize = givenSize;
		
		// load the font and setup paint instance for drawing
		 
		Paint paint = new Paint(); // Create Android Paint Instance
		paint.setAntiAlias(true); // Enable Anti Alias
		paint.setTextSize(size); // Set Text Size
		paint.setColor(Color.WHITE); // Set ARGB (White, Opaque)
		paint.setTypeface(tf); // Set Typeface

		// get font metrics
		Paint.FontMetrics fm = paint.getFontMetrics(); // Get Font Metrics
		fontHeight = (float) Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top)); // Calculate
																				// Font
																				// Height
		fontAscent = (float) Math.ceil(Math.abs(fm.ascent)); // Save Font Ascent
		fontDescent = (float) Math.ceil(Math.abs(fm.descent)); // Save Font
																// Descent

		// determine the width of each character (including unknown character)
		// also determine the maximum character width
		char[] s = new char[2]; // Create Character Array
		charWidthMax = charHeight = 0; // Reset Character Width/Height Maximums
		float[] w = new float[2]; // Working Width Value
		int cnt = 0; // Array Counter
		for (char c = CHAR_START; c <= CHAR_END; c++) { // FOR Each Character
			s[0] = c; // Set Character
			paint.getTextWidths(s, 0, 1, w); // Get Character Bounds
			charWidths[cnt] = w[0]; // Get Width
			if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max
												// Width
				charWidthMax = charWidths[cnt]; // Save New Max Width
			cnt++; // Advance Array Counter
		}
		for (char c: ADDITIONAL_CHARS_STR) {
			s[0] = c;
			paint.getTextWidths(s,  0, 1, w);
			charWidths[cnt] = w[0];
			if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max
				// Width
				charWidthMax = charWidths[cnt]; // Save New Max Width
			cnt++; // Advance Array Counter
		}
		s[0] = CHAR_NONE; // Set Unknown Character
		paint.getTextWidths(s, 0, 1, w); // Get Character Bounds
		charWidths[cnt] = w[0]; // Get Width
		if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
			charWidthMax = charWidths[cnt]; // Save New Max Width
		cnt++; // Advance Array Counter

		// set character height to font height
		charHeight = fontHeight; // Set Character Height

		// find the maximum size, validate, and setup cell sizes
		cellWidth = (int) charWidthMax + (2 * fontPadX); // Set Cell Width
		cellHeight = (int) charHeight + (2 * fontPadY); // Set Cell Height
		int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight; // Save
																		// Max
																		// Size
																		// (Width/Height)
		if (maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX) // IF Maximum
																// Size Outside
																// Valid Bounds
			return false; // Return Error

		// set texture size based on max font size (width or height)
		// NOTE: these values are fixed, based on the defined characters. when
		// changing start/end characters (CHAR_START/CHAR_END) this will need
		// adjustment too!
		if (maxSize <= 20) // IF Max Size is 18 or Less
			textureSize = 256; // Set 256 Texture Size
		else if (maxSize <= 36) // ELSE IF Max Size is 40 or Less
			textureSize = 512; // Set 512 Texture Size
		else if (maxSize <= 80) // ELSE IF Max Size is 80 or Less
			textureSize = 1024; // Set 1024 Texture Size
		else
			// ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
			textureSize = 2048; // Set 2048 Texture Size
		
		// create an empty bitmap (alpha only)
		Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize,
				Settings.colorDepth == 1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.ARGB_4444); // Create Bitmap
		Canvas canvas = new Canvas(bitmap); // Create Canvas for Rendering to
											// Bitmap
		bitmap.eraseColor(0x00000000); // Set Transparent Background (ARGB)

		// calculate rows/columns
		// NOTE: while not required for anything, these may be useful to have :)
		colCnt = textureSize / cellWidth; // Calculate Number of Columns
		rowCnt = (int) Math.ceil((float) CHAR_CNT / (float) colCnt); // Calculate
																		// Number
																		// of
																		// Rows

		// render each of the characters to the canvas (ie. build the font map)
		float x = fontPadX; // Set Start Position (X)
		float y = (cellHeight - 1) - fontDescent - fontPadY; // Set Start
																// Position (Y)
		for (char c = CHAR_START; c <= CHAR_END; c++) { // FOR Each Character
			s[0] = c; // Set Character to Draw
//			canvas.drawLine(x, y, x + cellWidth, y + cellHeight, paint);
			canvas.drawText("" + c, x, y, paint);
//			canvas.drawText(s, 0, 1, x, y, paint); // Draw Character
			x += cellWidth; // Move to Next Character
			if ((x + cellWidth - fontPadX) > textureSize) { // IF End of Line
															// Reached
				x = fontPadX; // Set X for New Row
				y += cellHeight; // Move Down a Row
			}
		}
		for (char c: ADDITIONAL_CHARS_STR) {
			s[0] = c; // Set Character to Draw
			canvas.drawText("" + c, x, y, paint);
			x += cellWidth; // Move to Next Character
			if ((x + cellWidth - fontPadX) > textureSize) { // IF End of Line
															// Reached
				x = fontPadX; // Set X for New Row
				y += cellHeight; // Move Down a Row
			}
		}
		s[0] = CHAR_NONE; // Set Character to Use for NONE
		canvas.drawText(s, 0, 1, x, y, paint); // Draw Character
//		canvas.drawText("" + CHAR_NONE, x, y, paint);

		// generate a new texture
		int[] textureIds = new int[1]; // Array to Get Texture Id
		GLES11.glGenTextures(1, textureIds, 0); // Generate New Texture
		textureId = textureIds[0]; // Save Texture Id
		
		// setup filters for texture
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, textureId); // Bind Texture
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D,
				GLES11.GL_TEXTURE_MIN_FILTER, GLES11.GL_NEAREST); // Set
																	// Minification
																	// Filter
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D,
				GLES11.GL_TEXTURE_MAG_FILTER, GLES11.GL_LINEAR); // Set
																	// Magnification
																	// Filter
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_S,
				GLES11.GL_CLAMP_TO_EDGE); // Set U Wrapping
		GLES11.glTexParameterf(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_T,
				GLES11.GL_CLAMP_TO_EDGE); // Set V Wrapping

		// load the generated bitmap onto the texture
		GLUtils.texImage2D(GLES11.GL_TEXTURE_2D, 0, bitmap, 0); // Load Bitmap
																// to Texture
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0); // Unbind Texture		
		
		// release the bitmap
		MemUtil.freeBitmap(bitmap);

		// setup the array of character texture regions
		x = 0; // Initialize X
		y = 0; // Initialize Y
		for (int c = 0; c < CHAR_CNT; c++) { // FOR Each Character (On Texture)
			charRgn[c] = new TextureRegion(textureSize, textureSize, x, y,
					cellWidth - 1, cellHeight - 1); // Create Region for
													// Character
			x += cellWidth; // Move to Next Char (Cell)
			if (x + cellWidth > textureSize) {
				x = 0; // Reset X Position to Start
				y += cellHeight; // Move to Next Row (Cell)
			}
		}

		// create full texture region
		textureRgn = new TextureRegion(textureSize, textureSize, 0, 0,
				textureSize, textureSize); // Create Full Texture Region

		// return success
		return true; // Return Success
	}

	// --Begin/End Text Drawing--//
	// D: call these methods before/after (respectively all draw() calls using a
	// text instance
	// NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha
	// only!!!
	// A: red, green, blue - RGB values for font (default = 1.0)
	// alpha - optional alpha value for font (default = 1.0)
	// R: [none]
	public void begin() {
		begin(1.0f, 1.0f, 1.0f, 1.0f); // Begin with White Opaque
	}

	public void begin(float alpha) {
		begin(1.0f, 1.0f, 1.0f, alpha); // Begin with White (Explicit Alpha)
	}

	public void begin(float red, float green, float blue, float alpha) {
		GLES11.glColor4f(red, green, blue, alpha); // Set Color+Alpha
		GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, textureId); // Bind the
																// Texture
		batch.beginBatch(); // Begin Batch
	}

	public void end() {
		batch.endBatch(); // End Batch
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Restore Default Color/Alpha
		GLES11.glDisable(GLES11.GL_TEXTURE_2D);
	}

	private int transformAdditional(int c) {
		int index = CHAR_END - CHAR_START + 1;
		for (int d: ADDITIONAL_CHARS) {
			if ((d - CHAR_START) == c) {
				return index;
			}
			index++;
		}
		return c;
	}
	
	// --Draw Text--//
	// D: draw text at the specified x,y position
	// A: text - the string to draw
	// x, y - the x,y position to draw text at (bottom left of text; including
	// descent)
	// R: [none]
	public void draw(String text, float x, float y) {
		float chrHeight = cellHeight * scaleY; // Calculate Scaled Character
												// Height
		float chrWidth = cellWidth * scaleX; // Calculate Scaled Character Width
		int len = text.length(); // Get String Length
		x += (chrWidth / 2.0f) - (fontPadX * scaleX); // Adjust Start X
		y += (chrHeight / 2.0f) - (fontPadY * scaleY); // Adjust Start Y
		for (int i = 0; i < len; i++) { // FOR Each Character in String
			int c = (int) text.charAt(i) - CHAR_START; // Calculate Character
														// Index (Offset by
														// First Char in Font)	
			if (c < 0 || c >= CHAR_CNT) {// IF Character Not In Font
				c = transformAdditional(c);
				if (c < 0 || c >= CHAR_CNT) {
					c = CHAR_UNKNOWN; // Set to Unknown Character Index
				}				
			}
			batch.drawSprite(x, y, chrWidth, chrHeight, charRgn[c]); // Draw the
																		// Character
			x += (charWidths[c] + spaceX) * scaleX; // Advance X Position by
													// Scaled Character Width
		}
	}

	// --Draw Text Centered--//
	// D: draw text CENTERED at the specified x,y position
	// A: text - the string to draw
	// x, y - the x,y position to draw text at (bottom left of text)
	// R: the total width of the text that was drawn
	public float drawC(String text, float x, float y) {
		float len = getLength(text); // Get Text Length
		draw(text, x - (len / 2.0f), y - (getCharHeight() / 2.0f)); // Draw Text
																	// Centered
		return len; // Return Length
	}

	public float drawCX(String text, float x, float y) {
		float len = getLength(text); // Get Text Length
		draw(text, x - (len / 2.0f), y); // Draw Text Centered (X-Axis Only)
		return len; // Return Length
	}

	public void drawCY(String text, float x, float y) {
		draw(text, x, y - (getCharHeight() / 2.0f)); // Draw Text Centered
														// (Y-Axis Only)
	}

	// --Set Scale--//
	// D: set the scaling to use for the font
	// A: scale - uniform scale for both x and y axis scaling
	// sx, sy - separate x and y axis scaling factors
	// R: [none]
	public void setScale(float scale) {
		scaleX = scaleY = scale; // Set Uniform Scale
	}

	public void setScale(float sx, float sy) {
		scaleX = sx; // Set X Scale
		scaleY = sy; // Set Y Scale
	}

	// --Get Scale--//
	// D: get the current scaling used for the font
	// A: [none]
	// R: the x/y scale currently used for scale
	public float getScaleX() {
		return scaleX; // Return X Scale
	}

	public float getScaleY() {
		return scaleY; // Return Y Scale
	}

	// --Set Space--//
	// D: set the spacing (unscaled; ie. pixel size) to use for the font
	// A: space - space for x axis spacing
	// R: [none]
	public void setSpace(float space) {
		spaceX = space; // Set Space
	}

	// --Get Space--//
	// D: get the current spacing used for the font
	// A: [none]
	// R: the x/y space currently used for scale
	public float getSpace() {
		return spaceX; // Return X Space
	}

	// --Get Length of a String--//
	// D: return the length of the specified string if rendered using current
	// settings
	// A: text - the string to get length for
	// R: the length of the specified string (pixels)
	public float getLength(String text) {
		float len = 0.0f; // Working Length
		int strLen = text.length(); // Get String Length (Characters)
		for (int i = 0; i < strLen; i++) { // For Each Character in String
											// (Except Last
			int c = (int) text.charAt(i) - CHAR_START; // Calculate Character
														// Index (Offset by
														// First Char in Font)
			if (c < 0 || c >= CHAR_CNT) {// IF Character Not In Font
				c = transformAdditional(c);
				if (c < 0 || c >= CHAR_CNT) {
					c = CHAR_UNKNOWN; // Set to Unknown Character Index
				}
			}
			len += (charWidths[c] * scaleX); // Add Scaled Character Width to
												// Total Length
		}
		len += (strLen > 1 ? ((strLen - 1) * spaceX) * scaleX : 0); // Add Space
																	// Length
		return len; // Return Total Length
	}

	// --Get Width/Height of Character--//
	// D: return the scaled width/height of a character, or max character width
	// NOTE: since all characters are the same height, no character index is
	// required!
	// NOTE: excludes spacing!!
	// A: chr - the character to get width for
	// R: the requested character size (scaled)
	public float getCharWidth(char chr) {
		int c = chr - CHAR_START; // Calculate Character Index (Offset by First
									// Char in Font)
		if (c < 0 || c >= CHAR_CNT) {// IF Character Not In Font
			c = transformAdditional(c);
		}
		return (charWidths[c] * scaleX); // Return Scaled Character Width
	}

	public float getCharWidthMax() {
		return (charWidthMax * scaleX); // Return Scaled Max Character Width
	}

	public float getCharHeight() {
		return (charHeight * scaleY); // Return Scaled Character Height
	}

	// --Get Font Metrics--//
	// D: return the specified (scaled) font metric
	// A: [none]
	// R: the requested font metric (scaled)
	public float getAscent() {
		return (fontAscent * scaleY); // Return Font Ascent
	}

	public float getDescent() {
		return (fontDescent * scaleY); // Return Font Descent
	}

	public float getHeight() {
		return (fontHeight * scaleY); // Return Font Height (Actual)
	}

	// --Draw Font Texture--//
	// D: draw the entire font texture (NOTE: for testing purposes only)
	// A: width, height - the width and height of the area to draw to. this is
	// used
	// to draw the texture to the top-left corner.
	public void drawTexture(int width, int height) {
		batch.beginBatch(textureId); // Begin Batch (Bind Texture)
		batch.drawSprite(textureSize / 2, height - (textureSize / 2),
				textureSize, textureSize, textureRgn); // Draw
		batch.endBatch(); // End Batch
	}

	public float getSize() {
		return givenFontSize;
	}
}
