package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.comandulli.engine.panoramic.playback.engine.math.Matrix3;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;

public class MatrixProcessor implements RotationSensor {

	private final SensorManager sensorManager;
	private final Sensor accelerationSensor;
	private final Sensor gyroSensor;

	private SensorEventListener listener;

	private final float[] gravity = new float[3];
	private final float[] orientation = new float[3];
	private boolean initState = true;

	private long lastReading;

	private boolean sensorEnabled;

	public final float MIN_DELTA = (float) (2 * Math.PI / 100.0f);
	public final float GYRO_WEIGHT = 0.98f;
	public final float GRAV_WEIGHT = 1 - GYRO_WEIGHT;
	public final float NANO_TO_SEC = 1 / 1000000000.0f;
	public final float EPSILON = 0.000001f;
	public final float SINGULARITY = (float) (Math.PI / 50.0f) * 3;

	public MatrixProcessor(Context context, SensorEventListener listener) {
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
		return new Quaternion().toArray();
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
		gravity[0] = 0.0f;
		gravity[1] = (float) Math.asin(-y);
		gravity[2] = (float) Math.atan2(-x, z);
		if (initState) {
			orientation[0] = gravity[0];
			orientation[1] = gravity[1];
			orientation[2] = gravity[2];
			initState = false;
		}
	}

	private void handleGyro(float[] readings, long time) {
		// if gravity hasn't set our start position.. wait for it
		if (initState) {
			// first reading
			lastReading = time;
			return;
		}
		float deltaTime = (time - lastReading) * NANO_TO_SEC;
		float[] gyro = new float[3];
		// weighted gyro difference over time
		gyro[0] = -readings[2] * deltaTime;
		gyro[1] = -readings[1] * deltaTime;
		gyro[2] = -readings[0] * deltaTime;
		// updates timestamp
		lastReading = time;
		
		Matrix3 orientationMatrix = new Matrix3(orientation);
		Matrix3 gyroMatrix = new Matrix3(eulerToMatrix(gyro));

		gyroMatrix.mul(orientationMatrix);

		gyro = matrixToEuler(gyroMatrix);

		orientation[0] = gyro[0];
		orientation[1] = gyro[1];
		orientation[2] = gyro[2];
		
		// gyroscope orientation
		// fused orientation
	}

	@Override
	public void pause() {
		sensorManager.unregisterListener(listener);
	}

	private Matrix3 eulerToMatrix(float[] euler) {
		float[] matrix = new float[9];

		float ch = (float) Math.cos(euler[0]);
		float sh = (float) Math.sin(euler[0]);
		float ca = (float) Math.cos(euler[1]);
		float sa = (float) Math.sin(euler[1]);
		float cb = (float) Math.cos(euler[2]);
		float sb = (float) Math.sin(euler[2]);

		matrix[0] = ch * ca;
		matrix[1] = sh * sb - ch * sa * cb;
		matrix[2] = ch * sa * sb + sh * cb;
		matrix[3] = sa;
		matrix[4] = ca * cb;
		matrix[5] = -ca * sb;
		matrix[6] = -sh * ca;
		matrix[7] = sh * sa * cb + ch * sb;
		matrix[8] = -sh * sa * sb + ch * cb;

		return new Matrix3(matrix);
	}

	private float[] matrixToEuler(Matrix3 matrix) {
		float[] rotation = new float[3];
		if (matrix.val[Matrix3.M10] > 0.998) { // singularity at north pole
			rotation[0] = (float) Math.atan2(matrix.val[Matrix3.M02], matrix.val[Matrix3.M22]);
			rotation[1] = (float) (Math.PI / 2);
			rotation[2] = 0;
			return rotation;
		}
		if (matrix.val[Matrix3.M10] < -0.998) { // singularity at south pole
			rotation[0] = (float) Math.atan2(matrix.val[Matrix3.M02], matrix.val[Matrix3.M22]);
			rotation[1] = (float) (-Math.PI / 2);
			rotation[2] = 0;
			return rotation;
		}
		rotation[0] = (float) Math.atan2(-matrix.val[Matrix3.M20], matrix.val[Matrix3.M00]);
		rotation[1] = (float) Math.atan2(-matrix.val[Matrix3.M12], matrix.val[Matrix3.M11]);
		rotation[2] = (float) Math.asin(matrix.val[Matrix3.M10]);
		return new float[3];
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

}
