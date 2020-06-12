package com.example.chatconversa;

import java.util.Objects;

public class RespuestaWS {
    private int statusCode;
    private String message;
    private User data;

    public RespuestaWS(int statusCode, String message, User data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public RespuestaWS() { }

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
        RespuestaWS that = (RespuestaWS) o;
        return statusCode == that.statusCode &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, message, data);
    }

    @Override
    public String toString() {
        return "RespuestaWS{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
