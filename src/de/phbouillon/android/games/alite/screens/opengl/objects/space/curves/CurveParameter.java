package de.phbouillon.android.games.alite.screens.opengl.objects.space.curves;

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

import java.io.Serializable;

class CurveParameter implements Serializable {
	private static final long serialVersionUID = 7165604208567329342L;

	private float [] h = new float[4];
	private CurveParameterKey [] keys;
	protected boolean inConst;
	protected boolean outConst;
	protected boolean end = false;
	
	CurveParameter(CurveParameterKey ...keys) {
		int n = keys.length;
		this.keys = new CurveParameterKey[n];		
		for (int i = 0; i < n; i++) {
			this.keys[i] = keys[i];
			if (i > 0) {
				this.keys[i].prev = this.keys[i - 1];
			}
			if (i < (n - 1)) {
				this.keys[i].next = keys[i + 1];
			}
			this.keys[i].index = i;
			inConst = false;
			outConst = false;
		}
	}
	
	private float incoming(CurveParameterKey key0, CurveParameterKey key1) {
        float d = key1.value - key0.value;

        if (key1.next != null) {
        	float t = (key1.time - key0.time) / (key1.next.time - key0.time);
        	return t * (key1.next.value - key1.value + d);
        }
        return d;
	}
	
	private float outgoing(CurveParameterKey key0, CurveParameterKey key1) {
		float d = key1.value - key0.value;

		if (key0.prev != null) {
			float t = (key1.time - key0.time) / (key1.time - key0.prev.time);
		    return t * (key0.value - key0.prev.value + d);
		}
		return d;	
	}
	
	private void hermite(float t) {
		float t2 = t * t;
		float t3 = t2 * t;
		
		h[1] = 3.0f  * t2 - t3 - t3;
		h[0] = 1.0f - h[1];
		h[3] = t3 - t2;
		h[2] = h[3] - t2 + t;
	}
	
	float getValue(float time) {		
		if (keys == null || keys.length == 0) {
			return 0.0f;
		}
		int n = keys.length;
		if (n == 1) {
			return keys[0].value;
		}
		
		if (time < keys[0].time) {
			if (inConst) {
				return keys[0].value;
			}
			float out = outgoing(keys[0], keys[0].next) / (keys[0].next.time - keys[0].time);
			return out * (time - keys[0].time) + keys[0].value;
		}
		
		if (time > keys[n - 1].time) {
			end = true;
			if (outConst) {
				return keys[n - 1].value;
			}
			float in = incoming(keys[n - 1].prev, keys[n - 1]) / (keys[n - 1].time - keys[n - 1].prev.time);
			return in * (time - keys[n - 1].time) + keys[n - 1].value;
		}

		CurveParameterKey key0 = keys[0];
		while (time > key0.next.time) {
			key0 = key0.next;
		}
		CurveParameterKey key1 = key0.next;
		
		if (time == key0.time) {
			return key0.value;
		}
		if (time == key1.time) {
			return key1.value;
		}
		float t = (time - key0.time) / (key1.time - key0.time);
		
		float out = outgoing(key0, key1);
		float in  = incoming(key0, key1);
		hermite(t);
		return h[0] * key0.value + h[1] * key1.value + h[2] * out + h[3] * in;
	}	
	
	public boolean reachedEnd() {
		return end;
	}
	
	void resetEnd() {
		end = false;
	}
}
