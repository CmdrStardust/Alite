package de.phbouillon.android.games.alite.screens.canvas;

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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.graphics.Color;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Pixmap;
import de.phbouillon.android.framework.impl.AndroidGame;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.model.library.ItemDescriptor;
import de.phbouillon.android.games.alite.model.library.LibraryPage;
import de.phbouillon.android.games.alite.model.library.Toc;
import de.phbouillon.android.games.alite.model.library.TocEntry;
import de.phbouillon.android.games.alite.screens.NavigationBar;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class LibraryPageScreen extends AliteScreen {
	private static final int PAGE_BEGIN = 120;
	
	private final TocEntry tocEntry;
	private int yPosition = 0;
	private int startY;
	private int startX;
	private int lastY;
	private int maxY;
	private int deltaY = 0;
	private final List <PageText> pageText = new ArrayList<PageText>();
	private Button next;
	private Button prev;
	private Button toc;
	private final List <Pixmap> imagePixmaps = new ArrayList<Pixmap>();
	private final List <Button> images = new ArrayList<Button>();
	private String imageText = null;
	private Button largeImage = null;
	private String currentFilter = null;
	private TextData [] nextTextData;
	private TextData [] prevTextData;
	private TextData [] tocTextData;
	private TextData [] largeImageTextData;
	private GLText currentFont = Assets.regularFont;
	private GLText newFont = null;
	private long currentColor = AliteColors.get().mainText();
	private long newColor = -1;
	private boolean needsCr = false;
	private final List <Pixmap> inlineImages = new ArrayList<Pixmap>();
	
	class PageText {
		private StyledText [] words;
		private int [] positions;
		private Pixmap pixmap = null;
		
		PageText(StyledText [] words, int [] positions) {
			this.words = words;
			this.positions = positions;
		}
		
		PageText(Pixmap pm) {
			this.pixmap = pm;
		}
	}
	
	class Sentence {
		private final List <StyledText> words;
		int width;
		boolean cr;
		
		Sentence() {
			words = new ArrayList<StyledText>();
			width = 0;
			cr = false;
		}
		
		void add(Graphics g, StyledText word) {
			words.add(word);
			if (word.pixmap != null) {
				return;
			}
			if (width != 0) {
				width += g.getTextWidth(" ", word.font);
			}
			width += g.getTextWidth(word.text, word.font);
		}
				
		int getWidth(Graphics g, StyledText word) {
			if (word.pixmap != null) {
				return 0;
			}
			if (width == 0) {
				return g.getTextWidth(word.text, word.font);
			}
			return width + g.getTextWidth(" ", word.font) + g.getTextWidth(word.text, word.font);
		}
		
		void terminate() {
			cr = true;
		}
		
		public String toString() {
			String result = "[";
			for (int i = 0, n = words.size(); i < n; i++) {
				if (words.get(i).text != null) {
					result += words.get(i).text;
				} else {
					result += "[pixmap]";
				}
				if (i < n - 1) {
					result += " ";
				}
			}
			return result + "]";
		}
	}
	
	class StyledText {
		private String text;
		private GLText font;
		private long   color;
		private Pixmap pixmap = null;
		
		StyledText(Pixmap pm) {
			this.pixmap = pm;
		}
		
		StyledText(String text) {
			this(text, Assets.regularFont, AliteColors.get().mainText());
		}
		
		StyledText(String text, GLText font) {
			this(text, font, AliteColors.get().mainText());
		}
		
		StyledText(String text, long color) {
			this(text, Assets.regularFont, color);
		}
		
		StyledText(String text, GLText font, long color) {
			this.text = text;
			this.font = font;
			this.color = color;
		}
	}
	
	public LibraryPageScreen(Game game, TocEntry entry, String currentFilter) {
		super(game);
		this.tocEntry = entry;
		this.currentFilter = currentFilter;
		Graphics g = game.getGraphics();
		if (entry.getLinkedPage() != null) {
			computePageText(game.getGraphics(), entry.getLinkedPage().getParagraphs());
			if (entry.getLinkedPage().getNextPage(game.getFileIO()) != null) {
				next = new Button(1200, 960, 500, 120, "", Assets.regularFont, null);
				next.setGradient(true);
				nextTextData = computeTextDisplay(g, "Next: "+ tocEntry.getLinkedPage().getNextPage(game.getFileIO()).getHeader(), next.getX(), next.getY() + 50, next.getWidth(), 40, AliteColors.get().baseInformation(), Assets.regularFont, true);
			}
			if (entry.getLinkedPage().getPrevPage(game.getFileIO()) != null) {
				prev = new Button(45, 960, 500, 120, "", Assets.regularFont, null);
				prev.setGradient(true);
				prevTextData = computeTextDisplay(g, "Prev: " + tocEntry.getLinkedPage().getPrevPage(game.getFileIO()).getHeader(), prev.getX(), prev.getY() + 50, prev.getWidth(), 40, AliteColors.get().baseInformation(), Assets.regularFont, true);
			}
			int counter = 0;
			for (ItemDescriptor id: entry.getLinkedPage().getImages()) {
				try {
					Pixmap pixmap = game.getGraphics().newPixmap("library/" + id.getFileName() + "_tn.png", true);
					Button b = new Button(1100, PAGE_BEGIN + counter * 275, 500, 250, pixmap);
					b.setPixmapOffset((int) ((b.getWidth() - pixmap.getWidth() / AndroidGame.scaleFactor)) >> 1, 0);
					b.setGradient(true);
					imagePixmaps.add(pixmap);
					images.add(b);
					counter++;
				} catch (RuntimeException e) {
					// Catching the exception here, without adding pixmap or button, will throw the
					// indexing off (ItemDescriptors are referenced by the bitmap position in the bitmap
					// array list), so this will cause problems -- but once the library is completely in
					// place, this should not be an issue. Just keep in mind during testing.
					AliteLog.e("[ALITE] LibraryPageScreen", "Image " + id.getFileName() + " not found. Skipping.");
				}
			}
		}
		this.toc = new Button(624, 960, 500, 120, "", Assets.regularFont, null);
		this.toc.setGradient(true);	
		tocTextData = computeTextDisplay(g, "Table of Contents", toc.getX(), toc.getY() + 50, toc.getWidth(), 40, AliteColors.get().baseInformation(), Assets.regularFont, true);
	}
	
	@Override
	public void activate() {		
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {
		try {
			Toc toc = Toc.read(alite.getFileIO().readPrivateFile("library/toc.xml"), alite.getFileIO());
			TocEntry[] entries = toc.getEntries();
			int entryNo = dis.readInt();
			TocEntry entry = findTocEntryForIndex(entries, entryNo, 0);
			if (entry == null) {
				entry = entries[0];
			}
			int len = dis.readInt();
			String filter = null;
			if (len > 0) {
				filter = "";
				for (int i = 0; i < len; i++) {
					filter += dis.readChar();
				}
			}		
			AliteLog.d("Entry Number", "Read Entry Number: " + entryNo);
			LibraryPageScreen lps = new LibraryPageScreen(alite, entry, filter);
			lps.yPosition = dis.readInt();	
			alite.setScreen(lps);
		} catch (Exception e) {
			AliteLog.e("Library Screen Initialize", "Error in initializer.", e);
			return false;			
		}					
		return true;
	}
	
	private int findTocEntryIndex(TocEntry [] entries, String name, int counter) {
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getName().equals(name)) {
				AliteLog.d("Found", "Found -- i = " + i);
				return i + counter;
			}
			TocEntry [] children = entries[i].getChildren();
			if (children.length > 0) {
				int result = findTocEntryIndex(children, name, counter + i + 1);
				if (result >= 0) {
					return result;
				}
				counter += children.length;
			}
		}
		return -1;
	}
	
	private static TocEntry findTocEntryForIndex(TocEntry [] entries, int index, int counter) {
		for (int i = 0; i < entries.length; i++) {
			if ((i + counter) == index) {
				AliteLog.d("Found", "Found -- i = " + i);
				return entries[i];
			}
			TocEntry [] children = entries[i].getChildren();
			if (children.length > 0) {
				TocEntry result = findTocEntryForIndex(children, index, counter + i + 1);
				if (result != null) {
					return result;
				}
				counter += children.length;
			}
		}
		return null;
	}

	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		Toc toc = Toc.read(game.getFileIO().readPrivateFile("library/toc.xml"), game.getFileIO());
		TocEntry[] entries = toc.getEntries();
		int index = findTocEntryIndex(entries, tocEntry.getName(), 0);
		if (index < 0) {
			index = 0;
		}
		dos.writeInt(index);		 
		dos.writeInt(currentFilter == null ? 0 : currentFilter.length());
		if (currentFilter != null) {
			dos.writeChars(currentFilter);
		}
		dos.writeInt(yPosition);

	}

	private void toggleBold(boolean end) {
		if (end) {
			if (newFont == null) {
				newFont = currentFont;
			}
			newFont = newFont == Assets.boldItalicFont ? Assets.italicFont : Assets.regularFont;
		} else {
			currentFont = currentFont == Assets.italicFont ? Assets.boldItalicFont : Assets.boldFont;
		}
	}
	
	private void toggleItalic(boolean end) {
		if (end) {
			if (newFont == null) {
				newFont = currentFont;
			}
			newFont = newFont == Assets.boldItalicFont ? Assets.boldFont : Assets.regularFont;
		} else {
			currentFont = currentFont == Assets.boldFont ? Assets.boldItalicFont : Assets.italicFont;
		}
	}

	private void toggleColor(long color, boolean end) {
		if (end) {
			newColor = AliteColors.get().mainText();
		} else {
			currentColor = color;
		}
	}
	
	private String loadInlineImage(String word) {
		int index = word.indexOf("[G:");
		String fileName = word.substring(index + 3, word.indexOf("]", index));
		inlineImages.add(((Alite) game).getGraphics().newPixmap("library/" + fileName + ".png", true));
		return "@I+" + (inlineImages.size() - 1) + ";";
	}
	
	private String filterTags(String word) {
		int leftIndex = -1;
		boolean end = false;
		boolean containsTags = false;
		
		while ((leftIndex = word.indexOf("[", leftIndex + 1)) != -1) {
			containsTags = true;
			int rightIndex = word.indexOf("]", leftIndex);
			if (word.charAt(leftIndex + 1) == 'G') {
				word = loadInlineImage(word);			
			} else {
				for (char c: word.substring(leftIndex + 1, rightIndex).toCharArray()) {
					switch (c) {
						case '/': end = true; break;
						case 'b': toggleBold(end); break;
						case 'i': toggleItalic(end); break;
						case 'p': toggleColor(AliteColors.get().informationText(), end); break;
						case 'o': toggleColor(AliteColors.get().selectedText(), end); break;
					}
				}
			}
		}
		return containsTags ? word.replaceAll("\\[.*?\\]", "") : word;
	}
	
	private StyledText getStyledText(String word) {
		needsCr = word.indexOf("\n") != -1;
		if (needsCr) {
			word = word.substring(0, word.indexOf("\n"));
		}
		word = filterTags(word.trim());
		StyledText result;
		if (word.startsWith("@I+")) {
			result = new StyledText(inlineImages.get(Integer.parseInt(word.substring(3, word.indexOf(";")))));
		} else {
			result = new StyledText(word, currentFont, currentColor);
		}
		if (newFont != null) {
			currentFont = newFont;
			newFont = null;
		}
		if (newColor != -1) {
			currentColor = newColor;
			newColor = -1;
		}
		return result;
	}
	
	private void formatTable(Graphics g, String table, int fieldWidth) {
		table = table.substring(3, table.lastIndexOf(":T]")); // cut off [T: and :T] tags
		String [] tableRows = table.split(";");
		List <Integer> columnWidths = new ArrayList<Integer>();
		Set <Integer> rightAlign = new HashSet<Integer>();
		boolean toggleColor = true;
		for (String row: tableRows) {
			int colIndex = 0;
			for (String col: row.split(",")) {				
				col = col.replaceAll("\\[s\\]", " ").trim();
				if (col.indexOf("[r]") != -1) {
					col.replaceAll("\\[r\\]", "");
					rightAlign.add(colIndex);
				}
				if (col.indexOf("[m]") != -1) {
					col.replaceAll("\\[m\\]", "");
					toggleColor = false;
				}
				StyledText styledWord = getStyledText(col);
				int width = g.getTextWidth(styledWord.text, styledWord.font);
				if (colIndex >= columnWidths.size()) {
					columnWidths.add(width);
				} else {
					int currentWidth = columnWidths.get(colIndex);
					if (width > currentWidth) {
						columnWidths.set(colIndex, width);
					}
				}
				colIndex++;
			}
		}
		int numOfColumns = columnWidths.size();
		int totalWidth = 0;
		for (int w: columnWidths) {
			totalWidth += w;
		}
		List <Integer> originalWidths = new ArrayList<Integer>();
		if (totalWidth < fieldWidth) {
			int buffer = (int) (((float) fieldWidth - (float) totalWidth) / (float) numOfColumns);
			for (int i = 0; i < columnWidths.size(); i++) {
				originalWidths.add(columnWidths.get(i));
				columnWidths.set(i, columnWidths.get(i) + buffer);
			}
		} else {
			originalWidths = columnWidths;
		}
		
		int rowIndex = 0;
		for (String row: tableRows) {
			int colIndex = 0;
			int xPos = 50;
			String [] cols = row.split(",");
			int [] pos = new int[cols.length];
			StyledText [] words = new StyledText[cols.length];
			for (String col: cols) {
				col = col.replaceAll("\\[s\\]", " ").trim();
				col = col.replaceAll("\\[c\\]", ",");
				words[colIndex] = getStyledText(col);	
				if (toggleColor) {
					if (rowIndex > 0 && (rowIndex % 2) == 0) {
						words[colIndex].color = AliteColors.get().selectedText();
					} else if (rowIndex > 0) {
						words[colIndex].color = AliteColors.get().mainText();
					}
				} 
				pos[colIndex] = rightAlign.contains(colIndex) ? xPos + originalWidths.get(colIndex) - g.getTextWidth(words[colIndex].text, words[colIndex].font) : xPos;
				xPos += columnWidths.get(colIndex);
				colIndex++;
			}
			pageText.add(new PageText(words, pos));
			rowIndex++;
		}
	}
	
	private Sentence formatWord(String word, Sentence sentence, List <Sentence> sentences, int fieldWidth, Graphics g) {
		StyledText styledWord = getStyledText(word);
		if (styledWord.pixmap != null) {
			needsCr = true;
			if (sentence.width > 0) {
				sentences.add(sentence);
				sentence = new Sentence();
			}
		}
		int width = sentence.getWidth(g, styledWord);
		if (width > fieldWidth) {
			sentences.add(sentence);
			sentence = new Sentence();
		}
		sentence.add(g, styledWord);
		if (needsCr) {
			sentence.terminate();
			sentences.add(sentence);
			sentence = new Sentence();
		}	
		return sentence;
	}
	
	private int formatSentences(List <Sentence> sentences, int x, int fieldWidth, int inlineImageHeight, Graphics g) {
		for (Sentence s: sentences) {
			int xPos = x;
			StyledText [] words = s.words.toArray(new StyledText[0]);
			if (words.length == 1 && words[0].pixmap != null) {
				pageText.add(new PageText(words[0].pixmap));
				inlineImageHeight += words[0].pixmap.getHeight() / AndroidGame.scaleFactor;
				continue;
			}
			int spaces = words.length - 1;
			if (spaces == 0) {
				pageText.add(new PageText(words, new int [] {x}));
				if (s.cr) {
					pageText.add(new PageText(new StyledText[] {new StyledText("")}, new int [] {0}));
				}
				continue;
			}
			int sentenceWidth = s.width;
			int diff = fieldWidth - sentenceWidth; 
			int enlarge = diff / spaces;
			int extraEnlarge = diff - spaces * enlarge;
			int [] pos = new int[words.length];
			int currentWord = 0;			
			for (StyledText w: words) {				
				pos[currentWord++] = xPos;
				if (w.pixmap != null) {
					pageText.add(new PageText(w.pixmap));
					inlineImageHeight += w.pixmap.getHeight() / AndroidGame.scaleFactor;
					continue;
				}
				xPos += g.getTextWidth(w.text, w.font);
				xPos += g.getTextWidth(" ", w.font) + (s.cr ? 0 : extraEnlarge + enlarge);
				extraEnlarge = 0;
			}
			pageText.add(new PageText(words, pos));		
			if (s.cr) {
				pageText.add(new PageText(new StyledText[] {new StyledText("")}, new int [] {0}));
			}
		}	
		sentences.clear();
		return inlineImageHeight;
	}
	
	private void computePageText(Graphics g, String text) {
		int fieldWidth = tocEntry.getLinkedPage().getImages().length == 0 ? 1620 : 1000;
		int x = 50;
		int inlineImageHeight = 0;
		
		String [] textWords = text.split(" ");
		List <Sentence> sentences = new ArrayList<Sentence>();
		Sentence sentence = new Sentence();
		String nextWord;
		int index;
		for (String word: textWords) {
			nextWord = null;
			if ((index = word.indexOf("\n\n")) != -1) {				
				if (word.length() > index) {
					nextWord = word.substring(index + 2);
				}
			}
			
			if (word.indexOf("[T:") > 0) {
				String table = word.substring(word.indexOf("[T:"));
				word = word.substring(0, word.indexOf("[T:"));
				sentence = formatWord(word, sentence, sentences, fieldWidth, g);
				if (sentence.width > 0) {
					sentences.add(sentence);
				}
				if (sentences.size() > 0) {
					inlineImageHeight = formatSentences(sentences, x, fieldWidth, inlineImageHeight, g);
				}
				int tableIndex = table.lastIndexOf(":T]");
				String tableString = table.substring(0, tableIndex + 3);
				if (table.length() > tableIndex + 3) {
					nextWord = table.substring(tableIndex + 3).trim();
				}
				AliteLog.e("Table", "Table == " + tableString);
				formatTable(g, tableString, fieldWidth);
			} else if (word.startsWith("[T:")) {
				if (sentence.width > 0) {
					sentences.add(sentence);
				}
				if (sentences.size() > 0) {
					inlineImageHeight = formatSentences(sentences, x, fieldWidth, inlineImageHeight, g);
				}
				int tableIndex = word.lastIndexOf(":T]");
				String table = word.substring(0, tableIndex + 3);
				if (word.length() > tableIndex + 3) {
					nextWord = word.substring(tableIndex + 3).trim();
				}
				AliteLog.e("Table", "Table == " + table);
				formatTable(g, table, fieldWidth);
			} else {
				sentence = formatWord(word, sentence, sentences, fieldWidth, g);
			}
			String tempWord = null;
			StyledText styledWord = null;
			int width;
			while (nextWord != null) {	
				if ((index = nextWord.indexOf("\n\n")) != -1) {				
					if (nextWord.length() > index) {
						tempWord = nextWord.substring(index + 2);
					}
				}
				styledWord = getStyledText(nextWord);
				nextWord = tempWord;
				tempWord = null;
				if (styledWord.pixmap != null) {
					Sentence s2 = new Sentence();
					s2.add(g, styledWord);
					s2.terminate();
					sentences.add(s2);
					continue;
				}
				width = sentence.getWidth(g, styledWord);
				if (width > fieldWidth) {
					sentences.add(sentence);
					sentence = new Sentence();
				}
				sentence.add(g, styledWord);				
			}
		}
		if (sentence.width > 0) {
			sentences.add(sentence);
		}
		inlineImageHeight = formatSentences(sentences, x, fieldWidth, inlineImageHeight, g);
		maxY = Math.max(0, PAGE_BEGIN + 45 * pageText.size() - 890 + inlineImageHeight);
	}

	@Override
	protected void processTouch(TouchEvent touch) {
		super.processTouch(touch);	
		if (getMessage() != null) {
			return;
		}		
		if (imageText != null && largeImage != null) {
			if (touch.type == TouchEvent.TOUCH_UP && touch.pointer == 0) {
				SoundManager.play(Assets.click);
				imageText = null;
				largeImage.getPixmap().dispose();
				largeImage = null;
			}
			return;
		}
		if (touch.type == TouchEvent.TOUCH_DOWN && touch.pointer == 0) {
			startX = touch.x;
			startY = lastY = touch.y;			
		}
		if (touch.type == TouchEvent.TOUCH_DRAGGED && touch.pointer == 0) {
			if (touch.x > (1920 - NavigationBar.SIZE)) {
				return;
			}			
			yPosition += lastY - touch.y;
			if (yPosition < 0) {
				yPosition = 0;
			}
			if (yPosition > maxY) {
				yPosition = maxY;
			}
			lastY = touch.y;
			deltaY = 0;
		}
		if (touch.type == TouchEvent.TOUCH_UP && touch.pointer == 0) {
			if (touch.x > (1920 - NavigationBar.SIZE)) {
				return;
			}
			if (Math.abs(startX - touch.x) < 20 &&
				Math.abs(startY - touch.y) < 20) {
				for (int i = 0, n = images.size(); i < n; i++) {
					Button b = images.get(i);
					if (b.isTouched(touch.x, touch.y)) {
						String fileName = "library/" + tocEntry.getLinkedPage().getImages()[i].getFileName() + ".png";
						Pixmap pixmap = game.getGraphics().newPixmap(fileName, true);
						largeImage = new Button(50, 100, 1650, 920, pixmap);
						largeImage.setPixmapOffset(((int) (largeImage.getWidth() - pixmap.getWidth() / AndroidGame.scaleFactor)) >> 1, 0);
						largeImage.setGradient(true);
						imageText = tocEntry.getLinkedPage().getImages()[i].getText();
						largeImageTextData = computeTextDisplay(game.getGraphics(), imageText, largeImage.getX(), largeImage.getY() + largeImage.getHeight() - 180, largeImage.getWidth(), 40, AliteColors.get().mainText(), Assets.regularFont, false);
						SoundManager.play(Assets.alert);
					}
				}
				if (next != null && next.isTouched(touch.x, touch.y)) {
					LibraryPage linkedPage = tocEntry.getLinkedPage().getNextPage(game.getFileIO());
					newScreen = new LibraryPageScreen(game, new TocEntry(linkedPage.getHeader(), linkedPage, null), currentFilter);
					SoundManager.play(Assets.click);
				}
				if (prev != null && prev.isTouched(touch.x, touch.y)) {
					LibraryPage linkedPage = tocEntry.getLinkedPage().getPrevPage(game.getFileIO());
					newScreen = new LibraryPageScreen(game, new TocEntry(linkedPage.getHeader(), linkedPage, null), currentFilter);
					SoundManager.play(Assets.click);
				}
				if (toc.isTouched(touch.x, touch.y)) {
					newScreen = new LibraryScreen(game, currentFilter);
					SoundManager.play(Assets.click);
				}
			}
		}		
		if (touch.type == TouchEvent.TOUCH_SWEEP && touch.x < (1920 - NavigationBar.SIZE)) {
			deltaY = touch.y2;
		}		
	}
		
	@Override
	public void present(float deltaTime) {		
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		displayTitle(tocEntry.getName());
		g.clear(Color.BLACK);
		displayTitle(tocEntry.getName());
		
		if (deltaY != 0) {
			deltaY += deltaY > 0 ? -1 : 1;
			yPosition -= deltaY;
			if (yPosition < 0) {
				yPosition = 0;
			}
			if (yPosition > maxY) {
				yPosition = maxY;
			}
		}
		
		if (next != null) {
			next.render(g);		
			displayText(g, nextTextData);
		}
		if (prev != null) {
			prev.render(g);
			displayText(g, prevTextData);
		}
		toc.render(g);
		displayText(g, tocTextData);
		
		for (Button b: images) {
			b.render(g);
		}
		g.setClip(0, (int) (PAGE_BEGIN - Assets.regularFont.getSize() + 40), -1, 980);
		String [] highlights = new String[0];
		if (currentFilter != null) {
			highlights = currentFilter.toLowerCase(Locale.getDefault()).split(" ");
		}
		int yOffset = 0;
		int hardClip = -1;
		for (int row = 0, rows = pageText.size(); row < rows; row++) {
			PageText pt = pageText.get(row);
			if (pt.pixmap != null) {
				int picYPos = PAGE_BEGIN - yPosition + row * 45 + 10 + yOffset;
				if (picYPos < PAGE_BEGIN) {
					picYPos = PAGE_BEGIN;
					hardClip = (int) (PAGE_BEGIN - Assets.regularFont.getSize() + 40 + pt.pixmap.getHeight() / AndroidGame.scaleFactor);
				}
				g.drawPixmap(pt.pixmap, 50, picYPos);
				yOffset += pt.pixmap.getHeight() / AndroidGame.scaleFactor + 45;
				continue;
			}
			for (int x = 0, words = pt.words.length; x < words; x++) {
				long color = pt.words[x].color;
				for (String h: highlights) {
					if (pt.words[x].text.toLowerCase(Locale.getDefault()).contains(h)) {
						color = AliteColors.get().selectedText();
					}
				}
				int textY = PAGE_BEGIN - yPosition + row * 45 + 10 + yOffset;
				if (textY > hardClip) {
					g.drawText(pt.words[x].text, pt.positions[x], textY, color, pt.words[x].font);
				}
			}
		}		
		g.setClip(-1, -1, -1, -1);

		if (largeImage != null) {
			largeImage.render(g);
			displayText(g, largeImageTextData);
		} 
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Pixmap pm: imagePixmaps) {
			pm.dispose();
		}
		imagePixmaps.clear();
		for (Pixmap pm: inlineImages) {
			pm.dispose();
		}
		inlineImages.clear();
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.LIBRARY_PAGE_SCREEN;
	}	
}
