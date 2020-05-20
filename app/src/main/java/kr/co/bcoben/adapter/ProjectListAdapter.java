package kr.co.bcoben.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.model.ProjectListData;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectHolder> {

    private Activity activity;
    private List<ProjectListData> list;

    public ProjectListAdapter(Activity activity, List<ProjectListData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ProjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_project, viewGroup, false);
        return new ProjectHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectHolder holder, final int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<ProjectListData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ProjectHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtName;

        ProjectHolder(View view) {
            super(view);
            this.view = view;
            txtName = view.findViewById(R.id.txt_name);
        }

        void onBind(final ProjectListData item) {
            txtName.setText(item.getProjectName());
            view.setSelected(item.isSelected());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ProjectListData data : list) {
                        data.setSelected(false);
                    }
                    item.setSelected(true);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
