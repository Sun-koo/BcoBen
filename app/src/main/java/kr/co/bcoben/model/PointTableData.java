package kr.co.bcoben.model;

import android.graphics.Bitmap;

public class PointTableData {
    private int pointId;
    private int num;
    private Bitmap label;
    private String content;
    private String measure;
    private int count;
    private boolean isChecked;

    public PointTableData(int pointId, int num, String content, String measure, int count) {
        this.pointId = pointId;
        this.num = num;
        this.content = content;
        this.measure = measure;
        this.count = count;
        this.isChecked = false;
    }

    public int getPointId() {
        return pointId;
    }
    public int getNum() {
        return num;
    }
    public Bitmap getLabel() {
        return label;
    }
    public void setLabel(Bitmap label) {
        this.label = label;
    }
    public String getContent() {
        return content;
    }
    public String getMeasure() {
        return measure;
    }
    public int getCount() {
        return count;
    }
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
