package de.phbouillon.android.games.alite;

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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.phbouillon.android.framework.FileIO;
import de.phbouillon.android.framework.Sound;

public class Settings {
	private static final boolean ALWAYS_WRITE_LOG = false;
	public static final boolean suppressOnlineLog = false;
	public static final boolean VIS_DEBUG = false;
	
	public static final int FIRE                   =   0;
	public static final int MISSILE                =   1;
	public static final int ECM                    =   2;
	public static final int RETRO_ROCKETS          =   3;	
	public static final int ESCAPE_CAPSULE         =   4;
	public static final int ENERGY_BOMB            =   5;	
	public static final int STATUS                 =   6;
	public static final int TORUS                  =   7;
	public static final int HYPERSPACE             =   8;
	public static final int GALACTIC_HYPERSPACE    =   9;	
	public static final int CLOAKING_DEVICE        =  10;
	public static final int ECM_JAMMER             =  11;	

	public static boolean animationsEnabled = true;
	public static String keyboardLayout = "QWERTY";
	public static boolean debugActive = false;
	public static boolean logToFile = ALWAYS_WRITE_LOG;
	public static boolean displayFrameRate = false;
	public static boolean displayDockingInformation = false;
	public static boolean memDebug = false;	
	public static boolean onlineMemDebug = false;
	public static int textureLevel = 1;
	public static int colorDepth = 1;
	public static float alpha = 0.75f;
	public static float controlAlpha = 0.5f;
	public static float volumes[] = { 1.0f, 0.5f, 0.5f, 0.5f };
	public static float vibrateLevel = 0.5f;
	public static ShipControl controlMode = ShipControl.ACCELEROMETER;
	public static int controlPosition = 1;
	public static int introVideoQuality = 255;
	public static boolean unlimitedFuel = false;
	public static boolean enterInSafeZone = false;
	public static boolean disableAttackers = false;
	public static boolean disableTraders = false;
	public static boolean invulnerable = false;
	public static boolean laserDoesNotOverheat = false;
	public static int particleDensity = 2;
	public static boolean tapRadarToChangeView = true;
	public static boolean laserButtonAutofire = true;
	public static boolean hasBeenPlayedBefore = false;	
	public static int [] buttonPosition = new int[12];
    public static boolean engineExhaust = true;
    public static boolean targetBox = true;
    public static int lockScreen = 0;
    public static int colorScheme = 0;    
    public static int laserPowerOverride = 0;
    public static int shieldPowerOverride = 0;
    public static boolean freePath = false;
    public static boolean autoId = true;    
    public static boolean reversePitch = false;
    public static boolean flatButtonDisplay = false;
    public static int dockingComputerSpeed = 0;
    public static int difficultyLevel = 3;
	public static int restoredCommanderCount = 0;
    public static boolean navButtonsVisible = true;
    
	public static void load(FileIO files) {
		for (int i = 0; i < 12; i++) {
			buttonPosition[i] = i;
		}
		BufferedReader in = null;
		boolean fastDC = false;
		try {
			in = new BufferedReader(new InputStreamReader(
					files.readFile(".alite")));
			animationsEnabled = Boolean.parseBoolean(in.readLine());
			keyboardLayout = in.readLine();
			if (keyboardLayout != null) {
				keyboardLayout = keyboardLayout.trim();
			}
			debugActive = Boolean.parseBoolean(in.readLine());
			logToFile = Boolean.parseBoolean(in.readLine()) || ALWAYS_WRITE_LOG;
			displayFrameRate = Boolean.parseBoolean(in.readLine());
			displayDockingInformation = Boolean.parseBoolean(in.readLine());
			memDebug = Boolean.parseBoolean(in.readLine());
			textureLevel = Integer.parseInt(in.readLine());
			colorDepth = Integer.parseInt(in.readLine());
			alpha = Float.parseFloat(in.readLine());
			volumes[Sound.SoundType.MUSIC.getValue()] = Float.parseFloat(in.readLine());
			volumes[Sound.SoundType.SOUND_FX.getValue()] = Float.parseFloat(in.readLine());
			volumes[Sound.SoundType.VOICE.getValue()] = Float.parseFloat(in.readLine());
			volumes[Sound.SoundType.COMBAT_FX.getValue()] = volumes[Sound.SoundType.SOUND_FX.getValue()];
			controlMode = ShipControl.values()[Integer.parseInt(in.readLine())];
			controlPosition = Integer.parseInt(in.readLine());
			controlAlpha = Float.parseFloat(in.readLine());
			introVideoQuality = Integer.parseInt(in.readLine());
			unlimitedFuel = Boolean.parseBoolean(in.readLine());
			enterInSafeZone = Boolean.parseBoolean(in.readLine());
			disableAttackers = Boolean.parseBoolean(in.readLine());
			disableTraders = Boolean.parseBoolean(in.readLine());
			invulnerable = Boolean.parseBoolean(in.readLine());
			laserDoesNotOverheat = Boolean.parseBoolean(in.readLine());
			particleDensity = Integer.parseInt(in.readLine());
			tapRadarToChangeView = Boolean.parseBoolean(in.readLine());
			laserButtonAutofire = Boolean.parseBoolean(in.readLine());
			hasBeenPlayedBefore = Boolean.parseBoolean(in.readLine());
			for (int i = 0; i < 12; i++) {
				buttonPosition[i] = Integer.parseInt(in.readLine());
			}
			fastDC = Boolean.parseBoolean(in.readLine()); // Dummy for fast DC
			engineExhaust = Boolean.parseBoolean(in.readLine());
			lockScreen = Integer.parseInt(in.readLine());
			colorScheme = Integer.parseInt(in.readLine());
			targetBox = Boolean.parseBoolean(in.readLine());
			autoId = Boolean.parseBoolean(in.readLine());
			reversePitch = Boolean.parseBoolean(in.readLine());
			flatButtonDisplay = Boolean.parseBoolean(in.readLine());
			volumes[Sound.SoundType.COMBAT_FX.getValue()] = Float.parseFloat(in.readLine());
			vibrateLevel = Float.parseFloat(in.readLine());
			dockingComputerSpeed = Integer.parseInt(in.readLine());
			difficultyLevel = Integer.parseInt(in.readLine());
			restoredCommanderCount = Integer.parseInt(in.readLine());
			navButtonsVisible = Boolean.parseBoolean(in.readLine());
		} catch (Throwable t) {
			dockingComputerSpeed = fastDC ? 2 : 0;
			// Ignore
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}
	}
	
