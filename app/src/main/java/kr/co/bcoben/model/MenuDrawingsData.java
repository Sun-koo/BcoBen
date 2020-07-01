package kr.co.bcoben.model;

import android.net.Uri;


public class MenuDrawingsData {
    private String name;
    private String facility;
//    private int facilityId;
    private Uri uri;

    public MenuDrawingsData(String name, String facility, Uri uri) {
        this.name = name;
        this.facility = facility;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFacility() {
        return facility;
    }
    public void setFacility(String facility) {
        this.facility = facility;
    }
    public Uri getUri() {
        return uri;
    }
    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
