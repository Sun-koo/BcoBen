package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 사이드 리스트 데이터
 */
public class ProjectListData implements DataModel {
    private List<ProjectList> project_list;

    public List<ProjectList> getProject_list() {
        return project_list;
    }

    public void setProject_list(List<ProjectList> project_list) {
        this.project_list = project_list;
    }

    public class ProjectList {
        private int project_id;
        private String project_name;
        private String grade_name;
        private boolean selected;

        public ProjectList(int project_id, String project_name, String grade_name) {
            this.project_id = project_id;
            this.project_name = project_name;
            this.grade_name = grade_name;
            this.selected = false;
        }

        public int getProject_id() {
            return project_id;
        }

        public void setProject_id(int project_id) {
            this.project_id = project_id;
        }

        public String getProject_name() {
            return project_name;
        }

        public void setProject_name(String project_name) {
            this.project_name = project_name;
        }

        public String getGrade_name() {
            return grade_name;
        }

        public void setGrade_name(String grade_name) {
            this.grade_name = grade_name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
