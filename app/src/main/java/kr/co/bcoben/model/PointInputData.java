package kr.co.bcoben.model;

import java.util.ArrayList;
import java.util.List;

public class PointInputData implements DataModel {
    private List<ItemData> material_list;
    private List<ItemData> direction_list;
    private List<ItemData> defect_list;
    private List<ItemData> architecture_list;
    private List<ItemData> length_list;
    private List<ItemData> width_list;
    private List<ItemData> height_list;
    private List<String> materialSpnList;
    private List<String> directionSpnList;
    private List<String> defectSpnList;
    private List<String> architectureSpnList;
    private List<String> lengthSpnList;
    private List<String> widthSpnList;
    private List<String> heightSpnList;

    public List<ItemData> getMaterial_list() {
        return material_list;
    }
    public List<ItemData> getDirection_list() {
        return direction_list;
    }
    public List<ItemData> getDefect_list() {
        return defect_list;
    }
    public List<ItemData> getArchitecture_list() {
        return architecture_list;
    }
    public List<ItemData> getLength_list() {
        return length_list;
    }
    public List<ItemData> getWidth_list() {
        return width_list;
    }
    public List<ItemData> getHeight_list() {
        return height_list;
    }
    public List<String> getMaterialSpnList() {
        return materialSpnList;
    }
    public List<String> getDirectionSpnList() {
        return directionSpnList;
    }
    public List<String> getDefectSpnList() {
        return defectSpnList;
    }
    public List<String> getArchitectureSpnList() {
        return architectureSpnList;
    }
    public List<String> getLengthSpnList() {
        return lengthSpnList;
    }
    public List<String> getWidthSpnList() {
        return widthSpnList;
    }
    public List<String> getHeightSpnList() {
        return heightSpnList;
    }
    public void setSpinnerList() {
        materialSpnList = setSpinnerList(material_list, true);
        directionSpnList = setSpinnerList(direction_list, true);
        defectSpnList = setSpinnerList(defect_list, true);
        architectureSpnList = setSpinnerList(architecture_list, true);
        lengthSpnList = setSpinnerList(length_list, false);
        widthSpnList = setSpinnerList(width_list, false);
        heightSpnList = setSpinnerList(height_list, false);
    }
    private List<String> setSpinnerList(List<ItemData> dataList, boolean isEdit) {
        List<String> spnList = new ArrayList<>();
        spnList.add("");
        for (ItemData data : dataList) {
            spnList.add(data.getItem_name());
        }
        if (isEdit) {
            spnList.add("직접입력");
        }
        return spnList;
    }

    public class ItemData {
        private int item_id;
        private String item_name;

        public int getItem_id() {
            return item_id;
        }
        public String getItem_name() {
            return item_name;
        }
    }
}
