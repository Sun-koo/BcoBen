package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 사이드 리스트 데이터
 */
public class PlanListData implements DataModel {
    private List<PlanList> plan_list;

    public List<PlanList> getPlan_list() {
        return plan_list;
    }

    public void setPlan_list(List<PlanList> plan_list) {
        this.plan_list = plan_list;
    }

    public class PlanList {
        private int plan_id;
        private String plan_name;
        private String plan_img;
        private String plan_thumb;

        public PlanList(int plan_id, String plan_name, String plan_img, String plan_thumb) {
            this.plan_id = plan_id;
            this.plan_name = plan_name;
            this.plan_img = plan_img;
            this.plan_thumb = plan_thumb;
        }

        public int getPlan_id() {
            return plan_id;
        }
        public void setPlan_id(int plan_id) {
            this.plan_id = plan_id;
        }
        public String getPlan_name() {
            return plan_name;
        }
        public void setPlan_name(String plan_name) {
            this.plan_name = plan_name;
        }
        public String getPlan_img() {
            return plan_img;
        }
        public void setPlan_img(String plan_img) {
            this.plan_img = plan_img;
        }
        public String getPlan_thumb() {
            return plan_thumb;
        }
        public void setPlan_thumb(String plan_thumb) {
            this.plan_thumb = plan_thumb;
        }
    }
}
