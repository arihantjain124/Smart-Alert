package com.idk.smartalert.Model;

public class user {

    private String id;
    private String username;
    private String usertype;

    public user(String id, String username,String usertype) {
        this.id = id;
        this.username = username;
        this.usertype=usertype;
    }

    public user() {

    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}

