package com.idk.smartalert.Model;

public class userloginmodel {
    private String id;
    private String usertype;

    public userloginmodel() {
    }

    public userloginmodel(String id, String usertype) {
        this.id = id;
        this.usertype = usertype;
    }

    public String getId() {
        return id;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }
}
