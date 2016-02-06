package de.phbouillon.android.games.alite.screens.opengl.objects.space;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.opengl.Matrix;
import de.phbouillon.android.framework.impl.gl.GraphicObject;
import de.phbouillon.android.framework.math.Quaternion;
import de.phbouillon.android.framework.math.Vector3f;
import de.phbouillon.android.games.alite.Alite;
import de.phbouillon.android.games.alite.AliteLog;
import de.phbouillon.android.games.alite.Settings;
import de.phbouillon.android.games.alite.model.statistics.ShipType;
import de.phbouillon.android.games.alite.screens.opengl.ingame.InGameManager;
import de.phbouillon.android.games.alite.screens.opengl.ingame.ObjectType;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.curves.BreakDown;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.curves.BreakUp;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.curves.Curve;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.CobraMkIII;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Missile;
import de.phbouillon.android.games.alite.screens.opengl.objects.space.ships.Viper;

public final class SpaceObjectAI implements Serializable {
	private static final long serialVersionUID = 8646121427456794783L;

	private static final float MISSILE_MIN_DIST_SQ                      = 36000000.0f;
	public  static final float SHOOT_DISTANCE_SQ                        = 81000000.0f;
	private static final float FIRE_MISSILE_UPON_FIRST_HIT_PROBABILITY  = 5.0f;
	private static final long  BASE_DELAY_BETWEEN_SHOOT_CHECKS          = 59880239l; // 16.7 FPS
	private static final long  SHOOT_DELAY_REDUCE_PER_RATING_LEVEL      =  3318363l; // 1.6625 Delta FPS
	                                                                                 // => ~30 FPS at Elite.
	private final SpaceObject so;

	private final Quaternion q1 = new Quaternion();
	private final Quaternion q2 = new Quaternion();
	private final Quaternion q3 = new Quaternion();
	private final Vector3f   v0 = new Vector3f(0, 0, 0);
	private final Vector3f   v1 = new Vector3f(0, 0, 0);
	private final Vector3f   v2 = new Vector3f(0, 0, 0);
	private final Vector3f   v3 = new Vector3f(0, 0, 0);

	private Stack <AIState> currentState = new Stack<AIState>();
	private GraphicObject target = null;
	private Vector3f evadePosition = new Vector3f(0, 0, 0);
	private float evadeRangeSq = 0;
	private final List <WayPoint> waypoints = new ArrayList<WayPoint>();
	private float currentDistance = -1;
	private boolean pitchingOver = false;
	private float flightRoll = 0.0f;
	private float flightPitch = 0.0f;
	private boolean waitForSafeZoneExit = false;
	private long lastShootCheck = -1;
	private Curve curve = null;
	private long curveFollowStart;
	private final Vector3f lastRotation = new Vector3f(0, 0, 0);

	public SpaceObjectAI(final SpaceObject so) {
		this.so = so;
		currentState.push(AIState.IDLE);
	}

	public float orientUsingRollPitchOnly(Vector3f targetPosition, Vector3f targetUp, float deltaTime) {
		float result = trackInternal(targetPosition, targetUp, 1000.0f, deltaTime, false);
		executeSteeringNoSpeedChange(targetPosition);
		return result;
	}

	private float trackPosition(Vector3f targetPosition, Vector3f targetUp, float deltaTime) {
		so.updateInternals();
		Quaternion.fromMatrix(so.getMatrix(), q1);
		q1.normalize();

		so.getPosition().sub(targetPosition, v0);
		v0.normalize();

		targetUp.cross(v0, v1);
		v1.normalize();
		v0.cross(v1, v2);
		v2.normalize();

		Quaternion.fromVectors(v1, v2, v0, q2);
		q2.normalize();
		q1.computeDifference(q2, q3);
		q3.normalize();

		q3.axisOfRotation(v0);
		float angle = (float) Math.toDegrees(q3.angleOfRotation());
		if (angle > 180) {
			angle = 360 - angle;
			v0.negate();
		}
		float absAngle = Math.abs(angle);
		if (deltaTime > 0) {
			angle = clamp(angle, -so.getMaxPitchSpeed(), so.getMaxPitchSpeed()) * deltaTime;
		}
		if (Math.abs(angle) > 0.0001f && !Float.isInfinite(angle) && !Float.isNaN(angle)) {
			Matrix.rotateM(so.getMatrix(), 0, angle, v0.x, v0.y, v0.z);
			so.computeInternals();
		}

		return absAngle;
	}

