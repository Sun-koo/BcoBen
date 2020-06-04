package kr.co.bcoben.activity;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.R;
import kr.co.bcoben.adapter.CustomSpinnerAdapter;
import kr.co.bcoben.adapter.DrawingPictureListAdapter;
import kr.co.bcoben.adapter.PictureListAdapter;
import kr.co.bcoben.adapter.RecodeListAdapter;
import kr.co.bcoben.adapter.TableListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CanvasView;
import kr.co.bcoben.component.DrawingsSelectDialog;
import kr.co.bcoben.databinding.ActivityDrawingsBinding;
import kr.co.bcoben.model.DrawingPointData;

import static kr.co.bcoben.model.DrawingPointData.DrawingType;
import static kr.co.bcoben.util.CommonUtil.dpToPx;
import static kr.co.bcoben.util.CommonUtil.showToast;

public class DrawingsActivity extends BaseActivity<ActivityDrawingsBinding> implements View.OnClickListener {

    private List<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    // image
    private float initScale;
    private int currentScale = 2;
    private List<DrawingPointData> pinList = new ArrayList<>();
    private DrawingPointData regPointData;
    private DrawingPictureListAdapter drawingPictureListAdapter;

    // table
    private TableListAdapter tableListAdapter;
    private ArrayList<JSONObject> listTableData;

    // popup
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
    private int[][] pinPointArray = {{1200, 500}, {1300, 700}, {1000, 1500}, {2000, 1800}, {500, 1200}, {2300, 1800}};
    private DrawingType[] pinTypeArray = {DrawingType.NORMAL, DrawingType.NORMAL, DrawingType.IMAGE, DrawingType.VOICE, DrawingType.VOICE, DrawingType.MEMO};
    private String[] urlArray = {
            "https://cdn.pixabay.com/photo/2019/08/19/10/37/stone-4416019_1280.jpg",
            "https://cdn.pixabay.com/photo/2013/09/22/19/15/brick-wall-185086_1280.jpg",
            "https://cdn.pixabay.com/photo/2020/05/22/07/59/girl-5204296_960_720.jpg"
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings;
    }

    @Override
    protected void initView() {
        // Spinner
        listCategory = getIntent().getStringArrayListExtra("category_list");
        listArchitecture = getIntent().getStringArrayListExtra("architecture_list");
        listResearch = getIntent().getStringArrayListExtra("research_list");
        listFacility = getIntent().getStringArrayListExtra("facility_list");
        category = getIntent().getStringExtra("category");
        architecture = getIntent().getStringExtra("architecture");
        research = getIntent().getStringExtra("research");
        facility = getIntent().getStringExtra("facility");

        initSpinner(dataBinding.spnCategory, listCategory, category);
        initSpinner(dataBinding.spnArchitecture, listArchitecture, architecture);
        initSpinner(dataBinding.spnResearch, listResearch, research);
        initSpinner(dataBinding.spnFacility, listFacility, facility);

        initDrawing();

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

    // 도면 이미지 Initialize
    @SuppressLint("ClickableViewAccessibility")
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
                        dataBinding.txtNewPin.setText(df.format(pinList.size() + 1));

                        initPopupView();
                        getPictureDataList();
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
                        int width = drawingPictureListAdapter.setList(pinList.get(index).getRegImageList());

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
        dataBinding.imgDrawings.setImage(ImageSource.asset("drawings_detail.png"));
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
                requestDrawingPointDataList();
            }
        });

        // 핀에 등록된 이미지 리스트 팝업
        drawingPictureListAdapter = new DrawingPictureListAdapter(this);
        dataBinding.recyclerPicturePopup.setAdapter(drawingPictureListAdapter);
        dataBinding.recyclerPicturePopup.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

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
        switch (v.getId()) {
            case R.id.btn_home:
            case R.id.btn_close:
                finish();
                break;

            case R.id.btn_save:
                //TODO For Test
                showDrawingsSelectDialog();
                break;
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
            case R.id.btn_popup_close:
                dataBinding.layoutResearchPopup.setVisibility(View.INVISIBLE);
                break;
            case R.id.txt_input_tab:
                setSelectedTab(DrawingType.NORMAL);
                break;
            case R.id.txt_picture_tab:
                setSelectedTab(DrawingType.IMAGE);
                break;
            case R.id.txt_recode_tab:
                setSelectedTab(DrawingType.VOICE);
                break;
            case R.id.txt_memo_tab:
                setSelectedTab(DrawingType.MEMO);
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
                dataBinding.imgDrawings.addPin(regPointData);
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

    private void initSpinner(AppCompatSpinner spinner, List<String> list, String selectData) {
        spinner.setAdapter(new CustomSpinnerAdapter(this, R.layout.item_spinner, list));

        for (int i = 0; i < list.size(); i++) {
            String cate = list.get(i);
            if (cate.equals(selectData)) {
                spinner.setSelection(i);
                break;
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

    //TODO request table data list api
    private void requestDrawingPointDataList() {
        pinList.clear();
        for (int i = 0; i < pinPointArray.length; i++) {
            final DrawingPointData data = new DrawingPointData(new PointF(pinPointArray[i][0], pinPointArray[i][1]), pinTypeArray[i]);
            if (pinTypeArray[i] == DrawingType.NORMAL || pinTypeArray[i] == DrawingType.IMAGE) {
                int count = i % 3 + 1;
                final List<String> regUrlList = new ArrayList<>(Arrays.asList(urlArray));
                regUrlList.subList(count, regUrlList.size()).clear();

                final List<Bitmap> regImageList = new ArrayList<>();
                for (int j = 0; j < regUrlList.size(); j++) {
                    final int index = j;
                    Glide.with(AppApplication.getContext()).asBitmap().load(regUrlList.get(j)).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            regImageList.add(bitmap);
                            if (regImageList.size() == regUrlList.size()) {
                                data.setRegImageList(regImageList);
                                dataBinding.imgDrawings.invalidate();
                            }
                        }
                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {}
                    });
                }
            }
            pinList.add(data);
        }
        dataBinding.imgDrawings.setPinList(pinList);
    }

    private void initPopupView() {
        setSelectedTab(DrawingType.NORMAL);
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
    private void setSelectedTab(DrawingType type) {
        dataBinding.researchPopup.txtInputTab.setSelected(false);
        dataBinding.researchPopup.txtPictureTab.setSelected(false);
        dataBinding.researchPopup.txtRecodeTab.setSelected(false);
        dataBinding.researchPopup.txtMemoTab.setSelected(false);

        switch (type) {
            case NORMAL: dataBinding.researchPopup.txtInputTab.setSelected(true); break;
            case IMAGE: dataBinding.researchPopup.txtPictureTab.setSelected(true); break;
            case VOICE: dataBinding.researchPopup.txtRecodeTab.setSelected(true); break;
            case MEMO: dataBinding.researchPopup.txtMemoTab.setSelected(true); break;
        }

        dataBinding.researchPopup.layoutInput.setVisibility(type == DrawingType.NORMAL ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutInputPicture.setVisibility((type == DrawingType.NORMAL || type == DrawingType.IMAGE) ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutRecode.setVisibility(type == DrawingType.VOICE ? View.VISIBLE : View.GONE);
        dataBinding.researchPopup.layoutMemo.setVisibility(type == DrawingType.MEMO ? View.VISIBLE : View.GONE);

        regPointData.setType(type);
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

    @SuppressLint("ClickableViewAccessibility")
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
