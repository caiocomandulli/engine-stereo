package com.comandulli.engine.panoramic.playback.engine.assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.comandulli.engine.panoramic.playback.engine.assets.loader.AssetManagerLoader;
import com.comandulli.engine.panoramic.playback.engine.audio.AudioSound;
import com.comandulli.engine.panoramic.playback.engine.render.material.Shader;
import com.comandulli.engine.panoramic.playback.engine.render.material.Texture;
import com.comandulli.engine.panoramic.playback.engine.render.material.VideoTexture;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.Mesh;

public class Assets {

	private static List<Shader> loadedShaders;
	private static List<Mesh> loadedMeshes;
	private static List<Texture> loadedTextures;
	private static List<AudioSound> loadedSounds;

	private static AssetLoader loader;

	public static void init(Context currentContext) {
		loadedShaders = new ArrayList<>();
		loadedMeshes = new ArrayList<>();
		loadedTextures = new ArrayList<>();
		loadedSounds = new ArrayList<>();
		loader = new AssetManagerLoader(currentContext);
	}

	public static void load() {
		for (Texture texture : loadedTextures) {
			texture.load();
		}
		for (Shader shader : loadedShaders) {
			shader.load();
		}
	}

	public static void setLoader(AssetLoader newLoader) {
		loader = newLoader;
	}

	// /////////////////////////////
	// LOADERS //
	// /////////////////////////////

	public static String readString(String path) {
		return loader.loadText(path);
	}

	public static int loadTexture(String source) {
		Texture texture = loader.loadTexture(source);
		int index = loadedTextures.size();
		loadedTextures.add(texture);
		return index;
	}

	public static int loadVideoTexture() {
		VideoTexture videoTexture = new VideoTexture();
		int index = loadedTextures.size();
		loadedTextures.add(videoTexture);
		return index;
	}

	public static int loadMesh(String sourcePath) {
		Mesh mesh = loader.loadMesh(sourcePath);
		int index = loadedMeshes.size();
		loadedMeshes.add(mesh);
		return index;
	}

	public static int loadSound(String source) {
		AudioSound audio = loader.loadSound(source);
		int index = loadedSounds.size();
		loadedSounds.add(audio);
		return index;
	}

	public static int initShader(Shader shader, String shaderName) {
		loader.loadShader(shader, shaderName);
		int index = loadedShaders.size();
		loadedShaders.add(shader);
		return index;
	}

	public static InputStream openInputStream(String source) {
		return loader.openInputStream(source);
	}

	// /////////////////////////////
	// GETTERS //
	// /////////////////////////////

	public static Shader getShader(int index) {
		return loadedShaders.get(index);
	}

	public static Mesh getMesh(int index) {
		return loadedMeshes.get(index);
	}

	public static Texture getTexture(int index) {
		return loadedTextures.get(index);
	}

	public static AudioSound getSound(int index) {
		return loadedSounds.get(index);
	}

	public static List<Shader> getShaders() {
		return loadedShaders;
	}

	// /////////////////////////////
	// INNER METHODS //
	// /////////////////////////////

	public static String readStringInputStream(InputStream inputStream) throws IOException {
		StringBuilder body = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String nextLine;
		while ((nextLine = bufferedReader.readLine()) != null) {
			body.append(nextLine);
			body.append('\n');
		}
		bufferedReader.close();
		return body.toString();
	}
}
