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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;

public class MenuCheckInputListAdapter extends RecyclerView.Adapter {

    private Activity activity;
    private List<String> list;
    private List<String> nameList;
    private List<String> countList;

    public MenuCheckInputListAdapter(Activity activity, List<String> list, List<String> nameList, List<String> countList) {
        this.activity = activity;
        this.list = list;
        this.nameList = nameList;
        this.countList = countList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_check_input_list ,viewGroup, false);
        return new MenuCheckInputListAdapter.MenuCheckInputHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MenuCheckInputListAdapter.MenuCheckInputHolder view = (MenuCheckInputListAdapter.MenuCheckInputHolder) holder;

        if (list.size() > position) {
            view.txtName.setVisibility(View.VISIBLE);
            view.checkBox.setVisibility(View.VISIBLE);
            view.editCount.setVisibility(View.VISIBLE);
            view.bottomLine.setVisibility(View.VISIBLE);
            view.btnNext.setVisibility(View.GONE);

            final String name = list.get(position);

            view.txtName.setText(name);
            view.checkBox.setChecked(false);
            view.editCount.getText().clear();
            view.editCount.setEnabled(false);
            view.editCount.setAlpha(0.38f);

            for (int i=0;i<nameList.size();i++) {
                if (view.txtName.getText().toString().equals(nameList.get(i))) {
                    view.checkBox.setChecked(true);
                    view.editCount.setEnabled(true);
                    view.editCount.setAlpha(1f);

                    final String count = countList.get(position);
                    view.editCount.setText(count);
                }
            }

            view.listLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    view.checkBox.setChecked(!view.checkBox.isChecked());

                    if (view.checkBox.isChecked()) {
                        nameList.add(view.txtName.getText().toString());

                        view.editCount.setEnabled(true);
                        view.editCount.setAlpha(1f);

                    } else {
                        nameList.remove(view.txtName.getText().toString());
                        countList.remove(view.editCount.getText().toString());

                        view.editCount.getText().clear();
                        view.editCount.setEnabled(false);
                        view.editCount.setAlpha(0.38f);
                    }
                }
            });

            view.editCount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        } else {
            view.txtName.setVisibility(View.GONE);
            view.checkBox.setVisibility(View.GONE);
            view.bottomLine.setVisibility(View.GONE);
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
        return list.size() + 1;
    }

    public void setList(List<String> list, List<String> nameList, List<String> countList) {
        this.list = list;
        this.nameList = nameList;
        this.countList = countList;
        notifyDataSetChanged();
    }

    public List<String> getSelectedName() {
        return nameList;
    }
    public List<String> getSelectedCount() {
        return countList;
    }

    private class MenuCheckInputHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        TextView txtName;
        CheckBox checkBox;
        EditText editCount;
        Button btnNext;
        View bottomLine;

        public MenuCheckInputHolder(View view, int position) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            txtName = view.findViewById(R.id.txt_name);
            checkBox = view.findViewById(R.id.checkbox);
            editCount = view.findViewById(R.id.edit_count);
            btnNext = view.findViewById(R.id.btn_project_next);
            bottomLine = view.findViewById(R.id.line_bottom);
        }
    }
}
