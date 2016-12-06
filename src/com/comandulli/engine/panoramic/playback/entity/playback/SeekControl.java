package com.comandulli.engine.panoramic.playback.entity.playback;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.core.Transform;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;

import android.media.MediaPlayer;

public class SeekControl extends Entity {

	private final Camera camera;
	private final MediaPlayer player;
	private final Subtitle subs;

	private static final float MIN_ANGLE = -38.0f;
	private static final float MAX_ANGLE = 38.0f;
	private static final float ANGLE_LENGTH = 76.0f;

	private static final float MIN_HEIGHT = 30.0f;
	private static final float MAX_HEIGHT = 35.0f;

	private boolean isInFocus;
	private float timemark;

	private Entity pivot;
	private final PlaybackControls controls;

	private final int mesh; // MESH_STATUS
	private final int shader; // SHADER_COLOR

	public SeekControl(String name, MediaPlayer player, Camera camera, PlaybackControls controls, Subtitle subs, int mesh, int shader) {
		super(name);
		this.camera = camera;
		this.player = player;
		this.controls = controls;
		this.subs = subs;
		this.mesh = mesh;
		this.shader = shader;
	}

	@Override
	public void start() {
		Entity marker = new Entity("Start");
		marker.addComponent(new MeshRenderer(mesh, new Material(shader)));
		marker.transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(180.0f), 0.0f);
		marker.transform.scale = Vector3.scaled(Vector3.SCALE_ONE, 0.1f);
		marker.transform.position = new Vector3(0.0f, 0.7f, 8.8f);

        pivot = new Entity("Pivot");
		pivot.transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(MAX_ANGLE), 0.0f);
		pivot.transform.position = new Vector3(0.0f, -6.0f, 0.2f);
		marker.transform.parent = pivot.transform;
		Engine.getScene().addEntity(marker);
		super.start();
		pivot.transform.parent = this.transform.parent;
	}

	@Override
	public void update() {
		super.update();

		int duration;
		float progress;
		try {
			duration = player.getDuration();
			progress = (float) player.getCurrentPosition() / duration;
		} catch (IllegalStateException e) {
			return;
		}
		float currentAngle = MAX_ANGLE - progress * ANGLE_LENGTH;
		pivot.transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(currentAngle), 0.0f);

		Vector3 euler = camera.entity.transform.rotation.getEulerVector();
		float height = (float) Math.toDegrees(euler.x);
		float angle = (float) Math.toDegrees(euler.y - pivot.transform.parent.getWorldRotation().getEulerVector().y);
		if (height > MIN_HEIGHT && height < MAX_HEIGHT) {
			if (angle > MIN_ANGLE && angle < MAX_ANGLE) {
				controls.currentAlpha = 1.0f;
				if (!isInFocus) {
					isInFocus = true;
					timemark = Time.time;
				}
				if (Time.time > timemark + 1.0f) {
					float t = (MAX_ANGLE - angle) / ANGLE_LENGTH;
					float pos = duration * t;
					player.seekTo((int) pos);
					timemark = Time.time;
					if (subs != null) {
						subs.init(pos / 1000.0f);
					}
				}
			} else {
				isInFocus = false;
			}
		} else {
			isInFocus = false;
		}
	}
}