	private float trackInternal(Vector3f targetPosition, Vector3f targetUp, float desiredRangeSq, float deltaTime, boolean retreat) {
		float rate1 = 2.0f * deltaTime;
		float rate2 = 4.0f * deltaTime;
		float stickRoll = 0.0f;
		float stickPitch = 0.0f;
		float reverse = 1.0f;
		float minD = 0.004f;
		float maxCos = 0.995f;

		float maxPitch = so.getMaxPitchSpeed() * 30 * deltaTime;
		float maxRoll  = so.getMaxRollSpeed() * 30 * deltaTime;

		if (retreat) {
			reverse = -reverse;
		}

		so.getPosition().sub(targetPosition, v0);
		float rangeSq = v0.lengthSq();
		if (rangeSq > desiredRangeSq) {
			maxCos = (float) Math.sqrt(1.0 - 0.90 * desiredRangeSq / rangeSq);
		}
		if (v0.isZeroVector()) {
			v0.z = 1.0f;
		} else {
			v0.normalize();
		}

		float dRight   = v0.dot(so.getRightVector());
		float dUp      = v0.dot(so.getUpVector());
		float dForward = v0.dot(so.getForwardVector());

		if (pitchingOver) {
			maxPitch *= 4.0f;
			maxRoll *= 4.0f;
			if (reverse * dUp < 0) {
				stickPitch = maxPitch;
			} else {
				stickPitch = -maxPitch;
			}
			pitchingOver = reverse * dForward < 0.707;
		}
		if (dForward < maxCos || retreat) {
			if (dForward <= -maxCos) {
				dUp = minD * 2.0f;
			}
			if (dUp > minD) {
				int factor = (int) Math.sqrt(Math.abs(dRight) / Math.abs(minD));
				if (factor > 8) {
					factor = 8;
				}
				if (dRight > minD) {
					stickRoll = -maxRoll * 0.125f * factor;
				}
				if (dRight < -minD) {
					stickRoll = maxRoll * 0.125f * factor;
				}
				if (Math.abs(dRight) < Math.abs(stickRoll) * deltaTime) {
					stickRoll = Math.abs(dRight) / deltaTime * (stickRoll < 0 ? -1 : 1);
				}
			}
			if (dUp < -minD) {
				int factor = (int) Math.sqrt(Math.abs(dRight) / Math.abs(minD));
				if (factor > 8) {
					factor = 8;
				}
				if (dRight > minD) {
					stickRoll = maxRoll * 0.125f * factor;
				}
				if (dRight < -minD) {
					stickRoll = -maxRoll * 0.125f * factor;
				}
				if (Math.abs(dRight) < Math.abs(stickRoll) * deltaTime) {
					stickRoll = Math.abs(dRight) / deltaTime * (stickRoll < 0 ? -1 : 1);
				}
			}
			if (Math.abs(stickRoll) < 0.0001) {
				int factor = (int) Math.sqrt(Math.abs(dUp) / Math.abs(minD));
				if (factor > 8) {
					factor = 8;
				}
				if (dUp > minD) {
					stickPitch = -maxPitch * reverse * 0.125f * factor;
				}
				if (dUp < -minD) {
					stickPitch = maxPitch * reverse * 0.125f * factor;
				}
				if (Math.abs(dUp) < Math.abs(stickPitch) * deltaTime) {
					stickPitch = Math.abs(dUp) / deltaTime * (stickPitch < 0 ? -1 : 1);
				}
			}
		}
//		if (targetUp != null) {
//			stickRoll = rollToMatchUp(targetUp);
//		}

		if ((stickRoll > 0.0 && flightRoll < 0.0) || (stickRoll < 0.0 && flightRoll > 0.0)) {
			rate1 *= 4.0f;
		}
		if ((stickPitch > 0.0 && flightPitch < 0.0) || (stickPitch < 0.0 && flightPitch > 0.0)) {
			rate2 *= 4.0f;
		}

		if (flightRoll < stickRoll - rate1) {
			stickRoll = flightRoll + rate1;
		}
		if (flightRoll > stickRoll + rate1) {
			stickRoll = flightRoll - rate1;
		}
		if (flightPitch < stickPitch - rate2) {
			stickPitch = flightPitch + rate2;
		}
		if (flightPitch > stickPitch + rate2) {
			stickPitch = flightPitch - rate2;
		}

		flightRoll = stickRoll;
		flightPitch = stickPitch;


		if (retreat) {
			dForward *= dForward;
		}
		if (dForward < 0.0) {
			return 0.0f;
		}

		if (Math.abs(flightRoll) < 0.0001 && Math.abs(flightPitch) < 0.0001) {
			return 1.0f;
		}

		return dForward;
	}

