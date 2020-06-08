package kr.co.bcoben.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuSelectFacilityData;

public class MenuCheckFacilityListAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<MenuSelectFacilityData> list;

    public MenuCheckFacilityListAdapter(Activity activity, List<MenuSelectFacilityData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_select_facility ,viewGroup, false);
        return new MenuCheckFacilityHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MenuCheckFacilityHolder view = (MenuCheckFacilityHolder) holder;

        view.txtName.setText(list.get(position).getFacility() + " > " + list.get(position).getFacCate() + " > " + list.get(position).getArch());
        view.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<MenuSelectFacilityData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<MenuSelectFacilityData> getSelectedValue() {
        return list;
    }

    private class MenuCheckFacilityHolder extends RecyclerView.ViewHolder {

        TextView txtName, btnRemove;

        public MenuCheckFacilityHolder(View view) {
            super(view);

            txtName = view.findViewById(R.id.txt_name);
            btnRemove = view.findViewById(R.id.btn_remove);
        }
    }
}
