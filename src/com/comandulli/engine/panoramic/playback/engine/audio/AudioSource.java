package com.comandulli.engine.panoramic.playback.engine.audio;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;

public class AudioSource extends Component {

	private final boolean playOnStart;
	private final boolean loop;
	private float left;
	private float right;
	private final float rate;
	private final int priority;
	private final AudioSound audio;

	public AudioSource(AudioSound audio, boolean playOnStart, boolean loop) {
		this.playOnStart = playOnStart;
		this.loop = loop;
		this.audio = audio;
		this.rate = 1.0f;
		this.priority = 1;
	}

	@Override
	public void start() {
		super.start();
		if (playOnStart) {
			audio.play(left, right, loop, rate, priority);
		}
	}

	@Override
	public void update() {
		super.update();
		audio.setVolume(left, right);
	}

	public void determinePan(AudioListener listener) {
		Vector3 listenPosition = listener.entity.transform.position;
		Vector3 listenNormalDirection = Vector3.DIRECTION_LEFT.copy().rotate(listener.entity.transform.rotation.toMatrix());
		Vector3 soundPosition = entity.transform.position;

		Vector3 delta = Vector3.deltaVector(listenPosition, soundPosition);
		float cosAngle = Vector3.dot(listenNormalDirection, delta);

		this.left = cosAngle;
		this.right = 1 - cosAngle;
	}

	public void play() {
		audio.play(left, right, loop, rate, priority);
	}

	public void stop() {
		audio.stop();
	}

	@Override
    public void pause() {
		audio.pause();
	}

	@Override
    public void resume() {
		audio.resume();
	}

	@Override
	protected void register() {
		super.register();
		Audio.registerSource(this);
	}

	@Override
	protected void unregister() {
		super.unregister();
		Audio.removeSource(this);
	}

}