	private float executeSteering(float desiredSpeed) {
		so.applyDeltaRotation(flightPitch, 0, flightRoll);
		if (so instanceof CobraMkIII && ((CobraMkIII) so).isPlayerCobra()) {
			so.getGame().getCobra().setRotation(flightPitch, flightRoll);
		}
		so.getPosition().copy(v0);
		if (target == null) {
			AliteLog.dumpStack("Execute Steering", "No target set. But why?");
			AliteLog.e("Target is not set in executeSteering", "No target in execute steering for object " + so.getName() + ".");
			return 90; // To make sure this doesn't fire randomly...
		}
		v0.sub(target.getPosition());
		v0.normalize();
		float angle = so.getForwardVector().angleInDegrees(v0);
		if (desiredSpeed > so.getMaxSpeed()) {
			so.adjustSpeed(-so.getMaxSpeed());
		} else if (desiredSpeed < 0) {
			calculateTrackingSpeed(angle);
		} else {
			so.adjustSpeed(-desiredSpeed);
		}
		return angle;
	}

	private float executeSteeringNoSpeedChange(Vector3f targetPosition) {
		so.applyDeltaRotation(flightPitch, 0, flightRoll);
		if (so instanceof CobraMkIII && ((CobraMkIII) so).isPlayerCobra()) {
			so.getGame().getCobra().setRotation(flightPitch, flightRoll);
		}
		so.getPosition().copy(v0);
		v0.sub(targetPosition);
		v0.normalize();
		float angle = so.getForwardVector().angleInDegrees(v0);
		return angle;
	}

	private float executeSteering(float desiredSpeed, Vector3f position) {
		so.applyDeltaRotation(flightPitch, 0, flightRoll);
		if (so instanceof CobraMkIII && ((CobraMkIII) so).isPlayerCobra()) {
			so.getGame().getCobra().setRotation(flightPitch, flightRoll);
		}
		so.getPosition().copy(v0);
		v0.sub(position);
		v0.normalize();
		float angle = so.getForwardVector().angleInDegrees(v0);
		if (desiredSpeed > so.getMaxSpeed()) {
			so.adjustSpeed(-so.getMaxSpeed());
		} else if (desiredSpeed < 0) {
			calculateTrackingSpeed(angle);
		} else {
			so.adjustSpeed(-desiredSpeed);
		}
		return angle;
	}

	private final float clamp(float val, float min, float max) {
		return val < min ? min : val > max ? max : val;
	}

	public float orient(Vector3f targetPosition, Vector3f targetUp, float deltaTime) {
		so.updateInternals();
		Quaternion.fromMatrix(so.getMatrix(), q1);
		q1.normalize();

		so.getPosition().sub(targetPosition, v0);
		v0.normalize();

		targetUp.cross(v0, v1);
		v1.normalize();
		v0.cross(v1, v2);
		v2.normalize();

		Quaternion.fromVectors(v1, v2, v0, q2);
		q2.normalize();
		q1.computeDifference(q2, q3);
		q3.normalize();

		q3.axisOfRotation(v0);
		float angle = (float) Math.toDegrees(q3.angleOfRotation());
		if (angle > 180) {
			angle = 360 - angle;
			v0.negate();
		}
		float result = 0.0f;
		if (deltaTime > 0) {
			float tempAngle = clamp(angle, -so.getMaxPitchSpeed() * 40, so.getMaxPitchSpeed() * 40) * deltaTime;
			result = Math.abs(tempAngle - angle);
			angle = tempAngle;
		}
		if (Math.abs(angle) > 0.0001f && !Float.isInfinite(angle) && !Float.isNaN(angle)) {
			Matrix.rotateM(so.getMatrix(), 0, angle, v0.x, v0.y, v0.z);
			if (so instanceof CobraMkIII && ((CobraMkIII) so).isPlayerCobra()) {
				v1.x = 1;
				v1.y = 0;
				v1.z = 0;
				v2.x = 0;
				v2.y = 0;
				v2.z = 1;
				so.getGame().getCobra().setRotation(v0.dot(v1), v0.dot(v2));
			}
			so.computeInternals();
		}
		return result;
	}

