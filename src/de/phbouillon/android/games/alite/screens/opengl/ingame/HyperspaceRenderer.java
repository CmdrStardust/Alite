package de.phbouillon.android.games.alite.screens.opengl.ingame;

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

import java.io.Serializable;

import android.opengl.GLES11;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Assets;
import de.phbouillon.android.games.alite.SoundManager;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.IMethodHook;
import de.phbouillon.android.games.alite.screens.opengl.objects.CylinderSpaceObject;

public class HyperspaceRenderer implements Serializable {
	private static final long serialVersionUID = -2731678015624042806L;

	private String textureFilename = "textures/plasma.png";
	private long startTime;
	
    private boolean intergal;
    
    private float red, green, blue;
    private transient IMethodHook finishHook = null;
    private boolean restartedSound = true;

    private float progress = 0.0f;
    private boolean finished = false;

    private CylinderSpaceObject cylinder;
    private InGameManager inGame;
    private Vector3f movementVector = new Vector3f(0, 0, 0);
    
	HyperspaceRenderer(InGameManager inGame) {
		this.inGame = inGame;
		init();		
	}
	
	private void init() {
		inGame.setPlayerControl(false);
		inGame.setViewport(0);
		inGame.getShip().setSpeed(0);
		inGame.getShip().getForwardVector().copy(movementVector);		
		startTime = System.nanoTime();
		Alite.get().getTextureManager().addTexture(textureFilename);
		cylinder = new CylinderSpaceObject(Alite.get(), "HyperspaceTunnel", 12500.0f, 100.0f, 16, false, false, textureFilename);
		red   = 0.2f + (float) Math.random() * 0.5f;
		green = 0.2f + (float) Math.random() * 0.5f;
		blue  = 0.2f + (float) Math.random() * 0.5f;
		SoundManager.stop(Assets.hyperspace);
		SoundManager.play(Assets.hyperspace);		
		progress = 0.0f;		
		cylinder.setMatrix(inGame.getShip().getMatrix());		
		cylinder.setColor(red, green, blue, 1.0f);
		cylinder.setSpeed(-400.0f);
		cylinder.moveForward(17.0f, movementVector);
		GLES11.glTexEnvf(GLES11.GL_TEXTURE_ENV, GLES11.GL_TEXTURE_ENV_MODE, GLES11.GL_MODULATE);
		movementVector.negate();
	}
		
	public void performUpdate(float deltaTime) {
		if (!restartedSound) {
			SoundManager.stop(Assets.hyperspace);
			SoundManager.play(Assets.hyperspace);
			restartedSound = true;
		}
		long diffTime = System.nanoTime() - startTime;
		if (diffTime >= 10000000000l){
			progress = 1.0f;
			finished = true;
		    GLES11.glMatrixMode(GLES11.GL_TEXTURE);
		    GLES11.glLoadIdentity();
			if (finishHook != null) {
				finishHook.execute(deltaTime);				
			} else {
				if (intergal) {
					Alite.get().performIntergalacticJump();
				} else {
					Alite.get().performHyperspaceJump();
				}
			}
		} else {
			progress = (float) diffTime / (float) 10000000000l;
		}
		cylinder.moveForward(deltaTime, movementVector);
		cylinder.applyDeltaRotation(0, 0, deltaTime * 21.0f);
	}

	public void setFinishHook(IMethodHook finishHook) {
		this.finishHook = finishHook;
	}
	
	public IMethodHook getFinishHook() {
		return finishHook;
	}

	public void performPresent(float deltaTime) {
        GLES11.glPointSize(2.0f);                
	    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE_MINUS_SRC_ALPHA);
        GLES11.glDisable(GLES11.GL_BLEND);

    	GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES11.glMatrixMode(GLES11.GL_TEXTURE);
    	GLES11.glTranslatef(0.0007f, -0.015f, 0.0f);
    	GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glPushMatrix();
		GLES11.glMultMatrixf(cylinder.getMatrix(), 0);
		GLES11.glColor4f(red, green, blue, 1.0f);		
		cylinder.render();
		GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES11.glPopMatrix();
		GLES11.glPointSize(1.0f);
	}
	
	boolean isFinished() {
		return finished;
	}
	
	float getProgress() {
		return progress;
	}
}
