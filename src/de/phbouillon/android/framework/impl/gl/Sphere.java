package de.phbouillon.android.framework.impl.gl;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;

import android.opengl.GLES11;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.opengl.sprites.SpriteData;

public class Sphere implements Serializable {
	private static final long serialVersionUID = 604349083831769333L;

	protected transient FloatBuffer normalBuffer;
	protected transient FloatBuffer vertexBuffer;
	protected transient FloatBuffer texCoordBuffer;
	
	protected final int numberOfVertices;
	protected final int glDrawMode;
	protected transient Alite alite;
	protected final String textureFilename;
	
	protected final float [] allNormals;
	protected float radius;
	private final int slices;
	private final int stacks;
	private final boolean inside;
	private final boolean hasNormals;
	private final SpriteData spriteData;
	private float r, g, b, a;
	
	public Sphere(final Alite alite, final float radius, final int slices, final int stacks, final String textureFilename, final SpriteData spriteData, final boolean inside) {
		numberOfVertices = slices * stacks * 6;
		this.alite = alite;
		this.radius = radius;
		this.spriteData = spriteData;
		this.slices = slices;
		this.stacks = stacks;
		this.inside = inside;
		hasNormals = !inside;
		
		vertexBuffer   = GlUtils.allocateFloatBuffer(4 * 3 * numberOfVertices);
		if (textureFilename != null) {
			texCoordBuffer = GlUtils.allocateFloatBuffer(4 * 2 * numberOfVertices);
		} else {
			texCoordBuffer = null;
		}
		if (hasNormals) {
			normalBuffer = GlUtils.allocateFloatBuffer(4 * 3 * numberOfVertices);
			allNormals   = new float[3 * numberOfVertices];
		} else {
			normalBuffer = null;
			allNormals = null;
		}

		plotSpherePoints(slices, stacks, radius, inside);
		this.textureFilename = textureFilename;
		if (textureFilename != null) {
			alite.getTextureManager().addTexture(textureFilename);
		}
		glDrawMode = GLES11.GL_TRIANGLES;
	}

	private void readObject(ObjectInputStream in) throws IOException {
		try {
			AliteLog.e("readObject", "Sphere.readObject");
			in.defaultReadObject();
			AliteLog.e("readObject", "Sphere.readObject I");
			this.alite = Alite.get();
			vertexBuffer   = GlUtils.allocateFloatBuffer(4 * 3 * numberOfVertices);
			if (textureFilename != null) {
				texCoordBuffer = GlUtils.allocateFloatBuffer(4 * 2 * numberOfVertices);
			} else {
				texCoordBuffer = null;
			}
			if (hasNormals) {
				normalBuffer = GlUtils.allocateFloatBuffer(4 * 3 * numberOfVertices);
			} else {
				normalBuffer = null;
			}			
			plotSpherePoints(slices, stacks, radius, inside);
			AliteLog.e("readObject", "Sphere.readObject II");
		} catch (ClassNotFoundException e) {
			AliteLog.e("Class not found", e.getMessage(), e);
		}
	}
	
	private void writeObject(ObjectOutputStream out)
            throws IOException {
		try {
			out.defaultWriteObject();
		} catch(IOException e) {
			AliteLog.e("PersistenceException", "Sphere " + textureFilename, e);
			throw(e);
		}
    }

	public void setNewSize(float newRadius) {
		this.radius = newRadius;
		vertexBuffer.clear();
		if (hasNormals) {
			for (float n: allNormals) {
				vertexBuffer.put(newRadius * n);
			}
		}
		vertexBuffer.position(0);
	}
	
	public float getRadius() {
		return radius;
	}
	
