package com.example.healthapp.study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Study implements Serializable {

    private int id;
    private String title;
    private String fullText;
    private String date;
    private List<String> photoUris = new ArrayList<>();


    public Study(int id, String title, String fullText, String date, String photoUri) {
        this.id = id;
        this.title = title;
        this.fullText = fullText;
        this.date = date;
        this.photoUris = new ArrayList<>();  // создаём пустой список
        if (photoUri != null && !photoUri.isEmpty()) {
            this.photoUris.add(photoUri);   // добавляем строку в список
        }
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getFullText() { return fullText; }
    public String getDate() { return date; }
    public List<String> getPhotoUri() { return photoUris; }


    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setFullText(String fullText) { this.fullText = fullText; }
    public void setDate(String date) { this.date = date;}

    // ---------- фото ----------

    public void addPhoto(String uri){
        photoUris.add(uri);
    }

    public void removePhoto(String uri){
        photoUris.remove(uri);
    }

}
