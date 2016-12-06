package com.comandulli.engine.panoramic.playback.engine.render.material;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public abstract class Shader {

	public ShaderProgram shaderProgram;

	private final float[] mvpMatrix = new float[16];

	private String vertexShader;
	private String fragmentShader;

	private final List<String> attributes;

	private static final int FLOAT_BUFFER_SIZE = 1;
	private static final int VECTOR2_BUFFER_SIZE = 2;
	private static final int VECTOR3_BUFFER_SIZE = 3;
	private static final int VECTOR4_BUFFER_SIZE = 4;

	private int programHandle;
	public String name;

	public int customAttributes;

	public boolean depthTest = true;
	public boolean alphaBlend;
	public int sFactor;
	public int dFactor;

	public Shader() {
		this.attributes = new ArrayList<>();
	}

	public void setProgram(String vertexShader, String fragmentShader) {
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;
	}

	public void load() {
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.compiled()) {
			throw new RuntimeException("Shader program didn't went through.");
		}

		String[] attributeList = new String[attributes.size()];
		for (int i = 0; i < attributes.size(); i++) {
			String attribute = attributes.get(i);
			attributeList[i] = attribute;
		}

		shader.init(attributeList);
		shaderProgram = shader;
	}

	public void begin() {
		if (depthTest) {
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		} else {
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
		if (alphaBlend) {
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(sFactor, dFactor);
		} else {
			GLES20.glDisable(GLES20.GL_BLEND);
		}
		programHandle = getProgramHandle();
		GLES20.glUseProgram(programHandle);
	}

	public void bindMVP(int handle, float[] model, float[] projection, float[] view) {
		Matrix.multiplyMM(mvpMatrix, 0, view, 0, model, 0);
		Matrix.multiplyMM(mvpMatrix, 0, projection, 0, mvpMatrix, 0);
		GLES20.glUniformMatrix4fv(handle, 1, false, mvpMatrix, 0);
	}

	public abstract void render(Renderer renderer, Camera camera);

	@SuppressWarnings("EmptyMethod")
    public void end() {

	}

	public int getProgramHandle() {
		return shaderProgram.getProgramHandle();
	}

	public void bindVector2(int handle, float[] array) {
		GLES20.glUniform2fv(handle, 1, array, 0);
	}

	public void bindVector3(int handle, float[] array) {
		GLES20.glUniform3fv(handle, 1, array, 0);
	}

	public void bindVector4(int handle, float[] array) {
		GLES20.glUniform4fv(handle, 1, array, 0);
	}

	public void bindTexture(int handle, Texture texture) {
		texture.bind(GLES20.GL_TEXTURE0, handle);
	}

	public void bindFloatBuffer(int handle, FloatBuffer buffer) {
		buffer.position(0);
		GLES20.glVertexAttribPointer(handle, FLOAT_BUFFER_SIZE, GLES20.GL_FLOAT, false, 0, buffer);
		GLES20.glEnableVertexAttribArray(handle);
	}

	public void bindVector2Buffer(int handle, FloatBuffer buffer) {
		buffer.position(0);
		GLES20.glVertexAttribPointer(handle, VECTOR2_BUFFER_SIZE, GLES20.GL_FLOAT, false, 0, buffer);
		GLES20.glEnableVertexAttribArray(handle);
	}

	public void bindVector3Buffer(int handle, FloatBuffer buffer) {
		buffer.position(0);
		GLES20.glVertexAttribPointer(handle, VECTOR3_BUFFER_SIZE, GLES20.GL_FLOAT, false, 0, buffer);
		GLES20.glEnableVertexAttribArray(handle);
	}

	public void bindVector4Buffer(int handle, FloatBuffer buffer) {
		buffer.position(0);
		GLES20.glVertexAttribPointer(handle, VECTOR4_BUFFER_SIZE, GLES20.GL_FLOAT, false, 0, buffer);
		GLES20.glEnableVertexAttribArray(handle);
	}

	public void unbindBuffer(int handle) {
		GLES20.glDisableVertexAttribArray(handle);
	}

	public int addUniform(String name) {
		return GLES20.glGetUniformLocation(programHandle, name);
	}

	public int addAttribute(String name) {
		int handle = attributes.size();
		attributes.add(name);
		return handle;
	}

}
