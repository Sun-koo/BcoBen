package kr.co.bcoben.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsListActivity;

public class PictureListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;

    public PictureListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_picture ,viewGroup, false);
        return new PictureListAdapter.PictureHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String imageUrl = mList.get(position).optString("img_url", "");

        final PictureListAdapter.PictureHolder view = (PictureListAdapter.PictureHolder) holder;

        Glide.with(mActivity.getApplicationContext())
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                .into(view.ivPicture);

        view.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO picture remove in list
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

    private class PictureHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        ImageView ivPicture;
        ImageButton btnDelete;

        public PictureHolder(View view, int position) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            ivPicture = view.findViewById(R.id.iv_picture);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }
}
