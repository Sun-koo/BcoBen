package kr.co.bcoben.component;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.R;

public class PermissionDialog extends Dialog {

    public interface BtnClickListener {
        void onClick(PermissionDialog dialog);
    }

    private PermissionDialog(Activity activity, String txtContent, final BtnClickListener btnConfirmListener, @Nullable final BtnClickListener btnCloseListener) {
        super(activity);
        setContentView(R.layout.dialog_permission);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        TextView txtPermissionContent = findViewById(R.id.txt_permission_content);
        ImageView btnPermissionClose = findViewById(R.id.btn_permission_close);
        Button btnPermissionConfirm = findViewById(R.id.btn_permission_confirm);

        txtPermissionContent.setText(txtContent);
        btnPermissionConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfirmListener.onClick(PermissionDialog.this);
            }
        });
        btnPermissionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCloseListener != null) {
                    btnCloseListener.onClick(PermissionDialog.this);
                } else {
                    dismiss();
                }
            }
        });
    }

    public static Builder builder(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private Activity activity;
        private String txtPermissionContent;
        private BtnClickListener btnConfirmListener;
        private BtnClickListener btnCloseListener;

        Builder(Activity activity) {
            this.activity = activity;
        }
        public Builder setTxtPermissionContent(String str) {
            this.txtPermissionContent = str;
            return this;
        }
        public Builder setTxtPermissionContent(int resId) {
            this.txtPermissionContent = AppApplication.getContext().getString(resId);
            return this;
        }
        public Builder setBtnConfirmListener(BtnClickListener btnConfirmListener) {
            this.btnConfirmListener = btnConfirmListener;
            return this;
        }
        public Builder setBtnCloseListener(BtnClickListener btnCloseListener) {
            this.btnCloseListener = btnCloseListener;
            return this;
        }
        public void show() {
            if (btnConfirmListener == null) {
                throw new IllegalArgumentException("ConfirmListener is null");
            }
            new PermissionDialog(activity, txtPermissionContent, btnConfirmListener, btnCloseListener).show();
        }
    }
}
