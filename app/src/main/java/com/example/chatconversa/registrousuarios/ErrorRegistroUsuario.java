package com.example.chatconversa.registrousuarios;

import java.util.Objects;

public class ErrorRegistroUsuario {
        private String message;
        private String errors;

    public ErrorRegistroUsuario(String message, String errors) {
        this.message = message;
        this.errors = errors;
    }

    public ErrorRegistroUsuario() { }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorRegistroUsuario that = (ErrorRegistroUsuario) o;
        return Objects.equals(message, that.message) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, errors);
    }

    @Override
    public String toString() {
        return "ErrorRegistroUsuario{" +
                "message='" + message + '\'' +
                ", errors='" + errors + '\'' +
                '}';
    }
}
