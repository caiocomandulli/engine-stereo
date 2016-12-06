package com.comandulli.engine.panoramic.playback.entity.playback;

import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData.Chapter;

import android.media.MediaPlayer;

public class ChapterSelector extends ControlBox {

	private final Camera camera;
	private boolean isCurrentlyEnabled;
	private final ChapterBox[] boxes;

	// private Transform pivot;

	public ChapterSelector(String name, ChapterData data, Camera camera, MediaPlayer player, Subtitle subs, int chapterMesh, int shader, int fontTexture) {
		super(name, new Vector3(-6.05f, -7.4f, 9.0f), new Vector3(1.2f, 1.2f, 1.0f), -35.0f);
		this.camera = camera;
		List<Chapter> chapterList = data.getChapters();
		boxes = new ChapterBox[chapterList.size()];
		for (int i = 0; i < chapterList.size(); i++) {
			Chapter chapter = chapterList.get(i);
			ChapterBox chapterBox = new ChapterBox(chapter, player, subs, this, chapterMesh, shader, fontTexture);
			chapterBox.transform.scale = new Vector3(1.0f, 1.0f, 0.1f);
			chapterBox.transform.position = new Vector3(i * -2.5f, 0.0f, 9.0f);
			chapterBox.setEnabled(false);
			boxes[i] = chapterBox;
		}
		maxPosition = 2.5f * (boxes.length - 1);
		// pivot = new Transform();
	}

	@Override
	public void start() {
        for (ChapterBox box : boxes) {
            Engine.getScene().addEntity(box);
            box.transform.parent = transform.parent;
        }
		// pivot.parent = transform.parent;
		super.start();
	}

	@Override
	public void action() {
		super.action();
		isCurrentlyEnabled = !isCurrentlyEnabled;
        for (ChapterBox box : boxes) {
            box.setEnabled(isCurrentlyEnabled);
        }
	}

	private static final float SLIDE_SPEED = 10.0f;
	private static final float DEADZONE = 0.2f;
	private static final float ANGLE_OF_CHANGE = 90.0f;
	private final float maxPosition;
	private float centerPosition;

	@Override
	public void update() {
		super.update();
		if (isCurrentlyEnabled) {
			float sensorAngle = (float) Math.toDegrees(camera.entity.transform.rotation.getEulerVector().y - transform.parent.rotation.getEulerVector().y);
			if (sensorAngle > -ANGLE_OF_CHANGE && sensorAngle < ANGLE_OF_CHANGE) {
				float sine = (float) -Math.sin(Math.toRadians(sensorAngle));
				if (Math.abs(sine) > DEADZONE) {
					float delta = Time.deltaTime * SLIDE_SPEED * sine;
					float newPosition = centerPosition + delta;
					if (newPosition < maxPosition && newPosition > 0.0f) {
						centerPosition = newPosition;
						// pivot.position.x += delta;
						for (ChapterBox box : boxes) {
							box.transform.position.x += delta;
						}
					}
				}
			}
		}
	}
}
