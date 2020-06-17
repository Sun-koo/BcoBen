package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuCheckListData implements DataModel {
    private List<MenuCheckData> grade_list;
    private List<MenuCheckData> facility_list;
    private List<MenuCheckData> fac_cate_list;
    private List<MenuCheckData> structure_list;
    private List<MenuCheckData> research_list;

    public List<MenuCheckData> getGrade_list() {
        return grade_list;
    }
    public void setGrade_list(List<MenuCheckData> grade_list) {
        this.grade_list = grade_list;
    }
    public List<MenuCheckData> getFacility_list() {
        return facility_list;
    }
    public void setFacility_list(List<MenuCheckData> facility_list) {
        this.facility_list = facility_list;
    }
    public List<MenuCheckData> getFac_cate_list() {
        return fac_cate_list;
    }
    public void setFac_cate_list(List<MenuCheckData> fac_cate_list) {
        this.fac_cate_list = fac_cate_list;
    }
    public List<MenuCheckData> getStructure_list() {
        return structure_list;
    }
    public void setStructure_list(List<MenuCheckData> structure_list) {
        this.structure_list = structure_list;
    }
    public List<MenuCheckData> getResearch_list() {
        return research_list;
    }
    public void setResearch_list(List<MenuCheckData> research_list) {
        this.research_list = research_list;
    }
}
