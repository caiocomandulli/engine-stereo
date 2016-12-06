package com.comandulli.engine.panoramic.playback.media;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;

import java.io.IOException;

/**
 * Created by Caio on 06-Jul-16.
 */
public class StreamingMediaDescriptor implements MediaPlayerDescriptor {

    private final int videoMode;
    private final Context context;
    private final String url;

    public StreamingMediaDescriptor(Context context, int videoMode, String url) {
        this.context = context;
        this.videoMode = videoMode;
        this.url = url;
    }

    @Override
    public MediaPlayer createMediaPlayer() {
        MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(context, Uri.parse(url));
        } catch (IOException e) {
            throw new AssetNotFoundException(e);
        }
        return player;
    }

    @Override
    public Subtitle createSubtitle() {
        return null;
    }

    @Override
    public ChapterData createChapterData() {
        return null;
    }

    @Override
    public int getVideoMode() {
        return videoMode;
    }

}
