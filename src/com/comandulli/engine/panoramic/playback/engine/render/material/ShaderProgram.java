package com.comandulli.engine.panoramic.playback.engine.render.material;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderProgram {

	private final int vertexShaderHandle;
	private final int fragmentShaderHandle;
	private int programHandle;

	public ShaderProgram(String vertexShaderSource, String fragmentShaderSource) {
		this.vertexShaderHandle = compileShader(vertexShaderSource, GLES20.GL_VERTEX_SHADER);
		this.fragmentShaderHandle = compileShader(fragmentShaderSource, GLES20.GL_FRAGMENT_SHADER);
	}

	public boolean compiled() {
        return vertexShaderHandle != 0 && fragmentShaderHandle != 0;
	}

	public void init(String[] attributes) {
		this.programHandle = linkProgram(vertexShaderHandle, fragmentShaderHandle, attributes);
	}

	public int getProgramHandle() {
		return programHandle;
	}

	private int compileShader(String shaderSource, int shaderType) {
		int shaderHandle = GLES20.glCreateShader(shaderType);
		if (shaderHandle != 0) {
			// pass the source code
			GLES20.glShaderSource(shaderHandle, shaderSource);
			// compile
			GLES20.glCompileShader(shaderHandle);
			// compilation status
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			if (compileStatus[0] == 0) {
				Log.d("GL Shader", GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}
		if (shaderHandle == 0) {
			String shaderError = "Error compiling ";
			if (shaderType == GLES20.GL_VERTEX_SHADER) {
				shaderError += " vertex shader.";
			} else {
				shaderError += " fragment shader.";
			}
			throw new RuntimeException(shaderError);
		}
		return shaderHandle;
	}

	private int linkProgram(int vertexHandle, int fragmentHandle, String[] attributes) {
		int programHandle = GLES20.glCreateProgram();
		if (programHandle != 0) {
			// attach shaders to program
			GLES20.glAttachShader(programHandle, vertexHandle);
			GLES20.glAttachShader(programHandle, fragmentHandle);
			// bind attributes
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
			}
			// link shaders
			GLES20.glLinkProgram(programHandle);
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] == 0) {
				Log.d("GL Shader", GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		if (programHandle == 0) {
			throw new RuntimeException("Error Creating program.");
		}

		return programHandle;
	}

}