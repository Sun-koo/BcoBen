package kr.co.bcoben.activity;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.CustomSpinnerAdapter;
import kr.co.bcoben.adapter.PictureListAdapter;
import kr.co.bcoben.adapter.RecodeListAdapter;
import kr.co.bcoben.adapter.TableListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CanvasView;
import kr.co.bcoben.component.DrawingsSelectDialog;
import kr.co.bcoben.databinding.ActivityDrawingsBinding;

public class DrawingsActivity extends BaseActivity<ActivityDrawingsBinding> implements View.OnClickListener {

    private ArrayList<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    // image
//    private PhotoViewAttacher photoViewAttacher;
    private float initScale;
    private int currentScale = 2;

    // table
    private TableListAdapter tableListAdapter;
    private ArrayList<JSONObject> listTableData;

    // popup
    private enum CurrentTab {
        INPUT, PICTURE, RECODE, MEMO
    }

    private PictureListAdapter pictureListAdapter;
    private ArrayList<JSONObject> listPictureData;

    private int audioSource = MediaRecorder.AudioSource.MIC;
    private int sampleRate = 44100;
    private int channelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelCount, audioFormat);
    public AudioRecord audioRecord = null;
    public Thread recordThread = null;
    private boolean isRecoding = false;
    private RecodeListAdapter recodeListAdapter;
    private ArrayList<JSONObject> listRecodingData;

    // Dummy Data

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        // Spinner
        listCategory = new ArrayList<>();
        listArchitecture = new ArrayList<>();
        listResearch = new ArrayList<>();
        listFacility = new ArrayList<>();

        ArrayList<String> categoryList = getIntent().getStringArrayListExtra("category_list");
        ArrayList<String> architectureList = getIntent().getStringArrayListExtra("architecture_list");
        ArrayList<String> researchList = getIntent().getStringArrayListExtra("research_list");
        ArrayList<String> facilityList = getIntent().getStringArrayListExtra("facility_list");

        listCategory = categoryList;
        listArchitecture = architectureList;
        listResearch = researchList;
        listFacility = facilityList;

        category = getIntent().getStringExtra("category");
        architecture = getIntent().getStringExtra("architecture");
        research = getIntent().getStringExtra("research");
        facility = getIntent().getStringExtra("facility");

        initSpinner(dataBinding.spnCategory, listCategory, category);
        initSpinner(dataBinding.spnArchitecture, listArchitecture, architecture);
        initSpinner(dataBinding.spnResearch, listResearch, research);
        initSpinner(dataBinding.spnFacility, listFacility, facility);

        // 도면 이미지
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (dataBinding.imgDrawings.isReady()) {
                    PointF pin = dataBinding.imgDrawings.viewToSourceCoord(e.getX(), e.getY());
                    dataBinding.imgDrawings.setPin(pin);
                }
                return true;
            }
        });

        dataBinding.imgDrawings.setImage(ImageSource.asset("drawings_detail.png"));
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
                initScale = dataBinding.imgDrawings.getScale();
                dataBinding.imgDrawings.setMaxScale(12);
            }
        });

        // 집계표
        dataBinding.checkboxNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tableListAdapter.setAllChecked(isChecked);
            }
        });

        dataBinding.recyclerTable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        listTableData = new ArrayList<>();
        tableListAdapter = new TableListAdapter(this, listTableData);
        dataBinding.recyclerTable.setAdapter(tableListAdapter);

        requestTableDataList();

        // popup
        // 사진 탭
        dataBinding.researchPopup.recyclerPicture.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        listPictureData = new ArrayList<>();
        pictureListAdapter = new PictureListAdapter(this, listPictureData);
        dataBinding.researchPopup.recyclerPicture.setAdapter(pictureListAdapter);
        // 음성 탭
        dataBinding.researchPopup.recodeView.recyclerRecode.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        listRecodingData = new ArrayList<>();
        recodeListAdapter = new RecodeListAdapter(this, listRecodingData);
        dataBinding.researchPopup.recodeView.recyclerRecode.setAdapter(recodeListAdapter);

        recordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] readData = new byte[bufferSize];
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.pcm";
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(filePath);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                while (isRecoding) {
                    int ret = audioRecord.read(readData, 0, bufferSize);
                    Log.e("aaa", "read bytes is " + ret);

                    try {
                        fos.write(readData, 0, bufferSize);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;

                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 메모 탭
        initCanvas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
            case R.id.btn_close:
                finish();
                break;

            case R.id.btn_save:
                //TODO For Test
                showDrawingsSelectDialog();
                break;

            case R.id.btn_zoom_in:
                calculateScale(true);
                break;
            case R.id.btn_zoom_out:
                calculateScale(false);
                break;
//            case R.id.btn_new:
//                //for test
//                dataBinding.layoutResearchPopup.setVisibility(View.VISIBLE);
//                getPictureDataList();
//                break;

            case R.id.btn_table_handle:
                dataBinding.btnTableHandle.setImageDrawable(dataBinding.layoutTable.getVisibility() == View.VISIBLE ? getResources().getDrawable(R.drawable.ic_arrow_left) : getResources().getDrawable(R.drawable.ic_arrow_right));
                dataBinding.layoutTable.setVisibility(dataBinding.layoutTable.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
            case R.id.btn_popup_close:
                dataBinding.layoutResearchPopup.setVisibility(View.GONE);
                initPopupView();
                break;

            case R.id.layout_input_tab:
                setSelectedTab(CurrentTab.INPUT);
                break;

            case R.id.layout_picture_tab:
                setSelectedTab(CurrentTab.PICTURE);
                break;

            case R.id.layout_recode_tab:
                setSelectedTab(CurrentTab.RECODE);
                break;

            case R.id.layout_memo_tab:
                setSelectedTab(CurrentTab.MEMO);
                break;

            case R.id.layout_sub_title:
                dataBinding.researchPopup.layoutSubTitle.setBackground(getResources().getDrawable(R.drawable.background_pink_popup_menu));
                break;

            case R.id.layout_direction:
                dataBinding.researchPopup.layoutDirection.setBackground(getResources().getDrawable(R.drawable.background_pink_popup_menu));
                break;

            case R.id.layout_defect_content:
                dataBinding.researchPopup.layoutDefectContent.setBackground(getResources().getDrawable(R.drawable.background_pink_popup_menu));
                break;

            case R.id.layout_architecture:
                dataBinding.researchPopup.layoutArchitecture.setBackground(getResources().getDrawable(R.drawable.background_pink_popup_menu));
                break;

            case R.id.btn_reg:
                //TODO
                dataBinding.layoutResearchPopup.setVisibility(View.GONE);
                break;

            case R.id.btn_recode:
                dataBinding.researchPopup.recodeView.btnRecode.setVisibility(View.GONE);
                dataBinding.researchPopup.recodeView.layoutRecoding.setVisibility(View.VISIBLE);
                dataBinding.researchPopup.recodeView.recyclerRecode.setVisibility(View.GONE);
                dataBinding.researchPopup.recodeView.layoutRecodePlay.setVisibility(View.GONE);

                recode();
                break;

            case R.id.btn_recode_stop:
                if (isRecoding) {
                    dataBinding.researchPopup.recodeView.btnRecodeStop.setText(R.string.popup_reg_research_recode_save);
                } else {
                    dataBinding.researchPopup.recodeView.btnRecode.setVisibility(View.VISIBLE);
                    dataBinding.researchPopup.recodeView.layoutRecoding.setVisibility(View.GONE);
                    //TODO get recoded real data
                    getRecodeDataList();
                    dataBinding.researchPopup.recodeView.recyclerRecode.setVisibility(View.VISIBLE);
                }

                isRecoding = false;
                break;
        }
    }

    private void initSpinner(AppCompatSpinner spinner, ArrayList<String> list_data, String select_data) {
        spinner.setAdapter(new CustomSpinnerAdapter(this, R.layout.item_spinner, list_data));

        setSpinnerSelectedData(list_data, select_data, spinner);
    }

    private void setSpinnerSelectedData(ArrayList<String> list_data, String select_data, AppCompatSpinner spinner) {
        for (int i=0;i<list_data.size();i++) {
            String cate = list_data.get(i);
            if (cate.equals(select_data)) {
                spinner.setSelection(i);
            }
        }
    }

    // 도면 선택 다이얼로그 출력
    private void showDrawingsSelectDialog() {
        final DrawingsSelectDialog dialog = new DrawingsSelectDialog(this);
        dialog.selectInputListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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

    // 현재 줌 배수 출력
    private void showScaleView() {
        DecimalFormat df = new DecimalFormat("0.#");
        String scaleStr = "x" + df.format(currentScale / 2.0f) + "배";

        dataBinding.layoutScale.setVisibility(View.VISIBLE);
        dataBinding.txtScale.setText(scaleStr);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
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

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
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

    //TODO request table data list api
    private void requestTableDataList() {
        listTableData.clear();

        for (int i = 0;i < 5;i++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("number", i + 1);
                jsonObject.put("legend", "icon");
                jsonObject.put("content", "균열(수직균열)");
                jsonObject.put("value", "폭(10)");
                jsonObject.put("count", 3);
                listTableData.add(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        tableListAdapter.setList(listTableData);
        tableListAdapter.notifyDataSetChanged();
    }

    private void initPopupView() {
        setSelectedTab(CurrentTab.INPUT);
        dataBinding.researchPopup.layoutPictureList.setVisibility(View.GONE);
        dataBinding.researchPopup.recodeView.btnRecode.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.recodeView.layoutRecoding.setVisibility(View.GONE);
        dataBinding.researchPopup.recodeView.recyclerRecode.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.recodeView.layoutRecodePlay.setVisibility(View.GONE);
        dataBinding.researchPopup.memoView.layoutGuideContainer.setVisibility(View.VISIBLE);
        dataBinding.researchPopup.memoView.layoutCanvas.clear();
        dataBinding.researchPopup.memoView.txtGuide.setVisibility(View.VISIBLE);
    }

    // popup tab update isSelected
    private void setSelectedTab(CurrentTab tab) {
        dataBinding.researchPopup.layoutInputTab.setBackground(tab == CurrentTab.INPUT ? getDrawable(R.drawable.background_keycolor_tab) : getDrawable(R.drawable.background_gray_tab));
        dataBinding.researchPopup.txtInputTab.setTextColor(tab == CurrentTab.INPUT ? getResources().getColor(R.color.colorWhite) : getResources().getColor(R.color.colorTextGrayPopup));

        dataBinding.researchPopup.layoutPictureTab.setBackground(tab == CurrentTab.PICTURE ? getDrawable(R.drawable.background_keycolor_tab) : getDrawable(R.drawable.background_gray_tab));
        dataBinding.researchPopup.txtPictureTab.setTextColor(tab == CurrentTab.PICTURE ? getResources().getColor(R.color.colorWhite) : getResources().getColor(R.color.colorTextGrayPopup));

        dataBinding.researchPopup.layoutRecodeTab.setBackground(tab == CurrentTab.RECODE ? getDrawable(R.drawable.background_keycolor_tab) : getDrawable(R.drawable.background_gray_tab));
        dataBinding.researchPopup.txtRecodeTab.setTextColor(tab == CurrentTab.RECODE ? getResources().getColor(R.color.colorWhite) : getResources().getColor(R.color.colorTextGrayPopup));

        dataBinding.researchPopup.layoutMemoTab.setBackground(tab == CurrentTab.MEMO ? getDrawable(R.drawable.background_keycolor_tab) : getDrawable(R.drawable.background_gray_tab));
        dataBinding.researchPopup.txtMemoTab.setTextColor(tab == CurrentTab.MEMO ? getResources().getColor(R.color.colorWhite) : getResources().getColor(R.color.colorTextGrayPopup));

        dataBinding.researchPopup.layoutInput.setVisibility(tab == CurrentTab.INPUT ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutInputPicture.setVisibility((tab == CurrentTab.INPUT || tab == CurrentTab.PICTURE) ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutRecode.setVisibility(tab == CurrentTab.RECODE ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutMemo.setVisibility(tab == CurrentTab.MEMO ? View.VISIBLE : View.GONE);
    }

    //TODO get picture data
    private void getPictureDataList() {
        listPictureData.clear();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("img_url", "https://www.dropbox.com/s/puuidymupu4tmon/sample_img.png?dl=0");
            listPictureData.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pictureListAdapter.setList(listPictureData);
        pictureListAdapter.notifyDataSetChanged();

        dataBinding.researchPopup.layoutPictureList.setVisibility(View.VISIBLE);
    }

    private void recode() {
        if (isRecoding) {
            isRecoding = false;
            dataBinding.researchPopup.recodeView.btnRecodeStop.setText(R.string.popup_reg_research_recode_save);
        } else {
            isRecoding = true;

            if (audioRecord == null) {
                audioRecord = new AudioRecord(audioSource, sampleRate, channelCount, audioFormat, bufferSize);
                audioRecord.startRecording();
            }
            recordThread.start();
        }
    }

    //TODO get recoding data
    private void getRecodeDataList() {
        listRecodingData.clear();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "11 음성녹음-1");
            jsonObject.put("time", "03:20");
            listRecodingData.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recodeListAdapter.setList(listRecodingData);
        recodeListAdapter.notifyDataSetChanged();
    }

    public void startRecodePlay() {
        dataBinding.researchPopup.recodeView.recyclerRecode.setVisibility(View.GONE);
        dataBinding.researchPopup.recodeView.layoutRecodePlay.setVisibility(View.VISIBLE);
    }

    private void initCanvas() {
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
}
