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

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import de.phbouillon.android.framework.impl.AndroidFileIO;
import de.phbouillon.android.framework.impl.VideoView;
import de.phbouillon.android.games.alite.io.ObbExpansionsManager;

public class AliteIntro extends Activity implements OnClickListener {
	private MediaPlayer mediaPlayer;
	private boolean isInPlayableState = false;
	private boolean needsToPlay = false;
	private boolean isResumed = false;
	private int stopPosition = 0;
	private boolean aliteStarted = false;
	private int cyclingThroughVideoQualities = -1;
	private AndroidFileIO fileIO;
	private VideoView videoView;
	private FileInputStream videoViewFileInputStream = null;
	private OnErrorListener errorListener;
	
	private String getAbsolutePath(String file) {		
		try {
			for (int i = 0; i < 10; i++) {
				if (ObbExpansionsManager.getInstance() == null) {
					break;
				}		
				String path = ObbExpansionsManager.getInstance().getMainRoot();
				if (path == null) {
					AliteLog.e("AliteIntro playback", "OBB not yet mounted. Trying again in 200ms");
					Thread.sleep(200);
					continue;
				}
				AliteLog.d("AliteIntro playback", "Getting path for file: " + path + file);
				return path + file;
			}
		} catch (InterruptedException e) {			
		}
		throw new RuntimeException("Mount OBB Error");
	}
	
	private FileDescriptor getFileDescriptor(String file) throws IOException {
		AliteLog.d("AliteIntro playback", "Getting path for file: " + file);
		FileDescriptor fd = fileIO.getFileDescriptor(file).getFileDescriptor();
		AliteLog.d("FileDescriptor", "FD == " + fd + ", " + fd.valid());
		return fd;
	}
	
	private int determineIntroId(int quality) {
//		switch (quality) {
//			case 0: AliteLog.d("Video Playback", "Using video resolution 1920x1080");
//			        return R.raw.alite_intro_b1920;
//			case 1: AliteLog.d("Video Playback", "Using video resolution 1280x720");
//				    return R.raw.alite_intro_b1280;
//			case 2:	AliteLog.d("Video Playback", "Using video resolution 640x360");
//					return R.raw.alite_intro_b640;					
//			case 3: AliteLog.d("Video Playback", "Using video resolution 320x180");
//					return R.raw.alite_intro_b320;
//			case 4: AliteLog.d("Video Playback", "Failsafe mode 1: 288");
//					return R.raw.alite_intro_288;
//			case 5: AliteLog.d("Video Playback", "Failsafe mode 2: 240");
//					return R.raw.alite_intro_b240;
//			default: AliteLog.d("Video Playback", "No mode found. Giving up :(.");
//					 return -1;
//		}
		return -1;
	}

