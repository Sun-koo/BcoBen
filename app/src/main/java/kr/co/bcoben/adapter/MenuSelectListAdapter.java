package kr.co.bcoben.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private EditText editName, editCount;
    private boolean isCount;

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
            this.editCount = holder.editCount;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }
    public void setList(List<String> list, boolean isCount) {
        this.list = list;
        this.isCount = isCount;
        editName = null;
        editCount = null;
        notifyDataSetChanged();
    }
    public String getEditName() {
        if (editName != null) {
            return editName.getText().toString();
        } else {
            return "";
        }
    }
    public String getEditCount() {
        if (editCount != null) {
            return editCount.getText().toString();
        } else {
            return "";
        }
    }

    class MenuSelectHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtName;
        private EditText editName, editCount;
        private LinearLayout layoutSelf;
        private Button btnReg;

        MenuSelectHolder(View view) {
            super(view);
            this.view = view;
            txtName = view.findViewById(R.id.txt_name);
            editName = view.findViewById(R.id.edit_name);
            editCount = view.findViewById(R.id.edit_count);
            layoutSelf = view.findViewById(R.id.layout_self);
            btnReg = view.findViewById(R.id.btn_reg);
        }

        void onBind(@Nullable final String name) {
            if (name != null) {
                txtName.setText(name);
                txtName.setVisibility(View.VISIBLE);
                layoutSelf.setVisibility(View.GONE);
                editCount.setVisibility(View.GONE);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.setResearchSelectedText(name);
                    }
                });
            } else {
                txtName.setVisibility(View.GONE);
                layoutSelf.setVisibility(View.VISIBLE);
                editCount.setVisibility(isCount ? View.VISIBLE : View.GONE);
                btnReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.researchNextStep();
                    }
                });
            }
        }
    }
}
