package com.example.clininallied;

import android.net.Uri;

public class Doctors {
    private String name;
    private String speciality;
    private String college;
    private String experience;
    private String patients;
    private String ratings;
    private byte[] imageToSotreInDB;
    private String image ;

    public void setName(String name) {
        this.name = name;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public void setPatients(String patients) {
        this.patients = patients;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public byte[] getImageToStoreInDB() {
        return imageToSotreInDB;
    }

    public void setImageToSotreInDB(byte[] imageToSotreInDB) {
        this.imageToSotreInDB = imageToSotreInDB;
    }
    public Doctors(){

    }
    public Doctors(String name, String speciality, String college, String experience, String patients, String ratings) {
        this.name = name;
        this.speciality = speciality;
        this.college = college;
        this.experience = experience;
        this.patients = patients;
        this.ratings = ratings;
    }





    public Doctors(String image , String name, String speciality,
                   String college, String patients, String ratings,
                   String experience) {
        this.name = name;
        this.speciality = speciality;
        this.college = college;
        this.patients = patients;
        this.ratings = ratings;
        this.experience = experience;
        this.image = image;
    }



    public String getExperience() {
        return experience;
    }

    public String getPatients() {
        return patients;
    }

    public String getCollege() {
        return college;
    }

    public String getSpeciality() {
        return speciality;
    }

    public String getName() {
        return name;
    }



    public String getRatings() {
        return ratings;
    }







}
