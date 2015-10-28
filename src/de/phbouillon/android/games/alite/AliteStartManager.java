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

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Messenger;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;

import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.games.alite.io.AliteDownloaderService;
import de.phbouillon.android.games.alite.io.AliteFiles;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.IMethodHook;

public class AliteStartManager extends Activity implements IDownloaderClient {
	public static final boolean HAS_EXTENSION_APK = true;
	public static final int EXTENSION_FILE_VERSION = 2180;
	private static final long EXTENSION_FILE_LENGTH = 427117633l;
	                                                  
	public static final String ALITE_STATE_FILE = "current_state.dat";
		
	private IStub downloaderClientStub;
	private IDownloaderService remoteService; 
	private String currentStatus = "Idle";
	private float avgSpeed = 0.0f;
	private int progressUpdateCalls = 0;
	private AndroidFileIO fileIO;
	private PendingIntent pendingIntent;

	private class ErrorHook implements IMethodHook {
		private static final long serialVersionUID = -3231920982342356254L;

		@Override
		public void execute(float deltaTime) {
			setContentView(R.layout.activity_start_manager);
			((TextView) findViewById(R.id.downloadTextView)).setText(
					"There was an error when trying to access Alite's resource files. " +
					"I am very sorry for the inconvenience! You can either try to restart " +
					"your device (seriously: It helped on a Nexus 4), or you can download " +
					"the all-in-one-version of Alite, where this problem cannot occur, " +
					"from http://alite.mobi");
		}	
	}
	
	private boolean expansionFilesDelivered() {
        File oldObb = new File(Environment.getExternalStorageDirectory() + "/Android/obb/de.phbouillon.android.games.alite/main.2170.de.phbouillon.android.games.alite.obb");
        if (oldObb.exists()) {
        	oldObb.delete();
        }
	    String fileName = Helpers.getExpansionAPKFileName(this, true, EXTENSION_FILE_VERSION);
	    File fileForNewFile = new File(Helpers.generateSaveFileName(this, fileName));
	    AliteLog.e("Check for OBB", "OBB exists? " + fileForNewFile.getAbsolutePath());
	    return Helpers.doesFileExist(this, fileName, EXTENSION_FILE_LENGTH, false);
	}
		
	private void setStatus(String status) {
		if (currentStatus.equals(status)) {
			return;
		}
		((TextView) findViewById(R.id.statusTextView)).setText(status);
		currentStatus = status;
	}
	
	private void checkDownload() {
		if (!expansionFilesDelivered()) {
			AliteLog.d("File does not exist!", "Expansion file does not exist. Downloading...");
			Intent notifierIntent = new Intent(this, getClass());
			notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			pendingIntent = PendingIntent.getActivity(this, 0, notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			try {
				int startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(this, pendingIntent,
								AliteDownloaderService.class);
				if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
					downloaderClientStub = DownloaderClientMarshaller.CreateStub(this, AliteDownloaderService.class);
					setContentView(R.layout.activity_start_manager);
					return;
				}
			} catch (NameNotFoundException e) {
				setStatus("Error while downloading expansion file: " + e.getMessage());
				AliteLog.e("Error while Downloading Expansions", "Name not Found: " + e.getMessage(), e);
			}
		}

		AliteFiles.performMount(this,  
				new IMethodHook() {
			private static final long serialVersionUID = -6200281383445218241L;

			@Override
			public void execute(float deltaTime) {
				startGame();
			}
		}, new ErrorHook());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (fileIO == null) {
			fileIO = new AndroidFileIO(this);
		}
		if (!AliteLog.isInitialized()) {
			AliteLog.initialize(fileIO);
		}
		final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {				
	            AliteLog.e("Uncaught Exception (AliteStartManager)", "Message: " + (paramThrowable == null ? "<null>" : paramThrowable.getMessage()), paramThrowable);
				if (oldHandler != null) {
					oldHandler.uncaughtException(paramThread, paramThrowable);
				} else {
					System.exit(2);
				}
			}
		});
		AliteLog.d("Alite Start Manager", "Alite Start Manager has been created.");
		Settings.load(fileIO);

		if (HAS_EXTENSION_APK) {
			checkDownload();
		} else {
			startGame();
		}
	}
	
