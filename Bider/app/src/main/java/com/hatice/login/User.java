package com.hatice.login;

public class User {
    private String name;
    private static User user;

    public User(String name) {
        this.name = name;
    }

    public static void setUser(User _user) {
        user =  _user;
    }

    public static User getUser() {
        return user;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
