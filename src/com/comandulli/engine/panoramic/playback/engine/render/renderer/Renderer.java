package com.comandulli.engine.panoramic.playback.engine.render.renderer;

import java.nio.FloatBuffer;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;

public abstract class Renderer extends Component {

	public static final int FLAG_EXCLUDE_LEFT = 0x0000010;
	public static final int FLAG_EXCLUDE_RIGHT = 0x0000001;

	public int flags;

	public FloatBuffer positionBuffer;
	public FloatBuffer textureCoordinateBuffer;
	public FloatBuffer normalBuffer;
	public FloatBuffer tangentBuffer;
	public FloatBuffer bitangentBuffer;

	public int bufferSize;
	public float[] modelMatrix;
	public final Material material;

	public Renderer(Material material) {
		super();
		this.material = material;
	}

    @Override
    public void start() {
        this.modelMatrix = entity.transform.getModelMatrix();
    }

	@Override
	public void update() {
		this.modelMatrix = entity.transform.getModelMatrix();
	}

	@Override
	protected void register() {
		entity.renderer = this;
	}

	@Override
	protected void unregister() {
		entity.renderer = null;
	}

}
