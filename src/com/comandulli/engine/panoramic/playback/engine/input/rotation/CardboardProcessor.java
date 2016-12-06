package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class CardboardProcessor implements RotationSensor {

    private final HeadTracker tracker;

	public CardboardProcessor(Context context) {
        tracker = HeadTracker.createFromContext(context);
        tracker.startTracking();
	}

	@Override
	public float[] getRotation() {
		float[] matrix = new float[16];
		tracker.getLastHeadView(matrix, 0);
        return new Quaternion().setFromMatrix(true, matrix).toArray();
	}

	@Override
	public boolean isSensorEnabled() {
		return true;
	}

	@Override
	public void update(SensorEvent event) {
		tracker.onSensorChanged(event);
	}

	@Override
	public void resume() {
		tracker.startTracking();
	}

	@Override
	public void pause() {
		tracker.stopTracking();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		tracker.onAccuracyChanged(sensor, accuracy);
	}

}
