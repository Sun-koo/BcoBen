package kr.co.bcoben.model;


import java.util.List;

public class FacCateListData implements DataModel {
    private List<FacCateList> user_group_list;

    public List<FacCateList> getUser_group_list() {
        return user_group_list;
    }

    public void setUser_group_list(List<FacCateList> user_group_list) {
        this.user_group_list = user_group_list;
    }

    public class FacCateList {
        private String group_id;
        private String group_name;

        public FacCateList(String group_id, String group_name) {
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
