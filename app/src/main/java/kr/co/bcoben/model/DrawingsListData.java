package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;


public class DrawingsListData {
    private String name;
    private Bitmap bitmap;
    private String filePath;

    public DrawingsListData(String name, Bitmap bitmap, String filePath) {
        this.name = name;
        this.bitmap = bitmap;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
