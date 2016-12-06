package com.comandulli.engine.panoramic.playback.engine.physics;

import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

public class SphereCollider extends Collider {

	private final Vector3 offsetPosition;
	private final float radius;

	public SphereCollider(float radius) {
		this(new Vector3(), radius);
	}

	public SphereCollider(Vector3 offsetPosition, float radius) {
		super();
		this.radius = radius;
		this.offsetPosition = offsetPosition;
	}

	@Override
	public boolean raycast(Ray ray) {
		Vector3 sphereCenter = entity.transform.getWorldPosition().add(offsetPosition);
		Vector3 distance = Vector3.deltaVector(ray.origin, sphereCenter);
		float dot = Vector3.dot(ray.direction, distance);
		if (dot < 0) {
			float value = dot * dot - distance.squareLength() + radius * radius;
			// dot * dot - dir * dir - rad * rad;
            return value >= 0;
		} else {
			return false;
		}
	}

	@Override
	public Renderer getDebugModel() {
		// TODO return primitive mesh
		@SuppressWarnings("UnnecessaryLocalVariable") MeshRenderer mesh = new MeshRenderer(0, Physics.debugMaterial);
		return mesh;
	}

}
