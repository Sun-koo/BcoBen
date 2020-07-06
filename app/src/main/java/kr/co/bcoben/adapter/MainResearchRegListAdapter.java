package kr.co.bcoben.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuCheckData;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class MainResearchRegListAdapter extends RecyclerView.Adapter<MainResearchRegListAdapter.MenuSelectHolder> {

    private MainActivity activity;
    private List<MenuCheckData> list;
    private EditText editName, editCount;
    private boolean isCount;

    public MainResearchRegListAdapter(MainActivity activity, List<MenuCheckData> list) {
        this.activity = activity;
        this.list = list;
        this.isCount = false;
    }

    @NonNull
    @Override
    public MenuSelectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_select_list ,viewGroup, false);
        return new MenuSelectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuSelectHolder holder, final int position) {
        if (list.size() > position) {
            final MenuCheckData data = list.get(position);
            holder.onBind(data);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.requestResearchItemClick(data.getItem_id());
                    activity.setResearchSelectedText(data.getItem_name(), data.getTot_count());
                }
            });
        } else {
            holder.onBind(null);
            this.editName = holder.editName;
            this.editCount = holder.editCount;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (activity.getCurrentResearchStep() == MainActivity.ResearchStep.GRADE ? 0 : 1);
    }
    public void setList(List<MenuCheckData> list, boolean isCount) {
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

        void onBind(@Nullable final MenuCheckData data) {
            editName.setText("");
            editCount.setText("");

            if (data != null) {
                if (data.getTot_count() == 0) {
                    txtName.setText(data.getItem_name());
                } else {
                    txtName.setText("(" + data.getTot_count() + "개소) " + data.getItem_name());
                }
                txtName.setVisibility(View.VISIBLE);
                layoutSelf.setVisibility(View.GONE);
                editCount.setVisibility(View.GONE);
            } else {
                txtName.setVisibility(View.GONE);
                layoutSelf.setVisibility(View.VISIBLE);
                editCount.setVisibility(isCount ? View.VISIBLE : View.GONE);
                btnReg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!getEditName().trim().equals("")) {
                            for (MenuCheckData checkData : list) {
                                if (checkData.getItem_name().equals(getEditName().trim())) {
                                    switch (activity.getCurrentResearchStep()) {
                                        case FACILITY: showToast(R.string.toast_input_duplicate_facility); break;
                                        case FACILITY_CATEGORY: showToast(R.string.toast_input_duplicate_fac_cate); break;
                                        case ARCHITECTURE: showToast(R.string.toast_input_duplicate_architecture); break;
                                        case RESEARCH: showToast(R.string.toast_input_duplicate_research); break;
                                    }
                                    return;
                                }
                            }
                        }
                        activity.researchNextStep();
                    }
                });
            }
        }
    }
}
