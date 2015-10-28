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
import java.nio.FloatBuffer;

import android.graphics.Rect;
import android.opengl.GLES11;
import android.opengl.Matrix;
import de.phbouillon.android.framework.Game;
import de.phbouillon.android.framework.GlScreen;
import de.phbouillon.android.framework.impl.AndroidGraphics;
import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.ScreenCodes;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.IMethodHook;

//This screen never needs to be serialized, as it is not part of the InGame state.
@SuppressWarnings("serial")
public class HyperspaceScreen extends GlScreen {
	private long startTime;
	private static final float[] sScratch = new float[32];
	
	private int windowWidth;
	private int windowHeight;
	
    private boolean intergal;
    
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer textureBuffer;
    private float counter = 0.0f;
    private int totalIndices;
    private int crossSectionSides = 20;
    private int wholeTorusSides = 40;
    private float torusRadius = 1.75f;
    private float crossSectionRadius = 0.875f;
    private final String textureFilename = "textures/plasmabw.png";
    private float red, green, blue;
    private int increase;
    private IMethodHook finishHook = null;
    private boolean restartedSound = true;
    private transient boolean screenLoad = false;
    
	public HyperspaceScreen(Game game, boolean intergal) {
		super(game);
		this.intergal = intergal;		
	}

	public static HyperspaceScreen createScreen(Alite alite, DataInputStream dis) throws IOException {
		boolean intergal = dis.readBoolean();
		HyperspaceScreen hs = new HyperspaceScreen(alite, intergal);
		hs.counter = dis.readFloat();
		hs.red = dis.readFloat();
		hs.green = dis.readFloat();
		hs.blue = dis.readFloat();
		hs.increase = dis.readInt();
		hs.restartedSound = dis.readBoolean();
		hs.screenLoad = true;
		return hs;
	}
	
	public static boolean initialize(Alite alite, DataInputStream dis) {		
		try {
			HyperspaceScreen hs = createScreen(alite, dis);
			alite.setScreen(hs);
		} catch (Exception e) {
			AliteLog.e("Hyperspace Screen Initialize", "Error in initializer.", e);
			return false;			
		}		
		return true;
	}
	
	@Override
	public void saveScreenState(DataOutputStream dos) throws IOException {
		dos.writeBoolean(intergal);
		dos.writeFloat(counter);
		dos.writeFloat(red);
		dos.writeFloat(green);
		dos.writeFloat(blue);
		dos.writeInt(increase);
		dos.writeBoolean(restartedSound);
	}

	public void setNeedsSoundRestart() {
		restartedSound = false;
	}
	
	public void onActivation() {
		int [] size = game.getSize();
		windowWidth = size[0];
		windowHeight = size[1];
		startTime = System.nanoTime();
		((Alite) game).getTextureManager().addTexture(textureFilename);
		makeTorus(wholeTorusSides, crossSectionSides, torusRadius, crossSectionRadius);
		Rect visibleArea = ((AndroidGraphics) game.getGraphics()).getVisibleArea();
		initializeGl(visibleArea);
		if (!screenLoad) {
			increase = (int) (Math.random() * 3.0f);
			red   = 0.2f + (float) Math.random() * 0.5f;
			green = 0.2f + (float) Math.random() * 0.5f;
			blue  = 0.2f + (float) Math.random() * 0.5f;
		}
		screenLoad = false;
		SoundManager.stop(Assets.hyperspace);
		SoundManager.play(Assets.hyperspace);
	}
	
	@Override
	public void performUpdate(float deltaTime) {
		if (!restartedSound) {
			SoundManager.stop(Assets.hyperspace);
			SoundManager.play(Assets.hyperspace);
			restartedSound = true;
		}
		if (System.nanoTime() - startTime > 8000000000l){
			if (finishHook != null) {
				finishHook.execute(deltaTime);
			} else {
				if (intergal) {
					((Alite) game).performIntergalacticJump();
				} else {
					((Alite) game).performHyperspaceJump();
				}
			}
		}
	}

