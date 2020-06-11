package kr.co.bcoben.model;


import java.util.List;

public class FacilityListData implements DataModel {
    private List<FacilityList> user_facility_list;

    public List<FacilityList> getUser_facility_list() {
        return user_facility_list;
    }

    public void setUser_facility_list(List<FacilityList> user_facility_list) {
        this.user_facility_list = user_facility_list;
    }

    public class FacilityList {
        private String facility_id;
        private String facility_name;

        public FacilityList(String facility_id, String facility_name) {
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
