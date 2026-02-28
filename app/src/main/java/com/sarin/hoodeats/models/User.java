package com.sarin.hoodeats.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String flatNumber;
    private String block;
    private String contactNumber;
    private double ledgerBalance;
    private String profileImageUrl;

    // Required empty constructor for Firestore
    public User() {}

    public User(String id, String name, String email, String flatNumber,
                String block, String contactNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.flatNumber = flatNumber;
        this.block = block;
        this.contactNumber = contactNumber;
        this.ledgerBalance = 0.0;
        this.profileImageUrl = "";
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getFlatNumber() { return flatNumber; }
    public String getBlock() { return block; }
    public String getContactNumber() { return contactNumber; }
    public double getLedgerBalance() { return ledgerBalance; }
    public String getProfileImageUrl() { return profileImageUrl; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public void setBlock(String block) { this.block = block; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setLedgerBalance(double ledgerBalance) { this.ledgerBalance = ledgerBalance; }
    public void setProfileImageUrl(String url) { this.profileImageUrl = url; }
}