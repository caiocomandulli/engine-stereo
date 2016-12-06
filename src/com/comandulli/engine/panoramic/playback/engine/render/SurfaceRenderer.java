package com.comandulli.engine.panoramic.playback.engine.render;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.view.View;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;

public class SurfaceRenderer implements Renderer {

	private final SurfaceRendererView surfaceView;

	public final Engine engine;

	public SurfaceRenderer(Context context, Engine engine) {
		this.engine = engine;
		surfaceView = new SurfaceRendererView(context);
		surfaceView.setEGLContextClientVersion(2);
		surfaceView.setRenderer(this);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		engine.start();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		engine.adjustViewport(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		engine.update();
		engine.render();
	}

	public void resume() {
		// TODO: must resolve egl context lost
		// surfaceView.onResume();
	}

	public void pause() {
		// surfaceView.onPause();
	}

	public View getView() {
		return surfaceView;
	}
}