	private final void calculateTrackingSpeed(float angle) {
		if (so instanceof Missile) {
			if (angle > 50) {
				so.setSpeed(-so.getMaxSpeed() * 0.3f);
			} else if (angle > 40) {
				so.setSpeed(-so.getMaxSpeed() * 0.4f);
			} else if (angle > 30) {
				so.setSpeed(-so.getMaxSpeed() * 0.5f);
			} else if (angle > 20) {
				so.setSpeed(-so.getMaxSpeed() * 0.6f);
			} else if (angle > 10) {
				so.setSpeed(-so.getMaxSpeed() * 0.7f);
			} else {
				so.setSpeed(-so.getMaxSpeed());
			}
			return;
		}
		if (angle > 50) {
			so.adjustSpeed(-so.getMaxSpeed() * 0.2f);
		} else if (angle > 40) {
			so.adjustSpeed(-so.getMaxSpeed() * 0.4f);
		} else if (angle > 30) {
			so.adjustSpeed(-so.getMaxSpeed() * 0.6f);
		} else if (angle > 20) {
			so.adjustSpeed(-so.getMaxSpeed() * 0.7f);
		} else if (angle > 10) {
			so.adjustSpeed(-so.getMaxSpeed() * 0.8f);
		} else {
			so.adjustSpeed(-so.getMaxSpeed());
		}
	}

	private void avoidCollision() {
		SpaceObject proximity = so.getProximity();
		if (proximity != null && !so.inBay) {
			pushState(AIState.EVADE, proximity);
		}
	}

	private void attackObject(float deltaTime) {
		if (target instanceof CobraMkIII && ((CobraMkIII) target).isPlayerCobra()) {
			if (InGameManager.playerInSafeZone && !(so instanceof Viper) && !(so.isIgnoreSafeZone())) {
				waitForSafeZoneExit = true;
				pushState(AIState.FLEE, target);
				return;
			}
		}
		trackInternal(target.getPosition(), null, 1000.0f, deltaTime, false);
		avoidCollision();
		float angle = executeSteering(-1);
		float distanceSq = so.getPosition().distanceSq(target.getPosition());
		if (angle < 10 && distanceSq < SHOOT_DISTANCE_SQ && !so.hasEjected()) {
			if (target instanceof SpaceObject && ((SpaceObject) target).isCloaked()) {
				return;
			}
			long time = System.nanoTime();
			int rating = Alite.get().getPlayer().getRating().ordinal();
			if (rating >= 7 || lastShootCheck == -1 || (time - lastShootCheck) >= (BASE_DELAY_BETWEEN_SHOOT_CHECKS - (rating + 2) * SHOOT_DELAY_REDUCE_PER_RATING_LEVEL)) {
				int rand = (int) (Math.random() * 256);
				if (so.getAggressionLevel() > rand) {
					if (so.getGame().getLaserManager() != null) {
						so.getGame().getLaserManager().fire(so, target);
					}
				}
				lastShootCheck = System.nanoTime();
			}
		}
	}

	private void fleeObject(float deltaTime) {
		if (target instanceof CobraMkIII && ((CobraMkIII) target).isPlayerCobra()) {
			if (!InGameManager.playerInSafeZone && waitForSafeZoneExit) {
				popState();
				waitForSafeZoneExit = false;
				return;
			}
		}
		trackInternal(target.getPosition(), null, 1000.0f, deltaTime, true);
		avoidCollision();
		executeSteering(so.getMaxSpeed());
	}

	private void flyStraight(float deltaTime) {
		avoidCollision();
	}

