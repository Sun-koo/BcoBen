package kr.co.bcoben.activity;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener {

    public enum CurrentResearchStep {
        GRADE, FACILITY, FACILITY_CATEGORY, ARCHITECTURE, RESEARCH
    }

    public enum CurrentProjectStep {
        SUMMARY, GRADE, FACILITY, FACILITY_CATEGORY, ARCHITECTURE, RESEARCH, DRAWINGS
    }

    // side menu
    private CurrentResearchStep currentResearchStep = CurrentResearchStep.GRADE;
    private CurrentProjectStep currentProjectStep = CurrentProjectStep.SUMMARY;
    private MenuSelectListAdapter menuSelectListAdapter;
    private ArrayList<JSONObject> listFacility, listFacilityCategory, listArchitecture, listResearch;
    private List<String> regResearchGrade = new ArrayList<>();
    private List<String> regResearchFacility = new ArrayList<>();
    private List<String> regResearchFacCate = new ArrayList<>();
    private List<String> regResearchArchitecture = new ArrayList<>();
    private List<String> regResearchResearch = new ArrayList<>();

    // main
    private ProjectListAdapter projectListAdapter;
    private ProjectDataPageAdapter projectPageAdapter;
    private List<ProjectListData> projectList = new ArrayList<>();
    private List<ProjectData> projectDataList = new ArrayList<>();

    // Dummy Data
    private String[] projectGrade = {"정밀안전진단"};
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
        dataBinding.mainDrawer.layoutDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                hideKeyboard(activity);
            }
        });
        dataBinding.mainDrawer.layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        menuSelectListAdapter = new MenuSelectListAdapter(this, regResearchGrade);
        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.setAdapter(menuSelectListAdapter);
        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.setLayoutManager(new LinearLayoutManager(this));

        requestRegResearchData();

        // main
        projectListAdapter = new ProjectListAdapter(this, projectList);
        dataBinding.recyclerProject.setAdapter(projectListAdapter);
        dataBinding.recyclerProject.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.txtVersion.setText(getAppVersion());

        projectPageAdapter = new ProjectDataPageAdapter(getSupportFragmentManager(), projectDataList);
        dataBinding.mainDrawer.mainContents.pagerProjectData.setAdapter(projectPageAdapter);
        dataBinding.mainDrawer.mainContents.pagerProjectData.setOffscreenPageLimit(5);

        dataBinding.mainDrawer.mainContents.tabProjectFacility.setupWithViewPager(dataBinding.mainDrawer.mainContents.pagerProjectData);

        requestProjectList();
        requestProjectDataList();
    }

    @Override
    public void onBackPressed() {
        if (dataBinding.mainDrawer.layoutDrawer.isDrawerOpen(GravityCompat.START)) {
            dataBinding.mainDrawer.layoutDrawer.closeDrawer(GravityCompat.START);
        } else {
            finishApp(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // side menu
            case R.id.btn_input_research:
                goToDrawingsPage();
                break;
            case R.id.btn_next:
                researchNextStep();
                break;
//            case R.id.layout_detail_safe_check:
//                setSelectedText(getResources().getString(R.string.side_menu_detail_safe_check));
//                break;

            // main
            case R.id.btn_home:
                break;
            case R.id.btn_add_project:
                openDrawerProject();
                break;
            case R.id.txt_logout:
                Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_logout);
                finish();
                break;
            case R.id.btn_update:
                break;
            case R.id.layout_register_research:
                String project = projectListAdapter.getSelectedProject();
                openDrawerResearch(project, "", "");
                break;
        }
    }

    // side menu(research)
    public void onDrawerResearchMenuClick(View v) {
        switch (v.getId()) {
            case R.id.layout_grade: updateStepResearchMenuUI(CurrentResearchStep.GRADE); break;
            case R.id.layout_facility: updateStepResearchMenuUI(CurrentResearchStep.FACILITY); break;
            case R.id.layout_facility_category: updateStepResearchMenuUI(CurrentResearchStep.FACILITY_CATEGORY); break;
            case R.id.layout_architecture: updateStepResearchMenuUI(CurrentResearchStep.ARCHITECTURE); break;
            case R.id.layout_research: updateStepResearchMenuUI(CurrentResearchStep.RESEARCH); break;
        }
    }

    // side menu(project)
    public void onDrawerProjectMenuClick(View v) {
        switch (v.getId()) {
            case R.id.layout_summary: updateStepProjectMenuUI(CurrentProjectStep.SUMMARY); break;
            case R.id.layout_grade: updateStepProjectMenuUI(CurrentProjectStep.GRADE); break;
            case R.id.layout_facility: updateStepProjectMenuUI(CurrentProjectStep.FACILITY); break;
            case R.id.layout_facility_category: updateStepProjectMenuUI(CurrentProjectStep.FACILITY_CATEGORY); break;
            case R.id.layout_architecture: updateStepProjectMenuUI(CurrentProjectStep.ARCHITECTURE); break;
            case R.id.layout_research: updateStepProjectMenuUI(CurrentProjectStep.RESEARCH); break;
            case R.id.layout_drawings: updateStepProjectMenuUI(CurrentProjectStep.DRAWINGS); break;
        }
    }

    public void openDrawerResearch(String project, String grade, String facility) {
        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.VISIBLE);
        initDrawerResearch();

        setNavigationViewWidth(false);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    public void openDrawerProject() {
        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.VISIBLE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.GONE);
        initDrawerProject();

        setNavigationViewWidth(false);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    public boolean isDrawingOpen() {
        return dataBinding.mainDrawer.layoutDrawer.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawing() {
        dataBinding.mainDrawer.layoutDrawer.closeDrawer(GravityCompat.START);
    }

    private void setNavigationViewWidth(boolean isProject) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) dataBinding.mainDrawer.naviMain.getLayoutParams();
        params.width = (int) (displayMetrics.widthPixels * (isProject ? 1.0 : 0.6));
        dataBinding.mainDrawer.naviMain.setLayoutParams(params);
    }

    private void initDrawerResearch() {
        updateStepResearchMenuUI(CurrentResearchStep.GRADE);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setClickable(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setClickable(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(false);

        dataBinding.mainDrawer.layoutRegResearch.txtGrade.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtFacility.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtResearch.setText("");
    }

    private void initDrawerProject() {
        updateStepProjectMenuUI(CurrentProjectStep.SUMMARY);
        dataBinding.mainDrawer.layoutRegProject.layoutGrade.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutFacility.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutFacilityCategory.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutArchitecture.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutResearch.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setClickable(false);

        dataBinding.mainDrawer.layoutRegProject.txtSummaryName.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtSummaryDate.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtGrade.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtFacility.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtFacilityCategory.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtArchitecture.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtResearch.setText("");
    }

    private void updateStepResearchMenuUI(CurrentResearchStep step) {
        currentResearchStep = step;

        dataBinding.mainDrawer.layoutRegResearch.layoutGrade.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setSelected(false);

        switch (currentResearchStep) {
            case GRADE:
                dataBinding.mainDrawer.layoutRegResearch.layoutGrade.setSelected(true);
                menuSelectListAdapter.setList(regResearchGrade);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setSelected(true);
                menuSelectListAdapter.setList(regResearchFacility);
                break;
            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setSelected(true);
                menuSelectListAdapter.setList(regResearchFacCate);
                break;
            case ARCHITECTURE:
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setSelected(true);
                menuSelectListAdapter.setList(regResearchArchitecture);
                break;
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setSelected(true);
                menuSelectListAdapter.setList(regResearchResearch);
                break;
        }
        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.scrollToPosition(0);
    }

    private void updateStepProjectMenuUI(CurrentProjectStep step) {
        currentProjectStep = step;

        dataBinding.mainDrawer.layoutRegProject.layoutSummary.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutGrade.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutFacility.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutFacilityCategory.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutArchitecture.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutResearch.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setSelected(false);

        switch (currentProjectStep) {
            case SUMMARY:
                dataBinding.mainDrawer.layoutRegProject.layoutSummary.setSelected(true);

                break;
            case GRADE:
                dataBinding.mainDrawer.layoutRegProject.layoutGrade.setSelected(true);

                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegProject.layoutFacility.setSelected(true);

                break;
            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.layoutRegProject.layoutFacilityCategory.setSelected(true);

                break;
            case ARCHITECTURE:
                dataBinding.mainDrawer.layoutRegProject.layoutArchitecture.setSelected(true);

                break;
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegProject.layoutResearch.setSelected(true);

                break;
            case DRAWINGS:
                dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setSelected(true);

                break;
        }
//        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.scrollToPosition(0);
    }

    private void researchNextStep() {
        String name = menuSelectListAdapter.getEditName();
        switch (currentResearchStep) {
            case GRADE:
                if (name.isEmpty()) {
                    showToast(R.string.toast_input_grade);
                    return;
                }
                regResearchGrade.add(name);
                break;
            case FACILITY:
                if (name.isEmpty()) {
                    showToast(R.string.toast_input_facility);
                    return;
                }
                regResearchFacility.add(name);
                break;
            case FACILITY_CATEGORY:
                if (name.isEmpty()) {
                    showToast(R.string.toast_input_facility_category);
                    return;
                }
                regResearchFacCate.add(name);
                break;
            case ARCHITECTURE:
                if (name.isEmpty()) {
                    showToast(R.string.toast_input_architecture);
                    return;
                }
                regResearchArchitecture.add(name);
                break;
            case RESEARCH:
                if (name.isEmpty()) {
                    showToast(R.string.toast_input_research);
                    return;
                }
                regResearchResearch.add(name);
                break;
        }

        hideKeyboard(this);
        setSelectedText(name);
    }

    public void setSelectedText(String value) {
        switch (currentResearchStep) {
            case GRADE:
                dataBinding.mainDrawer.layoutRegResearch.txtGrade.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setClickable(true);
                updateStepResearchMenuUI(CurrentResearchStep.FACILITY);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegResearch.txtFacility.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setClickable(true);
                updateStepResearchMenuUI(CurrentResearchStep.FACILITY_CATEGORY);
                break;
            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(true);
                updateStepResearchMenuUI(CurrentResearchStep.ARCHITECTURE);
                break;
            case ARCHITECTURE:
                dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(true);
                updateStepResearchMenuUI(CurrentResearchStep.RESEARCH);
                break;
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegResearch.txtResearch.setText(value);
                updateStepResearchMenuUI(CurrentResearchStep.RESEARCH);
                break;
        }
    }

    private void goToDrawingsPage() {
        Intent intent = new Intent(MainActivity.this, DrawingsListActivity.class);

        intent.putStringArrayListExtra("category_list", (ArrayList<String>) regResearchFacCate);
        intent.putStringArrayListExtra("architecture_list", (ArrayList<String>) regResearchArchitecture);
        intent.putStringArrayListExtra("research_list", (ArrayList<String>) regResearchResearch);
        intent.putStringArrayListExtra("facility_list", (ArrayList<String>) regResearchFacility);
        intent.putExtra("category", dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.getText().toString());
        intent.putExtra("architecture", dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.getText().toString());
        intent.putExtra("research", dataBinding.mainDrawer.layoutRegResearch.txtResearch.getText().toString());
        intent.putExtra("facility", dataBinding.mainDrawer.layoutRegResearch.txtFacility.getText().toString());
        startActivity(intent);
    }

    //TODO request project list api
    private void requestProjectList() {
        projectList.clear();
        boolean isFirst = true;
        for (String name : projectName) {
            ProjectListData data = new ProjectListData(name);
            if (isFirst) {
                data.setSelected(true);
                isFirst = false;
            }
            projectList.add(data);
        }
        projectListAdapter.setList(projectList);
    }

    //TODO request project data list api
    private void requestProjectDataList() {
        projectDataList.clear();
        int count = 0;
        for (String facility : projectFacility) {
            List<ProjectResearchData> researchDataList = new ArrayList<>();
            if (++count < projectFacility.length) {
                for (int i = 0; i < projectResearchFacCate.length; i++) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.MONTH, c.get(Calendar.MONTH) - i);
                    c.set(Calendar.DATE, c.get(Calendar.DATE) - (i * 2));
                    researchDataList.add(new ProjectResearchData(projectResearchFacCate[i], projectResearchArch[i], projectResearchTitle[i], c.getTime(), (i + 1) * 3 + 2, 3));
                }
            }
            ProjectData data = new ProjectData(facility, researchDataList);
            projectDataList.add(data);
        }
        projectPageAdapter.setProjectDataList(projectDataList);
    }

    private void requestRegResearchData() {
        regResearchGrade.clear();
        regResearchFacility.clear();
        regResearchFacCate.clear();
        regResearchArchitecture.clear();
        regResearchResearch.clear();

        regResearchGrade.addAll(Arrays.asList(projectGrade));
        regResearchFacility.addAll(Arrays.asList(projectFacility));
        regResearchFacCate.addAll(Arrays.asList(projectResearchFacCate));
        regResearchArchitecture.addAll(Arrays.asList(projectResearchArch));
        for (int i = 0; i < projectResearchTitle.length; i++) {
            regResearchResearch.add("(20개소) " + projectResearchTitle[i]);
        }

        menuSelectListAdapter.setList(regResearchGrade);
    }
}
