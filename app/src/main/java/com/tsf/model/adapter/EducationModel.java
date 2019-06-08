package com.tsf.model.adapter;

public class EducationModel {
    private String organisation, degree, location, start_year, end_year;

    public EducationModel(String orrganisation, String degree, String location, String start_year, String end_year) {
        this.organisation = orrganisation;
        this.degree = degree;
        this.location = location;
        this.start_year = start_year;
        this.end_year = end_year;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getDegree() {
        return degree;
    }

    public String getLocation() {
        return location;
    }

    public String getStart_year() {
        return start_year;
    }

    public String getEnd_year() {
        return end_year;
    }
}
