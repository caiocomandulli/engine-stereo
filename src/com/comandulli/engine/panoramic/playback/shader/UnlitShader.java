package com.comandulli.engine.panoramic.playback.shader;

import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import android.opengl.GLES20;

public class UnlitShader extends Shader {

	private int u_mvpMatrixHandle;
	private int u_textureHandle;
	private int u_tiling;
	private int u_offset;
	private int u_colorHandle;

	private final int a_positionHandle;
	private final int a_textureCoordinateHandle;

	public UnlitShader() {
		super();
		a_positionHandle = addAttribute("a_Position");
		a_textureCoordinateHandle = addAttribute("a_TexCoordinate");
		this.depthTest = false;
		this.alphaBlend = true;
		this.sFactor = GLES20.GL_ONE;
		this.dFactor = GLES20.GL_ONE_MINUS_SRC_ALPHA;
	}

	@Override
	public void begin() {
		super.begin();
		u_mvpMatrixHandle = addUniform("u_MVPMatrix");
		u_textureHandle = addUniform("u_Texture");
		u_tiling = addUniform("u_tiling");
		u_offset = addUniform("u_offset");
		u_colorHandle = addUniform("u_Color");
	}

	@Override
	public void render(Renderer renderer, Camera camera) {
		bindVector2(u_tiling, renderer.material.textureTiling.toArray());
		bindVector2(u_offset, renderer.material.textureOffset.toArray());
		bindVector4(u_colorHandle, renderer.material.color.toArray());
		bindTexture(u_textureHandle, renderer.material.mainTexture);
		bindMVP(u_mvpMatrixHandle, renderer.modelMatrix, camera.projectionMatrix, camera.viewMatrix);
		bindVector3Buffer(a_positionHandle, renderer.positionBuffer);
		bindVector2Buffer(a_textureCoordinateHandle, renderer.textureCoordinateBuffer);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, renderer.bufferSize);
	}

}
