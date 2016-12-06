package com.comandulli.engine.panoramic.playback.engine.render.material;

import java.io.IOException;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.view.Surface;

public class VideoTexture extends Texture {

	private SurfaceTexture videoTexture;

    @Override
	public void load() {
		int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);
		this.textureDataHandle = textureHandle[0];
		if (textureDataHandle == 0) {
			throw new RuntimeException("Error loading texture.");
		}
		textureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureDataHandle);

		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		videoTexture = new SurfaceTexture(textureDataHandle);
	}

	public void setVideo(MediaPlayer player, boolean loop) {
		try {
			Surface surface = new Surface(videoTexture);
			player.setSurface(surface);
			player.setLooping(loop);
			surface.release();
			if (!player.isPlaying()) {
				try {
					player.prepare();
				} catch (IllegalStateException ignored) {
				}
				player.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update() {
		videoTexture.updateTexImage();
		super.update();
	}

}
