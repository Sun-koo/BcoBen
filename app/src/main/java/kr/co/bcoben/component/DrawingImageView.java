package kr.co.bcoben.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import kr.co.bcoben.R;
import kr.co.bcoben.model.DrawingPointData;

public class DrawingImageView extends SubsamplingScaleImageView {

    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    private List<DrawingPointData> pinList;
    private int saveCnt = 0;

    public DrawingImageView(Context context) {
        this(context, null);
    }
    public DrawingImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setPinList(List<DrawingPointData> pinList) {
        this.pinList = pinList;
        for (DrawingPointData data : this.pinList) {
            data.setPinImage(initialise(data.getPinImage()));
        }
        invalidate();
    }

    public void addPin(DrawingPointData pin) {
        pin.setPinImage(initialise(pin.getPinImage()));
        pinList.add(pin);
        invalidate();
    }

    private Bitmap initialise(Bitmap bitmap) {
        float density = getResources().getDisplayMetrics().densityDpi;
        float w = (density/420f) * bitmap.getWidth();
        float h = (density/420f) * bitmap.getHeight();
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)w, (int)h, true);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);
        paint.setTextSize(16);
        paint.setTextAlign(Paint.Align.CENTER);

        if (pinList != null && !pinList.isEmpty()) {
            DecimalFormat df = new DecimalFormat("00");
            for (int i = 0; i < pinList.size(); i++) {
                DrawingPointData data = pinList.get(i);
                PointF point = data.getPoint();
                Bitmap pinImage = data.getPinImage();

                if (point != null && pinImage != null) {
                    sourceToViewCoord(point, vPin);
                    paint.setColor(getResources().getColor(R.color.colorBlack));
                    paint.setTextAlign(Paint.Align.LEFT);
                    float vX = vPin.x - (pinImage.getWidth() / 2.0f);
                    float vY = vPin.y - (pinImage.getHeight() / 2.0f);
                    canvas.drawBitmap(pinImage, vX, vY, paint);
                    canvas.drawText(df.format(i + 1), vX, vY - 7, paint);

                    if (data.getRepImage() != null) {
                        paint.setColor(getResources().getColor(R.color.colorWhite));
                        paint.setTextAlign(Paint.Align.CENTER);
                        Bitmap badge = BitmapFactory.decodeResource(getResources(), R.drawable.badge_drawing);
                        badge = initialise(badge);

                        vX += pinImage.getWidth() + 5;
                        vY -= data.getRepImage().getHeight() / 2.0f;
                        canvas.drawBitmap(data.getRepImage(), vX, vY, paint);

                        vX += data.getRepImage().getWidth() - badge.getWidth() / 2.0f;
                        vY -= badge.getHeight() / 2.0f;
                        canvas.drawBitmap(badge, vX, vY, paint);

                        vX += badge.getWidth() / 2.0f;
                        vY += badge.getHeight() / 2.0f + 5;
                        canvas.drawText(String.valueOf(data.getImgCount()), vX, vY, paint);
                    }
                }
            }
        }
    }

    public int checkClickPoint(PointF point) {
        Log.e("Drawing", "scale : " + getScale());
        for (int i = 1; i <= pinList.size(); i++) {
            DrawingPointData data = pinList.get(i - 1);
            PointF pinPoint = data.getPoint();
            float size = data.getPinImage().getWidth() / getScale();

            if ((point.x > pinPoint.x - size && point.x < pinPoint.x + size) &&
                    (point.y > pinPoint.y - size - 15 && point.y < pinPoint.y + size)) {
                return i;
            }

            if (data.getRepImage() != null) {
                Bitmap badge = initialise(BitmapFactory.decodeResource(getResources(), R.drawable.badge_drawing));

                float repWidth = (data.getRepImage().getWidth() + badge.getWidth()) / getScale();
                float repHeight = ((data.getRepImage().getHeight() + badge.getWidth()) / 2.0f) / getScale();
                if ((point.x > pinPoint.x + size && point.x < pinPoint.x + size + repWidth) &&
                        (point.y > pinPoint.y - size - repHeight && point.y < pinPoint.y + repHeight)) {
                    return -i;
                }
            }
        }
        return 0;
    }
}
