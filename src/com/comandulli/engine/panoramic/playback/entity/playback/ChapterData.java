package com.comandulli.engine.panoramic.playback.entity.playback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChapterData {

	private final List<Chapter> chapters;

	public ChapterData(InputStream is) throws IOException {
		chapters = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		Chapter currentChapter = null;
		boolean timeFilled = false;
		while (reader.ready()) {
			String line = reader.readLine();
			if (line == null) {
                break;
            }
			if (currentChapter == null) {
				if (line.isEmpty()) {
					continue;
				}
				currentChapter = new Chapter();
                currentChapter.number = Integer.parseInt(line);
				timeFilled = false;
			} else {
				if (!timeFilled) {
                    currentChapter.timestamp = Float.parseFloat(line);
					timeFilled = true;
				} else {
					if (line.isEmpty()) {
						chapters.add(currentChapter);
						currentChapter = null;
					} else {
						if (currentChapter.name == null) {
							currentChapter.name = line;
						} else {
							currentChapter.name += " " + line;
						}
					}
				}
			}
		}
		if (currentChapter != null) {
			chapters.add(currentChapter);
		}
        reader.close();
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public class Chapter {
		public String name;
		public float timestamp;
		public int number;
	}

}
