package kr.co.bcoben.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;
import kr.co.bcoben.databinding.ActivityMainBinding;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener {

    private List<String> items;
    private String category;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings_list;
    }

    @Override
    protected void initView() {
        dataBinding.setActivity(this);

        items = new ArrayList<>();
        ArrayList<String> categoryList = getIntent().getStringArrayListExtra("category_list");
        items = categoryList;
        category = getIntent().getStringExtra("category");

        initSpinner();

        for (int i=0;i<categoryList.size();i++) {
            String cate = categoryList.get(i);
            if (cate.equals(category)) {
                dataBinding.spCategory.setSelection(i);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                finish();
                break;
        }
    }

    public class CategorySpinnerAdapter extends ArrayAdapter<String> {
        CategorySpinnerAdapter(Context context, int layout_resource_id, int tv_resource_id, List tv_list) {
            super(context, layout_resource_id, tv_resource_id, tv_list);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        View getCustomView(int position, View converView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_spinner, parent, false);

            TextView txtSelect = row.findViewById(R.id.txt_list);
            txtSelect.setText(items.get(position));

            return row;
        }
    }

    private void initSpinner() {
        dataBinding.spCategory.setAdapter(new CategorySpinnerAdapter(this, R.layout.item_spinner, R.id.txt_list, items));

        dataBinding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("aaa", "position: " + position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("aaa", "not selected!");
            }
        });
    }
}
