package com.comandulli.engine.panoramic.playback.media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;

public class ObbMediaDescriptor implements MediaPlayerDescriptor {

	private static final String EXP_PATH = "/Android/obb/";

	private String filePath;
	private int videoMode;
	private Context context;

	private int subsRes;
	private int chapRes;

	public ObbMediaDescriptor(Context context, int version, int videoMode) {
		String packName = context.getPackageName();
		String obbPath = Environment.getExternalStorageDirectory() + EXP_PATH + packName;
		if (new File(obbPath).exists()) {
			filePath = obbPath + File.separator + "main." + version + "." + packName + ".obb";
			if (!new File(filePath).exists()) {
				throw new MediaNotFoundException("Obb file does not exist.");
			}
			this.videoMode = videoMode;
		} else {
			throw new MediaNotFoundException("Obb directory not found.");
		}
		this.context = context;
	}

	@Override
	public MediaPlayer createMediaPlayer() {
		MediaPlayer player = new MediaPlayer();
		try {
			player.setDataSource(filePath);
		} catch (IOException e) {
			throw new AssetNotFoundException(e);
		}
		return player;
	}

	@Override
	public Subtitle createSubtitle() {
		if (subsRes == 0) {
            return null;
        }
		InputStream stream = context.getResources().openRawResource(subsRes);
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
		if (chapRes == 0) {
            return null;
        }
		ChapterData cpd;
		try {
			InputStream stream = context.getResources().openRawResource(chapRes);
			cpd = new ChapterData(stream);
			cpd.getChapters();
			return cpd;
		} catch (IOException e) {
			return null;
		}
	}

	public void setSubtitleResource(int resource) {
		this.subsRes = resource;
	}

	public void setChapterResource(int resource) {
		this.chapRes = resource;
	}

	@Override
	public int getVideoMode() {
		return videoMode;
	}

}