	private String determineIntroFilename(int quality) {
		switch (quality) {
			case 0: AliteLog.d("Video Playback", "Using video resolution 1920x1080");
			        return "intro/alite_intro_b1920.mp4";
			case 1: AliteLog.d("Video Playback", "Using video resolution 1280x720");
				    return "intro/alite_intro_b1280.mp4";
			case 2:	AliteLog.d("Video Playback", "Using video resolution 640x360");
					return "intro/alite_intro_b640.mp4";					
			case 3: AliteLog.d("Video Playback", "Using video resolution 320x180");
					return "intro/alite_intro_b320.mp4";
			case 4: AliteLog.d("Video Playback", "Failsafe mode 1: 288");
					return "intro/alite_intro_288.3gp";
			case 5: AliteLog.d("Video Playback", "Failsafe mode 2: 240");
					return "intro/alite_intro_b240.mp4";
			default: AliteLog.d("Video Playback", "No mode found. Giving up :(.");
					 return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		Intent intent = getIntent();
		fileIO = new AndroidFileIO(this);
		if (intent == null || !intent.getBooleanExtra(Alite.LOG_IS_INITIALIZED, false)) {
			AliteLog.initialize(fileIO);			
		}
		final Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
	            AliteLog.e("Uncaught Exception (AliteIntro)", "Message: " + (paramThrowable == null ? "<null>" : paramThrowable.getMessage()), paramThrowable);
				if (oldHandler != null) {
					oldHandler.uncaughtException(paramThread, paramThrowable);
				} else {
					System.exit(2);
				}
			}
		});
		Settings.load(fileIO);
		switch (Settings.lockScreen) {
			case 0: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); break;
			case 1: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); break;
			case 2: setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); break;
		}				
		
		AliteLog.d("IntroVideoQuality", "IntroVideoQuality == " + Settings.introVideoQuality);
		if (Settings.introVideoQuality < 0) {
			// Device is not capable of playing intro video :(. Skip to game.
			startAlite(null);
			return;
		}
		if (savedInstanceState != null) {
			stopPosition = savedInstanceState.getInt("position");
		}
		setContentView(R.layout.activity_play_intro);
		
		if (videoView == null) {
			initializeVideoView();
		}
		if (AliteStartManager.HAS_EXTENSION_APK) {
			playVideoFromOBB();
		} else {
			playVideoFromRawFolder();
		}
	}
	
	private void playVideoFromRawFolder() {
		int introId = 0;	
		AliteLog.d("IntroVideoQuality", "IntroVideoQuality = " + Settings.introVideoQuality);
		if (Settings.introVideoQuality == 255) {
			cyclingThroughVideoQualities = 0;
			AliteLog.d("Video Playback", "Using video resolution 1920x1080");
			introId = -1; //R.raw.alite_intro_b1920;
		} else {
			introId = determineIntroId(Settings.introVideoQuality);
		}
		AliteLog.d("cyclingThroughVideoQualities", "cyclingThroughVideoQualities = " + cyclingThroughVideoQualities);
        videoView.setVideoURI(Uri.parse("android.resource://de.phbouillon.android.games.alite/" + introId));
        videoView.setMediaController(null);
        videoView.requestFocus();
	}
	
	private void playVideoFromOBB() {
		String introFilename = "intro/alite_intro_b1920.mp4";
		AliteLog.d("IntroVideoQuality", "IntroVideoQuality = " + Settings.introVideoQuality);
		if (Settings.introVideoQuality == 255) {
			cyclingThroughVideoQualities = 0;
			AliteLog.d("Video Playback", "Using video resolution 1920x1080");
		} else {
			introFilename = determineIntroFilename(Settings.introVideoQuality);
		}
		AliteLog.d("cyclingThroughVideoQualities", "cyclingThroughVideoQualities = " + cyclingThroughVideoQualities);
		if (introFilename == null) {
			startAlite(videoView);
			return;
		}
		final String iFilename = introFilename;
		try {
			if (videoViewFileInputStream != null) {
				try {
					videoViewFileInputStream.close();
					videoViewFileInputStream = null;
				} catch (IOException e) {
					// ignore;
				}
			}
			videoViewFileInputStream = new FileInputStream(getAbsolutePath(iFilename));
			AliteLog.d("Intro path", "Intro path: " + getAbsolutePath(iFilename));
			videoView.setVideoFD(videoViewFileInputStream.getFD());
		} catch (IOException e) {
			AliteLog.e("VideoViewException", e.getMessage(), e);
			errorListener.onError(null, 0, 0);
		}
		videoView.setMediaController(null);
		videoView.requestFocus();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initializeVideoView() {
		videoView = new VideoView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		videoView.setLayoutParams(layoutParams);
		videoView.setClickable(true);		
		videoView.setOnClickListener(this);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.introContainer);
		layout.addView(videoView);
		videoView.getRootView().setBackgroundColor(getResources().getColor(android.R.color.black));
		videoView.setVisibility(View.VISIBLE);
    
        videoView.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {				
				if (v instanceof VideoView) {
					startAlite((VideoView) v);
					return true;
				}				
				return v.performClick();
			}
		});
		final OnCompletionListener onCompletionListener = new OnCompletionListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void onCompletion(final MediaPlayer mp) {
				startAlite(videoView);
			}
		};
		videoView.setOnCompletionListener(onCompletionListener);
		
		AliteLog.e("Creating Error Listener", "EL created");
		errorListener = new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				AliteLog.d("cyclingThroughVideoQualities [1]", "cyclingThroughVideoQualities = " + cyclingThroughVideoQualities);
				if (AliteStartManager.HAS_EXTENSION_APK) {
					if (errorHandlerObb(mp)) {
						return true;
					}
				} else {
					if (errorHandlerRawFolder(mp)) {
						return true;
					}
				}
				String cause = "Undocumented cause: " + what;				
				switch (what) {
					case MediaPlayer.MEDIA_ERROR_UNKNOWN: cause = "Unknown cause."; break;
					case MediaPlayer.MEDIA_ERROR_SERVER_DIED: cause = "Server died."; break;
					case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK: cause = "Not valid for progressive playback."; break;
				}
				
				String details = "Undocumented error details: " + extra;
				switch (extra) {
					case MediaPlayer.MEDIA_ERROR_IO: details = "Media Error IO."; break;
					case MediaPlayer.MEDIA_ERROR_MALFORMED: details = "Media Error Malformed."; break;
					case MediaPlayer.MEDIA_ERROR_UNSUPPORTED: details = "Media Error Unsupported."; break;
					case MediaPlayer.MEDIA_ERROR_TIMED_OUT: details = "Media Error Timed Out."; break;
					case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK: details = "Not valid for progressive playback."; break;
				}
				AliteLog.d("Intro Playback Error", "Couldn't playback intro. " + cause + " " + details);
				onCompletionListener.onCompletion(mp);
				return true;
			}

			private boolean errorHandlerRawFolder(MediaPlayer mp) {
				if (cyclingThroughVideoQualities != -1) {
					cyclingThroughVideoQualities++;
					int introId = determineIntroId(cyclingThroughVideoQualities);
					if (introId == -1) {
						cyclingThroughVideoQualities = -1;
						Settings.introVideoQuality = -1;
						Settings.save(fileIO);
						startAlite(videoView);
					} else {
						if (mp != null) {
							mp.reset();
						}
						needsToPlay = true;
				        videoView.setVideoURI(Uri.parse("android.resource://de.phbouillon.android.games.alite/" + introId));
				        return true;
					}
				}
				return false;
			}

			private boolean errorHandlerObb(final MediaPlayer mp) {
				if (videoViewFileInputStream != null) {
					try {
						videoViewFileInputStream.close();
						videoViewFileInputStream = null;
					} catch (IOException e) {						
					}
				}
				if (cyclingThroughVideoQualities != -1) {
					cyclingThroughVideoQualities++;
					String introFilename = determineIntroFilename(cyclingThroughVideoQualities);
					if (introFilename == null) {
						cyclingThroughVideoQualities = -1;
						Settings.introVideoQuality = -1;
						Settings.save(fileIO);
						startAlite(videoView);
					} else {
						if (mp != null) {
							mp.reset();
						}
						needsToPlay = true;
						try {
							if (videoViewFileInputStream != null) {
								try {
									videoViewFileInputStream.close();
									videoViewFileInputStream = null;
								} catch (IOException e) {
									// Ignore
								}
							}
							if (AliteStartManager.HAS_EXTENSION_APK) {
								videoViewFileInputStream = new FileInputStream(getAbsolutePath(introFilename));
								AliteLog.d("Intro path", "Intro path: " + getAbsolutePath(introFilename));
								videoView.setVideoFD(videoViewFileInputStream.getFD());
							} else {
								FileDescriptor fd = getFileDescriptor(introFilename);
								videoViewFileInputStream = new FileInputStream(fd);
								videoView.setVideoFD(fd);
							}
						} catch (IOException e) {
							AliteLog.e("VideoViewException", e.getMessage(), e);
							errorListener.onError(null, 0, 0);
						}
				        return true;
					}
				}
				return false;
			}
		};
		
		AliteLog.e("Creating Error Listener", "Video View, EL set");
		videoView.setOnErrorListener(errorListener);  		
		
		videoView.setOnPreparedListener(new OnPreparedListener() {			
			@Override
			public void onPrepared(MediaPlayer mp) {
				AliteLog.d("VideoView", "VideoView is prepared. Playing video.");
				mediaPlayer = mp;
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						isInPlayableState = true;
						if (needsToPlay && isResumed) {
							videoView.seekTo(stopPosition);
							videoView.start();
							needsToPlay = false;
						}
						return null;
					}
				}.execute(null, null, null);		
			}
		});		
	}
	
	@Override
	protected void onPause() {
		if (videoView == null) {
			AliteLog.e("Alite Intro", "Video view is not found. [onPause]");
			super.onPause();
			isResumed = false;
			return;
		}		
		stopPosition = videoView.getCurrentPosition();
		videoView.pause();
		if (videoViewFileInputStream != null) {
			try {
				videoViewFileInputStream.close();
				videoViewFileInputStream = null;
			} catch (IOException e) {
				// ignore;
			}
		}
		super.onPause();
		isResumed = false;		
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("position", stopPosition);
	}
	
	public void onClick(View v) {
		if (v instanceof VideoView) {
			VideoView videoView = (VideoView) v;
			if (videoView != null) {
				try {
					videoView.stopPlayback();
				} catch (IllegalStateException e) {
					// Ignore...
				}
			}
			startAlite(videoView);
		}		
	}
	
	private synchronized void startAlite(VideoView videoView) {
		AliteLog.d("startAlite call", "startAlite begin");
		if (!aliteStarted) {			
			aliteStarted = true;
			AliteLog.d("startAlite", "startAlite flag changed");
			try {
				boolean result = fileIO.deleteFile(AliteStartManager.ALITE_STATE_FILE);
				AliteLog.d("Deleting state file", "Delete result: " + result); 
			} catch (IOException e) {
				// Ignore any error. Just log it for reference.
				AliteLog.e("Error while deleting state file.", e.getMessage(), e);
			}
			AliteLog.d("startAlite", "updating settings");
			if (cyclingThroughVideoQualities != -1) {
				Settings.introVideoQuality = cyclingThroughVideoQualities;
				Settings.save(new AndroidFileIO(this));
			}
			
			AliteLog.d("startAlite", "Killing video view");
			if (videoView != null) {
				try {
					videoView.stopPlayback();
					videoView.pause();
					videoView.clearAnimation();
					videoView.clearFocus();
				} catch (Exception e) {
					AliteLog.e("Exception", "Exception", e);
					// Ignore...
				}
				if (videoViewFileInputStream != null) {
					try {
						videoViewFileInputStream.close();						
					} catch (IOException e) {				
						AliteLog.e("Exception", "Exception", e);
					}
					videoViewFileInputStream = null;
				}
			}
			if (mediaPlayer != null) {
				try {
					mediaPlayer.release();					
				} catch (Exception e) {
					AliteLog.e("Exception", "Exception", e);
					// Ignore...
				}
				mediaPlayer = null;
			}
			AliteLog.d("startAlite", "Calling Alite start intent");
			Intent intent = new Intent(this, Alite.class);
			intent.putExtra(Alite.LOG_IS_INITIALIZED, true);
			AliteLog.d("startAlite", "Calling startActivity");
			startActivity(intent);
			AliteLog.d("startAlite", "Done");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isResumed = true;
		if (videoView == null) {
			AliteLog.e("Alite Intro", "Video view is not found. [onResume]");
			isInPlayableState = false;			
		}
		if (isInPlayableState) {
			videoView.seekTo(stopPosition);	
			videoView.start();					
		} else {
			needsToPlay = true;
		}
	}
}
