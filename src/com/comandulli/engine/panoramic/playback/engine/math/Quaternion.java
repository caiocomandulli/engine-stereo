package com.comandulli.engine.panoramic.playback.engine.math;

public class Quaternion {

	public float w;
	public float x;
	public float y;
	public float z;
	public static final float EPSILON = 0.000001f;

	public Quaternion() {
		this.set(1.0f, 0.0f, 0.0f, 0.0f);
	}

	public Quaternion(float w, float x, float y, float z) {
		this.set(w, x, y, z);
	}

	public Quaternion(Vector3 euler) {
		this.fromEulerAngles(euler.x, euler.y, euler.z);
	}

	public Quaternion(float pitch, float yaw, float roll) {
		this.fromEulerAngles(pitch, yaw, roll);
	}

	public Quaternion(Vector3 axis, float angle) {
		this.fromAngleAxis(axis, angle);
	}

	public Quaternion fromEulerAngles(float pitch, float yaw, float roll) {
		final float hr = roll * 0.5f;
		final float shr = (float) Math.sin(hr);
		final float chr = (float) Math.cos(hr);
		final float hp = pitch * 0.5f;
		final float shp = (float) Math.sin(hp);
		final float chp = (float) Math.cos(hp);
		final float hy = yaw * 0.5f;
		final float shy = (float) Math.sin(hy);
		final float chy = (float) Math.cos(hy);
		final float chy_shp = chy * shp;
		final float shy_chp = shy * chp;
		final float chy_chp = chy * chp;
		final float shy_shp = shy * shp;
		x = chy_shp * chr + shy_chp * shr;
		y = shy_chp * chr - chy_shp * shr;
		z = chy_chp * shr - shy_shp * chr;
		w = chy_chp * chr + shy_shp * shr;
		return this;
	}

	public float getAxisAngleRad(Vector3 axis) {
		// if w>1 acos and sqrt will produce errors, this cant happen if
		// quaternion is normalised
		if (this.w > 1) {
            this.normalize();
        }
		float angle = (float) (2.0 * Math.acos(this.w));
		// assuming quaternion normalised then w is less than 1, so term always
		// positive.
		double s = Math.sqrt(1 - this.w * this.w);
		// test to avoid divide by zero, s is always positive due to sqrt
		// if s close to zero then direction of axis not important
		if (s < 0.000001f) {
			// if it is important that axis is normalised then replace with x=1;
			// y=z=0;
			axis.x = 1.0f;
			axis.y = 0.0f;
			axis.z = 0.0f;
		} else {
			axis.x = (float) (this.x / s); // normalise axis
			axis.y = (float) (this.y / s);
			axis.z = (float) (this.z / s);
		}

		return angle;
	}

	public float getAxisAngleDegrees(Vector3 axis) {
		return getAxisAngleRad(axis) * (float) (180 / Math.PI);
	}

	public Quaternion fromAngleAxis(Vector3 axis, float angle) {
        float l_sin = (float) Math.sin(angle / 2.0f);
		float l_cos = (float) Math.cos(angle / 2.0f);
		return this.set(l_cos, axis.x * l_sin, axis.y * l_sin, axis.z * l_sin).normalize();
	}

	public Quaternion normalize() {
		float len = this.length();
		if (len < EPSILON) {
            return this.set(1.0f, 0.0f, 0.0f, 0.0f);
        }
		w /= len;
		x /= len;
		y /= len;
		z /= len;
		return this;
	}

	public static Quaternion normalized(Quaternion quaternion) {
		float len = quaternion.length();
		if (len < EPSILON) {
            return new Quaternion(1.0f, 0.0f, 0.0f, 0.0f);
        }
		return new Quaternion(quaternion.w / len, quaternion.x / len, quaternion.y / len, quaternion.z / len);
	}

	public static Quaternion multiply(Quaternion first, Quaternion second) {
		final float newX = first.w * second.x + first.x * second.w + first.y * second.z - first.z * second.y;
		final float newY = first.w * second.y + first.y * second.w + first.z * second.x - first.x * second.z;
		final float newZ = first.w * second.z + first.z * second.w + first.x * second.y - first.y * second.x;
		final float newW = first.w * second.w - first.x * second.x - first.y * second.y - first.z * second.z;
		return new Quaternion(newW, newX, newY, newZ).normalize();
	}

