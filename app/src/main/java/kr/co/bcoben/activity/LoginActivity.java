package kr.co.bcoben.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.annotation.NonNull;

import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.component.PermissionDialog;
import kr.co.bcoben.databinding.ActivityLoginBinding;
import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.ResponseData;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.CommonUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.CASE_ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.stringPatternCheck;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements View.OnClickListener{

    private final String[] PERMISSION = {Manifest.permission.READ_PHONE_STATE};
    private String deviceId;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        if (requestPermission(this, PERMISSION)) {
            getDeviceId();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final CommonUtil.PermissionState state = resultRequestPermission(this, permissions, grantResults);

        if (state == CommonUtil.PermissionState.GRANT) {
            getDeviceId();
        } else {
            PermissionDialog.builder(this)
                    .setTxtPermissionContent(R.string.dialog_permission_content_phone)
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
    public void onBackPressed() {
        finishApp(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_pw:
                //TODO move to reset pw page
                Intent intent_reset_certificate = new Intent(LoginActivity.this, ResetCertificateActivity.class);
                startActivity(intent_reset_certificate);
                overridePendingTransition(R.anim.activity_start_in, R.anim.activity_start_out);
                break;
            case R.id.btn_exit:
                finishApp(this);
                break;
            case R.id.btn_login:
                String id = dataBinding.editId.getText().toString();
                String pw = dataBinding.editPw.getText().toString();

                if (checkValidInput(id, pw)) {
                    //TODO request login api
                    RetrofitClient.getRetrofitApi().userLogin(id, pw, deviceId).enqueue(new Callback<ResponseData<LoginData>>() {
                        @Override
                        public void onResponse(Call<ResponseData<LoginData>> call, Response<ResponseData<LoginData>> response) {
                            if (response.body().isResult()) {
                                UserData.getInstance().setCompanyId(response.body().getData().getUser().getCompany_id());
                                Intent intent_login = new Intent(LoginActivity.this, CertificateActivity.class);
                                startActivity(intent_login);
                                overridePendingTransition(R.anim.activity_start_in, R.anim.activity_start_out);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseData<LoginData>> call, Throwable t) {

                        }
                    });
                }
                break;
        }
    }

    // 아이디, 비밀번호 유효성 검사
    private boolean checkValidInput(String id, String pw) {
        if (id.isEmpty()) {
            showToast(R.string.toast_input_id);
            return false;
        }
        if (pw.isEmpty()) {
            showToast(R.string.toast_input_pw);
            return false;
        }
        if (!stringPatternCheck(id, ALPHA_NUM, 6, -1)) {
            showToast(R.string.toast_invalid_id);
            return false;
        }
        if (!stringPatternCheck(pw, CASE_ALPHA_NUM, 10, -1)) {
            showToast(R.string.toast_invalid_pw);
            return false;
        }
        if (deviceId == null) {
            return requestPermission(this, PERMISSION);
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    private void getDeviceId() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = tm != null ? tm.getDeviceId() : null;
    }
}
