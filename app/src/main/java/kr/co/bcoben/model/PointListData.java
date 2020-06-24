package kr.co.bcoben.model;

import java.util.List;

public class PointListData implements DataModel {
    private String label_img;
    private List<PointData> point_list;

    public String getLabel_img() {
        return label_img;
    }
    public List<PointData> getPoint_list() {
        return point_list;
    }
}
