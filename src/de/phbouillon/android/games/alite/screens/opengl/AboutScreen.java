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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.opengl.GLES11;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.GlScreen;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.Music;
import de.phbouillon.android.framework.Sound;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.impl.gl.Sprite;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.colors.AliteColors;
import de.phbouillon.android.games.alite.screens.canvas.TextData;
import de.phbouillon.android.games.alite.screens.canvas.options.OptionsScreen;
import de.phbouillon.android.games.alite.screens.opengl.sprites.AliteFont;

// ??              - Adder Mk II          - Gopher
//                 - Mosquito Trader      - Indigo
//                 - Jabberwocky          - Dugite
//                 - TIE-Fighter          - TIE-Fighter
//                 - Tiger                - Lyre
// ADCK            - Eagle Mk IV          - Rattlesnake   - Michael Francis             
// Captain Beatnik - Coluber Pitviper     - Harlequin     - captain.beatnik@gmx.de (Robert Triflinger)
// Wyvern
// Clym Angus      - Kirin                - Yellowbelly   -  
// Galileo         - Huntsman             - Mussurana     - Eric Walsh
// Murgh           - Bandy-Bandy Courier  - Coral         - ??
//                 - Cat                  - Cougar        - ??
// Wolfwood        - Drake Mk II          - Bushmaster    - ??             
  

