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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.opengl.GLES11;
import android.os.Build;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;
import de.phbouillon.android.framework.FileIO;
import de.phbouillon.android.framework.impl.AndroidGame;

public class AliteLog {
	private static final long GB = 1024 * 1024 * 1024;
	private static final long MB = 1024 * 1024;
	private static final long KB = 1024;
	
	private static FileIO fileIO;	
	private static String logFilename;
	private static boolean first = true;
	private static long started;
	
	public static void d(String title, String message) {
		if (!Settings.suppressOnlineLog) {
			Log.d(title, message);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.d("Mem Dump", getMemoryData());
		}
		internalWrite("[Debug]", title, message, null);
	}
	
	public static void d(String title, String message, Throwable cause) {
		if (!Settings.suppressOnlineLog) {
			Log.d(title, message, cause);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.d("Mem Dump", getMemoryData());
		}
		internalWrite("[Debug]", title, message, cause);
	}

	public static void w(String title, String message) {
		if (!Settings.suppressOnlineLog) {
			Log.w(title, message);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.w("Mem Dump", getMemoryData());
		}
		internalWrite("[Warning]", title, message, null);
	}
	
	public static void w(String title, String message, Throwable cause) {
		if (!Settings.suppressOnlineLog) {
			Log.w(title, message, cause);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.w("Mem Dump", getMemoryData());
		}
		internalWrite("[Warning]", title, message, cause);
	}

	public static void e(String title, String message) {
		if (!Settings.suppressOnlineLog) {
			Log.e(title, message);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.d("Mem Dump", getMemoryData());
		}
		internalWrite("[Error]", title, message, null);
	}

	public static void e(String title, String message, Throwable cause) {
		if (!Settings.suppressOnlineLog) {
			Log.e(title, message, cause);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.d("Mem Dump", getMemoryData());
		}
		internalWrite("[Error]", title, message, cause);
	}
	
	private static String toReadableMemString(long memory) {
		if (memory > GB) {
			return String.format(Locale.getDefault(), "%3.2f GB", ((float) memory / (float) GB)); 
		} else if (memory > MB) {
			return String.format(Locale.getDefault(), "%5.2f MB", ((float) memory / (float) MB));
		} else if (memory > KB) {
			return String.format(Locale.getDefault(), "%5.2f KB", ((float) memory / (float) KB));
		}
		return String.format(Locale.getDefault(), "%d Bytes", memory);		
	}
	
	private static String dumpTrace() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		new Throwable("stack dump").printStackTrace(writer);
		writer.close();
		try {
			stringWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringWriter.toString();
	}

	public static void dumpStack(String title, String message) {
		String stackTrace = dumpTrace();
		if (!Settings.suppressOnlineLog) {
			Log.e(title, message + " - " + stackTrace);
		}
		if (Settings.memDebug && Settings.onlineMemDebug) {
			Log.d("Mem Dump", getMemoryData());
		}
		internalWrite("[Debug]", title, message + " - " + stackTrace, null);		
	}
	
	public static String getMemoryData() {
		return "FRM: " + toReadableMemString(Runtime.getRuntime().freeMemory()) +
			   ", MRM: " + toReadableMemString(Runtime.getRuntime().maxMemory()) +
			   ", TRM: " + toReadableMemString(Runtime.getRuntime().totalMemory()) + 
			   ", FNM: " + toReadableMemString(Debug.getNativeHeapFreeSize()) +
			   ", ANM: " + toReadableMemString(Debug.getNativeHeapAllocatedSize()) + 
			   ", TNM: " + toReadableMemString(Debug.getNativeHeapSize()) + "\n";
	}
	
	public static String getDeviceInfo() {
		return "Android version: " + Build.VERSION.RELEASE + "\n" +
               "Device: " + Build.DEVICE + "\n" +
	           "Product: " + Build.PRODUCT + "\n" +
               "Brand: " + Build.BRAND + "\n" +
	           "Display: " + Build.DISPLAY + "\n" +
               "Manufacturer: " + Build.MANUFACTURER + "\n" +
	           "Model: " + Build.MODEL + "\n";

	}
	
