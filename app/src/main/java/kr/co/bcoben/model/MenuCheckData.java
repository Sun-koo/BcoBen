package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuCheckData implements Parcelable {
    private String name;
    private int count;
    private boolean checked;

    public MenuCheckData(String name, int count, boolean checked) {
        this.name = name;
        this.count = count;
        this.checked = checked;
    }

    protected MenuCheckData(Parcel in) {
        name = in.readString();
        count = in.readInt();
//        checked = in.readBoolean();
    }

    public static final Creator<MenuCheckData> CREATOR = new Creator<MenuCheckData>() {
        @Override
        public MenuCheckData createFromParcel(Parcel in) {
            return new MenuCheckData(in);
        }

        @Override
        public MenuCheckData[] newArray(int size) {
            return new MenuCheckData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(count);
//        dest.writeBoolean(checked);
    }
}
