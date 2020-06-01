package kr.co.bcoben.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import kr.co.bcoben.R;
import kr.co.bcoben.util.CommonUtil;

import static kr.co.bcoben.util.CommonUtil.requestPermission;
import static kr.co.bcoben.util.CommonUtil.resultRequestPermission;

public class SplashActivity extends AppCompatActivity {

    private String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        startApp();
        if (requestPermission(this, permission)) {
            startApp();
        }
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
        CommonUtil.PermissionState permissionState = resultRequestPermission(this, permissions, grantResults);
        if (permissionState == CommonUtil.PermissionState.GRANT) {
            startApp();
        } else if (permissionState == CommonUtil.PermissionState.DENY) {

        } else if (permissionState == CommonUtil.PermissionState.ALWAYS_DENY) {

        }
    }
}
