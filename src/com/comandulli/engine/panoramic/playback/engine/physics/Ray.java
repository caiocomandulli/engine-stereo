package com.comandulli.engine.panoramic.playback.engine.physics;

import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class Ray {

	public final Vector3 origin;
	public final Vector3 direction;
	public final float range;

	public Ray(Vector3 origin, Vector3 direction) {
		this.origin = origin;
		this.direction = direction;
		this.range = 0.0f;
	}

	public Ray(Vector3 origin, Vector3 direction, float range) {
		this.origin = origin;
		this.direction = direction;
		this.range = range;
	}

}
