package com.comandulli.engine.panoramic.playback.entity;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.render.camera.StereoscopicCamera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.material.Texture;
import com.comandulli.engine.panoramic.playback.engine.render.material.VideoTexture;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

@SuppressWarnings("FieldCanBeLocal")
public class VideoPlayer extends Entity {

	public static final int MODE_FLAT = 0;
	public static final int MODE_OVER_UNDER = 1;
	public static final int MODE_LEFT_RIGHT = 2;

	private final StereoscopicCamera camera;
	private final MediaPlayer player;
	private Texture videoTexture;
	private final int videoMode;

    private Renderer leftSphere;
    private Renderer rightSphere;

    private boolean loop = true;

	private final int movieTexture;
	private final int movieShader;
	private final int mesh;

	public VideoPlayer(String name, MediaPlayer player, StereoscopicCamera camera, int videoMode, int movieTexture, int movieShader, int mesh) {
		super(name);
		this.player = player;
		this.camera = camera;
		this.videoMode = videoMode;
		this.movieTexture = movieTexture;
		this.movieShader = movieShader;
		this.mesh = mesh;
	}

	public void setCallback(final Runnable callback, boolean loop) {
		this.loop = loop;
		player.setLooping(loop);
		if (!loop) {
			player.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					callback.run();
				}
			});
		} else {
			player.setOnCompletionListener(null);
		}
	}

	@Override
	public void start() {
		super.start();
		videoTexture = Assets.getTexture(movieTexture);
		((VideoTexture) videoTexture).setVideo(player, loop);

        this.leftSphere = new MeshRenderer(mesh, new Material(movieTexture, movieShader));
        this.leftSphere.flags |= Renderer.FLAG_EXCLUDE_RIGHT;

        this.rightSphere = new MeshRenderer(mesh, new Material(movieTexture, movieShader));
        this.rightSphere.flags |= Renderer.FLAG_EXCLUDE_LEFT;

		if (videoMode == MODE_OVER_UNDER) {
            this.leftSphere.material.textureTiling = new Vector2(1.0f, 0.5f);
            this.rightSphere.material.textureTiling = new Vector2(1.0f, 0.5f);
            this.rightSphere.material.textureOffset = new Vector2(0.0f, 0.5f);
		} else if (videoMode == MODE_LEFT_RIGHT) {
            this.leftSphere.material.textureTiling = new Vector2(0.5f, 1.0f);
            this.rightSphere.material.textureTiling = new Vector2(0.5f, 1.0f);
            this.rightSphere.material.textureOffset = new Vector2(0.5f, 0.0f);
		}

		Entity leftEntity = new Entity("LeftSphere");
		Entity rightEntity = new Entity("RightSphere");
		leftEntity.addComponent(leftSphere);
		rightEntity.addComponent(rightSphere);
        leftEntity.renderer = leftSphere;
        rightEntity.renderer = rightSphere;
        camera.addToRenderQueue(leftEntity);
        camera.addToRenderQueue(rightEntity);
	}

	@Override
	public void update() {
		super.update();
		videoTexture.update();
        this.leftSphere.modelMatrix = transform.getModelMatrix();
        this.rightSphere.modelMatrix = transform.getModelMatrix();
	}
	
	@Override
	public void pause() {
		player.pause();
	}
	
	@Override
	public void resume() {
		player.start();
	}
	
	@Override
	public void destroy() {
		super.destroy();
		if (player.isPlaying()) {
			player.stop();
		}
		player.release();
	}

}
