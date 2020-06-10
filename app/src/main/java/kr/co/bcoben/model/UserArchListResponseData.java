package kr.co.bcoben.model;

import java.util.ArrayList;

public class UserArchListResponseData implements DataModel {
    private ArrayList<UserArchList> userStructureList;

    public ArrayList<UserArchList> getUserStructureList() {
        return userStructureList;
    }

    public void setUserStructureList(ArrayList<UserArchList> userStructureList) {
        this.userStructureList = userStructureList;
    }

    public class UserArchList {
        private String structure_id;
        private String structure_name;

        public UserArchList(String structure_id, String structure_name) {
            this.structure_id = structure_id;
            this.structure_name = structure_name;
        }

        public String getStructure_id() {
            return structure_id;
        }

        public void setStructure_id(String structure_id) {
            this.structure_id = structure_id;
        }

        public String getStructure_name() {
            return structure_name;
        }

        public void setStructure_name(String structure_name) {
            this.structure_name = structure_name;
        }
    }
}
