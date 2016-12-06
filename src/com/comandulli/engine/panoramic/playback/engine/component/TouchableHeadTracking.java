package com.comandulli.engine.panoramic.playback.engine.component;

import com.comandulli.engine.panoramic.playback.engine.core.Time;
import com.comandulli.engine.panoramic.playback.engine.input.Input;
import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent;
import com.comandulli.engine.panoramic.playback.engine.input.TouchEvent.TouchType;
import com.comandulli.engine.panoramic.playback.engine.math.Quaternion;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;

/**
 * Created by Caio on 08-Jul-16.
 */
public class TouchableHeadTracking extends HeadTracking {

    private static final float SCROLL_SPEED = 0.1f;
    private Float lastValue;
    private final Camera camera;

    private float totalRotationY;

    public TouchableHeadTracking(Camera camera) {
        super(camera);
        this.camera = camera;
    }

    @Override
    public void update() {
        super.update();
        touchValue();
    }

    private void touchValue() {
        TouchEvent event = Input.touch;
        if (event != null) {
            if (lastValue == null) {
                lastValue = event.x;
            }
            if (event.type == TouchType.MOVE) {
                float x = event.x;
                float diffX = x - lastValue;
                lastValue = x;
                totalRotationY += -diffX * Time.deltaTime * SCROLL_SPEED;
            } else if (event.type == TouchType.UP) {
                lastValue = null;
            }
        }
        camera.entity.transform.rotation = Quaternion.multiply(new Quaternion(0.0f, totalRotationY, 0.0f), camera.entity.transform.rotation);
    }
}
