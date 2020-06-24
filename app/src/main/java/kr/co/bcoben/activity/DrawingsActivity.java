package kr.co.bcoben.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingInputSpinnerAdapter;
import kr.co.bcoben.adapter.DrawingPictureListAdapter;
import kr.co.bcoben.adapter.InputPopupPictureListAdapter;
import kr.co.bcoben.adapter.RecordListAdapter;
import kr.co.bcoben.adapter.TableListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CanvasView;
import kr.co.bcoben.component.DrawingsSelectDialog;
import kr.co.bcoben.databinding.ActivityDrawingsBinding;
import kr.co.bcoben.model.DrawingPointData;
import kr.co.bcoben.model.MenuCheckData;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.PointData;
import kr.co.bcoben.model.PointListData;
import kr.co.bcoben.model.PointRegisterData;
import kr.co.bcoben.model.PointTableData;
import kr.co.bcoben.model.RecordData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.model.ResearchCheckData;
import kr.co.bcoben.model.ResearchRegData;
import kr.co.bcoben.model.ResearchSpinnerData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.CommonUtil.PermissionState;
import kr.co.bcoben.util.FTPConnectUtil;
import kr.co.bcoben.util.SharedPrefUtil;

import static kr.co.bcoben.model.DrawingPointData.DrawingType;
import static kr.co.bcoben.util.CommonUtil.dpToPx;
import static kr.co.bcoben.util.CommonUtil.getFileChooserImage;
import static kr.co.bcoben.util.CommonUtil.getImageResult;
import static kr.co.bcoben.util.CommonUtil.hideKeyboard;
import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;
import static kr.co.bcoben.util.CommonUtil.showKeyboard;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.RecordUtil.startRecord;
import static kr.co.bcoben.util.RecordUtil.stopAudio;
import static kr.co.bcoben.util.RecordUtil.stopRecord;

public class DrawingsActivity extends BaseActivity<ActivityDrawingsBinding> implements View.OnClickListener {

    private final String[] RECORD_PERMISSION = {Manifest.permission.RECORD_AUDIO};
    private final String[] IMAGE_PERMISSION = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private final int RECORD_PERMISSION_CODE = 301;
    private final int IMAGE_PERMISSION_CODE = 302;

    // drawing
    private int planId;
    private int researchId;
    private String planFile;
    private float initScale;
    private int currentScale = 2;
    private List<PointData> pointList = new ArrayList<>();
    private PointRegisterData pointRegDataList;
    private DrawingPointData regPointData;
    private DrawingPictureListAdapter drawingPictureListAdapter;

    // spinner
    private List<PlanDataList.PlanData> planList;
    private List<String> planNameList = new ArrayList<>();
    private List<ResearchRegData.FacilityData.FacCateData> facCateDataList = new ArrayList<>();
    private List<ResearchRegData.ResearchData> researchDataList = new ArrayList<>();
    private ResearchSpinnerData.ResearchSelectData researchSelectData;
    private List<MenuCheckData> newFacCateList = new ArrayList<>();
    private List<MenuCheckData> newArchList = new ArrayList<>();
    private List<MenuCheckData> newResearchList = new ArrayList<>();
    private int projectId;
    private int facilityId;
    private int selectedFacCateId;
    private int selectedArchitectureId;
    private int selectedResearchId;
    private boolean isFirst = true;

    // table
    private TableListAdapter tableListAdapter;
    private List<PointTableData> tableDataList = new ArrayList<>();

    // popup
    private InputPopupPictureListAdapter inputPopupPictureListAdapter;
    private List<String> pictureDataList = new ArrayList<>();

    // record
    private String recordName;
    private int recordTime;
    private Timer recordTimer;
    private File recordFile;
    private RecordListAdapter recordListAdapter;
    private List<RecordData> recordDataList = new ArrayList<>();

