package kr.co.bcoben.adapter;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.MenuCheckData;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class MenuCheckInputListAdapter extends RecyclerView.Adapter<MenuCheckInputListAdapter.MenuCheckInputHolder> {

    private Activity activity;
    private List<MenuCheckData> list;

    public MenuCheckInputListAdapter(Activity activity, List<MenuCheckData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public MenuCheckInputHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_check_input_list ,viewGroup, false);
        return new MenuCheckInputListAdapter.MenuCheckInputHolder(view, new EditCountTextChangeListener());
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuCheckInputHolder holder, int position) {
        if (list.size() > position) {
            holder.onBind(list.get(position), position);

        } else if (list.size() == position) {
            holder.layoutMenuInput.setVisibility(View.GONE);
            holder.layoutSelf.setVisibility(View.VISIBLE);
            holder.btnNext.setVisibility(View.GONE);

            holder.btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = holder.editNameSelf.getText().toString();
                    String count = holder.editCountSelf.getText().toString();

                    if (name.isEmpty()) {
                        showToast(R.string.toast_input_research);
                        return;
                    }
                    if (count.isEmpty()) {
                        showToast(R.string.toast_input_research_count);
                        return;
                    }
                    MenuCheckData data = new MenuCheckData(0, name);
                    data.setTot_count(Integer.parseInt(count));
                    data.setChecked(true);
                    list.add(data);
                    notifyDataSetChanged();
                }
            });

        } else {
            holder.layoutMenuInput.setVisibility(View.GONE);
            holder.layoutSelf.setVisibility(View.GONE);
            holder.btnNext.setVisibility(View.VISIBLE);

            holder.btnNext.setOnClickListener(new View.OnClickListener() {
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

    public void setList(List<MenuCheckData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<MenuCheckData> getSelectedValue() {
        return list;
    }

    class MenuCheckInputHolder extends RecyclerView.ViewHolder {

        View view;
        RelativeLayout layoutMenuInput;
        LinearLayout layoutSelf;
        TextView txtName;
        CheckBox checkBox;
        EditText editCount, editNameSelf, editCountSelf;
        Button btnNext, btnReg;
        EditCountTextChangeListener textChangeListener;

        public MenuCheckInputHolder(View view, EditCountTextChangeListener textChangeListener) {
            super(view);

            this.view = view;
            this.textChangeListener = textChangeListener;

            layoutMenuInput = view.findViewById(R.id.layout_menu_input);
            layoutSelf = view.findViewById(R.id.layout_self);
            txtName = view.findViewById(R.id.txt_name);
            checkBox = view.findViewById(R.id.checkbox);
            editCount = view.findViewById(R.id.edit_count);
            editNameSelf = view.findViewById(R.id.edit_name_self);
            editCountSelf = view.findViewById(R.id.edit_count_self);
            btnNext = view.findViewById(R.id.btn_project_next);
            btnReg = view.findViewById(R.id.btn_reg);

            editCount.addTextChangedListener(textChangeListener);
        }

        void onBind(final MenuCheckData data, int position) {
            textChangeListener.setPosition(position);
            textChangeListener.setEditText(editCount);
            layoutMenuInput.setVisibility(View.VISIBLE);
            layoutSelf.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);

            txtName.setText(data.getItem_name());
            checkBox.setChecked(data.isChecked());
            editCount.setEnabled(data.isChecked());
            editCount.setAlpha(data.isChecked() ? 1f : 0.38f);
            editCount.setText(String.valueOf(data.getTot_count()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.setChecked(!data.isChecked());
                    notifyDataSetChanged();
                }
            });
        }
    }

    private class EditCountTextChangeListener implements TextWatcher {
        private int position;
        private EditText editText;
        private String beforeTxt = "";

        public void setPosition(int position) {
            this.position = position;
        }
        public void setEditText(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(beforeTxt)) {
                beforeTxt = s.toString();
                if (beforeTxt.equals("")) {
                    editText.setText("0");
                } else {
                    editText.setText(String.valueOf(Integer.parseInt(beforeTxt)));
                }
                editText.setSelection(editText.getText().toString().length());
            }
            try {
                list.get(position).setTot_count(Integer.parseInt(s.toString()));
            } catch (NumberFormatException e) {
                list.get(position).setTot_count(0);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
