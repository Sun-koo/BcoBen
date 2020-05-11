package kr.co.bcoben.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.MenuSelectListAdapter;
import kr.co.bcoben.adapter.ProjectListAdapter;

import static kr.co.bcoben.util.CommonUtil.finishApp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener {

    public enum CurrentStep {
        GRADE, FACILITY, FACILITY_CATEGORY, ARCHITECTURE, RESEARCH
    }

    // side menu
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private RelativeLayout gradeLayout, facilityLayout, facilityCategoryLayout, architectureLayout, researchLayout;
    private TextView txtGradeTitle, txtGrade, txtFacilityTitle, txtFacility, txtFacilityCategoryTitle, txtFacilityCategory, txtArchitectureTitle, txtArchitecture, txtResearchTitle;
    private LinearLayout gradeContentLayout, facilityContentLayout, facilityCategoryContentLayout, architectureContentLayout, researchContentLayout;

    private CurrentStep currentStep = CurrentStep.GRADE;

    private RecyclerView rvFacility, rvFacilityCategory, rvArchitecture, rvResearch;
    private MenuSelectListAdapter menuSelectListAdapter;
    private ArrayList<JSONObject> listFacility, listFacilityCategory, listArchitecture, listResearch;

    // main
    private RecyclerView rvProject;
    private ProjectListAdapter projectListAdapter;
    private ArrayList<JSONObject> listProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // side menu
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(this);

        navigationView = findViewById(R.id.nv_main);
        setNavigationViewWidth();

        TextView txtProjectName = findViewById(R.id.txt_project_name);

        gradeLayout = findViewById(R.id.grade_layout);
        txtGradeTitle = findViewById(R.id.txt_grade_title);
        txtGrade = findViewById(R.id.txt_grade);

        facilityLayout = findViewById(R.id.facility_layout);
        txtFacilityTitle = findViewById(R.id.txt_facility_title);
        txtFacility = findViewById(R.id.txt_facility);

        facilityCategoryLayout = findViewById(R.id.facility_category_layout);
        txtFacilityCategoryTitle = findViewById(R.id.txt_facility_category_title);
        txtFacilityCategory = findViewById(R.id.txt_facility_category);

        architectureLayout = findViewById(R.id.architecture_layout);
        txtArchitectureTitle = findViewById(R.id.txt_architecture_title);
        txtArchitecture = findViewById(R.id.txt_architecture);

        researchLayout = findViewById(R.id.research_layout);
        txtResearchTitle = findViewById(R.id.txt_research_title);

        Button btnInputResearch = findViewById(R.id.btn_input_research);

        ImageButton ibNext = findViewById(R.id.ib_next);

        gradeContentLayout = findViewById(R.id.grade_content_layout);
        facilityContentLayout = findViewById(R.id.facility_content_layout);
        facilityCategoryContentLayout = findViewById(R.id.facility_category_content_layout);
        architectureContentLayout = findViewById(R.id.architecture_content_layout);
        researchContentLayout = findViewById(R.id.research_content_layout);

        gradeLayout.setOnClickListener(this);
        facilityLayout.setOnClickListener(this);
        facilityCategoryLayout.setOnClickListener(this);
        architectureLayout.setOnClickListener(this);
        researchLayout.setOnClickListener(this);
        btnInputResearch.setOnClickListener(this);
        ibNext.setOnClickListener(this);

        rvFacility = findViewById(R.id.rv_facility);
        rvFacilityCategory = findViewById(R.id.rv_facility_category);
        rvArchitecture = findViewById(R.id.rv_architecture);
        rvResearch = findViewById(R.id.rv_research);
        rvFacility.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvFacilityCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvArchitecture.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvResearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        listFacility = new ArrayList<>();
        listFacilityCategory = new ArrayList<>();
        listArchitecture = new ArrayList<>();
        listResearch = new ArrayList<>();
        menuSelectListAdapter = new MenuSelectListAdapter(this, listFacility);
        rvFacility.setAdapter(menuSelectListAdapter);
        rvFacilityCategory.setAdapter(menuSelectListAdapter);
        rvArchitecture.setAdapter(menuSelectListAdapter);
        rvResearch.setAdapter(menuSelectListAdapter);

        // main
        ImageButton ibHome = findViewById(R.id.ib_home);
        ImageButton ibAdd = findViewById(R.id.ib_add);
        TextView txtLogout = findViewById(R.id.txt_logout);
        TextView txtVersion = findViewById(R.id.txt_version);
        Button btnUpdate = findViewById(R.id.btn_update);

        TextView txtSubTitle = findViewById(R.id.txt_sub_title);
        RelativeLayout regLayout = findViewById(R.id.regist_layout);

        ibHome.setOnClickListener(this);
        ibAdd.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        regLayout.setOnClickListener(this);

        rvProject = findViewById(R.id.rv_project);
        rvProject.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        listProject = new ArrayList<>();
        projectListAdapter = new ProjectListAdapter(this, listProject);
        rvProject.setAdapter(projectListAdapter);

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
            case R.id.grade_layout:
                showSelectedStep(CurrentStep.GRADE);
                break;

            case R.id.facility_layout:
                requestFacilityList();
                showSelectedStep(CurrentStep.FACILITY);
                break;

            case R.id.facility_category_layout:
                requestFacilityCategoryList();
                showSelectedStep(CurrentStep.FACILITY_CATEGORY);
                break;

            case R.id.architecture_layout:
                requestArchitectureList();
                showSelectedStep(CurrentStep.ARCHITECTURE);
                break;

            case R.id.research_layout:
                requestResearchList();
                showSelectedStep(CurrentStep.RESEARCH);
                break;

            case R.id.btn_input_research:
                break;

            case R.id.ib_next:
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
            case R.id.ib_home:
                break;

            case R.id.ib_add:
                break;

            case R.id.txt_logout:
                Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_logout);
                finish();
                break;

            case R.id.btn_update:
                break;

            case R.id.regist_layout:
                drawerLayout.openDrawer(GravityCompat.START);
