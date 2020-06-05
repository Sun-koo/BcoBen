package kr.co.bcoben.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.bcoben.R;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class MenuNodeBinder extends TreeViewBinder<MenuNodeBinder.ViewHolder> {
    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, TreeNode node) {
        Dir dirNode = (Dir) node.getContent();

        holder.layoutFacilityMenu.setVisibility(node.isLeaf() ? View.GONE : View.VISIBLE);
        holder.layoutFacilityChildMenu.setVisibility(node.isLeaf() ? View.VISIBLE : View.GONE);
        holder.lineBottom.setVisibility(node.isLeaf() ? View.GONE : View.VISIBLE);

        if (node.isLeaf()) {
            holder.txtChildName.setText(dirNode.dirName);
        } else {
            holder.txtName.setText(dirNode.dirName);
            holder.txtHandle.setText(node.isExpand() ? R.string.minus : R.string.plus);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_menu_node;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder {
        private TextView txtName, txtHandle, txtChildName, btnReg;
        private RelativeLayout layoutFacilityMenu, layoutFacilityChildMenu;
        private View lineBottom;

        public ViewHolder(View rootView) {
            super(rootView);
            this.txtName = rootView.findViewById(R.id.txt_name);
            this.txtHandle = rootView.findViewById(R.id.txt_handle);
            this.txtChildName = rootView.findViewById(R.id.txt_child_name);
            this.btnReg = rootView.findViewById(R.id.btn_reg);
            this.layoutFacilityMenu = rootView.findViewById(R.id.layout_facility_menu);
            this.layoutFacilityChildMenu = rootView.findViewById(R.id.layout_facility_child_menu);
            this.lineBottom = rootView.findViewById(R.id.line_bottom);
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtHandle() {
            return txtHandle;
        }

        public TextView getTxtChildName() {
            return txtChildName;
        }

        public TextView getBtnReg() {
            return btnReg;
        }

        public RelativeLayout getLayoutFacilityMenu() {
            return layoutFacilityMenu;
        }

        public RelativeLayout getLayoutFacilityChildMenu() {
            return layoutFacilityChildMenu;
        }
    }
}
