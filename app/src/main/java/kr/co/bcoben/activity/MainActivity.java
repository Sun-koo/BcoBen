package kr.co.bcoben.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.MenuSelectListAdapter;
import kr.co.bcoben.adapter.ProjectDataPageAdapter;
import kr.co.bcoben.adapter.ProjectListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CameraDialog;
import kr.co.bcoben.component.DrawingsSelectDialog;
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

    public enum DatePickerType {
        START, END
    }

    // side menu
    private CurrentResearchStep currentResearchStep = CurrentResearchStep.GRADE;
    private CurrentProjectStep currentProjectStep = CurrentProjectStep.SUMMARY;
    private MenuSelectListAdapter menuSelectListAdapter;

    private DatePickerType datePickerType = DatePickerType.START;
    private int startYear, startMonth, startDay;

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
            // side menu(research)
            case R.id.btn_input_research:
                goToDrawingsPage();
                break;
            case R.id.btn_next:
                researchNextStep();
                break;
            // side menu(project)
            case R.id.btn_input_project:

                break;
            case R.id.btn_project_next: case R.id.btn_arrow_next:
                projectNextStep();
                break;
            case R.id.layout_project_start_date:
                // 현재 날짜 Get
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
                SimpleDateFormat monthFormat = new SimpleDateFormat("M", Locale.getDefault());
                SimpleDateFormat yearFormat = new SimpleDateFormat("y", Locale.getDefault());

                String curYear = yearFormat.format(currentTime);
                String curMonth = monthFormat.format(currentTime);
                String curDay = dayFormat.format(currentTime);

                datePickerType = DatePickerType.START;
                DatePickerDialog dialog_start = new DatePickerDialog(this, onDateSetListener, Integer.parseInt(curYear), Integer.parseInt(curMonth) - 1, Integer.parseInt(curDay));
                dialog_start.show();
                break;
            case R.id.layout_project_end_date:
                datePickerType = DatePickerType.END;
                DatePickerDialog dialog_end = new DatePickerDialog(this, onDateSetListener, startYear, startMonth, startDay);
                dialog_end.show();
                break;
            case R.id.btn_drawings_upload:
                showCameraDialog();
                break;

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
                openDrawerResearch("");
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

    public void openDrawerResearch(String facility) {
        String project = projectListAdapter.getSelectedProject();
        String grade = dataBinding.mainDrawer.mainContents.txtSubTitle.getText().toString().replace("(", "").replace(")", "");

        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.VISIBLE);
        initDrawerResearch(project, grade, facility);

        setNavigationViewWidth(false);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    public void openDrawerProject() {
        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.VISIBLE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.GONE);
        initDrawerProject();

        setNavigationViewWidth(true);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    public boolean isDrawingOpen() {
        return dataBinding.mainDrawer.layoutDrawer.isDrawerOpen(GravityCompat.START);
    }

    public void closeDrawer() {
        dataBinding.mainDrawer.layoutDrawer.closeDrawer(GravityCompat.START);
    }

    private void setNavigationViewWidth(boolean isProject) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        LinearLayout.LayoutParams params_home = (LinearLayout.LayoutParams) dataBinding.layoutHome.getLayoutParams();
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) dataBinding.mainDrawer.naviMain.getLayoutParams();
        params.width = (int) (displayMetrics.widthPixels * (isProject ? 1.0 : 0.6));
        if (isProject) {
            params.width -= params_home.width;
        }
        dataBinding.mainDrawer.naviMain.setLayoutParams(params);
    }

    private void initDrawerResearch(String project, String grade, String facility) {
        updateStepResearchMenuUI(CurrentResearchStep.GRADE);
        dataBinding.mainDrawer.layoutRegResearch.txtProjectName.setText(project);

        dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setClickable(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setClickable(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(false);

        dataBinding.mainDrawer.layoutRegResearch.txtGrade.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtFacility.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.setText("");
        dataBinding.mainDrawer.layoutRegResearch.txtResearch.setText("");

        if (grade != null && !grade.isEmpty()) {
            setResearchSelectedText(grade);
        }
        if (facility != null && !facility.isEmpty()) {
            setResearchSelectedText(facility);
        }
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

        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectName.setText("");
        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.setText("");
        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.setText("");
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

        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.layoutRoot.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.recyclerList.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.layoutRoot.setVisibility(View.GONE);

        switch (currentProjectStep) {
            case SUMMARY:
                dataBinding.mainDrawer.layoutRegProject.layoutSummary.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.layoutRoot.setVisibility(View.VISIBLE);

                break;
            case GRADE:
                dataBinding.mainDrawer.layoutRegProject.layoutGrade.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerList.setVisibility(View.VISIBLE);

                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegProject.layoutFacility.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerList.setVisibility(View.VISIBLE);

                break;
            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.layoutRegProject.layoutFacilityCategory.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerList.setVisibility(View.VISIBLE);

                break;
            case ARCHITECTURE:
                dataBinding.mainDrawer.layoutRegProject.layoutArchitecture.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerList.setVisibility(View.VISIBLE);

                break;
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegProject.layoutResearch.setSelected(true);

                break;
            case DRAWINGS:
                dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.layoutRoot.setVisibility(View.VISIBLE);

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
        setResearchSelectedText(name);
    }

    private void projectNextStep() {
        List<String> selectedValue = new ArrayList<>();
        switch (currentProjectStep) {
            case SUMMARY:
                String name = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectName.getText().toString();
                String startDate = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.getText().toString();
                String endDate = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.getText().toString();
                if (name.isEmpty()) {
                    showToast(R.string.toast_input_project_name);
                    return;
                }
                if (startDate.isEmpty() || endDate.isEmpty()) {
                    showToast(R.string.toast_input_project_date);
                    return;
                }
                selectedValue.add(name);
                selectedValue.add(startDate);
                selectedValue.add(endDate);
                break;
            case GRADE:

                break;
            case FACILITY:

                break;
            case FACILITY_CATEGORY:

                break;
            case ARCHITECTURE:

                break;
            case RESEARCH:

                break;
            case DRAWINGS:

                break;
        }

        hideKeyboard(this);
        setProjectSelectedText(selectedValue);
    }

    public void setResearchSelectedText(String value) {
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

    public void setProjectSelectedText(List<String> value) {
        switch (currentProjectStep) {
            case SUMMARY:
                dataBinding.mainDrawer.layoutRegProject.txtSummaryName.setText(value.get(0));
                dataBinding.mainDrawer.layoutRegProject.txtSummaryDate.setText(value.get(1) + " ~ " + value.get(2));
                dataBinding.mainDrawer.layoutRegProject.layoutGrade.setClickable(true);
                updateStepProjectMenuUI(CurrentProjectStep.GRADE);
                break;
            case GRADE:
                String grade = value.get(0);
                for (int i=1;i<value.size();i++) {
                    grade = grade + ", " + value.get(i);
                }
                dataBinding.mainDrawer.layoutRegProject.txtGrade.setText(grade);
                dataBinding.mainDrawer.layoutRegProject.layoutFacility.setClickable(true);
                updateStepProjectMenuUI(CurrentProjectStep.FACILITY);
                break;
            case FACILITY:
                String facility = value.get(0);
                for (int i=1;i<value.size();i++) {
                    facility = facility + ", " + value.get(i);
                }
                dataBinding.mainDrawer.layoutRegProject.txtFacility.setText(facility);
                dataBinding.mainDrawer.layoutRegProject.layoutFacilityCategory.setClickable(true);
                updateStepProjectMenuUI(CurrentProjectStep.FACILITY_CATEGORY);
                break;
            case FACILITY_CATEGORY:
                String facilityCategory = value.get(0);
                for (int i=1;i<value.size();i++) {
                    facilityCategory = facilityCategory + ", " + value.get(i);
                }
                dataBinding.mainDrawer.layoutRegProject.txtFacilityCategory.setText(facilityCategory);
                dataBinding.mainDrawer.layoutRegProject.layoutArchitecture.setClickable(true);
                updateStepProjectMenuUI(CurrentProjectStep.ARCHITECTURE);
                break;
            case ARCHITECTURE:
                String architecture = value.get(0);
                for (int i=1;i<value.size();i++) {
                    architecture = architecture + ", " + value.get(i);
                }
                dataBinding.mainDrawer.layoutRegProject.txtArchitecture.setText(architecture);
                dataBinding.mainDrawer.layoutRegProject.layoutResearch.setClickable(true);
                updateStepProjectMenuUI(CurrentProjectStep.RESEARCH);
                break;
            case RESEARCH:
                //TODO
                updateStepProjectMenuUI(CurrentProjectStep.DRAWINGS);
                break;
            case DRAWINGS:
                //TODO
                break;
        }
    }

    // 날짜 선택 다이얼로그
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startYear = year;
            startMonth = month;
            startDay = dayOfMonth;
            String monthStr = String.valueOf(month + 1);
            String dayStr = String.valueOf(dayOfMonth);

            if (monthStr.length() == 1) {
                monthStr = "0" + monthStr;
            }
            if (dayStr.length() == 1) {
                dayStr = "0" + dayStr;
            }

            String date = year + "/" + monthStr + "/" + dayStr;

            if (datePickerType == DatePickerType.START) {
                dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.setText(date);
            } else {
                dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.setText(date);
            }
        }
    };

    // 카메라 촬영 / 앨범 선택 다이얼로그 출력
    private void showCameraDialog() {
        final CameraDialog dialog = new CameraDialog(this);
        dialog.selectCameraListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                dialog.dismiss();
            }
        });
        dialog.selectAlbumInputListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                dialog.dismiss();
            }
        });
        dialog.show();
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDrawer();
            }
        }, 1000);
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
