package kr.co.bcoben.activity;

import android.content.Intent;
import android.view.View;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityResetPwBinding;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallback;
import kr.co.bcoben.service.retrofit.RetrofitClient;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.CASE_ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.stringPatternCheck;

public class ResetPwActivity extends BaseActivity<ActivityResetPwBinding> implements View.OnClickListener {

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_reset_pw;
    }

    @Override
    protected void initView() {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                finishApp(this);
                break;
            case R.id.btn_login:
                String pw = dataBinding.editPw.getText().toString();
                String pwConfirm = dataBinding.editPwConfirm.getText().toString();

                if (checkValidInput(pw, pwConfirm)) {
                    startLoading();
                    RetrofitClient.getRetrofitApi().updatePassword(UserData.getInstance().getUserId(), pw).enqueue(new RetrofitCallback() {
                        @Override
                        public void onResponseData() {
                            Intent intent = new Intent(ResetPwActivity.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                        @Override
                        public void onCallbackFinish() { endLoading(); }
                    });
                }
                break;
        }
    }

    // 아이디, 비밀번호 유효성 검사
    private boolean checkValidInput(String pw, String pw_confirm) {
        if (pw.isEmpty()) {
            showToast(R.string.toast_input_pw);
            return false;
        }
        if (pw_confirm.isEmpty()) {
            showToast(R.string.toast_input_pw);
            return false;
        }
        if (!stringPatternCheck(pw, CASE_ALPHA_NUM, 10, -1)) {
            showToast(R.string.toast_invalid_pw);
            return false;
        }
        if (!pw.equals(pw_confirm)) {
            showToast(R.string.toast_not_equal_pw);
            return false;
        }
        return true;
    }
}
