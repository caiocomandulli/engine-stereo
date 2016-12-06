package com.comandulli.engine.panoramic.playback.entity.focus;

import android.util.Log;

import com.comandulli.engine.panoramic.playback.engine.core.Component;
import com.comandulli.engine.panoramic.playback.engine.math.Vector2;

/**
 * Created by Caio on 21-Nov-16.
 */

public abstract class ClickableObject extends Component {

    public void dispatchClick(Vector2 click) {
        if (isEnabled()) {
            onClick(click);
        }
    }

    public abstract void onClick(Vector2 click);

}