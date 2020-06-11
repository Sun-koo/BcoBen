package kr.co.bcoben.model;

public class LoginData implements DataModel {
    private User user;

    public LoginData(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class User {
        private int user_id;
        private String id;
        private String name;
        private String company_id;
        private String device_id;

        public User(int user_id, String id, String name, String company_id, String device_id) {
            this.user_id = user_id;
            this.id = id;
            this.name = name;
            this.company_id = company_id;
            this.device_id = device_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }
    }
}
