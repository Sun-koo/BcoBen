package kr.co.bcoben.model;

/**
 * 메인화면 조사항목 리스트 데이터
 */
public class MenuSelectFacilityData {
    private String facility;
    private String facCate;
    private String arch;

    public MenuSelectFacilityData(String facility, String facCate, String arch) {
        this.facility = facility;
        this.facCate = facCate;
        this.arch = arch;
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
}
