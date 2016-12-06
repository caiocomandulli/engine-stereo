package com.comandulli.engine.panoramic.playback.engine.render.material;

import android.util.SparseArray;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;

public class Material {

	public Texture mainTexture;
	public Texture normalMap;
	public final int shader;

	public Vector2 textureTiling;
	public Vector2 textureOffset;

	public Color color;

	private final SparseArray<Object> attributes;
	
	public Material(int texture, int shader, Color color) {
		this(texture, shader);
		this.color = color;
	}
	
	public Material(int shader, Color color) {
		this(shader);
		this.color = color;
	}

	public Material(int texture, int shader) {
		this(shader);
		this.mainTexture = Assets.getTexture(texture);
	}
	
	public Material(int shader) {
		this.shader = shader;

		this.textureTiling = new Vector2(1.0f, 1.0f);
		this.textureOffset = new Vector2(0.0f, 0.0f);
		this.color = Color.WHITE;
		this.attributes = new SparseArray<>();
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setNormalMapTexture(int texture) {
		this.normalMap = Assets.getTexture(texture);
	}

	public void setTextureTiling(float xTiling, float yTiling, int xOffset, int yOffset) {
		this.textureTiling = new Vector2(xTiling, yTiling);
		this.textureOffset = new Vector2(xOffset, yOffset);
	}

	public void setTextureValue(int name, int texture) {
		attributes.append(name, Assets.getTexture(texture));
	}

	public void setFloatValue(int name, float[] value) {
		attributes.append(name, value);
	}

	public Texture getTextureValue(int name) {
		return (Texture) attributes.get(name);
	}

	public float[] getFloatValue(int name) {
		return (float[]) attributes.get(name);
	}

}
