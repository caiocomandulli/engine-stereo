package com.comandulli.engine.panoramic.playback.engine.render.camera;

/**
 * Created by Caio on 21-Nov-16.
 */

public class ScreenSpaceCamera extends Camera {

    public ScreenSpaceCamera() {
        super(Projection.Orthographic);
    }

    @Override
    public void adjustViewport(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;

        this.x = (int) (left * viewportWidth);
        this.y = (int) (bottom * viewportHeight);
        this.width = (int) ((right - left) * viewportWidth);
        this.height = (int) ((top - bottom) * viewportHeight);

        this.ortographicSize = this.height/2;

        final float ratio = (float) this.width / this.height;
        switch (projection) {
            case Orthographic:
                setOrthoProjection(-ratio * ortographicSize, ratio * ortographicSize, -1.0f * ortographicSize, 1.0f * ortographicSize, near, far);
                break;
            case Perspective:
                setPerspectiveProjection(fieldOfView, ratio, near, far);
                break;
        }
    }
}
