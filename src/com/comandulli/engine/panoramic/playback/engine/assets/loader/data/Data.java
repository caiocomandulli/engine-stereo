package com.comandulli.engine.panoramic.playback.engine.assets.loader.data;

import java.io.RandomAccessFile;

public class Data {

	protected RandomAccessFile accessDataFile;

	protected static final long HEADER_LOCATION = 0;
	protected static final int HEADER_LENGTH = 4;

	protected static final int MAX_KEY_LENGTH = 64;
	protected static final int PTR_LENGTH = 16;
	protected static final int INDEX_ENTRY_LENGTH = MAX_KEY_LENGTH + PTR_LENGTH;

	protected long indexPositionToKeyFilePosition(int pos) {
		return HEADER_LENGTH + INDEX_ENTRY_LENGTH * pos;
	}

	protected long indexPositionToRecordHeaderFilePosition(int pos) {
		return indexPositionToKeyFilePosition(pos) + MAX_KEY_LENGTH;
	}

}
