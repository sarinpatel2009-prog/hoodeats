package com.sarin.hoodeats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sarin.hoodeats.models.ListingEntity;
import com.sarin.hoodeats.models.User;
import com.sarin.hoodeats.services.ListingService;
import com.sarin.hoodeats.services.UserService;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvListings;
    private TextView tvEmptyState;
    private Button btnRefresh, btnPostFood, btnProfile, btnLogout;

    private ListingAdapter adapter;
    private ListingService listingService;
    private UserService userService;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize services
        listingService = new ListingService();
        userService = new UserService();

        // Initialize views
        rvListings = findViewById(R.id.rvListings);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnPostFood = findViewById(R.id.btnPostFood);
        btnProfile = findViewById(R.id.btnProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Setup RecyclerView
        rvListings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListingAdapter(this::onClaimClick);
        rvListings.setAdapter(adapter);

        // Load current user
        loadCurrentUser();

        // Setup button listeners
        btnRefresh.setOnClickListener(v -> loadListings());
        btnPostFood.setOnClickListener(v -> {
            // TODO: Navigate to PostFoodActivity
            Toast.makeText(this, "Post Food feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        btnProfile.setOnClickListener(v -> {
            // TODO: Navigate to ProfileActivity
            Toast.makeText(this, "Profile feature coming soon!", Toast.LENGTH_SHORT).show();
        });
        btnLogout.setOnClickListener(v -> logout());

        // Load listings
        loadListings();
    }

    private void loadCurrentUser() {
        userService.getCurrentUser()
                .addOnSuccessListener(user -> {
                    currentUser = user;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadListings() {
        btnRefresh.setEnabled(false);
        btnRefresh.setText("Loading...");

        listingService.listActiveListings()
                .addOnSuccessListener(listings -> {
                    if (listings.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvListings.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvListings.setVisibility(View.VISIBLE);
                        adapter.setListings(listings);
                    }
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("Refresh");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading listings: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("Refresh");
                });
    }

    private void onClaimClick(ListingEntity listing) {
        if (currentUser == null) {
            Toast.makeText(this, "Please wait, loading user data...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is trying to claim their own listing
        if (listing.getOwnerId().equals(currentUser.getId())) {
            Toast.makeText(this, "You cannot claim your own food!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Claim the listing
        listingService.claimListingWithLedger(
                        listing.getId(),
                        currentUser.getId(),
                        listing.getOwnerId(),
                        listing.getPriceOrValue()
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Food claimed successfully!", Toast.LENGTH_SHORT).show();
                    loadListings(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to claim: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void logout() {
        userService.logoutUser();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}