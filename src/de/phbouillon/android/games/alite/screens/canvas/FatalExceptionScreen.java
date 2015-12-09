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

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.Graphics;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.gl.font.GLText;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteIntro;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.Button;
import de.phbouillon.android.games.alite.colors.AliteColors;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class FatalExceptionScreen extends AliteScreen {
	private Throwable cause;
	private TextData [] causeText;
	private Button saveErrorCause;
	private Button restartAlite;
	private String plainCauseText;
	private String savedFilename = null;
	private final List <String> textToDisplay = new ArrayList<String>();
	
	public FatalExceptionScreen(Game game, Throwable cause) {
		super(game);
		this.cause = cause;
	}

	private final TextData [] computeCRTextDisplay(Graphics g, String text, int x, int y, int fieldWidth, int deltaY, long color, GLText font, boolean centered) {
		String [] textWords = text.split(" ");
		textToDisplay.clear();
		String result = "";
		for (String word: textWords) {
			int cr = word.indexOf("\n");
			String [] crWords = cr == -1 ? new String[] {word} : word.split("\n");
			for (int i = 0; i < crWords.length; i++) {
				String crWord = crWords[i];
				if (result.length() == 0) {
					result += crWord;
				} else {
					String test = result + " " + crWord;
					int width = g.getTextWidth(test, font);
					if (width > fieldWidth) {
						textToDisplay.add(result);
						result = crWord;
					} else {
						result += " " + crWord;
					}
				}
				if (i < (crWords.length - 1) && !result.isEmpty()) {
					textToDisplay.add(result);
					result = "";
				}
			}
			
		}
		if (result.length() > 0) {
			textToDisplay.add(result);
		}			
		int count = 0;
		ArrayList <TextData> resultList = new ArrayList<TextData>();
		for (String t: textToDisplay) {
			int halfWidth = centered ? (g.getTextWidth(t, font) >> 1) : (fieldWidth >> 1);
			resultList.add(new TextData(t, x + (fieldWidth >> 1) - halfWidth, y + deltaY * count++, color, font));		
		}
		return resultList.toArray(new TextData[0]);
	}

	@Override
	public void present(float deltaTime) {
		if (disposed) {
			return;
		}
		Graphics g = game.getGraphics();
		g.clear(AliteColors.get().background());
		g.gradientRect(0, 0, 1919, 80, false, true, AliteColors.get().backgroundDark(), AliteColors.get().backgroundLight());
		g.drawPixmap(Assets.aliteLogoSmall, 20, 5);
		g.drawPixmap(Assets.aliteLogoSmall, 1800, 5);
		centerTextWide("Fatal Error Occurred", 60, Assets.titleFont, AliteColors.get().message());		
		g.drawText("Unfortunately, Alite has crashed. I am sorry for the inconvenience :(", 20, 150, AliteColors.get().mainText(), Assets.regularFont);
		
		g.drawText("Following is the error cause, maybe it helps tracking down the problem:", 20, 320, AliteColors.get().mainText(), Assets.regularFont);
		if (causeText != null) {
			displayText(g, causeText);
		}
		if (savedFilename != null) {
			g.drawText("Report saved to: " + savedFilename, 20,  190, AliteColors.get().mainText(), Assets.regularFont);
		}
		saveErrorCause.render(g);
		restartAlite.render(g);
	}

	@Override
	public void update(float deltaTime) {
		for (TouchEvent t: game.getInput().getTouchEvents()) {
			if (t.type == TouchEvent.TOUCH_UP) {
				if (saveErrorCause.isTouched(t.x, t.y)) {
					saveErrorCause();
				} else if (restartAlite.isTouched(t.x, t.y)) {
					Intent intent = new Intent((Alite) game, AliteIntro.class);
					intent.putExtra(Alite.LOG_IS_INITIALIZED, true);
					((Alite) game).startActivityForResult(intent, 0);
				}
			} 
		}
	}
	
	private void saveErrorCause() {
		try {
			savedFilename = "crash_reports/report-" + (new SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault()).format(new Date())) + ".txt";
			if (!game.getFileIO().exists("crash_reports")) {
				game.getFileIO().mkDir("crash_reports");
			}
			OutputStream file = game.getFileIO().writeFile(savedFilename);
			String message = AliteLog.getErrorReportText(plainCauseText);
			file.write(message.getBytes());
			file.close();
		} catch (IOException e) {
			saveErrorCause.setText("Saving Failed");
			savedFilename = null;
			return;
		}
		saveErrorCause.setVisible(false);
	}

	@Override
	public void activate() {
		String message = cause == null ? "No additional information could be retrieved." :
				 		 cause.getMessage() == null ? cause.getClass().getName() : cause.getMessage();
		StringBuffer buffer = new StringBuffer(message);
		buffer.append("\n");
		while (cause != null) {
			if (cause.getStackTrace() != null) {
				for (StackTraceElement ste: cause.getStackTrace()) {
					buffer.append("-- at ");
					buffer.append(ste.getClassName());
					buffer.append(" .");
					buffer.append(ste.getMethodName());
					buffer.append("(");
					buffer.append(ste.getFileName());
					buffer.append(":");
					buffer.append(ste.getLineNumber());
					buffer.append(")\n");
				}
			}
			cause = cause.getCause();
			if (cause != null) {
				message = cause == null ? "No additional information could be retrieved." :
			 		      cause.getMessage() == null ? cause.getClass().getName() : cause.getMessage();
				buffer.append("Caused by: " + message);
			}
		}
		plainCauseText = buffer.toString();
		causeText = computeCRTextDisplay(game.getGraphics(), buffer.toString(), 20, 380, 1880, 40, AliteColors.get().warningMessage(), Assets.regularFont, false);
		saveErrorCause = new Button(1070, 180, 400, 110, "Save Error to File", Assets.regularFont, null);
		saveErrorCause.setGradient(true);
		restartAlite = new Button(1520, 180, 400, 110, "Restart Alite", Assets.regularFont, null);
		restartAlite.setGradient(true);
	}

	@Override
	public int getScreenCode() {
		return -1;
	}
}
