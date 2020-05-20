package kr.co.bcoben.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kr.co.bcoben.R;
import kr.co.bcoben.model.ProjectResearchData;

public class ProjectFacilityFragment extends Fragment {

    private static final String ARG_PARAM = "project research data";

    private List<ProjectResearchData> dataList;

    // TODO: Rename and change types and number of parameters
    public static ProjectFacilityFragment newInstance(List<ProjectResearchData> list) {
        ProjectFacilityFragment fragment = new ProjectFacilityFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, (ArrayList<? extends Parcelable>) list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dataList = getArguments().getParcelableArrayList(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_facility, container, false);

        return view;
    }
}
