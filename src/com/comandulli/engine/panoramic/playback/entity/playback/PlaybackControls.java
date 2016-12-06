package com.comandulli.engine.panoramic.playback.entity.playback;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer.Alignment;
import com.comandulli.engine.panoramic.playback.entity.component.Logger;
import com.comandulli.engine.panoramic.playback.entity.component.PivotRotation;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.WindowManager.LayoutParams;

public class PlaybackControls extends Entity {

	private Runnable callback;
	private final MediaPlayer player;
	private float volume = 0.9f;
	private float brightness = 1.0f;
	private final Activity activity;
	private final Camera camera;

	public boolean controlsInFocus;

	private Subtitle subtitle;
	private ChapterData chapterData;

	private final int mesh;
	private final int texture;
	private final int shader;
	private final int fontTexture;
	private final int seekMesh;
	private final int seekShader;
	private final int chapterTexture;
	private final int chapterMesh;

	public PlaybackControls(String name, MediaPlayer player, Activity activity, Camera camera, int mesh, int texture, int shader, int fontTexture, int seekMesh, int seekShader, int chapterTexture, int chapterMesh) {
		super(name);
		this.player = player;
		this.activity = activity;
		this.camera = camera;
		this.mesh = mesh;
		this.texture = texture;
		this.shader = shader;
		this.fontTexture = fontTexture;
		this.seekMesh = seekMesh;
		this.seekShader = seekShader;
		this.chapterTexture = chapterTexture;
		this.chapterMesh = chapterMesh;
	}

	@Override
	public void start() {
		Entity playback = new Entity("Playback");
		Material playbackMaterial = new Material(texture, shader);
		playback.addComponent(new MeshRenderer(mesh, playbackMaterial));

		playback.transform.position = new Vector3(0.0f, -6.0f, 8.0f);
		playback.transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(180.0f), 0.0f);

		playback.transform.scale = Vector3.scaled(Vector3.SCALE_ONE, 0.1f);
		playbackMaterial.textureTiling = new Vector2(1.0f, -1.0f);

		brightness = activity.getWindow().getAttributes().screenBrightness;
		if (brightness < 0.0f) {
			brightness = -brightness;
		}

