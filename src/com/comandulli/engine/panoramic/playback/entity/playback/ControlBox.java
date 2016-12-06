package com.comandulli.engine.panoramic.playback.entity.playback;

import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.physics.BoxCollider;
import com.comandulli.engine.panoramic.playback.entity.focus.ClickableObject;
import com.comandulli.engine.panoramic.playback.entity.focus.InteractiveObject;

@SuppressWarnings("FieldCanBeLocal")
public class ControlBox extends Entity implements InteractiveObject {

	private PlaybackControls controls;
	private float lastOccurrence;
	public final float timeToFocus = 1.0f;

	public ControlBox(String name, Vector3 position, Vector3 scale, float angle) {
		super(name);
		transform.translate(position);
		transform.rotation = new Quaternion(0.0f, (float) Math.toRadians(angle), 0.0f);
		transform.scale(new Vector3(0.5f, 0.5f, 0.1f).scale(scale));
		addComponent(new BoxCollider(Vector3.scaled(Vector3.SCALE_ONE, 2.0f)));

        addComponent(new ClickableObject() {
            @Override
            public void onClick(Vector2 click) {
                action();
            }
        });
	}

	@Override
	public void FocusIn() {
		action();
	}

	@Override
	public void FocusOut() {
        if(this.controls != null) {
        this.controls.controlsInFocus = false;
	}
    }

	@Override
	public void FocusUpdate(float time) {
		if (Time.time > lastOccurrence + timeToFocus) {
			action();
		}
	}

	public void action() {
		lastOccurrence = Time.time;
	}

	@Override
	public float getTimeToFocus() {
		return timeToFocus;
	}

	@Override
	public void FocusStarted() {
        if(this.controls != null) {
		this.controls.controlsInFocus = true;
	}
    }

	@Override
	public void FocusCanceled() {
        if(this.controls != null) {
        this.controls.controlsInFocus = false;
	}
    }

	public ControlBox setControls(PlaybackControls controls) {
		this.controls = controls;
		return this;
	}

}
