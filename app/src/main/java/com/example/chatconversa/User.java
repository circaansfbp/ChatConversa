package com.example.chatconversa;

import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String lastname;
    private String run;
    private String username;
    private String email;
    private String password;
    private String tokenEnterprise;
    private String image;
    private String thumbnail;

    public User(String name, String lastname, String run, String username, String email, String password, String tokenEnterprise) {
        this.id = 0;
        this.name = name;
        this.lastname = lastname;
        this.run = run;
        this.username = username;
        this.email = email;
        this.password = password;
        this.tokenEnterprise = tokenEnterprise;
        this.image = null;
        this.thumbnail = null;
    }
}

