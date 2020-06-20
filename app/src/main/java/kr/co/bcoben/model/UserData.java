package kr.co.bcoben.model;

public class UserData {
    private static UserData instance;

    private int userId;
    private String deviceId;

    public static UserData getInstance() {
        if(instance == null){
            synchronized (UserData.class) {
                if(instance == null)
                    instance = new UserData();
            }
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public void removeData() {
        instance = null;
    }
}
