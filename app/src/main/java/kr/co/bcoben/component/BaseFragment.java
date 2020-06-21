package kr.co.bcoben.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    protected String TAG = "BaseActivity";
    protected T dataBinding;

    abstract protected int getLayoutResource();     // 레이아웃 파일 정의
    abstract protected void initView();             // onCreate 내부 처리

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TAG = getClass().getName().substring(getClass().getName().lastIndexOf(".") + 1);
        TAG = TAG.length() > 23 ? TAG.replace("Fragment", "") : TAG;
        dataBinding = DataBindingUtil.inflate(inflater, getLayoutResource(), container, false);
        View view = dataBinding.getRoot();
        initView();
        return view;
    }
}
