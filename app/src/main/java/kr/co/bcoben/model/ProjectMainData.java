package kr.co.bcoben.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 메인화면 사이드 리스트 데이터
 */
public class ProjectMainData implements DataModel {
    private List<ProjectData> facility_list;
    private ResearchRegData research_reg_data;

    public List<ProjectData> getFacility_list() {
        return facility_list;
    }
    public void setFacility_list(List<ProjectData> facility_list) {
        this.facility_list = facility_list;
    }
    public ResearchRegData getResearch_reg_data() {
        return research_reg_data;
    }
    public void setResearch_reg_data(ResearchRegData research_reg_data) {
        this.research_reg_data = research_reg_data;
    }
}