    // Dummy Data
    private String[] inputDataArray = {"부풀음", "점부식", "박리", "백악화", "직접입력", ""};
    private String[] inputSizeDataArray = {"폭", "단차", "두께", "길이", "기타", ""};

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings;
    }

    @Override
    protected void initView() {
        startLoading();

        planList = getIntent().getParcelableArrayListExtra("plan_list");
        researchId = getIntent().getIntExtra("research_id", 0);
        int planIndex = getIntent().getIntExtra("plan_index", 0);
        planId = planList.get(planIndex).getPlan_id();
        planFile = planList.get(planIndex).getPlan_img_file();
        planId = planList.get(planIndex).getPlan_id();

        initPlanNameSpinner();

        projectId = getIntent().getIntExtra("project_id", 0);
        facilityId = getIntent().getIntExtra("facility_id", 0);
        selectedFacCateId = getIntent().getIntExtra("fac_cate_id", 0);
        selectedArchitectureId = getIntent().getIntExtra("architecture_id", 0);
        selectedResearchId = getIntent().getIntExtra("research_type_id", 0);
        Serializable serialFacCate = getIntent().getSerializableExtra("fac_cate_list");
        Serializable serialResearch = getIntent().getSerializableExtra("research_list");
        facCateDataList = (List<ResearchRegData.FacilityData.FacCateData>) serialFacCate;
        researchDataList = (List<ResearchRegData.ResearchData>) serialResearch;

        requestResearchCheckData();

        initDrawing();

        // 집계표
        dataBinding.checkboxNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tableListAdapter.setAllChecked(isChecked);
            }
        });

        dataBinding.recyclerTable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        tableListAdapter = new TableListAdapter(this, tableDataList);
        dataBinding.recyclerTable.setAdapter(tableListAdapter);

        requestPointRegisterData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionState state = resultRequestPermission(this, permissions, grantResults);

        if (state == PermissionState.GRANT) {
            if (requestCode == RECORD_PERMISSION_CODE) {
                recording();
            } else if (requestCode == IMAGE_PERMISSION_CODE) {
                getFileChooserImage(this);
            }
        } else {

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

    private void initPlanNameSpinner() {
        List<String> nameList = new ArrayList<>();
        for (PlanDataList.PlanData data : planList) {
            if (SharedPrefUtil.getBoolean(data.getPlan_img_file(), false)) {
                nameList.add(data.getPlan_name());
            }
        }

        dataBinding.spnPlan.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, nameList));

        for (int i = 0; i < planList.size(); i++) {
            if (planList.get(i).getPlan_id() == planId) {
                dataBinding.spnPlan.setSelection(i);
            }
        }
        dataBinding.spnPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                planId = planList.get(position).getPlan_id();
                if (!isFirst) {
                    changeDrawingsImage(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void changeDrawingsImage(int index) {
        planFile = planList.get(index).getPlan_img_file();
        initDrawing();
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
                if (!isFirst) {
                    showDrawingsSelectDialog();
                }
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
                if (!isFirst) {
                    showDrawingsSelectDialog();
                }
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
                if (!isFirst) {
                    showDrawingsSelectDialog();
                }
                isFirst = false;
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

    // 도면 이미지 Initialize
    private void initDrawing() {
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e) {
                if (dataBinding.imgDrawings.isReady()) {
                    PointF pin = dataBinding.imgDrawings.viewToSourceCoord(e.getX(), e.getY());
                    int index = dataBinding.imgDrawings.checkClickPoint(pin);

                    if (index == 0) {   // 새로운 핀 등록
                        regPointData = new DrawingPointData(pin, DrawingType.NORMAL);
                        DecimalFormat df = new DecimalFormat("00");
                        dataBinding.txtNewPin.setText(df.format(pointList.size() + 1));

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
                        showToast(index + "번 Point");
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
        dataBinding.researchPopup.inputView.spnLength.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, inputSizeDataArray));
        dataBinding.researchPopup.inputView.spnWidth.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, inputSizeDataArray));
        dataBinding.researchPopup.inputView.spnHeight.setAdapter(new DrawingInputSpinnerAdapter(activity, R.layout.item_spinner_research, inputSizeDataArray));

        setSpinnerListener(dataBinding.researchPopup.inputView.spnMaterial, dataBinding.researchPopup.inputView.editMaterial, dataBinding.researchPopup.inputView.txtMaterial);
        setSpinnerListener(dataBinding.researchPopup.inputView.spnDirection, dataBinding.researchPopup.inputView.editDirection, dataBinding.researchPopup.inputView.txtDirection);
        setSpinnerListener(dataBinding.researchPopup.inputView.spnDefect, dataBinding.researchPopup.inputView.editDefect, dataBinding.researchPopup.inputView.txtDefect);
        setSpinnerListener(dataBinding.researchPopup.inputView.spnArchitecture, dataBinding.researchPopup.inputView.editArchitecture, dataBinding.researchPopup.inputView.txtArchitecture);
        setSpinnerListener(dataBinding.researchPopup.inputView.spnLength, dataBinding.researchPopup.inputView.editLength, dataBinding.researchPopup.inputView.txtLength);
        setSpinnerListener(dataBinding.researchPopup.inputView.spnWidth, dataBinding.researchPopup.inputView.editWidth, dataBinding.researchPopup.inputView.txtWidth);
        setSpinnerListener(dataBinding.researchPopup.inputView.spnHeight, dataBinding.researchPopup.inputView.editHeight, dataBinding.researchPopup.inputView.txtHeight);

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                resetInputPopupContentLayout();
                if (hasFocus) {
                    ((ConstraintLayout) v.getParent()).setSelected(true);
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

        // 사진 탭
        inputPopupPictureListAdapter = new InputPopupPictureListAdapter(this, pictureDataList);
        dataBinding.researchPopup.pictureView.recyclerPicture.setAdapter(inputPopupPictureListAdapter);
        dataBinding.researchPopup.pictureView.recyclerPicture.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        // 음성 탭
        recordListAdapter = new RecordListAdapter(this, recordDataList);
        dataBinding.researchPopup.recordView.recyclerRecord.setAdapter(recordListAdapter);
        dataBinding.researchPopup.recordView.recyclerRecord.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

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
    }

    // 도면 입력 팝업 Spinner 설정
    private void setSpinnerListener(AppCompatSpinner spinner, final EditText editText, final TextView textView) {
        spinner.setSelection(spinner.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition() == parent.getCount() - 1) {
                    editText.setText("");
                    textView.setText("");
                    editText.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    showKeyboard(activity, editText);
                } else if (parent.getSelectedItemPosition() != parent.getCount()) {
                    editText.setText(parent.getItemAtPosition(parent.getSelectedItemPosition() + 1).toString());
                    textView.setText(parent.getItemAtPosition(parent.getSelectedItemPosition() + 1).toString());
                    editText.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
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
        dataBinding.researchPopup.inputView.spnMaterial.setSelection(-1);
        dataBinding.researchPopup.inputView.spnDirection.setSelection(-1);
        dataBinding.researchPopup.inputView.spnDefect.setSelection(-1);
        dataBinding.researchPopup.inputView.spnArchitecture.setSelection(-1);
        dataBinding.researchPopup.inputView.editLength.setText("");
        dataBinding.researchPopup.inputView.editWidth.setText("");
        dataBinding.researchPopup.inputView.editHeight.setText("");
        dataBinding.researchPopup.inputView.editCount.setText("");

        // 사진탭
        pictureDataList = new ArrayList<>();
        inputPopupPictureListAdapter.setList(pictureDataList);

        // 음성탭

        // 메모탭
        dataBinding.researchPopup.memoView.layoutCanvas.clear();
        dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.VISIBLE);
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
    public void setPictureCount(int count) {
        dataBinding.researchPopup.pictureView.txtPictureCount.setText("(" + count + "건)");
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
            case R.id.btn_close: finish(); break;
            case R.id.layout_picture_popup:
                dataBinding.layoutPicturePopup.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_zoom_in:
                calculateScale(true);
                break;
            case R.id.btn_zoom_out:
                calculateScale(false);
                break;
            case R.id.btn_table_handle:
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

            // table
            case R.id.btn_select:
                String btnText = dataBinding.btnSelect.getText().toString();

                dataBinding.btnSelect.setText(btnText.equals(getString(R.string.select)) ? R.string.cancel : R.string.select);
                dataBinding.txtNumber.setVisibility(btnText.equals(getString(R.string.select)) ? View.GONE : View.VISIBLE);
                dataBinding.checkboxNumber.setVisibility(btnText.equals(getString(R.string.select)) ? View.VISIBLE : View.GONE);
                dataBinding.btnDelete.setVisibility(btnText.equals(getString(R.string.select)) ? View.VISIBLE : View.GONE);

                tableListAdapter.setCheckable(btnText.equals(getString(R.string.select)));
                break;

            case R.id.btn_delete:
                //TODO delete api and table data list refresh
                break;

            // popup
            case R.id.btn_reg:
                //TODO
                dataBinding.layoutResearchPopup.setVisibility(View.GONE);
//                dataBinding.imgDrawings.addPin(regPointData);
                break;

        }
    }

    // 도면 입력 팝업 상단 탭 클릭 리스너
    public void onInputPopupTabClick(View v) {
        hideKeyboard(this);
        switch (v.getId()) {
            case R.id.btn_popup_close: dataBinding.layoutResearchPopup.setVisibility(View.INVISIBLE); break;
            case R.id.txt_input_tab: setSelectedTab(DrawingType.NORMAL); break;
            case R.id.txt_picture_tab: setSelectedTab(DrawingType.IMAGE); break;
            case R.id.txt_record_tab: setSelectedTab(DrawingType.VOICE); break;
            case R.id.txt_memo_tab: setSelectedTab(DrawingType.MEMO); break;
        }
    }

    // 도면 입력 팝업 상단 탭 이동
    private void setSelectedTab(DrawingType type) {
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

        regPointData.setType(type);
    }

    public void onInputPopupContentClick(View v) {
        hideKeyboard(this);
        resetInputPopupContentLayout();
        v.setSelected(true);

        switch (v.getId()) {
            case R.id.layout_material: dataBinding.researchPopup.inputView.spnMaterial.performClick(); break;
            case R.id.layout_direction: dataBinding.researchPopup.inputView.spnDirection.performClick(); break;
            case R.id.layout_defect: dataBinding.researchPopup.inputView.spnDefect.performClick(); break;
            case R.id.layout_architecture: dataBinding.researchPopup.inputView.spnArchitecture.performClick(); break;
            case R.id.layout_count: showKeyboard(this, dataBinding.researchPopup.inputView.editCount); break;

            case R.id.layout_length_select:
                dataBinding.researchPopup.inputView.spnLength.performClick();
                dataBinding.researchPopup.inputView.layoutLength.setSelected(true);
                break;
            case R.id.layout_length_input:
                dataBinding.researchPopup.inputView.layoutLength.setSelected(true);
                String length = dataBinding.researchPopup.inputView.txtLength.getText().toString();
                if (length.isEmpty()) {
                    dataBinding.researchPopup.inputView.spnLength.performClick();
                } else {
                    showKeyboard(this, dataBinding.researchPopup.inputView.editLengthInput);
                }
                break;

            case R.id.layout_width_select:
                dataBinding.researchPopup.inputView.spnWidth.performClick();
                dataBinding.researchPopup.inputView.layoutWidth.setSelected(true);
                break;
            case R.id.layout_width_input:
                dataBinding.researchPopup.inputView.layoutWidth.setSelected(true);
                String width = dataBinding.researchPopup.inputView.txtWidth.getText().toString();
                if (width.isEmpty()) {
                    dataBinding.researchPopup.inputView.spnWidth.performClick();
                } else {
                    showKeyboard(this, dataBinding.researchPopup.inputView.editWidthInput);
                }
                break;

            case R.id.layout_height_select:
                dataBinding.researchPopup.inputView.spnHeight.performClick();
                dataBinding.researchPopup.inputView.layoutHeight.setSelected(true);
                break;
            case R.id.layout_height_input:
                dataBinding.researchPopup.inputView.layoutHeight.setSelected(true);
                String height = dataBinding.researchPopup.inputView.txtHeight.getText().toString();
                if (height.isEmpty()) {
                    dataBinding.researchPopup.inputView.spnHeight.performClick();
                } else {
                    showKeyboard(this, dataBinding.researchPopup.inputView.editHeightInput);
                }
                break;

            case R.id.layout_picture_register:
                if (requestPermission(this, IMAGE_PERMISSION, IMAGE_PERMISSION_CODE)) {
                    getFileChooserImage(this);
                }
                break;
        }
    }
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

                    recordListAdapter.setRecording(false);
                    RecordData recordData = new RecordData(recordFile, null, recordName, recordTime);
                    recordListAdapter.addData(recordData);
                }
                break;
        }
    }

    private void recording() {

        dataBinding.researchPopup.recordView.btnRecord.setVisibility(View.GONE);
        dataBinding.researchPopup.recordView.layoutRecording.setVisibility(View.VISIBLE);
        recordName = dataBinding.txtNewPin.getText().toString() + " 음성녹음 - " + (recordDataList.size() + 1) + "(자동완성)";
        dataBinding.researchPopup.recordView.txtRecordingName.setText(recordName);

        recordListAdapter.setRecording(true);
        recordListAdapter.resetData();
        recordListAdapter.notifyDataSetChanged();

        recordFile = startRecord(recordName + ".mp3");
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
        final DrawingsSelectDialog dialog = new DrawingsSelectDialog(this);
//        dialog.selectInputListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.selectResetInputListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.selectOtherListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 조사내용 데이터 확인 API 호출
    private void requestResearchCheckData() {
        setIsLoading(true);
        RetrofitClient.getRetrofitApi().researchCheckData(UserData.getInstance().getUserId(), researchId, projectId, facilityId, selectedFacCateId, selectedArchitectureId, selectedResearchId).enqueue(new RetrofitCallbackModel<ResearchCheckData>() {
            @Override
            public void onResponseData(ResearchCheckData data) {
                researchSelectData = data.getResearch_data();

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
                setIsLoading(false);
            }

            @Override
            public void onCallbackFinish() {
                setIsLoading(false);
            }
        });
    }

    // 도면 입력 데이터 리스트
    private void requestPointList() {
        RetrofitClient.getRetrofitApi().researchPointList(UserData.getInstance().getUserId(), researchId, planId).enqueue(new RetrofitCallbackModel<PointListData>() {
            @Override
            public void onResponseData(PointListData data) {
                pointList = data.getPoint_list();
                tableDataList.clear();
                for (int i = 0; i < pointList.size(); i++) {
                    PointData point = pointList.get(i);
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
                        tableDataList.add(new PointTableData(i + 1, content, measure, point.getCount()));
                    }
                }
                setImageList(data.getLabel_img());
            }
            @Override
            public void onCallbackFinish() { endLoading(); }
        });
    }

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
                    data.setDrawingPointData();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tableListAdapter.setList(tableDataList);
                        dataBinding.imgDrawings.setPinList(pointList);
                        endLoading();
                    }
                });
            }
        }).start();
    }

    // 도면 입력 등록 팝업 데이터
    private void requestPointRegisterData() {
        RetrofitClient.getRetrofitApi().researchPointRegisterData(UserData.getInstance().getUserId(), researchId).enqueue(new RetrofitCallbackModel<PointRegisterData>() {
            @Override
            public void onResponseData(PointRegisterData data) {
                data.setSpinnerList();
                pointRegDataList = data;
                initInputPopup();
            }
            @Override
            public void onCallbackFinish() {}
        });
    }
}
