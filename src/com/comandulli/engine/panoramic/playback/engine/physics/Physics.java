package com.comandulli.engine.panoramic.playback.engine.physics;

import java.util.ArrayList;
import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

public class Physics {

	public static final boolean debugRenderEnabled = false;
	// TODO load debug shader and material
	public static final Shader debugShader = null;
	public static final Material debugMaterial = null;
	private static List<Collider> colliders;

	public static void init() {
		colliders = new ArrayList<>();
	}

	public static void registerCollider(Collider collider) {
		colliders.add(collider);
	}

	public static void removeCollider(Collider collider) {
		colliders.remove(collider);
	}

	public static List<Entity> raycast(Ray ray) {
		List<Entity> entities = new ArrayList<>();
		for (Collider collider : colliders) {
			if (collider.isEnabled()) {
				if (collider.raycast(ray)) {
					entities.add(collider.getEntity());
				}
			}
		}
		return entities;
	}

	public static void render(Camera camera) {
		Renderer[] queue = new Renderer[colliders.size()];
		for (Collider collider : colliders) {
			if (collider.isEnabled()) {
				camera.render(queue, debugShader);
			}
		}
	}

}
