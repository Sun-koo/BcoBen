package kr.co.bcoben.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;
import kr.co.bcoben.databinding.ActivityMainBinding;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ArrayList<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    private DrawingsListAdapter drawingsListAdapter;
    private ArrayList<JSONObject> listDrawings;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings_list;
    }

    @Override
    protected void initView() {
        dataBinding.setActivity(this);

        listCategory = new ArrayList<>();
        listArchitecture = new ArrayList<>();
        listResearch = new ArrayList<>();
        listFacility = new ArrayList<>();

        ArrayList<String> categoryList = getIntent().getStringArrayListExtra("category_list");
        ArrayList<String> architectureList = getIntent().getStringArrayListExtra("architecture_list");
        ArrayList<String> researchList = getIntent().getStringArrayListExtra("research_list");
        ArrayList<String> facilityList = getIntent().getStringArrayListExtra("facility_list");

        listCategory = categoryList;
        listArchitecture = architectureList;
        listResearch = researchList;
        listFacility = facilityList;

        category = getIntent().getStringExtra("category");
        architecture = getIntent().getStringExtra("architecture");
        research = getIntent().getStringExtra("research");
        facility = getIntent().getStringExtra("facility");

        initSpinner(dataBinding.spCategory, listCategory, category);
        initSpinner(dataBinding.spArchitecture, listArchitecture, architecture);
        initSpinner(dataBinding.spResearch, listResearch, research);
        initSpinner(dataBinding.spFacility, listFacility, facility);

        dataBinding.recyclerDrawings.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));

        listDrawings = new ArrayList<>();
        drawingsListAdapter = new DrawingsListAdapter(this, listDrawings);
        dataBinding.recyclerDrawings.setAdapter(drawingsListAdapter);

        requestDrawingsList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home: case R.id.btn_close:
                finish();
                break;

            case R.id.btn_download_all:
                //TODO
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "position: " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e("aaa", "not selected!");
    }

    private void initSpinner(Spinner spinner, ArrayList<String> list_data, String select_data) {
        spinner.setAdapter(new CategorySpinnerAdapter(this, R.layout.item_spinner, R.id.txt_list, list_data));

        spinner.setOnItemSelectedListener(this);

        setSpinnerSelectedData(list_data, select_data, spinner);
    }

    private void setSpinnerSelectedData(ArrayList<String> list_data, String select_data, Spinner spinner) {
        for (int i=0;i<list_data.size();i++) {
            String cate = list_data.get(i);
            if (cate.equals(select_data)) {
                spinner.setSelection(i);
            }
        }
    }

    public class CategorySpinnerAdapter extends ArrayAdapter<String> {

        List<String> list;

        CategorySpinnerAdapter(Context context, int layout_resource_id, int tv_resource_id, List tv_list) {
            super(context, layout_resource_id, tv_resource_id, tv_list);
            list = tv_list;
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

        View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_spinner, parent, false);

            TextView txtSelect = row.findViewById(R.id.txt_list);
            txtSelect.setText(list.get(position));

            return row;
        }
    }

    //TODO request drawings list api
    private void requestDrawingsList() {
        listDrawings.clear();

        for (int i = 0;i < 10;i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "도면명");
                jsonObject.put("is_download", true);
                listDrawings.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", "도면명");
                jsonObject.put("is_download", false);
                listDrawings.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        drawingsListAdapter.setList(listDrawings);
        drawingsListAdapter.notifyDataSetChanged();
    }
}
