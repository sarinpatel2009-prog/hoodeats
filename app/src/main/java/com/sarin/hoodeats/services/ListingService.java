package com.sarin.hoodeats.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sarin.hoodeats.models.ListingEntity;

import java.util.List;
import java.util.ArrayList;

public class ListingService {
    private final FirebaseFirestore db;
    private static final String LISTINGS_COLLECTION = "listings";
    private static final String TRANSACTIONS_COLLECTION = "ledgerTransactions";

    public ListingService() {
        db = FirebaseFirestore.getInstance();
    }

    // CREATE new listing
    public Task<String> registerListing(ListingEntity listing) {
        String listingId = db.collection(LISTINGS_COLLECTION).document().getId();
        listing.setId(listingId);

        return db.collection(LISTINGS_COLLECTION)
                .document(listingId)
                .set(listing)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return listingId;
                    }
                    throw task.getException();
                });
    }

    // UPDATE listing
    public Task<Void> updateListing(ListingEntity listing) {
        return db.collection(LISTINGS_COLLECTION)
                .document(listing.getId())
                .set(listing);
    }

    // GET single listing
    public Task<ListingEntity> getListing(String listingId) {
        return db.collection(LISTINGS_COLLECTION)
                .document(listingId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        return task.getResult().toObject(ListingEntity.class);
                    }
                    return null;
                });
    }

    // DELETE listing
    public Task<Void> deleteListing(String listingId) {
        return db.collection(LISTINGS_COLLECTION)
                .document(listingId)
                .delete();
    }

    // LIST all active listings
    public Task<List<ListingEntity>> listActiveListings() {
        return db.collection(LISTINGS_COLLECTION)
                .whereEqualTo("isActive", true)
                .whereEqualTo("status", "available")
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    List<ListingEntity> listings = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ListingEntity listing = doc.toObject(ListingEntity.class);
                            listings.add(listing);
                        }
                    }
                    return listings;
                });
    }

    // CLAIM a listing
    public Task<Void> claimListing(String listingId, String claimerId) {
        return db.collection(LISTINGS_COLLECTION)
                .document(listingId)
                .update("claimedBy", claimerId, "status", "claimed", "isActive", false);
    }
}