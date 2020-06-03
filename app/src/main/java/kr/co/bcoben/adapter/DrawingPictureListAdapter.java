package kr.co.bcoben.adapter;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;

public class DrawingPictureListAdapter extends RecyclerView.Adapter<DrawingPictureListAdapter.DrawingPictureHolder> {

    private DrawingsActivity activity;
    private List<Bitmap> list;
    private int size = 3;

    public DrawingPictureListAdapter(DrawingsActivity activity) {
        this.activity = activity;
        list = new ArrayList<>();

        try {
            AssetManager assetManager = activity.getAssets();
            for (int i = 1; i <= 3; i++) {
                BufferedInputStream bis = new BufferedInputStream(assetManager.open("drawing_popup_img" + i + ".png"));
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                bis.close();

                list.add(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public DrawingPictureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawing_picture_list ,viewGroup, false);
        return new DrawingPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingPictureHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return size;
    }
    public void setSize(int size) {
        if (size > 3) {
            size = 3;
        } else if (size < 0) {
            size = 0;
        }
        this.size = size;
        notifyDataSetChanged();
    }

    class DrawingPictureHolder extends RecyclerView.ViewHolder {

        private ImageView ivPicture;

        DrawingPictureHolder(@NonNull View view) {
            super(view);
            ivPicture = (ImageView) view;
        }

        void onBind(Bitmap bitmap) {
            ivPicture.setImageBitmap(bitmap);
            ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
