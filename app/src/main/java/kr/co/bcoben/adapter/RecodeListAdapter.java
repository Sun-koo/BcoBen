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

import kr.co.bcoben.R;
import kr.co.bcoben.activity.DrawingsActivity;

public class RecodeListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;

    public RecodeListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recode_file ,viewGroup, false);
        return new RecodeListAdapter.RecodeHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String name = mList.get(position).optString("name", "");
        final String time = mList.get(position).optString("time", "");

        final RecodeListAdapter.RecodeHolder view = (RecodeListAdapter.RecodeHolder) holder;

        view.txtName.setText(name);
        view.txtTime.setText(time);

        view.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO show recode play view
                ((DrawingsActivity) mActivity).startRecodePlay();
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

    private class RecodeHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        TextView txtName, txtTime;

        public RecodeHolder(View view, int position) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            txtName = view.findViewById(R.id.txt_name);
            txtTime = view.findViewById(R.id.txt_time);
        }
    }
}
