package kr.co.bcoben.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

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

    public static class PlanData implements Parcelable {
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

        protected PlanData(Parcel in) {
            plan_id = in.readInt();
            plan_name = in.readString();
            plan_img = in.readString();
            plan_thumb = in.readString();
            plan_img_file = in.readString();
        }

        public static final Creator<PlanData> CREATOR = new Creator<PlanData>() {
            @Override
            public PlanData createFromParcel(Parcel in) {
                return new PlanData(in);
            }

            @Override
            public PlanData[] newArray(int size) {
                return new PlanData[size];
            }
        };

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

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof PlanData) {
                PlanData data = (PlanData) obj;
                return plan_id == data.getPlan_id() && plan_name.equals(data.getPlan_name()) && plan_img.equals(data.getPlan_img()) && plan_thumb.equals(data.getPlan_thumb());
            }
            return false;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(plan_id);
            dest.writeString(plan_name);
            dest.writeString(plan_img);
            dest.writeString(plan_thumb);
            dest.writeString(plan_img_file);
        }
    }
}
