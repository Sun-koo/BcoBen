package kr.co.bcoben.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.R;
import kr.co.bcoben.model.RecordData;

import static kr.co.bcoben.util.RecordUtil.isPlaying;
import static kr.co.bcoben.util.RecordUtil.pauseAudio;
import static kr.co.bcoben.util.RecordUtil.playAudio;
import static kr.co.bcoben.util.RecordUtil.resumeAudio;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.RecordHolder> {

    private Activity activity;
    private List<RecordData> list;
    private Timer playTimer;

    public RecordListAdapter(Activity activity, List<RecordData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record_file, viewGroup, false);
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecordHolder holder, int position) {
        final RecordData data = list.get(position);
        holder.onBind(data);

        final TimerTask playTask = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.txtRecordCurrentTime.setText(getPlayTime(data.getPlayTime()));
                        holder.seekRecordPlay.setProgress(data.getPlayTime());
                    }
                });
                data.setPlayTime(data.getPlayTime() + 1);
            }
        };

        holder.layoutRecordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playTimer != null) {
                    playTimer.cancel();
                    playTimer = null;
                }

                data.setPlay(true);
                data.setPlayTime(0);
                notifyDataSetChanged();

                playAudio(data.getRecordFile().getAbsolutePath());

                playTimer = new Timer();
                playTimer.schedule(playTask, 0, 1000);
            }
        });
        holder.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    pauseAudio();
                    holder.btnStop.setText(R.string.popup_reg_research_record_start);
                    playTask.cancel();
                } else {
                    resumeAudio();
                    holder.btnStop.setText(R.string.popup_reg_research_record_stop);
                    playTask.run();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<RecordData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void addData(RecordData data) {
        list.add(data);
        notifyItemChanged(list.size() - 1);
    }

    static String getPlayTime(int time) {
        DecimalFormat df = new DecimalFormat("00");
        int min = time / 60;
        int sec = time % 60;
        return df.format(min) + ":" + df.format(sec);
    }

    static class RecordHolder extends RecyclerView.ViewHolder {

        RelativeLayout layoutRecordInfo;
        TextView txtRecordInfoName, txtRecordInfoTime;
        LinearLayout layoutRecordPlay;
        TextView txtRecordPlayName, txtRecordCurrentTime, txtRecordTotalTime;
        SeekBar seekRecordPlay;
        Button btnBackwards, btnForward, btnStop;

        RecordHolder(View view) {
            super(view);

            layoutRecordInfo = view.findViewById(R.id.layout_record_info);
            txtRecordInfoName = view.findViewById(R.id.txt_record_info_name);
            txtRecordInfoTime = view.findViewById(R.id.txt_record_info_time);
            layoutRecordPlay = view.findViewById(R.id.layout_record_play);
            txtRecordPlayName = view.findViewById(R.id.txt_record_play_name);
            txtRecordCurrentTime = view.findViewById(R.id.txt_record_current_time);
            txtRecordTotalTime = view.findViewById(R.id.txt_record_total_time);
            seekRecordPlay = view.findViewById(R.id.seek_record_play);
            btnBackwards = view.findViewById(R.id.btn_backwards);
            btnForward = view.findViewById(R.id.btn_forward);
            btnStop = view.findViewById(R.id.btn_stop);
        }

        void onBind(final RecordData data) {
            txtRecordInfoName.setText(data.getRecordName());
            txtRecordPlayName.setText(data.getRecordName());
            txtRecordInfoTime.setText(getPlayTime(data.getRecordTime()));
            txtRecordTotalTime.setText(getPlayTime(data.getRecordTime()));
            txtRecordCurrentTime.setText(getPlayTime(data.getPlayTime()));
            btnStop.setText(R.string.popup_reg_research_record_stop);
            seekRecordPlay.setMax(data.getRecordTime());

            if (data.isPlay()) {
                layoutRecordInfo.setVisibility(View.GONE);
                layoutRecordPlay.setVisibility(View.VISIBLE);
            } else {
                layoutRecordInfo.setVisibility(View.VISIBLE);
                layoutRecordPlay.setVisibility(View.GONE);
            }
        }

    }
}
