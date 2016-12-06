package com.comandulli.engine.panoramic.playback.engine.assets.loader.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ReadableFile {
	private final String key;
	private final byte[] data;
	private final ByteArrayInputStream in;

	public ReadableFile(String key, byte[] data) {
		this.key = key;
		this.data = data;
		in = new ByteArrayInputStream(data);
	}

	public String getKey() {
		return key;
	}

	public byte[] getData() {
		return data;
	}

	public InputStream getInputStream() {
		return in;
	}
}
