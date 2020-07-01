package kr.co.bcoben.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

import static kr.co.bcoben.util.CommonUtil.hideKeyboard;

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
    public void bindView(final ViewHolder holder, int position, final TreeNode node) {
        final Dir dirNode = (Dir) node.getContent();

        holder.layoutFacilityMenu.setVisibility(node.isLeaf() ? View.GONE : View.VISIBLE);
        holder.layoutFacilityChildMenu.setVisibility(node.isLeaf() && !dirNode.getDirName().equals("") ? View.VISIBLE : View.GONE);
        holder.lineBottom.setVisibility(node.isLeaf() ? View.GONE : View.VISIBLE);
//        holder.btnNext.setVisibility(dirNode.getDirName().equals("") ? View.VISIBLE : View.GONE);
        holder.btnNext.setVisibility(dirNode.getDirName().equals("") && dirNode.getId() == -10 ? View.VISIBLE : View.GONE);
        // 직접입력(-1 : 시설물 / -2 : 시설물 분류 / -3 : 구조형식)
        holder.layoutSelf.setVisibility(dirNode.getId() == -1 || dirNode.getId() == -2 || dirNode.getId() == -3? View.VISIBLE : View.GONE);
        holder.lineBottomSelf.setVisibility(holder.layoutSelf.getVisibility());

        if (node.isLeaf()) {
            holder.txtChildName.setText(dirNode.dirName);
            holder.btnReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dir masterDir = (Dir) node.getParent().getParent().getContent();
                    Dir parentDir = (Dir) node.getParent().getContent();
                    Dir childDir = (Dir) node.getContent();

                    List<String> nameList = new ArrayList<>();
                    nameList.add(masterDir.getDirName());
                    nameList.add(parentDir.getDirName());
                    nameList.add(childDir.getDirName());
                    ((MainActivity) activity).setSelectedFacilityData(masterDir.getDirName(), parentDir.getDirName(), childDir.getDirName(), nameList);
                }
            });
            holder.btnRegSelf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputSelf = holder.editSelf.getText().toString();
                    if (inputSelf.isEmpty()) {
                        return;
                    }
                    ((MainActivity) activity).updateFacilityTreeData(inputSelf, dirNode.getId());

                    holder.editSelf.getText().clear();
                    hideKeyboard(activity);
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
        private RelativeLayout layoutFacilityMenu, layoutFacilityChildMenu, layoutSelf;
        private EditText editSelf;
        private View lineBottom, lineBottomSelf;
        private Button btnNext, btnRegSelf;

        public ViewHolder(View rootView) {
            super(rootView);
            this.txtName = rootView.findViewById(R.id.txt_name);
            this.txtHandle = rootView.findViewById(R.id.txt_handle);
            this.txtChildName = rootView.findViewById(R.id.txt_child_name);
            this.btnReg = rootView.findViewById(R.id.btn_reg);
            this.layoutFacilityMenu = rootView.findViewById(R.id.layout_facility_menu);
            this.layoutFacilityChildMenu = rootView.findViewById(R.id.layout_facility_child_menu);
            this.layoutSelf = rootView.findViewById(R.id.layout_self);
            this.editSelf = rootView.findViewById(R.id.edit_self);
            this.lineBottom = rootView.findViewById(R.id.line_bottom);
            this.lineBottomSelf = rootView.findViewById(R.id.line_bottom_self);
            this.btnNext = rootView.findViewById(R.id.btn_project_next);
            this.btnRegSelf = rootView.findViewById(R.id.btn_reg_self);
        }

        public TextView getTxtName() {
            return txtName;
        }

        public TextView getTxtHandle() {
            return txtHandle;
        }
    }
}
