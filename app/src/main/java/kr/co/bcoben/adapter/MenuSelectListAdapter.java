package kr.co.bcoben.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;

public class MenuSelectListAdapter extends RecyclerView.Adapter<MenuSelectListAdapter.MenuSelectHolder> {

    private MainActivity activity;
    private List<String> list;
    private EditText editName;

    public MenuSelectListAdapter(MainActivity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public MenuSelectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_select_list ,viewGroup, false);
        return new MenuSelectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuSelectHolder holder, int position) {
        if (list.size() > position) {
            holder.onBind(list.get(position));
        } else {
            holder.onBind(null);
            this.editName = holder.editName;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }
    public void setList(List<String> list) {
        this.list = list;
        editName = null;
        notifyDataSetChanged();
    }
    public String getEditName() {
        if (editName != null) {
            return editName.getText().toString();
        } else {
            return "";
        }
    }

    class MenuSelectHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtName;
        private EditText editName;

        MenuSelectHolder(View view) {
            super(view);
            this.view = view;
            txtName = view.findViewById(R.id.txt_name);
            editName = view.findViewById(R.id.edit_name);
        }

        void onBind(@Nullable final String name) {
            if (name != null) {
                txtName.setText(name);
                txtName.setVisibility(View.VISIBLE);
                editName.setVisibility(View.GONE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.setResearchSelectedText(name);
                    }
                });
            } else {
                txtName.setVisibility(View.GONE);
                editName.setVisibility(View.VISIBLE);
            }
        }
    }
}
