package kr.co.bcoben.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingInputSpinnerAdapter;
import kr.co.bcoben.adapter.DrawingPictureListAdapter;
import kr.co.bcoben.adapter.DrawingsPlanSpinnerAdapter;
import kr.co.bcoben.adapter.DrawingsSpinnerAdapter;
import kr.co.bcoben.adapter.InputPopupMemoListAdapter;
import kr.co.bcoben.adapter.InputPopupPictureListAdapter;
import kr.co.bcoben.adapter.InputPopupRecordListAdapter;
import kr.co.bcoben.adapter.DrawingsDataListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CanvasView;
import kr.co.bcoben.component.DrawingsSelectDialog;
import kr.co.bcoben.component.PermissionDialog;
import kr.co.bcoben.databinding.ActivityDrawingsBinding;
import kr.co.bcoben.model.MenuCheckData;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.PointData;
import kr.co.bcoben.model.PointInputData;
import kr.co.bcoben.model.PointListData;
import kr.co.bcoben.model.PointTableData;
import kr.co.bcoben.model.ResearchCheckData;
import kr.co.bcoben.model.ResearchSpinnerData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.CommonUtil;
import kr.co.bcoben.util.CommonUtil.PermissionState;
import kr.co.bcoben.util.FTPConnectUtil;
import kr.co.bcoben.util.FileUtil;
import kr.co.bcoben.util.SharedPrefUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static kr.co.bcoben.model.DrawingPointData.DrawingType;
import static kr.co.bcoben.util.CommonUtil.dpToPx;
import static kr.co.bcoben.util.CommonUtil.getCachePath;
import static kr.co.bcoben.util.CommonUtil.getDateFormat;
import static kr.co.bcoben.util.CommonUtil.getFileChooserImage;
import static kr.co.bcoben.util.CommonUtil.getImageResult;
import static kr.co.bcoben.util.CommonUtil.hideKeyboard;
import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;
import static kr.co.bcoben.util.CommonUtil.showKeyboard;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.RecordUtil.isPlaying;
import static kr.co.bcoben.util.RecordUtil.isRecording;
import static kr.co.bcoben.util.RecordUtil.startRecord;
import static kr.co.bcoben.util.RecordUtil.stopAudio;
import static kr.co.bcoben.util.RecordUtil.stopRecord;

public class DrawingsActivity extends BaseActivity<ActivityDrawingsBinding> implements View.OnClickListener {

    private final String[] RECORD_PERMISSION = {Manifest.permission.RECORD_AUDIO};
    private final String[] IMAGE_PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private final int RECORD_PERMISSION_CODE = 301;
    private final int IMAGE_PERMISSION_CODE = 302;

    // drawing
    private int planId;
    private int planIndex;
    private String planFile;
    private float initScale;
    private int currentScale = 2;
    private List<PointData> pointList = new ArrayList<>();
    private PointF regPoint;
    private int pointNum;
    private PointInputData pointRegDataList;
    private DrawingPictureListAdapter drawingPictureListAdapter;

    // spinner
    private ResearchSpinnerData.ResearchSelectData researchSelectData;
    private ResearchSpinnerData.ProjectFacData projectFacData;
    private List<PlanDataList.PlanData> planList;
    private boolean isFirst = true;
    private boolean isShowingDrawingsSelectDialog = false;

    // table
    private DrawingsDataListAdapter drawingsDataListAdapter;
    private List<PointTableData> tableDataList = new ArrayList<>();

