package kr.co.bcoben.model;

/**
 * 메인화면 사이드 리스트 데이터
 */
public class ProjectListData {
    private String projectName;
    private boolean selected = false;

    public ProjectListData(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
