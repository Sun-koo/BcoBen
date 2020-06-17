package kr.co.bcoben.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class MenuNodeBinder extends TreeViewBinder<MenuNodeBinder.ViewHolder> {

    private Activity activity;

    public MenuNodeBinder(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, final TreeNode node) {
        Dir dirNode = (Dir) node.getContent();

        holder.layoutFacilityMenu.setVisibility(node.isLeaf() ? View.GONE : View.VISIBLE);
        holder.layoutFacilityChildMenu.setVisibility(node.isLeaf() && !dirNode.getDirName().equals("") ? View.VISIBLE : View.GONE);
        holder.lineBottom.setVisibility(node.isLeaf() ? View.GONE : View.VISIBLE);
        holder.btnNext.setVisibility(dirNode.getDirName().equals("") ? View.VISIBLE : View.GONE);

        if (node.isLeaf()) {
            holder.txtChildName.setText(dirNode.dirName);
            holder.btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dir masterDir = (Dir) node.getParent().getParent().getContent();
                    Dir parentDir = (Dir) node.getParent().getContent();
                    Dir childDir = (Dir) node.getContent();

                    List<Integer> idList = new ArrayList<>();
                    idList.add(masterDir.getId());
                    idList.add(parentDir.getId());
                    idList.add(childDir.getId());
                    ((MainActivity) activity).setSelectedFacilityData(masterDir.getDirName(), parentDir.getDirName(), childDir.getDirName(), idList);
                }
            });
        } else {
            holder.txtName.setText(dirNode.dirName);
            holder.txtName.setTypeface(node.isExpand() ? holder.txtName.getTypeface() : null, node.isExpand() ? Typeface.BOLD : Typeface.NORMAL);
            holder.txtHandle.setText(node.isExpand() ? R.string.minus : R.string.plus);
        }

        holder.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).projectNextStep();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_menu_node;
    }

    public static class ViewHolder extends TreeViewBinder.ViewHolder {
        private TextView txtName, txtHandle, txtChildName, btnReg;
        private RelativeLayout layoutFacilityMenu, layoutFacilityChildMenu;
        private View lineBottom;
        private Button btnNext;

        public ViewHolder(View rootView) {
            super(rootView);
            this.txtName = rootView.findViewById(R.id.txt_name);
            this.txtHandle = rootView.findViewById(R.id.txt_handle);
            this.txtChildName = rootView.findViewById(R.id.txt_child_name);
            this.btnReg = rootView.findViewById(R.id.btn_reg);
            this.layoutFacilityMenu = rootView.findViewById(R.id.layout_facility_menu);
            this.layoutFacilityChildMenu = rootView.findViewById(R.id.layout_facility_child_menu);
            this.lineBottom = rootView.findViewById(R.id.line_bottom);
            this.btnNext = rootView.findViewById(R.id.btn_project_next);
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtHandle() {
            return txtHandle;
        }
    }
}
