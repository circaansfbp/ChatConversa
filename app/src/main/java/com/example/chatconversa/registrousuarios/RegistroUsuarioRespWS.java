package com.example.chatconversa.registrousuarios;

import com.example.chatconversa.User;

import java.util.Objects;

public class RegistroUsuarioRespWS {
    private int status_code;
    private String message;
    private User data;

    public RegistroUsuarioRespWS(int status_code, String message, User data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
    }

    public RegistroUsuarioRespWS() { }

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
        RegistroUsuarioRespWS that = (RegistroUsuarioRespWS) o;
        return status_code == that.status_code &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status_code, message, data);
    }

    @Override
    public String toString() {
        return "RegistroUsuarioRespWS{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
