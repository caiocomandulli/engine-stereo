package com.comandulli.engine.panoramic.playback.engine.render.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;

public class TextRenderer extends Renderer {

	private static final float RI_TEXT_UV_BOX_WIDTH = 0.125f;
	private static final float RI_TEXT_WIDTH = 32.0f;
	private static final float RI_TEXT_SPACESIZE = 10.0f;

	private final float[] vecs;
	private final float[] uvs;

	private int index_vecs;
	private int index_uvs;

	private float uniformscale = 0.01f;

	private static final int[] l_size = { 36, 29, 30, 34, 25, 25, 34, 33, 11, 20, 31, 24, 48, 35, 39, 29, 42, 31, 27, 31, 34, 35, 46, 35, 31, 27, 30, 26, 28, 26, 31, 28, 28, 28, 29, 29, 14, 24, 30, 18, 26, 14, 14, 14, 25, 28, 31, 0, 0, 38, 39, 12, 36, 34, 0, 0, 0, 38, 0, 0, 0, 0, 0, 0 };

	public final String text;

	public enum Alignment {
		FLOAT_LEFT, FLOAT_RIGHT, CENTER
	}

	public TextRenderer(String text, Material material) {
		this(text, 0.01f, Alignment.FLOAT_LEFT, material);

	}

	public TextRenderer(String text, float scale, Alignment alignment, Material material) {
		super(material);
		this.uniformscale = scale;
		this.text = text;
		index_vecs = 0;
		index_uvs = 0;
		// Get the total amount of characters
		int charCount = text.length();
		// Create the arrays we need with the correct size.
		vecs = new float[charCount * 18];
		uvs = new float[charCount * 12];
		float offset = 0;
		switch (alignment) {
		case FLOAT_RIGHT:
			for (int i = 0; i < text.length(); i++) {
				int index = convertCharToIndex((int) text.charAt(i));
				float size;
				if (index == -1) {
					size = RI_TEXT_SPACESIZE;
				} else {
					size = l_size[index];
				}
				offset += size / 2 * uniformscale;
			}
			break;
		case CENTER:
			for (int i = 0; i < text.length(); i++) {
				int index = convertCharToIndex((int) text.charAt(i));
				float size;
				if (index == -1) {
					size = RI_TEXT_SPACESIZE;
				} else {
					size = l_size[index];
				}
				offset += size / 2 * uniformscale;
			}
			offset /= 2;
			break;
		case FLOAT_LEFT:
			break;
		}
		convertTextToTriangleInfo(text, offset);
		pushToBuffers();
	}

	private void AddCharRenderInformation(float[] vec, float[] uv) {
		// We should add the vec, translating the indices to our saved vector
        for (float aVec : vec) {
            vecs[index_vecs] = aVec;
            index_vecs++;
        }

		// We should add the uvs
        for (float anUv : uv) {
            uvs[index_uvs] = anUv;
            index_uvs++;
        }
	}

	private void pushToBuffers() {
		this.positionBuffer = ByteBuffer.allocateDirect(vecs.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.positionBuffer.put(vecs);
		this.positionBuffer.position(0);

		this.textureCoordinateBuffer = ByteBuffer.allocateDirect(uvs.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.textureCoordinateBuffer.put(uvs);
		this.textureCoordinateBuffer.position(0);
	}

	private int convertCharToIndex(int c_val) {
		int index = -1;

		// Retrieve the index
		if (c_val > 64 && c_val < 91) // A-Z
        {
            index = c_val - 65;
        } else if (c_val > 96 && c_val < 123) // a-z
        {
            index = c_val - 97;
        } else if (c_val > 47 && c_val < 58) // 0-9
        {
            index = c_val - 48 + 26;
        } else if (c_val == 43) // +
        {
            index = 38;
        } else if (c_val == 45) // -
        {
            index = 39;
        } else if (c_val == 33) // !
        {
            index = 36;
        } else if (c_val == 63) // ?
        {
            index = 37;
        } else if (c_val == 61) // =
        {
            index = 40;
        } else if (c_val == 58) // :
        {
            index = 41;
        } else if (c_val == 46) // .
        {
            index = 42;
        } else if (c_val == 44) // ,
        {
            index = 43;
        } else if (c_val == 42) // *
        {
            index = 44;
        } else if (c_val == 36) // $
        {
            index = 45;
        }

		return index;
	}

	private void convertTextToTriangleInfo(String text, float offset) {
		// Get attributes from text object
		float x = offset;
		float y = 0;

		// Create
		for (int i = 0; i < text.length(); i++) {
			// get ascii value
			char c = text.charAt(i);
			int c_val = (int) c;

			int index = convertCharToIndex(c_val);

			if (index == -1) {
				// unknown character, we will add a space for it to be safe.
				x += -RI_TEXT_SPACESIZE * uniformscale;
				continue;
			}

			// Calculate the uv parts
			int row = index / 8;
			int col = index % 8;

			float v = row * RI_TEXT_UV_BOX_WIDTH;
			float v2 = v + RI_TEXT_UV_BOX_WIDTH;
			float u = col * RI_TEXT_UV_BOX_WIDTH;
			float u2 = u + RI_TEXT_UV_BOX_WIDTH;

			// Creating the triangle information
			float[] vec = new float[18];
			float[] uv = new float[12];

			float w = -(RI_TEXT_WIDTH * uniformscale);
			float h = RI_TEXT_WIDTH * uniformscale;
			float z = 0.0f;
			Vector3 vert0 = new Vector3(x, y + h, z);
			Vector3 vert1 = new Vector3(x, y, z);
			Vector3 vert2 = new Vector3(x + w, y, z);
			Vector3 vert3 = new Vector3(x + w, y + h, z);

			// vert 0
			vec[0] = vert0.x;
			vec[1] = vert0.y;
			vec[2] = vert0.z;

			// vert 1
			vec[3] = vert1.x;
			vec[4] = vert1.y;
			vec[5] = vert1.z;

			// vert 2
			vec[6] = vert2.x;
			vec[7] = vert2.y;
			vec[8] = vert2.z;

			// vert 0
			vec[9] = vert0.x;
			vec[10] = vert0.y;
			vec[11] = vert0.z;

			// vert 2
			vec[12] = vert2.x;
			vec[13] = vert2.y;
			vec[14] = vert2.z;

			// vert 3
			vec[15] = vert3.x;
			vec[16] = vert3.y;
			vec[17] = vert3.z;

			// 0.001f = texture bleeding hack/fix
			float tb = 0.001f * 0;
			Vector2 uv0 = new Vector2(u + tb, v + tb);
			Vector2 uv1 = new Vector2(u + tb, v2 - tb);
			Vector2 uv2 = new Vector2(u2 - tb, v2 - tb);
			Vector2 uv3 = new Vector2(u2 - tb, v + tb);
			// uv 0
			uv[0] = uv0.x;
			uv[1] = uv0.y;

			// uv 1
			uv[2] = uv1.x;
			uv[3] = uv1.y;

			// uv 2
			uv[4] = uv2.x;
			uv[5] = uv2.y;

			// uv 0
			uv[6] = uv0.x;
			uv[7] = uv0.y;

			// uv 2
			uv[8] = uv2.x;
			uv[9] = uv2.y;

			// uv 3
			uv[10] = uv3.x;
			uv[11] = uv3.y;

			bufferSize += 6;

			// Add our triangle information to our collection for 1 render call.
			AddCharRenderInformation(vec, uv);

			// Calculate the new position
			x -= l_size[index] / 2 * uniformscale;
		}
	}

}
