package com.example.fittest;

public class User {

    String email;
    boolean email_verified;
    String name;
    String uid;

    public User(){}

    public User(String email, boolean email_verified, String name, String uid) {
        this.email = email;
        this.email_verified = email_verified;
        this.name = name;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(boolean email_verified) {
        this.email_verified = email_verified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
