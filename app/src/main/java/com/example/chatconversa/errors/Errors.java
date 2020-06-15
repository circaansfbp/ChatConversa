package com.example.chatconversa.errors;

import java.util.Arrays;

public class Errors {
    private String[] name;
    private String[] lastname;
    private String[] run;
    private String[] username;
    private String[] email;
    private String[] password;
    private String[] token_enterprise;

    public Errors(String[] name, String[] lastname, String[] run, String[] username, String[] email, String[] password, String[] token_enterprise) {
        this.name = name;
        this.lastname = lastname;
        this.run = run;
        this.username = username;
        this.email = email;
        this.password = password;
        this.token_enterprise = token_enterprise;
    }

    public Errors() { }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    public String[] getLastname() {
        return lastname;
    }

    public void setLastname(String[] lastname) {
        this.lastname = lastname;
    }

    public String[] getRun() {
        return run;
    }

    public void setRun(String[] run) {
        this.run = run;
    }

    public String[] getUsername() {
        return username;
    }

    public void setUsername(String[] username) {
        this.username = username;
    }

    public String[] getEmail() {
        return email;
    }

    public void setEmail(String[] email) {
        this.email = email;
    }

    public String[] getPassword() {
        return password;
    }

    public void setPassword(String[] password) {
        this.password = password;
    }

    public String[] getToken_enterprise() {
        return token_enterprise;
    }

    public void setToken_enterprise(String[] token_enterprise) {
        this.token_enterprise = token_enterprise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Errors errors = (Errors) o;
        return Arrays.equals(name, errors.name) &&
                Arrays.equals(lastname, errors.lastname) &&
                Arrays.equals(run, errors.run) &&
                Arrays.equals(username, errors.username) &&
                Arrays.equals(email, errors.email) &&
                Arrays.equals(password, errors.password) &&
                Arrays.equals(token_enterprise, errors.token_enterprise);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(name);
        result = 31 * result + Arrays.hashCode(lastname);
        result = 31 * result + Arrays.hashCode(run);
        result = 31 * result + Arrays.hashCode(username);
        result = 31 * result + Arrays.hashCode(email);
        result = 31 * result + Arrays.hashCode(password);
        result = 31 * result + Arrays.hashCode(token_enterprise);
        return result;
    }

    @Override
    public String toString() {
        return "Error{" +
                "name=" + Arrays.toString(name) +
                ", lastname=" + Arrays.toString(lastname) +
                ", run=" + Arrays.toString(run) +
                ", username=" + Arrays.toString(username) +
                ", email=" + Arrays.toString(email) +
                ", password=" + Arrays.toString(password) +
                ", token_enterprise=" + Arrays.toString(token_enterprise) +
                '}';
    }
}
