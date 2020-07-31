package com.example.chatconversa.registrarfotousuario;

import java.util.Objects;

public class Data {
    private String image;
    private String thumbnail;

    public Data() {
    }

    public Data(String image, String thumbnail) {
        this.image = image;
        this.thumbnail = thumbnail;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equals(image, data.image) &&
                Objects.equals(thumbnail, data.thumbnail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, thumbnail);
    }

    @Override
    public String toString() {
        return "Data{" +
                "image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
