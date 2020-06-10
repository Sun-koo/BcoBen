package kr.co.bcoben.model;

import java.util.ArrayList;

public class UserProjectListResponseData implements DataModel {
    private ArrayList<UserProjectList> userProjectList;

    public ArrayList<UserProjectList> getUserProjectList() {
        return userProjectList;
    }

    public void setUserProjectList(ArrayList<UserProjectList> userProjectList) {
        this.userProjectList = userProjectList;
    }

    public class UserProjectList {
        private String project_id;
        private String project_name;
        private String project_class;
        private String project_class_name;
        private String company_id;

        public UserProjectList(String project_id, String project_name, String project_class, String project_class_name, String company_id) {
            this.project_id = project_id;
            this.project_name = project_name;
            this.project_class = project_class;
            this.project_class_name = project_class_name;
            this.company_id = company_id;
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
    }
}
