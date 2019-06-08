package com.tsf.model.adapter;

public class ProfessionModel {
    String organisation, designation, start_date, end_date;

    public ProfessionModel(String organisation, String designation, String start_date, String end_date) {
        this.organisation = organisation;
        this.designation = designation;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getDesignation() {
        return designation;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

}
