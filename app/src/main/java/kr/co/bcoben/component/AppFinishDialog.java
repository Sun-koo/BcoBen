package kr.co.bcoben.component;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import kr.co.bcoben.R;

public class AppFinishDialog extends Dialog {

    private AppFinishDialog(final Activity activity) {
        super(activity);
        setContentView(R.layout.dialog_app_finish);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        findViewById(R.id.btn_finish_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finishAffinity();
            }
        });
        findViewById(R.id.btn_finish_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public static Builder builder(Activity activity) {
        return new AppFinishDialog.Builder(activity);
    }

    public static class Builder {
        private Activity activity;

        Builder(Activity activity) {
            this.activity = activity;
        }
        public void show() {
            new AppFinishDialog(activity).show();
        }
    }
}
