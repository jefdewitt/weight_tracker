package com.zybooks.cs360appjefdewitt;

public class Account {
    private int mId;
    private String mUsername;
    private String mPassword;

    public Account() {}

    public Account(int id, String username, String password) {
        mId = id;
        mUsername = username;
        mPassword = password;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }
}
