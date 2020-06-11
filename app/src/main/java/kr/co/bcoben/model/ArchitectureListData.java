package kr.co.bcoben.model;

import java.util.List;

public class ArchitectureListData implements DataModel {
    private List<ArchitectureList> user_structure_list;

    public List<ArchitectureList> getUser_structure_list() {
        return user_structure_list;
    }

    public void setUser_structure_list(List<ArchitectureList> user_structure_list) {
        this.user_structure_list = user_structure_list;
    }

    public class ArchitectureList {
        private String structure_id;
        private String structure_name;

        public ArchitectureList(String structure_id, String structure_name) {
            this.structure_id = structure_id;
            this.structure_name = structure_name;
        }

        public String getStructure_id() {
            return structure_id;
        }

        public void setStructure_id(String structure_id) {
            this.structure_id = structure_id;
        }

        public String getStructure_name() {
            return structure_name;
        }

        public void setStructure_name(String structure_name) {
            this.structure_name = structure_name;
        }
    }
}
