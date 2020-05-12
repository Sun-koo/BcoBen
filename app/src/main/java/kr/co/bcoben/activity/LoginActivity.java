package kr.co.bcoben.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.bcoben.R;

import static kr.co.bcoben.util.CommonUtil.finishApp;
import static kr.co.bcoben.util.CommonUtil.showToast;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.StringPattern.CASE_ALPHA_NUM;
import static kr.co.bcoben.util.ValidateUtil.stringPatternCheck;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edtId, edtPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtId = findViewById(R.id.edt_id);
        edtPw = findViewById(R.id.edt_pw);
    }

    @Override
    public void onBackPressed() {
        finishApp(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exit:
                finishApp(this);
                break;

            case R.id.btn_login:
                String id = edtId.getText().toString();
                String pw = edtPw.getText().toString();

                if (checkValidInput(id, pw)) {
                    //TODO request login api
                    Intent intent = new Intent(LoginActivity.this, CertificateActivity.class);
                    startActivity(intent);
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
