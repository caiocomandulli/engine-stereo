package com.comandulli.engine.panoramic.playback.engine.exception;

public class EngineRunningException extends RuntimeException {

	private static final long serialVersionUID = -687274114294508334L;

	public EngineRunningException() {
		super();
	}
	
	public EngineRunningException(String error) {
		super(error);
	}
}
