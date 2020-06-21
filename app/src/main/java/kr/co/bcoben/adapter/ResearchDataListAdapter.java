package kr.co.bcoben.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.bcoben.R;
import kr.co.bcoben.activity.MainActivity;
import kr.co.bcoben.model.ProjectResearchList;

public class ResearchDataListAdapter extends RecyclerView.Adapter<ResearchDataListAdapter.ResearchDataListHolder> {

    private MainActivity activity;
    private List<ProjectResearchList.ProjectResearchData> list;

    public ResearchDataListAdapter(MainActivity activity, List<ProjectResearchList.ProjectResearchData> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ResearchDataListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_list_research_data, viewGroup, false);
        return new ResearchDataListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResearchDataListHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(List<ProjectResearchList.ProjectResearchData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ResearchDataListHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtResearchArchitecture;
        private TextView txtResearchTitle;
        private TextView txtResearchDate;
        private TextView txtResearchPercent;
        private TextView txtResearchCount;

        ResearchDataListHolder(View view) {
            super(view);
            this.view = view;
            txtResearchArchitecture = view.findViewById(R.id.txt_research_architecture);
            txtResearchTitle = view.findViewById(R.id.txt_research_title);
            txtResearchDate = view.findViewById(R.id.txt_research_date);
            txtResearchPercent = view.findViewById(R.id.txt_research_percent);
            txtResearchCount = view.findViewById(R.id.txt_research_count);
        }

        void onBind(final ProjectResearchList.ProjectResearchData item) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault());
            String arch = item.getFac_cate_name() + ", " + item.getStructure_name();
            String title = item.getResearch_name();
            String date = sdf.format(new Date(item.getUpdate_date()));
            String percent = (item.getReg_count() * 100 / item.getTot_count()) + "%";
            String count = activity.getString(R.string.main_research_count, item.getReg_count(), item.getTot_count());

            txtResearchArchitecture.setText(arch);
            txtResearchTitle.setText(title);
            txtResearchDate.setText(date);
            txtResearchPercent.setText(percent);
            txtResearchCount.setText(count);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.goToDrawingsPage(item.getResearch_id());
                }
            });
        }
    }
}
