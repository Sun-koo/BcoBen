package kr.co.bcoben.adapter;

import kr.co.bcoben.R;
import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Dir implements LayoutItemType {
    public String dirName;
    public int id;

    public Dir(String dirName, int id) {
        this.dirName = dirName;
        this.id = id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_menu_node;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
