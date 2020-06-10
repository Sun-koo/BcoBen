package kr.co.bcoben.model;

import java.util.ArrayList;

public class UserFacCateListResponseData implements DataModel {
    private ArrayList<UserFacCateList> userGroupList;

    public ArrayList<UserFacCateList> getUserGroupList() {
        return userGroupList;
    }

    public void setUserGroupList(ArrayList<UserFacCateList> userGroupList) {
        this.userGroupList = userGroupList;
    }

    public class UserFacCateList {
        private String group_id;
        private String group_name;

        public UserFacCateList(String group_id, String group_name) {
            this.group_id = group_id;
            this.group_name = group_name;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }
    }
}
