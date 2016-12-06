package com.comandulli.engine.panoramic.playback.entity.playback;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.WindowManager;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.TextRenderer;

public class PlaybackControlsFlat extends Entity {

    private float volume = 0.9f;
    private float brightness = 1.0f;

    private Activity activity;
    private final MediaPlayer player;
    private Runnable callback;

    private int boxMesh;
    private int playbackTexture;
    private int unlitShader;
    private int fontTexture;
    private int seekMesh;
    private int seekShader;

    private static final Vector3 DEFAULT_SIZE = new Vector3(70.0f, 70.0f, 0.1f);
    private static final Vector3 VECTOR_POS_BRIG = new Vector3(-793.0f, -49.0f, 900.0f);
    private static final Vector3 VECTOR_NEG_BRIG = new Vector3(-592.0f, -49.0f, 900.0f);
    private static final Vector3 VECTOR_PLAY = new Vector3(-448.0f, -49.0f, 900.0f);
    private static final Vector3 VECTOR_POS_VOL = new Vector3(-314.0f, -49.0f, 900.0f);
    private static final Vector3 VECTOR_NEG_VOL = new Vector3(-131.0f, -49.0f, 900.0f);
    private static final Vector3 VECTOR_BACK = new Vector3(-48.0f, -49.0f, 900.0f);

    private static final Vector3 VECTOR_IND_VOL = new Vector3(280.0f, 53.0f, 899.0f);
    private static final Vector3 VECTOR_IND_BRIG = new Vector3(760.0f, 53.0f, 899.0f);

    private static final float TEXT_SCALE = 0.02f;

    private ControlBox backControl;
    private ControlBox minusVol;
    private ControlBox plusVol;
    private ControlBox playControl;
    private ControlBox minusBright;
    private ControlBox plusBright;
    private Entity volIndicatorText;
    private Entity brightIndicatorText;
    private Entity seek;

    public PlaybackControlsFlat(Activity activity, String name, MediaPlayer mediaPlayer, int boxMesh, int playbackTexture, int unlitShader, int fontTexture, int seekMesh, int seekShader) {
        super(name);
        this.activity = activity;
        this.player = mediaPlayer;
        this.boxMesh = boxMesh;
        this.playbackTexture = playbackTexture;
        this.unlitShader = unlitShader;
        this.fontTexture = fontTexture;
        this.seekMesh = seekMesh;
        this.seekShader = seekShader;

        Material testMaterial = new Material(playbackTexture, unlitShader);
        testMaterial.textureTiling = new Vector2(-1.0f, 1.0f);
        addComponent(new MeshRenderer(boxMesh, testMaterial));
        transform.position = new Vector3(0.0f, 275.0f, 900.0f);
        transform.scale = new Vector3(459.0f, 85.0f, 0.01f);
    }

