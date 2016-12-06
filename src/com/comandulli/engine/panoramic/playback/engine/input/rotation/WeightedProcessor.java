package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class WeightedProcessor implements RotationSensor {

	private final SensorManager sensorManager;
	private final Sensor accelerationSensor;
	private final Sensor gyroSensor;

	private SensorEventListener listener;

	private Quaternion lastOrientation;
	private Quaternion lastGravity;

	private long lastReading;

	private boolean sensorEnabled;

	public final float MIN_DELTA = (float) (2 * Math.PI / 100.0f);
	public final float GYRO_WEIGHT = 0.98f;
	public final float GRAV_WEIGHT = 1 - GYRO_WEIGHT;
	public final float NANO_TO_SEC = 1 / 1000000000.0f;
	public final float EPSILON = 0.000001f;
	public final float SINGULARITY = (float) (Math.PI / 50.0f) * 3;

	public final Vector3 REFERENCE_GRAVITY = new Vector3(0.0f, 0.0f, 1.0f);

	public WeightedProcessor(Context context, SensorEventListener listener) {
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
			return lastOrientation.toArray();
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
		// half angles
		float sinHalfAngle = (float) Math.sin(dotAngle / 2.0f);
		@SuppressWarnings("UnnecessaryLocalVariable")
        float cosHalfAngle = (float) Math.cos(dotAngle / 2.0f);
		// quaternion conversion
		float quaternionW = cosHalfAngle;
		float quaternionX = axisX * sinHalfAngle;
		float quaternionY = axisY * sinHalfAngle;
		// normalize quaternion
		float quaternionNorm = (float) (1 / Math.sqrt(quaternionW * quaternionW + quaternionX * quaternionX + quaternionY * quaternionY));
		if (quaternionNorm < EPSILON) {
			return;
		}
		quaternionW *= quaternionNorm;
		quaternionX *= quaternionNorm;
		quaternionY *= quaternionNorm;
		// throw to orientation
		lastGravity = new Quaternion(quaternionW, quaternionX, quaternionY, 0.0f).normalize();
		// initialize gravity direction vector
		// Vector3 gravityVector = new Vector3(-readings[2], -readings[1],
		// -readings[0]).normalized();
		// take its cross product, which gives us our axis of rotation
		// Vector3 axis = Vector3.cross(gravityVector, REFERENCE_GRAVITY);
		// take its dot product and then its arc-cosine, which gives us the
		// angle of rotation at the axis
		// float dot = Vector3.dot(gravityVector, REFERENCE_GRAVITY);
		// float angle = (float) Math.acos(dot);

		// float singularity = Vector3.dot(gravityVector, new Vector3(1.0f,
		// 0.0f, 0.0f));
		// if (Math.acos(singularity) < 3.6f) {
		// return;
		// }
		// axis.normalize();
		// final gravity rotator
		// lastGravity = new Quaternion(axis, angle);
		// if not initialized yet use the gravity as start position
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
		new Vector3(-readings[2], -readings[1], -readings[0]).scale(deltaTime);
		// updates timestamp
		lastReading = time;
		// delta over gravity
		// weighted delta
		// calculate final orientation
		Quaternion gravDelta = Quaternion.multiply(lastGravity, lastOrientation.conjugate());
		lastOrientation = Quaternion.multiply(lastOrientation, gravDelta);
		// noise filter
	}

	@Override
	public void pause() {
		sensorManager.unregisterListener(listener);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
