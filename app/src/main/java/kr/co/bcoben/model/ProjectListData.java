package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 사이드 리스트 데이터
 */
public class ProjectListData implements DataModel {
    private List<ProjectList> user_project_list;

    public List<ProjectList> getUser_project_list() {
        return user_project_list;
    }

    public void setUser_project_list(List<ProjectList> user_project_list) {
        this.user_project_list = user_project_list;
    }

    public class ProjectList {
        private String project_id;
        private String project_name;
        private String project_class;
        private String project_class_name;
        private String company_id;
        private boolean selected;

        public ProjectList(String project_id, String project_name, String project_class, String project_class_name, String company_id) {
            this.project_id = project_id;
            this.project_name = project_name;
            this.project_class = project_class;
            this.project_class_name = project_class_name;
            this.company_id = company_id;
            this.selected = false;
        }

        public String getProject_id() {
            return project_id;
        }

        public void setProject_id(String project_id) {
            this.project_id = project_id;
        }

        public String getProject_name() {
            return project_name;
        }

        public void setProject_name(String project_name) {
            this.project_name = project_name;
        }

        public String getProject_class() {
            return project_class;
        }

        public void setProject_class(String project_class) {
            this.project_class = project_class;
        }

        public String getProject_class_name() {
            return project_class_name;
        }

        public void setProject_class_name(String project_class_name) {
            this.project_class_name = project_class_name;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