    // popup
    private InputPopupPictureListAdapter inputPopupPictureListAdapter;
    private InputPopupRecordListAdapter inputPopupRecordListAdapter;
    private InputPopupMemoListAdapter inputPopupMemoListAdapter;
    private PointData updateData;
    private int voiceNum;
    private String recordName;
    private int recordTime;
    private Timer recordTimer;
    private File recordFile;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings;
    }

    @Override
    protected void initView() {
        startLoading();

        researchSelectData = (ResearchSpinnerData.ResearchSelectData) getIntent().getSerializableExtra("research_data");
        projectFacData = (ResearchSpinnerData.ProjectFacData) getIntent().getSerializableExtra("project_fac_data");
        planList = getIntent().getParcelableArrayListExtra("plan_list");
        planIndex = getIntent().getIntExtra("plan_index", 0);

        planId = planList.get(planIndex).getPlan_id();
        planFile = planList.get(planIndex).getPlan_img_file();

        initSpinner();
        initDrawing();

        // 집계표
        dataBinding.checkboxNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                drawingsDataListAdapter.setAllChecked(isChecked);
            }
        });

        dataBinding.recyclerTable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        drawingsDataListAdapter = new DrawingsDataListAdapter(this, tableDataList);
        dataBinding.recyclerTable.setAdapter(drawingsDataListAdapter);

        requestPointRegisterData();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final PermissionState state = resultRequestPermission(this, permissions, grantResults);

        if (state == PermissionState.GRANT) {
            if (requestCode == RECORD_PERMISSION_CODE) {
                recording();
            } else if (requestCode == IMAGE_PERMISSION_CODE) {
                getFileChooserImage(this, "img_" + getDateFormat("yyMMddHHmmss") + ".jpg");
            }
        } else {
            int contentStrId;
            if (requestCode == RECORD_PERMISSION_CODE) {
                contentStrId = R.string.dialog_permission_content_image;
            } else if (requestCode == IMAGE_PERMISSION_CODE) {
                contentStrId = R.string.dialog_permission_content_record;
            } else {
                contentStrId = R.string.dialog_permission_content;
            }

            PermissionDialog.builder(this)
                    .setTxtPermissionContent(contentStrId)
                    .setBtnConfirmListener(new PermissionDialog.BtnClickListener() {
                        @Override
                        public void onClick(PermissionDialog dialog) {
                            if (state == CommonUtil.PermissionState.ALWAYS_DENY) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivityForResult(intent, requestCode);
                            } else if (state == CommonUtil.PermissionState.DENY) {
                                requestPermission(activity, permissions, requestCode);
                            }
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (dataBinding.layoutResearchPopup.getVisibility() == View.VISIBLE) {
            dataBinding.researchPopup.btnPopupClose.performClick();
        } else if (dataBinding.layoutPicturePopup.getVisibility() == View.VISIBLE) {
            dataBinding.layoutPicturePopup.performClick();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyboard(this);
        switch (v.getId()) {
            case R.id.btn_home: DrawingsListActivity.isHomeReturn = true;
            case R.id.btn_close:
                Intent intent = new Intent();
                intent.putExtra("fac_cate_id", researchSelectData.getFac_cate_id());
                intent.putExtra("structure_id", researchSelectData.getStructure_id());
                intent.putExtra("research_type_id", researchSelectData.getResearch_type_id());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.layout_picture_popup: dataBinding.layoutPicturePopup.setVisibility(View.INVISIBLE); break;
            case R.id.btn_zoom_in: calculateScale(true); break;
            case R.id.btn_zoom_out: calculateScale(false); break;
            case R.id.btn_table_handle: {
                boolean isSelected = dataBinding.btnTableHandle.isSelected();
                dataBinding.btnTableHandle.setSelected(!isSelected);
                dataBinding.layoutTable.setVisibility(isSelected ? View.VISIBLE : View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (currentScale == 2) {
                            dataBinding.imgDrawings.animateScale(0.1f).withDuration(10).start();
                        }
                    }
                }, 10);
                break;
            }
            // table
            case R.id.btn_select: {
                boolean isSelected = dataBinding.btnSelect.getText().toString().equals(getString(R.string.select));

                dataBinding.btnSelect.setText(isSelected ? R.string.cancel : R.string.select);
                dataBinding.txtNumber.setVisibility(isSelected ? View.GONE : View.VISIBLE);
                dataBinding.checkboxNumber.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                dataBinding.btnDelete.setVisibility(isSelected ? View.VISIBLE : View.GONE);
                dataBinding.checkboxNumber.setChecked(false);

                drawingsDataListAdapter.setCheckable(isSelected);
                break;
            }
            case R.id.btn_delete: {
                List<Integer> deletePointList = drawingsDataListAdapter.getCheckedList();
                if (deletePointList.size() == 0) {
                    showToast(R.string.toast_table_delete_non_select);
                    return;
                }
                requestDeletePoint(deletePointList);
                break;
            }
            // popup
            case R.id.btn_reg:
                if (dataBinding.researchPopup.txtMemoTab.isSelected() && dataBinding.researchPopup.memoView.txtGuide.getVisibility() == View.GONE) {
                    Bitmap bitmap = dataBinding.researchPopup.memoView.layoutCanvas.getBitmap();
                    inputPopupMemoListAdapter.addBitmap(bitmap);

                    dataBinding.researchPopup.memoView.layoutCanvas.clear();
                    dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.VISIBLE);
                    setMemoCount();
                } else {
                    if (isPlaying()) {
                        showToast(R.string.toast_popup_reg_research_record_playing_register);
                        return;
                    }
                    if (isRecording()) {
                        showToast(R.string.toast_popup_reg_research_record_register);
                        return;
                    }
                    if (dataBinding.researchPopup.recordView.layoutRecording.getVisibility() == View.VISIBLE) {
                        showToast(R.string.toast_popup_reg_research_record_register_save);
                        return;
                    }
                    inputPopupRecordListAdapter.setRecording(false);
                    requestRegisterPoint();
                }
                break;
            case R.id.btn_save:
                if (dataBinding.researchPopup.txtMemoTab.isSelected() && dataBinding.researchPopup.memoView.txtGuide.getVisibility() == View.GONE) {
                    Bitmap bitmap = dataBinding.researchPopup.memoView.layoutCanvas.getBitmap();
                    inputPopupMemoListAdapter.addBitmap(bitmap);

                    dataBinding.researchPopup.memoView.layoutCanvas.clear();
                    dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.VISIBLE);
                    setMemoCount();
                } else {
                    if (isPlaying()) {
                        showToast(R.string.toast_popup_reg_research_record_playing_register);
                        return;
                    }
                    if (isRecording()) {
                        showToast(R.string.toast_popup_reg_research_record_register);
                        return;
                    }
                    if (dataBinding.researchPopup.recordView.layoutRecording.getVisibility() == View.VISIBLE) {
                        showToast(R.string.toast_popup_reg_research_record_register_save);
                        return;
                    }
                    inputPopupRecordListAdapter.setRecording(false);
                    requestUpdatePoint();
                }
                break;
            case R.id.btn_del: {
                if (isRecording()) {
                    showToast(R.string.toast_popup_reg_research_record_register);
                    return;
                }

                List<Integer> deletePointList = new ArrayList<>();
                deletePointList.add(updateData.getPoint_id());
                requestDeletePoint(deletePointList);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Uri photoUri = getImageResult(this, requestCode, resultCode, data);
        if (photoUri != null) {
            inputPopupPictureListAdapter.addImage(photoUri);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecord();
        stopAudio();
    }

    // 스피너 초기화
    private void initSpinner() {
        List<MenuCheckData> facCateList = projectFacData.getFacCateList();
        List<MenuCheckData> architectureList = projectFacData.getFacCateDataID(researchSelectData.getFac_cate_id()).getArchitectureList();
        List<MenuCheckData> researchList = projectFacData.getResearchList();

        dataBinding.spnCategory.setAdapter(new DrawingsSpinnerAdapter(this, R.layout.item_spinner, facCateList));
        dataBinding.spnArchitecture.setAdapter(new DrawingsSpinnerAdapter(this, R.layout.item_spinner, architectureList));
        dataBinding.spnResearch.setAdapter(new DrawingsSpinnerAdapter(this, R.layout.item_spinner, researchList));
        dataBinding.spnPlan.setAdapter(new DrawingsPlanSpinnerAdapter(this, R.layout.item_spinner, planList));

        dataBinding.spnCategory.setSelection(getSpinnerSelectIndex(facCateList, researchSelectData.getFac_cate_id()));
        dataBinding.spnArchitecture.setSelection(getSpinnerSelectIndex(architectureList, researchSelectData.getStructure_id()));
        dataBinding.spnResearch.setSelection(getSpinnerSelectIndex(researchList, researchSelectData.getResearch_type_id()));
        dataBinding.spnPlan.setSelection(planIndex);

        setFacilitySpinnerListener(dataBinding.spnCategory);
        setFacilitySpinnerListener(dataBinding.spnArchitecture);
        setFacilitySpinnerListener(dataBinding.spnResearch);
        dataBinding.spnPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != planIndex) {
                    planIndex = position;
                    planId = planList.get(position).getPlan_id();
                    planFile = planList.get(position).getPlan_img_file();

                    pointList = new ArrayList<>();
                    dataBinding.imgDrawings.setPinList(pointList);
                    if (SharedPrefUtil.getBoolean(planList.get(position).getPlan_img(), false)) {
                        initDrawing();
                    } else {
                        planDownload(position, planList.get(position));
                    }
                }
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

    // 스피너 리스너 설정
    private void setFacilitySpinnerListener(final AppCompatSpinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MenuCheckData data = (MenuCheckData) spinner.getSelectedItem();
                int itemId = -1;
                if (spinner == dataBinding.spnCategory) { itemId = researchSelectData.getFac_cate_id(); }
                else if (spinner == dataBinding.spnArchitecture) { itemId = researchSelectData.getStructure_id(); }
                else if (spinner == dataBinding.spnResearch) { itemId = researchSelectData.getResearch_type_id(); }

                if (data.getItem_id() != itemId) {
                    showDrawingsSelectDialog();
                    if (spinner == dataBinding.spnCategory) {
                        List<MenuCheckData> architectureList = projectFacData.getFacCateDataID(data.getItem_id()).getArchitectureList();
                        dataBinding.spnArchitecture.setAdapter(new DrawingsSpinnerAdapter(activity, R.layout.item_spinner, architectureList));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // 도면 다운로드 처리
    private void planDownload(final int position, final PlanDataList.PlanData data) {
        startLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isComplete = FTPConnectUtil.getInstance().ftpPlanDownload(position, data, null);
                if (isComplete) {
                    SharedPrefUtil.putBoolean(data.getPlan_img_file(), true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initDrawing();
                        }
                    });
                }
            }
        }).start();
    }

    // 도면 이미지 Initialize
    private void initDrawing() {
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e) {
                if (dataBinding.imgDrawings.isReady()) {
                    regPoint = dataBinding.imgDrawings.viewToSourceCoord(e.getX(), e.getY());
                    int index = dataBinding.imgDrawings.checkClickPoint(regPoint);

                    DecimalFormat df = new DecimalFormat("00");
                    if (index == 0) {   // 새로운 핀 등록
                        dataBinding.txtNewPin.setText(df.format(pointNum));

                        resetInputPopup();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dataBinding.ivNewPin.setTranslationX(e.getX() - dataBinding.ivNewPin.getWidth() / 2.0f);
                                dataBinding.ivNewPin.setTranslationY(e.getY() - dataBinding.ivNewPin.getHeight() / 2.0f);
                                dataBinding.txtNewPin.setX(dataBinding.ivNewPin.getX());
                                dataBinding.txtNewPin.setY(dataBinding.ivNewPin.getY() + dataBinding.ivNewPin.getHeight() + 3);
                                dataBinding.layoutResearchPopup.setVisibility(View.VISIBLE);
                            }
                        }, 1);
                    } else if (index > 0) { // 핀 클릭
                        PointData data = pointList.get(index - 1);
                        final PointF point = dataBinding.imgDrawings.sourceToViewCoord(data.getPoint_x(), data.getPoint_y());
                        dataBinding.txtNewPin.setText(df.format(data.getPoint_num()));

                        setupInputPopup(data);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dataBinding.ivNewPin.setTranslationX(point.x - dataBinding.ivNewPin.getWidth() / 2.0f);
                                dataBinding.ivNewPin.setTranslationY(point.y - dataBinding.ivNewPin.getHeight() / 2.0f);
                                dataBinding.txtNewPin.setX(dataBinding.ivNewPin.getX());
                                dataBinding.txtNewPin.setY(dataBinding.ivNewPin.getY() + dataBinding.ivNewPin.getHeight() + 3);
                                dataBinding.layoutResearchPopup.setVisibility(View.VISIBLE);
                            }
                        }, 1);
                    } else {    // 핀에 등록된 이미지 클릭
                        index = -(index + 1);
                        int width = drawingPictureListAdapter.setList(pointList.get(index).getDrawingPointData().getRegImageList());

                        final int i = index;
                        PointF position = dataBinding.imgDrawings.getImagePopupPosition(i, width, dataBinding.layoutPictureListPopup.getHeight());
                        dataBinding.layoutPictureListPopup.setX(position.x);
                        dataBinding.layoutPictureListPopup.setY(position.y);
                        dataBinding.layoutPicturePopup.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PointF position = dataBinding.imgDrawings.getImagePopupPosition(i, dataBinding.layoutPictureListPopup.getWidth(), dataBinding.layoutPictureListPopup.getHeight());
                                dataBinding.layoutPictureListPopup.setX(position.x);
                                dataBinding.layoutPictureListPopup.setY(position.y);
                            }
                        }, 10);
                    }
                }
                return true;
            }
        });

        // 도면 이미지 처리
        Bitmap drawingBitmap = BitmapFactory.decodeFile(planFile);
        dataBinding.imgDrawings.setImage(ImageSource.bitmap(drawingBitmap));
        dataBinding.imgDrawings.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        dataBinding.imgDrawings.setZoomEnabled(false);
        dataBinding.imgDrawings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        dataBinding.imgDrawings.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {
            @Override
            public void onReady() {
                super.onReady();
                initScale = dataBinding.imgDrawings.getMinScale();
                dataBinding.imgDrawings.setMaxScale(12);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestPointList();
                    }
                }, 500);
            }
        });

        // 핀에 등록된 이미지 리스트 팝업
        drawingPictureListAdapter = new DrawingPictureListAdapter(this);
        dataBinding.recyclerPicturePopup.setAdapter(drawingPictureListAdapter);
        dataBinding.recyclerPicturePopup.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    public void selectTableData(int pointId) {
        PointData data = null;
        for (PointData d : pointList) {
            if (d.getPoint_id() == pointId) {
                data = d;
                break;
            }
        }
        if (data != null) {
            DecimalFormat df = new DecimalFormat("00");
            final PointF point = dataBinding.imgDrawings.sourceToViewCoord(data.getPoint_x(), data.getPoint_y());
            dataBinding.txtNewPin.setText(df.format(data.getPoint_num()));

            setupInputPopup(data);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dataBinding.ivNewPin.setTranslationX(point.x - dataBinding.ivNewPin.getWidth() / 2.0f);
                    dataBinding.ivNewPin.setTranslationY(point.y - dataBinding.ivNewPin.getHeight() / 2.0f);
                    dataBinding.txtNewPin.setX(dataBinding.ivNewPin.getX());
                    dataBinding.txtNewPin.setY(dataBinding.ivNewPin.getY() + dataBinding.ivNewPin.getHeight() + 3);
                    dataBinding.layoutResearchPopup.setVisibility(View.VISIBLE);
                }
            }, 1);
        }
    }

    // 도면 사진리스트 팝업 Width 조정
    public int setDrawingPictureListAdapter(int size) {
        ViewGroup.LayoutParams params = dataBinding.layoutPictureListPopup.getLayoutParams();
        if (size == 1) {
            dataBinding.recyclerPicturePopup.setLayoutManager(new LinearLayoutManager(this));
            params.width = dpToPx(171);
        } else {
            dataBinding.recyclerPicturePopup.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            params.width = dpToPx(330);
        }
        dataBinding.layoutPictureListPopup.setLayoutParams(params);
        return params.width;
    }

    // 도면 입력 팝업 Initialize
    public void initInputPopup() {
        // 입력 탭
        dataBinding.researchPopup.inputView.spnMaterial.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getMaterialSpnList()));
        dataBinding.researchPopup.inputView.spnDirection.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getDirectionSpnList()));
        dataBinding.researchPopup.inputView.spnDefect.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getDefectSpnList()));
        dataBinding.researchPopup.inputView.spnArchitecture.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getArchitectureSpnList()));
        dataBinding.researchPopup.inputView.spnLength.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getLengthSpnList()));
        dataBinding.researchPopup.inputView.spnWidth.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getWidthSpnList()));
        dataBinding.researchPopup.inputView.spnHeight.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, pointRegDataList.getHeightSpnList()));

        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnMaterial, dataBinding.researchPopup.inputView.layoutMaterial);
        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnDirection, dataBinding.researchPopup.inputView.layoutDirection);
        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnDefect, dataBinding.researchPopup.inputView.layoutDefect);
        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnArchitecture, dataBinding.researchPopup.inputView.layoutArchitecture);
        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnLength, dataBinding.researchPopup.inputView.layoutLength);
        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnWidth, dataBinding.researchPopup.inputView.layoutWidth);
        setInputSpinnerListener(dataBinding.researchPopup.inputView.spnHeight, dataBinding.researchPopup.inputView.layoutHeight);

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                resetInputPopupContentLayout();
                if (hasFocus) {
                    if (v.getParent().getParent().getParent() instanceof ConstraintLayout) {
                        ((ConstraintLayout) v.getParent().getParent().getParent()).setSelected(true);
                    } else {
                        ((ConstraintLayout) v.getParent().getParent()).setSelected(true);
                    }
                }
            }
        };
        dataBinding.researchPopup.inputView.editMaterial.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editDirection.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editDefect.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editArchitecture.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editLength.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editWidth.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editHeight.setOnFocusChangeListener(focusChangeListener);
        dataBinding.researchPopup.inputView.editCount.setOnFocusChangeListener(focusChangeListener);

        dataBinding.researchPopup.inputView.layoutMaterial.setVisibility(pointRegDataList.getMaterial_list().isEmpty() ? View.GONE : View.VISIBLE);
        dataBinding.researchPopup.inputView.layoutDirection.setVisibility(pointRegDataList.getDirection_list().isEmpty() ? View.GONE : View.VISIBLE);
        dataBinding.researchPopup.inputView.layoutDefect.setVisibility(pointRegDataList.getDefect_list().isEmpty() ? View.GONE : View.VISIBLE);
        dataBinding.researchPopup.inputView.layoutArchitecture.setVisibility(pointRegDataList.getArchitecture_list().isEmpty() ? View.GONE : View.VISIBLE);
        dataBinding.researchPopup.inputView.layoutLength.setVisibility(pointRegDataList.getLength_list().isEmpty() ? View.GONE : View.VISIBLE);
        dataBinding.researchPopup.inputView.layoutWidth.setVisibility(pointRegDataList.getWidth_list().isEmpty() ? View.GONE : View.VISIBLE);
        dataBinding.researchPopup.inputView.layoutHeight.setVisibility(pointRegDataList.getHeight_list().isEmpty() ? View.GONE : View.VISIBLE);

        // 사진 탭
        inputPopupPictureListAdapter = new InputPopupPictureListAdapter(this);
        dataBinding.researchPopup.pictureView.recyclerPicture.setAdapter(inputPopupPictureListAdapter);
        dataBinding.researchPopup.pictureView.recyclerPicture.setLayoutManager(new GridLayoutManager(this, 2));

        // 음성 탭
        inputPopupRecordListAdapter = new InputPopupRecordListAdapter(this);
        dataBinding.researchPopup.recordView.recyclerRecord.setAdapter(inputPopupRecordListAdapter);
        dataBinding.researchPopup.recordView.recyclerRecord.setLayoutManager(new LinearLayoutManager(this));

        // 메모 탭
        dataBinding.researchPopup.memoView.layoutCanvas.setMode(CanvasView.Mode.DRAW);
        dataBinding.researchPopup.memoView.layoutCanvas.setDrawer(CanvasView.Drawer.PEN);
        dataBinding.researchPopup.memoView.layoutCanvas.setBaseColor(getResources().getColor(R.color.colorWhite));
        dataBinding.researchPopup.memoView.layoutCanvas.setPaintStyle(Paint.Style.STROKE);
        dataBinding.researchPopup.memoView.layoutCanvas.setPaintStrokeColor(getResources().getColor(R.color.colorBlack));
        dataBinding.researchPopup.memoView.layoutCanvas.setPaintStrokeWidth(8);
        dataBinding.researchPopup.memoView.layoutCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.GONE);
                return false;
            }
        });

        inputPopupMemoListAdapter = new InputPopupMemoListAdapter(this);
        dataBinding.researchPopup.memoView.recyclerMemo.setAdapter(inputPopupMemoListAdapter);
        dataBinding.researchPopup.memoView.recyclerMemo.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    // 도면 입력 팝업 Spinner 설정
    private void setInputSpinnerListener(final AppCompatSpinner spinner, View parent) {
        final TextView txtSpnHint = parent.findViewWithTag(getString(R.string.popup_reg_research_tag_spinner_hint));
        final TextView txtInputHint = parent.findViewWithTag(getString(R.string.popup_reg_research_tag_input_hint));
        final EditText editInput = parent.findViewWithTag(getString(R.string.popup_reg_research_tag_input));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (txtInputHint == null) {
                    if (parent.getSelectedItemPosition() == parent.getCount() - 1) {
                        txtSpnHint.setVisibility(View.GONE);
                        editInput.setVisibility(View.VISIBLE);
                        txtSpnHint.setText("");
                        Log.e(TAG, spinner.isSelected() + " " + editInput.getText().toString());
                        if (spinner.isSelected()) {
                            editInput.setText("");
                            showKeyboard(activity, editInput);
                        }
                        spinner.setSelected(true);
                    } else {
                        txtSpnHint.setVisibility(View.VISIBLE);
                        editInput.setVisibility(View.GONE);
                        txtSpnHint.setText(parent.getItemAtPosition(parent.getSelectedItemPosition()).toString());
                        editInput.setText(parent.getItemAtPosition(parent.getSelectedItemPosition()).toString());
                    }
                } else {
                    txtSpnHint.setText(parent.getItemAtPosition(parent.getSelectedItemPosition()).toString());
                    txtInputHint.setVisibility(parent.getSelectedItemPosition() != 0 ? View.GONE : View.VISIBLE);
                    editInput.setVisibility(parent.getSelectedItemPosition() != 0 ? View.VISIBLE : View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // 도면 입력 팝업 초기화
    private void resetInputPopup() {
        setSelectedTab(DrawingType.NORMAL);

        // 입력탭
        resetInputPopupContentLayout();
        dataBinding.researchPopup.inputView.spnMaterial.setSelection(0);
        dataBinding.researchPopup.inputView.spnDirection.setSelection(0);
        dataBinding.researchPopup.inputView.spnDefect.setSelection(0);
        dataBinding.researchPopup.inputView.spnArchitecture.setSelection(0);
        dataBinding.researchPopup.inputView.spnLength.setSelection(0);
        dataBinding.researchPopup.inputView.spnWidth.setSelection(0);
        dataBinding.researchPopup.inputView.spnHeight.setSelection(0);
        dataBinding.researchPopup.inputView.editLength.setText("");
        dataBinding.researchPopup.inputView.editWidth.setText("");
        dataBinding.researchPopup.inputView.editHeight.setText("");
        dataBinding.researchPopup.inputView.editCount.setText("");
        dataBinding.researchPopup.inputView.editCount.setVisibility(View.VISIBLE);

        // 사진탭
        inputPopupPictureListAdapter.resetList();
        setPictureCount();

        // 음성탭
        dataBinding.researchPopup.recordView.btnRecord.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.recordView.layoutRecording.setVisibility(View.GONE);
        dataBinding.researchPopup.recordView.btnRecordStop.setText(R.string.popup_reg_research_record_stop);
        inputPopupRecordListAdapter.resetList();
        voiceNum = 1;

        // 메모탭
        dataBinding.researchPopup.memoView.layoutCanvas.clear();
        dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.VISIBLE);
        inputPopupMemoListAdapter.resetList();
        setMemoCount();

        dataBinding.researchPopup.layoutBtnRegister.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.layoutBtnModify.setVisibility(View.GONE);
        dataBinding.researchPopup.scrollInput.fullScroll(View.FOCUS_UP);
    }

    private void setupInputPopup(PointData data) {
        setSelectedTab(data.getDrawingPointData().getType());

        // 입력탭
        resetInputPopupContentLayout();
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnMaterial, dataBinding.researchPopup.inputView.editMaterial, pointRegDataList.getMaterialSpnList(), data.getMaterial());
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnDirection, dataBinding.researchPopup.inputView.editDirection, pointRegDataList.getDirectionSpnList(), data.getDirection());
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnDefect, dataBinding.researchPopup.inputView.editDefect, pointRegDataList.getDefectSpnList(), data.getDefect());
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnArchitecture, dataBinding.researchPopup.inputView.editArchitecture, pointRegDataList.getArchitectureSpnList(), data.getArchitecture());
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnLength, null, pointRegDataList.getLengthSpnList(), data.getLength_unit());
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnWidth, null, pointRegDataList.getWidthSpnList(), data.getWidth_unit());
        setupInputPopupSpinner(dataBinding.researchPopup.inputView.spnHeight, null, pointRegDataList.getHeightSpnList(), data.getHeight_unit());
        dataBinding.researchPopup.inputView.editLength.setText(data.getLength() == 0 ? "" : String.valueOf(data.getLength()));
        dataBinding.researchPopup.inputView.editWidth.setText(data.getWidth() == 0 ? "" : String.valueOf(data.getWidth()));
        dataBinding.researchPopup.inputView.editHeight.setText(data.getHeight() == 0 ? "" : String.valueOf(data.getHeight()));
        dataBinding.researchPopup.inputView.editCount.setText(String.valueOf(data.getCount()));
        dataBinding.researchPopup.inputView.editCount.setVisibility(View.VISIBLE);

        // 사진탭
        inputPopupPictureListAdapter.resetList();
        inputPopupPictureListAdapter.setList(data.getPoint_img());

        // 음성탭
        dataBinding.researchPopup.recordView.btnRecord.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.recordView.layoutRecording.setVisibility(View.GONE);
        dataBinding.researchPopup.recordView.btnRecordStop.setText(R.string.popup_reg_research_record_stop);
        inputPopupRecordListAdapter.resetList();
        inputPopupRecordListAdapter.setList(data.getPoint_voice());
        voiceNum = data.getVoice_num();

        // 메모탭
        dataBinding.researchPopup.memoView.layoutCanvas.clear();
        dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.VISIBLE);
        inputPopupMemoListAdapter.resetList();
        inputPopupMemoListAdapter.setList(data.getPoint_memo());

        dataBinding.researchPopup.layoutBtnRegister.setVisibility(View.GONE);
        dataBinding.researchPopup.layoutBtnModify.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.scrollInput.fullScroll(View.FOCUS_UP);

        updateData = data;
    }

    private void setupInputPopupSpinner(Spinner spinner, EditText editText, List<String> spnList, String itemName) {
        boolean isSelected = false;
        for (int i = 0; i < spnList.size(); i++) {
            if (spnList.get(i).equals(itemName)) {
                spinner.setSelection(i);
                isSelected = true;
                break;
            }
        }
        if (!isSelected) {
            if (itemName == null) {
                spinner.setSelection(0);
            } else {
                spinner.setSelected(false);
                spinner.setSelection(spnList.size() - 1);
                editText.setText(itemName);
            }
        }
    }

    private void calculateScale(boolean plus) {
        if (plus) {
            switch (currentScale) {
                case 2: currentScale = 3; break;
                case 3: currentScale = 4; break;
                case 4: currentScale = 8; break;
                case 8: currentScale = 12; break;
                default: return;
            }
        } else {
            switch (currentScale) {
                case 3: currentScale = 2; break;
                case 4: currentScale = 3; break;
                case 8: currentScale = 4; break;
                case 12: currentScale = 8; break;
                default: return;
            }
        }
        dataBinding.imgDrawings.animateScale(initScale * currentScale / 2.0f).withDuration(500).start();
        showScaleView();
    }

    private void showScaleView() {
        DecimalFormat df = new DecimalFormat("0.#");
        String scaleStr = "x" + df.format(currentScale / 2.0f) + "배";

        dataBinding.layoutScale.setVisibility(View.VISIBLE);
        dataBinding.txtScale.setText(scaleStr);

        final Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                dataBinding.layoutScale.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                dataBinding.layoutScale.startAnimation(fadeOut);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        dataBinding.layoutScale.startAnimation(fadeIn);
    }

    // 도면 입력 팝업 사진 개수
    public void setPictureCount() {
        dataBinding.researchPopup.pictureView.txtPictureCount.setText("(" + inputPopupPictureListAdapter.getItemCount() + "건)");
    }

    // 도면 입력 팝업 메모 개수
    public void setMemoCount() {
        dataBinding.researchPopup.memoView.txtMemoCount.setText("(" + inputPopupMemoListAdapter.getItemCount() + "건)");
    }

    // 도면 입력 팝업 상단 탭 클릭 리스너
    public void onInputPopupTabClick(View v) {
        hideKeyboard(this);
        switch (v.getId()) {
            case R.id.btn_popup_close:
                regPoint = null;
                stopRecord();
                inputPopupRecordListAdapter.setRecording(false);
                if (recordTimer != null) {
                    recordTimer.cancel();
                    recordTimer.purge();
                }

                dataBinding.layoutResearchPopup.setVisibility(View.INVISIBLE);
                break;
            case R.id.txt_input_tab: setSelectedTab(DrawingType.NORMAL); break;
            case R.id.txt_picture_tab: setSelectedTab(DrawingType.IMAGE); break;
            case R.id.txt_record_tab: setSelectedTab(DrawingType.VOICE); break;
            case R.id.txt_memo_tab: setSelectedTab(DrawingType.MEMO); break;
        }
    }

    // 도면 입력 팝업 상단 탭 이동
    private void setSelectedTab(DrawingType type) {
        if (type != DrawingType.VOICE && isRecording()) {
            showToast(R.string.toast_popup_reg_research_record_tab_change);
            return;
        }
        dataBinding.researchPopup.txtInputTab.setSelected(false);
        dataBinding.researchPopup.txtPictureTab.setSelected(false);
        dataBinding.researchPopup.txtRecordTab.setSelected(false);
        dataBinding.researchPopup.txtMemoTab.setSelected(false);

        switch (type) {
            case NORMAL: dataBinding.researchPopup.txtInputTab.setSelected(true); break;
            case IMAGE: dataBinding.researchPopup.txtPictureTab.setSelected(true); break;
            case VOICE: dataBinding.researchPopup.txtRecordTab.setSelected(true); break;
            case MEMO: dataBinding.researchPopup.txtMemoTab.setSelected(true); break;
        }

        dataBinding.researchPopup.layoutInput.setVisibility(type == DrawingType.NORMAL ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutPicture.setVisibility((type == DrawingType.NORMAL || type == DrawingType.IMAGE) ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutRecord.setVisibility(type == DrawingType.VOICE ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutMemo.setVisibility(type == DrawingType.MEMO ? View.VISIBLE : View.GONE);
    }

    // 도면 입력 팝업 입력 탭 내용 클릭
    public void onInputPopupContentClick(View v) {
        hideKeyboard(this);
        resetInputPopupContentLayout();
        v.setSelected(true);

        switch (v.getId()) {
            case R.id.layout_material: dataBinding.researchPopup.inputView.spnMaterial.performClick(); break;
            case R.id.layout_direction: dataBinding.researchPopup.inputView.spnDirection.performClick(); break;
            case R.id.layout_defect: dataBinding.researchPopup.inputView.spnDefect.performClick(); break;
            case R.id.layout_architecture: dataBinding.researchPopup.inputView.spnArchitecture.performClick(); break;
            case R.id.layout_length: dataBinding.researchPopup.inputView.spnLength.performClick(); break;
            case R.id.layout_length_input:
                dataBinding.researchPopup.inputView.layoutLength.setSelected(true);
                if (dataBinding.researchPopup.inputView.editLength.getVisibility() == View.GONE) {
                    dataBinding.researchPopup.inputView.spnLength.performClick();
                } else {
                    showKeyboard(this, dataBinding.researchPopup.inputView.editLength);
                }
                break;
            case R.id.layout_width: dataBinding.researchPopup.inputView.spnWidth.performClick(); break;
            case R.id.layout_width_input:
                dataBinding.researchPopup.inputView.layoutWidth.setSelected(true);
                if (dataBinding.researchPopup.inputView.editWidth.getVisibility() == View.GONE) {
                    dataBinding.researchPopup.inputView.spnWidth.performClick();
                } else {
                    showKeyboard(this, dataBinding.researchPopup.inputView.editWidth);
                }
                break;
            case R.id.layout_height: dataBinding.researchPopup.inputView.spnHeight.performClick(); break;
            case R.id.layout_height_input:
                dataBinding.researchPopup.inputView.layoutHeight.setSelected(true);
                if (dataBinding.researchPopup.inputView.editHeight.getVisibility() == View.GONE) {
                    dataBinding.researchPopup.inputView.spnHeight.performClick();
                } else {
                    showKeyboard(this, dataBinding.researchPopup.inputView.editHeight);
                }
                break;
            case R.id.layout_count: showKeyboard(this, dataBinding.researchPopup.inputView.editCount); break;
            case R.id.layout_picture_register:
                if (requestPermission(this, IMAGE_PERMISSION, IMAGE_PERMISSION_CODE)) {
                    getFileChooserImage(this, "img_" + getDateFormat("yyMMddHHmmss") + ".jpg");
                }
                break;
        }
    }

    // 도면 입력 팝업 입력 탭 선택 초기화
    private void resetInputPopupContentLayout() {
        dataBinding.researchPopup.inputView.layoutMaterial.setSelected(false);
        dataBinding.researchPopup.inputView.layoutDirection.setSelected(false);
        dataBinding.researchPopup.inputView.layoutDefect.setSelected(false);
        dataBinding.researchPopup.inputView.layoutArchitecture.setSelected(false);
        dataBinding.researchPopup.inputView.layoutLength.setSelected(false);
        dataBinding.researchPopup.inputView.layoutWidth.setSelected(false);
        dataBinding.researchPopup.inputView.layoutHeight.setSelected(false);
        dataBinding.researchPopup.inputView.layoutCount.setSelected(false);
    }

    public void onInputPopupRecordClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                if (requestPermission(this, RECORD_PERMISSION, RECORD_PERMISSION_CODE)) {
                    recording();
                }
                break;
            case R.id.btn_record_stop:
                String txt = ((Button) v).getText().toString();
                if (txt.equals(getString(R.string.popup_reg_research_record_stop))) {
                    if (recordTime < 1000) {
                        return;
                    }

                    stopRecord();
                    recordTimer.cancel();
                    recordTimer.purge();
                    dataBinding.researchPopup.recordView.btnRecordStop.setText(R.string.popup_reg_research_record_save);
                } else {
                    dataBinding.researchPopup.recordView.btnRecord.setVisibility(View.VISIBLE);
                    dataBinding.researchPopup.recordView.layoutRecording.setVisibility(View.GONE);
                    dataBinding.researchPopup.recordView.btnRecordStop.setText(R.string.popup_reg_research_record_stop);

                    inputPopupRecordListAdapter.setRecording(false);
                    PointData.PointVoice data = new PointData.PointVoice(0, voiceNum, recordTime, recordFile, recordName);
                    inputPopupRecordListAdapter.addData(data);
                }
                break;
        }
    }

    private void recording() {
        dataBinding.researchPopup.recordView.btnRecord.setVisibility(View.GONE);
        dataBinding.researchPopup.recordView.layoutRecording.setVisibility(View.VISIBLE);
        recordName = dataBinding.txtNewPin.getText().toString() + " 음성녹음 - " + (voiceNum++) + "(자동완성)";
        dataBinding.researchPopup.recordView.txtRecordingName.setText(recordName);

        inputPopupRecordListAdapter.setRecording(true);
        inputPopupRecordListAdapter.resetPlay();
        inputPopupRecordListAdapter.notifyDataSetChanged();

        String filename = getDateFormat("yyMMddHHmmss") + "_" + UserData.getInstance().getDeviceId() + ".mp3";
        recordFile = startRecord(filename);
        recordTime = 0;
        recordTimer = new Timer();
        recordTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DecimalFormat df = new DecimalFormat("00");
                int min = recordTime / 1000 / 60;
                int sec = (recordTime / 1000) % 60;
                final String time = df.format(min) + ":" + df.format(sec);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataBinding.researchPopup.recordView.txtRecordingTimer.setText(time);
                    }
                });
                recordTime += 10;
            }
        }, 0, 10);
    }

    // 도면 선택 다이얼로그 출력
    private void showDrawingsSelectDialog() {
        if (isShowingDrawingsSelectDialog) {
            return;
        }
        if (dataBinding.layoutResearchPopup.getVisibility() == View.VISIBLE) {
            dataBinding.researchPopup.btnPopupClose.performClick();
//            dataBinding.btnClose.performClick();
        }
        DrawingsSelectDialog.builder(this)
                .setBtnResetListener(new DrawingsSelectDialog.BtnClickListener() {
                    @Override
                    public void onClick(DrawingsSelectDialog dialog) {
                        dialog.dismiss();
                        requestResearchCheck();
                    }
                })
                .setBtnOtherListener(new DrawingsSelectDialog.BtnClickListener() {
                    @Override
                    public void onClick(DrawingsSelectDialog dialog) {
                        dialog.dismiss();

                        int facCateId = ((MenuCheckData) dataBinding.spnCategory.getSelectedItem()).getItem_id();
                        int structureId = ((MenuCheckData) dataBinding.spnArchitecture.getSelectedItem()).getItem_id();
                        int researchTypeId = ((MenuCheckData) dataBinding.spnResearch.getSelectedItem()).getItem_id();

                        Intent intent = new Intent();
                        intent.putExtra("fac_cate_id", facCateId);
                        intent.putExtra("structure_id", structureId);
                        intent.putExtra("research_type_id", researchTypeId);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setBtnCloseListener(new DrawingsSelectDialog.BtnClickListener() {
                    @Override
                    public void onClick(DrawingsSelectDialog dialog) {
                        initSpinner();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowingDrawingsSelectDialog = false;
                            }
                        },100);
                    }
                })
                .show();
        isShowingDrawingsSelectDialog = true;
    }

    // 도면 입력 데이터 리스트
    private void requestPointList() {
        RetrofitClient.getRetrofitApi().researchPointList(UserData.getInstance().getUserId(), researchSelectData.getResearch_id(), planId).enqueue(new RetrofitCallbackModel<PointListData>() {
            @Override
            public void onResponseData(PointListData data) {
                responsePointList(data);
            }
            @Override
            public void onCallbackFinish() { endLoading(); }
        });
    }

    // 도면 입력 데이터 처리
    private void responsePointList(PointListData data) {
        pointList = data.getPoint_list();
        pointNum = data.getPoint_next_num();
        tableDataList.clear();
        for (PointData point : pointList) {
            if (point.getPoint_type() == 1) {
                String content = "-";
                String measure = "-";
                if (point.getMaterial() != null && !point.getMaterial().equals("")) {
                    content = point.getMaterial();
                } else if (point.getDirection() != null && !point.getDirection().equals("")) {
                    content = point.getDirection();
                } else if (point.getDefect() != null && !point.getDefect().equals("")) {
                    content = point.getDefect();
                }
                if (point.getLength_unit() != null && !point.getLength_unit().equals("")) {
                    measure = point.getLength_unit() + "(" + point.getLength() + ")";
                } else if (point.getWidth_unit() != null && !point.getWidth_unit().equals("")) {
                    measure = point.getWidth_unit() + "(" + point.getWidth() + ")";
                } else if (point.getHeight_unit() != null && !point.getHeight_unit().equals("")) {
                    measure = point.getHeight_unit() + "(" + point.getHeight() + ")";
                }
                tableDataList.add(new PointTableData(point.getPoint_id(), point.getPoint_num(), content, measure, point.getCount()));
            }
        }
        setImageList(data.getLabel_img());
    }

    // 도면 입력 이미지 가져오기
    private void setImageList(final String labelImg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!tableDataList.isEmpty() && labelImg != null && !labelImg.equals("")) {
                    Bitmap bitmap = FTPConnectUtil.getInstance().ftpImageBitmap(labelImg);
                    for (PointTableData data : tableDataList) {
                        data.setLabel(bitmap);
                    }
                }
                for (PointData data : pointList) {
                    if (!data.getPoint_img().isEmpty()) {
                        List<String> pathList = new ArrayList<>();
                        for (PointData.PointImg img : data.getPoint_img()) {
                            pathList.add(img.getImg_url());
                        }
                        List<Bitmap> bitmapList = FTPConnectUtil.getInstance().ftpImageBitmap(pathList);
                        for (int i = 0; i < bitmapList.size(); i++) {
                            data.getPoint_img().get(i).setImgBitmap(bitmapList.get(i));
                        }
                    }
                    if (!data.getPoint_voice().isEmpty()) {
                        List<String> pathList = new ArrayList<>();
                        for (PointData.PointVoice voice : data.getPoint_voice()) {
                            pathList.add(voice.getVoice_file());
                        }
                        List<File> fileList = FTPConnectUtil.getInstance().ftpFileTempDownload(pathList);
                        for (int i = 0; i < fileList.size(); i++) {
                            data.getPoint_voice().get(i).setVoiceFile(fileList.get(i));
                            if (data.getPoint_voice().get(i).getVoice_num() == 0) {
                                String filename = fileList.get(i).getName();
                                data.getPoint_voice().get(i).setVoiceName(filename.substring(0, filename.lastIndexOf(".")));
                            } else {
                                DecimalFormat df = new DecimalFormat("00");
                                String filename = df.format(data.getPoint_num()) + " 음성녹음 - " + data.getPoint_voice().get(i).getVoice_num() + "(자동완성)";
                                data.getPoint_voice().get(i).setVoiceName(filename);
                            }
                        }
                    }
                    if (!data.getPoint_memo().isEmpty()) {
                        List<String> pathList = new ArrayList<>();
                        for (PointData.PointMemo memo : data.getPoint_memo()) {
                            pathList.add(memo.getMemo_url());
                        }
                        List<Bitmap> bitmapList = FTPConnectUtil.getInstance().ftpImageBitmap(pathList);
                        for (int i = 0; i < bitmapList.size(); i++) {
                            data.getPoint_memo().get(i).setMemoBitmap(bitmapList.get(i));
                        }
                    }
                    data.setDrawingPointData();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawingsDataListAdapter.setList(tableDataList);
                        dataBinding.imgDrawings.setPinList(pointList);
                        endLoading();
                    }
                });
            }
        }).start();
    }

    // 도면 입력 등록 팝업 데이터
    private void requestPointRegisterData() {
        RetrofitClient.getRetrofitApi()
                .researchPointRegisterData(UserData.getInstance().getUserId(), researchSelectData.getResearch_id())
                .enqueue(new RetrofitCallbackModel<PointInputData>() {
                    @Override
                    public void onResponseData(PointInputData data) {
                        data.setSpinnerList();
                        pointRegDataList = data;
                        initInputPopup();
                    }
                    @Override
                    public void onCallbackFinish() {}
                });
    }

    // 조사종류 변경
    private void requestResearchCheck() {
        int facCateId = ((MenuCheckData) dataBinding.spnCategory.getSelectedItem()).getItem_id();
        int structureId = ((MenuCheckData) dataBinding.spnArchitecture.getSelectedItem()).getItem_id();
        int researchTypeId = ((MenuCheckData) dataBinding.spnResearch.getSelectedItem()).getItem_id();

        startLoading();
        RetrofitClient.getRetrofitApi()
                .researchCheckData(UserData.getInstance().getUserId(), researchSelectData.getResearch_id(), researchSelectData.getProject_id(), researchSelectData.getFacility_id(), facCateId, structureId, researchTypeId)
                .enqueue(new RetrofitCallbackModel<ResearchCheckData>() {
                    @Override
                    public void onResponseData(ResearchCheckData data) {
                        researchSelectData = data.getResearch_data();
                        requestPointList();
                        requestPointRegisterData();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isShowingDrawingsSelectDialog = false;
                            }
                        }, 100);
                    }
                    @Override
                    public void onCallbackFinish() { endLoading(); }
                });
    }

    // 도면 입력 등록
    private void requestRegisterPoint() {
        if (regPoint != null) {
            int pointX = (int) regPoint.x;
            int pointY = (int) regPoint.y;
            String material = dataBinding.researchPopup.inputView.editMaterial.getText().toString().trim();
            String direction = dataBinding.researchPopup.inputView.editDirection.getText().toString().trim();
            String defect = dataBinding.researchPopup.inputView.editDefect.getText().toString().trim();
            String architecture = dataBinding.researchPopup.inputView.editArchitecture.getText().toString().trim();
            String lengthUnit = dataBinding.researchPopup.inputView.txtLength.getText().toString().trim();
            String length = dataBinding.researchPopup.inputView.editLength.getText().toString().trim();
            String widthUnit = dataBinding.researchPopup.inputView.txtWidth.getText().toString().trim();
            String width = dataBinding.researchPopup.inputView.editWidth.getText().toString().trim();
            String heightUnit = dataBinding.researchPopup.inputView.txtHeight.getText().toString().trim();
            String height = dataBinding.researchPopup.inputView.editHeight.getText().toString().trim();
            final String count = dataBinding.researchPopup.inputView.editCount.getText().toString().trim();

            if (!pointRegDataList.getDefect_list().isEmpty() && defect.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_defect);
                return;
            }
            if (count.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_count);
                return;
            }
