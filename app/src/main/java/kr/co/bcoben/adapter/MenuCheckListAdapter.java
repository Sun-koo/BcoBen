package kr.co.bcoben.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsListActivity;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuCheckData;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

import static kr.co.bcoben.util.CommonUtil.hideKeyboard;
import static kr.co.bcoben.util.CommonUtil.showToast;

public class MenuCheckListAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<MenuCheckData> list;

    public MenuCheckListAdapter(Activity activity, List<MenuCheckData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_check_list ,viewGroup, false);
        return new MenuCheckListAdapter.MenuCheckHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MenuCheckListAdapter.MenuCheckHolder view = (MenuCheckListAdapter.MenuCheckHolder) holder;

        if (list.size() > position) {
            view.txtName.setVisibility(View.VISIBLE);
            view.checkBox.setVisibility(View.VISIBLE);
            view.bottomLine.setVisibility(View.VISIBLE);
            view.layoutSelf.setVisibility(View.GONE);
            view.btnNext.setVisibility(View.GONE);

            view.txtName.setText(list.get(position).getItem_name());
            view.checkBox.setChecked(list.get(position).isChecked());

            view.listLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (MenuCheckData data : list) {
                        data.setChecked(false);
                    }
                    list.get(position).setChecked(true);
                    notifyDataSetChanged();

//                    view.checkBox.setChecked(!view.checkBox.isChecked());
//                    list.get(position).setChecked(view.checkBox.isChecked());
                }
            });
        } else if (list.size() == position) {
            view.txtName.setVisibility(View.GONE);
            view.checkBox.setVisibility(View.GONE);
            view.bottomLine.setVisibility(View.GONE);
            view.layoutSelf.setVisibility(View.VISIBLE);
            view.btnNext.setVisibility(View.GONE);

            view.btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = view.editName.getText().toString().trim();
                    if (name.isEmpty()) {
                        showToast(R.string.toast_input_grade);
                        return;
                    }

                    for (MenuCheckData data : list) {
                        if (name.equals(data.getItem_name())) {
                            showToast(R.string.toast_input_duplicate_grade);
                            return;
                        }
                    }
                    for (MenuCheckData data : list) {
                        data.setChecked(false);
                    }
                    MenuCheckData data = new MenuCheckData(0, name);
                    data.setChecked(true);
                    list.add(data);
                    notifyDataSetChanged();
                    hideKeyboard(activity);
                }
            });

        } else {
            view.txtName.setVisibility(View.GONE);
            view.checkBox.setVisibility(View.GONE);
            view.bottomLine.setVisibility(View.GONE);
            view.layoutSelf.setVisibility(View.GONE);
            view.btnNext.setVisibility(View.VISIBLE);

            view.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) activity).projectNextStep();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 2;
    }

    public void setList(List<MenuCheckData> mList) {
        this.list = mList;
        notifyDataSetChanged();
    }

    public List<MenuCheckData> getSelectedValue() {
        return list;
    }

    private class MenuCheckHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout, layoutSelf;
        TextView txtName;
        CheckBox checkBox;
        Button btnNext, btnReg;
        View bottomLine;
        EditText editName;

        public MenuCheckHolder(View view) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            layoutSelf = view.findViewById(R.id.layout_self);
            txtName = view.findViewById(R.id.txt_name);
            checkBox = view.findViewById(R.id.checkbox);
            btnNext = view.findViewById(R.id.btn_project_next);
            btnReg = view.findViewById(R.id.btn_reg);
            bottomLine = view.findViewById(R.id.line_bottom);
            editName = view.findViewById(R.id.edit_name);
        }
    }
}
