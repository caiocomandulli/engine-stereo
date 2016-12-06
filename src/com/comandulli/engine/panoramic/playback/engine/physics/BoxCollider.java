package com.comandulli.engine.panoramic.playback.engine.physics;

import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

public class BoxCollider extends Collider {

	protected final Vector3 offsetPosition;
	protected final Quaternion offsetRotation;
	protected final Vector3 dimensions;

	public BoxCollider(Vector3 dimensions) {
		this(new Vector3(), new Quaternion(), dimensions);
	}

	public BoxCollider(Vector3 offsetPosition, Quaternion offsetRotation, Vector3 dimensions) {
		super();
		this.offsetPosition = offsetPosition;
		this.offsetRotation = offsetRotation;
		this.dimensions = dimensions;
	}

	@Override
	public boolean raycast(Ray ray) {
		Vector3 center = Vector3.sum(entity.transform.getWorldPosition(), offsetPosition);
		Quaternion rotation = Quaternion.multiply(entity.transform.getWorldRotation(), offsetRotation);
		Vector3 scaledDimensions = Vector3.scaled(dimensions, entity.transform.getWorldScale());

		float[] rotationMatrix = rotation.toMatrix();
		Vector3 upNormal = Vector3.rotated(Vector3.DIRECTION_UP, rotationMatrix);
		Vector3 bottomNormal = Vector3.scaled(upNormal, -1.0f);
		Vector3 frontNormal = Vector3.rotated(Vector3.DIRECTION_FORWARD, rotationMatrix);
		Vector3 backNormal = Vector3.scaled(frontNormal, -1.0f);
		Vector3 leftNormal = Vector3.rotated(Vector3.DIRECTION_LEFT, rotationMatrix);
		Vector3 rightNormal = Vector3.scaled(leftNormal, -1.0f);

		Vector3 upCenter = Vector3.sum(center, Vector3.scaled(upNormal, scaledDimensions.y / 2));
		Vector3 bottomCenter = Vector3.sum(center, Vector3.scaled(bottomNormal, scaledDimensions.y / 2));
		Vector3 frontCenter = Vector3.sum(center, Vector3.scaled(frontNormal, scaledDimensions.z / 2));
		Vector3 backCenter = Vector3.sum(center, Vector3.scaled(backNormal, scaledDimensions.z / 2));
		Vector3 leftCenter = Vector3.sum(center, Vector3.scaled(leftNormal, scaledDimensions.x / 2));
		Vector3 rightCenter = Vector3.sum(center, Vector3.scaled(rightNormal, scaledDimensions.x / 2));

		Vector3 upIntersection = isLineIntersectingPlane(upNormal, upCenter, ray.direction, ray.origin);
		if (upIntersection != null) {
			if (isPointWithingBoundaries(frontCenter, frontNormal, upIntersection, scaledDimensions.z)) {
				if (isPointWithingBoundaries(backCenter, backNormal, upIntersection, scaledDimensions.z)) {
					if (isPointWithingBoundaries(leftCenter, leftNormal, upIntersection, scaledDimensions.x)) {
						if (isPointWithingBoundaries(rightCenter, rightNormal, upIntersection, scaledDimensions.x)) {
							return true;
						}
					}
				}
			}
		}
		Vector3 bottomIntersection = isLineIntersectingPlane(bottomNormal, bottomCenter, ray.direction, ray.origin);
		if (bottomIntersection != null) {
			if (isPointWithingBoundaries(frontCenter, frontNormal, bottomIntersection, scaledDimensions.z)) {
				if (isPointWithingBoundaries(backCenter, backNormal, bottomIntersection, scaledDimensions.z)) {
					if (isPointWithingBoundaries(leftCenter, leftNormal, bottomIntersection, scaledDimensions.x)) {
						if (isPointWithingBoundaries(rightCenter, rightNormal, bottomIntersection, scaledDimensions.x)) {
							return true;
						}
					}
				}
			}
		}
		Vector3 frontIntersection = isLineIntersectingPlane(frontNormal, frontCenter, ray.direction, ray.origin);
		if (frontIntersection != null) {
			if (isPointWithingBoundaries(upCenter, upNormal, frontIntersection, scaledDimensions.y)) {
				if (isPointWithingBoundaries(bottomCenter, bottomNormal, frontIntersection, scaledDimensions.y)) {
					if (isPointWithingBoundaries(leftCenter, leftNormal, frontIntersection, scaledDimensions.x)) {
						if (isPointWithingBoundaries(rightCenter, rightNormal, frontIntersection, scaledDimensions.x)) {
							return true;
						}
					}
				}
			}
		}
		Vector3 backIntersection = isLineIntersectingPlane(backNormal, backCenter, ray.direction, ray.origin);
		if (backIntersection != null) {
			if (isPointWithingBoundaries(upCenter, upNormal, backIntersection, scaledDimensions.y)) {
				if (isPointWithingBoundaries(bottomCenter, bottomNormal, backIntersection, scaledDimensions.y)) {
					if (isPointWithingBoundaries(leftCenter, leftNormal, backIntersection, scaledDimensions.x)) {
						if (isPointWithingBoundaries(rightCenter, rightNormal, backIntersection, scaledDimensions.x)) {
							return true;
						}
					}
				}
			}
		}
		Vector3 leftIntersection = isLineIntersectingPlane(leftNormal, leftCenter, ray.direction, ray.origin);
		if (leftIntersection != null) {
			if (isPointWithingBoundaries(upCenter, upNormal, leftIntersection, scaledDimensions.y)) {
				if (isPointWithingBoundaries(bottomCenter, bottomNormal, leftIntersection, scaledDimensions.y)) {
					if (isPointWithingBoundaries(frontCenter, frontNormal, leftIntersection, scaledDimensions.z)) {
						if (isPointWithingBoundaries(backCenter, backNormal, leftIntersection, scaledDimensions.z)) {
							return true;
						}
					}
				}
			}
		}
		Vector3 rightIntersection = isLineIntersectingPlane(rightNormal, rightCenter, ray.direction, ray.origin);
		if (rightIntersection != null) {
			if (isPointWithingBoundaries(upCenter, upNormal, rightIntersection, scaledDimensions.y)) {
				if (isPointWithingBoundaries(bottomCenter, bottomNormal, rightIntersection, scaledDimensions.y)) {
					if (isPointWithingBoundaries(frontCenter, frontNormal, rightIntersection, scaledDimensions.z)) {
						if (isPointWithingBoundaries(backCenter, backNormal, rightIntersection, scaledDimensions.z)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static Vector3 isLineIntersectingPlane(Vector3 planeNormal, Vector3 planeCenter, Vector3 lineDirection, Vector3 lineOrigin) {
		float dot = Vector3.dot(lineDirection, planeNormal);
		if (dot < 0) {
			float distance = Vector3.dot(Vector3.deltaVector(planeCenter, lineOrigin), planeNormal) / dot;
			if (distance < 0) {
				return null;
			}

			return Vector3.scaled(lineDirection, distance).add(lineOrigin);
		}
		return null;
	}

	public static boolean isPointWithingBoundaries(Vector3 planeCenter, Vector3 planeNormal, Vector3 point, float boundary) {
		float distance = Math.abs(Vector3.dot(Vector3.deltaVector(planeCenter, point), planeNormal));
        return distance < boundary;
    }

	@Override
	public Renderer getDebugModel() {
		// TODO return primitive mesh
		@SuppressWarnings("UnnecessaryLocalVariable") MeshRenderer mesh = new MeshRenderer(0, Physics.debugMaterial);
		return mesh;
	}

}
