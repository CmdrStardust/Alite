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

import android.annotation.TargetApi;
import android.os.Build;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import de.phbouillon.android.framework.Input.TouchEvent;
import de.phbouillon.android.framework.impl.Pool.PoolObjectFactory;

@TargetApi(Build.VERSION_CODES.FROYO)
public class MultiTouchHandler implements TouchHandler {
	private static final int MAX_TOUCHPOINTS = 10;
	
	private final boolean [] isTouched = new boolean[MAX_TOUCHPOINTS];
	private final int [] touchX = new int[MAX_TOUCHPOINTS];
	private final int [] touchY = new int[MAX_TOUCHPOINTS];
	private final int [] id = new int[MAX_TOUCHPOINTS];
	private final Pool <TouchEvent> touchEventPool;
	private final Vector <TouchEvent> touchEvents = new Vector<TouchEvent>();
	private final Vector <TouchEvent> retainTouchEvents = new Vector<TouchEvent>();
	private final Vector <TouchEvent> touchEventsBuffer = new Vector<TouchEvent>();
	private final float scaleX;
	private final float scaleY;
	private final int offsetX;
	private final int offsetY;
	private float zoomFactor = 1.0f;
	private final ScaleGestureDetector scaleDetector;
	private final GestureDetector sweepDetector;
	private View currentView;
	private long freeze = -1;
	private long delay = -1;
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        zoomFactor *= detector.getScaleFactor();

	        // Don't let the object get too small or too large.
	        zoomFactor = Math.max(1.0f, Math.min(zoomFactor, 8.0f));
	        
			TouchEvent touchEvent = touchEventPool.newObject();
			touchEvent.type = TouchEvent.TOUCH_SCALE;
			touchEvent.x2 = (int) ((detector.getFocusX() - offsetX) * scaleX);
			touchEvent.y2 = (int) ((detector.getFocusY() - offsetY) * scaleY);
			touchEvent.zoomFactor = zoomFactor;
			touchEventsBuffer.add(touchEvent);

	        return true;
	    }
	}
	
	private class SweepListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity, float yVelocity) {
			TouchEvent touchEvent = touchEventPool.newObject();
			touchEvent.type = TouchEvent.TOUCH_SWEEP;
			if (start == null) {
				return true;
			}
			touchEvent.x = (int) ((start.getRawX() - offsetX) * scaleX);
			touchEvent.y = (int) ((start.getRawY() - offsetY) * scaleY);
			touchEvent.x2 = (int) (xVelocity / (100.0f / scaleX));
			touchEvent.y2 = (int) (yVelocity / (100.0f / scaleY));
			
			touchEventsBuffer.add(touchEvent);
			
			return true;
		}
	}

	public MultiTouchHandler(View view, float scaleX, float scaleY, int offsetX, int offsetY) {
		PoolObjectFactory <TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
			private static final long serialVersionUID = 8367135535412320757L;

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
		scaleDetector = new ScaleGestureDetector(view.getContext(), new ScaleListener());
		sweepDetector = new GestureDetector(view.getContext(), new SweepListener());
		
		currentView = view;
	}
	
	void setView(View view) {
		if (view != currentView) {
			currentView = view;
			view.setOnTouchListener(this);
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
			scaleDetector.onTouchEvent(event);
			sweepDetector.onTouchEvent(event);
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int pointerCount = event.getPointerCount();
			TouchEvent touchEvent;
			
			for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
				if (i >= pointerCount) {
					isTouched[i] = false;
					id[i] = -1;
					continue;
				}
				int pointerId = event.getPointerId(i);
				if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
					continue;
				}
				switch (action) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DOWN;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int) ((event.getX(i) - offsetX) * scaleX);
						touchEvent.y = touchY[i] = (int) ((event.getY(i) - offsetY) * scaleY);
						isTouched[i] = true;
						id[i] = pointerId;
						touchEventsBuffer.add(touchEvent);
						break;
						
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_CANCEL:
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_UP;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int) ((event.getX(i) - offsetX) * scaleX);
						touchEvent.y = touchY[i] = (int) ((event.getY(i) - offsetY) * scaleY);
						isTouched[i] = false;
						id[i] = -1;
						touchEventsBuffer.add(touchEvent);
						break;
						
					case MotionEvent.ACTION_MOVE:
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DRAGGED;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int) ((event.getX(i) - offsetX) * scaleX);
						touchEvent.y = touchY[i] = (int) ((event.getY(i) - offsetY) * scaleY);
						isTouched[i] = true;
						id[i] = pointerId;
						touchEventsBuffer.add(touchEvent);
						break;								
				}
			}
			return true;
		}		
	}

	@Override
	public boolean isTouchDown(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			return index >= 0 && index < MAX_TOUCHPOINTS ? isTouched[index] : false;
		}
	}

	@Override
	public int getTouchX(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			return index >= 0 && index < MAX_TOUCHPOINTS ? touchX[index] : 0;
		}
	}

	@Override
	public int getTouchY(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			return index >= 0 && index < MAX_TOUCHPOINTS ? touchY[index] : 0;
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

	private int getIndex(int pointerId) {
		for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
			if (id[i] == pointerId) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getTouchCount() {
		int result = 0;
		for (boolean b: isTouched) {
			if (b) {
				result++;
			}
		}
		return result;
	}

	@Override
	public void setZoomFactor(float zoomFactor) {
		this.zoomFactor = zoomFactor;
	}
}