	private void plotSpherePoints(int slices, int stacks, float radius, boolean inside) {
	    float theta, phi;
	    float phi_step = (float) (2.0f * Math.PI / (slices - 1));
	    float theta_step = (float) (Math.PI / (stacks - 1));
		
	    float u, v;
	    float u_step = 1.0f / (slices - 1);
	    float v_step = -1.0f / (stacks - 1);
				
	    int normalOffset = 0;
	    
		/* Step 360 degrees around pole (slice loop) */
	    for (phi = 0f, u = 0f; phi < 2.0 * Math.PI; phi += phi_step, u += u_step) {
			/* For current slice calculate 180 degree stack from pole to pole */ 
			for (theta = 0, v = 0; theta < Math.PI; theta += theta_step, v += v_step) {
				/*
				 * Calculate quad. Original showed a pole facing viewer so swapped
				 * Y and Z to have poles going up/down rather than rotate geometry;
				 * also negated new Y (the old Z) to flip texture so North pole
				 * is up.
				 */
				float x1 = (float) (Math.sin(phi) * Math.sin(theta));
				float y1 = (float) (-Math.cos(theta));
				float z1 = (float) (Math.cos(phi) * Math.sin(theta));

	            float x2 = (float) (Math.sin(phi + phi_step) * Math.sin(theta));
	            float y2 = (float) (-Math.cos(theta));
	            float z2 = (float) (Math.cos(phi + phi_step) * Math.sin(theta));
	            
	            float x3 = (float) (Math.sin(phi + phi_step) * Math.sin(theta + theta_step));
	            float y3 = (float) (-Math.cos(theta + theta_step));
	            float z3 = (float) (Math.cos(phi + phi_step) * Math.sin(theta + theta_step));

	            float x4 = (float) (Math.sin(phi) * Math.sin(theta + theta_step));
	            float y4 = (float) (-Math.cos(theta + theta_step));
	            float z4 = (float) (Math.cos(phi) * Math.sin(theta + theta_step));
				
				/*
				 * Split quad into 2 triangles (although 2 vertices are shared we output
				 * 6 vertices because the shared vertices will need different uv values;
				 * an index array would provide a TnL performance improvement).
				 */ 
				vertexBuffer.put(radius * x1);
				vertexBuffer.put(radius * y1);
				vertexBuffer.put(radius * z1);
				vertexBuffer.put(radius * x2);
				vertexBuffer.put(radius * y2);
				vertexBuffer.put(radius * z2);
				vertexBuffer.put(radius * x3);
				vertexBuffer.put(radius * y3);
				vertexBuffer.put(radius * z3);
				vertexBuffer.put(radius * x1);
				vertexBuffer.put(radius * y1);
				vertexBuffer.put(radius * z1);
				vertexBuffer.put(radius * x3);
				vertexBuffer.put(radius * y3);
				vertexBuffer.put(radius * z3);
				vertexBuffer.put(radius * x4);
				vertexBuffer.put(radius * y4);
				vertexBuffer.put(radius * z4);

				if (hasNormals) {
					normalBuffer.put(x1); allNormals[normalOffset++] = x1;
					normalBuffer.put(y1); allNormals[normalOffset++] = y1;
					normalBuffer.put(z1); allNormals[normalOffset++] = z1;
					normalBuffer.put(x2); allNormals[normalOffset++] = x2;
					normalBuffer.put(y2); allNormals[normalOffset++] = y2;
					normalBuffer.put(z2); allNormals[normalOffset++] = z2;
					normalBuffer.put(x3); allNormals[normalOffset++] = x3;
					normalBuffer.put(y3); allNormals[normalOffset++] = y3;
					normalBuffer.put(z3); allNormals[normalOffset++] = z3;
					normalBuffer.put(x1); allNormals[normalOffset++] = x1;
					normalBuffer.put(y1); allNormals[normalOffset++] = y1;
					normalBuffer.put(z1); allNormals[normalOffset++] = z1;
					normalBuffer.put(x3); allNormals[normalOffset++] = x3;
					normalBuffer.put(y3); allNormals[normalOffset++] = y3;
					normalBuffer.put(z3); allNormals[normalOffset++] = z3;
					normalBuffer.put(x4); allNormals[normalOffset++] = x4;
					normalBuffer.put(y4); allNormals[normalOffset++] = y4;
					normalBuffer.put(z4); allNormals[normalOffset++] = z4;
				}
				
				if (spriteData == null) {
					if (texCoordBuffer != null) {
						texCoordBuffer.put(inside ? -u : u);
						texCoordBuffer.put(v);
						texCoordBuffer.put(inside ? -(u + u_step) : u + u_step);
						texCoordBuffer.put(v);
						texCoordBuffer.put(inside ? -(u + u_step) : u + u_step);
						texCoordBuffer.put(v + v_step);

						texCoordBuffer.put(inside ? -u : u);
						texCoordBuffer.put(v);
						texCoordBuffer.put(inside ? -(u + u_step) : u + u_step);
						texCoordBuffer.put(v + v_step);
						texCoordBuffer.put(inside ? -u : u);
						texCoordBuffer.put(v + v_step);
					}
				} else {
					float dx = spriteData.x2 - spriteData.x;
					float dy = spriteData.y2 - spriteData.y;
					texCoordBuffer.put(spriteData.x + u * dx);
					texCoordBuffer.put(spriteData.y + v * dy);
					texCoordBuffer.put(spriteData.x + (u + u_step) * dx);
					texCoordBuffer.put(spriteData.y + v * dy);
					texCoordBuffer.put(spriteData.x + (u + u_step) * dx);
					texCoordBuffer.put(spriteData.y + (v + v_step) * dy);
					texCoordBuffer.put(spriteData.x + (u) * dx);
					texCoordBuffer.put(spriteData.y + (v) * dy);
					texCoordBuffer.put(spriteData.x + (u + u_step) * dx);
					texCoordBuffer.put(spriteData.y + (v + v_step) * dy);
					texCoordBuffer.put(spriteData.x + (u) * dx);
					texCoordBuffer.put(spriteData.y + (v + v_step) * dy);
				}
	        }
	    }
	    vertexBuffer.position(0);	    
	    if (texCoordBuffer != null) {
	    	texCoordBuffer.position(0);
	    }
	    if (hasNormals) {
	    	normalBuffer.position(0);
	    }
	}
	
	public void render() {
		if (hasNormals) {
			GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
			GLES11.glNormalPointer(GLES11.GL_FLOAT, 0, normalBuffer);
		} else {
			GLES11.glDisableClientState(GLES11.GL_NORMAL_ARRAY);
		}
		GLES11.glVertexPointer(3, GLES11.GL_FLOAT, 0, vertexBuffer);
		if (textureFilename != null) {
			GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 0, texCoordBuffer);
		} else {
			GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glDisable(GLES11.GL_LIGHTING);
			GLES11.glColor4f(r, g, b, a);
		}
		alite.getTextureManager().setTexture(textureFilename);
		GLES11.glDrawArrays(glDrawMode, 0, numberOfVertices);
		if (!hasNormals) {
			GLES11.glEnableClientState(GLES11.GL_NORMAL_ARRAY);
		}
		if (textureFilename == null) {
			GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
			GLES11.glEnable(GLES11.GL_LIGHTING);
			GLES11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);			
		}
	}
	
	public void drawArrays() {
		GLES11.glDrawArrays(glDrawMode, 0, numberOfVertices);
	}
			
	public void destroy() {
		if (textureFilename != null) {
			alite.getTextureManager().freeTexture(textureFilename);
		}
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public float getR() { return r; };
	public float getG() { return g; };
	public float getB() { return b; };
	public float getA() { return a; };
}
