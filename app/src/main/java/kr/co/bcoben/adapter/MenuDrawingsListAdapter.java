package kr.co.bcoben.adapter;

import android.app.Activity;
import android.net.Uri;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsListActivity;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuDrawingsData;

public class MenuDrawingsListAdapter extends RecyclerView.Adapter<MenuDrawingsListAdapter.MenuDrawingsHolder> {

    private Activity activity;
    private List<MenuDrawingsData> list;

    public MenuDrawingsListAdapter(Activity activity, List<MenuDrawingsData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public MenuDrawingsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_drawings ,viewGroup, false);
        return new MenuDrawingsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuDrawingsHolder holder, final int position) {
        holder.txtName.setText(list.get(position).getName());
        holder.txtFacilityName.setText(list.get(position).getFacility());

        Glide.with(activity.getApplicationContext())
                .load(list.get(position).getUri())
                .into(holder.ivDrawings);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                ((MainActivity) activity).setTextDrawingsCount();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<MenuDrawingsData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class MenuDrawingsHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtFacilityName, btnDelete;
        ImageView ivDrawings;

        MenuDrawingsHolder(View view) {
            super(view);
            this.txtName = view.findViewById(R.id.txt_name);
            this.txtFacilityName = view.findViewById(R.id.txt_facility_name);
            this.btnDelete = view.findViewById(R.id.btn_delete);
            this.ivDrawings = view.findViewById(R.id.iv_drawings);
        }
    }
}
