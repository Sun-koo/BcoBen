package kr.co.bcoben.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
    Activity activity;

    public CustomSpinnerAdapter(Activity activity, int resource, String[] objects) {
        super(activity, resource, objects);
        this.activity = activity;
    }

    public CustomSpinnerAdapter(Activity activity, int resource, List<String> objects) {
        super(activity, resource, objects);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
