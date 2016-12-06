package com.comandulli.engine.panoramic.playback.media;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaPlayer;

import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;

public class ResourceMediaDescriptor implements MediaPlayerDescriptor {

	private final Context context;
	private final int videoResource;
	private final int subtitleResource;
	private final int chapterResource;
	private final int videoMode;

	public ResourceMediaDescriptor(Context context, int resource, int subsResource, int chapterResource, int videoMode) {
		this.context = context;
		this.videoResource = resource;
		this.subtitleResource = subsResource;
		this.chapterResource = chapterResource;
		this.videoMode = videoMode;
	}

	@Override
	public MediaPlayer createMediaPlayer() {
		return MediaPlayer.create(context, videoResource);
	}

	@Override
	public Subtitle createSubtitle() {
		if (subtitleResource == 0) {
            return null;
        }
		InputStream stream = context.getResources().openRawResource(subtitleResource);
		Subtitle subtitle;
		try {
			subtitle = new Subtitle(stream);
		} catch (IOException e) {
			return null;
		}
		subtitle.init(0.0f);
		return subtitle;
	}

	@Override
	public ChapterData createChapterData() {
		if (chapterResource == 0) {
            return null;
        }
		ChapterData cpd;
		try {
			InputStream stream = context.getResources().openRawResource(chapterResource);
			cpd = new ChapterData(stream);
			cpd.getChapters();
			return cpd;
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public int getVideoMode() {
		return videoMode;
	}

}
