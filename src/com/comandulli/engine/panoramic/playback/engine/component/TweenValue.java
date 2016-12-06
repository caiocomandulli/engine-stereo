package com.comandulli.engine.panoramic.playback.engine.component;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.core.Time;

public abstract class TweenValue extends Component {

	private final float initialValue;
	private final float targetValue;
	private float timemark;
	private final float duration;
	private final float delay;

	public TweenValue(float initialValue, float targetValue, float duration, float delay) {
		this.initialValue = initialValue;
		this.duration = duration;
		this.delay = delay;
		this.targetValue = targetValue;
	}

	@Override
	public void start() {
		super.start();
		this.timemark = Time.time;
	}

	@Override
	public void update() {
		super.update();
		float delta = Time.time - timemark;
		if (delta > delay) {
			if (delta > duration + delay) {
				onValueUpdate(targetValue);
				entity.removeComponent(this);
			} else {
				float t = 1 - (duration - (delta - delay)) / duration;
				float lerped = initialValue + t * (targetValue - initialValue);
				onValueUpdate(lerped);
			}
		}
	}

	public abstract void onValueUpdate(float value);

}
