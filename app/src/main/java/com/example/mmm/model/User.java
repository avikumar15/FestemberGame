package com.example.mmm.model;

import java.io.Serializable;

public class User implements Serializable {

    public String Username;
    public String Email;
    public String Password;
    public String Name;

    public User() {}

    public User(String Username, String email, String password, String name) {
        this.Username = Username;
        this.Email = email;
        this.Password = password;
        this.Name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "Username='" + Username + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", Name='" + Name + '\'' +
                '}';
    }
}
