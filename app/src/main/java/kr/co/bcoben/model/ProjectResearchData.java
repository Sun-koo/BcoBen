package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class ProjectResearchData implements Parcelable {
    private String facilityCategory;
    private String architecture;
    private String researchTitle;
    private Date regDate;
    private int totCount;
    private int regCount;

    public ProjectResearchData(String facilityCategory, String architecture, String researchTitle, Date regDate, int totCount, int regCount) {
        this.facilityCategory = facilityCategory;
        this.architecture = architecture;
        this.researchTitle = researchTitle;
        this.regDate = regDate;
        this.totCount = totCount;
        this.regCount = regCount;
    }

    protected ProjectResearchData(Parcel in) {
        facilityCategory = in.readString();
        architecture = in.readString();
        researchTitle = in.readString();
        regDate = new Date(in.readLong());
        totCount = in.readInt();
        regCount = in.readInt();
    }

    public static final Creator<ProjectResearchData> CREATOR = new Creator<ProjectResearchData>() {
        @Override
        public ProjectResearchData createFromParcel(Parcel in) {
            return new ProjectResearchData(in);
        }

        @Override
        public ProjectResearchData[] newArray(int size) {
            return new ProjectResearchData[size];
        }
    };

    public String getFacilityCategory() {
        return facilityCategory;
    }
    public void setFacilityCategory(String facilityCategory) {
        this.facilityCategory = facilityCategory;
    }
    public String getArchitecture() {
        return architecture;
    }
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }
    public String getResearchTitle() {
        return researchTitle;
    }
    public void setResearchTitle(String researchTitle) {
        this.researchTitle = researchTitle;
    }
    public Date getRegDate() {
        return regDate;
    }
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
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
        dest.writeString(facilityCategory);
        dest.writeString(architecture);
        dest.writeString(researchTitle);
        dest.writeLong(regDate.getTime());
        dest.writeInt(totCount);
        dest.writeInt(regCount);
    }
}
