package kr.co.bcoben.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.model.ProjectListData;

public class TableListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;
    private boolean checkable = false;
    private boolean isAllChecked = false;

    public TableListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_table_list ,viewGroup, false);
        return new TableListAdapter.TableHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final int number = mList.get(position).optInt("number", 0);
        final String legend = mList.get(position).optString("legend", "");
        final String content = mList.get(position).optString("content", "");
        final String value = mList.get(position).optString("value", "");
        final int count = mList.get(position).optInt("count", 0);

        final TableListAdapter.TableHolder view = (TableListAdapter.TableHolder) holder;

        view.txtNumber.setText(String.valueOf(number));
        view.txtNumber.setVisibility(checkable ? View.GONE : View.VISIBLE);
        view.checkBoxNumber.setVisibility(checkable ? View.VISIBLE : View.GONE);
        view.checkBoxNumber.setChecked(isAllChecked);
        view.txtLegend.setText(legend);
        view.txtContent.setText(content);
        view.txtValue.setText(value);
        view.txtCount.setText(String.valueOf(count));

        view.checkBoxNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("aaa", ""+isChecked);
            }
        });

        view.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(ArrayList<JSONObject> mList) {
        this.mList = mList;
    }

    public void setCheckable(boolean isVisible) {
        checkable = isVisible;
        this.notifyDataSetChanged();
    }

    public void setAllChecked(boolean isChecked) {
        isAllChecked = isChecked;
        this.notifyDataSetChanged();
    }

    private class TableHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        TextView txtNumber, txtLegend, txtContent, txtValue, txtCount;
        CheckBox checkBoxNumber;

        public TableHolder(View view, int position) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            txtNumber = view.findViewById(R.id.txt_number);
            txtLegend = view.findViewById(R.id.txt_legend);
            txtContent = view.findViewById(R.id.txt_content);
            txtValue = view.findViewById(R.id.txt_value);
            txtCount = view.findViewById(R.id.txt_count);
            checkBoxNumber = view.findViewById(R.id.checkbox_number);
        }
    }
}
