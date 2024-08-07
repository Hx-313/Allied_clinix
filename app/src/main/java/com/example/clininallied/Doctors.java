package com.example.clininallied;

public class Doctors {
    private String name;
    private String speciality;
    private String college;
    private String experience;
    private String patients;
    private String ratings;
    private int image;

    public Doctors(int image ,String name, String speciality,
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

    public int getImage() {
        return image;
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
