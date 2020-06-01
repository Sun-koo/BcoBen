package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 메인화면 프로젝트 데이터
 */
public class ProjectData implements Parcelable {
    private String facility;
    private List<ProjectResearchData> researchList;
    private int totCount;
    private int regCount;

    public ProjectData(String facility, List<ProjectResearchData> researchList) {
        this.facility = facility;
        this.researchList = researchList;

        totCount = 0;
        regCount = 0;
        for (ProjectResearchData data : researchList) {
            totCount += data.getTotCount();
            regCount += data.getRegCount();
        }
    }

    protected ProjectData(Parcel in) {
        facility = in.readString();
        researchList = in.createTypedArrayList(ProjectResearchData.CREATOR);
        totCount = in.readInt();
        regCount = in.readInt();
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

    public String getFacility() {
        return facility;
    }
    public void setFacility(String facility) {
        this.facility = facility;
    }
    public List<ProjectResearchData> getResearchList() {
        return researchList;
    }
    public void setResearchList(List<ProjectResearchData> researchList) {
        this.researchList = researchList;
    }
    public int getTotCount() {
        return totCount;
    }
    public void setTotCount(int totCount) {
        this.totCount = totCount;
    }
    public int getRegCount() {
        return regCount;
    }
    public void setRegCount(int regCount) {
        this.regCount = regCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facility);
        dest.writeTypedList(researchList);
        dest.writeInt(totCount);
        dest.writeInt(regCount);
    }
}
