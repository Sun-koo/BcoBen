package kr.co.bcoben;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.IOException;

import kr.co.bcoben.model.AppUpdateData;
import kr.co.bcoben.util.SharedPrefUtil;

import static kr.co.bcoben.util.CommonUtil.getImagePath;

public class AppApplication extends Application {
    private static AppApplication instance;
    private static Context context;
    private AppUpdateData.UpdateData updateData;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = getApplicationContext();
        SharedPrefUtil.init(this);
    }

    public static Context getContext() {
        return context;
    }
    public static AppApplication getInstance() {
        return instance;
    }

    public AppUpdateData.UpdateData getUpdateData() {
        return updateData;
    }
    public void setUpdateData(AppUpdateData.UpdateData updateData) {
        this.updateData = updateData;
    }
}
