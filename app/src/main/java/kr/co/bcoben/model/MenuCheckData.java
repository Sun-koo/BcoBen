package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import java.util.Date;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuCheckData {
    private int item_id;
    private String item_name;
    private String count;
    private boolean checked;

    public MenuCheckData(int item_id, String item_name) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.count = "";
        this.checked = false;
    }

    public int getItem_id() {
        return item_id;
    }
    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }
    public String getItem_name() {
        return item_name;
    }
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
    public String getCount() {
        return count;
    }
    public void setCount(String count) {
        this.count = count;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
