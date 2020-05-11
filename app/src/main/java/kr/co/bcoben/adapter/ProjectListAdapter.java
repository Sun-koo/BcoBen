package kr.co.bcoben.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.bcoben.R;

public class ProjectListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;

    public ProjectListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_project ,viewGroup, false);
        return new ProjectListAdapter.ProjectHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String name = mList.get(position).optString("name", "");

        final ProjectListAdapter.ProjectHolder view = (ProjectListAdapter.ProjectHolder) holder;

        view.txtName.setText(name);
        view.txtName.setTextColor(mActivity.getResources().getColor(R.color.whiteColor));
        view.selectLayout.setVisibility(View.INVISIBLE);

        view.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.selectLayout.setVisibility(View.VISIBLE);
                view.txtName.setTextColor(mActivity.getResources().getColor(R.color.keyColor));
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

    private class ProjectHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        RelativeLayout selectLayout;
        TextView txtName;

        public ProjectHolder(View view, int position) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            selectLayout = view.findViewById(R.id.select_layout);
            txtName = view.findViewById(R.id.txt_name);
        }
    }
}
