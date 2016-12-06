package com.comandulli.engine.panoramic.playback.engine.assets;

import java.io.InputStream;

import com.comandulli.engine.panoramic.playback.engine.audio.AudioSound;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.material.Texture;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Mesh;

public interface AssetLoader {

	Texture loadTexture(String resource);

	Mesh loadMesh(String resource);

	AudioSound loadSound(String resource);

	void loadShader(Shader shader, String resource);

	String loadText(String source);

	InputStream openInputStream(String source);

}
