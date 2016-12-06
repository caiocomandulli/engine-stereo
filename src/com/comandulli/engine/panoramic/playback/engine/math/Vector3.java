package com.comandulli.engine.panoramic.playback.engine.math;

public class Vector3 {

	public float x;
	public float y;
	public float z;

	public static final Vector3 SCALE_ONE = new Vector3(1.0f, 1.0f, 1.0f);
	public static final Vector3 DIRECTION_UP = new Vector3(0.0f, 1.0f, 0.0f);
	public static final Vector3 DIRECTION_FORWARD = new Vector3(0.0f, 0.0f, 1.0f);
	public static final Vector3 DIRECTION_LEFT = new Vector3(1.0f, 0.0f, 0.0f);

	public Vector3() {
		this(0.0f, 0.0f, 0.0f);
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public float length() {
		return (float) Math.sqrt(squareLength());
	}
	
	public static float lengthOf(float x, float y, float z) {
		return (float) Math.sqrt(squareLengthOf(x, y, z));
	}

	public float squareLength() {
		return x * x + y * y + z * z;
	}
	
	public static float squareLengthOf(float x, float y, float z) {
		return x * x + y * y + z * z;
	}

	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}

	public Vector3 copy() {
		return new Vector3(x, y, z);
	}

	public float[] toArray() {
		final float[] array = new float[3];
		array[0] = x;
		array[1] = y;
		array[2] = z;
		return array;
	}

	public Vector3 normalize() {
		final float len2 = this.squareLength();
		if (len2 == 0.0f || len2 == 1.0f) {
            return this;
        }
		return this.scale(1.0f / (float) Math.sqrt(len2));
	}

	public static Vector3 normalized(Vector3 vector) {
		final float len2 = vector.squareLength();
		if (len2 == 0.0f || len2 == 1.0f) {
            return vector;
        }
		return scaled(vector, 1.0f / (float) Math.sqrt(len2));
	}

	public Vector3 scale(Vector3 scale) {
		this.x *= scale.x;
		this.y *= scale.y;
		this.z *= scale.z;
		return this;
	}

	public static Vector3 scaled(Vector3 target, Vector3 scale) {
		return target.copy().scale(scale);
	}

	public Vector3 scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		return this;
	}

	public static Vector3 scaled(Vector3 target, float scale) {
		return target.copy().scale(scale);
	}

	public static float dot(Vector3 first, Vector3 second) {
		return first.x * second.x + first.y * second.y + first.z * second.z;
	}

	public static Vector3 cross(Vector3 first, Vector3 second) {
		float crossX = first.y * second.z - first.z * second.y;
		float crossY = first.z * second.x - first.x * second.z;
		float crossZ = first.x * second.y - first.y * second.x;
		return new Vector3(crossX, crossY, crossZ);
	}

	public Vector3 add(Vector3 add) {
        this.x += add.x;
        this.y += add.y;
        this.z += add.z;
		return this;
	}

	public static Vector3 sum(Vector3 target, Vector3 add) {
		return new Vector3(target.x + add.x, target.y + add.y, target.z + add.z);
	}

	public static Vector3 deltaVector(Vector3 position, Vector3 origin) {
		return new Vector3(position.x - origin.x, position.y - origin.y, position.z - origin.z);
	}

	public static Vector3 rotated(Vector3 vector, final float[] rotation) {
		return vector.copy().rotate(rotation);
	}

	public Vector3 rotate(final float[] rotation) {
		return set(x * rotation[0] + y * rotation[4] + z * rotation[8], x * rotation[1] + y * rotation[5] + z * rotation[9], x * rotation[2] + y * rotation[6] + z * rotation[10]);
	}

	public Vector3 rotateAround(Vector3 point, Quaternion rotation) {
		Vector3 dir = this.copy();
		dir.rotate(rotation.toMatrix());
		return sum(point, dir);
	}

	public Vector3 rotatedAround(Vector3 target, Vector3 point, Quaternion rotation) {
		return target.copy().rotateAround(point, rotation);
	}

	public Vector3 lerp(final Vector3 target, float t) {
		x += t * (target.x - x);
		y += t * (target.y - y);
		z += t * (target.z - z);
		return this;
	}

	public static Vector3 lerped(final Vector3 source, final Vector3 target, float t) {
		float newX = source.x + t * (target.x - source.x);
		float newY = source.y + t * (target.y - source.y);
		float newZ = source.z + t * (target.z - source.z);
		return new Vector3(newX, newY, newZ);
	}

}
