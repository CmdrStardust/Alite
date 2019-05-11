package de.phbouillon.android.games.alite.screens.opengl.objects.space.ships;

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

/**
 * Boomslang model and texture by Rolf Schuetteler
 * Renamed to Harken by Justin Darkness (http://alite.mobi/node/22)
 */

import de.phbouillon.android.framework.impl.gl.GlUtils;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.EngineExhaust;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.SpaceObject;

public class Boomslang extends SpaceObject {
	private static final long serialVersionUID = -3848399365999955125L;

	public static final Vector3f HUD_COLOR = new Vector3f(0.55f, 0.67f, 0.94f);

    private static final float [] VERTEX_DATA = new float [] {
         54.24f,  40.58f, -53.35f,  45.05f,  -0.00f,  -2.90f, 
         45.05f,  -0.00f, -59.77f,  45.05f,  -0.00f, -97.45f, 
         54.24f,  81.54f, -97.45f,  62.28f,  22.21f, -97.45f, 
         79.13f,  14.22f,  -2.90f,  62.28f,  28.44f,  -2.90f, 
         79.13f,  14.22f, -97.45f,  82.04f,   4.09f, -97.45f, 
         83.64f,  -1.46f,  -2.90f,  62.28f, -28.44f,  -2.90f, 
         79.13f, -14.22f,  -2.90f,  79.13f, -14.22f, -97.45f, 
         62.28f, -28.44f, -97.45f,  54.24f, -40.58f, -53.35f, 
         54.24f, -81.54f, -97.45f,  83.64f,  -1.46f, -97.45f, 
         61.31f,  -0.17f, 118.10f,  66.64f,   8.62f, 118.10f, 
         71.84f,   4.23f, 118.10f,  73.24f,  -0.62f, 118.10f, 
         71.84f,  -4.56f, 118.10f,  66.64f,  -8.96f, 118.10f, 
         75.11f,   8.71f,  47.28f,  64.68f,  17.51f,  47.28f, 
         77.90f,  -0.99f,  47.28f,  75.11f,  -8.90f,  47.28f, 
         64.68f, -17.70f,  47.28f,  54.02f,  -0.09f,  47.28f, 
         77.77f,  12.35f,  77.76f,  63.09f,  24.74f,  77.76f, 
         81.69f,  -1.30f,  77.76f,  77.77f,  -7.63f,  77.76f, 
         63.09f, -13.78f,  77.76f,  48.09f,  -0.03f,  77.76f, 
         46.20f,  -7.13f, -59.77f, -165.00f,  -7.11f,  17.96f, 
        -165.00f,  -0.00f,  17.96f, -128.20f,  -7.11f,  71.63f, 
        -145.07f,  -0.00f, -44.14f, -145.07f,  -7.11f, -44.14f, 
        -87.56f,  -0.00f, -93.21f, -87.56f,  -7.11f, -93.21f, 
        -17.80f,  -0.00f, -100.11f, -17.80f,  -7.11f, -100.11f, 
         13.64f,  -0.00f, -17.30f, -36.20f,  -0.00f,  -8.87f, 
        -66.86f,  -0.00f,  47.10f, -39.26f,  -7.11f, 100.00f, 
         48.51f,  -0.12f, -62.84f,  62.47f, -18.84f, -62.84f, 
         79.79f,  -1.08f, -62.84f,  62.47f,  18.59f, -62.84f, 
         62.47f,  18.59f, -118.10f,  62.47f, -18.84f, -118.10f, 
         48.51f,  -0.12f, -118.10f,  79.79f,  -1.08f, -118.10f, 
        107.27f,   1.13f, -65.41f, 165.00f,   1.13f, -97.45f
    };

