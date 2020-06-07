package kr.co.bcoben.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class DrawingInputSpinnerAdapter extends ArrayAdapter<String> {

    public DrawingInputSpinnerAdapter(@NonNull Context context, int textViewResourceId, @NonNull String[] objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
