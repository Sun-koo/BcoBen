package kr.co.bcoben.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;
import kr.co.bcoben.model.PointTableData;

public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.TableHolder> {

    private DrawingsActivity activity;
    private List<PointTableData> list;
    private boolean checkable = false;
    private boolean isAllChecked = false;

    public TableListAdapter(DrawingsActivity activity, List<PointTableData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public TableHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_table_list ,viewGroup, false);
        return new TableListAdapter.TableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public List<PointTableData> getList() {
        return list;
    }
    public void setList(List<PointTableData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public void setCheckable(boolean isVisible) {
        checkable = isVisible;
        setAllChecked(false);
    }
    public void setAllChecked(boolean isChecked) {
        isAllChecked = isChecked;
        notifyDataSetChanged();
    }

    class TableHolder extends RecyclerView.ViewHolder {

        private View view ;
        private TextView txtNumber;
        private ImageView imgLabel;
        private TextView txtContent;
        private TextView txtMeasure;
        private TextView txtCount;
        private CheckBox checkBoxNumber;

        TableHolder(View view) {
            super(view);
            this.view = view;

            txtNumber = view.findViewById(R.id.txt_number);
            imgLabel = view.findViewById(R.id.img_label);
            txtContent = view.findViewById(R.id.txt_content);
            txtMeasure = view.findViewById(R.id.txt_measure);
            txtCount = view.findViewById(R.id.txt_count);
            checkBoxNumber = view.findViewById(R.id.checkbox_number);
        }

        void onBind(final PointTableData data) {
            txtNumber.setText(String.valueOf(data.getNum()));
            txtNumber.setVisibility(checkable ? View.GONE : View.VISIBLE);
            checkBoxNumber.setVisibility(checkable ? View.VISIBLE : View.GONE);
            checkBoxNumber.setChecked(isAllChecked);
            txtContent.setText(data.getContent());
            txtMeasure.setText(data.getMeasure());
            txtCount.setText(String.valueOf(data.getCount()));

            checkBoxNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    data.setChecked(isChecked);
                }
            });
        }
    }
}
