package de.phbouillon.android.games.alite;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

public class AndroidUtil {
	private AndroidUtil() {		
	}
	
	@SuppressLint("InlinedApi")
	public static void setImmersion(View view) {
		if (view == null || Settings.navButtonsVisible) {
			return;
		}
		int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		} 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
					View.SYSTEM_UI_FLAG_FULLSCREEN;					
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;	
		}
		if (flags != 0) {
			view.setSystemUiVisibility(flags);
		}
	}
	
	@SuppressLint("NewApi")
	public static void getDisplaySize(Point result, Display display) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && !Settings.navButtonsVisible) {
			DisplayMetrics metrics = new DisplayMetrics();
			display.getRealMetrics(metrics);
			result.x = metrics.widthPixels;
			result.y = metrics.heightPixels;
		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			result.x = metrics.widthPixels;
			result.y = metrics.heightPixels;
		}
	}
}
