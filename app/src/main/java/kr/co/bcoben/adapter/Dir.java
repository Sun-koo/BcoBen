package kr.co.bcoben.adapter;

import kr.co.bcoben.R;
import tellh.com.recyclertreeview_lib.LayoutItemType;

public class Dir implements LayoutItemType {
    public String dirName;

    public Dir(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_menu_node;
    }
}
