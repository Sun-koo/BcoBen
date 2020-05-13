package kr.co.bcoben.component;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

abstract public class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    protected String TAG = "BaseActivity";
    protected T dataBinding;
    protected Activity activity;

    abstract protected int getLayoutResource();     // 레이아웃 파일 정의
    abstract protected void initView();             // onCreate 내부 처리

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
        TAG = TAG.length() > 23 ? TAG.replace("Activity", "") : TAG;
        activity = this;
        dataBinding = DataBindingUtil.setContentView(this, getLayoutResource());
        initView();
    }
}
