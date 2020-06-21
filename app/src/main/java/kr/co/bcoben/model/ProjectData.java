package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 메인화면 프로젝트 데이터
 */
public class ProjectData implements Parcelable {
    private int facility_id;
    private String facility_name;

    public ProjectData(int facility_id, String facility_name) {
        this.facility_id = facility_id;
        this.facility_name = facility_name;
    }

    protected ProjectData(Parcel in) {
        facility_id = in.readInt();
        facility_name = in.readString();
    }

    public static final Creator<ProjectData> CREATOR = new Creator<ProjectData>() {
        @Override
        public ProjectData createFromParcel(Parcel in) {
            return new ProjectData(in);
        }

        @Override
        public ProjectData[] newArray(int size) {
            return new ProjectData[size];
        }
    };

    public int getFacility_id() {
        return facility_id;
    }
    public void setFacility_id(int facility_id) {
        this.facility_id = facility_id;
    }
    public String getFacility_name() {
        return facility_name;
    }
    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(facility_id);
        dest.writeString(facility_name);
    }
}
