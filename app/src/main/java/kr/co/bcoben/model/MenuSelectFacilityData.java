package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuSelectFacilityData {
    private String facility;
    private String facCate;
    private String arch;
    private List<String> nameList;

    public MenuSelectFacilityData(String facility, String facCate, String arch, List<String> nameList) {
        this.facility = facility;
        this.facCate = facCate;
        this.arch = arch;
        this.nameList = nameList;
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
    public List<String> getNameList() {
        return nameList;
    }
    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
    }
}
