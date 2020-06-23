package kr.co.bcoben.model;

import java.util.ArrayList;
import java.util.List;

public class ResearchSpinnerData implements DataModel {
    private ResearchSelectData research_data;
    private ProjectFacData project_fac_data;

    public ResearchSpinnerData(ResearchSelectData research_data, ProjectFacData project_fac_data) {
        this.research_data = research_data;
        this.project_fac_data = project_fac_data;
    }

    public ResearchSelectData getResearch_data() {
        return research_data;
    }
    public void setResearch_data(ResearchSelectData research_data) {
        this.research_data = research_data;
    }
    public ProjectFacData getProject_fac_data() {
        return project_fac_data;
    }
    public void setProject_fac_data(ProjectFacData project_fac_data) {
        this.project_fac_data = project_fac_data;
    }

    public static class ResearchSelectData {
        private int research_id;
        private int project_id;
        private int facility_id;
        private int fac_cate_id;
        private int structure_id;
        private int research_type_id;
        private int tot_count;
        private int reg_count;

        public ResearchSelectData(int research_id, int project_id, int facility_id, int fac_cate_id, int structure_id, int research_type_id, int tot_count, int reg_count) {
            this.research_id = research_id;
            this.project_id = project_id;
            this.facility_id = facility_id;
            this.fac_cate_id = fac_cate_id;
            this.structure_id = structure_id;
            this.research_type_id = research_type_id;
            this.tot_count = tot_count;
            this.reg_count = reg_count;
        }

        public int getResearch_id() {
            return research_id;
        }
        public void setResearch_id(int research_id) {
            this.research_id = research_id;
        }
        public int getProject_id() {
            return project_id;
        }
        public void setProject_id(int project_id) {
            this.project_id = project_id;
        }
        public int getFacility_id() {
            return facility_id;
        }
        public void setFacility_id(int facility_id) {
            this.facility_id = facility_id;
        }
        public int getFac_cate_id() {
            return fac_cate_id;
        }
        public void setFac_cate_id(int fac_cate_id) {
            this.fac_cate_id = fac_cate_id;
        }
        public int getStructure_id() {
            return structure_id;
        }
        public void setStructure_id(int structure_id) {
            this.structure_id = structure_id;
        }
        public int getResearch_type_id() {
            return research_type_id;
        }
        public void setResearch_type_id(int research_type_id) {
            this.research_type_id = research_type_id;
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
    }

    public static class ProjectFacData {
        private List<ResearchRegData.FacilityData.FacCateData> fac_cate_list;
        private List<ResearchRegData.ResearchData> research_list;

        public ProjectFacData(List<ResearchRegData.FacilityData.FacCateData> fac_cate_list, List<ResearchRegData.ResearchData> research_list) {
            this.fac_cate_list = fac_cate_list;
            this.research_list = research_list;
        }

        public List<ResearchRegData.FacilityData.FacCateData> getFac_cate_list() {
            return fac_cate_list;
        }
        public void setFac_cate_list(List<ResearchRegData.FacilityData.FacCateData> fac_cate_list) {
            this.fac_cate_list = fac_cate_list;
        }
        public List<ResearchRegData.ResearchData> getResearch_list() {
            return research_list;
        }
        public void setResearch_list(List<ResearchRegData.ResearchData> research_list) {
            this.research_list = research_list;
        }
    }
}