	private void flyPath(float deltaTime) {
		if (waypoints.isEmpty()) {
			popState();
			if (currentState.isEmpty()) {
				setState(AIState.IDLE, (Object []) null);
			}
			so.aiStateCallback(AiStateCallback.EndOfWaypointsReached);
			return;
		}
		WayPoint wp = waypoints.get(0);
		float d = trackInternal(wp.position, wp.upVector, 1000.0f, deltaTime, false);
		float targetSpeed = wp.orientFirst ? 0.0f : so.getMaxSpeed();
		if (Math.abs(1.0 - d) < 0.01 && wp.orientFirst) {
			targetSpeed = so.getMaxSpeed();
			wp.orientFirst = false;
		}
		so.adjustSpeed(-targetSpeed);
		avoidCollision();
		executeSteering(targetSpeed, wp.position);
		float distance = so.getPosition().distanceSq(wp.position);
		if (distance < 1000 || (currentDistance > 0 && currentDistance < 40000 && distance > currentDistance)) {
			currentDistance = -1;
			wp.reached();
			waypoints.remove(0);
		} else {
			currentDistance = distance;
		}
	}

	private void followCurve(float deltaTime) {
		float time = (System.nanoTime() - curveFollowStart) / 1000000000.0f;
		curve.compute(time);

		so.setPosition(curve.getCurvePosition());
		so.setForwardVector(curve.getcForward());
		so.setRightVector(curve.getcRight());
		so.setUpVector(curve.getcUp());

		curve.getCurveRotation().copy(v0);
		so.applyDeltaRotation(v0.x, v0.y, v0.z);
		so.assertOrthoNormal();

//		avoidCollision();

		if (curve.reachedEnd()) {
			popState();
			if (currentState.isEmpty()) {
				so.setSpeed(0);
				setState(AIState.IDLE, (Object []) null);
			}
		}
	}

	private void updateEvade(float deltaTime) {
		float distanceSq = so.getPosition().distanceSq(evadePosition);
		SpaceObject proximity = so.getProximity();
		boolean clearEvade = false;
		if (proximity != null && proximity.getType() == ObjectType.SpaceStation) {
			float maxExtentSq = proximity.getMaxExtent();
			maxExtentSq *= maxExtentSq;
			clearEvade = distanceSq > maxExtentSq;
		}
		if (clearEvade || distanceSq > evadeRangeSq || proximity == null || proximity.getHullStrength() <= 0) {
			so.setProximity(null);
			popState();
			if (currentState.isEmpty()) {
				setState(AIState.IDLE, (Object []) null);
			}
			return;
		}
		proximity.getPosition().sub(so.getPosition(), v0);
		v0.scale(0.5f);
		v0.add(so.getPosition());
		v0.copy(evadePosition);
		evadeRangeSq = (proximity.getBoundingSphereRadiusSq() * 3.0f + so.getBoundingSphereRadiusSq() * 3.0f) * 18;
		float dForward = trackInternal(evadePosition, null, evadeRangeSq, deltaTime, true);
		executeSteering(so.getMaxSpeed() * (0.5f * dForward + 0.5f));
	}

	private void updateTrack(float deltaTime) {
		if (so instanceof Missile && target != null) {
			float angle = trackPosition(target.getPosition(), target.getUpVector(), deltaTime);
			calculateTrackingSpeed(angle);
			return;
		}
		if (target instanceof SpaceObject && !((SpaceObject) target).isCloaked()) {
			trackInternal(target.getPosition(), null, 1000.0f, deltaTime, false);
		}
		executeSteering(so instanceof Missile ? -1 : so.getMaxSpeed());
	}

