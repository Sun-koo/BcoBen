package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 프로젝트 데이터
 */
public class ProjectData {
    private String facility;
    private List<ProjectResearchData> researchList;

    public ProjectData(String facility, List<ProjectResearchData> researchList) {
        this.facility = facility;
        this.researchList = researchList;
    }

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
}
