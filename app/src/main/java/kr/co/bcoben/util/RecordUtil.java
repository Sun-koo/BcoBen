package kr.co.bcoben.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class RecordUtil {

    private static final String TAG = "RecordUtil";
    private static MediaRecorder recorder;
    private static MediaPlayer player;
    private static int playPosition = 0;
    private static PlayCompleteListener playCompleteListener;

    public interface PlayCompleteListener {
        void onComplete();
    }

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
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    public static boolean isRecording() {
        return recorder != null;
    }

    // 오디오 파일 재생
    public static void playAudio(String filename) {
        if (player != null) {
            player.stop();
            player = null;
        }

        try {
            player = new MediaPlayer();
            player.setDataSource(filename);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    playPosition = 0;
                    mp.start();
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e(TAG, mp.getCurrentPosition() + "");
                    playPosition = 0;
                    if (playCompleteListener != null) {
                        playCompleteListener.onComplete();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 오디오 파일 일시중지
    public static void pauseAudio() {
        if (player != null) {
            playPosition = player.getCurrentPosition();
            player.pause();
        }
    }

    // 오디오 파일 다시재생
    public static void resumeAudio() {
        if (player != null && !player.isPlaying()) {
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

    // 오디오 재생위치 변경
    public static void setPlayPosition(int sec) {
        int msec = player.getCurrentPosition() + sec;
        if (msec > player.getDuration()) {
            msec = player.getDuration();
        }
        player.seekTo(msec);
    }

    // 오디오 파일 재생 플래그
    public static boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    // 오디오 파일 재생 완료 리스너
    public static void setPlayCompleteListener(PlayCompleteListener playCompleteListener) {
        RecordUtil.playCompleteListener = playCompleteListener;
    }
}
