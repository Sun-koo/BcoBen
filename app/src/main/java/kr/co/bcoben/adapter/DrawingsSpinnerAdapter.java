package kr.co.bcoben.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import kr.co.bcoben.activity.DrawingsListActivity;
import kr.co.bcoben.model.MenuCheckData;
import kr.co.bcoben.model.ResearchRegData;

public class DrawingsSpinnerAdapter extends ArrayAdapter<MenuCheckData> {

    private List<MenuCheckData> list;

    public DrawingsSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<MenuCheckData> list) {
        super(context, resource, list);
        this.list = list;
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public MenuCheckData getItem(int position){
        return list.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(list.get(position).getItem_name());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setText(list.get(position).getItem_name());
        return view;
    }
}
