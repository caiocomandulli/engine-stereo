package com.comandulli.engine.panoramic.playback.engine.render;

import com.comandulli.engine.panoramic.playback.engine.input.Input;
import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent;
import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent.TouchType;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceRendererView extends GLSurfaceView {

	public SurfaceRendererView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		TouchEvent touch = new TouchEvent(event.getX(), event.getY());
		touch.type = TouchType.STILL;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touch.type = TouchType.DOWN;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			touch.type = TouchType.UP;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			touch.type = TouchType.MOVE;
		}
		Input.registerTouch(touch);
		performClick();
		return true;
	}

}
