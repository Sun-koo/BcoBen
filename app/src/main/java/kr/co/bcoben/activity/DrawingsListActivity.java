package kr.co.bcoben.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.DrawingsListAdapter;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityDrawingsListBinding;
import kr.co.bcoben.ftp.ConnectFTP;
import kr.co.bcoben.model.DrawingsListData;

import static kr.co.bcoben.util.CommonUtil.getFilePath;

public class DrawingsListActivity extends BaseActivity<ActivityDrawingsListBinding> implements View.OnClickListener {

    static boolean isHomeReturn = false;
    private List<String> listCategory, listArchitecture, listResearch, listFacility;
    private String category, architecture, research, facility;

    private DrawingsListAdapter drawingsListAdapter;
    private ArrayList<DrawingsListData> listDrawings;
    final List<String> imgList = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_drawings_list;
    }

    @Override
    protected void initView() {
        listCategory = getIntent().getStringArrayListExtra("category_list");
        listArchitecture = getIntent().getStringArrayListExtra("architecture_list");
        listResearch = getIntent().getStringArrayListExtra("research_list");
        listFacility = getIntent().getStringArrayListExtra("facility_list");
        category = getIntent().getStringExtra("category");
        architecture = getIntent().getStringExtra("architecture");
        research = getIntent().getStringExtra("research");
        facility = getIntent().getStringExtra("facility");

        dataBinding.spnCategory.setEnabled(false);
        dataBinding.spnArchitecture.setEnabled(false);
        initSpinner(dataBinding.spnCategory, listCategory, category);
        initSpinner(dataBinding.spnArchitecture, listArchitecture, architecture);
        initSpinner(dataBinding.spnResearch, listResearch, research);
//        initSpinner(dataBinding.spnFacility, listFacility, facility);

        dataBinding.recyclerDrawings.setLayoutManager(new GridLayoutManager(getApplicationContext(), 5));
        listDrawings = new ArrayList<>();
        drawingsListAdapter = new DrawingsListAdapter(DrawingsListActivity.this, listDrawings);
        dataBinding.recyclerDrawings.setAdapter(drawingsListAdapter);

        requestDrawingsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isHomeReturn) {
            finish();
        }
        isHomeReturn = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectFTP ftp = ConnectFTP.getInstance();
        ftp.ftpDisconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_download_all:
                downloadAll();
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
        listDrawings.clear();
        //TODO request api (add img file path to imgList)
        for (int i = 0;i < 10;i++) {
            imgList.add("/data/bcoben/1/App Upload/drawings_detail.png");
        }

        for (int i = 0;i < imgList.size();i++) {
            String[] strArr = imgList.get(i).split("/bcoben/");
            String lastPath = strArr[1].substring(strArr[1].indexOf("/"));
            String desFilePath = getFilePath().getAbsolutePath() + lastPath;

            DrawingsListData data = new DrawingsListData("도면명", null, desFilePath);
            listDrawings.add(data);
        }

        final ConnectFTP ftp = ConnectFTP.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean connect = ftp.ftpConnect();

                if (connect) {
                    List<Bitmap> bitmapList = ftp.ftpImageFile(imgList);
                    for (int i = 0; i < listDrawings.size(); i++) {
                        listDrawings.get(i).setBitmap(bitmapList.get(i));
                    }

                    drawingsListAdapter.setList(listDrawings);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawingsListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();

        drawingsListAdapter.setList(listDrawings);
        drawingsListAdapter.notifyDataSetChanged();
    }

    private void downloadAll() {
        final ConnectFTP ftp = ConnectFTP.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> filePathList = ftp.ftpDownloadFileList(imgList);
                for (int i = 0; i < listDrawings.size(); i++) {
                    listDrawings.get(i).setFilePath(filePathList.get(i));
                }
                drawingsListAdapter.setList(listDrawings);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawingsListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        drawingsListAdapter.setList(listDrawings);
        drawingsListAdapter.notifyDataSetChanged();
    }

    public void downloadFile(final int position) {
        final ConnectFTP ftp = ConnectFTP.getInstance();
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(getFilePath().getAbsolutePath());
                file.mkdirs();
                try {
                    file = new File(listDrawings.get(position).getFilePath());
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ftp.ftpDownloadFile(imgList.get(position), listDrawings.get(position).getFilePath());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        drawingsListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        drawingsListAdapter.notifyDataSetChanged();
    }

    public void sendSpinnerData() {
        Intent intent = new Intent(DrawingsListActivity.this, DrawingsActivity.class);
        intent.putStringArrayListExtra("category_list", (ArrayList<String>) listCategory);
        intent.putStringArrayListExtra("architecture_list", (ArrayList<String>) listArchitecture);
        intent.putStringArrayListExtra("research_list", (ArrayList<String>) listResearch);
        intent.putStringArrayListExtra("facility_list", (ArrayList<String>) listFacility);
        intent.putExtra("category", category);
        intent.putExtra("architecture", architecture);
        intent.putExtra("research", research);
        intent.putExtra("facility", facility);
        startActivity(intent);
    }
}
