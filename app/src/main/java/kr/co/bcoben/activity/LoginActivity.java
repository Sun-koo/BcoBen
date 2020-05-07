package kr.co.bcoben.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import kr.co.bcoben.R;

import static kr.co.bcoben.util.CommonUtil.finishApp;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onBackPressed() {
        finishApp(this);
    }
}
