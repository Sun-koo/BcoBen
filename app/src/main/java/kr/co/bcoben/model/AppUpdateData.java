package kr.co.bcoben.model;

import android.util.Log;

import static kr.co.bcoben.util.CommonUtil.getAppVersion;

public class AppUpdateData implements DataModel {
    private UpdateData update_data;

    public UpdateData getUpdate_data() {
        return update_data;
    }

    public class UpdateData {
        private String type;
        private String version;
        private String apk_url;

        public String getType() {
            return type;
        }
        public String getVersion() {
            return version;
        }
        public String getApk_url() {
            return apk_url;
        }
        public boolean isUpdate() {
            if (version != null) {
                if (!version.equals(getAppVersion())) {
                    int updateVersion = Integer.parseInt(version.replace(".", ""));
                    int currentVersion = Integer.parseInt(getAppVersion().replace(".", ""));
                    return updateVersion > currentVersion;
                }
            }
            return false;
        }
    }
}
