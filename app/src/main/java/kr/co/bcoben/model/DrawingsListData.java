package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;


public class DrawingsListData {
    private int id;
    private String name;
    private Bitmap bitmap;
    private String filePath;

    public DrawingsListData(int id, String name, Bitmap bitmap, String filePath) {
        this.id = id;
        this.name = name;
        this.bitmap = bitmap;
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
