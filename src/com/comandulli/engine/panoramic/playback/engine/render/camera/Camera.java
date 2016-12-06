package com.comandulli.engine.panoramic.playback.engine.render.camera;

import java.util.ArrayList;
import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Camera extends Component {

	public enum Projection {
        Orthographic, Perspective
    }

	public final Projection projection;

	protected float left;
	protected float bottom;
	protected float right;
	protected float top;

	protected float near;
	protected float far;

	protected int viewportWidth;
	protected int viewportHeight;

	protected int x;
	protected int y;
	protected int width;
	protected int height;

    protected float fieldOfView = 100.0f;

	protected final List<Shader> shaders;

	protected final List<List<Entity>> renderQueue;

    protected float ortographicSize = 1.0f;

	public enum ClearFlag {
		SOLID_COLOR, DEPTH_ONLY, DONT_CLEAR
	}

	public ClearFlag clearFlag;
	public Color clearColor;
	public final float[] projectionMatrix = new float[16];
	public float[] viewMatrix = new float[16];

	public Camera(Projection projection) {
		super();
		this.clearFlag = ClearFlag.SOLID_COLOR;
		this.clearColor = Color.WHITE;
		this.projection = projection;
		setViewport(0.0f, 1.0f, 0.0f, 1.0f, 0.1f, 1000.0f);
		shaders = Assets.getShaders();
		this.renderQueue = new ArrayList<>();
		for (int i = 0; i < shaders.size(); i++) {
			this.renderQueue.add(new ArrayList<Entity>());
		}
	}

    public void setClearFlag(ClearFlag flag) {
        this.clearFlag = flag;
    }

    public void setClearColor(Color clearColor) {
        this.clearColor = clearColor;
    }

	public void setViewport(float bottom, float top, float left, float right, float near, float far) {
		this.bottom = Math.max(Math.min(bottom, 1.0f), 0.0f);
		this.top = Math.max(Math.min(top, 1.0f), 0.0f);
		this.left = Math.max(Math.min(left, 1.0f), 0.0f);
		this.right = Math.max(Math.min(right, 1.0f), 0.0f);

		this.near = near;
		this.far = far;

		if (viewportHeight != 0) {
			y = (int) (bottom * viewportHeight);
			height = (int) ((top - bottom) * viewportHeight);
		}
		if (viewportWidth != 0) {
			x = (int) (left * viewportWidth);
			width = (int) ((right - left) * viewportWidth);
		}
	}

	public void render() {
		clear();
		this.viewMatrix = entity.transform.getViewMatrix();
		for (int i = 0; i < shaders.size(); i++) {
			Shader shader = shaders.get(i);
			shader.begin();
			GLES20.glViewport(x, y, width, height);
			List<Entity> shaderQueue = renderQueue.get(i);
			for (Entity entity : shaderQueue) {
                Renderer renderer = entity.renderer;
				if (renderer.isEnabled()) {
					shader.render(renderer, this);
				}
			}
			shader.end();
		}
	}

	public void render(Renderer[] queue, Shader shader) {
		clear();
		this.viewMatrix = entity.transform.getViewMatrix();
		shader.begin();
		GLES20.glViewport(x, y, width, height);
		for (Renderer renderer : queue) {
			if (renderer.isEnabled()) {
				shader.render(renderer, this);
			}
		}
		shader.end();
	}

	public void adjustViewport(int width, int height) {
		this.viewportWidth = width;
		this.viewportHeight = height;

		this.x = (int) (left * viewportWidth);
		this.y = (int) (bottom * viewportHeight);
		this.width = (int) ((right - left) * viewportWidth);
		this.height = (int) ((top - bottom) * viewportHeight);

		final float ratio = (float) this.width / this.height;
		switch (projection) {
		case Orthographic:
			setOrthoProjection(-ratio * ortographicSize, ratio * ortographicSize, -1.0f * ortographicSize, 1.0f * ortographicSize, near, far);
			break;
		case Perspective:
			setPerspectiveProjection(fieldOfView, ratio, near, far);
			break;
		}
	}

    public void setFieldOfView(float fov) {
        this.fieldOfView = fov;
    }

	protected void clear() {
		switch (clearFlag) {
		case SOLID_COLOR:
			GLES20.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			break;
		case DEPTH_ONLY:
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
			break;
		case DONT_CLEAR:
			break;
		}
	}

	public void setFrustumProjection(float left, float right, float bottom, float top, float near, float far) {
		Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
	}

	public void setOrthoProjection(float left, float right, float bottom, float top, float near, float far) {
		Matrix.orthoM(projectionMatrix, 0, left, right, bottom, top, near, far);
	}

	public void setPerspectiveProjection(float fov, float aspect, float near, float far) {
		Matrix.perspectiveM(projectionMatrix, 0, fov, aspect, near, far);
	}

	public void addToRenderQueue(Entity entity) {
		renderQueue.get(entity.renderer.material.shader).add(entity);
	}

	public void removeFromRenderQueue(Entity entity) {
		renderQueue.get(entity.renderer.material.shader).remove(entity);
	}

	public void clearRenderQueue() {
		renderQueue.clear();
	}

    public void setOrthographicSize(float size) {
        this.ortographicSize = size;
		if (projection == Projection.Orthographic) {
			final float ratio = (float) this.width / this.height;
            setOrthoProjection(-ratio * ortographicSize, ratio * ortographicSize, -1.0f * ortographicSize, 1.0f * ortographicSize, near, far);
		}
	}

	@Override
	protected void register() {
		if (entity.camera != null) {
			entity.camera.unregister();
		}
		Engine.getScene().registerCamera(this);
		entity.camera = this;
	}

	@Override
	protected void unregister() {
		Engine.getScene().removeCamera(this);
		entity.camera = null;
	}

}
