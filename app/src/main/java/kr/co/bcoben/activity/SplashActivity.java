package kr.co.bcoben.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import kr.co.bcoben.R;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startApp();
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
}
