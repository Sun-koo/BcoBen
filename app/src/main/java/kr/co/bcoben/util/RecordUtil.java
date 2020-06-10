package kr.co.bcoben.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class RecordUtil {
    private static MediaRecorder recorder;
    private static MediaPlayer player;
    private static int playPosition = 0;

    // 녹음 시작
    public static File startRecord(String filename) {
        File file = new File(CommonUtil.getCachePath(), filename);

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(file.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // 녹음 중지
    public static void stopRecord() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    // 오디오 파일 재생
    public static void playAudio(String filename) {
        if (player != null) {
            player.release();
            player = null;
        }

        try {
            player = new MediaPlayer();
            player.setDataSource(filename);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 오디오 파일 일시중지
    public static void pauseAudio() {
        if (player != null) {
            Log.e("RecordUtil", "pauseAudio : " + playPosition);
            playPosition = player.getCurrentPosition();
            player.pause();
        }
    }

    // 오디오 파일 다시재생
    public static void resumeAudio() {
        if (player != null && !player.isPlaying()) {
            Log.e("RecordUtil", "resumeAudio : " + playPosition);
            player.seekTo(playPosition);
            player.start();
        }
    }

    // 오디오 파일 중지
    public static void stopAudio() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
        }
    }

    // 오디오 파일 재생 플래그
    public static boolean isPlaying() {
        return player.isPlaying();
    }
}
