package com.sarin.hoodeats;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.sarin.hoodeats.models.ListingEntity;
import com.sarin.hoodeats.models.User;
import com.sarin.hoodeats.services.ImageUploadService;
import com.sarin.hoodeats.services.ListingService;
import com.sarin.hoodeats.services.UserService;
import java.util.Calendar;
import java.util.Date;

public class PostFoodActivity extends AppCompatActivity {

    private EditText etFoodName, etPrice, etExpiryDate;
    private ImageView ivFoodImage;
    private Button btnSelectImage, btnPost, btnCancel;

    private Uri selectedImageUri;
    private Calendar expiryCalendar;

    private UserService userService;
    private ListingService listingService;
    private ImageUploadService imageUploadService;
    private User currentUser;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_food);

        // Initialize services
        userService = new UserService();
        listingService = new ListingService();
        imageUploadService = new ImageUploadService();
        expiryCalendar = Calendar.getInstance();

        // Initialize views
        etFoodName = findViewById(R.id.etFoodName);
        etPrice = findViewById(R.id.etPrice);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnPost = findViewById(R.id.btnPost);
        btnCancel = findViewById(R.id.btnCancel);

        // Setup image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        ivFoodImage.setImageURI(uri);
                    }
                }
        );

        // Load current user
        loadCurrentUser();

        // Setup button listeners
        btnSelectImage.setOnClickListener(v -> selectImage());
        etExpiryDate.setOnClickListener(v -> showDateTimePicker());
        btnPost.setOnClickListener(v -> postFood());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadCurrentUser() {
        userService.getCurrentUser()
                .addOnSuccessListener(user -> {
                    currentUser = user;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void selectImage() {
        imagePickerLauncher.launch("image/*");
    }

    private void showDateTimePicker() {
        Calendar now = Calendar.getInstance();

        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    expiryCalendar.set(Calendar.YEAR, year);
                    expiryCalendar.set(Calendar.MONTH, month);
                    expiryCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Time picker after date is selected
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                expiryCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                expiryCalendar.set(Calendar.MINUTE, minute);

                                // Update the EditText with selected date and time
                                String dateTime = String.format("%02d/%02d/%d %02d:%02d",
                                        dayOfMonth, month + 1, year, hourOfDay, minute);
                                etExpiryDate.setText(dateTime);
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    private void postFood() {
        if (currentUser == null) {
            Toast.makeText(this, "User not loaded, please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        String foodName = etFoodName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String expiryStr = etExpiryDate.getText().toString().trim();

        // Validation
        if (foodName.isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceStr.isEmpty()) {
            Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expiryStr.isEmpty()) {
            Toast.makeText(this, "Please select expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPost.setEnabled(false);
        btnPost.setText("Posting...");

        // Create listing
        Date expiryDate = expiryCalendar.getTime();
        Timestamp expiryTimestamp = new Timestamp(expiryDate);

        ListingEntity listing = new ListingEntity(
                "", // ID will be generated
                foodName,
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getFlatNumber(),
                currentUser.getBlock(),
                expiryTimestamp,
                price,
                "" // Image URL will be set after upload
        );

        // Register listing first
        listingService.registerListing(listing)
                .addOnSuccessListener(listingId -> {
                    // If image is selected, upload it
                    if (selectedImageUri != null) {
                        uploadImageAndUpdateListing(listingId);
                    } else {
                        // No image, just finish
                        Toast.makeText(this, "Food posted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnPost.setEnabled(true);
                    btnPost.setText("Post Food");
                });
    }

    private void uploadImageAndUpdateListing(String listingId) {
        imageUploadService.uploadListingImage(listingId, selectedImageUri)
                .addOnSuccessListener(imageUrl -> {
                    // Update listing with image URL
                    listingService.getListing(listingId)
                            .addOnSuccessListener(listing -> {
                                if (listing != null) {
                                    listing.setImageUrl(imageUrl);
                                    listingService.updateListing(listing)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Food posted successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Posted but image update failed", Toast.LENGTH_SHORT).show();
                                                finish();
                                            });
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Posted but image upload failed", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}