	public Quaternion set(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Quaternion conjugate() {
		return new Quaternion(w, -x, -y, -z);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public Quaternion inverse() {
		float norm = x * x + y * y + z * z + w * w;
		if (norm < EPSILON) {
            return new Quaternion(1.0f, 0.0f, 0.0f, 0.0f);
        }
		return new Quaternion(w / norm, -x / norm, -y / norm, -z / norm);
	}

	public Vector3 getEulerVector() {
		float yaw = (float) Math.atan2(2.0f * (y * w + x * z), 1.0f - 2.0f * (y * y + x * x));
		float pitch = (float) Math.asin(Math.max(-1.0f, Math.min(1.0f, 2.0f * (w * x - z * y))));
		float roll = (float) Math.atan2(2.0f * (w * z + y * x), 1.0f - 2.0f * (x * x + z * z));
		return new Vector3(pitch, yaw, roll);
	}

	public float[] toArray() {
		float[] array = new float[4];
		array[0] = this.y;
		array[1] = -this.z;
		array[2] = -this.x;
		array[3] = this.w;
		return array;
	}

	public int getGimbalPole() {
		final float t = y * x + z * w;
		return t > 0.499f ? 1 : t < -0.499f ? -1 : 0;
	}

	public float getRollRad() {
		final int pole = getGimbalPole();
		return (float) (pole == 0 ? Math.atan2(2.0f * (w * z + y * x), 1.0f - 2.0f * (x * x + z * z)) : (float) pole * 2.0f * Math.atan2(y, w));
	}

	public float getPitchRad() {
		final int pole = getGimbalPole();
		return (float) (pole == 0 ? (float) Math.asin(Math.max(-1.0f, Math.min(1.0f, 2.0f * (w * x - z * y)))) : (float) pole * Math.PI * 0.5f);
	}

	public float getYawRad() {
		return (float) (getGimbalPole() == 0 ? Math.atan2(2.0f * (y * w + x * z), 1.0f - 2.0f * (y * y + x * x)) : 0.0f);
	}

	public Quaternion copy() {
		return new Quaternion(w, x, y, z);
	}

	public float getAngle() {
		return (float) (2 * Math.acos(w));
	}

	@Override
	public String toString() {
		return "[" + w + "," + x + "," + y + "," + z + "]";
	}

	public static Quaternion slerp(Quaternion start, Quaternion end, float alpha) {
		final float dot = start.x * end.x + start.y * end.y + start.z * end.z + start.w * end.w;
		float absDot = dot < 0.0f ? -dot : dot;

		// Set the first and second scale for the interpolation
		float scaleStart = 1 - alpha;
		float scaleEnd = alpha;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if (1 - absDot > 0.1) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final double angle = Math.acos(absDot);
			final double invSinTheta = 1.0f / Math.sin(angle);

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scaleStart = (float) (Math.sin((1 - alpha) * angle) * invSinTheta);
			scaleEnd = (float) (Math.sin(alpha * angle) * invSinTheta);
		}

		if (dot < 0.0f) {
            scaleEnd = -scaleEnd;
        }
		Quaternion slerp = new Quaternion();
		// Calculate the x, y, z and w values for the quaternion by using a
		// special form of linear interpolation for quaternions.
		slerp.x = scaleStart * start.x + scaleEnd * end.x;
		slerp.y = scaleStart * start.y + scaleEnd * end.y;
		slerp.z = scaleStart * start.z + scaleEnd * end.z;
		slerp.w = scaleStart * start.w + scaleEnd * end.w;

		// Return the interpolated quaternion
		return slerp;
	}

	public float[] toMatrix() {
		float[] matrix = new float[16];
		final float xx = x * x;
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yy = y * y;
		final float yz = y * z;
		final float yw = y * w;
		final float zz = z * z;
		final float zw = z * w;
		matrix[0] = 1 - 2 * (yy + zz);
		matrix[4] = 2 * (xy - zw);
		matrix[8] = 2 * (xz + yw);
		matrix[12] = 0;
		matrix[1] = 2 * (xy + zw);
		matrix[5] = 1 - 2 * (xx + zz);
		matrix[9] = 2 * (yz - xw);
		matrix[13] = 0;
		matrix[2] = 2 * (xz - yw);
		matrix[6] = 2 * (yz + xw);
		matrix[10] = 1 - 2 * (xx + yy);
		matrix[14] = 0;
		matrix[3] = 0;
		matrix[7] = 0;
		matrix[11] = 0;
		matrix[15] = 1;
		return matrix;
	}

	public Quaternion setFromMatrix(boolean normalizeAxes, float[] matrix) {
		return setFromAxes(normalizeAxes, matrix[0], matrix[4], matrix[8], matrix[1], matrix[5], matrix[9], matrix[2], matrix[6], matrix[10]);
	}

	public Quaternion setFromAxes(boolean normalizeAxes, float xx, float xy, float xz, float yx, float yy, float yz, float zx, float zy, float zz) {
		if (normalizeAxes) {
			final float lx = 1.0f / Vector3.lengthOf(xx, xy, xz);
			final float ly = 1.0f / Vector3.lengthOf(yx, yy, yz);
			final float lz = 1.0f / Vector3.lengthOf(zx, zy, zz);
			xx *= lx;
			xy *= lx;
			xz *= lx;
			yx *= ly;
			yy *= ly;
			yz *= ly;
			zx *= lz;
			zy *= lz;
			zz *= lz;
		}
		// the trace is the sum of the diagonal elements; see
		// http://mathworld.wolfram.com/MatrixTrace.html
		final float t = xx + yy + zz;

		// we protect the division by s by ensuring that s>=1
		if (t >= 0) { // |w| >= .5
			float s = (float) Math.sqrt(t + 1); // |s|>=1 ...
			w = 0.5f * s;
			s = 0.5f / s; // so this division isn't bad
			x = (zy - yz) * s;
			y = (xz - zx) * s;
			z = (yx - xy) * s;
		} else if (xx > yy && xx > zz) {
			float s = (float) Math.sqrt(1.0 + xx - yy - zz); // |s|>=1
			x = s * 0.5f; // |x| >= .5
			s = 0.5f / s;
			y = (yx + xy) * s;
			z = (xz + zx) * s;
			w = (zy - yz) * s;
		} else if (yy > zz) {
			float s = (float) Math.sqrt(1.0 + yy - xx - zz); // |s|>=1
			y = s * 0.5f; // |y| >= .5
			s = 0.5f / s;
			x = (yx + xy) * s;
			z = (zy + yz) * s;
			w = (xz - zx) * s;
		} else {
			float s = (float) Math.sqrt(1.0 + zz - xx - yy); // |s|>=1
			z = s * 0.5f; // |z| >= .5
			s = 0.5f / s;
			x = (xz + zx) * s;
			y = (zy + yz) * s;
			w = (yx - xy) * s;
		}

		return this;
	}

}
