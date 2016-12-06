package com.comandulli.engine.panoramic.playback.engine.core;

public class Component {

	public Entity entity;
	private boolean enabled = true;

	// FLOW //

	public void start() {
	}

	public void update() {
	}

	public void destroy() {
	}

	public void pause() {
	}

	public void resume() {
	}

	protected void register() {
	}

	protected void unregister() {
	}

	// GETTERS AND SETTERS

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

    public boolean isEnabled() {
        return enabled && entity.isEnabled();
    }

    public boolean isEnabledInHierarchy() {
        return enabled;
    }

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}

}