package com.comandulli.engine.panoramic.playback.shader;

import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import android.opengl.GLES20;

public class ColorShader extends Shader {

	private int u_mvpMatrixHandle;
	private int u_colorHandle;

	private final int a_positionHandle;

	public ColorShader() {
		super();
		a_positionHandle = addAttribute("a_Position");
	}

	@Override
	public void begin() {
		super.begin();
		u_mvpMatrixHandle = addUniform("u_MVPMatrix");
		u_colorHandle = addUniform("u_Color");
	}

	@Override
	public void render(Renderer renderer, Camera camera) {
		bindVector4(u_colorHandle, renderer.material.color.toArray());
		bindMVP(u_mvpMatrixHandle, renderer.modelMatrix, camera.projectionMatrix, camera.viewMatrix);
		bindVector3Buffer(a_positionHandle, renderer.positionBuffer);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, renderer.bufferSize);
	}

}