//	private void checkSHA() {
//	    String fileName = Helpers.getExpansionAPKFileName(this, true, EXTENSION_FILE_VERSION);
//	    AliteLog.e("SHA-Checksum", "Start SHA-Checksum calculation");
//	    File fileForNewFile = new File(Helpers.generateSaveFileName(this, fileName));
//	    AliteLog.e("SHA-Checksum", "Checksum for obb: " + FileUtils.computeSHAString(fileForNewFile));
//	}
				
	private void startGame() {
		AliteLog.d("Alite Start Manager", "Loading Alite State");
//		loadOXPs();
		loadCurrentGame(fileIO);			
	}
	
	private void startAliteIntro() {
		AliteLog.d("Alite Start Manager", "Starting INTRO!");
		Intent intent = new Intent(this, AliteIntro.class);
		intent.putExtra(Alite.LOG_IS_INITIALIZED, true);
		startActivity(intent);	
	}
	
	private void startAlite() {
		AliteLog.d("Alite Start Manager", "Starting Alite.");
		Intent intent = new Intent(this, Alite.class);
		intent.putExtra(Alite.LOG_IS_INITIALIZED, true);
		startActivity(intent);	
	}

	private void loadCurrentGame(AndroidFileIO fileIO) {
		try {
			if (fileIO.exists(ALITE_STATE_FILE)) {
				AliteLog.d("Alite Start Manager", "Alite state file exists. Opening it.");
				// State file exists, so open the first byte to check if the
				// Intro Activity or the Game Activity must be started.
				byte [] b = fileIO.readPartialFileContents(ALITE_STATE_FILE, 1);
				if (b == null || b.length != 1) {
					// Fallback in case of an error
					AliteLog.d("Alite Start Manager", "Reading screen code failed. b == " + (b == null ? "<null>" : b) + " -- " + (b == null ? "<null>" : b.length));
					startAliteIntro();
				} else {
					if (b[0] == ScreenCodes.INTRO_SCREEN) {
						AliteLog.d("Alite Start Manager", "Saved screen code == " + ((int) b[0]) + " - loading intro.");
						startAliteIntro();
					} else {
						AliteLog.d("Alite Start Manager", "Saved screen code == " + ((int) b[0]) + " - starting game.");
						startAlite();
					}
				}
			} else {
				AliteLog.d("Alite Start Manager", "No state file present: Starting intro.");
				startAliteIntro();			
			}			
		} catch (IOException e) {
			// Default to Intro...
			AliteLog.e("Alite Start Manager", "Exception occurred. Starting intro.", e);
			startAliteIntro();
		}
	}
	
	@Override
	protected void onPause() {
		AliteLog.d("Alite Start Manager", "ASM has been paused.");
		super.onPause();		
	}
		
	@Override
	protected void onResume() {
		if (null != downloaderClientStub) {
	        downloaderClientStub.connect(this);
	    }
		super.onResume();
		AliteLog.d("Alite Start Manager", "ASM has been resumed.");
	}
	
	@Override
	protected void onStop() {
		if (null != downloaderClientStub) {
	        downloaderClientStub.disconnect(this);
	    }		
		super.onStop();
	}

	@Override
	public void onServiceConnected(Messenger m) {
		remoteService = DownloaderServiceMarshaller.CreateProxy(m);
	    remoteService.onClientUpdated(downloaderClientStub.getMessenger());
	}

	@Override
	public void onDownloadStateChanged(int newState) {
		setStatus(getString(Helpers.getDownloaderStringResourceIDFromState(newState)));
		if (newState == IDownloaderClient.STATE_COMPLETED) {
			((TextView) findViewById(R.id.downloadProgressPercentTextView)).setText("100%");
			((ProgressBar) findViewById(R.id.downloadProgressBar)).setProgress((int) EXTENSION_FILE_LENGTH);
			((TextView) findViewById(R.id.downloadTextView)).setText("Download complete.");
			AliteFiles.performMount(this, new IMethodHook() {
				private static final long serialVersionUID = -5369313962579796580L;

				@Override
				public void execute(float deltaTime) {
					startGame();
				}
			}, new ErrorHook());
		}
	}

	@Override
	public void onDownloadProgress(DownloadProgressInfo progress) {
		progressUpdateCalls++;
		avgSpeed += progress.mCurrentSpeed;
		TextView report = (TextView) findViewById(R.id.downloadTextView);
		int mbRead = (int) (progress.mOverallProgress / 1024.0f / 1024.0f);
		int mbTotal = (int) (progress.mOverallTotal / 1024.0f / 1024.0f);
		report.setText("Downloading... " + mbRead + " of " + mbTotal + "MB read. Speed: " + String.format(Locale.getDefault(), "%4.2f", (avgSpeed / (float) progressUpdateCalls / 1024.0f)) + " MB/s. Time remaining: " + (int) ((float) progress.mTimeRemaining / 1000.0f) + "s.");
		((ProgressBar) findViewById(R.id.downloadProgressBar)).setMax((int) progress.mOverallTotal);
		((ProgressBar) findViewById(R.id.downloadProgressBar)).setProgress((int) progress.mOverallProgress);
		((TextView) findViewById(R.id.downloadProgressPercentTextView)).setText((int) (((float) progress.mOverallProgress / (float) progress.mOverallTotal * 100.0f)) + "%"); 
		AliteLog.d("Progress", "Current Speed: " + progress.mCurrentSpeed + ", Overall progress: " + progress.mOverallProgress + ", Total progress: " + progress.mOverallTotal + ", Time Remaining: " + progress.mTimeRemaining);
	}	
}
