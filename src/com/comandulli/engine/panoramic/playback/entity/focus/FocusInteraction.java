package com.comandulli.engine.panoramic.playback.entity.focus;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.physics.Physics;
import com.comandulli.engine.panoramic.playback.engine.physics.Ray;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;

import java.util.List;

public class FocusInteraction extends Entity {

	private final Camera camera;

	private InteractiveObject focusedObject;
	private InteractiveObject potentialFocus;
	private float timemark;

	private final int mesh;
	private final int shader;

	public FocusInteraction(String name, Camera camera, int mesh, int shader) {
		super(name);
		this.camera = camera;
		this.mesh = mesh;
		this.shader = shader;
	}

	@Override
	public void start() {
		Entity smallBox = new Entity("Crosshair");
		smallBox.addComponent(new MeshRenderer(mesh, new Material(shader, Color.WHITE)));
		smallBox.transform.translate(new Vector3(0.0f, 0.0f, 2.0f));
		smallBox.transform.parent = camera.entity.transform;
		smallBox.transform.scale(new Vector3(0.01f, 0.01f, 0.01f));
		Engine.getScene().addEntity(smallBox);
	}

	@Override
	public void update() {
		Vector3 origin = camera.entity.transform.position;
		Vector3 direction = camera.entity.transform.getForward();
		Ray ray = new Ray(origin, direction);
		List<Entity> entityList = Physics.raycast(ray);
		if (entityList.isEmpty()) {
			if (focusedObject != null) {
				focusedObject.FocusOut();
			}
			if (potentialFocus != null) {
				potentialFocus.FocusCanceled();
			}
			focusedObject = null;
			potentialFocus = null;
		}
		for (Entity entity : entityList) {
			if (entity instanceof InteractiveObject) {
				if (focusedObject != null) {
					if (entity != focusedObject) {
						focusedObject.FocusOut();
						timemark = Time.time;
						potentialFocus = (InteractiveObject) entity;
						potentialFocus.FocusStarted();
					} else {
						focusedObject.FocusUpdate(Time.time - (timemark + focusedObject.getTimeToFocus()));
					}
				} else {
					if (potentialFocus != null) {
						if (entity == potentialFocus) {
							if (Time.time > timemark + potentialFocus.getTimeToFocus()) {
								((InteractiveObject) entity).FocusIn();
								focusedObject = potentialFocus;
								potentialFocus = null;
							}
						} else {
							potentialFocus.FocusCanceled();
							potentialFocus = (InteractiveObject) entity;
							potentialFocus.FocusStarted();
						}
					} else {
						timemark = Time.time;
						potentialFocus = (InteractiveObject) entity;
						potentialFocus.FocusStarted();
					}
				}
                break;
			}
		}
		super.update();
	}
}
