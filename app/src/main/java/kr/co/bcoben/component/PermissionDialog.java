package kr.co.bcoben.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;

import kr.co.bcoben.R;

public class PermissionDialog extends Dialog {

    public interface BtnClickListener {
        void onClick(PermissionDialog dialog);
    }

    private PermissionDialog(final Activity activity, final BtnClickListener confirmListener) {
        super(activity);
        setContentView(R.layout.dialog_permission);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        findViewById(R.id.btn_permission_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmListener.onClick(PermissionDialog.this);
            }
        });
        findViewById(R.id.btn_finish_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finishAffinity();
            }
        });
    }

    public static Builder builder(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private Activity activity;
        private BtnClickListener confirmListener;

        Builder(Activity activity) {
            this.activity = activity;
        }
        public Builder setConfirmListener(BtnClickListener confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }
        public void show() {
            if (confirmListener == null) {
                throw new IllegalArgumentException("ConfirmListener is null");
            }
            new PermissionDialog(activity, confirmListener).show();
        }
    }
}
