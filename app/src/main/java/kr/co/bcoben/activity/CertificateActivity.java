package kr.co.bcoben.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.R;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class CertificateActivity extends AppCompatActivity implements View.OnClickListener {

    private static int AUTH_RESTRICT_TIME = 300;
    private int mIntAuthTimerCount = AUTH_RESTRICT_TIME;

    EditText edtNumber;
    TextView txtTime;

    Timer mTimerAuth = null;
    Boolean mIsAuth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        edtNumber = findViewById(R.id.edt_certificate_number);
        txtTime = findViewById(R.id.txt_remain_time);
        Button btnSend = findViewById(R.id.btn_send);
        Button btnBack = findViewById(R.id.btn_back);
        Button btnComplete = findViewById(R.id.btn_complete);

        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnComplete.setOnClickListener(this);

        setAuthTime();

        // 인증 시간 타이머
        mTimerAuth = new Timer();
        mTimerAuth.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
            }
        }, 1000, 1000);
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

    /**
     * 인증 시간 타이머의 핸들러이다.
     * 타이머에서 1초에 한번씩 핸들러에 메세지를 보내면,
     * mIntAuthTimerCount를 하나씩 감소시킨다(이 변수의 값이 초 역활을 한다)
     * 위의 변수가 0보다 클 때는 시간을 감소 시키며 보여주고
     * 인증을 막기 위한 플래그를 설정한 후, Timer를 종료 시킨다.
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mIntAuthTimerCount--;

            if (mIntAuthTimerCount >= 0) {
                setAuthTime();
            } else {
                mIsAuth = false;
//                resetAuth();
            }
        }
    };

    private void setAuthTime() {
        int min = mIntAuthTimerCount / 60;
        int sec = mIntAuthTimerCount % 60;

        String time;
        if (sec < 10) {
            time = String.format("0%s:0%s", min, sec);
        } else {
            time = String.format("0%s:%s", min, sec);
        }

        txtTime.setText(time);
    }

    private void resetAuth() {
        if (mTimerAuth != null) {
            mTimerAuth.cancel();
            mTimerAuth = null;
            mIntAuthTimerCount = AUTH_RESTRICT_TIME;

            setAuthTime();
        }
    }
}
