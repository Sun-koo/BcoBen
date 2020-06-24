package kr.co.bcoben.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import java.util.Locale;

import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.PermissionDialog;
import kr.co.bcoben.databinding.ActivityResetCertificateBinding;
import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallback;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.CommonUtil;

import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.phoneCheck;
import static kr.co.bcoben.util.ValidateUtil.stringPatternCheck;

public class ResetCertificateActivity extends BaseActivity<ActivityResetCertificateBinding> implements View.OnClickListener {

    private final String[] PERMISSION = {Manifest.permission.READ_PHONE_STATE};
    private String deviceId;

    private static int AUTH_RESTRICT_TIME = 180;
    private int authTimerCount = 0;

    private Handler authHandler = new Handler();
    private Runnable runnable;
    private String id = "";
    private String phone = "";

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

        dataBinding.editPhone.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final CommonUtil.PermissionState state = resultRequestPermission(this, permissions, grantResults);

        if (state == CommonUtil.PermissionState.GRANT) {
            getDeviceId();
        } else {
            PermissionDialog.builder(this)
                    .setTxtPermissionContent(R.string.dialog_permission_content_reset_phone)
                    .setBtnConfirmListener(new PermissionDialog.BtnClickListener() {
                        @Override
                        public void onClick(PermissionDialog dialog) {
                            if (state == CommonUtil.PermissionState.ALWAYS_DENY) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivityForResult(intent, 123);
                            } else if (state == CommonUtil.PermissionState.DENY) {
                                requestPermission(activity, PERMISSION);
                            }
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String id = dataBinding.editId.getText().toString().trim();
                String phone = dataBinding.editPhone.getText().toString().trim();

                if ((this.id.equals("") && this.phone.equals("")) || !id.equals(this.id) || !phone.equals(this.phone)) {
                    this.id = id;
                    this.phone = phone;

                    if (checkValidInputInfo(id, phone)) {
                        startLoading();
                        RetrofitClient.getRetrofitApi().resetPassword(id, phone, UserData.getInstance().getDeviceId()).enqueue(new RetrofitCallbackModel<LoginData>() {
                            @Override
                            public void onResponseData(LoginData data) {
                                dataBinding.editCertificateNumber.setEnabled(true);
                                dataBinding.btnComplete.setEnabled(true);
                                dataBinding.editCertificateNumber.setText(data.getAuth_no());
                                dataBinding.editCertificateNumber.requestFocus();

                                UserData.getInstance().setUserId(data.getUser_id());
                                showToast(R.string.toast_send_number);
                                dataBinding.txtRemainTime.setVisibility(View.VISIBLE);

                                authHandler.removeCallbacks(runnable);
                                startAuth();
                                endLoading();
                            }
                            @Override
                            public void onCallbackFinish() { endLoading(); }
                        });
                    }
                } else {
                    startLoading();
                    RetrofitClient.getRetrofitApi().sendAuth(UserData.getInstance().getUserId(), "reset").enqueue(new RetrofitCallbackModel<LoginData>() {
                        @Override
                        public void onResponseData(LoginData data) {
                            dataBinding.editCertificateNumber.setText(data.getAuth_no());

                            UserData.getInstance().setUserId(data.getUser_id());
                            showToast(R.string.toast_send_number);
                            dataBinding.txtRemainTime.setVisibility(View.VISIBLE);

                            if (authTimerCount == 0) {
                                authHandler.removeCallbacks(runnable);
                                startAuth();
                            }
                            endLoading();
                        }
                        @Override
                        public void onCallbackFinish() { endLoading(); }
                    });
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_complete:
                String number = dataBinding.editCertificateNumber.getText().toString();

                if (checkValidInputCertificate(number)) {
                    startLoading();
                    RetrofitClient.getRetrofitApi().checkAuth(UserData.getInstance().getUserId(), "reset", number).enqueue(new RetrofitCallback() {
                        @Override
                        public void onResponseData() {
                            Intent intent = new Intent(ResetCertificateActivity.this, ResetPwActivity.class);
                            startActivity(intent);
                            finishAffinity();
                            overridePendingTransition(R.anim.activity_start_in, R.anim.activity_start_out);
                        }
                        @Override
                        public void onCallbackFinish() { endLoading(); }
                    });
                }
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        UserData.getInstance().removeData();
        overridePendingTransition(R.anim.activity_finish_in, R.anim.activity_finish_out);
    }

    private void startAuth() {
        authTimerCount = AUTH_RESTRICT_TIME;
        authHandler.post(runnable);
    }

    private boolean checkValidInputCertificate(String number) {
        if (number.isEmpty()) {
            showToast(R.string.toast_input_certificate_number);
            return false;
        }
        if (number.length() < 6) {
            showToast(R.string.toast_input_certificate_number_length);
            return false;
        }
        if (authTimerCount == 0) {
            showToast(R.string.toast_time_over_certificate_number);
            return false;
        }
        return true;
    }

    // 아이디, 휴대전화번호 유효성 검사
    private boolean checkValidInputInfo(String id, String phone) {
        if (id.equals("")) {
            showToast(R.string.toast_input_id);
            return false;
        }
        if (phone.equals("")) {
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
        if (deviceId == null) {
            return getDeviceId();
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private boolean getDeviceId() {
        if (requestPermission(this, PERMISSION)) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm != null ? tm.getDeviceId() : null;
            if (deviceId == null) {
                showToast(R.string.toast_not_check_device_id);
                return false;
            } else {
                UserData.getInstance().setDeviceId(deviceId);
                return true;
            }
        }
        return false;
    }
}
