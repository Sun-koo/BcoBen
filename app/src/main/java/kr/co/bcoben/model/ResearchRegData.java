package kr.co.bcoben.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchRegData {
    private String project_name;
    private String grade_name;
    private List<FacilityData> facility_list;
    private List<ResearchData> research_list;

    public ResearchRegData(String project_name, String grade_name, List<FacilityData> facility_list, List<ResearchData> research_list) {
        this.project_name = project_name;
        this.grade_name = grade_name;
        this.facility_list = facility_list;
        this.research_list = research_list;
    }

    public String getProject_name() {
        return project_name;
    }
    public String getGrade_name() {
        return grade_name;
    }
    public void addFacility(FacilityData data) {
        facility_list.add(data);
    }
    public void addResearch(String item_name, int tot_count) {
        research_list.add(new ResearchData(0, item_name, tot_count));
    }
    public FacilityData getFacilityDataID(int itemId) {
        for (FacilityData d : facility_list) {
            if (d.getItem_id() == itemId) {
                return d;
            }
        }
        return null;
    }
    public ResearchData getResearchDataID(int itemId) {
        for (ResearchData d : research_list) {
            if (d.getItem_id() == itemId) {
                return d;
            }
        }
        return null;
    }
    public List<MenuCheckData> getFacilityList() {
        List<MenuCheckData> list = new ArrayList<>();
        for (FacilityData d : facility_list) {
            list.add(d.getData());
        }
        return list;
    }
    public List<MenuCheckData> getResearchList() {
        List<MenuCheckData> list = new ArrayList<>();
        for (ResearchData d : research_list) {
            list.add(d.getData());
        }
        return list;
    }

    // 시설물 데이터
    public static class FacilityData extends MenuCheckData {
        private List<FacCateData> fac_cate_list;

        public FacilityData(int item_id, String item_name, List<FacCateData> fac_cate_list) {
            super(item_id, item_name);
            this.fac_cate_list = fac_cate_list;
        }
        public void addFacCate(FacCateData data) {
            fac_cate_list.add(data);
        }
        public FacCateData getFacCateDataID(int itemId) {
            if (fac_cate_list != null) {
                for (FacCateData d : fac_cate_list) {
                    if (d.getItem_id() == itemId) {
                        return d;
                    }
                }
            }
            return null;
        }
        public List<MenuCheckData> getFacCateList() {
            List<MenuCheckData> list = new ArrayList<>();
            if (fac_cate_list != null) {
                for (FacCateData d : fac_cate_list) {
                    list.add(d.getData());
                }
            }
            return list;
        }

        // 시설물분류 데이터
        public static class FacCateData extends MenuCheckData {
            private List<ArchitectureData> structure_list;

            public FacCateData(int item_id, String item_name, List<ArchitectureData> structure_list) {
                super(item_id, item_name);
                this.structure_list = structure_list;
            }
            public void addArchitecture(ArchitectureData data) {
                structure_list.add(data);
            }
            public ArchitectureData getArchitectureDataID(int itemId) {
                if (structure_list != null) {
                    for (ArchitectureData d : structure_list) {
                        if (d.getItem_id() == itemId) {
                            return d;
                        }
                    }
                }
                return null;
            }
            public List<MenuCheckData> getArchitectureList() {
                List<MenuCheckData> list = new ArrayList<>();
                if (structure_list != null) {
                    for (ArchitectureData d : structure_list) {
                        list.add(d.getData());
                    }
                }
                return list;
            }

            // 구조형식 데이터
            public static class ArchitectureData extends MenuCheckData {
                public ArchitectureData(int item_id, String item_name) {
                    super(item_id, item_name);
                }
            }
        }
    }

    // 조사종류 데이터
    public static class ResearchData extends MenuCheckData {
        public ResearchData(int item_id, String item_name, int tot_count) {
            super(item_id, item_name, tot_count);
        }
    }
}
