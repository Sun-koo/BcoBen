package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.EditText;

import java.util.Date;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuCheckData {
    private String name;
    private String count;
    private boolean checked;

    public MenuCheckData(String name) {
        this.name = name;
        this.count = "";
        this.checked = false;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
