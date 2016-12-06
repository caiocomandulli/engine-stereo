package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class SimpleProcessor implements RotationSensor {

	private final SensorManager sensorManager;
	private final Sensor accelerationSensor;
	private final Sensor gyroSensor;

	private SensorEventListener listener;

	private Vector3 lastOrientation;
	private Vector3 lastGravity;

	private long lastReading;

	private boolean sensorEnabled;

	public final float MIN_DELTA = (float) (2 * Math.PI / 100.0f);
	public final float GYRO_WEIGHT = 0.98f;
	public final float GRAV_WEIGHT = 1 - GYRO_WEIGHT;
	public final float NANO_TO_SEC = 1 / 1000000000.0f;
	public final float EPSILON = 0.000001f;
	public final float SINGULARITY = (float) (Math.PI / 50.0f) * 3;

	public SimpleProcessor(Context context, SensorEventListener listener) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		sensorManager.registerListener(listener, accelerationSensor, 0);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager.registerListener(listener, gyroSensor, 0);

		if (accelerationSensor != null && gyroSensor != null) {
			this.listener = listener;
			sensorEnabled = true;
		}
	}

	@Override
	public float[] getRotation() {
		if (lastOrientation != null) {
			return new Quaternion(lastOrientation).toArray();
		} else {
			return new Quaternion().toArray();
		}
	}

	@Override
	public boolean isSensorEnabled() {
		return sensorEnabled;
	}

	@Override
	public void update(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			handleAcceleration(event.values);
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			handleGyro(event.values, event.timestamp);
		}
	}

	@Override
	public void resume() {
		if (sensorEnabled) {
			sensorManager.registerListener(listener, accelerationSensor, 0);
			sensorManager.registerListener(listener, gyroSensor, 0);
		}
	}

	private void handleAcceleration(float[] readings) {
		// gravity vector
		float gravityX = -readings[2];
		float gravityY = -readings[1];
		float gravityZ = -readings[0];
		// normalize gravity
		float gravityNorm = (float) (1 / Math.sqrt(gravityX * gravityX + gravityY * gravityY + gravityZ * gravityZ));
		if (gravityNorm < EPSILON) {
			return;
		}
		gravityX *= gravityNorm;
		gravityY *= gravityNorm;
		gravityZ *= gravityNorm;
		// acos of dot product
		float dotAngle = (float) Math.acos(gravityZ);
		if (dotAngle > Math.PI) {
			dotAngle -= 2 * Math.PI;
		}
		// cross product axis
		float axisX = gravityY;
		float axisY = -gravityX;
		float axisNorm = (float) (1 / Math.sqrt(axisX * axisX + axisY * axisY));
		if (axisNorm < EPSILON) {
			return;
		}
		// normalize axis
		axisX *= axisNorm;
		axisY *= axisNorm;
		// convert to euler
		float s = (float) Math.sin(dotAngle);
		float c = (float) Math.cos(dotAngle);
		float t = 1 - c;
		float test = axisX * axisY * t;
		float yaw;
		float pitch;
		float roll;
		if (test > 0.998) { // north pole singularity detected
			yaw = (float) (2 * Math.atan2(axisX * Math.sin(dotAngle / 2), Math.cos(dotAngle / 2)));
			pitch = (float) (Math.PI / 2);
			roll = 0;
            lastGravity = new Vector3(roll, yaw, pitch);
            if (lastOrientation == null) {
                lastOrientation = lastGravity.copy();
            }
            return;
		}
		if (test < -0.998) { // south pole singularity detected
			yaw = (float) (-2 * Math.atan2(axisX * Math.sin(dotAngle / 2), Math.cos(dotAngle / 2)));
			pitch = (float) (-Math.PI / 2);
			roll = 0;
            lastGravity = new Vector3(roll, yaw, pitch);
            if (lastOrientation == null) {
                lastOrientation = lastGravity.copy();
            }
            return;
		}
		yaw = (float) Math.atan2(axisY * s, 1 - axisY * axisY * t);
		pitch = (float) Math.asin(axisX * axisY * t);
		roll = (float) Math.atan2(axisX * s, 1 - axisX * axisX * t);
		lastGravity = new Vector3(roll, yaw, pitch);
		if (lastOrientation == null) {
			lastOrientation = lastGravity.copy();
		}
	}

	private void handleGyro(float[] readings, long time) {
		// if gravity hasn't set our start position.. wait for it
		if (lastOrientation == null) {
			// first reading
			lastReading = time;
			return;
		}
		float deltaTime = (time - lastReading) * NANO_TO_SEC;
		// weighted gyro difference over time
		Vector3 gyroDeltaVector = new Vector3(-readings[2], -readings[1], -readings[0]).scale(deltaTime);
		// updates timestamp
		lastReading = time;
		// gyroscope orientation
		// Vector3 gyroOrientation = lastOrientation.add(gyroDeltaVector);

		Vector3 deltaGrav = Vector3.sum(lastGravity, Vector3.scaled(lastOrientation, -1.0f));

		// fused orientation
		lastOrientation = lastOrientation.add(new Vector3(gyroDeltaVector.x * GYRO_WEIGHT + deltaGrav.x * GRAV_WEIGHT, gyroDeltaVector.y * GYRO_WEIGHT + deltaGrav.y * GRAV_WEIGHT, gyroDeltaVector.z));
		// lastOrientation = new Vector3(gyroOrientation.x * GYRO_WEIGHT +
		// lastGravity.x * GRAV_WEIGHT, gyroOrientation.y * GYRO_WEIGHT +
		// lastGravity.y * GRAV_WEIGHT, gyroOrientation.z);
	}

	@Override
	public void pause() {
		sensorManager.unregisterListener(listener);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
