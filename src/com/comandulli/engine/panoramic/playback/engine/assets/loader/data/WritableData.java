package com.comandulli.engine.panoramic.playback.engine.assets.loader.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class WritableData extends Data {

	public WritableData(String dataPath, List<WritableFile> files) throws IOException, DataFileException {
		File dataFile = new File(dataPath);
		if (dataFile.exists()) {
			if (!dataFile.delete()) {
				throw new DataFileException("Error cant access path: " + dataPath);
			}
		}
		accessDataFile = new RandomAccessFile(dataFile, "rw");
		int numFiles = files.size();
		long dataStartPtr = indexPositionToKeyFilePosition(numFiles);
		accessDataFile.setLength(dataStartPtr);
		writeNumFilesHeader(numFiles);

		for (int i = 0; i < numFiles; i++) {
			insertFile(files.get(i), i);
		}
		close();
	}

	private synchronized void insertFile(WritableFile fileEntry, int index) throws DataFileException, IOException {
		String key = fileEntry.getKey();
		FileHeader newEntry = allocateFile(fileEntry.getDataLength());
		writeFileData(newEntry, fileEntry);
		addEntryToIndex(key, newEntry, index);
	}

	private synchronized void close() throws IOException {
		try {
			accessDataFile.close();
		} finally {
			accessDataFile = null;
		}
	}

	private void writeNumFilesHeader(int numRecords) throws IOException {
		accessDataFile.seek(HEADER_LOCATION);
		accessDataFile.writeInt(numRecords);
	}

	private FileHeader allocateFile(int dataLength) throws IOException {
        long fp = accessDataFile.length();
        accessDataFile.setLength(fp + dataLength);
		return new FileHeader(fp, dataLength);
	}

	private void writeFileData(FileHeader header, WritableFile rw) throws IOException, DataFileException {
		if (rw.getDataLength() > header.dataCapacity) {
			throw new DataFileException("File data does not fit");
		}
		header.dataCount = rw.getDataLength();
		accessDataFile.seek(header.dataPointer);
		rw.writeTo(accessDataFile);
	}

	private void addEntryToIndex(String key, FileHeader newRecord, int currentIndex) throws IOException, DataFileException {
		// byte[] keyBytes = key.getBytes();
		// if (keyBytes.length > MAX_KEY_LENGTH) {
		// throw new DataFileException("Key of " + key.getBytes().length +
		// " is larger than allowed size of " + MAX_KEY_LENGTH + " bytes");
		// }
		ByteArrayOutputStream temp = new ByteArrayOutputStream(MAX_KEY_LENGTH);
		DataOutputStream dos = new DataOutputStream(temp);
        dos.writeUTF(key);
        dos.close();
		if (temp.size() > MAX_KEY_LENGTH) {
			throw new DataFileException("Key is larger than permitted size of " + MAX_KEY_LENGTH + " bytes");
		}
		accessDataFile.seek(indexPositionToKeyFilePosition(currentIndex));
		byte[] data = temp.toByteArray();
		accessDataFile.write(data);

		accessDataFile.seek(indexPositionToRecordHeaderFilePosition(currentIndex));
		newRecord.write(accessDataFile);
		newRecord.setIndexPosition(currentIndex);
	}

}
