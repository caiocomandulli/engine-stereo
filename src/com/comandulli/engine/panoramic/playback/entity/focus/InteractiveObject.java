package com.comandulli.engine.panoramic.playback.entity.focus;

/**
 * Created by Caio on 21-Nov-16.
 */

public interface InteractiveObject {

    float getTimeToFocus();

    void FocusStarted();

    void FocusCanceled();

    void FocusIn();

    void FocusOut();

    void FocusUpdate(float time);
}
