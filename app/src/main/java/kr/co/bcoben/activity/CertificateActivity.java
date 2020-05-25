package kr.co.bcoben.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import java.util.Locale;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityCertificateBinding;

import static kr.co.bcoben.util.CommonUtil.showToast;

public class CertificateActivity extends BaseActivity<ActivityCertificateBinding> implements View.OnClickListener {

    private static int AUTH_RESTRICT_TIME = 180;
    private int authTimerCount = AUTH_RESTRICT_TIME;

    private Handler authHandler = new Handler();
    private Runnable runnable;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_certificate;
    }

    @Override
    protected void initView() {
        // 인증 시간 타이머
        runnable = new Runnable() {
            @Override
            public void run() {
                authTimerCount--;
                if (authTimerCount > 0) {
                    int min = authTimerCount / 60;
                    int sec = authTimerCount % 60;
                    String time = getString(R.string.certificate_remain_time, String.format(Locale.getDefault(), "%02d:%02d", min, sec));

                    dataBinding.txtRemainTime.setText(time);

                    authHandler.postDelayed(runnable, 1000);
                } else {
                    dataBinding.txtRemainTime.setText(R.string.certificate_remain_time_over);
                }
            }
        };
        startAuth();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (authTimerCount == 0) {
                    startAuth();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_complete:
                String number = dataBinding.editCertificateNumber.getText().toString();

                if (checkValidInput(number)) {
                    Intent intent = new Intent(CertificateActivity.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_finish_in, R.anim.activity_finish_out);
    }

    private void startAuth() {
        authTimerCount = AUTH_RESTRICT_TIME;
        authHandler.post(runnable);
    }

    private boolean checkValidInput(String number) {
        if (authTimerCount == 0) {
            showToast(R.string.toast_time_over_certificate_number);
            return false;
        }
        if (number.isEmpty()) {
            showToast(R.string.toast_input_certificate_number);
            return false;
        }
        if (number.length() < 6) {
            showToast(R.string.toast_input_certificate_number_length);
            return false;
        }
        return true;
    }
}
