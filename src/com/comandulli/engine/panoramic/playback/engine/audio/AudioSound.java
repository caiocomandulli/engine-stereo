package com.comandulli.engine.panoramic.playback.engine.audio;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.InputStream;

public class AudioSound {

	private static final int LOOP_FOREVER = -1;
	private static final int NO_LOOP = 0;
	private static final int DEFAULT_QUALITY = 0;
	private static final int MAX_STREAMS_PER_POOL = 1;
	private static final int DEFAULT_PRIORITY = 1;

	private int soundId;
	private SoundPool pool;

    public AudioSound(InputStream inputStream) {
        // TODO: input stream loading
    }

	@SuppressWarnings("deprecation")
    public AudioSound(AssetFileDescriptor afd) {
		pool = new SoundPool(MAX_STREAMS_PER_POOL, AudioManager.STREAM_MUSIC, DEFAULT_QUALITY);
		soundId = pool.load(afd, DEFAULT_PRIORITY);
	}

	@SuppressWarnings("deprecation")
    public AudioSound(String path) {
		pool = new SoundPool(MAX_STREAMS_PER_POOL, AudioManager.STREAM_MUSIC, DEFAULT_QUALITY);
		soundId = pool.load(path, DEFAULT_PRIORITY);
	}

	public void play(float left, float right, boolean loop, float rate, int priority) {
		pool.play(soundId, left, right, priority, loop ? LOOP_FOREVER : NO_LOOP, rate);
	}

	public void setVolume(float left, float right) {
		pool.setVolume(soundId, left, right);
	}

	public void stop() {
		pool.stop(soundId);
	}

	public void pause() {
		pool.pause(soundId);
	}

	public void resume() {
		pool.resume(soundId);
	}

	public void setLoop(boolean loop) {
		pool.setLoop(soundId, loop ? LOOP_FOREVER : NO_LOOP);
	}

	public void setRate(float rate) {
		pool.setRate(soundId, rate);
	}

	public void setPriority(int priority) {
		pool.setPriority(soundId, priority);
	}

}
