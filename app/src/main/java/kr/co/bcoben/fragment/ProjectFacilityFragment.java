package kr.co.bcoben.fragment;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.adapter.CustomSpinnerAdapter;
import kr.co.bcoben.adapter.ResearchDataListAdapter;
import kr.co.bcoben.component.BaseFragment;
import kr.co.bcoben.databinding.FragmentProjectFacilityBinding;
import kr.co.bcoben.model.ProjectData;
import kr.co.bcoben.model.ProjectResearchData;

public class ProjectFacilityFragment extends BaseFragment<FragmentProjectFacilityBinding> {

    private static final String ARG_PARAM = "project_data";

    private ProjectData projectData;
    private ResearchDataListAdapter adapter;

    // TODO: Rename and change types and number of parameters
    public static ProjectFacilityFragment newInstance(ProjectData data) {
        ProjectFacilityFragment fragment = new ProjectFacilityFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectData = getArguments().getParcelable(ARG_PARAM);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_project_facility;
    }
    @Override
    protected void initView() {
        String count = getString(R.string.main_research_count, projectData.getRegCount(), projectData.getTotCount());
        String percent = (projectData.getRegCount() * 100 / projectData.getTotCount()) + "%";

        dataBinding.txtFacilityPercent.setText(percent);
        dataBinding.txtFacilityCount.setText(count);

        adapter = new ResearchDataListAdapter(getActivity(), projectData.getResearchList());
        dataBinding.recyclerResearch.setLayoutManager(new GridLayoutManager(getContext(), 2));
        dataBinding.recyclerResearch.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                outRect.bottom = 17;
                outRect.right = position % 2 == 0 ? 26 : 0;
            }
        });
        dataBinding.recyclerResearch.setAdapter(adapter);

        List<String> spinnerList = new ArrayList<>();
        spinnerList.add("전체");
        spinnerList.add("건축");
        spinnerList.add("지하도상가");

        dataBinding.spnResearchOrder.setAdapter(new CustomSpinnerAdapter(getActivity(), R.layout.item_spinner_research, spinnerList));
    }
}
