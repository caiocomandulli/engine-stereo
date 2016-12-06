package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class KalmanProcessor implements RotationSensor {

	private final SensorManager sensorManager;
	private final Sensor accelerationSensor;
	private final Sensor gyroSensor;

	private SensorEventListener listener;

	private boolean sensorEnabled;

	public final float MIN_DELTA = (float) (2 * Math.PI / 100.0f);
	public final float GYRO_WEIGHT = 0.98f;
	public final float GRAV_WEIGHT = 1 - GYRO_WEIGHT;
	public final float NANO_TO_SEC = 1 / 1000000000.0f;

	private final float[] gyro = new float[3];
	private float[] gyroMatrix = new float[9];
	private final float[] gyroOrientation = new float[3];
	private final float[] gravOrientation = new float[3];
	private final float[] fusedOrientation = new float[3];
	private long lastReading;
	private boolean initState = true;

	public KalmanProcessor(Context context, SensorEventListener listener) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		sensorManager.registerListener(listener, accelerationSensor, 0);
		gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager.registerListener(listener, gyroSensor, 0);

		if (accelerationSensor != null && gyroSensor != null) {
			this.listener = listener;
			sensorEnabled = true;
		}

		gyroOrientation[0] = 0.0f;
		gyroOrientation[1] = 0.0f;
		gyroOrientation[2] = 0.0f;

		gyroMatrix[0] = 1.0f;
		gyroMatrix[1] = 0.0f;
		gyroMatrix[2] = 0.0f;
		gyroMatrix[3] = 0.0f;
		gyroMatrix[4] = 1.0f;
		gyroMatrix[5] = 0.0f;
		gyroMatrix[6] = 0.0f;
		gyroMatrix[7] = 0.0f;
		gyroMatrix[8] = 1.0f;
	}

	@Override
	public float[] getRotation() {
		float[] rotation = new float[4];
		final float hr = -fusedOrientation[2] * 0.5f;
		final float shr = (float) Math.sin(hr);
		final float chr = (float) Math.cos(hr);
		final float hp = -fusedOrientation[1] * 0.5f;
		final float shp = (float) Math.sin(hp);
		final float chp = (float) Math.cos(hp);
		final float hy = -fusedOrientation[0] * 0.5f;
		final float shy = (float) Math.sin(hy);
		final float chy = (float) Math.cos(hy);
		final float chy_shp = chy * shp;
		final float shy_chp = shy * chp;
		final float chy_chp = chy * chp;
		final float shy_shp = shy * shp;
		final float x = chy_shp * chr + shy_chp * shr;
		final float y = shy_chp * chr - chy_shp * shr;
		final float z = chy_chp * shr - shy_shp * chr;
		final float w = chy_chp * chr + shy_shp * shr;
		rotation[0] = y;
		rotation[1] = -z;
		rotation[2] = -x;
		rotation[3] = w;
		return rotation;
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
		float x = readings[0];
		float y = readings[1];
		float z = readings[2];
		final float norm = (float) Math.sqrt(x * x + y * y + z * z);
		x /= norm;
		y /= norm;
		z /= norm;
		gravOrientation[0] = 0.0f;
		gravOrientation[1] = (float) Math.asin(-y);
		gravOrientation[2] = (float) Math.atan2(-x, z);
	}

	private void handleGyro(float[] readings, long time) {
		if (gravOrientation == null) {
			return;
		}
		if (initState) {
            float[] initMatrix = getRotationMatrixFromOrientation(gravOrientation);
			float[] test = new float[3];
			SensorManager.getOrientation(initMatrix, test);
			gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
			initState = false;
		}

		float[] deltaVector = new float[4];
		if (lastReading != 0) {
			final float dt = (time - lastReading) * NANO_TO_SEC;
			System.arraycopy(readings, 0, gyro, 0, 3);
			float[] normValues = new float[3];
			float omegaMagnitude = (float) Math.sqrt(gyro[0] * gyro[0] + gyro[1] * gyro[1] + gyro[2] * gyro[2]);
			if (omegaMagnitude > MIN_DELTA) {
				normValues[0] = gyro[0] / omegaMagnitude;
				normValues[1] = gyro[1] / omegaMagnitude;
				normValues[2] = gyro[2] / omegaMagnitude;
			}

			float thetaOverTwo = omegaMagnitude * dt;
			float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
			float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
			deltaVector[0] = sinThetaOverTwo * normValues[0];
			deltaVector[1] = sinThetaOverTwo * normValues[1];
			deltaVector[2] = sinThetaOverTwo * normValues[2];
			deltaVector[3] = cosThetaOverTwo;
		}
		lastReading = time;

		float[] deltaMatrix = new float[9];
		SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
		gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);
		SensorManager.getOrientation(gyroMatrix, gyroOrientation);

		fusedOrientation[0] = gyroOrientation[0];
		fusedOrientation[1] = gyroOrientation[1];
		fusedOrientation[2] = gyroOrientation[2];

		gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
		System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
	}

	private float[] getRotationMatrixFromOrientation(float[] o) {
		float[] xM = new float[9];
		float[] yM = new float[9];
		float[] zM = new float[9];

		float sinX = (float) Math.sin(o[1]);
		float cosX = (float) Math.cos(o[1]);
		float sinY = (float) Math.sin(o[2]);
		float cosY = (float) Math.cos(o[2]);
		float sinZ = (float) Math.sin(o[0]);
		float cosZ = (float) Math.cos(o[0]);

		// rotation about x-axis (pitch)
		xM[0] = 1.0f;
		xM[1] = 0.0f;
		xM[2] = 0.0f;
		xM[3] = 0.0f;
		xM[4] = cosX;
		xM[5] = sinX;
		xM[6] = 0.0f;
		xM[7] = -sinX;
		xM[8] = cosX;

		// rotation about y-axis (roll)
		yM[0] = cosY;
		yM[1] = 0.0f;
		yM[2] = sinY;
		yM[3] = 0.0f;
		yM[4] = 1.0f;
		yM[5] = 0.0f;
		yM[6] = -sinY;
		yM[7] = 0.0f;
		yM[8] = cosY;

		// rotation about z-axis (azimuth)
		zM[0] = cosZ;
		zM[1] = sinZ;
		zM[2] = 0.0f;
		zM[3] = -sinZ;
		zM[4] = cosZ;
		zM[5] = 0.0f;
		zM[6] = 0.0f;
		zM[7] = 0.0f;
		zM[8] = 1.0f;

		// rotation order is y, x, z (roll, pitch, azimuth)
		float[] resultMatrix = matrixMultiplication(xM, yM);
		resultMatrix = matrixMultiplication(zM, resultMatrix);
		return resultMatrix;
	}

	private float[] matrixMultiplication(float[] A, float[] B) {
		float[] result = new float[9];

		result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
		result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
		result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

		result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
		result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
		result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

		result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
		result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
		result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

		return result;
	}

	@Override
	public void pause() {
		sensorManager.unregisterListener(listener);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
