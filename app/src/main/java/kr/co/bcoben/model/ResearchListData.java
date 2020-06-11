package kr.co.bcoben.model;


import java.util.List;

public class ResearchListData implements DataModel {
    private List<ResearchList> user_checktype_list;

    public List<ResearchList> getUser_checktype_list() {
        return user_checktype_list;
    }

    public void setUser_checktype_list(List<ResearchList> user_checktype_list) {
        this.user_checktype_list = user_checktype_list;
    }

    public class ResearchList {
        private String checktype_id;
        private String checktype_name;
        private int goalcount;

        public ResearchList(String checktype_id, String checktype_name, int goalcount) {
            this.checktype_id = checktype_id;
            this.checktype_name = checktype_name;
            this.goalcount = goalcount;
        }

        public String getChecktype_id() {
            return checktype_id;
        }

        public void setChecktype_id(String checktype_id) {
            this.checktype_id = checktype_id;
        }

        public String getChecktype_name() {
            return checktype_name;
        }

        public void setChecktype_name(String checktype_name) {
            this.checktype_name = checktype_name;
        }

        public int getGoalcount() {
            return goalcount;
        }

        public void setGoalcount(int goalcount) {
            this.goalcount = goalcount;
        }
    }
}
