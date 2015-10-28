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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.games.alite.screens.canvas.AliteScreen;

public class ButtonRegistry {
	private static final ButtonRegistry instance = new ButtonRegistry();
	
	private final Map <AliteScreen, Set<Button>> buttons = new HashMap<AliteScreen, Set<Button>>();
	private final Set <Button> messageButtons = new HashSet<Button>();
	
	private ButtonRegistry() {		
	}
	
	public static final ButtonRegistry get() {
		return instance;
	}
	
	public void addMessageButton(Button button) {
		messageButtons.add(button);
	}
	
	public void clearMessageButtons() {
		messageButtons.clear();
	}
	
	public void addButton(AliteScreen definingScreen, Button button) {
		Set <Button> definedButtons = buttons.get(definingScreen);
		if (definedButtons == null) {
			definedButtons = new HashSet<Button>();
		}
		definedButtons.add(button);
		buttons.put(definingScreen, definedButtons);
	}
	
	public void removeButtons(AliteScreen definingScreen) {
		if (buttons.containsKey(definingScreen)) {
			buttons.remove(definingScreen);
		}
	}
	
	public void processTouch(TouchEvent touch) {
		Set <Button> set = messageButtons.isEmpty() ? buttons.get(Alite.getDefiningScreen()) : messageButtons;
		if (set == null) {
			return;
		}
		if (touch.type == TouchEvent.TOUCH_DOWN) {
			for (Button b: set) {
				if (b.isTouched(touch.x, touch.y)) {
					b.fingerDown(touch.pointer);
				}
			}			
		} else if (touch.type == TouchEvent.TOUCH_DRAGGED) {
			for (Button b : set) {
				if (b.isTouched(touch.x, touch.y)) {
					b.fingerDown(touch.pointer);
				} else {
					b.fingerUp(touch.pointer);
				}
			}
		} else if (touch.type == TouchEvent.TOUCH_UP) {
			for (Button b : set) {
				if (b.isTouched(touch.x, touch.y)) {
					b.fingerUp(touch.pointer);
				}
			}
		}
	}
}