    private static final float [] NORMAL_DATA = new float [] {
          0.97535f,  -0.22066f,   0.00000f,   0.97535f,  -0.22066f,   0.00000f, 
         -0.98331f,  -0.13330f,  -0.12380f,  -0.64420f,  -0.76320f,   0.05030f, 
         -0.42847f,  -0.90356f,   0.00000f,  -0.96104f,  -0.27640f,  -0.00000f, 
         -0.96104f,  -0.27641f,  -0.00000f,  -0.64502f,   0.76417f,   0.00000f, 
         -0.64502f,   0.76417f,   0.00000f,  -0.83358f,   0.55239f,   0.00000f, 
          0.98840f,   0.11129f,  -0.10336f,   0.00000f,   0.00000f,   1.00000f, 
          0.82767f,   0.50118f,  -0.25253f,  -0.94290f,   0.33307f,   0.00000f, 
         -0.94290f,   0.33307f,   0.00000f,   0.00000f,   0.00000f,  -1.00000f, 
          0.00000f,   0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f, 
          0.00000f,   0.00000f,  -1.00000f,   0.82767f,  -0.50118f,  -0.25253f, 
         -0.63917f,  -0.75724f,  -0.13435f,  -0.63917f,  -0.75724f,  -0.13435f, 
         -0.95556f,  -0.27482f,  -0.10669f,  -0.95556f,  -0.27483f,  -0.10669f, 
         -0.93716f,   0.33104f,  -0.11017f,  -0.93716f,   0.33105f,  -0.11017f, 
         -0.63941f,   0.75752f,  -0.13160f,  -0.63941f,   0.75752f,  -0.13161f, 
          0.84570f,   0.51210f,  -0.15016f,   0.84570f,   0.51210f,  -0.15016f, 
          0.84546f,  -0.51195f,  -0.15200f,   0.84546f,  -0.51196f,  -0.15200f, 
         -0.63811f,  -0.75599f,   0.14591f,  -0.63812f,  -0.75598f,   0.14591f, 
         -0.95456f,  -0.27454f,   0.11595f,  -0.95456f,  -0.27454f,   0.11595f, 
         -0.94071f,   0.33230f,   0.06816f,  -0.84468f,   0.52379f,   0.11029f, 
         -0.63948f,   0.75760f,  -0.13081f,  -0.38618f,   0.92241f,  -0.00459f, 
          0.84395f,   0.51104f,   0.16301f,   0.67439f,   0.73597f,  -0.05959f, 
          0.84367f,  -0.51088f,   0.16501f,   0.84367f,  -0.51087f,   0.16501f, 
         -0.62596f,  -0.74158f,  -0.24134f,  -0.62596f,  -0.74157f,  -0.24134f, 
         -0.94295f,  -0.27120f,  -0.19312f,  -0.94294f,  -0.27122f,  -0.19313f, 
         -0.83853f,   0.51998f,  -0.16272f,  -0.92398f,   0.32643f,  -0.19925f, 
         -0.38507f,   0.91975f,  -0.07602f,  -0.63762f,   0.75538f,  -0.15113f, 
          0.65996f,   0.72022f,  -0.21386f,   0.84748f,   0.51318f,  -0.13572f, 
          0.82327f,  -0.49852f,  -0.27150f,   0.82327f,  -0.49851f,  -0.27150f, 
          0.98723f,   0.15932f,  -0.00000f,   0.82473f,   0.00000f,  -0.56553f, 
          0.95215f,   0.00000f,   0.30563f,   0.95215f,   0.00000f,   0.30563f, 
          0.64912f,   0.00000f,   0.76069f,   0.64912f,   0.00000f,   0.76069f, 
          0.09842f,   0.00000f,   0.99514f,   0.09842f,   0.00000f,   0.99514f, 
         -0.54014f,   0.00000f,   0.84157f,  -0.53126f,  -0.08574f,   0.84286f, 
          0.05633f,   0.99080f,  -0.12304f,   0.00006f,  -1.00000f,  -0.00002f, 
         -0.00000f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f, 
          0.00000f,  -1.00000f,   0.00000f,  -0.00000f,  -1.00000f,   0.00000f, 
          0.00009f,  -1.00000f,   0.00000f,   0.00000f,  -1.00000f,   0.00000f, 
          0.04658f,  -0.98651f,  -0.15691f,   0.05326f,  -0.98452f,  -0.16697f, 
          0.00000f,   1.00000f,   0.00000f,  -0.12919f,   0.97165f,   0.19801f, 
         -0.06600f,   0.99759f,  -0.02118f,  -0.05536f,   0.99801f,  -0.03033f, 
         -0.04722f,   0.99735f,  -0.05533f,  -0.01290f,   0.99701f,  -0.07621f, 
         -0.00816f,   0.99656f,  -0.08249f,   0.07081f,   0.99118f,  -0.11200f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.00000f,   0.00000f,   1.00000f,   0.00000f,   0.00000f,   1.00000f, 
          0.78507f,  -0.60877f,   0.11430f,   0.83002f,   0.50260f,   0.24177f, 
          0.79466f,   0.59313f,   0.12931f,  -0.75483f,   0.59738f,   0.27087f, 
         -0.73791f,  -0.66589f,   0.10991f,  -0.74448f,  -0.65551f,   0.12672f, 
         -0.80138f,   0.59815f,   0.00000f,  -0.80138f,   0.59815f,   0.00000f, 
         -0.80138f,  -0.59815f,   0.00000f,  -0.80138f,  -0.59815f,   0.00000f, 
          0.71581f,  -0.69830f,   0.00000f,   0.71581f,  -0.69830f,   0.00000f, 
          0.75053f,   0.66084f,   0.00000f,   0.75053f,   0.66084f,   0.00000f, 
         -0.10891f,   0.99405f,   0.00000f,  -0.03175f,   0.99786f,  -0.05720f, 
         -0.04344f,  -0.99739f,  -0.05773f,   0.00000f,   0.00000f,   1.00000f, 
         -0.88675f,  -0.46125f,   0.03040f,  -0.97929f,   0.14833f,  -0.13776f, 
         -0.03551f,  -0.99732f,  -0.06396f,   0.98840f,  -0.11129f,  -0.10336f, 
          0.97274f,   0.23172f,  -0.00937f,   0.97161f,   0.23612f,   0.01500f, 
          0.79390f,  -0.59257f,   0.13634f,  -0.70831f,   0.69098f,   0.14437f
    };

