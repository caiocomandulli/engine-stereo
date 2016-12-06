package com.comandulli.engine.panoramic.playback.engine.assets.loader;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import com.comandulli.engine.panoramic.playback.engine.assets.AssetLoader;
import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.assets.ObjParser;
import com.comandulli.engine.panoramic.playback.engine.audio.AudioSound;
import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.material.Texture;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Mesh;

public class AssetManagerLoader implements AssetLoader {

	private final AssetManager assetManager;

	public AssetManagerLoader(Context context) {
		assetManager = context.getAssets();
	}

	@Override
	public Texture loadTexture(String resource) {
		return new Texture("textures/" + resource);
	}

	@Override
	public Mesh loadMesh(String resource) {
		try {
			InputStream is = assetManager.open("meshes/" + resource);
			return ObjParser.loadFromStream(is);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public void loadShader(Shader shader, String resource) {
        String vertexShader = loadText("shaders/" + resource + "/vertex.glsl");
        String fragmentShader = loadText("shaders/" + resource + "/fragment.glsl");
		shader.setProgram(vertexShader, fragmentShader);
	}

	@Override
	public InputStream openInputStream(String source) {
		try {
			return assetManager.open(source);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public String loadText(String source) {
		try {
			InputStream inputStream = assetManager.open(source);
			return Assets.readStringInputStream(inputStream);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public AudioSound loadSound(String resource) {
		try {
			AssetFileDescriptor afd = assetManager.openFd("sounds/" + resource);
			return new AudioSound(afd);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

}
