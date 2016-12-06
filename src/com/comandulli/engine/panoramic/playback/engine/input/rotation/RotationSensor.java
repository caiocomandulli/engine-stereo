package com.comandulli.engine.panoramic.playback.engine.input.rotation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

public interface RotationSensor {

	float[] getRotation();

	boolean isSensorEnabled();

	void update(SensorEvent event);

	void resume();

	void pause();
	
	void onAccuracyChanged(Sensor sensor, int accuracy);

}
