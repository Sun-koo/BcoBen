package kr.co.bcoben.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;

public class InputPopupPictureListAdapter extends RecyclerView.Adapter<InputPopupPictureListAdapter.InputPopupPictureHolder> {

    private Activity activity;
    private List<String> list;
    private List<Uri> uploadList;

    public InputPopupPictureListAdapter(Activity activity, List<String> list) {
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
                notifyDataSetChanged();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
