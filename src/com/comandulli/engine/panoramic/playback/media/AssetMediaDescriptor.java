package com.comandulli.engine.panoramic.playback.media;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;

import android.media.MediaPlayer;

public class AssetMediaDescriptor implements MediaPlayerDescriptor {

	private final String videoFile;
	private final int videoMode;

	public AssetMediaDescriptor(String videoFile, int videoMode) {
		this.videoFile = videoFile;
		this.videoMode = videoMode;
	}

	@Override
	public MediaPlayer createMediaPlayer() {
		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(videoFile);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
		return player;
	}

	@Override
	public Subtitle createSubtitle() {
		Subtitle subtitle;
        try {
			String subFile = videoFile.substring(0, videoFile.lastIndexOf(".")) + ".srt";
            InputStream stream = new FileInputStream(subFile);
			subtitle = new Subtitle(stream);
			subtitle.init(0.0f);
            stream.close();
			return subtitle;
		} catch (IOException e) {
			return null;
		}
    }

	@Override
	public ChapterData createChapterData() {
		ChapterData cpd;
		try {
			String cpdFile = videoFile.substring(0, videoFile.lastIndexOf(".")) + ".cpd";
            InputStream stream = new FileInputStream(cpdFile);
			cpd = new ChapterData(stream);
			cpd.getChapters();
            stream.close();
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
