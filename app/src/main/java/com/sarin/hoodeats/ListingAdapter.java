package com.sarin.hoodeats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.sarin.hoodeats.models.ListingEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private List<ListingEntity> listings;
    private OnClaimClickListener claimListener;

    public interface OnClaimClickListener {
        void onClaimClick(ListingEntity listing);
    }

    public ListingAdapter(OnClaimClickListener listener) {
        this.listings = new ArrayList<>();
        this.claimListener = listener;
    }

    public void setListings(List<ListingEntity> listings) {
        this.listings = listings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        ListingEntity listing = listings.get(position);

        holder.tvFoodName.setText(listing.getName());
        holder.tvOwnerInfo.setText("Posted by: " + listing.getOwnerName() +
                " - Flat " + listing.getOwnerFlat() + ", Block " + listing.getOwnerBlock());
        holder.tvPrice.setText("₹" + String.format("%.2f", listing.getPriceOrValue()));

        // Format expiry date
        if (listing.getExpiryDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            String expiryText = "Expires: " + sdf.format(listing.getExpiryDate().toDate());
            holder.tvExpiry.setText(expiryText);
        }

        // Load image with Glide
        if (listing.getImageUrl() != null && !listing.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(listing.getImageUrl())
                    .placeholder(android.R.color.darker_gray)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.ivFoodImage);
        } else {
            holder.ivFoodImage.setImageResource(android.R.color.darker_gray);
        }

        holder.btnClaim.setOnClickListener(v -> {
            if (claimListener != null) {
                claimListener.onClaimClick(listing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoodImage;
        TextView tvFoodName;
        TextView tvOwnerInfo;
        TextView tvPrice;
        TextView tvExpiry;
        Button btnClaim;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvOwnerInfo = itemView.findViewById(R.id.tvOwnerInfo);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnClaim = itemView.findViewById(R.id.btnClaim);
        }
    }
}