package kr.co.bcoben.model;

public class ResearchIdData implements DataModel {
    private int research_id;

    public ResearchIdData(int research_id) {
        this.research_id = research_id;
    }

    public int getResearch_id() {
        return research_id;
    }
    public void setResearch_id(int research_id) {
        this.research_id = research_id;
    }
}
