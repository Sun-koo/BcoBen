package kr.co.bcoben.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import kr.co.bcoben.R;

public class CameraDialog extends Dialog {

    private ImageButton btnClose;
    private Button btnCamera, btnAlbum;

    public CameraDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        btnClose = findViewById(R.id.btn_close);
        btnCamera = findViewById(R.id.btn_camera);
        btnAlbum = findViewById(R.id.btn_album);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void selectCameraListener(View.OnClickListener listener) {
        btnCamera.setOnClickListener(listener);
    }

    public void selectAlbumInputListener(View.OnClickListener listener) {
        btnAlbum.setOnClickListener(listener);
    }
}
