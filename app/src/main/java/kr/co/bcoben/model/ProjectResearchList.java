package kr.co.bcoben.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

import java.util.Date;
import java.util.List;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class ProjectResearchList implements DataModel {
    private List<ProjectResearchData> research_list;
    private int tot_count;
    private int reg_count;

    public ProjectResearchList(List<ProjectResearchData> research_list) {
        this.research_list = research_list;
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
    public int getReg_count() {
        return reg_count;
    }
    public void setCount() {
        tot_count = 0;
        reg_count = 0;
        for (ProjectResearchData data : research_list) {
            tot_count += data.getTot_count();
            reg_count += data.getReg_count();
        }
    }

    public static class ProjectResearchData implements Parcelable {
        private int research_id;
        private String fac_cate_name;
        private String structure_name;
        private String research_name;
        private int tot_count;
        private int reg_count;
        private long update_date;

        public ProjectResearchData(int research_id, String fac_cate_name, String structure_name, String research_name, int tot_count, int reg_count, long update_date) {
            this.research_id = research_id;
            this.fac_cate_name = fac_cate_name;
            this.structure_name = structure_name;
            this.research_name = research_name;
            this.tot_count = tot_count;
            this.reg_count = reg_count;
            this.update_date = update_date;
        }

        protected ProjectResearchData(Parcel in) {
            research_id = in.readInt();
            fac_cate_name = in.readString();
            structure_name = in.readString();
            research_name = in.readString();
            tot_count = in.readInt();
            reg_count = in.readInt();
            update_date = in.readLong();
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

        public int getResearch_id() {
            return research_id;
        }
        public void setResearch_id(int research_id) {
            this.research_id = research_id;
        }
        public String getFac_cate_name() {
            return fac_cate_name;
        }
        public void setFac_cate_name(String fac_cate_name) {
            this.fac_cate_name = fac_cate_name;
        }
        public String getStructure_name() {
            return structure_name;
        }
        public void setStructure_name(String structure_name) {
            this.structure_name = structure_name;
        }
        public String getResearch_name() {
            return research_name;
        }
        public void setResearch_name(String research_name) {
            this.research_name = research_name;
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
        public long getUpdate_date() {
            return update_date;
        }
        public void setUpdate_date(long update_date) {
            this.update_date = update_date;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(research_id);
            dest.writeString(fac_cate_name);
            dest.writeString(structure_name);
            dest.writeString(research_name);
            dest.writeInt(tot_count);
            dest.writeInt(reg_count);
            dest.writeLong(update_date);
        }
    }
}
