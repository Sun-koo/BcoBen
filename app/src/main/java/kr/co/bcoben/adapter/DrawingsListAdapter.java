package kr.co.bcoben.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsListActivity;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.util.SharedPrefUtil;

public class DrawingsListAdapter extends RecyclerView.Adapter<DrawingsListAdapter.DrawingHolder> {

    private DrawingsListActivity activity;
    private List<PlanDataList.PlanData> list;

    public static final String START_DOWNLOAD = "start_download";
    public static final String DOWNLOADING = "downloading";
    public static final String COMPLETE_DOWNLOAD = "complete_download";

    public DrawingsListAdapter(DrawingsListActivity activity, List<PlanDataList.PlanData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public DrawingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawings_list ,viewGroup, false);
        return new DrawingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DrawingHolder holder, final int position) {
        holder.onBind(list.get(position), position);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            for (Object payload : payloads) {
                String status = (String) payload;
                if (status.equals(START_DOWNLOAD)) {
                    holder.txtPercent.setVisibility(View.VISIBLE);
                    holder.ivDownload.setVisibility(View.GONE);
                } else if (status.equals(DOWNLOADING)) {
                    holder.txtPercent.setText(list.get(position).getDownPercent() + "%");
                } else if (status.equals(COMPLETE_DOWNLOAD)) {
                    holder.layoutDownload.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<PlanDataList.PlanData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void setData(final int position, final PlanDataList.PlanData data, final String payload) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.set(position, data);
                notifyItemChanged(position, payload);
            }
        });
    }

    public class DrawingHolder extends RecyclerView.ViewHolder {

        private View view;
        private RelativeLayout layoutDownload;
        private RelativeLayout btnDownload;
        private TextView txtName;
        private TextView txtPercent;
        private ImageView ivDrawings;
        private ImageView ivDownload;

        DrawingHolder(View view) {
            super(view);
            this.view = view;
            this.layoutDownload = view.findViewById(R.id.layout_download);
            this.btnDownload = view.findViewById(R.id.btn_download);
            this.txtName = view.findViewById(R.id.txt_name);
            this.txtPercent = view.findViewById(R.id.txt_percent);
            this.ivDrawings = view.findViewById(R.id.iv_drawings);
            this.ivDownload = view.findViewById(R.id.iv_download);
        }

        void onBind(PlanDataList.PlanData data, final int position) {
            txtName.setText(data.getPlan_name());
            if (SharedPrefUtil.getBoolean(data.getPlan_img_file(), false)) {
                data.setDownPercent(100);
                layoutDownload.setVisibility(View.GONE);
            } else {
                layoutDownload.setVisibility(View.VISIBLE);
            }
            Glide.with(activity).asBitmap().load(data.getPlan_bitmap()).into(ivDrawings);

            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.downloadFile(position);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (layoutDownload.getVisibility() == View.GONE) {
                        activity.sendSpinnerData(position);
                    }
                }
            });
        }
    }
}
