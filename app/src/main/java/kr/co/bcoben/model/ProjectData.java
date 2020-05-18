package kr.co.bcoben.model;

public class ProjectData {
    private String projectName;
    private boolean selected = false;

    public ProjectData(String projectName) {
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
