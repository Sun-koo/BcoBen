package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 메인화면 프로젝트 데이터
 */
public class ProjectData implements Parcelable {
    private String facility_name;
    private List<ProjectResearchData> research_list;
    private int tot_count;
    private int reg_count;

    public ProjectData(String facility_name, List<ProjectResearchData> research_list) {
        this.facility_name = facility_name;
        this.research_list = research_list;

        tot_count = 0;
        reg_count = 0;
        for (ProjectResearchData data : research_list) {
            tot_count += data.getTot_count();
            reg_count += data.getReg_count();
        }
    }

    protected ProjectData(Parcel in) {
        facility_name = in.readString();
        research_list = in.createTypedArrayList(ProjectResearchData.CREATOR);
        tot_count = in.readInt();
        reg_count = in.readInt();
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

    public String getFacility_name() {
        return facility_name;
    }
    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }
    public List<ProjectResearchData> getResearch_list() {
        return research_list;
    }
    public void setResearch_list(List<ProjectResearchData> research_list) {
        this.research_list = research_list;
    }
    public int getTot_count() {
        return tot_count;
    }
    public void setTot_count(int tot_count) {
        this.tot_count = tot_count;
    }
    public int getReg_count() {
        return reg_count;
    }
    public void setReg_count(int reg_count) {
        this.reg_count = reg_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(facility_name);
        dest.writeTypedList(research_list);
        dest.writeInt(tot_count);
        dest.writeInt(reg_count);
    }
}
