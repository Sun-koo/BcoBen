package kr.co.bcoben.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.R;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class CertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private static int AUTH_RESTRICT_TIME = 300;
    private int authTimerCount = AUTH_RESTRICT_TIME;

    private EditText edtNumber;
    private TextView txtTime;

    private Timer authTimer;
    private boolean isAuth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        edtNumber = findViewById(R.id.edt_certificate_number);
        txtTime = findViewById(R.id.txt_remain_time);

        // 인증 시간 타이머
        authTimer = new Timer();
        authTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                authTimerCount--;
                if (authTimerCount >= 0) {
                    setAuthTime();
                } else {
                    isAuth = false;
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                break;

            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_complete:
                String number = edtNumber.getText().toString();

                if (number.isEmpty()) {
                    showToast(R.string.toast_input_certificate_number);
                    return;
                }

                //TODO request confirm certificate number api
                Intent intent = new Intent(CertificateActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                break;
        }
    }

    private void setAuthTime() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int min = authTimerCount / 60;
                int sec = authTimerCount % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", min, sec);

                txtTime.setText(time);
            }
        });
    }

    private void resetAuth() {
        if (authTimer != null) {
            authTimer.cancel();
            authTimer = null;
            authTimerCount = AUTH_RESTRICT_TIME;

            setAuthTime();
        }
    }
}
