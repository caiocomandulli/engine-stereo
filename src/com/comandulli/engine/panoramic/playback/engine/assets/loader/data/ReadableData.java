package com.comandulli.engine.panoramic.playback.engine.assets.loader.data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Hashtable;

import com.comandulli.engine.panoramic.playback.engine.exception.DataFileException;

public class ReadableData extends Data {

	private Hashtable<String, FileHeader> memIndex;

	public ReadableData(String dbPath) throws IOException, DataFileException {
		File file = new File(dbPath);
		if (!file.exists()) {
			throw new DataFileException("Database not found: " + dbPath);
		}
		accessDataFile = new RandomAccessFile(file, "rw");
		int numFiles = readNumRecordsHeader();
		memIndex = new Hashtable<>(numFiles);
		for (int i = 0; i < numFiles; i++) {
			String key = readKeyFromIndex(i);
			FileHeader header = readFileHeaderFromIndex(i);
			header.setIndexPosition(i);
			memIndex.put(key, header);
		}
	}

	public synchronized ReadableFile readFile(String key) throws IOException {
		byte[] data = readData(key);
		return new ReadableFile(key, data);
	}

	public synchronized void close() throws IOException {
		try {
			accessDataFile.close();
		} finally {
			accessDataFile = null;
			memIndex.clear();
			memIndex = null;
		}
	}

	private FileHeader readFileHeaderFromIndex(int position) throws IOException {
		accessDataFile.seek(indexPositionToRecordHeaderFilePosition(position));
		return FileHeader.readHeader(accessDataFile);
	}

	private String readKeyFromIndex(int position) throws IOException {
		accessDataFile.seek(indexPositionToKeyFilePosition(position));
		return accessDataFile.readUTF();
	}

	private int readNumRecordsHeader() throws IOException {
		accessDataFile.seek(HEADER_LOCATION);
		return accessDataFile.readInt();
	}

	private byte[] readData(String key) throws IOException {
		FileHeader header = memIndex.get(key);
		if (header == null) {
			throw new DataFileException("Key not found: " + key);
		}
		byte[] buf = new byte[header.dataCount];
		accessDataFile.seek(header.dataPointer);
		accessDataFile.readFully(buf);
		return buf;
	}
	
	public FileHeader getFileHeader(String key) {
		return memIndex.get(key);
	}
}
