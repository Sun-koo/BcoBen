package kr.co.bcoben.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.adapter.ResearchDataListAdapter;
import kr.co.bcoben.component.BaseFragment;
import kr.co.bcoben.databinding.FragmentProjectFacilityBinding;
import kr.co.bcoben.model.ProjectData;

public class ProjectFacilityFragment extends BaseFragment<FragmentProjectFacilityBinding> {

    private static final String ARG_PARAM = "project_data";

    private MainActivity activity;
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

        activity = (MainActivity) getActivity();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_project_facility;
    }
    @Override
    protected void initView() {
        String count = getString(R.string.main_research_count, projectData.getReg_count(), projectData.getTot_count());
        String percent = projectData.getTot_count() == 0 ? "0%" : ((projectData.getReg_count() * 100 / projectData.getTot_count()) + "%");

        dataBinding.txtFacilityPercent.setText(percent);
        dataBinding.txtFacilityCount.setText(count);
        dataBinding.txtResearchNone.setVisibility(projectData.getResearch_list().isEmpty() ? View.VISIBLE : View.GONE);

        adapter = new ResearchDataListAdapter(getActivity(), projectData.getResearch_list());
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
        spinnerList.add("진척율순");
        spinnerList.add("최근작업일순");

        dataBinding.spnResearchOrder.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_spinner_research, spinnerList));
        dataBinding.spnResearchOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activity.currentOrder = (parent.getSelectedItem().toString()).equals("진척율순") ? "progress" : "recent";
                activity.requestProjectDataList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dataBinding.layoutRegisterResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openDrawerResearch(projectData.getFacility_name());
            }
        });
    }
}
