package kr.co.bcoben.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.activity.DrawingsListActivity;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.ftp.ConnectFTP;
import kr.co.bcoben.model.DrawingsListData;

import static kr.co.bcoben.util.CommonUtil.getFilePath;

public class DrawingsListAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private ArrayList<DrawingsListData> list;

    public DrawingsListAdapter(Activity activity, ArrayList<DrawingsListData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawings_list ,viewGroup, false);
        return new DrawingsListAdapter.DrawingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String name = list.get(position).getName();

        final DrawingsListAdapter.DrawingHolder view = (DrawingsListAdapter.DrawingHolder) holder;

        view.txtName.setText(name);
        if (list.get(position).getBitmap() != null) {
            Glide.with(activity).asBitmap().load(list.get(position).getBitmap()).into(view.ivDrawings);
        }

///data/user/0/kr.co.bcoben/files/drawings_detail.png
        File file = new File(list.get(position).getFilePath());
        view.downloadLayout.setVisibility(file.exists() ? View.GONE : View.VISIBLE);

        view.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.ivDownload.setVisibility(View.GONE);
                view.txtPercent.setVisibility(View.VISIBLE);
                view.txtPercent.setText("100%");

                ((DrawingsListActivity) activity).downloadFile(position);
            }
        });

        view.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawingsListActivity) activity).sendSpinnerData();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<DrawingsListData> mList) {
        this.list = mList;
    }

    private class DrawingHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        RelativeLayout downloadLayout, btnDownload;
        TextView txtName, txtPercent;
        ImageView ivDrawings, ivDownload;

        public DrawingHolder(View view) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            downloadLayout = view.findViewById(R.id.layout_download);
            btnDownload = view.findViewById(R.id.btn_download);
            txtName = view.findViewById(R.id.txt_name);
            txtPercent = view.findViewById(R.id.txt_percent);
            ivDrawings = view.findViewById(R.id.iv_drawings);
            ivDownload = view.findViewById(R.id.iv_download);
        }
    }
}
