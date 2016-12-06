package com.comandulli.engine.panoramic.playback.entity.component;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.core.Transform;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class PivotRotation extends Component {

	private final Transform parent;
	private static final float DIFF_VALUE = 38.0f;

	public PivotRotation(Transform parent) {
		this.parent = parent;
	}

	@Override
	public void update() {
		super.update();
		entity.transform.position = parent.position;
		float cameraAngle = (float) Math.toDegrees(parent.rotation.getEulerVector().y);

		Vector3 eulerPivot = entity.transform.rotation.getEulerVector();
		float pivotAngle = (float) Math.toDegrees(eulerPivot.y);
		if (cameraAngle < 0) {
			cameraAngle += 360;
		}
		if (pivotAngle < 0) {
			pivotAngle += 360;
		}
		if (cameraAngle > 270.0f && pivotAngle < 90.0f) {
			cameraAngle -= 360;
		}
		if (pivotAngle > 270.0f && cameraAngle < 90.0f) {
			cameraAngle += 360;
		}

		float diff = Math.abs(cameraAngle - pivotAngle);
		if (diff > DIFF_VALUE) {
			float newAngle = cameraAngle;
			if (cameraAngle > pivotAngle) {
				newAngle -= DIFF_VALUE;
			} else {
				newAngle += DIFF_VALUE;
			}
			entity.transform.rotation = new Quaternion(eulerPivot.x, (float) Math.toRadians(newAngle), eulerPivot.z);
		}
	}

}
