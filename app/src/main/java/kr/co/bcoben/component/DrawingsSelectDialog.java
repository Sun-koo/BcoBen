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

public class DrawingsSelectDialog extends Dialog {

    private ImageButton btnClose;
    private Button btnInput, btnResetInput, btnOtherSelect;

    public DrawingsSelectDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_drawings_select);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        btnClose = findViewById(R.id.btn_close);
//        btnInput = findViewById(R.id.btn_input);
        btnResetInput = findViewById(R.id.btn_reset_input);
        btnOtherSelect = findViewById(R.id.btn_other_select);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

//    public void selectInputListener(View.OnClickListener listener) {
//        btnInput.setOnClickListener(listener);
//    }

    public void selectResetInputListener(View.OnClickListener listener) {
        btnResetInput.setOnClickListener(listener);
    }

    public void selectOtherListener(View.OnClickListener listener) {
        btnOtherSelect.setOnClickListener(listener);
    }
}
