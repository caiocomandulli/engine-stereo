package com.comandulli.engine.panoramic.playback.engine.assets.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class Kompressor {

	private static final int BUFFER = 2048;

	public static void zip(String folder, String zipFile) {
		File directory = new File(folder);
		if (directory.isDirectory()) {
			List<String> filesList = new ArrayList<>();
			listFiles(directory, filesList, "");
			String[] files = new String[filesList.size()];
			filesList.toArray(files);
			try {
				BufferedInputStream origin;
				FileOutputStream dest = new FileOutputStream(zipFile);
				ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
				byte data[] = new byte[BUFFER];
                for (String file : files) {
                    FileInputStream fi = new FileInputStream(folder + file);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(file);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                    fi.close();
                }
				out.close();
				dest.flush();
				dest.close();
			} catch (Exception e) {
				Log.e("Compress", "zip", e);
			}
		}
	}

	private static void listFiles(File directory, List<String> filesList, String append) {
		String[] children = directory.list();
		for (String fileName : children) {
			File child = new File(directory, fileName);
			if (child.isDirectory()) {
				listFiles(child, filesList, append + "/" + fileName);
			} else {
				filesList.add(append + "/" + fileName);
			}
		}
	}

	private static int lastIndex;

	public static File unzip(String fileName, String location, String zipFile) {
		try {
			FileInputStream fin = new FileInputStream(location + zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				if (!ze.isDirectory()) {
					if (ze.getName().equals(fileName)) {
						File tempDir = new File(location + "/temp");
						if(!tempDir.mkdir()) {
                            Log.w("Folder creation", "Failed to create folder");
                        }
						File temp = new File(location + "/temp/" + lastIndex + ".tmp");
						lastIndex++;
						FileOutputStream out = new FileOutputStream(temp);
						for (int c = zin.read(); c != -1; c = zin.read()) {
							out.write(c);
						}
						zin.closeEntry();
						out.close();
						return temp;
					}
				}
			}
            fin.close();
			zin.close();
		} catch (Exception e) {
			Log.e("Decompress", "unzip", e);
		}
		return null;
	}

}
