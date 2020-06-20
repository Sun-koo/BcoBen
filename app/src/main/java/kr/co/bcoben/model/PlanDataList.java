package kr.co.bcoben.model;

import android.graphics.Bitmap;

import java.util.List;

import kr.co.bcoben.util.CommonUtil;

/**
 * 도면 리스트 데이터
 */
public class PlanDataList implements DataModel {
    private List<PlanData> plan_list;

    public List<PlanData> getPlan_list() {
        return plan_list;
    }
    public void setPlan_list(List<PlanData> plan_list) {
        this.plan_list = plan_list;
    }
    public void setPlanImgFile() {
        for (PlanData data : plan_list) {
            String img = data.getPlan_img();
            if (img != null && !img.equals("")) {
                String[] pathArr = img.split("/");
                String file = CommonUtil.getFilePath() + "/" + pathArr[pathArr.length - 2] + "/" + pathArr[pathArr.length - 1];
                data.setPlan_img_file(file);
            }
        }
    }

    public static class PlanData {
        private int plan_id;
        private String plan_name;
        private String plan_img;
        private String plan_thumb;
        private String plan_img_file;
        private Bitmap plan_bitmap;
        private int downPercent = 0;

        public PlanData(int plan_id, String plan_name, String plan_img, String plan_thumb) {
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
        public String getPlan_img_file() {
            return plan_img_file;
        }
        public void setPlan_img_file(String plan_img_file) {
            this.plan_img_file = plan_img_file;
        }
        public Bitmap getPlan_bitmap() {
            return plan_bitmap;
        }
        public void setPlan_bitmap(Bitmap plan_bitmap) {
            this.plan_bitmap = plan_bitmap;
        }
        public int getDownPercent() {
            return downPercent;
        }
        public void setDownPercent(int downPercent) {
            this.downPercent = downPercent;
        }
    }
}