//                backgroundLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
        drawerLayout.bringChildToFront(drawerView);
        drawerLayout.requestLayout();
    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {
        initView();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    private void setNavigationViewWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = (int) (displayMetrics.widthPixels * 0.6);
        navigationView.setLayoutParams(params);
    }

    private void initView() {
        showSelectedStep(CurrentStep.GRADE);
    }

    private void showSelectedStep(CurrentStep step) {
        gradeContentLayout.setVisibility(step == CurrentStep.GRADE ? View.VISIBLE : View.GONE);
        facilityContentLayout.setVisibility(step == CurrentStep.FACILITY ? View.VISIBLE : View.GONE);
        facilityCategoryContentLayout.setVisibility(step == CurrentStep.FACILITY_CATEGORY ? View.VISIBLE : View.GONE);
        architectureContentLayout.setVisibility(step == CurrentStep.ARCHITECTURE ? View.VISIBLE : View.GONE);
        researchContentLayout.setVisibility(step == CurrentStep.RESEARCH ? View.VISIBLE : View.GONE);

        currentStep = step;

        updateStepMenuUI();
    }

    private void updateStepMenuUI() {
        gradeLayout.setBackgroundColor(currentStep == CurrentStep.GRADE ? getResources().getColor(R.color.pinkBackgroundColor) : getResources().getColor(R.color.lightKeyColor));
        facilityLayout.setBackgroundColor(currentStep == CurrentStep.FACILITY ? getResources().getColor(R.color.pinkBackgroundColor) : getResources().getColor(R.color.lightKeyColor));
        facilityCategoryLayout.setBackgroundColor(currentStep == CurrentStep.FACILITY_CATEGORY ? getResources().getColor(R.color.pinkBackgroundColor) : getResources().getColor(R.color.lightKeyColor));
        architectureLayout.setBackgroundColor(currentStep == CurrentStep.ARCHITECTURE ? getResources().getColor(R.color.pinkBackgroundColor) : getResources().getColor(R.color.lightKeyColor));
        researchLayout.setBackgroundColor(currentStep == CurrentStep.RESEARCH ? getResources().getColor(R.color.pinkBackgroundColor) : getResources().getColor(R.color.lightKeyColor));
        txtGradeTitle.setTextColor(currentStep == CurrentStep.GRADE ? getResources().getColor(R.color.lightKeyColor) : getResources().getColor(R.color.whiteColor));
        txtFacilityTitle.setTextColor(currentStep == CurrentStep.FACILITY ? getResources().getColor(R.color.lightKeyColor) : getResources().getColor(R.color.whiteColor));
        txtFacilityCategoryTitle.setTextColor(currentStep == CurrentStep.FACILITY_CATEGORY ? getResources().getColor(R.color.lightKeyColor) : getResources().getColor(R.color.whiteColor));
        txtArchitectureTitle.setTextColor(currentStep == CurrentStep.ARCHITECTURE ? getResources().getColor(R.color.lightKeyColor) : getResources().getColor(R.color.whiteColor));
        txtResearchTitle.setTextColor(currentStep == CurrentStep.RESEARCH ? getResources().getColor(R.color.lightKeyColor) : getResources().getColor(R.color.whiteColor));
    }

    //TODO request project list api
    private void requestProjectList() {
        //TODO add dummy data for test
        listProject.clear();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "서초구청");
            listProject.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "성수초등학교");
            listProject.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "프로젝트명");
            listProject.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        projectListAdapter.setList(listProject);
        projectListAdapter.notifyDataSetChanged();
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
