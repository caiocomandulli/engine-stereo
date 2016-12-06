package com.comandulli.engine.panoramic.playback.engine.exception;

public class AssetNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3752291001386559710L;

	public AssetNotFoundException() {
		super("Required asset not available.");
	}
	
	public AssetNotFoundException(String argument) {
		super(argument);
	}

	public AssetNotFoundException(Throwable throwable) {
		super(throwable);
	}

}
