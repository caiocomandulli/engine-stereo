package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class BeeSensorProcessor implements RotationSensor {

	private float[] rotation = { 1.0f, 0.0f, 0.0f, 0.0f };
	private final float[] diffQuaternion = new float[4];

	private final SensorManager sensorManager;
	private Sensor sensorRotation;
	private boolean sensorEnabled;

	private boolean gyroscopeEnabled = true;
	private boolean accelerometerEnabled = true;
	private boolean versionCompatible = true;

	private SensorEventListener listener;

	@SuppressLint("InlinedApi")
	public BeeSensorProcessor(Context context, SensorEventListener listener) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		// checking for sensors
		if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
			accelerometerEnabled = false;
		}
		if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null) {
			gyroscopeEnabled = false;
		}

		if (accelerometerEnabled && gyroscopeEnabled) {
			sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
			if (sensorRotation == null) {
				versionCompatible = false;
			}
			if (versionCompatible) {
				sensorEnabled = true;
				sensorManager.registerListener(listener, sensorRotation, 0);
				quaternionFromEuler(diffQuaternion, new float[] { 0, 90, 180 });
				this.listener = listener;
			}
		}
	}

	@Override
	public float[] getRotation() {
		float[] rot = rotation.clone();
		quaternionMultiplication(rot, diffQuaternion);
		return rot;
	}
	
	public float[] getUnityRotation() {
		float[] unityRotation = new float[4];

		unityRotation[0] = rotation[3];
		unityRotation[1] = -rotation[2];
		unityRotation[2] = -rotation[1];
		unityRotation[3] = rotation[0];
		quaternionMultiplication(unityRotation, diffQuaternion);
		
		return unityRotation;
	}

	@Override
	public void update(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR || event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
			rotation = new float[4];
			float[] quaternion = new float[4];
			SensorManager.getQuaternionFromVector(quaternion, event.values);
			rotation[0] = quaternion[0];
			rotation[1] = quaternion[1];
			rotation[2] = quaternion[3];
			rotation[3] = quaternion[2];
		}
	}

	@Override
	public boolean isSensorEnabled() {
		return sensorEnabled;
	}

	public boolean isAccelerometerEnabled() {
		return accelerometerEnabled;
	}

	public boolean isGyroscopeEnabled() {
		return gyroscopeEnabled;
	}

	public boolean isVersionCompatible() {
		return versionCompatible;
	}

	@Override
	public void resume() {
		if (sensorRotation != null && sensorEnabled) {
			sensorManager.registerListener(listener, sensorRotation, 0);
		}
	}

	@Override
	public void pause() {
		if (sensorManager != null && sensorEnabled) {
			sensorManager.unregisterListener(listener);
		}
	}

	private void quaternionMultiplication(final float[] source, final float[] other) {
		final float qx = source[3] * other[0] + source[0] * other[3] + source[1] * other[2] - source[2] * other[1];
		final float qy = source[3] * other[1] + source[1] * other[3] + source[2] * other[0] - source[0] * other[2];
		final float qz = source[3] * other[2] + source[2] * other[3] + source[0] * other[1] - source[1] * other[0];
		final float qw = source[3] * other[3] - source[0] * other[0] - source[1] * other[1] - source[2] * other[2];
		source[0] = qx;
		source[1] = qy;
		source[2] = qz;
		source[3] = qw;
	}

	private void quaternionFromEuler(final float[] quaternion, final float[] euler) {
		final float hr = (float) (Math.toRadians(euler[2]) * 0.5f);
		final float shr = (float) Math.sin(hr);
		final float chr = (float) Math.cos(hr);
		final float hp = (float) (Math.toRadians(euler[1]) * 0.5f);
		final float shp = (float) Math.sin(hp);
		final float chp = (float) Math.cos(hp);
		final float hy = (float) (Math.toRadians(euler[0]) * 0.5f);
		final float shy = (float) Math.sin(hy);
		final float chy = (float) Math.cos(hy);
		final float chy_shp = chy * shp;
		final float shy_chp = shy * chp;
		final float chy_chp = chy * chp;
		final float shy_shp = shy * shp;

		quaternion[0] = chy_shp * chr + shy_chp * shr;
		quaternion[1] = shy_chp * chr - chy_shp * shr;
		quaternion[2] = chy_chp * shr - shy_shp * chr;
		quaternion[3] = chy_chp * chr + shy_shp * shr;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
}
