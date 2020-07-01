package kr.co.bcoben.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.text.DecimalFormat;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.model.DrawingPointData;
import kr.co.bcoben.model.PointData;

public class DrawingImageView extends SubsamplingScaleImageView {

    private final String TAG = "DrawingImageView";
    private final Paint paint = new Paint();
    private final PointF vPin = new PointF();
    private List<PointData> pinList;

    public DrawingImageView(Context context) {
        this(context, null);
    }
    public DrawingImageView(Context context, AttributeSet attr) {
        super(context, attr);
        invalidate();
    }

    public void setPinList(List<PointData> pinList) {
        this.pinList = pinList;
        for (PointData data : pinList) {
            data.getDrawingPointData().setPinImage(initialise(data.getDrawingPointData().getPinImage()));
        }
        invalidate();
    }

    public void addPin(PointData pin) {
        pin.getDrawingPointData().setPinImage(initialise(pin.getDrawingPointData().getPinImage()));
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
            for (PointData pointData : pinList) {
                DrawingPointData data = pointData.getDrawingPointData();
                PointF point = data.getPoint();
                Bitmap pinImage = data.getPinImage();

                if (point != null && pinImage != null) {
                    sourceToViewCoord(point, vPin);
                    paint.setColor(getResources().getColor(R.color.colorBlack));
                    paint.setTextAlign(Paint.Align.LEFT);
                    float vX = vPin.x - (pinImage.getWidth() / 2.0f);
                    float vY = vPin.y - (pinImage.getHeight() / 2.0f);
                    canvas.drawBitmap(pinImage, vX, vY, paint);
                    canvas.drawText(df.format(pointData.getPoint_num()), vX, vY - 7, paint);

                    if (data.getRegImage() != null) {
                        paint.setColor(getResources().getColor(R.color.colorWhite));
                        paint.setTextAlign(Paint.Align.CENTER);
                        Bitmap badge = BitmapFactory.decodeResource(getResources(), R.drawable.badge_drawing);
                        badge = initialise(badge);

                        vX += pinImage.getWidth() + 5;
                        vY -= data.getRegImage().getHeight() / 2.0f;
                        canvas.drawBitmap(data.getRegImage(), vX, vY, paint);

                        vX += data.getRegImage().getWidth() - badge.getWidth() / 2.0f;
                        vY -= badge.getHeight() / 2.0f;
                        canvas.drawBitmap(badge, vX, vY, paint);

                        vX += badge.getWidth() / 2.0f;
                        vY += badge.getHeight() / 2.0f + 5;
                        canvas.drawText(String.valueOf(data.getRegImageList().size()), vX, vY, paint);
                    }
                }
            }
        }
    }

    public int checkClickPoint(PointF point) {
        for (int i = 1; i <= pinList.size(); i++) {
            DrawingPointData data = pinList.get(i - 1).getDrawingPointData();
            PointF pinPoint = data.getPoint();
            float size = data.getPinImage().getWidth() / getScale();

            if ((point.x > pinPoint.x - size && point.x < pinPoint.x + size) &&
                    (point.y > pinPoint.y - size - 15 && point.y < pinPoint.y + size)) {
                return i;
            }

            if (data.getRegImageList() != null) {
                Bitmap badge = initialise(BitmapFactory.decodeResource(getResources(), R.drawable.badge_drawing));

                float repWidth = (data.getRegImage().getWidth() + badge.getWidth()) / getScale();
                float repHeight = ((data.getRegImage().getHeight() + badge.getWidth()) / 2.0f) / getScale();
                if ((point.x > pinPoint.x + size && point.x < pinPoint.x + size + repWidth) &&
                        (point.y > pinPoint.y - size - repHeight && point.y < pinPoint.y + repHeight)) {
                    return -i;
                }
            }
        }
        return 0;
    }

    public PointF getImagePopupPosition(int index, int popupWidth, int popupHeight) {
        DrawingPointData data = pinList.get(index).getDrawingPointData();
        PointF position = sourceToViewCoord(data.getPoint());
        position.x += 95;
        position.y -= 28;
        int popupEndPointX = (int) (position.x + popupWidth);
        int popupEndPointY = (int) (position.y + popupHeight);

        if (popupEndPointX > getWidth()) {
            position.x -= (popupWidth + 110);
        }
        if (popupEndPointY > getHeight()) {
            position.y -= (popupEndPointY - getHeight() + 10);
        }
        return position;
    }
}
