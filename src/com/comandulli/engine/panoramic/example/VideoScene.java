package com.comandulli.engine.panoramic.example;

import android.media.MediaPlayer;

import com.comandulli.engine.panoramic.playback.engine.component.FPSCounter;
import com.comandulli.engine.panoramic.playback.engine.component.HeadTracking;
import com.comandulli.engine.panoramic.playback.engine.core.Engine;
import com.comandulli.engine.panoramic.playback.engine.core.Entity;
import com.comandulli.engine.panoramic.playback.engine.core.Scene;
import com.comandulli.engine.panoramic.playback.engine.math.Vector3;
import com.comandulli.engine.panoramic.playback.engine.render.camera.Camera.Projection;
import com.comandulli.engine.panoramic.playback.engine.render.camera.StereoscopicCamera;
import com.comandulli.engine.panoramic.playback.entity.VideoPlayer;
import com.comandulli.engine.panoramic.playback.entity.component.Logger;
import com.comandulli.engine.panoramic.playback.entity.focus.FocusInteraction;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.PlaybackControls;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;
import com.comandulli.engine.panoramic.playback.media.MediaPlayerDescriptor;

public class VideoScene extends Scene {

    private MediaPlayerDescriptor descriptor;

    public VideoScene(MediaPlayerDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void init() {
        // Descriptor
        MediaPlayer player = descriptor.createMediaPlayer();


        Subtitle subtitle = descriptor.createSubtitle();
        ChapterData cpd = descriptor.createChapterData();
        // stereo camera
        Entity camera = new Entity("StereoCamera");
        StereoscopicCamera cameraComponent = new StereoscopicCamera(Projection.Perspective);
        camera.addComponent(cameraComponent);
        cameraComponent.setViewport(0.0f, 1.0f, 0.0f, 1.0f, 0.1f, 100.0f);
        cameraComponent.setInterpupillaryDistance(0.06f);
        addEntity(camera);
        // logger
        Entity logger = new Entity("Logger", new Logger(VideoPlayerEngine.TEXTURE_FONT, VideoPlayerEngine.SHADER_UNLIT_TEXTURE));
        addEntity(logger);
        // counter
        Entity counter = new Entity("FPS Counter", new FPSCounter());
        addEntity(counter);
        // headtracking
        Entity headTracking = new Entity("HeadTracking", new HeadTracking(cameraComponent));
        addEntity(headTracking);
        // player
        final VideoPlayer videoPlayer = new VideoPlayer("VideoPlayer", player, cameraComponent, descriptor.getVideoMode(), VideoPlayerEngine.VIDEOTEXTURE_VIDEO, VideoPlayerEngine.SHADER_MOVIE_TEXTURE, VideoPlayerEngine.MESH_SPHERE);
        addEntity(videoPlayer);
        videoPlayer.transform.scale = new Vector3(0.05f, 0.05f, 0.05f);
        videoPlayer.setCallback(new Runnable() {
            @Override
            public void run() {

            }
        }, true);
        // playback controls
        PlaybackControls playbackControls = new PlaybackControls("Test", player, Engine.getContext(), cameraComponent, VideoPlayerEngine.MESH_PLAYBACK, VideoPlayerEngine.TEXTURE_PLAYBACK, VideoPlayerEngine.SHADER_OPACITY_SHADER, VideoPlayerEngine.TEXTURE_FONT, VideoPlayerEngine.MESH_STATUS,
                VideoPlayerEngine.SHADER_COLOR, VideoPlayerEngine.TEXTURE_FILMROLL, VideoPlayerEngine.MESH_BOX);
        addEntity(playbackControls);
        addEntity(new FocusInteraction("FocusInteraction", cameraComponent, VideoPlayerEngine.MESH_BOX, VideoPlayerEngine.SHADER_COLOR));
        playbackControls.setSubtitle(subtitle);
        playbackControls.setChapterData(cpd);
        playbackControls.setCallback(new Runnable() {
            @Override
            public void run() {
                Engine.getContext().finish();
            }
        });
    }
}
