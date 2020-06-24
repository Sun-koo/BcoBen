package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.AppApplication;
import kr.co.bcoben.R;

public class DrawingPointData {
    public enum DrawingType { NORMAL, IMAGE, VOICE, MEMO }
    private PointF point;
    private DrawingType type;
    private Bitmap pinImage;
    private Bitmap regImage;
    private List<Bitmap> regImageList;

    public DrawingPointData(PointF point, DrawingType type) {
        this.point = point;
        setType(type);
    }

    public PointF getPoint() {
        return point;
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
    public Bitmap getRegImage() {
        return regImage;
    }
    public void setRegImage(Bitmap regImage) {
        this.regImage = regImage;
    }
    public List<Bitmap> getRegImageList() {
        return regImageList;
    }
    public void setRegImageList(List<Bitmap> regImageList) {
        this.regImageList = regImageList;
        if (regImageList != null && !regImageList.isEmpty()) {
            Bitmap bitmap = Bitmap.createScaledBitmap(regImageList.get(0), 60, 33, true);
            setRegImage(bitmap);
        }
    }
}
