package com.comandulli.engine.panoramic.playback.media;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;

import com.comandulli.engine.panoramic.playback.engine.exception.AssetNotFoundException;
import com.comandulli.engine.panoramic.playback.entity.playback.ChapterData;
import com.comandulli.engine.panoramic.playback.entity.playback.Subtitle;

import com.comandulli.engine.panoramic.playback.engine.assets.loader.data.FileHeader;
import com.comandulli.engine.panoramic.playback.engine.assets.loader.data.ReadableData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Caio on 08-Jun-16.
 */
public class PartialObbMediaDescriptor implements MediaPlayerDescriptor {

    private static final String EXP_PATH = "/Android/obb/";

    private String filePath;
    private int videoMode;
    private String filename;
    private Context context;

    private int subsRes;
    private int chapRes;

    public PartialObbMediaDescriptor(Context context, int version, int videoMode, String filename) {
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
        this.filename = filename;
    }

    @Override
    public MediaPlayer createMediaPlayer() {
        MediaPlayer player = new MediaPlayer();
        try {
            ReadableData readableData = new ReadableData(filePath);
            FileHeader fileHeader = readableData.getFileHeader(filename);
            if (fileHeader != null) {
                fileHeader.getDataCapacity();
                long dataPosition = fileHeader.getDataPosition();
                long dataLength = fileHeader.getDataLength();

                File file = new File(filePath);
                FileInputStream inputStream = new FileInputStream(file);
                player.setDataSource(inputStream.getFD(), dataPosition, dataLength);
                inputStream.close();
            } else {
                throw new AssetNotFoundException();
            }
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
