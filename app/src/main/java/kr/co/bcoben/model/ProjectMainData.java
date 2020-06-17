package kr.co.bcoben.model;

import java.util.List;

/**
 * 메인화면 사이드 리스트 데이터
 */
public class ProjectMainData implements DataModel {
    private List<ProjectData> facility_list;
    private ResearchRegData research_reg_data;

    public List<ProjectData> getFacility_list() {
        return facility_list;
    }

    public void setFacility_list(List<ProjectData> facility_list) {
        this.facility_list = facility_list;
    }

    public ResearchRegData getResearch_reg_data() {
        return research_reg_data;
    }

    public void setResearch_reg_data(ResearchRegData research_reg_data) {
        this.research_reg_data = research_reg_data;
    }

    public class ResearchRegData {
        private String project_name;
        private String grade_name;
        private List<FacilityList> facility_list;
        private List<ResearchList> research_list;

        public ResearchRegData(String project_name, String grade_name, List<FacilityList> facility_list, List<ResearchList> research_list) {
            this.project_name = project_name;
            this.grade_name = grade_name;
            this.facility_list = facility_list;
            this.research_list = research_list;
        }

        public String getProject_name() {
            return project_name;
        }
        public void setProject_name(String project_name) {
            this.project_name = project_name;
        }
        public String getGrade_name() {
            return grade_name;
        }
        public void setGrade_name(String grade_name) {
            this.grade_name = grade_name;
        }
        public List<FacilityList> getFacility_list() {
            return facility_list;
        }
        public void setFacility_list(List<FacilityList> facility_list) {
            this.facility_list = facility_list;
        }
        public List<ResearchList> getResearch_list() {
            return research_list;
        }
        public void setResearch_list(List<ResearchList> research_list) {
            this.research_list = research_list;
        }

        public class FacilityList {
            private String item_id;
            private String item_name;
            private List<FacCateList> fac_cate_list;

            public FacilityList(String item_id, String item_name, List<FacCateList> fac_cate_list) {
                this.item_id = item_id;
                this.item_name = item_name;
                this.fac_cate_list = fac_cate_list;
            }

            public String getItem_id() {
                return item_id;
            }
            public void setItem_id(String item_id) {
                this.item_id = item_id;
            }
            public String getItem_name() {
                return item_name;
            }
            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }
            public List<FacCateList> getFac_cate_list() {
                return fac_cate_list;
            }
            public void setFac_cate_list(List<FacCateList> fac_cate_list) {
                this.fac_cate_list = fac_cate_list;
            }

            public class FacCateList {
                private String item_id;
                private String item_name;
                private List<ArchitectureList> structure_list;

                public FacCateList(String item_id, String item_name, List<ArchitectureList> structure_list) {
                    this.item_id = item_id;
                    this.item_name = item_name;
                    this.structure_list = structure_list;
                }

                public String getItem_id() {
                    return item_id;
                }
                public void setItem_id(String item_id) {
                    this.item_id = item_id;
                }
                public String getItem_name() {
                    return item_name;
                }
                public void setItem_name(String item_name) {
                    this.item_name = item_name;
                }
                public List<ArchitectureList> getStructure_list() {
                    return structure_list;
                }
                public void setStructure_list(List<ArchitectureList> structure_list) {
                    this.structure_list = structure_list;
                }

                public class ArchitectureList {
                    private String item_id;
                    private String item_name;

                    public ArchitectureList(String item_id, String item_name) {
                        this.item_id = item_id;
                        this.item_name = item_name;
                    }

                    public String getItem_id() {
                        return item_id;
                    }
                    public void setItem_id(String item_id) {
                        this.item_id = item_id;
                    }
                    public String getItem_name() {
                        return item_name;
                    }
                    public void setItem_name(String item_name) {
                        this.item_name = item_name;
                    }
                }
            }
        }

        public class ResearchList {
            private int item_id;
            private String item_name;
            private int tot_count;

            public ResearchList(int item_id, String item_name, int tot_count) {
                this.item_id = item_id;
                this.item_name = item_name;
                this.tot_count = tot_count;
            }

            public int getItem_id() {
                return item_id;
            }
            public void setItem_id(int item_id) {
                this.item_id = item_id;
            }
            public String getItem_name() {
                return item_name;
            }
            public void setItem_name(String item_name) {
                this.item_name = item_name;
            }
            public int getTot_count() {
                return tot_count;
            }
            public void setTot_count(int tot_count) {
                this.tot_count = tot_count;
            }
        }
    }
}
