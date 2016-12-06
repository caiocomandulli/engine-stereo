package com.comandulli.engine.panoramic.playback.engine.render.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Mesh {

	public static final int BYTES_PER_FLOAT = 4;
	public static final int POSITION_DATA_SIZE = 3;
	public static final int TEXTURE_COORDINATE_DATA_SIZE = 2;
	public static final int NORMAL_DATA_SIZE = 3;
	public static final int VERTS_IN_FACE = 3;

	public final FloatBuffer positionBuffer;
	public FloatBuffer textureCoordinateBuffer;
	public FloatBuffer normalBuffer;

	public final int bufferSize;

	public final boolean hasTexture;
	public final boolean hasNormals;

	public Mesh(int faceSize, int positionBufferSize, int textureBufferSize, int normalBufferSize) {
		hasTexture = textureBufferSize > 0;
		hasNormals = normalBufferSize > 0;

		this.bufferSize = faceSize * VERTS_IN_FACE;
		this.positionBuffer = ByteBuffer.allocateDirect(positionBufferSize * POSITION_DATA_SIZE * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		if (hasTexture) {
            this.textureCoordinateBuffer = ByteBuffer.allocateDirect(textureBufferSize * TEXTURE_COORDINATE_DATA_SIZE * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
		if (hasNormals) {
            this.normalBuffer = ByteBuffer.allocateDirect(normalBufferSize * NORMAL_DATA_SIZE * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        }
	}
}