	private static void outputDeviceInfo(OutputStream logFile) throws IOException {				
		logFile.write(getDeviceInfo().getBytes());		
	}
	
	private static void internalWrite(String tag, String title, String message, Throwable cause) {
		if (!Settings.logToFile || fileIO == null) {
			return;
		}
		try {
			if (!fileIO.exists("logs")) {
				fileIO.mkDir("logs");
			}
			OutputStream logFile = fileIO.appendFile(logFilename);
			String errorText = null;
			if (cause != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				cause.printStackTrace(pw);
				pw.close();
				errorText = cause.getMessage() + "\n" + sw.toString();
			}
			if (first) {
				first = false;
				started = System.currentTimeMillis();
				logFile.write(("Info - Alite Started - Alite version " + Alite.VERSION_STRING + " started on " + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "\n").getBytes());
				outputDeviceInfo(logFile);
			}
			String result = "[" + (System.currentTimeMillis() - started) + "ms] - " + tag + " - " + title + " - " + message + (errorText == null ? "" : " - " + errorText) + "\n";
//			result += getMemoryData();
			logFile.write(result.getBytes());
			logFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static String getGlDetails() {
		String vendor = GLES11.glGetString(GLES11.GL_VENDOR);
		String renderer = GLES11.glGetString(GLES11.GL_RENDERER);
		String version = GLES11.glGetString(GLES11.GL_VERSION);
		String extensions = GLES11.glGetString(GLES11.GL_EXTENSIONS);
		StringBuffer result = new StringBuffer();
		result.append("OpenGL Vendor:   " + vendor + "\n");
		result.append("OpenGL Renderer: " + renderer + "\n");
		result.append("OpenGL Version:  " + version + "\n");
		if (extensions != null) {
			result.append("Extensions:\n");
			for (String e: extensions.split(" ")) {
				result.append("  " + e + "\n");
			}
		} else {
			result.append("No Extensions.\n");
		}
		return result.toString();
	}
	
	public static void debugGlVendorData() {
		String vendor = GLES11.glGetString(GLES11.GL_VENDOR);
		String renderer = GLES11.glGetString(GLES11.GL_RENDERER);
		String version = GLES11.glGetString(GLES11.GL_VERSION);
		String extensions = GLES11.glGetString(GLES11.GL_EXTENSIONS);
		d("GL Vendor Data", "Vendor:   " + vendor);
		d("GL Vendor Data", "Renderer: " + renderer);
		d("GL Vendor Data", "Version:  " + version);
		if (extensions != null) {
			for (String e: extensions.split(" ")) {
				d("GL Vendor Data", "Extension: " + e);
			}
		}
	}

	public static final void initialize(FileIO fileIO) {
		logFilename = "logs/AliteLog-" + (new SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault()).format(new Date())) + ".txt";
		AliteLog.fileIO = fileIO;
	}	
	
	public static boolean isInitialized() {
		return AliteLog.fileIO != null;
	}
	
	public static String getErrorReportText(String errorText) {
		return "The following crash occurred in Alite (" + Alite.VERSION_STRING + "):\n\n" + errorText + 
			   "\n\nDevice details:\n" + getDeviceInfo() +
			   "\n\nGL Details:\n" + getGlDetails() +
			   "\n\nMemory data:\n" + getMemoryData() +
			   "\n\nCurrent date/time: " + (new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault()).format(new Date()));
	}
	
	public static void sendMail(AndroidGame game, String errorText) {
		try {
			String mailAddress = "Philipp.Bouillon@gmail.com";
			String subject = "Alite Crash Report.";
			String message = getErrorReportText(errorText);
			
			final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { mailAddress });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			game.startActivityForResult(Intent.createChooser(emailIntent, "Sending email..."), 0);
		} catch (Throwable t) {
			Toast.makeText(game, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
