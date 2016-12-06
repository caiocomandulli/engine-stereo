package com.comandulli.engine.panoramic.playback.engine.input;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent.TouchType;
import com.comandulli.engine.panoramic.playback.engine.input.rotation.CardboardProcessor;
import com.comandulli.engine.panoramic.playback.engine.input.rotation.RotationSensor;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;

public class Input implements SensorEventListener {

	public enum RotationValue {
		X(0), Y(1), Z(2), W(3);

		public final int index;

		RotationValue(int index) {
			this.index = index;
		}
	}

	public enum RotationDirection {
		POSITIVE(1), NEGATIVE(-1), ZERO(0);

		public final int multiplier;

		RotationDirection(int multiplier) {
			this.multiplier = multiplier;
		}
	}

	public static final RotationValue AXIS_X = RotationValue.Z;
	public static final RotationValue AXIS_Y = RotationValue.X;
	public static final RotationValue AXIS_Z = RotationValue.Y;
	public static final RotationValue AXIS_W = RotationValue.W;
	public static final RotationDirection DIRECTION_X = RotationDirection.POSITIVE;
	public static final RotationDirection DIRECTION_Y = RotationDirection.POSITIVE;
	public static final RotationDirection DIRECTION_Z = RotationDirection.POSITIVE;
	public static final RotationDirection DIRECTION_W = RotationDirection.NEGATIVE;

	private static Input input;
	private final RotationSensor rotationSensor;

	public static TouchEvent touch;
	private static TouchEvent lastTouch;

	private Input(Context context) {
		this.rotationSensor = new CardboardProcessor(context);
	}

	public static synchronized Input init(Context context) {
		if (input == null) {
			input = new Input(context);
		} else {
            resume();
        }
		return input;
	}

	public static void update() {
		touch = lastTouch;
		lastTouch = null;
	}

	public static void pause() {
		input.rotationSensor.pause();
	}

	public static void resume() {
		input.rotationSensor.resume();
	}

	public static void registerTouch(TouchEvent touch) {
        if (lastTouch != null) {
            if (lastTouch.type == TouchType.UPANDDOWN) {
                touch.type = TouchType.UPANDDOWN;
            } else if (lastTouch.type == TouchType.DOWN) {
                if (touch.type == TouchType.UP) {
                    touch.type = TouchType.UPANDDOWN;
                } else {
                    touch.type = TouchType.DOWN;
                }
            } else if (lastTouch.type == TouchType.UP) {

            }
        }
		lastTouch = touch;
	}

	public static boolean getTouchDown() {
        return touch != null && (touch.type == TouchType.DOWN || touch.type == TouchType.UPANDDOWN);
    }

	public static boolean getTouchUp() {
        return touch != null && (touch.type == TouchType.UP || touch.type == TouchType.UPANDDOWN);
    }

	public static boolean getTouch() {
        return touch != null;
    }

	public static Quaternion getOrientation() {
		return input.getOrientationInstance();
	}

	public static RotationSensor getRotationSensor() {
		return input.rotationSensor;
	}

	private Quaternion getOrientationInstance() {
		float[] rotation = rotationSensor.getRotation();

		float x = DIRECTION_X.multiplier * rotation[AXIS_X.index];
		float y = DIRECTION_Y.multiplier * rotation[AXIS_Y.index];
		float z = DIRECTION_Z.multiplier * rotation[AXIS_Z.index];
		float w = DIRECTION_W.multiplier * rotation[AXIS_W.index];

		return new Quaternion(w, x, y, z).normalize();
		// BeeProcessor return new Quaternion(-rotation[3], rotation[0], rotation[1], -rotation[2]).normalize();
		// return new Quaternion(rotation[3], rotation[0], rotation[1], rotation[2]).normalize();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		rotationSensor.update(event);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		rotationSensor.onAccuracyChanged(sensor, accuracy);
	}
}
