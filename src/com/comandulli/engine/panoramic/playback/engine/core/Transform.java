package com.comandulli.engine.panoramic.playback.engine.core;

import android.opengl.Matrix;

import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class Transform {

	public Vector3 position;
	public Quaternion rotation;
	public Vector3 scale;
	public Transform parent;

	public Entity entity;

	private static final float[] CAMERA_VIEW_MATRIX = { -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

	public Transform() {
		this(new Vector3(), new Quaternion(), Vector3.SCALE_ONE.copy());
	}

	public Transform(Vector3 position, Quaternion rotation, Vector3 scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Transform translate(Vector3 translation) {
		position.add(translation);
		return this;
	}

	public Transform translate(float x, float y, float z) {
		position.add(new Vector3(x, y, z));
		return this;
	}

	public Transform rotate(Vector3 euler) {
		rotation = Quaternion.multiply(rotation, new Quaternion(euler));
		return this;
	}

	public Transform rotate(Vector3 axis, float angle) {
		rotation = Quaternion.multiply(rotation, new Quaternion(axis, angle));
		return this;
	}

	public Transform rotate(Quaternion rotation) {
		this.rotation = Quaternion.multiply(this.rotation, rotation);
		return this;
	}

	public Transform scale(Vector3 scale) {
		this.scale.scale(scale);
		return this;
	}

	public float[] getModelMatrix() {
		float[] transformMatrix = new float[16];
		Matrix.setIdentityM(transformMatrix, 0);
		if (parent != null) {
			transformMatrix = parent.getModelMatrix().clone();
		}
		Matrix.translateM(transformMatrix, 0, position.x, position.y, position.z);
		Vector3 axis = new Vector3();
		float angle = rotation.getAxisAngleDegrees(axis);
		Matrix.rotateM(transformMatrix, 0, angle, axis.x, axis.y, axis.z);
		Matrix.scaleM(transformMatrix, 0, scale.x, scale.y, scale.z);
		return transformMatrix;
	}

	public float[] getViewMatrix() {
		float[] transformMatrix = CAMERA_VIEW_MATRIX.clone();
		if (parent != null) {
			transformMatrix = parent.getModelMatrix().clone();
		}
		Matrix.translateM(transformMatrix, 0, position.x, position.y, position.z);
		Vector3 axis = new Vector3();
		float angle = rotation.getAxisAngleDegrees(axis);
		Matrix.rotateM(transformMatrix, 0, -angle, axis.x, axis.y, axis.z);
		return transformMatrix;
	}

	public Vector3 getForward() {
		return Vector3.rotated(Vector3.DIRECTION_FORWARD, rotation.toMatrix());
	}

	public Vector3 getWorldPosition() {
		return getWorldPosition(this);
	}

	public Quaternion getWorldRotation() {
		return getWorldRotation(this);
	}

	public Vector3 getWorldScale() {
		return getWorldScale(this);
	}

	private Vector3 getWorldPosition(Transform transform) {
		if (transform.parent != null) {
			return transform.position.rotateAround(transform.parent.getWorldPosition(), transform.parent.rotation);
		} else {
			return transform.position.copy();
		}
	}

	private Vector3 getWorldScale(Transform transform) {
		if (transform.parent != null) {
			return Vector3.scaled(transform.scale, transform.parent.getWorldScale());
		} else {
			return transform.scale.copy();
		}
	}

	private Quaternion getWorldRotation(Transform transform) {
		if (transform.parent != null) {
			return Quaternion.multiply(transform.parent.getWorldRotation(), transform.rotation);
		} else {
			return transform.rotation.copy();
		}
	}

    @Override
    public String toString() {
        return "{" + position + "," + rotation + "," + scale + "}";
    }
}
