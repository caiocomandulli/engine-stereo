package com.comandulli.engine.panoramic.playback.engine.assets.loader.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FileHeader {

	protected long dataPointer;
	protected int dataCount;
	protected int dataCapacity;
	protected int indexPosition;

	protected FileHeader() {

	}

	protected FileHeader(long dataPointer, int dataCapacity) {
		if (dataCapacity < 1) {
			throw new IllegalArgumentException("Bad record size: " + dataCapacity);
		}
		this.dataPointer = dataPointer;
		this.dataCapacity = dataCapacity;
		this.dataCount = 0;
	}

	protected int getIndexPosition() {
		return indexPosition;
	}

	protected void setIndexPosition(int indexPosition) {
		this.indexPosition = indexPosition;
	}

	public int getDataCapacity() {
		return dataCapacity;
	}
	
	public long getDataPosition() {
		return dataPointer;
	}

	public int getDataLength() {
		return dataCount;
	}

	protected int getFreeSpace() {
		return dataCapacity - dataCount;
	}

	protected void read(DataInput in) throws IOException {
		dataPointer = in.readLong();
		dataCapacity = in.readInt();
		dataCount = in.readInt();
	}

	protected void write(DataOutput out) throws IOException {
		out.writeLong(dataPointer);
		out.writeInt(dataCapacity);
		out.writeInt(dataCount);
	}

	protected static FileHeader readHeader(DataInput in) throws IOException {
		FileHeader r = new FileHeader();
		r.read(in);
		return r;
	}

}
