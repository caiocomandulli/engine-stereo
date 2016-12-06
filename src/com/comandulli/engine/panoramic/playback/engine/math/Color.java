package com.comandulli.engine.panoramic.playback.engine.math;

public class Color {

	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);

	public static final Color RED = new Color(1.0f, 0.0f, 0.0f, 1.0f);
	public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
	public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
	public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f, 1.0f);

	public final float r;
	public final float g;
	public final float b;
	public final float a;

	public Color() {
		this(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public float[] toArray() {
		final float[] array = new float[4];
		array[0] = r;
		array[1] = g;
		array[2] = b;
		array[3] = a;
		return array;
	}

	public Color copy() {
		return new Color(r, g, b, a);
	}

}
