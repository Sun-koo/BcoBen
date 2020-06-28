package kr.co.bcoben.component;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import kr.co.bcoben.R;

public class CameraDialog extends Dialog {

    public interface BtnClickListener {
        void onClick(CameraDialog dialog);
    }

    private CameraDialog(Activity activity, @NonNull final BtnClickListener btnCameraListener, @NonNull final BtnClickListener btnGalleyListener) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_camera);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.btn_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCameraListener.onClick(CameraDialog.this);
            }
        });
        findViewById(R.id.btn_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnGalleyListener.onClick(CameraDialog.this);
            }
        });
    }

    public static Builder builder(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private Activity activity;
        private BtnClickListener btnCameraListener;
        private BtnClickListener btnGalleyListener;

        Builder(Activity activity) {
            this.activity = activity;
        }
        public Builder setBtnCameraListener(BtnClickListener btnCameraListener) {
            this.btnCameraListener = btnCameraListener;
            return this;
        }
        public Builder setBtnGalleyListener(BtnClickListener btnGalleyListener) {
            this.btnGalleyListener = btnGalleyListener;
            return this;
        }
        public void show() {
            if (btnCameraListener == null) {
                throw new NullPointerException("Camera Button Listener is null");
            }
            if (btnGalleyListener == null) {
                throw new NullPointerException("Galley Button Listener is null");
            }
            new CameraDialog(activity, btnCameraListener, btnGalleyListener).show();
        }
    }
}
