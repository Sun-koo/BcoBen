package kr.co.bcoben.activity;

import android.content.Intent;
import android.view.View;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityCertificateBinding;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class CertificateActivity extends BaseActivity<ActivityCertificateBinding> implements View.OnClickListener {

    private static int AUTH_RESTRICT_TIME = 300;
    private int authTimerCount = AUTH_RESTRICT_TIME;

    private Timer authTimer;
    private boolean isAuth = true;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_certificate;
    }

    @Override
    protected void initView() {
        dataBinding.setActivity(this);

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
                String number = dataBinding.editCertificateNumber.getText().toString();

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

                dataBinding.txtRemainTime.setText(time);
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
