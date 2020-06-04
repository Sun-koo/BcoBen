package kr.co.bcoben.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import kr.co.bcoben.R;

import static kr.co.bcoben.util.CommonUtil.dpToPx;

public class DrawingPictureDialog extends Dialog {

    private ImageView ivPicture;
    private TextView btnDialogClose;

    private DrawingPictureDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_drawing_picture);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        btnDialogClose = findViewById(R.id.btn_dialog_close);
        ivPicture = findViewById(R.id.iv_picture);

        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    private DrawingPictureDialog(Context context, Bitmap picture) {
        this(context);
        ivPicture.setImageBitmap(picture);

        int picWidth = picture.getWidth();
        int picHeight = picture.getHeight();
        int ivHeight = dpToPx(450);
        int ivWidth = picWidth * ivHeight / picHeight;

        ViewGroup.LayoutParams params = ivPicture.getLayoutParams();
        params.width = ivWidth;
        ivPicture.setLayoutParams(params);
    }
    @Override
    public void onBackPressed() {
        dismiss();
    }

    public static Builder builder(Context context) {
        return new DrawingPictureDialog.Builder(context);
    }

    public static class Builder {
        private Context context;
        private Bitmap picture;

        Builder(Context context) {
            this.context = context;
        }
        public Builder setPicture(Bitmap picture) {
            this.picture = picture;
            return this;
        }
        public DrawingPictureDialog build() {
            return new DrawingPictureDialog(context, picture);
        }
    }
}
