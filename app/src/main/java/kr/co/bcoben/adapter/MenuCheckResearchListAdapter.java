package kr.co.bcoben.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuCheckData;

public class MenuCheckResearchListAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<MenuCheckData> list;

    public MenuCheckResearchListAdapter(Activity activity, List<MenuCheckData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_check_research ,viewGroup, false);
        return new MenuCheckResearchListAdapter.MenuCheckResearchHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MenuCheckResearchListAdapter.MenuCheckResearchHolder view = (MenuCheckResearchListAdapter.MenuCheckResearchHolder) holder;

        view.txtName.setText(list.get(position).getItem_name());
        view.txtCount.setText(list.get(position).getCount() + "개소");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<MenuCheckData> mList) {
        this.list = mList;
        notifyDataSetChanged();
    }

    public List<MenuCheckData> getSelectedValue() {
        return list;
    }

    private class MenuCheckResearchHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtCount;

        public MenuCheckResearchHolder(View view, int position) {
            super(view);

            txtName = view.findViewById(R.id.txt_name);
            txtCount = view.findViewById(R.id.txt_count);
        }
    }
}
