package kr.co.bcoben.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
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
        stopAudio();

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
                    Log.e(TAG, "Complete : " + mp.getCurrentPosition());
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
            if (playPosition == player.getDuration()) {
                playPosition = 0;
            }
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

    // 오디오 재생위치 가져오기
    public static int getPlayPosition() {
        if (player != null) {
            try {
                return player.getCurrentPosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // 오디오 재생위치 변경
    public static void setPlayPosition(int sec) {
        playPosition = player.getCurrentPosition() + sec;
        if (playPosition > player.getDuration()) {
            playPosition = player.getDuration();
        }
        player.seekTo(playPosition);
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
