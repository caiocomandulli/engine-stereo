package com.comandulli.engine.panoramic.playback.engine.math;

public class Vector2 {

    public final float x;
    public final float y;

    public Vector2() {
        this(0.0f, 0.0f);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float[] toArray() {
        final float[] array = new float[2];
        array[0] = x;
        array[1] = y;
        return array;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }
}
