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

class TextureRegion {

   //--Members--//
   public float u1, v1;                               // Top/Left U,V Coordinates
   public float u2, v2;                               // Bottom/Right U,V Coordinates

   //--Constructor--//
   // D: calculate U,V coordinates from specified texture coordinates
   // A: texWidth, texHeight - the width and height of the texture the region is for
   //    x, y - the top/left (x,y) of the region on the texture (in pixels)
   //    width, height - the width and height of the region on the texture (in pixels)
   public TextureRegion(float texWidth, float texHeight, float x, float y, float width, float height)  {
      this.u1 = x / texWidth;                         // Calculate U1
      this.v1 = y / texHeight;                        // Calculate V1
      this.u2 = this.u1 + ( width / texWidth );       // Calculate U2
      this.v2 = this.v1 + ( height / texHeight );     // Calculate V2
   }
}
