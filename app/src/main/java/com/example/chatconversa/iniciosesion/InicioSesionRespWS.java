package com.example.chatconversa.iniciosesion;

import com.example.chatconversa.User;

import java.util.Objects;

public class InicioSesionRespWS {
    private int statusCode;
    private String message;
    private String token;
    private User data;

    public InicioSesionRespWS(int statusCode, String message, String token, User data) {
        this.statusCode = statusCode;
        this.message = message;
        this.token = token;
        this.data = data;
    }

    public InicioSesionRespWS() { }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InicioSesionRespWS that = (InicioSesionRespWS) o;
        return Objects.equals(statusCode, that.statusCode) &&
                Objects.equals(message, that.message) &&
                Objects.equals(token, that.token) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message, token, data);
    }

    @Override
    public String toString() {
        return "InicioSesionRespWS{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                '}';
    }
}
