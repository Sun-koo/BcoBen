package kr.co.bcoben.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuSelectFacilityData;

public class MenuCheckFacilityListAdapter extends RecyclerView.Adapter<MenuCheckFacilityListAdapter.MenuCheckFacilityHolder> {

    private Activity activity;
    private List<MenuSelectFacilityData> list;
    private boolean isSelected = false;

    public MenuCheckFacilityListAdapter(Activity activity, List<MenuSelectFacilityData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public MenuCheckFacilityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_select_facility ,viewGroup, false);
        return new MenuCheckFacilityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCheckFacilityHolder holder, final int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<MenuSelectFacilityData> list) {
        this.list = list;
        this.isSelected = true;
        notifyDataSetChanged();
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        notifyDataSetChanged();
    }
    public List<MenuSelectFacilityData> getSelectedValue() {
        return list;
    }

    class MenuCheckFacilityHolder extends RecyclerView.ViewHolder {

        private RelativeLayout layoutRoot;
        private TextView txtName, btnRemove;

        MenuCheckFacilityHolder(View view) {
            super(view);

            layoutRoot = view.findViewById(R.id.layout_root);
            txtName = view.findViewById(R.id.txt_name);
            btnRemove = view.findViewById(R.id.btn_remove);
        }

        void onBind(final MenuSelectFacilityData data) {
            layoutRoot.setSelected(isSelected);
            txtName.setText(data.getFacility() + " > " + data.getFacCate() + " > " + data.getArch());
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.remove(data);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
