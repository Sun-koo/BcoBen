package kr.co.bcoben.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import kr.co.bcoben.fragment.ProjectFacilityFragment;
import kr.co.bcoben.model.ProjectData;

public class ProjectDataPageAdapter extends FragmentStatePagerAdapter {

    private List<ProjectData> projectDataList;

    public ProjectDataPageAdapter(@NonNull FragmentManager fm, List<ProjectData> projectDataList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.projectDataList = projectDataList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ProjectFacilityFragment.newInstance(projectDataList.get(position));
    }

    @Override
    public int getCount() {
        return projectDataList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return projectDataList.get(position).getFacility_name();
    }

    public void setProjectDataList(List<ProjectData> projectDataList) {
        this.projectDataList = projectDataList;
        notifyDataSetChanged();
    }

}
