package kr.co.bcoben.model;

public class AppUpdateData implements DataModel {
    private UpdateData update_data;

    public UpdateData getUpdate_data() {
        return update_data;
    }
    public void setUpdate_data(UpdateData update_data) {
        this.update_data = update_data;
    }

    public static class UpdateData {
        private String type;
        private String version;
        private String apk_url;

        public UpdateData(String type, String version, String apk_url) {
            this.type = type;
            this.version = version;
            this.apk_url = apk_url;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getVersion() {
            return version;
        }
        public void setVersion(String version) {
            this.version = version;
        }
        public String getApk_url() {
            return apk_url;
        }
        public void setApk_url(String apk_url) {
            this.apk_url = apk_url;
        }
    }
}
