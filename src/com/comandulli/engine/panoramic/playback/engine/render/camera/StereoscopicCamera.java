package com.comandulli.engine.panoramic.playback.engine.render.camera;

import java.util.List;

import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class StereoscopicCamera extends Camera {

	protected float interpupillaryDistance = 0.06f;

	protected boolean isStereo = true;

	public StereoscopicCamera(Projection projection) {
		super(projection);
	}

	public void setInterpupillaryDistance(float interpupillaryDistance) {
		this.interpupillaryDistance = interpupillaryDistance;
	}

	@Override
	public void render() {
		if (!isStereo) {
			super.render();
			return;
		}
		clear();
		float[] monoView = entity.transform.getViewMatrix();
		float[] leftView = monoView.clone();
		Matrix.translateM(leftView, 0, -(interpupillaryDistance / 2), 0.0f, 0.0f);
		float[] rightView = monoView.clone();
		Matrix.translateM(rightView, 0, interpupillaryDistance / 2, 0.0f, 0.0f);

		for (int i = 0; i < shaders.size(); i++) {
			Shader shader = shaders.get(i);
			shader.begin();
			GLES20.glViewport(x, y, width / 2, height);
			List<Entity> shaderQueue = renderQueue.get(i);
			this.viewMatrix = leftView;
			for (Entity entity : shaderQueue) {
                Renderer renderer = entity.renderer;
				if (renderer.isEnabled()) {
					if ((renderer.flags & Renderer.FLAG_EXCLUDE_LEFT) != Renderer.FLAG_EXCLUDE_LEFT) {
						shader.render(renderer, this);
					}
				}
			}
			shader.end();
		}

		for (int i = 0; i < shaders.size(); i++) {
			Shader shader = shaders.get(i);
			shader.begin();
			GLES20.glViewport(x + width / 2, y, width / 2, height);
			List<Entity> shaderQueue = renderQueue.get(i);
			this.viewMatrix = rightView;
			for (Entity entity : shaderQueue) {
                Renderer renderer = entity.renderer;
				if (renderer.isEnabled()) {
					if ((renderer.flags & Renderer.FLAG_EXCLUDE_RIGHT) != Renderer.FLAG_EXCLUDE_RIGHT) {
						shader.render(renderer, this);
					}
				}
			}
			shader.end();
		}
	}

	@Override
	public void render(Renderer[] queue, Shader shader) {
		if (!isStereo) {
			super.render(queue, shader);
			return;
		}
		clear();
		float[] monoView = entity.transform.getViewMatrix();
		float[] leftView = monoView.clone();
		Matrix.translateM(leftView, 0, -(interpupillaryDistance / 2), 0.0f, 0.0f);
		float[] rightView = monoView.clone();
		Matrix.translateM(rightView, 0, interpupillaryDistance / 2, 0.0f, 0.0f);
		shader.begin();
		GLES20.glViewport(x, y, width / 2, height);
		entity.transform.translate(new Vector3(0.0f, 100.0f, 0.0f));
		this.viewMatrix = leftView;
		for (Renderer renderer : queue) {
			if (renderer.isEnabled()) {
				shader.render(renderer, this);
			}
		}
		GLES20.glViewport(x + width / 2, y, width / 2, height);
		entity.transform.translate(new Vector3(0.0f, -100.0f, 0.0f));
		this.viewMatrix = rightView;
		for (Renderer renderer : queue) {
			if (renderer.isEnabled()) {
				shader.render(renderer, this);
			}
		}
		shader.end();
	}

	@Override
	public void adjustViewport(int width, int height) {
		this.viewportWidth = width;
		this.viewportHeight = height;

		this.x = (int) (left * viewportWidth);
		this.y = (int) (bottom * viewportHeight);
		this.width = (int) ((right - left) * viewportWidth);
		this.height = (int) ((top - bottom) * viewportHeight);

		float ratio;
		if (isStereo) {
			ratio = (float) (this.width / 2) / this.height;
		} else {
			ratio = (float) this.width / this.height;
		}
		setPerspectiveProjection(fieldOfView, ratio, near, far);
	}

	public boolean isStereo() {
		return isStereo;
	}

	public void setStereo(boolean isStereo) {
		this.isStereo = isStereo;
		adjustViewport(width, height);
	}

}
