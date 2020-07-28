package com.example.chatconversa.sesionactiva.chatview;

import java.util.Objects;

public class DatosSesionActiva {
    private int id;
    private String date;
    private String message;
    private String latitude;
    private String longitude;
    private String image;
    private String thumbnail;
    private UserSesionActiva user;

    public DatosSesionActiva(int id, String date, String message, String latitude, String longitude, String image, String thumbnail, UserSesionActiva user) {
        this.id = id;
        this.date = date;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.thumbnail = thumbnail;
        this.user = user;
    }

    public DatosSesionActiva() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public UserSesionActiva getUser() {
        return user;
    }

    public void setUser(UserSesionActiva user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatosSesionActiva that = (DatosSesionActiva) o;
        return id == that.id &&
                Objects.equals(date, that.date) &&
                Objects.equals(message, that.message) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(image, that.image) &&
                Objects.equals(thumbnail, that.thumbnail) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, message, latitude, longitude, image, thumbnail, user);
    }

    @Override
    public String toString() {
        return "DatosSesionActiva{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", message='" + message + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", user=" + user +
                '}';
    }
}
