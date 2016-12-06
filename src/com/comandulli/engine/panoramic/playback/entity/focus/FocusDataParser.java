package com.comandulli.engine.panoramic.playback.entity.focus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class FocusDataParser {

	public static Hashtable<String, Vector3> readData(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Hashtable<String, Vector3> table = new Hashtable<>();
		while (reader.ready()) {
			try {
				String line = reader.readLine();
				if (line == null) {
                    break;
                }
				if (line.isEmpty()) {
                    continue;
                }
				int firstHash = line.indexOf("#");
				int openBrackets = line.indexOf("[");
				int closeBrackets = line.indexOf("]");
				String name = line.substring(firstHash + 1, openBrackets);
				int endOfX = line.indexOf(",");
				int endOfY = line.indexOf(",", endOfX + 1);
				float x = Float.parseFloat(line.substring(openBrackets + 1, endOfX));
				float y = Float.parseFloat(line.substring(endOfX + 1, endOfY));
				float z = Float.parseFloat(line.substring(endOfY + 1, closeBrackets));
				Vector3 position = new Vector3(x, y, z);
				table.put(name, position);
			} catch (NoSuchElementException e) {
				break;
			}
		}
        reader.close();
		return table;
	}
}
