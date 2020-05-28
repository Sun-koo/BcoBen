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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.MenuSelectListAdapter;
import kr.co.bcoben.adapter.ProjectDataPageAdapter;
import kr.co.bcoben.adapter.ProjectListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityMainBinding;
import kr.co.bcoben.model.ProjectData;
import kr.co.bcoben.model.ProjectListData;
import kr.co.bcoben.model.ProjectResearchData;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.getAppVersion;
import static kr.co.bcoben.util.CommonUtil.hideKeyboard;
import static kr.co.bcoben.util.CommonUtil.showToast;

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
    private ProjectDataPageAdapter projectPageAdapter;
    private List<ProjectListData> projectList = new ArrayList<>();
    private List<ProjectData> projectDataList = new ArrayList<>();

    // Dummy Data
    private String[] projectName = {"서초구청", "성수초등학교", "프로젝트명"};
    private String[] projectFacility = {"본관동", "별관동", "기숙사동", "대극장"};
    private String[] projectResearchFacCate = {"건축", "지하도상가", "교량", "터널", "지하차도", "복개구조물", "항만", "항만외곽", "댐"};
    private String[] projectResearchArch = {"RC조", "S조", "SRC조", "조적도", "RC교", "T Beam교", "프리플렉스교", "사장교", "현수교"};
    private String[] projectResearchTitle = {
            "비파괴, 장비조사 | 지반(BH)",
            "비파괴, 장비조사 | 기울기(SL)",
            "비파괴, 장비조사 | 기초(BF)",
            "비파괴, 장비조사 | 탄산화(NE)",
            "비파괴, 장비조사 | 강재부식(MC)",
            "비파괴, 장비조사 | 내화피복(FC)",
            "비파괴, 장비조사 | 철근탐사(RL)",
            "비파괴, 장비조사 | 코아채취(CO)",
            "외관조사"
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        // side menu
        dataBinding.mainDrawer.layoutDrawer.addDrawerListener(this);
        dataBinding.mainDrawer.layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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

        initUI();

        // main
        projectListAdapter = new ProjectListAdapter(this, projectList);
        dataBinding.recyclerProject.setAdapter(projectListAdapter);
        dataBinding.recyclerProject.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.txtVersion.setText(getAppVersion());

        projectPageAdapter = new ProjectDataPageAdapter(getSupportFragmentManager(), getLifecycle(), projectDataList);
        dataBinding.mainDrawer.mainContents.pagerProjectData.setAdapter(projectPageAdapter);

        requestProjectList();
        requestProjectDataList();
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
                goToDrawingsPage();
                break;
            case R.id.btn_next:
                goToNextStep();
                break;
            case R.id.layout_detail_safe_check:
                setSelectedText(getResources().getString(R.string.side_menu_detail_safe_check));
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
        hideKeyboard(this);

        showSelectedStep(CurrentStep.GRADE);
        dataBinding.mainDrawer.layoutFacility.setClickable(false);
        dataBinding.mainDrawer.layoutFacilityCategory.setClickable(false);
        dataBinding.mainDrawer.layoutArchitecture.setClickable(false);
        dataBinding.mainDrawer.layoutResearch.setClickable(false);

        dataBinding.mainDrawer.txtGrade.setText("");
        dataBinding.mainDrawer.txtFacility.setText("");
        dataBinding.mainDrawer.txtFacilityCategory.setText("");
        dataBinding.mainDrawer.txtArchitecture.setText("");
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

    private void goToNextStep() {
        switch (currentStep) {
            case GRADE:
                String grade = dataBinding.mainDrawer.editGradeSelf.getText().toString();

                if (grade.isEmpty()) {
                    showToast(R.string.toast_input_grade);
                    return;
                }
                dataBinding.mainDrawer.txtGrade.setText(grade);
                dataBinding.mainDrawer.layoutFacility.setClickable(true);
                requestFacilityList();
                showSelectedStep(CurrentStep.FACILITY);
                break;

            case FACILITY:
                String facility = dataBinding.mainDrawer.editFacilitySelf.getText().toString();

                if (facility.isEmpty()) {
                    showToast(R.string.toast_input_facility);
                    return;
                }
                dataBinding.mainDrawer.txtFacility.setText(facility);
                dataBinding.mainDrawer.layoutFacilityCategory.setClickable(true);
                requestFacilityCategoryList();
                showSelectedStep(CurrentStep.FACILITY_CATEGORY);
                break;

            case FACILITY_CATEGORY:
                String facilityCategory = dataBinding.mainDrawer.editFacilityCategorySelf.getText().toString();

                if (facilityCategory.isEmpty()) {
                    showToast(R.string.toast_input_facility_category);
                    return;
                }
                dataBinding.mainDrawer.txtFacilityCategory.setText(facilityCategory);
                dataBinding.mainDrawer.layoutArchitecture.setClickable(true);
                requestArchitectureList();
                showSelectedStep(CurrentStep.ARCHITECTURE);
                break;

            case ARCHITECTURE:
                String architecture = dataBinding.mainDrawer.editArchitectureSelf.getText().toString();

                if (architecture.isEmpty()) {
                    showToast(R.string.toast_input_architecture);
                    return;
                }
                dataBinding.mainDrawer.txtArchitecture.setText(architecture);
                dataBinding.mainDrawer.layoutResearch.setClickable(true);
                requestResearchList();
                showSelectedStep(CurrentStep.RESEARCH);
                break;

            case RESEARCH:
                String research = dataBinding.mainDrawer.editResearchSelf.getText().toString();

                if (research.isEmpty()) {
                    showToast(R.string.toast_input_research);
                    return;
                }

                break;
        }
    }

    public void setSelectedText(String value) {
        switch (currentStep) {
            case GRADE:
                dataBinding.mainDrawer.txtGrade.setText(value);
                dataBinding.mainDrawer.layoutFacility.setClickable(true);
                requestFacilityList();
                showSelectedStep(CurrentStep.FACILITY);
                break;

            case FACILITY:
                dataBinding.mainDrawer.txtFacility.setText(value);
                dataBinding.mainDrawer.layoutFacilityCategory.setClickable(true);
                requestFacilityCategoryList();
                showSelectedStep(CurrentStep.FACILITY_CATEGORY);
                break;

            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.txtFacilityCategory.setText(value);
                dataBinding.mainDrawer.layoutArchitecture.setClickable(true);
                requestArchitectureList();
                showSelectedStep(CurrentStep.ARCHITECTURE);
                break;

            case ARCHITECTURE:
                dataBinding.mainDrawer.txtArchitecture.setText(value);
                dataBinding.mainDrawer.layoutResearch.setClickable(true);
                requestResearchList();
                showSelectedStep(CurrentStep.RESEARCH);
                break;

            case RESEARCH:
                dataBinding.mainDrawer.txtResearch.setText(value);
                break;
        }
    }

    private void goToDrawingsPage() {
        Intent intent = new Intent(MainActivity.this, DrawingsListActivity.class);

        intent.putStringArrayListExtra("category_list", getCategoryList(listFacilityCategory));
        intent.putStringArrayListExtra("architecture_list", getCategoryList(listArchitecture));
        intent.putStringArrayListExtra("research_list", getCategoryList(listResearch));
        intent.putStringArrayListExtra("facility_list", getCategoryList(listFacility));
        intent.putExtra("category", dataBinding.mainDrawer.txtFacilityCategory.getText().toString());
        intent.putExtra("architecture", dataBinding.mainDrawer.txtArchitecture.getText().toString());
        intent.putExtra("research", dataBinding.mainDrawer.txtResearch.getText().toString());
        intent.putExtra("facility", dataBinding.mainDrawer.txtFacility.getText().toString());
        startActivity(intent);
    }

    private ArrayList<String> getCategoryList(ArrayList<JSONObject> list_json) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0;i<list_json.size();i++) {
            String category = list_json.get(i).optString("name");

            items.add(category);
        }

        return items;
    }

    //TODO request project list api
    private void requestProjectList() {
        //TODO add dummy data for test
        projectList.clear();
        for (String name : projectName) {
            ProjectListData data = new ProjectListData(name);
            projectList.add(data);
        }
        projectListAdapter.setList(projectList);
    }

    //TODO request project data list api
    private void requestProjectDataList() {
        //TODO add dummy data for test
        projectDataList.clear();
        for (String facility : projectFacility) {
            List<ProjectResearchData> researchDataList = new ArrayList<>();
            for (int i = 0; i < projectResearchFacCate.length; i++) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, c.get(Calendar.MONTH) - i);
                c.set(Calendar.DATE, c.get(Calendar.DATE) - (i * 2));
                researchDataList.add(new ProjectResearchData(projectResearchFacCate[i], projectResearchArch[i], projectResearchTitle[i], c.getTime(), (i + 1) * 3 + 2, 3));
            }
            ProjectData data = new ProjectData(facility, researchDataList);
            projectDataList.add(data);

            dataBinding.mainDrawer.mainContents.tabProjectFacility.addTab(dataBinding.mainDrawer.mainContents.tabProjectFacility.newTab().setText(facility));
        }
        projectPageAdapter.setProjectDataList(projectDataList);
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
