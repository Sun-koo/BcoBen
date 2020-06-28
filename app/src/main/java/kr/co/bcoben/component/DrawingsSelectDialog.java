package kr.co.bcoben.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.R;

public class DrawingsSelectDialog extends Dialog implements View.OnClickListener {

    private BtnClickListener btnResetListener;
    private BtnClickListener btnOtherListener;
    private BtnClickListener btnCloseListener;

    public interface BtnClickListener {
        void onClick(DrawingsSelectDialog dialog);
    }

    public DrawingsSelectDialog(Context context, final BtnClickListener btnResetListener, final BtnClickListener btnOtherListener, @Nullable final BtnClickListener btnCloseListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_drawings_select);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        this.btnResetListener = btnResetListener;
        this.btnOtherListener = btnOtherListener;
        this.btnCloseListener = btnCloseListener;

        findViewById(R.id.btn_reset_input).setOnClickListener(this);
        findViewById(R.id.btn_other_select).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset_input: btnResetListener.onClick(this); break;
            case R.id.btn_other_select: btnOtherListener.onClick(this); break;
            case R.id.btn_close:
                if (btnCloseListener != null) {
                    btnCloseListener.onClick(DrawingsSelectDialog.this);
                }
                dismiss();
                break;
        }
    }

    public static Builder builder(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private Activity activity;
        private BtnClickListener btnCloseListener;
        private BtnClickListener btnResetListener;
        private BtnClickListener btnOtherListener;

        Builder(Activity activity) {
            this.activity = activity;
        }
        public Builder setBtnCloseListener(BtnClickListener btnCloseListener) {
            this.btnCloseListener = btnCloseListener;
            return this;
        }
        public Builder setBtnResetListener(BtnClickListener btnResetListener) {
            this.btnResetListener = btnResetListener;
            return this;
        }
        public Builder setBtnOtherListener(BtnClickListener btnOtherListener) {
            this.btnOtherListener = btnOtherListener;
            return this;
        }
        public void show() {
            if (btnResetListener == null) {
                throw new IllegalArgumentException("ResetListener is null");
            }
            if (btnOtherListener == null) {
                throw new IllegalArgumentException("OtherListener is null");
            }
            new DrawingsSelectDialog(activity, btnResetListener, btnOtherListener, btnCloseListener).show();
        }
    }
}
