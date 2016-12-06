package com.comandulli.engine.panoramic.playback.engine.input;

public class TouchEvent {

	public enum TouchType {
		UP, DOWN, STILL, MOVE, UPANDDOWN
	}

	public final float x;
	public final float y;
	public TouchType type;

	public TouchEvent(float x, float y) {
		this.x = x;
		this.y = y;
	}

}
