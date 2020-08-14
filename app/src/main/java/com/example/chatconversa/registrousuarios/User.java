package com.example.chatconversa.registrousuarios;

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

    public User(int id, String name, String lastname, String run, String username, String email, String password, String tokenEnterprise, String image, String thumbnail) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.run = run;
        this.username = username;
        this.email = email;
        this.password = password;
        this.tokenEnterprise = tokenEnterprise;
        this.image = image;
        this.thumbnail = thumbnail;
    }

    public User() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenEnterprise() {
        return tokenEnterprise;
    }

    public void setTokenEnterprise(String tokenEnterprise) {
        this.tokenEnterprise = tokenEnterprise;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(lastname, user.lastname) &&
                Objects.equals(run, user.run) &&
                Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(tokenEnterprise, user.tokenEnterprise) &&
                Objects.equals(image, user.image) &&
                Objects.equals(thumbnail, user.thumbnail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastname, run, username, email, password, tokenEnterprise, image, thumbnail);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", run='" + run + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", tokenEnterprise='" + tokenEnterprise + '\'' +
                ", image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}

