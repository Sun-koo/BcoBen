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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.component.DrawingPictureDialog;

public class InputPopupPictureListAdapter extends RecyclerView.Adapter<InputPopupPictureListAdapter.InputPopupPictureHolder> {

    private DrawingsActivity activity;
    private List<String> list;
    private List<Uri> uploadList;

    public InputPopupPictureListAdapter(DrawingsActivity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
        this.uploadList = new ArrayList<>();
    }

    @NonNull
    @Override
    public InputPopupPictureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_picture ,viewGroup, false);
        return new InputPopupPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputPopupPictureHolder holder, final int position) {
        Glide.with(activity.getApplicationContext())
                .load(position < list.size() ? list.get(position) : uploadList.get(position - list.size()))
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(5)))
                .into(holder.ivPicture);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < list.size()) {
                    list.remove(position);
                } else {
                    uploadList.remove(position - list.size());
                }
                activity.setPictureCount();
                notifyDataSetChanged();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(activity)
                        .asBitmap()
                        .load(position < list.size() ? list.get(position) : uploadList.get(position - list.size()))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                DrawingPictureDialog.builder(activity)
                                        .setPicture(resource)
                                        .show();
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {}
                        });
            }
        });
        activity.setPictureCount();
    }

    @Override
    public int getItemCount() {
        return list.size() + uploadList.size();
    }
    public void setList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void addImage(Uri uri) {
        uploadList.add(uri);
        notifyDataSetChanged();
    }
    public void resetList() {
        this.list = new ArrayList<>();
        this.uploadList = new ArrayList<>();
        notifyDataSetChanged();
    }
    public List<Uri> getUploadList() {
        return uploadList;
    }

    static class InputPopupPictureHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView ivPicture;
        ImageView btnDelete;

        InputPopupPictureHolder(View view) {
            super(view);
            this.view = view;
            this.ivPicture = view.findViewById(R.id.iv_picture);
            this.btnDelete = view.findViewById(R.id.btn_delete);
        }
    }
}
