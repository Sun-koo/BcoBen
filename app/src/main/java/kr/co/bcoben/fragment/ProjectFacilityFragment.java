package kr.co.bcoben.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import kr.co.bcoben.model.ProjectResearchList;
import kr.co.bcoben.model.UserData;
import kr.co.bcoben.service.retrofit.RetrofitCallbackModel;
import kr.co.bcoben.service.retrofit.RetrofitClient;

public class ProjectFacilityFragment extends BaseFragment<FragmentProjectFacilityBinding> {

    private static final String ARG_PROJECT_ID = "project_id";
    private static final String ARG_PROJECT_DATA = "project_data";
    private final String[] orderArr = {"progress", "recent"};

    private MainActivity activity;
    private int projectId;
    private ProjectData projectData;
    private List<ProjectResearchList.ProjectResearchData> researchList = new ArrayList<>();
    private ResearchDataListAdapter adapter;
    private boolean isFirst = true;

    // TODO: Rename and change types and number of parameters
    public static ProjectFacilityFragment newInstance(int projectId, ProjectData data) {
        ProjectFacilityFragment fragment = new ProjectFacilityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PROJECT_ID, projectId);
        args.putParcelable(ARG_PROJECT_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            projectId = getArguments().getInt(ARG_PROJECT_ID);
            projectData = getArguments().getParcelable(ARG_PROJECT_DATA);
        }

        activity = (MainActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFirst) {
            dataBinding.spnResearchOrder.setSelection(0);
        }
        if (!isFirst && !activity.isImageIntent) {
            requestResearchList();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_project_facility;
    }
    @Override
    protected void initView() {
        adapter = new ResearchDataListAdapter(activity, researchList);
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

        dataBinding.spnResearchOrder.setAdapter(new ArrayAdapter<>(activity, R.layout.item_spinner_research, spinnerList));
        dataBinding.spnResearchOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst || !researchList.isEmpty()) {
                    requestResearchList();
                    isFirst = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dataBinding.layoutRegisterResearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.openDrawerResearch(projectData.getFacility_id());
            }
        });
    }

    public void requestResearchList() {
        String order = orderArr[dataBinding.spnResearchOrder.getSelectedItemPosition()];
        activity.startLoading();
        RetrofitClient.getRetrofitApi().projectResearchList(UserData.getInstance().getUserId(), projectId, projectData.getFacility_id(), order).enqueue(new RetrofitCallbackModel<ProjectResearchList>() {
            @Override
            public void onResponseData(ProjectResearchList data) {
                activity.endLoading();
                data.setCount();
                researchList = data.getResearch_list();
                adapter.setList(researchList);

                String count = getString(R.string.main_research_count, data.getReg_count(), data.getTot_count());
                String percent = data.getTot_count() == 0 ? "0%" : ((data.getReg_count() * 100 / data.getTot_count()) + "%");

                dataBinding.txtFacilityPercent.setText(percent);
                dataBinding.txtFacilityCount.setText(count);
                dataBinding.txtResearchNone.setVisibility(researchList.isEmpty() ? View.VISIBLE : View.GONE);
                dataBinding.layoutResearchInfo.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCallbackFinish() { activity.endLoading(); }
        });
    }
}