	public static void save(FileIO files) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(files.writeFile(".alite")));
			out.write(Boolean.toString(animationsEnabled) + "\n");
			out.write(keyboardLayout + "\n");
			out.write(Boolean.toString(debugActive) + "\n");
			out.write(Boolean.toString(logToFile) + "\n");
			out.write(Boolean.toString(displayFrameRate) + "\n");
			out.write(Boolean.toString(displayDockingInformation) + "\n");
			out.write(Boolean.toString(memDebug) + "\n");
			out.write(Integer.toString(textureLevel) + "\n");
			out.write(Integer.toString(colorDepth) + "\n");
			out.write(Float.toString(alpha) + "\n");
			out.write(Float.toString(volumes[Sound.SoundType.MUSIC.getValue()]) + "\n");
			out.write(Float.toString(volumes[Sound.SoundType.SOUND_FX.getValue()]) + "\n");
			out.write(Float.toString(volumes[Sound.SoundType.VOICE.getValue()]) + "\n");
			out.write(Integer.toString(controlMode.ordinal()) + "\n");
			out.write(Integer.toString(controlPosition) + "\n");
			out.write(Float.toString(controlAlpha) + "\n");
			out.write(Integer.toString(introVideoQuality) + "\n");
			out.write(Boolean.toString(false) + "\n");
			out.write(Boolean.toString(false) + "\n");
			out.write(Boolean.toString(disableAttackers) + "\n");
			out.write(Boolean.toString(disableTraders) + "\n");
			out.write(Boolean.toString(false) + "\n");
			out.write(Boolean.toString(false) + "\n");
			out.write(Integer.toString(particleDensity) + "\n");
			out.write(Boolean.toString(tapRadarToChangeView) + "\n");
			out.write(Boolean.toString(laserButtonAutofire) + "\n");
			out.write(Boolean.toString(hasBeenPlayedBefore) + "\n");
			for (int i = 0; i < 12; i++) {
				out.write(Integer.toString(buttonPosition[i]) + "\n");
			}
			out.write(Boolean.toString(false) + "\n"); // Backward compatibility
			out.write(Boolean.toString(engineExhaust) + "\n");
			out.write(Integer.toString(lockScreen) + "\n");
			out.write(Integer.toString(colorScheme) + "\n");
			out.write(Boolean.toString(targetBox) + "\n");
			out.write(Boolean.toString(autoId) + "\n");
			out.write(Boolean.toString(reversePitch) + "\n");
			out.write(Boolean.toString(flatButtonDisplay) + "\n");
			out.write(Float.toString(volumes[Sound.SoundType.COMBAT_FX.getValue()]) + "\n");
			out.write(Float.toString(vibrateLevel)  + "\n");
			out.write(Integer.toString(dockingComputerSpeed) + "\n");
			out.write(Integer.toString(difficultyLevel) + "\n");
			out.write(Integer.toString(restoredCommanderCount) + "\n");
			out.write(Boolean.toString(navButtonsVisible) + "\n");
		} catch (Exception e) {
			// Ignore
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}		
	}	
}