//            if (researchSelectData.getTot_count() < researchSelectData.getReg_count() + Integer.parseInt(count)) {
//                showToast(R.string.toast_popup_reg_research_over_count);
//                return;
//            }
            if (!lengthUnit.equals("") && length.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_length);
                return;
            }
            if (!widthUnit.equals("") && width.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_width);
                return;
            }
            if (!heightUnit.equals("") && height.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_height);
                return;
            }

            Map<String, RequestBody> partMap = new HashMap<>();
            if (!material.equals("")) { partMap.put("material", RequestBody.create(MediaType.parse("multipart/form-data"), material)); }
            if (!direction.equals("")) { partMap.put("direction", RequestBody.create(MediaType.parse("multipart/form-data"), direction)); }
            if (!defect.equals("")) { partMap.put("defect", RequestBody.create(MediaType.parse("multipart/form-data"), defect)); }
            if (!architecture.equals("")) { partMap.put("architecture", RequestBody.create(MediaType.parse("multipart/form-data"), architecture)); }
            if (!lengthUnit.equals("")) { partMap.put("length_unit", RequestBody.create(MediaType.parse("multipart/form-data"), lengthUnit)); }
            if (!length.equals("")) { partMap.put("length", RequestBody.create(MediaType.parse("multipart/form-data"), length)); }
            if (!widthUnit.equals("")) { partMap.put("width_unit", RequestBody.create(MediaType.parse("multipart/form-data"), widthUnit)); }
            if (!width.equals("")) { partMap.put("width", RequestBody.create(MediaType.parse("multipart/form-data"), width)); }
            if (!heightUnit.equals("")) { partMap.put("height_unit", RequestBody.create(MediaType.parse("multipart/form-data"), heightUnit)); }
            if (!height.equals("")) { partMap.put("height", RequestBody.create(MediaType.parse("multipart/form-data"), height)); }

            List<MultipartBody.Part> fileList = getUploadFile(partMap);
            if (fileList.size() == 0) {
                fileList = null;
            }

            startLoading();
            RetrofitClient.getRetrofitApi().researchRegisterPoint(UserData.getInstance().getUserId(), researchSelectData.getResearch_id(), planId, pointX, pointY, Integer.parseInt(count), partMap, fileList)
                    .enqueue(new RetrofitCallbackModel<PointListData>() {
                        @Override
                        public void onResponseData(PointListData data) {
                            dataBinding.layoutResearchPopup.setVisibility(View.INVISIBLE);
                            researchSelectData.setReg_count(data.getReg_count());
                            responsePointList(data);
                        }
                        @Override
                        public void onCallbackFinish() { endLoading(); }
                    });
        }
    }

    // 도면 입력 수정
    private void requestUpdatePoint() {
        if (updateData != null) {
            String material = dataBinding.researchPopup.inputView.editMaterial.getText().toString().trim();
            String direction = dataBinding.researchPopup.inputView.editDirection.getText().toString().trim();
            String defect = dataBinding.researchPopup.inputView.editDefect.getText().toString().trim();
            String architecture = dataBinding.researchPopup.inputView.editArchitecture.getText().toString().trim();
            String lengthUnit = dataBinding.researchPopup.inputView.txtLength.getText().toString().trim();
            String length = dataBinding.researchPopup.inputView.editLength.getText().toString().trim();
            String widthUnit = dataBinding.researchPopup.inputView.txtWidth.getText().toString().trim();
            String width = dataBinding.researchPopup.inputView.editWidth.getText().toString().trim();
            String heightUnit = dataBinding.researchPopup.inputView.txtHeight.getText().toString().trim();
            String height = dataBinding.researchPopup.inputView.editHeight.getText().toString().trim();
            String count = dataBinding.researchPopup.inputView.editCount.getText().toString().trim();

            if (!pointRegDataList.getDefect_list().isEmpty() && defect.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_defect);
                return;
            }
            if (count.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_count);
                return;
            }
            if (researchSelectData.getTot_count() < researchSelectData.getReg_count() - updateData.getCount() + Integer.parseInt(count)) {
                showToast(R.string.toast_popup_reg_research_over_count);
                return;
            }
            if (!lengthUnit.equals("") && length.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_length);
                return;
            }
            if (!widthUnit.equals("") && width.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_width);
                return;
            }
            if (!heightUnit.equals("") && height.equals("")) {
                showToast(R.string.toast_popup_reg_research_not_height);
                return;
            }

            Map<String, RequestBody> partMap = new HashMap<>();
            if (!material.equals("")) { partMap.put("material", RequestBody.create(MediaType.parse("multipart/form-data"), material)); }
            if (!direction.equals("")) { partMap.put("direction", RequestBody.create(MediaType.parse("multipart/form-data"), direction)); }
            if (!defect.equals("")) { partMap.put("defect", RequestBody.create(MediaType.parse("multipart/form-data"), defect)); }
            if (!architecture.equals("")) { partMap.put("architecture", RequestBody.create(MediaType.parse("multipart/form-data"), architecture)); }
            if (!lengthUnit.equals("")) { partMap.put("length_unit", RequestBody.create(MediaType.parse("multipart/form-data"), lengthUnit)); }
            if (!length.equals("")) { partMap.put("length", RequestBody.create(MediaType.parse("multipart/form-data"), length)); }
            if (!widthUnit.equals("")) { partMap.put("width_unit", RequestBody.create(MediaType.parse("multipart/form-data"), widthUnit)); }
            if (!width.equals("")) { partMap.put("width", RequestBody.create(MediaType.parse("multipart/form-data"), width)); }
            if (!heightUnit.equals("")) { partMap.put("height_unit", RequestBody.create(MediaType.parse("multipart/form-data"), heightUnit)); }
            if (!height.equals("")) { partMap.put("height", RequestBody.create(MediaType.parse("multipart/form-data"), height)); }

            List<MultipartBody.Part> fileList = getUploadFile(partMap);
            if (fileList.size() == 0) {
                fileList = null;
            }

            List<Integer> deleteImage = inputPopupPictureListAdapter.getDeleteList();
            List<Integer> deleteVoice = inputPopupRecordListAdapter.getDeleteList();
            List<Integer> deleteMemo = inputPopupMemoListAdapter.getDeleteList();
            if (deleteImage.size() > 0) { partMap.put("delete_img", RequestBody.create(MediaType.parse("multipart/form-data"), deleteImage.toString())); }
            if (deleteVoice.size() > 0) { partMap.put("delete_voice", RequestBody.create(MediaType.parse("multipart/form-data"), deleteVoice.toString())); }
            if (deleteMemo.size() > 0) { partMap.put("delete_memo", RequestBody.create(MediaType.parse("multipart/form-data"), deleteMemo.toString())); }

            startLoading();
            RetrofitClient.getRetrofitApi().researchUpdatePoint(UserData.getInstance().getUserId(), researchSelectData.getResearch_id(), updateData.getPoint_id(), Integer.parseInt(count), partMap, fileList)
                    .enqueue(new RetrofitCallbackModel<PointListData>() {
                        @Override
                        public void onResponseData(PointListData data) {
                            dataBinding.layoutResearchPopup.setVisibility(View.INVISIBLE);
                            researchSelectData.setReg_count(data.getReg_count());
                            responsePointList(data);
                        }
                        @Override
                        public void onCallbackFinish() {
                            endLoading();
                        }
                    });
        }
    }

    // 도면 입력 삭제
    private void requestDeletePoint(final List<Integer> deletePointList) {
        if (deletePointList.size() > 0) {
            startLoading();
            RetrofitClient.getRetrofitApi().researchDeletePoint(UserData.getInstance().getUserId(), deletePointList.toString())
                    .enqueue(new RetrofitCallbackModel<PointListData>() {
                        @Override
                        public void onResponseData(PointListData data) {
                            dataBinding.layoutResearchPopup.setVisibility(View.INVISIBLE);

                            boolean isSelected = dataBinding.btnSelect.getText().toString().equals(getString(R.string.select));
                            if (!isSelected) {
                                dataBinding.btnSelect.setText(R.string.select);
                                dataBinding.txtNumber.setVisibility(View.VISIBLE);
                                dataBinding.checkboxNumber.setVisibility(View.GONE);
                                dataBinding.btnDelete.setVisibility(View.GONE);
                                dataBinding.checkboxNumber.setChecked(false);
                                drawingsDataListAdapter.setCheckable(false);
                            }
                            researchSelectData.setReg_count(data.getReg_count());
                            responsePointList(data);
                        }
                        @Override
                        public void onCallbackFinish() { endLoading(); }
                    });
        }
    }

    // 도면 입력 업로드 파일
    private List<MultipartBody.Part> getUploadFile(Map<String, RequestBody> partMap) {
        List<MultipartBody.Part> fileList = new ArrayList<>();

        // 사진탭
        if (inputPopupPictureListAdapter.getUploadList().size() > 0) {
            for (PointData.PointImg data : inputPopupPictureListAdapter.getUploadList()) {
                try {
                    File file = FileUtil.from(this, data.getImgUri());
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("image", URLEncoder.encode(file.getName(), "utf-8"), requestBody);
                    fileList.add(multipartBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 음성탭
        if (inputPopupRecordListAdapter.getUploadList().size() > 0) {
            List<Integer> voiceTimeList = new ArrayList<>();
            for (PointData.PointVoice data : inputPopupRecordListAdapter.getUploadList()) {
                try {
                    File file = data.getVoiceFile();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("voice", URLEncoder.encode(file.getName(), "utf-8"), requestBody);
                    fileList.add(multipartBody);
                    voiceTimeList.add(data.getVoice_time());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            partMap.put("voice_time", RequestBody.create(MediaType.parse("multipart/form-data"), voiceTimeList.toString()));
        }

        // 메모탭
        if (inputPopupMemoListAdapter.getUploadList().size() > 0) {
            for (PointData.PointMemo data : inputPopupMemoListAdapter.getUploadList()) {
                File file = new File(getCachePath(), data.getMemoBitmapName());
                OutputStream output = null;
                try {
                    if (file.createNewFile()) {
                        output = new FileOutputStream(file);
                        data.getMemoBitmap().compress(Bitmap.CompressFormat.JPEG, 100, output);

                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("memo", URLEncoder.encode(file.getName(), "utf-8"), requestBody);
                        fileList.add(multipartBody);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return fileList;
    }
}
