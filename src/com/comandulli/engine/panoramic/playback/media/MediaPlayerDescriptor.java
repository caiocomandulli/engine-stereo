package com.comandulli.engine.panoramic.playback.media;

import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;

import android.media.MediaPlayer;

public interface MediaPlayerDescriptor {

	MediaPlayer createMediaPlayer();
	Subtitle createSubtitle();
	ChapterData createChapterData();
	int getVideoMode();
	
}
