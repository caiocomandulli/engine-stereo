package com.comandulli.engine.panoramic.playback.engine.render.renderer;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;

public class MeshRenderer extends Renderer {

	public MeshRenderer(int meshReference, Material material) {
		super(material);
		Mesh mesh = Assets.getMesh(meshReference);
		this.positionBuffer = mesh.positionBuffer;
		this.textureCoordinateBuffer = mesh.textureCoordinateBuffer;
		this.normalBuffer = mesh.normalBuffer;
		this.bufferSize = mesh.bufferSize;
	}

}
