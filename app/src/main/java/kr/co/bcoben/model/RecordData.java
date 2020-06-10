package kr.co.bcoben.model;

import androidx.annotation.Nullable;

import java.io.File;

public class RecordData {
    private File recordFile;
    private String recordUrl;
    private String recordName;
    private int recordTime;
    private int playTime;
    private boolean isPlay;

    public RecordData(@Nullable File recordFile, @Nullable String recordUrl, String recordName, int recordTime) {
        this.recordFile = recordFile;
        this.recordUrl = recordUrl;
        this.recordName = recordName;
        this.recordTime = recordTime;
        this.playTime = 0;
        this.isPlay = false;
    }

    public File getRecordFile() {
        return recordFile;
    }
    public void setRecordFile(File recordFile) {
        this.recordFile = recordFile;
    }
    public String getRecordUrl() {
        return recordUrl;
    }
    public void setRecordUrl(String recordUrl) {
        this.recordUrl = recordUrl;
    }
    public String getRecordName() {
        return recordName;
    }
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }
    public int getRecordTime() {
        return recordTime;
    }
    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }
    public int getPlayTime() {
        return playTime;
    }
    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }
    public boolean isPlay() {
        return isPlay;
    }
    public void setPlay(boolean play) {
        isPlay = play;
    }
}
