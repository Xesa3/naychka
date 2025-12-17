package com.example.healthapp.study;

import java.io.Serializable;

public class Study implements Serializable {

    private int id;
    private String title;
    private String fullText;
    private String date;
    private String photoUri;


    public Study(int id, String title, String fullText, String date, String photoUri) {
        this.id = id;
        this.title = title;
        this.fullText = fullText;
        this.date = date;
        this.photoUri = photoUri;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFullText() { return fullText; }
    public void setFullText(String fullText) { this.fullText = fullText; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getPhotoUri() { return photoUri; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }


}
