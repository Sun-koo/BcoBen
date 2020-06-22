package kr.co.bcoben.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.Dir;
import kr.co.bcoben.adapter.MenuCheckFacilityListAdapter;
import kr.co.bcoben.adapter.MenuCheckInputListAdapter;
import kr.co.bcoben.adapter.MenuCheckListAdapter;
import kr.co.bcoben.adapter.MenuCheckResearchListAdapter;
import kr.co.bcoben.adapter.MenuDrawingsListAdapter;
import kr.co.bcoben.adapter.MenuNodeBinder;
import kr.co.bcoben.adapter.MainResearchRegListAdapter;
import kr.co.bcoben.adapter.ProjectDataPageAdapter;
import kr.co.bcoben.adapter.ProjectListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CameraDialog;
import kr.co.bcoben.databinding.ActivityMainBinding;
import kr.co.bcoben.model.MenuCheckData;
import kr.co.bcoben.model.MenuCheckListData;
import kr.co.bcoben.model.MenuDrawingsData;
import kr.co.bcoben.model.MenuSelectFacilityData;
import kr.co.bcoben.model.ProjectData;
import kr.co.bcoben.model.ProjectListData;
import kr.co.bcoben.model.ProjectMainData;
import kr.co.bcoben.model.ResearchIdData;
import kr.co.bcoben.model.ResearchRegData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallback;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.FileUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.getAppVersion;
import static kr.co.bcoben.util.CommonUtil.getCameraImage;
import static kr.co.bcoben.util.CommonUtil.getGalleryImage;
import static kr.co.bcoben.util.CommonUtil.getImageResult;
import static kr.co.bcoben.util.CommonUtil.hideKeyboard;
import static kr.co.bcoben.util.CommonUtil.showToast;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener {

    public enum ResearchStep {
        GRADE("grade"), FACILITY("facility"), FACILITY_CATEGORY("fac_cate"), ARCHITECTURE("structure"), RESEARCH("research_type");

        private String value;
        ResearchStep(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
    public enum ProjectStep { SUMMARY, GRADE, FACILITY, RESEARCH, DRAWINGS }
    public enum DatePickerType { START, END }

    // side menu
    private MainResearchRegListAdapter mainResearchRegListAdapter;
    private MenuCheckListAdapter menuCheckListAdapter;
    private TreeViewAdapter treeViewAdapter;
    private MenuCheckFacilityListAdapter menuCheckFacilityListAdapter;
    private MenuCheckInputListAdapter menuCheckInputListAdapter;
    private MenuCheckResearchListAdapter menuCheckResearchListAdapter;
    private MenuDrawingsListAdapter menuDrawingsListAdapter;

    private DatePickerType datePickerType = DatePickerType.START;
    private int startYear, startMonth, startDay;

    // 조사등록
    private ResearchStep currentResearchStep = ResearchStep.GRADE;
    private ResearchRegData researchRegData;
    private List<MenuCheckData> regResearchDataList = new ArrayList<>();
    private List<ResearchRegData.FacilityData.FacCateData> newResearchFacCateList = new ArrayList<>();
    private List<ResearchRegData.FacilityData.FacCateData.ArchitectureData> newResearchArchList = new ArrayList<>();
    private Map<String, MenuCheckData> selectResearchRegData = new HashMap<>();
    private String selectedResearchName;
    private int selectedResearchCount;

    // 프로젝트 등록
    private ProjectStep currentProjectStep = ProjectStep.SUMMARY;
    private List<MenuCheckData> regProjectGrade = new ArrayList<>();
    private List<MenuSelectFacilityData> regProjectFacility = new ArrayList<>();
    private List<MenuCheckData> regProjectResearch = new ArrayList<>();
    private List<MenuDrawingsData> regDrawingsList = new ArrayList<>();
    private String checkedFacility = "";
    private int checkedFacilityId = 0;

    private List<CheckBox> cbList = new ArrayList<>();

    // main
    private ProjectListAdapter projectListAdapter;
    private ProjectDataPageAdapter projectPageAdapter;
    private List<ProjectListData.ProjectList> projectList = new ArrayList<>();
    private List<ProjectData> projectDataList = new ArrayList<>();
    private int currentProjectId;

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

        initDrawerResearch();
        initDrawerProject();

        // main
        projectListAdapter = new ProjectListAdapter(this, projectList);
        dataBinding.recyclerProject.setAdapter(projectListAdapter);
        dataBinding.recyclerProject.setLayoutManager(new LinearLayoutManager(this));

        projectPageAdapter = new ProjectDataPageAdapter(getSupportFragmentManager(), projectDataList);
        dataBinding.mainDrawer.mainContents.pagerProjectData.setAdapter(projectPageAdapter);
        dataBinding.mainDrawer.mainContents.pagerProjectData.setOffscreenPageLimit(5);

        dataBinding.txtVersion.setText(getAppVersion());
        dataBinding.mainDrawer.mainContents.tabProjectFacility.setupWithViewPager(dataBinding.mainDrawer.mainContents.pagerProjectData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserData.getInstance().getUserId() == 0){
            Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
            intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent_logout);
            finish();
        }
        requestProjectList();
        requestRegProjectCheckData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = getImageResult(this, requestCode, resultCode, data);
        if (uri != null) {
            regDrawingsList.add(new MenuDrawingsData(uri.getLastPathSegment(), checkedFacility, uri, checkedFacilityId));
            menuDrawingsListAdapter.setList(regDrawingsList);
            dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.txtDrawingsCount.setText("(" + menuDrawingsListAdapter.getItemCount() + "건)");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (dataBinding.mainDrawer.layoutDrawer.isDrawerOpen(GravityCompat.START)) {
            dataBinding.mainDrawer.layoutDrawer.closeDrawer(GravityCompat.START);
        } else {
            finishApp(this);
        }
    }

    public boolean isDrawingOpen() {
        return dataBinding.mainDrawer.layoutDrawer.isDrawerOpen(GravityCompat.START);
    }
    public void closeDrawer() {
        dataBinding.mainDrawer.layoutDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // side menu(research)

            // side menu(project)
            case R.id.btn_input_project_cancel:
                closeDrawer();
            case R.id.btn_input_project:
                String projectName = dataBinding.mainDrawer.layoutRegProject.txtSummaryName.getText().toString();
                String[] projectDateArr = (dataBinding.mainDrawer.layoutRegProject.txtSummaryDate.getText().toString()).split(" ~ ");
                String startDate = projectDateArr[0];
                String endDate = projectDateArr[1];
                // 진단등급 등록번호
                int gradeId = 0;
                for (MenuCheckData data : regProjectGrade) {
                    if (data.isChecked()) {
                        gradeId = data.getItem_id();
                    }
                }
                // 시설물 등록번호 리스트
                List<List<Integer>> facilityList = new ArrayList<>();
                for (MenuSelectFacilityData data : regProjectFacility) {
                    facilityList.add(data.getIdList());
                }
                // 조사종류 리스트
                final List<List<Integer>> researchList = new ArrayList<>();

                for (MenuCheckData data : menuCheckResearchListAdapter.getSelectedValue()) {
                    if (data.isChecked()) {
                        List<Integer> list = new ArrayList<>();
                        list.add(data.getItem_id());
                        list.add(data.getTot_count());
                        researchList.add(list);
                    }
                }
                // 도면 이미지 리스트
                List<Integer> idList = new ArrayList<>();
                for (MenuDrawingsData data1 : regDrawingsList) {
                    if (!idList.contains(data1.getFacilityId())) {
                        idList.add(data1.getFacilityId());
                    }
                }

                List<MultipartBody.Part> requestBodyList = new ArrayList<>();
                for (int id : idList) {

                    for (MenuDrawingsData data : regDrawingsList) {

                        if (data.getFacilityId() == id) {

                            try {
                                File file = FileUtil.from(this, data.getUri());

                                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("plan"+ id +"", file.getName(), requestBody);
                                requestBodyList.add(multipartBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                RequestBody projectNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), projectName);
                RequestBody startDateBody = RequestBody.create(MediaType.parse("multipart/form-data"), startDate);
                RequestBody endDateBody = RequestBody.create(MediaType.parse("multipart/form-data"), endDate);
                RequestBody facilityListBody = RequestBody.create(MediaType.parse("multipart/form-data"), facilityList.toString());
                RequestBody researchListBody = RequestBody.create(MediaType.parse("multipart/form-data"), researchList.toString());

                setIsLoading(true);
                RetrofitClient.getRetrofitApi().regProject(UserData.getInstance().getUserId(), projectNameBody, startDateBody, endDateBody, gradeId, facilityListBody, researchListBody, requestBodyList).enqueue(new RetrofitCallbackModel<ProjectListData>() {
                    @Override
                    public void onResponseData(ProjectListData data) {
                        closeDrawer();
                        List<ProjectListData.ProjectList> list = data.getProject_list();

                        if (list.isEmpty()) {
                            dataBinding.mainDrawer.mainContents.txtSubTitle.setVisibility(View.GONE);
                            dataBinding.mainDrawer.mainContents.layoutRegisterResearch.setVisibility(View.GONE);
                            dataBinding.mainDrawer.mainContents.pagerProjectData.setVisibility(View.GONE);
                            return;
                        }
                        dataBinding.mainDrawer.mainContents.txtSubTitle.setVisibility(View.VISIBLE);
                        dataBinding.mainDrawer.mainContents.layoutRegisterResearch.setVisibility(View.VISIBLE);
                        dataBinding.mainDrawer.mainContents.pagerProjectData.setVisibility(View.VISIBLE);

                        boolean existSelected = false;
                        for (ProjectListData.ProjectList projectListData : list) {
                            if (projectListData.isSelected()) {
                                existSelected = true;
                                dataBinding.mainDrawer.mainContents.txtSubTitle.setText("(" + projectListData.getGrade_name() + ")");
                            }
                        }
                        if (!existSelected) {
                            list.get(0).setSelected(true);
                            dataBinding.mainDrawer.mainContents.txtSubTitle.setText("(" + list.get(0).getGrade_name() + ")");
                        }
                        projectList = list;
                        projectListAdapter.setList(projectList);
                    }
                    @Override
                    public void onCallbackFinish() { setIsLoading(false); }
                });
                break;
            case R.id.btn_project_next:
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
                if (checkedFacility.equals("")) {
                    showToast(R.string.toast_input_select_facility);
                    return;
                }
                showCameraDialog();
                break;

            // main
            case R.id.btn_home:
                break;
            case R.id.btn_add_project:
                openDrawerProject();
                break;
            case R.id.txt_logout:
                RetrofitClient.getRetrofitApi().logout(UserData.getInstance().getUserId()).enqueue(new RetrofitCallback() {
                    @Override
                    public void onResponseData() {
                        UserData.getInstance().removeData();
                        Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
                        intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent_logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_logout);
                        finish();
                    }
                    @Override
                    public void onCallbackFinish() {}
                });
                break;
            case R.id.btn_update:
                break;
            case R.id.layout_register_research:
                openDrawerResearch(0);
                break;
        }
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

    // 조사등록
    public void initDrawerResearch() {
        mainResearchRegListAdapter = new MainResearchRegListAdapter(this, regResearchDataList);
        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.setAdapter(mainResearchRegListAdapter);
        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.setLayoutManager(new LinearLayoutManager(this));
    }

    // 조사등록 사이드메뉴 초기화
    private void resetDrawerResearch(String project, String grade, int facilityId) {
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
            currentResearchStep = ResearchStep.GRADE;
            setSelectResearchRegData(0);
            setResearchSelectedText(grade, 0);
        }
        if (facilityId != 0) {
            currentResearchStep = ResearchStep.FACILITY;
            setSelectResearchRegData(facilityId);
            String facility = getSelectResearchRegData(currentResearchStep.getValue()).getItem_name();
            setResearchSelectedText(facility, 0);
        }
        updateStepResearchMenuUI(currentResearchStep);
    }

    // 조사등록 사이드메뉴 열기
    public void openDrawerResearch(int facilityId) {
        String project = projectListAdapter.getSelectedProject();
        String grade = getTextGrade();

        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.VISIBLE);
        resetDrawerResearch(project, grade, facilityId);

        setNavigationViewWidth(false);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    // 조사등록 클릭 리스너
    public void onDrawerResearchMenuClick(View v) {
        switch (v.getId()) {
            case R.id.btn_input_research_home: closeDrawer(); break;
            case R.id.layout_grade: updateStepResearchMenuUI(ResearchStep.GRADE); break;
            case R.id.layout_facility: updateStepResearchMenuUI(ResearchStep.FACILITY); break;
            case R.id.layout_facility_category: updateStepResearchMenuUI(ResearchStep.FACILITY_CATEGORY); break;
            case R.id.layout_architecture: updateStepResearchMenuUI(ResearchStep.ARCHITECTURE); break;
            case R.id.layout_research: updateStepResearchMenuUI(ResearchStep.RESEARCH); break;
            case R.id.btn_input_research:
                String facilityName = dataBinding.mainDrawer.layoutRegResearch.txtFacility.getText().toString();
                String facCateName = dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.getText().toString();
                String structureName = dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.getText().toString();

                if (checkValidateResearchRegister(facilityName, facCateName, structureName, selectedResearchName)) {
                    setIsLoading(true);
                    RetrofitClient.getRetrofitApi()
                            .regResearch(UserData.getInstance().getUserId(), currentProjectId, facilityName, facCateName, structureName, selectedResearchName, selectedResearchCount)
                            .enqueue(new RetrofitCallbackModel<ResearchIdData>() {
                                @Override
                                public void onResponseData(ResearchIdData data) {
                                    goToDrawingsPage(data.getResearch_id());
                                }
                                @Override
                                public void onCallbackFinish() { setIsLoading(false); }
                            });
                }
                break;
        }
    }

    // 조사등록 유효성 검사
    private boolean checkValidateResearchRegister(String facilityName, String facCateName, String structureName, String researchName) {
        if (facilityName.equals("")) { showToast(R.string.toast_input_select_facility); return false; }
        if (facCateName.equals("")) { showToast(R.string.toast_input_select_facility_category); return false; }
        if (structureName.equals("")) { showToast(R.string.toast_input_select_architecture); return false; }
        if (researchName.equals("")) { showToast(R.string.toast_input_select_research); return false; }
        return true;
    }

    // 조사등록 UI변경
    private void updateStepResearchMenuUI(ResearchStep step) {
        currentResearchStep = step;

        dataBinding.mainDrawer.layoutRegResearch.layoutGrade.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setSelected(false);
        dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setSelected(false);

        switch (currentResearchStep) {
            case GRADE:
                dataBinding.mainDrawer.layoutRegResearch.layoutGrade.setSelected(true);
                regResearchDataList.clear();
                regResearchDataList.add(getSelectResearchRegData(ResearchStep.GRADE.getValue()));
                mainResearchRegListAdapter.setList(regResearchDataList, false);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setSelected(true);
                regResearchDataList = researchRegData.getFacilityList();
                mainResearchRegListAdapter.setList(regResearchDataList, false);
                break;
            case FACILITY_CATEGORY: {
                dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setSelected(true);
                MenuCheckData facilityData = getSelectResearchRegData(ResearchStep.FACILITY.getValue());
                regResearchDataList = researchRegData.getFacilityDataID(facilityData.getItem_id()).getFacCateList();
                mainResearchRegListAdapter.setList(regResearchDataList, false);
                break;
            }
            case ARCHITECTURE: {
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setSelected(true);
                MenuCheckData facilityData = getSelectResearchRegData(ResearchStep.FACILITY.getValue());
                MenuCheckData facCateData = getSelectResearchRegData(ResearchStep.FACILITY_CATEGORY.getValue());
                regResearchDataList = researchRegData.getFacilityDataID(facilityData.getItem_id()).getFacCateDataID(facCateData.getItem_id()).getArchitectureList();
                mainResearchRegListAdapter.setList(regResearchDataList, false);
                break;
            }
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setSelected(true);
                regResearchDataList = researchRegData.getResearchList();
                mainResearchRegListAdapter.setList(regResearchDataList, true);
                break;
        }
        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.scrollToPosition(0);
    }

    // 조사등록 - 선택한 데이터 출력
    public void setResearchSelectedText(String value, int count) {
        switch (currentResearchStep) {
            case GRADE:
                dataBinding.mainDrawer.layoutRegResearch.txtGrade.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutFacility.setClickable(true);
                updateStepResearchMenuUI(ResearchStep.FACILITY);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegResearch.txtFacility.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setClickable(true);
                updateStepResearchMenuUI(ResearchStep.FACILITY_CATEGORY);
                break;
            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(true);
                updateStepResearchMenuUI(ResearchStep.ARCHITECTURE);
                break;
            case ARCHITECTURE:
                dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(true);
                updateStepResearchMenuUI(ResearchStep.RESEARCH);
                break;
            case RESEARCH:
                selectedResearchName = value;
                selectedResearchCount = count;
                dataBinding.mainDrawer.layoutRegResearch.txtResearch.setText("(" + count + "개소) " + value);
                updateStepResearchMenuUI(ResearchStep.RESEARCH);
                break;
        }
    }

    // 조사등록 직접입력
    public void researchNextStep() {
        String name = mainResearchRegListAdapter.getEditName();
        switch (currentResearchStep) {
            case FACILITY: {
                if (name.isEmpty()) { showToast(R.string.toast_input_facility); return; }
                int index = researchRegData.getFacilityList().get(researchRegData.getFacilityList().size() - 1).getItem_id();
                ResearchRegData.FacilityData data = new ResearchRegData.FacilityData(index > 0 ? -1 : index - 1, name, newResearchFacCateList);
                researchRegData.addFacility(data);
                break;
            }
            case FACILITY_CATEGORY: {
                if (name.isEmpty()) { showToast(R.string.toast_input_facility_category); return; }
                ResearchRegData.FacilityData facilityData = researchRegData.getFacilityDataID(getSelectResearchRegData(ResearchStep.FACILITY.getValue()).getItem_id());
                int index = facilityData.getFacCateList().get(facilityData.getFacCateList().size() - 1).getItem_id();
                ResearchRegData.FacilityData.FacCateData data = new ResearchRegData.FacilityData.FacCateData(index > 0 ? -1 : index - 1, name, newResearchArchList);
                facilityData.addFacCate(data);
                break;
            }
            case ARCHITECTURE: {
                if (name.isEmpty()) { showToast(R.string.toast_input_architecture); return; }
                ResearchRegData.FacilityData.FacCateData facCateData = researchRegData .getFacilityDataID(getSelectResearchRegData(ResearchStep.FACILITY.getValue()).getItem_id()) .getFacCateDataID(getSelectResearchRegData(ResearchStep.FACILITY_CATEGORY.getValue()).getItem_id());
                int index = facCateData.getArchitectureList().get(facCateData.getArchitectureList().size() - 1).getItem_id();
                ResearchRegData.FacilityData.FacCateData.ArchitectureData data = new ResearchRegData.FacilityData.FacCateData.ArchitectureData(index > 0 ? -1 : index - 1, name);
                facCateData.addArchitecture(data);
                break;
            }
            case RESEARCH:
                String count = mainResearchRegListAdapter.getEditCount();
                if (count.isEmpty()) { showToast(R.string.toast_input_research_count); return; }
                if (name.isEmpty()) { showToast(R.string.toast_input_research); return; }
                researchRegData.addResearch(name, Integer.parseInt(count));
                break;
        }

        hideKeyboard(this);
        updateStepResearchMenuUI(currentResearchStep);
    }

    // 조사등록 - 선택한 시설물 데이터 저장
    public void setSelectResearchRegData(int itemId) {
        MenuCheckData data = null;
        switch (currentResearchStep) {
            case GRADE:
                data = new MenuCheckData(0, getTextGrade());
                break;
            case FACILITY:
                data = researchRegData.getFacilityDataID(itemId);
                dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.setText("");
                dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.setText("");
                break;
            case FACILITY_CATEGORY: {
                MenuCheckData facilityData = getSelectResearchRegData(ResearchStep.FACILITY.getValue());
                data = researchRegData.getFacilityDataID(facilityData.getItem_id()).getFacCateDataID(itemId);
                dataBinding.mainDrawer.layoutRegResearch.txtArchitecture.setText("");
                break;
            }
            case ARCHITECTURE: {
                MenuCheckData facilityData = getSelectResearchRegData(ResearchStep.FACILITY.getValue());
                MenuCheckData facCateData = getSelectResearchRegData(ResearchStep.FACILITY_CATEGORY.getValue());
                data = researchRegData.getFacilityDataID(facilityData.getItem_id()).getFacCateDataID(facCateData.getItem_id()).getArchitectureDataID(itemId);
                break;
            }
            case RESEARCH:
                data = researchRegData.getResearchDataID(itemId);
                break;
        }
        selectResearchRegData.put(currentResearchStep.getValue(), data);
    }
    // 조사등록 - 선택된 시설물 데이터 가져오기
    public MenuCheckData getSelectResearchRegData(String key) {
        return selectResearchRegData.get(key);
    }

    // 조사등록 - 아이템 클릭 처리
    public void requestResearchItemClick(int itemId) {
        setSelectResearchRegData(itemId);
        RetrofitClient.getRetrofitApi().itemClick(UserData.getInstance().getUserId(), currentProjectId, itemId).enqueue(new RetrofitCallback() {
            @Override
            public void onResponseData() {}
            @Override
            public void onCallbackFinish() {}
        });
    }

    // 조사등록 - 현재 스텝
    public ResearchStep getCurrentResearchStep() {
        return currentResearchStep;
    }


    // 프로젝트 등록
    public void initDrawerProject() {
        // 진단등급 리스트
        menuCheckListAdapter = new MenuCheckListAdapter(this, regProjectGrade);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheck.setAdapter(menuCheckListAdapter);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheck.setLayoutManager(new LinearLayoutManager(this));

        // 조사종류 리스트
        menuCheckInputListAdapter = new MenuCheckInputListAdapter(this, regProjectResearch);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckInput.setAdapter(menuCheckInputListAdapter);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckInput.setLayoutManager(new LinearLayoutManager(this));

        // 도면 등록 리스트
        menuDrawingsListAdapter = new MenuDrawingsListAdapter(this, regDrawingsList);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.recyclerDrawings.setAdapter(menuDrawingsListAdapter);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.recyclerDrawings.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        // 시설물 등록 리스트
        menuCheckFacilityListAdapter = new MenuCheckFacilityListAdapter(this, regProjectFacility);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckFacility.setAdapter(menuCheckFacilityListAdapter);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckFacility.setLayoutManager(new LinearLayoutManager(this));

        // 조사종류 등록 리스트
        menuCheckResearchListAdapter = new MenuCheckResearchListAdapter(this, regProjectResearch);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckResearch.setAdapter(menuCheckResearchListAdapter);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckResearch.setLayoutManager(new LinearLayoutManager(this));
    }
    // side menu(project)
    public void onDrawerProjectMenuClick(View v) {
        switch (v.getId()) {
            case R.id.layout_summary: updateStepProjectMenuUI(ProjectStep.SUMMARY); break;
            case R.id.layout_grade: updateStepProjectMenuUI(ProjectStep.GRADE); break;
            case R.id.layout_facility: updateStepProjectMenuUI(ProjectStep.FACILITY); break;
            case R.id.layout_research: updateStepProjectMenuUI(ProjectStep.RESEARCH); break;
            case R.id.layout_drawings: updateStepProjectMenuUI(ProjectStep.DRAWINGS); break;
        }
    }

    public void openDrawerProject() {
        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.VISIBLE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.GONE);
        resetDrawerProject();

        setNavigationViewWidth(true);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    private void resetDrawerProject() {
        updateStepProjectMenuUI(ProjectStep.SUMMARY);
        dataBinding.mainDrawer.layoutRegProject.layoutGrade.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutFacility.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutResearch.setClickable(false);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setClickable(false);

        dataBinding.mainDrawer.layoutRegProject.txtSummaryName.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtSummaryDate.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtGrade.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtFacility.setText("");
        dataBinding.mainDrawer.layoutRegProject.txtResearch.setText("");

        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectName.setText("");
        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.setText("");
        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.setText("");

        regProjectFacility.clear();
        regProjectResearch.clear();
        menuCheckFacilityListAdapter.setList(regProjectFacility);
        menuCheckResearchListAdapter.setList(regProjectResearch);
    }

    private void updateStepProjectMenuUI(ProjectStep step) {
        currentProjectStep = step;

        dataBinding.mainDrawer.layoutRegProject.layoutSummary.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutGrade.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutFacility.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutResearch.setSelected(false);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setSelected(false);

        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.layoutRoot.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheck.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.recyclerMenuTree.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckInput.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.layoutRoot.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.btnProjectNext.setVisibility(View.GONE);
        dataBinding.mainDrawer.layoutRegProject.txtInputGuide.setText(R.string.side_menu_please_select);

        switch (currentProjectStep) {
            case SUMMARY:
                dataBinding.mainDrawer.layoutRegProject.layoutSummary.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.layoutRoot.setVisibility(View.VISIBLE);
                dataBinding.mainDrawer.layoutRegProject.btnProjectNext.setVisibility(View.VISIBLE);
                dataBinding.mainDrawer.layoutRegProject.txtInputGuide.setText(R.string.side_menu_please_input);
                menuCheckFacilityListAdapter.setSelected(false);
                break;
            case GRADE:
                dataBinding.mainDrawer.layoutRegProject.layoutGrade.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerCheck.setVisibility(View.VISIBLE);
                menuCheckListAdapter.setList(regProjectGrade);
                menuCheckFacilityListAdapter.setSelected(false);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegProject.layoutFacility.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerMenuTree.setVisibility(View.VISIBLE);
                menuCheckFacilityListAdapter.setSelected(true);
                break;
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegProject.layoutResearch.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerCheckInput.setVisibility(View.VISIBLE);
                dataBinding.mainDrawer.layoutRegProject.txtInputGuide.setText(R.string.side_menu_please_select_input);
                menuCheckInputListAdapter.setList(regProjectResearch);
                menuCheckFacilityListAdapter.setSelected(false);
                break;
            case DRAWINGS:
                dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.flexLayoutFacility.removeAllViews();

                List<String> facilityList = new ArrayList<>();
                List<Integer> facilityIdList = new ArrayList<>();
                for (MenuSelectFacilityData data : regProjectFacility) {
                    facilityList.add(data.getFacility());
                    facilityIdList.add(data.getIdList().get(0));
                }
                facilityList = new ArrayList<>(new HashSet<>(facilityList));
                facilityIdList = new ArrayList<>(new HashSet<>(facilityIdList));

                for (int i=0;i < facilityList.size();i++) {
                    facilityCheckListItemAdd(facilityList.get(i), facilityIdList.get(i));
                }

                dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.layoutRoot.setVisibility(View.VISIBLE);
                menuCheckFacilityListAdapter.setSelected(false);
                break;
        }
//        dataBinding.mainDrawer.layoutRegResearch.recyclerContent.scrollToPosition(0);
    }



    public void projectNextStep() {
        List<String> projectSummaryValue = new ArrayList<>();
        List<MenuCheckData> selectedValue = new ArrayList<>();
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
                projectSummaryValue.add(name);
                projectSummaryValue.add(startDate);
                projectSummaryValue.add(endDate);
                break;
            case GRADE:
                regProjectGrade = menuCheckListAdapter.getSelectedValue();

                if (!checkIsSelectMenu(regProjectGrade)) {
                    showToast(R.string.toast_input_select_grade);
                    return;
                }

                selectedValue = regProjectGrade;
                break;
            case FACILITY:
                regProjectFacility = menuCheckFacilityListAdapter.getSelectedValue();

                if (regProjectFacility.isEmpty()) {
                    showToast(R.string.toast_input_select_facility);
                    return;
                }
                break;
            case RESEARCH:
                regProjectResearch = menuCheckInputListAdapter.getSelectedValue();

                if (!checkIsSelectMenu(regProjectResearch)) {
                    showToast(R.string.toast_input_select_research);
                    return;
                }

                for (MenuCheckData data : regProjectResearch) {
                    if (data.isChecked()) {
                        if (data.getTot_count() == 0) {
                            showToast(R.string.toast_input_select_research_count);
                            return;
                        }
                    }
                }

                selectedValue = regProjectResearch;
                break;
            case DRAWINGS:
                //TODO
//                for (MenuCheckData data : regProjectFacility) {
//                    facilityCheckListItemAdd(data);
//                }

                break;
        }

        hideKeyboard(this);
        setProjectSelectedText(selectedValue, projectSummaryValue);
    }

    private boolean checkIsSelectMenu(List<MenuCheckData> list) {
        boolean isChecked = false;

        for (MenuCheckData data : list) {
            if (data.isChecked()) {
                isChecked = true;
            }
        }

        return isChecked;
    }

    public void setProjectSelectedText(List<MenuCheckData> value, List<String> summary_value) {
        switch (currentProjectStep) {
            case SUMMARY:
                dataBinding.mainDrawer.layoutRegProject.txtSummaryName.setText(summary_value.get(0));
                dataBinding.mainDrawer.layoutRegProject.txtSummaryDate.setText(summary_value.get(1) + " ~ " + summary_value.get(2));
                dataBinding.mainDrawer.layoutRegProject.layoutGrade.setClickable(true);
                updateStepProjectMenuUI(ProjectStep.GRADE);
                break;
            case GRADE:
                String grade = "";

                for (MenuCheckData data : value) {
                    if (data.isChecked()) {
                        if (grade.equals("")) {
                            grade = data.getItem_name();
                        } else {
                            grade += ", " + data.getItem_name();
                        }
                    }
                }
                dataBinding.mainDrawer.layoutRegProject.txtGrade.setText(grade);
                dataBinding.mainDrawer.layoutRegProject.layoutFacility.setClickable(true);
                updateStepProjectMenuUI(ProjectStep.FACILITY);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegProject.layoutResearch.setClickable(true);
                updateStepProjectMenuUI(ProjectStep.RESEARCH);
                break;
            case RESEARCH:
                List<MenuCheckData> selectedList = new ArrayList<>();

                for (MenuCheckData data : value) {
                    if (data.isChecked()) {
                        selectedList.add(data);
                    }
                }

                menuCheckResearchListAdapter.setList(selectedList);

                dataBinding.mainDrawer.layoutRegProject.recyclerCheckResearch.setVisibility(View.VISIBLE);
                dataBinding.mainDrawer.layoutRegProject.txtResearch.setText("-");
                dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setClickable(true);
                updateStepProjectMenuUI(ProjectStep.DRAWINGS);
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

            String date = year + "-" + monthStr + "-" + dayStr;

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
                dialog.dismiss();
                getCameraImage(MainActivity.this);
            }
        });
        dialog.selectAlbumInputListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getGalleryImage(MainActivity.this);
            }
        });
        dialog.show();
    }

    // 도면 리스트 화면 이동
    public void goToDrawingsPage(int researchId) {
        Intent intent = new Intent(MainActivity.this, DrawingsListActivity.class);
        intent.putExtra("research_id", researchId);
        startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeDrawer();
            }
        }, 1000);
    }

    //프로젝트 리스트 조회 API 호출
    private void requestProjectList() {
        setIsLoading(true);
        RetrofitClient.getRetrofitApi().projectList(UserData.getInstance().getUserId()).enqueue(new RetrofitCallbackModel<ProjectListData>() {
            @Override
            public void onResponseData(ProjectListData data) {
                projectList = data.getProject_list();

                if (projectList.isEmpty()) {
                    dataBinding.mainDrawer.mainContents.layoutRegisterResearch.setVisibility(View.INVISIBLE);
                    dataBinding.mainDrawer.mainContents.layoutTabProjectFacility.setVisibility(View.GONE);
                    dataBinding.mainDrawer.mainContents.pagerProjectData.setVisibility(View.GONE);
                    setTextGrade("");
                } else {
                    dataBinding.mainDrawer.mainContents.layoutRegisterResearch.setVisibility(View.VISIBLE);
                    dataBinding.mainDrawer.mainContents.layoutTabProjectFacility.setVisibility(View.VISIBLE);
                    dataBinding.mainDrawer.mainContents.pagerProjectData.setVisibility(View.VISIBLE);

                    projectList.get(0).setSelected(true);
                    setTextGrade(projectList.get(0).getGrade_name());

                    projectListAdapter.setList(projectList);
                    setCurrentProjectId(projectList.get(0).getProject_id());
                    requestProjectDataList();
                }
            }
            @Override
            public void onCallbackFinish() { setIsLoading(false); }
        });
    }

    // 프로젝트 진단등급 Text
    public void setTextGrade(String grade) {
        dataBinding.mainDrawer.mainContents.txtSubTitle.setText("(" + grade + ")");
    }
    public String getTextGrade() {
        return dataBinding.mainDrawer.mainContents.txtSubTitle.getText().toString().replace("(", "").replace(")", "");
    }
    public void setCurrentProjectId(int projectId) {
        currentProjectId = projectId;
    }

    //프로젝트 조사 내용 및 조사등록용 데이터 API 호출
    public void requestProjectDataList() {
        setIsLoading(true);
        RetrofitClient.getRetrofitApi().projectData(UserData.getInstance().getUserId(), currentProjectId).enqueue(new RetrofitCallbackModel<ProjectMainData>() {
            @Override
            public void onResponseData(ProjectMainData data) {
                projectDataList = data.getFacility_list();
                researchRegData = data.getResearch_reg_data();
                projectPageAdapter.setProjectDataList(currentProjectId, projectDataList);
            }
            @Override
            public void onCallbackFinish() { setIsLoading(false); }
        });
    }

    public void setSelectedFacilityData(String facility, String facCate, String arch, List<Integer> list) {
        boolean isContain = false;
        for (MenuSelectFacilityData data : regProjectFacility) {
            List<Integer> idList = data.getIdList();
            boolean equal = true;
            for (int i = 0; i < idList.size(); i++) {
                if ((int) idList.get(i) != (int) list.get(i)) {
                    equal = false;
                    break;
                }
            }

            if (equal) {
                isContain = true;
                break;
            }
        }
        if (!isContain) {
            regProjectFacility.add(new MenuSelectFacilityData(facility, facCate, arch, list));
            menuCheckFacilityListAdapter.setList(regProjectFacility);
        }
    }

    //프로젝트 등록용 시설물 리스트 조회 API 호출
    private void requestRegProjectCheckData() {
       RetrofitClient.getRetrofitApi().projectRegDataList(UserData.getInstance().getUserId()).enqueue(new RetrofitCallbackModel<MenuCheckListData>() {
           @Override
           public void onResponseData(MenuCheckListData data) {
               regProjectGrade = data.getGrade_list();
               regProjectResearch = data.getResearch_list();

               newResearchFacCateList.clear();
               newResearchArchList.clear();
               for (MenuCheckData d : data.getStructure_list()) {
                   ResearchRegData.FacilityData.FacCateData.ArchitectureData archData = new ResearchRegData.FacilityData.FacCateData.ArchitectureData(d.getItem_id(), d.getItem_name());
                   newResearchArchList.add(archData);
               }
               for (MenuCheckData d : data.getFac_cate_list()) {
                   ResearchRegData.FacilityData.FacCateData cateData = new ResearchRegData.FacilityData.FacCateData(d.getItem_id(), d.getItem_name(), newResearchArchList);
                   newResearchFacCateList.add(cateData);
               }

               initMenuFacilityTreeData(data.getFacility_list(), data.getFac_cate_list(), data.getStructure_list());
           }
           @Override
           public void onCallbackFinish() {}
       });
    }

    private void initMenuFacilityTreeData(List<MenuCheckData> facilityList, List<MenuCheckData> facCateList, List<MenuCheckData> archList) {
        final List<TreeNode> nodes = new ArrayList<>();

        for (MenuCheckData facilityData : facilityList) {
            TreeNode<Dir> facility = new TreeNode<>(new Dir(facilityData.getItem_name(), facilityData.getItem_id()));
            nodes.add(facility);

            for (MenuCheckData facCateData : facCateList) {
                TreeNode<Dir> facCate = new TreeNode<>(new Dir(facCateData.getItem_name(), facCateData.getItem_id()));

                for (MenuCheckData archData : archList) {
                    facCate.addChild(new TreeNode<>(new Dir(archData.getItem_name(), archData.getItem_id())));
                }
                facility.addChild(facCate);
            }
        }

        // for next button
        nodes.add(new TreeNode<>(new Dir("", 0)));

        dataBinding.mainDrawer.layoutRegProject.recyclerMenuTree.setLayoutManager(new LinearLayoutManager(this));
        treeViewAdapter = new TreeViewAdapter(nodes, Arrays.asList(new MenuNodeBinder(this)));

        treeViewAdapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode treeNode, RecyclerView.ViewHolder viewHolder) {
                if (!treeNode.isLeaf()) {
                    onToggle(!treeNode.isExpand(), viewHolder);
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder viewHolder) {
                MenuNodeBinder.ViewHolder menuViewHolder = (MenuNodeBinder.ViewHolder) viewHolder;
                TextView txtName = menuViewHolder.getTxtName();
                txtName.setTypeface(isExpand ? txtName.getTypeface() : null, isExpand ? Typeface.BOLD : Typeface.NORMAL);
                TextView txtHandle = menuViewHolder.getTxtHandle();
                txtHandle.setText(isExpand ? R.string.minus : R.string.plus);
            }
        });

        dataBinding.mainDrawer.layoutRegProject.recyclerMenuTree.setAdapter(treeViewAdapter);
    }

    private void facilityCheckListItemAdd(String item, int id) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_menu_check_facility, null);

        final TextView facilityName = view.findViewById(R.id.txt_facility_name);
        final CheckBox cbFacility = view.findViewById(R.id.cb_facility);

        cbList.add(cbFacility);

        facilityName.setText(item);
        facilityName.setTag(id);
        cbFacility.setChecked(false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox cb : cbList) {
                    cb.setChecked(cb == cbFacility);
                }
                checkedFacility = facilityName.getText().toString();
                checkedFacilityId = (int) facilityName.getTag();
            }
        });
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.flexLayoutFacility.addView(view);
    }
}