    @Override
    public void start() {
        super.start();
        // elements
        brightness = activity.getWindow().getAttributes().screenBrightness;
        if (brightness < 0.0f) {
            brightness = -brightness;
        }

        final Material indicatorMaterial = new Material(fontTexture, unlitShader, Color.WHITE);

        volIndicatorText = new Entity("VolInd");
        volIndicatorText.addComponent(new TextRenderer(String.valueOf(Math.round(volume * 10)), TEXT_SCALE, TextRenderer.Alignment.CENTER, indicatorMaterial));
        volIndicatorText.transform.position = VECTOR_IND_VOL;
        volIndicatorText.transform.rotate(new Vector3(0.0f, 0.0f, (float) Math.toRadians(180.0f)));

        brightIndicatorText = new Entity("BrightInd");
        brightIndicatorText.addComponent(new TextRenderer(String.valueOf(Math.round(brightness * 10 - 1)), TEXT_SCALE, TextRenderer.Alignment.CENTER, indicatorMaterial));
        brightIndicatorText.transform.position = VECTOR_IND_BRIG;
        brightIndicatorText.transform.rotate(new Vector3(0.0f, 0.0f, (float) Math.toRadians(180.0f)));

        backControl = new ControlBox("Back", VECTOR_BACK, DEFAULT_SIZE, 0.0f) {
            @Override
            public void action() {
                super.action();
                if (callback != null) {
                    callback.run();
                }
            }
        };
        minusVol = new ControlBox("Minus Volume", VECTOR_NEG_VOL, DEFAULT_SIZE, 0.0f) {
            @Override
            public void action() {
                super.action();
                volume -= 0.1f;
                if (volume < 0.0f) {
                    volume = 0.0f;
                }
                player.setVolume(volume, volume);
                volIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(volume * 10)), TEXT_SCALE, TextRenderer.Alignment.CENTER, indicatorMaterial);
                volIndicatorText.addComponent(volIndicatorText.renderer);
            }
        };
        plusVol = new ControlBox("Plus Volume", VECTOR_POS_VOL, DEFAULT_SIZE, 0.0f) {
            @Override
            public void action() {
                super.action();
                volume += 0.1f;
                if (volume > 0.9f) {
                    volume = 0.9f;
                }
                player.setVolume(volume, volume);
                volIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(volume * 10)), TEXT_SCALE, TextRenderer.Alignment.CENTER, indicatorMaterial);
                volIndicatorText.addComponent(volIndicatorText.renderer);
            }
        };
        playControl = new ControlBox("Play", VECTOR_PLAY, DEFAULT_SIZE, 0.0f) {
            @Override
            public void action() {
                super.action();
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                }
            }
        };
        minusBright = new ControlBox("Minus Brightness", VECTOR_NEG_BRIG, DEFAULT_SIZE, 0.0f) {
            @Override
            public void action() {
                super.action();
                final WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
                brightness -= 0.1f;
                if (brightness < 0.1f) {
                    brightness = 0.1f;
                }
                layout.screenBrightness = brightness;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.getWindow().setAttributes(layout);
                    }
                });
                brightIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(brightness * 10 - 1)), TEXT_SCALE, TextRenderer.Alignment.CENTER, indicatorMaterial);
                brightIndicatorText.addComponent(brightIndicatorText.renderer);
            }
        };
        plusBright = new ControlBox("Plus Brightness", VECTOR_POS_BRIG, DEFAULT_SIZE, 0.0f) {
            @Override
            public void action() {
                super.action();
                final WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
                brightness += 0.1f;
                if (brightness > 1.0f) {
                    brightness = 1.0f;
                }
                layout.screenBrightness = brightness;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.getWindow().setAttributes(layout);
                    }
                });
                brightIndicatorText.renderer = new TextRenderer(String.valueOf(Math.round(brightness * 10 - 1)), TEXT_SCALE, TextRenderer.Alignment.CENTER, indicatorMaterial);
                brightIndicatorText.addComponent(brightIndicatorText.renderer);
            }
        };

        seek = new SeekControlFlat("Seek", player, camera);
        seek.addComponent(new MeshRenderer(seekMesh, new Material(seekShader, Color.WHITE)));
        Engine.getScene().addEntity(seek);

        Engine.getScene().addEntity(minusVol);
        Engine.getScene().addEntity(volIndicatorText);
        Engine.getScene().addEntity(plusVol);
        Engine.getScene().addEntity(playControl);
        Engine.getScene().addEntity(backControl);
        Engine.getScene().addEntity(minusBright);
        Engine.getScene().addEntity(brightIndicatorText);
        Engine.getScene().addEntity(plusBright);

        Engine.getScene().addEntity(new ControlBox("TEST", new Vector3(), Vector3.SCALE_ONE.copy().scale(100.0f), 0.0f) {
            @Override
            public void action() {
                Log.w("CLICK", "MATEST");
                super.action();
            }
        });
    }

    private boolean doOnce = true;

    @Override
    public void update() {
        super.update();

        if (doOnce) {
            doOnce = false;
            float height = Engine.getHeight();
            float calculatedHeight = height * 0.1f;
            float calculatedWidth = calculatedHeight * 6.46f;
            float hudYPos = height / 2 - calculatedHeight;
            float currentScale = calculatedHeight / 142.0f;
            float offset = calculatedWidth / 2;

            transform.position = new Vector3(0.0f, hudYPos, 900.0f);
            transform.scale = new Vector3(calculatedWidth, calculatedHeight, 0.01f);
            minusVol.transform.position = Vector3.scaled(VECTOR_NEG_VOL, currentScale).add(new Vector3(offset, -hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));
            plusVol.transform.position = Vector3.scaled(VECTOR_POS_VOL, currentScale).add(new Vector3(offset, -hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));
            playControl.transform.position = Vector3.scaled(VECTOR_PLAY, currentScale).add(new Vector3(offset, -hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));
            backControl.transform.position = Vector3.scaled(VECTOR_BACK, currentScale).add(new Vector3(offset, -hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));
            minusBright.transform.position = Vector3.scaled(VECTOR_NEG_BRIG, currentScale).add(new Vector3(offset, -hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));
            plusBright.transform.position = Vector3.scaled(VECTOR_POS_BRIG, currentScale).add(new Vector3(offset, -hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));

            brightIndicatorText.transform.position = Vector3.scaled(VECTOR_IND_BRIG, currentScale).add(new Vector3(-offset, hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));
            volIndicatorText.transform.position = Vector3.scaled(VECTOR_IND_VOL, currentScale).add(new Vector3(-offset, hudYPos, 0.0f)).scale(new Vector3(2.0f, 1.0f, 1.0f));

            minusVol.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);
            plusVol.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);
            playControl.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);
            backControl.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);
            minusBright.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);
            plusBright.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);

            brightIndicatorText.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);
            volIndicatorText.transform.scale = Vector3.scaled(DEFAULT_SIZE, currentScale);

            seek.transform.scale = Vector3.scaled(Vector3.scaled((Vector3.SCALE_ONE), 2.0f), currentScale);
            seek.transform.position = new Vector3(0.0f, SeekControlFlat.OFFSET_Y * currentScale + hudYPos, 800.0f);
        }
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }
}
