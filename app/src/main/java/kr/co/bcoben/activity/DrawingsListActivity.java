package kr.co.bcoben.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;
import kr.co.bcoben.model.MenuCheckData;
import kr.co.bcoben.model.ResearchRegData;
import kr.co.bcoben.model.ResearchSpinnerData;
import kr.co.bcoben.util.FTPConnectUtil;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.SharedPrefUtil;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener {

    static boolean isHomeReturn = false;
    private ResearchSpinnerData.ResearchSelectData researchSelectData;
    private List<MenuCheckData> newFacCateList = new ArrayList<>();
    private List<MenuCheckData> newArchList = new ArrayList<>();
    private List<MenuCheckData> newResearchList = new ArrayList<>();
    private List<ResearchRegData.FacilityData.FacCateData> facCateDataList = new ArrayList<>();
    private List<ResearchRegData.ResearchData> researchDataList = new ArrayList<>();
    private int researchId = -1;
    private int selectedResearchId = 0;
    private int selectedFacCateId = 0;
    private int selectedArchitectureId = 0;

    private DrawingsListAdapter drawingsListAdapter;
    private List<PlanDataList.PlanData> planList = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings_list;
    }

    @Override
    protected void initView() {
//        listCategory = getIntent().getStringArrayListExtra("category_list");
//        listArchitecture = getIntent().getStringArrayListExtra("architecture_list");
//        listResearch = getIntent().getStringArrayListExtra("research_list");
//        listFacility = getIntent().getStringArrayListExtra("facility_list");
//        category = getIntent().getStringExtra("category");
//        architecture = getIntent().getStringExtra("architecture");
//        research = getIntent().getStringExtra("research");
//        facility = getIntent().getStringExtra("facility");

//        dataBinding.spnCategory.setEnabled(false);
//        dataBinding.spnArchitecture.setEnabled(false);

        drawingsListAdapter = new DrawingsListAdapter(DrawingsListActivity.this, planList);
        dataBinding.recyclerDrawings.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        dataBinding.recyclerDrawings.setAdapter(drawingsListAdapter);

        requestSpinnerData();
        requestDrawingsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isHomeReturn) {
            isHomeReturn = false;
            finish();
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FTPConnectUtil.getInstance().ftpDisconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_download_all:
                downloadFile(-1);
                break;
        }
    }

    private void initResearchSpinner() {
        List<String> titleList = new ArrayList<>();
        for (MenuCheckData data : newResearchList) {
            titleList.add(data.getItem_name());
        }

        dataBinding.spnResearch.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, titleList));

        for (int i = 0; i < newResearchList.size(); i++) {
            if (newResearchList.get(i).isChecked()) {
                dataBinding.spnResearch.setSelection(i);
                selectedResearchId = newResearchList.get(i).getItem_id();
            }
        }
        dataBinding.spnResearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (MenuCheckData data : newResearchList) {
                    data.setChecked(false);
                }
                newResearchList.get(position).setChecked(true);
                selectedResearchId = newResearchList.get(position).getItem_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initFacCateSpinner() {
        List<String> titleList = new ArrayList<>();
        for (MenuCheckData data : newFacCateList) {
            titleList.add(data.getItem_name());
        }

        dataBinding.spnCategory.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, titleList));
        for (int i = 0; i < newFacCateList.size(); i++) {
            if (newFacCateList.get(i).isChecked()) {
                dataBinding.spnCategory.setSelection(i);
                selectedFacCateId = newFacCateList.get(i).getItem_id();
            }
        }
        dataBinding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (MenuCheckData data : newFacCateList) {
                    data.setChecked(false);
                }
                newFacCateList.get(position).setChecked(true);
                selectedFacCateId = newFacCateList.get(position).getItem_id();
                setArchitectureData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void initArchitectureSpinner() {
        List<String> titleList = new ArrayList<>();
        for (MenuCheckData data : newArchList) {
            titleList.add(data.getItem_name());
        }

        dataBinding.spnArchitecture.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, titleList));
        for (int i = 0; i < newArchList.size(); i++) {
            if (newArchList.get(i).isChecked()) {
                dataBinding.spnArchitecture.setSelection(i);
                selectedArchitectureId = newArchList.get(i).getItem_id();
            }
        }
        dataBinding.spnArchitecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (MenuCheckData data : newArchList) {
                    data.setChecked(false);
                }
                newArchList.get(position).setChecked(true);
                selectedArchitectureId = newArchList.get(position).getItem_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setArchitectureData() {
        newArchList.clear();
        int position = 0;
        for (int i = 0; i < newFacCateList.size(); i++) {
            if (newFacCateList.get(i).isChecked()) {
                position = i;
            }
        }
        for (MenuCheckData architectureData : facCateDataList.get(position).getArchitectureList()) {
            if (architectureData.getItem_id() == researchSelectData.getStructure_id()) {
                MenuCheckData menuCheckData = new MenuCheckData(architectureData.getItem_id(), architectureData.getItem_name());
                menuCheckData.setChecked(true);
                newArchList.add(menuCheckData);
            } else {
                newArchList.add(new MenuCheckData(architectureData.getItem_id(), architectureData.getItem_name()));
            }
        }
        initArchitectureSpinner();
    }

    private void requestDrawingsList() {
        if (researchId == -1) {
            researchId = getIntent().getIntExtra("research_id", 0);
            if (researchId == 0) {
                showToast(R.string.toast_error_invalidate);
                finish();
            }
        }
        setIsLoading(true);
        RetrofitClient.getRetrofitApi().planList(UserData.getInstance().getUserId(), researchId).enqueue(new RetrofitCallbackModel<PlanDataList>() {
            @Override
            public void onResponseData(PlanDataList data) {
                data.setPlanImgFile();
                if (!planList.isEmpty()) {
                    for (int i = 0; i < data.getPlan_list().size(); i++) {
                        PlanDataList.PlanData listData = data.getPlan_list().get(i);
                        for (PlanDataList.PlanData planData : planList) {
                            if (listData.equals(planData)) {
                                data.getPlan_list().set(i, planData);
                                break;
                            }
                        }
                    }
                }
                planList = data.getPlan_list();
                setDrawingsList();
            }
            @Override
            public void onCallbackFinish() { setIsLoading(false); }
        });
    }

    private void requestSpinnerData() {
        if (researchId == -1) {
            researchId = getIntent().getIntExtra("research_id", 0);
            if (researchId == 0) {
                showToast(R.string.toast_error_invalidate);
                finish();
            }
        }
        setIsLoading(true);
        RetrofitClient.getRetrofitApi().researchData(UserData.getInstance().getUserId(), researchId).enqueue(new RetrofitCallbackModel<ResearchSpinnerData>() {
            @Override
            public void onResponseData(ResearchSpinnerData data) {
                researchSelectData = data.getResearch_data();
                researchDataList = data.getProject_fac_data().getResearch_list();
                facCateDataList = data.getProject_fac_data().getFac_cate_list();

                for (ResearchRegData.ResearchData researchData : researchDataList) {
                    if (researchData.getItem_id() == researchSelectData.getResearch_type_id()) {
                        MenuCheckData menuCheckData = new MenuCheckData(researchData.getItem_id(), researchData.getItem_name());
                        menuCheckData.setChecked(true);
                        newResearchList.add(menuCheckData);
                    } else {
                        newResearchList.add(new MenuCheckData(researchData.getItem_id(), researchData.getItem_name()));
                    }
                }
                initResearchSpinner();

                newFacCateList.clear();
                for (ResearchRegData.FacilityData.FacCateData facData : facCateDataList) {
                    if (facData.getItem_id() == researchSelectData.getFac_cate_id()) {
                        MenuCheckData menuCheckData = new MenuCheckData(facData.getItem_id(), facData.getItem_name());
                        menuCheckData.setChecked(true);
                        newFacCateList.add(menuCheckData);
                    } else {
                        newFacCateList.add(new MenuCheckData(facData.getItem_id(), facData.getItem_name()));
                    }
                }
                initFacCateSpinner();
            }

            @Override
            public void onCallbackFinish() {
                setIsLoading(false);
            }
        });
    }

    private void setDrawingsList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> pathList = new ArrayList<>();
                for (PlanDataList.PlanData data : planList) {
                    pathList.add(data.getPlan_thumb());
                }
                List<Bitmap> bitmapList = FTPConnectUtil.getInstance().ftpImageBitmap(pathList);
                for (int i = 0; i < bitmapList.size(); i++) {
                    planList.get(i).setPlan_bitmap(bitmapList.get(i));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawingsListAdapter.setList(planList);
                        setIsLoading(false);
                    }
                });
            }
        }).start();
        drawingsListAdapter.setList(planList);
    }

    public void downloadFile(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                if (position == -1 && !planList.isEmpty()) {
                    for (int i = 0; i < planList.size(); i++) {
                        PlanDataList.PlanData data = planList.get(i);
                        if (data.getDownPercent() == 0) {
                            planDownload(i, data);
                            count++;
                        }
                    }
                } else if (position > -1 && position < planList.size()) {
                    planDownload(position, planList.get(position));
                    count++;
                }

                final int finalCount = count;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalCount == 0) {
                            showToast(R.string.toast_download_all_empty);
                        } else {
                            showToast(R.string.toast_download_complete);
                        }
                    }
                });
            }
        }).start();
    }

    private void planDownload(int position, PlanDataList.PlanData data) {
        boolean isComplete = FTPConnectUtil.getInstance().ftpPlanDownload(position, data, drawingsListAdapter);
        if (isComplete) {
            SharedPrefUtil.putBoolean(data.getPlan_img_file(), true);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawingsListAdapter.setData(position, data, DrawingsListAdapter.COMPLETE_DOWNLOAD);
        }
    }

    public void sendSpinnerData(int index) {
        Intent intent = new Intent(DrawingsListActivity.this, DrawingsActivity.class);
        intent.putParcelableArrayListExtra("plan_list", (ArrayList<? extends Parcelable>) planList);
        intent.putExtra("plan_index", index);
        intent.putExtra("research_id", researchId);
        intent.putExtra("project_id", researchSelectData.getProject_id());
        intent.putExtra("facility_id", researchSelectData.getFacility_id());
        intent.putExtra("fac_cate_id", selectedFacCateId);
        intent.putExtra("architecture_id", selectedArchitectureId);
        intent.putExtra("research_type_id", selectedResearchId);
        intent.putExtra("fac_cate_list", (Serializable) facCateDataList);
        intent.putExtra("research_list", (Serializable) researchDataList);
        startActivity(intent);
    }
}