		final Entity volIndicatorText = new Entity("VolInd");
		Material indicatorMaterial = new Material(fontTexture, shader, Color.WHITE);
		volIndicatorText.addComponent(new TextRenderer(String.valueOf(Math.round(volume * 10)), 0.02f, Alignment.CENTER, indicatorMaterial));
		volIndicatorText.transform.position = new Vector3(2.6f, -6.8f, 9.0f);
		volIndicatorText.transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(15.0f), 0.0f);

		final Entity brightIndicatorText = new Entity("BrightInd");
		brightIndicatorText.addComponent(new TextRenderer(String.valueOf(Math.round(brightness * 10 - 1)), 0.02f, Alignment.CENTER, indicatorMaterial));
		brightIndicatorText.transform.position = new Vector3(-3.1f, -6.9f, 9.0f);
		brightIndicatorText.transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(-20.0f), 0.0f);

		ControlBox backControl = new ControlBox("Back", new Vector3(6.2f, -7.4f, 9.0f), new Vector3(1.2f, 1.0f, 1.0f), 35.0f) {
			@Override
			public void action() {
				super.action();
				if (callback != null) {
					callback.run();
				}
			}
		};
		ControlBox minusVol = new ControlBox("Minus Volume", new Vector3(4.55f, -6.8f, 9.0f), new Vector3(1.0f, 1.0f, 1.0f), 30.0f) {
			@Override
			public void action() {
				super.action();
				volume -= 0.1f;
				if (volume < 0.0f) {
					volume = 0.0f;
				}
				player.setVolume(volume, volume);
				volIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(volume * 10)), 0.02f, Alignment.FLOAT_LEFT, volIndicatorText.renderer.material);
                volIndicatorText.addComponent(volIndicatorText.renderer);
			}
		};
		ControlBox volIndicatorBox = new ControlBox("Volume Indicator", new Vector3(3.1f, -6.5f, 9.0f), new Vector3(1.4f, 1.0f, 1.0f), 15.0f);
		ControlBox plusVol = new ControlBox("Plus Volume", new Vector3(1.6f, -6.3f, 9.0f), new Vector3(1.2f, 1.0f, 1.0f), 10.0f) {
			@Override
			public void action() {
				super.action();
				volume += 0.1f;
				if (volume > 0.9f) {
					volume = 0.9f;
				}
				player.setVolume(volume, volume);
				volIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(volume * 10)), 0.02f, Alignment.FLOAT_LEFT, volIndicatorText.renderer.material);
                volIndicatorText.addComponent(volIndicatorText.renderer);
			}
		};
		ControlBox playControl = new ControlBox("Play", new Vector3(0.1f, -6.1f, 9.0f), new Vector3(1.4f, 1.0f, 1.0f), 0.0f) {
			@Override
			public void action() {
				super.action();
				if (player.isPlaying()) {
					player.pause();
				} else {
					player.start();
				}
			}
		};
		ControlBox minusBright = new ControlBox("Minus Brightness", new Vector3(-1.4f, -6.2f, 9.0f), new Vector3(1.2f, 1.0f, 1.0f), -10.0f) {
			@Override
			public void action() {
				super.action();
				final LayoutParams layout = activity.getWindow().getAttributes();
				brightness -= 0.1f;
				if (brightness < 0.1f) {
					brightness = 0.1f;
				}
				layout.screenBrightness = brightness;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.getWindow().setAttributes(layout);
					}
				});
				brightIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(brightness * 10 - 1)), 0.02f, Alignment.FLOAT_LEFT, brightIndicatorText.renderer.material);
                brightIndicatorText.addComponent(brightIndicatorText.renderer);
			}
		};
		ControlBox brightIndicatorBox = new ControlBox("Brightness Indicator", new Vector3(-2.8f, -6.4f, 9.0f), new Vector3(1.4f, 1.0f, 1.0f), -20.0f);
		ControlBox plusBright = new ControlBox("Plus Brightness", new Vector3(-4.3f, -6.8f, 9.0f), new Vector3(1.2f, 1.0f, 1.0f), -25.0f) {
			@Override
			public void action() {
				super.action();
				final LayoutParams layout = activity.getWindow().getAttributes();
				brightness += 0.1f;
				if (brightness > 1.0f) {
					brightness = 1.0f;
				}
				layout.screenBrightness = brightness;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						activity.getWindow().setAttributes(layout);
					}
				});
				brightIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(brightness * 10 - 1)), 0.02f, Alignment.FLOAT_LEFT, brightIndicatorText.renderer.material);
                brightIndicatorText.addComponent(brightIndicatorText.renderer);
			}
		};

		Engine.getScene().addEntity(playback);
		Engine.getScene().addEntity(minusVol.setControls(this));
		Engine.getScene().addEntity(volIndicatorBox.setControls(this));
		Engine.getScene().addEntity(volIndicatorText);
		Engine.getScene().addEntity(plusVol.setControls(this));
		Engine.getScene().addEntity(playControl.setControls(this));
		Engine.getScene().addEntity(backControl.setControls(this));
		Engine.getScene().addEntity(minusBright.setControls(this));
		Engine.getScene().addEntity(brightIndicatorBox.setControls(this));
		Engine.getScene().addEntity(brightIndicatorText);
		Engine.getScene().addEntity(plusBright.setControls(this));

		Entity seek = new SeekControl("Seek", player, camera, this, subtitle, seekMesh, seekShader);
		Engine.getScene().addEntity(seek);

		materialList = new Material[2];
		materialList[0] = playbackMaterial;
		materialList[1] = indicatorMaterial;

		Entity pivotRot = new Entity("Pivot");
		pivotRot.addComponent(new PivotRotation(camera.entity.transform));
		Engine.getScene().addEntity(pivotRot);
		Logger.setParent(pivotRot.transform);

		playback.transform.parent = pivotRot.transform;
		minusVol.transform.parent = pivotRot.transform;
		volIndicatorBox.transform.parent = pivotRot.transform;
		volIndicatorText.transform.parent = pivotRot.transform;
		plusVol.transform.parent = pivotRot.transform;
		playControl.transform.parent = pivotRot.transform;
		backControl.transform.parent = pivotRot.transform;
		minusBright.transform.parent = pivotRot.transform;
		brightIndicatorBox.transform.parent = pivotRot.transform;
		brightIndicatorText.transform.parent = pivotRot.transform;
		plusBright.transform.parent = pivotRot.transform;
		seek.transform.parent = pivotRot.transform;

		if (chapterData != null) {
			ChapterSelector chapterSelector = new ChapterSelector("ChapterSelect", chapterData, camera, player, subtitle, chapterMesh, shader, fontTexture);
			Engine.getScene().addEntity(chapterSelector.setControls(this));
			chapterSelector.transform.parent = pivotRot.transform;
		}
	}

	public float currentAlpha = 1.0f;
	private float focusTimemark;
	private Material[] materialList;

	@Override
	public void update() {
		super.update();
		if (controlsInFocus) {
			currentAlpha = 1.0f;
			focusTimemark = Time.time;
		} else {
			if (currentAlpha > 0.1f) {
				if (Time.time > focusTimemark + 1.0f) {
					currentAlpha -= Time.deltaTime;
					if (currentAlpha < 0.1f) {
						currentAlpha = 0.1f;
					}
				}
			}
		}
		Color tint = new Color(1.0f, 1.0f, 1.0f, currentAlpha);
		for (Material material : materialList) {
			material.color = tint;
		}
		if (subtitle != null) {
			float sec = player.getCurrentPosition() / 1000.0f;
			String subs = subtitle.getSubText(sec);
			Logger.setText(subs);
		}
	}

	public void setSubtitle(Subtitle subtitle) {
		this.subtitle = subtitle;
	}

	public void setChapterData(ChapterData chapterData) {
		this.chapterData = chapterData;
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
}
