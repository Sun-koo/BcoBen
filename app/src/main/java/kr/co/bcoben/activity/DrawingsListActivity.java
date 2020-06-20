package kr.co.bcoben.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;
import kr.co.bcoben.ftp.ConnectFTP;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.SharedPrefUtil;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener {

    static boolean isHomeReturn = false;
    private List<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

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

        dataBinding.spnCategory.setEnabled(false);
        dataBinding.spnArchitecture.setEnabled(false);
//        initSpinner(dataBinding.spnCategory, listCategory, category);
//        initSpinner(dataBinding.spnArchitecture, listArchitecture, architecture);
//        initSpinner(dataBinding.spnResearch, listResearch, research);

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
            return;
        }
        requestDrawingsList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectFTP.getInstance().ftpDisconnect();
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
        //TODO get research_id
        int researchId = getIntent().getIntExtra("research_id", 0);
        if (researchId == 0) {
            showToast(R.string.toast_error_invalidate);
            finish();
        }
        RetrofitClient.getRetrofitApi().planList(UserData.getInstance().getUserId(), researchId).enqueue(new RetrofitCallbackModel<PlanDataList>() {
            @Override
            public void onResponseData(PlanDataList data) {
                data.setPlanImgFile();
                planList = data.getPlan_list();
                setDrawingsList();
            }
        });
    }

    private void setDrawingsList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectFTP.getInstance().ftpPlanThumbBitmap(planList);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawingsListAdapter.setList(planList);
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
        boolean isComplete = ConnectFTP.getInstance().ftpPlanDownload(position, data, drawingsListAdapter);
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

    public void sendSpinnerData() {
//        Intent intent = new Intent(DrawingsListActivity.this, DrawingsActivity.class);
//        intent.putStringArrayListExtra("category_list", (ArrayList<String>) listCategory);
//        intent.putStringArrayListExtra("architecture_list", (ArrayList<String>) listArchitecture);
//        intent.putStringArrayListExtra("research_list", (ArrayList<String>) listResearch);
//        intent.putStringArrayListExtra("facility_list", (ArrayList<String>) listFacility);
//        intent.putExtra("category", category);
//        intent.putExtra("architecture", architecture);
//        intent.putExtra("research", research);
//        intent.putExtra("facility", facility);
//        startActivity(intent);
    }
}
