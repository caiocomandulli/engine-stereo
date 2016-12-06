package com.comandulli.engine.panoramic.playback.media;

import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;

public class MediaNotFoundException extends AssetNotFoundException {

	private static final long serialVersionUID = 8592900467111633541L;

	public MediaNotFoundException() {
		super("Required asset not available.");
	}
	
	public MediaNotFoundException(String argument) {
		super(argument);
	}

	public MediaNotFoundException(Throwable throwable) {
		super(throwable);
	}
}
