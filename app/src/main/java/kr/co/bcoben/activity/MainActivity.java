package kr.co.bcoben.activity;

import android.Manifest;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
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
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;
import kr.co.bcoben.adapter.Dir;
import kr.co.bcoben.adapter.MainResearchRegListAdapter;
import kr.co.bcoben.adapter.MenuCheckFacilityListAdapter;
import kr.co.bcoben.adapter.MenuCheckInputListAdapter;
import kr.co.bcoben.adapter.MenuCheckListAdapter;
import kr.co.bcoben.adapter.MenuCheckResearchListAdapter;
import kr.co.bcoben.adapter.MenuDrawingsListAdapter;
import kr.co.bcoben.adapter.MenuNodeBinder;
import kr.co.bcoben.adapter.ProjectDataPageAdapter;
import kr.co.bcoben.adapter.ProjectListAdapter;
import kr.co.bcoben.component.AppUpdateDialog;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CameraDialog;
import kr.co.bcoben.component.PermissionDialog;
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
import kr.co.bcoben.service.app.UnCatchTaskService;
import kr.co.bcoben.service.retrofit.RetrofitCallback;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.CommonUtil;
import kr.co.bcoben.util.FileUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.getAppVersion;
import static kr.co.bcoben.util.CommonUtil.getCameraImage;
import static kr.co.bcoben.util.CommonUtil.getDateFormat;
import static kr.co.bcoben.util.CommonUtil.getGalleryImage;
import static kr.co.bcoben.util.CommonUtil.getImageResult;
import static kr.co.bcoben.util.CommonUtil.hideKeyboard;
import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;
import static kr.co.bcoben.util.CommonUtil.showToast;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener {

    private final String[] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

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

    // 조사등록
    private ResearchStep currentResearchStep = ResearchStep.GRADE;
    private MainResearchRegListAdapter mainResearchRegListAdapter;
    private ResearchRegData researchRegData;
    private List<MenuCheckData> regResearchDataList = new ArrayList<>();
    private List<ResearchRegData.FacilityData.FacCateData> newResearchFacCateList = new ArrayList<>();
    private List<ResearchRegData.FacilityData.FacCateData.ArchitectureData> newResearchArchList = new ArrayList<>();
    private Map<String, MenuCheckData> selectResearchRegData = new HashMap<>();
    private String selectedResearchName;
    private int selectedResearchCount;

    // 프로젝트 등록
    private ProjectStep currentProjectStep = ProjectStep.SUMMARY;
    private MenuCheckListAdapter menuCheckListAdapter;
    private TreeViewAdapter treeViewAdapter;
    private List<MenuCheckData> projectFacilityList, projectFacCateList, projectArchitectureList = new ArrayList<>();   // For Update Tree Data
    private MenuCheckFacilityListAdapter menuCheckFacilityListAdapter;
    private MenuCheckInputListAdapter menuCheckInputListAdapter;
    private MenuCheckResearchListAdapter menuCheckResearchListAdapter;
    private MenuDrawingsListAdapter menuDrawingsListAdapter;
    private List<MenuCheckData> regProjectGrade = new ArrayList<>();
    private List<MenuSelectFacilityData> regProjectFacility = new ArrayList<>();
    private List<MenuCheckData> regProjectResearch = new ArrayList<>();
    private List<MenuDrawingsData> regDrawingsList = new ArrayList<>();
    private List<CheckBox> cbList = new ArrayList<>();
    private String checkedFacility = "";
    private List<String> checkboxFacilityList = new ArrayList<>();
    public boolean isImageIntent = false;

    // main
    private ProjectListAdapter projectListAdapter;
    private ProjectDataPageAdapter projectPageAdapter;
    private List<ProjectListData.ProjectList> projectList = new ArrayList<>();
    private List<ProjectData> projectDataList = new ArrayList<>();
    private int currentProjectId;

    private Timer sessionCheckTimer;
    private final int SESSION_TIMER = 9 * 60 * 1000;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        startService(new Intent(this, UnCatchTaskService.class));

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
        projectPageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                dataBinding.mainDrawer.mainContents.tabProjectFacility.selectTab(dataBinding.mainDrawer.mainContents.tabProjectFacility.getTabAt(0));
            }
        });
        dataBinding.mainDrawer.mainContents.pagerProjectData.setAdapter(projectPageAdapter);
        dataBinding.mainDrawer.mainContents.pagerProjectData.setOffscreenPageLimit(5);

        dataBinding.txtVersion.setText(getAppVersion());
        dataBinding.btnUpdate.setVisibility(AppApplication.getInstance().getUpdateData() != null && AppApplication.getInstance().getUpdateData().isUpdate() ? View.VISIBLE : View.GONE);
        dataBinding.mainDrawer.mainContents.tabProjectFacility.setupWithViewPager(dataBinding.mainDrawer.mainContents.pagerProjectData);

        requestRegProjectCheckData();

        sessionCheckTimer = new Timer();
        sessionCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                RetrofitClient.getRetrofitApi().sessionCheck(UserData.getInstance().getUserId()).enqueue(new RetrofitCallback() {
                    @Override
                    public void onResponseData() {}
                    @Override
                    public void onCallbackFinish() {}
                });
            }
        }, SESSION_TIMER, SESSION_TIMER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserData.getInstance().getUserId() == 0){
            finish();
            return;
        }
        if (!isImageIntent) {
            requestProjectList();
        }
        isImageIntent = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (UserData.getInstance().getUserId() != 0) {
            RetrofitClient.getRetrofitApi().logout(UserData.getInstance().getUserId()).enqueue(new RetrofitCallback() {
                @Override
                public void onResponseData() {
                    UserData.getInstance().removeData();
                }
                @Override
                public void onCallbackFinish() {}
            });
        }
        sessionCheckTimer.cancel();
        sessionCheckTimer.purge();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = getImageResult(this, requestCode, resultCode, data);
        if (uri != null) {
            String filename = "";
            try {
                filename = FileUtil.from(this, uri).getName();
            } catch (IOException e) {
                filename = uri.getLastPathSegment();
            }
            regDrawingsList.add(new MenuDrawingsData(filename, checkedFacility, uri));
            menuDrawingsListAdapter.setList(regDrawingsList);
            setTextDrawingsCount();
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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final CommonUtil.PermissionState state = resultRequestPermission(this, permissions, grantResults);

        if (state == CommonUtil.PermissionState.GRANT) {
            showCameraDialog();
        } else {
            PermissionDialog.builder(this)
                    .setTxtPermissionContent(R.string.dialog_permission_content_plan)
                    .setBtnConfirmListener(new PermissionDialog.BtnClickListener() {
                        @Override
                        public void onClick(PermissionDialog dialog) {
                            if (state == CommonUtil.PermissionState.ALWAYS_DENY) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivityForResult(intent, requestCode);
                            } else if (state == CommonUtil.PermissionState.DENY) {
                                requestPermission(activity, PERMISSION);
                            }
                            dialog.dismiss();
                        }
                    }).show();
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
            case R.id.btn_input_project: {
                String projectName = dataBinding.mainDrawer.layoutRegProject.txtSummaryName.getText().toString();
                String projectDate = dataBinding.mainDrawer.layoutRegProject.txtSummaryDate.getText().toString();

                if (projectName.isEmpty()) {
                    showToast(R.string.toast_input_project_name);
                    return;
                }
                String[] projectDateArr = projectDate.split(" ~ ");
                String startDate = projectDateArr[0];
                String endDate = projectDateArr[1];
                // 진단등급
                String gradeName = "";
                for (MenuCheckData data : regProjectGrade) {
                    if (data.isChecked()) {
                        gradeName = data.getItem_name();
                    }
                }
                if (gradeName.isEmpty()) {
                    showToast(R.string.toast_input_select_grade);
                    return;
                }
                // 시설물 등록번호 리스트
//                List<List<Integer>> facilityList = new ArrayList<>();
                List<List<String>> facilityList = new ArrayList<>();
                for (MenuSelectFacilityData data : regProjectFacility) {
                    List<String> list = new ArrayList<>();

                    for (String name : data.getNameList()) {
                        list.add("\"" + name + "\"");
                    }
                    facilityList.add(list);
                }
                if (facilityList.isEmpty()) {
                    showToast(R.string.toast_input_select_facility);
                    return;
                }
                // 조사종류 리스트
//                final List<List<Integer>> researchList = new ArrayList<>();
                final List<List<String>> researchList = new ArrayList<>();

                for (MenuCheckData data : menuCheckResearchListAdapter.getSelectedValue()) {
                    if (data.isChecked()) {
                        List<String> list = new ArrayList<>();
                        list.add("\"" + data.getItem_name() + "\"");
                        list.add("\"" + data.getTot_count() + "\"");
                        researchList.add(list);
                    }
                }
                if (researchList.isEmpty()) {
                    showToast(R.string.toast_input_select_research);
                    return;
                }
                // 도면 이미지 리스트
                List<String> facilityNameList = new ArrayList<>();
                for (MenuDrawingsData data1 : regDrawingsList) {
                    if (!facilityNameList.contains(data1.getFacility())) {
                        facilityNameList.add(data1.getFacility());
                    }
                }
                if (facilityNameList.isEmpty()) {
                    showToast(R.string.toast_input_select_drawings);
                    return;
                }
                for (String facility : checkboxFacilityList) {
                    if (!facilityNameList.contains(facility)) {
                        showToast(facility + " 도면을 등록해주세요");
                        return;
                    }
                }

                List<MultipartBody.Part> requestBodyList = new ArrayList<>();
                for (String facility : facilityNameList) {

                    for (MenuDrawingsData data : regDrawingsList) {

                        if (data.getFacility().equals(facility)) {

                            try {
                                File file = FileUtil.from(this, data.getUri());

                                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                MultipartBody.Part multipartBody = MultipartBody.Part.createFormData(URLEncoder.encode("plan" + facility, "utf-8"), URLEncoder.encode(file.getName(), "utf-8"), requestBody);
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
                RequestBody gradeNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), gradeName);
                RequestBody facilityListBody = RequestBody.create(MediaType.parse("multipart/form-data"), facilityList.toString());
                RequestBody researchListBody = RequestBody.create(MediaType.parse("multipart/form-data"), researchList.toString());

                startLoading();
                RetrofitClient.getRetrofitApi().regProject(UserData.getInstance().getUserId(), projectNameBody, startDateBody, endDateBody, gradeNameBody, facilityListBody, researchListBody, requestBodyList).enqueue(new RetrofitCallbackModel<ProjectListData>() {
                    @Override
                    public void onResponseData(ProjectListData data) {
                        closeDrawer();
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

                            for (ProjectListData.ProjectList projectListData : projectList) {
                                if (projectListData.getProject_id() == currentProjectId) {
                                    projectListData.setSelected(true);
                                    setTextGrade(projectListData.getGrade_name());
                                }
                            }
                            projectListAdapter.setList(projectList);
                        }
                        endLoading();
                    }
                    @Override
                    public void onCallbackFinish() { endLoading(); }
                });
                break;
            }

            // main
            case R.id.btn_home:
                break;
            case R.id.btn_add_project:
                openDrawerProject();
                break;
            case R.id.txt_logout:
                Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentLogout);
                finish();
                break;
            case R.id.btn_update:
                AppUpdateDialog.builder(this).show();
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
        selectedResearchName = "";
        selectedResearchCount = 0;

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
                    startLoading();
                    RetrofitClient.getRetrofitApi()
                            .regResearch(UserData.getInstance().getUserId(), currentProjectId, facilityName, facCateName, structureName, selectedResearchName, selectedResearchCount)
                            .enqueue(new RetrofitCallbackModel<ResearchIdData>() {
                                @Override
                                public void onResponseData(ResearchIdData data) {
                                    goToDrawingsPage(data.getResearch_id());
                                }
                                @Override
                                public void onCallbackFinish() { endLoading(); }
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
                dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setClickable(false);
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(false);
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(false);
                updateStepResearchMenuUI(ResearchStep.FACILITY);
                break;
            case FACILITY:
                dataBinding.mainDrawer.layoutRegResearch.txtFacility.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutFacilityCategory.setClickable(true);
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(false);
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(false);
                updateStepResearchMenuUI(ResearchStep.FACILITY_CATEGORY);
                break;
            case FACILITY_CATEGORY:
                dataBinding.mainDrawer.layoutRegResearch.txtFacilityCategory.setText(value);
                dataBinding.mainDrawer.layoutRegResearch.layoutArchitecture.setClickable(true);
                dataBinding.mainDrawer.layoutRegResearch.layoutResearch.setClickable(false);
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
        String name = mainResearchRegListAdapter.getEditName().trim().toUpperCase();
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
                String count = mainResearchRegListAdapter.getEditCount().trim();
                if (count.isEmpty() || count.equals("0")) { showToast(R.string.toast_input_research_count); return; }
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

    /**
     * 프로젝트 Drawer
     */
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
        menuCheckResearchListAdapter = new MenuCheckResearchListAdapter(this, new ArrayList<MenuCheckData>());
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckResearch.setAdapter(menuCheckResearchListAdapter);
        dataBinding.mainDrawer.layoutRegProject.recyclerCheckResearch.setLayoutManager(new LinearLayoutManager(this));

        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectName.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    // 프로젝트 등록 초기화
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

        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.txtDrawingsCount.setText("");

        resetCheckedMenu(regProjectGrade);
        resetCheckedMenu(regProjectResearch);
        regProjectFacility.clear();
        regDrawingsList.clear();
        checkedFacility = "";

        menuCheckFacilityListAdapter.setList(regProjectFacility);
        menuCheckResearchListAdapter.setList(new ArrayList<MenuCheckData>());
        menuDrawingsListAdapter.setList(regDrawingsList);
    }
    private void resetCheckedMenu(List<MenuCheckData> list) {
        for (MenuCheckData data : list) {
            data.setChecked(false);
            data.setTot_count(0);
            if (data.getItem_id() == 0) {
                list.remove(data);
            }
        }
    }

    // 프로젝트 등록 클릭 리스너
    public void onDrawerProjectMenuClick(View v) {
        switch (v.getId()) {
            case R.id.btn_input_project_cancel: closeDrawer(); break;
            case R.id.layout_summary: updateStepProjectMenuUI(ProjectStep.SUMMARY); break;
            case R.id.layout_grade: updateStepProjectMenuUI(ProjectStep.GRADE); break;
            case R.id.layout_facility: updateStepProjectMenuUI(ProjectStep.FACILITY); break;
            case R.id.layout_research: updateStepProjectMenuUI(ProjectStep.RESEARCH); break;
            case R.id.layout_drawings: updateStepProjectMenuUI(ProjectStep.DRAWINGS); break;
            case R.id.btn_project_next: projectNextStep(); break;
            case R.id.layout_project_start_date: {
                hideKeyboard(this);

                int year, month, day;
                String startDate = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.getText().toString();
                if (startDate.equals("")) {
                    Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    String[] startDateArr = startDate.split("/");
                    year = Integer.parseInt(startDateArr[0]);
                    month = Integer.parseInt(startDateArr[1]) - 1;
                    day = Integer.parseInt(startDateArr[2]);
                }

                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat df = new DecimalFormat("00");
                        String startDate = year + "/" + df.format(month + 1) + "/" + df.format(dayOfMonth);
                        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.setText(startDate);

                        String endDate = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.getText().toString();
                        if (!endDate.equals("")) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                                Date start = sdf.parse(startDate);
                                Date end = sdf.parse(endDate);

                                int compare = start.compareTo(end);
                                if (compare > 0) {
                                    dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.setText(startDate);
                                }
                            } catch (ParseException ignored) {}
                        }
                    }
                }, year, month, day).show();
                break;
            }
            case R.id.layout_project_end_date: {
                hideKeyboard(this);

                int year, month, day;
                String endDate = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.getText().toString();
                String startDate = dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectStartDate.getText().toString();
                if (endDate.equals("")) {
                    if (startDate.equals("")) {
                        Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH);
                        day = c.get(Calendar.DAY_OF_MONTH);
                    } else {
                        String[] startDateArr = startDate.split("/");
                        year = Integer.parseInt(startDateArr[0]);
                        month = Integer.parseInt(startDateArr[1]) - 1;
                        day = Integer.parseInt(startDateArr[2]);
                    }
                } else {
                    String[] endDateArr = endDate.split("/");
                    year = Integer.parseInt(endDateArr[0]);
                    month = Integer.parseInt(endDateArr[1]) - 1;
                    day = Integer.parseInt(endDateArr[2]);
                }

                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DecimalFormat df = new DecimalFormat("00");
                        String date = year + "/" + df.format(month + 1) + "/" + df.format(dayOfMonth);
                        dataBinding.mainDrawer.layoutRegProject.layoutSummaryInput.editProjectEndDate.setText(date);
                    }
                }, year, month, day);
                if (!startDate.equals("")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                        Date start = sdf.parse(startDate);
                        dialog.getDatePicker().setMinDate(start.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                dialog.show();
                break;
            }
            case R.id.btn_drawings_upload: {
                if (checkedFacility.equals("")) {
                    showToast(R.string.toast_input_select_facility);
                    return;
                }
                if (requestPermission(this, PERMISSION)) {
                    showCameraDialog();
                }
                break;
            }
        }
    }

    // 프로젝트 등록 사이드메뉴 열기
    public void openDrawerProject() {
        dataBinding.mainDrawer.layoutRegProject.layoutRoot.setVisibility(View.VISIBLE);
        dataBinding.mainDrawer.layoutRegResearch.layoutRoot.setVisibility(View.GONE);
        resetDrawerProject();

        setNavigationViewWidth(true);
        dataBinding.mainDrawer.layoutDrawer.openDrawer(GravityCompat.START);
    }

    // 프로젝트 등록 UI변경
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
                treeViewAdapter.collapseAll();
                dataBinding.mainDrawer.layoutRegProject.recyclerMenuTree.scrollToPosition(0);
                break;
            case RESEARCH:
                dataBinding.mainDrawer.layoutRegProject.layoutResearch.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.recyclerCheckInput.setVisibility(View.VISIBLE);
                dataBinding.mainDrawer.layoutRegProject.txtInputGuide.setText(R.string.side_menu_please_select_input);
                menuCheckInputListAdapter.setList(regProjectResearch);
                menuCheckFacilityListAdapter.setSelected(false);
                dataBinding.mainDrawer.layoutRegProject.scrollRegProject.fullScroll(ScrollView.FOCUS_DOWN);
                dataBinding.mainDrawer.layoutRegProject.recyclerCheckInput.scrollToPosition(0);
                break;
            case DRAWINGS:
                dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.flexLayoutFacility.removeAllViews();

                List<String> facilityList = new ArrayList<>();
                for (MenuSelectFacilityData data : regProjectFacility) {
                    facilityList.add(data.getFacility());
                }
                facilityList = new ArrayList<>(new HashSet<>(facilityList));

                checkboxFacilityList.clear();
                checkedFacility = "";
                for (int i=0;i < facilityList.size();i++) {
                    facilityCheckListItemAdd(facilityList.get(i));
                }

                dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setSelected(true);
                dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.layoutRoot.setVisibility(View.VISIBLE);
                menuCheckFacilityListAdapter.setSelected(false);
                dataBinding.mainDrawer.layoutRegProject.scrollRegProject.fullScroll(ScrollView.FOCUS_DOWN);
                dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.recyclerDrawings.scrollToPosition(0);
                break;
        }
    }

    // 프로젝트 등록 다음 버튼 처리
    public void projectNextStep() {
        hideKeyboard(this);

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
                selectedValue = menuCheckInputListAdapter.getSelectedValue();

                if (!checkIsSelectMenu(selectedValue)) {
                    showToast(R.string.toast_input_select_research);
                    return;
                }

                for (MenuCheckData data : selectedValue) {
                    if (data.isChecked()) {
                        if (data.getTot_count() == 0) {
                            showToast(R.string.toast_input_select_research_count);
                            return;
                        }
                    }
                }
                break;
        }

        setProjectSelectedText(selectedValue, projectSummaryValue);
    }

    private boolean checkIsSelectMenu(List<MenuCheckData> list) {
        boolean isChecked = false;
        for (MenuCheckData data : list) {
            if (data.isChecked()) { isChecked = true; }
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

                dataBinding.mainDrawer.layoutRegProject.txtResearch.setText("-");
                dataBinding.mainDrawer.layoutRegProject.layoutDrawings.setClickable(true);
                updateStepProjectMenuUI(ProjectStep.DRAWINGS);
                break;
        }
    }

    // 카메라 촬영 / 앨범 선택 다이얼로그 출력
    private void showCameraDialog() {
        CameraDialog.builder(this)
                .setBtnCameraListener(new CameraDialog.BtnClickListener() {
                    @Override
                    public void onClick(CameraDialog dialog) {
                        dialog.dismiss();
                        isImageIntent = true;
                        String filename = "plan_" + getDateFormat("yyMMddHHmmss") + ".jpg";
                        getCameraImage(MainActivity.this, filename);
                    }
                })
                .setBtnGalleyListener(new CameraDialog.BtnClickListener() {
                    @Override
                    public void onClick(CameraDialog dialog) {
                        dialog.dismiss();
                        isImageIntent = true;
                        getGalleryImage(MainActivity.this);
                    }
                }).show();
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
        startLoading();
        RetrofitClient.getRetrofitApi().projectList(UserData.getInstance().getUserId()).enqueue(new RetrofitCallbackModel<ProjectListData>() {
            @Override
            public void onResponseData(ProjectListData data) {
                int projectId = 0;
                if (!projectList.isEmpty()) {
                    for (ProjectListData.ProjectList p : projectList) {
                        if (p.isSelected()) {
                            projectId = p.getProject_id();
                            break;
                        }
                    }
                }
                projectList = data.getProject_list();

                if (projectList.isEmpty()) {
                    dataBinding.mainDrawer.mainContents.layoutRegisterResearch.setVisibility(View.INVISIBLE);
                    dataBinding.mainDrawer.mainContents.layoutTabProjectFacility.setVisibility(View.GONE);
                    dataBinding.mainDrawer.mainContents.pagerProjectData.setVisibility(View.GONE);
                    setTextGrade("");
                    endLoading();
                } else {
                    dataBinding.mainDrawer.mainContents.layoutRegisterResearch.setVisibility(View.VISIBLE);
                    dataBinding.mainDrawer.mainContents.layoutTabProjectFacility.setVisibility(View.VISIBLE);
                    dataBinding.mainDrawer.mainContents.pagerProjectData.setVisibility(View.VISIBLE);

                    int index = -1;
                    if (projectId != 0) {
                        for (int i = 0; i < projectList.size(); i++) {
                            if (projectList.get(i).getProject_id() == projectId) {
                                index = i;
                                break;
                            }
                        }
                    }

                    projectList.get(index == -1 ? 0 : index).setSelected(true);
                    setTextGrade(projectList.get(index == -1 ? 0 : index).getGrade_name());

                    projectListAdapter.setList(projectList);
                    setCurrentProjectId(projectList.get(index == -1 ? 0 : index).getProject_id());

                    requestProjectDataList();
                }
            }
            @Override
            public void onCallbackFinish() { endLoading(); }
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
        startLoading();
        RetrofitClient.getRetrofitApi().projectData(UserData.getInstance().getUserId(), currentProjectId).enqueue(new RetrofitCallbackModel<ProjectMainData>() {
            @Override
            public void onResponseData(ProjectMainData data) {
                projectDataList = data.getFacility_list();
                researchRegData = data.getResearch_reg_data();
                projectPageAdapter.setProjectDataList(currentProjectId, projectDataList);
            }
            @Override
            public void onCallbackFinish() { endLoading(); }
        });
    }

    public void setSelectedFacilityData(String facility, String facCate, String arch, List<String> list) {
        boolean isContain = false;
        for (MenuSelectFacilityData data : regProjectFacility) {
            List<String> nameList = data.getNameList();
            boolean equal = true;
            for (int i = 0; i < nameList.size(); i++) {
                if (!nameList.get(i).equals(list.get(i))) {
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
            dataBinding.mainDrawer.layoutRegProject.recyclerCheckFacility.scrollToPosition(regProjectFacility.size() - 1);
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
               projectFacilityList = data.getFacility_list();
               projectFacCateList = data.getFac_cate_list();
               projectArchitectureList = data.getStructure_list();
               initMenuFacilityTreeData();
           }
           @Override
           public void onCallbackFinish() {}
       });
    }

    private void initMenuFacilityTreeData() {
        checkedFacility = "";

        final List<TreeNode> nodes = new ArrayList<>();

        for (MenuCheckData facilityData : projectFacilityList) {
            TreeNode<Dir> facility = new TreeNode<>(new Dir(facilityData.getItem_name(), facilityData.getItem_id()));
            nodes.add(facility);

            for (MenuCheckData facCateData : projectFacCateList) {
                TreeNode<Dir> facCate = new TreeNode<>(new Dir(facCateData.getItem_name(), facCateData.getItem_id()));

                for (MenuCheckData archData : projectArchitectureList) {
                    facCate.addChild(new TreeNode<>(new Dir(archData.getItem_name(), archData.getItem_id())));
                    if (projectArchitectureList.get(projectArchitectureList.size() - 1) == archData) {
                        // 구조형식 직접입력
                        facCate.addChild(new TreeNode<>(new Dir("", -3)));
                    }
                }
                facility.addChild(facCate);
                if (projectFacCateList.get(projectFacCateList.size() - 1) == facCateData) {
                    // 시설물 분류 직접입력
                    facility.addChild(new TreeNode<>(new Dir("", -2)));
                }
            }
        }
        // 시설물 직접입력
        nodes.add(new TreeNode<>(new Dir("", -1)));
        // for next button
        nodes.add(new TreeNode<>(new Dir("", -10)));

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

    public boolean updateFacilityTreeData(String name, int id) {
        if (id == -1) {
            for (MenuCheckData data : projectFacilityList) {
                if (name.equals(data.getItem_name())) {
                    showToast(R.string.toast_input_duplicate_facility);
                    return false;
                }
            }
            projectFacilityList.add(new MenuCheckData(0, name));
        } else if (id == -2){
            for (MenuCheckData data : projectFacCateList) {
                if (name.equals(data.getItem_name())) {
                    showToast(R.string.toast_input_duplicate_fac_cate);
                    return false;
                }
            }
            projectFacCateList.add(new MenuCheckData(0, name));
        } else {
            for (MenuCheckData data : projectArchitectureList) {
                if (name.equals(data.getItem_name())) {
                    showToast(R.string.toast_input_duplicate_architecture);
                    return false;
                }
            }
            projectArchitectureList.add(new MenuCheckData(0, name));
        }
        initMenuFacilityTreeData();
        return true;
    }

    private void facilityCheckListItemAdd(String item) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_menu_check_facility, null);

        final TextView facilityName = view.findViewById(R.id.txt_facility_name);
        final CheckBox cbFacility = view.findViewById(R.id.cb_facility);

        cbList.add(cbFacility);

        facilityName.setText(item);
        cbFacility.setChecked(false);

        checkboxFacilityList.add(item);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox cb : cbList) {
                    cb.setChecked(cb == cbFacility);
                }
                checkedFacility = facilityName.getText().toString();
            }
        });
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.flexLayoutFacility.addView(view);
    }
    // 프로젝트 생성 > 도면 > 등록 도면 이미지 개수
    public void setTextDrawingsCount() {
        dataBinding.mainDrawer.layoutRegProject.layoutDrawingsInput.txtDrawingsCount.setText("(" + menuDrawingsListAdapter.getItemCount() + "건)");
    }
}
