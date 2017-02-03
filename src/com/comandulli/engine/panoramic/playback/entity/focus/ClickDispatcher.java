package com.comandulli.engine.panoramic.playback.entity.focus;

import android.util.Log;

import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.input.Input;
import com.comandulli.engine.panoramic.playback.engine.math.Color;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.physics.Physics;
import com.comandulli.engine.panoramic.playback.engine.physics.Ray;
import com.comandulli.engine.panoramic.playback.engine.render.material.Material;
import com.comandulli.engine.panoramic.playback.engine.render.renderer.MeshRenderer;

import java.util.List;

/**
 * Created by Caio on 21-Nov-16.
 */

public class ClickDispatcher extends Entity {

    private Entity ballEntity;

    private int debugMesh;
    private int debugShader;
    private boolean debugEnabled = false;

    public ClickDispatcher(String name, int debugMesh, int debugShader) {
        super(name);
        this.debugMesh = debugMesh;
        this.debugShader = debugShader;
    }

    @Override
    public void start() {
        super.start();
        ballEntity = new Entity("TOUCH_BALL");
        ballEntity.addComponent(new MeshRenderer(debugMesh, new Material(debugShader, Color.RED)));
        ballEntity.setEnabled(false);
        ballEntity.transform.scale.scale(1.0f);
        Engine.getScene().addEntity(ballEntity);
    }

    public void setDebug(boolean mode) {
        this.debugEnabled = mode;
    }

    @Override
    public void update() {
        super.update();
        if (Input.getTouchDown()) {
            Vector2 screen = new Vector2(Input.touch.x, Input.touch.y);
            Vector3 origin = new Vector3(screen.x - Engine.getWidth() / 2, screen.y - Engine.getHeight() / 2, 0.0f);
            Vector3 direction = Vector3.DIRECTION_FORWARD;
            Ray ray = new Ray(origin, direction);
            List<Entity> entityList = Physics.raycast(ray);
            Log.w("CLICK", "DISPATCHING AT:" + screen + " " + origin);
            if (!entityList.isEmpty()) {
                for (Entity entity : entityList) {
                    ClickableObject clickable = (ClickableObject) entity.getComponent(ClickableObject.class);
                    if (clickable != null) {
                        Log.w("CLICK", entity + " " + entity.transform);
                        clickable.dispatchClick(screen);
                    }
                }
            }
        }

        if (Input.getTouch() && debugEnabled) {
            ballEntity.setEnabled(debugEnabled);
            ballEntity.transform.position = new Vector3(-Input.touch.x + Engine.getWidth() / 2, -Input.touch.y + Engine.getHeight() / 2, 500.0f);
        } else {
            ballEntity.setEnabled(false);
        }
    }
}
