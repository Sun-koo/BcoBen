package kr.co.bcoben.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.adapter.DrawingsSpinnerAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;
import kr.co.bcoben.model.MenuCheckData;
import kr.co.bcoben.model.ResearchCheckData;
import kr.co.bcoben.model.ResearchSpinnerData;
import kr.co.bcoben.service.retrofit.RetrofitCallback;
import kr.co.bcoben.util.FTPConnectUtil;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.SharedPrefUtil;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener {

    public static boolean isHomeReturn = false;
    private final int DRAWING_ACTIVITY_REQUEST = 1001;

    private ResearchSpinnerData.ResearchSelectData researchSelectData;
    private ResearchSpinnerData.ProjectFacData projectFacData;
    private int researchId = -1;
    private boolean isFirst = true;

    private DrawingsListAdapter drawingsListAdapter;
    private List<PlanDataList.PlanData> planList = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings_list;
    }

    @Override
    protected void initView() {
        if (researchId == -1) {
            researchId = getIntent().getIntExtra("research_id", 0);
            if (researchId == 0) {
                showToast(R.string.toast_error_invalidate);
                finish();
            }
        }

        drawingsListAdapter = new DrawingsListAdapter(DrawingsListActivity.this, planList);
        dataBinding.recyclerDrawings.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        dataBinding.recyclerDrawings.setAdapter(drawingsListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isHomeReturn) {
            isHomeReturn = false;
            finish();
        }

        drawingsListAdapter.notifyDataSetChanged();
        if (isFirst) {
            requestSpinnerData();
            isFirst = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DRAWING_ACTIVITY_REQUEST) {
                if (data != null) {
                    int facCateId = data.getIntExtra("fac_cate_id", 0);
                    int structureId = data.getIntExtra("structure_id", 0);
                    int researchTypeId = data.getIntExtra("research_type_id", 0);

                    researchSelectData.setFac_cate_id(facCateId);
                    researchSelectData.setStructure_id(structureId);
                    researchSelectData.setResearch_type_id(researchTypeId);

                    initSpinner();
                }
            }
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

    // 상단 스피너 데이터
    private void requestSpinnerData() {
        startLoading();
        RetrofitClient.getRetrofitApi().researchData(UserData.getInstance().getUserId(), researchId).enqueue(new RetrofitCallbackModel<ResearchSpinnerData>() {
            @Override
            public void onResponseData(ResearchSpinnerData data) {
                researchSelectData = data.getResearch_data();
                projectFacData = data.getProject_fac_data();

                initSpinner();

                requestDrawingsList();
            }
            @Override
            public void onCallbackFinish() {
                endLoading();
            }
        });
    }

    // 스피너 초기화
    private void initSpinner() {
        List<MenuCheckData> facCateList = projectFacData.getFacCateList();
        List<MenuCheckData> architectureList = projectFacData.getFacCateDataID(researchSelectData.getFac_cate_id()).getArchitectureList();
        List<MenuCheckData> researchList = projectFacData.getResearchList();

        dataBinding.spnCategory.setAdapter(new DrawingsSpinnerAdapter(this, R.layout.item_spinner, facCateList));
        dataBinding.spnArchitecture.setAdapter(new DrawingsSpinnerAdapter(this, R.layout.item_spinner, architectureList));
        dataBinding.spnResearch.setAdapter(new DrawingsSpinnerAdapter(this, R.layout.item_spinner, researchList));

        dataBinding.spnCategory.setSelection(getSpinnerSelectIndex(facCateList, researchSelectData.getFac_cate_id()));
        dataBinding.spnArchitecture.setSelection(getSpinnerSelectIndex(architectureList, researchSelectData.getStructure_id()));
        dataBinding.spnResearch.setSelection(getSpinnerSelectIndex(researchList, researchSelectData.getResearch_type_id()));

        dataBinding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MenuCheckData data = (MenuCheckData) dataBinding.spnCategory.getSelectedItem();
                List<MenuCheckData> architectureList = projectFacData.getFacCateDataID(data.getItem_id()).getArchitectureList();

                dataBinding.spnArchitecture.setAdapter(new DrawingsSpinnerAdapter(activity, R.layout.item_spinner, architectureList));
                dataBinding.spnArchitecture.setSelection(getSpinnerSelectIndex(architectureList, researchSelectData.getStructure_id()));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // 스피너 Item ID에 맞는 index값 가져오기
    private int getSpinnerSelectIndex(List<MenuCheckData> list, int itemId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getItem_id() == itemId) { return i; }
        }
        return 0;
    }

    // 도면 리스트 데이터
    private void requestDrawingsList() {
        startLoading();
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
            public void onCallbackFinish() { endLoading(); }
        });
    }

    // 도면 리스트 Bitmap 가져오기
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
                        endLoading();
                    }
                });
            }
        }).start();
        drawingsListAdapter.setList(planList);
    }

    // 도면 다운로드 요청
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

    // 도면 다운로드 처리
    private void planDownload(int position, PlanDataList.PlanData data) {
        boolean isComplete = FTPConnectUtil.getInstance().ftpPlanDownload(position, data, drawingsListAdapter);
        if (isComplete) {
            SharedPrefUtil.putBoolean(data.getPlan_img_file(), true);
            RetrofitClient.getRetrofitApi().downloadPlan(UserData.getInstance().getUserId(), data.getPlan_id()).enqueue(new RetrofitCallback() {
                @Override
                public void onResponseData() {}
                @Override
                public void onCallbackFinish() {}
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            drawingsListAdapter.setData(position, data, DrawingsListAdapter.COMPLETE_DOWNLOAD);
        }
    }

    // 스피너 데이터 체크
    public void requestCheckData(final int planIndex) {
        int researchId = researchSelectData.getResearch_id();
        int projectId = researchSelectData.getProject_id();
        int facilityId = researchSelectData.getFacility_id();
        int facCateId = ((MenuCheckData) dataBinding.spnCategory.getSelectedItem()).getItem_id();
        int structureId = ((MenuCheckData) dataBinding.spnArchitecture.getSelectedItem()).getItem_id();
        int researchTypeId = ((MenuCheckData) dataBinding.spnResearch.getSelectedItem()).getItem_id();

        startLoading();
        RetrofitClient.getRetrofitApi().researchCheckData(UserData.getInstance().getUserId(), researchId, projectId, facilityId, facCateId, structureId, researchTypeId).enqueue(new RetrofitCallbackModel<ResearchCheckData>() {
            @Override
            public void onResponseData(ResearchCheckData data) {
                researchSelectData = data.getResearch_data();
                startDrawingsActivity(planIndex);
                endLoadingDelay();
            }
            @Override
            public void onCallbackFinish() { endLoading(); }
        });
    }

    // 도면 상세화면 이동
    public void startDrawingsActivity(int planIndex) {
        Intent intent = new Intent(DrawingsListActivity.this, DrawingsActivity.class);
        intent.putParcelableArrayListExtra("plan_list", (ArrayList<? extends Parcelable>) planList);
        intent.putExtra("plan_index", planIndex);
        intent.putExtra("research_data", researchSelectData);
        intent.putExtra("project_fac_data", projectFacData);
        startActivityForResult(intent, DRAWING_ACTIVITY_REQUEST);
    }
}
