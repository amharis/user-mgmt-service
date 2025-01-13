package com.user.mgmt.model;

public class User {
    private String id;

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

    private String username;

    public User() {}

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }


}
