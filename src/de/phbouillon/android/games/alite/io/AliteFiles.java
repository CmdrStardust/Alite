package de.phbouillon.android.games.alite.io;

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

import android.content.Context;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.screens.canvas.tutorial.IMethodHook;

public class AliteFiles {
	public static void performMount(final Context context, final IMethodHook methodHook, final IMethodHook errorHook) {
		AliteLog.d("Performing Mount", "Mounting obb");
		ObbExpansionsManager.createNewInstance(context, new ObbExpansionsManager.ObbListener() {
			@Override
			public void onObbStateChange(String path, int state) {
				super.onObbStateChange(path, state);
				if (state != MOUNTED) {
					errorHook.execute(0);
				}
			}
			
			@Override
			public void onMountSuccess() {
				String mountedPath = ObbExpansionsManager.getInstance().getMainRoot();
				AliteLog.d("OBB Path set", "OBB Path = " + mountedPath);
				methodHook.execute(0);
			}
			
			@Override
			public void onFilesNotFound() {
				AliteLog.e("OBB Mounting error", "An error occurred when trying to mount the OBB");
				errorHook.execute(0);
			}
		});
	}
}
