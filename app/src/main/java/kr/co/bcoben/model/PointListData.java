package kr.co.bcoben.model;

import java.util.List;

public class PointListData implements DataModel {
    private String label_img;
    private int point_next_num;
    private List<PointData> point_list;
    private int reg_count;

    public String getLabel_img() {
        return label_img;
    }
    public int getPoint_next_num() {
        return point_next_num;
    }
    public List<PointData> getPoint_list() {
        return point_list;
    }
    public int getReg_count() {
        return reg_count;
    }
    public void setReg_count(int reg_count) {
        this.reg_count = reg_count;
    }
}
