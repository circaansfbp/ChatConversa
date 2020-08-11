package com.example.chatconversa.cerrarsesion;

import java.util.Objects;

public class CerrarSesionRespWS {
    private int status_code;
    private String message;

    public CerrarSesionRespWS(int status_code, String message) {
        this.status_code = status_code;
        this.message = message;
    }

    public CerrarSesionRespWS() { }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CerrarSesionRespWS that = (CerrarSesionRespWS) o;
        return status_code == that.status_code &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status_code, message);
    }

    @Override
    public String toString() {
        return "CerrarSesionRespWS{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                '}';
    }
}
