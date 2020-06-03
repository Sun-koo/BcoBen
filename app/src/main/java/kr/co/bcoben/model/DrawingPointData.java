package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;

import androidx.annotation.Nullable;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.R;

public class DrawingPointData {
    public enum DrawingType {
        NORMAL, IMAGE, VOICE, MEMO
    }
    private PointF point;
    private DrawingType type;
    private Bitmap pinImage;
    private Bitmap repImage;
    private int imgCount;

    public DrawingPointData(PointF point, DrawingType type) {
        this(point, type, null, 0);
    }

    public DrawingPointData(PointF point, DrawingType type, @Nullable Bitmap repImage, int imgCount) {
        this.point = point;
        setType(type);

        if (repImage != null) {
            repImage = Bitmap.createScaledBitmap(repImage, 60, 33, true);
        }
        this.repImage = repImage;
        this.imgCount = imgCount;
    }

    public PointF getPoint() {
        return point;
    }
    public void setPoint(PointF point) {
        this.point = point;
    }
    public DrawingType getType() {
        return type;
    }
    public void setType(DrawingType type) {
        this.type = type;

        int pinResource = 0;
        switch (type) {
            case NORMAL: pinResource = R.drawable.point_black; break;
            case IMAGE: pinResource = R.drawable.point_blue; break;
            case VOICE: pinResource = R.drawable.point_red; break;
            case MEMO: pinResource = R.drawable.point_gray; break;
        }
        pinImage = BitmapFactory.decodeResource(AppApplication.getContext().getResources(), pinResource);
    }
    public Bitmap getPinImage() {
        return pinImage;
    }
    public void setPinImage(Bitmap pinImage) {
        this.pinImage = pinImage;
    }
    public Bitmap getRepImage() {
        return repImage;
    }
    public void setRepImage(Bitmap repImage) {
        this.repImage = repImage;
    }
    public int getImgCount() {
        return imgCount;
    }
    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }
}
