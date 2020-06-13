package com.example.chatconversa.registrousuarios;

import com.example.chatconversa.User;

import java.util.Objects;

public class RegistroUsuarioRespWS {
    private int statusCode;
    private String message;
    private User data;
    private ErrorRegistroUsuario errors;

    public RegistroUsuarioRespWS(int statusCode, String message, ErrorRegistroUsuario errors) {
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
    }

    public RegistroUsuarioRespWS(int statusCode, String message, User data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ErrorRegistroUsuario getErrors() {
        return errors;
    }

    public void setErrors(ErrorRegistroUsuario errors) {
        this.errors = errors;
    }

    public RegistroUsuarioRespWS() { }

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
        return statusCode == that.statusCode &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message, data, errors);
    }

    @Override
    public String toString() {
        return "RespuestaWS{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", errors=" + errors +
                '}';
    }
}
