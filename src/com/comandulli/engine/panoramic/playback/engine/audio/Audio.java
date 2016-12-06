package com.comandulli.engine.panoramic.playback.engine.audio;

import java.util.ArrayList;
import java.util.List;

public class Audio {

	private static List<AudioListener> listeners;
	private static List<AudioSource> sources;

	public static void init() {
		listeners = new ArrayList<>();
		sources = new ArrayList<>();
	}

	public static void registerListener(AudioListener listener) {
		listeners.add(listener);
	}

	public static void removeListener(AudioListener listener) {
		listeners.remove(listener);
	}

	public static void registerSource(AudioSource source) {
		sources.add(source);
	}

	public static void removeSource(AudioSource source) {
		sources.remove(source);
	}

	public static void update() {
		for (AudioListener listener : listeners) {
			if (listener.isEnabled()) {
				for (AudioSource source : sources) {
					if (source.isEnabled()) {
						source.determinePan(listener);
					}
				}
			}
		}
	}

	public static void resume() {
		for (AudioSource source : sources) {
			if (source.isEnabled()) {
				source.resume();
			}
		}
	}

	public static void pause() {
		for (AudioSource source : sources) {
			if (source.isEnabled()) {
				source.pause();
			}
		}
	}

}
