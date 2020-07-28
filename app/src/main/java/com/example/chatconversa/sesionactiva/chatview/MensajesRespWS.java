package com.example.chatconversa.sesionactiva.chatview;

import java.util.Arrays;
import java.util.Objects;

public class MensajesRespWS {
    private int status_code;
    private String message;
    private DatosSesionActiva[] data;

    public MensajesRespWS(int status_code, String message, DatosSesionActiva[] data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
    }

    public MensajesRespWS() { }

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

    public DatosSesionActiva[] getData() {
        return data;
    }

    public void setData(DatosSesionActiva[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MensajesRespWS that = (MensajesRespWS) o;
        return status_code == that.status_code &&
                Objects.equals(message, that.message) &&
                Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(status_code, message);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "MensajesRespWS{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
