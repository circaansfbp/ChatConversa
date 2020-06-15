package com.example.chatconversa.errors;

import java.util.Objects;

public class ErrorResponse {
    private int status_code;
    private String message;
    private Errors errors;

    public ErrorResponse(int status_code, String message, Errors errors) {
        this.status_code = status_code;
        this.message = message;
        this.errors = errors;
    }

    public ErrorResponse() { }

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

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return status_code == that.status_code &&
                Objects.equals(message, that.message) &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status_code, message, errors);
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                '}';
    }
}
