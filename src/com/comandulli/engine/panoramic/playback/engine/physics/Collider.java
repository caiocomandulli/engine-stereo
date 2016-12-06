package com.comandulli.engine.panoramic.playback.engine.physics;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

public abstract class Collider extends Component {

	public static final float EPSYLON = 0.03f;

    public boolean raycast(Ray ray) {
		Vector3 delta = Vector3.deltaVector(entity.transform.position, ray.origin).normalize();
		float dot = Vector3.dot(delta, ray.direction);
        return dot < 1 + EPSYLON || dot > 1 - EPSYLON;
    }

	public abstract Renderer getDebugModel();

	@Override
	protected void register() {
		if (entity.collider != null) {
			entity.collider.unregister();
		}
		Physics.registerCollider(this);
		entity.collider = this;
	}

	@Override
	protected void unregister() {
		Physics.removeCollider(this);
		entity.collider = null;
	}
}
