package kr.co.bcoben.model;

import java.util.ArrayList;

public class UserResearchListResponseData implements DataModel {
    private ArrayList<UserResearchList> userCheckTypeList;

    public ArrayList<UserResearchList> getUserCheckTypeList() {
        return userCheckTypeList;
    }

    public void setUserCheckTypeList(ArrayList<UserResearchList> userCheckTypeList) {
        this.userCheckTypeList = userCheckTypeList;
    }

    public class UserResearchList {
        private String checktype_id;
        private String checktype_name;
        private int goalcount;

        public UserResearchList(String checktype_id, String checktype_name, int goalcount) {
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
