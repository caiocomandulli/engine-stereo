package com.comandulli.engine.panoramic.playback.entity.playback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Subtitle {

	private SubLine[] subs;

	private int currentIndex;
	private SubLine currentSub;
	private boolean isRunning;

	public Subtitle(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		List<SubLine> sublines = new ArrayList<>();
		SubLine currentSubline = null;
		boolean timeFilled = false;
		while (reader.ready()) {
			String line = reader.readLine();
			if (line == null) {
                break;
            }
			if (currentSubline == null) {
				if (line.isEmpty()) {
					continue;
				}
				currentSubline = new SubLine();
                currentSubline.index = Integer.parseInt(line);
				timeFilled = false;
			} else {
				if (!timeFilled) {
					int first = line.indexOf(" ");
					int last = line.lastIndexOf(" ");
					String startString = line.substring(0, first);
					String endString = line.substring(last + 1, line.length());
					currentSubline.start = convertToSeconds(startString);
					currentSubline.end = convertToSeconds(endString);
					timeFilled = true;
				} else {
					if (line.isEmpty()) {
						sublines.add(currentSubline);
						boolean lookForSpecials = true;
						while (lookForSpecials) {
							int opening = currentSubline.text.indexOf("<");
							int closing = currentSubline.text.indexOf(">");
							if (opening != -1 && closing != -1) {
								currentSubline.text = currentSubline.text.substring(0, opening) + currentSubline.text.substring(closing + 1, currentSubline.text.length());
							} else {
								lookForSpecials = false;
							}
						}
						currentSubline = null;
					} else {
						if (currentSubline.text == null) {
							currentSubline.text = line;
						} else {
							currentSubline.text += " " + line;
						}
					}
				}
			}
		}
		if (currentSubline != null) {
			sublines.add(currentSubline);
		}

		subs = new SubLine[sublines.size()];
		for (SubLine subLine : sublines) {
			subs[subLine.index - 1] = subLine;
		}
        reader.close();
	}

	private float convertToSeconds(String text) {
		float seconds = 0.0f;
		String[] values = text.split(":");
		seconds += Integer.parseInt(values[0]) * 3600.0f;
		seconds += Integer.parseInt(values[1]) * 60.0f;
		seconds += Float.parseFloat(values[2].replace(",", "."));
		return seconds;
	}

	public void init(float time) {
		currentIndex = 0;
		currentSub = subs[currentIndex];
		isRunning = true;
		while (currentSub.end < time) {
			currentIndex++;
			if (currentIndex >= subs.length) {
				isRunning = false;
				break;
			} else {
				currentSub = subs[currentIndex];
			}
		}
	}

	public String getSubText(float time) {
		if (isRunning) {
			if (time > currentSub.end) {
				currentIndex++;
				if (currentIndex >= subs.length) {
					isRunning = false;
					return "";
				}
				currentSub = subs[currentIndex];
				return "";
			} else if (time > currentSub.start) {
				return currentSub.text;
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	public class SubLine {

		public int index;
		public float start;
		public float end;
		public String text;

	}

}
