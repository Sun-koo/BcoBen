package kr.co.bcoben.activity;

import android.content.Intent;
import android.view.View;

import kr.co.bcoben.R;
import kr.co.bcoben.component.BaseActivity;
import kr.co.bcoben.databinding.ActivityLoginBinding;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.CASE_ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.stringPatternCheck;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements View.OnClickListener{

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {}

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
                    Intent intent_login = new Intent(LoginActivity.this, CertificateActivity.class);
                    startActivity(intent_login);
                    overridePendingTransition(R.anim.activity_start_in, R.anim.activity_start_out);
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
        return true;
    }
}
