package com.comandulli.engine.panoramic.playback.engine.core;

import com.comandulli.engine.panoramic.playback.engine.physics.Collider;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	public final String name;
	private boolean enabled;
	private boolean registered;
	private final List<Component> components;
	// COMPONENTS //
	public final Transform transform;
	public Renderer renderer;
	public Camera camera;
	public Collider collider;
	// public Audio audio;
	// public Light light;
	// public RigidBody rigidBody;
	// public Tag tag;

	public Entity(String name, Transform transform) {
		this.name = name;
		this.enabled = true;
		this.registered = false;
		this.components = new ArrayList<>();
		this.transform = transform;
	}

	public Entity(String name) {
		this(name, new Transform());
        this.transform.entity = this;
	}

	public Entity(String name, Component component) {
		this(name);
		addComponent(component);
	}

	// FLOW //

	public void start() {
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.start();
			}
		}
	}

	public void update() {
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.update();
			}
		}
	}

	public void destroy() {
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.destroy();
			}
		}
	}
	
	public void pause() {
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.pause();
			}
		}
	}
	
	public void resume() {
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.resume();
			}
		}
	}

	protected void register() {
		registered = true;
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.register();
			}
		}
	}

	protected void unregister() {
		registered = false;
		for (Component component : components) {
            if (component.isEnabledInHierarchy()) {
				component.unregister();
			}
		}
	}

	// COMPONENT //

	public void addComponent(Component component) {
		component.setEntity(this);
		components.add(component);
		if (registered) {
			component.register();
		}
	}

    public Component getComponent(Class<?> type) {
        for (Component component : components) {
            if (component.getClass().equals(type)) {
                return component;
            }
            if (component.getClass().isAssignableFrom(type)) {
                return component;
            }
            if (component.getClass().getSuperclass().equals(type)) {
                return component;
            }
        }
        return null;
    }

    public List<Component> getComponents(Class<?> type) {
        List<Component> list = new ArrayList<>();
        for (Component component : components) {
            if (component.getClass().equals(type)) {
                list.add(component);
            }
            if (component.getClass().isAssignableFrom(type)) {
                list.add(component);
            }
            if (component.getClass().getSuperclass().equals(type)) {
                list.add(component);
            }
        }
        return list;
    }

	public void removeComponent(Component component) {
		component.setEntity(null);
		components.remove(component);
		if (registered) {
			component.unregister();
		}
	}

	// METHODS //

	@Override
	public String toString() {
		return name;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
        if (transform.parent != null && transform.parent.entity != null) {
            return enabled && transform.parent.entity.isEnabled();
        } else {
		return enabled;
	}
}

    public boolean isEnabledInHierarchy() {
        return enabled;
    }

    public void assignParent(Entity parent) {
        this.transform.parent = parent.transform;
    }

    public Entity getParent() {
        return this.transform.parent != null ? this.transform.parent.entity : null;
    }

}