package kr.co.bcoben.activity;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.MenuSelectListAdapter;
import kr.co.bcoben.adapter.ProjectListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityMainBinding;
import kr.co.bcoben.model.ProjectData;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.getAppVersion;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener, DrawerLayout.DrawerListener {

    public enum CurrentStep {
        GRADE, FACILITY, FACILITY_CATEGORY, ARCHITECTURE, RESEARCH
    }

    // side menu
    private CurrentStep currentStep = CurrentStep.GRADE;
    private MenuSelectListAdapter menuSelectListAdapter;
    private ArrayList<JSONObject> listFacility, listFacilityCategory, listArchitecture, listResearch;

    // main
    private ProjectListAdapter projectListAdapter;
    private ArrayList<ProjectData> projectList = new ArrayList<>();

    // Dummy Data
    private String[] projectName = {"서초구청", "성수초등학교", "프로젝트명"};

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // side menu
        dataBinding.mainDrawer.layoutDrawer.addDrawerListener(this);
        setNavigationViewWidth();

        dataBinding.mainDrawer.recyclerFacility.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dataBinding.mainDrawer.recyclerFacilityCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dataBinding.mainDrawer.recyclerArchitecture.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dataBinding.mainDrawer.recyclerResearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        listFacility = new ArrayList<>();
        listFacilityCategory = new ArrayList<>();
        listArchitecture = new ArrayList<>();
        listResearch = new ArrayList<>();
        menuSelectListAdapter = new MenuSelectListAdapter(this, listFacility);

        dataBinding.mainDrawer.recyclerFacility.setAdapter(menuSelectListAdapter);
        dataBinding.mainDrawer.recyclerFacilityCategory.setAdapter(menuSelectListAdapter);
        dataBinding.mainDrawer.recyclerArchitecture.setAdapter(menuSelectListAdapter);
        dataBinding.mainDrawer.recyclerResearch.setAdapter(menuSelectListAdapter);

        // main
        projectListAdapter = new ProjectListAdapter(this, projectList);
        dataBinding.recyclerProject.setAdapter(projectListAdapter);
        dataBinding.recyclerProject.setLayoutManager(new LinearLayoutManager(this));

        dataBinding.txtVersion.setText(getAppVersion());

        requestProjectList();
    }

    @Override
    public void onBackPressed() {
        finishApp(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // side menu
            case R.id.btn_input_research:
                break;
            case R.id.btn_next:
                switch (currentStep) {
                    case GRADE:
                        requestFacilityList();
                        showSelectedStep(CurrentStep.FACILITY);
                        break;
                    case FACILITY:
                        requestFacilityCategoryList();
                        showSelectedStep(CurrentStep.FACILITY_CATEGORY);
                        break;
                    case FACILITY_CATEGORY:
                        requestArchitectureList();
                        showSelectedStep(CurrentStep.ARCHITECTURE);
                        break;
                    case ARCHITECTURE:
                        requestResearchList();
                        showSelectedStep(CurrentStep.RESEARCH);
                        break;
                    case RESEARCH:
                        break;
                }
                break;

            // main
            case R.id.btn_home:
                break;
            case R.id.btn_add_project:
                break;
            case R.id.txt_logout:
                Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_logout);
                finish();
                break;
            case R.id.btn_update:
                break;
            case R.id.layout_register:
                dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
                break;
        }
    }

    // side menu
    public void onDrawerMenuClick(View v) {
        switch (v.getId()) {
            case R.id.layout_grade:
                showSelectedStep(CurrentStep.GRADE);
                break;
            case R.id.layout_facility:
                requestFacilityList();
                showSelectedStep(CurrentStep.FACILITY);
                break;
            case R.id.layout_facility_category:
                requestFacilityCategoryList();
                showSelectedStep(CurrentStep.FACILITY_CATEGORY);
                break;
            case R.id.layout_architecture:
                requestArchitectureList();
                showSelectedStep(CurrentStep.ARCHITECTURE);
                break;
            case R.id.layout_research:
                requestResearchList();
                showSelectedStep(CurrentStep.RESEARCH);
                break;
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        dataBinding.mainDrawer.layoutDrawer.bringChildToFront(drawerView);
        dataBinding.mainDrawer.layoutDrawer.requestLayout();
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        initUI();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    private void setNavigationViewWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) dataBinding.mainDrawer.naviMain.getLayoutParams();
        params.width = (int) (displayMetrics.widthPixels * 0.6);
        dataBinding.mainDrawer.naviMain.setLayoutParams(params);
    }

    private void initUI() {
        showSelectedStep(CurrentStep.GRADE);
    }

    private void showSelectedStep(CurrentStep step) {
        dataBinding.mainDrawer.layoutGradeContent.setVisibility(step == CurrentStep.GRADE ? View.VISIBLE : View.GONE);
        dataBinding.mainDrawer.layoutFacilityContent.setVisibility(step == CurrentStep.FACILITY ? View.VISIBLE : View.GONE);
        dataBinding.mainDrawer.layoutFacilityCategoryContent.setVisibility(step == CurrentStep.FACILITY_CATEGORY ? View.VISIBLE : View.GONE);
        dataBinding.mainDrawer.layoutArchitectureContent.setVisibility(step == CurrentStep.ARCHITECTURE ? View.VISIBLE : View.GONE);
        dataBinding.mainDrawer.layoutResearchContent.setVisibility(step == CurrentStep.RESEARCH ? View.VISIBLE : View.GONE);

        currentStep = step;

        updateStepMenuUI();
    }

    private void updateStepMenuUI() {
        dataBinding.mainDrawer.layoutGrade.setBackgroundColor(currentStep == CurrentStep.GRADE ? getResources().getColor(R.color.colorBackgroundPink) : getResources().getColor(R.color.colorPrimary));
        dataBinding.mainDrawer.layoutFacility.setBackgroundColor(currentStep == CurrentStep.FACILITY ? getResources().getColor(R.color.colorBackgroundPink) : getResources().getColor(R.color.colorPrimary));
        dataBinding.mainDrawer.layoutFacilityCategory.setBackgroundColor(currentStep == CurrentStep.FACILITY_CATEGORY ? getResources().getColor(R.color.colorBackgroundPink) : getResources().getColor(R.color.colorPrimary));
        dataBinding.mainDrawer.layoutArchitecture.setBackgroundColor(currentStep == CurrentStep.ARCHITECTURE ? getResources().getColor(R.color.colorBackgroundPink) : getResources().getColor(R.color.colorPrimary));
        dataBinding.mainDrawer.layoutResearch.setBackgroundColor(currentStep == CurrentStep.RESEARCH ? getResources().getColor(R.color.colorBackgroundPink) : getResources().getColor(R.color.colorPrimary));

        dataBinding.mainDrawer.txtGradeTitle.setTextColor(currentStep == CurrentStep.GRADE ? getResources().getColor(R.color.colorTextNavy) : getResources().getColor(R.color.colorWhite));
        dataBinding.mainDrawer.txtFacilityTitle.setTextColor(currentStep == CurrentStep.FACILITY ? getResources().getColor(R.color.colorTextNavy) : getResources().getColor(R.color.colorWhite));
        dataBinding.mainDrawer.txtFacilityCategoryTitle.setTextColor(currentStep == CurrentStep.FACILITY_CATEGORY ? getResources().getColor(R.color.colorTextNavy) : getResources().getColor(R.color.colorWhite));
        dataBinding.mainDrawer.txtArchitectureTitle.setTextColor(currentStep == CurrentStep.ARCHITECTURE ? getResources().getColor(R.color.colorTextNavy) : getResources().getColor(R.color.colorWhite));
        dataBinding.mainDrawer.txtResearchTitle.setTextColor(currentStep == CurrentStep.RESEARCH ? getResources().getColor(R.color.colorTextNavy) : getResources().getColor(R.color.colorWhite));
    }

    //TODO request project list api
    private void requestProjectList() {
        //TODO add dummy data for test
        projectList.clear();
        for (String name : projectName) {
            ProjectData data = new ProjectData(name);
            projectList.add(data);
        }
        projectListAdapter.setList(projectList);
    }

    //TODO request facility list api
    private void requestFacilityList() {
        //TODO add dummy data for test
        listFacility.clear();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "본관동");
            listFacility.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "별관동");
            listFacility.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "기숙사동");
            listFacility.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menuSelectListAdapter.setList(listFacility);
        menuSelectListAdapter.notifyDataSetChanged();
    }

    //TODO request facility category list api
    private void requestFacilityCategoryList() {
        //TODO add dummy data for test
        listFacilityCategory.clear();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "건축");
            listFacilityCategory.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "지하도상가");
            listFacilityCategory.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "교량");
            listFacilityCategory.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "터널");
            listFacilityCategory.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menuSelectListAdapter.setList(listFacilityCategory);
        menuSelectListAdapter.notifyDataSetChanged();
    }

    //TODO request architecture list api
    private void requestArchitectureList() {
        //TODO add dummy data for test
        listArchitecture.clear();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "RC조");
            listArchitecture.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "S조");
            listArchitecture.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "SRC조");
            listArchitecture.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menuSelectListAdapter.setList(listArchitecture);
        menuSelectListAdapter.notifyDataSetChanged();
    }

    //TODO request research list api
    private void requestResearchList() {
        //TODO add dummy data for test
        listResearch.clear();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "(20개소) 비파괴, 장비조사 | 지반(BH)");
            listResearch.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menuSelectListAdapter.setList(listResearch);
        menuSelectListAdapter.notifyDataSetChanged();
    }
}
