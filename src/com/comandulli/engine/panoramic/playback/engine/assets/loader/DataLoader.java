package com.comandulli.engine.panoramic.playback.engine.assets.loader;

import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import com.comandulli.engine.panoramic.playback.engine.assets.AssetLoader;
import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.assets.ObjParser;
import com.comandulli.engine.panoramic.playback.engine.assets.loader.data.ReadableData;
import com.comandulli.engine.panoramic.playback.engine.audio.AudioSound;
import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.engine.exception.DataFileException;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.material.Texture;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Mesh;

public class DataLoader implements AssetLoader {

	private static final String DATAPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.comandulli.engine.panoramic.concert/files/assets/data.bdf";
	private ReadableData readStream;

	public DataLoader() {
		try {
			readStream = new ReadableData(DATAPATH);
		} catch (DataFileException | IOException e) {
			throw new AssetNotFoundException(e);
		}
    }

	@Override
	public Texture loadTexture(String resource) {
		return new Texture("textures/" + resource);
	}

	@Override
	public Mesh loadMesh(String resource) {
		try {
			InputStream is = openInputStream("meshes/" + resource);
			return ObjParser.loadFromStream(is);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public AudioSound loadSound(String resource) {
		InputStream is = openInputStream("sounds/" + resource);
        return new AudioSound(is);
	}

	@Override
	public void loadShader(Shader shader, String resource) {
        String vertexShader = loadText("shaders/" + resource + "/vertex.glsl");
        String fragmentShader = loadText("shaders/" + resource + "/fragment.glsl");
		shader.setProgram(vertexShader, fragmentShader);
	}

	@Override
	public String loadText(String source) {
		try {
			InputStream inputStream = openInputStream(source);
			return Assets.readStringInputStream(inputStream);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

	@Override
	public InputStream openInputStream(String source) {
		try {
			String newSource = "\\" + source;
			newSource = newSource.replace("/", "\\");
			return readStream.readFile(newSource).getInputStream();
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
	}

}
