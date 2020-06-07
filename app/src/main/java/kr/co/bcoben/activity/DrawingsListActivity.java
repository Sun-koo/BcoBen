package kr.co.bcoben.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener {

    private List<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    private DrawingsListAdapter drawingsListAdapter;
    private ArrayList<JSONObject> listDrawings;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings_list;
    }

    @Override
    protected void initView() {
        listCategory = getIntent().getStringArrayListExtra("category_list");
        listArchitecture = getIntent().getStringArrayListExtra("architecture_list");
        listResearch = getIntent().getStringArrayListExtra("research_list");
        listFacility = getIntent().getStringArrayListExtra("facility_list");
        category = getIntent().getStringExtra("category");
        architecture = getIntent().getStringExtra("architecture");
        research = getIntent().getStringExtra("research");
        facility = getIntent().getStringExtra("facility");

        initSpinner(dataBinding.spnCategory, listCategory, category);
        initSpinner(dataBinding.spnArchitecture, listArchitecture, architecture);
        initSpinner(dataBinding.spnResearch, listResearch, research);
        initSpinner(dataBinding.spnFacility, listFacility, facility);

        dataBinding.recyclerDrawings.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));

        listDrawings = new ArrayList<>();
        drawingsListAdapter = new DrawingsListAdapter(this, listDrawings);
        dataBinding.recyclerDrawings.setAdapter(drawingsListAdapter);

        requestDrawingsList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_download_all:
                //TODO
                break;
        }
    }

    private void initSpinner(AppCompatSpinner spinner, List<String> list, String selectData) {
        spinner.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, list));

        for (int i = 0; i < list.size(); i++) {
            String cate = list.get(i);
            if (cate.equals(selectData)) {
                spinner.setSelection(i);
                break;
            }
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

    public void sendSpinnerData() {
        Intent intent = new Intent(DrawingsListActivity.this, DrawingsActivity.class);
        intent.putStringArrayListExtra("category_list", (ArrayList<String>) listCategory);
        intent.putStringArrayListExtra("architecture_list", (ArrayList<String>) listArchitecture);
        intent.putStringArrayListExtra("research_list", (ArrayList<String>) listResearch);
        intent.putStringArrayListExtra("facility_list", (ArrayList<String>) listFacility);
        intent.putExtra("category", category);
        intent.putExtra("architecture", architecture);
        intent.putExtra("research", research);
        intent.putExtra("facility", facility);
        startActivity(intent);
    }
}