    private static final float [] TEXTURE_COORDINATE_DATA = new float [] {
          0.13f,   0.11f,   0.24f,   0.22f,   0.12f,   0.22f, 
          0.12f,   0.22f,   0.04f,   0.22f,   0.13f,   0.11f, 
          0.52f,   0.01f,   0.52f,   0.14f,   0.61f,   0.11f, 
          0.72f,   0.19f,   0.72f,   0.14f,   0.52f,   0.14f, 
          0.52f,   0.14f,   0.52f,   0.19f,   0.72f,   0.19f, 
          0.52f,   0.21f,   0.72f,   0.22f,   0.72f,   0.19f, 
          0.72f,   0.19f,   0.52f,   0.19f,   0.52f,   0.21f, 
          0.72f,   0.30f,   0.72f,   0.25f,   0.52f,   0.26f, 
          0.52f,   0.26f,   0.52f,   0.30f,   0.72f,   0.30f, 
          0.52f,   0.30f,   0.61f,   0.33f,   0.72f,   0.30f, 
          0.04f,   0.42f,   0.04f,   0.22f,   0.13f,   0.33f, 
          0.43f,   0.72f,   0.46f,   0.67f,   0.44f,   0.53f, 
          0.24f,   0.30f,   0.13f,   0.33f,   0.24f,   0.22f, 
          0.52f,   0.26f,   0.72f,   0.25f,   0.72f,   0.22f, 
          0.72f,   0.22f,   0.52f,   0.22f,   0.52f,   0.26f, 
          0.65f,   0.81f,   0.61f,   0.75f,   0.58f,   0.78f, 
          0.65f,   0.81f,   0.58f,   0.78f,   0.57f,   0.81f, 
          0.65f,   0.81f,   0.57f,   0.81f,   0.58f,   0.84f, 
          0.65f,   0.81f,   0.58f,   0.84f,   0.61f,   0.87f, 
          0.24f,   0.22f,   0.13f,   0.11f,   0.24f,   0.14f, 
          0.72f,   0.14f,   0.72f,   0.19f,   0.83f,   0.19f, 
          0.83f,   0.19f,   0.83f,   0.16f,   0.72f,   0.14f, 
          0.72f,   0.19f,   0.72f,   0.22f,   0.83f,   0.22f, 
          0.83f,   0.22f,   0.83f,   0.19f,   0.72f,   0.19f, 
          0.72f,   0.22f,   0.72f,   0.25f,   0.83f,   0.25f, 
          0.83f,   0.25f,   0.83f,   0.22f,   0.72f,   0.22f, 
          0.72f,   0.25f,   0.72f,   0.30f,   0.83f,   0.29f, 
          0.83f,   0.29f,   0.83f,   0.25f,   0.72f,   0.25f, 
          0.24f,   0.30f,   0.24f,   0.22f,   0.35f,   0.22f, 
          0.35f,   0.22f,   0.35f,   0.29f,   0.24f,   0.30f, 
          0.24f,   0.22f,   0.24f,   0.14f,   0.35f,   0.16f, 
          0.35f,   0.16f,   0.35f,   0.22f,   0.24f,   0.22f, 
          0.83f,   0.16f,   0.83f,   0.19f,   0.90f,   0.19f, 
          0.90f,   0.19f,   0.90f,   0.14f,   0.83f,   0.16f, 
          0.83f,   0.19f,   0.83f,   0.22f,   0.90f,   0.22f, 
          0.90f,   0.22f,   0.90f,   0.19f,   0.83f,   0.19f, 
          0.83f,   0.22f,   0.83f,   0.25f,   0.90f,   0.25f, 
          0.90f,   0.25f,   0.90f,   0.22f,   0.83f,   0.22f, 
          0.83f,   0.25f,   0.83f,   0.29f,   0.90f,   0.30f, 
          0.90f,   0.30f,   0.90f,   0.25f,   0.83f,   0.25f, 
          0.35f,   0.29f,   0.35f,   0.22f,   0.41f,   0.22f, 
          0.41f,   0.22f,   0.41f,   0.30f,   0.35f,   0.29f, 
          0.35f,   0.22f,   0.35f,   0.16f,   0.41f,   0.14f, 
          0.41f,   0.14f,   0.41f,   0.22f,   0.35f,   0.22f, 
          0.90f,   0.14f,   0.90f,   0.19f,   0.98f,   0.21f, 
          0.98f,   0.21f,   0.98f,   0.20f,   0.90f,   0.14f, 
          0.90f,   0.19f,   0.90f,   0.22f,   0.98f,   0.22f, 
          0.98f,   0.22f,   0.98f,   0.21f,   0.90f,   0.19f, 
          0.90f,   0.22f,   0.90f,   0.25f,   0.98f,   0.23f, 
          0.98f,   0.23f,   0.98f,   0.22f,   0.90f,   0.22f, 
          0.90f,   0.25f,   0.90f,   0.30f,   0.98f,   0.25f, 
          0.98f,   0.25f,   0.98f,   0.23f,   0.90f,   0.25f, 
          0.41f,   0.30f,   0.41f,   0.22f,   0.50f,   0.22f, 
          0.50f,   0.22f,   0.50f,   0.25f,   0.41f,   0.30f, 
          0.41f,   0.22f,   0.41f,   0.14f,   0.50f,   0.20f, 
          0.50f,   0.20f,   0.50f,   0.22f,   0.41f,   0.22f, 
          0.04f,   0.22f,   0.12f,   0.22f,   0.12f,   0.23f, 
          0.13f,   0.98f,   0.13f,   0.97f,   0.04f,   0.98f, 
          0.21f,   0.97f,   0.13f,   0.97f,   0.13f,   0.98f, 
          0.13f,   0.98f,   0.21f,   0.98f,   0.21f,   0.97f, 
          0.35f,   0.97f,   0.21f,   0.97f,   0.21f,   0.98f, 
          0.21f,   0.98f,   0.35f,   0.98f,   0.35f,   0.97f, 
          0.51f,   0.97f,   0.35f,   0.97f,   0.35f,   0.98f, 
          0.35f,   0.98f,   0.51f,   0.98f,   0.51f,   0.97f, 
          0.62f,   0.97f,   0.51f,   0.97f,   0.51f,   0.98f, 
          0.51f,   0.98f,   0.62f,   0.98f,   0.62f,   0.97f, 
          0.52f,   0.60f,   0.59f,   0.70f,   0.52f,   0.73f, 
          0.52f,   0.60f,   0.59f,   0.70f,   0.66f,   0.51f, 
          0.70f,   0.72f,   0.77f,   0.85f,   0.94f,   0.64f, 
          0.59f,   0.70f,   0.82f,   0.52f,   0.66f,   0.51f, 
          0.70f,   0.72f,   0.94f,   0.64f,   0.82f,   0.52f, 
          0.59f,   0.70f,   0.70f,   0.72f,   0.82f,   0.52f, 
          0.52f,   0.60f,   0.52f,   0.73f,   0.59f,   0.70f, 
          0.77f,   0.85f,   0.99f,   0.78f,   0.94f,   0.64f, 
          0.77f,   0.85f,   0.71f,   0.97f,   0.99f,   0.78f, 
          0.71f,   0.97f,   0.91f,   0.90f,   0.99f,   0.78f, 
          0.99f,   0.78f,   0.91f,   0.90f,   0.71f,   0.97f, 
          0.99f,   0.78f,   0.71f,   0.97f,   0.77f,   0.85f, 
          0.94f,   0.64f,   0.99f,   0.78f,   0.77f,   0.85f, 
          0.94f,   0.64f,   0.77f,   0.85f,   0.70f,   0.72f, 
          0.82f,   0.52f,   0.94f,   0.64f,   0.70f,   0.72f, 
          0.82f,   0.52f,   0.70f,   0.72f,   0.59f,   0.70f, 
          0.66f,   0.51f,   0.82f,   0.52f,   0.59f,   0.70f, 
          0.52f,   0.60f,   0.66f,   0.51f,   0.59f,   0.70f, 
          0.02f,   0.78f,   0.07f,   0.85f,   0.14f,   0.78f, 
          0.14f,   0.78f,   0.07f,   0.71f,   0.02f,   0.78f, 
          0.46f,   0.67f,   0.50f,   0.72f,   0.49f,   0.71f, 
          0.49f,   0.71f,   0.49f,   0.69f,   0.46f,   0.67f, 
          0.46f,   0.79f,   0.49f,   0.75f,   0.50f,   0.72f, 
          0.43f,   0.72f,   0.44f,   0.91f,   0.46f,   0.79f, 
          0.38f,   0.58f,   0.30f,   0.58f,   0.38f,   0.52f, 
          0.30f,   0.58f,   0.21f,   0.58f,   0.22f,   0.52f, 
          0.22f,   0.52f,   0.30f,   0.52f,   0.30f,   0.58f, 
          0.21f,   0.58f,   0.12f,   0.58f,   0.22f,   0.52f, 
          0.12f,   0.58f,   0.02f,   0.58f,   0.02f,   0.52f, 
          0.02f,   0.52f,   0.12f,   0.52f,   0.12f,   0.58f, 
          0.38f,   0.52f,   0.30f,   0.52f,   0.30f,   0.68f, 
          0.30f,   0.68f,   0.38f,   0.68f,   0.38f,   0.52f, 
          0.30f,   0.52f,   0.22f,   0.52f,   0.22f,   0.68f, 
          0.22f,   0.68f,   0.30f,   0.68f,   0.30f,   0.52f, 
          0.22f,   0.52f,   0.12f,   0.52f,   0.12f,   0.68f, 
          0.12f,   0.68f,   0.22f,   0.68f,   0.22f,   0.52f, 
          0.12f,   0.52f,   0.02f,   0.52f,   0.02f,   0.68f, 
          0.02f,   0.68f,   0.12f,   0.68f,   0.12f,   0.52f, 
          0.27f,   0.31f,   0.48f,   0.31f,   0.34f,   0.37f, 
          0.34f,   0.37f,   0.27f,   0.51f,   0.27f,   0.31f, 
          0.48f,   0.31f,   0.27f,   0.31f,   0.34f,   0.37f, 
          0.95f,   0.58f,   0.98f,   0.58f,   0.97f,   0.35f, 
          0.52f,   0.14f,   0.72f,   0.14f,   0.61f,   0.11f, 
          0.52f,   0.30f,   0.52f,   0.42f,   0.61f,   0.33f, 
          0.27f,   0.31f,   0.27f,   0.51f,   0.34f,   0.37f, 
          0.04f,   0.22f,   0.04f,   0.01f,   0.13f,   0.11f, 
          0.12f,   0.23f,   0.24f,   0.22f,   0.13f,   0.33f, 
          0.13f,   0.33f,   0.04f,   0.22f,   0.12f,   0.23f, 
          0.30f,   0.58f,   0.30f,   0.52f,   0.38f,   0.52f, 
          0.12f,   0.58f,   0.12f,   0.52f,   0.22f,   0.52f
    };

