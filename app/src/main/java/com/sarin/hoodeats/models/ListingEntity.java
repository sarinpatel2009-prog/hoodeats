package com.sarin.hoodeats.models;

import com.google.firebase.Timestamp;

public class ListingEntity {
    private String id;
    private String name;
    private String ownerId;
    private String ownerName;
    private String ownerFlat;
    private String ownerBlock;
    private String claimedBy;
    private Timestamp expiryDate;
    private Timestamp postedAt;
    private double priceOrValue;
    private String imageUrl;
    private boolean isActive;
    private String status; // "available", "claimed", "expired"

    // Empty constructor required for Firestore
    public ListingEntity() {}

    public ListingEntity(String id, String name, String ownerId, String ownerName,
                         String ownerFlat, String ownerBlock, Timestamp expiryDate,
                         double priceOrValue, String imageUrl) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerFlat = ownerFlat;
        this.ownerBlock = ownerBlock;
        this.claimedBy = null;
        this.expiryDate = expiryDate;
        this.postedAt = Timestamp.now();
        this.priceOrValue = priceOrValue;
        this.imageUrl = imageUrl;
        this.isActive = true;
        this.status = "available";
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getOwnerFlat() { return ownerFlat; }
    public String getOwnerBlock() { return ownerBlock; }
    public String getClaimedBy() { return claimedBy; }
    public Timestamp getExpiryDate() { return expiryDate; }
    public Timestamp getPostedAt() { return postedAt; }
    public double getPriceOrValue() { return priceOrValue; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return isActive; }
    public String getStatus() { return status; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void setOwnerFlat(String ownerFlat) { this.ownerFlat = ownerFlat; }
    public void setOwnerBlock(String ownerBlock) { this.ownerBlock = ownerBlock; }
    public void setClaimedBy(String claimedBy) { this.claimedBy = claimedBy; }
    public void setExpiryDate(Timestamp expiryDate) { this.expiryDate = expiryDate; }
    public void setPostedAt(Timestamp postedAt) { this.postedAt = postedAt; }
    public void setPriceOrValue(double priceOrValue) { this.priceOrValue = priceOrValue; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setActive(boolean active) { isActive = active; }
    public void setStatus(String status) { this.status = status; }
}