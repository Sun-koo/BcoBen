package kr.co.bcoben.model;

import java.util.List;

public class ResearchCheckData implements DataModel {
    private ResearchSpinnerData.ResearchSelectData research_data;

    public ResearchCheckData(ResearchSpinnerData.ResearchSelectData research_data) {
        this.research_data = research_data;
    }

    public ResearchSpinnerData.ResearchSelectData getResearch_data() {
        return research_data;
    }
    public void setResearch_data(ResearchSpinnerData.ResearchSelectData research_data) {
        this.research_data = research_data;
    }
}
