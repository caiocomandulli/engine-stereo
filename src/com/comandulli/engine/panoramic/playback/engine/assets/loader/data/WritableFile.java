package com.comandulli.engine.panoramic.playback.engine.assets.loader.data;

import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WritableFile {
	private final String key;
	private File input;

	public WritableFile(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void writeFile(File file) {
		input = file;
	}

	public int getDataLength() {
		return (int) input.length();
	}

	public void writeTo(DataOutput str) throws IOException {
		FileInputStream fin = new FileInputStream(input);
		byte[] data = new byte[getDataLength()];
        //noinspection ResultOfMethodCallIgnored
        fin.read(data);
		str.write(data);
		fin.close();
	}
}
