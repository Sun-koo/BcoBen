package kr.co.bcoben.model;

import java.util.ArrayList;

public class UserFacilityListResponseData implements DataModel {
    private ArrayList<UserFacilityList> userFacilityList;

    public ArrayList<UserFacilityList> getUserFacilityList() {
        return userFacilityList;
    }

    public void setUserFacilityList(ArrayList<UserFacilityList> userFacilityList) {
        this.userFacilityList = userFacilityList;
    }

    public class UserFacilityList {
        private String facility_id;
        private String facility_name;

        public UserFacilityList(String facility_id, String facility_name) {
            this.facility_id = facility_id;
            this.facility_name = facility_name;
        }

        public String getFacility_id() {
            return facility_id;
        }

        public void setFacility_id(String facility_id) {
            this.facility_id = facility_id;
        }

        public String getFacility_name() {
            return facility_name;
        }

        public void setFacility_name(String facility_name) {
            this.facility_name = facility_name;
        }
    }
}
