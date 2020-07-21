package kr.co.bcoben.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.R;
import kr.co.bcoben.model.PointData;
import kr.co.bcoben.util.RecordUtil;

import static kr.co.bcoben.util.RecordUtil.getPlayPosition;
import static kr.co.bcoben.util.RecordUtil.isPlaying;
import static kr.co.bcoben.util.RecordUtil.pauseAudio;
import static kr.co.bcoben.util.RecordUtil.playAudio;
import static kr.co.bcoben.util.RecordUtil.resumeAudio;
import static kr.co.bcoben.util.RecordUtil.setPlayCompleteListener;
import static kr.co.bcoben.util.RecordUtil.setPlayPosition;
import static kr.co.bcoben.util.RecordUtil.stopAudio;

public class InputPopupRecordListAdapter extends RecyclerView.Adapter<InputPopupRecordListAdapter.InputPopupRecordHolder> {

    private Activity activity;
    private List<PointData.PointVoice> list = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();
    private Timer playTimer;
    private boolean isRecording = false;

    public InputPopupRecordListAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public InputPopupRecordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record_file, viewGroup, false);
        return new InputPopupRecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InputPopupRecordHolder holder, int position) {
        final PointData.PointVoice data = list.get(position);
        holder.onBind(data);

        holder.layoutRecordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    if (playTimer != null) {
                        playTimer.cancel();
                        playTimer = null;
                    }

                    resetPlay();
                    data.setPlay(true);
                    notifyDataSetChanged();

                    playAudio(data.getVoiceFile().getAbsolutePath());
                    setPlayCompleteListener(new RecordUtil.PlayCompleteListener() {
                        @Override
                        public void onComplete() {
                            holder.btnStop.setText(R.string.popup_reg_research_record_start);
                            data.setPlayTime(getPlayPosition());

                            if (playTimer != null) {
                                playTimer.cancel();
                                playTimer = null;
                            }
                        }
                    });

                    playTimer = new Timer();
                    playTimer.schedule(getPlayTask(holder, data), 0, 10);
                }
            }
        });

        holder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    pauseAudio();
                    holder.btnStop.setText(R.string.popup_reg_research_record_start);
                    if (playTimer != null) {
                        playTimer.cancel();
                        playTimer = null;
                    }
                } else {
                    resumeAudio();
                    holder.btnStop.setText(R.string.popup_reg_research_record_stop);
                    playTimer = new Timer();
                    playTimer.schedule(getPlayTask(holder, data), 0, 10);
                }
            }
        });

        holder.btnBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sec = -10 * 1000;
                setPlayPosition(sec);
                data.setPlayTime(getPlayPosition());
                holder.txtRecordCurrentTime.setText(getPlayTime(data.getPlayTime()));
                holder.seekRecordPlay.setProgress(data.getPlayTime());
            }
        });
        holder.btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sec = 10 * 1000;
                setPlayPosition(sec);
                data.setPlayTime(getPlayPosition());
                holder.txtRecordCurrentTime.setText(getPlayTime(data.getPlayTime()));
                holder.seekRecordPlay.setProgress(data.getPlayTime());
            }
        });
    }

    private TimerTask getPlayTask(final InputPopupRecordHolder holder, final PointData.PointVoice data) {
        return new TimerTask() {
            @Override
            public void run() {
                data.setPlayTime(getPlayPosition());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.txtRecordCurrentTime.setText(getPlayTime(data.getPlayTime()));
                        holder.seekRecordPlay.setProgress(data.getPlayTime());
                        if (data.getPlayTime() == data.getVoice_time()) {
                            if (playTimer != null) {
                                playTimer.cancel();
                                playTimer = null;
                            }
                        }
                    }
                });
            }
        };
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<PointData.PointVoice> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void addData(PointData.PointVoice data) {
        list.add(data);
        notifyDataSetChanged();
    }
    public List<PointData.PointVoice> getUploadList() {
        List<PointData.PointVoice> uploadList = new ArrayList<>();
        for (PointData.PointVoice data : list) {
            if (data.getVoice_id() == 0) {
                uploadList.add(data);
            }
        }
        return uploadList;
    }
    public List<Integer> getDeleteList() {
        return deleteList;
    }
    public void resetList() {
        this.list = new ArrayList<>();
        this.deleteList = new ArrayList<>();
        notifyDataSetChanged();
    }
    public void resetPlay() {
        for (PointData.PointVoice data : list) {
            data.setPlay(false);
            data.setPlayTime(0);
        }
    }
    public void setRecording(boolean isRecording) {
        this.isRecording = isRecording;
        if (playTimer != null) {
            playTimer.cancel();
            playTimer.purge();
            playTimer = null;
        }
        stopAudio();
    }

    private String getPlayTime(int time) {
        time /= 1000;
        DecimalFormat df = new DecimalFormat("00");
        int min = time / 60;
        int sec = time % 60;
        return df.format(min) + ":" + df.format(sec);
    }

    class InputPopupRecordHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutRecordInfo;
        private TextView txtRecordInfoName, txtRecordInfoTime;
        private LinearLayout layoutRecordPlay;
        private TextView txtRecordPlayName, txtRecordCurrentTime, txtRecordTotalTime;
        private TextView txtRecordDelete;
        private SeekBar seekRecordPlay;
        private Button btnBackwards, btnForward, btnStop;

        InputPopupRecordHolder(View view) {
            super(view);

            layoutRecordInfo = view.findViewById(R.id.layout_record_info);
            txtRecordInfoName = view.findViewById(R.id.txt_record_info_name);
            txtRecordInfoTime = view.findViewById(R.id.txt_record_info_time);
            layoutRecordPlay = view.findViewById(R.id.layout_record_play);
            txtRecordPlayName = view.findViewById(R.id.txt_record_play_name);
            txtRecordCurrentTime = view.findViewById(R.id.txt_record_current_time);
            txtRecordTotalTime = view.findViewById(R.id.txt_record_total_time);
            txtRecordDelete = view.findViewById(R.id.txt_record_delete);
            seekRecordPlay = view.findViewById(R.id.seek_record_play);
            btnBackwards = view.findViewById(R.id.btn_backwards);
            btnForward = view.findViewById(R.id.btn_forward);
            btnStop = view.findViewById(R.id.btn_stop);
        }

        void onBind(final PointData.PointVoice data) {
            txtRecordInfoName.setText(data.getVoiceName());
            txtRecordPlayName.setText(data.getVoiceName());
            txtRecordInfoTime.setText(getPlayTime(data.getVoice_time()));
            txtRecordTotalTime.setText(getPlayTime(data.getVoice_time()));
            txtRecordCurrentTime.setText(getPlayTime(data.getPlayTime()));
            btnStop.setText(R.string.popup_reg_research_record_stop);
            seekRecordPlay.setMax(data.getVoice_time());

            if (data.isPlay()) {
                layoutRecordInfo.setVisibility(View.GONE);
                layoutRecordPlay.setVisibility(View.VISIBLE);
            } else {
                layoutRecordInfo.setVisibility(View.VISIBLE);
                layoutRecordPlay.setVisibility(View.GONE);
            }

            seekRecordPlay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            txtRecordDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopAudio();
                    resetPlay();

                    if (data.getVoice_id() != 0) {
                        deleteList.add(data.getVoice_id());
                    }
                    list.remove(data);
                    notifyDataSetChanged();
                }
            });
        }

    }
}
