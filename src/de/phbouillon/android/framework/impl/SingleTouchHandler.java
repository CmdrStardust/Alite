package de.phbouillon.android.framework.impl;

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

import java.util.Vector;

import android.view.MotionEvent;
import android.view.View;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.Pool.PoolObjectFactory;

public class SingleTouchHandler implements TouchHandler {
	private boolean isTouched;
	private int touchX;
	private int touchY;
	private Pool <TouchEvent> touchEventPool;
	private final Vector <TouchEvent> touchEvents = new Vector<TouchEvent>();
	private final Vector <TouchEvent> retainTouchEvents = new Vector<TouchEvent>();
	private final Vector <TouchEvent> touchEventsBuffer = new Vector<TouchEvent>();
	private final float scaleX;
	private final float scaleY;
	private final int offsetX;
	private final int offsetY;
	private View currentView;
	private long freeze = -1;
	private long delay = -1;
	
	public SingleTouchHandler(View view, float scaleX, float scaleY, int offsetX, int offsetY) {
		PoolObjectFactory <TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
			private static final long serialVersionUID = 5539793923683841494L;

			@Override
			public TouchEvent createObject() {
				return new TouchEvent();
			}
		};
		touchEventPool = new Pool<TouchEvent>(factory, 100);
		view.setOnTouchListener(this);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.currentView = view;
	}
	
	void setView(View view) {
		if (currentView != view) {
			view.setOnTouchListener(this);
			currentView = view;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (freeze != -1 && (System.currentTimeMillis() - freeze) < delay) {
			return true;
		}
		freeze = -1;
		synchronized (this) {
			if (currentView != v) {
				return false;
			}
			TouchEvent touchEvent = touchEventPool.newObject();
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touchEvent.type = TouchEvent.TOUCH_DOWN;
					isTouched = true;
					break;
				case MotionEvent.ACTION_MOVE:
					touchEvent.type = TouchEvent.TOUCH_DRAGGED;
					isTouched = true;
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					touchEvent.type = TouchEvent.TOUCH_UP;
					isTouched = false;
					break;
			}
			
			touchEvent.x = touchX = (int) ((event.getX() - offsetX) * scaleX);
			touchEvent.y = touchY = (int) ((event.getY() - offsetY) * scaleY);			
			touchEventsBuffer.add(touchEvent);
			
			return true;
		}		
	}

	@Override
	public boolean isTouchDown(int pointer) {
		synchronized (this) {
			return pointer == 0 ? isTouched : false;
		}
	}

	@Override
	public int getTouchX(int pointer) {
		synchronized (this) {
			return pointer == 0 ? touchX : 0;
		}
	}

	@Override
	public int getTouchY(int pointer) {
		synchronized (this) {
			return pointer == 0 ? touchY : 0;
		}
	}

	@Override
	public Vector<TouchEvent> getTouchEvents() {
		synchronized (this) {
			int len = touchEvents.size();
			for (int i = 0; i < len; i++) {
				touchEventPool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			return touchEvents;
		}
	}

	@Override
	public Vector<TouchEvent> getAndRetainTouchEvents() {
		synchronized (this) {
			retainTouchEvents.clear();
			retainTouchEvents.addAll(touchEventsBuffer);
			return retainTouchEvents;
		}
	}

	@Override
	public int getTouchCount() {
		return isTouched ? 1 : 0;
	}

	@Override
	public void setZoomFactor(float zoomFactor) {
		// Nothing to do here...
	}		
}
