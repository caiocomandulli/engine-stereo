package com.comandulli.engine.panoramic.playback.engine.component;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.core.Time;

import android.util.Log;

public class FPSCounter extends Component {

	float startTime;
	int frames;

    @Override
	public void start() {
		startTime = Time.time;
	}

	@Override
	public void update() {
		super.update();
		frames++;
		if (Time.time - startTime > 1.0f) {
			Log.d("BEE", "FPS:" + frames);
			frames = 0;
			startTime = Time.time;
		}
	}

}
