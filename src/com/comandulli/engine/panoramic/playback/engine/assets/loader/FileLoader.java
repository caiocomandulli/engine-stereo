package com.comandulli.engine.panoramic.playback.engine.assets.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import com.comandulli.engine.panoramic.playback.engine.assets.AssetLoader;
import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.assets.ObjParser;
import com.comandulli.engine.panoramic.playback.engine.audio.AudioSound;
import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.material.Texture;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Mesh;

public class FileLoader implements AssetLoader {

	private static final String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.comandulli.engine.panoramic.concert/files/assets";

	@Override
	public Texture loadTexture(String resource) {
		return new Texture(FILEPATH + "/assets/textures/" + resource);
	}

	@Override
	public Mesh loadMesh(String resource) {
		try {
			FileInputStream inputStream = new FileInputStream(FILEPATH + "/assets/meshes/" + resource);
            inputStream.close();
			return ObjParser.loadFromStream(inputStream);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public void loadShader(Shader shader, String resource) {
		shader.setProgram(loadText("/assets/shaders/" + resource + "/vertex.glsl"), loadText("/assets/shaders/" + resource + "/fragment.glsl"));
	}

	@Override
	public AudioSound loadSound(String resource) {
        return new AudioSound(FILEPATH + "/assets/sounds/" + resource);
	}

	@Override
	public String loadText(String source) {
		try {
			FileInputStream inputStream = new FileInputStream(FILEPATH + source);
            inputStream.close();
			return Assets.readStringInputStream(inputStream);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public InputStream openInputStream(String source) {
		try {
			return new FileInputStream(source);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

}
