package com.comandulli.engine.panoramic.playback.engine.component;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.input.Input;
import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent;
import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent.TouchType;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;

public class HeadTracking extends Component {

	private final Camera camera;

	private static final float SCROLL_SPEED = 0.1f;
	private Vector2 lastValues;
	private boolean gyroEnabled = true;

	public HeadTracking(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void update() {
		if (gyroEnabled) {
			Quaternion turn = new Quaternion(new Vector3(0.0f, (float) Math.toRadians(180.0f), 0.0f));
			// Quaternion turn = new Quaternion(new Vector3(0.0f, (float) Math.toRadians(180.0f), (float) Math.toRadians(180.0f)));
			camera.entity.transform.rotation = Quaternion.multiply(turn, Input.getOrientation());
		} else {
			updateWithoutGyro();
		}
		super.update();
	}

	private void updateWithoutGyro() {
		TouchEvent event = Input.touch;
		if (event != null) {
			if (lastValues == null) {
				float x = event.x;
				float y = event.y;
				lastValues = new Vector2(x, y);
			}
			if (event.type == TouchType.MOVE) {
				float x = event.x;
				float y = event.y;

				float diffX = x - lastValues.x;
				float diffY = y - lastValues.y;
				lastValues = new Vector2(x, y);
				camera.entity.transform.rotate(new Vector3(diffY * Time.deltaTime * SCROLL_SPEED, -diffX * Time.deltaTime * SCROLL_SPEED, 0.0f));
			} else if (event.type == TouchType.UP) {
				lastValues = null;
			}
		}
	}

	public boolean isGyroEnabled() {
		return gyroEnabled;
	}

	public void setGyroEnabled(boolean gyroEnabled) {
		this.gyroEnabled = gyroEnabled;
		if (!gyroEnabled) {
			camera.entity.transform.rotation = new Quaternion();
		}
	}

}
