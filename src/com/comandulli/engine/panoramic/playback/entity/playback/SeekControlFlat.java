package com.comandulli.engine.panoramic.playback.entity.playback;

import android.media.MediaPlayer;
import android.util.Log;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.input.Input;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera;

public class SeekControlFlat extends Entity {

    private final Camera camera;
    private final MediaPlayer player;

    private boolean isInFocus;
    private float timemark;

    public final static int OFFSET_Y = -100;
    public final static int WIDTH = 846;
    public final static int HEIGHT = 22;

    public SeekControlFlat(String name, MediaPlayer player, Camera camera) {
        super(name);
        this.camera = camera;
        this.player = player;
    }

    @Override
    public void update() {
        super.update();

        int duration;
        float progress;
        try {
            duration = player.getDuration();
            progress = (float) player.getCurrentPosition() / duration;
        } catch (IllegalStateException e) {
            return;
        }
        float scaledWidth = transform.scale.x * WIDTH;
        transform.position.x = (-0.5f + progress) * scaledWidth;

        if (Input.getTouchDown()) {
            Vector2 screen = new Vector2(Input.touch.x, Input.touch.y);
            Vector2 origin = new Vector2(screen.x - Engine.getWidth() / 2, screen.y - Engine.getHeight() / 2);

            float halfWidth = scaledWidth / 2;

            if (origin.x > -halfWidth && origin.x < halfWidth) {
                float percentage = 1 - (origin.x + halfWidth) / scaledWidth;
                float scaledHalfHeight = HEIGHT * transform.scale.y;
                if (-origin.y > transform.position.y - scaledHalfHeight && -origin.y < transform.position.y + scaledHalfHeight) {
                    Log.e("HEY", percentage + " * " + duration);
                    player.seekTo((int) (percentage * duration));
                }
            }
        }
    }
}
