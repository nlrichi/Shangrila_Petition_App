package com.example.cw2_rya3.models;

public class Petition {
    private int id;
    private String title;
    private String content;
    private String status;
    private String petitionerName;
    private int signaturesCount;
    private String response;

    // Constructor for general petitions (used in ViewPetitionsActivity, etc.)
    public Petition(int id, String title, String content, String status, String petitionerName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.petitionerName = petitionerName;
    }

    // Constructor for petitions with signature count and response (used in RespondToPetitionsActivity)
    public Petition(int id, String title, String content, String status, int signaturesCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.signaturesCount = signaturesCount;
        this.response = response;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public String getPetitionerName() { return petitionerName; }
    public int getSignaturesCount() { return signaturesCount; }
    public String getResponse() { return response; }

    // Setters
    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

