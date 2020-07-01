package kr.co.bcoben.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.BuildConfig;
import kr.co.bcoben.R;
import kr.co.bcoben.component.PermissionDialog;
import kr.co.bcoben.model.AppUpdateData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;
import kr.co.bcoben.util.CommonUtil.PermissionState;

import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;

public class SplashActivity extends AppCompatActivity {

    private final String[] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE};

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestAppUpdate();
//        if (requestPermission(this, PERMISSION)) {
//            startApp();
//        }
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        finishAffinity();
    }

    private void startApp() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        };

        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final PermissionState state = resultRequestPermission(this, permissions, grantResults);

        if (state == PermissionState.GRANT) {
            startApp();
        } else {
            PermissionDialog.builder(this)
                    .setTxtPermissionContent(R.string.dialog_permission_content)
                    .setBtnConfirmListener(new PermissionDialog.BtnClickListener() {
                        @Override
                        public void onClick(PermissionDialog dialog) {
                            if (state == PermissionState.ALWAYS_DENY) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivityForResult(intent, 123);
                            } else if (state == PermissionState.DENY) {
                                requestPermission(SplashActivity.this, PERMISSION);
                            }
                            dialog.dismiss();
                        }
                    })
                    .setBtnCloseListener(new PermissionDialog.BtnClickListener() {
                        @Override
                        public void onClick(PermissionDialog dialog) {
                            startApp();
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    private void requestAppUpdate() {
        RetrofitClient.getRetrofitApi().appUpdate().enqueue(new RetrofitCallbackModel<AppUpdateData>() {
            @Override
            public void onResponseData(AppUpdateData data) {
                AppApplication.setUpdateData(data.getUpdate_data());

                if (requestPermission(SplashActivity.this, PERMISSION)) {
                    startApp();
                }
            }

            @Override
            public void onCallbackFinish() {

            }
        });
    }
}
