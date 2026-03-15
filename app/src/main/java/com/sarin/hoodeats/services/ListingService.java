package com.sarin.hoodeats.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sarin.hoodeats.models.ListingEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    // LIST all active listings - FIXED VERSION
    public Task<List<ListingEntity>> listActiveListings() {
        return db.collection(LISTINGS_COLLECTION)
                .whereEqualTo("status", "available")  // Only filter by status
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    List<ListingEntity> listings = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ListingEntity listing = doc.toObject(ListingEntity.class);
                            // Filter isActive in code instead of query to avoid index issues
                            if (listing != null && listing.isActive()) {
                                listings.add(listing);
                            }
                        }
                    }
                    return listings;
                });
    }

    // CLAIM a listing - simple version without ledger
    public Task<Void> claimListing(String listingId, String claimerId) {
        return db.collection(LISTINGS_COLLECTION)
                .document(listingId)
                .update("claimedBy", claimerId, "status", "claimed", "isActive", false);
    }

    // CLAIM a listing WITH ledger transaction - NEW METHOD
    public Task<Void> claimListingWithLedger(String listingId, String claimerId, String providerId, double amount) {
        // Update listing status
        return db.collection(LISTINGS_COLLECTION)
                .document(listingId)
                .update("claimedBy", claimerId, "status", "claimed", "isActive", false)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        // Create ledger transaction
                        String transactionId = db.collection(TRANSACTIONS_COLLECTION).document().getId();

                        Map<String, Object> transaction = new HashMap<>();
                        transaction.put("id", transactionId);
                        transaction.put("listingId", listingId);
                        transaction.put("providerId", providerId);
                        transaction.put("claimerId", claimerId);
                        transaction.put("amount", amount);
                        transaction.put("timestamp", com.google.firebase.Timestamp.now());
                        transaction.put("status", "pending");

                        return db.collection(TRANSACTIONS_COLLECTION)
                                .document(transactionId)
                                .set(transaction);
                    }
                    throw task.getException();
                });
    }

    // GET user's posted listings
    public Task<List<ListingEntity>> getUserListings(String userId) {
        return db.collection(LISTINGS_COLLECTION)
                .whereEqualTo("ownerId", userId)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    List<ListingEntity> listings = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ListingEntity listing = doc.toObject(ListingEntity.class);
                            if (listing != null) {
                                listings.add(listing);
                            }
                        }
                    }
                    return listings;
                });
    }

    // GET user's claimed listings
    public Task<List<ListingEntity>> getClaimedListings(String userId) {
        return db.collection(LISTINGS_COLLECTION)
                .whereEqualTo("claimedBy", userId)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    List<ListingEntity> listings = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            ListingEntity listing = doc.toObject(ListingEntity.class);
                            if (listing != null) {
                                listings.add(listing);
                            }
                        }
                    }
                    return listings;
                });
    }
}