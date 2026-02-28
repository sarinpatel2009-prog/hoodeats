package com.sarin.hoodeats.services;

import android.net.Uri;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ImageUploadService {
    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public ImageUploadService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    // Upload user profile image
    public Task<String> uploadUserImage(String userId, Uri imageUri) {
        StorageReference userImageRef = storageRef.child("users/" + userId + "/profile.jpg");

        return userImageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return userImageRef.getDownloadUrl();
                    }
                    throw task.getException();
                })
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toString();
                    }
                    throw task.getException();
                });
    }

    // Upload listing image
    public Task<String> uploadListingImage(String listingId, Uri imageUri) {
        StorageReference listingImageRef = storageRef.child("listings/" + listingId + "/image.jpg");

        return listingImageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        return listingImageRef.getDownloadUrl();
                    }
                    throw task.getException();
                })
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        return task.getResult().toString();
                    }
                    throw task.getException();
                });
    }
}