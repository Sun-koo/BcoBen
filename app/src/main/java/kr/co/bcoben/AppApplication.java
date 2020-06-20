package kr.co.bcoben;

import android.app.Application;
import android.content.Context;

import kr.co.bcoben.util.SharedPrefUtil;

public class AppApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPrefUtil.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