// This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class AboutScreen extends GlScreen {
	private final Rect visibleArea;
	private final int windowWidth;
	private final int windowHeight;
	private Sprite background;
	private Sprite aliteLogo;
	private long startTime;
	private long lastTime;
	private Music endCreditsMusic;
	private float alpha = 0.0001f;
	private float globalAlpha = 1.0f;
	private int mode = 0;
	private int y = 1200;
	private AliteFont font;
	private boolean end = false;
	private boolean returnToOptions = false;
	private float musicVolume = Settings.volumes[Sound.SoundType.MUSIC.getValue()];
	
	private int pendingMode = -1;
	
	private final String credits = "[  03.0y]Alite\n" +
	                               "[3001.5w]A Game By\n" +
			                       "[ 602.0y]Philipp Bouillon\n" +
			                       "[ 902.0y]Duane McDonnell\n" +
			                       
			                       "[2501.5w]Programming\n" +
	                               "[ 602.0y]Philipp Bouillon\n" +
			                       
			                       "[2501.5w]Additional Programming\n" +
			                       "[ 602.0y]Steven Phillips\n" +

	                               "[2501.5y]Alite is inspired by classic Elite\n" +
			                       "[ 601.5y]\u00a9 Acornsoft, Bell & Braben\n" +
	                               
	                               "[2501.5w]Intro Movie\n" +
	                               "[1001.5w]Production, Modeling, Rendering\n" +
			                       "[ 602.0y]James Scott\n" +
	                               "[ 902.0o]jscottmedia.com\n" +
	                               
	                               "[2001.5w]Docking Computer Music\n" +
	                               "[ 601.5w]The Blue Danube (Op. 314)\n" +
	                               "[ 601.5w]by Johann Strauss\n" +
	                               "[ 602.0y]Arranged by Chris Huelsbeck\n" +
	                               "[2001.5w]Background Music\n" +
			                       "[ 602.0y]Antti Martikainen\n" +
	                               "[ 902.0o]www.youtube.com/user/AJMartikainen\n" +
			                       "[ 902.0y]The Chase\n" +
	                               "[ 902.0o]anttimartikainen.bandcamp.com/track/the-chase\n" +
			                       "[ 902.0y]To Valhalla\n" +
			                       "[ 902.0o]anttimartikainen.bandcamp.com/track/to-valhalla\n" +
			                       
			                       "[2001.5w]Voice Acting\n" +
	                               "[1001.5w]Commander Quelo\n" +
			                       "[ 602.0y]R.J. O'Connell\n" +			                       
	                               "[1501.5w]Lave Station Commander\n" +
			                       "[ 602.0y]Amy Perkins\n" +
	                               "[1001.5w]Commander Ripley (Viper 4)\n" +
			                       "[ 602.0y]Cliff Thompson\n" +
	                               "[1001.5w]Commander Stevenson\n" +
			                       "[ 602.0y]Bret Newton\n" +
	                               "[ 902.0o]www.bretnewton.com\n" +
			                       "[1001.5w]Computer Voice\n" +
			                       "[ 602.0y]Dahlia Lynn\n" +
			                       "[1001.5w]Constrictor General\n" +
			                       "[ 602.0y]David Bodtcher\n" +
			                       "[1001.5w]Thargoid Documents General\n" +
			                       "[ 602.0y]River Kanoff\n" +
			                       "[1001.5w]Emergency Announcements\n" +
			                       "[ 602.0y]Cass McPhee\n" +
			                       "[ 902.0o]https://cassmcphee.wordpress.com\n" +
			                       "[1001.5w]Refugee\n" +
			                       "[ 602.0y]Caitlin Buckley\n" +
			                       "[ 902.0o]https://caitlinva.wordpress.com\n" +
			                       "[1001.5w]Thargoid Station General\n" +
			                       "[ 602.0y]Adoxographist\n" +
			                       "[ 902.0o]http://adoxtalks.tumblr.com\n" +
	                               
			                       "[2001.5w]Modeling\n" +
	                               "[ 602.0y]Phil Griff\n" +
	                               "[ 902.0y]Rolf Schuetteler\n" +
	                               "[ 902.0y]Giles Williams\n" +
	                               "[ 902.0y]Jens Ayton\n" +
	                               "[ 902.0y]Clym Angus\n" +
	                               "[ 902.0y]Marko Susimets\u00e4\n" +
	                               "[ 902.0y]ADCK\n" +
	                               "[ 902.0y]Captain Beatnik\n" +	                               
	                               "[ 902.0y]DeepSpace\n" +
	                               "[ 902.0y]Galileo\n" +
	                               "[ 902.0y]Murgh\n" +	                               

	                               "[2001.5w]Icon and Alien Graphics\n" +
	                               "[ 602.0y]Stan Stoyanov\n" +
	                               "[2001.5w]Commander Quelo Portrait\n" +
	                               "[ 602.0y]Aleksandar Grujic - Dimmensa\n" +
	                               "[2001.5w]Planetary Textures\n" +
	                               "[ 602.0y]Planetcreator\n" +
	                               "[ 902.0y]by Christian Hart\n" +
	                               "[2001.5w]Star Textures\n" +
	                               "[ 602.0y]From Celestia User gradius_fanatic\n" +
	                               "[2001.5w]Cobra Mk III Image\n" +
	                               "[ 602.0y]From www.kennyscrap.com\n" + 
	                               
	                               "[2001.5w]Game Testers\n" +
	                               "[ 602.0y]Arnd Houben\n" +
	                               "[ 902.0y]Cornelius Dirmeier\n" +
	                               "[ 902.0y]Duane McDonnell\n" +
	                               "[ 902.0y]Franz-Josef Bongartz\n" +
	                               "[ 902.0y]Gunnar Tacke\n" +
	                               "[ 902.0y]Hussein Baagil\n" +
	                               "[ 902.0y]Jens-Peter Hack\n" +	
	                               "[ 902.0y]Manuel Schupp\n" +
	                               "[ 902.0y]Mathias Busche\n" +
	                               "[ 902.0y]Michael Breuer\n" +
	                               "[ 902.0y]Michael Raue\n" +
	                               "[ 902.0y]Olav Riediger\n" +
	                               "[ 902.0y]Rolf Paulsen\n" +
	                               "[ 902.0y]Scott McGeachie\n" + 
	                               "[ 902.0y]Stefan Widmaier\n" +
	                               
	                               "[2001.5w]Special Thanks To\n" +
	                               "[ 602.0y]Duane McDonnell\n" +
	                               "[ 902.0o]This game would not have been completed\n" +
	                               "[ 902.0o]without your help. You are the real\n" +
	                               "[ 902.0o]Elite wizard\n" +
	                               "[ 902.0o]and you taught me everything I know about 3D.\n" +
	                               "[2002.0y]The Oolite Community\n" +
	                               "[ 902.0o]Alite would not have been possible without\n" + 
	                               "[ 902.0o]the support of the Oolite fan community.\n" +
	                               "[2002.0y]Ian Bell and David Braben\n" +
	                               "[ 902.0o]Thank you for Elite.\n" +
	                               "[ 902.0o]The best game in history.\n" +
	                               "[2002.0y]Rob Nicholson\n" +
	                               "[ 902.0o]You have created my favorite Elite version:\n" +
	                               "[ 902.0o]Amiga Elite. Thank you also for your\n" +
	                               "[ 902.0o]encouragement to write this game.\n" +
	                               "[2002.0y]My Wife Klaudia\n" +
	                               "[ 902.0o]Thank you for letting me write this game\n" +
	                               "[ 902.0o]during all those long hours in the night.\n" +
	                               "[ 902.0o]For this, and so much more, I love you!";
	                               	                               	                              	                               
	private final List <TextData> texts;
	
	public AboutScreen(Game game) {
		super(game);
		visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
		background = new Sprite((Alite) game, visibleArea.left, visibleArea.top, visibleArea.right, visibleArea.bottom, 0.0f, 0.0f, 1.0f, 1.0f, "textures/star_map_title.png");
		aliteLogo  = new Sprite((Alite) game, visibleArea.left, visibleArea.top, visibleArea.right, visibleArea.bottom, 0.0f, 0.0f, 1615.0f / 2048.0f, 1080.0f / 2048.0f, "title_logo.png");
		aliteLogo.scale(0.96f, visibleArea.left, visibleArea.top, visibleArea.right, visibleArea.bottom);
		windowWidth = visibleArea.width();
		windowHeight = visibleArea.height();
		startTime = System.nanoTime();		
		lastTime = startTime;
		endCreditsMusic = game.getAudio().newMusic("music/end_credits.mp3", Sound.SoundType.MUSIC);
		texts = new ArrayList<TextData>();
		int curY = 0;
		for (String s: credits.split("\n")) {
			TextData td = new TextData("", 960, curY, AliteColors.get().message(), null);
			if (s.startsWith("[")) {
				curY += parseControlSequence(td, s.substring(1, s.indexOf("]")), s.substring(s.indexOf("]") + 1));
			} else {
				td.text = s;
				curY += 40;
			}
			td.y = curY;
			texts.add(td);			
		}
		font = ((Alite) game).getFont();
	}

	private int parseControlSequence(TextData td, String sequence, String text) {
		td.text = text;
		int height = Integer.parseInt(sequence.substring(0, 3).trim());
		td.scale = Float.parseFloat(sequence.substring(3, 6));
		char c = sequence.charAt(6);
		if (c == 'w') {
			td.color = AliteColors.get().creditsDescription();
		} else if (c == 'y') {
			td.color = AliteColors.get().creditsPerson();
		} else if (c == 'o') {
			td.color = AliteColors.get().creditsAddition();
		}
		return height;
	}
	
	@Override
	public void onActivation() {
		endCreditsMusic.setLooping(true);
		endCreditsMusic.play();
		initializeGl();
		mode = pendingMode == -1 ? 0 : pendingMode;
		pendingMode = -1;
	}

	public static boolean initialize(Alite alite, DataInputStream dis) {
		AboutScreen as = new AboutScreen(alite);
		try {
			as.pendingMode = dis.readInt();
			as.y = dis.readInt();
			as.alpha = dis.readFloat();
		} catch (Exception e) {
			AliteLog.e("About Screen Initialize", "Error in initializer.", e);
			return false;			
		}
		alite.setScreen(as);
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeInt(mode);
		dos.writeInt(y);
		dos.writeFloat(alpha);
	}
	
	private void initializeGl() {
		float ratio = (float) windowWidth / (float) windowHeight;	     
        GLES11.glMatrixMode(GLES11.GL_PROJECTION);
        GlUtils.setViewport(visibleArea);
        GLES11.glLoadIdentity();
        GlUtils.gluPerspective(game, 45.0f, ratio, 10.0f, 1000.0f);
        GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
        GLES11.glLoadIdentity();        
        GLES11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);
        GLES11.glHint(GLES11.GL_PERSPECTIVE_CORRECTION_HINT, GLES11.GL_NICEST);		        
        GLES11.glEnable(GLES11.GL_DEPTH_TEST);
        GLES11.glDepthFunc(GLES11.GL_LEQUAL);
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
	}

	private void performFadeIn(float deltaTime) {
        if (System.nanoTime() - lastTime > 50000) {        	        
        	y -= 2;
        }
        if (System.nanoTime() - lastTime > 1000000) {        	        
        	alpha *= 1.1f;
        	lastTime = System.nanoTime();
        	if (alpha >= 1.0f) {
        		alpha = 1.0f;
        		startTime = System.nanoTime();        		
        		mode = 1;
        	}
        }
	}
	
	private void performWait(long timeToWait) {
        if (System.nanoTime() - lastTime > 50000) {        	        
        	y -= 2;
        }
		if (System.nanoTime() - startTime > timeToWait) {
			lastTime = System.nanoTime();		
			mode++;
		}
	}

	private void performFadeOut(float deltaTime) {
        if (System.nanoTime() - lastTime > 50000) {        	        
        	y -= 2;
        }
        if (System.nanoTime() - lastTime > 1000000) {        	        
        	alpha *= 0.99f;
        	lastTime = System.nanoTime();
        	if (alpha <= 0.3f) {
        		alpha = 0.3f;
        		startTime = System.nanoTime();        		
        		mode = 3;
        	}
        }
	}
	
	private void performUpdateLines(float deltaTime) {
        if (System.nanoTime() - lastTime > 50000) {        	        
        	if (mode == 4) {
        		int n = texts.size();
        		// Leave the Thanks to Klaudia part on the screen,
        		// but scroll everything else...
        		for (int i = 1; i < 5; i++) {
        			texts.get(n - i).y += 2;
        		}
        	}
        	y -= 2;
        	if (end) {
        		mode = 4;
        	}
        }						
	}

	@Override
	public void performUpdate(float deltaTime) {
		if (mode == 0) {
			performFadeIn(deltaTime);
		} else if (mode == 1) {
			performWait(3000000000l);
		} else if (mode == 2) {
			performFadeOut(deltaTime);
		} else if (mode == 3 || mode == 4) {
			performUpdateLines(deltaTime);
		} 
		if (returnToOptions) {
			globalAlpha *= 0.95f;
			musicVolume *= 0.95f;
			endCreditsMusic.setVolume(musicVolume);
		}
		for (TouchEvent event: game.getInput().getTouchEvents()) {
			if (event.type == TouchEvent.TOUCH_DOWN && !returnToOptions) {
				SoundManager.play(Assets.click);
				returnToOptions = true;
			}
		}
		if (returnToOptions && globalAlpha < 0.01) {		
			GLES11.glClear(GLES11.GL_DEPTH_BUFFER_BIT | GLES11.GL_COLOR_BUFFER_BIT);
			GLES11.glDisable(GLES11.GL_DEPTH_TEST);
			game.setScreen(new OptionsScreen(game));
		}
	}
	
	private void setGlColor(long color) {
		int alpha = (int) ((color & 0xFF000000l) >> 24);
		int red   = (int) (color & 0x00FF0000) >> 16;
		int green = (int) (color & 0x0000FF00) >>  8;
		int blue  = (int) (color & 0x000000FF);
		
		GLES11.glColor4f(red / 255.0f * globalAlpha, green / 255.0f * globalAlpha, blue / 255.0f * globalAlpha, alpha / 255.0f * globalAlpha);
	}


	@Override
	public void performPresent(float deltaTime) {
		if (isDisposed) {
			return;
		}
		GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);
        GLES11.glClearDepthf(1.0f);

        GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
        GLES11.glLoadIdentity();                    

        GLES11.glEnable(GLES11.GL_TEXTURE_2D);
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glPushMatrix();		
		GLES11.glLoadIdentity();
		Rect visibleArea = ((AndroidGraphics) ((Alite) game).getGraphics()).getVisibleArea();
		GlUtils.ortho(game, visibleArea);		
		
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glLoadIdentity();
		GLES11.glDisable(GLES11.GL_DEPTH_TEST);		
		GLES11.glColor4f(globalAlpha, globalAlpha, globalAlpha, globalAlpha);
		background.render();
        GLES11.glColor4f(globalAlpha * alpha, globalAlpha * alpha, globalAlpha * alpha, globalAlpha * alpha);
        aliteLogo.render();
        if (y < 1200) {
        	int i = 0;
        	for (TextData text: texts) {
        		i++;
        		if (y + text.y > -120) {
            		if (y + text.y > 1080) {
            			break;
            		}
            		setGlColor(text.color);
            		font.drawText(text.text, text.x, y + text.y, true, text.scale);
            		if (y + text.y < 525 && i == texts.size() - 1) {
            			end = true;
            		}
        		}
        	}
        }
        GLES11.glColor4f(globalAlpha, globalAlpha, globalAlpha, globalAlpha);
        ((Alite) game).getFont().drawText("Alite Version " + Alite.VERSION_STRING, 0, 1030, false, 1.0f);
		GLES11.glDisable(GLES11.GL_CULL_FACE);
		GLES11.glMatrixMode(GLES11.GL_PROJECTION);
		GLES11.glPopMatrix();
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glEnable(GLES11.GL_DEPTH_TEST);
		
        GLES11.glDisable(GLES11.GL_TEXTURE_2D);
		GLES11.glBindTexture(GLES11.GL_TEXTURE_2D, 0);
	}

	@Override
	public void postPresent(float deltaTime) {
	}

	@Override
	public void pause() {
		super.pause();
		if (endCreditsMusic != null) {			
			endCreditsMusic.stop();
			endCreditsMusic.dispose();
			endCreditsMusic = null;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (aliteLogo != null) {
			aliteLogo.destroy();
			aliteLogo = null;
		}
		if (background != null) {
			background.destroy();
			background = null;
		}
		if (endCreditsMusic != null) {
			endCreditsMusic.stop();
			endCreditsMusic.dispose();
			endCreditsMusic = null;			
		}
	}

	@Override
	public void loadAssets() {
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.ABOUT_SCREEN;
	}	
}
