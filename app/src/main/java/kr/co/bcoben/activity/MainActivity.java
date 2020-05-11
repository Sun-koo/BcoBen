package kr.co.bcoben.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.ProjectListAdapter;

import static kr.co.bcoben.util.CommonUtil.finishApp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvProject;
    private ProjectListAdapter adapter;
    private ArrayList<JSONObject> listProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton ibHome = findViewById(R.id.ib_home);
        ImageButton ibAdd = findViewById(R.id.ib_add);
        TextView txtLogout = findViewById(R.id.txt_logout);
        TextView txtVersion = findViewById(R.id.txt_version);
        Button btnUpdate = findViewById(R.id.btn_update);

        TextView txtSubTitle = findViewById(R.id.txt_sub_title);
        RelativeLayout regLayout = findViewById(R.id.regist_layout);

        ibHome.setOnClickListener(this);
        ibAdd.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        regLayout.setOnClickListener(this);

        rvProject = findViewById(R.id.rv_project);
        rvProject.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        listProject = new ArrayList<>();
        adapter = new ProjectListAdapter(this, listProject);
        rvProject.setAdapter(adapter);

        requestProjectList();
    }

    @Override
    public void onBackPressed() {
        finishApp(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_home:
                break;

            case R.id.ib_add:
                break;

            case R.id.txt_logout:
                Intent intent_logout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent_logout);
                finish();
                break;

            case R.id.btn_update:
                break;

            case R.id.regist_layout:
                break;
        }
    }

    //TODO request project list api
    private void requestProjectList() {
        //TODO add dummy data for test
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "서초구청");
            listProject.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "성수초등학교");
            listProject.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "프로젝트명");
            listProject.add(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setList(listProject);
        adapter.notifyDataSetChanged();
    }
}
