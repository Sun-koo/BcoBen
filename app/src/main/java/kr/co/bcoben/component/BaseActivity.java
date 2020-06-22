package kr.co.bcoben.component;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import kr.co.bcoben.BR;

abstract public class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected String TAG = "BaseActivity";
    protected T dataBinding;
    protected Activity activity;
    private final ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableBoolean getIsLoading() { return isLoading; }
    public void setIsLoading(boolean isLoading) { this.isLoading.set(isLoading); }

    abstract protected int getLayoutResource();     // 레이아웃 파일 정의
    abstract protected void initView();             // onCreate 내부 처리

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
        TAG = TAG.length() > 23 ? TAG.replace("Activity", "") : TAG;
        activity = this;
        dataBinding = DataBindingUtil.setContentView(this, getLayoutResource());
        dataBinding.setVariable(BR.isLoading, isLoading);
        initView();
    }
}
