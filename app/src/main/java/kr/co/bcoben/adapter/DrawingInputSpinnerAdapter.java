package kr.co.bcoben.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DrawingInputSpinnerAdapter extends ArrayAdapter<String> {

    private List<String> list;
    private int textViewResourceId;

    public DrawingInputSpinnerAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<String> objects) {
        super(context, textViewResourceId, objects);
        this.list = objects;
        this.textViewResourceId = textViewResourceId;
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position + 1, convertView, parent);
    }
}
