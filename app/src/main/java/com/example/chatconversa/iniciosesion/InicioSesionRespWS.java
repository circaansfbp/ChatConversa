package com.example.chatconversa.iniciosesion;

import com.example.chatconversa.registrousuarios.User;

import java.util.Objects;

public class InicioSesionRespWS {
    private int status_code;
    private String message;
    private String token;
    private User data;

    public InicioSesionRespWS(int status_code, String message, String token, User data) {
        this.status_code = status_code;
        this.message = message;
        this.token = token;
        this.data = data;
    }

    public InicioSesionRespWS() { }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
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
        return status_code == that.status_code &&
                Objects.equals(message, that.message) &&
                Objects.equals(token, that.token) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status_code, message, token, data);
    }

    @Override
    public String toString() {
        return "InicioSesionRespWS{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                '}';
    }
}
