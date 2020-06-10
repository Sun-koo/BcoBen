package kr.co.bcoben.model;

public class LoginResponseData implements DataModel {
    private User user;

    public LoginResponseData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class User {
        private int userId;
        private String id;
        private String name;
        private String companyId;
        private String deviceId;

        public User(int userId, String id, String name, String companyId, String deviceId) {
            this.userId = userId;
            this.id = id;
            this.name = name;
            this.companyId = companyId;
            this.deviceId = deviceId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }
    }
}
