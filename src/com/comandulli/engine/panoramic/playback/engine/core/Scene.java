package com.comandulli.engine.panoramic.playback.engine.core;

import java.util.ArrayList;
import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;

public abstract class Scene {

	private final List<Entity> queueToAdd;
	private final List<Entity> entitiesToStart;
	private final List<Entity> entities;
	private final List<Entity> queueToRemove;
	private final List<Camera> cameras;
	private boolean queuedFinish;

	public Scene() {
		this.entities = new ArrayList<>();
		this.queueToAdd = new ArrayList<>();
		this.entitiesToStart = new ArrayList<>();
		this.queueToRemove = new ArrayList<>();
		this.cameras = new ArrayList<>();
	}

	public abstract void init();

	public void start() {
		init();
		enqueue();
	}

	public void enqueue() {
		if (!queueToAdd.isEmpty()) {
			for (Entity entity : queueToAdd) {
				entitiesToStart.add(entity);
				if (entity.renderer != null) {
					for (Camera camera : cameras) {
						camera.addToRenderQueue(entity);
					}
				}
			}
			queueToAdd.clear();
			for (Entity entity : entitiesToStart) {
				entity.start();
			}
			entities.addAll(entitiesToStart);
			entitiesToStart.clear();
		}
	}

	public void update() {
		enqueue();
		for (Entity entity : queueToRemove) {
			if (entities.contains(entity)) {
				if (entity.renderer != null) {
					for (Camera camera : cameras) {
						camera.removeFromRenderQueue(entity);
					}
				}
				entities.remove(entity);
			}
		}
		queueToRemove.clear();
		for (Entity entity : entities) {
			if (entity.isEnabled()) {
				entity.update();
			}
		}
		if (queuedFinish) {
			destroy();
		}
	}

    public void postRender() {
        // TODO: postRender
		// do stuff after render
	}

	public void finish() {
		queuedFinish = true;
	}

	public void finishNow() {
		destroy();
	}

	protected void destroy() {
		for (Entity entity : entities) {
			entity.unregister();
			entity.destroy();
		}
		queueToAdd.clear();
		entitiesToStart.clear();
		entities.clear();
		queueToRemove.clear();
	}

	public void pause() {
		for (Entity entity : entities) {
			entity.pause();
		}
	}

	public void resume() {
		for (Entity entity : entities) {
			entity.resume();
		}
	}

	public void addEntity(Entity entity) {
		this.queueToAdd.add(entity);
		entity.register();
	}

	public void addEntity(Entity... entity) {
		for (Entity value : entity) {
			addEntity(value);
		}
	}

	public void removeEntity(Entity entity) {
		this.queueToRemove.add(entity);
		entity.unregister();
	}

	public void registerCamera(Camera camera) {
		cameras.add(camera);
		for (Entity entity : entities) {
			if (entity.renderer != null) {
				camera.addToRenderQueue(entity);
			}
		}
	}

	public void removeCamera(Camera camera) {
		camera.clearRenderQueue();
		cameras.remove(camera);
	}

	public List<Camera> getCameras() {
		return cameras;
	}

	public void adjustViewport(int width, int height) {
		for (Camera camera : cameras) {
			camera.adjustViewport(width, height);
		}
	}
}
