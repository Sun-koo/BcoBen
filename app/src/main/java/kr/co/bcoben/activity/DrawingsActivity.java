package kr.co.bcoben.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.PictureListAdapter;
import kr.co.bcoben.adapter.RecodeListAdapter;
import kr.co.bcoben.adapter.TableListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.CanvasView;
import kr.co.bcoben.component.DrawingsSelectDialog;
import kr.co.bcoben.databinding.ActivityDrawingsBinding;

import static kr.co.bcoben.util.CommonUtil.showKeyboard;

public class DrawingsActivity extends BaseActivity<ActivityDrawingsBinding> implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ArrayList<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    // image
    private PhotoViewAttacher photoViewAttacher;
    private float scale;

    // table
    private TableListAdapter tableListAdapter;
    private ArrayList<JSONObject> listTableData;

    // popup
    private enum CurrentTab {
        INPUT, PICTURE, RECODE, MEMO
    }

    private PictureListAdapter pictureListAdapter;
    private ArrayList<JSONObject> listPictureData;

    short[] bufferRecord, bufferStore;
    int bufferRecordSize, bufferTrackSize;
    int samplingRate = 44100;
    int bufferShortSize = samplingRate * 6;
    ShortBuffer shortBuffer = ShortBuffer.allocate(bufferShortSize);
    AudioTrack audioTrack;
    AudioRecord audioRecord;
    private boolean isRecoding = false;
    private RecodeListAdapter recodeListAdapter;
    private ArrayList<JSONObject> listRecodingData;

    private CanvasView canvasView;
    private GestureDetectorCompat mDetector;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings;
    }

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

        initSpinner(dataBinding.spCategory, listCategory, category);
        initSpinner(dataBinding.spArchitecture, listArchitecture, architecture);
        initSpinner(dataBinding.spResearch, listResearch, research);
        initSpinner(dataBinding.spFacility, listFacility, facility);

        // 도면 이미지
        photoViewAttacher = new PhotoViewAttacher(dataBinding.ivDrawings);
        photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);
        photoViewAttacher.setMaximumScale(6);

        photoViewAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                dataBinding.layoutTable.setVisibility(View.VISIBLE);
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
        // 메모 탭
        mDetector = new GestureDetectorCompat(this, new TapAndDragGestureListener());
        dataBinding.researchPopup.memoView.layoutGuideContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return onTouchEvent(event);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home: case R.id.btn_close:
                finish();
                break;

            case R.id.btn_save:
                //TODO For Test
                showDrawingsSelectDialog();
                break;

            case R.id.btn_zoom_in:
                scale = calculateScale(photoViewAttacher.getScale());
                scale = (scale + 0.5f) > 6 ? 6f : scale + 0.5f;
                photoViewAttacher.setScale(scale);

                showScaleView();
                break;

            case R.id.btn_zoom_out:
                scale = calculateScale(photoViewAttacher.getScale());
                scale = (scale - 0.5f) < 1 ? 1f : scale - 0.5f;
                photoViewAttacher.setScale(scale);

                showScaleView();
                break;

            case R.id.btn_new:
                //for test
                dataBinding.layoutResearchPopup.setVisibility(View.VISIBLE);
                getPictureDataList();
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

                startRecoding();
                break;

            case R.id.btn_recode_stop:
                if (isRecoding) {
                    stopRecoding();
                    dataBinding.researchPopup.recodeView.btnRecodeStop.setText(R.string.popup_reg_research_recode_save);
                } else {
                    dataBinding.researchPopup.recodeView.btnRecode.setVisibility(View.VISIBLE);
                    dataBinding.researchPopup.recodeView.layoutRecoding.setVisibility(View.GONE);
                    //TODO get recoded real data
                    getRecodeDataList();
                    dataBinding.researchPopup.recodeView.recyclerRecode.setVisibility(View.VISIBLE);
                }

                isRecoding = !isRecoding;
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("aaa", "position: " + position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.e("aaa", "not selected!");
    }

    private void initSpinner(Spinner spinner, ArrayList<String> list_data, String select_data) {
        spinner.setAdapter(new CategorySpinnerAdapter(this, R.layout.item_spinner, R.id.txt_list, list_data));

        spinner.setOnItemSelectedListener(this);

        setSpinnerSelectedData(list_data, select_data, spinner);
    }

    private void setSpinnerSelectedData(ArrayList<String> list_data, String select_data, Spinner spinner) {
        for (int i=0;i<list_data.size();i++) {
            String cate = list_data.get(i);
            if (cate.equals(select_data)) {
                spinner.setSelection(i);
            }
        }
    }

    public class CategorySpinnerAdapter extends ArrayAdapter<String> {

        List<String> list;

        CategorySpinnerAdapter(Context context, int layout_resource_id, int tv_resource_id, List tv_list) {
            super(context, layout_resource_id, tv_resource_id, tv_list);
            list = tv_list;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_spinner, parent, false);

            TextView txtSelect = row.findViewById(R.id.txt_list);
            txtSelect.setText(list.get(position));

            return row;
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
    //TODO Pinch Zoom 으로 바뀐 Scale을 0.5단위로 맞추기 위해 구현
    private float calculateScale(float scale) {
        if (scale > 1f && scale < 1.5f) {
            return 1.5f;
        } else if (scale > 1.5f && scale < 2f) {
            return 2f;
        } else if (scale > 2f && scale < 2.5f) {
            return 2.5f;
        } else if (scale > 2.5f && scale < 3f) {
            return 3f;
        } else if (scale > 3f && scale < 3.5f) {
            return 3.5f;
        } else if (scale > 3.5f && scale < 4f) {
            return 4f;
        } else if (scale > 4f && scale < 4.5f) {
            return 4.5f;
        } else if (scale > 4.5f && scale < 5f) {
            return 5f;
        } else if (scale > 5f && scale < 5.5f) {
            return 5.5f;
        } else if (scale > 5.5f && scale < 6f) {
            return 6f;
        } else {
            return scale;
        }
    }

    // 현재 줌 배수 출력
    private void showScaleView() {
        dataBinding.layoutScale.setVisibility(View.VISIBLE);
        dataBinding.txtScale.setText("x"+ scale +"배");

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dataBinding.layoutScale.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dataBinding.layoutScale.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
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
        dataBinding.researchPopup.memoView.editMemo.setVisibility(View.GONE);
        dataBinding.researchPopup.memoView.editMemo.getText().clear();
        dataBinding.researchPopup.memoView.layoutCanvas.clear();
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

    private void startRecoding() {
        isRecoding = true;
        bufferStore = new short[samplingRate];
        bufferTrackSize = bufferStore.length;
        // 녹음용 버퍼 사이즈 구하기 및 버퍼 초기화
        bufferRecordSize = AudioRecord.getMinBufferSize(samplingRate,
        AudioFormat.CHANNEL_IN_STEREO,
        AudioFormat.ENCODING_PCM_16BIT) * 2;
        bufferRecord = new short[bufferRecordSize];

        //AudioRecord 초기화
        audioRecord=null;
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                samplingRate,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,bufferRecord.length);

        //AudioTrack 초기화
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                samplingRate,
//                AudioFormat.CHANNEL_IN_STEREO,
//                AudioFormat.ENCODING_PCM_16BIT,
//                bufferStore.length,
//                AudioTrack.MODE_STREAM);

//        audioTrack.play();
        audioRecord.startRecording();

        //녹음버퍼의 내용을 저장 버퍼로 옮김
        int retBufferSize;
        shortBuffer.rewind();

        while(shortBuffer.position() + bufferRecordSize < bufferShortSize){
            retBufferSize = audioRecord.read(bufferRecord, 0, bufferRecordSize);
            shortBuffer.put(bufferRecord,0,retBufferSize);
        }
    }
    private void stopRecoding() {
        int Index=0;
        shortBuffer.position(0);

        while(shortBuffer.position() <= bufferShortSize - bufferTrackSize){
            shortBuffer.get(bufferStore, 0, bufferTrackSize);
//            audioTrack.write(bufferStore, 0, bufferTrackSize);
            Index=Index + bufferTrackSize;
        }

        audioRecord.stop();
        audioRecord.release();
//        audioTrack.stop();
//        audioTrack.release();
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

    private void updateMemoView(boolean isDrawing) {
        dataBinding.researchPopup.memoView.layoutGuideContainer.setVisibility(View.GONE);

        if (isDrawing) {
            dataBinding.researchPopup.memoView.layoutCanvas.setMode(CanvasView.Mode.DRAW);
            dataBinding.researchPopup.memoView.layoutCanvas.setDrawer(CanvasView.Drawer.PEN);
            dataBinding.researchPopup.memoView.layoutCanvas.setBaseColor(getResources().getColor(R.color.colorWhite));
            dataBinding.researchPopup.memoView.layoutCanvas.setPaintStyle(Paint.Style.STROKE);
            dataBinding.researchPopup.memoView.layoutCanvas.setPaintStrokeColor(getResources().getColor(R.color.colorBlack));
            dataBinding.researchPopup.memoView.layoutCanvas.setPaintStrokeWidth(8);

        } else {
            dataBinding.researchPopup.memoView.editMemo.setVisibility(View.VISIBLE);
            showKeyboard(this, dataBinding.researchPopup.memoView.editMemo);
        }
    }
    // 메모 영역 화면 탭 했을 경우와 드로잉 제스쳐를 구분하기 위해 리스너 추가
    class TapAndDragGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            updateMemoView(false);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            updateMemoView(true);
            return true;
        }
    }
}
