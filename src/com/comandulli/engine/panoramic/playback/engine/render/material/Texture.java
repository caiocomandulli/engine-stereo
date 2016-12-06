package com.comandulli.engine.panoramic.playback.engine.render.material;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;

public class Texture {

	public int textureDataHandle;
	protected int textureTarget;

	private String source;

	protected Texture() {
	}

	public Texture(String source) {
		this.source = source;
	}

	public void load() {
		final int[] textureHandle = new int[1];
		GLES20.glGenTextures(1, textureHandle, 0);
		if (textureHandle[0] != 0) {
			InputStream is = Assets.openInputStream(source);
			final Bitmap bitmap = BitmapFactory.decodeStream(is);
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			//
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}
		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}
		textureDataHandle = textureHandle[0];
		textureTarget = GLES20.GL_TEXTURE_2D;
	}

	public void bind(int target, int handle) {
		GLES20.glActiveTexture(target);
		GLES20.glBindTexture(textureTarget, textureDataHandle);
		GLES20.glUniform1i(handle, 0);
	}

	public void update() {
	}

}