	private void initiateTrack(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.TRACK) {
			currentState.push(AIState.TRACK);
		}
		target = data == null || data[0] == null ? null : (GraphicObject) data[0];
	}

	private void initiateMissileTrack(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		target = data == null || data[0] == null ? null : (GraphicObject) data[0];
		if (target != null) {
			float distanceSq = target.getPosition().distanceSq(so.getPosition());
			if (distanceSq < 9000000) {
				currentState.push(AIState.TRACK);
			} else {
				pushState(AIState.TRACK, target);
				so.getPosition().copy(v0);
				target.getPosition().sub(v0, v1);
				v1.scale(0.5f);
				target.getPosition().sub(v1, v0);
				target.getRightVector().copy(v1);
				v1.scale(1000);
				v0.add(v1);
				WayPoint wp = WayPoint.newWayPoint(v0, target.getUpVector());
				pushState(AIState.FLY_PATH, wp);
			}
		}
	}

	private void initiateIdle(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.IDLE) {
			currentState.push(AIState.IDLE);
		}
		so.adjustSpeed(0);
	}

	private void initiateStraight(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.FLY_STRAIGHT) {
			currentState.push(AIState.FLY_STRAIGHT);
		}
		if (data != null && data.length > 0) {
			so.adjustSpeed(-(Float) data[0]);
			if (data.length > 1) {
				// Force new speed...
				so.setSpeed(-(Float) data[0]);
			}
		} else {
			so.adjustSpeed(-so.getMaxSpeed());
		}
	}

	private void initiatePath(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.FLY_PATH) {
			currentState.push(AIState.FLY_PATH);
		}
		waypoints.clear();
		if (data != null && data.length > 0) {
			for (Object o: data) {
				if (o != null) {
					waypoints.add((WayPoint) o);
				}
			}
		}
		currentDistance = -1;
		if (!waypoints.isEmpty() && !waypoints.get(0).orientFirst) {
			so.adjustSpeed(-so.getMaxSpeed());
		} else {
			so.adjustSpeed(0);
		}
	}

	private void initiateFollowCurve(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.FOLLOW_CURVE) {
			currentState.push(AIState.FOLLOW_CURVE);
		}
		waypoints.clear();
		if (data != null && data.length > 0) {
			curve = (Curve) data[0];
			curveFollowStart = System.nanoTime();
		}
		currentDistance = -1;
		so.adjustSpeed(-so.getMaxSpeed());
		lastRotation.x = 0;
		lastRotation.y = 0;
		lastRotation.z = 0;
	}

	private void initiateFlee(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.FLEE) {
			currentState.push(AIState.FLEE);
		}
		target = data == null || data[0] == null ? null : (GraphicObject) data[0];
	}

	private void getFartherDirection(Vector3f direction, final Vector3f targetVector, final float scale) {
		so.getPosition().sub(target.getPosition(), v0);
		v0.normalize();

		direction.copy(targetVector);
		targetVector.scale(scale);
		so.getPosition().add(targetVector, v2);
		float d1s = v2.distanceSq(target.getPosition());

		targetVector.negate();
		so.getPosition().add(targetVector, v2);
		float d2s = v2.distanceSq(target.getPosition());

		if (d1s > d2s) {
			targetVector.negate();
		}
	}

	private void initiateBank(boolean replace, Object [] data) {
		target = data == null || data[0] == null ? null : (GraphicObject) data[0];
		if (target == null) {
			if (currentState.isEmpty()) {
				setState(AIState.IDLE, 0);
			} else {
				popState();
			}
			return;
		}
		getFartherDirection(so.getRightVector(), v1, 500);
		getFartherDirection(so.getUpVector(), v3, 500);
		v2.x = so.getPosition().x + v1.x + v3.x + 800 * v0.x;
		v2.y = so.getPosition().y + v1.y + v3.y + 800 * v0.y;
		v2.z = so.getPosition().z + v1.z + v3.z + 800 * v0.z;
		WayPoint wp1 = WayPoint.newWayPoint(v2, target.getRightVector());
		v0.scale(10000.0f);
		v2.add(v0);
		WayPoint wp2 = WayPoint.newWayPoint(v2, target.getUpVector());

		if (replace) {
			setState(AIState.FLY_PATH, wp1, wp2);
		} else {
			pushState(AIState.FLY_PATH, wp1, wp2);
		}
	}

	private void initiateEvade(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.EVADE) {
			currentState.push(AIState.EVADE);
			if (target == null) {
				target = so.getProximity();
			}
		}
		so.getProximity().getPosition().sub(so.getPosition(), v0);
		v0.scale(0.5f);
		v0.add(so.getPosition());
		v0.copy(evadePosition);
		evadeRangeSq = (so.getProximity().getBoundingSphereRadiusSq() * 3.0f + so.getBoundingSphereRadiusSq() * 3.0f) * 18;
		pitchingOver = true;
	}

	private void initiateAttack(boolean replace, Object [] data) {
		if (replace) {
			currentState.clear();
		}
		if (currentState.isEmpty() || currentState.peek() != AIState.ATTACK) {
			currentState.push(AIState.ATTACK);
		}
		target = data == null || data[0] == null ? null : (GraphicObject) data[0];
	}

	private final void alterStateInternal(AIState newState, boolean replace, Object ...data) {
		switch (newState) {
			case ATTACK:        initiateAttack(replace, data);
					    break;
			case BANK:          initiateBank(replace, data);
					    break;
			case EVADE:         initiateEvade(replace, data);
			                    break;
			case FLEE:          initiateFlee(replace, data);
					    break;
			case FLY_STRAIGHT:  initiateStraight(replace, data);
					    break;
			case FLY_PATH:      initiatePath(replace, data);
					    break;
			case IDLE:          initiateIdle(replace, data);
					    break;
			case TRACK:         initiateTrack(replace, data);
					    break;
			case MISSILE_TRACK: initiateMissileTrack(replace, data);
			                    break;
			case FOLLOW_CURVE:  initiateFollowCurve(replace, data);
			                    break;
			default:            break;
		}
	}

	final void setState(AIState newState, Object ...data) {
		alterStateInternal(newState, true, data);
	}

	final void pushState(AIState newState, Object ...data) {
		alterStateInternal(newState, false, data);
	}

	final void popState() {
		if (currentState.isEmpty()) {
			return;
		}
		currentState.pop();
		AIState state = getState();
		if (state == AIState.FOLLOW_CURVE) {
			// Make sure that an interrupted "follow curve" is not resumed.
			popState();
		}
	}

	final void update(float deltaTime) {
		if (so != null) {
			if (so.escapeCapsuleCaps > 0 && so.getHullStrength() < 2 && !so.hasEjected()) {
				if (Math.random() * 100 < 10) {
					so.setEjected(true);
					so.addObjectToSpawn(ShipType.EscapeCapsule);
				}
			}
		}
		if (currentState.isEmpty() || so.hasEjected()) {
			return;
		}
		so.updateSpeed(deltaTime);
		switch (currentState.peek()) {
			case ATTACK:
				attackObject(deltaTime);
				break;
			case BANK:
				AliteLog.e("Updating bank state", "This should not happen...");
				break;
			case EVADE:
				updateEvade(deltaTime);
				break;
			case FLEE:
				fleeObject(deltaTime);
				break;
			case FLY_STRAIGHT:
				flyStraight(deltaTime);
				break;
			case FLY_PATH:
				flyPath(deltaTime);
				break;
			case IDLE:
				break;
			case TRACK:
				updateTrack(deltaTime);
				break;
			case FOLLOW_CURVE:
				followCurve(deltaTime);
				break;
			default:
				break;
		}
		if (Settings.VIS_DEBUG) {
			if (so instanceof CobraMkIII && ((CobraMkIII) so).isPlayerCobra()) {
				String sl = "";
				switch (currentState.peek()) {
				case ATTACK:       sl = "AT"; break;
				case BANK:         sl = "BN"; break;
				case EVADE:        sl = "EV"; break;
				case FLEE:         sl = "FL"; break;
				case FLY_STRAIGHT: sl = "FS"; break;
				case FLY_PATH:     sl = "FP"; break;
				case IDLE:         sl = "ID"; break;
				case TRACK:	   sl = "TR"; break;
				case FOLLOW_CURVE: sl = "FC"; break;
				default:           sl = "DE"; break;
				}
				AliteLog.e("AIS", "SOPATH: Player " + sl + " (" + so.getPosition().x + ":" + so.getPosition().y + ":" + so.getPosition().z +
					   ":" + so.getForwardVector().x + ":" + so.getForwardVector().y + ":" + so.getForwardVector().z +
					   ":" + so.getUpVector().x + ":" + so.getUpVector().y + ":" + so.getUpVector().z +
					   ":" + so.getRightVector().x + ":" + so.getRightVector().y + ":" + so.getRightVector().z +
					   ")");
			}
		}
	}

	public AIState getState() {
		if (currentState.isEmpty()) {
			return null;
		}
		return currentState.peek();
	}

	public String getStateStack() {
		String stack = "";
		for (int i = 0; i < currentState.size(); i++) {
			stack += currentState.get(i) + ", ";
		}
		return stack;
	}

	private void bankOrAttack(SpaceObject player) {
		AIState state = getState();
		AliteLog.d("Object has been hit", "Object has been hit. Current State == " + state.name());
		if (state == AIState.FOLLOW_CURVE) {
			return;
		}
		if (state == AIState.BANK || state == AIState.EVADE || state == AIState.FLY_PATH) {
			float f = (float) Math.random();
			if (f < 0.3) {
				// Do nothing...
				AliteLog.d("NPC got Hit", "On Hit (should be 'no change'): New AI Stack: " + getStateStack());
				return;
			}
			if (f < 0.7) {
				popState();
				so.getForwardVector().copy(v0);
				v0.x *= -Math.random() * 2 + 1;
				v0.y *= -Math.random() * 2 + 1;
				v0.z *= -Math.random() * 2 + 1;
				if (v0.isZeroVector()) {
					v0.x = 1;
					v0.y = 0;
					v0.z = 0;
				}
				v0.normalize();
				pushState(AIState.ATTACK, player);
				pushState(AIState.FLY_PATH, WayPoint.newWayPoint(MathHelper.getRandomPosition(so.getPosition(), v0, 5000, 1000), so.getUpVector()));
				AliteLog.d("NPC got Hit", "On Hit (should be fly path): New AI Stack: " + getStateStack());
				return;
			}
			popState();
			pushState(AIState.ATTACK, player);
			pushState(AIState.FOLLOW_CURVE, Math.random() < 0.5 ? new BreakUp(so) : new BreakDown(so));
			AliteLog.d("NPC got Hit", "On Hit (should be follow curve): New AI Stack: " + getStateStack());
		} else if (state != AIState.ATTACK) {
			pushState(AIState.BANK, player, true);
		}
	}

	private void flee(SpaceObject player) {
		if (getState() != AIState.FLEE) {
			if (Math.random() * 100 < FIRE_MISSILE_UPON_FIRST_HIT_PROBABILITY) {
				if (!player.isCloaked() && so.getMissileCount() > 0 && so.getPosition().distanceSq(player.getPosition()) >= MISSILE_MIN_DIST_SQ) {
					if (so.canFireMissile()) {
						so.setMissileCount(so.getMissileCount() - 1);
						so.addObjectToSpawn(ShipType.Missile);
					}
				}
			}
			setState(AIState.FLEE, player);
		}
	}

	private void fleeBankOrAttack(SpaceObject player) {
		AIState state = getState();
		if (state == AIState.ATTACK) {
			bankOrAttack(player);
		} else if (state == AIState.FLEE) {
			pushState(AIState.BANK, player);
		} else if (state == AIState.BANK || state == AIState.EVADE) {
			popState();
			pushState(AIState.BANK, player);
		} else if (state == AIState.FLY_PATH || state == AIState.FLY_STRAIGHT || state == AIState.IDLE) {
			if (Math.random() * 50 < so.getAggressionLevel()) {
				bankOrAttack(player);
			} else {
				flee(player);
			}
		}
		// Else do nothing...
	}

	void executeHit(SpaceObject player) {
		if (so.getHullStrength() > 0 && !so.mustBeRemoved() && !so.hasEjected()) {
			int rand = (int) (Math.random() * 256);
			int check = (so.getGame().getPlayer().getRating().ordinal() << 2) + 15;
			if (so.getMissileCount() > 0 && rand < check && !player.isCloaked() && so.getPosition().distanceSq(player.getPosition()) >= MISSILE_MIN_DIST_SQ) {
				if (so.canFireMissile()) {
					so.setMissileCount(so.getMissileCount() - 1);
					so.addObjectToSpawn(ShipType.Missile);
				}
			}
		}

		switch (so.getType()) {
			case Asteroid:      break; // Nothing to do
			case CargoCanister: break; // Nothing to do
			case EnemyShip:     bankOrAttack(player); break;
			case EscapeCapsule: pushState(AIState.BANK, player); break;
			case Missile:       break; // Nothing to do
			case Shuttle:       so.hasBeenHitByPlayer(); flee(player); break;
			case SpaceStation:  break; // Nothing to do (actually handled in the SpaceStation code)
			case Thargoid:      bankOrAttack(player); break;
			case Thargon:       bankOrAttack(player); break;
			case Trader:        so.hasBeenHitByPlayer(); fleeBankOrAttack(player); break;
			case Viper:         bankOrAttack(player); break;
			case Buoy:          break; // Nothing to do
			case Platlet:       break; // Nothing to do
		}
	}
}
