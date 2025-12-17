package com.example.healthapp.model;

import com.example.healthapp.study.Study;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patient implements Serializable {
    private String name;
    private String secondName;
    private String sureName;
    private String age;
    private boolean male;

    private String foolName;
    private String createdAt;

    private List<Study> studies;

    public Patient(String name,String secondName,String sureName,String age,boolean male){
        this.name = name;
        this.age = age;
        this.sureName = sureName;
        this.secondName = secondName;
        this.male = male;

    }

    public Patient(String foolName, String age, String createdAt){
        this.age = age;
        this.foolName = foolName;
        this.createdAt = createdAt;
        this.studies = new ArrayList<>();

    }

    public void addStudy(Study study) {
        studies.add(study);
    }


    public void setStudy(List<Study> studies) {
        this.studies = studies;
    }

    public List<Study> getStudy(){
        return studies;
    }
    public String getName() {return name;}
    public String getSureName() {return sureName;}
    public String getSecondName() {return secondName;}
    public String getCreatedAt() {return createdAt;}
    public String getFoolName() {return foolName;}
    public String getAge() {return age;}
    public boolean getMale() {return male;}





}
