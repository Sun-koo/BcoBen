package kr.co.bcoben.adapter;

import android.app.Activity;
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
import kr.co.bcoben.activity.MainActivity;

public class MenuSelectListAdapter extends RecyclerView.Adapter {

    private Activity mActivity;
    private ArrayList<JSONObject> mList;

    public MenuSelectListAdapter(Activity activity, ArrayList<JSONObject> list) {
        this.mActivity = activity;
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_menu_select_list ,viewGroup, false);
        return new MenuSelectListAdapter.MenuSelectHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String name = mList.get(position).optString("name", "");

        final MenuSelectListAdapter.MenuSelectHolder view = (MenuSelectListAdapter.MenuSelectHolder) holder;

        view.txtName.setText(name);

        view.listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = (MainActivity) mActivity;
                main.setSelectedText(name);
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

    private class MenuSelectHolder extends RecyclerView.ViewHolder {

        LinearLayout listLayout;
        TextView txtName;

        public MenuSelectHolder(View view, int position) {
            super(view);

            listLayout = view.findViewById(R.id.list_layout);
            txtName = view.findViewById(R.id.txt_name);
        }
    }
}