	public void initializeGl(Rect visibleArea) {				
		float ratio = (float) windowWidth / (float) windowHeight;
	     
		GlUtils.setViewport(visibleArea);
        GLES11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GLES11.glPointSize(2.0f);

        GLES11.glTexEnvf(GLES11.GL_TEXTURE_ENV, GLES11.GL_TEXTURE_ENV_MODE, GLES11.GL_MODULATE);
        
	    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
        GLES11.glDisable(GLES11.GL_BLEND);

        GLES11.glMatrixMode(GLES11.GL_PROJECTION);
        GLES11.glLoadIdentity();
        GlUtils.gluPerspective(game, 120f, ratio, 0.01f, 100f);
        GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
        GLES11.glLoadIdentity();        
        GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
        GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
        
        GLES11.glEnable(GLES11.GL_TEXTURE_2D);
        GLES11.glEnable(GLES11.GL_DEPTH_TEST);        
        ((Alite) game).getTextureManager().setTexture(textureFilename);
        GLES11.glDisable(GLES11.GL_LIGHTING);
	}
	
	private void makeTorus(int sides, int csSides, float radius, float csRadius) {
		totalIndices = 2 * (csSides + 1) * sides;

		vertexBuffer  = GlUtils.allocateFloatBuffer(4 * 3 * totalIndices);
		textureBuffer = GlUtils.allocateFloatBuffer(4 * 2 * totalIndices);
		
		float TAU = (float) (2.0f * Math.PI);		
		for (int i = 0; i < sides; i++) {
			for (int j = 0; j <= csSides; j++) {
				for (int k = 0; k <= 1; k++) {
					double s = (i + k) % sides + 0.5;
					double t = j % (csSides + 1);
					
					double x = (radius + csRadius * Math.cos(s * TAU / sides)) * Math.cos(t * TAU / csSides);
					double y = (radius + csRadius * Math.cos(s * TAU / sides)) * Math.sin(t * TAU / csSides);
					
					double z = csRadius * Math.sin(s * TAU / sides);
					double u = (i + k) / (float) sides;
					double v = t / (float) csSides;

					vertexBuffer.put((float) (2.0f * x));
					vertexBuffer.put((float) (2.0f * y));
					vertexBuffer.put((float) (2.0f * z));
					textureBuffer.put((float) u);
					textureBuffer.put((float) v);
				}
			}
		}
		vertexBuffer.position(0);
		textureBuffer.position(0);
	}
	
	private static void lookAt(float eyeX, float eyeY, float eyeZ,
			float centerX, float centerY, float centerZ, float upX, float upY,
			float upZ) {
		float[] scratch = sScratch;
		Matrix.setLookAtM(scratch, 0, eyeX, eyeY, eyeZ, centerX, centerY,
				centerZ, upX, upY, upZ);
		GLES11.glMultMatrixf(scratch, 0);
	}

	@Override
	public void performPresent(float deltaTime) {
		if (isDisposed) {
			return;
		}
		GLES11.glDisable(GLES11.GL_CULL_FACE);
        GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);
        
        counter += 0.72f;
        if (counter > 360) {
        	counter = 0;
        }
        switch (increase) {
        	case 0: red += 0.002f; if (red > 1.0f) red = 1.0f; break;
        	case 1: green += 0.002f; if (green > 1.0f) green = 1.0f; break;
        	case 2: blue += 0.002f; if (blue > 1.0f) blue = 1.0f; break;
        }
        GLES11.glLoadIdentity();
        lookAt(-3.5f, 0, 0,
        	   -3.5f, 1.0f, 0,
        	   (float) Math.sin(Math.toRadians(counter)), 0.0f, (float) Math.cos(Math.toRadians(counter)));
        
        GLES11.glRotatef(counter * 2, 0.0f, 0.0f, 1.0f);
    	GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
    	GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, textureBuffer); 
    	
    	GLES11.glMatrixMode(GLES11.GL_TEXTURE);
    	GLES11.glTranslatef(0.0f, -0.015f, 0.0f);
    	GLES11.glMatrixMode(GLES11.GL_MODELVIEW);

    	GLES11.glColor4f(red, green, blue, 1.0f);
    	GLES11.glDrawArrays(GLES11.GL_TRIANGLE_STRIP, 0, totalIndices);
    	GLES11.glEnable(GLES11.GL_CULL_FACE);
    	GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void dispose() {
		super.dispose();
		Assets.hyperspace.stop();
	}

	@Override
	public void loadAssets() {
	}

	@Override
	public void postPresent(float deltaTime) {
	}
	
	public void setFinishHook(IMethodHook finishHook) {
		this.finishHook = finishHook;
	}
	
	public IMethodHook getFinishHook() {
		return finishHook;
	}
	
	@Override
	public int getScreenCode() {
		return ScreenCodes.HYPERSPACE_SCREEN;
	}
}
