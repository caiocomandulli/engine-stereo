package com.comandulli.engine.panoramic.example;

import android.app.Activity;

import com.comandulli.engine.panoramic.playback.engine.assets.Assets;
import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.media.MediaPlayerDescriptor;
import com.comandulli.engine.panoramic.playback.shader.ColorShader;
import com.comandulli.engine.panoramic.playback.shader.OpacityShader;
import com.comandulli.engine.panoramic.playback.shader.UnlitShader;

public class VideoPlayerEngine extends Engine {

	public static int SHADER_OPACITY_SHADER;
	public static int SHADER_UNLIT_TEXTURE;
	public static int SHADER_MOVIE_TEXTURE;
	public static int SHADER_COLOR;

	public static int MESH_BOX;
	public static int MESH_SPHERE;
	public static int MESH_PLAYBACK;
	public static int MESH_STATUS;

	public static int VIDEOTEXTURE_VIDEO;
	public static int TEXTURE_DEBUG;
	public static int TEXTURE_FONT;
	public static int TEXTURE_PLAYBACK;
	public static int TEXTURE_FILMROLL;

	public static int SOUND_TEST;

	public VideoPlayerEngine(Activity context, final MediaPlayerDescriptor descriptor) {
		super(context, new VideoScene(descriptor));
	}

	public void init() {
		// MESHES
		MESH_SPHERE = Assets.loadMesh("sphere.obj");
		MESH_BOX = Assets.loadMesh("box.obj");
		MESH_PLAYBACK = Assets.loadMesh("playback.obj");
		MESH_STATUS = Assets.loadMesh("status.obj");
		// SHADERS
		SHADER_MOVIE_TEXTURE = Assets.initShader(new UnlitShader(), "movie texture");
		SHADER_OPACITY_SHADER = Assets.initShader(new OpacityShader(), "unlit texture");
		SHADER_UNLIT_TEXTURE = Assets.initShader(new UnlitShader(), "unlit texture");
		SHADER_COLOR = Assets.initShader(new ColorShader(), "color");
		// TEXURES
		VIDEOTEXTURE_VIDEO = Assets.loadVideoTexture();
		TEXTURE_DEBUG = Assets.loadTexture("debug.png");
		TEXTURE_FONT = Assets.loadTexture("font.png");
		TEXTURE_PLAYBACK = Assets.loadTexture("player.jpg");
		TEXTURE_FILMROLL = Assets.loadTexture("filmroll.jpg");
		// SOUNDS
		// SOUND_TEST = Assets.loadSound("test.mp3");
	}

	@Override
	public void loadAssets() {
		// Assets.setLoader(new DataLoader());
		init();
	}

}
