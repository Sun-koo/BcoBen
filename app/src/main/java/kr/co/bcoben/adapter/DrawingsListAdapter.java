package kr.co.bcoben.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.activity.DrawingsListActivity;
import kr.co.bcoben.activity.MainActivity;

public class DrawingsListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;

    public DrawingsListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawings_list ,viewGroup, false);
        return new DrawingsListAdapter.DrawingHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String name = mList.get(position).optString("name", "");
        final boolean isDownload = mList.get(position).optBoolean("is_download", false);
        final String imageUrl = mList.get(position).optString("img_url", "");

        final DrawingsListAdapter.DrawingHolder view = (DrawingsListAdapter.DrawingHolder) holder;

        view.txtName.setText(name);
        view.downloadLayout.setVisibility(isDownload ? View.GONE : View.VISIBLE);

        //이미지 처리
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                URL url = null;
//                try {
//                    url = new URL("" + imageUrl);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//
//                    InputStream is = conn.getInputStream();
//                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            view.ivDrawings.setImageBitmap(bitmap);
//                        }
//                    });
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();

        view.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.ivDownload.setVisibility(View.GONE);
                view.txtPercent.setVisibility(View.VISIBLE);
                view.txtPercent.setText("100%");
                //TODO download image
            }
        });

        view.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DrawingsListActivity) mActivity).sendSpinnerData();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(ArrayList<JSONObject> mList) {
        this.mList = mList;
    }

    private class DrawingHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        RelativeLayout downloadLayout, btnDownload;
        TextView txtName, txtPercent;
        ImageView ivDrawings, ivDownload;

        public DrawingHolder(View view, int position) {
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
