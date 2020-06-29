package kr.co.bcoben.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.model.PointData;

import static kr.co.bcoben.util.CommonUtil.getDateFormat;

public class InputPopupMemoListAdapter extends RecyclerView.Adapter<InputPopupMemoListAdapter.DrawingPictureHolder> {

    private DrawingsActivity activity;
    private List<PointData.PointMemo> list = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();

    public InputPopupMemoListAdapter(DrawingsActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public DrawingPictureHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_memo,viewGroup, false);
        return new DrawingPictureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingPictureHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public List<PointData.PointMemo> getList() {
        return list;
    }
    public void setList(List<PointData.PointMemo> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void addBitmap(Bitmap bitmap) {
        PointData.PointMemo memo = new PointData.PointMemo(0, null);
        memo.setMemoBitmap(bitmap);
        memo.setMemoBitmapName("memo_" + getDateFormat("yyMMddHHmmss") + ".jpg");
        list.add(memo);
        notifyItemChanged(list.size() - 1);
    }
    public List<Integer> getDeleteList() {
        return deleteList;
    }
    public void resetList() {
        list = new ArrayList<>();
        deleteList = new ArrayList<>();
        notifyDataSetChanged();
    }

    class DrawingPictureHolder extends RecyclerView.ViewHolder {

        private ImageView ivMemo;
        private ImageView btnDelete;

        DrawingPictureHolder(@NonNull View view) {
            super(view);
            ivMemo = view.findViewById(R.id.iv_memo);
            btnDelete = view.findViewById(R.id.btn_delete);
        }

        void onBind(final PointData.PointMemo data) {
            ivMemo.setImageBitmap(data.getMemoBitmap());
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getMemo_id() != 0) {
                        deleteList.add(data.getMemo_id());
                    }
                    list.remove(data);
                    activity.setMemoCount();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
