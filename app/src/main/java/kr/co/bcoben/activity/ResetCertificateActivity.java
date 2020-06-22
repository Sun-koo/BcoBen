package kr.co.bcoben.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import java.util.Locale;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityResetCertificateBinding;
import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallback;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;

import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.phoneCheck;
import static kr.co.bcoben.util.ValidateUtil.stringPatternCheck;

public class ResetCertificateActivity extends BaseActivity<ActivityResetCertificateBinding> implements View.OnClickListener {

    private static int AUTH_RESTRICT_TIME = 180;
    private int authTimerCount = AUTH_RESTRICT_TIME;

    private Handler authHandler = new Handler();
    private Runnable runnable;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_reset_certificate;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String id = dataBinding.editId.getText().toString();
                String phone = dataBinding.editPhone.getText().toString();

                if (checkValidInputInfo(id, phone)) {
                    setIsLoading(true);
                    RetrofitClient.getRetrofitApi().resetPassword(id, phone, UserData.getInstance().getDeviceId()).enqueue(new RetrofitCallbackModel<LoginData>() {
                        @Override
                        public void onResponseData(LoginData data) {
                            dataBinding.editCertificateNumber.setText(data.getAuth_no());

                            UserData.getInstance().setUserId(data.getUser_id());
                            showToast(R.string.toast_send_number);
                            dataBinding.txtRemainTime.setVisibility(View.VISIBLE);
                            authHandler.removeCallbacks(runnable);
                            startAuth();
                        }
                        @Override
                        public void onCallbackFinish() { setIsLoading(false); }
                    });
                }
                break;

            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_complete:
                String number = dataBinding.editCertificateNumber.getText().toString();

                if (checkValidInputCertificate(number)) {
                    setIsLoading(true);
                    RetrofitClient.getRetrofitApi().checkAuth(UserData.getInstance().getUserId(), "reset", number).enqueue(new RetrofitCallback() {
                        @Override
                        public void onResponseData() {
                            Intent intent = new Intent(ResetCertificateActivity.this, ResetPwActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            overridePendingTransition(R.anim.activity_start_in, R.anim.activity_start_out);
                        }
                        @Override
                        public void onCallbackFinish() { setIsLoading(false); }
                    });
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

    private boolean checkValidInputCertificate(String number) {
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

    // 아이디, 휴대전화번호 유효성 검사
    private boolean checkValidInputInfo(String id, String phone) {
        if (id.isEmpty()) {
            showToast(R.string.toast_input_id);
            return false;
        }
        if (phone.isEmpty()) {
            showToast(R.string.toast_input_phone);
            return false;
        }
        if (!stringPatternCheck(id, ALPHA_NUM, 6, -1)) {
            showToast(R.string.toast_invalid_id);
            return false;
        }
        if (!phoneCheck(phone)) {
            showToast(R.string.toast_invalid_phone);
            return false;
        }
        return true;
    }
}
