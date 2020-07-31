package com.example.chatconversa.sesionactiva.enviarchat;

import com.example.chatconversa.sesionactiva.chatview.DatosSesionActiva;

import java.util.Objects;

public class EnviarMensajeRespWS {
    private int status_code;
    private String message;
    private DatosSesionActiva data;

    public EnviarMensajeRespWS(int status_code, String message, DatosSesionActiva data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
    }

    public EnviarMensajeRespWS() { }

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

    public DatosSesionActiva getData() {
        return data;
    }

    public void setData(DatosSesionActiva data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnviarMensajeRespWS that = (EnviarMensajeRespWS) o;
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
        return "TextRespWS{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
