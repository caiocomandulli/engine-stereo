package com.comandulli.engine.panoramic.playback.engine.core;

import java.util.Date;

public class Time {

	public static final float MILI_TO_SECONDS = 1 / 1000.0f;

	public static float time;
	public static float deltaTime;

	private static long startingTime;

	public static void start() {
		startingTime = getTime();
		time = 0.0f;
	}

	public static void update() {
		float currentTime = (getTime() - startingTime) * MILI_TO_SECONDS;
		deltaTime = currentTime - time;
		time = currentTime;
	}

	private static long getTime() {
		return new Date().getTime();
	}

}
