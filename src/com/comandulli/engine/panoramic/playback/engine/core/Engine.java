package com.comandulli.engine.panoramic.playback.engine.core;

import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.audio.Audio;
import com.comandulli.engine.panoramic.playback.engine.exception.EngineRunningException;
import com.comandulli.engine.panoramic.playback.engine.input.Input;
import com.comandulli.engine.panoramic.playback.engine.physics.Physics;
import com.comandulli.engine.panoramic.playback.engine.render.SurfaceRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;

import android.app.Activity;
import android.view.View;

public abstract class Engine {

	protected static Engine runningEngine;

	protected SurfaceRenderer renderer;

	private Scene scene;
	private Scene nextScene;

	protected Activity context;

	private boolean isRunning;
	private boolean isUpdating;

	private int width;
	private int height;

	private Runnable runAfterHalt;

    public Engine(Activity context, Scene scene) {
		if (runningEngine != null) {
			throw new EngineRunningException();
		}
		runningEngine = this;
		this.context = context;
		this.scene = scene;
		// Resources
		Assets.init(context);
		// Input
		Input.init(context);
		// Physics
		Physics.init();
		// Audio
		Audio.init();
		// Rendering
		renderer = new SurfaceRenderer(context, this);
		// Project Assets
		loadAssets();
	}

	public abstract void loadAssets();

	public void start() {
        if (isRunning) {
            throw new EngineRunningException();
        }
		isRunning = true;
		Time.start();
		Assets.load();
		scene.start();
	}

	public void update() {
		if (isRunning) {
			isUpdating = true;
			if (nextScene != null) {
				if (this.scene != null) {
					this.scene.finishNow();
				}
				this.scene = nextScene;
				this.scene.start();
				this.scene.adjustViewport(width, height);
				this.nextScene = null;
			}
			Time.update();
			Input.update();
			Audio.update();
			scene.update();
			isUpdating = false;
		} else {
			if (runAfterHalt != null) {
				runAfterHalt.run();
				runAfterHalt = null;
			}
		}
	}

	public void render() {
		if (isRunning) {
			List<Camera> cameras = scene.getCameras();
			for (Camera camera : cameras) {
				if (camera.isEnabled()) {
					camera.render();
					if (Physics.debugRenderEnabled) {
						Physics.render(camera);
					}
				}
			}
			scene.postRender();
		}
	}

	public void finish() {
		isRunning = false;
		if (isUpdating) {
			runAfterHalt = new Runnable() {
				@Override
				public void run() {
					finishNow();
				}
			};
		} else {
			finishNow();
		}
	}
	
	public void finishNow() {
		scene.finishNow();
		renderer.pause();
		Audio.pause();
		Input.pause();
		runningEngine = null;
	}

	public void adjustViewport(int width, int height) {
		this.width = width;
		this.height = height;
		scene.adjustViewport(width, height);
	}

	public void resume() {
		if (isRunning) {
			scene.resume();
			Input.resume();
			Audio.resume();
			renderer.resume();
		}
	}

	public void pause() {
		if (isRunning) {
			scene.pause();
			renderer.pause();
			Audio.pause();
			Input.pause();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public View getView() {
		return renderer.getView();
	}

	// STATIC //

	public static void setScene(Scene scene) {
		runningEngine.nextScene = scene;
	}

	public static Activity getContext() {
		return runningEngine.context;
	}

	public static Scene getScene() {
		return runningEngine.scene;
	}

	public static View getCurrentView() {
		return runningEngine.renderer.getView();
	}

	public static int getHeight() {
		return runningEngine.height;
	}

	public static int getWidth() {
		return runningEngine.width;
	}

	public static void end() {
		if (runningEngine != null) {
			runningEngine.finish();
		}
	}

}
