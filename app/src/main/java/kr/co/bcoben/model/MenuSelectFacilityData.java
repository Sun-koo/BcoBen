package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuSelectFacilityData {
    private String facility;
    private String facCate;
    private String arch;
    private List<Integer> idList;

    public MenuSelectFacilityData(String facility, String facCate, String arch, List<Integer> idList) {
        this.facility = facility;
        this.facCate = facCate;
        this.arch = arch;
        this.idList = idList;
    }

    public String getFacility() {
        return facility;
    }
    public void setFacility(String facility) {
        this.facility = facility;
    }
    public String getFacCate() {
        return facCate;
    }
    public void setFacCate(String facCate) {
        this.facCate = facCate;
    }
    public String getArch() {
        return arch;
    }
    public void setArch(String arch) {
        this.arch = arch;
    }
    public List<Integer> getIdList() {
        return idList;
    }
    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }
}
