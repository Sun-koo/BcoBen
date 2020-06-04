package kr.co.bcoben.adapter;

import android.app.Activity;
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

import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsListActivity;

public class MenuDrawingsListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;

    public MenuDrawingsListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_drawings ,viewGroup, false);
        return new MenuDrawingsListAdapter.MenuDrawingsHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MenuDrawingsListAdapter.MenuDrawingsHolder view = (MenuDrawingsListAdapter.MenuDrawingsHolder) holder;

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
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(ArrayList<JSONObject> mList) {
        this.mList = mList;
    }

    private class MenuDrawingsHolder extends RecyclerView.ViewHolder {

        ImageView ivDrawings;

        public MenuDrawingsHolder(View view, int position) {
            super(view);

            ivDrawings = view.findViewById(R.id.iv_drawings);
        }
    }
}
