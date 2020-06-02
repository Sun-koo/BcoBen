package kr.co.bcoben.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import kr.co.bcoben.R;

public class DrawingImageView extends SubsamplingScaleImageView {

    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    private PointF sPin;
    private Bitmap pin;

    public DrawingImageView(Context context) {
        this(context, null);
    }
    public DrawingImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public void setPin(PointF pin) {
        this.sPin = pin;
        initialise();
        invalidate();
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.point_black);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);
        paint.setTextSize(15);

        if (sPin != null && pin != null) {
            sourceToViewCoord(sPin, vPin);
            float vX = vPin.x - (pin.getWidth() / 2.0f);
            float vY = vPin.y - (pin.getHeight() / 2.0f);
            canvas.drawBitmap(pin, vX, vY, paint);
            canvas.drawText("10", vX, vY - 7, paint);
        }

    }
}
