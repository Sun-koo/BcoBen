package kr.co.bcoben;

import android.app.Application;
import android.content.Context;

import kr.co.bcoben.model.AppUpdateData;
import kr.co.bcoben.util.SharedPrefUtil;

public class AppApplication extends Application {
    private static Context context;
    private static AppUpdateData.UpdateData updateData;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SharedPrefUtil.init(this);
    }

    public static Context getContext() {
        return context;
    }

    public static AppUpdateData.UpdateData getUpdateData() {
        return updateData;
    }
    public static void setUpdateData(AppUpdateData.UpdateData updateData) {
        AppApplication.updateData = updateData;
    }
}
