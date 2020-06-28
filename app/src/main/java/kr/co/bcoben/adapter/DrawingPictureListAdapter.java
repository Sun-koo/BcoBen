package kr.co.bcoben.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.component.DrawingPictureDialog;

import static kr.co.bcoben.util.CommonUtil.dpToPx;

public class DrawingPictureListAdapter extends RecyclerView.Adapter<DrawingPictureListAdapter.DrawingPictureHolder> {

    private DrawingsActivity activity;
    private List<Bitmap> list = new ArrayList<>();

    public DrawingPictureListAdapter(DrawingsActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public DrawingPictureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawing_picture_popup,viewGroup, false);
        return new DrawingPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingPictureHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public int setList(List<Bitmap> list) {
        this.list = list;
        int width = activity.setDrawingPictureListAdapter(list.size());
        notifyDataSetChanged();
        return width;
    }

    class DrawingPictureHolder extends RecyclerView.ViewHolder {

        private ImageView ivPicture;

        DrawingPictureHolder(@NonNull View view) {
            super(view);
            ivPicture = view.findViewById(R.id.iv_picture);
        }

        void onBind(final int position) {
            ivPicture.setImageBitmap(list.get(position));
            ivPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawingPictureDialog.builder(activity)
                            .setPicture(list.get(position))
                            .show();
                }
            });

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivPicture.getLayoutParams();
            params.bottomMargin = position < getItemCount() - 2 ? dpToPx(12) : 0;
            ivPicture.setLayoutParams(params);
        }
    }
}
