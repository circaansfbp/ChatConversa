package com.example.chatconversa.iniciosesion;

import java.util.Objects;

public class ErrorInicioSesion {
        private String message;

    public ErrorInicioSesion(String message) {
        this.message = message;
    }

    public ErrorInicioSesion() { }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorInicioSesion that = (ErrorInicioSesion) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "ErrorInicioSesion{" +
                "message='" + message + '\'' +
                '}';
    }
}