    public Boomslang(Alite alite) {
        super(alite, "Harken", ObjectType.EnemyShip);
        shipType = ShipType.Boomslang;
        boundingBox = new float [] {-165.00f, 165.00f, -81.54f,  81.54f, -118.10f, 118.10f};
        numberOfVertices = 348;
        textureFilename = "textures/boomslang.png";
        maxSpeed          = 367.4f;
        maxPitchSpeed     = 2.500f;
        maxRollSpeed      = 3.250f;
        hullStrength      = 190.0f;
        hasEcm            = true;
        cargoType         = 11;
        aggressionLevel   = 14;
        escapeCapsuleCaps = 0;
        bounty            = 150;
        score             = 190;
        legalityType      = 1;
        maxCargoCanisters = 2;
        laserHardpoints.add(VERTEX_DATA[54]);
        laserHardpoints.add(VERTEX_DATA[55]);
        laserHardpoints.add(VERTEX_DATA[56]);
        laserHardpoints.add(VERTEX_DATA[147]);
        laserHardpoints.add(VERTEX_DATA[148]);
        laserHardpoints.add(VERTEX_DATA[149]);
        init();
    }
    
    @Override
    protected void init() {
        vertexBuffer = createFaces(VERTEX_DATA, NORMAL_DATA,
                0,   1,   2,   2,   3,   0,   4,   5,   0,   6,   7,   5,   5,   8,   6, 
                9,  10,   6,   6,   8,   9,  11,  12,  13,  13,  14,  11,  14,  15,  11, 
               16,   3,  15,   3,   5,   4,  11,  15,   1,  13,  12,  10,  10,  17,  13, 
               18,  19,  20,  18,  20,  21,  18,  21,  22,  18,  22,  23,   1,   0,   7, 
                7,   6,  24,  24,  25,   7,   6,  10,  26,  26,  24,   6,  10,  12,  27, 
               27,  26,  10,  12,  11,  28,  28,  27,  12,  11,   1,  29,  29,  28,  11, 
                1,   7,  25,  25,  29,   1,  25,  24,  30,  30,  31,  25,  24,  26,  32, 
               32,  30,  24,  26,  27,  33,  33,  32,  26,  27,  28,  34,  34,  33,  27, 
               28,  29,  35,  35,  34,  28,  29,  25,  31,  31,  35,  29,  31,  30,  20, 
               20,  19,  31,  30,  32,  21,  21,  20,  30,  32,  33,  22,  22,  21,  32, 
               33,  34,  23,  23,  22,  33,  34,  35,  18,  18,  23,  34,  35,  31,  19, 
               19,  18,  35,   3,   2,  36,  37,  38,  39,  40,  38,  37,  37,  41,  40, 
               42,  40,  41,  41,  43,  42,  44,  42,  43,  43,  45,  44,   2,  44,  45, 
               45,  36,   2,  36,  46,   1,   2,  46,  44,  47,  48,  40,  46,  42,  44, 
               47,  40,  42,  46,  47,  42,   2,   1,  46,  48,  38,  40,  48,  49,  38, 
               49,  39,  38,  37,  39,  49,  37,  49,  48,  41,  37,  48,  41,  48,  47, 
               43,  41,  47,  43,  47,  46,  45,  43,  46,  36,  45,  46,  50,  51,  52, 
               52,  53,  50,   5,  17,   9,   9,   8,   5,  14,  13,  17,   3,  16,  14, 
                5,   3,  54,   3,  14,  55,  55,  56,   3,  14,  17,  55,  17,   5,  54, 
               54,  57,  17,  54,  56,  50,  50,  53,  54,  56,  55,  51,  51,  50,  56, 
               55,  57,  52,  52,  51,  55,  57,  54,  53,  53,  52,  57,  17,  10,  58, 
               58,  59,  17,  10,   9,  58,   9,  17,  59,   5,   7,   0,  14,  16,  15, 
                9,  59,  58,   3,   4,   0,  36,   1,  15,  15,   3,  36,   3,  56,  54, 
               17,  57,  55);
        texCoordBuffer = GlUtils.toFloatBufferPositionZero(TEXTURE_COORDINATE_DATA);
        alite.getTextureManager().addTexture(textureFilename);  
        if (Settings.engineExhaust) {
        	addExhaust(new EngineExhaust(this, 13, 13, 30, 60, 0, 0, 0.82f, 0.51f, 0.72f, 0.7f));
        }
        initTargetBox();
    }

    @Override
    public boolean isVisibleOnHud() {
        return true;
    }

    @Override
    public Vector3f getHudColor() {
        return HUD_COLOR;
    }

    @Override
    public float getDistanceFromCenterToBorder(Vector3f dir) {
        return 50.0f;
    }
}
