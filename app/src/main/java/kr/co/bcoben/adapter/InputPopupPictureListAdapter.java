package kr.co.bcoben.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.component.DrawingPictureDialog;
import kr.co.bcoben.model.PointData;

public class InputPopupPictureListAdapter extends RecyclerView.Adapter<InputPopupPictureListAdapter.InputPopupPictureHolder> {

    private DrawingsActivity activity;
    private List<PointData.PointImg> list = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();

    public InputPopupPictureListAdapter(DrawingsActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public InputPopupPictureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_picture ,viewGroup, false);
        return new InputPopupPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputPopupPictureHolder holder, final int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<PointData.PointImg> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void addImage(Uri uri) {
        final PointData.PointImg img = new PointData.PointImg(0, null);
        img.setImgUri(uri);
        Glide.with(activity)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        img.setImgBitmap(bitmap);
                        list.add(img);
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {}
                });
    }
    public List<PointData.PointImg> getUploadList() {
        List<PointData.PointImg> uploadList =  new ArrayList<>();
        for (PointData.PointImg data : list) {
            if (data.getImg_id() == 0) {
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

    class InputPopupPictureHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView ivPicture;
        private ImageView btnDelete;

        InputPopupPictureHolder(View view) {
            super(view);
            this.view = view;
            this.ivPicture = view.findViewById(R.id.iv_picture);
            this.btnDelete = view.findViewById(R.id.btn_delete);
        }

        void onBind(final PointData.PointImg data) {
            Glide.with(activity.getApplicationContext())
                    .asBitmap()
                    .load(data.getImgBitmap())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                    .into(ivPicture);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getImg_id() != 0) {
                        deleteList.add(data.getImg_id());
                    }
                    list.remove(data);
                    activity.setPictureCount();
                    notifyDataSetChanged();
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawingPictureDialog.builder(activity)
                            .setPicture(data.getImgBitmap())
                            .show();
                }
            });
            activity.setPictureCount();
        }
    }
}
