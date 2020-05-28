package kr.co.bcoben.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import kr.co.bcoben.fragment.ProjectFacilityFragment;
import kr.co.bcoben.model.ProjectData;

public class ProjectDataPageAdapter extends FragmentStateAdapter {

    private List<ProjectData> projectDataList;

    public ProjectDataPageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<ProjectData> projectDataList) {
        super(fragmentManager, lifecycle);
        this.projectDataList = projectDataList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ProjectFacilityFragment.newInstance(projectDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return projectDataList.size();
    }

    public void setProjectDataList(List<ProjectData> projectDataList) {
        this.projectDataList = projectDataList;
        notifyDataSetChanged();
    }
}
